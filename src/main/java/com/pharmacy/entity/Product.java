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
@EqualsAndHashCode(of = "maSanPham")
public class Product {

	private int maSanPham;

	private String barcode;

	private String tenSanPham;

	private String tenSanPhamKhongDau;

	private String hoatChatHamLuong;

	private String dangBaoChe;

	private String duongDung;

	private String chiDinh;

	private String chongChiDinh;

	private String lieuDung;

	private String soDangKi;

	private String nuocSanXuat;

	private String nhaSanXuat;

	private String tieuChuanChatLuong;

	private String quyCachDongGoi;

	private String donViTinh;

	private String moTa;

	private String avatarUrl;

	private int tongSoLuong;

	private ProductType loaiSanPham;

	private boolean deleted;

	public String getFullName() {
		return String.format("%s - %s", barcode, tenSanPham);
	}

	public String toStringTmp() {
		return "Product [maSanPham=" + maSanPham + ", barcode=" + barcode + ", tenSanPham=" + tenSanPham
				+ ", tenSanPhamKhongDau=" + tenSanPhamKhongDau + ", hoatChatHamLuong=" + hoatChatHamLuong
				+ ", dangBaoChe=" + dangBaoChe + ", duongDung=" + duongDung + ", chiDinh=" + chiDinh + ", chongChiDinh="
				+ chongChiDinh + ", lieuDung=" + lieuDung + ", soDangKi=" + soDangKi + ", nuocSanXuat=" + nuocSanXuat
				+ ", nhaSanXuat=" + nhaSanXuat + ", tieuChuanChatLuong=" + tieuChuanChatLuong + ", quyCachDongGoi="
				+ quyCachDongGoi + ", donViTinh=" + donViTinh + ", moTa=" + moTa + ", avatarUrl=" + avatarUrl
				+ ", tongSoLuong=" + tongSoLuong + ", loaiSanPham=" + loaiSanPham + ", deleted=" + deleted + "]";
	}

	public String toString() {
		return String.format("%06d", maSanPham) + "-" + tenSanPham;
	}

	public Product(int maSanPham, String tenSanPham, String donViTinh) {
		this.maSanPham = maSanPham;
		this.tenSanPham = tenSanPham;
		this.donViTinh = donViTinh;
	}

	public Product(int maSanPham, String tenSanPham) {
		this.maSanPham = maSanPham;
		this.tenSanPham = tenSanPham;
	}

	public Object[] getObjects() {
		return new Object[] { String.format("%06d", maSanPham), tenSanPham, barcode, hoatChatHamLuong, soDangKi,
				tongSoLuong, donViTinh, loaiSanPham.getTenLoai(),
				deleted == true ? "Ngừng kinh doanh" : "Đang hoạt động" };
	}
}
