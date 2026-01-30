package com.pharmacy.dto;

import java.time.LocalDate;

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
public class PhanBoLoDTO {
	
	private int maSanPham;
	
	private String tenSanPham;
	
	private String soLo;
	
	private int soLuongCanLay;
	
	private double giaBinhQuan;
	
	private LocalDate hanSuDung;
	
	private int soLuongCon;
	
	private double giaGoc;
	
	public Object[] getObjects() {
		return new Object[] {soLo, FormatUtil.formatDate(hanSuDung), soLuongCon, FormatUtil.formatVND(giaGoc), soLuongCanLay};
	}
	
}
