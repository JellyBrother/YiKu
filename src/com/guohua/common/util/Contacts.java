package com.guohua.common.util;

public class Contacts {
	
	/**
	 * 跟路径
	 */
	public static String URL_ROOT = "http://182.254.161.94:8080";

//	public static String URL_ROOT = "http://192.168.1.102:8080";
	
	
	
	
	/**
	 * 请求客户端
	 */
	public static String URL_ENDING = "&client_type=A&version=1";
	
	/**
	 * 获取验证码
	 */
	public static final String URL_GET_CODE = URL_ROOT + "/ydg/customer!getCode?";
	
	/**
	 *验证验证码
	 */
	public static final String URL_CHECK_CODE = URL_ROOT + "/ydg/customer!checkCode?";
	
	/**
	 *用户注册-本人信息提交
	 */
	public static final String URL_REG = URL_ROOT + "/ydg/customer!reg?";
	
	/**
	 *用户登录接口
	 */
	public static final String URL_LOGIN = URL_ROOT + "/ydg/customer!login?";
	
	/**
	 *找回密码接口-请求验证码
	 */
	public static final String URL_FIND_PASSWORD_GET_CODE = URL_ROOT + "/ydg/customer!getCode?";
	
	/**
	 *找回密码接口-输入新密码
	 */
	public static final String URL_GET_PASSWORD = URL_ROOT + "/ydg/customer!getPassword?";
	
	/**
	 *修改密码接口
	 */
	public static final String URL_UPDATE_PASSWORD = URL_ROOT + "/ydg/customer!updatePassword?";
	
	/**
	 *用户信息修改接口
	 */
	public static final String URL_UPDATE_INFO = URL_ROOT + "/ydg/customer!updateInfo?";
	
	/**
	 *获取用户地址列表接口
	 */
	public static final String URL_GET_ADDRESS_LIST = URL_ROOT + "/ydg/customerAddress!getAddressList?";
	
	/**
	 *用户地址增删改查接口
	 *action 0获取1 添加 2 修改 3 删除
	 */
	public static final String URL_ADDRESS_CRUD = URL_ROOT + "/ydg/customerAddress!addressCRUD?";
	
	/**
	 *保存用户默认地址接口
	 */
	public static final String URL_SAVE_ADDRESS = URL_ROOT + "/ydg/customerAddress!saveAddress?";
	
	/**
	 *获取配送店的列表接口
	 */
	public static final String URL_GET_STORE_INFO_LIST = URL_ROOT + "/ydg/storeInfo!getStoreInfoList?";
	
	/**
	 *获取店铺的商品分类接口
	 */
	public static final String URL_GET_STORE_CAT_BY_ID = URL_ROOT + "/ydg/storeCat!getStoreCatById?";
	
	/**
	 *获取店铺的促销信息接口
	 */
	public static final String URL_GET_STORE_FAVOR_INFO = URL_ROOT + "/ydg/storeFavor!getStoreFavorInfo?";
	
	/**
	 *用户收藏增删改查接口
	 *
	 *	customer_id
	 *	store_prod_id
	 *	id
	 *	action 0获取1 添加 2 修改（暂无） 3 删除
	 */
	public static final String URL_MARKER_CRUD = URL_ROOT + "/ydg/customerMarker!markerCRUD?";
	
	/**
	 *获取用户收藏列表接口
	 *
	 *	customer_id 用户的id
	 */
	public static final String URL_GET_MARKER_LIST = URL_ROOT + "/ydg/customerMarker!getMarkerList?";
	
	/**
	 * 获取用户的优惠券接口
	 */
	public static final String URL_GET_FAVOR_LIST = URL_ROOT + "/ydg/customerFavor!getFavorList?";
	
	/**
	 * 获取用户的订单列表
	 */
	public static final String URL_GET_CUSTOMER_ORDERS = URL_ROOT + "/ydg/customer!getProductOrderList?";
	
	/**
	 * 获取订单详情
	 */
	public static final String URL_GET_ORDER_DETAIL_INFO = URL_ROOT + "/ydg/productOrder!getInfo?";
	
	/**
	 * 生成订单
	 */
	public static final String URL_ADD_ORDER = URL_ROOT + "/ydg/productOrder!addOrder?";
	
	/**
	 * 获取用户的基本信息
	 */
	public static final String URL_GET_CUSTOMER_INFO = URL_ROOT + "/ydg/customer!getCustomerInfo?";
	
	/**
	 * 修改订单状态接口
	 * order_item_id	订单商品的id
	 * new_status 		2发货3 确认收货
	 */
	public static final String URL_UPDATE_PAY_STATUS = URL_ROOT + "/ydg/productOrder!updatePayStatus?";
	
	/**
	 * 用户评论接口
	 */
	public static final String URL_SAVE_CUSTOMER_COMMENT = URL_ROOT + "/ydg/customerComment!saveCustomerComment?";
	
