package com.pharmacy.gui.setting;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.pharmacy.bus.AccountBUS;
import com.pharmacy.bus.Auth;
import com.pharmacy.config.Translator;
import com.pharmacy.utils.ConfigEmailUtil;

import raven.modal.Toast;

public class SettingGUI extends javax.swing.JPanel {

	private static final Logger log = Logger.getLogger(SettingGUI.class.getName());

	private ConfigEmailUtil configUtil;

	private AccountBUS accountBUS;

	public SettingGUI() {

		accountBUS = new AccountBUS();

		configUtil = new ConfigEmailUtil();

		initComponents();

		btnSaveEmail.setEnabled(Auth.hasPermission("CONFIG_EMAIL"));

		/*
		 * Register listeners for language change event
		 */
		Translator.getInstance().addLanguageChangeListener(locale -> {
			SwingUtilities.invokeLater(this::updateTexts);
		});

		updateTexts();

		loadSettings();
	}

	private void updateTexts() {
		Translator lang = Translator.getInstance();
		jLabel1.setText(lang.getString("setting.lbl.pwdcur"));
		jLabel3.setText(lang.getString("setting.lbl.pwdnew"));
		jLabel4.setText(lang.getString("setting.lbl.pwdconfirm"));
		jLabel5.setText(lang.getString("setting.lbl.email"));
		jLabel11.setText(lang.getString("setting.lbl.lang"));
	}

