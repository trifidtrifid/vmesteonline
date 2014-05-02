package com.vmesteonline.be;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.protocol.TJSONProtocol;

import com.vmesteonline.be.access.VoServiceRoleAcessValidator;
import com.vmesteonline.be.shop.ShopFEService;

@SuppressWarnings("serial")
public class ShopServiceServlet extends VoServlet {

	public ShopServiceServlet() {
		super(new TJSONProtocol.Factory());
		ShopServiceImpl servImpl = new ShopServiceImpl();
		serviceImpl = servImpl;
		TBaseProcessor<ShopServiceImpl> proc = new ShopFEService.Processor<ShopServiceImpl>(servImpl);
		proc.setAccessValidator( new VoServiceRoleAcessValidator(servImpl));
		super.setProcessor(proc);
	}
}
