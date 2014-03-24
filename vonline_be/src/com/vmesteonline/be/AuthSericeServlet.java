package com.vmesteonline.be;
import org.apache.thrift.TBaseProcessor;
import org.apache.thrift.protocol.TJSONProtocol;

import com.vmesteonline.be.AuthService.Processor;
import com.vmesteonline.be.access.VoServiceMapAccessValidator;

public class AuthSericeServlet extends VoServlet {

	public AuthSericeServlet() {
		super(new TJSONProtocol.Factory());
		AuthServiceImpl servImpl = new AuthServiceImpl();
		serviceImpl = servImpl;
		TBaseProcessor<AuthServiceImpl> proc = new AuthService.Processor<AuthServiceImpl>(servImpl);
		proc.setAccessValidator( new VoServiceMapAccessValidator(servImpl));
		super.setProcessor(proc);
	}

	private static final long serialVersionUID = -9014665255913474234L;
}
