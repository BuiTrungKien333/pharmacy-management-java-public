package com.pharmacy.dao;

public interface AccountDAO {

	boolean login(String user, String pass);

	boolean saveToken(String user_name, String otp);

	boolean checkOtpVerify(String user_name, String otp);

	boolean changePassword(String user_name, String newPass);

}
