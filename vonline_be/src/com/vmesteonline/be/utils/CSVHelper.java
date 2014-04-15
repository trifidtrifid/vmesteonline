package com.vmesteonline.be.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;
import com.vmesteonline.be.shop.ExchangeFieldType;

public class CSVHelper {

	private static Logger logger = Logger.getLogger(CSVHelper.class.getCanonicalName());

	public static <T> List<T> loadCSVData(/*InputStream is*/byte[] data, Map<Integer, String> fieldPosMap, T otf) throws IOException {
		return CSVHelper.loadCSVData(data, fieldPosMap, otf, null, null, null);
	}

	public static List<List<String>> parseCSV( byte[] data, String fieldDelim, String setDelim, String avpDelim) throws IOException{
		List<List<String>> res = new ArrayList<List<String>>();
		String fd = null == fieldDelim ? ";" : fieldDelim;
		String sd = null == setDelim ? "|" : setDelim;
		String avpd = null == avpDelim ? ":" : avpDelim;
		
		List<String> lines = readLines(data);
		for (String nextLine : lines) {
			ArrayList<String> lineCols = new ArrayList<String>();
			String[] items = nextLine.split("[" + fd + "]");
			int delimSkipped = 0;
			for (int pos = 0; pos < items.length; pos++) {

					String nextItem = items[pos].replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
					if (nextItem.trim().startsWith("\"") && !nextItem.trim().endsWith("\"")) {
						for (pos++; pos < items.length; pos++) {
							delimSkipped++;
							nextItem += fd + items[pos];
							if (items[pos].trim().endsWith("\""))
								break;
						}
					}
					nextItem = nextItem.trim();
					if (nextItem.startsWith("\""))
						nextItem = nextItem.substring(1);
					if (nextItem.endsWith("\""))
						nextItem = nextItem.substring(0, nextItem.length() - 1);
					
					lineCols.add(nextItem);
				}
			if(lineCols.size()>0) 
				res.add(lineCols);
		}
		
		return res;
	}
	
