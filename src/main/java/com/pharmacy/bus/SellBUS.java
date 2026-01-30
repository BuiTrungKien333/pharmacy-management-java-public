package com.pharmacy.bus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.dao.BatchDAO;
import com.pharmacy.dao.CustomerDAO;
import com.pharmacy.dao.InvoiceDAO;
import com.pharmacy.dao.InvoiceDetailDAO;
import com.pharmacy.dao.SellDAO;
import com.pharmacy.dao.VoucherDAO;
import com.pharmacy.dao.impl.BatchDaoImpl;
import com.pharmacy.dao.impl.SellDaoImpl;
import com.pharmacy.dto.BatchReqDTO;
import com.pharmacy.dto.PhanBoLoDTO;
import com.pharmacy.dto.ProductBatchesReqDTO;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.Invoice;
import com.pharmacy.exception.ResourceNotFoundException;

public class SellBUS {

	private static final Logger log = LoggerFactory.getLogger(SellBUS.class);

	private final SellDAO sellDAO = new SellDaoImpl();

	private final CustomerDAO customerDAO = new CustomerDAO();

	private final VoucherDAO voucherDAO = new VoucherDAO();

	private final InvoiceDAO invoiceDAO = new InvoiceDAO();

	private final InvoiceDetailDAO invoiceDetailDAO = new InvoiceDetailDAO();

	private final BatchDAO batchDAO = new BatchDaoImpl();

	public ProductBatchesReqDTO getBatchesByBarcode(String barcode) {
		ProductBatchesReqDTO res = sellDAO.getBatchesByBarcode(barcode);

		if (res == null) {
			log.warn("[BUS] Scan barcode failed: {} (Not found)", barcode);
			throw new ResourceNotFoundException(
					"Không tìm thấy sản phẩm hoặc thuốc không hợp lệ (hết hàng or sắp hết hạn) với barcode: "
							+ barcode);
		}

		if (res.isDeleted()) {
			log.warn("[BUS] Scan barcode failed: {} (Product deleted)", barcode);
			throw new IllegalArgumentException("Sản phẩm đã ngừng kinh doanh. Không được phép bán.");
		}

		log.info("[BUS] Scanned product: {} - {}", barcode, res.getTenSanPham());
		return res;
	}

	public void processAddToCart(ProductBatchesReqDTO prod, int soLuongCanBan) {

		if (soLuongCanBan > prod.getTongSoLuong()) {
			log.warn("[BUS] AddToCart failed: Request={}, Available={}", soLuongCanBan, prod.getTongSoLuong());
			throw new IllegalArgumentException("Số lượng hàng trong kho không đủ.");
		}

		log.debug("[BUS] Processing allocation for Product: {}, Qty: {}", prod.getTenSanPham(), soLuongCanBan);

		List<BatchReqDTO> dsCacLoCanDung = new ArrayList<>();
		double giaBinhQuan = 0;
		int tempSoLuong = soLuongCanBan;

		for (BatchReqDTO batch : prod.getDanhSachLoThuoc()) {
			if (tempSoLuong <= 0)
				break;

			int soLuongCanLayCuaLoNay = Math.min(tempSoLuong, batch.getSoLuongCon());
			giaBinhQuan += soLuongCanLayCuaLoNay * batch.getGiaBan();

			dsCacLoCanDung.add(batch);

			tempSoLuong -= soLuongCanLayCuaLoNay;
		}

		giaBinhQuan /= soLuongCanBan;

		List<PhanBoLoDTO> itemsThemVaoGioHang = new ArrayList<>();
		int soLuongConLai = soLuongCanBan;

		for (BatchReqDTO batch : dsCacLoCanDung) {
			int soLuongLay = Math.min(soLuongConLai, batch.getSoLuongCon());

			PhanBoLoDTO item = new PhanBoLoDTO();
			item.setMaSanPham(prod.getMaSanPham());
			item.setTenSanPham(prod.getTenSanPham());
			item.setSoLo(batch.getSoLo());
			item.setSoLuongCanLay(soLuongLay);
			item.setGiaBinhQuan(giaBinhQuan);
			item.setHanSuDung(batch.getHanSuDung());
			item.setSoLuongCon(batch.getSoLuongCon());
			item.setGiaGoc(batch.getGiaBan());

			itemsThemVaoGioHang.add(item);

			log.debug("   -> Allocate Batch: {} | Qty: {} | Price: {}", batch.getSoLo(), soLuongLay, batch.getGiaBan());

			soLuongConLai -= soLuongLay;
		}

		prod.setChiTietGiaoDich(itemsThemVaoGioHang);
		prod.setSoLuongBan(soLuongCanBan);
		prod.setGiaBanThucTe(giaBinhQuan);
		prod.setThanhTien();

		log.info("[BUS] Calculated Price: {} (Avg) for {} items", giaBinhQuan, soLuongCanBan);
	}

