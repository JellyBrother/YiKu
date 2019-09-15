package com.yst.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.guohua.common.util.MD5;
import com.jack.fragment.YiKuFragment;
import com.jack.ui.MProcessDialog;
import com.yst.yiku.R;

/**
 * 登录界面
 * 
 * @author Administrator
 * 
 */
public class LoginActivity extends Activity {

	private EditText mAccountEt, mKeyEt;
	private CheckBox mRememberCheckbox;
	private RequestQueue mQueue;
	private SharedPreferences msp;

	private int loginIndex = 0, index;
	private String store_id;
	private Intent intent = null;
	private Bundle bundle = null;
	private boolean flag = false;
	
	private ImageView activity_back_iv ;
	private TextView activity_title_tv ;

	private String mAccountStr;
	private String mPasswdStr;
	
	private List<Map<String, String>> cart_list = null ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		
		
		loginIndex = getIntent().getIntExtra("activity", -1);
		if(loginIndex == StoreProductDetailsActivity.ACTIVITY1){
			index = getIntent().getIntExtra("index", StoreProductDetailsActivity.ACTIVITY1);
			cart_list = (List<Map<String, String>>) getIntent().getSerializableExtra("cart_products");
			store_id = getIntent().getStringExtra("store_id");
		} else if(loginIndex == StoreProductDetailsActivity.ACTIVITY2){
			index = getIntent().getIntExtra("index", StoreProductDetailsActivity.ACTIVITY1);
			cart_list = (List<Map<String, String>>) getIntent().getSerializableExtra("cart_products");
			store_id = getIntent().getStringExtra("store_id");
		}
		
		initView();
	}
	
	//初始化界面控件
	private void initView() {
		msp = getSharedPreferences("yiku", MODE_PRIVATE);
		mQueue = Volley.newRequestQueue(this);
		mAccountEt = (EditText) findViewById(R.id.login_user_account);
		mKeyEt = (EditText) findViewById(R.id.login_user_passwd);
		mRememberCheckbox = (CheckBox) findViewById(R.id.login_rememeber_checkbox);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LoginActivity.this.finish();
			}
		});
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_login_label);
	}

	@Override
	protected void onResume() {
		if (!TextUtils.isEmpty(msp.getString("user", ""))) {
			mAccountEt.setText(msp.getString("user", ""));
			mKeyEt.setText(msp.getString("key", ""));
		}
		mRememberCheckbox.setChecked(msp.getBoolean("remember", false));
		super.onResume();
	}

	public void onBtnClick(View v) {
		switch (v.getId()) {
		case R.id.login_register_iv:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
		case R.id.login_btn:
			checkUser();
			break;
		case R.id.login_forget_passwd_iv:
			startActivity(new Intent(this, ForgetPasswdActivity.class));
			break;
		case R.id.login_remember_check_content:
			if (mRememberCheckbox.isChecked()) {
				mRememberCheckbox.setChecked(false);
			} else {
				mRememberCheckbox.setChecked(true);
			}
			break;
		}
	}

	// 检测用户和密码是否正确
	private void checkUser() {
		mAccountStr = mAccountEt.getText().toString().trim();
		mPasswdStr = mKeyEt.getText().toString().trim();
		if (TextUtils.isEmpty(mAccountStr)) {
			Toast.makeText(this, "请输入手机号!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(mPasswdStr)) {
			Toast.makeText(this, "请输入密码!", Toast.LENGTH_SHORT).show();
			return;
		}
		if(!LoginActivity.isMobileNO(mAccountStr)){
			Toast.makeText(this, "请输入有效的手机号", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		submitMessage();
	}
	
	// 提交注册信息
	private void submitMessage() {
		//TODO
		String urls = Contacts.URL_LOGIN + "phone=" + mAccountStr + "&password=" + MD5.md5(mPasswdStr) + Contacts.URL_ENDING;
		
		showInfoProgressDialog();
		
		JsonObjectRequest loginjrs = new JsonObjectRequest(urls, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					String success = response.getString("result");
					
					Log.e("sss","login message is " + response.toString());
					
					if("SUCCESS".equals(success)){
						
						Customer.customer_id = response.getJSONObject("data").getString("customer_id");
						if(response.getJSONObject("data").has("recomm_customer")){
							msp.edit().putString("referee", response.getJSONObject("data").getString("recomm_customer")).commit();
						} else {
							msp.edit().putString("referee", "").commit();
						}
						
						
						if(response.getJSONObject("data").has("address_id")){
							msp.edit().putString("address_id", response.getJSONObject("data").getString("address_id")).commit();
							Customer.address_id = response.getJSONObject("data").getString("address_id");
						} else {
							msp.edit().putString("address_id", "").commit();
						}
						
						msp.edit().putString("customer_id", response.getJSONObject("data").getString("customer_id")).commit();
						msp.edit().putString("user", mAccountStr).commit();
						msp.edit().putString("key", mPasswdStr).commit();
						msp.edit().putBoolean("remember", mRememberCheckbox.isChecked()).commit();
						msp.edit().putBoolean("logout", false).commit();
						Customer.customer_id = response.getJSONObject("data").getString("customer_id");
						MainActivity.isLogin = true ;
						
						switch(loginIndex){
						case StoreProductDetailsActivity.ACTIVITY1:
							Intent it = new Intent(LoginActivity.this, SubmitOrderActivity.class);
							it.putExtra("cart_products", (Serializable)cart_list);
							it.putExtra("index", StoreProductDetailsActivity.ACTIVITY1);
							it.putExtra("store_id", store_id);
							it.putExtra("activity", StoreProductDetailsActivity.ACTIVITY1);
							startActivity(it);
							break;
						case StoreProductDetailsActivity.ACTIVITY2:
							Intent it1 = new Intent(LoginActivity.this, SubmitOrderActivity.class);
							it1.putExtra("cart_products", (Serializable)cart_list);
							it1.putExtra("index", StoreProductDetailsActivity.ACTIVITY2);
							it1.putExtra("store_id", store_id);
							it1.putExtra("activity", StoreProductDetailsActivity.ACTIVITY2);
							startActivity(it1);
							break;
						case StoreProductDetailsActivity.ACTIVITY3:
							break;
						case StoreProductDetailsActivity.ACTIVITY4:
							break;
						case StoreProductDetailsActivity.ACTIVITY5:
							break;
						}
						
						LoginActivity.this.finish();
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
					dismissInfoProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
					dismissInfoProgressDialog();
					Toast.makeText(getApplicationContext(), "验证信息解析异常", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(getApplicationContext(), "网络请求错误，请重试!",	Toast.LENGTH_SHORT).show();
				dismissInfoProgressDialog();
			}
		});
		mQueue.add(loginjrs);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		hintKbTwo(this);
	}
	
	//用来隐藏键盘的方法
	public static void hintKbTwo(Activity context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive() && context.getCurrentFocus() != null) {
			if (context.getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(context.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}

	//正则匹配是否是手机号
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	private MProcessDialog mInfoProgressDialog;
	
	//显示进度条
	public void showInfoProgressDialog() {
		if (mInfoProgressDialog == null)
			mInfoProgressDialog = new MProcessDialog(this);
		mInfoProgressDialog.setMessage("加载中");
		mInfoProgressDialog.setCancelable(false);
		if (!this.isFinishing()) {
			try {
				mInfoProgressDialog.show();
			} catch (Exception e) {

			}
		}
	}

	//关闭进度条
	public void dismissInfoProgressDialog() {
		mInfoProgressDialog.dismiss();
	}

}
