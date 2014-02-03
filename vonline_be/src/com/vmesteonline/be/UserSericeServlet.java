package com.vmesteonline.be;

import org.apache.thrift.protocol.TJSONProtocol;

public class UserSericeServlet extends VoServlet {

	public UserSericeServlet() {
		super(new TJSONProtocol.Factory());
		AuthServiceImpl servImpl = new AuthServiceImpl();
		serviceImpl = servImpl;
		super.setProcessor(new AuthService.Processor<AuthServiceImpl>(servImpl));
	}

	private static final long serialVersionUID = 988473042573260646L;

}
