package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.entity.ProductType;

public class ProductTypeDAO {

	private static final Logger log = LoggerFactory.getLogger(ProductTypeDAO.class);

	public ProductType getProductTypeById(int id) {
		String sql = "select id, ten_loai from tbl_loai_san_pham where id=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new ProductType(id, rs.getString("ten_loai"));
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] Error getting ProductType by ID: {}", id, e);
		}
		return null;
	}

}