package com.jack.contacts;

/**
 * @param 商户信息
 */
public class Merchant {

	/**
	 * 商户ID
	 */
	public static String shanghu_id = "";

	/**
	 * 商户名称name
	 */
	public static String shanghu_name = "";
	
	/**
	 * 商户描述
	 */
	public static String shanghu_desc ="";


	/**
	 * 商户名称营业执照一
	 */
	public static String shanghu_image_cred_1 = "";

	/**
	 * 商户名称营业执照二
	 */
	public static String shanghu_image_cred_2 = "";
	/**
	 * 审核状态
	 */
	public static String shanghu_verify = "3";

	public  static String getShanghu_id() {
		return shanghu_id;
	}

	public static void setShanghu_id(String shanghu_id) {
		Merchant.shanghu_id = shanghu_id;
	}

	public static String getShanghu_name() {
		return shanghu_name;
	}

	public static void setShanghu_name(String shanghu_name) {
		Merchant.shanghu_name = shanghu_name;
	}

	public static String getShanghu_desc() {
		return shanghu_desc;
	}
	
	public static void setShanghu_desc(String shanghu_desc) {
		Merchant.shanghu_desc = shanghu_desc;
	}

	public static String getShanghu_image_cred_1() {
		return shanghu_image_cred_1;
	}

	public static void setShanghu_image_cred_1(String shanghu_image_cred_1) {
		Merchant.shanghu_image_cred_1 = shanghu_image_cred_1;
	}

	public static String getShanghu_image_cred_2() {
		return shanghu_image_cred_2;
	}

	public static void setShanghu_image_cred_2(String shanghu_image_cred_2) {
		Merchant.shanghu_image_cred_2 = shanghu_image_cred_2;
	}

	public static String getShanghu_verify() {
		return shanghu_verify;
	}

	public static void  setShanghu_verify(String shanghu_verify) {
		Merchant.shanghu_verify = shanghu_verify;
	}

	@Override
	public String toString() {
		return "Merchant [getShanghu_id()=" + getShanghu_id()
				+ ", getShanghu_name()=" + getShanghu_name()
				+ ", getShanghu_desc()=" + getShanghu_desc()
				+ ", getShanghu_image_cred_1()=" + getShanghu_image_cred_1()
				+ ", getShanghu_image_cred_2()=" + getShanghu_image_cred_2()
				+ ", getShanghu_error_info()=" + getShanghu_verify() + "]";
	}
	
	
	
}