	public static <T> List<T> loadCSVData(/*InputStream is*/byte[] data, Map<Integer, String> fieldPosMap, T otf, String fieldDelim, String setDelim, String avpDelim)
			throws IOException {
		List<T> rslt = new ArrayList<T>();

		String fd = null == fieldDelim ? ";" : fieldDelim;
		String sd = null == setDelim ? "|" : setDelim;
		String avpd = null == avpDelim ? ":" : avpDelim;

		String fieldName;
		List<String> lines = readLines(data);//is);
		try {
			for (String nextLine : lines) {
				T nextOtf = (T) otf.getClass().getConstructor(new Class[] {}).newInstance(new Object[] {});
				String[] items = nextLine.split("[" + fd + "]");
				int delimSkipped = 0;
				for (int pos = 0; pos < items.length; pos++) {
					if (null != (fieldName = fieldPosMap.get(pos - delimSkipped))) {
						Field field = otf.getClass().getField(fieldName);
						String nextItem = items[pos].replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");
						if (nextItem.trim().startsWith("\"") && !nextItem.trim().endsWith("\"")) {
							for (pos++; pos < items.length; pos++) {
								delimSkipped++;
								nextItem += fd + items[pos];
								if (items[pos].trim().endsWith("\""))
									break;
							}
						}
						nextItem = nextItem.trim();
						if (nextItem.startsWith("\""))
							nextItem = nextItem.substring(1);
						if (nextItem.endsWith("\""))
							nextItem = nextItem.substring(0, nextItem.length() - 1);

						Object fo = field.get(nextOtf);
						if (null == fo) { // try to create fo by default constructor
							if (field.getType() == List.class )
								fo = new ArrayList();
							else if( field.getType() == Set.class)
								fo = new HashSet();
							else if (field.getType() == Map.class)
								fo = new HashMap();
							else if (field.getType().getSuperclass() == Number.class) {
								fo = field.getType().getConstructor(new Class[] { String.class }).newInstance(new Object[] { "0" });
							} else {
								fo = field.getType().getConstructor(new Class[] {}).newInstance(new Object[] {});
							}
							field.set(nextOtf, fo);
						}
						try {
							if (fo instanceof Double)
								field.set(nextOtf, Double.parseDouble(nextItem));
							else if (fo instanceof Integer)
								field.set(nextOtf, Integer.parseInt(nextItem));
							else if (fo instanceof Boolean){
								field.set(nextOtf, Boolean.parseBoolean(nextItem));
								try{
									field.set(nextOtf, Integer.parseInt(nextItem) != 0);
								} catch(NumberFormatException nfe){}
							}
							else if (fo instanceof Long)
								field.set(nextOtf, Long.parseLong(nextItem));
							else if (fo instanceof Float)
								field.set(nextOtf, Float.parseFloat(nextItem));
							else if (fo instanceof String)
								field.set(nextOtf, nextItem);
							else if (fo instanceof Set) {
								String[] setItems = nextItem.split("[" + sd + "]");
								for (String string : setItems) {
									if ((string = string.trim()).length() > 0)
										((Set) fo).add(string);
								}
							} else if (fo instanceof List) {
								String[] setItems = nextItem.split("[" + sd + "]");
								for (String string : setItems) {
									if ((string = string.trim()).length() > 0)
										((List) fo).add(string);
								}
							} else if (fo instanceof Map) {
								String[] setItems = nextItem.split("[" + sd + "]");
								for (String string : setItems) {
									String[] avp = string.split("[" + avpd + "]");
									if (avp.length > 1 && (avp[0] = avp[0].trim()).length() > 0 && (avp[1] = avp[1].trim()).length() > 0)
										((Map) fo).put(avp[0], avp[1]);
								}
							}
						} catch (Throwable t) {
							t.printStackTrace();
							String errStr = "Failed to parse data Filed:" + fieldName + " could not be filled with:'" + nextItem + "' content. Line was: '"+nextLine+"'. Exception:"+t.getMessage();
							logger.error(errStr);
							throw new InvalidOperation(VoError.IncorrectParametrs, errStr);
						}
					} else {
						continue;
					}
				}
				rslt.add(nextOtf);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Failed to reda data. " + e.getMessage(), e);
		}
		return rslt;
	}

	// =====================================================================================================================

	public static <T> void writeCSVData(OutputStream os, Map<Integer, String> fieldsMap, List<T> listToRead, List<List<String>> fieldsToFill)
			throws IOException {
		writeCSVData(os, fieldsMap, listToRead, fieldsToFill, null, null, null);
	}

	// ====================================================================================================================
	public static <T> void writeCSVData(OutputStream os, Map<Integer, String> fieldsMap, List<T> listToRead, List<List<String>> fieldsToFill,
			String fieldDelim, String setDelim, String avpDelim) throws IOException {

		String fd = null == fieldDelim ? ";" : fieldDelim;
		String sd = null == setDelim ? "|" : setDelim;
		String avpd = null == avpDelim ? ":" : avpDelim;

		Collection<Object> fieldNames = new ArrayList<Object>();
		;
		if (null != fieldsMap) {
			SortedMap<Integer, String> sortedFields = new TreeMap<Integer, String>();
			sortedFields.putAll(fieldsMap);
			fieldNames.addAll(sortedFields.values());
		}

		int maxLineLength = 0;
		try {
			for (T objectToWrite : listToRead) {
				String lineStr = "";

				ArrayList<String> nextFieldsLine = null;
				if (null != fieldsToFill) {
					nextFieldsLine = new ArrayList<String>();
					fieldsToFill.add(nextFieldsLine);
				}

				if (fieldsMap == null) {
					fieldNames.clear();
					fieldNames.addAll(Arrays.asList(objectToWrite.getClass().getFields()));
				}
				int lineLength = 0;
				for (Object value : fieldNames) {
					lineStr += fd;
					String outStr = "";
					Field field = value instanceof Field ? (Field) value : objectToWrite.getClass().getField(value.toString());
					Object fieldToWrite = field.get(objectToWrite);
					if (null != fieldToWrite) {
						if (fieldToWrite instanceof Number)
							outStr = fieldToWrite.toString();

						else if (fieldToWrite instanceof Set || fieldToWrite instanceof List) {
							for (Object object : listToRead) {
								outStr += sd + object;
							}
							outStr = outStr.substring(sd.length());
						} else if (fieldToWrite instanceof Map) {
							for (Object en : ((Map) fieldToWrite).entrySet()) {
								outStr += sd + ((Entry) en).getKey() + avpd + ((Entry) en).getValue();
							}
							outStr = outStr.substring(sd.length());
						} else {
							outStr = fieldToWrite.toString().contains(fd) ? "\"" + fieldToWrite.toString() + "\"" : "" + fieldToWrite;
						}
						if (nextFieldsLine != null) {
							nextFieldsLine.add(outStr);
							lineLength++;
						}

						lineStr += outStr;
					}
				}
				if( lineLength > maxLineLength ) maxLineLength = lineLength;
				
				os.write((lineStr.substring(fd.length()) + "\n").getBytes());
			}
			//arrange all lines to be the same size
			if(null!=fieldsToFill)
				for( List< String > line : fieldsToFill ){
					while(line.size() < maxLineLength)
						line.add(null);	
				} 
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Failed to write CSV:" + e.getMessage(), e);
		}
	}

	// ====================================================================================================================
	public static <T> SortedMap<Integer, String> getFieldsMap(T instance, ExchangeFieldType id, Map<Integer, ExchangeFieldType> requiredFields)
			throws InvalidOperation {
		SortedMap<Integer, String> fmap = new TreeMap<Integer, String>();
		Field[] fields = instance.getClass().getFields();
		int i = 0;
		for (Entry<Integer, ExchangeFieldType> fte : requiredFields.entrySet()) {
			int idx = fte.getValue().getValue() - id.getValue();
			if (idx >= 0 && idx < fields.length) {
				fmap.put(fte.getKey(), fields[idx].getName());
			} else {
				throw new InvalidOperation(VoError.IncorrectParametrs, "Field[" + fte.getValue().name() + "] is not described in the class or id["
						+ id.name() + "] is incorrect");
			}
		}
		return fmap;
	}

	// ====================================================================================================================
	private static List<String> readLines(byte[] buf) throws IOException {

		List<String> lines = new ArrayList<String>();
		int lastPos = 0, pos = 0;
			lastPos = 0;
			if( buf.length > 0 ) 
				do {
					char c;
					if (pos == buf.length || (c = (char) buf[pos] ) == '\n' || c == '\r' ) {
						
						String nl = new String(buf, lastPos, pos - lastPos);
						if (nl.trim().length() > 0)
							lines.add(nl);
	
						// skip emptyLines
						for (lastPos = pos; lastPos + 1 < buf.length/*read*/ && (buf[lastPos] == '\r' || buf[lastPos] == '\n'); lastPos++)
							pos = lastPos;
					}
					if( pos++ == buf.length) 
						break;
					
				} while (true); 

		if (lastPos != pos && lastPos < buf.length) {
			String nl = new String(buf, lastPos, pos - lastPos);
			if (nl.trim().length() > 0)
				lines.add(nl);
		}
		return lines;
	}

}
