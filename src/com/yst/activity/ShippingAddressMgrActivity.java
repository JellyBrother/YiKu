package com.yst.activity;

import java.util.ArrayList;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.guohua.common.util.NetworkUtils;
import com.jack.ui.MProcessDialog;
import com.jack.ui.XListView;
import com.jack.ui.XListView.IXListViewListener;
import com.yst.yiku.R;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 地址管理界面
 * @author lixiangchao
 *
 */
public class ShippingAddressMgrActivity extends Activity implements OnRefreshListener, OnClickListener, IXListViewListener{
	
	private ImageView activity_back_iv ;
	private TextView activity_title_tv ;
	
	private TextView shipping_add_address_tv;
	private RelativeLayout shipping_add_address_bottom ;
	
	private SwipeRefreshLayout swipeLayout ;

	private XListView listView;
	private MyAdapter adapter;
	
	private List<Map<String, String>> provinceMapList = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> cityMapList = new ArrayList<Map<String, String>>();
	public static List<HashMap<String,Object>> addressList ;
	public static List<HashMap<String,Object>> tempList = new ArrayList<HashMap<String,Object>>();
	private RequestQueue requestQueue ;
	private MProcessDialog mInfoProgressDialog;
	
	private SharedPreferences msp;
	private Handler mHandler;
	private int page_size = 10;
	private int totalSize = 0;
	private int m_currentpage = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shipping_address_mgr);
		
		initView();
	}
	
	//初始化控件方法
	private void initView(){
		requestQueue =  Volley.newRequestQueue(this);
		mHandler = new Handler();
		msp = getSharedPreferences("yiku", MODE_PRIVATE);
		addressList = new ArrayList<HashMap<String,Object>>();
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_address_label);
		
		shipping_add_address_tv = (TextView) findViewById(R.id.shipping_add_address_tv);
		shipping_add_address_bottom = (RelativeLayout) findViewById(R.id.shipping_add_address_bottom);
		shipping_add_address_tv.setOnClickListener(this);
		shipping_add_address_bottom.setOnClickListener(this);
		
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe3);
		swipeLayout.setColorScheme(R.color.holo_blue_bright, R.color.holo_green_light, R.color.holo_orange_light, R.color.holo_red_light);
		swipeLayout.setVisibility(View.VISIBLE);
		
		listView = (XListView) findViewById(R.id.list3);
		listView.setPullLoadEnable(true);
		listView.setPullRefreshEnable(false);
		listView.setXListViewListener(this);
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				for(int i = 0 ; i< addressList.size() ; i++){
					addressList.get(i).put("flag", false);
				}
				
				saveAddress((String)addressList.get(position - 1).get("id"));
				
				addressList.get(position - 1).put("flag", true);
				adapter.notifyDataSetChanged();
			}
		});
		swipeLayout.setOnRefreshListener(this);
		getProv();
		getCity();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		addressList.clear();
		m_currentpage = 0 ;
		adapter.notifyDataSetChanged();
		getAddressList();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.activity_back_iv:
			finish();
			break;
		case R.id.shipping_add_address_tv:
		case R.id.shipping_add_address_bottom:
			startActivity(new Intent(this, ShippingAddressActivity.class).putExtra("FLAG", 1));
			break;
		}
	}


	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return addressList.size();
		}

		@Override
		public Object getItem(int position) {
			return addressList.get(position);
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
						.inflate(R.layout.address_mgr_list_item, null);
				holder = new ViewHolder();
				holder.service_package_address_list_item_name = (TextView) convertView.findViewById(R.id.service_package_address_list_item_name);
				holder.service_package_address_list_item_phone = (TextView) convertView.findViewById(R.id.service_package_address_list_item_phone);
				holder.service_package_address_list_item_shengshiqu = (TextView) convertView.findViewById(R.id.service_package_address_list_item_shengshiqu);
				holder.service_package_address_list_item_shiqu = (TextView) convertView.findViewById(R.id.service_package_address_list_item_shiqu);
				holder.service_package_address_list_item_jiedao = (TextView) convertView.findViewById(R.id.service_package_address_list_item_jiedao);
				holder.service_package_address_list_item_jiebian = (TextView) convertView.findViewById(R.id.service_package_address_list_item_jiebian);
				holder.service_package_address_list_item = (ImageView) convertView.findViewById(R.id.service_package_address_list_item);
				holder.service_package_address_list_update = (LinearLayout) convertView.findViewById(R.id.service_package_address_list_update);
				holder.service_package_address_list_delete = (LinearLayout) convertView.findViewById(R.id.service_package_address_list_delete);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final int pos = position ;
			
			if((String)addressList.get(position).get("prov_id") != null && !TextUtils.isEmpty(addressList.get(position).get("prov_id").toString())) {
				if(addressList.get(position).get("prov_id").toString().equals("0")) {
					holder.service_package_address_list_item_shengshiqu.setVisibility(View.GONE);
					holder.service_package_address_list_item_shengshiqu.setText("");
				} else {
					holder.service_package_address_list_item_shengshiqu.setVisibility(View.VISIBLE);
					holder.service_package_address_list_item_shengshiqu.setText(getProvName((String)addressList.get(position).get("prov_id")));
				}
				if((String)addressList.get(position).get("city_id") != null && !TextUtils.isEmpty(addressList.get(position).get("city_id").toString())) {
					if(addressList.get(position).get("city_id").toString().equals("0")) {
						holder.service_package_address_list_item_shiqu.setVisibility(View.GONE);
						holder.service_package_address_list_item_shiqu.setText("");
					} else {
						holder.service_package_address_list_item_shiqu.setVisibility(View.VISIBLE);
						holder.service_package_address_list_item_shiqu.setText(getCityName(addressList.get(position).get("city_id").toString()));
					}
				} else {
					holder.service_package_address_list_item_shiqu.setVisibility(View.GONE);
					holder.service_package_address_list_item_shiqu.setText("");
				}
			} else {
				holder.service_package_address_list_item_shengshiqu.setVisibility(View.GONE);
				holder.service_package_address_list_item_shengshiqu.setText("");
			}
			
			
			holder.service_package_address_list_item_name.setText((String)addressList.get(position).get("name"));
			holder.service_package_address_list_item_phone.setText((String)addressList.get(position).get("phone"));
			holder.service_package_address_list_item_jiedao.setText((String)addressList.get(position).get("address"));
			holder.service_package_address_list_item_jiebian.setText((String)addressList.get(position).get("zip_code"));
			if((Boolean)addressList.get(position).get("flag")){
				holder.service_package_address_list_item.setImageResource(R.drawable.address_ok);
			} else {
				holder.service_package_address_list_item.setImageResource(R.drawable.address_no);
			}
			holder.service_package_address_list_update.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent it = new Intent(ShippingAddressMgrActivity.this, ShippingAddressActivity.class);
					Bundle extras = new Bundle();
					extras.putSerializable("maps", addressList.get(pos));
					it.putExtras(extras);
					startActivity(it);
				}
			});
			holder.service_package_address_list_delete.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					final AlertDialog builder = new AlertDialog.Builder(ShippingAddressMgrActivity.this).create();
					builder.setTitle("提示");
					builder.setMessage("确定要删除该记录吗?");
					builder.setButton("取消", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							builder.dismiss();
						}
					});
					builder.setButton2("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							builder.dismiss();
							deleteAddress(pos, (String)addressList.get(pos).get("id"));
//							notifyDataSetChanged();
						}
					});
					builder.show();
				}
			});
			
			return convertView;
		}
		
		class ViewHolder{
			ImageView service_package_address_list_item ;
			TextView service_package_address_list_item_name, service_package_address_list_item_phone, service_package_address_list_item_shengshiqu ;
			TextView service_package_address_list_item_jiedao, service_package_address_list_item_jiebian ;
			LinearLayout service_package_address_list_update, service_package_address_list_delete ;
			TextView service_package_address_list_item_shiqu;
		}

	}

	@Override
	public void onRefresh() {
		addressList.clear();
		m_currentpage = 0 ;
		if(adapter != null) adapter.notifyDataSetChanged();
		getAddressList();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				swipeLayout.setRefreshing(true);
				adapter.notifyDataSetChanged();
				swipeLayout.setRefreshing(false);
			}
		}, 3000);
	}
	
	/**
	 * 获取用户的收货地址
	 */
	private void getAddressList(){ // URL_GET_ADDRESS_LIST
		tempList.clear();
		showInfoProgressDialog();
		
		Log.e("sss",Contacts.URL_GET_ADDRESS_LIST + "customer_id=" + Customer.customer_id + Contacts.URL_ENDING);
		
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_ADDRESS_LIST + "customer_id=" + Customer.customer_id+ "&page_size="
				+ page_size + "&page_no=" + m_currentpage + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					
					Log.e("sss","shipping address mgr list ====" + response.toString());
					
					if(response.getString("result").equals("SUCCESS")){
						
						if(response.has("page_model")){
							totalSize = response.getJSONObject("page_model").getInt("rowCount");
						}
						
						JSONArray tarray = response.getJSONArray("data");
						if(tarray.length() > 0){
							for(int i = 0 ;i< tarray.length() ;i++){
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("id", ((JSONObject)tarray.get(i)).getString("id"));
								map.put("phone", ((JSONObject)tarray.get(i)).getString("phone"));
								map.put("zip_code", ((JSONObject)tarray.get(i)).getString("zip_code"));
								map.put("address", ((JSONObject)tarray.get(i)).getString("address"));
								map.put("name", ((JSONObject)tarray.get(i)).getString("name"));
								if(((JSONObject)tarray.get(i)).has("prov_id")) {
									map.put("prov_id", ((JSONObject)tarray.get(i)).getString("prov_id"));
								}
								if(((JSONObject)tarray.get(i)).has("city_id")) {
									map.put("city_id", ((JSONObject)tarray.get(i)).getString("city_id"));
								}
								if(!TextUtils.isEmpty(Customer.address_id)){
									if(Customer.address_id.equals(((JSONObject)tarray.get(i)).getString("id"))){
										map.put("flag", true);
									} else {
										map.put("flag", false);
									}
								} else {
									map.put("flag", false);
								}
								tempList.add(map);
							}
							addressList.addAll(tempList);
						} else {
							Toast.makeText(getApplicationContext(), "暂无收货地址列表", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
					dismissInfoProgressDialog();
					Toast.makeText(getApplicationContext(), "地址列表信息解析异常!", Toast.LENGTH_SHORT).show();
				}
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(getApplicationContext(), "网络请求失败，请重试!", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 保存默认地址
	 */
	private void saveAddress(final String id){
		
		Log.e("sss", Contacts.URL_SAVE_ADDRESS + "customer_id=" + Customer.customer_id + "&id=" + id + Contacts.URL_ENDING);
		
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_SAVE_ADDRESS + "customer_id=" + Customer.customer_id + "&id=" + id + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				String success;
				try {
					success = response.getString("result");
					if("SUCCESS".equals(success)){
						Customer.address_id = id ;
						msp.edit().putString("address_id", id).commit();
					} else {
						Log.e("sss","sssss =====" + response.getString("error_info"));
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();;
					}
					dismissInfoProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
					dismissInfoProgressDialog();
					Log.e("sss", "save address is ==== " + e.getMessage());
					Toast.makeText(getApplicationContext(), "设置信息解析异常", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "网络请求错误，请重试!", Toast.LENGTH_SHORT).show();
				dismissInfoProgressDialog();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 删除收货地址
	 */
	private void deleteAddress(final int pos, final String id){
		int is_default = 0;
		
		if(id.equals(Customer.address_id)) {
			is_default = 1;
		} else {
			is_default = 0;
		}
		
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_ADDRESS_CRUD + "customer_id=" + Customer.customer_id + "&id=" + id + "&is_default=" + is_default + "&action=3" + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				String success;
				try {
					success = response.getString("result");
					if("SUCCESS".equals(success)){
						addressList.remove(pos);
						if(id.equals(Customer.address_id)){
							Customer.address_id = "";
							msp.edit().putString("address_id", "").commit();
						}
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();;
					}
					dismissInfoProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
					dismissInfoProgressDialog();
					Log.e("sss","sssssss=======" + e.getMessage());
					Toast.makeText(getApplicationContext(), "删除地址信息解析异常", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "网络请求错误，请重试!", Toast.LENGTH_SHORT).show();
				dismissInfoProgressDialog();
			}
		});
		requestQueue.add(jsr);
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
	
	//加载更多的方法
		private void showNextPage() {
			if (totalSize != addressList.size()) {
				if (NetworkUtils.getNetworkState(this) != NetworkUtils.NETWORN_NONE) {
					this.m_currentpage++;
					getAddressList();
				} else {
					Toast.makeText(ShippingAddressMgrActivity.this, "无法连接网络",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(ShippingAddressMgrActivity.this, "已加载全部数据",
						Toast.LENGTH_SHORT).show();
			}
		}
		// 用来关闭加载和刷新视图
		private void onLoad() {
			listView.stopLoadMore();
			listView.stopRefresh();
		}
		
	private void getProv() {
		provinceMapList.clear();
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_PROV, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					if("SUCCESS".equals(response.getString("result"))) {
						JSONArray array = response.getJSONArray("data");
						for(int i =0 ;i<array.length();i++) {
							JSONObject obj = (JSONObject) array.get(i);
							Map<String,String> map = new HashMap<String,String>();
							map.put("id", obj.getString("id"));
							map.put("name", obj.getString("name"));
							provinceMapList.add(map);
						}
					} else {
						if(response.has("error_info")) {
							Toast.makeText(getApplicationContext(), response.getString("error_info"), Toast.LENGTH_SHORT).show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
			}
		});
		requestQueue.add(jsr);
	}
	
	private void getCity() {
		cityMapList.clear();
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_CITY, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					if("SUCCESS".equals(response.getString("result"))) {
						JSONArray array = response.getJSONArray("data");
						for(int i =0 ;i<array.length();i++) {
							JSONObject obj = (JSONObject) array.get(i);
							Map<String,String> map = new HashMap<String,String>();
							map.put("id", obj.getString("id"));
							map.put("name", obj.getString("name"));
							cityMapList.add(map);
						}
					} else {
						if(response.has("error_info")) {
							Toast.makeText(getApplicationContext(), response.getString("error_info"), Toast.LENGTH_SHORT).show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
			}
		});
		requestQueue.add(jsr);
	}
	
	private String getProvName(String prov_id) {
		String name = "";
		if(provinceMapList.size() < 1) return name;
		for(int i = 0 ;i<provinceMapList.size();i++) {
			if(prov_id.equals(provinceMapList.get(i).get("id"))) {
				name = provinceMapList.get(i).get("name");
				return name;
			}
		}
		return name;
	}
	
	private String getCityName(String city_id) {
		Log.e("sss","city id is === " + city_id);
		String name = "";
		if(cityMapList.size() < 1) return name;
		for(int i = 0 ;i<cityMapList.size();i++) {
			if(city_id.equals(cityMapList.get(i).get("id"))) {
				name = cityMapList.get(i).get("name");
				return name;
			}
		}
		return name;
	}
}
