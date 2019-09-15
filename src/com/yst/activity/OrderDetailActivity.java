package com.yst.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.jack.ui.MProcessDialog;
import com.yst.activity.CustomerOrdersActivity.PopAdapter;
import com.yst.yiku.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

/**
 * 订单详情页面
 * 
 * @author lixiangchao
 *
 */
public class OrderDetailActivity extends Activity implements OnClickListener {

	private RelativeLayout oder_detail_order_status_layout,
			oder_detail_order_details_layout;
	private ViewPager order_detail_vp;

	private View statusView, detailsView;

	private List<View> viewlist;

	private ImageView activity_handle_iv, activity_back_iv;

	private TextView order_detail_line_tips1, order_detail_line_tips2;
	private View order_detail_line1, order_detail_line2;

	private RequestQueue requestQueue;
	private MProcessDialog mInfoProgressDialog;
	
	//商户名称
	private TextView order_details_store_name_tv ;
	private ListView order_details_store_items_list_view;
	private TextView order_details_peisongfei_tv;
//	private TextView order_details_manjianyouhui_tv;
	private TextView order_details_total_price_tv;
	private TextView order_details_dingdanhaoma_tv;
	private TextView order_details_dingdanshijian_tv;
	private TextView order_details_zhifufangshi_tv;
	private TextView order_details_shouhuoren_tv;
	private TextView order_details_shoujihaoma_tv;
	private TextView order_details_address_tv;
	private TextView order_details_send_msg_tv;
	
	private LinearLayout order_detail_linear_layout;
	private TextView order_detail_youhui_desc_tv;
	private TextView order_details_youhui_price;
	
	private ListView order_status_list_view;
	private List<Map<String,Object>> listtt ;
	private String pay_status = "", stroe_phone = "", order_id = "", is_yd = "0" ;
	private StatusAdapter sAdapter ;
	private String store_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);

		initView();
	}

	private Button order_status_contact_btn, order_detail_view_again_btn;
	
	// 初始化部分控件
	private void initView() {
		requestQueue = Volley.newRequestQueue(this);
		order_detail_vp = (ViewPager) findViewById(R.id.order_detail_vp);
		listtt = new ArrayList<Map<String,Object>>();
		oder_detail_order_status_layout = (RelativeLayout) findViewById(R.id.oder_detail_order_status_layout);
		oder_detail_order_details_layout = (RelativeLayout) findViewById(R.id.oder_detail_order_details_layout);
		oder_detail_order_status_layout.setOnClickListener(this);
		oder_detail_order_details_layout.setOnClickListener(this);
		
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);

		activity_handle_iv = (ImageView) findViewById(R.id.activity_handle_iv);
		activity_handle_iv.setOnClickListener(this);

		order_detail_line_tips1 = (TextView) findViewById(R.id.order_detail_line_tips1);
		order_detail_line_tips2 = (TextView) findViewById(R.id.order_detail_line_tips2);
		order_detail_line1 = (View) findViewById(R.id.order_detail_line1);
		order_detail_line2 = (View) findViewById(R.id.order_detail_line2);

		viewlist = new ArrayList<View>();
		detailsView = getLayoutInflater().inflate(R.layout.order_details_view, null);
		statusView = getLayoutInflater().inflate(R.layout.order_status_view, null);
		viewlist.add(statusView);
		viewlist.add(detailsView);
		
		order_detail_linear_layout = (LinearLayout) detailsView.findViewById(R.id.order_detail_linear_layout);
		order_detail_youhui_desc_tv = (TextView) detailsView.findViewById(R.id.order_detail_youhui_desc_tv);
		order_details_youhui_price = (TextView) detailsView.findViewById(R.id.order_details_youhui_price);
		
		order_detail_view_again_btn = (Button) detailsView.findViewById(R.id.order_detail_view_again_btn);
		order_detail_view_again_btn.setOnClickListener(this);
		
		order_status_contact_btn = (Button) statusView.findViewById(R.id.order_status_contact_btn);
		order_status_contact_btn.setOnClickListener(this);
		
		//订单的状态列表显示
		order_status_list_view = (ListView) statusView.findViewById(R.id.order_status_list_view);
		
		order_details_store_name_tv = (TextView) detailsView.findViewById(R.id.order_details_store_name_tv);
		order_details_peisongfei_tv = (TextView) detailsView.findViewById(R.id.order_details_peisongfei_tv);
