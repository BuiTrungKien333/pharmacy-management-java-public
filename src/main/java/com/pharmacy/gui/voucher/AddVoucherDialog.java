package com.pharmacy.gui.voucher;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import com.pharmacy.bus.VoucherBUS;
import com.pharmacy.entity.CustomerRank;
import com.pharmacy.entity.Voucher;
import com.toedter.calendar.JDateChooser;

public class AddVoucherDialog extends javax.swing.JDialog {
	
	

	private static final java.util.logging.Logger logger = java.util.logging.Logger
			.getLogger(AddVoucherDialog.class.getName());
	private JDateChooser dateFrom;
	private JDateChooser dateTo;
	private VoucherBUS voucherBus; 
	private VoucherGUI voucherGUI; 

	public AddVoucherDialog(java.awt.Frame parent, boolean modal, VoucherGUI voucherGUI) {
		super(parent, modal);
		
		this.voucherGUI = voucherGUI; 
		voucherBus = new VoucherBUS(); 
		initComponents();
		
		btnSave.addActionListener(e -> luuAction());
	}

	private void luuAction() {
		 Voucher voucher = getInfo ();
		 
		 if (voucher != null) {
			 if (voucherBus.insertVoucher(voucher)) {
				 JOptionPane.showMessageDialog(this, "Thêm voucher thành công!", "Thông báo", JOptionPane.DEFAULT_OPTION);
				 voucherGUI.addData();
				 this.dispose();
				 
			 }
			 else JOptionPane.showMessageDialog(this, "Thêm voucher thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			 
		 }
	}

	private Voucher getInfo() {
	    Voucher voucher = new Voucher();

	    CustomerRank customerRank = new CustomerRank();
	    customerRank.setId(cmbDieuKien.getSelectedIndex() + 1);
	    voucher.setCustomerRank(customerRank);

	    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    voucher.setDonToiThieu(Double.parseDouble(txtDonToiThieu.getText().trim()));
	    voucher.setGiamToiDa(Double.parseDouble(txtGiamToiDa.getText().trim()));
	    voucher.setGiaTri(Integer.parseInt(txtGiaTri.getText().replace("%", "").trim()));
	    
	   LocalDate ngay_bat_dau = LocalDate.parse(txtNgayBatDau.getText().trim(), fmt);
	   if (ngay_bat_dau.isBefore(LocalDate.now())) {
		   JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải lớn hơn hoặc bằng ngày hiện tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
		   return null; 
	   }
	   
	   LocalDate ngay_ket_thuc = LocalDate.parse(txtNgayKetThuc.getText().trim(), fmt);
	    if (ngay_ket_thuc.isBefore(ngay_bat_dau)) {
	    	JOptionPane.showMessageDialog(this, "Ngày kết thúc phải sau ngày bắt đầu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
	    	return null; 
	    }
	    voucher.setNgayBatDau(ngay_bat_dau);
	    voucher.setNgayKetThuc(ngay_ket_thuc);

	    voucher.setSoLuotDaSuDung(0);
	    voucher.setSoLuotSuDungToiDa(Integer.parseInt(txtSLSDTD.getText().trim()));
	    
	    DateTimeFormatter fm = DateTimeFormatter.ofPattern("ddMMyyyy");

	    return voucher;
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		lblTitle = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		lblNgayBatDau = new javax.swing.JLabel();
		txtNgayBatDau = new javax.swing.JTextField();
		lblNgayKetThuc = new javax.swing.JLabel();
		txtSLSDTD = new javax.swing.JTextField();
		lblDieuKien = new javax.swing.JLabel();
		txtNgayKetThuc = new javax.swing.JTextField();
		lblGiaTri = new javax.swing.JLabel();
		txtGiaTri = new javax.swing.JTextField();
		lblDonToiThieu = new javax.swing.JLabel();
		txtDonToiThieu = new javax.swing.JTextField();
		lblGiamToiDa = new javax.swing.JLabel();
		txtGiamToiDa = new javax.swing.JTextField();
		lblSLSDTD = new javax.swing.JLabel();
		cmbDieuKien = new javax.swing.JComboBox<>();
		btnSave = new javax.swing.JButton();
		btnClear = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setBackground(new java.awt.Color(255, 255, 255));
		setPreferredSize(new java.awt.Dimension(400, 700));

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		jPanel1.setPreferredSize(new java.awt.Dimension(400, 40));

		lblTitle.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
		lblTitle.setForeground(new java.awt.Color(51, 51, 255));
		lblTitle.setText("Thêm mới voucher");
		jPanel1.add(lblTitle);

		getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

		jPanel2.setBackground(new java.awt.Color(255, 255, 255));

		lblNgayBatDau.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblNgayBatDau.setText("Ngày bắt đầu");

		lblNgayKetThuc.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblNgayKetThuc.setText("Ngày kết thúc");
		
		dateFrom = new JDateChooser();
		dateTo = new JDateChooser();

		dateFrom.setDateFormatString("dd/MM/yyyy");
		dateTo.setDateFormatString("dd/MM/yyyy");

		dateFrom.setVisible(false);
		dateTo.setVisible(false);

		dateFrom.setBounds(txtNgayBatDau.getBounds());
		dateTo.setBounds(txtNgayKetThuc.getBounds());

		this.add(dateFrom);
		this.add(dateTo);

		txtNgayBatDau.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        dateFrom.setVisible(true);
		        dateFrom.getCalendarButton().doClick();
		    }
		});

