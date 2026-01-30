package com.pharmacy.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.util.UIScale;
import com.pharmacy.bus.Auth;
import com.pharmacy.config.Translator;
import com.pharmacy.gui.batch.BatchGUI;
import com.pharmacy.gui.batch.InventoryGUI;
import com.pharmacy.gui.cus.CustomerGUI;
import com.pharmacy.gui.dashboard.Dashboard;
import com.pharmacy.gui.empl.EmployeeGUI;
import com.pharmacy.gui.invoice.InvoiceGUI;
import com.pharmacy.gui.menu.Menu;
import com.pharmacy.gui.menu.MenuAction;
import com.pharmacy.gui.prod.ProductGUI;
import com.pharmacy.gui.profile.ProfileGUI;
import com.pharmacy.gui.refund.ReturnProductGUI;
import com.pharmacy.gui.report.ReportGUI;
import com.pharmacy.gui.sell.SellGUI;
import com.pharmacy.gui.setting.SettingGUI;
import com.pharmacy.gui.supplier.SupplierGUI;
import com.pharmacy.gui.voucher.VoucherGUI;

public class MainForm extends JLayeredPane {

	private Menu menu;
	private JPanel pnlBody;
	private JButton btnMenu;

	private ProductGUI product;

	private SettingGUI setting;

	private ReportGUI report;

	private BatchGUI batch;

	private SellGUI sell;

	private ReturnProductGUI refund;

	private InvoiceGUI invoice;

	public MainForm() {
		init();
	}

