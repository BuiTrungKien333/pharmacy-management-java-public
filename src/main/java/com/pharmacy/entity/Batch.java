package com.pharmacy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "soLo")
public class Batch {

	private String soLo;

	private LocalDate ngaySanXuat;

	private LocalDate hanSuDung;

	private LocalDateTime ngayNhap;

	private int soLuongNhap;

	private int soLuongCon;

	private double giaNhap;

	private double thanhTien;

	private double giaBan;

	private Product product;

	private NhaCungCap nhaCungCap;

	private Employee employee;

	private BatchStatus shipmentStatus;

	public Batch(String soLo) {
		this.soLo = soLo;
	}

	public void setThanhTienNhap() {
		this.thanhTien = soLuongNhap * giaNhap;
	}

	public Object[] getObjects() {
		return new Object[] { soLo, String.format("%s - %s", product.getBarcode(), product.getTenSanPham()),
				String.format("%s - %s", nhaCungCap.getMaNhaMay(), nhaCungCap.getTenNhaCungCap()),
				String.format("%s - %s", employee.getMaNhanVien(), employee.getTenNhanVien()), ngaySanXuat, hanSuDung,
				ngayNhap, soLuongNhap, product.getDonViTinh(), giaNhap, thanhTien, soLuongCon, giaBan,
				shipmentStatus.getTenTrangThai() };
	}

}
