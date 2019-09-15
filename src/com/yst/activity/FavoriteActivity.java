package com.yst.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的收藏界面
 * @author lixiangchao
 *
 */
public class FavoriteActivity extends Activity implements OnRefreshListener, OnClickListener,IXListViewListener{
	
	// 用来刷新的
	private SwipeRefreshLayout swipeLayout;
	// 用来显示数据列表数据的
	private XListView listView;
//	private ArrayList<String> list;
	private MyAdapter adapter;
	private int count = 0;
	
	private View favorite_no_list_view ;
	
	private List<HashMap<String,String>> favoritList ;
	
	private ImageView activity_back_iv ;
	private TextView activity_title_tv ;
	
	//用来访问网络
	private RequestQueue requestQueue ;
	private ImageLoader imageLoader ;
	private MProcessDialog mInfoProgressDialog;
	
	private Handler mHandler;
	private int page_size = 10;
	private int totalSize = 0;
	private int m_currentpage = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		requestQueue = Volley.newRequestQueue(this);
		
		initView();
	}
	
	// 初始化控件
	private void initView(){
		favoritList = new ArrayList<HashMap<String,String>>();
		mHandler = new Handler();
		imageLoader = new ImageLoader(requestQueue, new BitmapCache());
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_favorite_label);
		
		favorite_no_list_view = findViewById(R.id.favorite_no_list_view);
		
