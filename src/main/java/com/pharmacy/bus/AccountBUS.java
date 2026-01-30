package com.pharmacy.bus;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pharmacy.dao.AccountDAO;
import com.pharmacy.dao.EmployeeDAO;
import com.pharmacy.dao.PermissionDAO;
import com.pharmacy.dao.impl.AccountDaoImpl;
import com.pharmacy.dao.impl.EmployeeDaoImpl;
import com.pharmacy.entity.Employee;
import com.pharmacy.exception.InvalidDataException;
import com.pharmacy.exception.ResourceNotFoundException;
import com.pharmacy.utils.EmailUtil;
import com.pharmacy.utils.OTPUlti;
import com.pharmacy.utils.PasswordUtil;
import com.pharmacy.utils.TemplateUtil;

public class AccountBUS {

	private static final Logger log = LoggerFactory.getLogger(AccountBUS.class);

	private final AccountDAO accountDAO = new AccountDaoImpl();

	private final EmployeeDAO employeeDAO = new EmployeeDaoImpl();

	private final PermissionDAO permissionDAO = new PermissionDAO();

	public boolean login(String user, String pass) {
		log.debug("Processing login request for user: {}", user);
		if (user.isEmpty() || pass.isEmpty())
			throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin.");

		if (!accountDAO.login(user, pass)) {
			log.warn("Login failed: Invalid credentials for user: {}", user);
			throw new IllegalArgumentException("Thông tin đăng nhập không chính xác.");
		}

		try {
			Employee employee = employeeDAO.getUser(user);
			Set<String> permissions = permissionDAO.getPermissionByEmplId(user);

			log.info("Login successful for user: {}. Loaded {} permissions.", user, permissions.size());

			Auth.loginSuccess(employee, permissions);
			return true;
		} catch (Exception e) {
			log.error("Login successful but failed to load user data/permissions for user: {}", user, e);
			throw e;
		}
	}

	public void forgotPassword(String empId, String email) {
		log.info("Requesting password reset (OTP) for EmpID: {}, Email: {}", empId, email);

		if (!employeeDAO.checkExistsByEmpIdAndEmail(empId, email)) {
			log.warn("Password reset failed: No matching record for EmpID: {} and Email: {}", empId, email);
			throw new ResourceNotFoundException(
					"Mã nhân viên hoặc email không chính xác, hoặc tài khoản không tồn tại.");
		}

		try {
			String otp = OTPUlti.generateOTP();

			log.debug("Preparing email template for {}", email);
			String html = TemplateUtil.loadTemplate("templates/send-email-forgot-password.html")
					.replace("{{user}}", email).replace("{{OTP}}", otp);

			EmailUtil.sendEmail(email, "Alami Pharmacy - Forgot password", html);
			log.info("OTP email sent successfully to {}", email);

			accountDAO.saveToken(empId, otp);
			log.debug("OTP token saved to database for EmpID: {}", empId);

		} catch (Exception e) {
			log.error("Error during forgot password process for EmpID: {}", empId, e);
			throw new RuntimeException("Có lỗi xảy ra trong quá trình gửi mail hoặc lưu OTP.");
		}
	}

	public void verifyOTP(String empId, String otpInput) {
		log.debug("Verifying OTP for EmpID: {}", empId);

		if (!accountDAO.checkOtpVerify(empId, otpInput)) {
			log.warn("OTP verification failed for EmpID: {}. Invalid or expired OTP.", empId);
			throw new InvalidDataException("Mã OTP không chính xác hoặc đã hết hạn.");
		}

		log.info("OTP verified successfully for EmpID: {}", empId);
	}

	public void changePassword(String empId, String pass) {
		log.info("Processing password change for EmpID: {}", empId);

		try {
			String newPass = PasswordUtil.createPasswordHash(pass);
			boolean success = accountDAO.changePassword(empId, newPass);

			if (success) {
				log.info("Password changed successfully for EmpID: {}", empId);
			} else {
				log.error("Failed to update password in database for EmpID: {}", empId);
				throw new RuntimeException("Không thể cập nhật mật khẩu.");
			}
		} catch (Exception e) {
			log.error("Error changing password for EmpID: {}", empId, e);
			throw e;
		}
	}
}
