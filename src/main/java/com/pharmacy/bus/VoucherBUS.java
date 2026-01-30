package com.pharmacy.bus;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.dao.VoucherDAO;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.Voucher;

public class VoucherBUS {

	private static final Logger log = LoggerFactory.getLogger(VoucherBUS.class);

	private VoucherDAO voucherDAO = new VoucherDAO();

	public List<Voucher> getAllVoucherByConditionCus(Customer cus, double tongTien) {
		List<Voucher> list = voucherDAO.getAllVoucherByConditionCus(cus, tongTien);
		log.info(
				"[BUS] getAllVoucherByConditionCustomer (customerRank={}, phone={}, tongTienHang={}) -> Loaded {} items",
				cus.getCustomerRank().getId(), cus.getSoDienThoai(), tongTien, list.size());
		return list;
	}

	public List<Voucher> getAllVoucher() {
		return voucherDAO.getAllVoucher();
	}

	public List<Voucher> filter(String keyword, Integer status, LocalDate from, LocalDate to) {
		return voucherDAO.filter(keyword, status, from, to);
	}

	public boolean updateVoucher(Voucher v) {
		return voucherDAO.update(v);
	}

	public boolean insertVoucher(Voucher v) {
		return voucherDAO.insert(v);
	}

}
