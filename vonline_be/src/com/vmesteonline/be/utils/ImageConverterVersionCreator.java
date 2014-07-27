package com.vmesteonline.be.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.mail.internet.ContentType;

import org.apache.log4j.Logger;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.tools.cloudstorage.GcsFilename;
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

		Scale scale = extractScale( params );
		Crop crop = extractCrop( params );
		
		if( null == scale && null == crop )
			return original;
				
	
		String versionKey = "image:"+
				(null != scale ? scale.getVersionModificator() : "") + 
				(crop != null ? crop.getVersionModificator() : "");
		
		VoFileAccessRecord version = original.getVersion(versionKey);
		
		if( version == null && createIfNotExists ) {
		
			GcsFilename fileName = original.getGSFileName();

	    try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				StorageHelper.getFile(fileName, baos);
				baos.close();
				Image oldImage = ImagesServiceFactory.makeImage(baos.toByteArray());
				
		    if( scale != null ) 
		    	oldImage = imagesService.applyTransform(scale.getTransform(oldImage), oldImage);
		    if( crop != null )
		    	oldImage = imagesService.applyTransform(crop.getTransform(oldImage), oldImage);
		    
		    VoFileAccessRecord newVoFileAccessRecord =  new VoFileAccessRecord(original.getUserId(), original.isPublic(), 
		    		original.getFileName(), original.getContentType(),
		    		versionKey, original);
		    StorageHelper.saveFileData(new ByteArrayInputStream(oldImage.getImageData()), newVoFileAccessRecord);
				pm.makePersistent(original);
				pm.makePersistent(newVoFileAccessRecord);
				
				return newVoFileAccessRecord;
				
			} catch (IOException e) {
				e.printStackTrace();
			}  
		} 
		return version;
		
	}
	//=================================================================================================
	private Crop extractCrop(Map<String, String[]> params) {
		String[] cropParams;
		String[] sparams = params.get("s");
		if( null == sparams || 0 == sparams.length || (cropParams = sparams[0].split(",")).length < 4 )
			return null;
		try{
			return new Crop(
					Integer.parseInt(cropParams[0]),Integer.parseInt(cropParams[1]),Integer.parseInt(cropParams[2]),Integer.parseInt(cropParams[3]));
		} catch ( NumberFormatException nfe ){
			logger.warn("Failed to parse 's' parameter '"+sparams[0]+"' for crop. Would not be cropped");
			return null;
		}
	}
	//=================================================================================================
	private Scale extractScale(Map<String, String[]> params) {
		if( null==params.get("w") || params.get("w").length == 0  ||
				null==params.get("h") || params.get("h").length == 0 )
			return null;
		String widthStr = params.get("w")[0]; 
		String heightStr = params.get("h")[0];
		String heightDigitsStr, widthDigitsStr;
		
		if( widthStr == null || ( widthDigitsStr = widthStr.replaceAll("[^0-9]", "")).length() == 0 || 
				heightStr == null || ( heightDigitsStr = heightStr.replaceAll("[^0-9]", "")).length() == 0)
			return null;
		
		return new Scale( Integer.parseInt(widthDigitsStr), Integer.parseInt(heightDigitsStr));
	}
	//=================================================================================================
	private static class Scale{
		int x;
		int y;
		public Scale(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public String getVersionModificator(){ return "sc["+x +"x"+ y+"]";}
		
		public Transform getTransform(Image img){
			return ImagesServiceFactory.makeResize(x, y);
		} 
	}
	//=================================================================================================
	private static class Crop{
		int Xlt,Ylt;
		int Xrb,Yrb;
		
		public Crop(int xlt, int ylt, int xrb, int yrb) {
			Xlt = xlt;
			Ylt = ylt;
			Xrb = xrb;
			Yrb = yrb;
		}
		public String getVersionModificator(){ return "cr["+Xlt +","+ Ylt + "," + Xrb +","+Yrb+"]";}
		public Transform getTransform(Image img){
			float width = img.getWidth();
			float height = img.getHeight();
			return ImagesServiceFactory.makeCrop(
					Math.min(((float)Xlt)/width,1.0F) , Math.min(((float)Ylt)/height,1.0F), 
							Math.min(((float)Xrb)/width,1.0F), Math.min(((float)Yrb)/height,1.0F));
		}
	}
	//=================================================================================================
	public static Logger logger;

	static {
		logger = Logger.getLogger(ImageConverterVersionCreator.class);
	}
	
}
