package com.pharmacy.bus;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.dao.BatchDAO;
import com.pharmacy.dao.impl.BatchDaoImpl;
import com.pharmacy.entity.Batch;
import com.pharmacy.entity.Product;
import com.pharmacy.exception.ResourceNotFoundException;
import com.pharmacy.utils.FormatUtil;
import com.pharmacy.utils.Pagination;

public class BatchBUS {

	private static final Logger log = LoggerFactory.getLogger(BatchBUS.class);

	private final BatchDAO shipmentDAO = new BatchDaoImpl();

	public List<Batch> getAllShipmentByPage(Pagination page, int option) {
		List<Batch> list = shipmentDAO.getAllShipmentByPage(page.getOffset(), page.getPageSize(), option);

		log.info("[BUS] getAllShipmentByPage(offset={}, limit={}, option={}) -> Loaded {} items", page.getOffset(),
				page.getPageSize(), option, list.size());

		return list;
	}

	public int getTotalRecord() {
		int total = shipmentDAO.countShipments();
		log.debug("[BUS] getTotalRecord() -> {}", total);
		return total;
	}

	public Batch getShipmentById(String id) {
		Batch shipment = shipmentDAO.getShipmentById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy số lô với ID: " + id));

		log.info("[BUS] Found shipment by ID: {}", id);

		return shipment;
	}

	public void addShipment(Batch shipment) {
		boolean ok = shipmentDAO.addShipment(shipment);
		if (!ok)
			throw new IllegalArgumentException("Thêm lô mới thất bại");

		log.info("[BUS] Added shipment successfully: {}", shipment.getSoLo());
	}

	public void updateShipment(Batch shipment) {
		System.out.println(shipment);
		boolean ok = shipmentDAO.updateShipment(shipment);

		if (!ok)
			throw new IllegalArgumentException("Cập nhật thông tin lô thất bại");

		log.info("[BUS] Updated shipment successfully: {}", shipment.getSoLo());
	}

	public Product getProdByBarcode(String barcode) {
		Product p = shipmentDAO.getProdByBarcode(barcode)
				.orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với barcode: " + barcode));

		if (p.isDeleted())
			throw new IllegalArgumentException("Sản phẩm này đã ngừng kinh doanh.");

		log.info("[BUS] Found product for batch setup: {} - {}", barcode, p.getTenSanPham());

		return p;
	}

	public int countFilteredShipmentAndSearchBySoLo(int type, int filter, LocalDate dateFrom, LocalDate dateTo,
			String keyword) {
		int count = shipmentDAO.countFilteredShipmentAndSearchBySoLo(type, filter, dateFrom, dateTo, keyword);
		log.debug("[BUS] countFilteredShipmentAndSearchBySoLo -> Result: {}", count);
		return count;
	}

	public int countFilteredShipment(int type, int filter, LocalDate dateFrom, LocalDate dateTo) {
		int count = shipmentDAO.countFilteredShipment(type, filter, dateFrom, dateTo);
		log.debug("[BUS] countFilteredShipment -> Result: {}", count);
		return count;
	}

	public List<Batch> getFilteredShipment(int type, int filter, Pagination page, LocalDate dateFrom, LocalDate dateTo,
			int option) {
		List<Batch> list = shipmentDAO.getFilteredShipment(type, filter, page.getOffset(), page.getPageSize(), dateFrom,
				dateTo, option);

		log.info("[BUS] Filter Shipments (type={}, filter={}, option={}) -> Loaded {} items", type, filter, option,
				list.size());
		return list;
	}

	public List<Batch> getFilteredShipmentAndSearchBySoLo(int type, int filter, Pagination page, LocalDate dateFrom,
			LocalDate dateTo, String keyword, int option) {
		List<Batch> list = shipmentDAO.getFilteredShipmentAndSearchBySoLo(type, filter, page.getOffset(),
				page.getPageSize(), dateFrom, dateTo, keyword, option);

		log.info("[BUS] Search Shipments by SoLo (keyword='{}') -> Found {} items", keyword, list.size());
		return list;
	}

	public int countFilteredShipmentAndSearchByBarcode(int type, int filter, LocalDate dateFrom, LocalDate dateTo,
			String barcode) {
		int count = shipmentDAO.countFilteredShipmentAndSearchByBarcode(type, filter, dateFrom, dateTo, barcode);
		log.debug("[BUS] countFilteredShipmentAndSearchByBarcode -> Result: {}", count);
		return count;
	}

	public List<Batch> getFilteredShipmentAndSearchByBarcode(int type, int filter, Pagination page, LocalDate dateFrom,
			LocalDate dateTo, String barcode, int option) {
		List<Batch> list = shipmentDAO.getFilteredShipmentAndSearchByBarcode(type, filter, page.getOffset(),
				page.getPageSize(), dateFrom, dateTo, barcode, option);

		log.info("[BUS] Search Shipments by Barcode (barcode='{}') -> Found {} items", barcode, list.size());
		return list;
	}

	public LocalDate convertStringToLocalDate(int filter, String text) {
		if (filter != 4)
			return LocalDate.now();

		if (text.isBlank())
			return null;

		return FormatUtil.convertStringToDate(text);
	}

	public double autoSetThanhTien(String soLuong, String giaNhap) {
		if (soLuong.isEmpty() || giaNhap.isEmpty())
			return 0;

		try {
			return Integer.parseInt(soLuong) * Double.parseDouble(giaNhap);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public boolean checkNgay(LocalDate ngaySX, LocalDate hanSD) {
		LocalDate today = LocalDate.now();

		if (!ngaySX.isBefore(hanSD))
			throw new IllegalArgumentException("Ngày sản xuất phải trước hạn sử dụng!");

		if (!ngaySX.isBefore(today))
			throw new IllegalArgumentException("Ngày sản xuất phải trước ngày hôm nay!");

		if (!hanSD.isAfter(today))
			throw new IllegalArgumentException("Hạn sử dụng phải sau ngày hôm nay!");

		return true;
	}

	public boolean checkGiaAndSoLuong(double giaNhap, double giaBan, int soLuongCon, int soLuongNhap) {
		if (giaNhap < 0 || giaBan < 0)
			throw new IllegalArgumentException("Giá không được < 0");

		if (giaNhap > giaBan)
			throw new IllegalArgumentException("Giá bán ra phải lớn hơn hoặc bằng giá nhập.");

		if (soLuongCon < 0 || soLuongNhap < 0)
			throw new IllegalArgumentException("Số lượng không được < 0");

		if (soLuongCon > soLuongNhap)
			throw new IllegalArgumentException("Số lượng còn không được lớn hơn số lượng nhập.");

		return true;
	}

	public void capNhatTrangThaiHetHanCuaLo() {
		boolean ok = shipmentDAO.capNhatTrangThaiHetHan();
		if (ok)
			log.info("[BUS] Cập nhật trạng thái hạn sử dụng của lô thuốc thành công");
		else
			log.debug("[BUS] Lỗi: Cập nhật trạng thái hết hạn của lô thuốc.");
	}

	public List<Batch> getAllBatchToExport(int type, int filter, LocalDate startDate, LocalDate endDate, int option) {
		List<Batch> list = shipmentDAO.getAllBatchToExport(type, filter, startDate, endDate, option);

		log.info("[BUS] getAllBatchToExport (type={}, filter={}, option={}) -> Loaded {} items", type, filter, option,
				list.size());
		return list;
	}

}