	public double calculateTienThua(String s, double soTienCanThanhToan) {
		if (s.isEmpty())
			return 0;

		try {
			double tienKhachDua = Double.parseDouble(s);
			return tienKhachDua < soTienCanThanhToan ? 0 : tienKhachDua - soTienCanThanhToan;
		} catch (NumberFormatException e) {
		}
		return 0;
	}

	public double calculateTongTienHang(List<ProductBatchesReqDTO> list) {
		return list.stream().mapToDouble(ProductBatchesReqDTO::getThanhTien).sum();
	}

	public double calculateTongCanThanhToan(double tongTienHang, double soTienDuocGiam) {
		double res = tongTienHang - soTienDuocGiam;
		if (res < 0)
			throw new IllegalArgumentException("Tính tiền bị sai kìa.");

		return res;
	}

	/*
	 * Sự kiện bán hàng, tạo hoá đơn (Transaction)
	 */
	public void processPayment(Invoice invoice, List<ProductBatchesReqDTO> list) {
		log.info("[BUS] Starting payment transaction for Invoice: {}", invoice.getMaHoaDon());

		try (Connection con = ConnectDB.getInstance().getConnection()) {
			con.setAutoCommit(false);

			try {
				if (invoice.getCustomer() != null) {
					Customer dbCustomer = customerDAO.getCustomerByPhone(invoice.getCustomer().getSoDienThoai());
					boolean cusSuccess;

					if (dbCustomer == null) {
						cusSuccess = customerDAO.addCustomer(con, invoice.getCustomer());
						log.debug("[BUS] Created new customer: {}", invoice.getCustomer().getTenKhachHang());
					} else {
						invoice.getCustomer().setMaKhachHang(dbCustomer.getMaKhachHang());
						dbCustomer.setDiemTichLuy(dbCustomer.getDiemTichLuy() + invoice.getCustomer().getDiemTichLuy());
						cusSuccess = customerDAO.updateCustomer(con, dbCustomer);
						log.debug("[BUS] Updated customer points: {}", dbCustomer.getSoDienThoai());
					}

					if (!cusSuccess)
						throw new IllegalArgumentException("Lỗi xử lý thông tin khách hàng.");
				}

				if (invoice.getVoucher() != null) {
					if (!voucherDAO.updateSoLuotSuDung(con, invoice.getVoucher())) {
						log.warn("[BUS] Voucher update failed: {}", invoice.getVoucher().getMaVoucher());
						throw new IllegalArgumentException("Lỗi cập nhật voucher (có thể hết lượt sử dụng).");
					}
				}

				if (!invoiceDAO.insertInvoice(con, invoice)) {
					log.error("[BUS] Failed to insert invoice header");
					throw new IllegalArgumentException("Lỗi tạo hóa đơn.");
				}

				for (ProductBatchesReqDTO prod : list) {
					for (PhanBoLoDTO item : prod.getChiTietGiaoDich()) {

						if (!invoiceDetailDAO.insert(con, invoice.getMaHoaDon(), item)) {
							log.error("[BUS] Failed to insert invoice detail for Prod ID: {}", prod.getMaSanPham());
							throw new IllegalArgumentException(
									"Lỗi thêm chi tiết hóa đơn cho SP: " + prod.getTenSanPham());
						}

						if (!batchDAO.deductBatchQuantity(con, item.getSoLo(), item.getSoLuongCanLay())) {
							log.error("[BUS] Failed to deduct inventory. Batch: {}, Qty: {}", item.getSoLo(),
									item.getSoLuongCanLay());
							throw new IllegalArgumentException(
									"Lỗi trừ kho lô: " + item.getSoLo() + ". (Không đủ hàng hoặc lỗi hệ thống)");
						}
					}
				}

				con.commit();
				log.info("[BUS] Transaction COMMITTED successfully. Invoice ID: {}", invoice.getMaHoaDon());

			} catch (Exception e) {
				con.rollback();
				log.warn("[BUS] Transaction ROLLED BACK due to error: {}", e.getMessage());
				throw e;
			}

		} catch (SQLException e) {
			log.error("[BUS] Database connection error", e);
			throw new IllegalArgumentException("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
		} catch (Exception e) {
			log.error("[BUS] Transaction failed", e);
			throw new IllegalArgumentException("Lỗi khi thực hiện bán hàng: " + e.getMessage());
		}
	}

}
