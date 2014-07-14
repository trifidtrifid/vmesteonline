package com.vmesteonline.be.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.util.Map;





import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.mail.internet.ContentType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.datanucleus.util.Base64;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.vmesteonline.be.ServiceImpl;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoFileAccessRecord;
import com.vmesteonline.be.jdo2.VoFileAccessRecord.VersionCreator;

public class StorageHelper {

	private static Logger logger = Logger.getLogger(StorageHelper.class.getCanonicalName());

	/**
	 * Used below to determine the size of chucks to read in. Should be > 1kb and < 10MB
	 */
	private static final int BUFFER_SIZE = 2 * 1024 * 1024;

	/**
	 * This is where backoff parameters are configured. Here it is aggressively retrying with backoff, up to 10 times but taking no more that 15 seconds
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
	 * MEthod saves image that provided as an http URL or as a JPEG data and returns URL the image is accessible from
	 * 
	 * @param urlOrContent
	 *          - http URL or content of a JPEG coded image
	 * @return
	 */
	public static String saveImage(byte[] urlOrContent, long ownerId, boolean isPublic, PersistenceManager _pm) throws IOException {
		return saveImage(urlOrContent, null, ownerId, isPublic, _pm, null);
		
	}
	public static String saveImage(byte[] urlOrContent, String _contentType, long ownerId, boolean isPublic, PersistenceManager _pm, String fileName) throws IOException {

		if (null == urlOrContent || 0 == urlOrContent.length) {
			throw new IOException("Invalid content. Failed to store null or empty content");

		} else {
			String fname;
			InputStream is = null;
			String contentType = null==_contentType ? "binary/stream" : _contentType;

			try { // try to create URL from content
				URL url = new URL(new String(urlOrContent));
				if (null != url.getProtocol() && url.getProtocol().toLowerCase().startsWith("http")) {
					HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
					httpConnection.connect();
					httpConnection.getHeaderFields();
					contentType = httpConnection.getContentType();
					is = httpConnection.getInputStream();
				} else {
					is = url.openStream();
					// file name for the same sources will be the same
				}
				fname = url.getFile();

			} catch (MalformedURLException e) {
				String contentString = new String(urlOrContent);
				String[] split = contentString.split("[():;,]");
				if( split.length >= 5 && split[0].equalsIgnoreCase("URL") && split[1].equalsIgnoreCase("data")){
					if( split[3].equals("base64")){
						is = new ByteArrayInputStream( Base64.decode(split[4]));
						contentType = split[2];
					} 
				}
				if( null == is )
					is = new ByteArrayInputStream(urlOrContent);
				
				fname = ( null==fileName || 0 == fileName.trim().length()) ? 
						numberToString((long) (Math.random() * Long.MAX_VALUE)) : 
							URLEncoder.encode (fileName, "UTF-8");
				
			}

			return saveImage(fname, contentType, ownerId, isPublic, is, _pm);
		}
	}

	// ===================================================================================================================

	public static VoFileAccessRecord createFileAccessRecord(long userId, boolean isPublic, String fileName, String contentType) {
		return new VoFileAccessRecord(userId, isPublic, fileName, contentType);
	}

	// ===================================================================================================================

	public static String saveImage(String urlOrContent, long onerId, boolean isPublic, PersistenceManager _pm) throws IOException {
		return saveImage(urlOrContent.getBytes(), onerId, isPublic, _pm);
	}

	// ===================================================================================================================

