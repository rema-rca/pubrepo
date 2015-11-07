package com.nikola.amazon.stanalyzer.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.nikola.amazon.stanalyzer.mail.MailSender;

@Configuration
@PropertySource({ "classpath:stanalyzer-mail.properties" })
public class MailConfig {
	
	@Autowired
	private Environment env;
	
	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setPort(env.getProperty("mail_port",Integer.class));
		sender.setHost(env.getProperty("mail_host"));
		sender.setUsername(env.getProperty("mail_username"));
		sender.setPassword(env.getProperty("mail_password"));
		
		Properties props = new Properties();
		props.put(env.getProperty("mail_property_1"), env.getProperty("mail_property_1_value"));
		props.put(env.getProperty("mail_property_2"), env.getProperty("mail_property_2_value"));
		props.put(env.getProperty("mail_property_3"), env.getProperty("mail_property_3_value"));
		props.put(env.getProperty("mail_property_4"), env.getProperty("mail_property_4_value"));
		props.put(env.getProperty("mail_property_5"), env.getProperty("mail_property_5_value"));
		props.put(env.getProperty("mail_property_6"), env.getProperty("mail_property_6_value"));
		props.put(env.getProperty("mail_property_7"), env.getProperty("mail_property_7_value"));
		
		sender.setJavaMailProperties(props);
		return sender;
	}
	
	@Bean
	public SimpleMailMessage templateMessage() {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(env.getProperty("mail_from"));
		message.setSubject(env.getProperty("mail_subject"));
		return message;
	}
	
	@Bean
	public MailSender stanUpdateMailSender() {
		MailSender mailSender = new MailSender();
		mailSender.setMailSender(mailSender());
		mailSender.setTemplateMessage(templateMessage());
		return mailSender;
	}

}