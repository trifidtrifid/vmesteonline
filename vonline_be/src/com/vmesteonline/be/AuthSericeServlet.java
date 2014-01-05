package com.vmesteonline.be;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.TServlet;

public class AuthSericeServlet extends TServlet {
	public AuthSericeServlet() {
		super(
				new AuthService.Processor(
				new AuthServiceImpl()),
				new TJSONProtocol.Factory()
		);
	}
}
