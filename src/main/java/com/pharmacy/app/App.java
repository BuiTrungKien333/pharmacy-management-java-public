package com.pharmacy.app;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.pharmacy.connectDB.ConnectDB;
import com.pharmacy.gui.auth.Login;
import com.pharmacy.gui.splashscreen.SplashScreen;

public class App extends JFrame {

	private static final Logger log = LoggerFactory.getLogger(App.class);

	private static App app;

	private final Login login;

	private final MainForm mainForm;

	public App() {
		initComponents();
		mainForm = new MainForm();
		login = new Login();
		login.setVisible(true);
		setContentPane(login);
		getRootPane().putClientProperty("JRootPane.useWindowDecorations", true);
	}

	private void initComponents() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new GroupLayout(getContentPane()));
		setSize(1000, 700);
		setResizable(false);
		setLocationRelativeTo(null);
	}

	public static void showForm(Component component) {
		log.debug("Navigating to form: {}", component.getClass().getSimpleName());
		component.applyComponentOrientation(app.getComponentOrientation());
		app.mainForm.showForm(component);
	}

	public static void loginSuccess() {
		log.info("Login successful. Initializing Main Dashboard.");

		app.login.setVisible(false);
		FlatAnimatedLafChange.showSnapshot();
		app.setContentPane(app.mainForm);
		app.mainForm.applyComponentOrientation(app.getComponentOrientation());
		setSelectedMenu(0, 0);
		app.mainForm.hideMenu();
		SwingUtilities.updateComponentTreeUI(app.mainForm);

		FlatAnimatedLafChange.hideSnapshotWithAnimation();
		SwingUtilities.invokeLater(() -> {
			app.setResizable(true);
			app.setExtendedState(JFrame.MAXIMIZED_BOTH);
		});
	}

	public static void logoutSuccess() {
		log.info("User logged out. Cleaning up and disconnecting Database.");

		app.setSize(1000, 700);
		app.setLocationRelativeTo(null);
		app.setResizable(false);
		app.login.setVisible(true);
		app.login.resetLogin();
		ConnectDB.disconnect();

		FlatAnimatedLafChange.showSnapshot();
		app.setContentPane(app.login);
		app.login.applyComponentOrientation(app.getComponentOrientation());
		SwingUtilities.updateComponentTreeUI(app.login);
		FlatAnimatedLafChange.hideSnapshotWithAnimation();
	}

	public static void setSelectedMenu(int index, int subIndex) {
		app.mainForm.setSelectedMenu(index, subIndex);
	}

	public static App getInstance() {
		return app;
	}

	public static void main(String[] args) {
		// TODO:...
		System.out.println("Hello World!");
	}
}
