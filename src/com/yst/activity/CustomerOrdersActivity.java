package com.yst.activity;

import java.text.DecimalFormat;
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
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.guohua.common.util.NetworkUtils;
import com.jack.headpicselect.BitmapCache;
import com.jack.ui.MProcessDialog;
import com.jack.ui.XListView;
import com.jack.ui.XListView.IXListViewListener;
import com.yst.yiku.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用户订单界面
 * 
 * @author lixiangchao
 *
 */
public class CustomerOrdersActivity extends Activity implements
		OnRefreshListener, OnClickListener, IXListViewListener {

	private SwipeRefreshLayout swipeLayout;
	private XListView listView;
	// private ArrayList<String> list;
	private MyAdapter adapter;
	private int count = 0, orderIndex = 0;

	private View orders_no_list_view;

	private PopupWindow popRight;
	private View layoutRight;
	private ListView menulistRight;
	private List<Map<String, String>> productOrderList;
	private List<Map<String, String>> productsList = new ArrayList<Map<String,String>>();
	private PopAdapter mAdapter;
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	private MProcessDialog mInfoProgressDialog;

	private Handler mHandler;
	private int page_size = 10;
	private int totalSize = 0;
	private int m_currentpage = 0;

	private ImageView activity_back_iv;
	private TextView activity_title_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_orders);

		// initData();
		initView();
		register();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		m_currentpage = 0 ;
		productOrderList.clear();
		getProductOrderList();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregister();
