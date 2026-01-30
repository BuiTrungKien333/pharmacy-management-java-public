package com.pharmacy.gui.cus;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pharmacy.bus.CustomerBUS;
import com.pharmacy.bus.InvoiceBUS;
import com.pharmacy.config.Translator;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.Invoice;

public class CustomerGUI extends javax.swing.JPanel {

	private static final long serialVersionUID = 1L;
	
	private final CustomerBUS customerBUS;
	
	private final InvoiceBUS invoiceBUS;
	
	private List<Customer> ds;
	
	private Timer searchTimer;

	public CustomerGUI() {
		customerBUS = new CustomerBUS();
		
		invoiceBUS = new InvoiceBUS();
		
		ds = new ArrayList<Customer>();

		initComponents();
		
		Translator.getInstance().addLanguageChangeListener(locale -> {
			SwingUtilities.invokeLater(this::updateTexts);
		});
		
		decorateTable(tbl_cus);
		
		decorateTable(tbl_history);
		
		updateTexts();
		
		loadData(ds);
		
		initSearch();
		
		initEvent();
	}
	
	private void initEvent() {
		tbl_cus.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int row = tbl_cus.getSelectedRow();
				if (row != -1) {
					DefaultTableModel model = (DefaultTableModel) tbl_cus.getModel();
					int maKH = (int) model.getValueAt(row, 0);
					List<Invoice> dshd = invoiceBUS.getInvoiceByCustomerCode(maKH);
					int tong_so_hd = dshd.size();
					double tong_tien_da_mua = getTongTienDaMua(dshd);
					NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

					txtMaKH.setText(model.getValueAt(row, 0) + "");
					txtName.setText(model.getValueAt(row, 1) + "");
					txtPhone.setText(model.getValueAt(row, 2) + "");
					txtScore.setText(model.getValueAt(row, 3) + "");
					txtRank.setText(model.getValueAt(row, 4) + "");
					txtQty.setText(tong_so_hd + "");
					txtMoney.setText(formatter.format(tong_tien_da_mua));

					initHistoryTable(dshd);
				}
			}

			private void initHistoryTable(List<Invoice> dshd) {
				DefaultTableModel model = (DefaultTableModel) tbl_history.getModel();
				model.setRowCount(0);
				NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
				DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				for (Invoice hd : dshd) {
					model.addRow(new Object[] { hd.getMaHoaDon(), hd.isDaTra() ? "Trả" : "Mua",
							hd.getNgayLap().format(df), formatter.format(hd.getTongTienHang()) });
				}
			}
			
