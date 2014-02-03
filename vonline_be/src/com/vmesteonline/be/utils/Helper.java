package com.vmesteonline.be.utils;

import java.util.Map;
import java.util.Map.Entry;

public class Helper {

	
	@SuppressWarnings("unchecked")
	public static <A,B,D> Map<B,D> ccopyTheMap( Map<A,D> inMap, Map<B,D> outMap){
		for(Entry<A, D> e: inMap.entrySet()){ 
			outMap.put((B)e.getKey(), e.getValue());
		}
		return outMap;
	}
	public Helper() {
	
	}

}