//		totalSize = 0;
//		m_currentpage = 0;
	}
	
	private void register(){
		// 动态注册广播 使用的方法是为了刷新界面
		IntentFilter filter = new IntentFilter();
		filter.addAction(ORDER_REFRESH_ACTION);
		registerReceiver(myReceiver, filter);
	}
	
	private void unregister(){
		unregisterReceiver(myReceiver);
	}

	/**
	 * 解析时间为月-日 小时-分钟
	 * @param date
	 * @return
	 */
	public String isTime(String date) {
		String time = "";
		time = date.substring(5, 16);
		return time;
	}

	/**
	 * 返回是今天还是昨天
	 * @param date
	 * @return
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

	// 初始化
	private void initView() {
		requestQueue = Volley.newRequestQueue(this);
		mHandler = new Handler();
		imageLoader = new ImageLoader(requestQueue, new BitmapCache());
		productOrderList = new ArrayList<Map<String, String>>();

		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_orders_label);

		orders_no_list_view = findViewById(R.id.orders_no_list_view);

		// orders_no_list_view.setVisibility(View.VISIBLE);

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe2);
		swipeLayout.setColorScheme(R.color.holo_blue_bright,
				R.color.holo_green_light, R.color.holo_orange_light,
				R.color.holo_red_light);
		swipeLayout.setVisibility(View.VISIBLE);

		listView = (XListView) findViewById(R.id.list2);

		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(false);
		listView.setXListViewListener(this);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent(CustomerOrdersActivity.this, OrderDetailActivity.class);
				it.putExtra("ORDER_ID", productOrderList.get(position-1).get("order_id"));
				it.putExtra("PAY_STATUS", productOrderList.get(position-1).get("pay_status"));
				startActivity(it);
			}
		});
		swipeLayout.setOnRefreshListener(this);

	}

	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return productOrderList.size();
		}
		@Override
		public Object getItem(int position) {
			return productOrderList.get(position);
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.customer_orders_list_item, null);
				holder = new ViewHolder();
				
				holder.orders_product_pay_btn = (Button) convertView.findViewById(R.id.orders_product_pay_btn);
				
				holder.order_list_item_image_iv = (NetworkImageView) convertView.findViewById(R.id.order_list_item_image_iv);
				
				holder.orders_product_express_tv = (TextView) convertView.findViewById(R.id.orders_product_express_tv);

				holder.order_list_item_store_name_tv = (TextView) convertView
						.findViewById(R.id.order_list_item_store_name_tv);

				holder.order_list_item_store_pay_status_tv = (TextView) convertView
						.findViewById(R.id.order_list_item_store_pay_status_tv);

				holder.orders_product_price_tv = (TextView) convertView
						.findViewById(R.id.orders_product_price_tv);

				holder.orders_product_time_tv = (TextView) convertView
						.findViewById(R.id.orders_product_time_tv);

				holder.orders_product_desc_tv = (TextView) convertView
						.findViewById(R.id.orders_product_desc_tv);

				holder.order_list_item_bottom_layout = (RelativeLayout) convertView
						.findViewById(R.id.order_list_item_bottom_layout);

				holder.orders_product_evaluate_btn = (Button) convertView
						.findViewById(R.id.orders_product_evaluate_btn);
				holder.orders_product_verify_order_btn = (Button) convertView
						.findViewById(R.id.orders_product_verify_order_btn);
				holder.orders_product_again_btn = (Button) convertView.findViewById(R.id.orders_product_again_btn);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.order_list_item_image_iv.setDefaultImageResId(R.drawable.app_default_icon);
			holder.order_list_item_image_iv.setErrorImageResId(R.drawable.app_default_icon);
			
			if(productOrderList.get(position).get("store_image") != null){
				holder.order_list_item_image_iv.setImageUrl(Contacts.URL_ROOT + "/ydg/" + productOrderList.get(
					position).get("store_image"), imageLoader);
			} else {
				holder.order_list_item_image_iv.setImageUrl(Contacts.URL_ROOT, imageLoader);
			}

			holder.order_list_item_store_name_tv.setText(productOrderList.get(position).get("store_name"));

			int pay_online = Integer.parseInt(productOrderList.get(position).get("pay_online"));
			int index = Integer.parseInt(productOrderList.get(position).get("pay_status"));
			final int pos = position ;
			//订单状态 1 创建未支付 2 已经支付 3 已经发货 4 已经收货 5 已经完成 6. 已经评论 9 订单取消
			switch(index){
			case 1:
				if(pay_online == 0){
					holder.order_list_item_bottom_layout.setVisibility(View.VISIBLE);
					holder.orders_product_verify_order_btn.setVisibility(View.GONE);
					holder.orders_product_again_btn.setVisibility(View.GONE);
					holder.orders_product_pay_btn.setVisibility(View.VISIBLE);
					holder.orders_product_pay_btn.setOnClickListener(new PayListener(pos));
					holder.orders_product_express_tv.setVisibility(View.GONE);
					holder.order_list_item_store_pay_status_tv.setText("未支付");
					holder.orders_product_evaluate_btn.setVisibility(View.GONE);
					holder.order_list_item_store_pay_status_tv.setTextColor(Color.parseColor("#000000"));
				} else {
					holder.order_list_item_bottom_layout.setVisibility(View.GONE);
					holder.orders_product_verify_order_btn.setVisibility(View.GONE);
					holder.orders_product_again_btn.setVisibility(View.GONE);
					holder.orders_product_pay_btn.setVisibility(View.GONE);
					holder.orders_product_express_tv.setVisibility(View.GONE);
					holder.orders_product_evaluate_btn.setVisibility(View.GONE);
					holder.order_list_item_store_pay_status_tv.setText("待发货");
					holder.order_list_item_store_pay_status_tv.setTextColor(Color.parseColor("#000000"));
				}
				break;
			case 2:
				holder.orders_product_pay_btn.setVisibility(View.GONE);
				holder.orders_product_express_tv.setVisibility(View.GONE);
				holder.order_list_item_bottom_layout.setVisibility(View.VISIBLE);
				holder.order_list_item_store_pay_status_tv.setText("已支付");
				holder.order_list_item_store_pay_status_tv.setTextColor(Color.parseColor("#000000"));
				holder.orders_product_evaluate_btn.setVisibility(View.GONE);
				holder.orders_product_again_btn.setVisibility(View.VISIBLE);
//				holder.orders_product_verify_order_btn.setVisibility(View.VISIBLE);
//				holder.orders_product_verify_order_btn.setOnClickListener(new MyReceiveListener(pos));
				holder.orders_product_again_btn.setOnClickListener(new MyAgainListener(pos));
				break;
			case 3:
				holder.orders_product_pay_btn.setVisibility(View.GONE);
				holder.orders_product_express_tv.setVisibility(View.GONE);
				holder.order_list_item_bottom_layout.setVisibility(View.VISIBLE);
				
//				if(null != productOrderList.get(position).get("express_name")){
//					if(null != productOrderList.get(position).get("express_no")){
//						holder.orders_product_express_tv.setText(productOrderList.get(position).get("express_name") + "  " + productOrderList.get(position).get("express_no"));
//					} else {
//						holder.orders_product_express_tv.setText(productOrderList.get(position).get("express_name"));
//					}
//				} else {
//					holder.orders_product_express_tv.setText("");
//				}
				
				holder.order_list_item_store_pay_status_tv.setText("已发货");
				holder.order_list_item_store_pay_status_tv.setTextColor(Color.parseColor("#00BDFF"));
				holder.orders_product_evaluate_btn.setVisibility(View.GONE);
				holder.orders_product_again_btn.setVisibility(View.VISIBLE);
				holder.orders_product_again_btn.setOnClickListener(new MyAgainListener(pos));
				holder.orders_product_verify_order_btn.setVisibility(View.VISIBLE);
				holder.orders_product_verify_order_btn.setOnClickListener(new MyReceiveListener(pos));
				break;
			case 4:
				holder.orders_product_pay_btn.setVisibility(View.GONE);
				holder.orders_product_express_tv.setVisibility(View.GONE);
				holder.order_list_item_bottom_layout.setVisibility(View.VISIBLE);
				holder.order_list_item_store_pay_status_tv.setText("已收货 ");
				holder.order_list_item_store_pay_status_tv.setTextColor(Color.parseColor("#000000"));
				holder.orders_product_again_btn.setVisibility(View.VISIBLE);
				holder.orders_product_evaluate_btn.setVisibility(View.GONE);
				holder.orders_product_again_btn.setOnClickListener(new MyAgainListener(pos));
				holder.orders_product_verify_order_btn.setVisibility(View.VISIBLE);
				holder.orders_product_verify_order_btn.setOnClickListener(new MyReceiveListener(pos));
				break;
			case 5:
				holder.orders_product_pay_btn.setVisibility(View.GONE);
				holder.orders_product_express_tv.setVisibility(View.GONE);
				holder.order_list_item_bottom_layout.setVisibility(View.VISIBLE);
				holder.order_list_item_store_pay_status_tv.setText("待评价");
				holder.order_list_item_store_pay_status_tv.setTextColor(Color.parseColor("#000000"));
				holder.orders_product_again_btn.setVisibility(View.VISIBLE);
				holder.orders_product_verify_order_btn.setVisibility(View.GONE); 
				holder.orders_product_evaluate_btn.setVisibility(View.VISIBLE);
				holder.orders_product_evaluate_btn.setOnClickListener(new MyEvaluateListener(pos));
				holder.orders_product_again_btn.setOnClickListener(new MyAgainListener(pos));
				break;
			case 6:
				holder.orders_product_pay_btn.setVisibility(View.GONE);
				holder.orders_product_express_tv.setVisibility(View.GONE);
				holder.order_list_item_bottom_layout.setVisibility(View.VISIBLE);
				holder.order_list_item_store_pay_status_tv.setText("已完成");
				holder.order_list_item_store_pay_status_tv.setTextColor(Color.parseColor("#000000"));
				holder.orders_product_again_btn.setVisibility(View.VISIBLE);
				holder.orders_product_verify_order_btn.setVisibility(View.GONE); 
				holder.orders_product_evaluate_btn.setVisibility(View.GONE);
				holder.orders_product_again_btn.setOnClickListener(new MyAgainListener(pos));
				break;
			case 9:
				holder.orders_product_pay_btn.setVisibility(View.GONE);
				holder.orders_product_express_tv.setVisibility(View.GONE);
				holder.order_list_item_bottom_layout.setVisibility(View.VISIBLE);
				holder.order_list_item_store_pay_status_tv.setText("已取消");
				holder.order_list_item_store_pay_status_tv.setTextColor(Color.parseColor("#000000"));
				holder.orders_product_again_btn.setVisibility(View.VISIBLE);
				holder.orders_product_verify_order_btn.setVisibility(View.GONE); 
				holder.orders_product_evaluate_btn.setVisibility(View.GONE);
				holder.orders_product_again_btn.setOnClickListener(new MyAgainListener(pos));
				break;
				default:
					holder.orders_product_express_tv.setVisibility(View.GONE);
					holder.order_list_item_bottom_layout.setVisibility(View.GONE);
					holder.order_list_item_store_pay_status_tv.setText("未知状态");
					break;
			}
			
			holder.orders_product_price_tv.setText("¥ " + String.format("%.2f", Double.parseDouble(productOrderList.get(position).get("total_price"))));

			if (-1 == isYesterday(productOrderList.get(position).get("create_date").substring(0, 10))) {
				holder.orders_product_time_tv.setText("昨天 "
						+ productOrderList.get(position).get("create_date")
								.substring(11, 16));
			} else if (0 == isYesterday(productOrderList.get(position).get(
					"create_date"))) {
				holder.orders_product_time_tv.setText("今天 "
						+ productOrderList.get(position).get("create_date")
								.substring(11, 16));
			} else {
				holder.orders_product_time_tv.setText(isTime(productOrderList
						.get(position).get("create_date")));
			}

			holder.orders_product_desc_tv.setText("由"
					+ productOrderList.get(position).get("store_name")
					+ "提供外卖和配送服务");

			return convertView;
		}

		class ViewHolder {
			Button orders_product_pay_btn;//待支付
			Button orders_product_evaluate_btn, orders_product_verify_order_btn, orders_product_again_btn;//评价、确认收货、再来一单
			RelativeLayout order_list_item_bottom_layout;//底部再来一单、确认收货、评价布局
			TextView order_list_item_store_name_tv;//店铺的名称
			TextView order_list_item_store_pay_status_tv;//订单的状态的提示文字
			TextView orders_product_price_tv, orders_product_time_tv;//订单的价格和时间
			TextView orders_product_desc_tv;//订单的简单描述
			NetworkImageView order_list_item_image_iv;//店铺的图片
			TextView orders_product_express_tv;//快递信息
		}
		
		//用来评价的接口
		class MyEvaluateListener implements OnClickListener{
			private int position ;
			public MyEvaluateListener(int pos) {
				this.position = pos;
			}
			@Override
			public void onClick(View v) {
				startActivity(new Intent(CustomerOrdersActivity.this,EvaluateOrderActivity.class).putExtra("ORDER_ID", productOrderList.get(position).get("order_id")));
			}
		}
		
		//用来收货的接口
		class MyReceiveListener implements OnClickListener{
			private int position ;
			public MyReceiveListener(int pos) {
				this.position = pos;
			}
			@Override
			public void onClick(View v) {
				orderIndex = position ;
				getOrderDetailInfo(productOrderList.get(position).get("order_id"));
			}
		}
		
		//用来再下一单的接口
		class MyAgainListener implements OnClickListener{
			private int position ;
			public MyAgainListener(int pos) {
				this.position = pos;
			}
			@Override
			public void onClick(View v) {
				Intent it = new Intent(CustomerOrdersActivity.this, SubmitOrderActivity.class);
				it.putExtra("PRODUCT_INFO", productOrderList.get(position).get("order_id"));
				it.putExtra("index", 3);
				startActivity(it);
			}
		}
		
		//用来显示支付页面的
		class PayListener implements OnClickListener{
			private int position ;
			public PayListener(int pos) {
				position = pos;
			}
			@Override
			public void onClick(View v) {
				Intent it = new Intent(CustomerOrdersActivity.this, ShowOrderPayActivity.class);
				it.putExtra("send_out_trade_no", productOrderList.get(position).get("order_id"));
				it.putExtra("send_subject", productOrderList.get(position).get("order_id"));
				it.putExtra("send_total_fee", productOrderList.get(position).get("total_price"));
				it.putExtra("storeId", productOrderList.get(position).get("store_id"));
				startActivity(it);
			}
		}
	}

	class PopAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return productsList.size();
		}
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return productsList.get(position);
		}
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(
						R.layout.order_products_list_item, null);
				holder = new ViewHolder();
				holder.order_product_list_item_count_tv = (TextView) convertView.findViewById(R.id.order_product_list_item_count_tv);
				holder.order_product_list_item_makesure_btn = (Button) convertView
						.findViewById(R.id.order_product_list_item_makesure_btn);
				holder.order_product_list_item_name = (TextView) convertView
						.findViewById(R.id.order_product_list_item_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final int pos = position;

			//商品的状态从0转到1，未收货状态 1
			if(productsList.get(position).get("status").equals("4")){
				holder.order_product_list_item_makesure_btn.setEnabled(false);
				holder.order_product_list_item_makesure_btn.setText(" 已收货 ");
				holder.order_product_list_item_makesure_btn
						.setBackgroundResource(R.drawable.login_black_btn_png);
			} else {
				holder.order_product_list_item_makesure_btn.setEnabled(true);
				holder.order_product_list_item_makesure_btn.setText("确认收货");
				holder.order_product_list_item_makesure_btn
						.setBackgroundResource(R.drawable.login_btn_png);
			} 

			holder.order_product_list_item_name.setText(productsList.get(position).get("name"));
			holder.order_product_list_item_makesure_btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					//应该使用这个方法
					updatePayStatus(pos, productsList.get(pos).get("id"));
				}
			});
			
			holder.order_product_list_item_count_tv.setText(productsList.get(position).get("count"));

			return convertView;
		}

		class ViewHolder {
			Button order_product_list_item_makesure_btn;
			TextView order_product_list_item_name, order_product_list_item_count_tv;
		}

	}
	
	PopupWindow popWindow;
	
	/**
	 * 获取订单的详情接口
	 * @param order_id 订单的id
	 */
	private void getOrderDetailInfo(String order_id){
		// url = http://182.254.161.94:8080/ydg/productOrder!getInfo?order_id=20151005171948867&client_type=A&version=1
		
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_ORDER_DETAIL_INFO + "order_id=" + order_id + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					if("SUCCESS".equals(response.getString("result"))){
						productsList.clear();
						productsList = JsonToProducts(response); 
						View parent = ((ViewGroup) CustomerOrdersActivity.this.findViewById(android.R.id.content)) .getChildAt(0);
						final View popView = getLayoutInflater().inflate( R.layout.pop_menulist, null); // 初始化视图
						menulistRight = (ListView) popView .findViewById(R.id.menulist);
						
						mAdapter = new PopAdapter();
						menulistRight.setAdapter(mAdapter);
						final int height = getResources().getDisplayMetrics().heightPixels;
						if (productsList.size() > 5) { 
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, 310);
							params.topMargin = height / 2 - 125;
							menulistRight.setLayoutParams(params); 
						} else {
							RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
							params.topMargin =  height / 2 - 125; 
							menulistRight.setLayoutParams(params); 
						}
						// 屏幕的宽高 
						int width = getResources().getDisplayMetrics().widthPixels;
						// 显示控件 
						popWindow = new PopupWindow(popView, width, height); 
						popWindow.setAnimationStyle(R.style.AnimBottom);
						popWindow.setFocusable(true);
						popWindow.setTouchable(true); // 设置可点击
						popWindow.setOutsideTouchable(true);
						ColorDrawable dw = new ColorDrawable(0x30000000);
						popWindow.setBackgroundDrawable(dw);
						popWindow .showAtLocation(parent, Gravity.CENTER, 0, 100);
						popView.setOnTouchListener(new OnTouchListener() {
							public boolean onTouch(View arg0, MotionEvent arg1) {
								int height = popView.findViewById(R.id.menulist).getTop();
								int height1 = popView.findViewById(R.id.menulist).getBottom();
								int y = (int) arg1.getY();
								if (MotionEvent.ACTION_UP == arg1.getAction()) {
									if (y < height) {
										popWindow.dismiss();
									} else if(y > height1){
										popWindow.dismiss();
									}
								}
								return true;
							}
						});
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "数据解析异常", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "访问网络错误，请重试!", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 确认订单的收货接口
	 * @param order_id 订单的id
	 */
	private void updatePayStatus(final int position, String order_item_id){
		// url = http://182.254.161.94:8080/ydg/productOrder!getInfo?order_id=20151005171948867&client_type=A&version=1
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_UPDATE_PAY_STATUS + "order_item_id=" + order_item_id + "&new_status=4" + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					if("SUCCESS".equals(response.getString("result"))){
						count +=1;
						productsList.get(position).put("status", "4");
						mAdapter.notifyDataSetChanged();
						
						if(count == productsList.size()){
//							productOrderList.get(orderIndex).put("pay_status", "5");
//							adapter.notifyDataSetChanged();
							if(popWindow != null && popWindow.isShowing()) popWindow.dismiss();
							m_currentpage = 0 ;
							productOrderList.clear();
							adapter.notifyDataSetChanged();
							getProductOrderList();
						}
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "数据解析异常", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "访问网络错误，请重试!", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}

	// 获取用户的订单列表
	private void getProductOrderList() {
		showInfoProgressDialog();
		Log.e("sss","urls ======= " + Contacts.URL_GET_CUSTOMER_ORDERS + "customer_id=" + Customer.customer_id
						+ "&page_size=" + page_size + "&page_no="
						+ m_currentpage + Contacts.URL_ENDING);
		
		//TODO 此处需要修改为用户自己的id，为了测试用的用户的id是1的数据
		JsonObjectRequest jsr = new JsonObjectRequest(
//				Contacts.URL_GET_CUSTOMER_ORDERS + "customer_id=1"
				Contacts.URL_GET_CUSTOMER_ORDERS + "customer_id=" + Customer.customer_id
						+ "&page_size=" + page_size + "&page_no="
						+ m_currentpage + Contacts.URL_ENDING, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							if ("SUCCESS".equals(response.getString("result"))) {
								productOrderList.addAll(JsonToList(response));
								if(response.has("page_model")){
									totalSize = response.getJSONObject("page_model").getInt("rowCount");
								}
							}
							adapter.notifyDataSetChanged();
							dismissInfoProgressDialog();
						} catch (JSONException e) {
							e.printStackTrace();
							dismissInfoProgressDialog();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), "访问网络错误，请重试",
								Toast.LENGTH_SHORT).show();
						dismissInfoProgressDialog();
					}
				});
		requestQueue.add(jsr);
	}
	
	/**
	 * 解析订单的详情接口
	 * @param json
	 * @return
	 */
	private List<Map<String,String>> JsonToProducts(JSONObject json){
		count = 0;
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
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("price"));
					map.put("count", "x " + ((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item")
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
					
					if(((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item")
							.get(i)).getString("status").equals("4")){
						count +=1;
					}
					list.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("sss","product message is ==== " + e.getMessage());
				list.clear();
			}
		}
		return list ;
	}

	/**
	 * 解析订单信息
	 * @param json
	 * @return
	 */
	private List<Map<String, String>> JsonToList(JSONObject json) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (json.has("data")) {
			try {
				for (int i = 0; i < json.getJSONArray("data").length(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					
					if(((JSONObject) json.getJSONArray("data").get(i)).has("express_name")){
						map.put("express_name",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("express_name"));
					}
					
					if(((JSONObject) json.getJSONArray("data").get(i)).has("express_no")){
						map.put("express_no",((JSONObject) json.getJSONArray("data").get(i))
								.getString("express_no"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("pay_online")){
						map.put("pay_online",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("pay_online"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("receive_phone")){
						map.put("receive_phone",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("receive_phone"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("receive_name")){
						map.put("receive_name",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("receive_name"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("store_id")){
						map.put("store_id", ((JSONObject) json.getJSONArray("data")
								.get(i)).getString("store_id"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("store_image")){
						map.put("store_image",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("store_image"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("order_id")){
						map.put("order_id", ((JSONObject) json.getJSONArray("data")
								.get(i)).getString("order_id"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("total_price")){
						map.put("total_price", String.valueOf(((JSONObject) json.getJSONArray("data").get(i)).getDouble("total_price")/100));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("deleted")){
						map.put("deleted", ((JSONObject) json.getJSONArray("data")
								.get(i)).getString("deleted"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("pay_status")){
						map.put("pay_status",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("pay_status"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("create_time")){
						map.put("create_date",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("create_time"));//create_date------->>>>>create_time
					}
					if (((JSONObject) json.getJSONArray("data").get(i))
							.has("customer_favor_id")) {
						map.put("customer_favor_id", ((JSONObject) json
								.getJSONArray("data").get(i))
								.getString("customer_favor_id"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("store_name")){
						map.put("store_name",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("store_name"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("customer_id")){
						map.put("customer_id",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("customer_id"));
					}
					if(((JSONObject) json.getJSONArray("data").get(i)).has("receive_addr")){
						map.put("receive_addr",
								((JSONObject) json.getJSONArray("data").get(i))
										.getString("receive_addr"));
					}
					list.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("sss", "the json exception is === " + e.getMessage());
				list.clear();
			}
		}
		return list;
	}

	@Override
	public void onRefresh() {
		productOrderList.clear();
		adapter.notifyDataSetChanged();
		this.m_currentpage = 0;
		getProductOrderList();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				swipeLayout.setRefreshing(true);
				swipeLayout.setRefreshing(false);
			}
		}, 3000);
	}

	@Override
	public void onLoadMore() {
		showNextPage();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 1000);
	}

	// 加载更多的方法
	private void showNextPage() {
		if (totalSize != productOrderList.size()) {
			if (NetworkUtils.getNetworkState(this) != NetworkUtils.NETWORN_NONE) {
				this.m_currentpage++;
				getProductOrderList();
			} else {
				Toast.makeText(this, "无法连接网络", Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(this, "已加载全部数据", Toast.LENGTH_SHORT).show();
		}
	}

	// 用来关闭加载和刷新视图
	private void onLoad() {
		listView.stopLoadMore();
		listView.stopRefresh();
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

	@Override
	public void onClick(View v) {
		finish();
	}
	
	public static final String ORDER_REFRESH_ACTION = "yiku.customer.order.refresh.action";
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(ORDER_REFRESH_ACTION.equals(intent.getAction())) {
				productOrderList.clear();
				m_currentpage = 0;
				getProductOrderList();
			}
		}
	};
}
