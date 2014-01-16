package com.vmesteonline.be;

import javax.servlet.http.HttpSession;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.TServlet;

import com.vmesteonline.be.AuthService.Processor;

public class AuthSericeServlet extends VoServlet {
	protected AuthServiceImpl authServiceImpl;

	public AuthSericeServlet() {
		super(new TJSONProtocol.Factory());
		authServiceImpl = new AuthServiceImpl();
		super.setProcessor(new AuthService.Processor<AuthServiceImpl>(
				authServiceImpl));
	}

	@Override
	protected void doPost(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		HttpSession ssn = request.getSession();
		if (ssn != null) {
			System.out.print(ssn.getId() + "\n");
		}

		authServiceImpl.setHttpSess(ssn);
		super.doPost(request, response);
	}
}
