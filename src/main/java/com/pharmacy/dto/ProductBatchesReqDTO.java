package com.pharmacy.dto;

import java.util.List;

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
public class ProductBatchesReqDTO {
	
	private int maSanPham;
	
	private String tenSanPham;
	
	private String barcode;
	
	private int tongSoLuong;
	
	private String avatarUrl;
	
	private String donViTinh;
	
	private boolean deleted;
	
	private int soLuongBan;
	
	private double giaBanThucTe;
	
	private double thanhTien;
	
	public void setThanhTien() {
		this.thanhTien = this.giaBanThucTe * this.soLuongBan;
	}
	
	private List<BatchReqDTO> danhSachLoThuoc;
	
	private List<PhanBoLoDTO> chiTietGiaoDich;

}
