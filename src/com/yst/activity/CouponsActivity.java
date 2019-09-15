package com.yst.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
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
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 优惠券界面
 * @author lixiangchao
 *
 */
public class CouponsActivity extends Activity implements OnRefreshListener, OnClickListener, IXListViewListener{
	
	private SwipeRefreshLayout swipeLayout;
	private XListView listView;
	private ArrayList<String> list;
	private MyAdapter adapter;
	private int count = 0;
	
	private RelativeLayout coupons_no_list_view ;
	private ImageView no_list_tips_iv ;
	
	private ImageView activity_back_iv ;
	private TextView activity_title_tv ;
	
	private RequestQueue requestQueue ;
	private List<Map<String,String>> couponsList ;
	private ImageLoader imageLoader ;
	private MProcessDialog mInfoProgressDialog;
	
	private Handler mHandler;
	private int page_size = 15;
	private int totalSize = 0;
	private int m_currentpage = 0;
	
	private boolean canUse = false ;
	private double total_price = 0 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coupons);
		
		
		canUse = getIntent().getBooleanExtra("ISORDER", false);
		total_price = getIntent().getDoubleExtra("total_price", 0);
		
//		initData();
		initView();
		
		getFavorList();
	}
	
	//初始化控件
	private void initView(){
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_coupons_label);
		
		requestQueue = Volley.newRequestQueue(this);
		couponsList = new ArrayList<Map<String,String>>();
		mHandler = new Handler();
		imageLoader = new ImageLoader(requestQueue, new BitmapCache());
		
		coupons_no_list_view = (RelativeLayout)findViewById(R.id.coupons_no_list_view);
		no_list_tips_iv = (ImageView) findViewById(R.id.no_list_tips_iv1);
		no_list_tips_iv.setOnClickListener(this);
