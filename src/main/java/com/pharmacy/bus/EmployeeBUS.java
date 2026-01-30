package com.pharmacy.bus;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.pharmacy.dao.EmployeeDAO;
import com.pharmacy.dao.impl.EmployeeDaoImpl;
import com.pharmacy.entity.Employee;

public class EmployeeBUS {

	private EmployeeDAO employeeDAO;

	private List<Employee> list = new ArrayList<>();

	private static final Pattern DIACRITICS_AND_TONE_MARKS = Pattern.compile("\\p{M}+");

	public EmployeeBUS() {
		employeeDAO = new EmployeeDaoImpl();
		loadData();
	}

	private void loadData() {
		list = employeeDAO.getAllEmployee();
	}

	public Employee getEmployeeByEmpId(String empId) {
		return employeeDAO.getUser(empId);
	}

	public Employee getProfileCurrentUser() {
		return employeeDAO.getEmployeeById(Auth.getCurrentUser().getMaNhanVien());
	}

	public List<Employee> getAllNhanVien() {
		return employeeDAO.getAllEmployee();
	}

	public List<Employee> getEmployeeSearch(String searchText) {
		String text = toUnsignedString(searchText.trim());
		return list.stream()
				.filter(e -> ((e.getEmail().contains(text)) || (toUnsignedString(e.getTenNhanVien()).contains(text)))
						|| (e.getSoDienThoai().contains(text)))
				.toList();
	}

	public void addEmployee(Employee empl) {
		if (isEmail(empl))
			throw new IllegalArgumentException("Email đã tồn tại trong hệ thống!");

		if (isPhoneNum(empl))
			throw new IllegalArgumentException("Số điện thoại đã tồn tại trong hệ thống!");

		if (!employeeDAO.addEmployee(empl))
			throw new IllegalArgumentException("Thêm nhân viên không thành công!");

		reloadEmployeeList();
	}

	public String getRoleNameCurrentUser() {
		return employeeDAO.getRoleNameCurrentUser(Auth.getCurrentUser().getMaNhanVien());
	}

	private void reloadEmployeeList() {
		list = employeeDAO.getAllEmployee();
	}

	private boolean isPhoneNum(Employee empl) {
		return list.stream().anyMatch(e -> e.getSoDienThoai().equals(empl.getSoDienThoai()));
	}

	private boolean isEmail(Employee empl) {
		return list.stream().anyMatch(e -> e.getEmail().equals(empl.getEmail()));
	}

	public Employee getEmployeeById(String id) {
		return list.stream().filter(e -> id.equals(e.getMaNhanVien())).findFirst().orElse(null);
	}

	public void updateEmployee(Employee empl, String ma_nv) {
		if (employeeDAO.updateInfEmpl(empl, ma_nv)) {
			empl.setMaNhanVien(ma_nv);
			for (int i = 0; i < list.size(); i++) {
				Employee e = list.get(i);

				if (e.getMaNhanVien().equals(ma_nv)) {
					list.set(i, empl);
					break;
				}
			}
		}

	}

	public static String toUnsignedString(String name) {
		if (name == null)
			return "";
		String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
		String noDiacritics = DIACRITICS_AND_TONE_MARKS.matcher(normalized).replaceAll("");
		noDiacritics = noDiacritics.replaceAll("đ", "d").replaceAll("Đ", "D");
		return noDiacritics.toLowerCase();
	}

}
