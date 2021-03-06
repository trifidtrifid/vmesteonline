package com.vmesteonline.be;

import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.protocol.TJSONProtocol;

import com.vmesteonline.be.access.VoServiceMapAccessValidator;


public class UserServiceServlet extends VoServlet {

	public UserServiceServlet() {
		super(new TJSONProtocol.Factory());
		UserServiceImpl servImpl = new UserServiceImpl();
		serviceImpl = servImpl;
		TBaseProcessor<UserServiceImpl> proc = new UserService.Processor<UserServiceImpl>(servImpl);
		proc.setAccessValidator( new VoServiceMapAccessValidator(servImpl));
		super.setProcessor(proc);
	}

	private static final long serialVersionUID = 988473042573260646L;

}
