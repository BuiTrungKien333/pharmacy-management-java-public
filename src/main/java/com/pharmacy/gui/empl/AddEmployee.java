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
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pharmacy.bus.EmployeeBUS;
import com.pharmacy.bus.StoreBUS;
import com.pharmacy.entity.Employee;
import com.pharmacy.entity.Store;
import com.toedter.calendar.JDateChooser;

public class AddEmployee extends javax.swing.JDialog {

	private JDateChooser dateChooser;
	private JTextField activeTextField;
	private StoreBUS storeBus;
	private EmployeeBUS employeeBUS;
	private String imgPath;
	private EmployeeGUI employeeGUI;

	public AddEmployee(java.awt.Frame parent, boolean modal, EmployeeGUI employeeGUI, EmployeeBUS employeeBUS,
			StoreBUS storeBUS) {
		super(parent, modal);

		this.employeeGUI = employeeGUI;
		this.employeeBUS = employeeBUS;
		this.storeBus = storeBUS;

		initComponents();

		btnThemNhanVien.addActionListener(e -> themAction());

	}

	private void initComponents() {

		jPanel3 = new javax.swing.JPanel();
		JpLeft = new javax.swing.JPanel();
		lblAnh = new javax.swing.JLabel();
		btnChonAnh = new javax.swing.JButton();
		JpRight = new javax.swing.JPanel();
		JpCapNhat1 = new javax.swing.JPanel();
		lblTen = new javax.swing.JLabel();
		txtTen = new javax.swing.JTextField();
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
		btnThemNhanVien = new javax.swing.JButton();
		lblTieuDe = new javax.swing.JLabel();
		comboTrangThai = new javax.swing.JComboBox<>();
		lblChucVu = new javax.swing.JLabel();
		comboChucVu = new javax.swing.JComboBox<>();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setBackground(new java.awt.Color(102, 0, 0));
		setFocusCycleRoot(false);
		setPreferredSize(new java.awt.Dimension(1050, 600));

		jPanel3.setBackground(new java.awt.Color(218, 226, 233));

		JpLeft.setBackground(new java.awt.Color(255, 255, 255));

		lblAnh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblAnh.setIcon(
				new javax.swing.ImageIcon(getClass().getResource("/images/default.png"))); // NOI18N
		
		lblAnh.setPreferredSize(new java.awt.Dimension(150, 150));

		btnChonAnh.setBackground(new java.awt.Color(204, 204, 204));
		btnChonAnh.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		btnChonAnh.setText("Chọn ảnh đại diện");

		btnChonAnh.addActionListener(e -> chonAnh());

		javax.swing.GroupLayout JpLeftLayout = new javax.swing.GroupLayout(JpLeft);
		JpLeft.setLayout(JpLeftLayout);
		JpLeftLayout.setHorizontalGroup(JpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpLeftLayout.createSequentialGroup().addGap(26, 26, 26)
						.addComponent(lblAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 242,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(27, Short.MAX_VALUE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JpLeftLayout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnChonAnh,
								javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(58, 58, 58)));
		JpLeftLayout.setVerticalGroup(JpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpLeftLayout.createSequentialGroup().addGap(46, 46, 46)
						.addComponent(lblAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 292,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(46, 46, 46).addComponent(btnChonAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(107, Short.MAX_VALUE)));

		JpRight.setBackground(new java.awt.Color(255, 255, 255));

		JpCapNhat1.setBackground(new java.awt.Color(255, 255, 255));

		lblTen.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblTen.setText("Tên đầy đủ");

		txtTen.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				txtTenActionPerformed(evt);
			}
		});

		lblNgayVaoLam.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblNgayVaoLam.setText("Ngày vào làm");

		lblSoDienThoai.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblSoDienThoai.setText("Số điện thoại");

		lblEmail.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblEmail.setText("Email");

		lblNgaySinh.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblNgaySinh.setText("Ngày sinh");

