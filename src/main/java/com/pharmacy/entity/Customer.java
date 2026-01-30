package com.pharmacy.entity;

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
public class Customer {

	private int maKhachHang;

	private String tenKhachHang;

	private String soDienThoai;

	private int diemTichLuy;

	private CustomerRank customerRank;

	public Customer(String tenKH, String sdt) {
		this.tenKhachHang = tenKH;
		this.soDienThoai = sdt;
	}

	public void setTangDiemTichLuy(double tongTienHang) {
		this.diemTichLuy = (int) tongTienHang / 1000;

	}
	
	public String getFullName() {
		return String.format("%s - %s(%d)", tenKhachHang, customerRank.getTenHangTV(), diemTichLuy);
	}

	public Customer(String soDienThoai) {
		this.soDienThoai = soDienThoai;
	}
	
	public Object[] getObject () {
		return new Object[] {maKhachHang, tenKhachHang, soDienThoai, diemTichLuy, customerRank.getTenHangTV()};
	}

}
