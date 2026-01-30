package com.pharmacy.gui.supplier;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JOptionPane;

import com.pharmacy.bus.NhaCungCapBUS;
import com.pharmacy.entity.NhaCungCap;

public class AddSupplierDialog extends javax.swing.JDialog {

	private NhaCungCapBUS nhaCungCapBUS;
	private SupplierGUI gui; 
	public AddSupplierDialog(java.awt.Frame parent, boolean modal, SupplierGUI gui) {
		super(parent, modal);
		
		nhaCungCapBUS = new NhaCungCapBUS();
		this.gui = gui; 
		initComponents();
		this.setSize(new Dimension(700, 750));
		
		initEvent();
	}

	private void initEvent() {
		btnLuu.addActionListener(e -> saveAtion());
		btnHuy.addActionListener(e->clearAll());
		
	}

	private void saveAtion() {
		NhaCungCap ncc = getInfo();
		try {
			if (nhaCungCapBUS.insertSupplier(ncc)) {
				JOptionPane.showMessageDialog(this, "Thêm thành công nhà cung cấp", "Thông báo", JOptionPane.DEFAULT_OPTION);
				clearAll();
				dispose();
				gui.loadDataFromSQL();
				
			}
			else JOptionPane.showMessageDialog(this, "Thêm nhà cung cấp không thành công!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	private void clearAll() {
		txtDiaChi.setText("");
		txtEmail.setText("");
		txtGhiChu.setText("");
		txtMaNhaMay.setText("");
		txtMaSoThue.setText("");
		txtSdt.setText("");
		txtTenNCC.setText("");
		txtWeb.setText("");
		
	}

	private NhaCungCap getInfo() {
		NhaCungCap ncc = new NhaCungCap(); 
		ncc.setDiaChi(txtDiaChi.getText());
		if (!txtEmail.getText().matches("^[a-zA-Z0-9_$][a-zA-Z0-9_$%]*@gmail.com$")) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập email đúng định dạng", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return null; 
		}
		ncc.setEmail(txtEmail.getText());
		ncc.setGhiChu(txtGhiChu.getText());
		ncc.setMaNhaMay(txtMaNhaMay.getText());
		ncc.setMaSoThue(txtMaSoThue.getText());
		if (!txtSdt.getText().matches("^0[0-9]{9}$")) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại đúng định dạng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return null; 
		}
		
		ncc.setSoDienThoai(txtSdt.getText());
		ncc.setTenNhaCungCap(txtTenNCC.getText());
		ncc.setWebsite(txtWeb.getText());
		
		return ncc; 
	}

