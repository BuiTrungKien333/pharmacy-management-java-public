package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.entity.Batch;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.Employee;
import com.pharmacy.entity.Invoice;
import com.pharmacy.entity.InvoiceDetail;
import com.pharmacy.entity.InvoiceDetailReturn;
import com.pharmacy.entity.InvoiceReturn;
import com.pharmacy.entity.Product;
import com.pharmacy.entity.Voucher;

public class InvoiceDAO {

	private static final Logger log = LoggerFactory.getLogger(InvoiceDAO.class);

	public boolean insertInvoice(Connection con, Invoice invoice) {
		String sql = """
				INSERT INTO tbl_hoa_don (ma_kh, ma_nv, ma_vou, tong_tien, tong_tien_sau_giam)
				 OUTPUT inserted.id VALUES (?, ?, ?, ?, ?)
				""";

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			if (invoice.getCustomer() != null)
				stmt.setInt(1, invoice.getCustomer().getMaKhachHang());
			else
				stmt.setNull(1, java.sql.Types.INTEGER);

			stmt.setString(2, invoice.getEmployee().getMaNhanVien());

			if (invoice.getVoucher() != null)
				stmt.setString(3, invoice.getVoucher().getMaVoucher());
			else
				stmt.setNull(3, java.sql.Types.VARCHAR);

			stmt.setDouble(4, invoice.getTongTienHang());
			stmt.setDouble(5, invoice.getTongTienCanThanhToan());

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String id = rs.getString(1);
					invoice.setMaHoaDon(id);
					return true;
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] Insert failed for InvoiceID: {}. Error: {}", invoice.getMaHoaDon(), e.getMessage(), e);
		}
		return false;
	}

	/*
	 * ----------------- START INVOICE RETURN --------------------
	 */

	public List<InvoiceReturn> getFilterInvoiceReturnByPage(int filter, int filterDate, LocalDate dateFrom,
			LocalDate dateTo, int offset, int pageSize) {
		StringBuilder sql = new StringBuilder("""
				select hdt.id, hdt.ngay_lap, hdt.tien_hoan, hdt.da_duyet,
				kh.so_dien_thoai
				from tbl_hoa_don_tra hdt
				inner join tbl_khach_hang kh on hdt.ma_kh=kh.id
				where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, dateFrom, dateTo);

		sql.append(" order by hdt.ngay_lap desc ");
		sql.append(" offset ? rows fetch next ? rows only ");

		List<InvoiceReturn> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					InvoiceReturn invoice = new InvoiceReturn();
					invoice.setMaHDTra(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTienHoan(rs.getDouble("tien_hoan"));
					invoice.setDaDuyet(rs.getBoolean("da_duyet"));
					invoice.setCustomer(new Customer(rs.getString("so_dien_thoai")));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getFilterInvoiceReturnByPage failed. Filters: ({}, {}, {}, {}, {}, {}). Error: {}", filter,
					filterDate, dateFrom, dateTo, offset, pageSize, e.getMessage(), e);
		}

		return list;
	}

	public int countFilteredInvoiceReturn(int filter, int filterDate, LocalDate dateFrom, LocalDate dateTo) {
		StringBuilder sql = new StringBuilder("select count(*) from tbl_hoa_don_tra hdt where 1=1 ");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, dateFrom, dateTo);

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.error("[DAO] Error countFilteredInvoiceReturn: {}", e.getMessage(), e);
		}
		return 0;
	}

	public List<InvoiceReturn> getInvReturnByFilteredAndSearchByMaHDT(int filter, int filterDate, LocalDate dateFrom,
			LocalDate dateTo, int offset, int pageSize, String keyword) {

		StringBuilder sql = new StringBuilder("""
				select hdt.id, hdt.ngay_lap, hdt.tien_hoan, hdt.da_duyet,
				kh.so_dien_thoai
				from tbl_hoa_don_tra hdt
				inner join tbl_khach_hang kh on hdt.ma_kh=kh.id
				where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, dateFrom, dateTo);

		sql.append(" and (hdt.id like ? OR kh.so_dien_thoai like ?) ");
		params.add("%" + keyword + "%");
		params.add("%" + keyword + "%");

		sql.append(" order by hdt.ngay_lap desc ");
		sql.append(" offset ? rows fetch next ? rows only ");

		List<InvoiceReturn> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					InvoiceReturn invoice = new InvoiceReturn();
					invoice.setMaHDTra(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTienHoan(rs.getDouble("tien_hoan"));
					invoice.setDaDuyet(rs.getBoolean("da_duyet"));
					invoice.setCustomer(new Customer(rs.getString("so_dien_thoai")));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error(
					"[DAO] getInvReturnByFilteredAndSearchByMaHDT failed. Filters: ({}, {}, {}, {}, {}, {}, {}). Error: {}",
					filter, filterDate, dateFrom, dateTo, offset, pageSize, keyword, e.getMessage(), e);
		}

		return list;
	}

	public int countInvReturnByFilteredAndSearchByMaHDT(int filter, int filterDate, LocalDate dateFrom,
			LocalDate dateTo, String keyword) {
		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(*) FROM tbl_hoa_don_tra hdt inner join tbl_khach_hang kh on hdt.ma_kh = kh.id where 1=1 ");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, dateFrom, dateTo);

		sql.append(" and (hdt.id like ? OR kh.so_dien_thoai like ?) ");
		params.add("%" + keyword + "%");
		params.add("%" + keyword + "%");

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.error("[DAO] Error countInvReturnByFilteredAndSearchByMaHDT: {}", e.getMessage(), e);
		}
		return 0;
	}

	private void buildParams(PreparedStatement stmt, List<Object> params) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			Object p = params.get(i);
			if (p instanceof LocalDate ld) {
				stmt.setDate(i + 1, Date.valueOf(ld));
			} else {
				stmt.setObject(i + 1, p);
			}
		}
	}

	private void buildFilterQueryInvReturn(StringBuilder sql, List<Object> params, int filter, int filterDate,
			LocalDate dateFrom, LocalDate dateTo) {

		if (filter != 0) {
			sql.append(" AND hdt.da_duyet = ? ");
			params.add(filter == 1);
		}

		if (filterDate != 0) {
			switch (filterDate) {
			case 1 -> sql.append(" AND CAST(hdt.ngay_lap AS DATE) = CAST(GETDATE() AS DATE) ");
			case 2 -> sql.append(" AND CAST(hdt.ngay_lap AS DATE) >= DATEADD(DAY, -7, CAST(GETDATE() AS DATE)) ");
			case 3 -> sql.append("""
					    AND MONTH(hdt.ngay_lap) = MONTH(GETDATE())
					    AND YEAR(hdt.ngay_lap) = YEAR(GETDATE())
					""");
			case 4 -> {
				sql.append(" AND CAST(hdt.ngay_lap AS DATE) BETWEEN ? AND ? ");
				params.add(dateFrom);
				params.add(dateTo);
			}
			}
		}
	}

	public InvoiceReturn getInvoiceReturnByMaHDT(String maHD) {
		String sql = """
				select hdt.id, hdt.ngay_lap, hdt.ly_do, hdt.tien_hoan, hdt.da_duyet,
				kh.ho_ten as ten_kh, kh.so_dien_thoai,
				hd.id as ma_hd, hd.ngay_lap as ngay_lap_hd,
				nv.ma_nv, nv.ho_ten as ten_nv
				from tbl_hoa_don_tra hdt
				inner join tbl_khach_hang kh on hdt.ma_kh = kh.id
				inner join tbl_hoa_don hd on hd.id = hdt.ma_hd
				inner join tbl_nhan_vien nv on hdt.ma_nv = nv.ma_nv
				where hdt.id=?
				""";
		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, maHD);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					InvoiceReturn inv = new InvoiceReturn();
					inv.setMaHDTra(maHD);
					inv.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					inv.setLyDo(rs.getString("ly_do"));
					inv.setTienHoan(rs.getDouble("tien_hoan"));
					inv.setDaDuyet(rs.getBoolean("da_duyet"));

					Customer cus = new Customer(rs.getString("ten_kh"), rs.getString("so_dien_thoai"));
					inv.setCustomer(cus);

					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(rs.getString("ma_hd"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap_hd").toLocalDateTime());
					inv.setInvoice(invoice);

					Employee emp = new Employee(rs.getString("ma_nv"), rs.getString("ten_nv"));
					inv.setEmployee(emp);

					return inv;
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] getInvoiceReturnByMaHDT failed for ID: {}. Error: {}", maHD, e.getMessage(), e);
		}

		return null;
	}

	public List<InvoiceDetailReturn> getAllInvDetailReturnByMaHDT(String maHD) {
		String sql = """
				select cthd.*, sp.ten_sp
				from tbl_chi_tiet_hoa_don_tra cthd
				inner join tbl_san_pham sp on cthd.ma_sp = sp.id
				where ma_hd_tra=?
				""";
		List<InvoiceDetailReturn> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maHD);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					InvoiceDetailReturn item = new InvoiceDetailReturn();

					item.setId(rs.getInt("id"));
					item.setSoLuong(rs.getInt("so_luong"));
					item.setDonGia(rs.getDouble("don_gia"));
					item.setThanhTien(rs.getDouble("thanh_tien"));
					item.setStatus(rs.getBoolean("trang_thai_id"));
					item.setHuongXuLy(rs.getString("huong_xu_ly"));
					item.setLyDo(rs.getString("ly_do"));

					Product product = new Product();
					product.setTenSanPham(rs.getString("ten_sp"));
					item.setProduct(product);

					Batch batch = new Batch();
					batch.setSoLo(rs.getString("so_lo"));
					item.setBatch(batch);

					InvoiceReturn invReturn = new InvoiceReturn();
					invReturn.setMaHDTra(rs.getString("ma_hd_tra"));
					item.setInvoiceReturn(invReturn);

					list.add(item);
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] getAllInvDetailReturnByMaHDT failed for ID: {}. Error: {}", maHD, e.getMessage(), e);
		}

		return list;
	}

	public int countInvReturnByFilteredAndSearchBySoLo(int filter, int filterDate, LocalDate startDate,
			LocalDate endDate, String keyword) {
		StringBuilder sql = new StringBuilder(
				"""
							select count(DISTINCT hdt.id) from tbl_hoa_don_tra hdt inner join tbl_chi_tiet_hoa_don_tra cthdt on hdt.id = cthdt.ma_hd_tra where 1=1
						""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, startDate, endDate);

		sql.append(" and cthdt.so_lo like ? ");
		params.add("%" + keyword + "%");

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.error("[DAO] countInvReturnByFilteredAndSearchBySoLo failed: {}", e.getMessage(), e);
		}

		return 0;
	}

	public List<InvoiceReturn> getInvReturnByFilteredAndSearchBySoLo(int filter, int filterDate, LocalDate startDate,
			LocalDate endDate, int offset, int pageSize, String keyword) {
		StringBuilder sql = new StringBuilder("""
					select hdt.id, hdt.ngay_lap, hdt.tien_hoan, hdt.da_duyet,
					kh.so_dien_thoai
					from tbl_hoa_don_tra hdt
					inner join tbl_khach_hang kh on hdt.ma_kh=kh.id
					inner join tbl_chi_tiet_hoa_don_tra cthdt on hdt.id = cthdt.ma_hd_tra
					where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, startDate, endDate);

		sql.append(" and cthdt.so_lo like ? ");
		params.add("%" + keyword + "%");

		sql.append(" group by hdt.id, hdt.ngay_lap, hdt.tien_hoan, hdt.da_duyet, kh.so_dien_thoai ");

		sql.append(" order by hdt.ngay_lap desc ");
		sql.append(" offset ? rows fetch next ? rows only ");

		List<InvoiceReturn> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					InvoiceReturn invoice = new InvoiceReturn();
					invoice.setMaHDTra(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTienHoan(rs.getDouble("tien_hoan"));
					invoice.setDaDuyet(rs.getBoolean("da_duyet"));
					invoice.setCustomer(new Customer(rs.getString("so_dien_thoai")));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error(
					"[DAO] getInvReturnByFilteredAndSearchBySoLo failed. Filters: ({}, {}, {}, {}, {}, {}, {}). Error: {}",
					filter, filterDate, startDate, endDate, offset, pageSize, keyword, e.getMessage(), e);
		}

		return list;
	}

	public List<InvoiceReturn> getAllInvoiceReturnToExport(int filter, int filterDate, LocalDate dateFrom,
			LocalDate dateTo) {
		StringBuilder sql = new StringBuilder("""
				select hdt.id, hdt.ngay_lap, hdt.tien_hoan, hdt.da_duyet, hdt.ly_do, hdt.ma_hd, hdt.ma_nv,
				kh.so_dien_thoai, kh.ho_ten as ten_kh,
				nv.ho_ten as ten_nv
				from tbl_hoa_don_tra hdt
				inner join tbl_khach_hang kh on hdt.ma_kh=kh.id
				inner join tbl_nhan_vien nv on hdt.ma_nv=nv.ma_nv
				where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, dateFrom, dateTo);

		sql.append(" order by hdt.ngay_lap desc ");

		List<InvoiceReturn> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					InvoiceReturn invoice = new InvoiceReturn();

					invoice.setMaHDTra(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTienHoan(rs.getDouble("tien_hoan"));
					invoice.setDaDuyet(rs.getBoolean("da_duyet"));
					invoice.setLyDo(rs.getString("ly_do"));
					invoice.setInvoice(new Invoice(rs.getString("ma_hd")));
					invoice.setCustomer(new Customer(rs.getString("ten_kh"), rs.getString("so_dien_thoai")));
					invoice.setEmployee(new Employee(rs.getString("ma_nv"), rs.getString("ten_nv")));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getAllInvoiceReturnToExport failed. Filters: ({}, {}, {}, {}). Error: {}", filter,
					filterDate, dateFrom, dateTo, e.getMessage(), e);
		}

		return list;
	}

	public List<?> getAllInvoiceReturnToExportAndSearchById(int filter, int filterDate, LocalDate dateFrom,
			LocalDate dateTo, String keyword) {
		StringBuilder sql = new StringBuilder("""
				select hdt.id, hdt.ngay_lap, hdt.tien_hoan, hdt.da_duyet, hdt.ly_do, hdt.ma_hd, hdt.ma_nv,
				kh.so_dien_thoai, kh.ho_ten as ten_kh,
				nv.ho_ten as ten_nv
				from tbl_hoa_don_tra hdt
				inner join tbl_khach_hang kh on hdt.ma_kh=kh.id
				inner join tbl_nhan_vien nv on hdt.ma_nv=nv.ma_nv
				where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, dateFrom, dateTo);

		sql.append(" and (hdt.id like ? OR kh.so_dien_thoai like ?) ");
		params.add("%" + keyword + "%");
		params.add("%" + keyword + "%");

		sql.append(" order by hdt.ngay_lap desc ");

		List<InvoiceReturn> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					InvoiceReturn invoice = new InvoiceReturn();

					invoice.setMaHDTra(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTienHoan(rs.getDouble("tien_hoan"));
					invoice.setDaDuyet(rs.getBoolean("da_duyet"));
					invoice.setLyDo(rs.getString("ly_do"));
					invoice.setInvoice(new Invoice(rs.getString("ma_hd")));
					invoice.setCustomer(new Customer(rs.getString("ten_kh"), rs.getString("so_dien_thoai")));
					invoice.setEmployee(new Employee(rs.getString("ma_nv"), rs.getString("ten_nv")));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getAllInvoiceReturnToExportAndSearchById failed. Filters: ({}, {}, {}, {}, {}). Error: {}",
					filter, filterDate, dateFrom, dateTo, keyword, e.getMessage(), e);
		}

		return list;
	}

	public List<?> getAllInvoiceReturnToExportAndSearchBySoLo(int filter, int filterDate, LocalDate dateFrom,
			LocalDate dateTo, String keyword) {
		StringBuilder sql = new StringBuilder("""
				select hdt.id, hdt.ngay_lap, hdt.tien_hoan, hdt.da_duyet, hdt.ly_do, hdt.ma_hd, hdt.ma_nv,
				kh.so_dien_thoai, kh.ho_ten as ten_kh,
				nv.ho_ten as ten_nv
				from tbl_hoa_don_tra hdt
				inner join tbl_khach_hang kh on hdt.ma_kh=kh.id
				inner join tbl_nhan_vien nv on hdt.ma_nv=nv.ma_nv
				inner join tbl_chi_tiet_hoa_don_tra cthdt on hdt.id = cthdt.ma_hd_tra
				where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvReturn(sql, params, filter, filterDate, dateFrom, dateTo);

		sql.append(" and cthdt.so_lo like ? ");
		params.add("%" + keyword + "%");

		sql.append(
				" group by hdt.id, hdt.ngay_lap, hdt.tien_hoan, hdt.da_duyet, hdt.ly_do, hdt.ma_hd, hdt.ma_nv, kh.so_dien_thoai, kh.ho_ten, nv.ho_ten ");

		sql.append(" order by hdt.ngay_lap desc ");

		List<InvoiceReturn> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					InvoiceReturn invoice = new InvoiceReturn();

					invoice.setMaHDTra(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTienHoan(rs.getDouble("tien_hoan"));
					invoice.setDaDuyet(rs.getBoolean("da_duyet"));
					invoice.setLyDo(rs.getString("ly_do"));
					invoice.setInvoice(new Invoice(rs.getString("ma_hd")));
					invoice.setCustomer(new Customer(rs.getString("ten_kh"), rs.getString("so_dien_thoai")));
					invoice.setEmployee(new Employee(rs.getString("ma_nv"), rs.getString("ten_nv")));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error(
					"[DAO] getAllInvoiceReturnToExportAndSearchBySoLo failed. Filters: ({}, {}, {}, {}, {}). Error: {}",
					filter, filterDate, dateFrom, dateTo, keyword, e.getMessage(), e);
		}

		return list;
	}

	/*
	 * ----------------- END INVOICE RETURN --------------------
	 */

	/*
	 * ----------------- START INVOICE SELL --------------------
	 */

	public List<Invoice> getFilterInvoice(int filterDate, LocalDate startDate, LocalDate endDate, int offset,
			int pageSize) {
		StringBuilder sql = new StringBuilder("""
					select hd.id, hd.ngay_lap, hd.tong_tien_sau_giam,
					kh.so_dien_thoai
					from tbl_hoa_don hd
					left join tbl_khach_hang kh on hd.ma_kh = kh.id
					where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		sql.append(" order by hd.ngay_lap desc ");
		sql.append(" offset ? rows fetch next ? rows only ");

		List<Invoice> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTongTienCanThanhToan(rs.getDouble("tong_tien_sau_giam"));
					String phone = rs.getString("so_dien_thoai");
					invoice.setCustomer(new Customer((phone == null) ? "Vãng lai" : phone));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getFilterInvoice failed. Filter: {}. Error: {}", filterDate, e.getMessage(), e);
		}

		return list;
	}

	public int countFilteredInvoice(int filterDate, LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("select count(*) from tbl_hoa_don hd where 1=1 ");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] countFilteredInvoice failed: {}", e.getMessage(), e);
		}
		return 0;
	}

	public List<Invoice> getInvoiceByFilteredAndSearchByID(int filterDate, LocalDate startDate, LocalDate endDate,
			int offset, int pageSize, String keyword) {

		StringBuilder sql = new StringBuilder("""
					select hd.id, hd.ngay_lap, hd.tong_tien_sau_giam,
					kh.so_dien_thoai
					from tbl_hoa_don hd
					left join tbl_khach_hang kh on hd.ma_kh = kh.id
					where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		sql.append(" and (hd.id like ? OR kh.so_dien_thoai like ?) ");
		params.add("%" + keyword + "%");
		params.add("%" + keyword + "%");

		sql.append(" order by hd.ngay_lap desc ");
		sql.append(" offset ? rows fetch next ? rows only ");

		List<Invoice> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTongTienCanThanhToan(rs.getDouble("tong_tien_sau_giam"));
					String phone = rs.getString("so_dien_thoai");
					invoice.setCustomer(new Customer((phone == null) ? "Vãng lai" : phone));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getInvoiceByFilteredAndSearchByID failed. Keyword: '{}'. Error: {}", keyword,
					e.getMessage(), e);
		}

		return list;
	}

	public int countInvoiceByFilteredAndSearchByID(int filterDate, LocalDate startDate, LocalDate endDate,
			String keyword) {
		StringBuilder sql = new StringBuilder("""
					select count(*)
					from tbl_hoa_don hd left join tbl_khach_hang kh on hd.ma_kh = kh.id
					where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		sql.append(" and (hd.id like ? OR kh.so_dien_thoai like ?) ");
		params.add("%" + keyword + "%");
		params.add("%" + keyword + "%");

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.error("[DAO] countInvoiceByFilteredAndSearchById failed: {}", e.getMessage(), e);
		}
		return 0;
	}

	private void buildFilterQueryInvoice(StringBuilder sql, List<Object> params, int filterDate, LocalDate startDate,
			LocalDate endDate) {
		if (filterDate != 0) {
			switch (filterDate) {
			case 1 -> sql.append(" AND CAST(hd.ngay_lap AS DATE) = CAST(GETDATE() AS DATE) ");
			case 2 -> sql.append(" AND CAST(hd.ngay_lap AS DATE) >= DATEADD(DAY, -7, CAST(GETDATE() AS DATE)) ");
			case 3 -> sql.append("""
					    AND MONTH(hd.ngay_lap) = MONTH(GETDATE())
					    AND YEAR(hd.ngay_lap) = YEAR(GETDATE())
					""");
			case 4 -> {
				sql.append(" AND CAST(hd.ngay_lap AS DATE) BETWEEN ? AND ? ");
				params.add(java.sql.Date.valueOf(startDate));
				params.add(java.sql.Date.valueOf(endDate));
			}
			}
		}
	}

	public int countInvoiceByFilteredAndSearchBySoLo(int filterDate, LocalDate startDate, LocalDate endDate,
			String keyword) {
		StringBuilder sql = new StringBuilder(
				"""
							select count(DISTINCT hd.id) from tbl_hoa_don hd inner join tbl_chi_tiet_hoa_don cthd on hd.id = cthd.ma_hd where 1=1
						""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		sql.append(" and cthd.so_lo like ? ");
		params.add("%" + keyword + "%");

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next())
					return rs.getInt(1);
			}
		} catch (SQLException e) {
			log.error("[DAO] countInvoiceByFilteredAndSearchBySoLo failed: {}", e.getMessage(), e);
		}

		return 0;
	}

	public List<Invoice> getInvoiceByFilteredAndSearchBySoLo(int filterDate, LocalDate startDate, LocalDate endDate,
			int offset, int pageSize, String keyword) {
		StringBuilder sql = new StringBuilder("""
					select hd.id, hd.ngay_lap, hd.tong_tien_sau_giam,
					kh.so_dien_thoai
					from tbl_hoa_don hd
					left join tbl_khach_hang kh on hd.ma_kh = kh.id
					inner join tbl_chi_tiet_hoa_don cthd on hd.id = cthd.ma_hd
					where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		sql.append(" and cthd.so_lo like ? ");
		params.add("%" + keyword + "%");

		sql.append(" group by hd.id, hd.ngay_lap, hd.tong_tien_sau_giam, kh.so_dien_thoai ");

		sql.append(" order by hd.ngay_lap desc ");
		sql.append(" offset ? rows fetch next ? rows only ");

		List<Invoice> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTongTienCanThanhToan(rs.getDouble("tong_tien_sau_giam"));
					String phone = rs.getString("so_dien_thoai");
					invoice.setCustomer(new Customer((phone == null) ? "Vãng lai" : phone));

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getInvoiceByFilteredAndSearchBySoLo failed. Keyword: '{}'. Error: {}", keyword,
					e.getMessage(), e);
		}

		return list;
	}

	public Invoice getInvoiceByMaHD(String maHD) {
		String sql = """
				select hd.id, hd.ma_kh, hd.ma_nv, hd.ma_vou, hd.ngay_lap, hd.tong_tien_sau_giam, hd.da_tra,
				kh.ho_ten as ten_kh, kh.so_dien_thoai, nv.ho_ten as ten_nv
				from tbl_hoa_don hd
				left join tbl_khach_hang kh on hd.ma_kh = kh.id
				inner join tbl_nhan_vien nv on hd.ma_nv = nv.ma_nv
				where hd.id=?
				""";
		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, maHD);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(maHD);
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTongTienCanThanhToan(rs.getDouble("tong_tien_sau_giam"));
					invoice.setDaTra(rs.getBoolean("da_tra"));

					Employee emp = new Employee();
					emp.setMaNhanVien(rs.getString("ma_nv"));
					emp.setTenNhanVien(rs.getString("ten_nv"));
					invoice.setEmployee(emp);

					int maKh = rs.getInt("ma_kh");
					if (!rs.wasNull()) {
						Customer customer = new Customer();
						customer.setMaKhachHang(maKh);
						customer.setTenKhachHang(rs.getString("ten_kh"));
						customer.setSoDienThoai(rs.getString("so_dien_thoai"));
						invoice.setCustomer(customer);
					}

					String maVou = rs.getString("ma_vou");
					invoice.setVoucher(maVou != null ? new Voucher(maVou) : null);

					return invoice;
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] getInvoiceByMaHD failed for ID: {}. Error: {}", maHD, e.getMessage(), e);
		}

		return null;
	}

	public List<InvoiceDetail> getAllInvoiceDetailByMaHD(String maHD) {
		String sql = """
				select cthd.*, sp.ten_sp
				from tbl_chi_tiet_hoa_don cthd
				inner join tbl_san_pham sp on cthd.ma_sp = sp.id
				where ma_hd=?
				""";
		List<InvoiceDetail> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, maHD);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					InvoiceDetail item = new InvoiceDetail();
					item.setInvoice(new Invoice(maHD));
					item.setId(rs.getInt("id"));

					item.setProduct(new Product(rs.getInt("ma_sp"), rs.getString("ten_sp")));

					String soLo = rs.getString("so_lo");
					item.setShipment(soLo != null ? new Batch(soLo) : null);

					item.setSoLuong(rs.getInt("so_luong"));
					item.setDonGia(rs.getDouble("don_gia"));
					item.setThanhTien(rs.getDouble("thanh_tien"));

					list.add(item);
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] getAllInvoiceDetailByMaHD failed for ID: {}. Error: {}", maHD, e.getMessage(), e);
		}

		return list;
	}

	public List<Invoice> getAllInvoiceToExport(int filterDate, LocalDate startDate, LocalDate endDate) {
		StringBuilder sql = new StringBuilder("""
				select hd.id, hd.ma_kh, hd.ma_nv, hd.ma_vou, hd.ngay_lap, hd.tong_tien_sau_giam, hd.da_tra,
				kh.ho_ten as ten_kh, kh.so_dien_thoai, nv.ho_ten as ten_nv
				from tbl_hoa_don hd
				left join tbl_khach_hang kh on hd.ma_kh = kh.id
				inner join tbl_nhan_vien nv on hd.ma_nv = nv.ma_nv
				where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		sql.append(" order by hd.ngay_lap desc ");

		List<Invoice> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTongTienCanThanhToan(rs.getDouble("tong_tien_sau_giam"));
					invoice.setDaTra(rs.getBoolean("da_tra"));

					Employee emp = new Employee();
					emp.setMaNhanVien(rs.getString("ma_nv"));
					emp.setTenNhanVien(rs.getString("ten_nv"));
					invoice.setEmployee(emp);

					int maKh = rs.getInt("ma_kh");
					if (!rs.wasNull()) {
						Customer customer = new Customer();
						customer.setMaKhachHang(maKh);
						customer.setTenKhachHang(rs.getString("ten_kh"));
						customer.setSoDienThoai(rs.getString("so_dien_thoai"));
						invoice.setCustomer(customer);
					}

					String maVou = rs.getString("ma_vou");
					invoice.setVoucher(maVou != null ? new Voucher(maVou) : null);

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getAllInvoiceToExport failed. Filter: {}. Error: {}", filterDate, e.getMessage(), e);
		}

		return list;
	}

	public List<?> getAllInvoiceToExportAndSearchById(int filterDate, LocalDate startDate, LocalDate endDate,
			String keyword) {

		StringBuilder sql = new StringBuilder("""
				select hd.id, hd.ma_kh, hd.ma_nv, hd.ma_vou, hd.ngay_lap, hd.tong_tien_sau_giam, hd.da_tra,
				kh.ho_ten as ten_kh, kh.so_dien_thoai, nv.ho_ten as ten_nv
				from tbl_hoa_don hd
				left join tbl_khach_hang kh on hd.ma_kh = kh.id
				inner join tbl_nhan_vien nv on hd.ma_nv = nv.ma_nv
				where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		sql.append(" and (hd.id like ? OR kh.so_dien_thoai like ?) ");
		params.add("%" + keyword + "%");
		params.add("%" + keyword + "%");

		sql.append(" order by hd.ngay_lap desc ");

		List<Invoice> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTongTienCanThanhToan(rs.getDouble("tong_tien_sau_giam"));
					invoice.setDaTra(rs.getBoolean("da_tra"));

					Employee emp = new Employee();
					emp.setMaNhanVien(rs.getString("ma_nv"));
					emp.setTenNhanVien(rs.getString("ten_nv"));
					invoice.setEmployee(emp);

					int maKh = rs.getInt("ma_kh");
					if (!rs.wasNull()) {
						Customer customer = new Customer();
						customer.setMaKhachHang(maKh);
						customer.setTenKhachHang(rs.getString("ten_kh"));
						customer.setSoDienThoai(rs.getString("so_dien_thoai"));
						invoice.setCustomer(customer);
					}

					String maVou = rs.getString("ma_vou");
					invoice.setVoucher(maVou != null ? new Voucher(maVou) : null);

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getAllInvoiceToExportAndSearchById failed. Filter: {}, keyword: {}. Error: {}", filterDate,
					keyword, e.getMessage(), e);
		}

		return list;
	}

	public List<?> getAllInvoiceToExportAndSearchBySoLo(int filterDate, LocalDate startDate, LocalDate endDate,
			String keyword) {
		StringBuilder sql = new StringBuilder("""
				select hd.id, hd.ma_kh, hd.ma_nv, hd.ma_vou, hd.ngay_lap, hd.tong_tien_sau_giam, hd.da_tra,
				kh.ho_ten as ten_kh, kh.so_dien_thoai, nv.ho_ten as ten_nv
				from tbl_hoa_don hd
				left join tbl_khach_hang kh on hd.ma_kh = kh.id
				inner join tbl_nhan_vien nv on hd.ma_nv = nv.ma_nv
				inner join tbl_chi_tiet_hoa_don cthd on hd.id = cthd.ma_hd
				where 1=1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQueryInvoice(sql, params, filterDate, startDate, endDate);

		sql.append(" and cthd.so_lo like ? ");
		params.add("%" + keyword + "%");

		sql.append(
				" group by hd.id, hd.ma_kh, hd.ma_nv, hd.ma_vou, hd.ngay_lap, hd.tong_tien_sau_giam, hd.da_tra, kh.ho_ten, kh.so_dien_thoai, nv.ho_ten ");

		sql.append(" order by hd.ngay_lap desc ");

		List<Invoice> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Invoice invoice = new Invoice();
					invoice.setMaHoaDon(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTongTienCanThanhToan(rs.getDouble("tong_tien_sau_giam"));
					invoice.setDaTra(rs.getBoolean("da_tra"));

					Employee emp = new Employee();
					emp.setMaNhanVien(rs.getString("ma_nv"));
					emp.setTenNhanVien(rs.getString("ten_nv"));
					invoice.setEmployee(emp);

					int maKh = rs.getInt("ma_kh");
					if (!rs.wasNull()) {
						Customer customer = new Customer();
						customer.setMaKhachHang(maKh);
						customer.setTenKhachHang(rs.getString("ten_kh"));
						customer.setSoDienThoai(rs.getString("so_dien_thoai"));
						invoice.setCustomer(customer);
					}

					String maVou = rs.getString("ma_vou");
					invoice.setVoucher(maVou != null ? new Voucher(maVou) : null);

					list.add(invoice);
				}
			}

		} catch (SQLException e) {
			log.error("[DAO] getAllInvoiceToExportAndSearchBySoLo failed. Filter: {}, keyword:{}. Error: {}",
					filterDate, keyword, e.getMessage(), e);
		}

		return list;
	}

	/*
	 * ----------------- END INVOICE --------------------
	 */

	public List<Invoice> getAllHoaDonByMaKH(int maKH) {
		List<Invoice> ds = new ArrayList<>();
		String sql = "Select * from tbl_hoa_don where ma_kh = ?";

		try (Connection con = ConnectDB.getInstance().getConnection();
				PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, maKH);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Invoice invoice = new Invoice();

					Customer customer = new Customer();
					customer.setMaKhachHang(rs.getInt("ma_kh"));

					Voucher voucher = new Voucher();
					voucher.setMaVoucher(rs.getString("ma_vou"));

					Employee employee = new Employee();
					employee.setMaNhanVien(rs.getString("ma_nv"));

					invoice.setCustomer(customer);
					invoice.setEmployee(employee);
					invoice.setGhiChu(rs.getString("ghi_chu"));
					invoice.setMaHoaDon(rs.getString("id"));
					invoice.setNgayLap(rs.getTimestamp("ngay_lap").toLocalDateTime());
					invoice.setTongTienCanThanhToan(rs.getDouble("tong_tien_sau_giam"));
					invoice.setTongTienHang(rs.getDouble("tong_tien"));
					invoice.setVoucher(voucher);
					invoice.setDaTra(rs.getBoolean("da_tra"));

					ds.add(invoice);
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] getAllHoaDonByMaKH failed. CustomerID: {}. Error: {}", maKH, e.getMessage(), e);
		}
		return ds;
	}

}