package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.entity.NhaCungCap;

public class NhaCungCapDAO {

	private static final Logger log = LoggerFactory.getLogger(NhaCungCapDAO.class);

	public NhaCungCap getNCCById(int id) {
		String sql = "select id, ma_nha_may, ten_nha_cung_cap from tbl_nha_cung_cap where id=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return new NhaCungCap(rs.getInt("id"), rs.getString("ma_nha_may"),
							rs.getString("ten_nha_cung_cap"));
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] Error getting Supplier by ID: {}", id, e);
		}
		return null;
	}

	public List<NhaCungCap> getAllNhaCungCap() {
		String sql = "select id, ma_nha_may, ten_nha_cung_cap from tbl_nha_cung_cap";
		List<NhaCungCap> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(new NhaCungCap(rs.getInt("id"), rs.getString("ma_nha_may"), rs.getString("ten_nha_cung_cap")));
			}
		} catch (SQLException e) {
			log.error("[DAO] Error getting all basic Suppliers", e);
		}

		return list;
	}

	public List<NhaCungCap> getAllInfoNhaCungCap() {
		String sql = "SELECT * FROM tbl_nha_cung_cap";
		List<NhaCungCap> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				int id = rs.getInt("id");
				String ma_nha_may = rs.getString("ma_nha_may");
				String ten_nha_cung_cap = rs.getString("ten_nha_cung_cap");
				String dia_chi = rs.getString("dia_chi");
				String so_dien_thoai = rs.getString("so_dien_thoai");
				String ma_so_thue = rs.getString("ma_so_thue");
				String email = rs.getString("email");
				String website = rs.getString("website");
				String ghi_chu = rs.getString("ghi_chu");

				list.add(new NhaCungCap(id, ma_nha_may, ten_nha_cung_cap, dia_chi, so_dien_thoai, ma_so_thue, email,
						website, ghi_chu));
			}
		} catch (SQLException e) {
			log.error("[DAO] Error getting all detailed Suppliers", e);
		}
		return list;
	}

	public NhaCungCap findSupplierByPhone(String phone) {
		String sql = "SELECT * FROM tbl_nha_cung_cap where so_dien_thoai = ?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, phone);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					NhaCungCap nhaCungCap = new NhaCungCap();
					nhaCungCap.setDiaChi(rs.getString("dia_chi"));
					nhaCungCap.setEmail(rs.getString("email"));
					nhaCungCap.setGhiChu(rs.getString("ghi_chu"));
					nhaCungCap.setId(rs.getInt("id"));
					nhaCungCap.setMaNhaMay(rs.getString("ma_nha_may"));
					nhaCungCap.setMaSoThue(rs.getString("ma_so_thue"));
					nhaCungCap.setSoDienThoai(rs.getString("so_dien_thoai"));
					nhaCungCap.setTenNhaCungCap(rs.getString("ten_nha_cung_cap"));
					nhaCungCap.setWebsite(rs.getString("website"));

					return nhaCungCap;
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] Error finding Supplier by phone: {}", phone, e);
		}

		return null;
	}

	public boolean findSupplierByEmail(String email) {
		String sql = "SELECT 1 FROM tbl_nha_cung_cap where email = ?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, email);

			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			log.error("[DAO] Error finding Supplier by email: {}", email, e);
		}

		return false;
	}

	public List<NhaCungCap> findSupplierByFactoryCode(String factory_code) {
		List<NhaCungCap> ds = new ArrayList<>();
		String sql = "SELECT * FROM tbl_nha_cung_cap where ma_nha_may like ?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, factory_code + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					NhaCungCap nhaCungCap = new NhaCungCap();
					nhaCungCap.setDiaChi(rs.getString("dia_chi"));
					nhaCungCap.setEmail(rs.getString("email"));
					nhaCungCap.setGhiChu(rs.getString("ghi_chu"));
					nhaCungCap.setId(rs.getInt("id"));
					nhaCungCap.setMaNhaMay(rs.getString("ma_nha_may"));
					nhaCungCap.setMaSoThue(rs.getString("ma_so_thue"));
					nhaCungCap.setSoDienThoai(rs.getString("so_dien_thoai"));
					nhaCungCap.setTenNhaCungCap(rs.getString("ten_nha_cung_cap"));
					nhaCungCap.setWebsite(rs.getString("website"));

					ds.add(nhaCungCap);
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] Error finding Supplier by factory code: {}", factory_code, e);
		}

		return ds;
	}

	public boolean updateInforSuplier(NhaCungCap ncc) {
		String sql = "UPDATE tbl_nha_cung_cap SET ma_nha_may = ?, ten_nha_cung_cap = ?, dia_chi = ?, so_dien_thoai = ?, ma_so_thue = ?, email = ?, website = ?, ghi_chu = ? WHERE id = ? ";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, ncc.getMaNhaMay());
			stmt.setString(2, ncc.getTenNhaCungCap());
			stmt.setString(3, ncc.getDiaChi());
			stmt.setString(4, ncc.getSoDienThoai());
			stmt.setString(5, ncc.getMaSoThue());
			stmt.setString(6, ncc.getEmail());
			stmt.setString(7, ncc.getWebsite());
			stmt.setString(8, ncc.getGhiChu());
			stmt.setInt(9, ncc.getId());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			log.error("[DAO] Error updating Supplier: {}", ncc.getTenNhaCungCap(), e);
		}
		return false;
	}

	public boolean insertSuplier(NhaCungCap ncc) {
		String sql = "INSERT INTO tbl_nha_cung_cap (ma_nha_may, ten_nha_cung_cap, dia_chi, so_dien_thoai, ma_so_thue, email, website, ghi_chu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, ncc.getMaNhaMay());
			stmt.setString(2, ncc.getTenNhaCungCap());
			stmt.setString(3, ncc.getDiaChi());
			stmt.setString(4, ncc.getSoDienThoai());
			stmt.setString(5, ncc.getMaSoThue());
			stmt.setString(6, ncc.getEmail());
			stmt.setString(7, ncc.getWebsite());
			stmt.setString(8, ncc.getGhiChu());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			log.error("[DAO] Error inserting Supplier: {}", ncc.getTenNhaCungCap(), e);
		}
		return false;
	}

}