//		order_details_manjianyouhui_tv = (TextView) detailsView.findViewById(R.id.order_details_manjianyouhui_tv);
		order_details_total_price_tv = (TextView) detailsView.findViewById(R.id.order_details_total_price_tv);
		order_details_dingdanhaoma_tv = (TextView) detailsView.findViewById(R.id.order_details_dingdanhaoma_tv);
		order_details_dingdanshijian_tv = (TextView) detailsView.findViewById(R.id.order_details_dingdanshijian_tv);
		order_details_zhifufangshi_tv = (TextView) detailsView.findViewById(R.id.order_details_zhifufangshi_tv);
		order_details_shouhuoren_tv = (TextView) detailsView.findViewById(R.id.order_details_shouhuoren_tv);
		order_details_shoujihaoma_tv = (TextView) detailsView.findViewById(R.id.order_details_shoujihaoma_tv);
		order_details_address_tv = (TextView) detailsView.findViewById(R.id.order_details_address_tv);
		order_details_send_msg_tv = (TextView) detailsView.findViewById(R.id.order_details_send_msg_tv);
		order_details_store_items_list_view = (ListView) detailsView.findViewById(R.id.order_details_store_items_list_view);
		
		order_details_store_name_tv.setOnClickListener(this);
		
		order_detail_vp.setAdapter(new MyAdapter());
		order_detail_vp.setCurrentItem(0);
		order_detail_vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch (arg0) {
				case 0:
					// 这里用parsecolor方法，其他的方法可能有问题
					order_detail_line_tips1.setTextColor(Color
							.parseColor("#00BDFF"));
					order_detail_line1.setBackgroundColor(Color
							.parseColor("#00BDFF"));
					order_detail_line_tips2.setTextColor(Color
							.parseColor("#000000"));
					order_detail_line2.setBackgroundColor(Color
							.parseColor("#DFDFDD"));
					break;
				case 1:
					order_detail_line_tips2.setTextColor(Color
							.parseColor("#00BDFF"));
					order_detail_line2.setBackgroundColor(Color
							.parseColor("#00BDFF"));
					order_detail_line_tips1.setTextColor(Color
							.parseColor("#000000"));
					order_detail_line1.setBackgroundColor(Color
							.parseColor("#DFDFDD"));
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
		pay_status = getIntent().getStringExtra("PAY_STATUS");
		order_id = getIntent().getStringExtra("ORDER_ID");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getOrderDetailInfo(order_id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.oder_detail_order_status_layout:
			order_detail_line_tips1.setTextColor(Color.parseColor("#00BDFF"));
			order_detail_line_tips2.setTextColor(Color.parseColor("#DFDFDD"));
			order_detail_line1.setBackgroundColor(Color.parseColor("#00BDFF"));
			order_detail_line2.setBackgroundColor(Color.parseColor("#DFDFDD"));
			order_detail_vp.setCurrentItem(0);
			break;
		case R.id.oder_detail_order_details_layout:
			order_detail_line_tips2.setTextColor(Color.parseColor("#00BDFF"));
			order_detail_line_tips1.setTextColor(Color.parseColor("#DFDFDD"));
			order_detail_line2.setBackgroundColor(Color.parseColor("#00BDFF"));
			order_detail_line1.setBackgroundColor(Color.parseColor("#DFDFDD"));
			order_detail_vp.setCurrentItem(1);
			break;
		case R.id.activity_handle_iv:
			showPopwindow();
			break;
		case R.id.order_details_store_name_tv:
		//	Toast.makeText(this, "跳转到商户详情界面", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(OrderDetailActivity.this, StoreDetailActivity.class);
			intent.putExtra("id", store_id);
			startActivity(intent);
			break;
		case R.id.activity_back_iv:
			finish();
			break;
		case R.id.order_status_contact_btn:
			showPopwindow();
			break;
		case R.id.order_detail_view_again_btn:
			Intent it = new Intent(this, SubmitOrderActivity.class);
			it.putExtra("PRODUCT_INFO", order_id);
			it.putExtra("index", 3);
			startActivity(it);
			break;
		}
	}

	/**
	 * 显示打电话对话框
	 */
	private void showPopwindow() {
		
		if(TextUtils.isEmpty(stroe_phone)) return ;
		
		View parent = ((ViewGroup) this.findViewById(android.R.id.content))
				.getChildAt(0);
		View popView = View.inflate(this, R.layout.camera_pop_menu, null);
		// 初始化视图
		Button btnCamera = (Button) popView
				.findViewById(R.id.btn_camera_pop_camera);
		TextView btnAlbum = (TextView) popView
				.findViewById(R.id.btn_camera_pop_album);
		Button btnCancel = (Button) popView
				.findViewById(R.id.btn_camera_pop_cancel);
		//TODO 需要商户的服务电话
		btnAlbum.setText("客服电话：" + stroe_phone);
		btnAlbum.setMovementMethod(LinkMovementMethod.getInstance());

		// 屏幕的宽高
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;

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
	 * 用来滑动的适配器
	 * 
	 * @author lixiangchao
	 *
	 */
	class MyAdapter extends PagerAdapter {

		@Override
		public void destroyItem(View container, int position, Object object) {
			// TODO Auto-generated method stub
			((ViewPager) container).removeView(viewlist.get(position));
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewlist.size();
		}

		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(viewlist.get(position), 0);
			return viewlist.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
	}

	// 界面等待条显示
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

	// 界面等待条关闭
	public void dismissInfoProgressDialog() {
		mInfoProgressDialog.dismiss();
	}
	
	
	/**
	 * 获取订单的详情接口
	 * 
	 * @param order_id 订单的id
	 */
	private void getOrderDetailInfo(String order_id){
		// url = http://182.254.161.94:8080/ydg/productOrder!getInfo?order_id=20151005171948867&client_type=A&version=1
		
		showInfoProgressDialog();
		
		Log.e("sss",Contacts.URL_GET_ORDER_DETAIL_INFO + "order_id=" + order_id + Contacts.URL_ENDING);
		
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_ORDER_DETAIL_INFO + "order_id=" + order_id + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					if("SUCCESS".equals(response.getString("result"))){
						
						listtt = getOrderStatus(response);
//						SimpleAdapter adapters = new SimpleAdapter(getApplicationContext(), listtt, R.layout.order_details_status_item, new String[]{"name", "time", "desc", "icon"}, new int[]{R.id.order_status_name1_tv, R.id.order_status_time1_tv, R.id.order_status_desc1_tv, R.id.order_status_iv1});
						sAdapter = new StatusAdapter();
						order_status_list_view.setAdapter(sAdapter);
						
						setListViewHeightBasedOnChildren(order_status_list_view);
						
						List<Map<String,String>> list = JsonToProducts(response);
						SimpleAdapter sAdapter = new SimpleAdapter(OrderDetailActivity.this, list, R.layout.order_details_store_item, new String[]{"name", "count", "price"}, new int[]{R.id.order_details_store_item_name_tv, R.id.order_details_store_item_count_tv, R.id.order_details_store_item_price_tv});
						order_details_store_items_list_view.setAdapter(sAdapter);
						setListViewHeightBasedOnChildren(order_details_store_items_list_view);
						
						order_details_store_name_tv.setText(response.getJSONObject("data").getString("store_name"));
						if(response.getJSONObject("data").has("send_fee")){
							order_details_peisongfei_tv.setText("¥ " + String.valueOf(response.getJSONObject("data").getDouble("send_fee") / 100));
						} else {
							order_details_peisongfei_tv.setText("¥ 0");
						}
						if(response.getJSONObject("data").has("store_id")){
							store_id = response.getJSONObject("data").getString("store_id");
						} else {
							store_id = "";
						}
//						if(response.getJSONObject("data").has("store_favor_price")){
//							order_details_manjianyouhui_tv.setText("-¥ " + String.valueOf(response.getJSONObject("data").getDouble("store_favor_price") / 100));
//						} else {
//							order_details_manjianyouhui_tv.setText("-¥ 0");
//						}
						order_details_total_price_tv.setText("¥ " + String.valueOf(response.getJSONObject("data").getDouble("total_price") / 100));
						order_details_dingdanhaoma_tv.setText("订单号码：" + response.getJSONObject("data").getString("order_id"));
						//create_date------->>>>create_time
						order_details_dingdanshijian_tv.setText("订单时间：" + response.getJSONObject("data").getString("create_time"));
						if(response.getJSONObject("data").getString("pay_online").equals("0")){
							order_details_zhifufangshi_tv.setText("支付方式：在线支付");
						} else {
							order_details_zhifufangshi_tv.setText("支付方式：货到付款");
						}
						
						if(response.getJSONObject("data").getString("send_type").equals("1")) {
							order_details_address_tv.setText("提货地址：" + response.getJSONObject("data").getString("store_address"));
						} else {
							order_details_address_tv.setText("收货地址：" + response.getJSONObject("data").getString("receive_addr"));
						}
						if(response.getJSONObject("data").has("is_yd")) {
							is_yd = response.getJSONObject("data").getString("is_yd");
							if(response.getJSONObject("data").getString("is_yd").equals("0")) {
								if(response.getJSONObject("data").has("customer_favor_id")) {
									order_detail_linear_layout.setVisibility(View.VISIBLE);
									order_detail_youhui_desc_tv.setText("优惠券金额");
									if(response.getJSONObject("data").has("customer_favor_price")) {
										String source = "<font color='#00BAFF'>" + "-¥" + (int)(Double.parseDouble(response.getJSONObject("data").getString("customer_favor_price")) / 100) + "</font>";
										order_details_youhui_price.setText(Html.fromHtml(source));
									} else {
										String source = "<font color='#00BAFF'>" + "-¥0" + "</font>";
										order_details_youhui_price.setText(Html.fromHtml(source));
									}
								} else {
									order_detail_linear_layout.setVisibility(View.GONE);
									order_detail_youhui_desc_tv.setText("");
									order_details_youhui_price.setText("");
									
								}
							} else if(response.getJSONObject("data").getString("is_yd").equals("1")) {
								if(response.getJSONObject("data").has("store_favor_id")) {
									order_detail_linear_layout.setVisibility(View.VISIBLE);
									String source1 = "<font color='#00BAFF'>" + "满减优惠" + "</font>";
									order_detail_youhui_desc_tv.setText(Html.fromHtml(source1));
									if(response.getJSONObject("data").has("store_favor_price")) {
										// #00BDFF
										String source = "<font color='#00BAFF'>" + "-¥" + (Double.parseDouble(response.getJSONObject("data").getString("store_favor_price")) / 100) + "</font>";
										order_details_youhui_price.setText(Html.fromHtml(source));
									} else {
										String source = "<font color='#00BAFF'>" + "-¥0" + "</font>";
										order_details_youhui_price.setText(Html.fromHtml(source));
									}
								} else {
									order_detail_linear_layout.setVisibility(View.GONE);
									order_detail_youhui_desc_tv.setText("");
									order_details_youhui_price.setText("");
								}
							}
						}
						
						order_details_shouhuoren_tv.setText("收货姓名：" + response.getJSONObject("data").getString("receive_name"));
						order_details_shoujihaoma_tv.setText("手机号码：" + response.getJSONObject("data").getString("receive_phone"));
						order_details_send_msg_tv.setText("配送信息:由" + response.getJSONObject("data").getString("store_name") + "专送提供高品质配送服务");
						if(response.getJSONObject("data").has("store_phone")){
							stroe_phone = response.getJSONObject("data").getString("store_phone");
						} else {
							stroe_phone = "";
						}
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
					dismissInfoProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
					dismissInfoProgressDialog();
					Toast.makeText(getApplicationContext(), "数据解析异常", Toast.LENGTH_SHORT).show();
					Log.e("sss","the exception message is === " + e.getMessage());
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(getApplicationContext(), "访问网络错误，请重试!", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 这是订单的状态列表
	 * 
	 * @param json
	 * 
	 * @return
	 */
	protected List<Map<String, Object>> getOrderStatus(JSONObject json) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			if(json.getJSONObject("data").has("create_time")){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name", "订单已提交");
				map.put("time", "" + json.getJSONObject("data").getString("create_time"));
				map.put("desc", "请耐心等待商家确认");
				map.put("icon", R.drawable.order_status1);
				list.add(map);
			}
			
			Log.e("sss"," pay status is === " + pay_status);
			
			if(pay_status.equals("1")){
				Map<String,Object> mapd = new HashMap<String, Object>();
				if(json.getJSONObject("data").getString("pay_online").equals("1")){
					Log.e("sss"," pay status is iiiiiiiiiiiiiii === " + pay_status);
					mapd.put("name", "待发货");
					mapd.put("time", "" + json.getJSONObject("data").getString("create_time"));
					mapd.put("desc", "请在发货后再确认订单收货哦");
					mapd.put("icon", R.drawable.order_status_2);
				} else {
					Log.e("sss"," pay status is eeeeeeeeeee === " + pay_status);
					mapd.put("name", "待支付");
					mapd.put("time", "" + json.getJSONObject("data").getString("create_time"));
					mapd.put("desc", "请在15分钟内完成支付");
					mapd.put("icon", R.drawable.order_status_2);
				}
				list.add(mapd);
				
			} else if(pay_status.equals("9")){
				Map<String,Object> mapd = new HashMap<String, Object>();
				mapd.put("name", "待支付");
				mapd.put("time", "" + json.getJSONObject("data").getString("create_time"));
				mapd.put("desc", "请在15分钟内完成支付");
				mapd.put("icon", R.drawable.order_status_2);
				list.add(mapd);
			}
			
			if(json.getJSONObject("data").has("pay_time")){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name", "支付时间");
				map.put("time", "" + json.getJSONObject("data").getString("pay_time"));
				map.put("desc", "订单已支付");
				map.put("icon", R.drawable.order_status_2);
				list.add(map);
			}
			
			if(json.getJSONObject("data").has("delivery_time")){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name", "订单已发货");
				map.put("time", "" + json.getJSONObject("data").getString("delivery_time"));
				map.put("desc", "请耐心等待收货");
				map.put("icon", R.drawable.order_status_2);
				list.add(map);
			}
			if(json.getJSONObject("data").has("take_time")){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name", "订单已收货");
				map.put("time", "" + json.getJSONObject("data").getString("take_time"));
				map.put("desc", "您已经完成收货了");
				map.put("icon", R.drawable.order_status3);
				list.add(map);
			}
			if(json.getJSONObject("data").has("over_time")){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name", "订单已完成");
				map.put("time", "" + json.getJSONObject("data").getString("over_time"));
				map.put("desc", "订单已经完成了");
				map.put("icon", R.drawable.order_status3);
				list.add(map);
			}
			if(json.getJSONObject("data").has("cancel_time")){
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name", "订单已取消");
				map.put("time", "" + json.getJSONObject("data").getString("cancel_time"));
				map.put("desc", "订单已经取消了");
				map.put("icon", R.drawable.order_status3);
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("sss","the status list exception is ==== " + e.getMessage());
		}
		
		Log.e("sss","the list status size is === " + list.size());
		Log.e("sss","the list status size is === " + list.toString());
		
		return list;
	}

	/**
	 * 为了解决ListView在ScrollView中只能显示一行数据的问题
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
	
	/**
	 * 解析订单的商品详情接口
	 * 
	 * @param json
	 * 
	 * @return
	 */
	private List<Map<String,String>> JsonToProducts(JSONObject json){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if (json.has("data")) {
			try {
				for (int i = 0; i < json.getJSONObject("data").getJSONArray("product_order_item").length(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("store_prod_id",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("store_prod_id"));
					map.put("id",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("id"));
					map.put("price",
							"¥" + String.valueOf(((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i)).getDouble("price") /100));
					map.put("count", "x" + ((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item")
							.get(i)).getString("count"));
					map.put("is_stock",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("is_stock"));
					map.put("status", ((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item")
							.get(i)).getString("status"));
					map.put("order_id",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("order_id"));
					map.put("name",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("name"));
					list.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				list.clear();
			}
		}
		return list ;
	}
	
	//用来显示状态数据的显示类
	class StatusAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listtt.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return listtt.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null ;
			if(null == convertView) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.order_details_status_item, null);
				holder.order_status_desc1_tv = (TextView) convertView.findViewById(R.id.order_status_desc1_tv);
				holder.order_status_time1_tv = (TextView) convertView.findViewById(R.id.order_status_time1_tv);
				holder.order_status_name1_tv = (TextView) convertView.findViewById(R.id.order_status_name1_tv);
				holder.order_status_iv1 = (ImageView) convertView.findViewById(R.id.order_status_iv1);
				holder.order_status_line_top = (View) convertView.findViewById(R.id.order_status_line_top);
				holder.order_status_line_bottom = (View) convertView.findViewById(R.id.order_status_line_bottom);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			//用来控制第一行顶部的竖线显示
			if(position == 0){
				holder.order_status_line_top.setVisibility(View.GONE);
			} else {
				holder.order_status_line_top.setVisibility(View.VISIBLE);
			}
			//用来控制最后一条数据的底部竖线的颜色
			if(position == (listtt.size() - 1)){
				holder.order_status_line_bottom.setBackgroundColor(Color.parseColor("#DFDFDD"));
			} else {
				holder.order_status_line_top.setBackgroundColor(Color.parseColor("#00BDFF"));
			}
			
//			listtt, 
//			R.layout.order_details_status_item, 
//			new String[]{"name", "time", "desc", "icon"}, 
//			new int[]{R.id.order_status_name1_tv, R.id.order_status_time1_tv, R.id.order_status_desc1_tv, R.id.order_status_iv1});
			holder.order_status_name1_tv.setText((String)listtt.get(position).get("name"));//显示的标题
			
			if(0 == isYesterday((String)listtt.get(position).get("time"))){
				holder.order_status_time1_tv.setText("今天" + ((String)listtt.get(position).get("time")).substring(11, 16));//显示的时间
			} else if(1 == isYesterday((String)listtt.get(position).get("time"))){
				holder.order_status_time1_tv.setText("昨天" +  ((String)listtt.get(position).get("time")).substring(11, 16));//显示的时间
			} else {
				holder.order_status_time1_tv.setText(((String)listtt.get(position).get("time")).substring(5, 16));//显示的时间
			}
			
			holder.order_status_desc1_tv.setText((String)listtt.get(position).get("desc"));//显示的标题描述
			holder.order_status_iv1.setImageResource((Integer)listtt.get(position).get("icon"));//显示的状态图标
			
			return convertView;
		}
		
		//用来缓存子项的类
		class ViewHolder {
			TextView order_status_name1_tv, order_status_time1_tv, order_status_desc1_tv ;
			ImageView order_status_iv1 ;
			View order_status_line_top, order_status_line_bottom ;
		}
	}
	
	/**
	 * 判断时间是今天还是昨天
	 * 
	 * @param date 传进来的日期
	 * 
	 * @return 0 今天 -1 昨天
	 */
	public int isYesterday(String date) {

		int day = 0;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {

			Date d1 = new Date();// 当前时间

			Date d2 = sdf.parse(date);// 传进的时间

			long cha = d2.getTime() - d1.getTime();

			day = new Long(cha / (1000 * 60 * 60 * 24)).intValue();

		} catch (ParseException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		return day;
	}
}
