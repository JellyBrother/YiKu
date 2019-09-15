package com.yst.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
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

public class PayTheFeesActivity extends Activity implements OnClickListener{
	
	private ImageView pay_the_fees_back_iv;
	private TextView pay_the_fees_title_tv;
	private TextView pay_the_fees_balance_unit_name_tv;
	private TextView pay_the_fees_balance_account_tv;
	private TextView pay_the_fees_balance_account_name_tv;
	private EditText pay_the_fees_balance_ev;
	private Button pay_the_fees_btn;
	private TextView pay_the_fees_balance_tv;
	
	private RequestQueue requestQueue ;
	private double balance ;
	private MProcessDialog mInfoProgressDialog ;
	
	private Intent it = null ;
	
	private String provCode = "";
	private String cityCode = "";
	private String type = "";
	private String cardId = "";
	private String chargeCompanyCode = "";
	private String Account = "";
	private String contractNo = "";
	private String payTheFeesUnit = "";
	private String accountName = "";
	private String account = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_the_fees);
		it = getIntent();
		
		Log.e("sss", "provcode === " + it.getStringExtra("provCode"));
		Log.e("sss", "cityCode === " + it.getStringExtra("cityCode"));
		Log.e("sss", "type === " + it.getStringExtra("type"));
		Log.e("sss", "cardId === " + it.getStringExtra("cardId"));
		Log.e("sss", "chargeCompanyCode === " + it.getStringExtra("chargeCompanyCode"));
		Log.e("sss", "Account === " + it.getStringExtra("Account"));
		Log.e("sss", "contractNo === " + it.getStringExtra("contractNo"));
		Log.e("sss", "payTheFeesUnit === " + it.getStringExtra("payTheFeesUnit"));
		Log.e("sss", "accountName === " + it.getStringExtra("accountName"));
		Log.e("sss", "account === " + it.getStringExtra("account"));
		
		initView();
		
		initData();
	}

	/**
	 * 初始化
	 */
	private void initView() {
		requestQueue = Volley.newRequestQueue(this);
		pay_the_fees_back_iv = (ImageView) findViewById(R.id.pay_the_fees_back_iv);
		pay_the_fees_back_iv.setOnClickListener(this);
		pay_the_fees_title_tv = (TextView) findViewById(R.id.pay_the_fees_title_tv);
		
		pay_the_fees_title_tv = (TextView) findViewById(R.id.pay_the_fees_title_tv);
		pay_the_fees_balance_unit_name_tv = (TextView) findViewById(R.id.pay_the_fees_balance_unit_name_tv);
		pay_the_fees_balance_account_tv = (TextView) findViewById(R.id.pay_the_fees_balance_account_tv);
		pay_the_fees_balance_account_name_tv = (TextView) findViewById(R.id.pay_the_fees_balance_account_name_tv);
		pay_the_fees_balance_ev = (EditText) findViewById(R.id.pay_the_fees_balance_ev);
		pay_the_fees_btn = (Button) findViewById(R.id.pay_the_fees_btn);
		pay_the_fees_btn.setOnClickListener(this);
		pay_the_fees_balance_tv = (TextView) findViewById(R.id.pay_the_fees_balance_tv);
	}
	
	/**
	 * 初始化展示信息
	 */
	private void initData() {
		if(null != it) {
			
			provCode = it.getStringExtra("provCode");
			cityCode = it.getStringExtra("cityCode");
			type = it.getStringExtra("type");
			cardId = it.getStringExtra("cardId");
			chargeCompanyCode = it.getStringExtra("chargeCompanyCode");
			Account = it.getStringExtra("Account");
			contractNo = it.getStringExtra("contractNo");
			payTheFeesUnit = it.getStringExtra("payTheFeesUnit");
			accountName = it.getStringExtra("accountName");
			account = it.getStringExtra("account");
			
			if(it.getStringExtra("type").equals(QueryChargeActivity.QUERY_WATER_TYPE)) {
				pay_the_fees_title_tv.setText("水费充值");
			} else if(it.getStringExtra("type").equals(QueryChargeActivity.QUERY_ELECTRIC_TYPE)) {
				pay_the_fees_title_tv.setText("电费充值");
			} else if(it.getStringExtra("type").equals(QueryChargeActivity.QUERY_GAS_TYPE)) {
				pay_the_fees_title_tv.setText("燃气费充值");
			}
			
			pay_the_fees_balance_unit_name_tv.setText(payTheFeesUnit);
			pay_the_fees_balance_account_tv.setText(account);
			pay_the_fees_balance_account_name_tv.setText(accountName);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getBalance();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.pay_the_fees_back_iv:
			finish();
			break;
		case R.id.pay_the_fees_btn:
			//TODO 去缴费
			GetPaySDM();
			break;
		}
	}
	
	/**
	 * 缴费
	 */
	private void GetPaySDM() {
		final String balanceStr = pay_the_fees_balance_ev.getText().toString().trim().replace(" ", "");
		if(TextUtils.isEmpty(balanceStr)) {
			Toast.makeText(this, "请输入充值金额", Toast.LENGTH_SHORT).show();
			return ;
		}
		if(balance < Double.parseDouble(balanceStr)) {
			Toast.makeText(this, "余额不足", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		if(TextUtils.isEmpty(provCode) || TextUtils.isEmpty(cityCode) || TextUtils.isEmpty(type) || TextUtils.isEmpty(contractNo) ||
				TextUtils.isEmpty(cardId) || TextUtils.isEmpty(chargeCompanyCode) || TextUtils.isEmpty(Account)) {
			return ;
		}
		
		showInfoProgressDialog();
		
		String urls = Contacts.URL_PAY_THE_FEES + "provCode=" + provCode + "&cityCode=" + cityCode + "&type=" + type + "&cardId=" + cardId
				+ "&chargeCompanyCode=" + chargeCompanyCode + "&Account=" + Account + "&orderMoney=" + balanceStr + "&contractNo=" + contractNo;
		
		JsonObjectRequest jsr = new JsonObjectRequest(urls, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				Log.e("sss","response to string is === " + response.toString());
				try {
					if(response.getString("code").equals("0")) {
						Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getApplicationContext(), response.getString("msg"), Toast.LENGTH_SHORT).show();
						PayTheFeesActivity.this.finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}finally {
					dismissInfoProgressDialog();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "网络请求失败，请重试!",
						Toast.LENGTH_SHORT).show();
				dismissInfoProgressDialog();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 获取账户余额
	 */
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
										balance = response.getJSONObject("data").getDouble("fee") / 100;
										pay_the_fees_balance_tv.setText(String.format("%.2f", (response.getJSONObject("data").getDouble("fee") / 100)));
									}
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
								pay_the_fees_balance_tv.setText("0");
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
						pay_the_fees_balance_tv.setText("0");
					}
				});
		requestQueue.add(payjrs);
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
