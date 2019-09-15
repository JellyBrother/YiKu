package com.jack.ui;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
import com.yst.yiku.R;

public class MerchantDetailsPager extends BasePager {

	private View view;
	private TextView store_name,// 商店名称
			// ratingBar_size,// 评分数
			// store_des,// 店铺描述
			send_low_price,// 起送价
			send_fee,// 配送费
			send_time,// 发送时间
			store_address,// 地址
			// send_info,//配送信息
			store_details_distance,// 距离
			man_jian_tv // 满减
			;
	// private ImageView store_desc_all;
	// private RatingBar store_ratingBar;
	private RequestQueue mQueue;
	/*
	 * private static final int VIDEO_CONTENT_DESC_MAX_LINE = 2;// 默认展示最大行数2行
	 * private static final int SHOW_CONTENT_NONE_STATE = 0;// 扩充 private static
	 * final int SHRINK_UP_STATE = 1;// 收起状态 private static final int
	 * SPREAD_STATE = 2;// 展开状态 private static int mState = SHRINK_UP_STATE;//
	 * 默认收起状态
	 */
	private String store_id = "";
	private String distanceStr = "";
	private String stroe_phone = "11111111111111";
	private ImageView call_phone_merchant;
	private LinearLayout man_jian_ll,// 满减布局
			yidian_ratingBar_ll// 评价 星星额的布局
			;
	private int yi_type;// 从易店来的还是从易库来的
	private RatingBar ratingBar_merchant;
	private String use_condition = "";
	protected String is_yd = "";

	public MerchantDetailsPager(Activity context) {
		super(context);
	}

	@Override
	public View initView() {
		view = View.inflate(context, R.layout.merchant_details, null);
		mQueue = Volley.newRequestQueue(context);

		init();
		Intent intent = context.getIntent();
		if (intent.getStringExtra("id") != null) {
			store_id = intent.getStringExtra("id");
		}
		initData(store_id);

		distanceStr = intent.getStringExtra("distance");
		if (distanceStr != null) {
			double distance = Double.parseDouble(distanceStr);
			if (distance == 0) {
				distanceStr = "0.00m";
			} else if (distance < 1000) {
				if (distance < 1) {
					distanceStr = "距离 0"
							+ new DecimalFormat("###.00").format(distance)
									.toString() + "米";
				} else {
					distanceStr = new DecimalFormat("###.00").format(distance)
							.toString() + "m";
				}
			} else {
				distanceStr = new DecimalFormat("###.00").format(
						distance / 1000).toString()
						+ "km";
			}
			store_details_distance.setText(distanceStr);
		}else {
			store_details_distance.setText("");
		}
		return view;
	}

