package com.pharmacy.entity;

import java.time.LocalDate;

import com.pharmacy.utils.FormatUtil;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

	private String maVoucher;

	private int giaTri;

	private double donToiThieu;

	private double giamToiDa;

	private LocalDate ngayBatDau;

	private LocalDate ngayKetThuc;

	private int soLuotDaSuDung;

	private int soLuotSuDungToiDa;

	private CustomerRank customerRank;

	private double tongTienDuocGiam;
	
	public void setTongTienDuocGiam(double tongTien) {
		this.tongTienDuocGiam = Math.min(tongTien * giaTri / 100, giamToiDa);
	}

	public Voucher(String maVoucher) {
		this.maVoucher = maVoucher;
	}

	public String toString() { // VCH021125_15%_silver_200.000_20/11/25
		return maVoucher + "_" + giaTri + "%_" + customerRank.getTenHangTV() + "_" + FormatUtil.formatVND(giamToiDa)
				+ "_" + FormatUtil.formatDateDDMMYY(ngayKetThuc);
	}

}
