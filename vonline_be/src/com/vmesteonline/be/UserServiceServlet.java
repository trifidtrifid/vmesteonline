package com.vmesteonline.be;

import org.apache.thrift.protocol.TJSONProtocol;

public class UserServiceServlet extends VoServlet {

	public UserServiceServlet() {
		super(new TJSONProtocol.Factory());
		UserServiceImpl servImpl = new UserServiceImpl();
		serviceImpl = servImpl;
		super.setProcessor(new UserService.Processor<UserServiceImpl>(servImpl));
	}

	private static final long serialVersionUID = 988473042573260646L;

}
