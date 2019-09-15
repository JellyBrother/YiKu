package com.yst.activity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.guohua.common.util.MD5;
import com.jack.headpicselect.NetWorkHelper;
import com.jack.ui.MProcessDialog;
import com.yst.yiku.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.jack.ui.QueryDetailsDialog;

/**
 * 查询电费、水费、燃气费界面
 * @author lixiangchao
 */
public class QueryChargeActivity extends Activity implements OnClickListener{

	//网络请求数据和显示等待条的声明
	private RequestQueue requestQueue ;
	private MProcessDialog mInfoProgressDialog ;
	//用来判断是查询那个费用的对应标志
	private int Query_Charge_Index = -1;
	//查询电费的标志
	public static final int QUERY_CHARGE_ELECTRIC = 1 ;
	//查询天然气的标志
	public static final int QUERY_CHARGE_GAS = 2 ;
	//查询水费的标志
	public static final int QUERY_CHARGE_WATER = 3 ;
	//查询水的类型描述
	public static final String QUERY_WATER_TYPE = "c2670";
	//查询电的类型描述
	public static final String QUERY_ELECTRIC_TYPE = "c2680";
	//查询燃气的类型描述
	public static final String QUERY_GAS_TYPE = "c2681";
		
	//显示省的标志
	public static final int STATUS1 = 1 ;
	//显示城市的标志
	public static final int STATUS2 = 2 ;
	//显示对应的单位的标志
	public static final int STATUS3 = 3 ;
	//返回按钮声明
	private ImageView query_charge_back_iv;
	//我的订单的布局声明
	private LinearLayout query_charge_order_layout;
	private ImageView query_charge_order_iv;
	private TextView query_charge_order_desc_tv;
	
	//订单历史的布局声明
	private LinearLayout query_charge_history_layout;
	private ImageView query_charge_history_iv;
	private TextView query_charge_history_desc;
	
