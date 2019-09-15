package com.yst.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.jack.headpicselect.ToastUtil;
import com.jack.ui.BasePager;
import com.jack.ui.MProcessDialog;
import com.jack.ui.MerchantDetailsPager;
import com.jack.ui.MerchantPingLunPager;
import com.yst.yiku.R;

public class StoreDetailActivity extends Activity implements OnClickListener {

	private RelativeLayout store_detail_store_status_layout;
	private RelativeLayout store_detail_store_details_layout;
	private ViewPager viewPager;
	private ArrayList<View> viewList = null;
	private TextView store_detail_line_tips1,// 评价字
			store_detail_line_tips2, // 商家详情字
			activity_title_tv;

	private View store_detail_line1, // 评价线
			store_detail_line2 // 商家详情线
			;
	private ImageView evaluate_order_goods_like_checkbox // 心
			;
	private LinearLayout activity_back_iv;
	private String id;
	private boolean IS_STAR = false;
	private RequestQueue mQueue;
	private String action = "1";
	private int IS_FAVOUR = 0;// 是否已经点赞 1是已点赞 0 是未点赞

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stroe_detail);

		initView();
		Intent intent = getIntent();
		if (intent.getStringExtra("id") != null) {
			id = intent.getStringExtra("id");
		}

	}

	private void initView() {
		mQueue = Volley.newRequestQueue(this);
		store_detail_store_status_layout = (RelativeLayout) findViewById(R.id.store_detail_store_status_layout);
		store_detail_store_details_layout = (RelativeLayout) findViewById(R.id.store_detail_store_details_layout);
		viewPager = (ViewPager) findViewById(R.id.viewpager);
		store_detail_line_tips1 = (TextView) findViewById(R.id.store_detail_line_tips1);
		store_detail_line_tips2 = (TextView) findViewById(R.id.store_detail_line_tips2);
		store_detail_line1 = findViewById(R.id.store_detail_line1);
		store_detail_line2 = findViewById(R.id.store_detail_line2);
		activity_back_iv = (LinearLayout) findViewById(R.id.activity_back_iv);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		evaluate_order_goods_like_checkbox = (ImageView) findViewById(R.id.evaluate_order_goods_like_checkbox);

		evaluate_order_goods_like_checkbox.setOnClickListener(this);
		activity_back_iv.setOnClickListener(this);
		store_detail_store_status_layout.setOnClickListener(this);
		store_detail_store_details_layout.setOnClickListener(this);
		viewList = new ArrayList<View>();
		BasePager orderPager = new MerchantPingLunPager(this);
		BasePager detialsPager = new MerchantDetailsPager(this);
		viewList.add(orderPager.getView());
		viewList.add(detialsPager.getView());
		viewPager.setAdapter(new MyAdapter());
		viewPager.setCurrentItem(1);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				switch (arg0) {
				case 0:
					store_detail_line_tips1.setTextColor(Color
							.parseColor("#00BDFF"));
					store_detail_line_tips2.setTextColor(Color
							.parseColor("#000000"));
					store_detail_line1.setBackgroundColor(Color
							.parseColor("#00BDFF"));
					store_detail_line2.setBackgroundColor(Color
							.parseColor("#DFDFDD"));
					break;
				case 1:
					store_detail_line_tips1.setTextColor(Color
							.parseColor("#000000"));
					store_detail_line_tips2.setTextColor(Color
							.parseColor("#00BDFF"));
					store_detail_line1.setBackgroundColor(Color
							.parseColor("#DFDFDD"));
					store_detail_line2.setBackgroundColor(Color
							.parseColor("#00BDFF"));
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}

	/**
	 * 获取店铺的名称和是否已被点赞
	 */
	/**
	 * 获取店铺信息
	 */
	private void getStoreInfo() {
		showInfoProgressDialog();
		String url = "";
		if(MainActivity.isLogin) {
			url = Contacts.URL_GET_STORE_DETAILS 
					+ "id=" + id
					+ "&accountId=" + Customer.customer_id
					+ Contacts.URL_ENDING;
		}else {
			url = Contacts.URL_GET_STORE_DETAILS 
					+ "id=" + id
					+ Contacts.URL_ENDING;
		}
		Log.i("qcs", "url===" + url);
		JsonObjectRequest rquest = new JsonObjectRequest(url, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("qcs", "success == " + response);
						try {
							if (response.has("result")) {// 是否有字段
								String result = response.getString("result");// 获取字段值
								if (result.equalsIgnoreCase("SUCCESS")) {
									if (response.has("data")) {
										JSONObject data = response
												.getJSONObject("data");
										if (data.has("name")) {
											activity_title_tv.setText(data
													.getString("name"));
										}
										if (data.has("is_favour")) {
											IS_FAVOUR = data
													.getInt("is_favour");
											if (IS_FAVOUR == 0) {// 如果未点赞
												evaluate_order_goods_like_checkbox
														.setImageResource(R.drawable.blue_heart);
											} else if (IS_FAVOUR == 1) {// 如果已经点赞
												evaluate_order_goods_like_checkbox
														.setImageResource(R.drawable.heart);
											}
										}
									}

								} else {// 信息失败
									if (response.has("error_info")) {// 是否有字段
										Toast.makeText(
												StoreDetailActivity.this,
												""
														+ response.getString("error_info"),
												Toast.LENGTH_SHORT).show();
									}
								}
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
						dismissInfoProgressDialog();
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
						Toast.makeText(getApplicationContext(), "访问网络错误，请重试!",
								Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(rquest);
	}
	class MyAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(viewList.get(position));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewList.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(viewList.get(position), 0);
			return viewList.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.store_detail_store_status_layout:
			store_detail_line_tips1.setTextColor(Color.parseColor("#00BDFF"));
			store_detail_line_tips2.setTextColor(Color.parseColor("#000000"));
			store_detail_line1.setBackgroundColor(Color.parseColor("#00BDFF"));
			store_detail_line2.setBackgroundColor(Color.parseColor("#DFDFDD"));
			viewPager.setCurrentItem(0);
			break;
		case R.id.store_detail_store_details_layout:
			store_detail_line_tips1.setTextColor(Color.parseColor("#000000"));
			store_detail_line_tips2.setTextColor(Color.parseColor("#00BDFF"));
			store_detail_line1.setBackgroundColor(Color.parseColor("#DFDFDD"));
			store_detail_line2.setBackgroundColor(Color.parseColor("#00BDFF"));
			viewPager.setCurrentItem(1);
			break;
		case R.id.activity_back_iv:
			finish();
			break;
		case R.id.evaluate_order_goods_like_checkbox:
			if (MainActivity.isLogin) {
				action = "1";
				markerStore();
			} else {
				startActivity(new Intent(this, LoginActivity.class));
			}
			break;
		}
	}

	private void markerStore() {
		showInfoProgressDialog();
		String url = Contacts.URL_MARKER_CRUD + "customer_id="
				+ Customer.customer_id + "&store_id=" + id + "&action="
				+ action + Contacts.URL_ENDING;
		Log.i("qcs", "url ---- " + url);
		JsonObjectRequest rquest = new JsonObjectRequest(url, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							String resultTwo = response.getString("result");
							if (resultTwo.equalsIgnoreCase("SUCCESS")) {
								if (response.has("error_info")) {// 是否有字段
									evaluate_order_goods_like_checkbox
									.setImageResource(R.drawable.heart);
									Toast.makeText(StoreDetailActivity.this,
											"" + response.getString("error_info"),
											Toast.LENGTH_SHORT).show();
								}
							}else {
								if (response.has("error_info")) {// 是否有字段
									Toast.makeText(StoreDetailActivity.this,
											"" + response.getString("error_info"),
											Toast.LENGTH_SHORT).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dismissInfoProgressDialog();
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
					}
				});
		mQueue.add(rquest);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*if (yi_type == 101) {
			evaluate_order_goods_like_checkbox.setVisibility(View.GONE);
			activity_title_tv.setText("三元桥");
		} else if (yi_type == 100) {// 易库跳过来的
			getStoreInfo();
		} else {*/
		getStoreInfo();
		//}
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
