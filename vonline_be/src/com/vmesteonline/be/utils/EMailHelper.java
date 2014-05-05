package com.vmesteonline.be.utils;

import java.io.IOException;

import com.google.appengine.api.mail.*;
import com.google.appengine.api.mail.MailService.Message;

public class EMailHelper {

	public static void sendSimpleEMail(String from, String to, String subject, String body) throws IOException {
		
		MailService mailService = MailServiceFactory.getMailService();
		Message message = new Message(from, to, subject, body);
		message.setHtmlBody(body);
		mailService.send(message);
	}
}
