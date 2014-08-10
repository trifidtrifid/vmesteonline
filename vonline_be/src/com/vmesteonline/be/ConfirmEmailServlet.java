package com.vmesteonline.be;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoUser;

public class ConfirmEmailServlet extends HttpServlet {
	
	private static String reqPrefix="/confirm/profile-";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String requestURI = req.getRequestURI();
		if( requestURI.startsWith(reqPrefix) ){
			String[] uidAndConfCode = requestURI.substring(reqPrefix.length()).split(",");
			if( uidAndConfCode.length == 2 ){
				PersistenceManager pm = PMF.getPm();
				try {
					VoUser user = pm.getObjectById(VoUser.class, Long.parseLong(uidAndConfCode[0]));
					if( (""+user.getConfirmCode()).equals(uidAndConfCode[1])){
						user.setEmailConfirmed(true);
						serviceImpl.setSession(req.getSession());
						serviceImpl.getCurrentSession().setUser(user);				
					}
				} catch (Exception e) {					
					e.printStackTrace();
				} finally {
					pm.close();
				}
			}
		}
		resp.sendRedirect("/main");
		return;
	}

	private ServiceImpl serviceImpl;

	public ConfirmEmailServlet() {
		super();
		this.serviceImpl = new ServiceImpl();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8056053718055418720L;

}
