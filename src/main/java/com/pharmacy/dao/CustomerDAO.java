package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.CustomerRank;

public class CustomerDAO {

	private static final Logger log = LoggerFactory.getLogger(CustomerDAO.class);

	public List<Customer> getAllCustomer() {
		String sql = """
				select kh.id, kh.ho_ten, kh.so_dien_thoai, kh.hang_thanh_vien as htv_id, kh.diem_tich_luy, htv.hang_thanh_vien
				from tbl_khach_hang kh
				inner join tbl_hang_thanh_vien htv
				on kh.hang_thanh_vien = htv.id
				""";
		List<Customer> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Customer cus = new Customer();
				cus.setMaKhachHang(rs.getInt("id"));
				cus.setTenKhachHang(rs.getString("ho_ten"));
				cus.setDiemTichLuy(rs.getInt("diem_tich_luy"));
				cus.setSoDienThoai(rs.getString("so_dien_thoai"));

				CustomerRank customerRank = new CustomerRank();
				customerRank.setId(rs.getInt("htv_id"));
				customerRank.setTenHangTV(rs.getString("hang_thanh_vien"));
				cus.setCustomerRank(customerRank);

				list.add(cus);
			}

		} catch (SQLException e) {
			log.error("[CustomerDAO] getAllCustomer failed", e);
		}

		return list;
	}

	public Customer findCustomerByPhone(String soDienThoai) {
		String sql = "select kh.id, kh.ho_ten, kh.hang_thanh_vien, kh.diem_tich_luy, htv.hang_thanh_vien as ten_htv "
				+ "from tbl_khach_hang kh inner join tbl_hang_thanh_vien htv on kh.hang_thanh_vien = htv.id"
				+ " where so_dien_thoai=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, soDienThoai);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int CusId = rs.getInt("id");
					String name = rs.getString("ho_ten");
					int diemTichLuy = rs.getInt("diem_tich_luy");

					CustomerRank customerRank = new CustomerRank();
					customerRank.setId(rs.getInt("hang_thanh_vien"));
					customerRank.setTenHangTV(rs.getString("ten_htv"));

					Customer customer = new Customer();
					customer.setMaKhachHang(CusId);
					customer.setTenKhachHang(name);
					customer.setDiemTichLuy(diemTichLuy);
					customer.setCustomerRank(customerRank);
					customer.setSoDienThoai(soDienThoai);

					return customer;
				}
			}
		} catch (SQLException e) {
			log.error("[CustomerDAO] findCustomerByPhone failed for {}: {}", soDienThoai, e.getMessage(), e);
		}

		return null;
	}

	public Customer getCustomerByPhone(String soDienThoai) {
		String sql = "select id, diem_tich_luy from tbl_khach_hang where so_dien_thoai=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, soDienThoai);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Customer customer = new Customer();
					customer.setMaKhachHang(rs.getInt("id"));
					customer.setDiemTichLuy(rs.getInt("diem_tich_luy"));
					customer.setSoDienThoai(soDienThoai);

					return customer;
				}
			}
		} catch (SQLException e) {
			log.error("[CustomerDAO] getCustomerByPhone failed for {}: {}", soDienThoai, e.getMessage(), e);
		}

		return null;
	}

	public boolean addCustomer(Connection con, Customer cus) {
		String sql = "INSERT INTO tbl_khach_hang (ho_ten, so_dien_thoai, diem_tich_luy) VALUES (?, ?, ?)";

		try (PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, cus.getTenKhachHang());
			stmt.setString(2, cus.getSoDienThoai());
			stmt.setInt(3, cus.getDiemTichLuy());

			int rows = stmt.executeUpdate();
			if (rows > 0) {
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						cus.setMaKhachHang(rs.getInt(1));
						return true;
					}
				}
			}

		} catch (SQLException e) {
			log.error("[CustomerDAO] addCustomer failed", e);
		}
		return false;
	}

	public boolean updateCustomer(Connection con, Customer customer) {
		String sql = "update tbl_khach_hang set diem_tich_luy=? where id=?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setInt(1, customer.getDiemTichLuy());
			stmt.setInt(2, customer.getMaKhachHang());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			log.error("[CustomerDAO] updateCustomer failed for ID {}: {}", customer.getMaKhachHang(), e.getMessage(),
					e);
		}
		return false;
	}

	public List<Customer> findCustomerByPhoneNum(String phoneNum) {
		String sql = "SELECT kh.id, kh.ho_ten, kh.so_dien_thoai, kh.diem_tich_luy, htv.hang_thanh_vien FROM tbl_khach_hang kh join tbl_hang_thanh_vien htv on kh.hang_thanh_vien = htv.id where kh.so_dien_thoai like ?";
		List<Customer> ds = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, phoneNum + "%");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Customer cus = new Customer();
					cus.setMaKhachHang(rs.getInt("id"));
					cus.setTenKhachHang(rs.getString("ho_ten"));
					CustomerRank customerRank = new CustomerRank();
					customerRank.setTenHangTV(rs.getString("hang_thanh_vien"));
					cus.setCustomerRank(customerRank);
					cus.setDiemTichLuy(rs.getInt("diem_tich_luy"));
					cus.setSoDienThoai(rs.getString("so_dien_thoai"));

					ds.add(cus);
				}
			}
		} catch (SQLException e) {
			log.error("[CustomerDAO] findCustomerByPhoneNum failed", e);
		}
		return ds;
	}

}