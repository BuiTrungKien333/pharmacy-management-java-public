package com.pharmacy.gui.voucher;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.math3.analysis.function.Cbrt;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.pharmacy.bus.VoucherBUS;
import com.pharmacy.entity.Customer;
import com.pharmacy.entity.CustomerRank;
import com.pharmacy.entity.Voucher;
import com.toedter.calendar.JDateChooser;

import net.miginfocom.swing.MigLayout;

public class VoucherGUI extends javax.swing.JPanel {

	private JDateChooser dateChooser;
	private JPanel pnlMainHeader;
	private JPanel pnlMainBody;
	private VoucherBUS voucherBus;
	private List<Voucher> list; 
	private Timer searchTimer;
	private JDateChooser dateFrom;
	private JDateChooser dateTo;

	public VoucherGUI() {
		voucherBus = new VoucherBUS(); 
		list = new ArrayList<Voucher>();
		
		initComponents();
		addData();
		
		initSearch();
		

		btnAdd.addActionListener(e -> {
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

			AddVoucherDialog sell = new AddVoucherDialog(frame, true, VoucherGUI.this);
			sell.setLocationRelativeTo(frame);
			sell.setVisible(true);

			glass.setVisible(false);
		});
		
		
	}

	private void initSearch() {

	    searchTimer = new Timer(200, e -> timKiemLive());
	    searchTimer.setRepeats(false);

	    txtSearch.getDocument().addDocumentListener(new DocumentListener() {
	        private void trigger() { restartTimer(); }

	        @Override public void insertUpdate(DocumentEvent e) { trigger(); }
	        @Override public void removeUpdate(DocumentEvent e) { trigger(); }
	        @Override public void changedUpdate(DocumentEvent e) { trigger(); }
	    });

	    ItemListener il = e -> restartTimer();

	    radConHieuLuc.addItemListener(il);
	    radHetHieuLuc.addItemListener(il);

	    radToday.addItemListener(il);
	    radWeek.addItemListener(il);
	    radMonth.addItemListener(il);
	    radDiff.addItemListener(il);
	    
	    dateFrom.getDateEditor().addPropertyChangeListener("date", evt -> restartTimer());
	    dateTo.getDateEditor().addPropertyChangeListener("date", evt -> restartTimer());

	}

	private void restartTimer() {
	    if (searchTimer.isRunning()) {
	        searchTimer.restart();
	    } else {
	        searchTimer.start();
	    }
	}

	private void timKiemLive() {

	    String keyword = txtSearch.getText().trim().toUpperCase();

	    int status = -1;
	    if (radConHieuLuc.isSelected()) status = 1;
	    else if (radHetHieuLuc.isSelected()) status = 0;

	    LocalDate from = null, to = null;

	    if (radToday.isSelected()) {
	        from = LocalDate.now();
	        to = LocalDate.now();
	    } else if (radWeek.isSelected()) {
	        from = LocalDate.now().minusDays(7);
	        to = LocalDate.now();
	    } else if (radMonth.isSelected()) {
	        from = LocalDate.now().minusDays(30);
	        to = LocalDate.now();
	    }
	    else if (radDiff.isSelected()) {

	        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	        try {
	            if (!txtTuNgay.getText().isEmpty())
	                from = LocalDate.parse(txtTuNgay.getText(), fmt);

	            if (!txtDenNgay.getText().isEmpty())
	                to = LocalDate.parse(txtDenNgay.getText(), fmt);

	        } catch (Exception ex) {
	            System.err.println("Lỗi định dạng ngày: " + ex.getMessage());
	            return; 
	        }
	    }

	    List<Voucher> ds = voucherBus.filter(keyword, status, from, to);

	    pnlMainBody.removeAll();
	    for (Voucher v : ds) {
	        pnlMainBody.add(createAccordionSection(v));
	    }
	    pnlMainBody.revalidate();
	    pnlMainBody.repaint();
	}



	
	public void addData() {
		pnlMainBody.removeAll();
	
		list = voucherBus.getAllVoucher(); 
		list.stream()
		.forEach(e -> pnlMainBody.add(createAccordionSection(e)));
		pnlMainBody.revalidate(); // cập nhật layout
	    pnlMainBody.repaint(); 
		
	}

