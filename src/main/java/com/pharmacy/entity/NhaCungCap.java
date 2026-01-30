package com.pharmacy.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class NhaCungCap {

	private int id;

	private String maNhaMay;

	private String tenNhaCungCap;

	private String diaChi;

	private String soDienThoai;

	private String maSoThue;

	private String email;

	private String website;

	private String ghiChu;

	public NhaCungCap(int id) {
		this.id = id;
	}

	public String toString() {
		String s = this.maNhaMay + " - " + this.tenNhaCungCap;
		if (s.length() > 90)
			return s.substring(0, 90) + "...";
		return s;
	}

	public NhaCungCap(int id, String maNhaMay, String tenNhaCungCap) {
		this.id = id;
		this.maNhaMay = maNhaMay;
		this.tenNhaCungCap = tenNhaCungCap;
	}

	public Object[] getObject() {
		return new Object[] { maNhaMay, tenNhaCungCap, diaChi, soDienThoai, email };
	}

}