	/**
	 * 个人余额充值接口
	 * 
	 * custom_id 用户的id
	 * 
	 * money_fen 充值余额，以分为单位
	 */
	public static final String URL_ADD_CUSTOMER_FEE = URL_ROOT + "/ydg/customerFeeHistory!addCustomerFee?";
	/**
	 * 选择提货点
	 */
	public static final String URL_CHECK_TIHUO_ADDRESS = URL_ROOT + "/ydg/storeInfo!getStoreInfoByDinates?";
	/**
	 * 获取店铺详情
	 */
	public static final String URL_GET_STORE_DETAILS = URL_ROOT + "/ydg/storeInfo!getStoreInfo?"; 
	/**
	 * 店铺评论接口
	 */
	public static final String URL_GET_STORE_PINGLUN = URL_ROOT + "/ydg/customerComment!getCommentList?";
	/**
	 * 获取商品详情接口
	 * id 店铺商品id(对应store_prod_id)
	 */
	public static final String URL_GET_STORE_PRODCT_DETAILS = URL_ROOT + "/ydg/storeProduct!getStoreProdctDetails?";
	
	/**
	 * 获取用户充值明细列表接口
	 * customerId
	 */
	public static final String URL_CUSTOMER_OUT_ACTION = URL_ROOT + "/ydg/customerOut.action?";

	/**
	 * 支付接口
	 * 
	 * customer_id	int	用户id
	 * 
	 * order_id	String	订单号
	 * 
	 * pay_act	int	订单来源:0余额充值,1商品付款
	 * 
	 * pay_type	int	支付类型:0余额支付,1支付宝,2银联
	 * 
	 * total_price	int	订单总价（单位：分）
	 * 
	 * store_id	int	店铺id 余额支付必填其他不必填
	 */
	public static final String URL_SEND_PAY_ORDER = URL_ROOT + "/ydg/productOrder!sendPayOrder?";
	
	/**
	 * 
	 * 余额支付接口
	 * client_type=A&
	 * customerId=1&
	 * fee=1000&
	 * orderId=2&
	 * storeId=3
	 */
	public static final String URL_AJAX_PAY_BY_REMAIN_ACTION = URL_ROOT + "/ydg/ajaxPayByRemain.action?";

	/**
	 * 易店行业首页接口
	 */
	public static final String URL_YI_DIAN_INDUSTRY = URL_ROOT + "/ydg/storeInfo!getStoreMainInfo?";
	/**
	 * 店铺满减优惠列表
	 */
	public static final String URL_YI_DIAN_QUERY_STOREFAVOR_LIST = URL_ROOT + "/ydg/storeFavor!queryStoreFavorList?";
	/**
	 * 易店行业排序接口
	 */
	public static final String URL_YI_DIAN_INDUSTRY_SORT = URL_ROOT + "/ydg/storeInfo!getStoreMainOrderInfo?";
	
	/**
	 * 协议密码
	 */
	public static final String URL_YST_PROTOCOL = "yst305";
	
	/**
	 * 获取省列表接口
	 */
	public static final String URL_GET_PROV = URL_ROOT + "/ydg/customerAddress!getProv?client_type=A&version=1";
	public static final String URL_GET_YST_PROV = "http://182.254.161.94:8801/sdm/GetProvince?ident=" + MD5.md5(URL_YST_PROTOCOL);
	
	/**
	 * 获取市列表接口
	 */
	public static final String URL_GET_CITY = URL_ROOT + "/ydg/customerAddress!getCity?client_type=A&version=1&prov_id=";
	public static final String URL_GET_YST_CITY = "http://182.254.161.94:8801/sdm/GetCity?ident=";
	
	/**
	 * 获取单位列表
	 */
	public static final String URL_GET_YST_UNITS = "http://182.254.161.94:8801/sdm/GetUnit?ident=";
	
	/**
	 * 查询商品信息
	 */
	public static final String URL_GET_YST_RRODUCT_INFO = "http://182.254.161.94:8801/sdm/GetGoodInfo?ident=";
	
	/**
	 * 查询欠费信息
	 */
	public static final String URL_GET_ACCOUNT_BALANCE = "http://182.254.161.94:8801/sdm/GetAccountBalance?";
	
	/**
	 * 获取App图文详情接口
	 */
	public static final String URL_GET_GOOD_INFO = URL_ROOT + "/ydg/productBasic!getWebView?client_type=A&version=1&id=";
	
	/**
	 * 手机充值接口
	 */
	public static final String URL_PAY_MOBILE = " http://182.254.161.94:8080/ydg/ydmvc/after/liftService/createorder.do?";
	
	/**
	 * 水电煤缴费接口
	 */
	public static final String URL_PAY_THE_FEES = "http://182.254.161.94:8080/ydg/ydmvc/after/liftService/GetPaySDM.do?";
}
