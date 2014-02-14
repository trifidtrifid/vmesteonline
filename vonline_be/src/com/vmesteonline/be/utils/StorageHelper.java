package com.vmesteonline.be.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.Channels;

import javax.servlet.ServletOutputStream;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

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
	public static String saveImage(byte[] urlOrContent) throws IOException {

		if (null == urlOrContent || 0 == urlOrContent.length) {
			throw new IOException("Invalid content. Failed to store null or empty content");

		} else {

			InputStream is = null;

			try { // try to create URL from content
				URL url;
				url = new URL(new String(urlOrContent));
				is = url.openStream();

			} catch (Exception e) {
				is = new ByteArrayInputStream(urlOrContent);
			}

			String fname = "" + ("" + System.currentTimeMillis() + "VOFILES").hashCode() + ".jpeg";
			GcsFilename filename = new GcsFilename("public", fname);
			GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());
			streamCopy(is, Channels.newOutputStream(outputChannel));

			return "/file/" + filename.getBucketName() + "/" + filename.getObjectName();
		}
	}

	public static String saveImage(String urlOrContent) throws IOException {
		return saveImage(urlOrContent.getBytes());
	}

	public static String replaceImage(String urlOrContent, String oldURL) throws IOException {
		String newURL = saveImage(urlOrContent);
		gcsService.delete(getFileName(oldURL));
		return newURL;
	}

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

	
	private static GcsFilename getFileName(String uri) {
		String[] splits = uri.split("/", 4);
		if (splits.length < 4 || !splits[0].equals("") || !splits[1].equals("file")) {
			throw new IllegalArgumentException("The URL is not formed as expected. " + "Expecting /file/<bucket>/<object>");
		}
		return new GcsFilename(splits[2], splits[3]);
	}

	/**
	 * Retrieves a file from GCS and returns it in the outputStream. If the
	 * request path is /file/Foo/Bar this will be interpreted as a request to read
	 * the GCS file named Bar in the bucket Foo.
	 */
	
	public static void getFile(String requestURI, ServletOutputStream outputStream) throws IOException {

		GcsFilename fileName = StorageHelper.getFileName(requestURI);
		GcsInputChannel readChannel = gcsService.openPrefetchingReadChannel(fileName, 0, BUFFER_SIZE);
		StorageHelper.streamCopy(Channels.newInputStream(readChannel), outputStream);

	}

	public static String saveImage(String requestURI, InputStream inputStream) throws IOException {
		
		GcsFilename fileName = getFileName(requestURI);
		GcsOutputChannel outputChannel = gcsService.createOrReplace(fileName,
				GcsFileOptions.getDefaultInstance());
		StorageHelper.streamCopy(inputStream, Channels.newOutputStream(outputChannel));
		return "/file/" + fileName.getBucketName()+"/" + fileName.getObjectName();
	}

	public static boolean deleteImage(String requestURI) throws IOException {
		
		GcsFilename fileName = getFileName(requestURI);
		return gcsService.delete(fileName);
	}
}
