package com.pharmacy.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.config.TokenConfig;
import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.dao.AccountDAO;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AccountDaoImpl implements AccountDAO {

	private static final Logger log = LoggerFactory.getLogger(AccountDaoImpl.class);

	private static TokenConfig token = ConfigFactory.create(TokenConfig.class);

	private static final long OTP_EXPIRY_MILLIS = 1000L * 60 * token.expiryMins();

	@Override
	public boolean saveToken(String userName, String otp) {
		String sql = "update tbl_tai_khoan set otp_key=?, expiry_time=? where user_name=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, otp);
			stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			stmt.setString(3, userName);

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				log.info("OTP generated and saved successfully for user: {}", userName);
				return true;
			} else {
				log.warn("Failed to save OTP. User not found: {}", userName);
			}
		} catch (SQLException e) {
			log.error("Database error while saving token for user: {}", userName, e);
		}
		return false;
	}

	@Override
	public boolean checkOtpVerify(String userName, String otp) {
		String sql = "select expiry_time from tbl_tai_khoan t where t.user_name=? and t.otp_key=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, userName);
			stmt.setString(2, otp);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Timestamp createdTime = rs.getTimestamp(1);
					boolean isExpired = isTokenExpired(createdTime);

					if (isExpired) {
						log.warn("OTP verification failed: Token expired for user: {}", userName);
						return false;
					} else {
						log.info("OTP verified successfully for user: {}", userName);
						return true;
					}
				} else {
					log.warn("OTP verification failed: Invalid OTP or Username for user: {}", userName);
				}
			}
		} catch (SQLException e) {
			log.error("Database error while verifying OTP for user: {}", userName, e);
		}
		return false;
	}

	private boolean isTokenExpired(Timestamp createdTime) {
		if (createdTime == null)
			return true;
		long curTime = System.currentTimeMillis();
		long expireAt = createdTime.getTime() + OTP_EXPIRY_MILLIS;
		return curTime > expireAt;
	}

	@Override
	public boolean changePassword(String userName, String newPass) {
		String sql = "update tbl_tai_khoan set password=? where user_name=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, newPass);
			stmt.setString(2, userName);

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				log.info("Password changed successfully for user: {}", userName);
				return true;
			} else {
				log.warn("Failed to change password. User not found: {}", userName);
			}
		} catch (SQLException e) {
			log.error("Database error while changing password for user: {}", userName, e);
		}
		return false;
	}

	@Override
	public boolean login(String user, String pass) {
		String sql = "select password from tbl_tai_khoan where account_locked='0' and user_name=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, user);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String storedHash = rs.getString("password");

					BCrypt.Result result = BCrypt.verifyer().verify(pass.toCharArray(), storedHash);
					if (result.verified) {
						log.info("User logged in successfully: {}", user);
						return true;
					} else {
						log.warn("Login failed: Invalid password for user: {}", user);
					}
				}
			}
		} catch (SQLException e) {
			log.error("Database error during login for user: {}", user, e);
		}
		return false;
	}

}