	public static String replaceImage(String urlOrContent, String oldURL, long userId, Boolean isPublic, PersistenceManager _pm) throws IOException {
		
		if(  urlOrContent.equals( oldURL ) ) return oldURL;
		
		PersistenceManager pm = _pm == null ? PMF.getPm() : _pm;
		try {
			if( null != oldURL ){
			long oldFileId = getFileId(oldURL);
			
				try {
					VoFileAccessRecord oldFile = pm.getObjectById(VoFileAccessRecord.class, oldFileId);
					if (0 == userId)
						userId = oldFile.getUserId();
					if (null == isPublic)
						isPublic = oldFile.isPublic();
					deleteImage(oldFile.getGSFileName());
					oldFile.deleteAllVersions(pm);
					pm.deletePersistent(oldFile);
				} catch (JDOObjectNotFoundException onfe) {
				}
			}
			return saveImage(urlOrContent.getBytes(), "image/jpeg", userId, isPublic, pm, "img.jpeg");
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	// ===================================================================================================================
	/**
	 * Transfer the data from the inputStream to the outputStream. Then close both streams.
	 */
	private static void streamCopy(InputStream input, OutputStream output) throws IOException {
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = input.read(buffer);
			while (bytesRead != -1) {
				output.write(buffer, 0, bytesRead);
				bytesRead = input.read(buffer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			output.close();
			input.close();
		}
	}

	// ===================================================================================================================
	public static boolean getFile(String url, OutputStream os, Map<String, String[]> params) throws IOException {
		long oldFileId = getFileId(url);
		PersistenceManager pm = PMF.getPm();
		try {
			try {
				VoFileAccessRecord vfar = pm.getObjectById(VoFileAccessRecord.class, oldFileId);
				VoFileAccessRecord version = vfar.getVersion( params, pm );
				getFile( (version == null ? vfar : version).getGSFileName(), os);
				return true;
			} catch (JDOObjectNotFoundException onfe) {
				return false;
			}
		} finally {
			pm.close();
		}
	}

	// ===================================================================================================================
	public static void sendFileResponse(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String queryString = req.getRequestURI()+(req.getQueryString() == null ? "" : "?"+req.getQueryString());
		logger.debug("Got request: URL:"+queryString);
		
		byte[] fileData;
		//req.getParameter("useCache")
		Object response = null != queryString ? 
				ServiceImpl.getObjectFromCache(queryString) : null;

		if( null!=response && response instanceof byte[] ){
			fileData = (byte[])response;
			logger.debug("Get '"+queryString+"' from cache.");
			
		} else {
			long oldFileId = getFileId(req.getRequestURI());
			PersistenceManager pm = PMF.getPm();
			try {
				try {
					VoFileAccessRecord vfar = pm.getObjectById(VoFileAccessRecord.class, oldFileId);
					resp.setStatus(HttpServletResponse.SC_OK);
					resp.setContentType(vfar.getContentType()+"; filename='"+vfar.getFileName()+"'");
					resp.addHeader( "Content-Disposition", "attachment; filename="+vfar.getFileName());
					VoFileAccessRecord theVersion = vfar.getVersion( req.getParameterMap(), pm );
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					getFile( theVersion.getGSFileName(), baos);
					baos.close();
					fileData = baos.toByteArray();
					if( null != queryString)
						ServiceImpl.putObjectToCache(queryString, fileData);
					
				} catch (JDOObjectNotFoundException onfe) {
					resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not Found");
					return;
				}
				
			} finally {
				pm.close();
			}
		}
		resp.getOutputStream().write(fileData);
	}

	// ===================================================================================================================

	public static void getFile(GcsFilename fileName, OutputStream outputStream ) throws IOException {
		GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
		StorageHelper.streamCopy(Channels.newInputStream(readChannel), outputStream);
	}

	// ===================================================================================================================

	public static String saveImage(String fileName, String contentType, long userId, boolean isPublic, InputStream is, PersistenceManager _pm)
			throws IOException {
		VoFileAccessRecord vfar = createFileAccessRecord(userId, isPublic, fileName, contentType);
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			vfar = pm.makePersistent(vfar);
		} finally {
			if (null == _pm)
				pm.close();
		}
		try {
			saveFileData(is, vfar);
			int liop; // append with '.bin' extension if no extension is set
			String url = getURL(vfar.getId(), -1 == (liop = fileName.lastIndexOf('.')) ? 
					(contentType.equals("binary/stream") ? "dat" : new ContentType(vfar.getContentType()).getSubType()) : fileName.substring(liop + 1));
			logger.debug( "File '" + vfar.getFileName() + "' stored with GSNAme:" + vfar.getGSFileName() + " with objectID:" + vfar.getId() + " URL:" + url);
			return url;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Failed to save file. " + e.getMessage(), e);
		}
	}

	public static void saveFileData(InputStream is, VoFileAccessRecord vfar) throws IOException {
		GcsOutputChannel outputChannel = gcsService.createOrReplace(vfar.getGSFileName(), GcsFileOptions.getDefaultInstance());
		streamCopy(is, Channels.newOutputStream(outputChannel));
	}

	// =====================================================================================================================

	public static boolean deleteImage(String url, PersistenceManager _pm) throws IOException {
		long oldFileId = getFileId(url);
		PersistenceManager pm = null == _pm ? PMF.getPm() : _pm;
		try {
			try {
				VoFileAccessRecord oldFile = pm.getObjectById(VoFileAccessRecord.class, oldFileId);
				deleteImage(oldFile.getGSFileName());
				oldFile.deleteAllVersions(pm);
				pm.deletePersistent(oldFile);
				return true;
			} catch (JDOObjectNotFoundException onfe) {
				return false;
			}
		} finally {
			if (null == _pm)
				pm.close();
		}
	}

	// ===================================================================================================================

	public static boolean deleteImage(GcsFilename fileName) throws IOException {
		return gcsService.delete(fileName);
	}

	// ===================================================================================================================
	public static String getURL(long id, String ext) {
		return "/file/" + numberToString(id) + "." + ext;
	}

	// ===================================================================================================================
	public static long getFileId(String requestURI) {
		String[] splits = requestURI.split("/", 3);
		if (splits.length < 3 || !splits[0].equals("") || !splits[1].equals("file") || splits[2].length() == 0) {
			throw new IllegalArgumentException("The URL '" + requestURI + "' is not formed as expected. " + "Expecting /file/<id>.<extension>");
		}
		splits = splits[2].split("[.]", 2);
		if (splits.length < 2 || splits[0].length() < 2) {
			throw new IllegalArgumentException("The URL '" + requestURI + "' is not formed as expected. " + "Expecting /file/<id>.<extension>");
		}
		return stringToNumber(splits[0]);
	}

	// ===================================================================================================================
	public static String numberToString(Long n) {
		String string = new String(Base64.encode(BigInteger.valueOf(n).toByteArray()));
		string = string.substring(0, string.length() - 1);
		string = string.replace("/", "_");
		return string;
	}

	public static long stringToNumber(String str) {
		str = str.replace("_", "/") + "=";
		return new BigInteger(Base64.decode(str)).longValue();
	}
//===================================================================================================================

	public static VersionCreator getVersionCreator(VoFileAccessRecord orig, ContentType ct, PersistenceManager pm) {
		if( ct.getPrimaryType().equalsIgnoreCase("image"))
			return new ImageConverterVersionCreator(orig,ct,pm);
		
		final VoFileAccessRecord original = orig;
		//create default creator that just returns the original 
		return new VersionCreator( ) {
			@Override
			public VoFileAccessRecord createParametrizedVersion(Map<String, String[]> params, boolean createIfNotExists) {
				return original;
			}
		};
	}

}
