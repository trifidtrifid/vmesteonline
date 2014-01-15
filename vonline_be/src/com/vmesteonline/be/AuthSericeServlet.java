package com.vmesteonline.be;

import javax.servlet.http.HttpSession;

import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.server.TServlet;

public class AuthSericeServlet extends TServlet {
	public AuthSericeServlet() {
		super(new AuthService.Processor(new AuthServiceImpl()),
				new TJSONProtocol.Factory());
	}

	@Override
	protected void doPost(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		HttpSession ssn = request.getSession();
		if (ssn != null) {
			System.out.print(ssn.getId());
		}
		
		super.doPost(request, response);
	}
}
