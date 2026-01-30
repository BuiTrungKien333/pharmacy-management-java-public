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
public class CustomerRank {
	
	private int id;
	
	private String tenHangTV;
	
	private int diemToiThieu;
	
	private String moTa;
	
	public CustomerRank(int id, String tenHTV) {
		this.id = id;
		this.tenHangTV = tenHTV;
	}
	
}
