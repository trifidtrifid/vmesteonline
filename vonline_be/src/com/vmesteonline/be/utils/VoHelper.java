package com.vmesteonline.be.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.VoError;

public class VoHelper {
	// ===================================================================================================================
	public static void copyIfNotNull(Object owner, String fieldName, Object objToCopy) throws NoSuchFieldException {
		if (null != objToCopy) {
			Field field = null;
			try {
				field = owner.getClass().getField(fieldName);
			} catch (SecurityException | NoSuchFieldException e1) {
				//field is not accessible directly
			}
			try {
				if (null != field && field.isAccessible())
					field.set(owner, objToCopy);
				else {
					Method method = owner.getClass().getMethod("set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1),
							new Class[] { objToCopy.getClass() });
					method.invoke(owner, new Object[] { objToCopy });
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new NoSuchFieldException("Failed to set. " + e.getMessage());
			}
		}
	}

	// ===================================================================================================================

	public static void replaceURL(Object owner, String fieldName, String newUrl, long userId, boolean isPublic, PersistenceManager _pm) throws NoSuchFieldException {
		if (null != newUrl) {
			String oldVal = null;
			try {
				Field field = null;
				try {
					field = owner.getClass().getField(fieldName);
					if (field.isAccessible())
						oldVal = field.get(owner).toString();
				} catch (Exception e){
					
					Method method = owner.getClass().getMethod("get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), new Class[] {});
					oldVal = (String)method.invoke(owner, new Object[] {});
				}
				String newStorageUrl;
				if (null == oldVal) { // old URL was not set
					newStorageUrl = StorageHelper.saveImage(newUrl, userId, isPublic, _pm);
				} else {
					newStorageUrl =  StorageHelper.replaceImage(newUrl, oldVal, userId, isPublic, _pm);
				}

				if( null==field ){
					Method method = owner.getClass().getMethod("set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1),
							new Class[] { newStorageUrl.getClass() });
					method.invoke(owner, new Object[] { newStorageUrl });
				} else  
					field.set(owner, newStorageUrl);

			} catch (Exception e) {
				e.printStackTrace();
				throw new NoSuchFieldException("Failed to reset URL: " + e.getMessage());
			}
		}
	}

	// ===================================================================================================================
	public static <A, B> Map<A, B> convertMap(Map<String, String> mapIn, Map<A, B> mapOut, A a, B b) {
		if (null != mapIn) {
			for (Iterator<Entry<String, String>> iterator = mapIn.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, String> e = iterator.next();
				Object key, value;
				if (a instanceof Long)
					key = Long.parseLong(e.getKey());
				else if (a instanceof Integer)
					key = Integer.parseInt(e.getKey());
				else if (a instanceof Byte)
					key = Byte.parseByte(e.getKey());
				else if (a instanceof Double)
					key = Double.parseDouble(e.getKey());
				else if (a instanceof Float)
					key = Float.parseFloat(e.getKey());
				else
					key = e.getKey();

				if (b instanceof Long)
					value = Long.parseLong(e.getValue());
				else if (b instanceof Integer)
					value = Integer.parseInt(e.getValue());
				else if (b instanceof Byte)
					value = Byte.parseByte(e.getValue());
				else if (b instanceof Double)
					value = Double.parseDouble(e.getValue());
				else if (b instanceof Float)
					value = Float.parseFloat(e.getValue());
				else
					value = e.getValue();

				mapOut.put((A) key, (B) value);
			}
		} else {
			return null;
		}
		return mapOut;
	}

	// ===================================================================================================================
	public static <T> List<T> convertSet(List<String> inList, ArrayList<T> outList, T b) {
		if (null != inList) {
			Object value;
			for (String e : inList) {
				if (b instanceof Long)
					value = Long.parseLong(e);
				else if (b instanceof Integer)
					value = Integer.parseInt(e);
				else if (b instanceof Byte)
					value = Byte.parseByte(e);
				else if (b instanceof Double)
					value = Double.parseDouble(e);
				else if (b instanceof Float)
					value = Float.parseFloat(e);
				else
					value = e;
				outList.add((T) value);
			}
		} else {
			return null;
		}
		return outList;
	}
	/* ===============================================================================================================
	//Method converts list of I object from list inList to list of O objects using mutation method of O object that 
	//tooks I objects and has name get<O.simpleName()>*/
	
	public static <I,O> List<O> convertMutableSet(List<I> inList, ArrayList<O> outList, O o) throws InvalidOperation {
		if(null==inList )
			return null;
		if( 0 == inList.size())
			return outList;
		I i0 = inList.get(0);
		try {
			Method method = i0.getClass().getMethod( "get" + o.getClass().getSimpleName(), new Class[] {});
			for (I i : inList) {
				outList.add( (O) method.invoke(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidOperation(VoError.GeneralError, "Class "+i0.getClass().getSimpleName() +" have no acccesible method get"
					+ o.getClass().getSimpleName());
		}
		return outList;
	}
	// ===================================================================================================================

}
