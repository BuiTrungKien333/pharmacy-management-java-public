package com.pharmacy.gui.batch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.pharmacy.bus.Auth;
import com.pharmacy.bus.BatchBUS;
import com.pharmacy.bus.NhaCungCapBUS;
import com.pharmacy.entity.Batch;
import com.pharmacy.entity.BatchStatus;
import com.pharmacy.entity.Employee;
import com.pharmacy.entity.NhaCungCap;
import com.pharmacy.entity.Product;
import com.pharmacy.utils.FormatUtil;
import com.pharmacy.utils.HelperImageIcon;

public class AddBatchDialog extends javax.swing.JDialog {

	private BatchGUI shipmentGUI;

	private BatchBUS shipmentBUS;

	private NhaCungCapBUS nhaCungCapBUS;

	private Product product;

	public AddBatchDialog(java.awt.Frame parent, boolean modal, BatchGUI shipmentGUI, BatchBUS shipmentBUS,
			NhaCungCapBUS nhaCungCapBUS) {
		super(parent, modal);

		this.shipmentBUS = shipmentBUS;

		this.shipmentGUI = shipmentGUI;

		this.nhaCungCapBUS = nhaCungCapBUS;

		this.product = new Product();

		initComponents();

		initEdit();

		initEvent();

		SwingUtilities.invokeLater(() -> txtBarcode.requestFocusInWindow());
	}

