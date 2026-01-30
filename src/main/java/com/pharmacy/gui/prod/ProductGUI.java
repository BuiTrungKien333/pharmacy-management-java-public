package com.pharmacy.gui.prod;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pharmacy.bus.Auth;
import com.pharmacy.bus.ProductBUS;
import com.pharmacy.config.Translator;
import com.pharmacy.entity.Product;
import com.pharmacy.exception.ResourceNotFoundException;
import com.pharmacy.utils.ExcelExporterUtil;
import com.pharmacy.utils.HelperImageIcon;
import com.pharmacy.utils.Pagination;

import net.miginfocom.swing.MigLayout;

public class ProductGUI extends javax.swing.JPanel {

	private int currentType = 0; // 0: tất cả, 1: thuốc, 2: thực phẩm, 3: dụng cụ

	private int currentFilter = 0; // 0: tất cả, 1-5: theo cmbField

	private ProductBUS productBUS;

	private Pagination pagination;

	private static final int pageSize = 20;

	private Timer searchTimer;

	private AddProdDialog addProdDialog;

	private EditProdDialog editProdDialog;

	// tỉ lệ scale cho 1 cái cart item
	private static final int WIDTH_CART = 260;
	private static final int HEIGHT_CART = 350;

	// tỉ lệ scale cho cái ảnh
	private static final int WIDTH_ITEM = 240;
	private static final int HEIGHT_ITEM = 230;

	private static final String activeButtonStyle = "" + "background:$primary;" + "foreground:$white;" + "arc:20;"
			+ "margin:5,10,5,10;" + "hoverBackground:$primary;" + "hoverForeground:$white;" + "font:$h5.font";

	private static final String normalButtonStyle = "" + "background:$white;" + "foreground:$black;" + "arc:20;"
			+ "margin:5,10,5,10;" + "borderColor:$gray;" + "hoverBackground:#E0E0E0;" + "hoverForeground:$black;"
			+ "hoverBorderColor:#007BFF;" + "font:$h5.font;";

	private static final String activePageButtonStyle = "" + "background:$primary;" + "foreground:$white;" + "arc:999;"
			+ "margin:5,5,5,5;" + "hoverBackground:$primary;" + "hoverForeground:$white;" + "font:$h6.font;";

	private static final String normalPageButtonStyle = "" + "background:$white;" + "foreground:$black;" + "arc:999;"
			+ "margin:5,5,5,5;" + "borderColor:$gray;" + "hoverBackground:#E0E0E0;" + "hoverForeground:$black;"
			+ "hoverBorderColor:#007BFF;" + "font:$h6.font;";

	private JButton btnAll;
	private JButton btnTool;
	private JButton btnMedicine;
	private JButton btnFood;
	private JPanel pnlCategory;
	private JPanel pnlProd;
	private JButton btnRefresh;

	private JTextField txtTotalPage;

	private JPanel pnlPage;

	public ProductGUI() {

		this.productBUS = new ProductBUS();

		initComponents();

		applyPermissions();

		Translator.getInstance().addLanguageChangeListener(locale -> {
			SwingUtilities.invokeLater(this::updateTexts);
		});

		updateTexts();

		initEventForButton();

		initSearchEvent();

		loadRefreshDataPage();

		SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
	}

	public void applyPermissions() {
		btnAdd.setEnabled(Auth.hasPermission("PRODUCT_ADD"));
		btnExport.setEnabled(Auth.hasPermission("PRODUCT_EXPORT"));
	}

	private void loadRefreshDataPage() {
		int total = productBUS.getTotalRecord();
		pagination = new Pagination(1, pageSize, total);
		txtTotalPage.setText(String.valueOf(total));

		initPage(productBUS.getAllProdByPage(pagination));
	}

	private void initPage(List<Product> list) {
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

		showProduct(list);
	}

	public void showPage(int pageNum) {
		if (pageNum == 0 || pageNum > pagination.getTotalPages())
			pageNum = 1;
		
		pagination.setPageNumber(pageNum);
		updatePageButtons(pageNum);
		showProduct(productBUS.getFilteredProducts(pagination, currentType, currentFilter));
	}

