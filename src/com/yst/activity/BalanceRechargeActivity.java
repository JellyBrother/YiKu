package com.yst.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
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
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.BaseConstants;
import com.guohua.common.util.Customer;
import com.jack.contacts.AppContext;
import com.jack.contacts.Contacts;
import com.jack.json.JsonTN;
import com.jack.ui.MProcessDialog;
import com.unionpay.UPPayAssistEx;
import com.yst.yiku.R;

/**
 * 支付订单
 */
public class BalanceRechargeActivity extends Activity implements
		OnClickListener {

	// 界面返回和提示标题
	private TextView activity_title_tv;
	private ImageView activity_back_iv;

	private MProcessDialog mInfoProgressDialog;

	private ImageView activity_balance_recharge_main_yhk_click_iv;
	private ImageView service_pkg_pay_zhifubao;
	
	private TextView activity_balance_recharge_main_price_tv;
	private TextView activity_balance_recharge_main_re_price_tv;
	private RelativeLayout activity_balance_recharge_main_yhk_layout;
	private RelativeLayout package_layout1;
	private Button activity_balance_recharge_main_commit_btn;

	private int index = 0;

	// 充值的金额，订单号，订单类型，订单标题
	private String price = "", send_out_trade_no = "", send_payment_type = "",
			send_subject = "";

	public static RequestQueue requestQueue = null;
	private StringRequest stringRequest = null;
	private List<Map<String, Object>> list_getTN;// 商品分类

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_balance_recharge_main);

		requestQueue = Volley.newRequestQueue(this);

		price = getIntent().getStringExtra("price");
		send_out_trade_no = getIntent().getStringExtra("send_out_trade_no");
