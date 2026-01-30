package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.CustomerRank;
import com.pharmacy.entity.Voucher;

public class VoucherDAO {

	private static final Logger log = LoggerFactory.getLogger(VoucherDAO.class);

	public List<Voucher> getAllVoucher() {
		String sql = "select * from tbl_voucher join tbl_hang_thanh_vien on tbl_voucher.dieu_kien_hang_tv = tbl_hang_thanh_vien.id";
		List<Voucher> ds = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Voucher voucher = new Voucher();
				voucher.setDonToiThieu(rs.getDouble("don_toi_thieu"));
				voucher.setGiamToiDa(rs.getDouble("giam_toi_da"));
				voucher.setGiaTri(rs.getInt("gia_tri"));
				voucher.setMaVoucher(rs.getString("ma_voucher"));
				voucher.setNgayBatDau(rs.getDate("ngay_bat_dau").toLocalDate());
				voucher.setNgayKetThuc(rs.getDate("ngay_ket_thuc").toLocalDate());
				voucher.setSoLuotDaSuDung(rs.getInt("so_lan_da_dung"));
				voucher.setSoLuotSuDungToiDa(rs.getInt("so_luot_su_dung_toi_da"));
				voucher.setTongTienDuocGiam(0);

				CustomerRank cusRank = new CustomerRank();
				cusRank.setId(rs.getInt("id"));
				cusRank.setMoTa(rs.getString("mo_ta"));
				cusRank.setTenHangTV(rs.getString("hang_thanh_vien"));
				cusRank.setDiemToiThieu(rs.getInt("diem_toi_thieu"));
				voucher.setCustomerRank(cusRank);

				ds.add(voucher);
			}
		} catch (SQLException e) {
			log.error("[DAO] getAllVoucher failed", e);
		}
		return ds;
	}

	public List<Voucher> getAllVoucherByConditionCus(Customer cus, double tongTien) {
		String sql = """
					select v.ma_voucher, v.gia_tri, v.don_toi_thieu, v.giam_toi_da,
					v.ngay_bat_dau, v.ngay_ket_thuc, v.so_lan_da_dung, v.so_luot_su_dung_toi_da,
					hvt.id as htv_id, hvt.hang_thanh_vien
					from tbl_voucher v
					inner join tbl_hang_thanh_vien hvt on v.dieu_kien_hang_tv = hvt.id
					where deleted=0
					and so_lan_da_dung < so_luot_su_dung_toi_da
					and dieu_kien_hang_tv <= ?
					and CAST(GETDATE() AS DATE) >= ngay_bat_dau and CAST(GETDATE() AS DATE) <= ngay_ket_thuc
					and don_toi_thieu <= ?
					order by IIF((? * gia_tri / 100) < giam_toi_da, (? * gia_tri / 100), giam_toi_da) desc
				""";

		List<Voucher> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, cus.getCustomerRank().getId());
			stmt.setDouble(2, tongTien);
			stmt.setDouble(3, tongTien);
			stmt.setDouble(4, tongTien);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Voucher voucher = new Voucher();
					voucher.setMaVoucher(rs.getString("ma_voucher"));
					voucher.setGiaTri(rs.getInt("gia_tri"));
					voucher.setDonToiThieu(rs.getDouble("don_toi_thieu"));
					voucher.setGiamToiDa(rs.getDouble("giam_toi_da"));
					voucher.setNgayBatDau(rs.getDate("ngay_bat_dau").toLocalDate());
					voucher.setNgayKetThuc(rs.getDate("ngay_ket_thuc").toLocalDate());
					voucher.setSoLuotDaSuDung(rs.getInt("so_lan_da_dung"));
					voucher.setSoLuotSuDungToiDa(rs.getInt("so_luot_su_dung_toi_da"));
					voucher.setTongTienDuocGiam(tongTien);

					CustomerRank customerRank = new CustomerRank();
					customerRank.setId(rs.getInt("htv_id"));
					customerRank.setTenHangTV(rs.getString("hang_thanh_vien"));

					voucher.setCustomerRank(customerRank);

					list.add(voucher);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getAllVoucherByConditionCus failed for Total: {}. Error: {}", tongTien, e.getMessage(), e);
		}

		return list;
	}

	public boolean updateSoLuotSuDung(Connection con, Voucher voucher) {
		String sql = "update tbl_voucher set so_lan_da_dung = so_lan_da_dung + 1 where ma_voucher=?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, voucher.getMaVoucher());

			int rowsAffected = stmt.executeUpdate();
			if (rowsAffected == 0) {
				log.warn("[DAO] Update failed for voucher {}: 0 rows affected (May be max usage reached)",
						voucher.getMaVoucher());
			}

			return rowsAffected > 0;
		} catch (SQLException e) {
			log.error("[DAO] updateSoLuotSuDung failed for voucher {}: {}", voucher.getMaVoucher(), e.getMessage(), e);
		}
		return false;
	}

	public List<Voucher> filter(String keyword, int status, LocalDate from, LocalDate to) {
		List<Voucher> list = new ArrayList<>();

		StringBuilder sql = new StringBuilder(
				"SELECT * FROM tbl_voucher join tbl_hang_thanh_vien on tbl_voucher.dieu_kien_hang_tv = tbl_hang_thanh_vien.id WHERE 1=1 ");

		List<Object> params = new ArrayList<>();

		if (keyword != null && !keyword.isEmpty()) {
			sql.append(" AND ma_voucher LIKE ? ");
			params.add("%" + keyword + "%");
		}

		if (status != -1) {
			if (status == 1) {
				sql.append(" AND ngay_ket_thuc >= GETDATE()");
			} else if (status == 0) {
				sql.append(" AND ngay_ket_thuc < GETDATE()");
			}
		}

		if (from != null) {
			sql.append(" AND ngay_bat_dau <= ?");
			params.add(java.sql.Date.valueOf(from));
		}

		if (to != null) {
			sql.append(" AND ngay_ket_thuc >= ?");
			params.add(java.sql.Date.valueOf(to));
		}

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				ps.setObject(i + 1, params.get(i));
			}

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Voucher voucher = new Voucher();
					voucher.setMaVoucher(rs.getString("ma_voucher"));
					voucher.setGiaTri(rs.getInt("gia_tri"));
					voucher.setDonToiThieu(rs.getDouble("don_toi_thieu"));
					voucher.setGiamToiDa(rs.getDouble("giam_toi_da"));
					voucher.setNgayBatDau(rs.getDate("ngay_bat_dau").toLocalDate());
					voucher.setNgayKetThuc(rs.getDate("ngay_ket_thuc").toLocalDate());
					voucher.setSoLuotDaSuDung(rs.getInt("so_lan_da_dung"));
					voucher.setSoLuotSuDungToiDa(rs.getInt("so_luot_su_dung_toi_da"));

					CustomerRank customerRank = new CustomerRank();
					customerRank.setId(rs.getInt("id"));
					customerRank.setTenHangTV(rs.getString("hang_thanh_vien"));

					voucher.setCustomerRank(customerRank);
					list.add(voucher);
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] filter failed", e);
		}

		return list;
	}

	public boolean update(Voucher v) {
		String sql = """
				UPDATE tbl_voucher SET
					gia_tri = ?,
					don_toi_thieu = ?,
					giam_toi_da = ?,
					ngay_bat_dau = ?,
					ngay_ket_thuc = ?,
					so_lan_da_dung = ?,
					so_luot_su_dung_toi_da = ?,
					dieu_kien_hang_tv = ?
				WHERE ma_voucher = ?
				""";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, v.getGiaTri());
			ps.setDouble(2, v.getDonToiThieu());
			ps.setDouble(3, v.getGiamToiDa());
			ps.setDate(4, java.sql.Date.valueOf(v.getNgayBatDau()));
			ps.setDate(5, java.sql.Date.valueOf(v.getNgayKetThuc()));
			ps.setInt(6, v.getSoLuotDaSuDung());
			ps.setInt(7, v.getSoLuotSuDungToiDa());
			ps.setInt(8, v.getCustomerRank().getId());
			ps.setString(9, v.getMaVoucher());

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			log.error("[DAO] update failed for: {}", v.getMaVoucher(), e);
		}
		return false;
	}

	public boolean insert(Voucher v) {
		String sql = "INSERT INTO tbl_voucher (gia_tri, don_toi_thieu, giam_toi_da, ngay_bat_dau, ngay_ket_thuc, "
				+ "so_lan_da_dung, so_luot_su_dung_toi_da, dieu_kien_hang_tv) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, v.getGiaTri());
			ps.setDouble(2, v.getDonToiThieu());
			ps.setDouble(3, v.getGiamToiDa());
			ps.setDate(4, java.sql.Date.valueOf(v.getNgayBatDau()));
			ps.setDate(5, java.sql.Date.valueOf(v.getNgayKetThuc()));
			ps.setInt(6, v.getSoLuotDaSuDung());
			ps.setInt(7, v.getSoLuotSuDungToiDa());
			ps.setInt(8, v.getCustomerRank().getId());

			return ps.executeUpdate() > 0;

		} catch (Exception e) {
			log.error("[DAO] insert failed", e);
		}

		return false;
	}

}