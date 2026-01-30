package com.pharmacy.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.dao.BatchDAO;
import com.pharmacy.entity.Batch;
import com.pharmacy.entity.BatchStatus;
import com.pharmacy.entity.Employee;
import com.pharmacy.entity.InvoiceDetailReturn;
import com.pharmacy.entity.NhaCungCap;
import com.pharmacy.entity.Product;
import com.pharmacy.entity.ProductType;

public class BatchDaoImpl implements BatchDAO {

	private static final Logger logger = LoggerFactory.getLogger(BatchDaoImpl.class);

	@Override
	public List<Batch> getAllShipmentByPage(int offset, int limit, int option) {
		long start = System.currentTimeMillis();

		String orderColumn = (option == 0) ? "lo.ngay_nhap" : "lo.han_su_dung";

		String sql = String.format("""
				select lo.so_lo, lo.ngay_nhap, lo.han_su_dung, lo.so_luong_nhap, lo.so_luong_con, lo.gia_ban,
				       sp.barcode, sp.ten_sp, sp.don_vi_tinh,
				       st_lo.id, st_lo.ten_trang_thai
				from tbl_lo_thuoc lo
				inner join tbl_san_pham sp on lo.ma_sp = sp.id
				inner join tbl_trang_thai_lo st_lo on lo.trang_thai_id = st_lo.id
				order by %s desc
				offset ? rows fetch next ? rows only
				""", orderColumn);

		List<Batch> list = new ArrayList<>();
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, offset);
			stmt.setInt(2, limit);

			logger.debug("[DAO] getAllShipmentByPage SQL: {} | Offset: {}, Limit: {}", "select...", offset, limit);
			
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Batch shipment = new Batch();
					shipment.setSoLo(rs.getString("so_lo"));
					shipment.setNgayNhap(rs.getTimestamp("ngay_nhap").toLocalDateTime());
					shipment.setHanSuDung(rs.getDate("han_su_dung").toLocalDate());
					shipment.setSoLuongNhap(rs.getInt("so_luong_nhap"));
					shipment.setSoLuongCon(rs.getInt("so_luong_con"));
					shipment.setGiaBan(rs.getDouble("gia_ban"));

					Product prod = new Product();
					prod.setBarcode(rs.getString("barcode"));
					prod.setTenSanPham(rs.getString("ten_sp"));
					prod.setDonViTinh(rs.getString("don_vi_tinh"));
					shipment.setProduct(prod);

					BatchStatus status = new BatchStatus();
					status.setId(rs.getInt("id"));
					status.setTenTrangThai(rs.getString("ten_trang_thai"));
					shipment.setShipmentStatus(status);

