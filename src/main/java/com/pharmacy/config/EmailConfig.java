package com.pharmacy.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("classpath:smtp.properties")
public interface EmailConfig extends Config {

	@Key("mail.from")
	String fromEmail();
	
	@Key("mail.user")
	String username();

	@Key("mail.password")
	String password();

	@Key("mail.smtp.host")
	String host();

	@Key("mail.smtp.port")
	String port();

	@Key("mail.smtp.starttls.enable")
	String enable();
	
	@Key("mail.smtp.auth")
	String auth();
	
}