	private void initComponents() {

		jPanel1 = new javax.swing.JPanel();
		jLabel2 = new javax.swing.JLabel();
		lblSearch = new javax.swing.JLabel();
		txtSearch = new javax.swing.JTextField();
		jLabel4 = new javax.swing.JLabel();
		lblStatus = new javax.swing.JLabel();
		radAllStatus = new javax.swing.JRadioButton();
		radConHieuLuc = new javax.swing.JRadioButton();
		radHetHieuLuc = new javax.swing.JRadioButton();
		jLabel6 = new javax.swing.JLabel();
		lblTime = new javax.swing.JLabel();
		radAllTime = new javax.swing.JRadioButton();
		radToday = new javax.swing.JRadioButton();
		radWeek = new javax.swing.JRadioButton();
		radMonth = new javax.swing.JRadioButton();
		radDiff = new javax.swing.JRadioButton();
		lblDenNgay = new javax.swing.JLabel();
		txtDenNgay = new javax.swing.JTextField();
		lblTuNgay = new javax.swing.JLabel();
		txtTuNgay = new javax.swing.JTextField();
		jPanel2 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		lblVoucher = new javax.swing.JLabel();
		btnExport = new javax.swing.JButton();
		btnImport = new javax.swing.JButton();
		btnAdd = new javax.swing.JButton();
		pnlMain = new javax.swing.JPanel();

		setLayout(new java.awt.BorderLayout());

		jPanel1.setBackground(new java.awt.Color(228, 228, 228));
		jPanel1.setPreferredSize(new java.awt.Dimension(350, 601));

		jLabel2.setIcon(new FlatSVGIcon("icon/svg/filter.svg", 25, 25)); // NOI18N
		jLabel2.setPreferredSize(new java.awt.Dimension(35, 35));

		lblSearch.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
		lblSearch.setForeground(new java.awt.Color(51, 51, 255));
		lblSearch.setText("Tìm kiếm");

		txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mã giảm giá cần tìm...");
		txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
				new FlatSVGIcon("icon/svg/search.svg", 0.4f));

		jLabel4.setIcon(new FlatSVGIcon("icon/svg/filter.svg", 25, 25)); // NOI18N
		jLabel4.setPreferredSize(new java.awt.Dimension(35, 35));

		lblStatus.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
		lblStatus.setForeground(new java.awt.Color(51, 51, 255));
		lblStatus.setText("Lọc theo trạng thái");

		ButtonGroup group1 = new ButtonGroup();
		group1.add(radAllStatus);
		group1.add(radConHieuLuc);
		group1.add(radHetHieuLuc);

		radAllStatus.setText("Tất cả");

		radConHieuLuc.setText("Còn hiệu lực");

		radHetHieuLuc.setText("Hết hiệu lực");

		jLabel6.setIcon(new FlatSVGIcon("icon/svg/filter.svg", 25, 25)); // NOI18N
		jLabel6.setPreferredSize(new java.awt.Dimension(35, 35));

		lblTime.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
		lblTime.setForeground(new java.awt.Color(51, 51, 255));
		lblTime.setText("Lọc theo thời gian");

		ButtonGroup group2 = new ButtonGroup();
		group2.add(radAllTime);
		group2.add(radToday);
		group2.add(radMonth);
		group2.add(radWeek);
		group2.add(radDiff);

		radAllTime.setText("Tất cả");

		radToday.setText("Ngày hôm nay");

		radWeek.setText("7 ngày trước");

		radMonth.setText("Tháng này");

		radDiff.setText("Lựa chọn khác");

