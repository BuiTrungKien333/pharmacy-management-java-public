package com.pharmacy.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources("classpath:application.properties")
public interface TokenConfig extends Config {

	@Key("otp.expiry.mins")
	long expiryMins();
	
}