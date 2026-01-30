package com.pharmacy.dao;

import com.pharmacy.dto.ProductBatchesReqDTO;

public interface SellDAO {
	
	ProductBatchesReqDTO getBatchesByBarcode(String barcode);

}
