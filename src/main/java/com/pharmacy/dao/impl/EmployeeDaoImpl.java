package com.pharmacy.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.dao.EmployeeDAO;
import com.pharmacy.entity.Employee;
import com.pharmacy.entity.Store;

public class EmployeeDaoImpl implements EmployeeDAO {

	private static final Logger log = LoggerFactory.getLogger(EmployeeDaoImpl.class);

	@Override
	public boolean checkExistsByEmpIdAndEmail(String empId, String email) {
		log.debug("Checking existence for Employee - ID: {}, Email: {}", empId, email);
		String sql = "select 1 from tbl_nhan_vien e where e.deleted=0 and e.ma_nv=? and e.email=?";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, empId);
			stmt.setString(2, email);

			try (ResultSet rs = stmt.executeQuery()) {
				boolean exists = rs.next();
				if (!exists) {
					log.warn("Employee not found with ID: {} and Email: {}", empId, email);
				}
				return exists;
			}
		} catch (SQLException e) {
			log.error("Database error while checking employee existence (ID: {})", empId, e);
		}
		return false;
	}

	@Override
	public Employee getUser(String empId) {
		String sql = "select ma_nv, ho_ten from tbl_nhan_vien where ma_nv=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, empId);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					log.debug("User loaded successfully: {}", empId);
					return new Employee(empId, rs.getString("ho_ten"));
				} else {
					log.warn("User not found in database: {}", empId);
				}
			}
		} catch (SQLException e) {
			log.error("Database error while fetching user: {}", empId, e);
		}

		return null;
	}

	@Override
	public String getRoleNameCurrentUser(String id) {
		log.debug("Fetching role name for User ID: {}", id);
		String sql = "select r.role_name from tbl_user_role ur inner join tbl_role r on ur.role_id = r.id where ur.user_id=?";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					String roleName = rs.getString("role_name");
					log.debug("Role found for User {}: {}", id, roleName);
					return roleName;
				} else {
					log.warn("No role assigned for User ID: {}", id);
				}
			}
		} catch (SQLException e) {
			log.error("Database error while fetching role for User ID: {}", id, e);
		}

		return "";
	}

	@Override
	public List<Employee> getAllEmployee() {
		List<Employee> dsnv = new ArrayList<>();
		String sql = "select * from tbl_nhan_vien";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql);
			 ResultSet rs = stmt.executeQuery()) {
			
			while (rs.next()) {
				// Mapping data
				String maNhanVien = rs.getString("ma_nv");
				String tenNhanVien = rs.getString("ho_ten");
				String soDienThoai = rs.getString("so_dien_thoai");
				String email = rs.getString("email");
				Date ngaySinh = rs.getDate("ngay_sinh");
				boolean gioiTinh = rs.getBoolean("gioi_tinh");
				String diaChi = rs.getString("dia_chi");
				Store cuaHang = new Store(rs.getString("cua_hang_id"));
				String avt_url = rs.getString("avatar_url");
				Date ngayVaoLam = rs.getDate("ngay_vao_lam");
				boolean trangThai = rs.getBoolean("deleted");
				
				Employee empl = new Employee(maNhanVien, tenNhanVien, soDienThoai, email, ngaySinh, gioiTinh, diaChi,
						cuaHang, trangThai, avt_url, ngayVaoLam);
				dsnv.add(empl);
			}
		} catch (SQLException e) {
			log.error("Database error while fetching all employees", e);
		}
		return dsnv;
	}

	@Override
	public List<Employee> getEmployeeSearch(String searchText) {
		List<Employee> dsnv = new ArrayList<>();

		String sql = "select * from tbl_nhan_vien where so_dien_thoai like ? or ho_ten like ? or email like ?";
		String phone = "%" + searchText + "%";
		String text = "%" + searchText.toLowerCase() + "%";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, phone);
			stmt.setString(2, text);
			stmt.setString(3, text);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String maNhanVien = rs.getString("ma_nv");
					String tenNhanVien = rs.getString("ho_ten");
					String soDienThoai = rs.getString("so_dien_thoai");
					String email = rs.getString("email");
					Date ngaySinh = rs.getDate("ngay_sinh");
					boolean gioiTinh = rs.getBoolean("gioi_tinh");
					String diaChi = rs.getString("dia_chi");
					Store cuaHang = new Store(rs.getString("cua_hang_id"));
					String avt_url = rs.getString("avatar_url");
					Date ngayVaoLam = rs.getDate("ngay_vao_lam");
					boolean trangThai = rs.getBoolean("deleted");
					
					Employee empl = new Employee(maNhanVien, tenNhanVien, soDienThoai, email, ngaySinh, gioiTinh, diaChi,
							cuaHang, trangThai, avt_url, ngayVaoLam);
					dsnv.add(empl);
				}
			}
		} catch (SQLException e) {
			log.error("Database error while searching employees with text: {}", searchText, e);
		}

		return dsnv;
	}

	@Override
	public boolean addEmployee(Employee empl) {
		String sql = "INSERT INTO tbl_nhan_vien (ma_nv, ho_ten, so_dien_thoai, email, ngay_sinh, gioi_tinh, dia_chi, cua_hang_id, avatar_url, ngay_vao_lam, deleted) VALUES ( CONCAT('ALA01', RIGHT('000' + CAST(NEXT VALUE FOR Emp_Seq AS VARCHAR(3)), 3)), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, empl.getTenNhanVien());
			stmt.setString(2, empl.getSoDienThoai());
			stmt.setString(3, empl.getEmail());
			stmt.setDate(4, new Date(empl.getNgaySinh().getTime()));
			stmt.setBoolean(5, empl.isGioiTinh());
			stmt.setString(6, empl.getDiaChi());
			stmt.setString(7, empl.getCuaHang().getMaCuaHang());
			stmt.setString(8, empl.getAvt_url());
			stmt.setDate(9, new Date(empl.getNgayVaoLam().getTime()));
			stmt.setBoolean(10, empl.isDeleted());
			
			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			log.error("Database error while adding employee: {}", empl.getTenNhanVien(), e);
		}

		return false;
	}

	@Override
	public boolean updateInfEmpl(Employee empl, String ma_nv) {
		String sql = "UPDATE tbl_nhan_vien SET ho_ten = ?, so_dien_thoai = ?, email = ?, ngay_sinh = ?,"
				+ " gioi_tinh = ?, dia_chi = ?, cua_hang_id = ?, avatar_url = ?, ngay_vao_lam = ?, deleted = ? where ma_nv = ?";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, empl.getTenNhanVien());
			stmt.setString(2, empl.getSoDienThoai());
			stmt.setString(3, empl.getEmail());
			stmt.setDate(4, new Date(empl.getNgaySinh().getTime()));
			stmt.setBoolean(5, empl.isGioiTinh());
			stmt.setString(6, empl.getDiaChi());
			stmt.setString(7, empl.getCuaHang().getMaCuaHang());
			stmt.setString(8, empl.getAvt_url());
			stmt.setDate(9, new Date(empl.getNgayVaoLam().getTime()));
			stmt.setBoolean(10, empl.isDeleted());
			stmt.setString(11, ma_nv);

			return stmt.executeUpdate() > 0;

		} catch (SQLException e) {
			log.error("Database error while updating employee: {}", ma_nv, e);
		}
		return false;
	}

	@Override
	public boolean isPhoneExist(String phoneNum) {
		String sql = "SELECT TOP 1 1 FROM tbl_nhan_vien where so_dien_thoai = ? AND deleted = 0";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, phoneNum);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return true;
			}
		} catch (SQLException e) {
			log.error("Database error while checking phone existence: {}", phoneNum, e);
		}

		return false;
	}

	@Override
	public boolean isEmailExist(String email) {
		String sql = "SELECT TOP 1 1 FROM tbl_nhan_vien where email = ? AND deleted = 0";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {
			
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) return true;
			}
		} catch (SQLException e) {
			log.error("Database error while checking email existence: {}", email, e);
		}

		return false;
	}

	@Override
	public Employee getEmployeeById(String id) {
		String sql = "SELECT * FROM tbl_nhan_vien where ma_nv = ?";
		
		try (Connection con = ConnectDB.getInstance().getConnection();
			 PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, id);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Employee employee = new Employee();

					employee.setMaNhanVien(rs.getString("ma_nv"));
					employee.setTenNhanVien(rs.getString("ho_ten"));
					employee.setSoDienThoai(rs.getString("so_dien_thoai"));
					employee.setEmail(rs.getString("email"));
					employee.setNgaySinh(rs.getDate("ngay_sinh"));
					employee.setGioiTinh(rs.getBoolean("gioi_tinh"));
					employee.setDiaChi(rs.getString("dia_chi"));

					Store cuaHang = new Store(rs.getString("cua_hang_id"));
					employee.setCuaHang(cuaHang);

					employee.setAvt_url(rs.getString("avatar_url"));
					employee.setNgayVaoLam(rs.getDate("ngay_vao_lam"));
					employee.setDeleted(rs.getBoolean("deleted"));
					
					return employee;
				}
			}
		} catch (SQLException e) {
			log.error("Database error while fetching employee by ID: {}", id, e);
		}

		return null;
	}

}