package com.yst.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dandelionlvfengli.lib.L;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.guohua.common.util.NetworkUtils;
import com.jack.contacts.AppContext;
import com.jack.contacts.AppContext_city;
import com.jack.fragment.ServiceFragment;
import com.jack.fragment.YiDianFragment;
import com.jack.fragment.YiKuFragment;
import com.jack.headpicselect.NetWorkHelper;
import com.jack.json.JsonBanBenInformation;
import com.ljp.download.service.BanbenUtil;
import com.ljp.download.service.DownloadService;
import com.ljp.download.service.TipsDialog;
import com.ljp.download.service.UpdateVersionSM;
import com.yst.yiku.R;

public class MainActivity extends FragmentActivity implements OnClickListener,
		AMapLocationListener {
	private final static String TAG = "MainActivity";
	private static FragmentManager fMgr;
	private RadioButton rb_yiku, rb_yidian, rb_fuwu;

	public static boolean isLogin = false;
	/** 省市 */
	public static String sheng, shi, qu;
	public static String street;
	/** 定位用到的类 */
	private LocationManagerProxy mAMapLocManager = null;
	int gpsDelay = 15;
	/** 城市定位计数 */
	private int jishishu = 0;
	/** 定位标识 */
	private boolean isDingwei = false;
	private SharedPreferences.Editor editor;
	private SharedPreferences preferences;
	public static RequestQueue requestQueue = null;
	private StringRequest stringRequest5 = null;

	private SharedPreferences msp;
	private String store_id = "-1";
	private Handler handler2 = null;
	private final static int STATE1 = 0;
	private final static int STATE2 = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// FragmentManager
		fMgr = getSupportFragmentManager();
		requestQueue = Volley.newRequestQueue(this);
		if (NetworkUtils.getNetworkState(this) == NetworkUtils.NETWORN_NONE) {
			Toast.makeText(MainActivity.this, "网络尚未连接，请连接网络",
					Toast.LENGTH_SHORT).show();
			// UI.showToast("网络尚未连接，请连接网络");
		} else {
			Log.e(TAG, "==开始城市定位");
			chengshidingwei();
		}
		initView();
		checkUpdate();
		initFragment();
	}

	private void initView() {
		msp = getSharedPreferences("yiku", MODE_PRIVATE);
		rb_yiku = (RadioButton) findViewById(R.id.rb_yiku);
		rb_yidian = (RadioButton) findViewById(R.id.rb_yidian);
		rb_fuwu = (RadioButton) findViewById(R.id.rb_fuwu);
		rb_yiku.setOnClickListener(this);
		rb_yidian.setOnClickListener(this);
		rb_fuwu.setOnClickListener(this);

		if (!msp.getBoolean("logout", false)) {
			if (msp.getBoolean("remember", false)) {
				isLogin = true;
				Customer.getInstance(MainActivity.this).setLogin();
			}
		}
	}

	YiKuFragment weiXinFragment;

	/**
	 * 初始化Fragment
	 */
	private void initFragment() {
		FragmentTransaction ft = fMgr.beginTransaction();
		weiXinFragment = new YiKuFragment();
		ft.add(R.id.fragmentRoot, weiXinFragment, "weiXinFragment");
		ft.addToBackStack("weiXinFragment");
		ft.commit();
	}

	/**
	 * 清空回退站
	 */
	public static void popAllFragmentsExceptTheBottomOne() {
		for (int i = 0, count = fMgr.getBackStackEntryCount() - 1; i < count; i++) {
			fMgr.popBackStack();
		}
	}

	private long time = 0;

	/**
	 * 设置退出提示： 对退出的设置时间监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - time > 2000) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				time = System.currentTimeMillis();
			} else {
				finish();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rb_yiku:
			if (fMgr.findFragmentByTag("weiXinFragment") != null
					&& fMgr.findFragmentByTag("weiXinFragment").isVisible()) {
				return;
			}
			popAllFragmentsExceptTheBottomOne();
			checkedToChange();
			rb_yiku.setTextColor(getResources().getColor(
					R.color.shouye_title_blue));
			rb_yiku.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.mainactivity_yiku1),
					null, null);
			break;
		case R.id.rb_yidian:
			popAllFragmentsExceptTheBottomOne();
			checkedToChange();
			rb_yidian.setTextColor(getResources().getColor(
					R.color.shouye_title_blue));
			rb_yidian
					.setCompoundDrawablesRelativeWithIntrinsicBounds(
							null,
							getResources().getDrawable(
									R.drawable.mainactivity_yidian1), null,
							null);
			// Toast.makeText(MainActivity.this, "该模块正在开发中！",
			// Toast.LENGTH_SHORT)
			// .show();
			FragmentTransaction ft = fMgr.beginTransaction();
			ft.hide(fMgr.findFragmentByTag("weiXinFragment"));
			YiDianFragment sf = new YiDianFragment();
			ft.add(R.id.fragmentRoot, sf, "AddressFragment");
			ft.addToBackStack("FindFragment");
			ft.commit();
			break;
		case R.id.rb_fuwu:
			popAllFragmentsExceptTheBottomOne();
			checkedToChange();
			rb_fuwu.setTextColor(getResources().getColor(
					R.color.shouye_title_blue));
			rb_fuwu.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.mainactivity_fuwu1),
					null, null);
			// Toast.makeText(MainActivity.this, "该模块正在开发中！",
			// Toast.LENGTH_SHORT)
			// .show();
			FragmentTransaction ft2 = fMgr.beginTransaction();
			ft2.hide(fMgr.findFragmentByTag("weiXinFragment"));
			ServiceFragment sf2 = new ServiceFragment();
			ft2.add(R.id.fragmentRoot, sf2, "MeFragment");
			ft2.addToBackStack("MeFragment");
			ft2.commit();
			break;

		default:
			break;
		}
	}

	private void checkedToChange() {
		rb_yiku.setTextColor(getResources().getColor(R.color.shouye_title_gray));
		rb_yidian.setTextColor(getResources().getColor(
				R.color.shouye_title_gray));
		rb_fuwu.setTextColor(getResources().getColor(R.color.shouye_title_gray));
		rb_yiku.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.mainactivity_yiku0),
				null, null);
		rb_yidian.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.mainactivity_yidian0),
				null, null);
		rb_fuwu.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.mainactivity_fuwu0),
				null, null);
	}

	/**
	 * 设置程序字体大小不随系统字体大小的改变而改变
	 */
	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config = new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config, res.getDisplayMetrics());
		return res;
	}

	private void getDefaultStoreTwo() {
		StringRequest stringRequest = new StringRequest(Method.POST,
				Contacts.URL_CHECK_TIHUO_ADDRESS,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.i("sss", "response == " + response);
						JSONObject response2 = null;
						try {
							response2 = new JSONObject(response);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (response2.has("result")) {
							try {
								String result = response2.getString("result");// 获取字段值
								if (result.equalsIgnoreCase("SUCCESS")) {
									if (response2.has("data")) {
										JSONArray data = response2
												.getJSONArray("data");
										JSONObject json = data.getJSONObject(0);
										if (json.has("id")) {
											store_id = json.getString("id");
											Log.i("qcs", "3-STORE_ID = "
													+ store_id);
											weiXinFragment.queryData(store_id);
											weiXinFragment
													.getStoreInfo(store_id);
										}
									}
								} else {// 信息失败
									if (response2.has("error_info")) {// 是否有字段
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				String cityName = AppContext_city.chengshidingweiTM.shi;
				if (cityName.contains("市")) {
					cityName = cityName.replace("市", "");
				}
				Map<String, String> map = new HashMap<String, String>();
				if ("北京".equals(cityName) || "重庆".equals(cityName)
						|| "天津".equals(cityName) || "上海".equals(cityName)) {

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
				map.put("version", "1");
				Log.i("sss", "map == " + map.toString());
				return map;
			}
		};
		requestQueue.add(stringRequest);
	}

	/**
	 * 获取离我最近的店铺的ID
	 */
	private void getDefaultStore() {
		handler2 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case STATE1:
					break;
				case STATE2:
					String result2 = (String) msg.obj;
					if (result2.equals("456")) {
						return;
					} else {
						Log.i("sss", "result2 == " + result2);
						JSONObject response = null;
						try {
							response = new JSONObject(result2);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (response.has("result")) {
							try {
								String result = response.getString("result");// 获取字段值
								if (result.equalsIgnoreCase("SUCCESS")) {
									if (response.has("data")) {
										JSONArray data = response
												.getJSONArray("data");
										JSONObject json = data.getJSONObject(0);
										if (json.has("id")) {
											store_id = json.getString("id");
											Log.i("qcs", "3-STORE_ID = "
													+ store_id);
											weiXinFragment.queryData(store_id);
											weiXinFragment
													.getStoreInfo(store_id);
										}
									}
								} else {// 信息失败
									if (response.has("error_info")) {// 是否有字段
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}

					break;
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				handler2.sendEmptyMessage(STATE1);
				String url = Contacts.URL_CHECK_TIHUO_ADDRESS;
				String map = "";
				String cityName = AppContext_city.chengshidingweiTM.shi;
				if (cityName.contains("市")) {
					cityName = cityName.replace("市", "");
				}
				if ("北京".equals(AppContext_city.chengshidingweiTM.sheng)
						|| "重庆".equals(AppContext_city.chengshidingweiTM.sheng)
						|| "天津".equals(AppContext_city.chengshidingweiTM.sheng)
						|| "上海".equals(AppContext_city.chengshidingweiTM.sheng)) {
					map = "lat=" + AppContext_city.chengshidingweiTM.weidu
							+ "&lng=" + AppContext_city.chengshidingweiTM.weidu
							+ "&customer_lat="
							+ AppContext_city.chengshidingweiTM.weidu
							+ "&customer_lng="
							+ AppContext_city.chengshidingweiTM.jingdu
							+ "&prov=" + cityName + Contacts.URL_ENDING;
				} else {
					map = "lat=" + AppContext_city.chengshidingweiTM.weidu
							+ "&lng=" + AppContext_city.chengshidingweiTM.weidu
							+ "&customer_lat="
							+ AppContext_city.chengshidingweiTM.weidu
							+ "&customer_lng="
							+ AppContext_city.chengshidingweiTM.weidu
							+ "&cityname=" + cityName + Contacts.URL_ENDING;
				}
				Log.d("qcs", "map--" + url + map);
				String result = NetWorkHelper.postImg_Record2(url, map);
				Message msg = handler2.obtainMessage();
				msg.what = STATE2;
				msg.obj = result;
				handler2.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 城市定位
	 */
	private void chengshidingwei() {
		Log.e(TAG, "==isDingwei0");
		mAMapLocManager = LocationManagerProxy.getInstance(this);
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		mAMapLocManager.setGpsEnable(false);
		mAMapLocManager.requestLocationData(LocationProviderProxy.AMapNetwork,
				1000 * 60 * 60, 10, (AMapLocationListener) this);
		// mAMapLocManager.setGpsEnable(false);
		// mAMapLocManager.requestLocationUpdates(
		// LocationProviderProxy.AMapNetwork, 5000, 10, this);
		L.timer.ui("dingweijishiqi", 1, new Runnable() {
			@Override
			public void run() {
				jishishu++;
				if (jishishu == gpsDelay) {
					if (!isDingwei) {
						Log.e(TAG, "==isDingwei1");
						if (AppContext.chengshidingweiTM.shi == null
								|| AppContext.chengshidingweiTM.shi.length() == 0) {
							Log.d("gps", "use default gps location");
							getDefaultLocation();
							// AppContext.chengshidingweiTM.sheng = "北京";
							// AppContext.chengshidingweiTM.shi = "北京市";
							// AppContext.chengshidingweiTM.qu = "朝阳区";
							// AppContext.shiqu_sheng = "北京";
							// AppContext.shiqu_shi = "北京市";
							// AppContext.shiqu_qu = "朝阳区";
							// AppContext.chengshidingweiTM.jingdu = 116.471981;
							// AppContext.chengshidingweiTM.weidu = 39.966036;
						}
						Toast.makeText(MainActivity.this, "定位失败,显示默认地址",
								Toast.LENGTH_SHORT).show();
						isDingwei = true;
						// shouye_GundongguanggaoView.shoyeguanggao();
						// if (mAMapLocManager != null) {
						// mAMapLocManager
						// .removeUpdates(ShouyeActivityNew.this);
						// mAMapLocManager.destory();
					}
				}
				L.timer.stop("dingweijishiqi");
			}
		});
	}

	void getDefaultLocation() {
		String sheng = this.preferences.getString("sheng", "北京");
		String shi = this.preferences.getString("shi", "北京市");
		String qu = this.preferences.getString("qu", "朝阳区");
		String strjingdu = this.preferences.getString("jingdu", "");
		String strweidu = this.preferences.getString("weidu", "");
		String street = this.preferences.getString("street", "");
		double jingdu, weidu;
		if (strjingdu.equalsIgnoreCase("")) {
			weidu = 39.966036;
			jingdu = 116.471981;
		} else {
			jingdu = Double.parseDouble(strjingdu);
			weidu = Double.parseDouble(strweidu);
		}

		AppContext_city.chengshidingweiTM.sheng = sheng;
		AppContext_city.chengshidingweiTM.shi = shi;
		AppContext_city.chengshidingweiTM.qu = qu;
		AppContext_city.chengshidingweiTM.jingdu = jingdu;
		AppContext_city.chengshidingweiTM.weidu = weidu;
		AppContext_city.chengshidingweiTM.street = street;
		isDingwei = true;
		editor.putString("shared_sheng", sheng);
		editor.putString("shared_shi", shi);
		editor.putString("shared_qu", qu);
		editor.commit();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
		}
	}

	/** 定位接口复写 */
	@SuppressLint("NewApi")
	public void onLocationChanged(AMapLocation location) {
		Log.d("gps", "==onLocationChanged enter");

		System.out.println("to location hunhe");
		if (location != null) {
			Log.e(TAG, "==定位成功");
			Toast.makeText(MainActivity.this, "位置信息获取成功", Toast.LENGTH_SHORT)
					.show();
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
			}
			sheng = location.getProvince();
			shi = location.getCity();
			qu = location.getDistrict();
			if (sheng == null || sheng.length() == 0) {
				if (shi != null) {
					sheng = shi.replaceAll("市", "");
				}
			}

			street = location.getExtras().getString("desc", "");
			if (street.trim().length() > 0) {
				String[] streets = street.split(" ");
				if (streets.length > 2) {
					street = "";
					for (int i = 2; i < streets.length; i++) {
						street = street + streets[i];
					}
				}
			}

			Log.d("gps", "==11111success get location and save " + sheng + " "
					+ shi + " " + qu + " " + location.getLongitude() + " "
					+ location.getLatitude() + " " + street);
			Log.e(TAG,
					"==AppContext.chengshidingweiTM.sheng:" + sheng + "___shi:"
							+ shi + "___qu:" + qu + "___jingdu:"
							+ location.getLongitude() + "___weidu:"
							+ location.getLatitude() + "___street" + street
							+ "_address" + location.getAddress());

			// this.saveLocation(sheng, shi, qu, location.getLongitude(),
			// location.getLatitude(), street);

			AppContext_city.chengshidingweiTM.sheng = sheng;
			AppContext_city.chengshidingweiTM.shi = shi;
			AppContext_city.chengshidingweiTM.qu = qu;
			AppContext_city.chengshidingweiTM.jingdu = location.getLongitude();
			AppContext_city.chengshidingweiTM.weidu = location.getLatitude();
			AppContext_city.chengshidingweiTM.street = street;
			// YiKuFragment.textView_yiku_fragment_location
			// .setText(AppContext_city.chengshidingweiTM.street);

			// if (weiXinFragment != null) {
			// Log.e(TAG, "==weiXinFragment != null");
			// weiXinFragment.onCreateView(this.getLayoutInflater(), null,
			// null);
			// }
			store_id = getIntent().getStringExtra("store_id");
			if (store_id == null) {
				store_id = msp.getString("STOREID", "-1");
			}

			if (Integer.parseInt(store_id) == -1) {
				getDefaultStoreTwo();
			} else {
				weiXinFragment.queryData(store_id);
				weiXinFragment.getStoreInfo(store_id);
			}
			Log.i("qcs", "STORE_ID = " + store_id);
			isDingwei = true;
			editor.putString("shared_sheng", sheng);
			editor.putString("shared_shi", shi);
			editor.putString("shared_qu", qu);
			editor.commit();
			// gonggaoneirong(sheng, shi, qu);
			mAMapLocManager.destory();
			location = null;
		} else {
			Log.d("gps", "get location failed,use default location ");
			Log.e(TAG, "==定位失败");
			Toast.makeText(MainActivity.this, "定位失败", Toast.LENGTH_SHORT)
					.show();
			sheng = "北京";
			shi = "北京市";
			qu = "朝阳区";
			street = "霄云路21号嘉里大通305";
			AppContext_city.chengshidingweiTM.sheng = sheng;
			AppContext_city.chengshidingweiTM.shi = shi;
			AppContext_city.chengshidingweiTM.qu = qu;
			AppContext_city.chengshidingweiTM.jingdu = 116.471981;
			AppContext_city.chengshidingweiTM.weidu = 39.966036;
			AppContext_city.chengshidingweiTM.street = "霄云路21号嘉里大通305";
			isDingwei = true;
			Animation ani = AnimationUtils.loadAnimation(this,
					R.anim.grow_from_top);
			editor.putString("shared_sheng", sheng);
			editor.putString("shared_shi", shi);
			editor.putString("shared_qu", qu);
			editor.commit();
			// gonggaoneirong(sheng, shi, qu);
			mAMapLocManager.destory();
		}

		Log.e(TAG, "==AppContext.chengshidingweiTM.street下面的:"
				+ AppContext_city.chengshidingweiTM.street);

	}

	void saveLocation(String sheng, String shi, String qu, double jingdu,
			double weidu, String street) {
		Log.d("gps", "save location");
		Editor editor = preferences.edit();
		editor.putString("sheng", sheng);
		editor.putString("shi", shi);
		editor.putString("qu", qu);
		editor.putString("jindgdu", jingdu + "");
		editor.putString("weidu", weidu + "");
		editor.putString("street", street);
		editor.commit();
	}

	/** 定位接口复写 */
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	/** 定位接口复写 */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	/** 定位接口复写 */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	/** 定位接口复写 */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	static String UPDATE_CHECK = "UPDATE_CHECK";
	/** 更新时用到的进度条等 */
	private ProgressBar progressBar;
	/** 显示进度的view */
	private TextView progressText;
	/** 进度数 */
	public static int loadingProgress;
	/** 版本更新显示界面 */
	private TipsDialog tipsDialog;
	/** 版本更新dialog宽度 */
	private int width;

	void checkUpdate() {
		loadingProgress = 0;
		// 这是获取显示宽度的地方，没有这个是不会显示更新dialog宽度的
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;

		Log.d("update", "begin to check if need update");
		this.banbengengxin();

		/*
		 * long currentTimeLong = new Date().getTime(); long lastTime =
		 * SharePreferenceUtil.getAsLong(this, UPDATE_CHECK, currentTimeLong);
		 * SharePreferenceUtil.setLong(this, UPDATE_CHECK, currentTimeLong);
		 * long timeDifferHour = (currentTimeLong - lastTime) / 1000 / 60 / 60;
		 * if (timeDifferHour > 1.0) { Log.d("update",
		 * "need to check update for an hour has been delay");
		 * this.banbengengxin();
		 * 
		 * } else { Log.d("update", "no need to check update"); }
		 */

	}

	private String xianzaibanben;
	private String gengxinbanben;
	private File file;
	private UpdateVersionSM itemSm;
	final String IMGURL = "http://182.254.161.94:8080/ydg/";
	private static List<Map<String, Object>> list_banbenInfo;

	public void banbengengxin() {
		String urlString = "http://182.254.161.94:8080/ydg/version!getNewApp?platform=Android&app_name=%E6%98%93%E8%AE%A2%E8%B4%AD&client_type=A&version=1";
		stringRequest5 = new StringRequest(urlString, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				if (!arg0.equalsIgnoreCase("[]") && arg0 != null) {
					if (arg0.contains("SUCCESS")) {
						list_banbenInfo = JsonBanBenInformation
								.JsonToList(arg0);
						initDataBanBen();
					}
				}
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(MainActivity.this, "数据加载异常...",
						Toast.LENGTH_SHORT).show();
			}
		});
		// stringRequest.setTag(getActivity());
		requestQueue.add(stringRequest5);
	}

	private void initDataBanBen() {
		xianzaibanben = BanbenUtil.getVerName(MainActivity.this);
		gengxinbanben = list_banbenInfo.get(0).get("app_version").toString();
		AppContext.lujing = list_banbenInfo.get(0).get("url").toString();
		String[] appName = list_banbenInfo.get(0).get("url").toString()
				.split("/");
		String filePath = Environment.getExternalStorageDirectory().getPath()
				+ "/" + "NightMan.apk";
		file = new File(filePath);
		boolean needUpdate = false;
		try {
			System.out.println("----------------3---------------");
			float currentVer = Float.parseFloat(xianzaibanben);
			float serverVer = Float.parseFloat(gengxinbanben);
			System.out.println("current ver is " + currentVer);
			System.out.println("server  ver is " + serverVer);
			if (currentVer < serverVer) {
				System.out.println("need update");
				needUpdate = true;
			} else {
				System.out.println("no need update");
				needUpdate = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (needUpdate) {
			if (file != null && file.exists()) {
				Log.d("update", "delete exist file");
				file.delete();
			}
			if (!L.file.exists(filePath)) {
				BanbenUtil.showSelectDialog(MainActivity.this, "新版本更新内容:\n"
						+ list_banbenInfo.get(0).get("des").toString()
						+ "\n是否更新？", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new Thread() {
							public void run() {
								Message msg = BroadcastHandler.obtainMessage();
								BroadcastHandler.sendMessage(msg);
								// 线程启动下载任务，通过handler传递消息
							}
						}.start();
					}
				}, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						BanbenUtil.alertDialog.dismiss();
						MainActivity.this.finish();
					}
				});

			} else {
				System.out.println("已经有apk");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(file),
						"application/vnd.android.package-archive");
				startActivity(intent);
			}
		}
	}

	/** 版本更新用到的handler */
	private Handler BroadcastHandler = new Handler() {
		public void handleMessage(Message msg) {
			newVerSen();
		}
	};

	// 从IP地址下载文件到本地
	public void loadFile(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);

			HttpEntity entity = response.getEntity();
			float length = entity.getContentLength();

			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				File file = new File(Environment.getExternalStorageDirectory()
						.getPath(), "NightMan.apk");
				fileOutputStream = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int ch = -1;
				float count = 0;
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					count += ch;
					sendMsg(1, (int) (count * 100 / length));
				}
			}
			sendMsg(2, 0);
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (Exception e) {
			sendMsg(-1, 0);
		}
	}

	private void sendMsg(int flag, int c) {
		Message msg = new Message();
		msg.what = flag;
		msg.arg1 = c;
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 1:
					progressBar.setProgress(msg.arg1);
					loadingProgress = msg.arg1;
					progressText.setText("已为您加载了：" + loadingProgress + "%");
					if (loadingProgress > 99)
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								tipsDialog.dismiss();
							}
						}, 1000);
					break;
				case 2:
					// 下载后打开文件
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					startActivity(intent);
					break;
				case -1:
					String error = msg.getData().getString("error");
					Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG)
							.show();
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 显示更新版本的DIAlog
	 */
	private void setDialog(String message, int grivate, Button btnConfirm,
			Button btnCancle, int width, int layout, int txtId, int anim) {
		tipsDialog = TipsDialog.creatTipsDialog(this, width, layout, grivate,
				anim);
		tipsDialog.setCanceledOnTouchOutside(false);

		progressBar = (ProgressBar) tipsDialog.findViewById(R.id.down_pb);
		progressText = (TextView) tipsDialog.findViewById(txtId);
		btnCancle = (Button) tipsDialog.findViewById(R.id.btnCancle);
		btnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						DownloadService.class);
				// 由intent启动service，后台运行下载进程，在服务中调用notifycation状态栏显示进度条
				startService(intent);
				tipsDialog.dismiss();
			}
		});
		tipsDialog.show();
		new Thread() {
			public void run() {
				loadFile(IMGURL + AppContext.lujing);
			}
		}.start();
	}

	/**
	 * 更新版本设置显示dialog
	 */
	private void newVerSen() {
		Button midConfirm = null, midCancle = null;

		setDialog("下载进度提示", Gravity.CENTER, midConfirm, midCancle,
				(width / 6) * 5, R.layout.dialog_tips_mid, R.id.tvId, 0);
	}

	private long enqueue;
	private DownloadManager dm;
	BroadcastReceiver receiver;

	public void downloadApk(String url) {

		Request request = new Request(Uri.parse(url));

		// 设置可用的网络类型
		request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
				| Request.NETWORK_WIFI);

		// 设置是否允许漫游网络 建立请求 默认true
		request.setAllowedOverRoaming(true);

		// 设置状态栏中显示Notification
		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);

		// 设置Notification的标题
		request.setTitle("下载中");

		// 设置Notification的描述
		request.setDescription("精彩即将开始 !!!");

		// 设置下载的目录
		request.setDestinationInExternalPublicDir("A", "abc.apk");

		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

		// 设置请求的Mime
		request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));

		dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

		enqueue = dm.enqueue(request);
	}

	public void registerDownloadListener() {

		receiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
					long downloadId = intent.getLongExtra(
							DownloadManager.EXTRA_DOWNLOAD_ID, 0);

					long id = intent.getExtras().getLong(
							DownloadManager.EXTRA_DOWNLOAD_ID);
					DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
					intent = new Intent(Intent.ACTION_VIEW);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					System.out.println("---dm---"
							+ dm.getUriForDownloadedFile(id));
					intent.setDataAndType(dm.getUriForDownloadedFile(id),
							dm.getMimeTypeForDownloadedFile(id));
					startActivity(intent);
				}
			}
		};

		registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}
}
