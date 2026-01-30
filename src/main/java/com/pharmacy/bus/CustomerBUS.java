package com.pharmacy.bus;

import java.util.List;

import com.pharmacy.dao.CustomerDAO;
import com.pharmacy.entity.Customer;

public class CustomerBUS {

	private CustomerDAO customerDAO = new CustomerDAO();

	public Customer searchCustomerByPhone(String phone) {
		if (phone == null || phone.isEmpty() || !phone.matches("^0[0-9]{9}$"))
			return null;

		return customerDAO.findCustomerByPhone(phone);
	}

	public List<Customer> getAllCustomer() {
		return customerDAO.getAllCustomer();
	}

	public List<Customer> findCustomerByPhoneNum(String phoneNum) {
		return customerDAO.findCustomerByPhoneNum(phoneNum);
	}

}
