package com.pharmacy.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.Sources;

@Sources({ "file:db.properties", "classpath:db.properties" })
public interface DbConfig extends Config {

	@Key("PORT")
	@DefaultValue("1433")
	int port();

	@Key("DB_NAME")
	String dbName();

	@Key("DB_USER")
	String user();

	@Key("DB_PASSWORD")
	String password();

	@Key("DB_URL")
	String url();
}