package com.pharmacy.dao;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.pharmacy.entity.Batch;
import com.pharmacy.entity.InvoiceDetailReturn;
import com.pharmacy.entity.Product;

public interface BatchDAO {

	boolean addShipment(Batch shipment);

	boolean updateShipment(Batch shipment);

	Optional<Batch> getShipmentById(String id);

	Optional<Product> getProdByBarcode(String barcode);

	int countShipments();

	List<Batch> getAllShipmentByPage(int offset, int limit, int option);

	int countFilteredShipment(int type, int filter, LocalDate dateFrom, LocalDate dateTo);

	List<Batch> getFilteredShipment(int type, int filter, int offset, int pageSize, LocalDate dateFrom,
			LocalDate dateTo, int option);

	int countFilteredShipmentAndSearchBySoLo(int type, int filter, LocalDate dateFrom, LocalDate dateTo,
			String keyword);

	List<Batch> getFilteredShipmentAndSearchBySoLo(int type, int filter, int offset, int pageSize, LocalDate dateFrom,
			LocalDate dateTo, String keyword, int option);

	int countFilteredShipmentAndSearchByBarcode(int type, int filter, LocalDate dateFrom, LocalDate dateTo,
			String barcode);

	List<Batch> getFilteredShipmentAndSearchByBarcode(int type, int filter, int offset, int pageSize,
			LocalDate dateFrom, LocalDate dateTo, String barcode, int option);

	boolean deductBatchQuantity(Connection con, String soLo, int soLuongCanLay);

	boolean capNhatTrangThaiHetHan();

	boolean updateQuantity(Connection con, InvoiceDetailReturn inv, int newQty);

	List<Batch> getAllBatchToExport(int type, int filter, LocalDate startDate, LocalDate endDate, int option);

}
