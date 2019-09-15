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
import com.jack.headpicselect.NetWorkHelper;
import com.jack.ui.MProcessDialog;
import com.yst.yiku.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 收货地址添加和修改界面
 * 
 * @author lixiangchao
 *
 */
public class ShippingAddressActivity extends Activity implements
		OnClickListener, OnItemSelectedListener {

	private ImageView activity_back_iv;
	private TextView activity_title_tv, activity_handle_tv;

	// 添加收货地址界面
	private EditText add_address_name_et, add_address_phone_et,
			add_address_zipcode_et, add_address_detail_et;
	private Button add_shipping_address_commit_btn;
	private Spinner service_package_add_address_shi,
			service_package_add_address_sheng;

	private List<String> provinceList = new ArrayList<String>();
	private List<String> cityList = new ArrayList<String>();

	private List<Map<String, String>> provinceMapList = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> cityMapList = new ArrayList<Map<String, String>>();

	private String current_province_id = "1";
	private String current_city_id = "1";
	private int current_province_position = 0, current_city_position = 0;
	
	private Handler handler = null ;
	private final static int STATE1 = 0;
	private final static int STATE2 = 1;
	private MProcessDialog mInfoProgressDialog;

	private String address_id = "";
	private int flag = 0 ;
	
	private RequestQueue requestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_shipping_address);
		initAddAddressView();
		
		flag = getIntent().getIntExtra("FLAG", 0);

		if (flag == 1) {
			activity_title_tv.setText(R.string.activity_customer_add_address_label);
			getProv();
		} else {
			activity_title_tv.setText(R.string.activity_customer_update_address_label);
			initUpdateAddressView();
		}
	}

	// 初始化返回按钮事件
	private void initAddAddressView() {
		requestQueue = Volley.newRequestQueue(this);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);

		add_address_name_et = (EditText) findViewById(R.id.add_address_name_et);
		add_address_phone_et = (EditText) findViewById(R.id.add_address_phone_et);
		add_address_zipcode_et = (EditText) findViewById(R.id.add_address_zipcode_et);
		add_address_detail_et = (EditText) findViewById(R.id.add_address_detail_et);

		add_shipping_address_commit_btn = (Button) findViewById(R.id.add_shipping_address_commit_btn);
		add_shipping_address_commit_btn.setOnClickListener(this);

		service_package_add_address_shi = (Spinner) findViewById(R.id.service_package_add_address_shi);
		service_package_add_address_sheng = (Spinner) findViewById(R.id.service_package_add_address_sheng);

		service_package_add_address_shi.setOnItemSelectedListener(this);
		service_package_add_address_sheng.setOnItemSelectedListener(this);
	}


	// 初始化修改收货地址界面
	private void initUpdateAddressView() {
		
		add_shipping_address_commit_btn.setVisibility(View.GONE);
		
		activity_handle_tv = (TextView) findViewById(R.id.activity_handle_tv);
		activity_handle_tv.setVisibility(View.VISIBLE);
		activity_handle_tv.setTextColor(Color.parseColor("#00BDFF"));
		activity_handle_tv.setText("保存");
		activity_handle_tv.setOnClickListener(this);
		
		HashMap<String,String> maps = (HashMap<String,String>)getIntent().getExtras().getSerializable("maps");
		
		if(null != maps && null != maps.get("id")){
			address_id = maps.get("id");
		}
		
		if(null != maps && null != maps.get("name")){
			add_address_name_et.setText(maps.get("name"));
		}
		
		if(null != maps && null != maps.get("address")){
			add_address_detail_et.setText(maps.get("address"));
		}
		
		if(null != maps && null != maps.get("zip_code")){
			add_address_zipcode_et.setText(maps.get("zip_code"));
		}
		
		if(null != maps && null != maps.get("phone")){
			add_address_phone_et.setText(maps.get("phone"));
		}
		
		if(null != maps && null != maps.get("prov_id")){
			current_province_id = maps.get("prov_id");
		}
		
		if(null != maps && null != maps.get("city_id")){
			current_city_id = maps.get("prov_id");
		}
		
		Log.e("sss","map to string is === " + maps.toString());
		
		getProv();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_shipping_address_commit_btn:
			addAddress(STATE1);
			break;

		case R.id.activity_handle_tv:
			addAddress(STATE2);
			break;
		case R.id.activity_back_iv:
			finish();
			break;
		}
	}

	// 添加----修改收货地址
	private void addAddress(final int position) {
		final String name = add_address_name_et.getText().toString().trim();
		final String phone = add_address_phone_et.getText().toString().trim();
		final String zipcode = add_address_zipcode_et.getText().toString()
				.trim();
		final String address = add_address_detail_et.getText().toString()
				.trim();

		if (TextUtils.isEmpty(name)) {
			Toast.makeText(this, "请输入收货人", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(phone)) {
			Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!LoginActivity.isMobileNO(phone)) {
			Toast.makeText(this, "手机号无效", Toast.LENGTH_SHORT).show();
			return;
		}
		if (TextUtils.isEmpty(zipcode)) {
			Toast.makeText(this, "请输入邮编", Toast.LENGTH_SHORT).show();
			return;
		} else if(zipcode.length() != 6){
			Toast.makeText(this, "请输入有效的邮编号码", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (TextUtils.isEmpty(address)) {
			Toast.makeText(this, "请输入地址", Toast.LENGTH_SHORT).show();
			return;
		}
		
		showInfoProgressDialog();
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case STATE1:
					showInfoProgressDialog();
					break;
				case STATE2:
					dismissInfoProgressDialog();
					String result = (String) msg.obj;
					Log.e("sss", "shipping address result is " + result);
					if (result.contains("success") || result.contains("Success") || result.contains("SUCCESS")) {
						if(flag == 1){
							Toast.makeText(getApplicationContext(), "添加收货地址成功!", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "修改收货地址成功!", Toast.LENGTH_SHORT).show();
						}
						ShippingAddressActivity.this.finish();
					} else {
						if(flag == 1){
							Toast.makeText(getApplicationContext(), "添加收货地址失败!", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(), "修改收货地址失败!", Toast.LENGTH_SHORT).show();
						}
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
				handler.sendEmptyMessage(STATE1);
				String url = Contacts.URL_ADDRESS_CRUD;
				String map = null;
				
				if(position == STATE1){
					map = "customer_id=" + Customer.customer_id + "&prov_id=" + current_province_id + "&city_id=" + current_city_id + "&address=" + address + "&phone=" + phone + "&name=" + name + "&zip_code=" + zipcode +"&action=1" + Contacts.URL_ENDING;
				} else {
					map = "customer_id=" + Customer.customer_id + "&id=" + address_id + "&prov_id=" + current_province_id + "&city_id=" + current_city_id + "&address=" + address + "&phone=" + phone + "&name=" + name + "&zip_code=" + zipcode +"&action=2" + Contacts.URL_ENDING;
				}
				if(null == map) return ;
				
				Log.e("sss", "shipping address url ==== " + url);
				Log.e("sss", "shipping address map ==== " + map);

				String result = NetWorkHelper.postImg_Record2(url, map);
				Message msg = handler.obtainMessage();
				msg.what = STATE2;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (parent.getId()) {

		case R.id.service_package_add_address_sheng:
			for (String key : provinceMapList.get(position).keySet()) {
				current_province_id = key;
//				current_province_name = provinceMapList.get(position).get(key);
			}
			getCity(current_province_id);
			break;

		case R.id.service_package_add_address_shi:
			for (String key : cityMapList.get(position).keySet()) {
				current_city_id = key;
//				current_city_name = cityMapList.get(position).get(key);
			}
			break;
		default:
			break;
		}
	}

	private void getProv() {
		provinceList.clear();
		provinceMapList.clear();
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_PROV, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					Log.e("sss"," response to string is === " + response.toString());
					if("SUCCESS".equals(response.getString("result"))) {
						JSONArray jsonArray = response.getJSONArray("data");
						getCity(current_province_id);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = jsonArray.getJSONObject(i);
							provinceList.add(obj.getString("name"));
							if (null != current_province_id && !"".equalsIgnoreCase(current_province_id)
									&& current_province_id.equalsIgnoreCase(obj.getString("id"))) {
								current_province_position = i;
							}

							Map<String, String> province = new HashMap<String, String>();
							province.put(obj.getString("id"), obj.getString("name"));
							provinceMapList.add(province);
						}
						ArrayAdapter<String> pro_adapter = new ArrayAdapter<String>(
								ShippingAddressActivity.this, R.layout.spinner_item, provinceList);
						service_package_add_address_sheng.setAdapter(pro_adapter);
						service_package_add_address_sheng.setSelection(current_province_position);
					} else {
						if(response.has("error_info")) {
							Toast.makeText(getApplicationContext(), response.getString("error_info"), Toast.LENGTH_SHORT).show();
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
			}
		});
		requestQueue.add(jsr);
	}
	
	private void getCity(String prov) {
		cityList.clear();
		cityMapList.clear();
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_CITY + prov, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				// TODO Auto-generated method stub
				try {
					if("SUCCESS".equals(response.getString("result"))) {
						JSONArray jsonArray = response.getJSONArray("data");
						for (int i = 0; i < jsonArray.length(); i++) {
							
							JSONObject obj = jsonArray.getJSONObject(i);
							
							cityList.add(obj.getString("name"));
							
							if (null != current_city_id	&& !"".equalsIgnoreCase(current_city_id)
									&& current_city_id.equalsIgnoreCase(obj.getString("id"))) {
								current_city_position = i;
							}
							
							Map<String, String> city = new HashMap<String, String>();
							city.put(obj.getString("id"), obj.getString("name"));
							cityMapList.add(city);
						}
						
						ArrayAdapter<String> pro_adapter = new ArrayAdapter<String>(
								ShippingAddressActivity.this, R.layout.spinner_item, cityList);
						
						service_package_add_address_shi.setAdapter(pro_adapter);
						service_package_add_address_shi.setSelection(current_city_position);
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
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub
	}
}
