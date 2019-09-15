package com.yst.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.jack.contacts.AppContext_city;
import com.jack.headpicselect.NetWorkHelper;
import com.jack.ui.MProcessDialog;
import com.jack.ui.XListView;
import com.jack.ui.XListView.IXListViewListener;
import com.yst.yiku.R;

public class CheckAddressActivity extends Activity implements OnClickListener,
		IXListViewListener, OnItemClickListener, OnGeocodeSearchListener {

	private XListView yiku_fragment_tihuodian_right // 右边ListView
	;
	private ArrayList<HashMap<String, String>> list = null;
	private int currentItem = 0;
	private LinearLayout activity_back_iv;// 返回
	private RequestQueue mQueue;
	private TextView check_city, // 选择城市
			activity_title_tv // 显示所选的城市
			;
	// 搜索框
	private EditText et_search;
	private int PAGE_COUNT = 0;
	private int PAGE_NO = 0;

	private String cityName = "";

	private String streetName = "";
	private double weidu;
	private double jingdu;
	private SharedPreferences sp = null; 

	private Handler handler = null;
	private final static int STATE1 = 0;
	private final static int STATE2 = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_tihuodian);
		initView();
		getDefaultStoreTwo();
	}
	
	private void getDefaultStoreTwo() {
		showInfoProgressDialog();
		StringRequest stringRequest = new StringRequest(Method.POST,
				Contacts.URL_CHECK_TIHUO_ADDRESS,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {// 是否有字段
						Log.i("sss", "response = " + response.toString());
						JSONObject response2 = null;
						try {
							response2 = new JSONObject(response);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							String result = response2.getString("result");// 获取字段值
							if (result.equalsIgnoreCase("SUCCESS")) {
								if (response2.has("page_model")) {
									JSONObject page_model = response2
											.getJSONObject("page_model");

									PAGE_COUNT = Integer.parseInt(page_model
											.getString("pageCount"));
								}
								if (response2.has("data")) {
									JSONArray data = response2
											.getJSONArray("data");
									for (int i = 0; i < data.length(); i++) {
										HashMap<String, String> map = new HashMap<String, String>();
										JSONObject json = data
												.getJSONObject(i);
										if (json.has("customerDistance")) {
											map.put("customerDistance", json
													.getString("customerDistance"));
										}
										if (json.has("name")) {
											map.put("name",
													json.getString("name"));
										}
										if (json.has("id")) {
											map.put("id",
													json.getString("id"));
										}
											list.add(map);
									}
								}
								if (rightAdapter == null) {
									rightAdapter = new MyRightAdapter();
									yiku_fragment_tihuodian_right
											.setAdapter(rightAdapter);
								} else {
									rightAdapter.notifyDataSetChanged();
								}
							} else {// 信息失败
								if (response2.has("error_info")) {// 是否有字段
									Toast.makeText(
											CheckAddressActivity.this,
											""
													+ response2
															.getString("error_info"),
											Toast.LENGTH_SHORT).show();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						dismissInfoProgressDialog();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
						Toast.makeText(CheckAddressActivity.this, "数据解析异常..", Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> map = new HashMap<String, String>();
				if ("北京".equals(cityName)
						|| "重庆".equals(cityName)
						|| "天津".equals(cityName)
						|| "上海".equals(cityName)) {

					map.put("prov", cityName);
				} else {
					map.put("cityname", cityName);
				}
				map.put("lat", AppContext_city.chengshidingweiTM.weidu + "");
				map.put("lng", AppContext_city.chengshidingweiTM.jingdu + "");
				map.put("customer_lat", AppContext_city.chengshidingweiTM.weidu
						+ "");
				map.put("customer_lng",
						AppContext_city.chengshidingweiTM.jingdu + "");
				map.put("client_type", "A");
				map.put("page_size", "15");
				map.put("page_no", PAGE_NO + "");
				map.put("version", "1");
				Log.i("sss", "map == " + map.toString());
				return map;
			}
		};
		mQueue.add(stringRequest);
	}


	private void initView() {
		sp = getSharedPreferences("yiku", MODE_PRIVATE);
		String cityname = sp.getString("cityName", "");
		if("".equals(cityname)){
			cityName = AppContext_city.chengshidingweiTM.shi;
		}else {
			cityName = cityname;
		}
		if(cityName.contains("市")) {
			cityName = cityName.replace("市", "");
		}
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
		list = new ArrayList<HashMap<String, String>>();
		mQueue = Volley.newRequestQueue(this);
		yiku_fragment_tihuodian_right = (XListView) findViewById(R.id.yiku_fragment_tihuodian_right);
		activity_back_iv = (LinearLayout) findViewById(R.id.activity_back_iv);
		check_city = (TextView) findViewById(R.id.check_city);
		et_search = (EditText) findViewById(R.id.et_search);
		yiku_fragment_tihuodian_right.setDividerHeight(0);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(cityName);
		et_search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (s.length() >= 2) {
					streetName = s + "";
					getLatlon(streetName, cityName);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		check_city.setOnClickListener(this);
		yiku_fragment_tihuodian_right.setXListViewListener(this);
		yiku_fragment_tihuodian_right.setPullLoadEnable(true);
		yiku_fragment_tihuodian_right.setOnItemClickListener(this);
		activity_back_iv.setOnClickListener(this);
	}

	MyRightAdapter rightAdapter = null;

	class MyRightAdapter extends BaseAdapter {
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
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(CheckAddressActivity.this,
						R.layout.store_right_list_item, null);
				holder.tihuodian_name = (TextView) convertView
						.findViewById(R.id.tihuodian_name);
				holder.distance_tv = (TextView) convertView
						.findViewById(R.id.distance_tv);
				holder.store_details = (TextView) convertView
						.findViewById(R.id.store_details);
				holder.store_list_view = convertView
						.findViewById(R.id.store_list_view);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == 0) {
				holder.store_list_view.setVisibility(View.GONE);
			} else {
				holder.store_list_view.setVisibility(View.VISIBLE);
			}
			holder.tihuodian_name.setText(list.get(position).get("name"));

			String distanceStr = list.get(position).get("customerDistance");
			if (distanceStr != null) {
				double distance = Double.parseDouble(distanceStr);
				
				if (distance == 0) {
					holder.distance_tv.setText("距离 0.00m");
				} else if (distance < 1000) {
					
					if(distance < 1) {
						holder.distance_tv.setText("距离 0"
								+ new DecimalFormat("###.00").format(distance)
										.toString() + "m");
					}else {
						holder.distance_tv.setText("距离 "
								+ new DecimalFormat("###.00").format(distance)
								.toString() + "m");
					}
				} else {
					holder.distance_tv.setText("距离 "
							+ new DecimalFormat("###.00").format(
									distance / 1000).toString() + "km");
				}
				holder.store_details.setOnClickListener(new MyStoreDetails(
						position));
			}
			return convertView;
		}

		class MyStoreDetails implements OnClickListener {
			private int position;

			public MyStoreDetails(int pos) {
				this.position = pos;
			}

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CheckAddressActivity.this,
						StoreDetailActivity.class);
				intent.putExtra("distance", list.get(position).get("distance"));
				intent.putExtra("id", list.get(position).get("id"));
				intent.putExtra("name", list.get(position).get("name"));
				intent.putExtra("yi_type", 100);
				startActivity(intent);
			}
		}
	}

	ViewHolder holder = null;

	class ViewHolder {
		TextView tihuodian_name,// 提货点名称
				distance_tv, // 距离
				store_details// 详情
				;
		View store_list_view;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_back_iv:
			finish();
			break;
		case R.id.check_city:// 去选择城市
			Intent intent = new Intent(CheckAddressActivity.this,
					CityListActivity.class);
			startActivityForResult(intent, 1);// 请求码为 1
			break;
		}
	}

	private void onLoad() {
		yiku_fragment_tihuodian_right.stopRefresh();
		yiku_fragment_tihuodian_right.stopLoadMore();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH时mm分");
		String time = format.format(date);
		yiku_fragment_tihuodian_right.setRefreshTime(time);
	}

	@Override
	public void onLoadMore() {
		loadData();
	}

	private void loadData() {
		if (PAGE_NO + 1 < PAGE_COUNT) {
			Log.e("联网前", PAGE_NO + "====" + PAGE_COUNT);
			PAGE_NO += 1;
			getDefaultStoreTwo();
		} else {
			Toast.makeText(this, "已经加载全部", Toast.LENGTH_SHORT).show();
			onLoad();
			yiku_fragment_tihuodian_right.setPullLoadEnable(false);
		}

	}

	@Override
	public void onRefresh() {
		rightAdapter = null;
		yiku_fragment_tihuodian_right.setPullLoadEnable(true);
		PAGE_NO = 0;
		list.clear();
		getDefaultStoreTwo();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		sp.edit().putString("STOREID",list.get(position - 1).get("id")).commit();
		sp.edit().putString("STORENAME",list.get(position - 1).get("name")).commit();
		sp.edit().putString("cityName",cityName).commit();
		Intent intent = new Intent();
		intent.putExtra("store_id", list.get(position - 1).get("id"));
		intent.putExtra("store_name", list.get(position - 1).get("name"));
		setResult(3, intent);
		finish();
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
		onLoad();
		mInfoProgressDialog.dismiss();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1 && resultCode == 1) {// 请求码和响应码对上号了
			String cityName = data.getStringExtra("cityname"); // 获取返回的城市名
			if (cityName != null) {
				if(cityName.contains("市")) {
					cityName = cityName.replace("市", "");
				}
				Log.i("sss", "cityName == " + cityName);
				activity_title_tv.setText(cityName);
				this.cityName = cityName;
				getLatlon(cityName, cityName);
			}
		}
	}

	private GeocodeSearch geocoderSearch;

	/**
	 * 响应地理编码
	 */
	public void getLatlon(final String streetName, final String cityName) {
		Log.i("qcs", "streetName==" + streetName + "--" + "cityName=="
				+ cityName);
		GeocodeQuery query = new GeocodeQuery(streetName, cityName);// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode，
		geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求
	}

	/**
	 * 地理编码回调
	 */
	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getGeocodeAddressList() != null
					&& result.getGeocodeAddressList().size() > 0) {
				GeocodeAddress address = result.getGeocodeAddressList().get(0);

				Log.i("qcs", "经纬度值:" + address.getLatLonPoint() + "-----"
						+ address.getFormatAddress());
				LatLonPoint point = address.getLatLonPoint();
				weidu = point.getLatitude();
				jingdu = point.getLongitude();
				PAGE_NO = 0;
				if(list != null){
					list.clear();
				}
				if(rightAdapter != null) {
					rightAdapter.notifyDataSetChanged();
				}
				getDefaultStoreTwo();
			} else {
			}
		} else if (rCode == 27) {
		} else if (rCode == 32) {
		} else {
		}
	}

	/**
	 * 逆地理编码回调
	 */
	@Override
	public void onRegeocodeSearched(RegeocodeResult arg0, int arg1) {

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(list != null) {
			list.clear();
			list = null;
		}
	}

}
