package com.vmesteonline.be.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URL;
import java.nio.channels.Channels;
import java.util.Arrays;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.datanucleus.util.Base64;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;

public class StorageHelper {

		/**
	 * Used below to determine the size of chucks to read in. Should be > 1kb and
	 * < 10MB
	 */
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;

	/**
	 * This is where backoff parameters are configured. Here it is aggressively
	 * retrying with backoff, up to 10 times but taking no more that 15 seconds
	 * total to do so.
	 */
	private static GcsService gcsService;

	static {
		gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder().initialRetryDelayMillis(10).retryMaxAttempts(10)
				.totalRetryPeriodMillis(15000).build());
	}

	public StorageHelper() {
	}

	/**
	 * MEthod saves image that provided as an http URL or as a JPEG data and
	 * returns URL the image is accessible from
	 * 
	 * @param urlOrContent
	 *          - http URL or content of a JPEG coded image
	 * @return
	 */
	public static String saveImage(byte[] urlOrContent, long ownerId, boolean isPublic, PersistenceManager _pm) throws IOException {

		if (null == urlOrContent || 0 == urlOrContent.length) {
			throw new IOException("Invalid content. Failed to store null or empty content");

		} else {
			String fname; 
			InputStream is = null;

			try { // try to create URL from content
				URL url;
				url = new URL(new String(urlOrContent));
				is = url.openStream();
				//file name for the same sources will be the same
				fname = url.getFile();

			} catch (Exception e) {
				is = new ByteArrayInputStream(urlOrContent);
				fname = numberToString( (long)(Math.random()*Long.MAX_VALUE));
			}

			return saveImage(fname, ownerId, isPublic, is, null );
		}
	}
//===================================================================================================================

	public static VoFileAccessRecord createFileAccessRecord( long userId, boolean isPublic, String fileName){
		return new VoFileAccessRecord(userId, isPublic, fileName);
	}
//===================================================================================================================

	public static String saveImage(String urlOrContent, long onerId, boolean isPublic, PersistenceManager _pm) throws IOException {
		return saveImage(urlOrContent.getBytes(), onerId, isPublic,_pm);
	}

	//===================================================================================================================
	
	public static String replaceImage(String urlOrContent, String oldURL, long userId, Boolean isPublic, PersistenceManager _pm) throws IOException {
		long oldFileId = getFileId(oldURL);
		PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;
		try{
			try {
				VoFileAccessRecord oldFile = pm.getObjectById(VoFileAccessRecord.class, oldFileId);
				if(0==userId) userId = oldFile.getUserId();
				if(null==isPublic) isPublic = oldFile.isPublic();
				deleteImage(oldFile.getFileName());
			} catch( JDOObjectNotFoundException onfe){
			}
			return saveImage(urlOrContent, userId, isPublic,pm);
		} finally {
			if(null==_pm) pm.close();
		}
	}
//===================================================================================================================
	/**
	 * Transfer the data from the inputStream to the outputStream. Then close both
	 * streams.
	 */
	private static void streamCopy(InputStream input, OutputStream output) throws IOException {
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1) {
				output.write(buffer, 0, bytesRead);
				bytesRead = input.read(buffer);
			}
		} finally {
			input.close();
			output.close();
		}
	}
//===================================================================================================================
	public static boolean getFile(String url, OutputStream os) throws IOException {
		long oldFileId = getFileId(url);
		PersistenceManager pm = PMF.getPm();
		try{
			try {
				VoFileAccessRecord vfar = pm.getObjectById(VoFileAccessRecord.class, oldFileId);
				getFile(vfar.getFileName(), os);
				return true;
			} catch( JDOObjectNotFoundException onfe){
				return false;
			}
		} finally {
			pm.close();
		}
	}
//===================================================================================================================
	
	private static void getFile(GcsFilename fileName, OutputStream outputStream) throws IOException {
		GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
		StorageHelper.streamCopy(Channels.newInputStream(readChannel), outputStream);
	}

	//===================================================================================================================

	public static String saveImage(String fileName, long userId, boolean isPublic, InputStream is, PersistenceManager _pm ) throws IOException {
		VoFileAccessRecord vfar = createFileAccessRecord(userId,isPublic, fileName);
		PersistenceManager pm = null==_pm ?  PMF.getPm() : _pm;
		try {
			vfar = pm.makePersistent(vfar);
		} finally {
			if(null==_pm) pm.close();
		}
		GcsOutputChannel outputChannel = gcsService.createOrReplace( vfar.getFileName(), GcsFileOptions.getDefaultInstance());
		streamCopy(is, Channels.newOutputStream(outputChannel));

		int liop; //append with '.bin' extension if no extension is set
		return getURL(vfar.getId(), -1 == (liop = fileName.lastIndexOf('.')) ? "bin" : fileName.substring( liop + 1 ));
	}
//===================================================================================================================

	private static void saveImage(GcsFilename fileName, InputStream inputStream) throws IOException {
		
		GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName,
				GcsFileOptions.getDefaultInstance());
		StorageHelper.streamCopy(inputStream, Channels.newOutputStream(outputChannel));
	}

	//===================================================================================================================

	public static boolean deleteImage(String url, PersistenceManager _pm) throws IOException {
		long oldFileId = getFileId(url);
		PersistenceManager pm = null==_pm ? PMF.getPm() : _pm;
		try{
			try {
				VoFileAccessRecord oldFile = pm.getObjectById(VoFileAccessRecord.class, oldFileId);
				deleteImage(oldFile.getFileName());
				return true;
			} catch( JDOObjectNotFoundException onfe){
				return false;
			}
		} finally {
			if(null==_pm) pm.close();
		}
	}
	//===================================================================================================================

	private static boolean deleteImage(GcsFilename fileName) throws IOException {
		return gcsService.delete(fileName);
	}
	
//===================================================================================================================
	public static String getURL( long id, String ext ){
		return "/file/"+numberToString(id)+"."+ext;
	}
	//===================================================================================================================
	public static long getFileId( String requestURI ){
		String[] splits = requestURI.split("/", 3);
		if (splits.length < 3 || !splits[0].equals("") || !splits[1].equals("file") ||
				splits[2].length()==0) {
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Expecting /file/<id>.<extension>");
		}
		splits = splits[2].split("[.]", 2);
		if( splits.length < 2 || splits[0].length()<2){
			throw new IllegalArgumentException("The URL '"+requestURI+"' is not formed as expected. " + "Expecting /file/<id>.<extension>");
		}
		return stringToNumber(splits[0]);
	}
	
//===================================================================================================================
	public static String numberToString( Long n ){
		String string = new String(Base64.encode( BigInteger.valueOf(n).toByteArray()));
		string = string.substring(0,string.length() - 1);
		string = string.replace("/", "_");
		return string;
	}
	
	public static long stringToNumber(String str){
		str = str.replace("_", "/") + "=";
		return new BigInteger(Base64.decode( str )).longValue();
	}
	
}
