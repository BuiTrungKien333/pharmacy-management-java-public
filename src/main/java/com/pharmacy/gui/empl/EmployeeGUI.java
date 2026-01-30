package com.pharmacy.gui.empl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pharmacy.bus.EmployeeBUS;
import com.pharmacy.bus.StoreBUS;
import com.pharmacy.entity.Employee;

public class EmployeeGUI extends javax.swing.JPanel {

	private final EmployeeBUS employeeBus;

	private List<Employee> dsnv;

	private Timer searchTimer;

	private final StoreBUS storeBUS;

	public EmployeeGUI() {
		employeeBus = new EmployeeBUS();

		storeBUS = new StoreBUS();

		initComponents();

		loadData(employeeBus.getAllNhanVien());

		decorateTable(tblNhanVien);

		decorateColumnTrangThai();

		decorateColumnHoTen();

		initSearch();

		initEvent();
	}

	private void initEvent() {
		btnThemNV.addActionListener(e -> addEmployee());
		tblNhanVien.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && tblNhanVien.getSelectedRow() != -1) {
					int selectedRow = tblNhanVien.getSelectedRow();
					DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();

					String maNV = model.getValueAt(selectedRow, 0).toString().trim();

					Employee emp = employeeBus.getEmployeeById(maNV);
					if (emp == null) {
						JOptionPane.showMessageDialog(tblNhanVien, "Không tìm thấy thông tin nhân viên!", "Lỗi",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					ProfileEmpl profile = new ProfileEmpl(null, true, maNV, EmployeeGUI.this, employeeBus, storeBUS);
					profile.loadInfo(emp);
					profile.setLocationRelativeTo(null);
					profile.setVisible(true);
				}
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
				String timKiemText = txtTimKiem.getText().trim();
				DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
				model.setRowCount(0);

				List<Employee> dsnv = employeeBus.getEmployeeSearch(timKiemText);
				if (dsnv.isEmpty())
					loadData(employeeBus.getAllNhanVien());

				for (Employee empl : dsnv)
					model.addRow(empl.getObject());
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
			}
		});

	}
	
	public int getRowSelected() {
		return tblNhanVien.getSelectedRow();
	}

	public String getEmpIdFromTable(int row) {
		if (row == -1)
			return null;
		
		DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
		return (String) model.getValueAt(row, 0);
	}

	public void loadData(List<Employee> dsnv) {
		DefaultTableModel model = (DefaultTableModel) tblNhanVien.getModel();
		model.setRowCount(0);
		for (Employee emp : dsnv) {
			model.addRow(emp.getObject());
		}
	}


	private void addEmployee() {
		AddEmployee form = new AddEmployee(null, true, EmployeeGUI.this, employeeBus, storeBUS);
		form.setLocationRelativeTo(null);
		form.setVisible(true);
	}

	private void decorateColumnHoTen() {
		tblNhanVien.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				JPanel panel = new JPanel(new GridBagLayout());
				panel.setOpaque(true);
				panel.setBackground(new Color(250, 250, 250));

				JButton btnHoTen = new JButton();
				btnHoTen.setText((String) value);
				btnHoTen.setIcon(new FlatSVGIcon("icon/svg/user.svg", 35, 35));
				btnHoTen.setContentAreaFilled(false);
				btnHoTen.setBorderPainted(false);

				if (isSelected)
					panel.setBackground(new Color(102, 178, 255));

				panel.setLayout(new FlowLayout(FlowLayout.LEFT));
				panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(220, 220, 220)));

				panel.add(btnHoTen);
				return panel;
			}
		});
	}

	private void decorateColumnTrangThai() {
		tblNhanVien.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {

				JPanel panel = new JPanel(new GridBagLayout());
				panel.setOpaque(true);
				panel.setBackground(new Color(255, 255, 255));

				JButton label = new JButton(value == null ? "" : value.toString()) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void paintComponent(Graphics g) {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

						int width = getWidth();
						int height = getHeight();

						g2.setColor(getBackground());
						g2.fillRoundRect(0, 0, width, height, height, height);

						super.paintComponent(g2);
						g2.dispose();
					}
				};

				label.setFont(new Font("Segoe UI", Font.BOLD, 12));
				label.setFocusPainted(false);
				label.setContentAreaFilled(false);
				label.setBorderPainted(false);
				label.setOpaque(false);
				label.setForeground(Color.BLACK);
				label.setHorizontalAlignment(SwingConstants.CENTER);
				label.setPreferredSize(new Dimension(150, 25));

				String trangThai = value == null ? "" : value.toString();
				switch (trangThai) {
				case "Đang làm việc":
					label.setBackground(new Color(204, 255, 204));
					label.setForeground(new Color(0, 100, 0));
					break;
				case "Nghỉ việc":
					label.setBackground(new Color(248, 215, 218));
					label.setForeground(new Color(120, 0, 0));
					break;
				default:
					label.setBackground(Color.WHITE);
					label.setForeground(Color.BLACK);
					break;
				}

				if (isSelected)
					panel.setBackground(new Color(102, 178, 255));

				panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(220, 220, 220)));

				panel.add(label);
				return panel;
			}
		});
	}

	private void decorateTable(JTable tblHoaDon) {

		tblHoaDon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		tblHoaDon.setRowHeight(55);
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

	private void initComponents() {
		JpNorth = new javax.swing.JPanel();
		txtTimKiem = new javax.swing.JTextField();
		comboLoc = new javax.swing.JComboBox<>();
		btnThemNV = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		tblNhanVien = new javax.swing.JTable();

		setLayout(new java.awt.BorderLayout());

		txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
				"Nhập họ tên, số điện thoại hoặc email nhân viên cần tìm...");

		txtTimKiem.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/search.svg", 0.4f));

		comboLoc.setPreferredSize(new Dimension(120, 35));

		comboLoc.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dược sĩ", "Quản lý" }));
		
		btnThemNV.setText("Thêm nhân viên mới");
		btnThemNV.setIcon(new FlatSVGIcon("icon/svg/add.svg", 20, 20));
		btnThemNV.setBackground(Color.blue);
		btnThemNV.setForeground(Color.white);

		tblNhanVien.setDefaultEditor(Object.class, null);

		tblNhanVien.setModel(new javax.swing.table.DefaultTableModel(new Object[][] {},
				new String[] { "Mã nhân viên", "Họ và tên", "Vai trò", "Số điện thoại", "Email", "Trạng thái" }));

		jScrollPane1.setViewportView(tblNhanVien);

		javax.swing.GroupLayout JpNorthLayout = new javax.swing.GroupLayout(JpNorth);
		JpNorth.setLayout(JpNorthLayout);
		JpNorthLayout.setHorizontalGroup(JpNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpNorthLayout.createSequentialGroup().addGap(50, 50, 50)
						.addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 493,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
						.addComponent(comboLoc, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(btnThemNV, javax.swing.GroupLayout.PREFERRED_SIZE, 194,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(68, 68, 68))
				.addGroup(JpNorthLayout.createSequentialGroup().addContainerGap().addComponent(jScrollPane1)));
		JpNorthLayout.setVerticalGroup(JpNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(JpNorthLayout.createSequentialGroup().addGap(19, 19, 19)
						.addGroup(JpNorthLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnThemNV, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(comboLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, Short.MAX_VALUE).addComponent(jScrollPane1,
								javax.swing.GroupLayout.PREFERRED_SIZE, 800, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));

		add(JpNorth, java.awt.BorderLayout.NORTH);
		add(jScrollPane1, BorderLayout.CENTER);
	}

	private javax.swing.JPanel JpNorth;
	private javax.swing.JButton btnThemNV;
	private javax.swing.JComboBox<String> comboLoc;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTable tblNhanVien;
	private javax.swing.JTextField txtTimKiem;
}
