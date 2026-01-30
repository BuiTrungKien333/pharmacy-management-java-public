package com.pharmacy.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.dao.StoreDAO;
import com.pharmacy.entity.Store;

public class StoreDaoImpl implements StoreDAO {

	private static final Logger log = LoggerFactory.getLogger(StoreDaoImpl.class);

	@Override
	public Store getInFoStore(String store_id) {
		String sql = "SELECT TOP 1 * FROM tbl_cua_hang where id = ?";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, store_id);
			
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String id = rs.getString("id");
					String ten_cua_hang = rs.getString("ten_cua_hang");
					String dia_chi = rs.getString("dia_chi");
					String so_dang_ky = rs.getString("so_dang_ky");
					String giay_chung_nhan = rs.getString("giay_chung_nhan");
					
					return new Store(id, ten_cua_hang, dia_chi, so_dang_ky, giay_chung_nhan);
				}
			}
		} catch (SQLException e) {
			log.error("[StoreDAO] Error getting store info for ID: {}", store_id, e);
		}
		return null;
	}

}