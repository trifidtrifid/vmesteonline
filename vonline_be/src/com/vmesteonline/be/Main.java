package com.vmesteonline.be;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.utils.SystemProperty;
import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.shop.VoShop;

@SuppressWarnings("serial")
public class Main implements javax.servlet.Filter {
	
	private static String landingURL = "voclub.co";
	private static String postfix; 
	private static Logger logger = Logger.getLogger(Main.class.getName());

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		
		if ( SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
			HttpServletRequest request = (HttpServletRequest) srequest;
			HttpServletResponse response = (HttpServletResponse) sresponse;
			
			String landingPage = "http://"+landingURL+postfix + ":" +request.getServerPort();
			String host = request.getServerName();
			
			if( host.contains( landingURL+postfix )){
				chain.doFilter( srequest, sresponse );
				ServiceImpl si = new ServiceImpl( request.getSession() );
				try {
					si.setCurrentAttribute( CurrentAttributeType.SHOP.getValue(), 0 );
				} catch (InvalidOperation e) {
					e.printStackTrace();
				}
				return;
			}
			
			if( postfix.length() > 0 && host.endsWith(postfix)) //remove local postfix if it's presented
				host = host.substring(0, host.length() - postfix.length());
			
			PersistenceManager pm = PMF.getPm();
			try {
				List<VoShop> shops = (List<VoShop>) pm.newQuery(VoShop.class, "hostName=='"+host+"' && activated==true").execute();
				if( 0!=shops.size() ){
					VoShop voShop = shops.get(0);
					ServiceImpl si = new ServiceImpl( request.getSession() );
					si.setCurrentAttribute( CurrentAttributeType.SHOP.getValue() , voShop.getId() );
					logger.fine("Found shop "+voShop.getName()+" by Hostname '"+host+"' Current shop set to: "+voShop.getId());
				} else {
					shops = (List<VoShop>) pm.newQuery(VoShop.class, "hostName=='www."+host+"' && activated==true").execute();
					if( 0!=shops.size() ){
						VoShop voShop = shops.get(0);
						ServiceImpl si = new ServiceImpl( request.getSession());
						si.setCurrentAttribute( CurrentAttributeType.SHOP.getValue() , voShop.getId() );
						logger.fine("Found shop "+voShop.getName()+" by Hostname '"+host+"' Current shop set to: "+voShop.getId());
					} else {
						//redirect to landing page
						response.sendRedirect(landingPage);
						logger.fine("URL not looks like shop's address so go to landing page '"+landingPage+"'");
						return;
					}
				}
				
			} catch (Exception e){
				logger.fine( "Failed to get shop for host '"+host+"' request URL: "+request.getRequestURI()+ " exc:"+e);
				response.sendRedirect(landingPage);
				return;
			} finally {
				pm.close();
			}
		}
		chain.doFilter( srequest, sresponse );
	}

	@Override
	public void init(FilterConfig conf) throws ServletException {
		landingURL = conf.getInitParameter("landingURL");
		String postfixStr = conf.getInitParameter("localPostfix");
		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production){
			postfix = postfixStr;
		} else {
			postfix = "";
		}
	}
}
