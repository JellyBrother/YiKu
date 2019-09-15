package com.jack.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.jack.ui.XListView.IXListViewListener;
import com.yst.yiku.R;

public class MerchantPingLunPager extends BasePager implements OnClickListener,
		IXListViewListener {

	private View view;
	private TextView all_pinglun,// 全部评论
			top_pinglun,// 好评
			center_pinglun,// 中评
			bottom_pinglun// 差评
			;
	private XListView store_pinglun_listview;
	private ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
	private RequestQueue mQueue;
	private int PAGE_COUNT = 0;
	private int PAGE_NO = 0;
	private int COMM_TYPE = 0;
	private String comm_high = "";
	private String comm_middle = "";
	private String comm_low = "";
	// private String store_id = "";
	private String store_id;

	public MerchantPingLunPager(Activity context) {
		super(context);
		Log.i("qcs", "OrderPager");
	}

	@Override
	public View initView() {
		mQueue = Volley.newRequestQueue(context);
		view = View.inflate(context, R.layout.merchant_pinglun, null);
		all_pinglun = (TextView) view.findViewById(R.id.all_pinglun);
		top_pinglun = (TextView) view.findViewById(R.id.top_pinglun);
		center_pinglun = (TextView) view.findViewById(R.id.center_pinglun);
		bottom_pinglun = (TextView) view.findViewById(R.id.bottom_pinglun);
		store_pinglun_listview = (XListView) view
				.findViewById(R.id.store_pinglun_listview);

		store_pinglun_listview.setDividerHeight(0);
		store_pinglun_listview.setXListViewListener(this);
		store_pinglun_listview.setPullLoadEnable(true);
		all_pinglun.setOnClickListener(this);
		top_pinglun.setOnClickListener(this);
		center_pinglun.setOnClickListener(this);
		bottom_pinglun.setOnClickListener(this);

		Intent intent = context.getIntent();
		if (intent.getStringExtra("id") != null) {
			store_id = context.getIntent().getStringExtra("id");
		}
		initData(COMM_TYPE);
		return view;
	}

	/**
	 * 获取店铺评论列表
	 */
	private void initData(int comm_type) {
		showInfoProgressDialog();
		String url = "";
		if (comm_type == 0) {
			url = Contacts.URL_GET_STORE_PINGLUN + "store_id=" + store_id
					+ "&page_size=" + 8 + "&page_on=" + PAGE_NO
					+ Contacts.URL_ENDING;
		} else {
			url = Contacts.URL_GET_STORE_PINGLUN + "store_id=" + store_id
					+ "&page_size=" + 8 + "&page_no=" + PAGE_NO + "&comm_type="
					+ comm_type + Contacts.URL_ENDING;
		}

		Log.i("qcs", "url===" + url);
		JsonObjectRequest rquest = new JsonObjectRequest(url, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.has("result")) {// 是否有字段
							try {
								if (response.has("page_model")) {
									JSONObject page_model = response
											.getJSONObject("page_model");

									PAGE_COUNT = Integer.parseInt(page_model
											.getString("pageCount"));
								}

								String result = response.getString("result");// 获取字段值
								if (result.equalsIgnoreCase("SUCCESS")) {
									if (response.has("data")) {
										JSONArray data = response
												.getJSONArray("data");
										JSONObject jsonOne = data
												.getJSONObject(0);
										if (jsonOne.has("comm_high")) {
											comm_high = jsonOne
													.getString("comm_high");
											top_pinglun.setText("好评("
													+ comm_high + ")");
										}
										if (jsonOne.has("comm_middle")) {
											comm_middle = jsonOne
													.getString("comm_middle");
											center_pinglun.setText("中评("
													+ comm_middle + ")");
										}
										if (jsonOne.has("comm_low")) {
											comm_low = jsonOne
													.getString("comm_low");
											bottom_pinglun.setText("差评("
													+ comm_low + ")");
										}
										int all = Integer.parseInt(comm_high)
												+ Integer.parseInt(comm_middle)
												+ Integer.parseInt(comm_low);
										all_pinglun.setText("全部(" + all + ")");

										for (int i = 0; i < data.length(); i++) {
											HashMap<String, String> map = new HashMap<String, String>();
											JSONObject json = data
													.getJSONObject(i);
											if (json.has("customer_name")) {
												map.put("customer_name",
														json.getString("customer_name"));
											}
											if (json.has("comm_date")) {
												map.put("comm_date", json
														.getString("comm_date"));
											}
											if (json.has("comm_star")) {
												map.put("comm_star", json
														.getString("comm_star"));
											}
											if (json.has("comm_content")) {
												map.put("comm_content",
														json.getString("comm_content"));
											}
											list.add(map);
										}
									}
									if (adapter == null) {
										adapter = new MyAdapter();
										store_pinglun_listview
												.setAdapter(adapter);
									} else {
										adapter.notifyDataSetChanged();
									}
								} else {// 信息失败
									if (list != null && adapter != null) {
										list.clear();
										adapter.notifyDataSetChanged();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						dismissInfoProgressDialog();
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
						Toast.makeText(context, "访问网络错误，请重试!",
								Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(rquest);
	}

	@Override
	public void onClick(View v) {
		// 页面从零开始
		if (list != null && adapter != null) {
			list.clear();
			adapter.notifyDataSetChanged();
		}
		PAGE_NO = 0;
		switch (v.getId()) {
		case R.id.all_pinglun:
			all_pinglun.setBackgroundResource(R.drawable.login_btn_png);
			top_pinglun.setBackgroundResource(R.drawable.text_view_border_two);
			center_pinglun
					.setBackgroundResource(R.drawable.text_view_border_two);
			bottom_pinglun
					.setBackgroundResource(R.drawable.text_view_border_two);
			all_pinglun.setTextColor(Color.parseColor("#FFFFFF"));
			top_pinglun.setTextColor(Color.parseColor("#000000"));
			center_pinglun.setTextColor(Color.parseColor("#000000"));
			bottom_pinglun.setTextColor(Color.parseColor("#000000"));
			COMM_TYPE = 0;
			initData(COMM_TYPE);
			break;
		case R.id.top_pinglun:
			all_pinglun.setBackgroundResource(R.drawable.text_view_border_two);
			top_pinglun.setBackgroundResource(R.drawable.login_btn_png);
			center_pinglun
					.setBackgroundResource(R.drawable.text_view_border_two);
			bottom_pinglun
					.setBackgroundResource(R.drawable.text_view_border_two);
			all_pinglun.setTextColor(Color.parseColor("#000000"));
			top_pinglun.setTextColor(Color.parseColor("#FFFFFF"));
			center_pinglun.setTextColor(Color.parseColor("#000000"));
			bottom_pinglun.setTextColor(Color.parseColor("#000000"));
			COMM_TYPE = 1;
			initData(COMM_TYPE);
			break;
		case R.id.center_pinglun:
			all_pinglun.setBackgroundResource(R.drawable.text_view_border_two);
			top_pinglun.setBackgroundResource(R.drawable.text_view_border_two);
			center_pinglun.setBackgroundResource(R.drawable.login_btn_png);
			bottom_pinglun
					.setBackgroundResource(R.drawable.text_view_border_two);
			all_pinglun.setTextColor(Color.parseColor("#000000"));
			top_pinglun.setTextColor(Color.parseColor("#000000"));
			center_pinglun.setTextColor(Color.parseColor("#FFFFFF"));
			bottom_pinglun.setTextColor(Color.parseColor("#000000"));
			COMM_TYPE = 2;
			initData(COMM_TYPE);
			break;
		case R.id.bottom_pinglun:
			all_pinglun.setBackgroundResource(R.drawable.text_view_border_two);
			top_pinglun.setBackgroundResource(R.drawable.text_view_border_two);
			center_pinglun
					.setBackgroundResource(R.drawable.text_view_border_two);
			bottom_pinglun.setBackgroundResource(R.drawable.login_btn_png);
			all_pinglun.setTextColor(Color.parseColor("#000000"));
			top_pinglun.setTextColor(Color.parseColor("#000000"));
			center_pinglun.setTextColor(Color.parseColor("#000000"));
			bottom_pinglun.setTextColor(Color.parseColor("#FFFFFF"));
			COMM_TYPE = 3;
			initData(COMM_TYPE);
			break;
		}
	}

	private void onLoad() {
		store_pinglun_listview.stopRefresh();
		store_pinglun_listview.stopLoadMore();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH时mm分");
		String time = format.format(date);
		store_pinglun_listview.setRefreshTime(time);
	}

	@Override
	public void onLoadMore() {
		loadData();
	}

	private void loadData() {
		// itemChild.clear();
		if (PAGE_NO + 1 < PAGE_COUNT) {
			Log.e("联网前", PAGE_NO + "====" + PAGE_COUNT);
			PAGE_NO += 1;
			initData(COMM_TYPE);
		} else {
			Toast.makeText(context, "已经加载全部", Toast.LENGTH_SHORT).show();
			onLoad();
			store_pinglun_listview.setPullLoadEnable(false);
		}
	}

	@Override
	public void onRefresh() {
		adapter = null;
		store_pinglun_listview.setPullLoadEnable(true);
		PAGE_NO = 0;
		list.clear();
		initData(COMM_TYPE);
		onLoad();
	}

	MyAdapter adapter = null;

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View
						.inflate(context, R.layout.pinglun_item, null);
				holder.customer_name = (TextView) convertView
						.findViewById(R.id.customer_name);
				holder.pinglun_date = (TextView) convertView
						.findViewById(R.id.pinglun_date);
				holder.store_pinglun_content = (TextView) convertView
						.findViewById(R.id.store_pinglun_content);
				holder.store_pinglun_ratingBar = (RatingBar) convertView
						.findViewById(R.id.store_pinglun_ratingBar);
				holder.pinglun_list_view = convertView
						.findViewById(R.id.pinglun_list_view);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String name = list.get(position).get("customer_name");
			holder.customer_name.setText(name.charAt(0) + "..."
					+ name.charAt(name.length() - 1));
			holder.pinglun_date.setText(list.get(position).get("comm_date"));
			holder.store_pinglun_content.setText(list.get(position).get(
					"comm_content"));
			if (position == 0) {
				holder.pinglun_list_view.setVisibility(View.GONE);
			}
			holder.store_pinglun_ratingBar.setRating(Float.parseFloat(list.get(
					position).get("comm_star")));

			return convertView;
		}
	}

	ViewHolder holder = null;

	class ViewHolder {
		TextView customer_name,// 品论这名称
				pinglun_date, // 日期
				store_pinglun_content // 评论内容
				;
		RatingBar store_pinglun_ratingBar;// 星星
		View pinglun_list_view;

	}

	private MProcessDialog mInfoProgressDialog;

	public void showInfoProgressDialog() {
		if (mInfoProgressDialog == null)
			mInfoProgressDialog = new MProcessDialog(context);
		mInfoProgressDialog.setMessage("加载中");
		mInfoProgressDialog.setCancelable(false);
		if (!context.isFinishing()) {
			try {
				mInfoProgressDialog.show();
			} catch (Exception e) {

			}
		}
	}

	public void dismissInfoProgressDialog() {
		onLoad();
		mInfoProgressDialog.dismiss();
	}
}