//		coupons_no_list_view.setVisibility(View.VISIBLE);
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
//		swipeLayout.setVisibility(View.VISIBLE);
		
		listView = (XListView) findViewById(R.id.list);
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
				//这里是用来操作点击事件的
				if(!canUse) return ; // 如果是用户中心跳转过来的，就直接返回不操作
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String str = formatter.format(curDate);
				
				String start_time = couponsList.get(position - 1).get("period_start_data");
				String end_time = couponsList.get(position - 1).get("period_end_data");
				
				int i = compare_date(str, start_time);
				int i1 = compare_date(str, end_time);
				
				
				if(i <= 0 &&  i1 >= 0) {
					System.out.println("可以使用券");
				} else {
					System.out.println("不可以使用券");
				}
				if(couponsList.get(position - 1).get("is_used").equals("1")){
					Toast.makeText(CouponsActivity.this, "该优惠券已使用", Toast.LENGTH_SHORT).show();
					return;
				}else if(i <= 0 &&  i1 >= 0) {
					Intent intent = new Intent();  
					String use_condition = couponsList.get(position - 1).get("use_condition");
					Log.i("sss", "目前总价 == " + total_price);
					Log.i("sss", "条件 == " + use_condition);
					Log.i("sss", "满足条件优惠 == " + Double.parseDouble(couponsList.get(position - 1).get("favor_money")));
					if(use_condition != null) {
						if(total_price >= Double.parseDouble(use_condition) / 100){// 大于满减的条件
							intent.putExtra("favor_money", Double.parseDouble(couponsList.get(position - 1).get("favor_money")));
							intent.putExtra("id", couponsList.get(position - 1).get("id"));
						}
					}
					setResult(110,intent);
					finish();
				}else{
					Toast.makeText(CouponsActivity.this, "该优惠券已过期", Toast.LENGTH_SHORT).show();
					return;
				}
				
			}
		});
		
		swipeLayout.setOnRefreshListener(this);
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return couponsList.size();
		}

		@Override
		public Object getItem(int position) {
			return couponsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder = null ;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.coupons_list_item, null);
				holder.coupons_money_iv = (LinearLayout) convertView.findViewById(R.id.coupons_money_iv);
				holder.coupons_date_text_view = (TextView) convertView.findViewById(R.id.coupons_date_text_view);
				holder.coupons_price_text_view = (TextView) convertView.findViewById(R.id.coupons_price_text_view);
				holder.couponse_use_config_tv = (TextView) convertView.findViewById(R.id.couponse_use_config_tv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.coupons_price_text_view.setText(String.valueOf((int)Double.parseDouble(couponsList.get(position).get("favor_money"))));
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String str = formatter.format(curDate);
			
			String start_time = couponsList.get(position).get("period_start_data");
			String end_time = couponsList.get(position).get("period_end_data");
			
			int i = compare_date(str, start_time);
			int i1 = compare_date(str, end_time);
			
			holder.couponse_use_config_tv.setText("满" + (int)(Double.parseDouble(couponsList.get(position).get("use_condition")) / 100) + "可用");
			
			if(i <= 0 &&  i1 >= 0) {
				System.out.println("可以使用券");
				holder.coupons_date_text_view.setText("有效期至：" + couponsList.get(position).get("period_end_data").substring(0, 11));
				holder.coupons_money_iv.setBackgroundResource(getMoney(Integer.parseInt(couponsList.get(position).get("is_used"))));
			} else {
				System.out.println("不可以使用券");
				holder.coupons_date_text_view.setText("有效期：" + couponsList.get(position).get("period_start_data").substring(0, 11) + " - " + couponsList.get(position).get("period_end_data").substring(0, 11));
				holder.coupons_money_iv.setBackgroundResource(R.drawable.coupons_time_out);
			}
			
			return convertView;
		}
		
		class ViewHolder{
			LinearLayout coupons_money_iv ;
			TextView coupons_date_text_view;
			TextView coupons_price_text_view;
			TextView couponse_use_config_tv;
		}
	}
	
	public int compare_date(String DATE1, String DATE2) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				System.out.println("dt1在dt2后");
				return -1;
			} else if (dt1.getTime() < dt2.getTime()) {
				System.out.println("dt1 在dt2前");
				return 1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}
	
	//用来显示是那张图
	private int getMoney(int money, int used){
		int tmoney = R.drawable.coupons_ten ;
		switch(money){
		case 10:
			tmoney = used == 0 ? R.drawable.coupons_ten : R.drawable.coupons_ten_used;
			break;
		case 20:
			tmoney = used == 0 ? R.drawable.coupons_twe : R.drawable.coupons_twe_used;
			break;
		case 30:
			tmoney = used == 0 ? R.drawable.coupons_thr : R.drawable.coupons3_thr_used;
			break;
		case 50:
			tmoney = used == 0 ? R.drawable.coupons_fiv : R.drawable.coupons_fiv_used;
			break;
		default:
			break;
		}
		return tmoney ;
	}
	
	//用来显示是那张图
	private int getMoney(int used){
		return used == 0 ? R.drawable.coupons_can_use : R.drawable.coupons_used;
	}

	@Override
	public void onRefresh() {
		couponsList.clear();
		this.m_currentpage = 0 ;
		if(adapter != null) adapter.notifyDataSetChanged();
		getFavorList();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				swipeLayout.setRefreshing(true);
				adapter.notifyDataSetChanged();
				swipeLayout.setRefreshing(false);
			}
		}, 3000);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.activity_back_iv:
			finish();
			break;
		case R.id.no_list_tips_iv1:
			couponsList.clear();
			m_currentpage = 0 ;
			getFavorList();
			break;
		}
	}
	
	// 获取用户的优惠券列表
	private void getFavorList(){
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_FAVOR_LIST + "customer_id=" + Customer.customer_id + "&page_size="
				+ page_size + "&page_no=" + m_currentpage + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					
					Log.e("sss", " the coupons response is === " + response.toString());
					
					if(response.getString("result").equals("SUCCESS")){
						totalSize = response.getJSONObject("page_model").getInt("rowCount");
						couponsList.addAll(JsonToList(response));
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
					
					if(totalSize == 0 ){
						coupons_no_list_view.setVisibility(View.VISIBLE);
						swipeLayout.setVisibility(View.GONE);
					} else {
						coupons_no_list_view.setVisibility(View.GONE);
						swipeLayout.setVisibility(View.VISIBLE);
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				swipeLayout.setVisibility(View.GONE);
				coupons_no_list_view.setVisibility(View.VISIBLE);
			}
		});
		requestQueue.add(jsr);
	}

	
	//用来解析收藏列表
	private List<HashMap<String, String>> JsonToList(JSONObject json) {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		// "id":1,"is_used":0,"favor_money":100,"use_condition":0,"customer_id":1
		if (null == json) {
			return list;
		} else {
			if (json.has("data")) {
				JSONArray tarray;
				try {
					tarray = json.getJSONArray("data");
					for (int i = 0; i < tarray.length(); i++) {
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("id",
								((JSONObject) tarray.get(i)).getString("id"));
						map.put("is_used", ((JSONObject) tarray.get(i))
								.getString("is_used"));
						map.put("favor_money", String.valueOf((int)((JSONObject) tarray.get(i))
								.getDouble("favor_money") / 100));
						map.put("use_condition", ((JSONObject) tarray.get(i))
								.getString("use_condition"));
						map.put("customer_id", ((JSONObject) tarray.get(i))
								.getString("customer_id"));
						map.put("period_start_data", ((JSONObject) tarray.get(i))
								.getString("period_start_data"));
						map.put("period_end_data", ((JSONObject) tarray.get(i))
								.getString("period_end_data"));
						list.add(map);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("sss",
							"json to list exception message is === "
									+ e.getMessage());
				}
			}
		}

		return list;
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
	public void onLoadMore() {
		// TODO Auto-generated method stub
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
		if (totalSize != couponsList.size()) {
			if (NetworkUtils.getNetworkState(this) != NetworkUtils.NETWORN_NONE) {
				this.m_currentpage++;
				getFavorList();
			} else {
				Toast.makeText(CouponsActivity.this, "无法连接网络",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(CouponsActivity.this, "已加载全部数据", Toast.LENGTH_SHORT)
					.show();
		}
	}

	// 用来关闭加载和刷新视图
	private void onLoad() {
		listView.stopLoadMore();
		listView.stopRefresh();
	}
}
