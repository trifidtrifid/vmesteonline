package com.vmesteonline.be.utils;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class StorageHelper {

	public StorageHelper() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * MEthod saves image that provided as an http URL or as a JPEG data and returns URL the image is accessible from
	 * @param urlOrContent - http URL or content of a JPEG coded image
	 * @return
	 */
	public static String saveImage( byte[] urlOrContent ) throws IOException {
		if( null==urlOrContent ||
				urlOrContent.length < 8 || 
				!new String( urlOrContent, 0, 7).startsWith("http://"))
			throw new IOException("Only external resources are supported now. URL must starts with 'http://'");
		return new String(urlOrContent);
	} 
	
	public static<T> List<T> loadCSVData( InputStream is, T otf){
		return null;
	}
}