	/**
	 * 获取店铺信息
	 */
	private void initData(final String store_id) {
		showInfoProgressDialog();
		String url = Contacts.URL_GET_STORE_DETAILS + "id=" + store_id
				+ Contacts.URL_ENDING;
		Log.i("sss", "url===" + url);
		JsonObjectRequest rquest = new JsonObjectRequest(url, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.has("result")) {// 是否有字段
							try {
								String result = response.getString("result");// 获取字段值
								if (result.equalsIgnoreCase("SUCCESS")) {
									if (response.has("data")) {
										JSONObject data = response
												.getJSONObject("data");

										if (data.has("name")) {
											store_name.setText(data
													.getString("name"));
										}
										if (data.has("send_low_price")) {
											send_low_price.setText(String.format(
													"%.2f",
													Double.parseDouble(data
															.getString("send_low_price")) / 100));
										}
										if (data.has("send_fee")) {
											send_fee.setText(String.format(
													"%.2f",
													Double.parseDouble(data
															.getString("send_fee")) / 100));
										}
										if (data.has("send_time")) {
											send_time.setText("配送时间:"
													+ data.getString("send_time"));
										}
										if (data.has("address")) {
											store_address.setText("商家地址:"
													+ data.getString("address"));
										}
										if(distanceStr == null) {
											if (data.has("distance")) {
												distanceStr = data.getString("distance");
												if (distanceStr != null) {
													double distance = Double.parseDouble(distanceStr);
													if (distance == 0) {
														distanceStr = "0.00m";
													} else if (distance < 1000) {
														if (distance < 1) {
															distanceStr = "距离 0"
																	+ new DecimalFormat("###.00").format(distance)
																			.toString() + "米";
														} else {
															distanceStr = new DecimalFormat("###.00").format(distance)
																	.toString() + "m";
														}
													} else {
														distanceStr = new DecimalFormat("###.00").format(
																distance / 1000).toString()
																+ "km";
													}
													store_details_distance.setText(distanceStr);
												}else {
													store_details_distance.setText("");
												}
											}
										}
										if (data.has("phone")) {
											stroe_phone = data
													.getString("phone");
										}

										if (data.has("image")) {
										}
										if (data.has("store_type")) {
										}
										if (data.has("is_yd")) {
											is_yd = data.getString("is_yd");
											Log.i("qcs", "is_yd == " + is_yd);
											if ("1".equals(is_yd)) {
												man_jian_ll
														.setVisibility(View.VISIBLE);
												getStoreFavorList(store_id);
											} else {
												man_jian_ll
														.setVisibility(View.GONE);
												man_jian_tv.setText("");
											}
										}

									}

								} else {// 信息失败
									if (response.has("error_info")) {// 是否有字段
										Toast.makeText(
												context,
												""
														+ response
																.getString("error_info"),
												Toast.LENGTH_SHORT).show();
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

	private void init() {
		store_name = (TextView) view.findViewById(R.id.store_name);
		// ratingBar_size = (TextView) view.findViewById(R.id.ratingBar_size);
		// store_ratingBar = (RatingBar)
		// view.findViewById(R.id.store_ratingBar);
		// store_des = (TextView) view.findViewById(R.id.store_des);
		// store_desc_all = (ImageView) view.findViewById(R.id.store_desc_all);
		send_low_price = (TextView) view.findViewById(R.id.send_low_price);
		send_fee = (TextView) view.findViewById(R.id.send_fee);
		// send_info = (TextView) view.findViewById(R.id.send_info);
		send_time = (TextView) view.findViewById(R.id.send_time);
		store_address = (TextView) view.findViewById(R.id.store_address);
		store_details_distance = (TextView) view
				.findViewById(R.id.store_details_distance);
		call_phone_merchant = (ImageView) view
				.findViewById(R.id.call_phone_merchant);
		man_jian_ll = (LinearLayout) view.findViewById(R.id.man_jian_ll);
		yidian_ratingBar_ll = (LinearLayout) view
				.findViewById(R.id.yidian_ratingBar_ll);
		ratingBar_merchant = (RatingBar) view
				.findViewById(R.id.ratingBar_merchant);
		man_jian_tv = (TextView) view.findViewById(R.id.man_jian_tv);

		call_phone_merchant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopwindow();
			}
		});
	}

	/**
	 * 显示打电话对话框
	 */
	private void showPopwindow() {

		if (TextUtils.isEmpty(stroe_phone))
			return;

		View parent = ((ViewGroup) context.findViewById(android.R.id.content))
				.getChildAt(0);
		View popView = View.inflate(context, R.layout.camera_pop_menu, null);
		// 初始化视图
		Button btnCamera = (Button) popView
				.findViewById(R.id.btn_camera_pop_camera);
		TextView btnAlbum = (TextView) popView
				.findViewById(R.id.btn_camera_pop_album);
		Button btnCancel = (Button) popView
				.findViewById(R.id.btn_camera_pop_cancel);
		// TODO 需要商户的服务电话
		btnAlbum.setText("客服电话：" + stroe_phone);
		btnAlbum.setMovementMethod(LinkMovementMethod.getInstance());

		// 屏幕的宽高
		int width = context.getResources().getDisplayMetrics().widthPixels;
		int height = context.getResources().getDisplayMetrics().heightPixels;

		// 显示控件
		final PopupWindow popWindow = new PopupWindow(popView, width, height);
		popWindow.setAnimationStyle(R.style.AnimBottom);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(false);

		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_camera_pop_camera:

					break;
				case R.id.btn_camera_pop_album:

					break;
				case R.id.btn_camera_pop_cancel:

					break;
				}
				popWindow.dismiss();
			}
		};

		btnCamera.setOnClickListener(listener);
		btnAlbum.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);

		ColorDrawable dw = new ColorDrawable(0x30000000);
		popWindow.setBackgroundDrawable(dw);
		popWindow.showAtLocation(parent, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	/**
	 * 获取店铺优惠信息列表
	 */
	ArrayList<HashMap<String,String>> store_favor_list = null;
	private void getStoreFavorList(String store_id) {
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest(
			Contacts.URL_YI_DIAN_QUERY_STOREFAVOR_LIST + "store_id=" + store_id
				+ Contacts.URL_ENDING, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						Log.i("qcs", "response == " + response.toString());
						try {
							if ("SUCCESS".equals(response.getString("result"))) {
								if (response.has("data")) {
									JSONArray dataArray = response
											.getJSONArray("data");
									store_favor_list = new ArrayList<HashMap<String,String>>();
									for(int i = 0;i < dataArray.length();i ++) {
										HashMap<String,String> favorMap = favorMap = new HashMap<String, String>();;
										JSONObject OBJ = dataArray.getJSONObject(i);
										if(OBJ.has("id")) {
											favorMap.put("id", OBJ.getString("id"));
										}
										if(OBJ.has("use_condition")) {
											favorMap.put("use_condition", Double.parseDouble(OBJ.getString("use_condition")) / 100 + "");
										}
										if(OBJ.has("favor_money")) {
											favorMap.put("favor_money",Double.parseDouble(OBJ.getString("favor_money")) / 100 + "");
										}
										if(store_favor_list.size() < 3) {
											store_favor_list.add(favorMap);
										}
									}
									StringBuffer priceBF = new StringBuffer(); 
									for(int i = 0;i < store_favor_list.size();i ++) {
										String temp = "满" + store_favor_list.get(i).get("use_condition") + "减" + store_favor_list.get(i).get("favor_money");
										if(i == 0) {
											priceBF.append(temp);
										}else {
											priceBF.append(" ; " + temp);
										}
									}
									man_jian_tv.setText(priceBF.toString() + "(在线支付专享)");
								}
							} else {
							}
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(context, "数据解析异常",
									Toast.LENGTH_SHORT).show();
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
		mQueue.add(jsr);
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
		mInfoProgressDialog.dismiss();
	}
}
