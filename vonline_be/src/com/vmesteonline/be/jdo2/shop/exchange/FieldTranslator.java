package com.vmesteonline.be.jdo2.shop.exchange;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.thrift.TEnum;

public class FieldTranslator {
	
	public static<T extends TEnum>  Map<Integer, String> Translate( int shift, Map<Integer,T> fieldm, Object container ) {
		Field[] cfields = container.getClass().getFields();
		Map<Integer, String> fieldsMap = new HashMap<Integer, String>();
		for( Entry<Integer, T> fe: fieldm.entrySet()){
			fieldsMap.put( fe.getKey(), cfields[ fe.getValue().getValue() - shift].getName());
		}
		return fieldsMap;
	}
}