	private void initEvent() {

		btnSave.addActionListener(e -> saveShipment());

		btnClear.addActionListener(e -> clearData());

		txtGiaNhap.addActionListener(e -> {
			txtGiaBan.selectAll();
			txtGiaBan.requestFocus();
		});

		txtGiaNhap.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				txtThanhTien.setText(
						FormatUtil.formatVND(shipmentBUS.autoSetThanhTien(txtSoLuong.getText(), txtGiaNhap.getText())));

				suggestGiaBan();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtThanhTien.setText(
						FormatUtil.formatVND(shipmentBUS.autoSetThanhTien(txtSoLuong.getText(), txtGiaNhap.getText())));

				suggestGiaBan();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		txtSoLuong.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				txtThanhTien.setText(
						FormatUtil.formatVND(shipmentBUS.autoSetThanhTien(txtSoLuong.getText(), txtGiaNhap.getText())));
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtThanhTien.setText(
						FormatUtil.formatVND(shipmentBUS.autoSetThanhTien(txtSoLuong.getText(), txtGiaNhap.getText())));
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		txtBarcode.addActionListener(e -> {
			String barcode = txtBarcode.getText().trim();
			if (barcode.isEmpty())
				return;

			try {
				Product prod = shipmentBUS.getProdByBarcode(barcode);
				this.product = prod;
				showProdChosen();
			} catch (RuntimeException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage());
				txtBarcode.selectAll();
				txtBarcode.requestFocus();
			}
		});
	}

	private void suggestGiaBan() {
		try {
			if (product == null)
				return;

			int prodType = product.getLoaiSanPham().getId();
			double price = Double.parseDouble(txtGiaNhap.getText());

			if (prodType == 1)
				price *= 1.4;
			else if (prodType == 2)
				price *= 1.6;
			else
				price *= 2;

			txtGiaBan.setText(String.format("%.0f", price));
		} catch (NumberFormatException e) {
		}
	}

	private void showProdChosen() {
		txtMaThuoc.setText(String.format("%06d - %s", product.getMaSanPham(), product.getTenSanPham()));
		lblIcon.setIcon(HelperImageIcon.scaleIcon(product.getAvatarUrl(), 190, 190));
		txtDonViTinh.setText(product.getDonViTinh());
	}

	private void saveShipment() {
		try {
			Batch shipment = new Batch();

			if (txtMaThuoc.getText().isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng quét mã barcode để thêm lô mới.");
				txtBarcode.requestFocus();
				return;
			}
			shipment.setProduct(product);

			shipment.setNhaCungCap((NhaCungCap) cmbNhaCungCap.getSelectedItem());

			LocalDate ngaySX = FormatUtil.convertStringToDate(txtNgaySX.getText()); // dd/MM/yyyy
			LocalDate hanSD = FormatUtil.convertStringToDate(txtHanSuDung.getText()); // dd/MM/yyyy

			shipmentBUS.checkNgay(ngaySX, hanSD);

			long months = ChronoUnit.MONTHS.between(LocalDate.now(), hanSD);
			if (months < 1) {
				JOptionPane.showMessageDialog(this, "Không thể nhập lô có hạn sử dụng dưới 30 ngày.");
				txtHanSuDung.requestFocus();
				txtHanSuDung.selectAll();
				return;
			} else if (months <= 18) {
				int confirm = JOptionPane.showConfirmDialog(this, "Hạn sử dụng của lô thuốc này chỉ còn: " + months
						+ " tháng.\n Bạn có chắc chắn muốn nhập lô ?");
				if (confirm != JOptionPane.YES_OPTION)
					return;
			}

			shipment.setNgaySanXuat(ngaySX);
			shipment.setHanSuDung(hanSD);

			shipment.setEmployee(new Employee(Auth.getCurrentUser().getMaNhanVien()));

			try {
				shipment.setSoLuongNhap(Integer.parseInt(txtSoLuong.getText().trim()));

				shipment.setSoLuongCon(shipment.getSoLuongNhap());

				shipment.setGiaNhap(Double.parseDouble(txtGiaNhap.getText().trim()));

				shipment.setGiaBan(Double.parseDouble(txtGiaBan.getText().trim()));

			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Các trường số lượng và giá phải là số hợp lệ!");
			}

			shipmentBUS.checkGiaAndSoLuong(shipment.getGiaNhap(), shipment.getGiaBan(), shipment.getSoLuongCon(),
					shipment.getSoLuongNhap());

			shipment.setNgayNhap(LocalDateTime.now());

			shipment.setShipmentStatus(new BatchStatus(1, "Đang lưu hành"));

			shipment.setThanhTienNhap();

			shipmentBUS.addShipment(shipment);

			shipmentGUI.refreshDataShipment();

			JOptionPane.showMessageDialog(this, "Thêm lô mới thành công!", "Success", JOptionPane.INFORMATION_MESSAGE);

			this.dispose();
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void initEdit() {
		txtNhanVien.setEditable(false);
		txtNhanVien.setText(String.format("%s - %s", Auth.getCurrentUser().getMaNhanVien(),
				Auth.getCurrentUser().getTenNhanVien()));
		txtThanhTien.setEditable(false);
		txtNgayNhap.setEditable(false);
		txtMaThuoc.setEditable(false);
		txtDonViTinh.setEditable(false);
		txtNgayNhap.setText(FormatUtil.formatDate(LocalDate.now()));
	}

	public void clearData() {
		txtBarcode.setText("");
		txtGiaNhap.setText("");
		txtGiaBan.setText("");
		txtHanSuDung.setText("");
		txtNgayNhap.setText(FormatUtil.formatDate(LocalDate.now()));
		txtNgaySX.setText("");
		txtNhanVien.setText(String.format("%s - %s", Auth.getCurrentUser().getMaNhanVien(),
				Auth.getCurrentUser().getTenNhanVien()));
		txtSoLuong.setText("");
		txtDonViTinh.setText("");
		txtThanhTien.setText("");
		txtMaThuoc.setText("");

		cmbNhaCungCap.removeAllItems();
		nhaCungCapBUS.getListNhaCungCap().forEach(ncc -> cmbNhaCungCap.addItem(ncc));
		cmbNhaCungCap.setSelectedIndex(0);

		lblIcon.setIcon(HelperImageIcon.scaleIcon("/images/prod/default.png", 190, 190));
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		lblTitle = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		btnClear = new javax.swing.JButton();
		btnSave = new javax.swing.JButton();
		jPanel6 = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		lblBarcode = new javax.swing.JLabel();
		lblMaNCC = new javax.swing.JLabel();
		lblNgaySX = new javax.swing.JLabel();
		txtNhanVien = new javax.swing.JTextField();
		lblHanSuDung = new javax.swing.JLabel();
		txtNgaySX = new javax.swing.JTextField();
		lblNgayNhap = new javax.swing.JLabel();
		txtHanSuDung = new javax.swing.JTextField();
		lblNhanVien = new javax.swing.JLabel();
		txtNgayNhap = new javax.swing.JTextField();
		txtBarcode = new javax.swing.JTextField();
		cmbNhaCungCap = new javax.swing.JComboBox<>();
		lblMaNCC1 = new javax.swing.JLabel();
		txtMaThuoc = new javax.swing.JTextField();
		jPanel5 = new javax.swing.JPanel();
		lblSoLuong = new javax.swing.JLabel();
		txtSoLuong = new javax.swing.JTextField();
		lblGiaNhap = new javax.swing.JLabel();
		txtGiaBan = new javax.swing.JTextField();
		lblThanhTien = new javax.swing.JLabel();
		txtGiaNhap = new javax.swing.JTextField();
		lblIcon = new javax.swing.JLabel();
		txtDonViTinh = new javax.swing.JTextField();
		lblThanhTien1 = new javax.swing.JLabel();
		txtThanhTien = new javax.swing.JTextField();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		jPanel1.setPreferredSize(new java.awt.Dimension(765, 50));

		lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		lblTitle.setForeground(new java.awt.Color(51, 51, 255));
		lblTitle.setText("Thêm mới lô thuốc");
		jPanel1.add(lblTitle);

		getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

		jPanel2.setLayout(new java.awt.BorderLayout());

		jPanel3.setBackground(new java.awt.Color(255, 255, 255));
		jPanel3.setPreferredSize(new java.awt.Dimension(765, 50));

		btnClear.setBackground(new java.awt.Color(204, 204, 204));
		btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnClear.setForeground(new java.awt.Color(255, 255, 255));
		btnClear.setText("Clear");
		btnClear.setPreferredSize(new java.awt.Dimension(100, 30));
		jPanel3.add(btnClear);

		btnSave.setBackground(new java.awt.Color(51, 153, 0));
		btnSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnSave.setForeground(new java.awt.Color(255, 255, 255));
		btnSave.setText("Save");
		btnSave.setPreferredSize(new java.awt.Dimension(100, 30));
		jPanel3.add(btnSave);

		jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_END);

		jPanel6.setBackground(new java.awt.Color(255, 255, 255));
		jPanel6.setLayout(new java.awt.GridLayout(1, 2));

		jPanel4.setBackground(new java.awt.Color(255, 255, 255));

		lblBarcode.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblBarcode.setText("Barcode");

		lblMaNCC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblMaNCC.setText("Nhà cung cấp");

		lblNgaySX.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblNgaySX.setText("Ngày sản xuất");

		lblHanSuDung.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblHanSuDung.setText("Hạn sử dụng");

		lblNgayNhap.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblNgayNhap.setText("Ngày nhập");

		lblNhanVien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblNhanVien.setText("Nhân viên nhập");

		nhaCungCapBUS.getListNhaCungCap().forEach(ncc -> cmbNhaCungCap.addItem(ncc));

		lblMaNCC1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblMaNCC1.setText("Mã thuốc - Tên thuốc");

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup().addGap(26, 26, 26).addGroup(jPanel4Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
						.addComponent(txtMaThuoc, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
						.addComponent(lblMaNCC1, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(lblBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMaNCC, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNgaySX, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtNhanVien, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addComponent(lblHanSuDung, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtNgaySX, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addComponent(lblNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtHanSuDung, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addComponent(lblNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtNgayNhap, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addComponent(txtBarcode, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
								.addComponent(cmbNhaCungCap, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addContainerGap(141, Short.MAX_VALUE)));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup().addContainerGap().addComponent(lblBarcode)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18).addComponent(lblMaNCC1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(txtMaThuoc, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
						.addComponent(lblMaNCC).addGap(12, 12, 12)
						.addComponent(cmbNhaCungCap, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(lblNgaySX)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtNgaySX, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(lblHanSuDung).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtHanSuDung, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(lblNgayNhap)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(lblNhanVien)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(txtNhanVien,
								javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(30, 30, 30)));

		jPanel6.add(jPanel4);

		jPanel5.setBackground(new java.awt.Color(255, 255, 255));

		lblSoLuong.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblSoLuong.setText("Số lượng nhập");

		lblGiaNhap.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblGiaNhap.setText("Giá nhập");

		lblThanhTien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblThanhTien.setText("Thành tiền");

		lblIcon.setIcon(HelperImageIcon.scaleIcon("/images/prod/default.png", 190, 190));
		lblIcon.setPreferredSize(new java.awt.Dimension(190, 190));

		lblThanhTien1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblThanhTien1.setText("Giá bán ra");

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel5Layout.createSequentialGroup().addContainerGap().addGroup(jPanel5Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(lblThanhTien1, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(txtThanhTien, javax.swing.GroupLayout.PREFERRED_SIZE, 240,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, 240,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(lblSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGroup(jPanel5Layout.createSequentialGroup()
												.addComponent(txtSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 240,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(18, 18, 18).addComponent(txtDonViTinh,
														javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(lblGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(lblThanhTien, javax.swing.GroupLayout.PREFERRED_SIZE, 133,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 240,
												javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addContainerGap(139, Short.MAX_VALUE)));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel5Layout.createSequentialGroup().addContainerGap().addComponent(lblSoLuong)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(txtDonViTinh)
								.addComponent(txtSoLuong, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE))
						.addGap(18, 18, 18).addComponent(lblGiaNhap)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(12, 12, 12).addComponent(lblThanhTien)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(txtThanhTien, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(12, 12, 12).addComponent(lblThanhTien1)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(txtGiaBan, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		jPanel6.add(jPanel5);

		jPanel2.add(jPanel6, java.awt.BorderLayout.CENTER);

		getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

		pack();
	}

	private javax.swing.JButton btnClear;
	private javax.swing.JButton btnSave;
	private javax.swing.JComboBox<NhaCungCap> cmbNhaCungCap;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JLabel lblBarcode;
	private javax.swing.JLabel lblGiaNhap;
	private javax.swing.JLabel lblHanSuDung;
	private javax.swing.JLabel lblIcon;
	private javax.swing.JLabel lblMaNCC;
	private javax.swing.JLabel lblMaNCC1;
	private javax.swing.JLabel lblNgayNhap;
	private javax.swing.JLabel lblNgaySX;
	private javax.swing.JLabel lblNhanVien;
	private javax.swing.JLabel lblSoLuong;
	private javax.swing.JLabel lblThanhTien;
	private javax.swing.JLabel lblThanhTien1;
	private javax.swing.JLabel lblTitle;
	private javax.swing.JTextField txtBarcode;
	private javax.swing.JTextField txtDonViTinh;
	private javax.swing.JTextField txtGiaBan;
	private javax.swing.JTextField txtGiaNhap;
	private javax.swing.JTextField txtHanSuDung;
	private javax.swing.JTextField txtMaThuoc;
	private javax.swing.JTextField txtNgayNhap;
	private javax.swing.JTextField txtNgaySX;
	private javax.swing.JTextField txtNhanVien;
	private javax.swing.JTextField txtSoLuong;
	private javax.swing.JTextField txtThanhTien;

}
