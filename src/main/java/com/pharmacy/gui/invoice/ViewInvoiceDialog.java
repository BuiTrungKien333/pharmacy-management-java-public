package com.pharmacy.gui.invoice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import com.pharmacy.entity.Invoice;
import com.pharmacy.entity.InvoiceDetail;
import com.pharmacy.utils.FormatUtil;

public class ViewInvoiceDialog extends javax.swing.JDialog {

	public ViewInvoiceDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();

		btnClose.addActionListener(e -> this.dispose());
	}

	private void viewTable(List<InvoiceDetail> list) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.setRowCount(0);

		int index = 1;
		for (InvoiceDetail inv : list) {
			model.addRow(inv.getObjects(index++));
		}
	}

	public void setInitData(Invoice invoice, List<InvoiceDetail> list) {
		viewData(invoice);
		viewTable(list);
	}

	private void viewData(Invoice invoice) {
		txtMaHD.setText(invoice.getMaHoaDon());

		txtNhanVien.setText(String.format("%s - %s", invoice.getEmployee().getMaNhanVien(),
				invoice.getEmployee().getTenNhanVien()));

		txtNgayLap.setText(FormatUtil.formatDate(invoice.getNgayLap()));

		txtKhachHang.setText(invoice.getCustomer() == null ? "Vãng lai"
				: String.format("%s - %s", invoice.getCustomer().getTenKhachHang(),
						invoice.getCustomer().getSoDienThoai()));

		txtTongTien.setText(FormatUtil.formatVND(invoice.getTongTienCanThanhToan()) + " VND");

		txtVoucher.setText(invoice.getVoucher() != null ? invoice.getVoucher().getMaVoucher() : "Không có");

		lblValueDaTra.setText(invoice.isDaTra() ? "Đã trả" : "");
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jPanel2 = new javax.swing.JPanel();
		btnClose = new javax.swing.JButton();
		jPanel3 = new javax.swing.JPanel();
		jPanel4 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		txtMaHD = new javax.swing.JTextField();
		jLabel3 = new javax.swing.JLabel();
		txtKhachHang = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		txtNhanVien = new javax.swing.JTextField();
		jLabel5 = new javax.swing.JLabel();
		txtNgayLap = new javax.swing.JTextField();
		jLabel6 = new javax.swing.JLabel();
		txtVoucher = new javax.swing.JTextField();
		jLabel7 = new javax.swing.JLabel();
		txtTongTien = new javax.swing.JTextField();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();
		lblValueDaTra = new javax.swing.JLabel();
		jPanel5 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		table = new javax.swing.JTable();

		txtKhachHang.setEditable(false);
		txtMaHD.setEditable(false);
		txtNgayLap.setEditable(false);
		txtNhanVien.setEditable(false);
		txtTongTien.setEditable(false);
		txtVoucher.setEditable(false);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));
		jPanel1.setPreferredSize(new java.awt.Dimension(734, 50));

		jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		jLabel1.setForeground(new java.awt.Color(0, 0, 255));
		jLabel1.setText("THÔNG TIN HÓA ĐƠN");
		jPanel1.add(jLabel1);

		getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

		jPanel2.setBackground(new java.awt.Color(255, 255, 255));
		jPanel2.setPreferredSize(new java.awt.Dimension(734, 50));

		btnClose.setBackground(new java.awt.Color(242, 242, 242));
		btnClose.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		btnClose.setText("Close");

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
						.addContainerGap(638, Short.MAX_VALUE).addComponent(btnClose,
								javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		jPanel2Layout.setVerticalGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel2Layout.createSequentialGroup().addContainerGap()
						.addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
						.addContainerGap()));

		getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

		jPanel3.setLayout(new java.awt.BorderLayout());

		jPanel4.setBackground(new java.awt.Color(255, 255, 255));
		jPanel4.setPreferredSize(new java.awt.Dimension(734, 200));

		jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel2.setText("Mã hóa đơn");

		jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel3.setText("Ngày lập");

		jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel4.setText("Nhân viên");

		jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel5.setText("Khách hàng");

		jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel6.setText("Voucher");

		jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel7.setText("Tổng tiền thanh toán");

		jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel8.setText("Trạng thái");

		jLabel9.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
		jLabel9.setForeground(new java.awt.Color(0, 153, 51));
		jLabel9.setText("Đã bán");

		jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel10.setText("Đã trả");

		lblValueDaTra.setFont(new java.awt.Font("Segoe UI", 3, 15)); // NOI18N
		lblValueDaTra.setForeground(new java.awt.Color(255, 51, 0));
		lblValueDaTra.setText("Đã trả");

		javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
		jPanel4.setLayout(jPanel4Layout);
		jPanel4Layout.setHorizontalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 84,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, 250,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 84,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 250,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 84,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 250,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 84,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 114,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addGap(41, 41, 41)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 75,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18).addComponent(txtNgayLap))
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 75,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18).addComponent(txtKhachHang))
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 130,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(txtTongTien))
								.addGroup(jPanel4Layout.createSequentialGroup()
										.addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 73,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18)
										.addComponent(lblValueDaTra, javax.swing.GroupLayout.PREFERRED_SIZE, 163,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(0, 87, Short.MAX_VALUE)))
						.addContainerGap()));
		jPanel4Layout.setVerticalGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel4Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel2)
								.addComponent(txtMaHD, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel3).addComponent(txtNgayLap, javax.swing.GroupLayout.PREFERRED_SIZE,
										30, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel4)
								.addComponent(txtNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel5).addComponent(txtKhachHang,
										javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel6).addComponent(jLabel7)
								.addComponent(txtTongTien, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(27, 27, 27)
						.addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel8).addComponent(jLabel9).addComponent(jLabel10)
								.addComponent(lblValueDaTra, javax.swing.GroupLayout.PREFERRED_SIZE, 29,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(12, Short.MAX_VALUE)));

		jPanel3.add(jPanel4, java.awt.BorderLayout.PAGE_START);

		jPanel5.setPreferredSize(new java.awt.Dimension(734, 280));
		jPanel5.setLayout(new java.awt.BorderLayout());

		table.setModel(new javax.swing.table.DefaultTableModel(
				new Object[][] { { null, null, null, null, null, null }, { null, null, null, null, null, null },
						{ null, null, null, null, null, null }, { null, null, null, null, null, null } },
				new String[] { "STT", "Tên thuốc", "Số lô", "SL Bán", "Giá bán", "Thành tiền" }) {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});

		styleTable(table);

		jScrollPane1.setViewportView(table);

		jPanel5.add(jScrollPane1, java.awt.BorderLayout.CENTER);

		jPanel3.add(jPanel5, java.awt.BorderLayout.CENTER);

		getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

		pack();
	}

	private void styleTable(JTable table) {
		table.setRowHeight(40);
		table.setShowVerticalLines(false);
		table.setGridColor(new Color(230, 230, 230));
		table.setSelectionBackground(new Color(220, 238, 255));
		table.setSelectionForeground(Color.BLACK);
		table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		table.setFillsViewportHeight(true);
		
		JTableHeader header = table.getTableHeader();
		header.setPreferredSize(new Dimension(header.getWidth(), 30));
		header.setFont(new Font("Segoe UI", Font.BOLD, 14));
		header.setBackground(new Color(46, 153, 217));
		header.setForeground(Color.WHITE);
		((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

		TableColumnModel columnModel = table.getColumnModel();

		columnModel.getColumn(0).setPreferredWidth(40);
		columnModel.getColumn(0).setMaxWidth(40);

		columnModel.getColumn(1).setMinWidth(150);

		columnModel.getColumn(2).setPreferredWidth(220);
		columnModel.getColumn(2).setMaxWidth(250);

		columnModel.getColumn(3).setPreferredWidth(70);
		columnModel.getColumn(3).setMaxWidth(80);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
		leftRenderer.setHorizontalAlignment(JLabel.LEFT);
		leftRenderer.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		for (int i = 0; i < table.getColumnCount(); i++) {
			columnModel.getColumn(i).setCellRenderer(i == 1 ? leftRenderer : centerRenderer);
		}
	}

	private javax.swing.JButton btnClose;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JLabel lblValueDaTra;
	private javax.swing.JTable table;
	private javax.swing.JTextField txtKhachHang;
	private javax.swing.JTextField txtMaHD;
	private javax.swing.JTextField txtNgayLap;
	private javax.swing.JTextField txtNhanVien;
	private javax.swing.JTextField txtTongTien;
	private javax.swing.JTextField txtVoucher;
}
