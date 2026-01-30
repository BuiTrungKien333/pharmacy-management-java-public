package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.dto.RefundReqDTO;
import com.pharmacy.entity.InvoiceDetailReturn;

public class RefundDetailDAO {

	private static final Logger log = LoggerFactory.getLogger(RefundDetailDAO.class);

	public boolean insert(Connection con, String maHDTra, RefundReqDTO refund) {
		String sql = "insert into tbl_chi_tiet_hoa_don_tra (ma_hd_tra, ma_sp, so_lo, so_luong, don_gia, huong_xu_ly, ly_do) values (?, ?, ?, ?, ?, ?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maHDTra);
			stmt.setInt(2, refund.getMaSP());
			stmt.setString(3, refund.getSoLo());
			stmt.setInt(4, refund.getSoLuongTra());
			stmt.setDouble(5, refund.getDonGia());
			stmt.setString(6, "Chờ xử lý");
			stmt.setString(7, "");

			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			log.error("[DAO] insert failed. ReturnID: {}, ProductID: {}, Batch: {}. Error: {}", maHDTra,
					refund.getMaSP(), refund.getSoLo(), e.getMessage(), e);
		}

		return false;
	}

	public boolean update(Connection con, InvoiceDetailReturn inv) {
		String sql = "update tbl_chi_tiet_hoa_don_tra set trang_thai_id=1, huong_xu_ly=?, ly_do=? where id=?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, inv.getHuongXuLy());
			stmt.setString(2, inv.getLyDo());
			stmt.setInt(3, inv.getId());

			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			log.error("[DAO] update status failed for DetailID: {}. Error: {}", inv.getId(), e.getMessage(),
					e);
		}

		return false;
	}

}