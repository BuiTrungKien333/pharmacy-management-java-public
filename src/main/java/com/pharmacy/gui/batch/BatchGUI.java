package com.pharmacy.gui.batch;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pharmacy.bus.Auth;
import com.pharmacy.bus.BatchBUS;
import com.pharmacy.bus.NhaCungCapBUS;
import com.pharmacy.config.Translator;
import com.pharmacy.entity.Batch;
import com.pharmacy.entity.BatchStatus;
import com.pharmacy.entity.NhaCungCap;
import com.pharmacy.entity.Product;
import com.pharmacy.utils.ExcelExporterUtil;
import com.pharmacy.utils.FormatUtil;
import com.pharmacy.utils.Pagination;
import com.toedter.calendar.JDateChooser;

import net.miginfocom.swing.MigLayout;
import raven.modal.Toast;

public class BatchGUI extends javax.swing.JPanel {

	private static final int pageSize = 20;

	private static final String activePageButtonStyle = "" + "background:$primary;" + "foreground:$white;" + "arc:999;"
			+ "margin:5,5,5,5;" + "hoverBackground:$primary;" + "hoverForeground:$white;" + "font:$h6.font;";

	private static final String normalPageButtonStyle = "" + "background:$white;" + "foreground:$black;" + "arc:999;"
			+ "margin:5,5,5,5;" + "borderColor:$gray;" + "hoverBackground:#E0E0E0;" + "hoverForeground:$black;"
			+ "hoverBorderColor:#007BFF;" + "font:$h6.font;";

	private JPanel pnlMainHeader;

	private JPanel pnlMainBody;

	private BatchBUS shipmentBUS;

	private NhaCungCapBUS nhaCungCapBUS;

	private AddBatchDialog shipment;

	private Pagination pagination;

	private JPanel pnlPage;

	private JDateChooser dateFrom;

	private JDateChooser dateTo;

	private boolean useSearchBySoLo = true;

	private int type = 0; // tất cả: 0, đang lưu hành: 1, đã huỷ: 2, đã bán hết: 3, 4: đã hết hạn

	private int filter = 0; // tất cả: 0, 1 -> 7: tùy chỉnh theo rad

	private int option = 0; // 0: ngay nhap, 1: han su dung

	private JLabel lblHeaderSoLo;

	private JLabel lblHeaderProd;

	private JLabel lblHeaderNgayNhap;

	private JLabel lblSoLuongNhap;

	private JLabel lblSoLuongCon;

	private JLabel lblGiaBan;

	public BatchGUI() {

		this.shipmentBUS = new BatchBUS();

		this.nhaCungCapBUS = new NhaCungCapBUS();

		shipmentBUS.capNhatTrangThaiHetHanCuaLo();

		initComponents();

		applyPermissions();

		Translator.getInstance().addLanguageChangeListener(locale -> {
			SwingUtilities.invokeLater(this::updateTexts);
		});

		updateTexts();

		initEvent();

		loadRefreshDataToDb();
	}

	private void applyPermissions() {
		btnAdd.setEnabled(Auth.hasPermission("BATCH_ADD"));
		btnExport.setEnabled(Auth.hasPermission("BATCH_EXPORT"));
		btnImport.setEnabled(Auth.hasPermission("BATCH_EXPORT"));
	}

	public void loadRefreshDataToDb() {
		int total = shipmentBUS.getTotalRecord();
		pagination = new Pagination(1, pageSize, total);
		txtTotalRecord.setText(String.valueOf(total));

		initPage(shipmentBUS.getAllShipmentByPage(pagination, option));
	}

	private void initEvent() {
		btnAdd.addActionListener(e -> addShipment());

		btnExport.addActionListener(e -> exportBatchToExcel());

		txtSearchSoLo.addActionListener(e -> searchBySoLo());

		txtSearchMaSP.addActionListener(e -> searchByBarcode());

		radStatusAll.addActionListener(e -> {
			type = 0;
			applyFilters();
		});

		radDangLuuHanh.addActionListener(e -> {
			type = 1;
			applyFilters();
		});

		radDaHuy.addActionListener(e -> {
			type = 2;
			applyFilters();
		});

		radDaBanHet.addActionListener(e -> {
			type = 3;
			applyFilters();
		});

		radDaHetHan.addActionListener(e -> {
			type = 4;
			applyFilters();
		});

		radTimeAll.addActionListener(e -> {
			filter = 0;
			applyFilters();
		});

		radToday.addActionListener(e -> {
			filter = 1;
			applyFilters();
		});

		radWeek.addActionListener(e -> {
			filter = 2;
			applyFilters();
		});

		radMonth.addActionListener(e -> {
			filter = 3;
			applyFilters();
		});

		radDiff.addChangeListener(e -> {
			filter = 4;

			boolean enable = radDiff.isSelected();

			txtTimeStart.setEnabled(enable);
			txtTimeEnd.setEnabled(enable);

			if (!enable) {
				txtTimeStart.setText("");
				txtTimeEnd.setText("");
				dateFrom.setVisible(false);
				dateTo.setVisible(false);
			}
		});

		radHetHan7Ngay.addActionListener(e -> {
			filter = 5;
			applyFilters();
		});
		radHetHan30Ngay.addActionListener(e -> {
			filter = 6;
			applyFilters();
		});

		radHetHan3Thang.addActionListener(e -> {
			filter = 7;
			applyFilters();
		});

		txtTimeStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!radDiff.isSelected())
					return;

