package com.jack.contacts;

import com.guohua.common.util.BaseConstants;

public class PayForMobileConstants {

	// 生成订单
	public static final String PAYFORMOBILE = BaseConstants.MALLHRY199
			+ "/Service/MobileReChargeRestService.svc/";
	// 易卡通
	public static final String VOLUMN = BaseConstants.MALLHRY199
			+ "/Service/MobileReChargeRestService.svc/ReChargeSJ";
	// 易卡通充值购物券
	public static final String VOLUMN2 = BaseConstants.MALLHRY199
			+ "/Service/GiftActivitiesService.svc/ProvideApplyByTongjuan";
	// 支付宝
	public static final String ALIPAY = BaseConstants.WWWCZSSD
			+ "/OnlineAlipay/AlipayPage.aspx?money=";
	// 银联
	public static final String UNION = BaseConstants.MALLHRY199
			+ "/OnlinePayApi/YinlianPurchase";

	// 获取充值记录
	public static final String GETPAYHISTORY = BaseConstants.MALLHRY199
			+ "/Service/MobileReChargeRestService.svc/ReChargeQuery";

	// 匹配手机号
	public static final String SHOUJIHAO = "^1[34589]\\d{9}$";
	// 移动
	public static final String YIDONG = "^1((34[0-8]|(3[5-9]|47|5[012789]|8[23478])[0-9]))\\d{7}$";
	// 联通
	public static final String LIANTONG = "^1(3[0-2]|5[56]|8[56]|45)\\d{8}$";
	// 电信
	public static final String DIANXIN = "^1(33|53|8[019])\\d{8}$";
}
