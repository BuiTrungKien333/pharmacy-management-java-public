package com.pharmacy.gui.supplier;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pharmacy.bus.NhaCungCapBUS;
import com.pharmacy.entity.NhaCungCap;

public class SupplierGUI extends javax.swing.JPanel {

	private final NhaCungCapBUS supplierBUS;
	
	private Timer searchTimer;
	
	private List<NhaCungCap> list;

	public SupplierGUI() {
		supplierBUS = new NhaCungCapBUS();
		list = supplierBUS.getAllInfoSupplier();

		initComponents();
		init();
		decorateTable(jTable1);
		loadDataFromSQL();


		initSearch();
		initEvent();
	}

	
	private void initEvent() {

		jTable1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
				int row = jTable1.getSelectedRow();
				if (row != -1) {
					String so_dien_thoai = model.getValueAt(row, 3) + "";
					NhaCungCap supplier = supplierBUS.getNhaCungCapByPhone(so_dien_thoai);
					txtDiaChi.setText(supplier.getDiaChi());
					txtEmail.setText(supplier.getEmail());
					txtGhiChu.setText(supplier.getGhiChu());
					txtMaNCC.setText(supplier.getId() + "");
					txtMaNhaMay.setText(supplier.getMaNhaMay());
					txtMaSoThue.setText(supplier.getMaSoThue());
					txtSdt.setText(supplier.getSoDienThoai());
					txtTenNCC.setText(supplier.getTenNhaCungCap());
					txtWeb.setText(supplier.getWebsite());
				}
			}
		});

		btnCapNhat.addActionListener(e -> updateInfor());
		btnThemNCC.addActionListener(e -> insertSuplier());

	}

	
	private void insertSuplier() {
		AddSupplierDialog form = new AddSupplierDialog(null, true, this);
		form.setLocationRelativeTo(null);
		form.setVisible(true);
	}

	private void updateInfor() {
		try {
			NhaCungCap supplier = new NhaCungCap();
		
			supplier.setDiaChi(txtDiaChi.getText());
			supplier.setEmail(txtEmail.getText());
			supplier.setGhiChu(txtGhiChu.getText());
			supplier.setMaNhaMay(txtMaNhaMay.getText());
			supplier.setMaSoThue(txtMaSoThue.getText());
			supplier.setSoDienThoai(txtSdt.getText());
			supplier.setTenNhaCungCap(txtTenNCC.getText());
			supplier.setWebsite(txtWeb.getText());
			supplier.setId(Integer.parseInt(txtMaNCC.getText()));
			if (supplierBUS.updateSupplier(supplier)) {
				JOptionPane.showMessageDialog(this, "Update thông tin nhà cung cấp thành công", "Thông báo",
						JOptionPane.DEFAULT_OPTION);
				loadDataFromSQL();
			} else
				JOptionPane.showMessageDialog(this, "Update thông tin nhà cung cấp không thành công!", "Lỗi",
						JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initSearch() {

		searchTimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timKiemLive();
			}

			private void timKiemLive() {
				String timKiemText = txtTimKiem.getText().trim();
				List<NhaCungCap> dsncc;
				DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
				model.setRowCount(0);

				dsncc = supplierBUS.findSupplierByFactoryCode(timKiemText);

				if (timKiemText.isEmpty()) {
					loadDataFromSQL();
				}

				dsncc.stream().forEach(ncc -> model.addRow(ncc.getObject()));
			}

		});
		searchTimer.setRepeats(false);
		txtTimKiem.getDocument().addDocumentListener(new DocumentListener() {
			private void triggerSearch() {
				searchTimer.restart();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				triggerSearch();

			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				triggerSearch();

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				triggerSearch();

			}
		});

	}

	private void decorateTable(JTable tblHoaDon) {

		tblHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tblHoaDon.setRowHeight(35);
		tblHoaDon.setGridColor(new Color(220, 220, 220));
		tblHoaDon.setSelectionBackground(new Color(102, 178, 255));
		tblHoaDon.setSelectionForeground(Color.BLACK);

		JTableHeader header = tblHoaDon.getTableHeader();
		header.setFont(new Font("Segoe UI", Font.BOLD, 15));
		header.setBackground(new Color(229, 229, 229));
		header.setForeground(Color.BLACK);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		tblHoaDon.setShowHorizontalLines(true);
		tblHoaDon.setShowVerticalLines(true);
		for (int i = 0; i < tblHoaDon.getColumnCount(); i++) {
			tblHoaDon.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
	}

	public void loadDataFromSQL() {
		list = supplierBUS.getAllInfoSupplier();
		DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
		model.setRowCount(0);
		list.stream().forEach(s -> model.addRow(s.getObject()));

		jTable1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && jTable1.getSelectedRow() != -1) {
					int selectedRow = jTable1.getSelectedRow();
					DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

					String soDienThoai = model.getValueAt(selectedRow, 3).toString().trim();

					NhaCungCap supli = supplierBUS.getNhaCungCapByPhone(soDienThoai);
					if (supli == null) {
						JOptionPane.showMessageDialog(jTable1, "Không tìm thấy thông tin nhà cung cấp!", "Lỗi",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

				}
			}

		});
	}

	private void initComponents() {

		JpRight = new javax.swing.JPanel();
		lblTieuDe = new javax.swing.JLabel();
		lblMaNCC = new javax.swing.JLabel();
		lblMaNhaMay = new javax.swing.JLabel();
		lblTenNCC = new javax.swing.JLabel();
		lblDiaChi = new javax.swing.JLabel();
		lblSdt = new javax.swing.JLabel();
		lblMaSoThue = new javax.swing.JLabel();
		lblEmail = new javax.swing.JLabel();
		lblWeb = new javax.swing.JLabel();
		txtMaNhaMay = new javax.swing.JTextField();
		txtTenNCC = new javax.swing.JTextArea();
		txtDiaChi = new javax.swing.JTextArea();
		txtSdt = new javax.swing.JTextField();
		txtMaSoThue = new javax.swing.JTextField();
		txtEmail = new javax.swing.JTextField();
		txtMaNCC = new javax.swing.JTextField();
		txtWeb = new javax.swing.JTextField();
		lblGhiChu = new javax.swing.JLabel();
		jScrollPane2 = new javax.swing.JScrollPane();
		txtGhiChu = new javax.swing.JTextArea();
		btnCapNhat = new javax.swing.JButton();
		JpLeft = new javax.swing.JPanel();
		JpNav = new javax.swing.JPanel();
		txtTimKiem = new javax.swing.JTextField();
		btnThemNCC = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTable1 = new javax.swing.JTable();

		JpRight.setBackground(Color.white);

		lblTieuDe.setFont(new java.awt.Font("Segoe UI Semibold", Font.BOLD, 18));
		lblTieuDe.setForeground(new java.awt.Color(0, 0, 255));
		lblTieuDe.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblTieuDe.setText("Thông tin chi tiết ");

		Font boldFont = new Font("Segoe UI", Font.BOLD, 13);
		JLabel[] labels = { lblDiaChi, lblEmail, lblGhiChu, lblMaNCC, lblMaNhaMay, lblMaSoThue, lblSdt, lblTenNCC,
				lblWeb };

		for (JLabel label : labels) {
			label.setFont(boldFont);
		}

		lblMaNCC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblMaNCC.setText("Mã nhà cung cấp:");

		lblMaNhaMay.setText("Mã nhà máy:");

		lblTenNCC.setText("Tên nhà cung cấp:");

		lblDiaChi.setText("Địa chỉ:");
		lblDiaChi.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

		lblSdt.setText("Số điện thoại:");

		lblMaSoThue.setText("Mã số thuế");

		lblEmail.setText("Email:");

		lblWeb.setText("Website:");

		txtMaNhaMay.setPreferredSize(new java.awt.Dimension(64, 30));

		txtTenNCC.setPreferredSize(new java.awt.Dimension(64, 30));
		txtTenNCC.setColumns(20);
		txtTenNCC.setRows(2);
		txtTenNCC.setLineWrap(true);
		txtTenNCC.setWrapStyleWord(true);
		txtTenNCC.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		txtDiaChi.setPreferredSize(new java.awt.Dimension(64, 30));
		txtDiaChi.setColumns(20);
		txtDiaChi.setRows(2);
		txtDiaChi.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		txtDiaChi.setLineWrap(true);
		txtDiaChi.setWrapStyleWord(true);

		txtSdt.setPreferredSize(new java.awt.Dimension(64, 30));

		txtMaSoThue.setPreferredSize(new java.awt.Dimension(64, 30));

		txtEmail.setPreferredSize(new java.awt.Dimension(64, 30));

		txtMaNCC.setEditable(false);
		txtMaNCC.setPreferredSize(new java.awt.Dimension(64, 30));

		txtWeb.setPreferredSize(new java.awt.Dimension(64, 30));

		lblGhiChu.setText("Ghi chú:");

		txtGhiChu.setColumns(20);
		txtGhiChu.setRows(10);
		txtGhiChu.setLineWrap(true);
		txtGhiChu.setWrapStyleWord(true);
		jScrollPane2.setViewportView(txtGhiChu);

		btnCapNhat.setBackground(new java.awt.Color(0, 51, 255));
		btnCapNhat.setFont(new java.awt.Font("Segoe UI", 1, 14));
		btnCapNhat.setForeground(new java.awt.Color(255, 255, 255));
		btnCapNhat.setText("Cập nhật thông tin");

		javax.swing.GroupLayout JpRightLayout = new javax.swing.GroupLayout(JpRight);
		JpRight.setLayout(JpRightLayout);
		JpRightLayout.setHorizontalGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpRightLayout.createSequentialGroup().addGroup(JpRightLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(JpRightLayout.createSequentialGroup().addGap(17, 17, 17).addGroup(JpRightLayout
								.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(lblMaSoThue)
								.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
										.addComponent(lblTenNCC)
										.addComponent(lblDiaChi, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(lblSdt, javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(JpRightLayout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(lblMaNhaMay).addComponent(lblMaNCC)))
								.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(lblWeb).addComponent(lblEmail)
										.addComponent(lblGhiChu, javax.swing.GroupLayout.Alignment.TRAILING)))
								.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(JpRightLayout.createSequentialGroup().addGap(14, 14, 14)
												.addGroup(JpRightLayout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(txtMaNhaMay,
																javax.swing.GroupLayout.PREFERRED_SIZE, 230,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(txtTenNCC, javax.swing.GroupLayout.PREFERRED_SIZE,
																230, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE,
																230, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(txtSdt, javax.swing.GroupLayout.PREFERRED_SIZE,
																230, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE,
																230, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(txtMaSoThue,
																javax.swing.GroupLayout.PREFERRED_SIZE, 230,
																javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(txtWeb, javax.swing.GroupLayout.PREFERRED_SIZE,
																230, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addComponent(txtMaNCC, javax.swing.GroupLayout.PREFERRED_SIZE,
																230, javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
												JpRightLayout.createSequentialGroup()
														.addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED)
														.addComponent(jScrollPane2,
																javax.swing.GroupLayout.PREFERRED_SIZE, 230,
																javax.swing.GroupLayout.PREFERRED_SIZE))))
						.addGroup(JpRightLayout.createSequentialGroup().addGap(92, 92, 92).addComponent(lblTieuDe))
						.addGroup(JpRightLayout.createSequentialGroup().addGap(80, 80, 80).addComponent(btnCapNhat,
								javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		JpRightLayout.setVerticalGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpRightLayout.createSequentialGroup().addGap(30, 30, 30).addComponent(lblTieuDe)
						.addGap(18, 18, 18)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblMaNCC).addComponent(txtMaNCC, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(21, 21, 21)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblMaNhaMay)
								.addComponent(txtMaNhaMay, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(21, 21, 21)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtTenNCC, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTenNCC))
						.addGap(21, 21, 21)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblDiaChi))
						.addGap(21, 21, 21)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(txtSdt, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblSdt, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(21, 21, 21)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtMaSoThue, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMaSoThue))
						.addGap(21, 21, 21)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblEmail))
						.addGap(21, 21, 21)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtWeb, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblWeb))
						.addGap(21, 21, 21)
						.addGroup(JpRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(JpRightLayout.createSequentialGroup().addGap(22, 22, 22).addComponent(
										lblGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(27, 27, 27).addComponent(btnCapNhat).addContainerGap(64, Short.MAX_VALUE)));

		JpLeft.setBackground(new java.awt.Color(255, 255, 255));

		txtTimKiem.setPreferredSize(new java.awt.Dimension(64, 30));

		txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã nhà máy cần tìm...");
		txtTimKiem.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/search.svg", 0.4f));

		btnThemNCC.setBackground(new java.awt.Color(51, 51, 255));
		btnThemNCC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnThemNCC.setForeground(new java.awt.Color(255, 255, 255));
		btnThemNCC.setText("Thêm mới");
		btnThemNCC.setPreferredSize(new java.awt.Dimension(85, 30));

		javax.swing.GroupLayout JpNavLayout = new javax.swing.GroupLayout(JpNav);
		JpNav.setLayout(JpNavLayout);
		JpNavLayout.setHorizontalGroup(JpNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpNavLayout.createSequentialGroup().addGap(15, 15, 15)
						.addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 568,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
						.addComponent(btnThemNCC, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(33, 33, 33)));
		JpNavLayout.setVerticalGroup(JpNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpNavLayout.createSequentialGroup().addGap(15, 15, 15)
						.addGroup(JpNavLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnThemNCC, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(16, Short.MAX_VALUE)));

		DefaultTableModel model = new DefaultTableModel(new Object[][] {},
				new String[] { "Mã nhà máy", "Tên nhà cung cấp", "Địa chỉ", "Số điện thoại", "Email" }) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		jTable1.setModel(model);
		jTable1.setRowHeight(40);
		jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		TableColumnModel columnModel = jTable1.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setResizable(false);
		}

		jTable1.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
		jTable1.getTableHeader().setBackground(new Color(230, 230, 230));
		jTable1.getTableHeader().setForeground(Color.BLACK);
		((DefaultTableCellRenderer) jTable1.getTableHeader().getDefaultRenderer())
				.setHorizontalAlignment(JLabel.CENTER);

		jScrollPane1.setViewportView(jTable1);

		javax.swing.GroupLayout JpLeftLayout = new javax.swing.GroupLayout(JpLeft);
		JpLeft.setLayout(JpLeftLayout);
		JpLeftLayout.setHorizontalGroup(JpLeftLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(JpNav,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(jScrollPane1));
		JpLeftLayout.setVerticalGroup(JpLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpLeftLayout.createSequentialGroup()
						.addComponent(JpNav, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(15, 15, 15)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 611, Short.MAX_VALUE)));

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				layout.createSequentialGroup().addContainerGap()
						.addComponent(JpLeft, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 0, 0).addComponent(JpRight, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addComponent(JpLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE)
				.addComponent(JpRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
						Short.MAX_VALUE));
	}

	private javax.swing.JPanel JpLeft;
	private javax.swing.JPanel JpNav;
	private javax.swing.JPanel JpRight;
	private javax.swing.JButton btnCapNhat;
	private javax.swing.JButton btnThemNCC;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTable jTable1;
	private javax.swing.JLabel lblDiaChi;
	private javax.swing.JLabel lblEmail;
	private javax.swing.JLabel lblGhiChu;
	private javax.swing.JLabel lblMaNCC;
	private javax.swing.JLabel lblMaNhaMay;
	private javax.swing.JLabel lblMaSoThue;
	private javax.swing.JLabel lblSdt;
	private javax.swing.JLabel lblTenNCC;
	private javax.swing.JLabel lblTieuDe;
	private javax.swing.JLabel lblWeb;
	private javax.swing.JTextArea txtDiaChi;
	private javax.swing.JTextField txtEmail;
	private javax.swing.JTextArea txtGhiChu;
	private javax.swing.JTextField txtMaNCC;
	private javax.swing.JTextField txtMaNhaMay;
	private javax.swing.JTextField txtMaSoThue;
	private javax.swing.JTextField txtSdt;
	private javax.swing.JTextArea txtTenNCC;
	private javax.swing.JTextField txtTimKiem;
	private javax.swing.JTextField txtWeb;

	private void init() {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int widthLeft = (int) (screen.width * 0.55);
		int heightLeft = screen.height;
		JpLeft.setPreferredSize(new Dimension(widthLeft, heightLeft));

		int widthRight = (int) (screen.width * 0.45);
		int heightRight = screen.height;
		JpRight.setPreferredSize(new Dimension(widthRight, heightRight));

	}

}