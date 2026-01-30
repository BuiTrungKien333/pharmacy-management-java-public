package com.pharmacy.entity;

import java.time.LocalDateTime;

import com.pharmacy.utils.FormatUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Invoice {

	private String maHoaDon;

	private Customer customer;

	private Employee employee;

	private Voucher voucher;

	private LocalDateTime ngayLap;

	private double tongTienHang;

	private double tongTienCanThanhToan;

	private String ghiChu;

	private boolean daTra;

	public Invoice(String maHoaDon) {
		this.maHoaDon = maHoaDon;
	}

	public Object[] getObjects() {
		return new Object[] { maHoaDon, FormatUtil.formatDate(ngayLap), customer.getSoDienThoai(), "Hóa đơn bán",
				"Đã bán", FormatUtil.formatVND(tongTienCanThanhToan) + " VND" };
	}

	public Object[] getObjectsToExcel() {
		return new Object[] { maHoaDon, ngayLap,
				customer == null ? "Vãng lai"
						: String.format("%s - %s", customer.getSoDienThoai(), customer.getTenKhachHang()),
				String.format("%s - %s", employee.getMaNhanVien(), employee.getTenNhanVien()),
				voucher == null ? "" : voucher.getMaVoucher(), tongTienCanThanhToan, daTra == true ? "Đã trả" : "" };
	}

}