		txtNgayKetThuc.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        dateTo.setVisible(true);
		        dateTo.getCalendarButton().doClick();
		    }
		});

		
		dateFrom.addPropertyChangeListener("date", evt -> {
		    if (dateFrom.getDate() != null) {
		        txtNgayBatDau.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateFrom.getDate()));
		        dateFrom.setVisible(false);
		    }
		});

		dateTo.addPropertyChangeListener("date", evt -> {
		    if (dateTo.getDate() != null) {
		        txtNgayKetThuc.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTo.getDate()));
		        dateTo.setVisible(false);
		    }
		});

		lblDieuKien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblDieuKien.setText("Điều kiện hạng thành viên");

		lblGiaTri.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblGiaTri.setText("Giá trị");

		lblDonToiThieu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblDonToiThieu.setText("Đơn tối thiểu");

		lblGiamToiDa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblGiamToiDa.setText("Giảm tối đa");

		lblSLSDTD.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblSLSDTD.setText("Số lượt sử dụng tối đa");

		cmbDieuKien.setModel(
				new javax.swing.DefaultComboBoxModel<>(new String[] { "Vô hạng", "Thành viên bạc", "Thành viên vàng"}));

		btnSave.setBackground(new java.awt.Color(51, 102, 255));
		btnSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnSave.setForeground(new java.awt.Color(255, 255, 255));
		btnSave.setText("Save");
		btnSave.setPreferredSize(new java.awt.Dimension(90, 30));

		btnClear.setBackground(new java.awt.Color(204, 204, 204));
		btnClear.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnClear.setForeground(new java.awt.Color(255, 255, 255));
		btnClear.setText("Clear");
		btnClear.setPreferredSize(new java.awt.Dimension(90, 30));

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addGap(18, 18, 18)
						.addGroup(jPanel2Layout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addGroup(jPanel2Layout.createSequentialGroup()
										.addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(32, 32, 32).addComponent(btnSave,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(
										jPanel2Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
												.addComponent(lblNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtNgayBatDau)
												.addComponent(lblNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE,
														90, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtNgayKetThuc, javax.swing.GroupLayout.DEFAULT_SIZE, 350,
														Short.MAX_VALUE)
												.addComponent(txtSLSDTD, javax.swing.GroupLayout.PREFERRED_SIZE, 233,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblGiaTri, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblDonToiThieu, javax.swing.GroupLayout.PREFERRED_SIZE,
														90, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtGiaTri, javax.swing.GroupLayout.PREFERRED_SIZE, 233,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtDonToiThieu, javax.swing.GroupLayout.PREFERRED_SIZE,
														233, javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblGiamToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, 90,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(txtGiamToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, 233,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblSLSDTD, javax.swing.GroupLayout.PREFERRED_SIZE, 140,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(lblDieuKien, javax.swing.GroupLayout.PREFERRED_SIZE, 171,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addComponent(cmbDieuKien, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)))
						.addContainerGap(32, Short.MAX_VALUE)));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup()
						.addComponent(lblNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(lblNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(lblGiaTri, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtGiaTri, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(lblDonToiThieu, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtDonToiThieu, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(lblGiamToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtGiamToiDa, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(lblSLSDTD, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtSLSDTD, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(lblDieuKien, javax.swing.GroupLayout.PREFERRED_SIZE, 25,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(cmbDieuKien, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
						.addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(15, 15, 15)));

		getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

		pack();
	}

	private javax.swing.JButton btnClear;
	private javax.swing.JButton btnSave;
	private javax.swing.JComboBox<String> cmbDieuKien;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JLabel lblDieuKien;
	private javax.swing.JLabel lblDonToiThieu;
	private javax.swing.JLabel lblGiaTri;
	private javax.swing.JLabel lblGiamToiDa;
	private javax.swing.JLabel lblNgayBatDau;
	private javax.swing.JLabel lblNgayKetThuc;
	private javax.swing.JLabel lblSLSDTD;
	private javax.swing.JLabel lblTitle;
	private javax.swing.JTextField txtDonToiThieu;
	private javax.swing.JTextField txtGiaTri;
	private javax.swing.JTextField txtGiamToiDa;
	private javax.swing.JTextField txtNgayBatDau;
	private javax.swing.JTextField txtNgayKetThuc;
	private javax.swing.JTextField txtSLSDTD;
}