	private void initComponents() {

		JpRight = new javax.swing.JPanel();
		lblTieuDe = new javax.swing.JLabel();
		lblMaNhaMay = new javax.swing.JLabel();
		lblTenNCC = new javax.swing.JLabel();
		lblDiaChi = new javax.swing.JLabel();
		lblSdt = new javax.swing.JLabel();
		lblMaSoThue = new javax.swing.JLabel();
		lblEmail = new javax.swing.JLabel();
		lblWeb = new javax.swing.JLabel();
		txtMaNhaMay = new javax.swing.JTextField();
		txtTenNCC = new javax.swing.JTextField();
		txtDiaChi = new javax.swing.JTextField();
		txtSdt = new javax.swing.JTextField();
		txtEmail = new javax.swing.JTextField();
		txtMaSoThue = new javax.swing.JTextField();
		txtWeb = new javax.swing.JTextField();
		lblGhiChu = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		txtGhiChu = new javax.swing.JTextArea();
		btnHuy = new javax.swing.JButton();
		btnLuu = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		

		JpRight.setBackground(new java.awt.Color(232, 232, 232));

		lblTieuDe.setFont(new java.awt.Font("Segoe UI Semibold", 0, 20)); // NOI18N
		lblTieuDe.setForeground(new java.awt.Color(0, 0, 255));
		lblTieuDe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblTieuDe.setText("Thêm nhà cung cấp mới");

		lblMaNhaMay.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
		lblMaNhaMay.setText("Mã nhà máy:");

		lblTenNCC.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
		lblTenNCC.setText("Tên nhà cung cấp:");

		lblDiaChi.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
		lblDiaChi.setText("Địa chỉ:");
		lblDiaChi.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

		lblSdt.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
		lblSdt.setText("Số điện thoại:");

		lblMaSoThue.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
		lblMaSoThue.setText("Mã số thuế");

		lblEmail.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
		lblEmail.setText("Email:");

		lblWeb.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
		lblWeb.setText("Website:");

		txtMaNhaMay.setPreferredSize(new java.awt.Dimension(64, 30));

		txtTenNCC.setPreferredSize(new java.awt.Dimension(64, 30));

		txtDiaChi.setPreferredSize(new java.awt.Dimension(64, 30));

		txtSdt.setPreferredSize(new java.awt.Dimension(64, 30));

		txtEmail.setPreferredSize(new java.awt.Dimension(64, 30));

		txtMaSoThue.setMinimumSize(new java.awt.Dimension(64, 30));
		txtMaSoThue.setPreferredSize(new java.awt.Dimension(64, 30));

		txtWeb.setPreferredSize(new java.awt.Dimension(64, 30));

		lblGhiChu.setFont(new java.awt.Font("Segoe UI Semibold", 0, 14)); // NOI18N
		lblGhiChu.setText("Ghi chú:");

		txtGhiChu.setColumns(20);
		txtGhiChu.setRows(5);
		jScrollPane2.setViewportView(txtGhiChu);

		btnHuy.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		btnHuy.setText("Clear");

		btnLuu.setBackground(new java.awt.Color(0, 51, 255));
		btnLuu.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		btnLuu.setForeground(new java.awt.Color(255, 255, 255));
		btnLuu.setText("Lưu thông tin");

		javax.swing.GroupLayout JpRightLayout = new javax.swing.GroupLayout(JpRight);
		JpRight.setLayout(JpRightLayout);
		JpRightLayout
				.setHorizontalGroup(
						JpRightLayout
								.createParallelGroup(
										javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JpRightLayout
										.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(lblTieuDe,
												javax.swing.GroupLayout.PREFERRED_SIZE, 239,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(219, 219, 219))
								.addGroup(JpRightLayout
										.createSequentialGroup().addGap(39, 39, 39).addGroup(JpRightLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(lblSdt).addComponent(lblMaNhaMay).addComponent(lblTenNCC)
												.addComponent(lblDiaChi).addComponent(
														lblEmail)
												.addComponent(lblMaSoThue)
												.addGroup(
														JpRightLayout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.TRAILING)
																.addComponent(lblGhiChu).addComponent(lblWeb)))
										.addGap(49, 49, 49)
										.addGroup(JpRightLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addGroup(JpRightLayout.createSequentialGroup().addComponent(btnHuy,
														javax.swing.GroupLayout.PREFERRED_SIZE, 142,
														javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE,
																244, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
														.addGroup(JpRightLayout.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING).addComponent(
																		txtMaNhaMay,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 432,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		txtTenNCC,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 432,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addGroup(JpRightLayout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING,
																		false)
																		.addComponent(txtEmail,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				432, Short.MAX_VALUE)
																		.addComponent(txtSdt,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE)
																		.addComponent(txtDiaChi,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				Short.MAX_VALUE))
																.addComponent(txtMaSoThue,
																		javax.swing.GroupLayout.Alignment.TRAILING,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 432,
																		javax.swing.GroupLayout.PREFERRED_SIZE)
																.addComponent(txtWeb,
																		javax.swing.GroupLayout.Alignment.TRAILING,
																		javax.swing.GroupLayout.PREFERRED_SIZE, 432,
																		javax.swing.GroupLayout.PREFERRED_SIZE))
														.addComponent(jScrollPane2,
																javax.swing.GroupLayout.PREFERRED_SIZE, 432,
																javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(78, Short.MAX_VALUE)));
		JpRightLayout
				.setVerticalGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpRightLayout.createSequentialGroup().addGap(25, 25, 25).addComponent(lblTieuDe)
								.addGap(26, 26, 26)
								.addGroup(JpRightLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(
												JpRightLayout.createSequentialGroup()
														.addComponent(lblGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE,
																35, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGap(224, 224, 224))
										.addGroup(JpRightLayout.createSequentialGroup().addGroup(JpRightLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(txtMaNhaMay, javax.swing.GroupLayout.PREFERRED_SIZE, 38,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblMaNhaMay)).addGap(18, 18, 18)
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(txtTenNCC, javax.swing.GroupLayout.PREFERRED_SIZE,
																40, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblTenNCC))
												.addGap(18, 18, 18)
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE,
																38, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblDiaChi))
												.addGap(18, 18, 18)
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(txtSdt, javax.swing.GroupLayout.PREFERRED_SIZE,
																38, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblSdt, javax.swing.GroupLayout.PREFERRED_SIZE,
																35, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGap(18, 18, 18)
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE,
																38, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblEmail))
												.addGap(18, 18, 18)
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(txtMaSoThue,
																javax.swing.GroupLayout.PREFERRED_SIZE, 38,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblMaSoThue))
												.addGap(18, 18, 18)
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(txtWeb, javax.swing.GroupLayout.PREFERRED_SIZE,
																39, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(lblWeb))
												.addGap(18, 18, 18)
												.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 106,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(79, 79, 79)
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(btnHuy, javax.swing.GroupLayout.PREFERRED_SIZE,
																30, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE,
																30, javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGap(90, 90, 90)))));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 717, Short.MAX_VALUE)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addComponent(JpRight, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE))));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 800, Short.MAX_VALUE)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(layout.createSequentialGroup().addContainerGap()
										.addComponent(JpRight, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addContainerGap())));

		pack();
	}

	private javax.swing.JPanel JpRight;
	private javax.swing.JButton btnHuy;
	private javax.swing.JButton btnLuu;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JLabel lblDiaChi;
	private javax.swing.JLabel lblEmail;
	private javax.swing.JLabel lblGhiChu;
	private javax.swing.JLabel lblMaNhaMay;
	private javax.swing.JLabel lblMaSoThue;
	private javax.swing.JLabel lblSdt;
	private javax.swing.JLabel lblTenNCC;
	private javax.swing.JLabel lblTieuDe;
	private javax.swing.JLabel lblWeb;
	private javax.swing.JTextField txtDiaChi;
	private javax.swing.JTextField txtEmail;
	private javax.swing.JTextArea txtGhiChu;
	private javax.swing.JTextField txtMaNhaMay;
	private javax.swing.JTextField txtMaSoThue;
	private javax.swing.JTextField txtSdt;
	private javax.swing.JTextField txtTenNCC;
	private javax.swing.JTextField txtWeb;

}