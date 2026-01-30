package com.pharmacy.bus;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.dao.ProductDAO;
import com.pharmacy.dao.impl.ProductDaoImpl;
import com.pharmacy.entity.Product;
import com.pharmacy.exception.ResourceNotFoundException;
import com.pharmacy.utils.Pagination;

public class ProductBUS {

	private static final Logger log = LoggerFactory.getLogger(ProductBUS.class);

	private final ProductDAO productDAO = new ProductDaoImpl();

	private int totalRecordFilter = 0;

	public int getTotalRecord() {
		int totalRecord = productDAO.countProducts();
		log.debug("[BUS] getTotalRecord() -> {}", totalRecord);
		return totalRecord;
	}

	public int getTotalRecordFiltered() {
		log.debug("[BUS] getTotalRecordFiltered() -> {}", totalRecordFilter);
		return totalRecordFilter;
	}

	public void addProd(Product product) {
		boolean success = productDAO.addProduct(product);
		if (!success)
			throw new IllegalArgumentException("Thêm sản phẩm thất bại");

		log.info("[BUS] Added product successfully: {}", product.getTenSanPham());
	}

	public void updateProd(Product product) {
		boolean success = productDAO.updateInfoProduct(product);
		if (!success)
			throw new IllegalArgumentException("Cập nhật thông tin sản phẩm thất bại");

		log.info("[BUS] Updated product info successfully: {}", product.getTenSanPham());
	}

	public boolean checkExistsSoDangKy(String text) {
		return productDAO.checkExistsSoDangKi(text);
	}

	public boolean checkExistsBarcode(String barcode) {
		return productDAO.checkExistsBarcode(barcode);
	}

	public Product getProdByBarcode(String barcode) {
		Product p = productDAO.getProdByBarcode(barcode)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với barcode: " + barcode));

		log.info("[BUS] Found product by barcode: {}", barcode);

		return p;
	}

	public List<Product> getAllProdByPage(Pagination page) {
		List<Product> result = productDAO.getAllProdByPage(page.getOffset(), page.getPageSize());

		log.info("[BUS] getAllProdByPage(offset={}, limit={}) -> Loaded {} items", page.getOffset(), page.getPageSize(),
				result.size());

		return result;
	}

	public List<Product> getFilteredProducts(Pagination page, int type, int filter) {
		List<Product> result = productDAO.getFilteredProducts(type, filter, page.getOffset(), page.getPageSize());

		this.totalRecordFilter = productDAO.countFilteredProducts(type, filter);

		log.info("[BUS] getFilteredProducts(type={}, filter={}) -> Loaded {} items (Total filtered: {})", type, filter,
				result.size(), totalRecordFilter);

		return result;
	}

	public List<Product> getFilteredProductsAndSearchByDB(int type, int filter, String keyword) {
		List<Product> result = productDAO.getFilteredProdsWithSearch(type, filter, keyword);

		log.info("[BUS] Search products (type={}, filter={}, keyword='{}') -> Found {} items", type, filter, keyword,
				result.size());

		return result;
	}

	public List<Product> getAllProdToExport(int currentType, int currentFilter) {
		List<Product> result = productDAO.getAllProdToExport(currentType, currentFilter);
		
		log.info("[BUS] getAllProdToExport(type={}, filter={}) -> Loaded {} items", currentType, currentFilter,
				result.size());

		return result;
	}

	public String[] getDangBaoChe() {
		return productDAO.getDangBaoChe().toArray(new String[0]);
	}

	public String[] getDuongDung() {
		return productDAO.getDuongDung().toArray(new String[0]);
	}

	public String[] getTieuChuanChatLuong() {
		return productDAO.getTieuChuanChatLuong().toArray(new String[0]);
	}

	public String[] getDonViTinh() {
		return productDAO.getDonViTinh().toArray(new String[0]);
	}

}