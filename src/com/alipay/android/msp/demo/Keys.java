/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.alipay.android.msp.demo;

//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	// 合作身份者id，以2088开头的16位纯数字
	// public static final String DEFAULT_PARTNER =
	// "2088611106110810";//万邦惠通合作者ID
	public static final String DEFAULT_PARTNER = "2088021843657028";

	// 收款支付宝账号
	// public static final String DEFAULT_SELLER =
	// "734971012@qq.com";//万邦惠通支付宝账号
	public static final String DEFAULT_SELLER = "754390723@qq.com";

	// 商户私钥，自助生成
	/*
	 * public static final String PRIVATE =
	 * "MIICWwIBAAKBgQDAA7JDUXsIlNEe8oJfEgUhrUYDscuyPU5cXgTqEHrYtVdhbp00" +
	 * "KYYo8ivjk+hqdsyUOgkgFO2iKBu/rrLNztjPrTFG3lu80vrRuy4hRhLHJZL6YqDs" +
	 * "co1gqc9byq7fKFVwWFy5aVWJN+9YXAfd88tCTmv+3jobEECMSphv+Zr/YQIDAQAB" +
	 * "AoGAE40MzJ/ySBhnBqCot6dtEFXFSEEFPZvSa8NC+tY8u+4S/J+3sAT+XRLzdxOr" +
	 * "WZPJFtql7TdUTuuIj8bo+92w459OfFz6HuL7QOEsE0COk4HtHOhcBHGtGNZJ4aui" +
	 * "w3nwNoZSsuoBPFk1n+pXOqS5fBk3OOhLfVV8jmn6L/VT3TECQQDmoOGtyMDRion4" +
	 * "qrudeyLpF/Z15VSSPdEU9rE4S9pXA5w70l4c6s0+Q5wyvUqSztnakXCnxM19Faa2" +
	 * "T93CQmaVAkEA1SNXIYRfhEqTA0XOrHlBUNPo+bPZ8zQI1XYcpSsUmLT7B6l/rbN/" +
	 * "E0KnBjiy6lxmgeu818zTFLm6ZlbYSWI+nQJAS25VQdzGTAKu67kT0gcjPO/MeXT/" +
	 * "ezFiwCOLhSkyCYGxyNMeWwDlDjdY0DOfP3MOR/GH0ieJOh8pvrlSg6XqHQJAMVu6" +
	 * "zNdD63X9r69V2llGR/qbJGCDI7A0L4LnxVRBs8+NpsuY9tfvoL05wtYUkPOPBZ1b" +
	 * "ITqc01rjkNmhwvTOfQJAK5N/3Bv5MD7/lT6vGiw6Xv0vW600hzQSvttxjEGK9div" +
	 * "WbjGbwKhhrUS4cU+8yqPMkpO1UXqjIIdB/ZKGFCfiQ==";
	 */

	public static final String PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCA"
			+ "mAwggJcAgEAAoGBALg+juB+ch5RKv0wMr0F0QO3hJ5u/zLFUn2bWQvhSQ8nGvJ9"
			+ "gz4Nc7gCg+164tNfASjrx3EqO4XjJAHqPnPzPRJ5Msfq/eo57SQ8CDpTAAYmifZ2"
			+ "Arhioja5KejpYRV9Pzc3eIu/SzycA1Hm6cGCE8A8SRRdLdaF1w8/xTtNF8n5AgMB"
			+ "AAECgYEAqTk8PdGlu1bN51L3p0nKWmGau3Izj0xRofCnbxPy6KoARC0n+Dyexqn4x"
			+ "oyR/ZZLGGzG+JfTp/uiPOgLiLAmrFraaloSqyLjMnayhf70/t9AKA8G0i24/q8SWaK"
			+ "7VGYWW/tLcdpCGO3ZkcFz3MJ1mt5vN4+BxlkiaBwWf5akickCQQDq7W6tbt7YXFiqmua"
			+ "MWL00/Wu7ln442iSKPJtJeHdC5ySa6KP/JikaUgtc/TlFHKTpPbd13NrBkdYICLFh/etj"
			+ "AkEAyMVOITi0g1C5OLCl4GEe/eL2zokKMk7RlMwCJdZeRmkcDfSvnQQV5aAYbszscahHD"
			+ "X3pNEV79EYrvgyqQK+p8wJASmrb3GR6gt79100qKtsRVG/SaKHLagbv/DomlqdqX+8IUWn"
			+ "QSUq1Snki0FoGgjYIpzm9m8BSK28e9XyLdBcTMQJAUNw+YlzvPvAVGUL0G9Gh5Mkzc+13oN"
			+ "ljyZT8zHZxE9Sjiu9gh1Gs7AW1sXJxTDllHIb4lKfji3dziKCbNWBKhwJATbjzXxU6wKZlXx"
			+ "0LqPxE6wVv6+LR7KdjKgNs6pT41+tLlBYwjVwmcHvsmLy8adm1trzBsLkjo9l8UfioAeZ+dg==";

	// string seller_email = "pay@zsshopping.com";
	// Net--沈国峰 11:34:06
	// key = "mgrict7vot89pnaudubjjsi6kr272re4";
	// partner = "2088011018546204";
	//

	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCsxVE/Rv+veQZltgW52+Uz7CVrb7n67jdI1vH9"
			+ "ndfPh6o+YO29lBd8INZJSl1fTlxX7pr9DLP0ua/7hD+pr5oOpuABXU28sSsW4gAatLytI0MjcDdh"
			+ "9hQFikKfWK4oS3AoS7EPeuV9bN/pXfKosMlHGW/p+XLGzy/RciDjN10UZQIDAQAB";
}
