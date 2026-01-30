package com.pharmacy.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {
	
	public static String createPasswordHash(String pass) {
		return BCrypt.withDefaults().hashToString(6, pass.toCharArray());
	}
	
}
