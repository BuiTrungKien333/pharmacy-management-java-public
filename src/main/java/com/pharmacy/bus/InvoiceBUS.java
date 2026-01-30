package com.pharmacy.bus;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.dao.InvoiceDAO;
import com.pharmacy.entity.Invoice;
import com.pharmacy.entity.InvoiceDetail;
import com.pharmacy.entity.InvoiceDetailReturn;
import com.pharmacy.entity.InvoiceReturn;
import com.pharmacy.utils.FormatUtil;
import com.pharmacy.utils.Pagination;

public class InvoiceBUS {

	private static final Logger log = LoggerFactory.getLogger(InvoiceBUS.class);

	private final InvoiceDAO invoiceDAO = new InvoiceDAO();

	private int totalRecordFilter = 0;

	public int getTotalRecordFilterd() {
		return this.totalRecordFilter;
	}

	public LocalDate convertStringToLocalDate(int filter, String text) {
		if (filter != 4)
			return LocalDate.now();

		if (text == null || text.isBlank())
			return null;

		return FormatUtil.convertStringToDate(text);
	}

	public List<?> getFilterInvoice(int type, int filter, int filterDate, LocalDate startDate, LocalDate endDate,
			Pagination page) {

		List<?> result;

		if (type == 0) {
			this.totalRecordFilter = invoiceDAO.countFilteredInvoice(filterDate, startDate, endDate);

			result = invoiceDAO.getFilterInvoice(filterDate, startDate, endDate, page.getOffset(), page.getPageSize());

			log.info("[BUS] Filter Sales Invoices (DateFilter={}) -> Total: {}, PageSize: {}", filterDate,
					totalRecordFilter, result.size());
		} else {
			this.totalRecordFilter = invoiceDAO.countFilteredInvoiceReturn(filter, filterDate, startDate, endDate);

			result = invoiceDAO.getFilterInvoiceReturnByPage(filter, filterDate, startDate, endDate, page.getOffset(),
					page.getPageSize());

			log.info("[BUS] Filter Return Invoices (Status={}, DateFilter={}) -> Total: {}, PageSize: {}", filter,
					filterDate, totalRecordFilter, result.size());
		}

		return result;
	}

	public List<?> getFilterInvoiceAndSearchById(int type, int filter, int filterDate, LocalDate startDate,
			LocalDate endDate, Pagination page, String keyword) {
		List<?> result;

		if (type == 0) {
			this.totalRecordFilter = invoiceDAO.countInvoiceByFilteredAndSearchByID(filterDate, startDate, endDate,
					keyword);

			result = invoiceDAO.getInvoiceByFilteredAndSearchByID(filterDate, startDate, endDate, page.getOffset(),
					page.getPageSize(), keyword);

			log.info("[BUS] Search Sales Invoices (Keyword='{}') -> Found Total: {}", keyword, totalRecordFilter);
		} else {
			this.totalRecordFilter = invoiceDAO.countInvReturnByFilteredAndSearchByMaHDT(filter, filterDate, startDate,
					endDate, keyword);

			result = invoiceDAO.getInvReturnByFilteredAndSearchByMaHDT(filter, filterDate, startDate, endDate,
					page.getOffset(), page.getPageSize(), keyword);

			log.info("[BUS] Search Return Invoices (Keyword='{}') -> Found Total: {}", keyword, totalRecordFilter);
		}
		return result;
	}

	public InvoiceReturn getInvoiceReturnByMaHDT(String maHD) {
		InvoiceReturn inv = invoiceDAO.getInvoiceReturnByMaHDT(maHD);
		if (inv != null)
			log.info("[BUS] Get details for InvoiceReturn ID: {}", maHD);
		else
			log.warn("[BUS] InvoiceReturn not found for ID: {}", maHD);

		return inv;
	}

	public List<InvoiceDetailReturn> getAllInvDetailReturnByMaHDT(String maHD) {
		List<InvoiceDetailReturn> list = invoiceDAO.getAllInvDetailReturnByMaHDT(maHD);
		log.info("[BUS] Get items for InvoiceReturn ID: {} -> Found {} items", maHD, list.size());
		return list;
	}