					list.add(shipment);
				}
			}
			logger.debug("[DAO] Loaded {} shipments in {} ms", list.size(), System.currentTimeMillis() - start);

		} catch (SQLException e) {
			logger.error("[DAO] Error getAllShipmentByPage: {}", e.getMessage(), e);
		}
		return list;
	}

	@Override
	public int countShipments() {
		String sql = "SELECT COUNT(*) FROM tbl_lo_thuoc";
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement ps = con.prepareStatement(sql); 
			 ResultSet rs = ps.executeQuery()) {
			
			if (rs.next()) {
				int count = rs.getInt(1);
				logger.debug("[DAO] Total shipments count: {}", count);
				return count;
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error countShipments: {}", e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public Optional<Batch> getShipmentById(String id) {
		String sql = """
				select lo.so_lo, lo.ngay_nhap, lo.ngay_san_xuat, lo.han_su_dung,
				lo.so_luong_nhap, lo.so_luong_con, lo.gia_nhap, lo.gia_ban, lo.thanh_tien,
				sp.barcode, sp.ten_sp, sp.don_vi_tinh, sp.loai_sp_id,
				ncc.id as ma_ncc, ncc.ma_nha_may, ncc.ten_nha_cung_cap,
				nv.ma_nv, nv.ho_ten,
				st_lo.id as status_lo_id, st_lo.ten_trang_thai
				from tbl_lo_thuoc lo
				inner join tbl_san_pham sp on lo.ma_sp = sp.id
				inner join tbl_trang_thai_lo st_lo on lo.trang_thai_id = st_lo.id
				inner join tbl_nha_cung_cap ncc on lo.ma_ncc = ncc.id
				inner join tbl_nhan_vien nv on lo.ma_nv = nv.ma_nv
				where so_lo=?
				""";
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, id);
			logger.debug("[DAO] getShipmentById: {}", id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Batch shipment = new Batch(rs.getString("so_lo"));
					shipment.setNgayNhap(rs.getTimestamp("ngay_nhap").toLocalDateTime());
					shipment.setNgaySanXuat(rs.getDate("ngay_san_xuat").toLocalDate());
					shipment.setHanSuDung(rs.getDate("han_su_dung").toLocalDate());
					shipment.setSoLuongNhap(rs.getInt("so_luong_nhap"));
					shipment.setSoLuongCon(rs.getInt("so_luong_con"));
					shipment.setGiaNhap(rs.getDouble("gia_nhap"));
					shipment.setGiaBan(rs.getDouble("gia_ban"));
					shipment.setThanhTien(rs.getDouble("thanh_tien"));

					Product prod = new Product();
					prod.setBarcode(rs.getString("barcode"));
					prod.setTenSanPham(rs.getString("ten_sp"));
					prod.setDonViTinh(rs.getString("don_vi_tinh"));
					prod.setLoaiSanPham(new ProductType(rs.getInt("loai_sp_id")));
					shipment.setProduct(prod);

					BatchStatus status = new BatchStatus();
					status.setId(rs.getInt("status_lo_id"));
					status.setTenTrangThai(rs.getString("ten_trang_thai"));
					shipment.setShipmentStatus(status);

					Employee empl = new Employee();
					empl.setMaNhanVien(rs.getString("ma_nv"));
					empl.setTenNhanVien(rs.getString("ho_ten"));
					shipment.setEmployee(empl);

					NhaCungCap ncc = new NhaCungCap();
					ncc.setId(rs.getInt("ma_ncc"));
					ncc.setMaNhaMay(rs.getString("ma_nha_may"));
					ncc.setTenNhaCungCap(rs.getString("ten_nha_cung_cap"));
					shipment.setNhaCungCap(ncc);

					return Optional.of(shipment);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error getShipmentById: {}", e.getMessage(), e);
		}

		return Optional.empty();
	}

	@Override
	public boolean addShipment(Batch shipment) {
		String sql = "{ call sp_insert_lo_thuoc(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) }";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 CallableStatement cs = con.prepareCall(sql)) {

			cs.setDate(1, java.sql.Date.valueOf(shipment.getNgaySanXuat()));
			cs.setDate(2, java.sql.Date.valueOf(shipment.getHanSuDung()));
			cs.setInt(3, shipment.getSoLuongNhap());
			cs.setInt(4, shipment.getSoLuongCon());
			cs.setDouble(5, shipment.getGiaNhap());
			cs.setDouble(6, shipment.getGiaBan());
			cs.setInt(7, shipment.getProduct().getMaSanPham());
			cs.setInt(8, shipment.getNhaCungCap().getId());
			cs.setString(9, shipment.getEmployee().getMaNhanVien());

			cs.registerOutParameter(10, java.sql.Types.VARCHAR);

			logger.debug("[DAO] Calling stored procedure: sp_insert_lo_thuoc(...)");

			cs.execute();
			String soLo = cs.getString(10);
			shipment.setSoLo(soLo);

			logger.info("[DAO] Inserted new shipment successfully. so_lo={}", soLo);
			return true;

		} catch (SQLException e) {
			logger.error("[DAO] Error addShipment: {}", e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean updateShipment(Batch shipment) {
		String sql = "update tbl_lo_thuoc set ngay_san_xuat=?, han_su_dung=?, so_luong_nhap=?, so_luong_con=?, "
				+ "gia_nhap=?, gia_ban=?, ma_ncc=?, trang_thai_id=?, updated_at=? where so_lo=?";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setDate(1, java.sql.Date.valueOf(shipment.getNgaySanXuat()));
			stmt.setDate(2, java.sql.Date.valueOf(shipment.getHanSuDung()));
			stmt.setInt(3, shipment.getSoLuongNhap());
			stmt.setInt(4, shipment.getSoLuongCon());
			stmt.setDouble(5, shipment.getGiaNhap());
			stmt.setDouble(6, shipment.getGiaBan());
			stmt.setInt(7, shipment.getNhaCungCap().getId());
			stmt.setInt(8, shipment.getShipmentStatus().getId());
			stmt.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

			stmt.setString(10, shipment.getSoLo());

			logger.debug("[DAO] Update shipment SQL: {} | so_lo={}", "update tbl_lo_thuoc...", shipment.getSoLo());
			boolean result = stmt.executeUpdate() > 0;

			if (result)
				logger.info("[DAO] Updated shipment successfully. so_lo={}", shipment.getSoLo());
			else
				logger.warn("[DAO] No shipment updated for so_lo={}", shipment.getSoLo());

			return result;
		} catch (SQLException e) {
			logger.error("[DAO] Error updateShipment: {}", e.getMessage(), e);
		}
		return false;
	}

	@Override
	public Optional<Product> getProdByBarcode(String barcode) {
		String sql = "select id, ten_sp, avatar_url, don_vi_tinh, deleted, loai_sp_id from tbl_san_pham where barcode=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Product product = new Product();

					product.setMaSanPham(rs.getInt("id"));
					product.setTenSanPham(rs.getString("ten_sp"));
					product.setBarcode(barcode);
					product.setDonViTinh(rs.getString("don_vi_tinh"));
					product.setAvatarUrl(rs.getString("avatar_url"));
					product.setDeleted(rs.getBoolean("deleted"));
					product.setLoaiSanPham(new ProductType(rs.getInt("loai_sp_id")));

					return Optional.of(product);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error getProdByBarcode: {}", e.getMessage(), e);
		}

		return Optional.empty();
	}

	@Override
	public List<Batch> getFilteredShipment(int type, int filter, int offset, int pageSize, LocalDate dateFrom,
			LocalDate dateTo, int option) {

		String orderColumn = (option == 0) ? "lo.ngay_nhap" : "lo.han_su_dung";

		StringBuilder sql = new StringBuilder("""
				    SELECT lo.so_lo, lo.ngay_nhap, lo.han_su_dung, lo.so_luong_nhap, lo.so_luong_con, lo.gia_ban,
				           sp.barcode, sp.ten_sp, sp.don_vi_tinh,
				           st_lo.id, st_lo.ten_trang_thai
				    FROM tbl_lo_thuoc lo
				    INNER JOIN tbl_san_pham sp ON lo.ma_sp = sp.id
				    INNER JOIN tbl_trang_thai_lo st_lo ON lo.trang_thai_id = st_lo.id
				    WHERE 1 = 1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQuery(sql, params, type, filter, dateFrom, dateTo);

		sql.append(" ORDER BY ").append(orderColumn).append(" DESC ");
		sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");

		List<Batch> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			// params filter
			buildParams(stmt, params);

			// params phân trang
			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			logger.debug("[DAO] getFilteredShipment SQL executed.");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Batch shipment = new Batch();
					shipment.setSoLo(rs.getString("so_lo"));
					shipment.setNgayNhap(rs.getTimestamp("ngay_nhap").toLocalDateTime());
					shipment.setHanSuDung(rs.getDate("han_su_dung").toLocalDate());
					shipment.setSoLuongNhap(rs.getInt("so_luong_nhap"));
					shipment.setSoLuongCon(rs.getInt("so_luong_con"));
					shipment.setGiaBan(rs.getDouble("gia_ban"));

					Product prod = new Product();
					prod.setBarcode(rs.getString("barcode"));
					prod.setTenSanPham(rs.getString("ten_sp"));
					prod.setDonViTinh(rs.getString("don_vi_tinh"));
					shipment.setProduct(prod);

					BatchStatus status = new BatchStatus();
					status.setId(rs.getInt("id"));
					status.setTenTrangThai(rs.getString("ten_trang_thai"));
					shipment.setShipmentStatus(status);

					list.add(shipment);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error getFilteredShipment: {}", e.getMessage(), e);
		}

		return list;
	}

	@Override
	public List<Batch> getFilteredShipmentAndSearchBySoLo(int type, int filter, int offset, int pageSize,
			LocalDate dateFrom, LocalDate dateTo, String keyword, int option) {

		String orderColumn = (option == 0) ? "lo.ngay_nhap" : "lo.han_su_dung";

		StringBuilder sql = new StringBuilder("""
				    SELECT lo.so_lo, lo.ngay_nhap, lo.han_su_dung, lo.so_luong_nhap, lo.so_luong_con, lo.gia_ban,
				           sp.barcode, sp.ten_sp, sp.don_vi_tinh,
				           st_lo.id, st_lo.ten_trang_thai
				    FROM tbl_lo_thuoc lo
				    INNER JOIN tbl_san_pham sp ON lo.ma_sp = sp.id
				    INNER JOIN tbl_trang_thai_lo st_lo ON lo.trang_thai_id = st_lo.id
				    WHERE 1 = 1
				""");

		List<Object> params = new ArrayList<>();
		buildFilterQuery(sql, params, type, filter, dateFrom, dateTo);

		sql.append(" and lo.so_lo like ? ");
		params.add("%" + keyword + "%");

		sql.append(" ORDER BY ").append(orderColumn).append(" DESC ");
		sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");

		List<Batch> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			// params filter
			buildParams(stmt, params);

			// params phân trang
			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			logger.debug("[DAO] getFilteredShipmentAndSearchBySoLo keyword='{}'", keyword);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Batch shipment = new Batch();
					shipment.setSoLo(rs.getString("so_lo"));
					shipment.setNgayNhap(rs.getTimestamp("ngay_nhap").toLocalDateTime());
					shipment.setHanSuDung(rs.getDate("han_su_dung").toLocalDate());
					shipment.setSoLuongNhap(rs.getInt("so_luong_nhap"));
					shipment.setSoLuongCon(rs.getInt("so_luong_con"));
					shipment.setGiaBan(rs.getDouble("gia_ban"));

					Product prod = new Product();
					prod.setBarcode(rs.getString("barcode"));
					prod.setTenSanPham(rs.getString("ten_sp"));
					prod.setDonViTinh(rs.getString("don_vi_tinh"));
					shipment.setProduct(prod);

					BatchStatus status = new BatchStatus();
					status.setId(rs.getInt("id"));
					status.setTenTrangThai(rs.getString("ten_trang_thai"));
					shipment.setShipmentStatus(status);

					list.add(shipment);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error getFilteredShipmentAndSearchBySoLo: {}", e.getMessage(), e);
		}

		return list;
	}

	@Override
	public int countFilteredShipmentAndSearchBySoLo(int type, int filter, LocalDate dateFrom, LocalDate dateTo,
			String keyword) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tbl_lo_thuoc lo where 1=1 ");

		List<Object> params = new ArrayList<>();
		buildFilterQuery(sql, params, type, filter, dateFrom, dateTo);

		sql.append(" and lo.so_lo like ? ");
		params.add("%" + keyword + "%");

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error countFilteredShipmentAndSearchBySoLo: {}", e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public int countFilteredShipment(int type, int filter, LocalDate dateFrom, LocalDate dateTo) {
		StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM tbl_lo_thuoc lo where 1=1 ");

		List<Object> params = new ArrayList<>();
		buildFilterQuery(sql, params, type, filter, dateFrom, dateTo);

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			// set parameters
			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error countFilteredShipment: {}", e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public int countFilteredShipmentAndSearchByBarcode(int type, int filter, LocalDate dateFrom, LocalDate dateTo,
			String barcode) {
		StringBuilder sql = new StringBuilder(
				"SELECT COUNT(*) FROM tbl_lo_thuoc lo inner join tbl_san_pham sp on sp.id = lo.ma_sp where sp.barcode=? ");

		List<Object> params = new ArrayList<>();
		params.add(barcode);

		buildFilterQuery(sql, params, type, filter, dateFrom, dateTo);

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error countFilteredShipmentAndSearchByBarcode: {}", e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<Batch> getFilteredShipmentAndSearchByBarcode(int type, int filter, int offset, int pageSize,
			LocalDate dateFrom, LocalDate dateTo, String barcode, int option) {

		String orderColumn = (option == 0) ? "lo.ngay_nhap" : "lo.han_su_dung";

		StringBuilder sql = new StringBuilder("""
				    SELECT lo.so_lo, lo.ngay_nhap, lo.han_su_dung, lo.so_luong_nhap, lo.so_luong_con, lo.gia_ban,
				           sp.barcode, sp.ten_sp, sp.don_vi_tinh,
				           st_lo.id, st_lo.ten_trang_thai
				    FROM tbl_lo_thuoc lo
				    INNER JOIN tbl_san_pham sp ON lo.ma_sp = sp.id
				    INNER JOIN tbl_trang_thai_lo st_lo ON lo.trang_thai_id = st_lo.id
				    WHERE sp.barcode=?
				""");

		List<Object> params = new ArrayList<>();
		params.add(barcode);

		buildFilterQuery(sql, params, type, filter, dateFrom, dateTo);

		sql.append(" ORDER BY ").append(orderColumn).append(" DESC ");
		sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");

		List<Batch> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			// params filter
			buildParams(stmt, params);

			// params phân trang
			int idx = params.size() + 1;
			stmt.setInt(idx++, offset);
			stmt.setInt(idx, pageSize);

			logger.debug("[DAO] getFilteredShipmentAndSearchByBarcode barcode={}", barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Batch shipment = new Batch();
					shipment.setSoLo(rs.getString("so_lo"));
					shipment.setNgayNhap(rs.getTimestamp("ngay_nhap").toLocalDateTime());
					shipment.setHanSuDung(rs.getDate("han_su_dung").toLocalDate());
					shipment.setSoLuongNhap(rs.getInt("so_luong_nhap"));
					shipment.setSoLuongCon(rs.getInt("so_luong_con"));
					shipment.setGiaBan(rs.getDouble("gia_ban"));

					Product prod = new Product();
					prod.setBarcode(rs.getString("barcode"));
					prod.setTenSanPham(rs.getString("ten_sp"));
					prod.setDonViTinh(rs.getString("don_vi_tinh"));
					shipment.setProduct(prod);

					BatchStatus status = new BatchStatus();
					status.setId(rs.getInt("id"));
					status.setTenTrangThai(rs.getString("ten_trang_thai"));
					shipment.setShipmentStatus(status);

					list.add(shipment);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error getFilteredShipmentAndSearchByBarcode: {}", e.getMessage(), e);
		}

		return list;
	}

	private void buildFilterQuery(StringBuilder sql, List<Object> params, int type, int filter, LocalDate dateFrom,
			LocalDate dateTo) {

		// lọc theo trạng thái
		if (type != 0) {
			sql.append(" AND lo.trang_thai_id = ? ");
			params.add(type);
		}

		// lọc theo filter
		if (filter != 0) {
			switch (filter) {
			case 1 -> sql.append(" AND CAST(lo.ngay_nhap AS DATE) = CAST(GETDATE() AS DATE) ");
			case 2 -> sql.append(" AND CAST(lo.ngay_nhap AS DATE) >= DATEADD(DAY, -7, CAST(GETDATE() AS DATE)) ");
			case 3 -> sql.append("""
					    AND MONTH(lo.ngay_nhap) = MONTH(GETDATE())
					    AND YEAR(lo.ngay_nhap) = YEAR(GETDATE())
					""");
			case 4 -> {
				sql.append(" AND CAST(lo.ngay_nhap AS DATE) BETWEEN ? AND ? ");
				params.add(Date.valueOf(dateFrom));
				params.add(Date.valueOf(dateTo));
			}
			case 5 -> sql.append("""
					AND lo.han_su_dung BETWEEN CAST(GETDATE() AS DATE)
					AND CAST(DATEADD(DAY, 7, GETDATE()) AS DATE)
					""");

			case 6 -> sql.append("""
					AND lo.han_su_dung BETWEEN CAST(GETDATE() AS DATE)
					AND CAST(DATEADD(DAY, 30, GETDATE()) AS DATE)
					""");

			case 7 -> sql.append("""
					AND lo.han_su_dung BETWEEN CAST(GETDATE() AS DATE)
					AND CAST(DATEADD(MONTH, 3, GETDATE()) AS DATE)
					""");
			}
		}
	}

	private void buildParams(PreparedStatement stmt, List<Object> params) throws SQLException {
		int index = 1;
		for (Object p : params) {
			if (p instanceof LocalDate ld) {
				stmt.setDate(index++, Date.valueOf(ld));
			} else {
				stmt.setObject(index++, p);
			}
		}
	}

	@Override
	public boolean deductBatchQuantity(Connection con, String soLo, int soLuongCanLay) {
		String sql = "update tbl_lo_thuoc set so_luong_con = so_luong_con - ? where so_lo=? and so_luong_con >= 0 and trang_thai_id=1";
		
		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, soLuongCanLay);
			stmt.setString(2, soLo);

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("[BatchDAO] deductBatchQuantity failed. Batch: {}. Error: {}", soLo, e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean capNhatTrangThaiHetHan() {
		String sql = "{call sp_KiemTraVaCapNhatHetHan}";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 CallableStatement cstmt = con.prepareCall(sql)) {

			cstmt.execute();
			logger.info("[DAO] Executed Stored Procedure: sp_KiemTraVaCapNhatHetHan successfully.");
			return true;
		} catch (SQLException e) {
			logger.error("[BatchDAO] capNhatTrangThaiHetHan failed: {}", e.getMessage(), e);
		}
		return false;
	}

	@Override
	public boolean updateQuantity(Connection con, InvoiceDetailReturn inv, int newQty) {
		String sql = "update tbl_lo_thuoc set so_luong_con = so_luong_con + ? where so_lo=?";
		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, newQty);
			stmt.setString(2, inv.getBatch().getSoLo());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			logger.error("[BatchDAO] updateQuantity failed. Batch: {}. Error: {}", inv.getBatch().getSoLo(),
					e.getMessage(), e);
		}

		return false;
	}

	@Override
	public List<Batch> getAllBatchToExport(int type, int filter, LocalDate dateFrom, LocalDate dateTo, int option) {
		String orderColumn = (option == 0) ? "lo.ngay_nhap" : "lo.han_su_dung";

		StringBuilder sql = new StringBuilder(
				"""
							SELECT lo.so_lo, lo.ngay_nhap, lo.ngay_san_xuat, lo.han_su_dung, lo.so_luong_nhap, lo.so_luong_con, lo.gia_ban, lo.gia_nhap, lo.thanh_tien,
						           sp.barcode, sp.ten_sp, sp.don_vi_tinh,
						           st_lo.id, st_lo.ten_trang_thai,
								   nv.ma_nv, nv.ho_ten,
								   ncc.id as ncc_id, ncc.ma_nha_may, ncc.ten_nha_cung_cap
						    FROM tbl_lo_thuoc lo
						    INNER JOIN tbl_san_pham sp ON lo.ma_sp = sp.id
						    INNER JOIN tbl_trang_thai_lo st_lo ON lo.trang_thai_id = st_lo.id
							INNER JOIN tbl_nhan_vien nv on lo.ma_nv = nv.ma_nv
							INNER JOIN tbl_nha_cung_cap ncc on lo.ma_ncc = ncc.id
						    WHERE 1 = 1
						""");

		List<Object> params = new ArrayList<>();
		buildFilterQuery(sql, params, type, filter, dateFrom, dateTo);

		sql.append(" ORDER BY ").append(orderColumn).append(" DESC ");

		List<Batch> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			buildParams(stmt, params);

			logger.debug("[DAO] getAllBatchToExport SQL executed.");

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Batch shipment = new Batch();
					shipment.setSoLo(rs.getString("so_lo"));
					shipment.setNgayNhap(rs.getTimestamp("ngay_nhap").toLocalDateTime());
					shipment.setHanSuDung(rs.getDate("han_su_dung").toLocalDate());
					shipment.setNgaySanXuat(rs.getDate("ngay_san_xuat").toLocalDate());
					shipment.setSoLuongNhap(rs.getInt("so_luong_nhap"));
					shipment.setSoLuongCon(rs.getInt("so_luong_con"));
					shipment.setGiaBan(rs.getDouble("gia_ban"));
					shipment.setGiaNhap(rs.getDouble("gia_nhap"));
					shipment.setThanhTien(rs.getDouble("thanh_tien"));

					Product prod = new Product();
					prod.setBarcode(rs.getString("barcode"));
					prod.setTenSanPham(rs.getString("ten_sp"));
					prod.setDonViTinh(rs.getString("don_vi_tinh"));
					shipment.setProduct(prod);

					BatchStatus status = new BatchStatus();
					status.setId(rs.getInt("id"));
					status.setTenTrangThai(rs.getString("ten_trang_thai"));
					shipment.setShipmentStatus(status);

					Employee employee = new Employee(rs.getString("ma_nv"), rs.getString("ho_ten"));
					shipment.setEmployee(employee);

					NhaCungCap ncc = new NhaCungCap(rs.getInt("ncc_id"), rs.getString("ma_nha_may"),
							rs.getString("ten_nha_cung_cap"));
					shipment.setNhaCungCap(ncc);

					list.add(shipment);
				}
			}
		} catch (SQLException e) {
			logger.error("[DAO] Error getAllBatchToExport: {}", e.getMessage(), e);
		}

		return list;
	}

}