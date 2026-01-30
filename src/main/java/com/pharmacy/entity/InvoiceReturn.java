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
public class InvoiceReturn {

	private String maHDTra;

	private LocalDateTime ngayLap;

	private double tienHoan;

	private String lyDo;

	private Invoice invoice;

	private Customer customer;

	private Employee employee;

	private boolean daDuyet;

	public Object[] getObjects() {
		return new Object[] { maHDTra, FormatUtil.formatDate(ngayLap), customer.getSoDienThoai(), "Hóa đơn trả",
				daDuyet == true ? "Đã duyệt" : "Chờ xử lý", FormatUtil.formatVND(tienHoan) + " VND" };
	}

	public Object[] getObjectsToExcel() {
		return new Object[] { maHDTra, ngayLap,
				String.format("%s - %s", customer.getSoDienThoai(), customer.getTenKhachHang()), invoice.getMaHoaDon(),
				String.format("%s - %s", employee.getMaNhanVien(), employee.getTenNhanVien()), tienHoan, lyDo,
				daDuyet == true ? "Đã duyệt" : "Chờ xử lý" };
	}

}
