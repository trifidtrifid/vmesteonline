package com.vmesteonline.be;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import com.vmesteonline.be.jdo2.VoSession;
import com.vmesteonline.be.jdo2.shop.VoShop;

@SuppressWarnings("serial")
public class Main implements javax.servlet.Filter {
	
	private static String landingURL = "voclub.co";
	private static String shopContext = "shop";
	
	private static String postfix; 
	private static Logger logger = Logger.getLogger(Main.class.getName());

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		
		if ( SystemProperty.environment.value() == SystemProperty.Environment.Value.Production){
			
			doAuthFilter(srequest, sresponse);
			
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
					if( shopContext !=null && null == request.getRequestURI() || 0==request.getRequestURI().length() || 
							request.getRequestURI().equals("/")){
						String shopHome = "http://"+host+"/"+shopContext;
						logger.fine("Send redirect to "+shopHome);
						response.sendRedirect( shopHome);
					} else {
						logger.fine("No redirect required shopContext:"+shopContext+" request.getRequestURI():"+request.getRequestURI());
					}
						
				} else {
					shops = (List<VoShop>) pm.newQuery(VoShop.class, "hostName=='www."+host+"' && activated==true").execute();
					if( 0!=shops.size() ){
						VoShop voShop = shops.get(0);
						ServiceImpl si = new ServiceImpl( request.getSession());
						si.setCurrentAttribute( CurrentAttributeType.SHOP.getValue() , voShop.getId() );
						logger.fine("Found shop "+voShop.getName()+" by Hostname '"+host+"' Current shop set to: "+voShop.getId());
						if( shopContext !=null && null == request.getRequestURI() || 0==request.getRequestURI().length() || 
								request.getRequestURI().equals("/") ){
							String shopHome = "http://www."+host+"/"+shopContext;
							logger.fine("Send redirect to "+shopHome);
							response.sendRedirect( shopHome);
						} else {
							logger.fine("No redirect required shopContext:"+shopContext+" request.getRequestURI():"+request.getRequestURI());
						}
						
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
		String src = conf.getInitParameter("shopRootContext");
		if( null!=src ) shopContext = src;
		
		String postfixStr = conf.getInitParameter("localPostfix");
		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production){
			postfix = postfixStr;
		} else {
			postfix = "";
		}
	}
	
	public void doAuthFilter(ServletRequest arg0, ServletResponse arg1) throws IOException, ServletException {
		
		String rt = arg0.getParameter("rt"); //type of request, auth or confirm
		HttpServletRequest req = (HttpServletRequest) arg0;
		
		String si = req.getParameter("si");
		String bu = req.getParameter("bu");
		
		String ref = req.getHeader("Referer");
		
		logger.fine("Got 'rt' = '"+rt+"' si='"+si+"' ref='"+ref+"' bu='"+bu+"'");
		
		if( null!=rt){
			
			HttpServletResponse resp = (HttpServletResponse) arg1;
			
			
			if( null!=ref || null!=(ref=bu)) try {
				
				URL refUrl = new URL(ref);//got exception if string is not like URL
				
				if( !refUrl.getHost().equals(req.getServerName()) || null!=bu ){ //check if HOST changed
					
					if("ci".equals(rt)){  //redirect to confirm auth
						String confUrl = refUrl+"?rt=co&si="+req.getSession().getId()+"&bu="+URLEncoder.encode(req.getRequestURL().toString());
						logger.fine("Got 'ci' request send redirect to '"+confUrl+"'");
						resp.sendRedirect(confUrl);
						
					} else if("co".equals(rt) && null!=si){

						String confUrl = refUrl+"?rt=co&si="+req.getSession().getId();
						logger.fine("Got 'co' check the session");

						PersistenceManager pm = PMF.getPm();
						try {
							VoSession cs = pm.getObjectById(VoSession.class,req.getSession().getId());
							long userId = cs.getUserId();
							VoSession rs = pm.getObjectById(VoSession.class, ""+si);
							rs.setUserId(userId);
							logger.fine("User id="+userId+" successfully attached to session '"+si+"' from session '"+req.getSession().getId()+"' REdirect req to '"+URLDecoder.decode(bu)+"'");
							resp.sendRedirect( URLDecoder.decode(bu) );
							

							
						} catch( Exception e){
							logger.warning("Failed to confirm user."+e.getMessage());
							resp.sendRedirect(ref);
							
						} finally {
							pm.close();
						}		
					}
				}
			} catch (Exception e){
				e.printStackTrace();
				logger.warning("Failed filter." + e.getMessage());
			}
		}
	}
}
