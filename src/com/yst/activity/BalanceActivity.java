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
import com.jack.ui.MProcessDialog;
import com.yst.yiku.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 我的余额
 */
public class BalanceActivity extends Activity implements OnClickListener{

	private RequestQueue mQueue;
	private SharedPreferences msp;
	private TextView mShowCashTv, mDetailTv, mPayTv;
	private MProcessDialog mInfoProgressDialog;
	
	//界面返回和提示标题
	private TextView activity_title_tv ;
	private ImageView activity_back_iv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balance);

		initView();
	}

	private void initView() {
		mQueue = Volley.newRequestQueue(this);
		msp = getSharedPreferences("yiku", MODE_PRIVATE);
		
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_balance_label);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		
		mShowCashTv = (TextView)findViewById(R.id.balance_show_cash_tv);
		mDetailTv = (TextView)findViewById(R.id.balance_detail_tv);
		mPayTv = (TextView)findViewById(R.id.balance_recharge_online_tv);
		mDetailTv.setOnClickListener(this);
		mPayTv.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_back_iv:
			this.finish();
			break;
		case R.id.balance_detail_tv:
			startActivity(new Intent(BalanceActivity.this,BalanceDetailActivity.class).putExtra("FLAG", 0));
			break;
		case R.id.balance_recharge_online_tv:
			startActivity(new Intent(BalanceActivity.this,BalanceDetailActivity.class).putExtra("FLAG", 1));
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getBalance();
	}

	private void getBalance() {
		showInfoProgressDialog();
		JsonObjectRequest payjrs = new JsonObjectRequest(Contacts.URL_GET_CUSTOMER_INFO + "customer_id=" + Customer.customer_id + Contacts.URL_ENDING, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							String success = response.getString("result");
							if ("SUCCESS".equals(success)) {
								if(response.has("data")){
									if(response.getJSONObject("data").has("fee")){
										String str = "账户余额：" + "<font color='#F28F00'>" + String.format("%.2f", (response.getJSONObject("data").getDouble("fee") / 100)) + " 元"  + "</font>";
										mShowCashTv.setText(Html.fromHtml(str));
									}
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
								String str = "账户余额：" + "<font color='#F28F00'>" + " 0元"  + "</font>";
								mShowCashTv.setText(Html.fromHtml(str));
							}
							dismissInfoProgressDialog();
						} catch (JSONException e) {
							e.printStackTrace();
							dismissInfoProgressDialog();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError arg0) {
						dismissInfoProgressDialog();
						Toast.makeText(getApplicationContext(), "网络请求失败，请重试!",
								Toast.LENGTH_SHORT).show();
						String str = "账户余额：" + "<font color='#F28F00'>" + " 0元"  + "</font>";
						mShowCashTv.setText(Html.fromHtml(str));
					}
				});
		mQueue.add(payjrs);
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