//		send_payment_type = getIntent().getStringExtra("send_payment_type");
//		send_subject = getIntent().getStringExtra("send_subject");

		Log.e("sss", "price = " + price);
		Log.e("sss", "send_out_trade_no = " + send_out_trade_no);
		Log.e("sss", "send_payment_type = " + send_payment_type);
		Log.e("sss", "send_subject = " + send_subject);

		initView();
	}

	private void initView() {
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_pay_order_label);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);

		activity_balance_recharge_main_yhk_click_iv = (ImageView) findViewById(R.id.activity_balance_recharge_main_yhk_click_iv);
		service_pkg_pay_zhifubao = (ImageView) findViewById(R.id.service_pkg_pay_zhifubao);
		activity_balance_recharge_main_yhk_click_iv.setOnClickListener(this);
		service_pkg_pay_zhifubao.setOnClickListener(this);

		activity_balance_recharge_main_price_tv = (TextView) findViewById(R.id.activity_balance_recharge_main_price_tv);
		activity_balance_recharge_main_re_price_tv = (TextView) findViewById(R.id.activity_balance_recharge_main_re_price_tv);

		// #AC291F
		String str = "订单金额：" + "<font color='#AC291F'>"
				+ getIntent().getStringExtra("price") + " 元" + "</font>";
		activity_balance_recharge_main_price_tv.setText("订单金额："
				+ getIntent().getStringExtra("price") + " 元");
		activity_balance_recharge_main_re_price_tv.setText(Html.fromHtml(str));

		activity_balance_recharge_main_yhk_layout = (RelativeLayout) findViewById(R.id.activity_balance_recharge_main_yhk_layout);
		package_layout1 = (RelativeLayout) findViewById(R.id.package_layout1);
		activity_balance_recharge_main_yhk_layout.setOnClickListener(this);
		package_layout1.setOnClickListener(this);
		index = 0;
		activity_balance_recharge_main_yhk_click_iv
				.setImageResource(R.drawable.check_enable);
		activity_balance_recharge_main_commit_btn = (Button) findViewById(R.id.activity_balance_recharge_main_commit_btn);
		activity_balance_recharge_main_commit_btn.setOnClickListener(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_back_iv:
			this.finish();
			break;
		case R.id.activity_balance_recharge_main_yhk_click_iv:
		case R.id.activity_balance_recharge_main_yhk_layout:
			index = 0;
			service_pkg_pay_zhifubao.setImageResource(R.drawable.check_no);
			activity_balance_recharge_main_yhk_click_iv
					.setImageResource(R.drawable.check_enable);
			break;
		case R.id.package_layout1:
		case R.id.service_pkg_pay_zhifubao:
			index = 1;
			service_pkg_pay_zhifubao.setImageResource(R.drawable.check_enable);
			activity_balance_recharge_main_yhk_click_iv
					.setImageResource(R.drawable.check_no);
			break;
		case R.id.activity_balance_recharge_main_commit_btn:
			if (index == 0) {
				getTN();
			} else if (index == 1) {
//				Toast.makeText(BalanceRechargeActivity.this, "支付宝正在申请中",
//						Toast.LENGTH_SHORT).show();
				GotoZhifubao();
			} else {
				Toast.makeText(getApplicationContext(), "请选择充值方式", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	private void getTN() {
		String urlString = Contacts.URL_GET_TN + "customer_id="
				+ Customer.customer_id + "&order_id=" + send_out_trade_no
				+ "&pay_act=" + 0 + "&pay_type=" + 2 + "&total_price="
				+ (int) (Double.parseDouble(price) * 100) + "&store_id"
				+ "&client_type=A&version=1";
		Log.e("TAG", "==" + urlString);
		showInfoProgressDialog();
		stringRequest = new StringRequest(urlString, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				if (!arg0.equalsIgnoreCase("[]") && arg0 != null) {
					if (arg0.contains("FAILED")) {
						list_getTN = JsonTN.JsonToListFAILED(arg0);
						Toast.makeText(BalanceRechargeActivity.this,
								list_getTN.get(0).get("error_info").toString(),
								Toast.LENGTH_SHORT).show();
					} else {
						list_getTN = JsonTN.JsonToList(arg0);
						String TN = list_getTN.get(0).get("data").toString();
						Log.e("--------------TN----------->>>", "====" + TN);
						int ret = UPPayAssistEx.startPay(
								BalanceRechargeActivity.this, null, null, TN,
								BaseConstants.UNION_CODE);
						if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
							UPPayAssistEx
									.installUPPayPlugin(BalanceRechargeActivity.this);
						}

					}
				}
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				dismissInfoProgressDialog();
				Toast.makeText(BalanceRechargeActivity.this, "数据加载异常...",
						Toast.LENGTH_SHORT).show();
			}
		});
		// stringRequest.setTag(getActivity());
		requestQueue.add(stringRequest);
	}
	
	private void GotoZhifubao() {
		String urlString = Contacts.URL_GET_TN + "customer_id="
				+ Customer.customer_id + "&order_id=" + send_out_trade_no
				+ "&pay_act=" + 0 + "&pay_type=" + 1 + "&total_price="
				+ (int) (Double.parseDouble(price) * 100) + "&store_id"
				+ "&client_type=A&version=1";
		Log.e("TAG", "==" + urlString);
		showInfoProgressDialog();
		stringRequest = new StringRequest(urlString, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				if (!arg0.equalsIgnoreCase("[]") && arg0 != null) {
					if (arg0.contains("FAILED")) {
						list_getTN = JsonTN.JsonToListFAILED(arg0);
						Toast.makeText(BalanceRechargeActivity.this,
								list_getTN.get(0).get("error_info").toString(),
								Toast.LENGTH_SHORT).show();
					} else {
						list_getTN = JsonTN.JsonToList(arg0);
						String TN = list_getTN.get(0).get("data").toString();
						Log.e("--------------TN----------->>>", "====" + TN);
						Intent intent = new Intent(BalanceRechargeActivity.this, Zhifu_WebviewActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString(Zhifu_WebviewActivity.PAY_NO, TN);
						bundle.putString(Zhifu_WebviewActivity.PAY_TYPE,"1");
						AppContext.zhifujine = Double.parseDouble(price);
						intent.putExtras(bundle);
						BalanceRechargeActivity.this.startActivity(intent);
						BalanceRechargeActivity.this.finish();
					}
				}
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				dismissInfoProgressDialog();
				Toast.makeText(BalanceRechargeActivity.this, "数据加载异常...",
						Toast.LENGTH_SHORT).show();
			}
		});
		// stringRequest.setTag(getActivity());
		requestQueue.add(stringRequest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) {
			Toast.makeText(BalanceRechargeActivity.this, "支付成功",
					Toast.LENGTH_SHORT).show();
			this.finish();
		} else if (str.equalsIgnoreCase("fail")) {
			Toast.makeText(BalanceRechargeActivity.this, "支付失败",
					Toast.LENGTH_SHORT).show();
		} else if (str.equalsIgnoreCase("cancel")) {
			Toast.makeText(BalanceRechargeActivity.this, "用户取消了支付",
					Toast.LENGTH_SHORT).show();
		}
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