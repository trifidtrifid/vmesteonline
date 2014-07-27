package com.vmesteonline.be;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vmesteonline.be.data.PMF;
import com.vmesteonline.be.jdo2.VoSession;

public class CrossAuthFilter implements javax.servlet.Filter {

	private static Logger logger = Logger.getLogger(CrossAuthFilter.class.getName());
	
		@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	/**
	 * 1. ci (come in) when we come into a host check that session set if so - auth the session on Referer if it's one of our host
	 * 2. To check - redirect request back to Referer by 'co' request to confirm that user authorized/ Confirm req contains id of session at target host
	 * 3. REferer got confirm request and if user authorized here it autorithe session from the request and redirects back to Referer     
	 */
	
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain chain) throws IOException, ServletException {
		
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
		chain.doFilter(arg0, arg1);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
	
	

}
