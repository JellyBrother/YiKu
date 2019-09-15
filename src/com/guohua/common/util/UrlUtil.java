package com.guohua.common.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class UrlUtil {

	private static final String TAG = "UrlUtil";

	/**
	 * 拼接完成的字符串
	 * 
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 * @return 访问大众点评的正确URL地址
	 */
	public static URL codecUrl(String apiUrl,
			String serviceName,Map<String, String> paramMap) {
		try {
			

			// 以下sha1签名代码效果等同
			// byte[] sha = DigestUtils.sha(StringUtils.getBytesUtf8(codes));
			// String sign1 = Hex.encodeHexString(sha).toUpperCase();

			// 使用签名生成访问
			StringBuilder sb = new StringBuilder();
			sb.append("serviceName=").append(serviceName);

			for (java.util.Map.Entry<String, String> entry : paramMap
					.entrySet()) {
				sb.append('&').append(entry.getKey()).append('=')
						.append(URLEncoder.encode(entry.getValue(), "utf-8"));// entry.getValue());
			}

			String requestUrl = apiUrl + "?" + sb.toString(); // +
																// URLEncoder.encode((
																// sb.toString()),
																// "UTF-8");
			Log.d("tag", "after sign the url is " + requestUrl);

			return new URL(requestUrl);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}// requestUrl);

		return null;
	}
	
	/**
	 * 拼接完成的字符串
	 * 
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 * @return 访问大众点评的正确URL地址
	 */
	public static URL codecUrl(String apiUrl,
			Map<String, String> paramMap) {
		try {
			

			// 以下sha1签名代码效果等同
			// byte[] sha = DigestUtils.sha(StringUtils.getBytesUtf8(codes));
			// String sign1 = Hex.encodeHexString(sha).toUpperCase();

			// 使用签名生成访问
			StringBuilder sb = new StringBuilder();
			sb.append("serviceName=").append("get");

			for (java.util.Map.Entry<String, String> entry : paramMap
					.entrySet()) {
				sb.append('&').append(entry.getKey()).append('=')
						.append(URLEncoder.encode(entry.getValue(), "utf-8"));// entry.getValue());
			}

			String requestUrl = apiUrl + "?" + sb.toString(); // +
																// URLEncoder.encode((
																// sb.toString()),
																// "UTF-8");
			Log.d("tag", "after sign the url is " + requestUrl);

			return new URL(requestUrl);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}// requestUrl);

		return null;
	}
	
	
	

	/**
	 * 返回结果
	 * 
	 * @param inputStream
	 */
	public static String readStream(InputStream inputStream) {
		String value = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"));
			StringBuffer buf = new StringBuffer();
			String str;

			while ((str = reader.readLine()) != null) {
				buf.append(str);
			}
			value = buf.toString();
			return value;
			/*
			 * Message msg = new Message(); msg.what = CODE; msg.obj = value;
			 * handler.sendMessage(msg); Log.d(TAG, "大众点评返回的数据是：" + value);
			 */

		} catch (Exception e) {
		}

		return null;
	}
	
	public static void addParamToMap(HashMap<String,String> map,String name,String value){
		if(value!=null){
			map.put(name, value);
		}else{
			Log.e(TAG, name + "'s value is null, ignore it");
		}
	}

	
}
