package com.yst.activity;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.BaseConstants;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.jack.contacts.AppContext;
import com.jack.json.JsonTN;
import com.jack.ui.MProcessDialog;
import com.unionpay.UPPayAssistEx;
import com.yst.yiku.R;

public class ShowOrderPayActivity extends Activity implements OnClickListener {

	private ImageView show_order_pay_back_iv,// 回退
			pay_bank_check,// 选择银行卡
			pay_yu_e_check;// 选择余额宝
	private ImageView pay_zhifubao_check ;// 选择支付宝
			;
	private RelativeLayout pay_bank_relativelayout,// 银行卡
			pay_yu_e_relativelayout;// 余额宝
	private RelativeLayout pay_zhifubao_relativelayout;// 支付宝
			;
	private Button pay_btn;// 支付
	
	private TextView order_price_tv,//订单价格
				 	 order_name_tv,//订单名称
				 	 customer_yue//用户余额
					 ;
	
	private TextView order_price_tv1;
	
	/**
	 * 选择的支付方式
	 */
	private int PAY_MODE = 1;// 1, 银行卡 2, 余额宝 3, 支付宝
	private TextView pay_time_minutes;// 剩余支付时间
	private TextView pay_time_seconds;// 剩余支付时间
	private boolean PAY_STATE = false;// 支付是否超时
	
	public RequestQueue requestQueue = null;
	private StringRequest stringRequest = null;
	private List<Map<String, Object>> list_getTN;// 商品分类
	
	private String orderId = "";
	private String fee = "";
	private String storeId = "";
	
