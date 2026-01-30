package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;

public class PermissionDAO {

	private static final Logger log = LoggerFactory.getLogger(PermissionDAO.class);

	public Set<String> getPermissionByEmplId(String id) {
		Set<String> permissions = new HashSet<>();
		String sql = """
				select distinct p.permission_key
				from tbl_user_role ur
				inner join tbl_role_permission rp on ur.role_id = rp.role_id
				inner join tbl_permission p on rp.permission_id = p.id
				where ur.user_id=?
				""";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					permissions.add(rs.getString("permission_key"));
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] Failed to get permissions for user ID: {}", id, e);
		}

		return permissions;
	}

}