				dateFrom.setVisible(true);
				dateFrom.getCalendarButton().doClick();
			}
		});

		txtTimeEnd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!radDiff.isSelected())
					return;

				dateTo.setVisible(true);
				dateTo.getCalendarButton().doClick();
			}
		});

		dateFrom.getDateEditor().addPropertyChangeListener("date", evt -> {
			Date date = dateFrom.getDate();
			if (date != null) {
				txtTimeStart.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
				applyFilters();
			}
		});

		dateTo.getDateEditor().addPropertyChangeListener("date", evt -> {
			Date date = dateTo.getDate();
			if (date != null) {
				txtTimeEnd.setText(new SimpleDateFormat("dd/MM/yyyy").format(date));
				applyFilters();
			}
		});

		cmbOption.addActionListener(e -> {
			option = cmbOption.getSelectedIndex();
			applyFilters();
		});
	}

	private void exportBatchToExcel() {
		LocalDate startDate = shipmentBUS.convertStringToLocalDate(filter, txtTimeStart.getText());
		LocalDate endDate = shipmentBUS.convertStringToLocalDate(filter, txtTimeEnd.getText());

		if (filter == 4) {
			if (startDate == null || endDate == null)
				return;

			if (startDate.isAfter(endDate)) {
				Toast.show(this, Toast.Type.ERROR, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
				return;
			}
		}

		List<Batch> list = shipmentBUS.getAllBatchToExport(type, filter, startDate, endDate, option);
		String[] headers = { "Số lô", "Barcode - Tên thuốc", "Nhà cung cấp", "Nhân viên nhập", "Ngày sản xuất",
				"Hạn sử dụng", "Ngày nhập", "Số lượng nhập", "DVT", "Giá nhập", "Thành tiền", "Số lượng còn", "Giá bán",
				"Trạng thái lô" };

		List<Object[]> data = new ArrayList<>();
		for (Batch b : list) {
			data.add(b.getObjects());
		}

		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

		FileDialog fileDialog = new FileDialog(parentFrame, "Xuất danh sách lô thuốc ra Excel", FileDialog.SAVE);

		fileDialog.setFile("Danh_Sach_Lo_Thuoc.xlsx");

		fileDialog.setFilenameFilter((dir, name) -> name.endsWith(".xlsx"));

		fileDialog.setVisible(true);

		String directory = fileDialog.getDirectory();
		String filename = fileDialog.getFile();

		if (filename != null && directory != null) {
			String filePath = directory + filename;

			if (!filePath.toLowerCase().endsWith(".xlsx"))
				filePath += ".xlsx";

			try {
				ExcelExporterUtil.exportDataToExcel(filePath, "DanhSachLoThuoc", headers, data);
				JOptionPane.showMessageDialog(this, "Xuất file thành công!");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xuất file!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void applyFilters() {
		LocalDate startDate = shipmentBUS.convertStringToLocalDate(filter, txtTimeStart.getText());
		LocalDate endDate = shipmentBUS.convertStringToLocalDate(filter, txtTimeEnd.getText());

		if (filter == 4) {
			if (startDate == null || endDate == null)
				return;

			if (startDate.isAfter(endDate)) {
				Toast.show(this, Toast.Type.ERROR, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
				return;
			}
		}

		String keyword = useSearchBySoLo ? txtSearchSoLo.getText().trim() : txtSearchMaSP.getText().trim();

		// Bước 1: tính tổng record
		int totalRecords;
		if (keyword.isEmpty()) {
			totalRecords = shipmentBUS.countFilteredShipment(type, filter, startDate, endDate);
		} else {
			totalRecords = useSearchBySoLo
					? shipmentBUS.countFilteredShipmentAndSearchBySoLo(type, filter, startDate, endDate, keyword)
					: shipmentBUS.countFilteredShipmentAndSearchByBarcode(type, filter, startDate, endDate, keyword);
		}

		txtTotalRecord.setText(String.valueOf(totalRecords));

		// Bước 2: khởi tạo pagination
		pagination = new Pagination(1, pageSize, totalRecords);

		// Show list shipment filtered
		List<Batch> list = keyword.isEmpty()
				? shipmentBUS.getFilteredShipment(type, filter, pagination, startDate, endDate, option)
				: useSearchBySoLo
						? shipmentBUS.getFilteredShipmentAndSearchBySoLo(type, filter, pagination, startDate, endDate,
								keyword, option)
						: shipmentBUS.getFilteredShipmentAndSearchByBarcode(type, filter, pagination, startDate,
								endDate, keyword, option);

		initPage(list);

	}

	private void searchBySoLo() {
		useSearchBySoLo = true;
		txtSearchMaSP.setText("");
		applyFilters();
	}

	private void searchByBarcode() {
		useSearchBySoLo = false;
		txtSearchSoLo.setText("");
		applyFilters();
	}

	public void showPage(int pageNum) {
		if (pageNum == 0 || pageNum > pagination.getTotalPages())
			pageNum = 1;

		pagination.setPageNumber(pageNum);

		LocalDate startDate = shipmentBUS.convertStringToLocalDate(filter, txtTimeStart.getText());
		LocalDate endDate = shipmentBUS.convertStringToLocalDate(filter, txtTimeEnd.getText());

		if (filter == 4) {
			if (startDate == null || endDate == null)
				return;

			if (startDate.isAfter(endDate)) {
				Toast.show(this, Toast.Type.ERROR, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
				return;
			}
		}

		String keyword = useSearchBySoLo ? txtSearchSoLo.getText().trim() : txtSearchMaSP.getText().trim();

		List<Batch> list = keyword.isEmpty()
				? shipmentBUS.getFilteredShipment(type, filter, pagination, startDate, endDate, option)
				: useSearchBySoLo
						? shipmentBUS.getFilteredShipmentAndSearchBySoLo(type, filter, pagination, startDate, endDate,
								keyword, option)
						: shipmentBUS.getFilteredShipmentAndSearchByBarcode(type, filter, pagination, startDate,
								endDate, keyword, option);

		updatePageButtons(pageNum);
		loadData(list);
	}

	private void updateTexts() {
		Translator lang = Translator.getInstance();

		lblHeaderSoLo.setText(lang.getString("shipment.lbl.batch"));
		lblHeaderProd.setText(lang.getString("shipment.lbl.product"));
		lblHeaderNgayNhap.setText(lang.getString("shipment.lbl.entry_date"));
		lblHanSuDung.setText(lang.getString("shipment.lbl.exp_date"));
		lblSoLuongNhap.setText(lang.getString("shipment.lbl.qty_in"));
		lblSoLuongCon.setText(lang.getString("shipment.lbl.qty_left"));
		lblGiaBan.setText(lang.getString("shipment.lbl.sale_price"));
		lblTrangThai.setText(lang.getString("shipment.lbl.status"));

		radDangLuuHanh.setText(lang.getString("shipment.rad.active"));
		radStatusAll.setText(lang.getString("shipment.rad.all"));
		radDaBanHet.setText(lang.getString("shipment.rad.sold_out"));
		radDaHuy.setText(lang.getString("shipment.rad.cancelled"));
		radDaHetHan.setText(lang.getString("shipment.rad.expired"));

		radTimeAll.setText(lang.getString("shipment.rad.time_all"));
		radToday.setText(lang.getString("shipment.rad.today"));
		radWeek.setText(lang.getString("shipment.rad.week"));
		radMonth.setText(lang.getString("shipment.rad.month"));
		radDiff.setText(lang.getString("shipment.rad.other"));

		radHetHan7Ngay.setText(lang.getString("shipment.rad.exp_7days"));
		radHetHan30Ngay.setText(lang.getString("shipment.rad.exp_30days"));
		radHetHan3Thang.setText(lang.getString("shipment.rad.exp_3months"));

		// Labels & buttons
		lblThoiGian.setText(lang.getString("shipment.lbl.time_filter"));
		lblNgayNhap.setText(lang.getString("shipment.lbl.entry_date"));
		lblHanSuDung.setText(lang.getString("shipment.lbl.exp_date"));

		lblQuanLyLoHang.setText(lang.getString("shipment.lbl.management"));
		btnAdd.setText(lang.getString("shipment.btn.add"));

		txtSearchSoLo.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, lang.getString("shipment.search.batch"));
		txtSearchMaSP.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, lang.getString("shipment.search.prod"));
	}

	private void initPage(List<Batch> list) {
		pnlPage.removeAll();

		JButton btnPageFirst = new JButton(new FlatSVGIcon("icon/svg/prev.svg", 30, 30));
		btnPageFirst.setFocusPainted(false);
		btnPageFirst.setBorderPainted(false);
		btnPageFirst.setContentAreaFilled(false);
		btnPageFirst.addActionListener(e -> showPage(1));
		pnlPage.add(btnPageFirst);

		for (int i = 1; i <= pagination.getTotalPages(); i++) {
			JButton btn = new JButton(String.valueOf(i));
			stylePageButton(btn, i == pagination.getPageNumber());
			final int pageNum = i;
			btn.addActionListener(e -> showPage(pageNum));
			pnlPage.add(btn);
		}

		JButton btnPageEnd = new JButton(new FlatSVGIcon("icon/svg/next.svg", 30, 30));
		btnPageEnd.setFocusPainted(false);
		btnPageEnd.setBorderPainted(false);
		btnPageEnd.setContentAreaFilled(false);
		btnPageEnd.addActionListener(e -> showPage(pagination.getTotalPages()));
		pnlPage.add(btnPageEnd);

		pnlPage.revalidate();
		pnlPage.repaint();

		loadData(list); // hiển thị dữ liệu page hiện tại
	}

	private void updatePageButtons(int activePage) {
		for (Component c : pnlPage.getComponents()) {
			if (c instanceof JButton b && b.getText() != null && b.getText().matches("\\d+")) {
				int pageNum = Integer.parseInt(b.getText());
				stylePageButton(b, pageNum == activePage);
			}
		}
	}

	private void stylePageButton(JButton btn, boolean active) {
		btn.putClientProperty(FlatClientProperties.STYLE, active ? activePageButtonStyle : normalPageButtonStyle);
	}

	public void loadData1(List<Batch> list) {
		pnlMainBody.setVisible(false);
		pnlMainBody.removeAll();

		list.forEach(s -> pnlMainBody.add(createAccordionSection(s)));

		pnlMainBody.revalidate();
		pnlMainBody.repaint();
		pnlMainBody.setVisible(true);
	}

	public void loadData(List<Batch> list) {
		if (list == null)
			return;

		try {
			pnlMainBody.setVisible(false);
			pnlMainBody.removeAll();

			for (int i = 0; i < list.size(); i++) {
				Batch s = list.get(i);
				pnlMainBody.add(createAccordionSection(s));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pnlMainBody.revalidate();
			pnlMainBody.repaint();
			pnlMainBody.setVisible(true);
		}
	}

	private void addShipment() {
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

		JPanel glass = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(new Color(0, 0, 0, 90));
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};
		glass.setOpaque(false);

		frame.setGlassPane(glass);
		glass.setVisible(true);

		if (shipment == null)
			shipment = new AddBatchDialog(frame, true, this, shipmentBUS, nhaCungCapBUS);
		else
			shipment.clearData();

		shipment.setLocationRelativeTo(frame);
		shipment.setVisible(true);

		glass.setVisible(false);
	}

	public void refreshDataShipment() {
		txtSearchMaSP.setText("");
		cmbOption.setSelectedIndex(0);
		txtSearchSoLo.setText("");
		radStatusAll.setSelected(true);
		radTimeAll.setSelected(true);

		loadRefreshDataToDb();
	}

	// tạo phần accordion như bootstrap
	private JPanel createAccordionSection(Batch shipment) {
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		pnlMain.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		pnlMain.setBackground(Color.WHITE);

		// header, dòng tiêu đề nội dung khi chưa show xuống
		JPanel pnlHeader = new JPanel();
		pnlHeader.setBackground(Color.WHITE);

		pnlHeader.setLayout(new MigLayout("fillx, insets 10",
				"[30!]10[200!]10[fill]10[130!]15[80!]15[80!]15[80!]15[100!][180!]", "[]"));

		JButton btn = new JButton();
		btn.setIcon(new FlatSVGIcon("icon/svg/down.svg", 15, 15));

		btn.setPreferredSize(new Dimension(25, 25));
		btn.setMaximumSize(new Dimension(25, 25));
		btn.setMinimumSize(new Dimension(25, 25));

		btn.setBorderPainted(false);
		btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setOpaque(false);
		btn.setBackground(Color.WHITE);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.putClientProperty(FlatClientProperties.STYLE, "background:null; borderWidth:0; focusWidth:0;");

		pnlHeader.add(btn);

		JLabel lblMaGiamGia = new JLabel();
		lblMaGiamGia.setText(shipment.getSoLo());
		pnlHeader.add(lblMaGiamGia);

		JLabel lblGiaTri = new JLabel();
		lblGiaTri.setText(shipment.getProduct().getTenSanPham());
		pnlHeader.add(lblGiaTri);

		JLabel lblNgayTao = new JLabel();
		lblNgayTao.setText(FormatUtil.formatDate(shipment.getNgayNhap()));

		pnlHeader.add(lblNgayTao);

		JLabel lblHanSuDung = new JLabel();
		lblHanSuDung.setText(FormatUtil.formatDate(shipment.getHanSuDung()));
		lblHanSuDung.setFont(new Font("Segoe UI", Font.BOLD, 12));
		pnlHeader.add(lblHanSuDung);

		JLabel lblSoLuot = new JLabel();
		lblSoLuot.setText(String.format("%d %s", shipment.getSoLuongNhap(), shipment.getProduct().getDonViTinh()));
		pnlHeader.add(lblSoLuot);

		JLabel lblSoLuot1 = new JLabel();
		lblSoLuot1.setText(String.format("%d %s", shipment.getSoLuongCon(), shipment.getProduct().getDonViTinh()));
		lblSoLuot1.setForeground(Color.BLACK);
		lblSoLuot1.setFont(new Font("Segoe UI", Font.BOLD, 12));
		pnlHeader.add(lblSoLuot1);

		JLabel lblSoLuot2 = new JLabel();
		lblSoLuot2.setText(FormatUtil.formatVND(shipment.getGiaBan()) + " đ");
		lblSoLuot2.setFont(new Font("Segoe UI", Font.BOLD, 12));
		pnlHeader.add(lblSoLuot2);

		JButton btnTrangThai = new JButton();
		btnTrangThai.setText(shipment.getShipmentStatus().getTenTrangThai());
		btnTrangThai.setBackground(new Color(204, 255, 204));

		int status = shipment.getShipmentStatus().getId();
		switch (status) {
		case 1 -> btnTrangThai.setForeground(new Color(0, 100, 0));
		case 2 -> btnTrangThai.setForeground(new Color(251, 188, 4));
		case 3 -> btnTrangThai.setForeground(new Color(229, 62, 49));
		case 4 -> btnTrangThai.setForeground(new Color(123, 31, 162));
		}

		if (status == 1) {
			long noOfDaysBetween = ChronoUnit.DAYS.between(LocalDate.now(), shipment.getHanSuDung());
			if (noOfDaysBetween <= 30) {
				btnTrangThai.setText("Sắp hết hạn. Không được bán");
				btnTrangThai.setForeground(new Color(255, 51, 51));
			} else if (noOfDaysBetween <= 90) {
				btnTrangThai.setText("Sắp hết hạn");
				btnTrangThai.setForeground(new Color(250, 184, 0));
			}
		}

		btnTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnTrangThai.setFocusPainted(false);
		btnTrangThai.setContentAreaFilled(false);
		btnTrangThai.setBorderPainted(false);
		btnTrangThai.setOpaque(false);
		btnTrangThai.setHorizontalAlignment(SwingConstants.CENTER);

		pnlHeader.add(btnTrangThai);

		// content, mở nội dung khi ấn nút show xuống
		JPanel pnlContent = new JPanel();
		pnlContent.setVisible(false);

		btn.addActionListener(e -> {
			boolean value = pnlContent.isVisible();
			if (!value && pnlContent.getComponentCount() == 0)
				initPnlContent(pnlContent, shipment.getSoLo());

			pnlContent.setVisible(!value);
			btn.setIcon(new FlatSVGIcon(String.format("icon/svg/%s.svg", value ? "down" : "top"), 15, 15));
		});

		pnlMain.add(pnlHeader, BorderLayout.NORTH);
		pnlMain.add(pnlContent, BorderLayout.CENTER);

		return pnlMain;
	}

	// Tạo phần content chứa nội dung chi tiết của batch
	private void initPnlContent(JPanel pnlContent, String soLo) {
		Batch shipment = shipmentBUS.getShipmentById(soLo);

		pnlContent.setLayout(new BorderLayout());

		JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
		pnlTitle.setBackground(new Color(217, 217, 217));
		JLabel lblTitle = new JLabel("Chi tiết");
		lblTitle.setForeground(Color.BLACK);
		lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		pnlTitle.add(lblTitle);

		JPanel pnlBody = new JPanel();
		pnlBody.setLayout(new MigLayout("wrap 4, fillx, insets 10 30 10 30", "[left][grow,fill]40[left][grow,fill]",
				"[]10[]10[]10[]10[]10[]10[]10[]"));

		Font labelFont = new Font("Segoe UI", Font.BOLD, 12);

		JLabel lblSoLo = new JLabel();
		lblSoLo.setFont(labelFont);
		JTextField txtSoLo = new JTextField();
		txtSoLo.setText(shipment.getSoLo());
		txtSoLo.setEditable(false);

		JLabel lblMaSP = new JLabel();
		lblMaSP.setFont(labelFont);
		JTextField txtMaSP = new JTextField();
		txtMaSP.setEditable(false);
		txtMaSP.setText(shipment.getProduct().getFullName());

		pnlBody.add(lblSoLo);
		pnlBody.add(txtSoLo);
		pnlBody.add(lblMaSP);
		pnlBody.add(txtMaSP);

		JLabel lblNgaySX = new JLabel();
		lblNgaySX.setFont(labelFont);
		JTextField txtNgaySX = new JTextField();
		txtNgaySX.setText(FormatUtil.formatDate(shipment.getNgaySanXuat()));

		JLabel lblNCC = new JLabel();
		lblNCC.setFont(labelFont);
		JComboBox<NhaCungCap> cmbNCC = new JComboBox<>();

		nhaCungCapBUS.getListNhaCungCap().forEach(ncc -> cmbNCC.addItem(ncc));

		cmbNCC.setSelectedItem(shipment.getNhaCungCap());

		pnlBody.add(lblNgaySX);
		pnlBody.add(txtNgaySX);
		pnlBody.add(lblNCC);
		pnlBody.add(cmbNCC);

		JLabel lblHanSuDung = new JLabel();
		lblHanSuDung.setFont(labelFont);
		JTextField txtHanSuDung = new JTextField();
		txtHanSuDung.setText(FormatUtil.formatDate(shipment.getHanSuDung()));

		JPanel pnl1 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		JLabel lblSoLuongNhap = new JLabel();
		lblSoLuongNhap.setFont(labelFont);
		JTextField txtSoLuongNhap = new JTextField();
		txtSoLuongNhap.setText(String.valueOf(shipment.getSoLuongNhap()));
		txtSoLuongNhap.setPreferredSize(new Dimension(170, 30));

		JTextField txtDonViTinh1 = new JTextField();
		txtDonViTinh1.setEditable(false);
		txtDonViTinh1.setText(shipment.getProduct().getDonViTinh());
		txtDonViTinh1.setPreferredSize(new Dimension(80, 30));

		pnl1.setBackground(Color.WHITE);
		pnl1.add(txtSoLuongNhap);
		pnl1.add(txtDonViTinh1);

		pnlBody.add(lblHanSuDung);
		pnlBody.add(txtHanSuDung);
		pnlBody.add(lblSoLuongNhap);
		pnlBody.add(pnl1);

		JLabel lblNgayNhap = new JLabel();
		lblNgayNhap.setFont(labelFont);
		JTextField txtNgayNhap = new JTextField();
		txtNgayNhap.setEditable(false);
		txtNgayNhap.setText(FormatUtil.formatDate(shipment.getNgayNhap()));

		JLabel lblSoLuongCon = new JLabel();
		lblSoLuongCon.setFont(labelFont);
		JPanel pnl2 = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		JTextField txtSoLuongCon = new JTextField();
		txtSoLuongCon.setText(String.valueOf(shipment.getSoLuongCon()));
		txtSoLuongCon.setPreferredSize(txtSoLuongNhap.getPreferredSize());

		JTextField txtDonViTinh2 = new JTextField();
		txtDonViTinh2.setEditable(false);
		txtDonViTinh2.setText(shipment.getProduct().getDonViTinh());
		txtDonViTinh2.setPreferredSize(new Dimension(80, 30));

		pnl2.setBackground(Color.WHITE);
		pnl2.add(txtSoLuongCon);
		pnl2.add(txtDonViTinh2);

		pnlBody.add(lblNgayNhap);
		pnlBody.add(txtNgayNhap);
		pnlBody.add(lblSoLuongCon);
		pnlBody.add(pnl2);

		JLabel lblNhanVien = new JLabel();
		lblNhanVien.setFont(labelFont);
		JTextField txtNhanVien = new JTextField();
		txtNhanVien.setEditable(false);
		txtNhanVien.setText(shipment.getEmployee().getDisplayName());

		JLabel lblGiaNhap = new JLabel();
		lblGiaNhap.setFont(labelFont);
		JTextField txtGiaNhap = new JTextField();
		txtGiaNhap.setText(String.format("%.0f", shipment.getGiaNhap()));

		pnlBody.add(lblNhanVien);
		pnlBody.add(txtNhanVien);
		pnlBody.add(lblGiaNhap);
		pnlBody.add(txtGiaNhap);

		JLabel lblTrangThai = new JLabel();
		lblTrangThai.setFont(labelFont);

		Translator lang = Translator.getInstance();
		int status = shipment.getShipmentStatus().getId();
		JComboBox<String> cboTrangThai;

		if (status >= 3) {
			String statusText = "";
			if (status == 3)
				statusText = lang.getString("shipment.status.sold_out"); // "Đã bán hết"
			else if (status == 4)
				statusText = lang.getString("shipment.status.expired"); // "Đã hết hạn"
			else
				statusText = "Không xác định";

			String[] singleOption = { statusText };
			cboTrangThai = new JComboBox<>(singleOption);

			cboTrangThai.setSelectedIndex(0);

			cboTrangThai.setEnabled(false);

		} else {
			String[] statusOptions = { lang.getString("shipment.status.active"),
					lang.getString("shipment.status.cancelled") };

			cboTrangThai = new JComboBox<>(statusOptions);

			int index = status - 1;
			if (index >= 0 && index < statusOptions.length)
				cboTrangThai.setSelectedIndex(index);
		}

		JLabel lblGiaBan = new JLabel();
		lblGiaBan.setFont(labelFont);
		JTextField txtGiaBan = new JTextField();
		txtGiaBan.setText(String.format("%.0f", shipment.getGiaBan()));

		pnlBody.add(lblTrangThai);
		pnlBody.add(cboTrangThai);
		pnlBody.add(lblGiaBan);
		pnlBody.add(txtGiaBan);

		JLabel lblThanhTien = new JLabel();
		lblThanhTien.setFont(labelFont);
		JTextField txtThanhTien = new JTextField();
		txtThanhTien.setEditable(false);
		txtThanhTien.setText(FormatUtil.formatVND(shipment.getThanhTien()) + " VND");

		if (!Auth.hasPermission("BATCH_EDIT")) {
			txtGiaNhap.setText("");
			txtGiaNhap.setEditable(false);
			txtGiaBan.setEditable(false);
			txtThanhTien.setText("");
		}

		pnlBody.add(new JLabel());
		pnlBody.add(new JLabel());
		pnlBody.add(lblThanhTien);
		pnlBody.add(txtThanhTien);

		JButton btn = new JButton("Save");
		btn.setPreferredSize(new Dimension(80, 30));
		btn.setMinimumSize(new Dimension(80, 30));
		btn.setMaximumSize(new Dimension(80, 30));
		btn.setBackground(Color.BLUE);
		btn.setForeground(Color.WHITE);
		btn.setFont(labelFont);
		btn.setEnabled(Auth.hasPermission("BATCH_EDIT"));
		pnlBody.add(btn, "span 4, align right, pushx");

		lblSoLo.setText(lang.getString("shipment.lbl.batch"));
		lblMaSP.setText(lang.getString("shipment.lbl.product"));
		lblNgaySX.setText(lang.getString("shipment.lbl.mfg_date"));
		lblNCC.setText(lang.getString("shipment.lbl.supplier"));
		lblHanSuDung.setText(lang.getString("shipment.lbl.exp_date"));
		lblSoLuongNhap.setText(lang.getString("shipment.lbl.qty_in"));
		lblNgayNhap.setText(lang.getString("shipment.lbl.entry_date"));
		lblSoLuongCon.setText(lang.getString("shipment.lbl.qty_left"));
		lblNhanVien.setText(lang.getString("shipment.lbl.employee"));
		lblGiaNhap.setText(lang.getString("shipment.lbl.cost_price"));
		lblTrangThai.setText(lang.getString("shipment.lbl.status"));

		lblGiaBan.setText(lang.getString("shipment.lbl.sale_price"));
		lblThanhTien.setText(lang.getString("shipment.lbl.total"));

		btn.addActionListener(e -> {
			updateShipment(shipment, txtNgaySX, txtHanSuDung, cboTrangThai, cmbNCC, txtSoLuongNhap, txtSoLuongCon,
					txtGiaNhap, txtGiaBan);
		});

		txtGiaNhap.addActionListener(e -> {
			txtGiaBan.selectAll();
			txtGiaBan.requestFocus();
		});

		txtGiaNhap.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				txtThanhTien.setText(String
						.valueOf(FormatUtil.formatVND(
								shipmentBUS.autoSetThanhTien(txtSoLuongNhap.getText(), txtGiaNhap.getText())))
						+ " VND");

				suggestGiaBan();
			}

			private void suggestGiaBan() {
				try {
					Product product = shipment.getProduct();
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

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtThanhTien.setText(String
						.valueOf(FormatUtil.formatVND(
								shipmentBUS.autoSetThanhTien(txtSoLuongNhap.getText(), txtGiaNhap.getText())))
						+ " VND");

				suggestGiaBan();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		txtSoLuongNhap.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				txtThanhTien.setText(String
						.valueOf(FormatUtil.formatVND(
								shipmentBUS.autoSetThanhTien(txtSoLuongNhap.getText(), txtGiaNhap.getText())))
						+ " VND");
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				txtThanhTien.setText(String
						.valueOf(FormatUtil.formatVND(
								shipmentBUS.autoSetThanhTien(txtSoLuongNhap.getText(), txtGiaNhap.getText())))
						+ " VND");
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});

		pnlBody.setBackground(Color.WHITE);
		pnlContent.add(pnlTitle, BorderLayout.NORTH);
		pnlContent.add(pnlBody, BorderLayout.CENTER);

	}

	private void updateShipment(Batch shipment, JTextField txtNgaySX, JTextField txtHanSuDung,
			JComboBox<String> cboTrangThai, JComboBox<NhaCungCap> cmbNCC, JTextField txtSoLuongNhap,
			JTextField txtSoLuongCon, JTextField txtGiaNhap, JTextField txtGiaBan) {

		if (cboTrangThai.getItemCount() == 1) {
			JOptionPane.showMessageDialog(this, "Lô thuốc này không thể cập nhật vì đã bán hết hoặc đã hết hạn.");
			return;
		}

		try {

			LocalDate ngaySX = FormatUtil.convertStringToDate(txtNgaySX.getText()); // dd/MM/yyyy
			LocalDate hanSD = FormatUtil.convertStringToDate(txtHanSuDung.getText()); // dd/MM/yyyy

			shipmentBUS.checkNgay(ngaySX, hanSD);

			long months = ChronoUnit.MONTHS.between(LocalDate.now(), hanSD);
			if (months <= 18) {
				int confirm = JOptionPane.showConfirmDialog(this, "Hạn sử dụng của lô thuốc này chỉ còn: " + months
						+ " tháng.\n Bạn có chắc chắn muốn nhập lô ?");
				if (confirm != JOptionPane.YES_OPTION)
					return;
			}

			shipment.setNgaySanXuat(ngaySX);
			shipment.setHanSuDung(hanSD);

			shipment.setNhaCungCap((NhaCungCap) cmbNCC.getSelectedItem());

			try {
				shipment.setSoLuongNhap(Integer.parseInt(txtSoLuongNhap.getText().trim()));

				shipment.setSoLuongCon(Integer.parseInt(txtSoLuongCon.getText().trim()));

				shipment.setGiaNhap(Double.parseDouble(txtGiaNhap.getText().trim()));

				shipment.setGiaBan(Double.parseDouble(txtGiaBan.getText().trim()));

			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Các trường số lượng và giá phải là số hợp lệ!");
			}

			shipmentBUS.checkGiaAndSoLuong(shipment.getGiaNhap(), shipment.getGiaBan(), shipment.getSoLuongCon(),
					shipment.getSoLuongNhap());

			shipment.setThanhTienNhap();

			shipment.setShipmentStatus(new BatchStatus(cboTrangThai.getSelectedIndex() + 1));

			shipmentBUS.updateShipment(shipment);

			JOptionPane.showMessageDialog(this, "Chỉnh sửa thông tin lô thuốc thành công!", "Success",
					JOptionPane.INFORMATION_MESSAGE);

			loadData(shipmentBUS.getAllShipmentByPage(pagination, option));

		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
		}
	}

	private void initMainBody() {
		pnlMain.setLayout(new BorderLayout());

		// Tạo dòng tiêu đề như table
		pnlMainHeader = new JPanel();
		pnlMainHeader.setLayout(new MigLayout("wrap, fillx, insets 10",
				"[fill][fill][fill][fill][fill][fill][fill][fill][fill]", "[]"));
		pnlMainHeader.setBackground(new Color(238, 238, 238));

		JLabel lblIcon = new JLabel();
		pnlMainHeader.add(lblIcon);

		lblHeaderSoLo = new JLabel();
		pnlMainHeader.add(lblHeaderSoLo);

		lblHeaderProd = new JLabel();
		pnlMainHeader.add(lblHeaderProd);

		lblHeaderNgayNhap = new JLabel();
		pnlMainHeader.add(lblHeaderNgayNhap);

		lblHanSuDung = new JLabel();
		pnlMainHeader.add(lblHanSuDung);

		lblSoLuongNhap = new JLabel();
		pnlMainHeader.add(lblSoLuongNhap);

		lblSoLuongCon = new JLabel();
		pnlMainHeader.add(lblSoLuongCon);

		lblGiaBan = new JLabel();
		pnlMainHeader.add(lblGiaBan);

		JLabel lblTrangThai = new JLabel();
		pnlMainHeader.add(lblTrangThai);

		JLabel lbl[] = { lblHeaderSoLo, lblHeaderProd, lblHeaderNgayNhap, lblHanSuDung, lblSoLuongNhap, lblSoLuongCon,
				lblGiaBan, lblTrangThai };

		for (JLabel label : lbl) {
			label.setForeground(new Color(119, 119, 119));
			label.setFont(new Font("Segoe UI", Font.BOLD, 14));
		}

		// phần main ở dưới, tạo giống từng row của table
		pnlMainBody = new JPanel();
		pnlMainBody.setBackground(Color.WHITE);
		pnlMainBody.setLayout(new MigLayout("wrap, fillx, aligny top", "[fill]", ""));

		pnlPage = new JPanel();
		pnlPage.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

		pnlMain.add(pnlMainHeader, BorderLayout.NORTH);
		pnlMain.add(new JScrollPane(pnlMainBody), BorderLayout.CENTER);
		pnlMain.add(pnlPage, BorderLayout.SOUTH);

		JScrollPane scroll = (JScrollPane) pnlMainBody.getParent().getParent();
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
				"background:$Table.background;track:$Table.background;trackArc:999");
		scroll.getVerticalScrollBar().setUnitIncrement(30);

	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		lblTimKiem = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		txtSearchSoLo = new javax.swing.JTextField();
		txtSearchMaSP = new javax.swing.JTextField();
		cmbOption = new javax.swing.JComboBox<>();
		jLabel5 = new javax.swing.JLabel();
		lblTrangThai = new javax.swing.JLabel();
		radDangLuuHanh = new javax.swing.JRadioButton();
		radStatusAll = new javax.swing.JRadioButton();
		radDaBanHet = new javax.swing.JRadioButton();
		radDaHuy = new javax.swing.JRadioButton();
		radDaHetHan = new javax.swing.JRadioButton();
		jLabel6 = new javax.swing.JLabel();
		lblThoiGian = new javax.swing.JLabel();
		lblNgayNhap = new javax.swing.JLabel();
		radTimeAll = new javax.swing.JRadioButton();
		radToday = new javax.swing.JRadioButton();
		radWeek = new javax.swing.JRadioButton();
		radMonth = new javax.swing.JRadioButton();
		radDiff = new javax.swing.JRadioButton();
		txtTimeEnd = new javax.swing.JTextField();
		txtTimeStart = new javax.swing.JTextField();
		jLabel2 = new javax.swing.JLabel();
		lblHanSuDung = new javax.swing.JLabel();
		radHetHan7Ngay = new javax.swing.JRadioButton();
		radHetHan30Ngay = new javax.swing.JRadioButton();
		radHetHan3Thang = new javax.swing.JRadioButton();
		jPanel2 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		lblQuanLyLoHang = new javax.swing.JLabel();
		btnExport = new javax.swing.JButton();
		btnAdd = new javax.swing.JButton();
		btnImport = new javax.swing.JButton();
		txtTotalRecord = new javax.swing.JTextField();
		jLabel1 = new javax.swing.JLabel();
		pnlMain = new javax.swing.JPanel();

		setPreferredSize(new java.awt.Dimension(1075, 768));
		setLayout(new java.awt.BorderLayout());

		jPanel1.setBackground(new java.awt.Color(225, 225, 225));
		jPanel1.setPreferredSize(new java.awt.Dimension(370, 700));

		lblTimKiem.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		lblTimKiem.setForeground(new java.awt.Color(51, 51, 255));
		lblTimKiem.setText("Tìm kiếm");

		jLabel4.setIcon(new FlatSVGIcon("icon/svg/filter-1.svg", 20, 20)); // NOI18N
		jLabel4.setPreferredSize(new java.awt.Dimension(35, 35));

		jLabel5.setIcon(new FlatSVGIcon("icon/svg/filter-1.svg", 20, 20)); // NOI18N
		jLabel5.setPreferredSize(new java.awt.Dimension(35, 35));

		lblTrangThai.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		lblTrangThai.setForeground(new java.awt.Color(51, 51, 255));
		lblTrangThai.setText("Lọc theo trạng thái");

		ButtonGroup group1 = new ButtonGroup();
		group1.add(radStatusAll);
		group1.add(radDangLuuHanh);
		group1.add(radDaBanHet);
		group1.add(radDaHuy);
		group1.add(radDaHetHan);

		jLabel6.setIcon(new FlatSVGIcon("icon/svg/filter-1.svg", 20, 20)); // NOI18N
		jLabel6.setPreferredSize(new java.awt.Dimension(35, 35));

		lblThoiGian.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		lblThoiGian.setForeground(new java.awt.Color(51, 51, 255));

		lblNgayNhap.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

		ButtonGroup group2 = new ButtonGroup();
		group2.add(radTimeAll);
		group2.add(radToday);
		group2.add(radMonth);
		group2.add(radWeek);
		group2.add(radDiff);
		group2.add(radHetHan30Ngay);
		group2.add(radHetHan7Ngay);
		group2.add(radHetHan3Thang);

		radTimeAll.setSelected(true);
		radStatusAll.setSelected(true);

		jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		jLabel2.setText("...");

		lblHanSuDung.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

		txtTimeStart.setEditable(false);
		txtTimeEnd.setEditable(false);

		dateFrom = new JDateChooser();
		dateTo = new JDateChooser();

		dateFrom.setDateFormatString("dd/MM/yyyy");
		dateTo.setDateFormatString("dd/MM/yyyy");

		dateFrom.setBounds(0, 0, 150, 30);
		dateTo.setBounds(0, 0, 150, 30);

		txtTimeStart.add(dateFrom);
		txtTimeEnd.add(dateTo);

		dateFrom.setVisible(false);
		dateTo.setVisible(false);

		txtTimeStart.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/calendar.svg", 0.4f));

		txtTimeEnd.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/calendar.svg", 0.4f));

		txtSearchSoLo.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/search.svg", 0.4f));

		txtSearchMaSP.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/search.svg", 0.4f));

		cmbOption.setModel(new javax.swing.DefaultComboBoxModel<>(
				new String[] { "Sắp xếp theo ngày nhập", "Sắp xếp theo hạn sử dụng" }));

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel1Layout.createSequentialGroup()
										.addComponent(txtTimeStart, javax.swing.GroupLayout.PREFERRED_SIZE, 156,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(18, 18, 18).addComponent(jLabel2)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(
												txtTimeEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(txtSearchSoLo)
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(lblTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 149,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(txtSearchMaSP)
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(lblTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 175,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(radDangLuuHanh).addComponent(radDaBanHet).addComponent(radDaHuy)
										.addGroup(jPanel1Layout.createSequentialGroup()
												.addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(lblThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 175,
														javax.swing.GroupLayout.PREFERRED_SIZE))
										.addComponent(lblNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 106,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(radDiff)
										.addComponent(
												lblHanSuDung, javax.swing.GroupLayout.PREFERRED_SIZE, 106,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(radHetHan7Ngay).addComponent(radHetHan3Thang)
										.addComponent(radHetHan30Ngay)
										.addComponent(cmbOption, 0, javax.swing.GroupLayout.DEFAULT_SIZE,
												Short.MAX_VALUE)
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
												jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING,
																false)
														.addGroup(jPanel1Layout.createSequentialGroup()
																.addComponent(radStatusAll)
																.addPreferredGap(
																		javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																		javax.swing.GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																.addComponent(radDaHetHan))
														.addGroup(jPanel1Layout.createSequentialGroup()
																.addGroup(jPanel1Layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(radTimeAll).addComponent(radWeek))
																.addGap(111, 111, 111)
																.addGroup(jPanel1Layout.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(radMonth)
																		.addComponent(radToday))))
														.addGap(73, 73, 73)))
										.addGap(0, 8, Short.MAX_VALUE)))
						.addContainerGap()));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(lblTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(cmbOption, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(txtSearchSoLo, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(txtSearchMaSP, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(radStatusAll).addComponent(radDaHetHan))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(radDangLuuHanh).addGap(18, 18, 18).addComponent(radDaBanHet).addGap(18, 18, 18)
						.addComponent(radDaHuy).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblThoiGian, javax.swing.GroupLayout.PREFERRED_SIZE, 31,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 24,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(radTimeAll).addComponent(radToday))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(radWeek).addComponent(radMonth))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(radDiff)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtTimeStart, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel2).addComponent(txtTimeEnd, javax.swing.GroupLayout.PREFERRED_SIZE,
										30, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(lblHanSuDung, javax.swing.GroupLayout.PREFERRED_SIZE, 24,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(radHetHan7Ngay)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(radHetHan30Ngay)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(radHetHan3Thang)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		add(jPanel1, java.awt.BorderLayout.LINE_END);

		jPanel2.setBackground(new java.awt.Color(204, 204, 255));
		jPanel2.setLayout(new java.awt.BorderLayout());

		jPanel3.setBackground(new java.awt.Color(255, 255, 255));
		jPanel3.setPreferredSize(new java.awt.Dimension(196, 50));

		lblQuanLyLoHang.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		lblQuanLyLoHang.setForeground(new java.awt.Color(51, 51, 255));

		btnExport.setBackground(new java.awt.Color(0, 0, 255));
		btnExport.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnExport.setForeground(new java.awt.Color(255, 255, 255));
		btnExport.setText("Export");
		btnExport.setPreferredSize(new java.awt.Dimension(90, 30));
		btnExport.setIcon(new FlatSVGIcon("icon/svg/export.svg", 0.30f));

		btnAdd.setBackground(new java.awt.Color(0, 0, 255));
		btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnAdd.setForeground(new java.awt.Color(255, 255, 255));
		btnAdd.setPreferredSize(new java.awt.Dimension(90, 30));
		btnAdd.setIcon(new FlatSVGIcon("icon/svg/add.svg", 0.30f));

		btnImport.setBackground(new java.awt.Color(0, 0, 255));
		btnImport.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnImport.setForeground(new java.awt.Color(255, 255, 255));
		btnImport.setText("Import");
		btnImport.setPreferredSize(new java.awt.Dimension(90, 30));
		btnImport.setIcon(new FlatSVGIcon("icon/svg/edit.svg", 0.35f));

		jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		jLabel1.setText("Total record");

		txtTotalRecord.setEditable(false);

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addGap(15, 15, 15)
						.addComponent(lblQuanLyLoHang, javax.swing.GroupLayout.PREFERRED_SIZE, 250,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
						.addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 150,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(26, 26, 26)
						.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 89,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(lblQuanLyLoHang)
								.addComponent(txtTotalRecord, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel1))
						.addContainerGap(9, Short.MAX_VALUE)));

		jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_START);

		javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
		pnlMain.setLayout(pnlMainLayout);
		pnlMainLayout.setHorizontalGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 196, Short.MAX_VALUE));
		pnlMainLayout.setVerticalGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 664, Short.MAX_VALUE));

		// Tạo phần body quản lý danh sách voucher
		initMainBody();

		jPanel2.add(pnlMain, java.awt.BorderLayout.CENTER);

		add(jPanel2, java.awt.BorderLayout.CENTER);
	}

	private javax.swing.JButton btnAdd;
	private javax.swing.JButton btnExport;
	private javax.swing.JButton btnImport;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JLabel lblHanSuDung;
	private javax.swing.JLabel lblNgayNhap;
	private javax.swing.JLabel lblQuanLyLoHang;
	private javax.swing.JLabel lblThoiGian;
	private javax.swing.JLabel lblTimKiem;
	private javax.swing.JLabel lblTrangThai;
	private javax.swing.JPanel pnlMain;
	private javax.swing.JRadioButton radDaBanHet;
	private javax.swing.JRadioButton radDaHetHan;
	private javax.swing.JRadioButton radDaHuy;
	private javax.swing.JRadioButton radDangLuuHanh;
	private javax.swing.JRadioButton radDiff;
	private javax.swing.JRadioButton radHetHan30Ngay;
	private javax.swing.JRadioButton radHetHan3Thang;
	private javax.swing.JRadioButton radHetHan7Ngay;
	private javax.swing.JRadioButton radMonth;
	private javax.swing.JRadioButton radStatusAll;
	private javax.swing.JRadioButton radTimeAll;
	private javax.swing.JRadioButton radToday;
	private javax.swing.JRadioButton radWeek;
	private javax.swing.JTextField txtSearchMaSP;
	private javax.swing.JTextField txtSearchSoLo;
	private javax.swing.JTextField txtTimeEnd;
	private javax.swing.JTextField txtTimeStart;
	private javax.swing.JTextField txtTotalRecord;
	private javax.swing.JComboBox<String> cmbOption;

}