	private void init() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new MainFormLayout());

		menu = new Menu();
		pnlBody = new JPanel(new BorderLayout());

		initMenuArrowIcon();

		btnMenu.putClientProperty(FlatClientProperties.STYLE,
				"" + "background:$Menu.button.background;" + "arc:999;" + "focusWidth:0;" + "borderWidth:0");

		btnMenu.addActionListener((ActionEvent e) -> {
			setMenuFull(!menu.isMenuFull());
		});

		initMenuEvent();
		setLayer(btnMenu, JLayeredPane.POPUP_LAYER);
		add(btnMenu);
		add(menu);
		add(pnlBody);
	}

	@Override
	public void applyComponentOrientation(ComponentOrientation o) {
		super.applyComponentOrientation(o);
		initMenuArrowIcon();
	}

	private void initMenuArrowIcon() {
		if (btnMenu == null) {
			btnMenu = new JButton();
		}
		String icon = (getComponentOrientation().isLeftToRight()) ? "menu_left.svg" : "menu_right.svg";
		btnMenu.setIcon(new FlatSVGIcon("icon/svg/" + icon, 0.8f));
	}

	private void initMenuEvent() {
		menu.addMenuEvent((int index, int subIndex, MenuAction action) -> {
			switch (index) {
			case 0 -> {
				App.showForm(new Dashboard());
			}
			case 1 -> {
				if (!Auth.hasPermission("SALE_ACCESS"))
					return;

				if (sell == null)
					sell = new SellGUI();

				App.showForm(sell);
			}
			case 2 -> {
				if (!Auth.hasPermission("RETURN_ACCESS"))
					return;

				if (refund == null)
					refund = new ReturnProductGUI();

				App.showForm(refund);
			}
			case 3 -> {
				if (!Auth.hasPermission("PRODUCT_VIEW"))
					return;

				if (product == null)
					product = new ProductGUI();
				else
					product.refreshDataProd();

				App.showForm(product);
			}
			case 4 -> {
				if (!Auth.hasPermission("INVOICE_VIEW"))
					return;

				if (invoice == null)
					invoice = new InvoiceGUI();
				else
					invoice.refreshData();

				App.showForm(invoice);
			}
			case 5 -> {
				switch (subIndex) {
				case 1 -> {
					if (!Auth.hasPermission("BATCH_VIEW"))
						return;

					if (batch == null)
						batch = new BatchGUI();
					else
						batch.refreshDataShipment();

					App.showForm(batch);
				}
				case 2 -> App.showForm(new InventoryGUI());
				default -> action.cancel();
				}
			}
			case 6 -> {
				if (!Auth.hasPermission("SUPPLIER_MANAGE"))
					return;
				
				App.showForm(new SupplierGUI());
			}
			case 7 -> {
				if (!Auth.hasPermission("CUSTOMER_MANAGE"))
					return;

				App.showForm(new CustomerGUI());
			}
			case 8 -> {
				if (!Auth.hasPermission("EMPLOYEE_MANAGE"))
					return;
				
				App.showForm(new EmployeeGUI());
			}
			case 9 -> {
				if (!Auth.hasPermission("VOUCHER_MANAGE"))
					return;
				
				App.showForm(new VoucherGUI());
			}
			case 10 -> {
				// TODO: 
			}
			case 11 -> {

				if (report == null)
					report = new ReportGUI();
				else
					report.clearForm();

				App.showForm(report);
			}
			case 12 -> App.showForm(new ProfileGUI());
			case 13 -> {
				if (!Auth.hasPermission("SEND_REPORT"))
					return;

				if (setting == null)
					setting = new SettingGUI();
				else
					setting.loadSettings();

				App.showForm(setting);
			}
			case 14 -> {
				int confirm = JOptionPane.showConfirmDialog(this,
						Translator.getInstance().getString("main.text.logout"));

				if (confirm == JOptionPane.YES_OPTION) {
					sell = null;
					refund = null;
					product = null;
					invoice = null;
					batch = null;
					report = null;
					setting = null;

					App.logoutSuccess();
					Auth.logout();
				}
			}
			default -> action.cancel();
			}
		});
	}

	private void setMenuFull(boolean full) {
		String icon;
		if (getComponentOrientation().isLeftToRight())
			icon = (full) ? "menu_left.svg" : "menu_right.svg";
		else
			icon = (full) ? "menu_right.svg" : "menu_left.svg";

		btnMenu.setIcon(new FlatSVGIcon("icon/svg/" + icon, 0.8f));
		menu.setMenuFull(full);
		revalidate();
	}

	public void hideMenu() {
		menu.hideMenuItem();
	}

	public void showForm(Component component) {
		pnlBody.removeAll();

		pnlBody.add(component);
		pnlBody.repaint();
		pnlBody.revalidate();
	}

	public void setSelectedMenu(int index, int subIndex) {
		menu.setSelectedMenu(index, subIndex);
	}

	private class MainFormLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				return new Dimension(5, 5);
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				return new Dimension(0, 0);
			}
		}

		@Override
		public void layoutContainer(Container parent) {
			synchronized (parent.getTreeLock()) {
				boolean ltr = parent.getComponentOrientation().isLeftToRight();
				Insets insets = UIScale.scale(parent.getInsets());
				int x = insets.left;
				int y = insets.top;
				int width = parent.getWidth() - (insets.left + insets.right);
				int height = parent.getHeight() - (insets.top + insets.bottom);
				int menuWidth = UIScale.scale(menu.isMenuFull() ? menu.getMenuMaxWidth() : menu.getMenuMinWidth());
				int menuX = ltr ? x : x + width - menuWidth;
				menu.setBounds(menuX, y, menuWidth, height);
				int btnMenuWidth = btnMenu.getPreferredSize().width;
				int btnMenuHeight = btnMenu.getPreferredSize().height;
				int menubX;
				if (ltr) {
					menubX = (int) (x + menuWidth - (btnMenuWidth * (menu.isMenuFull() ? 0.5f : 0.3f)));
				} else {
					menubX = (int) (menuX - (btnMenuWidth * (menu.isMenuFull() ? 0.5f : 0.7f)));
				}
				btnMenu.setBounds(menubX, UIScale.scale(30), btnMenuWidth, btnMenuHeight);
				int gap = UIScale.scale(5);
				int bodyWidth = width - menuWidth - gap;
				int bodyHeight = height;
				int bodyx = ltr ? (x + menuWidth + gap) : x;
				int bodyy = y;
				pnlBody.setBounds(bodyx, bodyy, bodyWidth, bodyHeight);
			}
		}
	}
}