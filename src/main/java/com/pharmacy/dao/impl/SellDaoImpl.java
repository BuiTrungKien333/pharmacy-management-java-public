package com.pharmacy.dao.impl;

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
import com.pharmacy.dao.SellDAO;
import com.pharmacy.dto.BatchReqDTO;
import com.pharmacy.dto.ProductBatchesReqDTO;

public class SellDaoImpl implements SellDAO {
	
	private static final Logger log = LoggerFactory.getLogger(SellDaoImpl.class);

	@Override
	public ProductBatchesReqDTO getBatchesByBarcode(String barcode) {
		String sql = """
				select lo.so_lo, lo.han_su_dung, lo.so_luong_con, lo.gia_ban,
				sp.id as ma_sp, sp.ten_sp, sp.don_vi_tinh, sp.avatar_url, sp.deleted
				from tbl_lo_thuoc lo
				join tbl_san_pham sp on lo.ma_sp = sp.id
				where sp.barcode = ?
				and lo.trang_thai_id = 1
				and lo.so_luong_con > 0
				and lo.han_su_dung > DATEADD(month, 1, CAST(GETDATE() AS DATE))
				order by lo.han_su_dung asc
				""";

		ProductBatchesReqDTO result = null;
		List<BatchReqDTO> danhSachLo = new ArrayList<>();
		int tongSoLuong = 0;

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, barcode);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					if (result == null) {
						result = new ProductBatchesReqDTO();

						result.setMaSanPham(rs.getInt("ma_sp"));
						result.setTenSanPham(rs.getString("ten_sp"));
						result.setBarcode(barcode);
						result.setDonViTinh(rs.getString("don_vi_tinh"));
						result.setAvatarUrl(rs.getString("avatar_url"));
						result.setDeleted(rs.getBoolean("deleted"));
						result.setGiaBanThucTe(rs.getDouble("gia_ban"));
					}

					String soLo = rs.getString("so_lo");
					LocalDate hsd = rs.getDate("han_su_dung").toLocalDate();
					int slCon = rs.getInt("so_luong_con");
					double giaBan = rs.getDouble("gia_ban");

					danhSachLo.add(new BatchReqDTO(soLo, hsd, slCon, giaBan));

					tongSoLuong += slCon;
				}

				if (result != null) {
					result.setDanhSachLoThuoc(danhSachLo);
					result.setTongSoLuong(tongSoLuong);
				}
			}
		} catch (SQLException e) {
			log.error("[SellDaoImpl] getBatchesByBarcode failed with barcode {}: {}", barcode, e.getMessage(), e);
		}

		return result;
	}

}