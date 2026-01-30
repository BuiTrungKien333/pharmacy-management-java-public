package com.pharmacy.gui.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.pharmacy.utils.ConfigEmailUtil;
import com.pharmacy.utils.EmailUtil;

public class ReportGUI extends JPanel {

	private ConfigEmailUtil configUtil = new ConfigEmailUtil();

	private JLabel labelTo = new JLabel("To: ");
	private JLabel labelSubject = new JLabel("Subject: ");

	private JTextField fieldTo = new JTextField(30);
	private JTextField fieldSubject = new JTextField(30);

	private JButton buttonSend = new JButton("SEND");

	private JFilePicker filePicker = new JFilePicker("Attached", "Attach File...");

	private JTextArea textAreaMessage = new JTextArea(10, 30);

	private GridBagConstraints constraints = new GridBagConstraints();

	public ReportGUI() {
		// set up layout
		setLayout(new BorderLayout(5, 5));
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);

		setupForm();
		setUpData();

		fieldSubject.requestFocus();
	}

	private void setupForm() {
		JLabel titleLabel = new JLabel("BÁO CÁO");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel pnl1 = new JPanel();
		pnl1.add(titleLabel);

		JPanel pnl2 = new JPanel();
		pnl2.setLayout(new GridBagLayout());

		constraints.gridx = 0;
		constraints.gridy = 0;
		pnl2.add(labelTo, constraints);

		constraints.gridx = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		pnl2.add(fieldTo, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0;
		pnl2.add(labelSubject, constraints);

		constraints.gridx = 1;
		constraints.gridwidth = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1.0;
		pnl2.add(fieldSubject, constraints);

		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.gridheight = 2;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.weightx = 0.3;
		buttonSend.setFont(new Font("Arial", Font.BOLD, 16));
		buttonSend.setBackground(new Color(54, 123, 227));
		pnl2.add(buttonSend, constraints);

		buttonSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				buttonSendActionPerformed(event);
			}
		});

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridheight = 1;
		constraints.gridwidth = 4;
		constraints.weightx = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.WEST;
		filePicker.setMode(JFilePicker.MODE_OPEN);
		pnl2.add(filePicker, constraints);

		constraints.gridy = 3;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.BOTH;
		pnl2.add(new JScrollPane(textAreaMessage), constraints);

		this.add(pnl1, BorderLayout.NORTH);
		this.add(pnl2, BorderLayout.CENTER);
	}

	private void setUpData() {
		fieldTo.setText("buitrungkien2005qng@gmail.com");
		fieldTo.setEditable(false);
	}

	private void buttonSendActionPerformed(ActionEvent event) {
		if (!validateFields())
			return;

		String toAddress = fieldTo.getText();
		String subject = fieldSubject.getText();
		String message = textAreaMessage.getText();

		File[] tempFiles = null;

		if (!filePicker.getSelectedFilePath().equals("")) {
			File selectedFile = new File(filePicker.getSelectedFilePath());
			tempFiles = new File[] { selectedFile };
		}

		final File[] filesToSend = tempFiles;

		JDialog loadingDialog = createLoadingDialog();

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				Properties smtpProperties = configUtil.loadProperties();
				EmailUtil.sendEmail(smtpProperties, toAddress, subject, message, filesToSend);
				return null;
			}

			@Override
			protected void done() {
				loadingDialog.dispose();

				try {
					get();

					JOptionPane.showMessageDialog(ReportGUI.this, "Đã gửi email thành công!");
					clearForm();
				} catch (Exception ex) {
					String errorMsg = ex.getMessage();
					if (ex instanceof java.util.concurrent.ExecutionException)
						errorMsg = ex.getCause().getMessage();

					JOptionPane.showMessageDialog(ReportGUI.this, "Error while sending the e-mail: " + errorMsg,
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		worker.execute();
		loadingDialog.setVisible(true);
	}

	private JDialog createLoadingDialog() {
		Window parentWindow = SwingUtilities.getWindowAncestor(this);

		JDialog dialog = new JDialog(parentWindow, "Đang xử lý...", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dialog.setSize(300, 80);
		dialog.setLocationRelativeTo(parentWindow);
		dialog.setLayout(new BorderLayout());

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);

		JLabel lblMessage = new JLabel("Đang gửi email, vui lòng chờ...", JLabel.CENTER);
		lblMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		dialog.add(lblMessage, BorderLayout.NORTH);
		dialog.add(progressBar, BorderLayout.CENTER);

		return dialog;
	}

	public void clearForm() {
		fieldSubject.setText("");
		textAreaMessage.setText("");
		filePicker.setMode(1);
		filePicker.setText("");
	}

	private boolean validateFields() {
		if (fieldSubject.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề báo cáo!", "Error", JOptionPane.ERROR_MESSAGE);
			fieldSubject.requestFocus();
			return false;
		}
		return true;
	}

}
