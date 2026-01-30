
package com.pharmacy.dao;

import java.util.List;

import com.pharmacy.entity.Employee;

public interface EmployeeDAO {

	boolean checkExistsByEmpIdAndEmail(String empId, String email);

	Employee getUser(String empId); 

	List<Employee> getAllEmployee();

	List<Employee> getEmployeeSearch(String searchText);

	boolean addEmployee(Employee empl);

	boolean updateInfEmpl(Employee empl, String ma_nv);

	boolean isPhoneExist(String phoneNum);

	boolean isEmailExist(String email);

	Employee getEmployeeById(String id);

	String getRoleNameCurrentUser(String maNhanVien);
}
