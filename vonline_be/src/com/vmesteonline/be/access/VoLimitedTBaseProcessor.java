package com.vmesteonline.be.access;

import java.util.Map;

import org.apache.thrift.ProcessFunction;
import org.apache.thrift.TBase;
import org.apache.thrift.TBaseProcessor;

import com.vmesteonline.be.InvalidOperation;
import com.vmesteonline.be.ServiceImpl;

public class VoLimitedTBaseProcessor<I> extends TBaseProcessor<I> {
	private ServiceImpl si;

	protected VoLimitedTBaseProcessor(ServiceImpl si, I iface, Map<String, ProcessFunction<I, ? extends TBase>> processFunctionMap) {
		super(iface, processFunctionMap);
		this.si = si; 
	}

	@Override
	protected boolean checkAccessRights(String functionName) {
		try {
			si.getCurrentUser(null);
		} catch (InvalidOperation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
