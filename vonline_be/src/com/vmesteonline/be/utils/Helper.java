package com.vmesteonline.be.utils;

import java.util.Map;
import java.util.Map.Entry;

public class Helper {

	
	@SuppressWarnings("unchecked")
	public static <A,B,C,D> Map<C,D> copyTheMap( Map<A,B> inMap, Map<C,D> outMap ){ 
		for(Entry<A, B> e: inMap.entrySet()) 
			outMap.put((C)e.getKey(), (D)e.getValue());
		return outMap;
	}  
	public Helper() {
	
	}

}
