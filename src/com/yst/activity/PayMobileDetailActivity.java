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
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 手机支付界面和手机支付列表界面
 */
public class PayMobileDetailActivity extends Activity implements OnClickListener{

	private RequestQueue mQueue;
	private MProcessDialog mInfoProgressDialog ;
	
	private ImageView query_charge_result_back_iv;
	private ImageView query_charge_back_iv1;
	private Button pay_mobile_pay_btn;
	private TextView query_charge_balance_tv;
	private TextView query_charge_pay_tv;
	private double balance ;
	
	private ImageView activity_back_iv ;
	private TextView activity_title_tv;
	
	private Button query_result_message_pay_btn;
	private Intent payTheFeesIt = null;
	
	private String phone = "";
	private String money = "";
	
	private int index = 1 ;
	
	//这里是查询水电煤的结果显示控件
	private LinearLayout layout1, layout2, layout3, layout4, layout5;
	private View view1, view2, view3, view4, view5 ;
	private TextView pay_the_fees_balance_unit_name_tv;
	private TextView pay_the_fees_balance_account_tv;
	private TextView pay_the_fees_balance_account_name_tv;
	private TextView pay_the_fees_balance_tv;
	private TextView pay_the_fees_contract_no_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(1 == getIntent().getIntExtra("list", 1)) {
			setContentView(R.layout.activity_pay_mobile1);
			initView();
			money = getIntent().getStringExtra("money");
			phone = getIntent().getStringExtra("phone");
			String str = "本次支付:  ¥ " + "<font color='#FE7115'>" + money + "</font>";
			query_charge_pay_tv.setText(Html.fromHtml(str));
		} else if(2 == getIntent().getIntExtra("list", 1)) {
			setContentView(R.layout.activity_my_hfcz);
			initView1();
		} else if(3 == getIntent().getIntExtra("list", 1)) {
			setContentView(R.layout.activity_query_result_message);
			initView2();
		}
		index = getIntent().getIntExtra("list", 1);
	}

	private void initView() {
		mQueue = Volley.newRequestQueue(this);
		query_charge_balance_tv = (TextView) findViewById(R.id.query_charge_balance_tv);
		query_charge_pay_tv = (TextView) findViewById(R.id.query_charge_pay_tv);
		pay_mobile_pay_btn = (Button) findViewById(R.id.pay_mobile_pay_btn);
		pay_mobile_pay_btn.setOnClickListener(this);
		query_charge_back_iv1 = (ImageView) findViewById(R.id.query_charge_back_iv1);
		query_charge_back_iv1.setOnClickListener(this);
	}
	
	private void initView1() {
		mQueue = Volley.newRequestQueue(this);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText("我的充值");
		activity_back_iv.setOnClickListener(this);
	}
	
	private void initView2() {
		mQueue = Volley.newRequestQueue(this);
		query_charge_result_back_iv = (ImageView) findViewById(R.id.query_charge_result_back_iv);
		query_charge_result_back_iv.setOnClickListener(this);
		
		payTheFeesIt = getIntent();
		query_result_message_pay_btn = (Button) findViewById(R.id.query_result_message_pay_btn);
		query_result_message_pay_btn.setOnClickListener(this);
		
		
		layout1 = (LinearLayout) findViewById(R.id.result_linear_layout1);
		layout2 = (LinearLayout) findViewById(R.id.result_linear_layout2);
		layout3 = (LinearLayout) findViewById(R.id.result_linear_layout3);
		layout4 = (LinearLayout) findViewById(R.id.result_linear_layout4);
		layout5 = (LinearLayout) findViewById(R.id.result_linear_layout5);
		
		view1 = (View) findViewById(R.id.result_line_view1);
		view2 = (View) findViewById(R.id.result_line_view2);
		view3 = (View) findViewById(R.id.result_line_view3);
		view4 = (View) findViewById(R.id.result_line_view4);
		view5 = (View) findViewById(R.id.result_line_view5);
		
		pay_the_fees_balance_unit_name_tv = (TextView) findViewById(R.id.pay_the_fees_balance_unit_name_tv);
		pay_the_fees_balance_account_tv = (TextView) findViewById(R.id.pay_the_fees_balance_account_tv);
		pay_the_fees_balance_account_name_tv = (TextView) findViewById(R.id.pay_the_fees_balance_account_name_tv);
		pay_the_fees_balance_tv = (TextView) findViewById(R.id.pay_the_fees_balance_tv);
		pay_the_fees_contract_no_tv = (TextView) findViewById(R.id.pay_the_fees_contract_no_tv);
		
		if(payTheFeesIt != null) {
			if(payTheFeesIt.getStringExtra("userCode") != null && !TextUtils.isEmpty(payTheFeesIt.getStringExtra("userCode"))) {
				layout1.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);
				pay_the_fees_balance_unit_name_tv.setText(payTheFeesIt.getStringExtra("userCode"));
			} else {
				layout1.setVisibility(View.GONE);
				view1.setVisibility(View.GONE);
			}
			if(payTheFeesIt.getStringExtra("account") != null && !TextUtils.isEmpty(payTheFeesIt.getStringExtra("account"))) {
				layout2.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
				pay_the_fees_balance_account_tv.setText(payTheFeesIt.getStringExtra("account"));
			} else {
				layout2.setVisibility(View.GONE);
				view2.setVisibility(View.GONE);
			}
			if(payTheFeesIt.getStringExtra("accountName") != null && !TextUtils.isEmpty(payTheFeesIt.getStringExtra("accountName"))) {
				layout3.setVisibility(View.VISIBLE);
				view3.setVisibility(View.VISIBLE);
				pay_the_fees_balance_account_name_tv.setText(payTheFeesIt.getStringExtra("accountName"));
			} else {
				layout3.setVisibility(View.GONE);
				view3.setVisibility(View.GONE);
			}
			if(payTheFeesIt.getStringExtra("balance") != null && !TextUtils.isEmpty(payTheFeesIt.getStringExtra("balance"))) {
				layout4.setVisibility(View.VISIBLE);
				view4.setVisibility(View.VISIBLE);
				pay_the_fees_balance_tv.setText(payTheFeesIt.getStringExtra("balance"));
			} else {
				layout4.setVisibility(View.GONE);
				view4.setVisibility(View.GONE);
			}
			if(payTheFeesIt.getStringExtra("contractNo") != null && !TextUtils.isEmpty(payTheFeesIt.getStringExtra("contractNo"))) {
				layout5.setVisibility(View.VISIBLE);
				view5.setVisibility(View.VISIBLE);
				pay_the_fees_contract_no_tv.setText(payTheFeesIt.getStringExtra("contractNo"));
			} else {
				layout5.setVisibility(View.GONE);
				view5.setVisibility(View.GONE);
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pay_mobile_pay_btn:
			chargeMobile();
			break;
		case R.id.query_charge_back_iv1:
			finish();
			break;
		case R.id.activity_back_iv:
			finish();
			break;
		case R.id.query_charge_result_back_iv:
			finish();
			break;
		case R.id.query_result_message_pay_btn:
			if(payTheFeesIt == null) {
				return ;
			}
			payTheFeesIt.setClass(this, PayTheFeesActivity.class);
			startActivity(payTheFeesIt);
			finish();
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(index == 1) {
			getBalance();
		}
	}
	
	/**
	 * 获取账户余额
	 */
	private void chargeMobile() {
		// http://182.254.161.94:8080/ydg/ydmvc/after/liftService/createorder.do?accountNum=&phone=&type=0&money=
		if(TextUtils.isEmpty(phone)) {
			return ;
		}
		if(TextUtils.isEmpty(money)) {
			return ;
		}
		
		if(balance < Double.parseDouble(money)) {
			Toast.makeText(getApplicationContext(), "余额不足，请充值", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		showInfoProgressDialog();
		JsonObjectRequest payjrs = new JsonObjectRequest(Contacts.URL_PAY_MOBILE + "accountNum=" + Customer.customer_id + "&phone=" + phone + "&money=" + money + "&type=0", null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							//{"code":0,"msg":"账户金额不足"}
							if(response.has("code")) {
								if("1".equals(response.getString("code"))) {
									if(response.has("content")) {
										if(response.getJSONObject("content").has("state")) {
											if(response.getJSONObject("content").getString("state").equals("0")) {
												if(response.getJSONObject("content").getJSONObject("data").getString("State").equals("9")) {
													if(response.getJSONObject("content").getJSONObject("data").has("Err_msg")) {
														Toast.makeText(getApplicationContext(), "" + response.getJSONObject("content").getJSONObject("data").getString("Err_msg"), Toast.LENGTH_SHORT).show();
													} else {
														Toast.makeText(getApplicationContext(), "充值失败", Toast.LENGTH_SHORT).show();
													}
												} else {
													Toast.makeText(getApplicationContext(), "充值成功", Toast.LENGTH_SHORT).show();
													PayMobileDetailActivity.this.finish();
												}
											}
										}
									}
								} else {
									if(response.has("msg")) {
										Toast.makeText(getApplicationContext(), "" + response.getString("msg"), Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(getApplicationContext(), "提交信息异常", Toast.LENGTH_SHORT).show();
									}
								}
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
						Toast.makeText(getApplicationContext(), "网络请求失败，请重试!", 	Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(payjrs);
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
										String str = "账户余额：  " + "<font color='#FE7115'>" + String.format("%.2f", (response.getJSONObject("data").getDouble("fee") / 100)) + " 元"  + "</font>";
										balance = response.getJSONObject("data").getDouble("fee") / 100;
										query_charge_balance_tv.setText(Html.fromHtml(str));
									}
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
								String str = "账户余额：  " + "<font color='#FE7115'>" + " 0元"  + "</font>";
								query_charge_balance_tv.setText(Html.fromHtml(str));
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
						String str = "账户余额：  " + "<font color='#FE7115'>" + " 0元"  + "</font>";
						query_charge_balance_tv.setText(Html.fromHtml(str));
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