		txtNgaySinh.setText(" ");
		txtNgaySinh.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				txtNgaySinhActionPerformed(evt);
			}
		});

		dateChooser = new JDateChooser();
		dateChooser.setDateFormatString("dd/MM/yyyy");
		dateChooser.setVisible(false);
		activeTextField = new JTextField();

		txtNgaySinh.add(dateChooser);
		txtNgayVaoLam.add(dateChooser);

		txtNgaySinh.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/calendar.svg", 0.4f));
		txtNgayVaoLam.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/calendar.svg", 0.4f));

		txtNgaySinh.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				activeTextField = txtNgaySinh;
				dateChooser.setVisible(true);
				dateChooser.getCalendarButton().doClick();
			}
		});

		txtNgayVaoLam.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				activeTextField = txtNgayVaoLam;
				dateChooser.setVisible(true);
				dateChooser.getCalendarButton().doClick();
			}
		});

		dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> {
			if (dateChooser.getDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				activeTextField.setText(sdf.format(dateChooser.getDate()));

			}
		});

		lblTrangThai.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblTrangThai.setText("Trạng thái");

		lblGioiTinh.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblGioiTinh.setText("Giới tính");

		comboGioiTinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ" }));
		comboGioiTinh.setPreferredSize(new java.awt.Dimension(72, 34));

		lblDiaChi.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblDiaChi.setText("Địa chỉ cụ thể");

		btnThemNhanVien.setBackground(new java.awt.Color(0, 0, 255));
		btnThemNhanVien.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		btnThemNhanVien.setForeground(new java.awt.Color(255, 255, 255));
		btnThemNhanVien.setText("Thêm nhân viên mới");
		btnThemNhanVien.setPreferredSize(new java.awt.Dimension(75, 34));

		lblTieuDe.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
		lblTieuDe.setForeground(new java.awt.Color(51, 51, 255));
		lblTieuDe.setText("Thông tin chi tiết");

		comboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đang làm việc", "Nghỉ việc" }));
		comboTrangThai.setPreferredSize(new java.awt.Dimension(72, 34));

		lblChucVu.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
		lblChucVu.setText("Chức vụ");

		comboChucVu.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dược sĩ", "Quản lý" }));

		javax.swing.GroupLayout JpCapNhat1Layout = new javax.swing.GroupLayout(JpCapNhat1);
		JpCapNhat1.setLayout(JpCapNhat1Layout);
		JpCapNhat1Layout.setHorizontalGroup(JpCapNhat1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpCapNhat1Layout.createSequentialGroup().addGap(29, 29, 29).addGroup(JpCapNhat1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpCapNhat1Layout.createSequentialGroup()
								.addComponent(lblTieuDe, javax.swing.GroupLayout.PREFERRED_SIZE, 193,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE))
						.addGroup(JpCapNhat1Layout.createSequentialGroup().addGroup(
								JpCapNhat1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(txtSoDienThoai, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
										.addComponent(lblTen, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.PREFERRED_SIZE, 67,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(lblSoDienThoai, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.PREFERRED_SIZE, 86,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(txtTen, javax.swing.GroupLayout.Alignment.LEADING))
								.addGap(45, 45, 45)
								.addGroup(JpCapNhat1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(JpCapNhat1Layout.createSequentialGroup()
												.addGroup(JpCapNhat1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(txtNgayVaoLam).addComponent(txtEmail))
												.addGap(21, 21, 21))
										.addGroup(JpCapNhat1Layout.createSequentialGroup().addGroup(JpCapNhat1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 47,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblNgayVaoLam, javax.swing.GroupLayout.PREFERRED_SIZE, 89,
														javax.swing.GroupLayout.PREFERRED_SIZE))
												.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE))))
						.addGroup(JpCapNhat1Layout.createSequentialGroup().addGroup(JpCapNhat1Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 183,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 64,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 53,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(comboChucVu, javax.swing.GroupLayout.PREFERRED_SIZE, 151,
										javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31,
										Short.MAX_VALUE)
								.addGroup(JpCapNhat1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(JpCapNhat1Layout.createSequentialGroup().addGroup(JpCapNhat1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(comboGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblGioiTinh)).addGap(45, 45, 45)
												.addGroup(JpCapNhat1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
				.addGroup(JpCapNhat1Layout
						.createSequentialGroup().addGap(193, 193, 193).addComponent(btnThemNhanVien,
								javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, Short.MAX_VALUE)));
		JpCapNhat1Layout.setVerticalGroup(JpCapNhat1Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpCapNhat1Layout.createSequentialGroup().addGap(41, 41, 41).addComponent(lblTieuDe)
						.addGap(18, 18, 18)
						.addGroup(JpCapNhat1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblNgayVaoLam).addComponent(lblTen))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(JpCapNhat1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtNgayVaoLam, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(JpCapNhat1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(JpCapNhat1Layout.createSequentialGroup()
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(lblSoDienThoai).addGap(69, 69, 69)
										.addGroup(JpCapNhat1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(lblGioiTinh, javax.swing.GroupLayout.Alignment.TRAILING)
												.addComponent(lblNgaySinh))
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(JpCapNhat1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(comboGioiTinh, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
								.addGroup(JpCapNhat1Layout.createSequentialGroup().addGap(7, 7, 7)
										.addComponent(lblEmail)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addGroup(JpCapNhat1Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE,
														34, javax.swing.GroupLayout.PREFERRED_SIZE))
										.addGap(24, 24, 24).addComponent(lblTrangThai)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
										.addComponent(comboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(14, 14, 14)
						.addGroup(JpCapNhat1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblDiaChi).addComponent(lblChucVu))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(JpCapNhat1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(comboChucVu, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(43, 43, 43)
						.addComponent(btnThemNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(46, 46, 46)));

		javax.swing.GroupLayout JpRightLayout = new javax.swing.GroupLayout(JpRight);
		JpRight.setLayout(JpRightLayout);
		JpRightLayout.setHorizontalGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 703, Short.MAX_VALUE)
				.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpRightLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
								.addComponent(JpCapNhat1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE))));
		JpRightLayout.setVerticalGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 0, Short.MAX_VALUE)
				.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpRightLayout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE)
								.addComponent(JpCapNhat1, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE))));

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
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
				jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
				jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE,
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
				int width = Math.max(lblAnh.getWidth(), 200);
				int height = Math.max(lblAnh.getHeight(), 200);

				BufferedImage originalImage = ImageIO.read(selectedFile);
				if (originalImage != null) {
					Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
					ImageIcon icon = new ImageIcon(scaledImage);

					lblAnh.setIcon(icon);
					lblAnh.revalidate();
					lblAnh.repaint();

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

	private void themAction() {
		Employee emp = getInfoEmp();

		if (emp == null)
			return;
		try {
			employeeBUS.addEmployee(emp);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
			return;
		}

		clearAll();
		this.dispose();
		employeeGUI.loadData(employeeBUS.getAllNhanVien());

	}

	private void clearAll() {
		lblAnh.setIcon(
				new javax.swing.ImageIcon("E:\\PTUD_N17\\pharmacy_project\\src\\main\\resources\\images\\default.png"));
		txtTen.setText("");
		txtDiaChi.setText("");
		txtEmail.setText("");
		txtNgaySinh.setText("");
		txtNgayVaoLam.setText("");
		txtSoDienThoai.setText("");
		txtSoDienThoai.setText("");
		comboChucVu.setSelectedIndex(-1);
		comboGioiTinh.setSelectedIndex(-1);
		comboTrangThai.setSelectedIndex(-1);

	}

	private Employee getInfoEmp() {

		String ho_ten = txtTen.getText().trim();
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

		if (!dia_chi.matches("^[\\p{L}\\s0-9/-]{2,50}$")) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ từ 2 - 50 ký tự");
			return null;
		}

		Store cua_hang = storeBus.getInfoStore("1");

		return new Employee(ho_ten, so_dien_thoai, email, ngaySinh, gioiTinh, dia_chi, cua_hang, trang_thai, imgPath,
				ngayVaoLam);

	}

	private void txtTenActionPerformed(java.awt.event.ActionEvent evt) {

	}

	private void txtNgaySinhActionPerformed(java.awt.event.ActionEvent evt) {

	}

	private javax.swing.JPanel JpCapNhat1;
	private javax.swing.JPanel JpLeft;
	private javax.swing.JPanel JpRight;
	private javax.swing.JButton btnChonAnh;
	private javax.swing.JButton btnThemNhanVien;
	private javax.swing.JComboBox<String> comboChucVu;
	private javax.swing.JComboBox<String> comboGioiTinh;
	private javax.swing.JComboBox<String> comboTrangThai;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JLabel lblAnh;
	private javax.swing.JLabel lblChucVu;
	private javax.swing.JLabel lblDiaChi;
	private javax.swing.JLabel lblEmail;
	private javax.swing.JLabel lblGioiTinh;
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
	private javax.swing.JTextField txtTen;

}