			private double getTongTienDaMua(List<Invoice> dshd) {
				return dshd.stream().mapToDouble(hd -> hd.getTongTienHang()).sum();
			}
		});
	}

	private void initSearch() {
		searchTimer = new Timer(100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				timKiemLive();
			}

			private void timKiemLive() {
				String phoneNum = txtSearch.getText().trim();
				List<Customer> dskh;
				DefaultTableModel model = (DefaultTableModel) tbl_cus.getModel();
				model.setRowCount(0);

				dskh = customerBUS.findCustomerByPhoneNum(phoneNum);

				if (phoneNum.isEmpty())
					loadData(ds);

				dskh.stream().forEach(cus -> model.addRow(cus.getObject()));
			}
		});
		searchTimer.setRepeats(false);
		txtSearch.getDocument().addDocumentListener(new DocumentListener() {
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
			}
		});

	}

	private void loadData(List<Customer> ds) {
		ds = customerBUS.getAllCustomer();
		DefaultTableModel tableModel = (DefaultTableModel) tbl_cus.getModel();
		tableModel.setRowCount(0);
		ds.stream().forEach(cus -> tableModel.addRow(cus.getObject()));
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

	private void updateTexts() {
		Translator bundle = Translator.getInstance();
		jLabel1.setText(bundle.getString("cus.title.info"));

		jLabel2.setText(bundle.getString("cus.title.detail"));
		lblMaKH.setText(bundle.getString("cus.lbl.id"));
		lblName.setText(bundle.getString("cus.lbl.name"));
		lblScore.setText(bundle.getString("cus.lbl.score"));
		lblPhone.setText(bundle.getString("cus.lbl.phone"));
		lblRank.setText(bundle.getString("cus.lbl.rank"));
		lblQty.setText(bundle.getString("cus.lbl.qty"));
		lblMoney.setText(bundle.getString("cus.lbl.money"));

		jLabel10.setText(bundle.getString("cus.title.history"));
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		txtSearch = new javax.swing.JTextField();
		jPanel4 = new javax.swing.JPanel();
		jPanel5 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		tbl_cus = new javax.swing.JTable();
		jLabel1 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		jPanel6 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		lblMaKH = new javax.swing.JLabel();
		lblName = new javax.swing.JLabel();
		lblScore = new javax.swing.JLabel();
		lblPhone = new javax.swing.JLabel();
		lblRank = new javax.swing.JLabel();
		lblQty = new javax.swing.JLabel();
		lblMoney = new javax.swing.JLabel();
		txtMaKH = new javax.swing.JTextField();
		txtName = new javax.swing.JTextField();
		txtPhone = new javax.swing.JTextField();
		txtMoney = new javax.swing.JTextField();
		txtScore = new javax.swing.JTextField();
		txtQty = new javax.swing.JTextField();
		txtRank = new javax.swing.JTextField();
		jPanel7 = new javax.swing.JPanel();
		jPanel8 = new javax.swing.JPanel();
		jLabel10 = new javax.swing.JLabel();
		jPanel9 = new javax.swing.JPanel();
		jScrollPane2 = new javax.swing.JScrollPane();
		tbl_history = new javax.swing.JTable();

		setLayout(new java.awt.BorderLayout());

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		jPanel1.setPreferredSize(new java.awt.Dimension(870, 498));
		jPanel1.setLayout(new java.awt.BorderLayout());

		jPanel3.setPreferredSize(new java.awt.Dimension(870, 70));

		txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
				Translator.getInstance().getString("cus.text.search"));
		txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/search.svg", 0.4f));

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout
						.createSequentialGroup().addGap(29, 29, 29).addComponent(txtSearch,
								javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(435, Short.MAX_VALUE)));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout
						.createSequentialGroup().addGap(17, 17, 17).addComponent(txtSearch,
								javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(19, Short.MAX_VALUE)));

		jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_START);

		jPanel4.setLayout(new java.awt.BorderLayout());

		String[] headers = Translator.getInstance().getString("cus.tbl.headers").split(",");

		tbl_cus.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {}, headers));
		tbl_cus.setDefaultEditor(Object.class, null);
		jScrollPane1.setViewportView(tbl_cus);

		jLabel1.setBackground(new java.awt.Color(0, 51, 255));
		jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		jLabel1.setPreferredSize(new java.awt.Dimension(117, 36));

		javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
		jPanel5.setLayout(jPanel5Layout);
		jPanel5Layout.setHorizontalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel5Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 857,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 870,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel5Layout.setVerticalGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel5Layout.createSequentialGroup()
						.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)));

		jPanel4.add(jPanel5, java.awt.BorderLayout.CENTER);

		jPanel1.add(jPanel4, java.awt.BorderLayout.CENTER);

		add(jPanel1, java.awt.BorderLayout.LINE_START);

		jPanel2.setBackground(new java.awt.Color(255, 255, 255));
		jPanel2.setLayout(new java.awt.GridLayout(2, 1));

		jPanel6.setBackground(new java.awt.Color(255, 255, 255));

		jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		jLabel2.setForeground(new java.awt.Color(51, 51, 255));

		Font boldFont = new Font("Segoe UI", Font.BOLD, 12);
		JLabel[] labels = { lblMaKH, lblName, lblScore, lblPhone, lblRank, lblQty, lblMoney };

		for (JLabel label : labels) {
			label.setFont(boldFont);
		}

		JTextField[] textFields = { txtMaKH, txtMoney, txtQty, txtRank, txtScore, txtName, txtPhone };

		for (JTextField field : textFields) {
			field.setEditable(false);
		}

		javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
		jPanel6.setLayout(jPanel6Layout);
		jPanel6Layout.setHorizontalGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel6Layout
						.createSequentialGroup().addContainerGap(18,
								Short.MAX_VALUE)
						.addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
								.addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING,
										javax.swing.GroupLayout.PREFERRED_SIZE, 256,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout
										.createSequentialGroup()
										.addGroup(jPanel6Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(lblQty).addComponent(lblMoney).addComponent(lblRank)
												.addComponent(
														lblScore)
												.addComponent(lblPhone).addComponent(lblName).addComponent(lblMaKH))
										.addGap(18, 18, 18)
										.addGroup(jPanel6Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(txtQty, javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(txtRank, javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(txtScore, javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(txtPhone, javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(txtName, javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(txtMaKH, javax.swing.GroupLayout.Alignment.LEADING,
														javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
												.addComponent(txtMoney))))
						.addGap(26, 26, 26)));
		jPanel6Layout.setVerticalGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel6Layout.createSequentialGroup().addGap(14, 14, 14)
						.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblMaKH).addComponent(txtMaKH, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblName))
						.addGap(18, 18, 18)
						.addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtPhone, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblPhone))
						.addGap(19, 19, 19)
						.addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblScore).addComponent(txtScore, javax.swing.GroupLayout.PREFERRED_SIZE,
										30, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtRank, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblRank))
						.addGap(18, 18, 18)
						.addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtQty, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblQty))
						.addGap(18, 18, 18)
						.addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblMoney))
						.addContainerGap(23, Short.MAX_VALUE)));

		jPanel2.add(jPanel6);

		jPanel7.setBackground(new java.awt.Color(255, 255, 255));
		jPanel7.setLayout(new java.awt.BorderLayout());

		jPanel8.setPreferredSize(new java.awt.Dimension(405, 60));

		jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		jLabel10.setForeground(new java.awt.Color(51, 51, 255));

		javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
		jPanel8.setLayout(jPanel8Layout);
		jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel8Layout.createSequentialGroup().addContainerGap()
						.addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 224,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel8Layout
						.createSequentialGroup().addGap(15, 15, 15).addComponent(jLabel10,
								javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(19, Short.MAX_VALUE)));

		jPanel7.add(jPanel8, java.awt.BorderLayout.PAGE_START);

		jPanel9.setLayout(new java.awt.BorderLayout());

		String[] historyHeaders = Translator.getInstance().getString("cus.tbl.history.headers").split(",");

		tbl_history.setModel(new javax.swing.table.DefaultTableModel(new Object[][] { { null, null, null, null },
				{ null, null, null, null }, { null, null, null, null }, { null, null, null, null } }, historyHeaders) {
			private static final long serialVersionUID = 1L;
			Class[] types = new Class[] { java.lang.String.class, java.lang.String.class, java.lang.String.class,
					java.lang.String.class };

			public Class getColumnClass(int columnIndex) {
				return types[columnIndex];
			}
		});

		TableColumn colMaHD = tbl_history.getColumnModel().getColumn(0);

		colMaHD.setMinWidth(130);
		colMaHD.setMaxWidth(130);
		colMaHD.setPreferredWidth(130);
		jScrollPane2.setViewportView(tbl_history);

		jPanel9.add(jScrollPane2, java.awt.BorderLayout.CENTER);

		jPanel7.add(jPanel9, java.awt.BorderLayout.CENTER);

		jPanel2.add(jPanel7);

		add(jPanel2, java.awt.BorderLayout.CENTER);
	}

	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JLabel lblMaKH;
	private javax.swing.JLabel lblMoney;
	private javax.swing.JLabel lblName;
	private javax.swing.JLabel lblPhone;
	private javax.swing.JLabel lblQty;
	private javax.swing.JLabel lblRank;
	private javax.swing.JLabel lblScore;
	private javax.swing.JTable tbl_cus;
	private javax.swing.JTable tbl_history;
	private javax.swing.JTextField txtMaKH;
	private javax.swing.JTextField txtMoney;
	private javax.swing.JTextField txtName;
	private javax.swing.JTextField txtPhone;
	private javax.swing.JTextField txtQty;
	private javax.swing.JTextField txtRank;
	private javax.swing.JTextField txtScore;
	private javax.swing.JTextField txtSearch;

}