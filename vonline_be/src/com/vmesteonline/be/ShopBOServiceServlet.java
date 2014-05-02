package com.vmesteonline.be;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.protocol.TJSONProtocol;

import com.vmesteonline.be.access.VoServiceRoleAcessValidator;
import com.vmesteonline.be.shop.bo.ShopBOService;

@SuppressWarnings("serial")
public class ShopBOServiceServlet extends VoServlet {

	public ShopBOServiceServlet() {
		super(new TJSONProtocol.Factory());
		ShopBOServiceImpl servImpl = new ShopBOServiceImpl();
		serviceImpl = servImpl;
		TBaseProcessor<ShopBOServiceImpl> proc = new ShopBOService.Processor<ShopBOServiceImpl>(servImpl);
		proc.setAccessValidator( new VoServiceRoleAcessValidator(servImpl));
		super.setProcessor(proc);
	}
}