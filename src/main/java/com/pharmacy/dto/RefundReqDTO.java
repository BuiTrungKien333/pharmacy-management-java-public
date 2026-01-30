package com.pharmacy.dto;

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
public class RefundReqDTO {

	private int maSP;

	private String soLo;

	private int soLuongTra;

	private double donGia;

	private double thanhTien;
	
	private String donViTinh;
	
	private String tenSP;

	public void setThanhTien() {
		this.thanhTien = this.soLuongTra * this.donGia;
	}

}
