package com.vmesteonline.be;

import org.apache.thrift.protocol.TJSONProtocol;

public class AuthSericeServlet extends VOTServlet {
	ServiceImpl si;
	public AuthSericeServlet() {
		super(new TJSONProtocol.Factory());
		AuthServiceImpl asi = new AuthServiceImpl();
		super.setProc(new AuthService.Processor(asi));
		si = asi;
	}
}

