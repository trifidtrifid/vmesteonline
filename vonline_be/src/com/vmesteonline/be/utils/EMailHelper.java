package com.vmesteonline.be.utils;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Logger;

import org.hamcrest.core.IsInstanceOf;

import com.google.appengine.api.utils.SystemProperty;
import com.vmesteonline.be.jdo2.VoUser;

public class EMailHelper {
	
	private static Logger logger = Logger.getLogger(EMailHelper.class.getName());
	
	private static String fromAddress = "ВместеОнлайн.ру <info@vmesteonline.ru>";

	public static void sendSimpleEMail( String from, String to, String subject, String body) throws IOException {
		
		logger.fine("Try to send MEssage to '"+to+"' from '"+fromAddress+"' Subj: '"+subject+"'");
		
		try {
	    URL url = new URL("https://mail.vmesteonline.ru/send.php");
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("POST");
	    
	    String urlParameters =  "from="+ URLEncoder.encode(fromAddress, "UTF-8") 
	    		+"&to="+URLEncoder.encode(to, "UTF-8")
	    		+"&cc="+URLEncoder.encode(fromAddress, "UTF-8")
	    		+"&subject="+URLEncoder.encode(subject, "UTF-8")
	    		+"&body="+URLEncoder.encode(body, "UTF-8");
	    con.addRequestProperty("Content-Length", ""+urlParameters.length());
	    
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
	    
	 
	    InputStream is = con.getInputStream();
	    StringBuffer sb = new StringBuffer();
	    byte[] buff = new byte[1024];
	    int read;
	    while( -1 != (read = is.read(buff))){
	    	sb.append( new String(buff, 0, read));
	    }
	    if( sb.toString().equals("OK")){
	    	logger.fine("MEssage sent to '"+to+"' from '"+fromAddress+"' Subj: '"+subject+"'");
	    	if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
	  			logger.fine("MEssage body was: "+body);
	  		}
	    	return;
	    } else {
	    	logger.fine("MEssage script returns:'"+sb+"'");
	    	
	    }
	    
	    
		} catch (Exception e) {
			logger.severe("Failed to send message '"+to+"' from '"+fromAddress+"' " +e.getMessage());
		} 
		
		/*MailService mailService = MailServiceFactory.getMailService();
		Message message = new Message(from, to, subject, body);
		message.setBcc( Arrays.asList( new String[]{fromAddress}));
		message.setHtmlBody(body);
		mailService.send(message);
		logger.fine("MEssage sent to '"+to+"' from '"+fromAddress+"' Subj: '"+subject+"'");
  	if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
			logger.fine("MEssage body was: "+body);
		}*/
	}
	
	public static void sendSimpleEMail( VoUser to, String subject, String body) throws IOException {
		sendSimpleEMail( to.getName() + " " + to.getLastName() + " <"+to.getEmail()+">", 
				subject, body);
	}
	
	public static void sendSimpleEMail( String to, String subject, String body) throws IOException {
		sendSimpleEMail( fromAddress, to, subject, body );
	}
		
}
