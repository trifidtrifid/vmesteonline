package com.vmesteonline.be;
import org.apache.thrift.protocol.TJSONProtocol;

public class AuthSericeServlet extends VoServlet {


	public AuthSericeServlet() {
		super(new TJSONProtocol.Factory());
		AuthServiceImpl servImpl = new AuthServiceImpl();
		serviceImpl = servImpl;
		super.setProcessor(new AuthService.Processor<AuthServiceImpl>(servImpl));
	}

	private static final long serialVersionUID = -9014665255913474234L;
}