	public List<?> getFilterInvoiceAndSearchBySoLo(int type, int filter, int filterDate, LocalDate startDate,
			LocalDate endDate, Pagination page, String keyword) {
		List<?> result;

		if (type == 0) {
			this.totalRecordFilter = invoiceDAO.countInvoiceByFilteredAndSearchBySoLo(filterDate, startDate, endDate,
					keyword);

			result = invoiceDAO.getInvoiceByFilteredAndSearchBySoLo(filterDate, startDate, endDate, page.getOffset(),
					page.getPageSize(), keyword);

			log.info("[BUS] Search Sales Invoices by Batch (SoLo='{}') -> Found Total: {}", keyword, totalRecordFilter);

		} else {
			this.totalRecordFilter = invoiceDAO.countInvReturnByFilteredAndSearchBySoLo(filter, filterDate, startDate,
					endDate, keyword);

			result = invoiceDAO.getInvReturnByFilteredAndSearchBySoLo(filter, filterDate, startDate, endDate,
					page.getOffset(), page.getPageSize(), keyword);

			log.info("[BUS] Search Return Invoices by Batch (SoLo='{}') -> Found Total: {}", keyword,
					totalRecordFilter);
		}

		return result;
	}

	public Invoice getInvoiceById(String maHD) {
		Invoice invoice = invoiceDAO.getInvoiceByMaHD(maHD);
		if (invoice != null)
			log.info("[BUS] Get details for Invoice ID: {}", maHD);
		else
			log.warn("[BUS] Invoice not found for ID: {}", maHD);

		return invoice;
	}

	public List<InvoiceDetail> getAllInvoiceByMaHD(String maHD) {
		List<InvoiceDetail> list = invoiceDAO.getAllInvoiceDetailByMaHD(maHD);
		log.info("[BUS] Get items for Invoice ID: {} -> Found {} items", maHD, list.size());
		return list;
	}

	public List<?> getAllInvoiceToExport(int type, int filter, int filterDate, LocalDate startDate, LocalDate endDate) {
		List<?> result;

		if (type == 0) {
			result = invoiceDAO.getAllInvoiceToExport(filterDate, startDate, endDate);

			log.info("[BUS] get list Sales Invoices to export Excel (DateFilter={}) -> Total: {} items", filterDate,
					result.size());
		} else {
			result = invoiceDAO.getAllInvoiceReturnToExport(filter, filterDate, startDate, endDate);

			log.info("[BUS] get list Return Invoices to export Excel (Status={}, DateFilter={}) -> Total: {} items",
					filter, filterDate, result.size());
		}

		return result;
	}

	public List<?> getAllInvoiceToExportAndSearchById(int type, int filter, int filterDate, LocalDate startDate,
			LocalDate endDate, String keyword) {
		List<?> result;

		if (type == 0) {
			result = invoiceDAO.getAllInvoiceToExportAndSearchById(filterDate, startDate, endDate, keyword);

			log.info("[BUS] Search Invoice by ID to export (DateFilter={}, Keyword='{}') -> Total: {} items",
					filterDate, keyword, result.size());
		} else {
			result = invoiceDAO.getAllInvoiceReturnToExportAndSearchById(filter, filterDate, startDate, endDate,
					keyword);

			log.info(
					"[BUS] Search Return Invoice by ID to export (Status={}, DateFilter={}, Keyword='{}') -> Total: {} items",
					filter, filterDate, keyword, result.size());
		}

		return result;
	}

	public List<?> getAllInvoiceToExportAndSearchBySoLo(int type, int filter, int filterDate, LocalDate startDate,
			LocalDate endDate, String keyword) {
		List<?> result;

		if (type == 0) {
			result = invoiceDAO.getAllInvoiceToExportAndSearchBySoLo(filterDate, startDate, endDate, keyword);

			log.info("[BUS] Search Invoice by Batch to export (DateFilter={}, Keyword='{}') -> Total: {} items",
					filterDate, keyword, result.size());
		} else {
			result = invoiceDAO.getAllInvoiceReturnToExportAndSearchBySoLo(filter, filterDate, startDate, endDate,
					keyword);

			log.info(
					"[BUS] Search Return Invoice by Batch to export (Status={}, DateFilter={}, Keyword='{}') -> Total: {} items",
					filter, filterDate, keyword, result.size());
		}

		return result;
	}

	public List<Invoice> getInvoiceByCustomerCode(int maKH) {
		return invoiceDAO.getAllHoaDonByMaKH(maKH);
	}

	public double calculateTotalRevenue(List<Invoice> list) {
		return list.stream().mapToDouble(Invoice::getTongTienCanThanhToan).sum();
	}

	public double calculateTotalRefund(List<InvoiceReturn> list) {
		return list.stream().mapToDouble(InvoiceReturn::getTienHoan).sum();
	}

}