//		favorite_no_list_view.setVisibility(View.VISIBLE);
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe1);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
		swipeLayout.setVisibility(View.VISIBLE);
		
		listView = (XListView) findViewById(R.id.list1);
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(false);
		listView.setXListViewListener(this);
		swipeLayout.setOnRefreshListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it = new Intent(FavoriteActivity.this, YiDianStoreGoodsActivity.class);
				it.putExtra("type", true);
				//店铺的id
				it.putExtra("store_id", favoritList.get(position - 1).get("store_id"));
				//店铺的名字
				it.putExtra("store_name", favoritList.get(position - 1).get("name"));
				//店铺的起送价
				if(favoritList.get(position - 1).get("send_low_price") != null){
					it.putExtra("init_price", Double.parseDouble(favoritList.get(position - 1).get("send_low_price")));
				} else {
					it.putExtra("init_price", 0);
				}
				//店铺的配送费
				if(favoritList.get(position - 1).get("send_fee") != null){
					it.putExtra("send_fee", Double.parseDouble(favoritList.get(position - 1).get("send_fee")));
				} else {
					it.putExtra("send_fee", 0);
				}
				//店铺的logo
				it.putExtra("store_image", favoritList.get(position - 1).get("store_image"));
				//店铺的类型 1是易店 其它是易库
				it.putExtra("is_yd", favoritList.get(position - 1).get("is_yd"));
				startActivity(it);
				
			}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(favoritList.size() == 0) return true;
				checkUpdate(position - 1, favoritList.get(position - 1).get("id"));
				return false;
			}
		});
		getMarketList();
	}
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return favoritList.size();
		}

		@Override
		public Object getItem(int position) {
			return favoritList.get(position);
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
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.favorite_list_item, null);
				holder = new ViewHolder();
				holder.favorite_list_xiaoshou_tv = (TextView) convertView.findViewById(R.id.favorite_list_xiaoshou_tv);
				holder.favorite_list_item_bottom_layout = (LinearLayout) convertView.findViewById(R.id.favorite_list_item_bottom_layout);
				holder.favorite_list_item_bottom_desc_tv = (TextView) convertView.findViewById(R.id.favorite_list_item_bottom_desc_tv);
				holder.favorite_product_image_iv = (NetworkImageView) convertView.findViewById(R.id.favorite_product_image_iv);
				holder.favorite_product_name_tv = (TextView) convertView.findViewById(R.id.favorite_product_name_tv);
				holder.ratingBar1 = (RatingBar) convertView.findViewById(R.id.ratingBar1);
				holder.favorite_product_qisongjia_tv = (TextView) convertView.findViewById(R.id.favorite_product_qisongjia_tv);
				holder.favorite_product_peisongfei_tv = (TextView) convertView.findViewById(R.id.favorite_product_peisongfei_tv);
				holder.favorite_product_peisongshijian_tv = (TextView) convertView.findViewById(R.id.favorite_product_peisongshijian_tv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.favorite_product_image_iv.setErrorImageResId(R.drawable.app_default_icon);
			holder.favorite_product_image_iv.setDefaultImageResId(R.drawable.app_default_icon);
			if(favoritList.get(position).get("image") != null && favoritList.get(position).get("image").endsWith(".jpg")){
				holder.favorite_product_image_iv.setImageUrl("http://182.254.161.94:8080/ydg/" + favoritList.get(position).get("image"), imageLoader);
			} else {
				holder.favorite_product_image_iv.setImageUrl("http://182.254.161.94", imageLoader);
			}
			if(favoritList.get(position).get("sales_sum") != null){
				holder.favorite_list_xiaoshou_tv.setText("月销售" + favoritList.get(position).get("sales_sum") + "单");
			} else {
				holder.favorite_list_xiaoshou_tv.setText("月销售0单");
			}
			
			holder.favorite_product_name_tv.setText(favoritList.get(position).get("name"));
			if(favoritList.get(position).get("send_low_price") != null) {
				holder.favorite_product_qisongjia_tv.setText("起送价 ¥" + favoritList.get(position).get("send_low_price"));
			} else {
				holder.favorite_product_qisongjia_tv.setText("起送价 ¥0");
			}
			
			if(favoritList.get(position).get("send_fee") != null) {
				holder.favorite_product_peisongfei_tv.setText("配送费 ¥" + favoritList.get(position).get("send_fee"));
			} else {
				holder.favorite_product_peisongfei_tv.setText("配送费 ¥0");
			}
			if(favoritList.get(position).get("send_time") != null) {
				holder.favorite_product_peisongshijian_tv.setText("配送时间 " + favoritList.get(position).get("send_time"));
			} else {
				holder.favorite_product_peisongshijian_tv.setText("配送时间暂无");
			}
			
			if(favoritList.get(position).get("array_size") != null){
				Log.e("sss", "array size is === " + favoritList.get(position).get("array_size"));
				
				int count = Integer.parseInt(favoritList.get(position).get("array_size"));
				if(count > 0){
					holder.favorite_list_item_bottom_layout.setVisibility(View.VISIBLE);
					for(int i = 0 ;i<count;i++){
						if(i == 0){
							holder.favorite_list_item_bottom_desc_tv.setText("满" + favoritList.get(position).get("use_condition0") + "减" + favoritList.get(position).get("favor_money0"));
						} else {
							holder.favorite_list_item_bottom_desc_tv.append(";满" + favoritList.get(position).get("use_condition" + i) + "减" + favoritList.get(position).get("favor_money" + i));
						}
						
						if(i == count-1){
							holder.favorite_list_item_bottom_desc_tv.append("(在线支付专享)");
						}
					}
				} else {
					holder.favorite_list_item_bottom_layout.setVisibility(View.GONE);
				}
			} else {
				holder.favorite_list_item_bottom_layout.setVisibility(View.GONE);
			}
			
			return convertView;
		}
		
		class ViewHolder{
			LinearLayout favorite_list_item_bottom_layout; //是否显示有优惠活动
			TextView favorite_list_item_bottom_desc_tv; //优惠活动的描述信息
			NetworkImageView favorite_product_image_iv; //图片资源显示
			TextView favorite_product_name_tv; //店铺的名称
			RatingBar ratingBar1; //店铺的评分
			TextView favorite_product_qisongjia_tv; //店铺的起送价格描述
			TextView favorite_product_peisongfei_tv; //店铺的配送费描述
			TextView favorite_product_peisongshijian_tv; //店铺的配送时间描述
			TextView favorite_list_xiaoshou_tv; //店铺的月销售/单
		}

	}
	

	@Override
	public void onRefresh() {
		favoritList.clear();
		this.m_currentpage = 0 ;
		if(adapter != null) adapter.notifyDataSetChanged();
		getMarketList();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				swipeLayout.setRefreshing(true);
				swipeLayout.setRefreshing(false);
			}
		}, 3000);
	}

	@Override
	public void onClick(View v) {
		finish();
	}
	
	//获取用户的收藏店铺信息列表
	private void getMarketList(){
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_MARKER_LIST + "customer_id=" + Customer.customer_id + "&page_size="
//		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_MARKER_LIST + "customer_id=" + 1 + "&page_size="
				+ page_size + "&page_no=" + m_currentpage + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					if("SUCCESS".equals(response.getString("result"))){
						
						Log.e("sss","response to string is " + response.toString());
						
						if(response.has("page_model")){
							totalSize = response.getJSONObject("page_model").getInt("rowCount");
						}
						
						favoritList.addAll(JsonToList(response));
						adapter = new MyAdapter();
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "店铺列表信息解析异常", Toast.LENGTH_SHORT).show();
					dismissInfoProgressDialog();
				}
				
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "访问网络 失败，请重试!", Toast.LENGTH_SHORT).show();
				dismissInfoProgressDialog();
			}
		});
		requestQueue.add(jsr);
	}
	
	//用来解析收藏列表
	private List<HashMap<String, String>> JsonToList(JSONObject json){
		List<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
		if(null == json){
			return list ;
		} else {
			if(json.has("data")){
				JSONArray tarray;
				try {
					tarray = json.getJSONArray("data");
					for(int i = 0 ; i < tarray.length() ; i++){
						HashMap<String,String> map = new HashMap<String,String>();
						if(((JSONObject)tarray.get(i)).has("region")){
							map.put("region", ((JSONObject)tarray.get(i)).getString("region"));
						}
						if(((JSONObject)tarray.get(i)).has("phone")){
							map.put("phone", ((JSONObject)tarray.get(i)).getString("phone"));
						}
						if(((JSONObject)tarray.get(i)).has("prov")){
							map.put("prov", ((JSONObject)tarray.get(i)).getString("prov"));
						}
						if(((JSONObject)tarray.get(i)).has("login_code")){
							map.put("login_code", ((JSONObject)tarray.get(i)).getString("login_code"));
						}
						if(((JSONObject)tarray.get(i)).has("store_id")){
							map.put("store_id", ((JSONObject)tarray.get(i)).getString("store_id"));
						}
						if(((JSONObject)tarray.get(i)).has("lng")){
							map.put("lng", ((JSONObject)tarray.get(i)).getString("lng"));
						}
						if(((JSONObject)tarray.get(i)).has("send_free_condition")){
							map.put("send_free_condition", String.valueOf(((JSONObject)tarray.get(i)).getDouble("send_free_condition") / 100));
						}
						if(((JSONObject)tarray.get(i)).has("store_type")){
							map.put("store_type", ((JSONObject)tarray.get(i)).getString("store_type"));
						}
						if(((JSONObject)tarray.get(i)).has("send_fee")){
							map.put("send_fee", String.valueOf(((JSONObject)tarray.get(i)).getDouble("send_fee") / 100));
						}
						if(((JSONObject)tarray.get(i)).has("short_desc")){
							map.put("short_desc", ((JSONObject)tarray.get(i)).getString("short_desc"));
						}
						if(((JSONObject)tarray.get(i)).has("deleted")){
							map.put("deleted", ((JSONObject)tarray.get(i)).getString("deleted"));
						}
						if(((JSONObject)tarray.get(i)).has("city")){
							map.put("city", ((JSONObject)tarray.get(i)).getString("city"));
						}
						if(((JSONObject)tarray.get(i)).has("send_low_price")){
							map.put("send_low_price", String.valueOf(((JSONObject)tarray.get(i)).getDouble("send_low_price") / 100));
						}
						if(((JSONObject)tarray.get(i)).has("id")){
							map.put("id", ((JSONObject)tarray.get(i)).getString("id"));
						}
						if(((JSONObject)tarray.get(i)).has("send_time")){
							map.put("send_time", ((JSONObject)tarray.get(i)).getString("send_time"));
						}
						if(((JSONObject)tarray.get(i)).has("sales_sum")){
							map.put("sales_sum", ((JSONObject)tarray.get(i)).getString("sales_sum"));
						}
						if(((JSONObject)tarray.get(i)).has("is_yd")){
							map.put("is_yd", ((JSONObject)tarray.get(i)).getString("is_yd"));
						}
						if(((JSONObject)tarray.get(i)).has("address")){
							map.put("address", ((JSONObject)tarray.get(i)).getString("address"));
						}
						if(((JSONObject)tarray.get(i)).has("name")){
							map.put("name", ((JSONObject)tarray.get(i)).getString("name"));
						}
						if(((JSONObject)tarray.get(i)).has("add_time")){
							map.put("add_time", ((JSONObject)tarray.get(i)).getString("add_time"));
						}
						if(((JSONObject)tarray.get(i)).has("customer_id")){
							map.put("customer_id", ((JSONObject)tarray.get(i)).getString("customer_id"));
						}
						if(((JSONObject)tarray.get(i)).has("lat")){
							map.put("lat", ((JSONObject)tarray.get(i)).getString("lat"));
						}
						if(((JSONObject)tarray.get(i)).has("image")){
							map.put("image", ((JSONObject)tarray.get(i)).getString("image"));
						}
						if(((JSONObject)tarray.get(i)).has("store_favor")){
							JSONArray array = ((JSONObject)tarray.get(i)).getJSONArray("store_favor");
							map.put("array_size", String.valueOf(array.length()));
							for(int k = 0 ;k<array.length() ;k++){
								map.put("favor_money" + k, String.valueOf(((JSONObject)array.get(k)).getDouble("favor_money") / 100));
								map.put("use_condition" + k, String.valueOf(((JSONObject)array.get(k)).getDouble("use_condition") / 100));
							}
						}
						list.add(map);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("sss","json to list exception message is === " + e.getMessage());
				}
			}
		}
		
		return list ;
	}
	
	//界面等待条显示
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

	//界面等待条关闭
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
	
	//加载更多的方法
	private void showNextPage() {
		if (totalSize != favoritList.size()) {
			if (NetworkUtils.getNetworkState(this) != NetworkUtils.NETWORN_NONE) {
				this.m_currentpage++;
				getMarketList();
			} else {
				Toast.makeText(FavoriteActivity.this, "无法连接网络",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(FavoriteActivity.this, "已加载全部数据",
					Toast.LENGTH_SHORT).show();
		}
	}
	
	// 用来关闭加载和刷新视图
	private void onLoad() {
		listView.stopLoadMore();
		listView.stopRefresh();
	}
	
	
	private void checkUpdate(final int pos, final String id) {
		final AlertDialog builder = new Builder(this).create();
		// 设置删除提示标题
		builder.setTitle("温馨提示");
		// 设置删除提示的标语
		builder.setMessage("确定要删除收藏的店铺吗？");
		// 设置外部点击事件不能消失
//		builder.setCanceledOnTouchOutside(false);
		// 设置取消按钮及点击事件
		builder.setButton("点错了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				builder.dismiss();
			}
		});
		// 设置更新按钮及点击事件
		builder.setButton2("狠心删除", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				markerCRUD(pos, id);
				builder.dismiss();
			}
		});
		// 对话框显示
		builder.show();
	}
	
	private void markerCRUD(final int pos, final String id){
		showInfoProgressDialog();
		//http://182.254.161.94:8080/ydg/customerMarker!markerCRUD?customer_id=3&action=3&client_type=A&version=1&id=1
//		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_MARKER_LIST + "customer_id=" + Customer.customer_id + "&id=" + id + "&action=3" + Contacts.URL_ENDING
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_MARKER_CRUD + "customer_id=" + Customer.customer_id + "&id=" + id + "&action=3" + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					if("SUCCESS".equals(response.getString("result"))){
						favoritList.remove(pos);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "店铺删除信息解析异常", Toast.LENGTH_SHORT).show();
					dismissInfoProgressDialog();
				}
				
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "访问网络 失败，请重试!", Toast.LENGTH_SHORT).show();
				dismissInfoProgressDialog();
			}
		});
		requestQueue.add(jsr);
	}
	
}
