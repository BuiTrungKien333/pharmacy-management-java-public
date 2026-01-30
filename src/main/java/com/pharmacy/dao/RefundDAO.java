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
import com.pharmacy.entity.Batch;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.Invoice;
import com.pharmacy.entity.InvoiceDetail;
import com.pharmacy.entity.InvoiceReturn;
import com.pharmacy.entity.Product;

public class RefundDAO {

	private static final Logger log = LoggerFactory.getLogger(RefundDAO.class);

	public Invoice getInvoiceById(String qrCode) {
		String sql = """
				select hd.id as ma_hd, hd.ngay_lap, hd.da_tra, kh.id as ma_kh, kh.so_dien_thoai, kh.ho_ten
				from tbl_hoa_don hd
				left join tbl_khach_hang kh on hd.ma_kh = kh.id
				where hd.id=?
				""";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, qrCode);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(rs.getString("ma_hd"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setDaTra(rs.getBoolean("da_tra"));

					Integer maKH = rs.getObject("ma_kh", Integer.class);
					if (maKH != null) {
						Customer c = new Customer();
						c.setMaKhachHang(maKH.intValue());
						c.setSoDienThoai(rs.getString("so_dien_thoai"));
						c.setTenKhachHang(rs.getString("ho_ten"));
						invoice.setCustomer(c);
					} else {
						invoice.setCustomer(null);
					}

					return invoice;
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] getInvoiceById failed for QR: {}. Error: {}", qrCode, e.getMessage(), e);
		}

		return null;
	}

	public List<InvoiceDetail> getAllInvoiceDetailByQrCode(String qrCode) {
		String sql = "select cthd.*, sp.id as ma_sp, sp.ten_sp, sp.don_vi_tinh from tbl_chi_tiet_hoa_don cthd inner join tbl_san_pham sp on cthd.ma_sp = sp.id where ma_hd=?";

		List<InvoiceDetail> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, qrCode);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					InvoiceDetail invoice = new InvoiceDetail();
					invoice.setId(rs.getInt("id"));
					invoice.setInvoice(new Invoice(qrCode));
					invoice.setShipment(new Batch(rs.getString("so_lo")));
					invoice.setSoLuong(rs.getInt("so_luong"));
					invoice.setDonGia(rs.getDouble("don_gia"));
					invoice.setThanhTien(rs.getDouble("thanh_tien"));
					invoice.setProduct(
							new Product(rs.getInt("ma_sp"), rs.getString("ten_sp"), rs.getString("don_vi_tinh")));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getAllInvoiceDetailByQrCode failed for QR: {}. Error: {}", qrCode, e.getMessage(),
					e);
		}

		return list;
	}

	public boolean insertInvoiceReturn(Connection con, InvoiceReturn invoiceReturn) {
		String sql = """
				INSERT INTO tbl_hoa_don_tra (ma_kh, ma_nv, ma_hd, tien_hoan, ly_do)
				 OUTPUT inserted.id VALUES (?, ?, ?, ?, ?)
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, invoiceReturn.getCustomer().getMaKhachHang());
			stmt.setString(2, invoiceReturn.getEmployee().getMaNhanVien());
			stmt.setString(3, invoiceReturn.getInvoice().getMaHoaDon());
			stmt.setDouble(4, invoiceReturn.getTienHoan());
			stmt.setString(5, invoiceReturn.getLyDo());

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String id = rs.getString(1);
					invoiceReturn.setMaHDTra(id);
					return true;
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] insertInvoiceReturn failed for Invoice: {}. Error: {}",
					invoiceReturn.getInvoice().getMaHoaDon(), e.getMessage(), e);
		}
		return false;
	}

	public boolean updateStatusInvoice(Connection con, String maHoaDon) {
		String sql = "update tbl_hoa_don set da_tra=1 where id=?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maHoaDon);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			log.error("[DAO] updateStatusInvoice failed for ID: {}. Error: {}", maHoaDon, e.getMessage(), e);
		}

		return false;
	}

	public boolean updateStatusForInvoiceReturn(Connection con, String maHDTra) {
		String sql = "update tbl_hoa_don_tra set da_duyet=1 where id=?";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {
			stmt.setString(1, maHDTra);
			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			log.error("[DAO] updateStatusForInvoiceReturn failed for ID: {}. Error: {}", maHDTra, e.getMessage(),
					e);
		}

		return false;
	}

}