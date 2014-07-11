package com.vmesteonline.be;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.shop.VoShop;
import com.vmesteonline.be.shop.Shop;

@SuppressWarnings("serial")
public class Main extends HttpServlet {
	
	String landingURL = "voclub.co";

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		URL ru = new URL(request.getRequestURL().toString());
		String host = ru.getHost();
		PersistenceManager pm = PMF.getPm();
		try {
			List<VoShop> shops = (List<VoShop>) pm.newQuery(VoShop.class, "hostName=='"+host+"' && activated==true");
			if( 0!=shops.size() ){
				ServiceImpl si = new ServiceImpl( request.getSession());
				si.setCurrentAttribute( CurrentAttributeType.SHOP.getValue() , shops.get(0).getId() );
			} else {
				shops = (List<VoShop>) pm.newQuery(VoShop.class, "hostName=='www."+host+"' && activated==true");
				if( 0!=shops.size() ){
					ServiceImpl si = new ServiceImpl( request.getSession());
					si.setCurrentAttribute( CurrentAttributeType.SHOP.getValue() , shops.get(0).getId() );
				} else {
					//redirect to landing page
					response.sendRedirect(landingURL);
					return;
				}
			}
		} catch (Exception e){
			
		} finally {
			pm.close();
		}
		super.service(request, response);
	}
	

}
