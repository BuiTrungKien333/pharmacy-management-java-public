package com.pharmacy.dto;

import java.time.LocalDate;

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
public class BatchReqDTO {
	
	private String soLo;
	
	private LocalDate hanSuDung;
	
	private int soLuongCon;
	
	private double giaBan;

}
