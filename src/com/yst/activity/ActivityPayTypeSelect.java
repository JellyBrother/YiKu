package com.yst.activity;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yst.yiku.R;

public class ActivityPayTypeSelect extends Activity implements OnClickListener {

	private TextView mPayName, mPayPrice;
	private RelativeLayout layout1, layout2, layout3;
	private ImageView zhifubaoIv, weixinIv, yinhangkaIv, mFinishIv;
	private Button mCommitBtn;
	private int flag = 1;
	NetworkImageView niv;
	ImageLoader imageLoader;
	RequestQueue mQueue;
	private String package_name;
	private String package_price;
	private String send_out_trade_no;

	public static RequestQueue requestQueue = null;
	private StringRequest stringRequest = null;
	private List<Map<String, Object>> list_pay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_pay);
		requestQueue = Volley.newRequestQueue(this);
		send_out_trade_no = this.getIntent()
				.getStringExtra("send_out_trade_no");
		package_name = this.getIntent().getStringExtra("package_name");
		package_price = this.getIntent().getStringExtra("package_price");

		initView();

		imageLoader = new ImageLoader(mQueue, new BitmapCache());

		/*
		 * niv.setDefaultImageResId(R.drawable.abc_ab_share_pack_holo_dark);
		 * niv.setErrorImageResId(R.drawable.abc_ab_share_pack_holo_dark);
		 * niv.setImageUrl("http://www.baidu.com", imageLoader);
		 */
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}
		String str = data.getExtras().getString("pay_result");
		if (str.equalsIgnoreCase("success")) {
			Toast.makeText(ActivityPayTypeSelect.this, "支付成功",
					Toast.LENGTH_SHORT).show();
		} else if (str.equalsIgnoreCase("fail")) {
			Toast.makeText(ActivityPayTypeSelect.this, "支付失败",
					Toast.LENGTH_SHORT).show();
		} else if (str.equalsIgnoreCase("cancel")) {
			Toast.makeText(ActivityPayTypeSelect.this, "用户取消了支付",
					Toast.LENGTH_SHORT).show();
		}
	}

	private void initView() {
		zhifubaoIv = (ImageView) findViewById(R.id.service_pkg_pay_zhifubao);
		weixinIv = (ImageView) findViewById(R.id.service_pkg_pay_weixin);
		yinhangkaIv = (ImageView) findViewById(R.id.service_pkg_pay_yinhangka);
		mFinishIv = (ImageView) findViewById(R.id.service_pkg_pay_title_back_iv);
		mCommitBtn = (Button) findViewById(R.id.service_pkg_pay_commit_btn);
		layout1 = (RelativeLayout) findViewById(R.id.package_layout1);
		layout2 = (RelativeLayout) findViewById(R.id.package_layout2);
		layout3 = (RelativeLayout) findViewById(R.id.package_layout3);
		mPayName = (TextView) findViewById(R.id.service_pkg_pay_name_tv);
		mPayName.setText(package_name + "大夫-在线咨询");
		mPayPrice = (TextView) findViewById(R.id.service_pkg_pay_price_tv);
		mPayPrice.setText(package_price + "元");

		layout1.setOnClickListener(this);
		layout2.setOnClickListener(this);
		layout3.setOnClickListener(this);
		mCommitBtn.setOnClickListener(this);
		mFinishIv.setOnClickListener(this);
		zhifubaoIv.setOnClickListener(this);
		weixinIv.setOnClickListener(this);
		yinhangkaIv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.service_pkg_pay_commit_btn:
			if (flag == 1) {
				Intent intent = new Intent(ActivityPayTypeSelect.this,
						Zhifu_WebviewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(Zhifu_WebviewActivity.PAY_NO,
						send_out_trade_no);
				bundle.putString(Zhifu_WebviewActivity.PAY_TYPE, "3");
				intent.putExtras(bundle);
				ActivityPayTypeSelect.this.startActivity(intent);
				finish();

			} else if (flag == 2) {
				showDialog();
			} else if (flag == 3) {
				// String TN = data.tongquantixianfanhui;
				// Log.e("--------------TN----------->>>", "====" + TN);
				// int ret = UPPayAssistEx.startPay(ActivityPayTypeSelect.this,
				// null, null, TN, BaseConstants.UNION_CODE);
				// if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
				// UPPayAssistEx
				// .installUPPayPlugin(ActivityPayTypeSelect.this);
				// }
			}
			break;
		case R.id.package_layout1:
		case R.id.service_pkg_pay_zhifubao:
			flag = 1;
			zhifubaoIv.setImageResource(R.drawable.check_enable);
			yinhangkaIv.setImageResource(R.drawable.check_no);
			weixinIv.setImageResource(R.drawable.check_no);
			break;
		case R.id.package_layout2:
		case R.id.service_pkg_pay_weixin:
			flag = 2;
			zhifubaoIv.setImageResource(R.drawable.check_no);
			yinhangkaIv.setImageResource(R.drawable.check_no);
			weixinIv.setImageResource(R.drawable.check_enable);
			break;
		case R.id.package_layout3:
		case R.id.service_pkg_pay_yinhangka:
			flag = 3;
			zhifubaoIv.setImageResource(R.drawable.check_no);
			weixinIv.setImageResource(R.drawable.check_no);
			yinhangkaIv.setImageResource(R.drawable.check_enable);
			break;
		case R.id.service_pkg_pay_title_back_iv:
			this.finish();
			break;
		}
	}

	AlertDialog alertDialog;

	/**
	 * 易卡通支付确认
	 */
	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = LayoutInflater.from(this).inflate(R.layout.ui_tongquan,
				null);

		alertDialog = builder.create();
		alertDialog.setView(view);
		final EditText zhifumimaEditText = (EditText) view
				.findViewById(R.id.zhifumimaEditText);
		Button zhifuquxiaoButton = (Button) view
				.findViewById(R.id.zhifuquxiaoButton);
		Button zhifuquedingButton = (Button) view
				.findViewById(R.id.zhifuquedingButton);

		zhifuquedingButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = zhifumimaEditText.getText().toString().trim()
						.replace(" ", "");
				if (password == null || password.length() == 0) {
					Toast.makeText(ActivityPayTypeSelect.this, "支付密码不能为空",
							Toast.LENGTH_SHORT).show();
					// UI.showToast("支付密码不能为空");
				} else {
					volumnRequest(password);
				}
				alertDialog.dismiss();
			}
		});

		zhifuquxiaoButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});

		alertDialog.show();
	}

	protected void volumnRequest(String password) {
		// String urlString = Contacts.URL_BALANCE_PAY_LIST + "customer_id="
		// + CustomerInfo.yonghuID + "&pay_relative_id="
		// + send_out_trade_no + "&pay_password=" + MD5.md5(password)
		// + "&client_type=A&version=1";
		// Log.e("TAG", "==" + urlString);
		// stringRequest = new StringRequest(urlString, new Listener<String>() {
		//
		// @Override
		// public void onResponse(String arg0) {
		// // Log.i(TAG, "==arg0:" + arg0);
		// if (!arg0.equalsIgnoreCase("[]") && arg0 != null) {
		// if (arg0.contains("SUCCESS")) {
		// Toast.makeText(ActivityPayTypeSelect.this, "余额支付成功",
		// Toast.LENGTH_SHORT).show();
		// ActivityPayTypeSelect.this.finish();
		// } else {
		// list_pay = JsonOrderID.JsonToListFAILED(arg0);
		// Toast.makeText(ActivityPayTypeSelect.this,
		// list_pay.get(0).get("error_info").toString(),
		// Toast.LENGTH_SHORT).show();
		// }
		// }
		// }
		//
		// }, new ErrorListener() {
		//
		// @Override
		// public void onErrorResponse(VolleyError arg0) {
		// Toast.makeText(ActivityPayTypeSelect.this, "数据加载异常...",
		// Toast.LENGTH_SHORT).show();
		// }
		// });
		// // stringRequest.setTag(getActivity());
		// requestQueue.add(stringRequest);
	}

	class BitmapCache implements ImageCache {
		private LruCache<String, Bitmap> mCache;

		public BitmapCache() {
			int maxSize = 10 * 1024 * 1024;
			mCache = new LruCache<String, Bitmap>(maxSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getRowBytes() * bitmap.getHeight();
				}
			};
		}

		public Bitmap getBitmap(String url) {
			return mCache.get(url);
		}

		public void putBitmap(String url, Bitmap bitmap) {
			mCache.put(url, bitmap);
		}
	}
}
