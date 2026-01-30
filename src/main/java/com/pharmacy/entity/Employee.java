package com.pharmacy.entity;


import java.util.Date;
import java.util.Objects;

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
public class Employee {

	private String maNhanVien;
	private String tenNhanVien;
	private String soDienThoai;
	private String email;
	private Date ngaySinh;
	private boolean gioiTinh;
	private String diaChi;
	private Store cuaHang;
	private boolean deleted;
	private String avt_url;
	private Date ngayVaoLam;

	public Employee(String tenNhanVien, String soDienThoai, String email, Date ngaySinh, boolean gioiTinh,
			String diaChi, Store cuaHang, boolean deleted, String avt_url, Date ngayVaoLam) {
		this.tenNhanVien = tenNhanVien;
		this.soDienThoai = soDienThoai;
		this.email = email;
		this.ngaySinh = ngaySinh;
		this.gioiTinh = gioiTinh;
		this.diaChi = diaChi;
		this.cuaHang = cuaHang;
		this.deleted = deleted;
		this.avt_url = avt_url;
		this.ngayVaoLam = ngayVaoLam;
	}

	public Employee(String maNhanVien) {
		this.maNhanVien = maNhanVien;
	}
	
	public String getDisplayName() {
		return this.maNhanVien + " - " + this.tenNhanVien;
	}

	public Object[] getObject () {
		return new Object[] {maNhanVien, tenNhanVien, "dược sĩ",soDienThoai, email, isDeleted() ? "Nghỉ việc" :"Đang làm việc" };
	}

	@Override
	public int hashCode() {
		return Objects.hash(maNhanVien);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Employee other = (Employee) obj;
		return Objects.equals(maNhanVien, other.maNhanVien);
	}

	public Employee(String maNhanVien, String tenNhanVien) {
		this.maNhanVien = maNhanVien;
		this.tenNhanVien = tenNhanVien;
	}
}
