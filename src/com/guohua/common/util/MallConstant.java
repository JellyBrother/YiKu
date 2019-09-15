package com.guohua.common.util;

public class MallConstant {

	public static final String STORE_ID = "storeId";
	public static final String MainClass_ID = "mainClass";
	public static final String SubClass_ID = "subClass";

	public static final String PROD_ID = "prodId";
	public static final String ORDER_ID = "orderId";
	public static final String STR_ProddetailUrl = "DetailsApp";
	public static final String PROD_Detail_type = "proddetail_type";
	public static final boolean IsStoreSelected = false;

	public static final String MALL_DETAIL_URL = BaseConstants.MALLCZSSD
			+ "/Service/GoodService.svc/GetGoodDetails";
	public static final String MALL_SELECT_URL = BaseConstants.MALLCZSSD
			+ "/Service/SupMarketService.svc/GetNearSupMarkt";
	public static final String MALL_GetGoodCategoryList = BaseConstants.MALLCZSSD
			+ "/service/GoodCategoryService.svc/GetGoodCategoryList";
	public static final String Mall_GetGoodBySeach = BaseConstants.MALLCZSSD
			+ "/Service/GoodService.svc/GetGoodBySeach";
	public static final String Mall_DeliveryAddress = BaseConstants.MALLCZSSD
			+ "/Service/DeliveryAddress.svc/AddresAction";
	public static final String Mall_GetSpecialGoodList = BaseConstants.MALLCZSSD
			+ "/service/GoodBenefitService.svc/GetGoodByPageindex";
	public static final String Mall_order_url = BaseConstants.MALLCZSSD
			+ "/Service/OrderHandle.svc/OrderAction";
	public static final String Mall_user_money_url = BaseConstants.MALLCZSSD
			+ "/Service/PayHandle.svc/GetUserMoney";

	// public static final String mall_order_pay_alipay =
	// "http://www.czssd.com/OnlineAlipay/AlipayPage.aspx";
	public static final String mall_order_pay_alipay = "http://service.sht315.com/OnlineAlipay/AlipayPage.aspx";

	public static final String mall_order_pay_unionpay = "http://service.sht315.com/Yinlian/examples/purchase.aspx";

	public static final String mall_order_pay_volumn = BaseConstants.MALLCZSSD
			+ "/Service/PayHandle.svc/UserOrderPay";

	public static final String IMAGE_PREFIX = BaseConstants.MALLCZSSD + "/";

	public static final String Mall_OrderAction = BaseConstants.MALLCZSSD
			+ "/Service/OrderHandle.svc/OrderAction";
	public static final String Mall_OrderGoodAciton = BaseConstants.MALLCZSSD
			+ "/Service/OrderHandle.svc/OrderGoodAciton";

	public static final String MALL_GET_PROVINCE = BaseConstants.WWWCZSSD
			+ "/LianMengSH/Services/APPLMAccount.ashx";
}
