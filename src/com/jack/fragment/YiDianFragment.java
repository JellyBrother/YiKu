package com.jack.fragment;

import java.io.Serializable;
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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guahua.common.cache.DrawableCache;
import com.guahua.common.cache.DrawableDownloadCacheListener;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.jack.contacts.AppContext_city;
import com.jack.headpicselect.BitmapCache;
import com.jack.headpicselect.NetWorkHelper;
import com.jack.ui.MProcessDialog;
import com.jack.ui.XListView;
import com.jack.ui.XListView.IXListViewListener;
import com.yst.activity.CustomerCenterActivity;
import com.yst.activity.LoginActivity;
import com.yst.activity.MainActivity;
import com.yst.activity.StoreDetailActivity;
import com.yst.activity.YiDianFenLeiActivity;
import com.yst.activity.YiDianStoreGoodsActivity;
import com.yst.yiku.R;

public class YiDianFragment extends Fragment implements IXListViewListener,
		OnClickListener, OnItemClickListener {

	private GridView gridview;
	private XListView product_listview;
	private View view,// rootView;
			include_title// 呵呵
			;
	private TextView street_tv;// 街道
	private ImageView login_iv;
	private String[] str = new String[] { "超市", "水果", "蔬菜", "鱼肉", "蛋糕", "牛奶",
			"套餐", "鲜花" };
	private int[] img = new int[] { R.drawable.market, R.drawable.cai,
			R.drawable.yundong, R.drawable.yu, R.drawable.cake,
			R.drawable.milk, R.drawable.the_meal_box, R.drawable.hua };
	private int[] img1 = new int[] { R.drawable.one, R.drawable.two,
			R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six,
			R.drawable.seven, R.drawable.enghit };
	private int[] img1_1 = new int[] { R.drawable.one_one, R.drawable.two_two,
			R.drawable.three_three, R.drawable.four_four, R.drawable.five_five,
			R.drawable.six_six, R.drawable.seven_seven,
			R.drawable.enghit_enghit };
	private int[] img2 = new int[] { R.drawable.one, R.drawable.yidian_high,
			R.drawable.yidian_distance };
	private int[] img3 = new int[] { R.drawable.yidian_mercahant_hui,
			R.drawable.yidian_send };
	// private LinearLayout yidianfenlei_root;
	private int PAGE_NO = 0;
	private int PAGE_COUNT = 0;
	private LinearLayout linearLayout_sort,// 排序
			linearLayout_merchant,// 商家分类
			linearLayout_activ,// 活动
			yidianfenlei_root // 排序整体
			;
	private PopupWindow popupWindow;// 选择列表
	private ListView popupList;
	private TextView textView1, textView2, textView3;
	private String[] str1 = new String[] { "综合排序", "销量最高", "距离最近" };
	private String[] str2 = new String[] { "满减优惠", "满赠活动 " };
	private int POPUPSTATE_ONE = 0;
	private int POPUPSTATE_TWO = 1;
	private int POPUPSTATE_THREE = 2;
	private int popupWindowState = POPUPSTATE_ONE;// 0 商家分类,1 排序 2 优惠
	private View bottom_fengexian,// 分类下的分隔线
			top_fengexian// 分类上的分隔线
			;
	private ImageView imageView1, imageView2, imageView3;// 三个小箭头
	private List<Map<String, Object>> list1, list2, list3 = null;
	private List<Map<String, Object>> storeTypes = null;
	private List<Map<String, Object>> storeInfos = null;
	private static RequestQueue mQueue = null;
	private ImageLoader imageLoader;
	private int TYPE = 0; // 分类的类别 类型0 商家分类 1 综合排序 2优惠活动
	private int VALUE = 0; // 排序规则
							// 商家分类id
							// 综合排序（1综合排序，2销量最高 3，距离最近）
							// 优惠活动（1满减优惠 2满赠活动）
	private Handler mHandler;
	private boolean ISTYPE = false; // 是否分类

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.yidian_fragment, container, false);
		mQueue = Volley.newRequestQueue(YiDianFragment.this.getActivity());
		imageLoader = new ImageLoader(mQueue, new BitmapCache());
		//getDefaultStore();
		getDefaultStoreTwo();
		initData();
		initView();
		storeInfos = new ArrayList<Map<String, Object>>();
		return view;
	}

	private void initData() {
		list1 = new ArrayList<Map<String, Object>>();
		list2 = new ArrayList<Map<String, Object>>();
		list3 = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < img1.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("state", false);
			map.put("img1", img1[i]);
			map.put("img2", img1_1[i]);
			map.put("name", str[i]);
			list1.add(map);
		}
		for (int i = 0; i < img2.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("img", img2[i]);
			map.put("name", str1[i]);
			list2.add(map);
		}
		for (int i = 0; i < img3.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("img", img3[i]);
			map.put("name", str2[i]);
			list3.add(map);
		}

	}

	private void initView() {
		mHandler = new Handler();
		gridview = (GridView) view.findViewById(R.id.grid_view);
		product_listview = (XListView) view.findViewById(R.id.product_listview);
		product_listview.setXListViewListener(this);
		product_listview.setPullLoadEnable(true);
		product_listview.setPullRefreshEnable(true);
		product_listview.setDividerHeight(0);
		street_tv = (TextView) view.findViewById(R.id.street_tv);
		login_iv = (ImageView) view.findViewById(R.id.login_iv);
		include_title = (View) view.findViewById(R.id.include_title);
		street_tv.setText(AppContext_city.chengshidingweiTM.street);
		linearLayout_sort = (LinearLayout) view
				.findViewById(R.id.linearLayout_sort);
		linearLayout_merchant = (LinearLayout) view
				.findViewById(R.id.linearLayout_merchant);
		linearLayout_activ = (LinearLayout) view
				.findViewById(R.id.linearLayout_activ);
		popupList = new ListView(YiDianFragment.this.getActivity());
		popupList.setDividerHeight(0);
		textView1 = (TextView) view.findViewById(R.id.textView1);
		textView2 = (TextView) view.findViewById(R.id.textView2);
		textView3 = (TextView) view.findViewById(R.id.textView3);
		imageView1 = (ImageView) view.findViewById(R.id.imageView1);
		imageView2 = (ImageView) view.findViewById(R.id.imageView2);
		imageView3 = (ImageView) view.findViewById(R.id.imageView3);
		include_title.setVisibility(View.GONE);
		bottom_fengexian = (View) view.findViewById(R.id.bottom_fengexian);
		top_fengexian = (View) view.findViewById(R.id.top_fengexian);
		yidianfenlei_root = (LinearLayout) view
				.findViewById(R.id.yidianfenlei_root);
		yidianfenlei_root.setVisibility(View.GONE);
		top_fengexian.setVisibility(View.GONE);
		bottom_fengexian.setVisibility(View.GONE);
		gridview.setVisibility(View.GONE);

		login_iv.setOnClickListener(this);
		linearLayout_sort.setOnClickListener(this);
		linearLayout_merchant.setOnClickListener(this);
		linearLayout_activ.setOnClickListener(this);
		popupList.setOnItemClickListener(this);
		// yidianfenlei_root.setOnClickListener(this);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(YiDianFragment.this.getActivity(),
						YiDianFenLeiActivity.class);
				intent.putExtra("type_id",
						Integer.parseInt(storeTypes.get(position).get("id") + ""));
				intent.putExtra("name",
						storeTypes.get(position).get("industry_name") + "");
				intent.putExtra("storeTypes", (Serializable) storeTypes);
				startActivity(intent);
			}
		});
		product_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent(YiDianFragment.this.getActivity(),
						YiDianStoreGoodsActivity.class);
				Log.i("sss", "storeInfos === " + storeInfos.get(position - 1).toString());
				// 店铺的id
				it.putExtra("store_id", storeInfos.get(position - 1).get("id")
						+ "");
				// 店铺的名字
				it.putExtra("store_name",
						storeInfos.get(position - 1).get("name") + "");
				// 店铺的起送价
				double init_price = 0;
				init_price = Double.parseDouble(storeInfos.get(
							position - 1).get("send_low_price") + "");
				it.putExtra("init_price", init_price);
				// 店铺的配送费
				double send_fee = 0;
				send_fee = Double.parseDouble(storeInfos.get(
							position - 1).get("send_fee") + "");
				Log.i("qcs", "send_fee == " + send_fee );
				it.putExtra("send_fee", send_fee);
				// 店铺的logo
				it.putExtra("store_image",
						storeInfos.get(position - 1).get("image") + "");
				// 1是易店 其他是易库
				it.putExtra("is_yd", "1");
				startActivity(it);
			}
		});
	}

	private Handler handler = null;
	private final static int STATE1 = 0;
	private final static int STATE2 = 1;

	/**
	 * 获取默认店铺
	 */
	private void getDefaultStore() {
		showInfoProgressDialog();
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case STATE1:
					break;
				case STATE2:
					String result = (String) msg.obj;
					if (result.equals("456")) {
						dismissInfoProgressDialog();
						return;
					} else {
						Log.i("sss", "result == " + result.toString());
						JSONObject response;
						try {
							response = new JSONObject(result);
							String resultTwo = response.getString("result");
							if (resultTwo.equalsIgnoreCase("SUCCESS")) {
								if(response.has("page_model")) {
									JSONObject page_model = response
											.getJSONObject("page_model");

									PAGE_COUNT = page_model.getInt("pageCount");
								}
								if (response.has("data")) {
									storeTypes = new ArrayList<Map<String, Object>>();
									JSONArray data = response
											.getJSONArray("data");
									JSONObject info = data.getJSONObject(0);
									JSONArray storeInfoArray = info
											.getJSONArray("StoreInfo");
									JSONArray storeTypeArray = info
											.getJSONArray("StoreType");
									for (int i = 0; i < storeInfoArray.length(); i++) {
										Map<String, Object> map = new HashMap<String, Object>();
										JSONObject storeInfo = storeInfoArray
												.getJSONObject(i);
										if (storeInfo.has("id")) { // 店铺名
											map.put("id",
													storeInfo.getString("id"));
										}
										if (storeInfo.has("name")) { // 店铺名
											map.put("name",
													storeInfo.getString("name"));
										}
										if (storeInfo.has("address")) { // 店铺地址
											map.put("address", storeInfo
													.getString("address"));
										}
										if (storeInfo.has("distance")) { // 店铺距离
											map.put("distance", storeInfo
													.getString("distance"));
										}
										if (storeInfo.has("send_time")) { // 店铺配送时间
											map.put("send_time", storeInfo
													.getString("send_time"));
										}
										if (storeInfo.has("sales_sum")) { // 月售
											map.put("sales_sum", storeInfo
													.getString("sales_sum"));
										}
										if (storeInfo
												.has("send_low_price")) { // 起送价
											map.put("send_low_price",
													""
															+ String.format(
																	"%.2f",
																	storeInfo
																			.getDouble("send_low_price") / 100));
										}
										if (storeInfo.has("send_fee")) { // 起运费
											map.put("send_fee",
													""
															+ String.format(
																	"%.2f",
																	storeInfo
																			.getDouble("send_fee") / 100));
										}
										if (storeInfo.has("image")) { // 商店logo
											map.put("image", storeInfo
													.getString("image"));
										}
										
										if(storeInfo.has("StoreFavor")) {
											List<HashMap<String,String>> storeFavorList = new ArrayList<HashMap<String,String>>(); 
											JSONArray storeFavorArray = storeInfo.getJSONArray("StoreFavor");
											for(int m = 0;m < storeFavorArray.length(); m ++) {
												JSONObject storeFavor = storeFavorArray.getJSONObject(m);
												HashMap<String,String> hashmap = new HashMap<String, String>(); 
												if(storeFavor.has("use_condition")) {
													hashmap.put("use_condition", storeFavor.getDouble("use_condition") / 100 + "");
												}
												if(storeFavor.has("favor_money")) {
													hashmap.put("favor_money", storeFavor.getDouble("favor_money") / 100 + "");
												}
												if(storeFavorList.size() < 3) {
													storeFavorList.add(hashmap);
												}
											}
											map.put("storefavor", storeFavorList);
										}
										storeInfos.add(map);
									}
									for (int i = 0; i < storeTypeArray.length(); i++) {
										Map<String, Object> map = new HashMap<String, Object>();
										JSONObject storeType = storeTypeArray
												.getJSONObject(i);

										if (storeType.has("id")) { // 分类ID
											map.put("id",
													storeType.getString("id"));
										}
										if (storeType.has("image_industry")) { // 图片路径
											map.put("image_industry",
													storeType
															.getString("image_industry"));
										}
										if (storeType.has("industry_name")) { // 分类名
											map.put("industry_name", storeType
													.getString("industry_name"));
										}
										map.put("state", false);
										storeTypes.add(map);
									}
									if(popupWindowAdapter == null) {
										popupWindowAdapter = new MyPopupWindowAdapter();
										popupWindowAdapter.setList(storeTypes);
										popupList.setAdapter(popupWindowAdapter);
									}else {
										popupWindowAdapter.notifyDataSetChanged();
									}
									if(gridAdapter == null) {
										gridAdapter = new MyGridAdapter();
										gridview.setAdapter(gridAdapter);
									}else {
										gridAdapter.notifyDataSetChanged();
									}
									if(xListViewAdapter == null) {
										xListViewAdapter = new MyXListViewAdapter();
										product_listview
										.setAdapter(xListViewAdapter);
									}else {
										xListViewAdapter.notifyDataSetChanged();
									}
								}
								Log.i("qcs",
										"storeInfos == "
												+ storeInfos.toString());
							} else {
								Log.i("qcs",
										"response ==  失败 "
												+ response.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					if (storeTypes != null && storeTypes.size() > 0) {
						top_fengexian.setVisibility(View.VISIBLE);
						yidianfenlei_root.setVisibility(View.VISIBLE);
						bottom_fengexian.setVisibility(View.VISIBLE);
						gridview.setVisibility(View.VISIBLE);
					}
					dismissInfoProgressDialog();
					break;
				}
			}
		};

		new Thread(new Runnable() {
			public void run() {
				handler.sendEmptyMessage(STATE1);
				String url = Contacts.URL_YI_DIAN_INDUSTRY;
				String map = "lat=" + AppContext_city.chengshidingweiTM.weidu
						+ "&lng=" + AppContext_city.chengshidingweiTM.jingdu
						/*+ "&cityname=" + AppContext_city.chengshidingweiTM.qu*/
						+ "&page_size=" + 8
						+ "&page_no=" + PAGE_NO
						+ Contacts.URL_ENDING;
				Log.d("qcs", "map--" + url + map);
				String result = NetWorkHelper.postImg_Record2(url, map);
				Message msg = handler.obtainMessage();
				msg.what = STATE2;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	private void getDefaultStoreTwo(){
		showInfoProgressDialog();
		String url = Contacts.URL_YI_DIAN_INDUSTRY;
		String map = "lat=" + AppContext_city.chengshidingweiTM.weidu
				+ "&lng=" + AppContext_city.chengshidingweiTM.jingdu
				+ "&page_size=" + 8
				+ "&page_no=" + PAGE_NO
				+ Contacts.URL_ENDING;
		Log.i("sss", "DefaultStoreTwoUrl ==== " + url + map);
		JsonObjectRequest request = new JsonObjectRequest(url + map, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String resultTwo = response.getString("result");
					if (resultTwo.equalsIgnoreCase("SUCCESS")) {
						if(response.has("page_model")) {
							JSONObject page_model = response
									.getJSONObject("page_model");

							PAGE_COUNT = page_model.getInt("pageCount");
						}
						if (response.has("data")) {
							storeTypes = new ArrayList<Map<String, Object>>();
							JSONArray data = response
									.getJSONArray("data");
							JSONObject info = data.getJSONObject(0);
							JSONArray storeInfoArray = info
									.getJSONArray("StoreInfo");
							JSONArray storeTypeArray = info
									.getJSONArray("StoreType");
							for (int i = 0; i < storeInfoArray.length(); i++) {
								Map<String, Object> map = new HashMap<String, Object>();
								JSONObject storeInfo = storeInfoArray
										.getJSONObject(i);
								if (storeInfo.has("id")) { // 店铺名
									map.put("id",
											storeInfo.getString("id"));
								}
								if (storeInfo.has("name")) { // 店铺名
									map.put("name",
											storeInfo.getString("name"));
								}
								if (storeInfo.has("address")) { // 店铺地址
									map.put("address", storeInfo
											.getString("address"));
								}
								if (storeInfo.has("distance")) { // 店铺距离
									map.put("distance", storeInfo
											.getString("distance"));
								}
								if (storeInfo.has("send_time")) { // 店铺配送时间
									map.put("send_time", storeInfo
											.getString("send_time"));
								}
								if (storeInfo.has("sales_sum")) { // 月售
									map.put("sales_sum", storeInfo
											.getString("sales_sum"));
								}
								if (storeInfo
										.has("send_low_price")) { // 起送价
									map.put("send_low_price",
											""
													+ String.format(
															"%.2f",
															storeInfo
																	.getDouble("send_low_price") / 100));
								}
								if (storeInfo.has("send_fee")) { // 起运费
									map.put("send_fee",
											""
													+ String.format(
															"%.2f",
															storeInfo
																	.getDouble("send_fee") / 100));
								}
								if (storeInfo.has("image")) { // 商店logo
									map.put("image", storeInfo
											.getString("image"));
								}
								
								if(storeInfo.has("StoreFavor")) {
									List<HashMap<String,String>> storeFavorList = new ArrayList<HashMap<String,String>>(); 
									JSONArray storeFavorArray = storeInfo.getJSONArray("StoreFavor");
									for(int m = 0;m < storeFavorArray.length(); m ++) {
										JSONObject storeFavor = storeFavorArray.getJSONObject(m);
										HashMap<String,String> hashmap = new HashMap<String, String>(); 
										if(storeFavor.has("use_condition")) {
											hashmap.put("use_condition", storeFavor.getDouble("use_condition") / 100 + "");
										}
										if(storeFavor.has("favor_money")) {
											hashmap.put("favor_money", storeFavor.getDouble("favor_money") / 100 + "");
										}
										if(storeFavorList.size() < 3) {
											storeFavorList.add(hashmap);
										}
									}
									map.put("storefavor", storeFavorList);
								}
								storeInfos.add(map);
							}
							for (int i = 0; i < storeTypeArray.length(); i++) {
								Map<String, Object> map = new HashMap<String, Object>();
								JSONObject storeType = storeTypeArray
										.getJSONObject(i);

								if (storeType.has("id")) { // 分类ID
									map.put("id",
											storeType.getString("id"));
								}
								if (storeType.has("image_industry")) { // 图片路径
									map.put("image_industry",
											storeType
													.getString("image_industry"));
								}
								if (storeType.has("industry_name")) { // 分类名
									map.put("industry_name", storeType
											.getString("industry_name"));
								}
								map.put("state", false);
								storeTypes.add(map);
							}
							if(popupWindowAdapter == null) {
								popupWindowAdapter = new MyPopupWindowAdapter();
								popupWindowAdapter.setList(storeTypes);
								popupList.setAdapter(popupWindowAdapter);
							}else {
								popupWindowAdapter.notifyDataSetChanged();
							}
							if(gridAdapter == null) {
								gridAdapter = new MyGridAdapter();
								gridview.setAdapter(gridAdapter);
							}else {
								gridAdapter.notifyDataSetChanged();
							}
							if(xListViewAdapter == null) {
								xListViewAdapter = new MyXListViewAdapter();
								product_listview
								.setAdapter(xListViewAdapter);
							}else {
								xListViewAdapter.notifyDataSetChanged();
							}
						}
					} else {
						Toast.makeText(YiDianFragment.this.getActivity(),
								"" + response.getString("error_info"),
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (storeTypes != null && storeTypes.size() > 0) {
					top_fengexian.setVisibility(View.VISIBLE);
					yidianfenlei_root.setVisibility(View.VISIBLE);
					bottom_fengexian.setVisibility(View.VISIBLE);
					gridview.setVisibility(View.VISIBLE);
				}
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(YiDianFragment.this.getActivity(), "访问网络错误，请重试!",
						Toast.LENGTH_SHORT).show();
			}
		});
		mQueue.add(request);
	}
	
	MyPopupWindowAdapter popupWindowAdapter = null;
	MyGridAdapter gridAdapter = null;
	MyXListViewAdapter xListViewAdapter = null;
	ViewPopupHolder holder = null;

	class MyPopupWindowAdapter extends BaseAdapter {
		private List<Map<String, Object>> list;

		public void setList(List<Map<String, Object>> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewPopupHolder();
				convertView = View.inflate(YiDianFragment.this.getActivity(),
						R.layout.popupwindow_listview_item, null);
				holder.popup_product_type_img = (ImageView) convertView
						.findViewById(R.id.popup_product_type_img);
				holder.popup_product_type_text = (TextView) convertView
						.findViewById(R.id.popup_product_type_text);
				convertView.setTag(holder);
			} else {
				holder = (ViewPopupHolder) convertView.getTag();
			}
			if (list.size() == 3) {
				holder.popup_product_type_text.setText(str1[position]);
				holder.popup_product_type_img.setImageResource(img2[position]);
			} else if (list.size() == 2) {
				holder.popup_product_type_text.setText(str2[position]);
				holder.popup_product_type_img.setImageResource(img3[position]);
			} else {
				holder.popup_product_type_text.setText(list.get(position).get(
						"industry_name")
						+ "");
				String imgurl = list.get(position).get("image_industry") + "";
				holder.popup_product_type_img.setImageDrawable(DrawableCache
						.getDrawableNew(Contacts.URL_ROOT + "/ydg/"
								+ imgurl, YiDianFragment.this.getActivity(), YiDianFragment.this.getActivity()
								.getCacheDir().getAbsolutePath(),
								new DrawableDownloadCacheListener() {

									@Override
									public void returnDrawable(
											Drawable drawable, Object... params) {
										Log.i("qcs", "drawable == " + drawable);
										if (drawable != null) {
											holder.popup_product_type_img
													.setImageDrawable(drawable);
										} else {
											holder.popup_product_type_img
													.setImageResource(R.drawable.app_default_icon);
										}
									}
								}));
			}
			View popup_item_fengexian = convertView
					.findViewById(R.id.popup_item_fengexian);
			if (position == 0) {
				popup_item_fengexian.setVisibility(View.GONE);
			} else {
				popup_item_fengexian.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}

	class ViewPopupHolder {
		ImageView popup_product_type_img;
		TextView popup_product_type_text;
	}

	class MyGridAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			int count = 0;
			if(storeTypes.size() <= 8) {
				count = storeTypes.size();
			}else {
				count = 8;
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			// return str[position];
			return storeTypes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(YiDianFragment.this.getActivity(),
						R.layout.yidian_gridview_item, null);
			}
			TextView textView = (TextView) convertView
					.findViewById(R.id.product_type_text);
			NetworkImageView product_type_img = (NetworkImageView) convertView
					.findViewById(R.id.product_type_img);
			textView.setText("" + storeTypes.get(position).get("industry_name"));
			product_type_img.setDefaultImageResId(R.drawable.app_default_icon);
			product_type_img.setErrorImageResId(R.drawable.app_default_icon);
			
			if(storeTypes.get(position).get("image_industry") != null){
				product_type_img.setImageUrl(Contacts.URL_ROOT + "/ydg/" + storeTypes.get(position).get("image_industry") + "", imageLoader);
			} else {
				product_type_img.setImageUrl(Contacts.URL_ROOT, imageLoader);
			}
			return convertView;
		}
	}
	ViewHolder viewHolder = null;

	class MyXListViewAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return storeInfos.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = View.inflate(YiDianFragment.this.getActivity(),
						R.layout.yidian_listview_item, null);
				viewHolder.store_info_img = (ImageView) convertView
						.findViewById(R.id.store_info_img);
				viewHolder.yidian_store_img = (NetworkImageView) convertView
						.findViewById(R.id.yidian_store_img);
				viewHolder.item_fengexian = convertView
						.findViewById(R.id.item_fengexian);
				viewHolder.name = (TextView) convertView
						.findViewById(R.id.yidian_item_name);
				viewHolder.address = (TextView) convertView
						.findViewById(R.id.yidian_store_address);
				viewHolder.distance = (TextView) convertView
						.findViewById(R.id.yidian_distance);
				viewHolder.sendtime = (TextView) convertView
						.findViewById(R.id.yidian_store_sendtime);
				viewHolder.yidian_manjian_one = (TextView) convertView
						.findViewById(R.id.yidian_manjian_one);
				viewHolder.yidian_manjian_two = (TextView) convertView
						.findViewById(R.id.yidian_manjian_two);
				viewHolder.yidian_manjian_three = (TextView) convertView
						.findViewById(R.id.yidian_manjian_three);
				viewHolder.yidian_item_manjian_ll_one = (LinearLayout) convertView
						.findViewById(R.id.yidian_item_manjian_ll_one);
				viewHolder.yidian_item_manjian_ll_two = (LinearLayout) convertView
						.findViewById(R.id.yidian_item_manjian_ll_two);
				viewHolder.yidian_item_manjian_ll_three = (LinearLayout) convertView
						.findViewById(R.id.yidian_item_manjian_ll_three);
				viewHolder.yidian_item_paycount = (TextView) convertView
						.findViewById(R.id.yidian_item_paycount);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				viewHolder.item_fengexian.setVisibility(View.GONE);
			} else {
				viewHolder.item_fengexian.setVisibility(View.VISIBLE);
			}
			viewHolder.store_info_img
					.setOnClickListener(new MyOnClick(position));
			if(storeInfos.get(position).get("name") != null) {
				viewHolder.name.setText(storeInfos.get(position).get("name") + "");
			}else {
				viewHolder.name.setText("");
			}
			if(storeInfos.get(position).get("address") != null) {
				viewHolder.address.setText(storeInfos.get(position).get("address") + "");
			}else {
				viewHolder.address.setText("");
			}
			if(storeInfos.get(position).get("send_time") != null) {
				viewHolder.sendtime.setText(storeInfos.get(position).get("send_time") + "");
			}else {
				viewHolder.sendtime.setText("");
			}
			if(storeInfos.get(position).get("sales_sum") != null) {
				viewHolder.yidian_item_paycount.setText("销售"
						+ storeInfos.get(position).get("sales_sum") + "单");
			}else {
				viewHolder.yidian_item_paycount.setText("销售0"
						+ "单");
			}
			List<HashMap<String,String>> storefavorList = new ArrayList<HashMap<String,String>>();
			if(!ISTYPE) {
				if(storeInfos.get(position).get("storefavor") != null) {
					storefavorList = (List<HashMap<String, String>>) storeInfos.get(position).get("storefavor");
					Log.i("sss", "storefavorList == " + storefavorList.toString());
				}
			}else {
				if(storeInfos.get(position).get("storefavor_sort") != null) {
					storefavorList = (List<HashMap<String, String>>) storeInfos.get(position).get("storefavor_sort");
					Log.i("sss", "storefavor_sort == " + storefavorList.toString());
				}
			}
			if(storefavorList.size() == 1) {
				viewHolder.yidian_item_manjian_ll_one.setVisibility(View.VISIBLE);
				viewHolder.yidian_item_manjian_ll_two.setVisibility(View.INVISIBLE);
				viewHolder.yidian_item_manjian_ll_three.setVisibility(View.INVISIBLE);
				viewHolder.yidian_manjian_one.setText("满" + storefavorList.get(0).get("use_condition") + "减" + storefavorList.get(0).get("favor_money"));
			}else if(storefavorList.size() == 2) {
				viewHolder.yidian_item_manjian_ll_one.setVisibility(View.VISIBLE);
				viewHolder.yidian_item_manjian_ll_two.setVisibility(View.VISIBLE);
				viewHolder.yidian_item_manjian_ll_three.setVisibility(View.INVISIBLE);
				viewHolder.yidian_manjian_one.setText("满" + storefavorList.get(0).get("use_condition") + "减" + storefavorList.get(0).get("favor_money"));
				viewHolder.yidian_manjian_two.setText("满" + storefavorList.get(1).get("use_condition") + "减" + storefavorList.get(1).get("favor_money"));
			}else if(storefavorList.size() == 3) {
				viewHolder.yidian_item_manjian_ll_one.setVisibility(View.VISIBLE);
				viewHolder.yidian_item_manjian_ll_two.setVisibility(View.VISIBLE);
				viewHolder.yidian_item_manjian_ll_three.setVisibility(View.VISIBLE);
				viewHolder.yidian_manjian_one.setText("满" + storefavorList.get(0).get("use_condition") + "减" + storefavorList.get(0).get("favor_money"));
				viewHolder.yidian_manjian_two.setText("满" + storefavorList.get(1).get("use_condition") + "减" + storefavorList.get(1).get("favor_money"));
				viewHolder.yidian_manjian_three.setText("满" + storefavorList.get(2).get("use_condition") + "减" + storefavorList.get(2).get("favor_money"));
			}else {
				viewHolder.yidian_item_manjian_ll_one.setVisibility(View.INVISIBLE);
				viewHolder.yidian_item_manjian_ll_two.setVisibility(View.INVISIBLE);
				viewHolder.yidian_item_manjian_ll_three.setVisibility(View.INVISIBLE);
			}
			String distanceStr = "" + storeInfos.get(position).get("distance");
			if (distanceStr != null) {
				double distance = Double.parseDouble(distanceStr);

				if (distance == 0) {
					viewHolder.distance.setText("0.00m");
				} else if (distance < 1000) {

					if (distance < 1) {
						viewHolder.distance.setText("0"
								+ new DecimalFormat("###.00").format(distance)
										.toString() + "m");
					} else {
						viewHolder.distance.setText(new DecimalFormat("###.00")
								.format(distance).toString() + "m");
					}
				} else {
					viewHolder.distance.setText(new DecimalFormat("###.00")
							.format(distance / 1000).toString() + "km");
				}
			}

			viewHolder.yidian_store_img.setDefaultImageResId(R.drawable.app_default_icon);
			viewHolder.yidian_store_img.setErrorImageResId(R.drawable.app_default_icon);
			
			if(storeInfos.get(position).get("image") != null){
				viewHolder.yidian_store_img.setImageUrl(Contacts.URL_ROOT + "/ydg/" + storeInfos.get(position).get("image") + "", imageLoader);
			} else {
				viewHolder.yidian_store_img.setImageUrl(Contacts.URL_ROOT, imageLoader);
			}
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			return storeInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		class MyOnClick implements OnClickListener {
			private int pos;

			public MyOnClick(int position) {
				this.pos = position;
			}

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(YiDianFragment.this.getActivity(),
						StoreDetailActivity.class);
				intent.putExtra("id", storeInfos.get(pos).get("id") + "");
				intent.putExtra("distance", "" + storeInfos.get(pos).get("distance"));
				startActivity(intent);
			}
		}
	}

	class ViewHolder {
		ImageView store_info_img;
		NetworkImageView yidian_store_img;
		View item_fengexian;
		TextView name,// 名称
				address, // 地址
				distance,// 距离
				sendtime,// 配送时间
				yidian_manjian_one,// 
				yidian_manjian_two,// 
				yidian_manjian_three,// 
				yidian_item_paycount// 月售
				;
		LinearLayout yidian_item_manjian_ll_one,
					 yidian_item_manjian_ll_two,
					 yidian_item_manjian_ll_three
					 ;
	}


	@Override
	public void onClick(View v) {
		DisplayMetrics metric = new DisplayMetrics();
		YiDianFragment.this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		if (popupWindow == null) {
			popupWindow = new PopupWindow(YiDianFragment.this.getActivity());

			ColorDrawable cd = new ColorDrawable(-0000);
			popupWindow.setBackgroundDrawable(cd);
			// 设置尺寸
			popupWindow.setWidth(width);
			popupWindow.setHeight(product_listview.getHeight() * 2 / 3);
			// popupWindow.setAnimationStyle(R.style.AnimTop2);
			popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
			// 设置popupWindow可以接收焦点
			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.update();
			popupWindow.setContentView(popupList);
		}

		switch (v.getId()) {
		case R.id.login_iv:
			if (MainActivity.isLogin) {
				startActivity(new Intent(YiDianFragment.this.getActivity(),
						CustomerCenterActivity.class));
			} else {
				startActivity(new Intent(YiDianFragment.this.getActivity(), LoginActivity.class));
			}
			break;

		case R.id.linearLayout_merchant:
			textView1.setTextColor(Color.parseColor("#00BDFF"));
			textView2.setTextColor(Color.parseColor("#000000"));
			textView3.setTextColor(Color.parseColor("#000000"));
			imageView1.setImageResource(R.drawable.yidian_down_press);
			imageView2.setImageResource(R.drawable.yidian_down_normal);
			imageView3.setImageResource(R.drawable.yidian_down_normal);

			popupWindowState = POPUPSTATE_ONE;
			popupWindowAdapter.setList(storeTypes);
			popupWindowAdapter.notifyDataSetChanged();

			// 显示popupWindow
			if (!popupWindow.isShowing()) {
				popupWindow.showAsDropDown(bottom_fengexian, 0, 0);
			} else {
				popupWindow.dismiss();
			}
			break;
		case R.id.linearLayout_sort:
			textView1.setTextColor(Color.parseColor("#000000"));
			textView2.setTextColor(Color.parseColor("#00BDFF"));
			textView3.setTextColor(Color.parseColor("#000000"));
			imageView1.setImageResource(R.drawable.yidian_down_normal);
			imageView2.setImageResource(R.drawable.yidian_down_press);
			imageView3.setImageResource(R.drawable.yidian_down_normal);
			popupWindowState = POPUPSTATE_TWO;
			popupWindowAdapter.setList(list2);
			popupWindowAdapter.notifyDataSetChanged();

			// 显示popupWindow
			if (!popupWindow.isShowing()) {
				popupWindow.showAsDropDown(bottom_fengexian, 0, 0);
			} else {
				popupWindow.dismiss();
			}
			break;
		case R.id.linearLayout_activ:
			textView1.setTextColor(Color.parseColor("#000000"));
			textView2.setTextColor(Color.parseColor("#000000"));
			textView3.setTextColor(Color.parseColor("#00BDFF"));
			imageView1.setImageResource(R.drawable.yidian_down_normal);
			imageView2.setImageResource(R.drawable.yidian_down_normal);
			imageView3.setImageResource(R.drawable.yidian_down_press);
			popupWindowState = POPUPSTATE_THREE;
			popupWindowAdapter.setList(list3);
			popupWindowAdapter.notifyDataSetChanged();

			// 显示popupWindow
			if (!popupWindow.isShowing()) {
				popupWindow.showAsDropDown(bottom_fengexian, 0, 0);
			} else {
				popupWindow.dismiss();
			}
			break;
		}

		if (popupWindow != null) {
			popupWindow.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss() {
					setMoRenState();
				}
			});
		}
	}

	private void setMoRenState() {
		imageView1.setImageResource(R.drawable.yidian_down_normal);
		imageView2.setImageResource(R.drawable.yidian_down_normal);
		imageView3.setImageResource(R.drawable.yidian_down_normal);
		textView1.setTextColor(Color.parseColor("#000000"));
		textView2.setTextColor(Color.parseColor("#000000"));
		textView3.setTextColor(Color.parseColor("#000000"));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PAGE_NO = 0;
		PAGE_COUNT = 0;
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
		if (popupWindowState == POPUPSTATE_ONE) {// 打开新的页面
			TYPE = 0;
			Intent intent = new Intent(new Intent(YiDianFragment.this.getActivity(),
					YiDianFenLeiActivity.class)); //
			intent.putExtra("name",
					storeTypes.get(position).get("industry_name") + "");
			intent.putExtra("type_id",
					Integer.parseInt(storeTypes.get(position).get("id") + ""));
			intent.putExtra("storeTypes", (Serializable) storeTypes);
			startActivity(intent);

		} else if (popupWindowState == POPUPSTATE_TWO) { // 在当前页面再次请求数据
			if(storeInfos != null && xListViewAdapter != null) {
				storeInfos.clear();
				xListViewAdapter.notifyDataSetChanged();
			}
			VALUE = position + 1;
			ISTYPE = true;
			TYPE = 1;
			getStoreMainSortInfo();
		} else {// 在当前页面再次请求数据
			if(storeInfos != null && xListViewAdapter != null) {
				storeInfos.clear();
				xListViewAdapter.notifyDataSetChanged();
			}
			VALUE = position + 1;
			ISTYPE = true;
			TYPE = 2;
			getStoreMainSortInfo();
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		Log.d("sss", "type == " + TYPE);
		Log.d("sss", "value == " + VALUE);
		//getStoreMainSortInfo();
	}

	private MProcessDialog mInfoProgressDialog;

	public void showInfoProgressDialog() {
		if (mInfoProgressDialog == null)
			mInfoProgressDialog = new MProcessDialog(YiDianFragment.this.getActivity());
		mInfoProgressDialog.setMessage("加载中");
		mInfoProgressDialog.setCancelable(false);
		if (!YiDianFragment.this.getActivity().isFinishing()) {
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
	private void onLoad() {
		product_listview.stopRefresh();
		product_listview.stopLoadMore();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH时mm分");
		String time = format.format(date);
		product_listview.setRefreshTime(time);
	}

	@Override
	public void onLoadMore() {
		showInfoProgressDialog();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadData();
				onLoad();
			}
		}, 500);
	}

	private void loadData() {
		// itemChild.clear();
		if (PAGE_NO + 1 < PAGE_COUNT) {
			Log.e("联网前", PAGE_NO + "====" + PAGE_COUNT);
			PAGE_NO += 1;
			//getDefaultStore();
			if(!ISTYPE) {
				getDefaultStoreTwo();
			}else {
				getStoreMainSortInfo();
			}
		} else {
			Toast.makeText(YiDianFragment.this.getActivity(), "已经加载全部", Toast.LENGTH_SHORT).show();
			dismissInfoProgressDialog();
			onLoad();
		}
	}

	@Override
	public void onRefresh() {
		showInfoProgressDialog();
		PAGE_NO = 0;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				storeInfos.clear();
				xListViewAdapter.notifyDataSetChanged();
				//getDefaultStore();
				if(!ISTYPE) {
					getDefaultStoreTwo();
				}else {
					getStoreMainSortInfo();
				}
			}
		}, 1500);
	}
	
	private void getStoreMainSortInfo() {
		showInfoProgressDialog();
		String url = Contacts.URL_YI_DIAN_INDUSTRY_SORT 
					+ "lat=" + AppContext_city.chengshidingweiTM.weidu
					+ "&lng=" + AppContext_city.chengshidingweiTM.jingdu
					+ "&type=" + TYPE
					+ "&value=" + VALUE
					+ "&page_size=" + 8
					+ "&page_no=" + PAGE_NO
					+ Contacts.URL_ENDING;
		Log.i("sss", "StoreMainSortInfoUrl ========== " + url);
		JsonObjectRequest request = new JsonObjectRequest(url, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					String resultTwo = response.getString("result");
					if (resultTwo.equalsIgnoreCase("SUCCESS")) {
						if(response.has("page_model")) {
							JSONObject page_model = response
									.getJSONObject("page_model");

							PAGE_COUNT = page_model.getInt("pageCount");
						}
						if (response.has("data")) {
							JSONArray data = response
									.getJSONArray("data");
							JSONObject info = data.getJSONObject(0);
							JSONArray storeInfoArray = info
									.getJSONArray("StoreInfo");
							for (int i = 0; i < storeInfoArray.length(); i++) {
								Map<String, Object> map = new HashMap<String, Object>();
								JSONObject storeInfo = storeInfoArray
										.getJSONObject(i);
								if (storeInfo.has("id")) { // 店铺名
									map.put("id",
											storeInfo.getString("id"));
								}
								if (storeInfo.has("name")) { // 店铺名
									map.put("name",
											storeInfo.getString("name"));
								}
								if (storeInfo.has("address")) { // 店铺地址
									map.put("address", storeInfo
											.getString("address"));
								}
								if (storeInfo.has("distance")) { // 店铺距离
									map.put("distance", storeInfo
											.getString("distance"));
								}
								if (storeInfo.has("send_time")) { // 店铺配送时间
									map.put("send_time", storeInfo
											.getString("send_time"));
								}
								if (storeInfo.has("sales_sum")) { // 月售
									map.put("sales_sum", storeInfo
											.getString("sales_sum"));
								}
								if (storeInfo
										.has("send_low_price")) { // 起送价
									map.put("send_low_price",
											""
													+ String.format(
															"%.2f",
															storeInfo
																	.getDouble("send_low_price") / 100));
								}
								if (storeInfo.has("send_fee")) { // 起运费
									map.put("send_fee",
											""
													+ String.format(
															"%.2f",
															storeInfo
																	.getDouble("send_fee") / 100));
								}
								if (storeInfo.has("image")) { // 商店logo
									map.put("image", storeInfo
											.getString("image"));
								}
								
								if(storeInfo.has("StoreFavor")) {
									List<HashMap<String,String>> storeFavorList = new ArrayList<HashMap<String,String>>(); 
									JSONArray storeFavorArray = storeInfo.getJSONArray("StoreFavor");
									for(int m = 0;m < storeFavorArray.length(); m ++) {
										JSONObject storeFavor = storeFavorArray.getJSONObject(m);
										HashMap<String,String> hashmap = new HashMap<String, String>(); 
										if(storeFavor.has("use_condition")) {
											hashmap.put("use_condition", storeFavor.getDouble("use_condition") / 100 + "");
										}
										if(storeFavor.has("favor_money")) {
											hashmap.put("favor_money", storeFavor.getDouble("favor_money") / 100 + "");
										}
										if(storeFavorList.size() < 3) {
											storeFavorList.add(hashmap);
										}
									}
									map.put("storefavor_sort", storeFavorList);
								}
								storeInfos.add(map);
							}
							if(xListViewAdapter == null) {
								xListViewAdapter = new MyXListViewAdapter();
								product_listview
								.setAdapter(xListViewAdapter);
							}else {
								xListViewAdapter.notifyDataSetChanged();
							}
							if(storeInfos.size() == 0) {
								Toast.makeText(YiDianFragment.this.getActivity(),
										"暂时没有店铺",
										Toast.LENGTH_SHORT).show();
							}
						}
					} else {
						if (response.has("error_info")) {// 是否有字段
							Toast.makeText(YiDianFragment.this.getActivity(),
									"" + response.getString("error_info"),
									Toast.LENGTH_SHORT).show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(YiDianFragment.this.getActivity(), "访问网络错误，请重试!",
						Toast.LENGTH_SHORT).show();
			}
		});
		mQueue.add(request);
	}
}
