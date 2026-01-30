package com.pharmacy.bus;

import java.util.List;

import com.pharmacy.dao.NhaCungCapDAO;
import com.pharmacy.entity.NhaCungCap;

public class NhaCungCapBUS {

	private final NhaCungCapDAO nhaCungCapDAO = new NhaCungCapDAO();

	public List<NhaCungCap> getListNhaCungCap() {
		return nhaCungCapDAO.getAllNhaCungCap();
	}

	public List<NhaCungCap> getAllInfoSupplier() {
		return nhaCungCapDAO.getAllInfoNhaCungCap();
	}

	public String toRemoveSpace(String text) {
		return text.replaceAll("\\s+", "");
	}

	public NhaCungCap getNhaCungCapByPhone(String soDienThoai) {
		return nhaCungCapDAO.findSupplierByPhone(soDienThoai);
	}

	public List<NhaCungCap> findSupplierByFactoryCode(String factory_code) {
		return nhaCungCapDAO.findSupplierByFactoryCode(factory_code);
	}

	public boolean updateSupplier(NhaCungCap ncc) {
		return nhaCungCapDAO.updateInforSuplier(ncc);
	}

	public boolean insertSupplier(NhaCungCap ncc) {
		return nhaCungCapDAO.insertSuplier(ncc);
	}

}
