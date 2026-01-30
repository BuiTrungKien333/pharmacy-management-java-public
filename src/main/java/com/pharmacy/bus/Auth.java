package com.pharmacy.bus;

import java.util.Set;

import com.pharmacy.entity.Employee;

public class Auth {

	private static Employee employee;

	private static Set<String> permissions;

	public static void loginSuccess(Employee empl, Set<String> per) {
		employee = empl;
		permissions = per;
	}

	public static Employee getCurrentUser() {
		return employee;
	}

	public static void logout() {
		employee = null;
		permissions = null;
	}

	public static boolean hasPermission(String permissionKey) {
		if (permissions == null)
			return false;

		return permissions.contains(permissionKey);
	}

}
