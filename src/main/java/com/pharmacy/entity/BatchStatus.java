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
public class BatchStatus {
	
	private int id;
	
	private String tenTrangThai;

	public BatchStatus(int id) {
		this.id = id;
	}
}
