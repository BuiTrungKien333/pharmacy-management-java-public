package com.pharmacy.entity;

public class Store {
	private String maCuaHang;
	private String tenCuaHang;
	private String soDangKy;
	private String giayChungNhan;
	private String diaChi; 

	public Store(String tenCuaHang, String diaChi,  String soDangKy, String giayChungNhan) {
		this.tenCuaHang = tenCuaHang;
		this.soDangKy = soDangKy;
		this.giayChungNhan = giayChungNhan;
		this.diaChi = diaChi; 
	}

	public Store(String maCuaHang) {
		this.maCuaHang = maCuaHang;
	}
	
	

	
	public Store(String maCuaHang, String tenCuaHang,String diaChi,  String soDangKy, String giayChungNhan) {
		this.maCuaHang = maCuaHang;
		this.tenCuaHang = tenCuaHang;
		this.soDangKy = soDangKy;
		this.giayChungNhan = giayChungNhan;
		this.diaChi = diaChi; 
		
	}

	public String getMaCuaHang() {
		return maCuaHang;
	}

	public String getTenCuaHang() {
		return tenCuaHang;
	}

	public void setTenCuaHang(String tenCuaHang) {
		this.tenCuaHang = tenCuaHang;
	}

	public String getSoDangKy() {
		return soDangKy;
	}

	public void setSoDangKy(String soDangKy) {
		this.soDangKy = soDangKy;
	}

	public String getGiayChungNhan() {
		return giayChungNhan;
	}

	public void setGiayChungNhan(String giayChungNhan) {
		this.giayChungNhan = giayChungNhan;
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = diaChi;
	}

	@Override
	public String toString() {
		return "Store [maCuaHang=" + maCuaHang + ", tenCuaHang=" + tenCuaHang + ", soDangKy=" + soDangKy
				+ ", giayChungNhan=" + giayChungNhan + ", diaChi=" + diaChi + "]";
	}

	
}
