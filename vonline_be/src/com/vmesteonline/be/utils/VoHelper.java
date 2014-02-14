package com.vmesteonline.be.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class VoHelper {
//===================================================================================================================
	public static void copyIfNotNull( Object owner, String fieldName, Object objToCopy) throws NoSuchFieldException {
		if( null!=objToCopy){
			Field field = owner.getClass().getField(fieldName);
			try {
				if( field.isAccessible() )
					field.set(owner, objToCopy);
				else {
					Method method = owner.getClass().getMethod( "set" + 
							Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 
							new Class[]{ objToCopy.getClass()});
					method.invoke(owner, new Object[] {objToCopy});
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new NoSuchFieldException("Failed to set. "+e.getMessage());
			}	
		}
	}
	
	//===================================================================================================================
	
	public static void replaceURL( Object owner, String fieldName, String newUrl ) throws NoSuchFieldException{
		if( null!=newUrl){
			String oldVal = null;
			try {
				Field field = owner.getClass().getField(fieldName);
				if( field.isAccessible() )
					oldVal = field.get(owner).toString();
				else {
					Method method = owner.getClass().getMethod( "get" + 
							Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 
							new Class[]{});
					oldVal = method.invoke(owner, new Object[] {}).toString();
				}
				String newStorageUrl;
				if( null==oldVal) { //old URL was not set
					newStorageUrl = StorageHelper.replaceImage(newUrl, oldVal);
				} else {
					newStorageUrl = StorageHelper.saveImage(newUrl);
				}
				
				if( field.isAccessible() )
					field.set(owner, newStorageUrl);
				else {
					Method method = owner.getClass().getMethod( "set" + 
							Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1), 
							new Class[]{ newStorageUrl.getClass()});
					method.invoke(owner, new Object[] {newStorageUrl});
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new NoSuchFieldException("Failed to reset URL: "+e.getMessage());
			}	
		}
		
	}
}
