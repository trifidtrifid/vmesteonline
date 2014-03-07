package com.vmesteonline.be.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.appengine.labs.repackaged.com.google.common.io.LineReader;
import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.jdo2.shop.exchange.OrderLineDescription;
import com.vmesteonline.be.shop.ExchangeFieldType;

public class CSVHelper {

	public static <T> List<T> loadCSVData(){
		return null;
	}
	
	public static <T> List<T> loadCSVData( InputStream is, Map<Integer,String> fieldPosMap, T otf) throws IOException{
		return CSVHelper.loadCSVData( is, fieldPosMap, otf, null,null,null);
	}

	public static<T> List<T> loadCSVData( InputStream is, Map<Integer,String> fieldPosMap, T otf, String fieldDelim, String setDelim, String avpDelim) throws IOException{
		LineReader lr = new LineReader(  new InputStreamReader( is ) );
		String nextLine, fieldName;
		List<T> rslt = new ArrayList<T>();
		
		String fd = null == fieldDelim ? ";" : fieldDelim ;
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
						if(null==fo){ //try to create fo by default constructor
							if( field.getType() == List.class || field.getType() == Set.class ) fo = new ArrayList();
							else if( field.getType() == Map.class ) fo = new HashMap();
							else if( field.getType().getSuperclass() == Number.class ) {
								fo = field.getType().getConstructor(new Class[]{String.class}).newInstance(new Object[]{"0"});
							} else {	
								fo = field.getType().getConstructor(new Class[]{}).newInstance(new Object[]{});
							}
							field.set(nextOtf, fo);
						}
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

//=====================================================================================================================
	
	public static<T> void writeCSVData(OutputStream os, Map<Integer, String> fieldsMap, List<T> listToRead, 
			List<List<String>> fieldsToFill ) throws IOException{
			writeCSVData(os, fieldsMap, listToRead, fieldsToFill, null, null, null );
	}
	//====================================================================================================================
	public static<T> void writeCSVData(OutputStream os, Map<Integer, String> fieldsMap, List<T> listToRead, List<List<String>> fieldsToFill,
			String fieldDelim, String setDelim, String avpDelim) throws IOException {
		
		String fd = null == fieldDelim ? "," : fieldDelim ;
		String sd = null == setDelim ? "|" : setDelim;
		String avpd = null == avpDelim ? ":" : avpDelim;
		
		Collection<Object> fieldNames = new ArrayList<Object>();; 
		if( null!=fieldsMap ){
			SortedMap<Integer, String> sortedFields = new TreeMap<Integer, String>();
			sortedFields.putAll(fieldsMap);
			fieldNames.addAll( sortedFields.values());
		} 
		
		try {
			for (T objectToWrite : listToRead) {
				String lineStr = "";
				
				ArrayList<String> nextFieldsLine = null;
				if(null!=fieldsToFill) {
					nextFieldsLine = new ArrayList<String>();
					fieldsToFill.add( nextFieldsLine);
				}

				if(fieldsMap == null ) {
					fieldNames.clear();
					fieldNames.addAll( Arrays.asList(objectToWrite.getClass().getFields()));
				}
				for( Object value: fieldNames ){
					lineStr += fd;
					String outStr = "";
					Field field = value instanceof Field ? (Field)value : objectToWrite.getClass().getField( value.toString() );
					Object fieldToWrite = field.get(objectToWrite);
					if( null!=fieldToWrite ){ 
						if( fieldToWrite instanceof Number )
							outStr = fieldToWrite.toString();
						
						else if( fieldToWrite instanceof Set || fieldToWrite instanceof List){
							for (Object object : listToRead) {
								outStr += sd + object;
							}
							outStr = outStr.substring(sd.length());
						}
						else if( fieldToWrite instanceof Map ){
							for( Object en : ((Map) fieldToWrite).entrySet()){
								outStr += sd + ((Entry)en).getKey() + avpd + ((Entry)en).getValue();
							}
							outStr = outStr.substring(sd.length());
						} else {
							outStr = fieldToWrite.toString().contains(fd) ? "\""+fieldToWrite.toString()+"\"" :
								"" + fieldToWrite;
						}
						if( nextFieldsLine!=null) 
							nextFieldsLine.add( outStr );
						
						lineStr += outStr;
					}
				}
				os.write( (lineStr.substring(fd.length()) + "\n").getBytes() );
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Failed to write CSV:"+e.getMessage(), e);
		}
	}
	//====================================================================================================================
	public static<T> SortedMap<Integer,String> getFieldsMap( T instance, ExchangeFieldType id, Map<Integer,ExchangeFieldType> requiredFields) throws InvalidOperation{
		SortedMap<Integer,String> fmap = new TreeMap<Integer, String>();
		Field[] fields = instance.getClass().getFields();
		int i = 0;
		for (Entry<Integer,ExchangeFieldType> fte : requiredFields.entrySet()) {
			int idx = fte.getValue().getValue()-id.getValue();
			if( idx >= 0 && idx <fields.length ){
				fmap.put( fte.getKey(), fields[ idx ].getName() );
			} else {
				throw new InvalidOperation(VoError.IncorrectParametrs, "Field["+fte.getValue().name()+"] is not described in the class or id["+id.name()+"] is incorrect");
			}
		}
		return fmap;
	}
}
