package com.yst.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.MD5;
import com.jack.ui.MProcessDialog;
import com.yst.yiku.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 注册界面
 * @author lixiangchao
 *
 */
public class RegisterActivity extends Activity implements OnClickListener {

	//一个是显示输入手机号的界面，一个是显示密码界面
	private View register_main_view1, register_main_view2 ;
	
	private ImageView activity_back_iv ;
	private TextView activity_title_tv ;
	
	//注册输入手机号界面
	private EditText register_name_et, register_coupons_et ;
	private Button register_next_btn ;
	
	//注册输入密码界面
	private EditText register_passwd_et, register_re_passwd_et ;
	private Button register_btn ;
	
	private RequestQueue mQueue ;
	private SharedPreferences msp ;
	private MProcessDialog mInfoProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initView();
	}
	
	//初始化控件
	private void initView(){
		msp = getSharedPreferences("yiku", MODE_PRIVATE);
		mQueue = Volley.newRequestQueue(this);
		
		register_main_view1 = findViewById(R.id.register_main_view1);
		register_main_view2 = findViewById(R.id.register_main_view2);
		
		register_name_et = (EditText) register_main_view1.findViewById(R.id.register_name_et);
		register_coupons_et = (EditText) register_main_view1.findViewById(R.id.register_coupons_et);
		register_next_btn = (Button) register_main_view1.findViewById(R.id.register_next_btn);
		register_next_btn.setOnClickListener(this);
		
		register_passwd_et = (EditText) register_main_view2.findViewById(R.id.register_passwd_et);
		register_re_passwd_et = (EditText) register_main_view2.findViewById(R.id.register_re_passwd_et);
		register_btn = (Button) register_main_view2.findViewById(R.id.register_btn);
		register_btn.setOnClickListener(this);
		
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_register_label);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.register_btn:
			registerInfo();
			break;
		case R.id.register_next_btn:
			showNextView();
			break;
		case R.id.activity_back_iv:
			showExitMessage();
			break;
		}
	}
	String passwd = "", repasswd = "" , user = "", referee = "";
	
	//显示下一步的布局文件
	private void showNextView(){
		user = register_name_et.getText().toString().trim();
		referee = register_coupons_et.getText().toString().trim();
		
		if(TextUtils.isEmpty(user)){
			Toast.makeText(this, "请输入手机号 ", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		if(! LoginActivity.isMobileNO(user)){
			Toast.makeText(this, "请输入合理的手机号", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		if(!TextUtils.isEmpty(referee)){
			if(! LoginActivity.isMobileNO(referee)){
				Toast.makeText(this, "请输入合理的推荐人手机号", Toast.LENGTH_SHORT).show();
				return ;
			}
			if(user.equals(referee)){
				Toast.makeText(this, "推荐人手机号不能是自己的手机号", Toast.LENGTH_SHORT).show();
				return ;
			}
		}
		
		register_main_view1.setVisibility(View.GONE);
		register_main_view2.setVisibility(View.VISIBLE);
	}
	
	//注册用户
	private void registerInfo(){
		 passwd = register_passwd_et.getText().toString().trim();
		 repasswd = register_re_passwd_et.getText().toString().trim();
		if(TextUtils.isEmpty(passwd)){
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return ;
		} else if(passwd.length() != 6){
			Toast.makeText(this, "请设置6位字符的密码", Toast.LENGTH_SHORT).show();
			return ;
		}
		if(! passwd.equals(repasswd)){
			Toast.makeText(this, "两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		//TODO register server
		
		showInfoProgressDialog();
		
		Log.e("sss", "===== " + Contacts.URL_REG + 
				"phone=" + user + "&recomm_customer=" + referee + "&password=" + MD5.md5(passwd) + Contacts.URL_ENDING);
		
		JsonObjectRequest loginjrs = new JsonObjectRequest(Contacts.URL_REG + 
				"phone=" + user + "&recomm_customer=" + referee + "&password=" + MD5.md5(passwd) + Contacts.URL_ENDING, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject json) {
						try {
							Log.e("sss","response is " + json.toString());
							
							String success = json.getString("result");
							if ("SUCCESS".equals(success)) {
								Toast.makeText(getApplicationContext(), "用户注册成功!", Toast.LENGTH_SHORT).show();
								msp.edit().putBoolean("remember", false).commit();
								msp.edit().putString("user", user).commit();
								msp.edit().putString("key", passwd).commit();
								msp.edit().putString("referee", referee).commit();
//								JSONObject dataJson = json.getJSONObject("data");
//								if(dataJson.has("pay_password")){
//									msp.edit().putString("paypass",dataJson.getString("pay_password")).commit();
//								}else{
//									msp.edit().putString("paypass","").commit();
//								}
//								msp.edit().putString("customer_id", dataJson.getString("customer_id")).commit();
//								msp.edit().putString("phone", dataJson.getString("phone")).commit();
//								msp.edit().putString("password", dataJson.getString("password")).commit();
//								msp.edit().putString("huanxin_id", dataJson.getString("huanxin_id")).commit();
//								msp.edit().putString("huanxin_password", dataJson.getString("huanxin_password")).commit();
								RegisterActivity.this.finish();
							} else {
								Toast.makeText(getApplicationContext(),	"" + json.getString("error_info"),
										Toast.LENGTH_SHORT).show();
							}
							dismissInfoProgressDialog();
						} catch (JSONException e) {
							e.printStackTrace();
							dismissInfoProgressDialog();
							Toast.makeText(getApplicationContext(),	"注册信息解析异常!",	Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError arg0) {
						dismissInfoProgressDialog();
						Toast.makeText(getApplicationContext(), "网络请求错误，请重试!",	Toast.LENGTH_SHORT).show();
						if(null == arg0){
							Log.e("sss","the register error message is null");
						} else {
							Log.e("sss","the register error message is " + arg0.getMessage());
						}
					}
				});
		mQueue.add(loginjrs);
	}
	
	//可能要谈一个提示框来提醒用户放弃注册
	private void showExitMessage(){
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LoginActivity.hintKbTwo(RegisterActivity.this);
	}
	
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

	public void dismissInfoProgressDialog() {
		mInfoProgressDialog.dismiss();
	}
}
