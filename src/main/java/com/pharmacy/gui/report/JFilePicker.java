package com.pharmacy.gui.report;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class JFilePicker extends JPanel {

	private String textFieldLabel;
	private String buttonLabel;

	private JLabel label;
	private JTextField textField;
	private JButton button;

	private JFileChooser fileChooser;

	private int mode;
	public static final int MODE_OPEN = 1;
	public static final int MODE_SAVE = 2;

	public JFilePicker(String textFieldLabel, String buttonLabel) {
		this.textFieldLabel = textFieldLabel;
		this.buttonLabel = buttonLabel;

		fileChooser = new JFileChooser();

		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// creates the GUI
		label = new JLabel(textFieldLabel);

		textField = new JTextField(30);
		button = new JButton(buttonLabel);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				buttonActionPerformed(evt);
			}
		});

		add(label);
		add(textField);
		add(button);

	}

	private void buttonActionPerformed(ActionEvent evt) {
		JFrame parent = new JFrame();
		parent.setLocationRelativeTo(null);
		parent.setAlwaysOnTop(true);

		if (mode == MODE_OPEN) {
			if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
				textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		} else if (mode == MODE_SAVE) {
			if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION)
				textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}

		parent.dispose();
	}

	public void addFileTypeFilter(String extension, String description) {
		FileTypeFilter filter = new FileTypeFilter(extension, description);
		fileChooser.addChoosableFileFilter(filter);
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public void setText(String s) {
		textField.setText(s);
	}

	public String getSelectedFilePath() {
		return textField.getText();
	}

	public JFileChooser getFileChooser() {
		return this.fileChooser;
	}
}