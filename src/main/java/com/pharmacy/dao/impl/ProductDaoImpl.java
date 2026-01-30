package com.pharmacy.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.dao.ProductDAO;
import com.pharmacy.entity.Product;
import com.pharmacy.entity.ProductType;

public class ProductDaoImpl implements ProductDAO {

	private static final Logger log = LoggerFactory.getLogger(ProductDaoImpl.class);

	@Override
	public Optional<Product> getProdByBarcode(String barcode) {
		String sql = "select * from tbl_san_pham where barcode=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, barcode);

			log.debug("[DAO] getProdByBarcode SQL: {} | Param: {}", sql, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Product product = new Product();
					product.setMaSanPham(rs.getInt("id"));
					product.setTenSanPham(rs.getString("ten_sp"));
					product.setTenSanPhamKhongDau(rs.getString("ten_sp_khong_dau"));
					product.setBarcode(barcode);
					product.setHoatChatHamLuong(rs.getString("hoat_chat_ham_luong"));
					product.setDangBaoChe(rs.getString("dang_bao_che"));
					product.setDuongDung(rs.getString("duong_dung"));
					product.setChiDinh(rs.getString("chi_dinh"));
					product.setChongChiDinh(rs.getString("chong_chi_dinh"));
					product.setLieuDung(rs.getString("lieu_dung"));
					product.setSoDangKi(rs.getString("so_dang_ky"));
					product.setNuocSanXuat(rs.getString("nuoc_san_xuat"));
					product.setNhaSanXuat(rs.getString("nha_san_xuat"));
					product.setTieuChuanChatLuong(rs.getString("tieu_chuan_chat_luong"));
					product.setQuyCachDongGoi(rs.getString("quy_cach_dong_goi"));
					product.setMoTa(rs.getString("mo_ta"));
					product.setDonViTinh(rs.getString("don_vi_tinh"));
					product.setAvatarUrl(rs.getString("avatar_url"));
					product.setTongSoLuong(rs.getInt("tong_so_luong"));
					product.setLoaiSanPham(new ProductType(rs.getInt("loai_sp_id")));
					product.setDeleted(rs.getBoolean("deleted"));

					return Optional.of(product);
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] Error getProdByBarcode: {}", e.getMessage(), e);
		}

