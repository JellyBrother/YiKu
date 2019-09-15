package com.jack.contacts;

/**
 * url_head
 * 
 * @author Administrator
 * 
 */
public class Contacts {
	/**
	 * 支付宝回调接口
	 */
	public static final String URL_ALIPAY_HEAD = "http://182.254.161.94:8080/ydg/";

	/**
	 * 图片head
	 */
	public static final String URL_PICTURE_HEAD = "http://182.254.161.94:8080/ydg/";

	/**
	 * 获取店铺的商品分类接口
	 */
	public static final String URL_STORECAT_HEAD = "http://182.254.161.94:8080/ydg/storeCat!getStoreCatById?";

	/**
	 * 获取店铺的商品列表
	 */
	public static final String URL_STOREPRODUCT_HEAD = "http://182.254.161.94:8080/ydg/storeProduct!getStoreProdctByCat?";

	/**
	 * 获取银联tn接口
	 */
	public static final String URL_GET_TN = "http://182.254.161.94:8080/ydg/productOrder!sendPayOrder?";

	/**
	 * 获取手机支付权限
	 */
	public static final String GET_AUTHORITY = "http://182.254.161.94:8801/mobile/GetTryDingDan?";

}
