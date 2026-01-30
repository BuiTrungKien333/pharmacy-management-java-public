package com.pharmacy.bus;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.dao.BatchDAO;
import com.pharmacy.dao.RefundDAO;
import com.pharmacy.dao.RefundDetailDAO;
import com.pharmacy.dao.impl.BatchDaoImpl;
import com.pharmacy.dto.RefundReqDTO;
import com.pharmacy.entity.Invoice;
import com.pharmacy.entity.InvoiceDetail;
import com.pharmacy.entity.InvoiceDetailReturn;
import com.pharmacy.entity.InvoiceReturn;

public class RefundBUS {

	private static final Logger log = LoggerFactory.getLogger(RefundBUS.class);

	private final RefundDAO refundDAO = new RefundDAO();

	private final RefundDetailDAO refundDetailDAO = new RefundDetailDAO();

	private final BatchDAO batchDAO = new BatchDaoImpl();

	public Invoice getInvoiceById(String qrCode) {
		Invoice invoice = refundDAO.getInvoiceById(qrCode);

		if (invoice == null) {
			log.warn("[BUS] Scan QR failed: {} (Not found)", qrCode);
			throw new IllegalArgumentException("Không tìm thấy hóa đơn với mã qr-code: " + qrCode);
		}

		if (invoice.getCustomer() == null) {
			log.warn("[BUS] Return rejected for Invoice {}: Guest customer", invoice.getMaHoaDon());
			throw new IllegalArgumentException("Không áp dụng chính sách trả hàng với khách hàng vảng lai.");
		}

		LocalDate hanDoiTra = invoice.getNgayLap().toLocalDate().plusDays(7);
		if (LocalDate.now().isAfter(hanDoiTra)) {
			log.warn("[BUS] Return rejected for Invoice {}: Expired (Date: {}, Limit: {})", invoice.getMaHoaDon(),
					LocalDate.now(), hanDoiTra);
			throw new IllegalArgumentException("Hóa đơn đã hết hạn đổi trả. (Tối đa 7 ngày kể từ ngày mua)");
		}

		if (invoice.isDaTra()) {
			log.warn("[BUS] Return rejected for Invoice {}: Already returned", invoice.getMaHoaDon());
			throw new IllegalArgumentException("Hóa đơn này đã được trả trước đó.");
		}

		log.info("[BUS] Validated Invoice for return: {}", invoice.getMaHoaDon());
		return invoice;
	}

	public List<InvoiceDetail> getAllInvoiceDetailByQrCode(String qrCode) {
		List<InvoiceDetail> list = refundDAO.getAllInvoiceDetailByQrCode(qrCode);
		log.debug("[BUS] Fetched {} details for QR: {}", list.size(), qrCode);
		return list;
	}

	/*
	 * Tạo phiếu yêu cầu trả hàng (Transaction)
	 */
	public void processInvoiceReturn(InvoiceReturn invoiceReturn, List<RefundReqDTO> listChosen) {
		log.info("[BUS] Starting return request for Invoice: {}", invoiceReturn.getInvoice().getMaHoaDon());

		try (Connection con = ConnectDB.getInstance().getConnection()) {
			con.setAutoCommit(false);

			try {
				if (!refundDAO.insertInvoiceReturn(con, invoiceReturn)) {
					log.error("[BUS] Failed to insert InvoiceReturn header");
					throw new IllegalArgumentException("Lỗi tạo hóa đơn trả.");
				}

				if (!refundDAO.updateStatusInvoice(con, invoiceReturn.getInvoice().getMaHoaDon())) {
					log.error("[BUS] Failed to update original invoice status");
					throw new IllegalArgumentException("Lỗi cập nhật trạng thái hóa đơn gốc.");
				}

				for (RefundReqDTO refund : listChosen) {
					if (!refundDetailDAO.insert(con, invoiceReturn.getMaHDTra(), refund)) {
						log.error("[BUS] Failed to insert return detail for Batch: {}", refund.getSoLo());
						throw new IllegalArgumentException(
								"Lỗi thêm chi tiết hóa đơn trả cho sản phẩm: " + refund.getTenSP());
					}
				}

				con.commit();
				log.info("[BUS] Return Request created successfully. ID: {}", invoiceReturn.getMaHDTra());

			} catch (Exception e) {
				con.rollback();
				log.warn("[BUS] Transaction ROLLED BACK due to error: {}", e.getMessage());
				throw e;
			}

		} catch (SQLException e) {
			log.error("[BUS] Database connection error", e);
			throw new IllegalArgumentException("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
		} catch (Exception e) {
			log.error("[BUS] processInvoiceReturn failed", e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/*
	 * Phê duyệt trả hàng (Cập nhật kho và trạng thái) - Transaction
	 */
	public void approveInvoiceReturn(List<InvoiceDetailReturn> listToUpdate) {
		if (listToUpdate.isEmpty())
			return;

		String maHDTra = listToUpdate.get(0).getInvoiceReturn().getMaHDTra();
		log.info("[BUS] Starting approval for Return ID: {}", maHDTra);

		try (Connection con = ConnectDB.getInstance().getConnection()) {
			con.setAutoCommit(false);

			try {
				if (!refundDAO.updateStatusForInvoiceReturn(con, maHDTra)) {
					log.error("[BUS] Failed to update InvoiceReturn status to Completed");
					throw new IllegalArgumentException("Lỗi khi cập nhật trạng thái hóa đơn trả");
				}

				for (InvoiceDetailReturn inv : listToUpdate) {
					if (!refundDetailDAO.update(con, inv)) {
						log.error("[BUS] Failed to update detail status for Product: {}",
								inv.getProduct().getMaSanPham());
						throw new IllegalArgumentException("Lỗi khi cập nhật trạng thái chi tiết hoá đơn trả");
					}

					int newQty = "Bán tiếp".equals(inv.getHuongXuLy()) ? inv.getSoLuong() : 0;

					if (newQty > 0) {
						log.debug("[BUS] Restocking Batch: {} | Qty: +{}", inv.getBatch().getSoLo(), newQty);
						if (!batchDAO.updateQuantity(con, inv, newQty)) {
							log.error("[BUS] Failed to restock inventory for Batch: {}", inv.getBatch().getSoLo());
							throw new IllegalArgumentException("Lỗi khi cập nhật số lượng cho lô thuốc");
						}
					} else {
						log.debug("[BUS] Discarding items (No Restock) for Batch: {} | Action: {}",
								inv.getBatch().getSoLo(), inv.getHuongXuLy());
					}
				}

				con.commit();
				log.info("[BUS] Approval COMMITTED successfully for Return ID: {}", maHDTra);

			} catch (Exception e) {
				con.rollback();
				log.warn("[BUS] Approval Transaction ROLLED BACK due to: {}", e.getMessage());
				throw e;
			}

		} catch (SQLException e) {
			log.error("[BUS] Database error during approval", e);
			throw new IllegalArgumentException("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
		} catch (Exception e) {
			log.error("[BUS] approveInvoiceReturn failed", e);
			throw new IllegalArgumentException(e.getMessage());
		}
	}

}
