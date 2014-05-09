package com.vmesteonline.be.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.mail.internet.ContentType;

import org.apache.log4j.Logger;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.vmesteonline.be.ShopBOServiceImpl;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoFileAccessRecord.VersionCreator;

public class ImageConverterVersionCreator implements VersionCreator {

	private static ImagesService imagesService = ImagesServiceFactory.getImagesService();
	
	private PersistenceManager pm;
	private VoFileAccessRecord original;
	
	public ImageConverterVersionCreator(VoFileAccessRecord orig, ContentType ct, PersistenceManager pm) {
		this.pm = pm;
		this.original = orig;
	}

	@Override
	public VoFileAccessRecord createParametrizedVersion(Map<String,String[]> params, boolean createIfNotExists) {

		if( null==params.get("w") || params.get("w").length == 0  ||
				null==params.get("h") || params.get("h").length == 0  )
			return original;
				
		String widthStr = params.get("w")[0]; 
		String heightStr = params.get("h")[0];
		String heightDigitsStr, widthDigitsStr;
		if( widthStr == null || ( widthDigitsStr = widthStr.replaceAll("[^0-9]", "")).length() == 0 || 
				heightStr == null || ( heightDigitsStr = heightStr.replaceAll("[^0-9]", "")).length() == 0)
		return original;

		String versionKey = "image:"+widthDigitsStr+"x"+heightDigitsStr;
		VoFileAccessRecord version = original.getVersion(versionKey);
		
		logger.debug("Image '"+original.getFileName()+"' with scale to "+widthDigitsStr+"x"+heightDigitsStr+" requested");
		if( version == null && createIfNotExists ) {
			GcsFilename fileName = original.getGSFileName();
/*			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			
			BlobKey blobKey = blobstoreService.createGsBlobKey(
			    "/gs/" + fileName.getBucketName() + "/" + fileName.getObjectName());
			
			Image oldImage = ImagesServiceFactory.makeImageFromBlob(blobKey);
*/
	    try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				StorageHelper.getFile(fileName, baos);
				baos.close();
				Image oldImage = ImagesServiceFactory.makeImage(baos.toByteArray());
				
		    int newWidth = Integer.parseInt(widthDigitsStr);
				int newHeight = Integer.parseInt(heightDigitsStr);
				/*double wScale = (double)newWidth / (double)oldImage.getWidth();
				double hScale = (double)newHeight / (double)oldImage.getHeight();
				
				double minScale = Math.min(wScale, hScale);
				newWidth = (int) (minScale * oldImage.getWidth());
				newHeight = (int) (minScale * oldImage.getHeight());*/
				
				Transform resize = ImagesServiceFactory.makeResize(newWidth*2, newHeight*2);
				
		    Image newImage = imagesService.applyTransform(resize, oldImage);
		    
		    VoFileAccessRecord newVoFileAccessRecord =  new VoFileAccessRecord(original.getUserId(), original.isPublic(), 
		    		original.getFileName(), original.getContentType(),
		    		versionKey, original);
		    StorageHelper.saveFileData(new ByteArrayInputStream(newImage.getImageData()), newVoFileAccessRecord);
				pm.makePersistent(original);
				pm.makePersistent(newVoFileAccessRecord);
				logger.info("Scaled version: "+newWidth+"x"+newHeight +" of file: "+original.getFileName()+" created.");
				return newVoFileAccessRecord;
				
			} catch (IOException e) {
				e.printStackTrace();
			}  
		} 
		return version;
		
	}
	
	public static Logger logger;

	static {
		logger = Logger.getLogger(ShopBOServiceImpl.class);
	}
	
}