	public void loadSettings() {
		log.info("Load settings for gui SettingGUI");
		// load setting for config email
		Properties configProps = null;
		try {
			configProps = configUtil.loadProperties();
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(this, "Error reading settings: " + ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		txtHost.setText(configProps.getProperty("mail.smtp.host"));
		txtPort.setText(configProps.getProperty("mail.smtp.port"));
		txtUser.setText(configProps.getProperty("mail.user"));
		txtPass.setText(configProps.getProperty("mail.password"));

		// load setting for change language
		Locale currentLocale = Translator.getInstance().getCurrentLocale();
		if (Locale.ENGLISH.equals(currentLocale))
			radEng.setSelected(true);
		else
			radViet.setSelected(true);
	}

	private void initComponents() {

		jPanel2 = new javax.swing.JPanel();
		jPanel5 = new javax.swing.JPanel();
		lblAvatar = new javax.swing.JLabel();
		jPanel1 = new javax.swing.JPanel();
		jPanel3 = new javax.swing.JPanel();
		jLabel1 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		pwfCurrent = new javax.swing.JPasswordField();
		pwfChange = new javax.swing.JPasswordField();
		pwfConfirm = new javax.swing.JPasswordField();
		btnChangePW = new javax.swing.JButton();
		jPanel4 = new javax.swing.JPanel();
		jPanel6 = new javax.swing.JPanel();
		jPanel8 = new javax.swing.JPanel();
		jLabel5 = new javax.swing.JLabel();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jLabel9 = new javax.swing.JLabel();
		jLabel10 = new javax.swing.JLabel();
		txtHost = new javax.swing.JTextField();
		txtPort = new javax.swing.JTextField();
		txtUser = new javax.swing.JTextField();
		btnSaveEmail = new javax.swing.JButton();
		txtPass = new javax.swing.JPasswordField();
		jPanel7 = new javax.swing.JPanel();
		jPanel9 = new javax.swing.JPanel();
		jLabel11 = new javax.swing.JLabel();
		radEng = new javax.swing.JRadioButton();
		radViet = new javax.swing.JRadioButton();
		btnSaveLang = new javax.swing.JButton();

		setLayout(new java.awt.GridLayout(2, 1, 0, 15));

		jPanel2.setBackground(new java.awt.Color(204, 255, 0));
		jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));
		jPanel2.setLayout(new java.awt.BorderLayout());

		jPanel5.setBackground(new java.awt.Color(255, 255, 255));

		lblAvatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		lblAvatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/default.png"))); // NOI18N
		lblAvatar.setPreferredSize(new java.awt.Dimension(170, 170));
		jPanel5.add(lblAvatar);

		jPanel2.add(jPanel5, java.awt.BorderLayout.PAGE_START);

		jPanel1.setBackground(new java.awt.Color(255, 255, 255));

		jPanel3.setPreferredSize(new java.awt.Dimension(600, 220));

		btnChangePW.setBackground(new java.awt.Color(51, 153, 255));
		btnChangePW.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		btnChangePW.setText("Save");
		btnChangePW.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnChangePWActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
		jPanel3.setLayout(jPanel3Layout);
		jPanel3Layout
				.setHorizontalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
								jPanel3Layout.createSequentialGroup().addContainerGap()
										.addGroup(jPanel3Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
												.addGroup(jPanel3Layout.createSequentialGroup()
														.addGap(0, 0, Short.MAX_VALUE).addComponent(btnChangePW,
																javax.swing.GroupLayout.PREFERRED_SIZE, 84,
																javax.swing.GroupLayout.PREFERRED_SIZE))
												.addGroup(jPanel3Layout
														.createSequentialGroup().addGroup(jPanel3Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addComponent(
																		jLabel4,
																		javax.swing.GroupLayout.Alignment.TRAILING)
																.addGroup(jPanel3Layout.createSequentialGroup()
																		.addGroup(jPanel3Layout.createParallelGroup(
																				javax.swing.GroupLayout.Alignment.LEADING)
																				.addComponent(jLabel1)
																				.addComponent(jLabel3))
																		.addGap(32, 32, 32)))
														.addGroup(jPanel3Layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.LEADING)
																.addGroup(jPanel3Layout
																		.createSequentialGroup().addGap(7, 7, 7)
																		.addComponent(
																				pwfCurrent,
																				javax.swing.GroupLayout.DEFAULT_SIZE,
																				455, Short.MAX_VALUE))
																.addGroup(jPanel3Layout
																		.createSequentialGroup()
																		.addPreferredGap(
																				javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(pwfConfirm))
																.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
																		jPanel3Layout.createSequentialGroup()
																				.addPreferredGap(
																						javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																				.addComponent(pwfChange)))))
										.addContainerGap()));
		jPanel3Layout.setVerticalGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel3Layout.createSequentialGroup().addGap(14, 14, 14)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel1).addComponent(pwfCurrent, javax.swing.GroupLayout.PREFERRED_SIZE,
										34, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(21, 21, 21)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(pwfChange, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel3))
						.addGap(18, 18, 18)
						.addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(pwfConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(jLabel4))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnChangePW,
								javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(0, 26, Short.MAX_VALUE)));

		jPanel1.add(jPanel3);

		jPanel2.add(jPanel1, java.awt.BorderLayout.CENTER);

		add(jPanel2);

		jPanel4.setBackground(new java.awt.Color(255, 255, 255));
		jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));
		jPanel4.setLayout(new java.awt.GridLayout(1, 2, 20, 0));

		jPanel6.setBackground(new java.awt.Color(255, 255, 255));

		jPanel8.setPreferredSize(new java.awt.Dimension(400, 500));

		jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

		jLabel7.setText("Host name");

		jLabel8.setText("Port number");

		jLabel9.setText("Username");

		jLabel10.setText("Password");

		btnSaveEmail.setBackground(new java.awt.Color(51, 153, 255));
		btnSaveEmail.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		btnSaveEmail.setText("Save");
		btnSaveEmail.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSaveEmailActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
		jPanel8.setLayout(jPanel8Layout);
		jPanel8Layout.setHorizontalGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						jPanel8Layout.createSequentialGroup()
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(jLabel5).addGap(140, 140, 140))
				.addGroup(jPanel8Layout.createSequentialGroup().addGap(28, 28, 28).addGroup(jPanel8Layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
						.addComponent(btnSaveEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 95,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addGroup(jPanel8Layout.createSequentialGroup().addComponent(jLabel7)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 258,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel8Layout.createSequentialGroup().addComponent(jLabel8)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15,
														Short.MAX_VALUE)
												.addComponent(
														txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 258,
														javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel8Layout.createSequentialGroup().addGroup(jPanel8Layout
												.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
												.addGroup(jPanel8Layout
														.createSequentialGroup().addComponent(jLabel9).addPreferredGap(
																javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
												.addGroup(
														javax.swing.GroupLayout.Alignment.TRAILING,
														jPanel8Layout.createSequentialGroup().addComponent(jLabel10)
																.addGap(31, 31, 31)))
												.addGroup(jPanel8Layout
														.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
																false)
														.addComponent(txtUser, javax.swing.GroupLayout.DEFAULT_SIZE,
																258, Short.MAX_VALUE)
														.addComponent(txtPass)))))
						.addContainerGap(33, Short.MAX_VALUE)));
		jPanel8Layout.setVerticalGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel8Layout.createSequentialGroup().addGap(34, 34, 34)
						.addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(18, 18, 18)
						.addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabel7).addComponent(txtHost, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabel8).addComponent(txtPort, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(jLabel9).addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 34,
										javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(18, 18, 18)
						.addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(jLabel10).addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE,
										34, javax.swing.GroupLayout.PREFERRED_SIZE))
						.addGap(33, 33, 33).addComponent(btnSaveEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(164, Short.MAX_VALUE)));

		jPanel6.add(jPanel8);

		jPanel4.add(jPanel6);

		jPanel7.setBackground(new java.awt.Color(255, 255, 255));

		jPanel9.setPreferredSize(new java.awt.Dimension(400, 500));

		jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

		ButtonGroup buttonGroup = new ButtonGroup();

		radEng.setText("Tiếng Anh (English)");

		radViet.setText("Tiếng Việt (Vietnamese)");

		buttonGroup.add(radViet);
		buttonGroup.add(radEng);

		btnSaveLang.setBackground(new java.awt.Color(51, 153, 255));
		btnSaveLang.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
		btnSaveLang.setText("Save");
		btnSaveLang.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnSaveLangActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
		jPanel9.setLayout(jPanel9Layout);
		jPanel9Layout.setHorizontalGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jPanel9Layout.createSequentialGroup().addGap(75, 75, 75)
						.addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(radViet).addComponent(radEng))
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
						.addContainerGap(156, Short.MAX_VALUE)
						.addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel9Layout.createSequentialGroup()
												.addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 101,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(143, 143, 143))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										jPanel9Layout.createSequentialGroup()
												.addComponent(btnSaveLang, javax.swing.GroupLayout.PREFERRED_SIZE, 95,
														javax.swing.GroupLayout.PREFERRED_SIZE)
												.addGap(31, 31, 31)))));
		jPanel9Layout
				.setVerticalGroup(
						jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addGroup(jPanel9Layout.createSequentialGroup().addGap(42, 42, 42)
										.addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 28,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addGap(29, 29, 29).addComponent(radViet).addGap(38, 38, 38)
										.addComponent(radEng).addGap(29, 29, 29)
										.addComponent(btnSaveLang, javax.swing.GroupLayout.PREFERRED_SIZE, 33,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addContainerGap(259, Short.MAX_VALUE)));

		jPanel7.add(jPanel9);

		jPanel4.add(jPanel7);

		add(jPanel4);
	}

	private void btnChangePWActionPerformed(java.awt.event.ActionEvent evt) {

		log.info("Event change password start!");

		String pwdCur = new String(pwfCurrent.getPassword());

		String pwdNew = new String(pwfChange.getPassword());

		if (pwdCur.isBlank() || pwdNew.isBlank()) {
			Toast.show(this, Toast.Type.WARNING, Translator.getInstance().getString("setting.toast.fill_info"));
			return;
		}

		// Kiểm tra mật khẩu mới và mật khẩu confirm có khớp
		if (!pwdNew.equals(new String(pwfConfirm.getPassword()))) {
			Toast.show(this, Toast.Type.ERROR, Translator.getInstance().getString("setting.toast.pwd_mismatch"));
			return;
		}

		// Kiểm tra xem mật khẩu hiện tại có chính xác
		if (!accountBUS.login(Auth.getCurrentUser().getMaNhanVien(), pwdCur)) {
			Toast.show(this, Toast.Type.ERROR, Translator.getInstance().getString("setting.toast.pwd_incorrect"));
			return;
		}

		int option = JOptionPane.showConfirmDialog(this,
				Translator.getInstance().getString("setting.confirm.change_pass"), "Warning",
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			accountBUS.changePassword(Auth.getCurrentUser().getMaNhanVien(), pwdNew);
			JOptionPane.showMessageDialog(this, Translator.getInstance().getString("setting.toast.change_success"));
			log.info("Change password successfully!");
		}

		log.info("Event change password end!");
	}

	private void btnSaveEmailActionPerformed(java.awt.event.ActionEvent evt) {
		log.info("Event change config email start!");

		int option = JOptionPane.showConfirmDialog(this,
				Translator.getInstance().getString("setting.confirm.change_email"), "Warning",
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			try {
				configUtil.saveProperties(txtHost.getText(), txtPort.getText(), txtUser.getText(),
						new String(txtPass.getPassword()));
				JOptionPane.showMessageDialog(this, "Properties were saved successfully!");
				log.info("Properties were saved successfully!");
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error saving properties file: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
		log.info("Event change config email end!");
	}

	private void btnSaveLangActionPerformed(java.awt.event.ActionEvent evt) {
		log.info("Event change language start!");
		int option = JOptionPane.showConfirmDialog(this,
				Translator.getInstance().getString("setting.confirm.change_lang"), "Warning",
				JOptionPane.YES_NO_OPTION);

		if (option == JOptionPane.YES_OPTION) {
			Translator.getInstance().setLocale(radEng.isSelected() ? Locale.ENGLISH : new Locale("vi", "VN"));
			log.info("Change language to " + (radEng.isSelected() ? "English " : "Viet Nam ") + "successfully!");
		}
		log.info("Event change language end!");
	}

	private javax.swing.JButton btnChangePW;
	private javax.swing.JButton btnSaveEmail;
	private javax.swing.JButton btnSaveLang;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel10;
	private javax.swing.JLabel jLabel11;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JLabel jLabel5;
	private javax.swing.JLabel jLabel7;
	private javax.swing.JLabel jLabel8;
	private javax.swing.JLabel jLabel9;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JPanel jPanel3;
	private javax.swing.JPanel jPanel4;
	private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel jPanel6;
	private javax.swing.JPanel jPanel7;
	private javax.swing.JPanel jPanel8;
	private javax.swing.JPanel jPanel9;
	private javax.swing.JLabel lblAvatar;
	private javax.swing.JPasswordField pwfChange;
	private javax.swing.JPasswordField pwfConfirm;
	private javax.swing.JPasswordField pwfCurrent;
	private javax.swing.JRadioButton radEng;
	private javax.swing.JRadioButton radViet;
	private javax.swing.JTextField txtHost;
	private javax.swing.JPasswordField txtPass;
	private javax.swing.JTextField txtPort;
	private javax.swing.JTextField txtUser;
}
