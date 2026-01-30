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
public class InvoiceDetailReturn {

	private int Id;

	private int soLuong;

	private double donGia;

	private double thanhTien;

	private Batch batch;

	private Product product;

	private InvoiceReturn invoiceReturn;

	private boolean status;

	private String huongXuLy;
	
	private String lyDo;

	public Object[] getObjects(int index) {
		return new Object[] { index, product.getTenSanPham(), batch.getSoLo(), soLuong, huongXuLy, lyDo };
	}

}
