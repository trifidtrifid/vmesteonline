package com.vmesteonline.be.utils;

import java.io.IOException;

import javax.jdo.Extent;
import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.http.HttpResponse;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.shop.VoShop;

public class UpdateSomething extends HttpServlet {

	@Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		PersistenceManager pm = PMF.getPm();
		try {
			Extent<VoShop> shops = pm.getExtent(VoShop.class);
			shops.iterator().next().setActivated(true);
		} finally {
			pm.close();
		}
		arg1.setStatus(HttpServletResponse.SC_OK);
	}
}
