package com.yst.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.guohua.common.util.MD5;
import com.jack.ui.MProcessDialog;
import com.yst.yiku.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
 * 忘记密码界面
 *
 */
public class ForgetPasswdActivity extends Activity implements OnClickListener {

	//界面返回和提示标题
	private TextView activity_title_tv ;
	private ImageView activity_back_iv;

	private Button mGetCodeBtn, mNextBtn;
	private RequestQueue mQueue;
	private SharedPreferences msp;
	private TimeCount mTimeCount ;
	private EditText mPhoneEt, mCodeEt, mPasswdEt, mRePasswdEt;
	private MProcessDialog mInfoProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_passwd);

		initView();
	}
	
	//初始化界面控件
	private void initView() {
		mTimeCount = new TimeCount(60000, 1000);
		mQueue = Volley.newRequestQueue(this);
		msp = getSharedPreferences("yiku", MODE_PRIVATE);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_forget_passwd_label);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		
		mPhoneEt = (EditText) findViewById(R.id.forget_passwd_phones_et);
		mCodeEt = (EditText) findViewById(R.id.forget_passwd_code_et);
		mPasswdEt = (EditText) findViewById(R.id.forget_passwd_set_pwd_et);
		mRePasswdEt = (EditText) findViewById(R.id.forget_passwd_reset_pwd_et);
		mNextBtn = (Button) findViewById(R.id.forget_passwd_next_btn);
		mGetCodeBtn = (Button) findViewById(R.id.forget_passwd_getcode_btn);
		mGetCodeBtn.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);
		activity_back_iv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_back_iv:
			this.finish();
			break;
		case R.id.forget_passwd_next_btn:
			resetKey();
			break;
		case R.id.forget_passwd_getcode_btn:
			getCode();
			break;
		}
	}
	
	//获取验证码，
	private void getCode() {
		final String mPhoneStr = mPhoneEt.getText().toString().trim();

		if (TextUtils.isEmpty(mPhoneStr) || !LoginActivity.isMobileNO(mPhoneStr)) {
			Toast.makeText(this, "请输入有效的手机号!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		mTimeCount.start();
		
		String url = Contacts.URL_GET_CODE + "phone=" + mPhoneStr + Contacts.URL_ENDING;

		JsonObjectRequest loginjrs = new JsonObjectRequest(url, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							String success = response.getString("result")
									.toLowerCase();
							if ("success".equals(success)) {
								Toast.makeText(getApplicationContext(),
										"验证码已发送!", Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(getApplicationContext(), "数据解析异常!",
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(getApplicationContext(), "网络请求失败，请重试!",
								Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(loginjrs);
	}

	// 重置密码
	private void resetKey() {
		final String mPhoneStr = mPhoneEt.getText().toString().trim();
		final String mCodeStr = mCodeEt.getText().toString().trim();
		final String mPasswdStr = mPasswdEt.getText().toString().trim();
		final String mRePasswdStr = mRePasswdEt.getText().toString().trim();

		if (TextUtils.isEmpty(mPhoneStr)) {
			Toast.makeText(this, "请输入您的手机号", Toast.LENGTH_SHORT).show();
			return;
		} else if (!LoginActivity.isMobileNO(mPhoneStr)) {
			Toast.makeText(this, "请输入有效的手机号", Toast.LENGTH_SHORT).show();
			return;
		}

		if (TextUtils.isEmpty(mCodeStr)) {
			Toast.makeText(this, "请输入验证码!", Toast.LENGTH_SHORT).show();
			return;
		}

		if (TextUtils.isEmpty(mPasswdStr)) {
			Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			return;
		}else if(mPasswdStr.length() != 6){
			Toast.makeText(this, "请输入有效的密码", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!mPasswdStr.equals(mRePasswdStr)) {
			Toast.makeText(this, "两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
			return;
		}
		
		showInfoProgressDialog();
		
		String url =  Contacts.URL_GET_PASSWORD + "phone="	+ mPhoneStr + "&password=" + MD5.md5(mPasswdStr) + "&code="
					+ mCodeStr + Contacts.URL_ENDING;
		
		JsonObjectRequest loginjrs = new JsonObjectRequest(url, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							String success = response.getString("result");
							Log.e("sss","the forget password message is ==== " + response.toString());
							if ("SUCCESS".equals(success)) {
								Toast.makeText(getApplicationContext(), "密码修改成功!", Toast.LENGTH_SHORT).show();
								msp.edit().putString("key", mPasswdStr).commit();
								Customer.key = mPasswdStr;
								ForgetPasswdActivity.this.finish();
							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
							}
							dismissInfoProgressDialog();
						} catch (JSONException e) {
							e.printStackTrace();
							dismissInfoProgressDialog();
							Toast.makeText(getApplicationContext(), "数据解析异常!",	Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError arg0) {
						dismissInfoProgressDialog();
						Toast.makeText(getApplicationContext(), "网络请求失败，请重试!",
								Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(loginjrs);
	}
	
	// 倒计时控制器
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mGetCodeBtn.setEnabled(false);
			mGetCodeBtn.setBackgroundColor(Color.parseColor("#00BDFF"));
//			mGetCodeBtn.setBackgroundResource(R.drawable.forget_pwd_getcode_obtain_focus);
			mGetCodeBtn.setText("重新获取 " + (millisUntilFinished + 1000)
					/ 1000 + "s");
		}

		@Override
		public void onFinish() {
			mGetCodeBtn.setText("获取验证码");
			mGetCodeBtn.setBackgroundColor(Color.parseColor("#00BDFF"));
//			mGetCodeBtn.setBackgroundResource(R.drawable.forget_pwd_get_verification_code_normal);
			mGetCodeBtn.setEnabled(true);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LoginActivity.hintKbTwo(ForgetPasswdActivity.this);//隐藏键盘
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