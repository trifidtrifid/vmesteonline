package com.vmesteonline.be.jdo2.shop.exchange;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TEnum;

public class FieldTranslator {
	
	public static<T extends TEnum>  Map<Integer, String> Translate( int shift, List<T> fields, Object container ) {
		Field[] cfields = container.getClass().getFields();
		Map<Integer, String> fieldsMap = new HashMap<Integer, String>();
		for (int pos = 0; pos< fields.size(); pos++) {
			TEnum field = fields.get(pos);
			fieldsMap.put( pos, cfields[field.getValue() - shift].getName());
		}
		return fieldsMap;
	}
}
