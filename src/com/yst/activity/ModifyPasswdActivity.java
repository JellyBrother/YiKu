package com.yst.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.jack.ui.MProcessDialog;
import com.yst.yiku.R;

/**
 * 修改登录密码
 * 
 * @author lixiangchao
 *
 */
public class ModifyPasswdActivity extends Activity implements OnClickListener {

	private EditText mInitEt, mPasswdEt, mRePasswdEt;
	private ImageView activity_back_iv;
	private TextView activity_title_tv;
	private Button mModifyIv;
	private RequestQueue mQueue;
	private SharedPreferences msp;
	private MProcessDialog mInfoProgressDialog;

	private int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_login_pwd);

		flag = getIntent().getIntExtra("FLAG", 0);

		initView();
	}

	// 初始化界面控件
	private void initView() {
		mQueue = Volley.newRequestQueue(this);
		msp = getSharedPreferences("yiku", MODE_PRIVATE);
		mInitEt = (EditText) findViewById(R.id.modify_login_pwd_init_et);
		mPasswdEt = (EditText) findViewById(R.id.modify_login_pwd_pwd_et);
		mRePasswdEt = (EditText) findViewById(R.id.modify_login_pwd_re_pwd_et);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv
				.setText(R.string.activity_customer_modify_passwd_label);
		mModifyIv = (Button) findViewById(R.id.modify_login_pwd_modify_iv);
		activity_back_iv.setOnClickListener(this);
		mModifyIv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.modify_login_pwd_modify_iv:
			modifyLoginPasswd();
			break;
		case R.id.activity_back_iv:
			LoginActivity.hintKbTwo(ModifyPasswdActivity.this);
			this.finish();
			break;
		}
	}

	// 修改密码方法
	private void modifyLoginPasswd() {
		final String mInitPwdStr = mInitEt.getText().toString().trim();
		final String mPasswdStr = mPasswdEt.getText().toString().trim();
		final String mRePasswdStr = mRePasswdEt.getText().toString().trim();

		Log.e("sss", "phone number is " + msp.getString("name", "")
				+ mInitPwdStr + mRePasswdStr);

		if (TextUtils.isEmpty(mInitPwdStr)) {
			Toast.makeText(this, "旧密码不能为空!", Toast.LENGTH_SHORT).show();
			return;
		} else if (mInitPwdStr.length() != 6) {
			Toast.makeText(this, "密码位数不够!", Toast.LENGTH_SHORT).show();
			return;
		}

		if (TextUtils.isEmpty(mPasswdStr)) {
			Toast.makeText(this, "新密码不能为空!", Toast.LENGTH_SHORT).show();
			return;
		} else if (mPasswdStr.length() != 6) {
			Toast.makeText(this, "密码位数不够!", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!mPasswdStr.equals(mRePasswdStr)) {
			Toast.makeText(this, "输入的密码不匹配!", Toast.LENGTH_SHORT).show();
			return;
		}

		showInfoProgressDialog();

		String urls = Contacts.URL_UPDATE_PASSWORD + "phone="
				+ msp.getString("user", "") + "&password="
				+ MD5.md5(mInitPwdStr) + "&new_password=" + MD5.md5(mPasswdStr)
				+ Contacts.URL_ENDING;

		JsonObjectRequest loginjrs = new JsonObjectRequest(urls, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							String success = response.getString("result");

							Log.e("sss", "the modify login password is ==== "
									+ response.toString());

							if ("SUCCESS".equals(success)) {
								Toast.makeText(getApplicationContext(),
										"密码修改成功!", Toast.LENGTH_SHORT).show();
								msp.edit().putString("key", mPasswdStr)
										.commit();
								Customer.key = mPasswdStr;
								ModifyPasswdActivity.this.finish();
							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"),
										Toast.LENGTH_SHORT).show();
							}
							dismissInfoProgressDialog();
						} catch (JSONException e) {
							e.printStackTrace();
							dismissInfoProgressDialog();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(getApplicationContext(), "网络请求失败，请重试!",Toast.LENGTH_SHORT).show();
						Log.e("sss","the error response message is ==== " + arg0.getMessage());
						dismissInfoProgressDialog();
					}
				});
		mQueue.add(loginjrs);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LoginActivity.hintKbTwo(ModifyPasswdActivity.this);
			this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LoginActivity.hintKbTwo(ModifyPasswdActivity.this);
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