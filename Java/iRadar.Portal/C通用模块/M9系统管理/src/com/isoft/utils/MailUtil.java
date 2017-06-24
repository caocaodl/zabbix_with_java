package com.isoft.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

public class MailUtil {
	private static final Logger LOG = Logger.getLogger(MailUtil.class);

	public static boolean sendMail(String smtpServer, String from, String to,
			String usr, String pwd, String subject, String msgBody) {
		return sendMail(smtpServer, from, new String[] { to }, null, usr, pwd,
				subject, msgBody);
	}

	public static boolean sendMail(String smtpServer, String from,
			String[] toList, String[] ccList, String usr, String pwd,
			String subject, String msgBody) {
		boolean missTo = (toList == null) || (toList.length == 0);
		boolean missCc = (ccList == null) || (ccList.length == 0);
		if ((missTo) && (missCc)) {
			return false;
		}

		Properties prop = new Properties();
		prop.put("mail.smtp.host", smtpServer);
		if (pwd != null) {
			prop.put("mail.smtp.auth", "true");
		}
		Session session = Session.getInstance(prop, null);
		MimeMessage message = new MimeMessage(session);
		try {
			message.setHeader("Content-Transfer-Encoding", "base64");
			message.setFrom(new InternetAddress(from));

			if (!missTo) {
				for (int i = 0; i < toList.length; i++) {
					message.addRecipient(Message.RecipientType.TO,
							new InternetAddress(toList[i]));
				}
			}

			if (!missCc) {
				for (int i = 0; i < ccList.length; i++) {
					message.addRecipient(Message.RecipientType.CC,
							new InternetAddress(ccList[i]));
				}
			}

			message.setSubject(subject);
			message.setText(msgBody);
			if (pwd != null) {
				Transport transport = session.getTransport("smtp");
				transport.connect(smtpServer,
						(usr == null || usr.length() == 0) ? extractUser(from)
								: usr, pwd);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} else {
				Transport.send(message);
			}
			return true;
		} catch (AddressException e) {
			LOG.error(
					"(smtpHost, props)=" + smtpServer + ", "
							+ session.getProperties(), e);
		} catch (MessagingException e) {
			LOG.error(
					"(smtpHost, props)=" + smtpServer + ", "
							+ session.getProperties(), e);
		}
		return false;
	}

	public static String extractUser(String email) {
		int pos = email.indexOf('@');
		return pos > 0 ? email.substring(0, pos) : email;
	}

	public static boolean isValidEMailAddress(String email) {
		if (email == null) {
			return false;
		}

		if (email.indexOf('@') < 1) {
			return false;
		}
		try {
			new InternetAddress(email);

			return true;
		} catch (AddressException e) {
		}
		return false;
	}
}
