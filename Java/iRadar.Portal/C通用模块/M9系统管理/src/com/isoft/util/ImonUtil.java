package com.isoft.util;

public class ImonUtil {

	/**
	 * 随机密码设置
	 * @param num 密码位数
	 */
	public static String getRandomPassword(int num){
		return "";
	}
	
	/**
	 * 邮件内容体
	 * @param tenantId 租户id
	 * @param name 发送人姓名
	 * @param randomPswd  随机密码
	 * @param portal_uri 入口地址
	 * @return
	 */
	public static String generateMailBody(String tenantId, String name,String randomPswd, String portal_uri){
		return "";
	}
	
	/**
	 * @param obj
	 * @param tenantId
	 * @return osTenantId
	 */
	public static String createOpenstackTenant(Object obj, String tenantId){
		return "";
	}
	
	/**
	 * 
	 * @param obj
	 * @param tenantId
	 * @param osTenantId
	 */
	public static boolean releaseOpenstackTenant(Object obj, String tenantId, String osTenantId){
		return true;
	}
	
	/**
	 * 发送邮件
	 * @param emailAddr  发送到邮件地址
	 * @param eMailHead  邮件头
	 * @param eMailBody  邮件体
	 * @return
	 */
	public static boolean sendMail(String emailAddr, String eMailHead, String eMailBody){
		return true;
	}
}
