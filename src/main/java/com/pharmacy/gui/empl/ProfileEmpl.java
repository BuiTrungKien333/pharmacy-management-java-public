package com.pharmacy.gui.empl;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.pharmacy.bus.EmployeeBUS;
import com.pharmacy.bus.StoreBUS;
import com.pharmacy.entity.Employee;
import com.pharmacy.entity.Store;

public class ProfileEmpl extends javax.swing.JDialog {
	
	private EmployeeBUS employeeBUS;
	private EmployeeGUI employeeGUI;
	private String imgPath;
	private String ma_nv;
	private StoreBUS storeBUS;

	public ProfileEmpl(java.awt.Frame parent, boolean modal, String ma_nv, EmployeeGUI employeeGUI,
			EmployeeBUS employeeBUS, StoreBUS storeBUS) {
		super(parent, modal);
		this.employeeGUI = employeeGUI;
		this.employeeBUS = employeeBUS;
		this.ma_nv = ma_nv;
		this.storeBUS = storeBUS;

		initComponents();

		// sự kiện
		btnCapNhat.addActionListener(e -> updateAction());
		btnXem.addActionListener(e -> loadInfo(getInfoEmpl()));
		btnXuat.addActionListener(e -> exportInfo());

	}

	private void initComponents() {

		jPanel = new javax.swing.JPanel();
		JpLeft = new javax.swing.JPanel();
		lblName = new javax.swing.JLabel();
		lblChucVu = new javax.swing.JLabel();
		btnXem = new javax.swing.JButton();
		btnXuat = new javax.swing.JButton();
		jLabel = new javax.swing.JLabel();
		JpRight = new javax.swing.JPanel();
		JpCapNhat = new javax.swing.JPanel();
		lblTen = new javax.swing.JLabel();
		txtHoTen = new javax.swing.JTextField();
		lblNgayVaoLam = new javax.swing.JLabel();
		txtNgayVaoLam = new javax.swing.JTextField();
		lblSoDienThoai = new javax.swing.JLabel();
		txtSoDienThoai = new javax.swing.JTextField();
		lblEmail = new javax.swing.JLabel();
		txtEmail = new javax.swing.JTextField();
		lblNgaySinh = new javax.swing.JLabel();
		txtNgaySinh = new javax.swing.JTextField();
		lblTrangThai = new javax.swing.JLabel();
		lblGioiTinh = new javax.swing.JLabel();
		comboGioiTinh = new javax.swing.JComboBox<>();
		lblDiaChi = new javax.swing.JLabel();
		txtDiaChi = new javax.swing.JTextField();
		btnAnh = new javax.swing.JButton();
		btnCapNhat = new javax.swing.JButton();
		lblTieuDe = new javax.swing.JLabel();
		comboTrangThai = new javax.swing.JComboBox<>();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setBackground(new java.awt.Color(102, 0, 0));
		setFocusCycleRoot(false);

		jPanel.setBackground(new java.awt.Color(218, 226, 233));

		JpLeft.setBackground(new java.awt.Color(255, 255, 255));

		lblName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		lblName.setText("Nguyễn Thị Thúy");

		lblChucVu.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
		lblChucVu.setText("Dược sĩ");

		btnXem.setBackground(new java.awt.Color(204, 204, 204));
		btnXem.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		btnXem.setText("Xem thông tin đầy đủ");

		btnXuat.setBackground(new java.awt.Color(255, 204, 204));
		btnXuat.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		btnXuat.setText("Xuất thông tin");

		jLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/default.png"))); // NOI18N
		jLabel.setPreferredSize(new java.awt.Dimension(150, 150));

		javax.swing.GroupLayout JpLeftLayout = new javax.swing.GroupLayout(JpLeft);
		JpLeft.setLayout(JpLeftLayout);
		JpLeftLayout.setHorizontalGroup(JpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpLeftLayout.createSequentialGroup().addGroup(JpLeftLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpLeftLayout.createSequentialGroup().addGap(118, 118, 118).addComponent(lblChucVu,
								javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(JpLeftLayout.createSequentialGroup().addGap(34, 34, 34)
								.addGroup(JpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(btnXem, javax.swing.GroupLayout.PREFERRED_SIZE, 219,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(btnXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 219,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGroup(JpLeftLayout.createSequentialGroup().addGap(67, 67, 67)
								.addGroup(JpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 200,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 140,
												javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addContainerGap(42, Short.MAX_VALUE)));
		JpLeftLayout.setVerticalGroup(JpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpLeftLayout.createSequentialGroup().addGap(75, 75, 75)
						.addComponent(jLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 140,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblName)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblChucVu)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
						.addComponent(btnXem, javax.swing.GroupLayout.PREFERRED_SIZE, 42,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18).addComponent(btnXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 42,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(78, 78, 78)));

		JpRight.setBackground(new java.awt.Color(255, 255, 255));
		JpCapNhat.setBackground(new java.awt.Color(255, 255, 255));

		lblTen.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblTen.setText("Tên đầy đủ");

		lblNgayVaoLam.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblNgayVaoLam.setText("Ngày vào làm");

		lblSoDienThoai.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblSoDienThoai.setText("Số điện thoại");

		lblEmail.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblEmail.setText("Email");

		lblNgaySinh.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblNgaySinh.setText("Ngày sinh");

		txtNgaySinh.setText(" ");

		lblTrangThai.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblTrangThai.setText("Trạng thái");

		lblGioiTinh.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblGioiTinh.setText("Giới tính");

		comboGioiTinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ" }));
		comboGioiTinh.setPreferredSize(new java.awt.Dimension(72, 34));

		lblDiaChi.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblDiaChi.setText("Địa chỉ cụ thể");

		btnAnh.setBackground(new java.awt.Color(204, 204, 204));
		btnAnh.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		btnAnh.setText("Chọn ảnh đại diện");
		btnAnh.addActionListener(e -> chonAnh());

		btnCapNhat.setBackground(new java.awt.Color(0, 0, 255));
		btnCapNhat.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		btnCapNhat.setForeground(new java.awt.Color(255, 255, 255));
		btnCapNhat.setText("Cập nhật thông tin");
		btnCapNhat.setPreferredSize(new java.awt.Dimension(75, 34));

		lblTieuDe.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
		lblTieuDe.setForeground(new java.awt.Color(51, 51, 255));
		lblTieuDe.setText("Thông tin chi tiết");

		comboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đang làm việc", "Nghỉ việc" }));
		comboTrangThai.setPreferredSize(new java.awt.Dimension(72, 34));

		javax.swing.GroupLayout JpCapNhatLayout = new javax.swing.GroupLayout(JpCapNhat);
		JpCapNhat.setLayout(JpCapNhatLayout);
		JpCapNhatLayout
				.setHorizontalGroup(JpCapNhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpCapNhatLayout.createSequentialGroup().addGap(29, 29, 29).addGroup(JpCapNhatLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(JpCapNhatLayout
										.createSequentialGroup()
										.addComponent(lblTieuDe, javax.swing.GroupLayout.PREFERRED_SIZE, 193,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(0, 0, Short.MAX_VALUE))
								.addGroup(JpCapNhatLayout.createSequentialGroup().addGroup(JpCapNhatLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(txtSoDienThoai, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
										.addComponent(lblTen, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.PREFERRED_SIZE, 67,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(lblSoDienThoai, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.PREFERRED_SIZE, 86,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(txtHoTen, javax.swing.GroupLayout.Alignment.LEADING)).addGap(45,
												45, 45)
										.addGroup(JpCapNhatLayout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(JpCapNhatLayout.createSequentialGroup()
														.addGroup(JpCapNhatLayout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(txtNgayVaoLam).addComponent(txtEmail))
														.addGap(21, 21, 21))
												.addGroup(JpCapNhatLayout.createSequentialGroup()
														.addGroup(JpCapNhatLayout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING).addComponent(
																		lblEmail,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 47,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblNgayVaoLam,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 89,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addContainerGap(
																javax.swing.GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))))
								.addGroup(JpCapNhatLayout.createSequentialGroup().addGroup(JpCapNhatLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 183,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(lblNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 64,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(
												btnAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 170,
												javax.swing.GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31,
												Short.MAX_VALUE)
										.addGroup(JpCapNhatLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(JpCapNhatLayout.createSequentialGroup()
														.addGroup(JpCapNhatLayout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(
																		comboGioiTinh,
																		javax.swing.GroupLayout.PREFERRED_SIZE,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblGioiTinh))
														.addGap(45, 45, 45)
														.addGroup(JpCapNhatLayout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(comboTrangThai,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 193,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(lblTrangThai,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 77,
																		javax.swing.GroupLayout.PREFERRED_SIZE)))
												.addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 404,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 101,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addContainerGap(21, Short.MAX_VALUE))))
						.addGroup(JpCapNhatLayout.createSequentialGroup().addGap(193, 193, 193).addComponent(btnCapNhat,
								javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE)));
		JpCapNhatLayout.setVerticalGroup(JpCapNhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpCapNhatLayout.createSequentialGroup().addGap(41, 41, 41).addComponent(lblTieuDe)
						.addGap(18, 18, 18)
						.addGroup(JpCapNhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblNgayVaoLam).addComponent(lblTen))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(JpCapNhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtNgayVaoLam, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(JpCapNhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(JpCapNhatLayout.createSequentialGroup()
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(lblSoDienThoai).addGap(69, 69, 69)
										.addGroup(JpCapNhatLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(lblGioiTinh).addComponent(lblNgaySinh))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(JpCapNhatLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(comboGioiTinh, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.addGroup(JpCapNhatLayout.createSequentialGroup().addGap(7, 7, 7).addComponent(lblEmail)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(JpCapNhatLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE,
														34, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(24, 24, 24).addComponent(lblTrangThai)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(comboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(0, 0, Short.MAX_VALUE)))
						.addGap(14, 14, 14).addComponent(lblDiaChi)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(JpCapNhatLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(43, 43, 43)
						.addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(46, 46, 46)));

		javax.swing.GroupLayout JpRightLayout = new javax.swing.GroupLayout(JpRight);
		JpRight.setLayout(JpRightLayout);
		JpRightLayout.setHorizontalGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 703, Short.MAX_VALUE)
				.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpRightLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
								.addComponent(JpCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE))));
		JpRightLayout.setVerticalGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 0, Short.MAX_VALUE)
				.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpRightLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
								.addComponent(JpCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE))));

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel);
		jPanel.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addContainerGap(22, Short.MAX_VALUE)
						.addComponent(JpLeft, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(JpRight, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(52, 52, 52)));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addGap(29, 29, 29)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(JpLeft, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(JpRight, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
				javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		pack();
	}

	private void chonAnh() {
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("Hình ảnh (jpg, png, gif)", "jpg", "png",
				"gif");
		fileChooser.setFileFilter(fileFilter);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();

			if (!selectedFile.exists()) {
				JOptionPane.showMessageDialog(this, "File không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				int width = Math.max(jLabel.getWidth(), 200);
				int height = Math.max(jLabel.getHeight(), 200);

				BufferedImage originalImage = ImageIO.read(selectedFile);
				if (originalImage != null) {
					Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
					ImageIcon icon = new ImageIcon(scaledImage);

					jLabel.setIcon(icon);
					jLabel.revalidate();
					jLabel.repaint();

					imgPath = selectedFile.getAbsolutePath();
				} else {
					JOptionPane.showMessageDialog(this,
							"Không thể đọc file ảnh! File có thể bị hỏng hoặc không phải định dạng ảnh hợp lệ.", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				}

			} catch (Exception e) {

				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void updateAction() {

		Employee emp = getInfoEmpl();

		System.out.println(ma_nv);

		if (emp == null)
			return;
		try {
			employeeBUS.updateEmployee(emp, ma_nv);

			JOptionPane.showMessageDialog(this, "Cập nhật thông tin nhân viên thành công!");

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
		employeeGUI.loadData(employeeBUS.getAllNhanVien());
		this.dispose();
	}

	private Employee getInfoEmpl() {
		String ho_ten = txtHoTen.getText().trim();
		String so_dien_thoai = txtSoDienThoai.getText().trim();
		String email = txtEmail.getText().trim();

		if (ho_ten.isEmpty() || so_dien_thoai.isEmpty() || email.isEmpty() || comboGioiTinh.getSelectedIndex() == -1
				|| comboTrangThai.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(this, "Bạn vui lòng chọn đầy đủ thông tin!", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return null;
		}

		if (!ho_ten.matches("^[\\p{L}\\s]{2,50}")) {
			JOptionPane.showMessageDialog(this, "Tên chỉ được chứa chữ và dấu cách!", "Lỗi nhập liệu",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		if (!so_dien_thoai.matches("^0[0-9]{9}$")) {
			JOptionPane.showMessageDialog(this, "Số điện thoại chỉ được là số và chứa 10 chữ số!", "Lỗi nhập liệu",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		if (!email.matches("^[a-zA-Z0-9_$][a-zA-Z0-9_$%]*@gmail.com$")) {
			JOptionPane.showMessageDialog(this, "Email theo định dạng ten@gmail.com!", "Lỗi nhập liệu",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setLenient(false);
		Date ngaySinh = null;
		Date ngayVaoLam = null;
		try {
			ngaySinh = sdf.parse(txtNgaySinh.getText().trim());
			ngayVaoLam = sdf.parse(txtNgayVaoLam.getText().trim());

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ (dd/MM/yyyy)", "Lỗi nhập liệu",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
		LocalDate ngayHienTai = LocalDate.now();
		Date date = Date.from(ngayHienTai.atStartOfDay(ZoneId.systemDefault()).toInstant());

		long tuoi = date.getTime() - ngaySinh.getTime();

		if (tuoi < 18) {
			JOptionPane.showMessageDialog(this, "Bạn chưa đủ tuổi làm việc!", "Lỗi nhập liệu",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		boolean gioiTinh = comboGioiTinh.getSelectedItem().equals("Nam") ? true : false;

		if (comboGioiTinh.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn giới tính", "Lỗi nhập liệu",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		boolean trang_thai = comboTrangThai.getSelectedItem().equals("Đang làm việc") ? false : true;

		if (comboTrangThai.getSelectedIndex() == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái làm việc", "Lỗi nhập liệu",
					JOptionPane.WARNING_MESSAGE);
			return null;
		}

		String dia_chi = txtDiaChi.getText().trim();

		if (!dia_chi.matches("^[\\p{L}\\s0-9,./-]{2,50}$")) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ từ 2 - 50 ký tự");
			return null;
		}

		Store cua_hang = storeBUS.getInfoStore("1");

		return new Employee(ho_ten, so_dien_thoai, email, ngaySinh, gioiTinh, dia_chi, cua_hang, trang_thai, imgPath,
				ngayVaoLam);

	}

	public void loadInfo(Employee emp) {
		txtHoTen.setText(emp.getTenNhanVien());
		txtDiaChi.setText(emp.getDiaChi());
		txtEmail.setText(emp.getEmail());
		lblName.setText(emp.getTenNhanVien());

		Date ngaySinh = emp.getNgaySinh();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dateStr = sdf.format(ngaySinh);

		Date ngayVaoLam = emp.getNgayVaoLam();
		String ngay_vao_lam = sdf.format(ngayVaoLam);

		txtNgaySinh.setText(dateStr);
		txtNgayVaoLam.setText(ngay_vao_lam);
		txtSoDienThoai.setText(emp.getSoDienThoai());

		String gioiTinh = emp.isGioiTinh() ? "Nam" : "Nữ";
		comboGioiTinh.setSelectedItem(gioiTinh);

		String trangThai = emp.isDeleted() ? "Nghỉ việc" : "Đang làm việc";
		comboTrangThai.setSelectedItem(trangThai);

		try {
			int width = Math.max(jLabel.getWidth(), 200);
			int height = Math.max(jLabel.getHeight(), 200);
			BufferedImage originalImage = null;

			String avtPath = emp.getAvt_url();

			if (avtPath != null && !avtPath.trim().isEmpty()) {
				File imgFile = new File(avtPath);
				if (imgFile.exists()) {
					originalImage = ImageIO.read(imgFile);
					Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
					jLabel.setIcon(new ImageIcon(scaledImage));
					jLabel.revalidate();
					jLabel.repaint();
				}
			}

			if (originalImage == null) {
				jLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/default.png")));
			}

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	private void exportInfo() {

	}

	private javax.swing.JPanel JpCapNhat;
	private javax.swing.JPanel JpLeft;
	private javax.swing.JPanel JpRight;
	private javax.swing.JButton btnAnh;
	private javax.swing.JButton btnXem;
	private javax.swing.JButton btnXuat;
	private javax.swing.JComboBox<String> comboGioiTinh;
	private javax.swing.JComboBox<String> comboTrangThai;
	private javax.swing.JButton btnCapNhat;
	private javax.swing.JLabel jLabel;
	private javax.swing.JLabel lblGioiTinh;
	private javax.swing.JPanel jPanel;
	private javax.swing.JTextField txtHoTen;
	private javax.swing.JLabel lblChucVu;
	private javax.swing.JLabel lblDiaChi;
	private javax.swing.JLabel lblEmail;
	private javax.swing.JLabel lblName;
	private javax.swing.JLabel lblNgaySinh;
	private javax.swing.JLabel lblNgayVaoLam;
	private javax.swing.JLabel lblSoDienThoai;
	private javax.swing.JLabel lblTen;
	private javax.swing.JLabel lblTieuDe;
	private javax.swing.JLabel lblTrangThai;
	private javax.swing.JTextField txtDiaChi;
	private javax.swing.JTextField txtEmail;
	private javax.swing.JTextField txtNgaySinh;
	private javax.swing.JTextField txtNgayVaoLam;
	private javax.swing.JTextField txtSoDienThoai;
}