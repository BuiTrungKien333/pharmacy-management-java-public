package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.dto.PhanBoLoDTO;

public class InvoiceDetailDAO {

	private static final Logger log = LoggerFactory.getLogger(InvoiceDetailDAO.class);

	public boolean insert(Connection con, String maHoaDon, PhanBoLoDTO item) {
		String sql = "insert into tbl_chi_tiet_hoa_don (ma_hd, ma_sp, so_lo, so_luong, don_gia, gia_goc) values (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, maHoaDon);
			stmt.setInt(2, item.getMaSanPham());
			stmt.setString(3, item.getSoLo());
			stmt.setInt(4, item.getSoLuongCanLay());
			stmt.setDouble(5, item.getGiaBinhQuan());
			stmt.setDouble(6, item.getGiaGoc());

			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			log.error("[DAO] Insert detail failed. Inv: {}, Batch: {}. Error: {}", maHoaDon,
					item.getSoLo(), e.getMessage(), e);
			return false;
		}
	}

}