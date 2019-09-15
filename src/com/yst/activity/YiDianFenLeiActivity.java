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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guahua.common.cache.DrawableCache;
import com.guahua.common.cache.DrawableDownloadCacheListener;
import com.guohua.common.util.Contacts;
import com.jack.contacts.AppContext_city;
import com.jack.headpicselect.BitmapCache;
import com.jack.ui.MProcessDialog;
import com.jack.ui.XListView;
import com.jack.ui.XListView.IXListViewListener;
import com.yst.yiku.R;

public class YiDianFenLeiActivity extends Activity implements OnClickListener,
		IXListViewListener, OnItemClickListener {
	private LinearLayout linearLayout_sort,// 排序
			linearLayout_merchant,// 商家分类
			linearLayout_activ// 活动
			;
	private View bottom_fengexian;
	private PopupWindow popupWindow;// 选择列表
	private XListView product_listview;
	private ListView popupList;
	private TextView textView1, textView2, textView3, street_tv// title
																// 上的TextView
			;
	private ImageView street_tag,// 距离标志
			login_iv// 登录
			;
	private LinearLayout yidian_title_back;// 返回
	private ImageView imageView1, imageView2, imageView3;// 三个小箭头
	private String[] str = new String[] { "超市","水果",  "蔬菜", "鱼肉", "蛋糕", "牛奶",
			"套餐", "鲜花" };
	private String[] str1 = new String[] { "综合排序", "销量最高", "距离最近" };
	private String[] str2 = new String[] { "满减优惠", "满赠活动 " };

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
	private int[] img2 = new int[]{R.drawable.one,R.drawable.yidian_high,R.drawable.yidian_distance};
	private int[] img3 = new int[]{R.drawable.yidian_mercahant_hui,R.drawable.yidian_send};
	private int POPUPSTATE_ONE = 0;
	private int POPUPSTATE_TWO = 1;
	private int POPUPSTATE_THREE = 2;
	private int popupWindowState = POPUPSTATE_ONE;// 0 商家分类,1 排序 2 优惠
	private List<Map<String,Object>> list1 ,list2 ,list3= null;
	private RequestQueue mQueue = null;
	private ImageLoader imageLoader;
	private String type;
	private List<Map<String, Object>> storeTypes = null; // 分类集合
	private List<Map<String, Object>> storeInfos = null; // 商店集合
	MyPopupWindowAdapter popupWindowAdapter = null;
	MyXListViewAdapter xListViewAdapter = null;
	private int TYPE = 0; // 分类的类别 类型0 商家分类 1 综合排序 2优惠活动
	private int VALUE = 0; // 排序规则
							// 商家分类id
							// 综合排序（1综合排序，2销量最高 3，距离最近）
							// 优惠活动（1满减优惠 2满赠活动）
	private int PAGE_NO = 0;
	private int PAGE_COUNT = 0;
	private Handler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.yidian_fenlei);
		mQueue = Volley.newRequestQueue(this);
		imageLoader = new ImageLoader(mQueue, new BitmapCache());
		initData();
		type = getIntent().getStringExtra("name");
		VALUE = getIntent().getIntExtra("type_id",-1);
		storeTypes = (List<Map<String, Object>>) getIntent().getSerializableExtra("storeTypes");
		initView();
		if (type != null) {
			street_tv.setText(type);
		}
		popupWindowAdapter = new MyPopupWindowAdapter();
		popupWindowAdapter.setList(storeTypes);
		popupList.setAdapter(popupWindowAdapter);
		getStoreMainSortInfo();
	}
	private void initData() {
		list1 = new ArrayList<Map<String,Object>>();
		list2 = new ArrayList<Map<String,Object>>();
		list3 = new ArrayList<Map<String,Object>>();
		
		for(int i = 0;i < img1.length;i ++) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("state", false);
			map.put("img1", img1[i]);
			map.put("img2", img1_1[i]);
			map.put("name", str[i]);
			list1.add(map);
		}
		for(int i = 0;i < img2.length;i ++) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("img", img2[i]);
			map.put("name", str1[i]);
			list2.add(map);
		}
		for(int i = 0;i < img3.length;i ++) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("img", img3[i]);
			map.put("name", str2[i]);
			list3.add(map);
		}
		
	}


	private void initView() {
		storeInfos = new ArrayList<Map<String,Object>>();
		mHandler = new Handler();
		linearLayout_sort = (LinearLayout) findViewById(R.id.linearLayout_sort);
		linearLayout_merchant = (LinearLayout) findViewById(R.id.linearLayout_merchant);
		linearLayout_activ = (LinearLayout) findViewById(R.id.linearLayout_activ);
		bottom_fengexian = (View) findViewById(R.id.bottom_fengexian);
		product_listview = (XListView) findViewById(R.id.product_listview);
		product_listview.setXListViewListener(this);
		product_listview.setPullLoadEnable(true);
		product_listview.setPullRefreshEnable(true);
		product_listview.setDividerHeight(0);
		popupList = new ListView(this);
		popupList.setDividerHeight(0);
		textView1 = (TextView) findViewById(R.id.textView1);
		textView2 = (TextView) findViewById(R.id.textView2);
		textView3 = (TextView) findViewById(R.id.textView3);
		street_tv = (TextView) findViewById(R.id.street_tv);
		yidian_title_back = (LinearLayout) findViewById(R.id.yidian_title_back);
		street_tag = (ImageView) findViewById(R.id.street_tag);
		login_iv = (ImageView) findViewById(R.id.login_iv);
		imageView1 = (ImageView) findViewById(R.id.imageView1);
		imageView2 = (ImageView) findViewById(R.id.imageView2);
		imageView3 = (ImageView) findViewById(R.id.imageView3);

		yidian_title_back.setVisibility(View.VISIBLE);
		login_iv.setVisibility(View.GONE);
		street_tag.setVisibility(View.GONE);

		yidian_title_back.setOnClickListener(this);
		login_iv.setOnClickListener(this);
		linearLayout_sort.setOnClickListener(this);
		linearLayout_merchant.setOnClickListener(this);
		linearLayout_activ.setOnClickListener(this);
		popupList.setOnItemClickListener(this);

		product_listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent(YiDianFenLeiActivity.this,
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


	@Override
	public void onClick(View v) {
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		if (popupWindow == null) {
			popupWindow = new PopupWindow(this);

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
		case R.id.yidian_title_back:
			finish();
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
				convertView = View.inflate(YiDianFenLeiActivity.this,
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
			if(storeInfos.get(position).get("storefavor_sort") != null) {
				storefavorList = (List<HashMap<String, String>>) storeInfos.get(position).get("storefavor_sort");
				Log.i("sss", "storefavor_sort == " + storefavorList.toString());
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
				Intent intent = new Intent(YiDianFenLeiActivity.this,
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

	class MyPopupWindowAdapter extends BaseAdapter {
		private List<Map<String,Object>> list;
		ViewPopupHolder holder = null;
		public void setList(List<Map<String,Object>> list) {
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
				convertView = View.inflate(YiDianFenLeiActivity.this,
						R.layout.popupwindow_listview_item, null);
				holder.popup_product_type_img = (ImageView) convertView.findViewById(R.id.popup_product_type_img);
				holder.popup_product_type_text = (TextView) convertView.findViewById(R.id.popup_product_type_text);
				convertView.setTag(holder);
			}else {
				holder = (ViewPopupHolder) convertView.getTag();
			}
			if (list.size() == 3) {
				holder.popup_product_type_text.setText(str1[position]);
				holder.popup_product_type_img.setImageResource(img2[position]);
			} else if (list.size() == 2) {
				holder.popup_product_type_text.setText(str2[position]);
				holder.popup_product_type_img.setImageResource(img3[position]);
			} else {
				holder.popup_product_type_text.setText(list.get(position).get("industry_name") + "");
				String imgurl = list.get(position).get("image_industry") + "";
				holder.popup_product_type_img.setImageDrawable(DrawableCache
						.getDrawableNew(
								Contacts.URL_ROOT + "/ydg/"
										+ imgurl,
								YiDianFenLeiActivity.this,
								YiDianFenLeiActivity.this.getCacheDir()
										.getAbsolutePath(),
								new DrawableDownloadCacheListener() {

									@Override
									public void returnDrawable(Drawable drawable,
											Object... params) {
										Log.i("qcs", "drawable == " + drawable);
										if(drawable != null){
											holder.popup_product_type_img.setImageDrawable(drawable);
										} else {
											holder.popup_product_type_img.setImageResource(R.drawable.app_default_icon);
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


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		PAGE_NO = 0;
		if(storeInfos != null && xListViewAdapter != null) {
			storeInfos.clear();
			xListViewAdapter.notifyDataSetChanged();
		}
		if (popupWindowState == POPUPSTATE_ONE) {
			TYPE = 0;
			VALUE = Integer.parseInt(storeTypes.get(position).get("id") + "");
			street_tv.setText(storeTypes.get(position).get("industry_name") + "");
			getStoreMainSortInfo();
		} else if (popupWindowState == POPUPSTATE_TWO) {
			VALUE = position + 1;
			TYPE = 1;
			getStoreMainSortInfo();
		} else {
			VALUE = position + 1;
			TYPE = 2;
			getStoreMainSortInfo();
		}

		if (popupWindow != null) {
			popupWindow.dismiss();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
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
			getStoreMainSortInfo();
		} else {
			Toast.makeText(this, "已经加载全部", Toast.LENGTH_SHORT).show();
			product_listview.setPullLoadEnable(false);
			onLoad();
		}
	}

	@Override
	public void onRefresh() {
		product_listview.setPullLoadEnable(true);
		PAGE_NO = 0;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				storeInfos.clear();
				xListViewAdapter.notifyDataSetChanged();
				getStoreMainSortInfo();
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
								Toast.makeText(YiDianFenLeiActivity.this,
										"暂时没有店铺",
										Toast.LENGTH_SHORT).show();
							}
						}
					} else {
						if (response.has("error_info")) {// 是否有字段
							Toast.makeText(YiDianFenLeiActivity.this,
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
				Toast.makeText(getApplicationContext(), "访问网络错误，请重试!",
						Toast.LENGTH_SHORT).show();
			}
		});
		mQueue.add(request);
	}
}
