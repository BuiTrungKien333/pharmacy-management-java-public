package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.entity.BatchStatus;

public class BatchStatusDAO {

	public BatchStatus getShipmentStatusById(int id) {
		String sql = "select id, ten_trang_thai from tbl_trang_thai_lo where id=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return new BatchStatus(id, rs.getString("ten_trang_thai"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