		return Optional.empty();
	}

	@Override
	public boolean addProduct(Product product) {
		String sql = "INSERT INTO tbl_san_pham ("
				+ "ten_sp, ten_sp_khong_dau, hoat_chat_ham_luong, dang_bao_che, duong_dung, "
				+ "chi_dinh, chong_chi_dinh, lieu_dung, so_dang_ky, nuoc_san_xuat, "
				+ "nha_san_xuat, tieu_chuan_chat_luong, quy_cach_dong_goi, mo_ta, "
				+ "avatar_url, loai_sp_id, barcode, don_vi_tinh, tong_so_luong, deleted )"
				+ " OUTPUT inserted.id VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, product.getTenSanPham());
			stmt.setString(2, product.getTenSanPhamKhongDau());
			stmt.setString(3, product.getHoatChatHamLuong());
			stmt.setString(4, product.getDangBaoChe());
			stmt.setString(5, product.getDuongDung());
			stmt.setString(6, product.getChiDinh());
			stmt.setString(7, product.getChongChiDinh());
			stmt.setString(8, product.getLieuDung());
			stmt.setString(9, product.getSoDangKi());
			stmt.setString(10, product.getNuocSanXuat());
			stmt.setString(11, product.getNhaSanXuat());
			stmt.setString(12, product.getTieuChuanChatLuong());
			stmt.setString(13, product.getQuyCachDongGoi());
			stmt.setString(14, product.getMoTa());
			stmt.setString(15, product.getAvatarUrl());
			stmt.setInt(16, product.getLoaiSanPham().getId());
			stmt.setString(17, product.getBarcode());
			stmt.setString(18, product.getDonViTinh());
			stmt.setInt(19, product.getTongSoLuong());
			stmt.setBoolean(20, product.isDeleted());

			log.debug("[DAO] Adding Product: {}", product.getTenSanPham());

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int id = rs.getInt(1);
					product.setMaSanPham(id);
					return true;
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] Error addProduct: {}", e.getMessage(), e);
		}

		return false;
	}

	@Override
	public boolean updateInfoProduct(Product product) {
		String sql = "UPDATE tbl_san_pham SET "
				+ "ten_sp = ?, ten_sp_khong_dau = ?, hoat_chat_ham_luong = ?, dang_bao_che = ?, duong_dung = ?, "
				+ "chi_dinh = ?, chong_chi_dinh = ?, lieu_dung = ?, so_dang_ky = ?, nuoc_san_xuat = ?, "
				+ "nha_san_xuat = ?, tieu_chuan_chat_luong = ?, quy_cach_dong_goi = ?, mo_ta = ?, avatar_url = ?, "
				+ "loai_sp_id = ?, barcode = ?, don_vi_tinh = ?, tong_so_luong = ?, deleted = ?, updated_at=?, deleted_at=? "
				+ "WHERE id = ?";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, product.getTenSanPham());
			stmt.setString(2, product.getTenSanPhamKhongDau());
			stmt.setString(3, product.getHoatChatHamLuong());
			stmt.setString(4, product.getDangBaoChe());
			stmt.setString(5, product.getDuongDung());
			stmt.setString(6, product.getChiDinh());
			stmt.setString(7, product.getChongChiDinh());
			stmt.setString(8, product.getLieuDung());
			stmt.setString(9, product.getSoDangKi());
			stmt.setString(10, product.getNuocSanXuat());
			stmt.setString(11, product.getNhaSanXuat());
			stmt.setString(12, product.getTieuChuanChatLuong());
			stmt.setString(13, product.getQuyCachDongGoi());
			stmt.setString(14, product.getMoTa());
			stmt.setString(15, product.getAvatarUrl());
			stmt.setInt(16, product.getLoaiSanPham().getId());
			stmt.setString(17, product.getBarcode());
			stmt.setString(18, product.getDonViTinh());
			stmt.setInt(19, product.getTongSoLuong());
			stmt.setBoolean(20, product.isDeleted());

			stmt.setTimestamp(21, Timestamp.valueOf(LocalDateTime.now()));

			if (product.isDeleted())
				stmt.setTimestamp(22, Timestamp.valueOf(LocalDateTime.now()));
			else
				stmt.setNull(22, java.sql.Types.TIMESTAMP);

			stmt.setInt(23, product.getMaSanPham());

			log.debug("[DAO] Updating Product ID: {}", product.getMaSanPham());

			return stmt.executeUpdate() > 0;
		} catch (SQLException e) {
			log.error("[DAO] Error updateInfoProduct: {}", e.getMessage(), e);
		}

		return false;
	}

	@Override
	public List<Product> getAllProdByPage(int offset, int limit) {
		long start = System.currentTimeMillis();
		List<Product> list = new ArrayList<>();
		String sql = "select id, barcode, ten_sp, tong_so_luong, don_vi_tinh, avatar_url from tbl_san_pham order by id offset ? rows fetch next ? rows only";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setInt(1, offset);
			stmt.setInt(2, limit);

			log.debug("[DAO] Executing SQL: {} (offset={}, limit={})", sql, offset, limit);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Product product = new Product();
					product.setMaSanPham(rs.getInt("id"));
					product.setBarcode(rs.getString("barcode"));
					product.setTenSanPham(rs.getString("ten_sp"));
					product.setTongSoLuong(rs.getInt("tong_so_luong"));
					product.setDonViTinh(rs.getString("don_vi_tinh"));
					product.setAvatarUrl(rs.getString("avatar_url"));

					list.add(product);
				}
			}
			log.debug("[DAO] Loaded {} rows in {} ms", list.size(), System.currentTimeMillis() - start);

		} catch (SQLException e) {
			log.error("[DAO] Error getAllProdByPage: {}", e.getMessage(), e);
		}

		return list;
	}

	@Override
	public int countProducts() {
		String sql = "SELECT COUNT(*) FROM tbl_san_pham";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement ps = con.prepareStatement(sql); 
			 ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
				int count = rs.getInt(1);
				log.info("[DAO] countProducts = {}", count);
				return count;
			}
		} catch (SQLException e) {
			log.error("[DAO] Error countProducts: {}", e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<Product> getFilteredProducts(int type, int filter, int offset, int limit) {
		long start = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder(
				"select id, barcode, ten_sp, tong_so_luong, don_vi_tinh, avatar_url from tbl_san_pham where 1=1 ");

		if (type != 0)
			sql.append("and loai_sp_id= ?");

		if (filter != 0) {
			switch (filter) {
			case 1 -> sql.append(" AND deleted=0 ");
			case 2 -> sql.append(" AND deleted=1 ");
			case 3 -> sql.append(" AND deleted=0 AND tong_so_luong > 0 ");
			case 4 -> sql.append(" AND deleted=0 AND tong_so_luong > 0 AND tong_so_luong <= 50 ");
			case 5 -> sql.append(" AND deleted=0 AND tong_so_luong = 0 ");
			}
		}

		sql.append(" ORDER BY id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

		List<Product> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			int index = 1;
			if (type != 0)
				stmt.setInt(index++, type);
			stmt.setInt(index++, offset);
			stmt.setInt(index, limit);

			log.debug("[DAO] Executing SQL: {} (type={}, filter={}, offset={}, limit={})", sql, type, filter, offset,
					limit);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Product product = new Product();
					product.setMaSanPham(rs.getInt("id"));
					product.setBarcode(rs.getString("barcode"));
					product.setTenSanPham(rs.getString("ten_sp"));
					product.setTongSoLuong(rs.getInt("tong_so_luong"));
					product.setDonViTinh(rs.getString("don_vi_tinh"));
					product.setAvatarUrl(rs.getString("avatar_url"));

					list.add(product);
				}
			}
			log.debug("[DAO] getFilteredProducts loaded {} rows in {} ms", list.size(),
					System.currentTimeMillis() - start);

		} catch (SQLException e) {
			log.error("[DAO] Error getFilteredProducts: {}", e.getMessage(), e);
		}

		return list;
	}

	@Override
	public int countFilteredProducts(int type, int filter) {
		StringBuilder sql = new StringBuilder("select count(*) from tbl_san_pham where 1=1 ");

		if (type != 0)
			sql.append("and loai_sp_id= ?");

		if (filter != 0) {
			switch (filter) {
			case 1 -> sql.append(" AND deleted=0 ");
			case 2 -> sql.append(" AND deleted=1 ");
			case 3 -> sql.append(" AND deleted=0 AND tong_so_luong > 0 ");
			case 4 -> sql.append(" AND deleted=0 AND tong_so_luong > 0 AND tong_so_luong <= 50 ");
			case 5 -> sql.append(" AND deleted=0 AND tong_so_luong = 0 ");
			}
		}

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			if (type != 0)
				stmt.setInt(1, type);

			log.debug("[DAO] Executing SQL: {} (type={}, filter={})", sql, type, filter);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					int count = rs.getInt(1);
					log.info("[DAO] countFilteredProducts result = {}", count);
					return count;
				}
			}
		} catch (SQLException e) {
			log.error("[DAO] Error countFilteredProducts: {}", e.getMessage(), e);
		}
		return 0;
	}

	@Override
	public List<Product> getFilteredProdsWithSearch(int type, int filter, String keyword) {
		long start = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder(
				"select top 50 id, barcode, ten_sp, tong_so_luong, don_vi_tinh, avatar_url from tbl_san_pham where 1=1 ");

		if (type != 0)
			sql.append("and loai_sp_id= ?");

		if (filter != 0) {
			switch (filter) {
			case 1 -> sql.append(" AND deleted=0 ");
			case 2 -> sql.append(" AND deleted=1 ");
			case 3 -> sql.append(" AND deleted=0 AND tong_so_luong > 0 ");
			case 4 -> sql.append(" AND deleted=0 AND tong_so_luong > 0 AND tong_so_luong <= 50 ");
			case 5 -> sql.append(" AND deleted=0 AND tong_so_luong = 0 ");
			}
		}

		sql.append(" and ten_sp_khong_dau like ?");

		List<Product> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			int index = 1;
			if (type != 0)
				stmt.setInt(index++, type);

			stmt.setString(index++, "%" + keyword + "%");

			log.debug("[DAO] Search SQL: {} | Keyword: {}", sql, keyword);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Product product = new Product();
					product.setMaSanPham(rs.getInt("id"));
					product.setBarcode(rs.getString("barcode"));
					product.setTenSanPham(rs.getString("ten_sp"));
					product.setTongSoLuong(rs.getInt("tong_so_luong"));
					product.setDonViTinh(rs.getString("don_vi_tinh"));
					product.setAvatarUrl(rs.getString("avatar_url"));

					list.add(product);
				}
			}
			log.debug("[DAO] Search finished: {} rows in {} ms", list.size(), System.currentTimeMillis() - start);

		} catch (SQLException e) {
			log.error("[DAO] Error getFilteredProdsWithSearch: {}", e.getMessage(), e);
		}

		return list;
	}

	@Override
	public boolean checkExistsBarcode(String barcode) {
		String sql = "select 1 from tbl_san_pham where barcode=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				boolean exists = rs.next();
				log.debug("[DAO] checkExistsBarcode('{}') -> {}", barcode, exists);
				return exists;
			}
		} catch (SQLException e) {
			log.error("[DAO] checkExistsBarcode failed: {}", e.getMessage(), e);
			return false;
		}
	}

	@Override
	public boolean checkExistsSoDangKi(String text) {
		String sql = "select 1 from tbl_san_pham where so_dang_ky=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, text);

			try (ResultSet rs = stmt.executeQuery()) {
				boolean exists = rs.next();
				log.debug("[DAO] checkExistsSoDangKi('{}') -> {}", text, exists);
				return exists;
			}
		} catch (SQLException e) {
			log.error("[DAO] checkExistsSoDangKi failed: {}", e.getMessage(), e);
			return false;
		}
	}

	@Override
	public List<Product> getAllProdToExport(int type, int filter) {
		long start = System.currentTimeMillis();
		StringBuilder sql = new StringBuilder(
				"select sp.id, barcode, ten_sp, hoat_chat_ham_luong, so_dang_ky, tong_so_luong, don_vi_tinh, deleted, loai_sp_id, lsp.ten_loai from tbl_san_pham sp inner join tbl_loai_san_pham lsp on sp.loai_sp_id = lsp.id where 1=1 ");

		if (type != 0)
			sql.append(" and loai_sp_id= ?");

		if (filter != 0) {
			switch (filter) {
			case 1 -> sql.append(" AND deleted=0 ");
			case 2 -> sql.append(" AND deleted=1 ");
			case 3 -> sql.append(" AND deleted=0 AND tong_so_luong > 0 ");
			case 4 -> sql.append(" AND deleted=0 AND tong_so_luong > 0 AND tong_so_luong <= 50 ");
			case 5 -> sql.append(" AND deleted=0 AND tong_so_luong = 0 ");
			}
		}

		List<Product> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql.toString())) {

			if (type != 0) 
				stmt.setInt(1, type);
			
			log.debug("[DAO] Executing SQL: {} (type={}, filter={}, export to Excel)", sql, type, filter);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Product product = new Product();
					product.setMaSanPham(rs.getInt("id"));
					product.setBarcode(rs.getString("barcode"));
					product.setTenSanPham(rs.getString("ten_sp"));
					product.setHoatChatHamLuong(rs.getString("hoat_chat_ham_luong"));
					product.setSoDangKi(rs.getString("so_dang_ky"));
					product.setTongSoLuong(rs.getInt("tong_so_luong"));
					product.setDonViTinh(rs.getString("don_vi_tinh"));
					product.setDeleted(rs.getBoolean("deleted"));
					ProductType prodType = new ProductType(rs.getInt("loai_sp_id"), rs.getString("ten_loai"));
					product.setLoaiSanPham(prodType);

					list.add(product);
				}
			}
			log.debug("[DAO] getAllProdToExport loaded {} rows in {} ms", list.size(),
					System.currentTimeMillis() - start);

		} catch (SQLException e) {
			log.error("[DAO] Error getAllProdToExport: {}", e.getMessage(), e);
		}

		return list;
	}

	// ------------------------------------------------------------------
	@Override
	public List<String> getDangBaoChe() {
		String sql = "select ten_dang_bao_che from tbl_dang_bao_che";
		List<String> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(rs.getString("ten_dang_bao_che"));
			}
		} catch (SQLException e) {
			log.error("[DAO] Error getDangBaoChe: {}", e.getMessage(), e);
		}
		return list;
	}

	@Override
	public List<String> getDuongDung() {
		String sql = "select ten_duong_dung from tbl_duong_dung";
		List<String> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(rs.getString("ten_duong_dung"));
			}
		} catch (SQLException e) {
			log.error("[DAO] Error getDuongDung: {}", e.getMessage(), e);
		}
		return list;
	}

	@Override
	public List<String> getTieuChuanChatLuong() {
		String sql = "select ten_tieu_chuan from tbl_tieu_chuan_chat_luong";
		List<String> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(rs.getString("ten_tieu_chuan"));
			}
		} catch (SQLException e) {
			log.error("[DAO] Error getTieuChuanChatLuong: {}", e.getMessage(), e);
		}
		return list;
	}

	@Override
	public List<String> getDonViTinh() {
		String sql = "select ten_dvt from tbl_don_vi_tinh";
		List<String> list = new ArrayList<>();

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				list.add(rs.getString("ten_dvt"));
			}
		} catch (SQLException e) {
			log.error("[DAO] Error getDonViTinh: {}", e.getMessage(), e);
		}
		return list;
	}

}