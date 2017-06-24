package com.isoft.utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class U4aUtil {

	public static final char[] ILLEGAL_CHARS = { '/', '\\', '*', ':', ';', '?',
			'\'', '"', '<', '>', '|', '%', '&', '#', '+', '' };

	/**
	 * 随机密码生成器
	 * 
	 * @param len
	 *            密码长度
	 * @return
	 */
	public static String getRandomPassword(int len) {
		if (len < 8) {
			len = 8;
		}
		Random rand = new Random();
		StringBuffer sb = new StringBuffer(len);
		for (int i = 0; i < len; i++) {
			int b = rand.nextInt(127);
			if (b < 33) {
				b += 33;
			}

			for (int j = 0; j < ILLEGAL_CHARS.length; j++) {
				if (b == ILLEGAL_CHARS[j]) {
					b = 88;
					break;
				}
			}
			sb.append((char) b);
		}
		String random = sb.toString();
		return random;
	}

	/**
	 * 发送邮件
	 * 
	 * @param toEmail
	 * @param subject
	 * @param msgBody
	 * @return
	 */
	public static boolean sendMail(String toEmail, String subject,
			String msgBody) {
		Properties properties = new Properties();
		try {
			InputStream is = U4aUtil.class
					.getResourceAsStream("/email.properties");
			properties.load(is);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String smtpServer = properties.getProperty("sys.smtpServer");
		String sysMail = properties.getProperty("sys.email");
		String sysMailUser = properties.getProperty("sys.email.usr");// "dongdong.li@i-soft.com.cn";
		String sysMailPwd = properties.getProperty("sys.email.pwd");
		return MailUtil.sendMail(smtpServer, sysMail, toEmail, sysMailUser,
				sysMailPwd, subject, msgBody);
	}

	/**
	 * 构造邮件内容信息
	 * 
	 * @param userName
	 * @param passwd
	 * @param sysName
	 * @param sysUrl
	 * @return
	 */
	public static String generateMailBody(String tenantId, String userName,
			String passwd, String sysUrl) {
		StringBuffer sb = new StringBuffer(256);
		sb.append("您的登录账号为：").append(userName);
		sb.append("\r\n");
		sb.append("您的登录密码为：").append(passwd);
		sb.append("\r\n");
		sb.append("为防止密码泄露,请您尽快登录系统并修改密码!");
		sb.append("\r\n");
		sb.append("\r\n");
		sb.append("系统地址： ").append(sysUrl);
		sb.append("\r\n");
		return sb.toString();
	}
}