	public Pagination getCurrentPage() {
		return pagination;
	}

	public void showFirstPage() {
		showPage(1);
	}

	public void showLastPage() {
		showPage(pagination.getTotalPages());
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

	private void initSearchEvent() {
		// search by barcode
		txtSearch.addActionListener(e -> {
			if (radBarcode.isSelected()) {
				String barcode = txtSearch.getText().trim();
				if (barcode.isEmpty())
					return;

				try {
					Product prod = productBUS.getProdByBarcode(barcode);
					showProduct(Arrays.asList(prod));

				} catch (ResourceNotFoundException ex) {
					JOptionPane.showMessageDialog(this, ex.getMessage());
				} finally {
					SwingUtilities.invokeLater(() -> {
						txtSearch.setText("");
						txtSearch.requestFocusInWindow();
					});
				}
			}
		});

		// search by product name
		// khi người dùng nhập thì nó dùng lại 100ms rồi mới bắt đầu search
		searchTimer = new Timer(100, e -> {
			performSearch();
		});
		searchTimer.setRepeats(false);

		txtSearch.getDocument().addDocumentListener(new DocumentListener() {

			private void handleSearch() {
				searchTimer.restart();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				handleSearch();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				handleSearch();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
	}

	private void performSearch() {
		if (radProdName.isSelected()) {
			String keyword = txtSearch.getText().toLowerCase();
			if (keyword.isEmpty()) {
				showProduct(productBUS.getFilteredProducts(pagination, currentType, currentFilter));
			} else {
				if (keyword.length() >= 2)
					showProduct(productBUS.getFilteredProductsAndSearchByDB(currentType, currentFilter, keyword));
			}
		}
	}

	private void initEventForButton() {

		btnAdd.addActionListener(e -> addProd());

		btnExport.addActionListener(e -> exportExcelProd());

		btnRefresh.addActionListener(e -> refreshDataProd());

		btnAll.addActionListener(e -> {
			currentType = 0;
			applyFilters();
			setActiveBtn(btnAll);
		});

		btnMedicine.addActionListener(e -> {
			currentType = 1;
			applyFilters();
			setActiveBtn(btnMedicine);
		});

		btnFood.addActionListener(e -> {
			currentType = 2;
			applyFilters();
			setActiveBtn(btnFood);
		});

		btnTool.addActionListener(e -> {
			currentType = 3;
			applyFilters();
			setActiveBtn(btnTool);
		});

		cmbField.addActionListener(e -> {
			currentFilter = cmbField.getSelectedIndex();
			applyFilters();
		});

		radBarcode.addActionListener(e -> {
			txtSearch.setText("");
			SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
		});

		radProdName.addActionListener(e -> {
			txtSearch.setText("");
			SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
		});

	}

	private void applyFilters() {
		int totalRecords = 0;
		List<Product> list;
		if (currentType == 0 && currentFilter == 0) {
			list = productBUS.getAllProdByPage(pagination);
			totalRecords = productBUS.getTotalRecord();
		} else {
			list = productBUS.getFilteredProducts(pagination, currentType, currentFilter);
			totalRecords = productBUS.getTotalRecordFiltered();
		}

		txtTotalPage.setText(String.valueOf(totalRecords));
		
		pagination = new Pagination(1, pageSize, totalRecords);

		initPage(list);
	}

	public void showProduct(List<Product> list) {
		pnlProd.setVisible(false);
		pnlProd.removeAll();

		list.forEach(item -> {
			pnlProd.add(initCardItem(item, WIDTH_CART, HEIGHT_CART));
		});

		pnlProd.revalidate();
		pnlProd.repaint();
		pnlProd.setVisible(true);
	}

	private void setActiveBtn(JButton activeBtn) {
		btnAll.putClientProperty(FlatClientProperties.STYLE, normalButtonStyle);
		btnFood.putClientProperty(FlatClientProperties.STYLE, normalButtonStyle);
		btnMedicine.putClientProperty(FlatClientProperties.STYLE, normalButtonStyle);
		btnTool.putClientProperty(FlatClientProperties.STYLE, normalButtonStyle);

		activeBtn.putClientProperty(FlatClientProperties.STYLE, activeButtonStyle);
	}

	public void exportExcelProd() {
		List<Product> list = productBUS.getAllProdToExport(currentType, currentFilter);
		String[] headers = { "Mã SP", "Tên SP", "Barcode", "Hoạt chất & Hàm lượng", "Số đăng kí", "Tổng số lượng",
				"DVT", "Loại SP", "Trạng thái" };

		List<Object[]> data = new ArrayList<>();
		for (Product p : list) {
			data.add(p.getObjects());
		}

		JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

		FileDialog fileDialog = new FileDialog(parentFrame, "Xuất danh sách sản phẩm ra Excel", FileDialog.SAVE);

		fileDialog.setFile("Danh_Sach_San_Pham.xlsx");

		fileDialog.setFilenameFilter((dir, name) -> name.endsWith(".xlsx"));

		fileDialog.setVisible(true);

		String directory = fileDialog.getDirectory();
		String filename = fileDialog.getFile();

		if (filename != null && directory != null) {
			String filePath = directory + filename;

			if (!filePath.toLowerCase().endsWith(".xlsx"))
				filePath += ".xlsx";

			try {
				ExcelExporterUtil.exportDataToExcel(filePath, "DanhSachSanPham", headers, data);
				JOptionPane.showMessageDialog(this, "Xuất file thành công!");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi xuất file!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void refreshDataProd() {
		txtSearch.setText("");
		SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
		cmbField.setSelectedIndex(0);
		currentType = 0;
		currentFilter = 0;
		radBarcode.setSelected(true);

		loadRefreshDataPage();
		setActiveBtn(btnAll);
	}

	private void addProd() {
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

		JPanel glass = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(new Color(0, 0, 0, 120));
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};
		glass.setOpaque(false);

		frame.setGlassPane(glass);
		glass.setVisible(true);

		if (addProdDialog == null)
			addProdDialog = new AddProdDialog(frame, true, this, productBUS);
		else
			addProdDialog.clearData();

		addProdDialog.setLocationRelativeTo(frame);
		addProdDialog.setVisible(true);

		glass.setVisible(false);
	}

	private void editDataForProd(String barcode) {
		Product product = productBUS.getProdByBarcode(barcode);

		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

		JPanel glass = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setColor(new Color(0, 0, 0, 120));
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.dispose();
			}
		};
		glass.setOpaque(false);

		frame.setGlassPane(glass);
		glass.setVisible(true);

		editProdDialog = new EditProdDialog(frame, true, product, this, productBUS);
		editProdDialog.setLocationRelativeTo(frame);
		editProdDialog.setVisible(true);

		glass.setVisible(false);
	}

	private void updateTexts() {
		Translator lang = Translator.getInstance();

		jLabel1.setText(lang.getString("prod.title"));
		btnAdd.setText(lang.getString("prod.btn.add"));
		jLabel2.setText(lang.getString("prod.lbl.status"));
		btnAll.setText(lang.getString("prod.btn.all"));
		btnMedicine.setText(lang.getString("prod.btn.medicine"));
		btnTool.setText(lang.getString("prod.btn.tool"));
		btnFood.setText(lang.getString("prod.btn.food"));
	}

	private void initComponents() {

		pnlTop = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		txtSearch = new javax.swing.JTextField();
		btnAdd = new javax.swing.JButton();
		cmbField = new javax.swing.JComboBox<>();
		jLabel2 = new javax.swing.JLabel();
		pnlMain = new javax.swing.JPanel();
		btnExport = new javax.swing.JButton();
		btnRefresh = new javax.swing.JButton();
		radBarcode = new javax.swing.JRadioButton();
		radProdName = new javax.swing.JRadioButton();

		setLayout(new java.awt.BorderLayout());

		txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
				Translator.getInstance().getString("prod.text.search"));
		txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/search.svg", 0.4f));

		ButtonGroup group = new ButtonGroup();
		group.add(radBarcode);
		group.add(radProdName);

		radBarcode.setText("Barcode");

		radProdName.setText("Prod name");

		radBarcode.setSelected(true);

		pnlTop.setBackground(new java.awt.Color(244, 248, 250));

		jLabel1.setBackground(new java.awt.Color(51, 51, 255));
		jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

		btnAdd.setBackground(new java.awt.Color(51, 153, 255));
		btnAdd.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
		btnAdd.setForeground(new java.awt.Color(255, 255, 255));
		btnAdd.setText("Thêm sản phẩm");
		btnAdd.setIcon(new FlatSVGIcon("icon/svg/add.svg", 0.30f));

		btnRefresh.setBackground(new java.awt.Color(51, 153, 255));
		btnRefresh.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
		btnRefresh.setForeground(new java.awt.Color(255, 255, 255));
		btnRefresh.setText("Refresh");
		btnRefresh.setIcon(new FlatSVGIcon("icon/svg/refresh.svg", 20, 20));

		btnExport.setBackground(new java.awt.Color(51, 153, 255));
		btnExport.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
		btnExport.setForeground(new java.awt.Color(255, 255, 255));
		btnExport.setText("Export");
		btnExport.setIcon(new FlatSVGIcon("icon/svg/export.svg", 0.30f));

		cmbField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Đang hoạt động",
				"Ngừng kinh doanh", "Còn hàng", "Sắp hết hàng", "Đã hết hàng" }));

		jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

		javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
		pnlTop.setLayout(pnlTopLayout);
		pnlTopLayout.setHorizontalGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnlTopLayout.createSequentialGroup().addGap(12, 12, 12)
						.addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 365,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 203,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(pnlTopLayout.createSequentialGroup().addComponent(radProdName)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35,
												Short.MAX_VALUE)
										.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 71,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(pnlTopLayout.createSequentialGroup().addComponent(radBarcode).addGap(0, 0,
										Short.MAX_VALUE)))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(cmbField, javax.swing.GroupLayout.PREFERRED_SIZE, 170,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 140,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 140,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(btnExport,
								javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		pnlTopLayout.setVerticalGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(pnlTopLayout.createSequentialGroup().addContainerGap()
						.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(cmbField, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(20, Short.MAX_VALUE))
				.addGroup(pnlTopLayout.createSequentialGroup().addGap(27, 27, 27).addComponent(radBarcode)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(radProdName)
						.addContainerGap(19, Short.MAX_VALUE)));

		add(pnlTop, java.awt.BorderLayout.PAGE_START);

		pnlMain.setBackground(new java.awt.Color(255, 247, 247));

		javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
		pnlMain.setLayout(pnlMainLayout);
		pnlMainLayout.setHorizontalGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 959, Short.MAX_VALUE));
		pnlMainLayout.setVerticalGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 386, Short.MAX_VALUE));

		initPnlMain();

		add(pnlMain, java.awt.BorderLayout.CENTER);
	}

	private void initPnlMain() {
		// pnlMain
		pnlMain.setLayout(new MigLayout("wrap, fill", "[fill]", "[grow 0][fill][grow 0]"));
		pnlCategory = new JPanel();
		pnlMain.add(pnlCategory);
		pnlProd = new JPanel();
		pnlProd.setBackground(new Color(255, 247, 247));
		pnlMain.add(new JScrollPane(pnlProd));

		// pnlCategory
		// add button category
		btnAll = new JButton();
		btnMedicine = new JButton();
		btnTool = new JButton();
		btnFood = new JButton();

		btnAll.setPreferredSize(new Dimension(150, 35));
		btnMedicine.setPreferredSize(btnAll.getPreferredSize());
		btnTool.setPreferredSize(btnAll.getPreferredSize());
		btnFood.setPreferredSize(btnAll.getPreferredSize());

		btnAll.putClientProperty(FlatClientProperties.STYLE, activeButtonStyle);
		btnFood.putClientProperty(FlatClientProperties.STYLE, normalButtonStyle);
		btnMedicine.putClientProperty(FlatClientProperties.STYLE, normalButtonStyle);
		btnTool.putClientProperty(FlatClientProperties.STYLE, normalButtonStyle);

		JPanel pnlTotalPage = new JPanel();
		JLabel lblTotalPage = new JLabel("Total Record:");
		lblTotalPage.setFont(new java.awt.Font("Segoe UI", 1, 12));
		txtTotalPage = new JTextField();
		txtTotalPage.setPreferredSize(new Dimension(100, 35));
		txtTotalPage.setEditable(false);
		pnlTotalPage.add(lblTotalPage);
		pnlTotalPage.add(txtTotalPage);

		pnlCategory.setLayout(new MigLayout("wrap", "[fill][fill][fill][fill][]", "[grow 0]"));
		pnlCategory.add(btnAll);
		pnlCategory.add(btnMedicine);
		pnlCategory.add(btnFood);
		pnlCategory.add(btnTool);
		pnlCategory.add(pnlTotalPage, "pushx, align right");

		pnlProd.setLayout(new MigLayout("wrap 5, fill",
				"[fill, grow]15[fill, grow]15[fill, grow]15[fill, grow]15[fill, grow]", "[al top]"));

		JScrollPane scroll = (JScrollPane) pnlProd.getParent().getParent();
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
				"background:$Table.background;track:$Table.background;trackArc:999");
		scroll.getVerticalScrollBar().setUnitIncrement(30);

		pnlPage = new JPanel();
		pnlPage.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

		pnlMain.add(pnlPage);
	}

	private JPanel initCardItem(Product product, int width, int height) {
		JPanel pnl = new JPanel();
		pnl.setLayout(new MigLayout("wrap, fill", "[]", "[][][]"));

		JLabel lblImage = new JLabel(HelperImageIcon.scaleIcon(product.getAvatarUrl(), WIDTH_ITEM, HEIGHT_ITEM));

		JLabel name = new JLabel("<html><body style='width: 150px'>" + product.getTenSanPham() + "</body></html>");
		name.putClientProperty(FlatClientProperties.STYLE, "" + "font:$h5.font;");

		JPanel pnlBtn = new JPanel();
		JButton btnView = new JButton();
		btnView.setText("View");
		btnView.setBackground(new Color(36, 145, 255));
		btnView.setForeground(Color.WHITE);
		btnView.setFont(new Font("Roboto", Font.BOLD, 12));
		pnlBtn.add(btnView);

		JLabel lblQty = new JLabel(String.format("Qty: %s Viên", product.getTongSoLuong()));
		lblQty.setFont(new Font("Roboto", Font.ITALIC, 13));
		lblQty.setForeground(Color.BLACK);

		pnl.add(lblImage, "al center");
		pnl.add(name, "al center");
		pnl.add(lblQty, "gapright push");
		pnl.add(pnlBtn, "gapleft push");

		pnl.putClientProperty(FlatClientProperties.STYLE, "background:#ffffff;foreground:#1a1a1a;arc:20");
		pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

		Dimension cardSize = new Dimension(width, height);
		pnl.setPreferredSize(cardSize);
		pnl.setMinimumSize(cardSize);
		pnl.setMaximumSize(cardSize);

		btnView.addActionListener(e -> editDataForProd(product.getBarcode()));

		return pnl;
	}

	private javax.swing.JButton btnAdd;
	private javax.swing.JButton btnExport;
	private javax.swing.JComboBox<String> cmbField;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JPanel pnlMain;
	private javax.swing.JPanel pnlTop;
	private javax.swing.JTextField txtSearch;
	private javax.swing.JRadioButton radBarcode;
	private javax.swing.JRadioButton radProdName;

}