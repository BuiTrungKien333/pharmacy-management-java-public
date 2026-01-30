package com.pharmacy.connectDB;

import java.sql.Connection;
import java.sql.SQLException;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.config.DbConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectDB {

	private static final Logger log = LoggerFactory.getLogger(ConnectDB.class);

	private static HikariDataSource dataSource;
	
	private static ConnectDB instance = new ConnectDB();
	
	private static final DbConfig cfg = ConfigFactory.create(DbConfig.class);

	private ConnectDB() {
	}

	public static ConnectDB getInstance() {
		return instance;
	}

	public static synchronized void connect() throws SQLException {
		if (dataSource == null || dataSource.isClosed()) {
			try {
				log.info("Initializing HikariCP Connection Pool...");
				HikariConfig config = new HikariConfig();

				config.setJdbcUrl(cfg.url());
				config.setUsername(cfg.user());
				config.setPassword(cfg.password());
				config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

				config.setMaximumPoolSize(10);
				config.setMinimumIdle(2);
				config.setIdleTimeout(600000); // 10 minutes
				config.setConnectionTimeout(30000); // 30 seconds
				config.setMaxLifetime(1800000); // 30 minutes

				config.addDataSourceProperty("cachePrepStmts", "true");
				config.addDataSourceProperty("prepStmtCacheSize", "250");
				config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

				config.setPoolName("PharmacyPool");

				dataSource = new HikariDataSource(config);

				try (Connection testCon = dataSource.getConnection()) {
					if (!testCon.isValid(5)) {
						throw new SQLException("Connection validation failed!");
					}
				}

				log.info("HikariCP Connection Pool initialized successfully!");
			} catch (Exception e) {
				log.error("Failed to initialize Connection Pool!", e);
				throw new SQLException("Database connection error: " + e.getMessage(), e);
			}
		}
	}

	public static void disconnect() {
		if (dataSource != null && !dataSource.isClosed()) {
			dataSource.close();
			log.info("HikariCP Connection Pool closed successfully!");
		}
	}

	public Connection getConnection() throws SQLException {
		if (dataSource == null || dataSource.isClosed())
			connect();

		return dataSource.getConnection();
	}
}