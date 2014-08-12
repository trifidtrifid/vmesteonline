package com.vmesteonline.be.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.logging.Logger;

import com.google.appengine.api.mail.*;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.utils.SystemProperty;
import com.vmesteonline.be.jdo2.VoUser;

public class EMailHelper {
	
	private static Logger logger = Logger.getLogger(EMailHelper.class.getName());
	
	private static String fromAddress = "ВместеОнлайн.ру <info@vmesteonline.ru>";

	public static void sendSimpleEMail( String from, String to, String subject, String body) throws IOException {
		
		logger.fine("Try to send MEssage to '"+to+"' from '"+fromAddress+"' Subj: '"+subject+"'");
		MailService mailService = MailServiceFactory.getMailService();
		Message message = new Message(from, to, subject, body);
		message.setBcc( Arrays.asList( new String[]{fromAddress}));
		message.setHtmlBody(body);
		mailService.send(message);
		logger.fine("MEssage sent to '"+to+"' from '"+fromAddress+"' Subj: '"+subject+"'");
		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
			logger.fine("MEssage body was: "+body);
		}
	}
	
	public static void sendSimpleEMail( VoUser to, String subject, String body) throws IOException {
		sendSimpleEMail( URLEncoder.encode(to.getName() + " " + to.getLastName(), "UTF-8") + " <"+to.getEmail()+">", 
				subject, body);
	}
	
	public static void sendSimpleEMail( String to, String subject, String body) throws IOException {
		sendSimpleEMail( fromAddress, to, subject, body );
	}
		
}
