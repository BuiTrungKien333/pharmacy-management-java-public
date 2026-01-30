package com.pharmacy.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;

import com.pharmacy.config.EmailConfig;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class EmailUtil {

	private static final EmailConfig efg = ConfigFactory.create(EmailConfig.class);

	private static final String fromEmail = efg.fromEmail();
	private static final String username = efg.username();
	private static final String password = efg.password();

	public static void sendEmail(String toEmail, String subject, String content) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", efg.host());
			props.put("mail.smtp.port", efg.port());
			props.put("mail.smtp.auth", efg.auth());
			props.put("mail.smtp.starttls.enable", efg.enable());

			Session session = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			Message msg = new MimeMessage(session);

			msg.addHeader("Content-type", "text/html; charset=UTF-8");
			msg.setFrom(new InternetAddress(fromEmail, "Alami Pharmacy"));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setContent(content, "text/html; charset=UTF-8");

			// sends the email
			Transport.send(msg);
		} catch (Exception e) {
			throw new RuntimeException("Gửi email thất bại: " + e.getMessage());
		}
	}
	
	public static void sendEmail(Properties smtpProperties, String toAddress, String subject, String message,
			File[] attachFiles) throws AddressException, MessagingException, IOException {

		final String userName = smtpProperties.getProperty("mail.user");
		final String password = smtpProperties.getProperty("mail.password");

		// creates a new session with an authenticator
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		};
		Session session = Session.getInstance(smtpProperties, auth);

		// creates a new e-mail message
		Message msg = new MimeMessage(session);

		msg.setFrom(new InternetAddress(userName));
		InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
		msg.setRecipients(Message.RecipientType.TO, toAddresses);
		msg.setSubject(subject);
		msg.setSentDate(new Date());

		// creates message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(message, "text/html");

		// creates multi-part
		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(messageBodyPart);

		// adds attachments
		if (attachFiles != null && attachFiles.length > 0) {
			for (File aFile : attachFiles) {
				MimeBodyPart attachPart = new MimeBodyPart();
				try {
					attachPart.attachFile(aFile);
				} catch (IOException ex) {
					throw ex;
				}

				multipart.addBodyPart(attachPart);
			}
		}

		// sets the multi-part as e-mail's content
		msg.setContent(multipart);

		// sends the e-mail
		Transport.send(msg);
	}

}
