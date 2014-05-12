package com.vmesteonline.be.utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.appengine.api.mail.*;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.utils.SystemProperty;

public class EMailHelper {
	
	private static Logger logger = Logger.getLogger(EMailHelper.class);
	
	private static String fromAddress = "Во! <trifid@vmesteonline.ru>";

	public static void sendSimpleEMail( String to, String subject, String body) throws IOException {
		
		logger.debug("Try to send MEssage to '"+to+"' from '"+fromAddress+"' Subj: '"+subject+"'");
		MailService mailService = MailServiceFactory.getMailService();
		Message message = new Message(fromAddress, to, subject, body);
		message.setHtmlBody(body);
		mailService.send(message);
		logger.debug("MEssage sent to '"+to+"' from '"+fromAddress+"' Subj: '"+subject+"'");
		if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
			logger.debug("MEssage body was: "+body);
		}
	}
}
