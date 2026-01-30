package com.pharmacy.entity;

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
public class InvoiceDetail {

	private int id;

	private int soLuong;

	private double donGia;

	private double thanhTien;

	private Invoice invoice;

	private Product product;

	private Batch shipment;

	public void setThanhTien() {
		this.thanhTien = this.soLuong * this.donGia;
	}

	public Object[] getObjects() {
		return new Object[] { false, product.getTenSanPham(), shipment.getSoLo(), soLuong, FormatUtil.formatVND(donGia),
				FormatUtil.formatVND(thanhTien) };
	}

	public Object[] getObjects(int index) {
		return new Object[] { index, product.getTenSanPham(), shipment.getSoLo(), soLuong, FormatUtil.formatVND(donGia),
				FormatUtil.formatVND(thanhTien) };
	}

}
