package com.pharmacy.bus;

import com.pharmacy.dao.impl.StoreDaoImpl;
import com.pharmacy.entity.Store;

public class StoreBUS {
	
	private final StoreDaoImpl dao = new StoreDaoImpl();

	public Store getInfoStore(String store_id) {
		return dao.getInFoStore(store_id); 
	}

}
