package com.pharmacy.dao;

import java.util.List;
import java.util.Optional;

import com.pharmacy.entity.Product;

public interface ProductDAO {
	
	boolean addProduct(Product product);
		
	boolean updateInfoProduct(Product newProd);
	
	Optional<Product> getProdByBarcode(String barcode);
				
	// truy vấn dưới db

	List<Product> getAllProdByPage(int offset, int limit);
	
	int countProducts();
	
	List<Product> getFilteredProducts(int type, int filter, int offset, int limit);
	
	int countFilteredProducts(int type, int filter);
	
	List<Product> getFilteredProdsWithSearch(int type, int filter, String keyword);

	boolean checkExistsBarcode(String barcode);

	boolean checkExistsSoDangKi(String text);
	
	List<Product> getAllProdToExport(int type, int filter);
	
	// support
	List<String> getDangBaoChe();
	
	List<String> getDuongDung();
	
	List<String> getTieuChuanChatLuong();

	List<String> getDonViTinh();
	
}
