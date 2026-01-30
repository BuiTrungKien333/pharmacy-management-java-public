package com.pharmacy.gui.profile;

import java.time.LocalDate;

import com.pharmacy.bus.EmployeeBUS;
import com.pharmacy.entity.Employee;
import com.pharmacy.utils.FormatUtil;
import com.pharmacy.utils.HelperImageIcon;

public class ProfileGUI extends javax.swing.JPanel {

	private final EmployeeBUS employeeBUS = new EmployeeBUS();

	public ProfileGUI() {
		initComponents();

		viewData();
	}

	private void viewData() {
		Employee employee = employeeBUS.getProfileCurrentUser();
		txtMaNV.setText(employee.getMaNhanVien());
		txtTenNV.setText(employee.getTenNhanVien());
		txtPhone.setText(employee.getSoDienThoai());

		if (employee.getNgayVaoLam() != null) {
			LocalDate nvl = new java.sql.Date(employee.getNgayVaoLam().getTime()).toLocalDate();
			txtNgayVaoLam.setText(FormatUtil.formatDate(nvl));
		} else {
			txtNgayVaoLam.setText("");
		}

		if (employee.getNgaySinh() != null) {
			LocalDate dob = new java.sql.Date(employee.getNgaySinh().getTime()).toLocalDate();
			txtNgaySinh.setText(FormatUtil.formatDate(dob));
		} else {
			txtNgaySinh.setText("");
		}

		txtGioiTinh.setText(employee.isGioiTinh() ? "Nam" : "Nữ");

		txtEmail.setText(employee.getEmail());

		txtDiaChi.setText(employee.getDiaChi());

		txtChucVu.setText(employeeBUS.getRoleNameCurrentUser());
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jPanel2 = new javax.swing.JPanel();
		lblAvatar = new javax.swing.JLabel();
		jPanel3 = new javax.swing.JPanel();
		lblMaNV = new javax.swing.JLabel();
		lblTenNV = new javax.swing.JLabel();
		lblChucVu = new javax.swing.JLabel();
		lblPhone = new javax.swing.JLabel();
		lblEmail = new javax.swing.JLabel();
		txtMaNV = new javax.swing.JTextField();
		txtChucVu = new javax.swing.JTextField();
		txtPhone = new javax.swing.JTextField();
		txtTenNV = new javax.swing.JTextField();
		txtEmail = new javax.swing.JTextField();
		lblNgaySinh = new javax.swing.JLabel();
		lblDiaChi = new javax.swing.JLabel();
		lblGioiTinh = new javax.swing.JLabel();
		lblNgayVaoLam = new javax.swing.JLabel();
		txtNgayVaoLam = new javax.swing.JTextField();
		txtDiaChi = new javax.swing.JTextField();
		txtGioiTinh = new javax.swing.JTextField();
		txtNgaySinh = new javax.swing.JTextField();

		txtChucVu.setEditable(false);
		txtDiaChi.setEditable(false);
		txtEmail.setEditable(false);
		txtGioiTinh.setEditable(false);
		txtMaNV.setEditable(false);
		txtNgaySinh.setEditable(false);
		txtNgayVaoLam.setEditable(false);
		txtPhone.setEditable(false);
		txtTenNV.setEditable(false);

		setBackground(new java.awt.Color(255, 255, 255));

		jPanel1.setPreferredSize(new java.awt.Dimension(500, 750));
		jPanel1.setLayout(new java.awt.BorderLayout());

		jPanel2.setPreferredSize(new java.awt.Dimension(500, 220));

		lblAvatar.setIcon(HelperImageIcon.scaleIcon("/images/default.png", 200, 200)); // NOI18N
		lblAvatar.setPreferredSize(new java.awt.Dimension(200, 200));
		jPanel2.add(lblAvatar);

		jPanel1.add(jPanel2, java.awt.BorderLayout.PAGE_START);

		lblMaNV.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblMaNV.setText("Mã nhân viên");

		lblTenNV.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblTenNV.setText("Tên nhân viên");

		lblChucVu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblChucVu.setText("Chức vụ");

		lblPhone.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblPhone.setText("Số điện thoại");

		lblEmail.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblEmail.setText("Email");

		lblNgaySinh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblNgaySinh.setText("Ngày sinh");

		lblDiaChi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblDiaChi.setText("Địa chỉ");

		lblGioiTinh.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblGioiTinh.setText("Giới tính");

		lblNgayVaoLam.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblNgayVaoLam.setText("Ngày vào làm");

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addGap(23, 23, 23)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(lblMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTenNV, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNgayVaoLam, javax.swing.GroupLayout.PREFERRED_SIZE, 102,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(32, 32, 32)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(txtNgayVaoLam, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtTenNV, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 320,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(23, Short.MAX_VALUE)));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addGap(17, 17, 17)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(lblMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtMaNV, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(16, 16, 16)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblTenNV, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtTenNV, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel3Layout.createSequentialGroup()
										.addComponent(lblGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addGroup(jPanel3Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(lblDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(18, 18, 18)
										.addGroup(jPanel3Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(lblNgayVaoLam, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtNgayVaoLam, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
														javax.swing.GroupLayout.PREFERRED_SIZE)))
								.addComponent(txtGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(80, Short.MAX_VALUE)));

		jPanel1.add(jPanel3, java.awt.BorderLayout.CENTER);

		add(jPanel1);
	}

	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JLabel lblAvatar;
	private javax.swing.JLabel lblChucVu;
	private javax.swing.JLabel lblDiaChi;
	private javax.swing.JLabel lblEmail;
	private javax.swing.JLabel lblGioiTinh;
	private javax.swing.JLabel lblMaNV;
	private javax.swing.JLabel lblNgaySinh;
	private javax.swing.JLabel lblNgayVaoLam;
	private javax.swing.JLabel lblPhone;
	private javax.swing.JLabel lblTenNV;
	private javax.swing.JTextField txtChucVu;
	private javax.swing.JTextField txtDiaChi;
	private javax.swing.JTextField txtEmail;
	private javax.swing.JTextField txtGioiTinh;
	private javax.swing.JTextField txtMaNV;
	private javax.swing.JTextField txtNgaySinh;
	private javax.swing.JTextField txtNgayVaoLam;
	private javax.swing.JTextField txtPhone;
	private javax.swing.JTextField txtTenNV;
}
