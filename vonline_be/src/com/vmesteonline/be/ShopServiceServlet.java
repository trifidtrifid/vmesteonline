package com.vmesteonline.be;

import org.apache.thrift.protocol.TJSONProtocol;

import com.vmesteonline.be.shop.ShopService;

@SuppressWarnings("serial")
public class ShopServiceServlet extends VoServlet {

	public ShopServiceServlet() {
		super(new TJSONProtocol.Factory());
		ShopServiceImpl servImpl = new ShopServiceImpl();
		serviceImpl = servImpl;
		super.setProcessor(new ShopService.Processor<ShopServiceImpl>(servImpl));
	}
}
