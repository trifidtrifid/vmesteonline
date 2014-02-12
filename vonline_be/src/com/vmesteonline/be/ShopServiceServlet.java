package com.vmesteonline.be;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import com.vmesteonline.be.shop.ShopService;

public class ShopServiceServlet extends VoServlet {

	public ShopServiceServlet() {
		super(new TJSONProtocol.Factory());
		ShopServiceImpl servImpl = new ShopServiceImpl();
		serviceImpl = servImpl;
		super.setProcessor(new ShopService.Processor<ShopServiceImpl>(servImpl));
	}
}