		// Icon FlatLaf
		txtTuNgay.putClientProperty(
		        FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
		        new FlatSVGIcon("icon/svg/calendar.svg", 0.4f)
		);
		txtDenNgay.putClientProperty(
		        FlatClientProperties.TEXT_FIELD_TRAILING_ICON,
		        new FlatSVGIcon("icon/svg/calendar.svg", 0.4f)
		);

		dateFrom = new JDateChooser();
		dateTo = new JDateChooser();

		dateFrom.setDateFormatString("dd/MM/yyyy");
		dateTo.setDateFormatString("dd/MM/yyyy");

		dateFrom.setVisible(false);
		dateTo.setVisible(false);

		dateFrom.setBounds(txtTuNgay.getBounds());
		dateTo.setBounds(txtDenNgay.getBounds());

		this.add(dateFrom);
		this.add(dateTo);

		txtTuNgay.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        dateFrom.setVisible(true);
		        dateFrom.getCalendarButton().doClick();
		    }
		});

		txtDenNgay.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        dateTo.setVisible(true);
		        dateTo.getCalendarButton().doClick();
		    }
		});

		
		dateFrom.addPropertyChangeListener("date", evt -> {
		    if (dateFrom.getDate() != null) {
		        txtTuNgay.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateFrom.getDate()));
		        dateFrom.setVisible(false);
		    }
		});

		dateTo.addPropertyChangeListener("date", evt -> {
		    if (dateTo.getDate() != null) {
		        txtDenNgay.setText(new SimpleDateFormat("dd/MM/yyyy").format(dateTo.getDate()));
		        dateTo.setVisible(false);
		    }
		});


		lblDenNgay.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblDenNgay.setText("Đến ngày");

		lblTuNgay.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		lblTuNgay.setText("Từ ngày");

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 300,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(jPanel1Layout.createSequentialGroup()
								.addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(lblSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 149,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGroup(jPanel1Layout.createSequentialGroup()
								.addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 149,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addComponent(radAllStatus).addComponent(radConHieuLuc).addComponent(radHetHieuLuc)
						.addGroup(jPanel1Layout.createSequentialGroup()
								.addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 149,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addComponent(radAllTime).addComponent(radWeek).addComponent(radMonth).addComponent(radDiff)
						.addComponent(lblDenNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 79,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(txtDenNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 245,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 79,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(txtTuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 245,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(radToday)).addContainerGap(44, Short.MAX_VALUE)));
		jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jPanel1Layout.createSequentialGroup().addGap(15, 15, 15).addComponent(lblSearch)
								.addGap(11, 11, 11))
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
						.addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(radAllStatus)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(radConHieuLuc).addGap(18, 18, 18).addComponent(radHetHieuLuc)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addComponent(lblTime, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
								.addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(radAllTime)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(radToday)
						.addGap(18, 18, 18).addComponent(radWeek).addGap(18, 18, 18).addComponent(radMonth)
						.addGap(18, 18, 18).addComponent(radDiff)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(lblTuNgay)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(txtTuNgay, javax.swing.GroupLayout.PREFERRED_SIZE, 30,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(lblDenNgay)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(txtDenNgay,
								javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(190, Short.MAX_VALUE)));

		add(jPanel1, java.awt.BorderLayout.LINE_END);

		jPanel2.setBackground(new java.awt.Color(255, 153, 153));
		jPanel2.setLayout(new java.awt.BorderLayout());

		jPanel3.setBackground(new java.awt.Color(255, 255, 255));
		jPanel3.setPreferredSize(new java.awt.Dimension(725, 50));

		lblVoucher.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
		lblVoucher.setForeground(new java.awt.Color(51, 51, 255));
		lblVoucher.setText("Vouchers");

		btnExport.setBackground(new java.awt.Color(51, 102, 255));
		btnExport.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnExport.setForeground(new java.awt.Color(255, 255, 255));
		btnExport.setText("Export");
		btnExport.setIcon(new FlatSVGIcon("icon/svg/export.svg", 0.35f));

		btnImport.setBackground(new java.awt.Color(51, 102, 255));
		btnImport.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
		btnImport.setForeground(new java.awt.Color(255, 255, 255));
		btnImport.setText("Import");
		btnImport.setIcon(new FlatSVGIcon("icon/svg/edit.svg", 0.35f));

		btnAdd.setBackground(new java.awt.Color(51, 102, 255));
		btnAdd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		btnAdd.setForeground(new java.awt.Color(255, 255, 255));
		btnAdd.setText("Thêm mới");
		btnAdd.setIcon(new FlatSVGIcon("icon/svg/add.svg", 0.35f));

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addGap(17, 17, 17)
						.addComponent(lblVoucher, javax.swing.GroupLayout.PREFERRED_SIZE, 125,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 120,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(42, 42, 42)
						.addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(42, 42, 42).addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 100,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addContainerGap()
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(lblVoucher)
								.addComponent(btnExport, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnImport, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addContainerGap(8, Short.MAX_VALUE)));

		jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_START);

		javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
		pnlMain.setLayout(pnlMainLayout);
		pnlMainLayout.setHorizontalGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 725, Short.MAX_VALUE));
		pnlMainLayout.setVerticalGroup(pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGap(0, 718, Short.MAX_VALUE));

		// Tạo phần body quản lý danh sách voucher
		initMainBody();

		jPanel2.add(pnlMain, java.awt.BorderLayout.CENTER);

		add(jPanel2, java.awt.BorderLayout.CENTER);
	}

	private void initMainBody() {
		pnlMain.setLayout(new BorderLayout());

		// Tạo dòng tiêu đề như table
		pnlMainHeader = new JPanel();
		pnlMainHeader
				.setLayout(new MigLayout("wrap, fillx, insets 10", "[fill][fill][fill][fill][fill][fill][fill]", "[]"));
		pnlMainHeader.setBackground(new Color(238, 238, 238));

		JLabel lblIcon = new JLabel();
		pnlMainHeader.add(lblIcon);

		JLabel lblMaGiamGia = new JLabel();
		lblMaGiamGia.setText("Mã giảm giá");
		pnlMainHeader.add(lblMaGiamGia);

		JLabel lblGiaTri = new JLabel();
		lblGiaTri.setText("Giá trị");
		pnlMainHeader.add(lblGiaTri);

		JLabel lblNgayTao = new JLabel();
		lblNgayTao.setText("Ngày tạo");
		pnlMainHeader.add(lblNgayTao);

		JLabel lblHanSuDung = new JLabel();
		lblHanSuDung.setText("Hạn sử dụng");
		pnlMainHeader.add(lblHanSuDung);

		JLabel lblSoLuot = new JLabel();
		lblSoLuot.setText("Số lượt sử dụng");
		pnlMainHeader.add(lblSoLuot);

		JLabel lblTrangThai = new JLabel();
		lblTrangThai.setText("Trạng thái");
		pnlMainHeader.add(lblTrangThai);

		JLabel lbl[] = { lblMaGiamGia, lblGiaTri, lblNgayTao, lblHanSuDung, lblSoLuot, lblTrangThai };
		for (JLabel label : lbl) {
			label.setForeground(new Color(119, 119, 119));
			label.setFont(new Font("Segoe UI", Font.BOLD, 14));
		}

		// phần main ở dưới, tạo giống từng row của table
		pnlMainBody = new JPanel();
		pnlMainBody.setBackground(Color.WHITE);
		pnlMainBody.setLayout(new MigLayout("wrap, fillx, aligny top", "[fill]", ""));

//		for (int i = 0; i < 15; i++) {
//			pnlMainBody.add(createAccordionSection());
//		}

		pnlMain.add(pnlMainHeader, BorderLayout.NORTH);
		pnlMain.add(new JScrollPane(pnlMainBody), BorderLayout.CENTER);

		JScrollPane scroll = (JScrollPane) pnlMainBody.getParent().getParent();
		scroll.setBorder(BorderFactory.createEmptyBorder());
		scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
				"background:$Table.background;track:$Table.background;trackArc:999");
		scroll.getVerticalScrollBar().setUnitIncrement(30);
	}

	// tạo phần accordion như bootstrap
	private JPanel createAccordionSection(Voucher voucher) {
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BorderLayout());
		pnlMain.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		pnlMain.setBackground(Color.WHITE);

		// header, dòng tiêu đề nội dung khi chưa show xuống
		JPanel pnlHeader = new JPanel();
		pnlHeader.setBackground(Color.WHITE);
		pnlHeader
				.setLayout(new MigLayout("wrap, fillx, insets 10", "[fill][fill][fill][fill][fill][fill][fill]", "[]"));

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
		lblMaGiamGia.setText(voucher.getMaVoucher());
		pnlHeader.add(lblMaGiamGia);

		JLabel lblGiaTri = new JLabel();
		lblGiaTri.setText(voucher.getGiaTri() + "%");
		pnlHeader.add(lblGiaTri);

		JLabel lblNgayTao = new JLabel();
		lblNgayTao.setText(voucher.getNgayBatDau()+"");
		pnlHeader.add(lblNgayTao);

		JLabel lblHanSuDung = new JLabel();
		lblHanSuDung.setText(voucher.getNgayKetThuc()+"");
		pnlHeader.add(lblHanSuDung);

		JLabel lblSoLuot = new JLabel();
		lblSoLuot.setText(voucher.getSoLuotSuDungToiDa()+"");
		pnlHeader.add(lblSoLuot);

		JButton btnTrangThai = new JButton();
		String trangThai = "";
		if (voucher.getNgayKetThuc().isBefore(LocalDate.now())) 
			trangThai = "Hết hạn";
		else if (voucher.getSoLuotDaSuDung() >= voucher.getSoLuotSuDungToiDa())
			trangThai = "Ngừng hoạt động";
		else trangThai = "Đang hoạt động";
		
		btnTrangThai.setText(trangThai);
		btnTrangThai.setBackground(new Color(204, 255, 204));
		btnTrangThai.setForeground(new Color(0, 100, 0));
		btnTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnTrangThai.setFocusPainted(false);
		btnTrangThai.setContentAreaFilled(false);
		btnTrangThai.setBorderPainted(false);
		btnTrangThai.setOpaque(false);
		btnTrangThai.setHorizontalAlignment(SwingConstants.CENTER);

		pnlHeader.add(btnTrangThai);

		JLabel lbl[] = { lblMaGiamGia, lblGiaTri, lblNgayTao, lblHanSuDung, lblSoLuot };
		for (JLabel l : lbl) {
			l.setForeground(Color.BLACK);
			l.setFont(new Font("Segoe UI", Font.BOLD, 12));
		}

		// content, mở nội dung khi ấn nút show xuống
		JPanel pnlContent = new JPanel();
		initPnlContent(pnlContent, voucher);
		pnlContent.setVisible(false);

		btn.addActionListener(e -> {
			boolean value = pnlContent.isVisible();
			pnlContent.setVisible(!value);
			btn.setIcon(new FlatSVGIcon(String.format("icon/svg/%s.svg", value == true ? "down" : "top"), 15, 15));
		});

		pnlMain.add(pnlHeader, BorderLayout.NORTH);
		pnlMain.add(pnlContent, BorderLayout.CENTER);

		return pnlMain;
	}

	// Tạo phần content chứa nội dung chi tiết của voucher
	private void initPnlContent(JPanel pnlContent, Voucher voucher) {
		pnlContent.setLayout(new BorderLayout());
		JPanel pnlTitle = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
		pnlTitle.setBackground(new Color(217, 217, 217));
		JLabel lblTitle = new JLabel("Chi tiết");
		lblTitle.setForeground(Color.BLACK);
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
		pnlTitle.add(lblTitle);

		JPanel pnlBody = new JPanel();
		pnlBody.setLayout(new MigLayout("wrap 4, fillx, insets 10 30 10 30", "[left][grow,fill]50[left][grow,fill]",
				"[]10[]10[]10[]10[]10[]"));

		Font labelFont = new Font("Segoe UI", Font.BOLD, 12);

		JLabel lblMaGiamGia = new JLabel("Mã giảm giá:");
		lblMaGiamGia.setFont(labelFont);
		JTextField txtMaGiamGia = new JTextField();
		txtMaGiamGia.setText(voucher.getMaVoucher());

		JLabel lblGiaTri = new JLabel("Giá trị:");
		lblGiaTri.setFont(labelFont);
		JTextField txtGiaTri = new JTextField();
		txtGiaTri.setText(voucher.getGiaTri()+ "%");

		pnlBody.add(lblMaGiamGia);
		pnlBody.add(txtMaGiamGia);
		pnlBody.add(lblGiaTri);
		pnlBody.add(txtGiaTri);

		JLabel lblNgayBatDau = new JLabel("Ngày bắt đầu:");
		lblNgayBatDau.setFont(labelFont);
		JTextField txtNgayBatDau = new JTextField();
		
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		txtNgayBatDau.setText(voucher.getNgayBatDau().format(df));

		JLabel lblNgayKetThuc = new JLabel("Ngày kết thúc:");
		lblNgayKetThuc.setFont(labelFont);
		JTextField txtNgayKetThuc = new JTextField();
		txtNgayKetThuc.setText(voucher.getNgayKetThuc().format(df));

		pnlBody.add(lblNgayBatDau);
		pnlBody.add(txtNgayBatDau);
		pnlBody.add(lblNgayKetThuc);
		pnlBody.add(txtNgayKetThuc);

		JLabel lblGiamToiDa = new JLabel("Giảm tối đa:");
		lblGiamToiDa.setFont(labelFont);
		JTextField txtGiamToiDa = new JTextField();
		txtGiamToiDa.setText(voucher.getGiamToiDa()+"");

		JLabel lblDaSuDung = new JLabel("Số lượt đã sử dụng:");
		lblDaSuDung.setFont(labelFont);
		JTextField txtDaSuDung = new JTextField();
		txtDaSuDung.setText(voucher.getSoLuotDaSuDung()+"");
		txtDaSuDung.setEditable(false);

		pnlBody.add(lblGiamToiDa);
		pnlBody.add(txtGiamToiDa);
		pnlBody.add(lblDaSuDung);
		pnlBody.add(txtDaSuDung);

		JLabel lblHangTV = new JLabel("Điều kiện hạng thành viên:");
		lblHangTV.setFont(labelFont);
		JComboBox<String> cboHangTV = new JComboBox<>(
				new String[] { "Vô hạng", "Thành viên bạc", "Thành viên vàng" });
		
//		cboHangTV.setSelectedIndex(voucher.getCustomerRank().getId() - 1);
		int idx = voucher.getCustomerRank().getId() - 1;

		if (idx >= 0 && idx < cboHangTV.getItemCount()) {
		    cboHangTV.setSelectedIndex(idx);
		} else {
		    cboHangTV.setSelectedIndex(0); // fallback
		}


		JLabel lblSuDungToiDa = new JLabel("Số lượt tối đa:");
		lblSuDungToiDa.setFont(labelFont);
		JTextField txtSuDungToiDa = new JTextField();
		txtSuDungToiDa.setText(voucher.getSoLuotSuDungToiDa()+"");

		pnlBody.add(lblHangTV);
		pnlBody.add(cboHangTV);
		pnlBody.add(lblSuDungToiDa);
		pnlBody.add(txtSuDungToiDa);

		JLabel lblTrangThai = new JLabel("Trạng thái:");
		lblTrangThai.setFont(labelFont);
		
		String trangThai = "";
		if (voucher.getNgayKetThuc().isBefore(LocalDate.now())) 
			trangThai = "Hết hạn";
		else if (voucher.getSoLuotDaSuDung() >= voucher.getSoLuotSuDungToiDa())
			trangThai = "Ngừng hoạt động";
		else trangThai = "Đang hoạt động";
		
		
		JComboBox<String> cboTrangThai = new JComboBox<>(new String[] { "Đang hoạt động", "Hết hạn"});
		cboTrangThai.setSelectedItem(trangThai);
		

		pnlBody.add(lblTrangThai);
		pnlBody.add(cboTrangThai, "growx");

		JLabel lblDonToiThieu = new JLabel("Đơn tối thiểu:");
		lblDonToiThieu.setFont(labelFont);
		JTextField txtDonToiThieu = new JTextField();
		txtDonToiThieu.setText(voucher.getDonToiThieu()+"");

		pnlBody.add(lblDonToiThieu);
		pnlBody.add(txtDonToiThieu);

		JButton btn = new JButton("Lưu");
		btn.setPreferredSize(new Dimension(80, 30));
		btn.setMinimumSize(new Dimension(80, 30));
		btn.setMaximumSize(new Dimension(80, 30));
		btn.setBackground(Color.BLUE);
		btn.setForeground(Color.WHITE);
		btn.setFont(labelFont);
		pnlBody.add(btn, "span 4, align right, pushx");

		for (JComboBox<?> cbo : new JComboBox[] { cboHangTV, cboTrangThai }) {
			cbo.putClientProperty(FlatClientProperties.STYLE, "arc:10;");
		}

		pnlBody.setBackground(Color.WHITE);
		pnlContent.add(pnlTitle, BorderLayout.NORTH);
		pnlContent.add(pnlBody, BorderLayout.CENTER);
		
		cboTrangThai.setEnabled(false);
		txtMaGiamGia.setEditable(false);
		
		
		// su kien update 
		btn.addActionListener(e -> {
		    try {
		        Voucher v = new Voucher();

		        v.setMaVoucher(txtMaGiamGia.getText());
		        v.setGiaTri(Integer.parseInt( txtGiaTri.getText().replace("%", "").trim()));
		        v.setDonToiThieu(Double.parseDouble(txtDonToiThieu.getText()));
		        v.setGiamToiDa(Double.parseDouble(txtGiamToiDa.getText()));

		        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		        v.setNgayBatDau(LocalDate.parse(txtNgayBatDau.getText(), fmt));
		        v.setNgayKetThuc(LocalDate.parse(txtNgayKetThuc.getText(), fmt));

		        v.setSoLuotDaSuDung(Integer.parseInt(txtDaSuDung.getText()));
		        v.setSoLuotSuDungToiDa(Integer.parseInt(txtSuDungToiDa.getText()));

		        CustomerRank rank = new CustomerRank();
		        rank.setId(cboHangTV.getSelectedIndex() + 1);
		        v.setCustomerRank(rank);
		       
		        boolean ok = voucherBus.updateVoucher(v);

		        if (ok) {
		            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
		            addData(); 
		        } else {
		            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
		        }

		    } catch (Exception ex) {
		        ex.printStackTrace();
		        JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
		    }
		});


	}

	private javax.swing.JButton btnAdd;
	private javax.swing.JButton btnExport;
	private javax.swing.JButton btnImport;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel6;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JLabel lblDenNgay;
	private javax.swing.JLabel lblSearch;
	private javax.swing.JLabel lblStatus;
	private javax.swing.JLabel lblTime;
	private javax.swing.JLabel lblTuNgay;
	private javax.swing.JLabel lblVoucher;
	private javax.swing.JPanel pnlMain;
	private javax.swing.JRadioButton radAllStatus;
	private javax.swing.JRadioButton radAllTime;
	private javax.swing.JRadioButton radConHieuLuc;
	private javax.swing.JRadioButton radDiff;
	private javax.swing.JRadioButton radHetHieuLuc;
	private javax.swing.JRadioButton radMonth;
	private javax.swing.JRadioButton radToday;
	private javax.swing.JRadioButton radWeek;
	private javax.swing.JTextField txtDenNgay;
	private javax.swing.JTextField txtSearch;
	private javax.swing.JTextField txtTuNgay;
}