package com.nikola.amazon.stanalyzer.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


public class MailSender {
	
	private static Logger LOG = LoggerFactory.getLogger(MailSender.class);
	

	private SimpleMailMessage templateMessage;
	
	private JavaMailSender mailSender;
	
	
	
	public void sendMail(String mail) {
		templateMessage.setTo("nkl.maric@gmail.com");
		templateMessage.setText(mail);
        try{
            this.mailSender.send(templateMessage);
            LOG.info("Mail successfully sent to nkl.maric@gmail.com");
        }
        catch (MailException ex) {
            LOG.error("Error sending mail", ex);
        }
	}
	
	
	

	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	

}
