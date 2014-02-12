package com.vmesteonline.be.utils;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.labs.repackaged.com.google.common.io.LineReader;

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
	
	public static String saveImage( String urlOrContent )  throws IOException{
		return saveImage(urlOrContent.getBytes());
	}
	
	public static<T> List<T> loadCSVData( InputStream is, Map<Integer,String> fieldPosMap, T otf, String fieldDelim, String setDelim, String avpDelim) throws IOException{
		LineReader lr = new LineReader(  new InputStreamReader( is ) );
		String nextLine, fieldName;
		List<T> rslt = new ArrayList<T>();
		
		String fd = null == fieldDelim ? "," : fieldDelim ;
		String sd = null == setDelim ? "|" : setDelim;
		String avpd = null == avpDelim ? ":" : avpDelim;
		
		try {
			while( (nextLine = lr.readLine()) != null){
				T nextOtf = (T)otf.getClass().getConstructor(new Class[]{}).newInstance(new Object[]{});
				String[] items = nextLine.split("["+fd+"]");
				int delimSkipped = 0;
				for (int pos = 0; pos < items.length; pos ++) {
					if( null != ( fieldName = fieldPosMap.get(pos-delimSkipped))){
						Field field = otf.getClass().getField(fieldName);
						String nextItem = items[pos];
						if( nextItem.trim().startsWith("\"") && 
								!nextItem.trim().endsWith("\"")) {
							for( pos++; pos < items.length; pos++){
								delimSkipped++;
								nextItem += fd +items[pos];
								if( items[pos].trim().endsWith("\""))
									break;
							}
						}
						nextItem = nextItem.trim();
						if( nextItem.startsWith("\"") ) nextItem = nextItem.substring(1);
						if( nextItem.endsWith("\"") ) nextItem = nextItem.substring(0, nextItem.length()-1);
						
						Object fo = field.get(nextOtf);
						if ( fo instanceof Double ) field.set(nextOtf, Double.parseDouble(nextItem));
						else if ( fo instanceof Integer ) field.set(nextOtf, Integer.parseInt(nextItem));
						else if ( fo instanceof Boolean ) field.set(nextOtf, Boolean.parseBoolean(nextItem));
						else if ( fo instanceof Long ) field.set(nextOtf, Long.parseLong(nextItem));
						else if ( fo instanceof Float ) field.set(nextOtf, Float.parseFloat(nextItem));
						else if ( fo instanceof String ) 
							field.set(nextOtf, nextItem);
						else if ( fo instanceof Set ) {
							String[] setItems = nextItem.split("["+sd+"]");
							for (String string : setItems) {
								if( (string = string.trim()).length() > 0 )
									((Set)fo).add(string);
							}
						} else if ( fo instanceof List ) {
							String[] setItems = nextItem.split("["+sd+"]");
							for (String string : setItems) {
								if( (string = string.trim()).length() > 0 )
									((List)fo).add(string);
							}
						} else if ( fo instanceof Map ) {
							String[] setItems = nextItem.split("["+sd+"]");
							for (String string : setItems) {
								String[] avp = string.split("["+avpd+"]");
								if( avp.length>1 && (avp[0] = avp[0].trim()).length() > 0  && (avp[1] = avp[1].trim()).length() > 0 )
									((Map) fo).put( avp[0], avp[1] );
							}
						}
					} else {
						continue;
					}
				}
				rslt.add(nextOtf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Failed to reda data. "+e.getLocalizedMessage(), e);
		}
		return rslt;
	}
}