	private double mybalance = 0 ;
	private int index = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_pay_order);
		
		//TODO这里需要上个界面来传进来数据
		
		timer.schedule(task, 1000, 1000); // timeTask
		
		initView();
		
		orderId = getIntent().getStringExtra("send_out_trade_no");
		fee = getIntent().getStringExtra("send_total_fee");
		storeId = getIntent().getStringExtra("storeId");
		index = getIntent().getIntExtra("index",-1);
		
		order_name_tv.setText(orderId);
		order_price_tv.setText(String.format("%.2f",Double.parseDouble(fee)) + "元");
		order_price_tv1.setText(String.format("%.2f",Double.parseDouble(fee)) + "元");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getBalance();
	}

	private void initView() {
		requestQueue = Volley.newRequestQueue(this);
		show_order_pay_back_iv = (ImageView) findViewById(R.id.show_order_pay_back_iv);
		pay_bank_check = (ImageView) findViewById(R.id.pay_bank_check);
		pay_yu_e_check = (ImageView) findViewById(R.id.pay_yu_e_check);
		pay_zhifubao_check = (ImageView) findViewById(R.id.pay_zhifubao_check);
		pay_bank_relativelayout = (RelativeLayout) findViewById(R.id.pay_bank_relativelayout);
		pay_yu_e_relativelayout = (RelativeLayout) findViewById(R.id.pay_yu_e_relativelayout);
		pay_zhifubao_relativelayout = (RelativeLayout) findViewById(R.id.pay_zhifubao_relativelayout);
		pay_time_minutes = (TextView) findViewById(R.id.pay_time_minutes);
		pay_time_seconds = (TextView) findViewById(R.id.pay_time_seconds);
		pay_btn = (Button) findViewById(R.id.pay_btn);
		order_price_tv = (TextView) findViewById(R.id.order_price_tv);
		order_name_tv = (TextView) findViewById(R.id.order_name_tv);
		order_price_tv1 = (TextView) findViewById(R.id.order_price_tv1);
		customer_yue = (TextView) findViewById(R.id.customer_yue);
		
		pay_btn.setOnClickListener(this);
		pay_bank_relativelayout.setOnClickListener(this);
		pay_yu_e_relativelayout.setOnClickListener(this);
		pay_zhifubao_relativelayout.setOnClickListener(this);
		show_order_pay_back_iv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.show_order_pay_back_iv:// 回退
			finish();
			break;
		case R.id.pay_btn:// 支付
			if (PAY_STATE) {
				Toast.makeText(this, "支付超时", Toast.LENGTH_SHORT).show();
			} else {
				switch(PAY_MODE){
				case 1:
					getTN();
					break;
				case 2:
					payByremain();
					break;
				case 3:
//					Toast.makeText(this, "支付宝正在积极开发中", Toast.LENGTH_SHORT).show();
					GotoZhiFuBao();
					break;
				}
			}
			break;
		case R.id.pay_bank_relativelayout:// 银行卡支付
			order_price_tv1.setText(String.format("%.2f",Double.parseDouble(fee)) + "元");
			pay_bank_check.setImageResource(R.drawable.check_enable);
			pay_yu_e_check.setImageResource(R.drawable.check_red_no);
			pay_zhifubao_check.setImageResource(R.drawable.check_red_no);
			PAY_MODE = 1;
			break;
		case R.id.pay_yu_e_relativelayout:// 余额宝
			order_price_tv1.setText(String.format("%.2f",Double.parseDouble(fee)) + "元");
			pay_bank_check.setImageResource(R.drawable.check_red_no);
			pay_yu_e_check.setImageResource(R.drawable.check_enable);
			pay_zhifubao_check.setImageResource(R.drawable.check_red_no);
			PAY_MODE = 2;
			break;
		case R.id.pay_zhifubao_relativelayout:// 支付宝
			order_price_tv1.setText(String.format("%.2f",Double.parseDouble(fee)) + "元");
			pay_bank_check.setImageResource(R.drawable.check_red_no);
			pay_yu_e_check.setImageResource(R.drawable.check_red_no);
			pay_zhifubao_check.setImageResource(R.drawable.check_enable);
			PAY_MODE = 3;
			break;
		}
	}

	Timer timer = new Timer();

	public static String[] getDiff(long time) {
		String[] diffStr = new String[] { "0", "0", "0", "0" };

		try {
			// 毫秒ms
			long diff = time;

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			// long diffHours = diff / (60 * 60 * 1000) % 24;
			// long diffDays = diff / (24 * 60 * 60 * 1000);

			diffStr = new String[] { diffMinutes + "", diffSeconds + "" };
		} catch (Exception e) {
			e.printStackTrace();
		}

		return diffStr;
	}

	long time = 900000;
	TimerTask task = new TimerTask() {
		@Override
		public void run() {
			runOnUiThread(new Runnable() { // UI thread
				@Override
				public void run() {
					String[] diff = getDiff(time);
					time -= 1000;
					pay_time_minutes.setText(diff[0] + ":");
					String seconds = diff[1];
					if (seconds.length() == 1) {
						pay_time_seconds.setText("0" + seconds);
					} else {
						pay_time_seconds.setText(seconds);
					}
					if (diff[0].equals("0") && diff[1].equals("0")) {
						timer.cancel();
						PAY_STATE = true;
					}
				}
			});
		}
	};
	
	/**
	 * 余额支付
	 */
	private void payByremain(){
//		Integer.parseInt(String.valueOf(Double.parseDouble(fee) * 100))
//		String urls = Contacts.URL_AJAX_PAY_BY_REMAIN_ACTION + "customerId=" + Customer.customer_id + "&fee=" + (int)(Double.parseDouble(fee) * 100) + "&orderId=" + orderId + "&storeId=" + storeId + "&client_type=A&version=1";
		
//		余额不足的时候，提示消费者“余额不足，是否充值”
		if(mybalance < Double.parseDouble(fee)){
			showRechargeDialog();
			return;
		}
		
		String urlString = Contacts.URL_SEND_PAY_ORDER + "total_price=" + (int)(Double.parseDouble(fee) * 100) 
				+ "&customer_id=" + Customer.customer_id + "&pay_act=1&pay_type=0&order_id=" + orderId + "&store_id=" + storeId
				+ Contacts.URL_ENDING;
		
		Log.e("sss","urls is === " + urlString);
		
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest(urlString, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					if("SUCCESS".equals(response.getString("result"))){
						Toast.makeText(getApplicationContext(), "支付完成", Toast.LENGTH_SHORT).show();
						startActivity(new Intent(ShowOrderPayActivity.this, CustomerOrdersActivity.class));
						ShowOrderPayActivity.this.finish();
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(ShowOrderPayActivity.this, "数据加载异常...", Toast.LENGTH_SHORT).show();
				}finally{
					dismissInfoProgressDialog();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(ShowOrderPayActivity.this, "数据加载异常...", Toast.LENGTH_SHORT).show();
				
				Log.e("sss","sssssssss ==== " + new String(error.networkResponse.data));
				
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 银联支付需要的tn号
	 */
	private void getTN() {
		//TODO 需要真实的金额
		String urlString = Contacts.URL_SEND_PAY_ORDER + "total_price=" + (int)(Double.parseDouble(fee) * 100) 
				+ "&customer_id=" + Customer.customer_id + "&pay_act=1&pay_type=2&order_id=" + orderId
				+ Contacts.URL_ENDING;
		Log.e("TAG", "==" + urlString);
		showInfoProgressDialog();
		stringRequest = new StringRequest(urlString, new Listener<String>() {
			@Override
			public void onResponse(String arg0) {
				if (!arg0.equalsIgnoreCase("[]") && arg0 != null) {
					if (arg0.contains("FAILED")) {
						list_getTN = JsonTN.JsonToListFAILED(arg0);
						Toast.makeText(ShowOrderPayActivity.this,
								list_getTN.get(0).get("error_info").toString(),
								Toast.LENGTH_SHORT).show();
					} else {
						list_getTN = JsonTN.JsonToList(arg0);
						String TN = list_getTN.get(0).get("data").toString();
						Log.e("--------------TN----------->>>", "====" + TN);
						int ret = UPPayAssistEx.startPay(ShowOrderPayActivity.this, null, null, TN,	BaseConstants.UNION_CODE);
						if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
							UPPayAssistEx.installUPPayPlugin(ShowOrderPayActivity.this);
						}
					}
				}
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				dismissInfoProgressDialog();
				Toast.makeText(ShowOrderPayActivity.this, "数据加载异常...", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(stringRequest);
	}
	
	private void GotoZhiFuBao() {
		
		//TODO 需要真实的金额
				String urlString = Contacts.URL_SEND_PAY_ORDER + "total_price=" + (int)(Double.parseDouble(fee) * 100) 
						+ "&customer_id=" + Customer.customer_id + "&pay_act=1&pay_type=1&order_id=" + orderId
						+ Contacts.URL_ENDING;
				Log.e("TAG", "==" + urlString);
				showInfoProgressDialog();
				stringRequest = new StringRequest(urlString, new Listener<String>() {
					@Override
					public void onResponse(String arg0) {
						if (!arg0.equalsIgnoreCase("[]") && arg0 != null) {
							if (arg0.contains("FAILED")) {
								list_getTN = JsonTN.JsonToListFAILED(arg0);
								Toast.makeText(ShowOrderPayActivity.this,
										list_getTN.get(0).get("error_info").toString(),
										Toast.LENGTH_SHORT).show();
							} else {
								list_getTN = JsonTN.JsonToList(arg0);
								String TN = list_getTN.get(0).get("data").toString();
								Intent intent = new Intent(ShowOrderPayActivity.this, Zhifu_WebviewActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString(Zhifu_WebviewActivity.PAY_NO, TN);
								bundle.putString(Zhifu_WebviewActivity.PAY_TYPE,"2");
								AppContext.zhifujine = Double.parseDouble(fee);
								intent.putExtras(bundle);
								startActivity(intent);
								finish();
							}
						}
						dismissInfoProgressDialog();
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError arg0) {
						dismissInfoProgressDialog();
						Toast.makeText(ShowOrderPayActivity.this, "数据加载异常...", Toast.LENGTH_SHORT).show();
					}
				});
				requestQueue.add(stringRequest);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) {
			Toast.makeText(ShowOrderPayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(ShowOrderPayActivity.this, CustomerOrdersActivity.class));
			this.finish();
		} else if (str.equalsIgnoreCase("fail")) {
			Toast.makeText(ShowOrderPayActivity.this, "支付失败",
					Toast.LENGTH_SHORT).show();
		} else if (str.equalsIgnoreCase("cancel")) {
			Toast.makeText(ShowOrderPayActivity.this, "用户取消了支付",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 获取用户余额
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
										String str = "当前余额" + "<font color='#F28F00'>" + String.format("%.2f", (response.getJSONObject("data").getDouble("fee") / 100)) + " 元"  + "</font>";
										customer_yue.setText(Html.fromHtml(str));
										mybalance = response.getJSONObject("data").getDouble("fee") / 100 ;
									}
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
								String str = "当前余额" + "<font color='#F28F00'>" + " 0元"  + "</font>";
								customer_yue.setText(Html.fromHtml(str));
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
						String str = "当前余额" + "<font color='#F28F00'>" + " 0元"  + "</font>";
						customer_yue.setText(Html.fromHtml(str));
					}
				});
		requestQueue.add(payjrs);
	}
	
	/**
	 * 余额不足的时候，提示消费者“余额不足，是否充值”
	 */
	private void showRechargeDialog(){
		final AlertDialog builder = new Builder(this).create();
		// 设置更新标题
		builder.setTitle("温馨提示");
		// 设置更新提示的标语
		builder.setMessage("余额不足，是否充值？");
		// 设置外部点击事件不能消失
//		builder.setCanceledOnTouchOutside(false);
		// 设置取消按钮及点击事件
		builder.setButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				builder.dismiss();
			}
		});
		// 设置更新按钮及点击事件
		builder.setButton2("充点钱", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(ShowOrderPayActivity.this,BalanceDetailActivity.class).putExtra("FLAG", 1));
				builder.dismiss();
			}
		});
		// 对话框显示
		builder.show();
	}
	
	private MProcessDialog mInfoProgressDialog;
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
