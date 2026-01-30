package com.pharmacy.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductType {
	
	private int id;
	
	private String tenLoai;

	public ProductType(int id) {
		this.id = id;
	}

}