	//底部的查询按钮
	private ImageView query_charge_iv;
	private QueryDetailsDialog queryDialog;
	private List<Map<String,String>> list;
	//省布局声明
	private ImageView query_charge_province_iv;
	private TextView query_charge_province_desc_tv;
	//市布局声明
	private ImageView query_charge_city_iv;
	private TextView query_charge_city_desc_tv;
	//单位布局声明
	private ImageView query_charge_unit_iv;
	private TextView query_charge_unit_desc_tv;
	//用户户号布局声明
	private TextView query_charge_account_et;
	private EditText query_charge_account_et1;
	//用来保存省列表的集合
	private List<Map<String, String>> provinceMapList = new ArrayList<Map<String, String>>();
	//用来保存对应省的市级列表集合
	private List<Map<String, String>> cityMapList = new ArrayList<Map<String, String>>();
	//用来保存对应省市的单位集合
	private List<Map<String, String>> unitMapList = new ArrayList<Map<String, String>>();
	//用来提交的接口字段变量
	private String account = null, privoinceStr = null, cityStr = null, unitStr = null ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_charge);
		
		Query_Charge_Index = getIntent().getIntExtra("Query_Charge_Index", -1);
		
		initView();
	}

	private void initView() {
		requestQueue = Volley.newRequestQueue(this);
		
		query_charge_back_iv = (ImageView) findViewById(R.id.query_charge_back_iv);
		query_charge_back_iv.setOnClickListener(this);
		
		query_charge_province_iv = (ImageView) findViewById(R.id.query_charge_province_iv);
		query_charge_province_desc_tv = (TextView) findViewById(R.id.query_charge_province_desc_tv);
		query_charge_province_desc_tv.setOnClickListener(this);
		query_charge_province_iv.setOnClickListener(this);
		//市布局声明
		query_charge_city_iv = (ImageView) findViewById(R.id.query_charge_city_iv);
		query_charge_city_desc_tv = (TextView) findViewById(R.id.query_charge_city_desc_tv);
		query_charge_city_desc_tv.setOnClickListener(this);
		query_charge_city_iv.setOnClickListener(this);
		//单位布局声明
		query_charge_unit_iv = (ImageView) findViewById(R.id.query_charge_unit_iv);
		query_charge_unit_desc_tv = (TextView) findViewById(R.id.query_charge_unit_desc_tv);
		query_charge_unit_desc_tv.setOnClickListener(this);
		query_charge_unit_iv.setOnClickListener(this);
		//用户户号布局声明
		query_charge_account_et = (TextView) findViewById(R.id.query_charge_account_et);
		query_charge_account_et.setOnClickListener(this);
		query_charge_account_et1 = (EditText) findViewById(R.id.query_charge_account_et1);
		//底部的订单布局声明
		query_charge_order_layout = (LinearLayout) findViewById(R.id.query_charge_order_layout);
		query_charge_order_iv = (ImageView) findViewById(R.id.query_charge_order_iv);
		query_charge_order_desc_tv = (TextView) findViewById(R.id.query_charge_order_desc_tv);
		query_charge_order_layout.setOnClickListener(this);
		query_charge_order_iv.setOnClickListener(this);
		query_charge_order_desc_tv.setOnClickListener(this);
		//底部的订单历史声明
		query_charge_history_layout = (LinearLayout) findViewById(R.id.query_charge_history_layout);
		query_charge_history_iv = (ImageView) findViewById(R.id.query_charge_history_iv);
		query_charge_history_desc = (TextView) findViewById(R.id.query_charge_history_desc);
		query_charge_history_layout.setOnClickListener(this);
		query_charge_history_iv.setOnClickListener(this);
		query_charge_history_desc.setOnClickListener(this);
		//查询布局文件声明
		query_charge_iv = (ImageView) findViewById(R.id.query_charge_iv);
		query_charge_iv.setOnClickListener(this);
		
		//用来显示查询按钮的背景图片
		switch(Query_Charge_Index) {
		case QUERY_CHARGE_ELECTRIC:
			query_charge_iv.setImageResource(R.drawable.electric);
			break;
		case QUERY_CHARGE_GAS:
			query_charge_iv.setImageResource(R.drawable.gas);
			break;
		case QUERY_CHARGE_WATER:
			query_charge_iv.setImageResource(R.drawable.water);
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		list = new ArrayList<Map<String,String>>();
		for(int i = 0;i < 6;i ++) {
			Map<String,String> map = new HashMap<String, String>();
			map.put("name", "机器" + i + 1 + "号");
			list.add(map);
		}
		Intent intent = new Intent(QueryChargeActivity.this,QueryChargeListActivity.class);
		switch (v.getId()) {
		case R.id.query_charge_back_iv://返回服务列表界面
			finish();
			LoginActivity.hintKbTwo(this);
			break;
		case R.id.query_charge_order_layout://跳转或者提示我的订单
		case R.id.query_charge_order_iv:
		case R.id.query_charge_order_desc_tv:
			switch(Query_Charge_Index) {
			case QUERY_CHARGE_ELECTRIC:
				Toast.makeText(getApplicationContext(), "跳转到电费订单界面", Toast.LENGTH_SHORT).show();
				intent.putExtra("Query_Charge_Index", QUERY_CHARGE_ELECTRIC);
				startActivity(intent);
				break;
			case QUERY_CHARGE_GAS:
				Toast.makeText(getApplicationContext(), "跳转到天然气费订单界面", Toast.LENGTH_SHORT).show();
				intent.putExtra("Query_Charge_Index", QUERY_CHARGE_ELECTRIC);
				startActivity(intent);
				break;
			case QUERY_CHARGE_WATER:
				Toast.makeText(getApplicationContext(), "跳转到水费订单界面", Toast.LENGTH_SHORT).show();
				intent.putExtra("Query_Charge_Index", QUERY_CHARGE_ELECTRIC);
				startActivity(intent);
				break;
			}
			break;
		case R.id.query_charge_history_layout://订单历史
		case R.id.query_charge_history_iv:
		case R.id.query_charge_history_desc:
			switch(Query_Charge_Index) {
			case QUERY_CHARGE_ELECTRIC:
				Toast.makeText(getApplicationContext(), "跳转到电费缴费历史界面", Toast.LENGTH_SHORT).show();
				queryDialog = new QueryDetailsDialog(this, list);
				break;
			case QUERY_CHARGE_GAS:
				Toast.makeText(getApplicationContext(), "跳转到天然气费缴费历史界面", Toast.LENGTH_SHORT).show();
				queryDialog = new QueryDetailsDialog(this, list);
				break;
			case QUERY_CHARGE_WATER:
				Toast.makeText(getApplicationContext(), "跳转到水费缴费历史界面", Toast.LENGTH_SHORT).show();
				queryDialog = new QueryDetailsDialog(this, list);
				break;
			}
			break;
		case R.id.query_charge_iv:
			//TODO 根据跳转过来的索引执行不同的操作 开始查询
			if(privoinceStr == null) {
				Toast.makeText(getApplicationContext(), "请选择省份", Toast.LENGTH_SHORT).show();
				return ;
			} else if(cityStr == null) {
				Toast.makeText(getApplicationContext(), "请选择城市", Toast.LENGTH_SHORT).show();
				return ;
			} else if(unitStr == null) {
				Toast.makeText(getApplicationContext(), "请选择单位", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			account = query_charge_account_et1.getText().toString().trim();
			if(null == account || TextUtils.isEmpty(account)) {
				Toast.makeText(getApplicationContext(), "请输入用户户号", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			LoginActivity.hintKbTwo(this);
			
			switch(Query_Charge_Index) {
			case QUERY_CHARGE_ELECTRIC:
				Toast.makeText(getApplicationContext(), "查询电费", Toast.LENGTH_SHORT).show();
				getGoodInfo(privoinceStr, cityStr, QUERY_ELECTRIC_TYPE, unitStr);
				break;
			case QUERY_CHARGE_GAS:
				Toast.makeText(getApplicationContext(), "查燃气费", Toast.LENGTH_SHORT).show();
				getGoodInfo(privoinceStr, cityStr, QUERY_GAS_TYPE, unitStr);
				break;
			case QUERY_CHARGE_WATER:
				Toast.makeText(getApplicationContext(), "查询水费", Toast.LENGTH_SHORT).show();
				getGoodInfo(privoinceStr, cityStr, QUERY_WATER_TYPE, unitStr);
				break;
			}
			break;
		case R.id.query_charge_account_et://输入户号点击事件
			query_charge_account_et1.setVisibility(View.VISIBLE);
			query_charge_account_et.setVisibility(View.GONE);
			break;
		case R.id.query_charge_province_iv://获取省
		case R.id.query_charge_province_desc_tv:
			getProv();
			break;
		case R.id.query_charge_city_iv://获取市
		case R.id.query_charge_city_desc_tv:
			if(privoinceStr == null) {
				Toast.makeText(getApplicationContext(), "请选择省份", Toast.LENGTH_SHORT).show();
				return ;
			}
			getCity(privoinceStr);
			break;
		case R.id.query_charge_unit_iv://获取单位
		case R.id.query_charge_unit_desc_tv:
			if(privoinceStr == null || cityStr == null) {
				Toast.makeText(getApplicationContext(), "请选择省市后再来选择单位吧", Toast.LENGTH_SHORT).show();
				return ;
			}
			
			switch(Query_Charge_Index) {
			case QUERY_CHARGE_ELECTRIC:
				getUnits(privoinceStr, cityStr, QUERY_ELECTRIC_TYPE);
				break;
			case QUERY_CHARGE_GAS:
				getUnits(privoinceStr, cityStr, QUERY_GAS_TYPE);
				break;
			case QUERY_CHARGE_WATER:
				getUnits(privoinceStr, cityStr, QUERY_WATER_TYPE);
				break;
			}
			
//			showPickView(STATUS3, realList());
			break;
		}
	}
	
	/**
	 * 省、市、单位的点击事件
	 * @param index 用来区分是那个菜单点击了
	 * @param mlist 省、市、单位分别对应的服务器数据列表
	 */
	private void showPickView(final int index, final List<Map<String, String>> mlist) {
		
		if(mlist == null || mlist.size() < 1) {
			Toast.makeText(getApplicationContext(), "该选项暂无列表可选择", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		View parent = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
		final View popView = View.inflate(this, R.layout.activity_query_charge_pick_item, null);
		// 初始化视图
		TextView pick_view_title_tv = (TextView) popView.findViewById(R.id.pick_view_title_tv);
		ListView pick_view_list = (ListView) popView.findViewById(R.id.pick_view_list);
		
		// 屏幕的宽高
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;

		// 显示控件
		final PopupWindow popWindow = new PopupWindow(popView, width, height);
		popWindow.setAnimationStyle(R.style.AnimBottom);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(false);
		
		switch(index) {
		case STATUS1:
			pick_view_title_tv.setText("选择所在省");
			break;
		case STATUS2:
			pick_view_title_tv.setText("选择所在市");
			break;
		case STATUS3:
			pick_view_title_tv.setText("选择所属单位");
			break;
		}
		MyAdapter mAdapter = new MyAdapter(mlist);
		pick_view_list.setAdapter(mAdapter);
		pick_view_list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch(index) {
				case STATUS1:
					query_charge_province_desc_tv.setText(mlist.get(position).get("name"));
					privoinceStr = mlist.get(position).get("id");
					query_charge_city_desc_tv.setText("请选择城市");
					query_charge_unit_desc_tv.setText("请选择单位");
					cityStr = null ;
					unitStr = null ;
					break;
				case STATUS2:
					query_charge_city_desc_tv.setText(mlist.get(position).get("name"));
					cityStr = mlist.get(position).get("id");
					query_charge_unit_desc_tv.setText("请选择单位");
					unitStr = null ;
					break;
				case STATUS3:
					query_charge_unit_desc_tv.setText(mlist.get(position).get("name"));
					unitStr = mlist.get(position).get("id");
					query_charge_account_et1.setText("");
					break;
				}
				popWindow.dismiss();
			}
		});
		
		if(mlist.size() > 5) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 350);
			pick_view_list.setLayoutParams(params);
		} else {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			pick_view_list.setLayoutParams(params);
		}
		ColorDrawable dw = new ColorDrawable(0x30000000);
		popWindow.setBackgroundDrawable(dw);
		popWindow.showAtLocation(parent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		popView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				int height = popView.findViewById(R.id.query_charge_pick_layout).getTop();
				int y = (int) arg1.getY();
				if (MotionEvent.ACTION_UP == arg1.getAction()) {
					if (y < height) {
						popWindow.dismiss();
					}
				}
				return true;
			}
		});
	}
	
	class MyAdapter extends BaseAdapter {
		private List<Map<String, String>> mlist ;
		public MyAdapter(List<Map<String, String>> mtlist) {
			this.mlist = mtlist ;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder ;
			if(null == convertView) {
				convertView = getLayoutInflater().inflate(R.layout.query_charge_pick_view_list_item, null);
				holder = new ViewHolder();
				holder.query_charge_list_item_tv = (TextView) convertView.findViewById(R.id.query_charge_list_item_tv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.query_charge_list_item_tv.setText(mlist.get(position).get("name"));
			return convertView;
		}
		
		class ViewHolder {
			TextView query_charge_list_item_tv ;
		}
	}
	
	//假数据制造厂
	private List<Map<String, String>> realList() {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		for(int i = 0 ;i < 10 ;i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("id", String.valueOf(i + 1));
			map.put("name", "北京市");
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 获取省列表 如果是已经获取了，就直接显示获取的省列表
	 */
	private void getProv() {
		if(provinceMapList != null && provinceMapList.size() > 0) {
			showPickView(STATUS1, provinceMapList);
			return ;
		}
		provinceMapList.clear();
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_YST_PROV, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					if("0".equals(response.getString("state"))) {
						String data = response.getString("data");
						JSONArray array = new JSONArray(data);
						if(array.length() > 0) {
							for(int i =0 ;i<array.length();i++) {
								JSONObject obj = (JSONObject) array.get(i);
								Map<String,String> map = new HashMap<String,String>();
								map.put("id", obj.getString("code"));
								map.put("name", obj.getString("proname"));
								provinceMapList.add(map);
							}
							showPickView(STATUS1, provinceMapList);
						} else {
							showErrorMsg("数据为空", "获取省列表失败");
						}
					} else {
						if(response.has("error")) {
							showErrorMsg("数据为空", response.getString("error"));
						} else {
							showErrorMsg("数据为空", "获取省列表失败");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					dismissInfoProgressDialog();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(getApplicationContext(), "网络访问异常，请重试", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 根据选择好的省来获取对应的城市列表
	 * @param prov_id
	 */
	private void getCity(String prov_id) {
		cityMapList.clear();
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest( Contacts.URL_GET_YST_CITY + MD5.md5(prov_id +Contacts.URL_YST_PROTOCOL) + "&ProId=" + prov_id, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				
				try {
					if("0".equals(response.getString("state"))) {
						JSONArray array = new JSONArray(response.getString("data"));
						if(array.length() > 0) {
							for(int i =0 ;i<array.length();i++) {
								JSONObject obj = (JSONObject) array.get(i);
								Map<String,String> map = new HashMap<String,String>();
								map.put("id", obj.getString("code"));
								map.put("name", obj.getString("cityname"));
								cityMapList.add(map);
							}
							showPickView(STATUS2, cityMapList);
						} else {
							showErrorMsg("数据为空", "获取城市列表失败");
						}
					} else {
						if(response.has("error")) {
							showErrorMsg("温馨提示", response.getString("error"));
						} else {
							showErrorMsg("温馨提示", "获取城市列表失败");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					dismissInfoProgressDialog();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(getApplicationContext(), "网络访问异常，请重试", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 根据选择好的省来获取对应的单位
	 * @param prov_id
	 */
	private void getUnits(String prov_id, String city, String typeCode) {
		unitMapList.clear();
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest( Contacts.URL_GET_YST_UNITS + MD5.md5(prov_id + city + typeCode +Contacts.URL_YST_PROTOCOL) + "&proid=" + prov_id + "&cityid=" + city + "&type=" + typeCode, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if("0".equals(response.getString("state"))) {
						JSONArray array = new JSONArray(response.getString("data"));
						if(array.length() > 0) {
							for(int i =0 ;i<array.length();i++) {
								JSONObject obj = (JSONObject) array.get(i);
								Map<String,String> map = new HashMap<String,String>();
								map.put("id", obj.getString("code"));
								map.put("name", obj.getString("unitname"));
								unitMapList.add(map);
							}
							showPickView(STATUS3, unitMapList);
						} else {
							showErrorMsg("数据为空", "未找到支持的公司列表");
						}
					} else {
						if(response.has("error")) {
							showErrorMsg("温馨提示", response.getString("error"));
						} else {
							showErrorMsg("温馨提示", "未找到支持的公司列表");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					dismissInfoProgressDialog();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(getApplicationContext(), "网络访问异常，请重试", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 四．查询商品信息
	 * @param prov_id
	 */
	private void getGoodInfo(String prov_id, String city, final String typeCode, String comcode) {
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest( Contacts.URL_GET_YST_RRODUCT_INFO + MD5.md5(prov_id + city + typeCode + comcode + Contacts.URL_YST_PROTOCOL) + "&provid=" + prov_id + "&cityid=" + city + "&type=" + typeCode + "&comcode=" + comcode, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if("0".equals(response.getString("state"))) {
						if(response.has("data") && !response.getString("data").equals("") && !response.getString("data").equals("null") && response.getString("data") != null) {
							initGet(typeCode, response.getJSONObject("data").getString("productId"));
						}
					} else {
						if(response.has("error")) {
							showErrorMsg("温馨提示", response.getString("error"));
						} else {
							showErrorMsg("温馨提示", "未找到支持的公司列表");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					dismissInfoProgressDialog();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(getApplicationContext(), "网络访问异常，请重试", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 查询欠费信息
	 * @param typeCode
	 * @param productId
	 */
	private void initGet(final String typeCode, final String productId) {
		final String provName = query_charge_province_desc_tv.getText().toString().trim();
		final String cityName = query_charge_city_desc_tv.getText().toString().trim();
		final String unitName = query_charge_unit_desc_tv.getText().toString().trim();
		showInfoProgressDialog();
		//1195185060 湖南长沙 国网湖南省电力公司长沙供电分公司 电费
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case STATE1:
					dismissInfoProgressDialog();
					break;
				case STATE2:
					dismissInfoProgressDialog();
					String result = (String) msg.obj;
					Log.e("sss", "shipping address result is " + result);
					if (result.contains("state") || result.contains("state") || result.contains("state")) {
						try {
							JSONObject json = new JSONObject(result);
							if(json.has("state") && json.getString("state").equals("0")) {
//								 public string userCode { get; set; }//用户编号
//						         public string account { get; set; }//缴费帐号
//						         public string accountName { get; set; }//用户姓名（查到才返回）
//						         public string balance { get; set; }//欠费信息
//						         public string contractNo { get; set; }//合同号 （查到才返回）
								if(json.has("data")) {
									String message = "";
									Intent it = new Intent(QueryChargeActivity.this, PayMobileDetailActivity.class);
									if(json.getJSONObject("data").has("userCode")) {
										message += "用户编号: " + json.getJSONObject("data").getString("userCode") + "\n";
										it.putExtra("userCode", json.getJSONObject("data").getString("userCode"));
									}
									if(json.getJSONObject("data").has("account")) {
										message += "缴费帐号: " + json.getJSONObject("data").getString("account") + "\n";
										it.putExtra("account", json.getJSONObject("data").getString("account"));
									}
									if(json.getJSONObject("data").has("accountName")) {
										message += "用户姓名: " + json.getJSONObject("data").getString("accountName") + "\n";
										it.putExtra("accountName", json.getJSONObject("data").getString("accountName"));
									}
									if(json.getJSONObject("data").has("balance")) {
										message += "欠费信息: " + json.getJSONObject("data").getString("balance") + "\n";
										it.putExtra("balance", json.getJSONObject("data").getString("balance"));
									}
									if(json.getJSONObject("data").has("contractNo")) {
										message += "合同号: " + json.getJSONObject("data").getString("contractNo");
										it.putExtra("contractNo", json.getJSONObject("data").getString("contractNo"));
									}
									
									it.putExtra("payTheFeesUnit", query_charge_unit_desc_tv.getText().toString().trim());
//									showGetPaySDM("查询信息", message);
									it.putExtra("provCode", privoinceStr);
									it.putExtra("cityCode", cityStr);
									it.putExtra("type", typeCode);
									it.putExtra("cardId", productId);
									it.putExtra("chargeCompanyCode", unitStr);
									it.putExtra("Account", account);
									it.putExtra("list", 3);
									it.putExtra("message", message);
									startActivity(it);
								}
							} else {
								showErrorMsg("温馨提示", json.getString("error"));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(getApplicationContext(), "查询失败", Toast.LENGTH_SHORT).show();
					}
					break;
				default:
					break;
				}
			}
		};
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpGet httpGet = new HttpGet(Contacts.URL_GET_ACCOUNT_BALANCE + "ident=" + MD5.md5(account + Contacts.URL_YST_PROTOCOL)
						 + "&provName=" + provName + "&cityName=" + cityName + "&type=" + typeCode + "&chargeCompanyCode=" + unitStr + "&chargeCompanyName=" + unitName + "&account=" + account + "&productId=" + productId);
				
				Log.e("sss","query url is === " + Contacts.URL_GET_ACCOUNT_BALANCE + "ident=" + MD5.md5(account + Contacts.URL_YST_PROTOCOL)
						 + "&provName=" + provName + "&cityName=" + cityName + "&type=" + typeCode + "&chargeCompanyCode=" + unitStr + "&chargeCompanyName=" + unitName + "&account=" + account + "&productId=" + productId);
				
		        HttpClient httpClient = new DefaultHttpClient();
		        // 发送请求
		        try {
		        	HttpResponse response = httpClient.execute(httpGet);
		            // 显示响应
		            showResponseResult(response);// 一个私有方法，将响应结果显示出来
		         } catch (Exception e) {
		             e.printStackTrace();
		             handler.sendEmptyMessage(STATE1);
		         }
			}
		}).start();
	}
	
	private Handler handler = null ;
	private final static int STATE1 = 0;
	private final static int STATE2 = 1;
	
	/**
     * 显示响应结果到命令行和TextView
     * @param response
     */
    private void showResponseResult(HttpResponse response) {
        if (null == response) return;
        HttpEntity httpEntity = response.getEntity();
        try {
            InputStream inputStream = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String result = "";
            String line = "";
            while (null != (line = reader.readLine())) {
                result += line;
            }
            Log.e("sss"," result is === " + result);
            Message msg = handler.obtainMessage();
			msg.what = STATE2;
			msg.obj = result;
			handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            handler.sendEmptyMessage(STATE1);
        }
    }

	/**
	 * 显示获取接口失败的信息
	 * @param errorMsg
	 */
	@SuppressWarnings("deprecation")
	private void showErrorMsg(String titleMsg, String errorMsg) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(titleMsg);
		dialog.setMessage(errorMsg);
		dialog.setButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	/**
	 * 显示缴费接口信息
	 * @param errorMsg
	 */
	@SuppressWarnings("deprecation")
	private void showGetPaySDM(String titleMsg, String errorMsg) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(Html.fromHtml(titleMsg));
		dialog.setMessage(errorMsg);
		dialog.setButton2("去缴费", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.setButton("知道了", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	//弹出获取数据的网络进度条
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