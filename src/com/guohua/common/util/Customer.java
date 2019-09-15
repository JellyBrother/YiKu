package com.guohua.common.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class Customer {
	
	//用户的id
	public static String customer_id = "";
	//用户注册的手机号
	public static String user = "";
	//用户的登录密码
	public static String key = "";
	//用户的默认地址索引
	public static String address_id = "";
	
	private static Customer customer = null ;
	
	private SharedPreferences msp ;
	
	private Customer(Activity activity){
		msp = activity.getSharedPreferences("yiku", activity.MODE_PRIVATE);
	}
	
	public static Customer getInstance(Activity context){
		if(null == customer){
			customer = new Customer(context);
		}
		return customer ;
	}
	
	public void setLogin(){
		customer_id = msp.getString("customer_id", "");
		user = msp.getString("user", "");
		key = msp.getString("key", "");
	}
	
	public void setLogout(){
		customer_id = "";
		user = "";
		key = "";
		address_id = "";
//		msp.edit().putString("key", "").commit();
		msp.edit().putString("referee", "").commit();
		msp.edit().putString("customer_id", "").commit();
		msp.edit().putBoolean("logout", true).commit();
		msp.edit().putString("customer_id", "").commit();
		msp.edit().putString("address_id", "").commit();
	}
}
