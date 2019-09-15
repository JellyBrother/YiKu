package com.yst.activity;

/**
 * 订单确认
 */
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.guohua.common.util.ProductBean;
import com.jack.fragment.YiKuFragment;
import com.jack.headpicselect.NetWorkHelper;
import com.jack.ui.MProcessDialog;
import com.jack.ui.PickerDialog;
import com.jack.ui.PickerDialog.ICheckTime;
import com.yst.yiku.R;

public class SubmitOrderActivity extends Activity implements OnClickListener,
		OnItemClickListener {
	private View manjian_hengxian,// 分隔线
			youhui_hengxian// 备注下面的横线
			;

	private ListView order_listview,// 订单列表
			shouhuo_address_listview// 地址列表
			;
	private RelativeLayout pay_line_relativelayout,// 在线支付
			huodaofukuan_relativelayout,// 货到付款
			kaijufapiao_relativelayout,// 开具发票
			beizhu_relativelayout,// 备注
			youhui_relativelayout,// 优惠卷
			manjian_relativelayout// 满减优惠的布局
			;
	private LinearLayout order_ziti_linearlayout,// 自提整体布局
			songda_time_linearlayout,// 选择时间
			shouhuo_mode_bottom_wuliu,// 最下方地址--模式--物流
			shouhuo_mode_bottom_ziqu,// 最下方地址--模式--自取
			order_submit_activity_one,// 配送费,满减整体布局
			peisong_fee_ll// 配送费整体布局
			;
	private ImageView pay_line_iv,// 在线支付
			huodaofukuan_iv,// 货到付款
			order_submit_back_iv// 回退
			;
	private TextView go_wuiu,// 物流
			go_ziti,// 自提
			order_songda_time,// 送达时间
			songda_time_tag,// 送达时间标题
			shouhuo_mode_bottom_wuliu_address,// 下方悬浮收货地址物流
			shouhuo_mode_bottom_ziqu_address,// 下方悬浮收货地址自取
			order_beizhu_tv, // 备注控件
			all_price_tv, // 总价格
			no_moren_address, // 还没有收货地址
			youhui_tv, // 最下边已优惠多少钱鬼的
			order_fapiao_tv, // 发票内容显示
			order_youhuiquan_tv // 优惠券文字描述
			;
	// 下方悬浮收货模式
	private Button go_order_btn;// 去下单
	private PickerDialog pickerDialog;// 事件选择对话框
	private String customer_id = "";// 用户ID
	private RequestQueue mQueue;// 请求
	// 地址列表集合
	private ArrayList<HashMap<String, Object>> addressList = null;
	private TextView peisong_linearlayout_merchant;// 配送房信息
	private TextView peisong_iv_price,// 配送费
			manjian_iv; // 满减
	// 地址adapter
	private MyAddressAdapter addressAdapter;
	private Handler handler = null;
	private final static int STATE1 = 0;
	private final static int STATE2 = 1;
	// 商品列表集合
	private ArrayList<ProductBean> productBeans = null;
	// 商品列表Array
	private JSONArray jsonArray = null;
	// json字符串,所选商品列表
	private String jsonString = null;
	// 店铺的id
	private String store_id = "";
	// 发票类型
	private String fapiao_type = "";
	// 发票抬头
	private String fapiao_title = "";
	// 发票模式
	private String fapiao_mode = "";
	// 送货时间
	private String send_time = "";
	// 备注
	private String order_comm = "";
	// 优惠卷ID
	private String customer_favor_id = "";
	// 店铺优惠ID
	private String store_favor_id = "";
	// 配送方式
	private int send_type = 2; // 1 是自提 ,2 为送货
	// 支付模式
	private int pay_online = 0; // 支付方式 0 在线 1 货到付款 默认0

	private HashMap<String, String> orderMap = null;
	private MProcessDialog mInfoProgressDialog;
	private TextView ziti_address_name;// 自提地址
	private String newTime;// 默认时间
	private String old_order_id;// 旧的订单ID

	private boolean IS_XIANSHI_NO_MORE_TEXTVIEW = true;// 是否显示该控件
														// (no_moren_address)
	private double new_all = 0; // 重新计算的价格
	private double use_condition = 0; // 易店满减优惠价钱
	private String is_yd = ""; // 是否是易店
	private boolean IS_GET_ADDRESS = true; // 是否需要再次请球用户地址

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_submit_activity);

		initView();

		initData();
	}

	/**
	 * 获取订单所有的所需信息
	 */
	private void initData() {
		all = 0;
		Intent intent = getIntent();
		index = intent.getIntExtra("index", -1);
		store_id = intent.getStringExtra("store_id");

		Log.e("sss", "ssssssss ====== " + index);
		Log.e("sss", "store_id ====== " + store_id);

		if (index == -1) {
			return;
		} else if (index == 1) { // 易库
			manjian_hengxian.setVisibility(View.GONE);
			manjian_relativelayout.setVisibility(View.GONE);
			youhui_hengxian.setVisibility(View.VISIBLE);
			youhui_relativelayout.setVisibility(View.VISIBLE);
			productBeans = new ArrayList<ProductBean>();
			list = (List<Map<String, String>>) intent
					.getSerializableExtra("cart_products");
			Log.i("sss", "list == " + list.toString());
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map = list.get(i);
				Log.i("qcs", map.toString());
				ProductBean productBean = new ProductBean();
				id = Integer.parseInt(map.get("id"));
				count = Integer.parseInt(map.get("count"));
				price = Double.parseDouble(map.get("price"));
				allPrice = price * count;
				all += allPrice;
				name = map.get("name");

				productBean.setProduct_id(id);
				productBean.setProduct_count(count);
				productBean.setProduct_all_price(allPrice);
				productBean.setProduct_name(name);
				productBeans.add(productBean);
			}

			changeArrayListToJson();
			productAdapter = new MyProductAdapter();
			order_listview.setAdapter(productAdapter);
			setListViewHeightBasedOnChildren(order_listview);
			getStoreInfo();
		} else if (index == 2) { // 易店
			manjian_hengxian.setVisibility(View.VISIBLE);
			manjian_relativelayout.setVisibility(View.VISIBLE);
			youhui_hengxian.setVisibility(View.GONE);
			youhui_relativelayout.setVisibility(View.GONE);
			manjian_iv.setText("-¥0");
			
			productBeans = new ArrayList<ProductBean>();
			list = (List<Map<String, String>>) intent
					.getSerializableExtra("cart_products");
			for (int i = 0; i < list.size(); i++) {
				Map<String, String> map = list.get(i);
				Log.i("qcs", map.toString());
				ProductBean productBean = new ProductBean();
				id = Integer.parseInt(map.get("id"));
				count = Integer.parseInt(map.get("count"));
				price = Double.parseDouble(map.get("price"));
				allPrice = price * count;
				all += allPrice;
				name = map.get("name");

				productBean.setProduct_id(id);
				productBean.setProduct_count(count);
				productBean.setProduct_all_price(allPrice);
				productBean.setProduct_name(name);
				productBeans.add(productBean);
			}

			/*
			 * ziti_address_name.setText("你想在哪提货"); all_price_tv .setText(Html
			 * .fromHtml
			 * ("<font color=\"#000000\">共</font>&nbsp<font color=\"#FEB010\">¥"
			 * + String.format("%.2f", all) + "</font>"));
			 * peisong_linearlayout_merchant.setText("本订单由" + "你想在哪提货" +
			 * "提供配送服务");
			 */
			changeArrayListToJson();
			productAdapter = new MyProductAdapter();
			order_listview.setAdapter(productAdapter);
			setListViewHeightBasedOnChildren(order_listview);
			
			getStoreFavorList();
		} else if (index == 3) { // 再来一单
			youhui_relativelayout.setVisibility(View.GONE);
			manjian_relativelayout.setVisibility(View.GONE);
			manjian_hengxian.setVisibility(View.GONE);
			youhui_hengxian.setVisibility(View.GONE);
			youhui_relativelayout.setVisibility(View.GONE);
			old_order_id = intent.getStringExtra("PRODUCT_INFO");
			list = new ArrayList<Map<String, String>>();
			productBeans = new ArrayList<ProductBean>();
			Log.i("qcs", "old_order_id == " + old_order_id);
			getOrderInfo();
		}

	}
	/**
	 * 获取店铺优惠信息列表
	 */
	ArrayList<HashMap<String,String>> store_favor_list = null;
	protected double store_favor_money = 0;// 商店优惠价格，默认为0 
	private void getStoreFavorList() {
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest(
			Contacts.URL_YI_DIAN_QUERY_STOREFAVOR_LIST + "store_id=" + store_id
				+ Contacts.URL_ENDING, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						Log.i("qcs", "response == " + response.toString());
						try {
							if ("SUCCESS".equals(response.getString("result"))) {
								if (response.has("data")) {
									JSONArray dataArray = response
											.getJSONArray("data");
									store_favor_list = new ArrayList<HashMap<String,String>>();
									for(int i = 0;i < dataArray.length();i ++) {
										HashMap<String,String> favorMap = favorMap = new HashMap<String, String>();;
										JSONObject OBJ = dataArray.getJSONObject(i);
										if(OBJ.has("id")) {
											favorMap.put("id", OBJ.getString("id"));
										}
										if(OBJ.has("use_condition")) {
											favorMap.put("use_condition", Double.parseDouble(OBJ.getString("use_condition")) / 100 + "");
										}
										if(OBJ.has("favor_money")) {
											favorMap.put("favor_money",Double.parseDouble(OBJ.getString("favor_money")) / 100 + "");
										}
										Log.i("sss", "use_condition == " + Double.parseDouble(favorMap.get("use_condition")));
										Log.i("sss", "all == " + all);
										if(all >= Double.parseDouble(favorMap.get("use_condition"))) {
											store_favor_list.add(favorMap);
										}
									}
									Log.i("sss", "store_favor_list == " + store_favor_list.size());
									if(store_favor_list.size() > 0){
										double[] use_condition = new double[store_favor_list.size()];
										double[] favor_money = new double[store_favor_list.size()];
										String[] id = new String[store_favor_list.size()];
										
										for(int i = 0; i < store_favor_list.size();i ++) {
											use_condition[i] = Double.parseDouble(store_favor_list.get(i).get("use_condition"));
											favor_money[i] = Double.parseDouble(store_favor_list.get(i).get("favor_money"));
											id[i] = store_favor_list.get(i).get("id");
										}
										for(int i = 0; i < 1;i ++) {
											for(int j = i + 1;j < use_condition.length;j ++) {
												// 从大到小
												if(use_condition[i] < use_condition[j]) {
													double use_condition_temp = use_condition[i];
													use_condition[i] = use_condition[j];
													use_condition[j] = use_condition_temp;
													
													double favor_money_temp = favor_money[i];
													favor_money[i] = favor_money[j];
													favor_money[j] = favor_money_temp;
													
													String id_temp = id[i];
													id[i] = id[j];
													id[j] = id_temp;
												}
											}
										}
										
										store_favor_money = favor_money[0];
										Log.i("sss", "store_favor_money === " + store_favor_money);
										store_favor_id= id[0];
										Log.i("sss", "store_favor_id === " + store_favor_id);
									}
									manjian_iv.setText("-¥" + String.format("%.2f",store_favor_money));
									youhui_tv.setText("已优惠¥" + String.format("%.2f",store_favor_money));										
									getStoreInfo();
									}
							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"),
										Toast.LENGTH_SHORT).show();
							}
							dismissInfoProgressDialog();
						} catch (JSONException e) {
							e.printStackTrace();
							dismissInfoProgressDialog();
							Toast.makeText(getApplicationContext(), "数据解析异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
						Toast.makeText(getApplicationContext(), "访问网络错误，请重试!",
								Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(jsr);
	}

	/**
	 * 获取订单信息
	 */
	private void getOrderInfo() {

		showInfoProgressDialog();

		Log.i("qcs", "orderurl == " + Contacts.URL_GET_ORDER_DETAIL_INFO
				+ "order_id=" + old_order_id + Contacts.URL_ENDING);
		JsonObjectRequest jsr = new JsonObjectRequest(
				Contacts.URL_GET_ORDER_DETAIL_INFO + "order_id=" + old_order_id
						+ Contacts.URL_ENDING, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						Log.i("qcs", "response == " + response.toString());
						try {
							if ("SUCCESS".equals(response.getString("result"))) {
								if (response.has("data")) {
									JSONObject dataObj = response
											.getJSONObject("data");
									if (dataObj.has("store_id")) {
										store_id = dataObj
												.getString("store_id");
									}
									if (dataObj.has("is_yd")) {
										is_yd = dataObj.getString("is_yd");
										Log.i("qcs", "is_yd == " + is_yd);
										if ("1".equals(is_yd)) {
											manjian_relativelayout
													.setVisibility(View.VISIBLE);
											manjian_hengxian
													.setVisibility(View.VISIBLE);
											youhui_hengxian
											.setVisibility(View.GONE);
											youhui_relativelayout
											.setVisibility(View.GONE);
											getStoreFavorList();
										} else {
											manjian_relativelayout
											.setVisibility(View.GONE);
											manjian_hengxian
											.setVisibility(View.GONE);
											youhui_hengxian
													.setVisibility(View.VISIBLE);
											youhui_relativelayout
													.setVisibility(View.VISIBLE);
											getStoreInfo();
										}
									}

									if (dataObj.has("product_order_item")) {
										JSONArray arrayProduct = dataObj
												.getJSONArray("product_order_item");
										for (int i = 0; i < arrayProduct
												.length(); i++) {
											Map<String, String> map = new HashMap<String, String>();
											JSONObject product_info = arrayProduct
													.getJSONObject(i);
											if (product_info.has("count")) {// 数量
												map.put("count", product_info
														.getString("count"));
											}
											if (product_info
													.has("store_prod_id")) {// store_prod_id
												map.put("store_prod_id",
														product_info
																.getString("store_prod_id"));
											}
											if (product_info.has("name")) {// 名称
												map.put("name", product_info
														.getString("name"));
											}
											if (product_info.has("price")) {// 单价
												map.put("price",
														(Double.parseDouble(product_info
																.getString("price")) / 100)
																+ "");
											}
											if (product_info.has("property")) {// 特性
												map.put("pro",
														product_info
																.getString("property"));
											}
											list.add(map);
										}

									}

									for (int i = 0; i < list.size(); i++) {
										Map<String, String> map = list.get(i);
										Log.i("qcs", map.toString());
										ProductBean productBean = new ProductBean();
										id = Integer.parseInt(map
												.get("store_prod_id"));
										count = Integer.parseInt(map
												.get("count"));
										price = Double.parseDouble(map
												.get("price"));
										allPrice = price * count;
										all += allPrice;
										name = map.get("name");

										productBean.setProduct_id(id);
										productBean.setProduct_count(count);
										productBean
												.setProduct_all_price(allPrice);
										productBean.setProduct_name(name);
										productBeans.add(productBean);
									}
								}

								changeArrayListToJson();
								productAdapter = new MyProductAdapter();
								order_listview.setAdapter(productAdapter);
								setListViewHeightBasedOnChildren(order_listview);

							} else {
								Toast.makeText(getApplicationContext(),
										"" + response.getString("error_info"),
										Toast.LENGTH_SHORT).show();
							}
							dismissInfoProgressDialog();
						} catch (JSONException e) {
							e.printStackTrace();
							dismissInfoProgressDialog();
							Toast.makeText(getApplicationContext(), "数据解析异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
						Toast.makeText(getApplicationContext(), "访问网络错误，请重试!",
								Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(jsr);
	}

	MyProductAdapter productAdapter = null;

	/**
	 * 将商品列表转为json格式的数据
	 */
	private void changeArrayListToJson() {
		jsonArray = new JSONArray();
		try {
			for (int i = 0; i < productBeans.size(); i++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("count", ""
						+ productBeans.get(i).getProduct_count());
				jsonObject.put("store_prod_id", ""
						+ productBeans.get(i).getProduct_id());
				jsonArray.put(jsonObject);
			}

			jsonString = jsonArray.toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		//
		mQueue = Volley.newRequestQueue(this);
		// 实例化控件
		order_listview = (ListView) findViewById(R.id.order_listview);
		order_listview.setDividerHeight(0);
		order_listview.setFocusable(false);
		shouhuo_address_listview = (ListView) findViewById(R.id.shouhuo_address_listview);
		shouhuo_address_listview.setDividerHeight(0);
		shouhuo_address_listview.setFocusable(false);
		pay_line_relativelayout = (RelativeLayout) findViewById(R.id.pay_line_relativelayout);
		pay_line_iv = (ImageView) findViewById(R.id.pay_line_iv);
		huodaofukuan_relativelayout = (RelativeLayout) findViewById(R.id.huodaofukuan_relativelayout);
		huodaofukuan_iv = (ImageView) findViewById(R.id.huodaofukuan_iv);
		kaijufapiao_relativelayout = (RelativeLayout) findViewById(R.id.kaijufapiao_relativelayout);
		go_wuiu = (TextView) findViewById(R.id.go_wuliu);
		go_ziti = (TextView) findViewById(R.id.go_ziti);
		order_ziti_linearlayout = (LinearLayout) findViewById(R.id.order_ziti_linearlayout);
		order_songda_time = (TextView) findViewById(R.id.order_songda_time);
		beizhu_relativelayout = (RelativeLayout) findViewById(R.id.beizhu_relativelayout);
		songda_time_linearlayout = (LinearLayout) findViewById(R.id.songda_time_linearlayout);
		songda_time_tag = (TextView) findViewById(R.id.songda_time_tag);
		go_order_btn = (Button) findViewById(R.id.go_order_btn);
		shouhuo_mode_bottom_wuliu_address = (TextView) findViewById(R.id.shouhuo_mode_bottom_wuliu_address);
		shouhuo_mode_bottom_ziqu_address = (TextView) findViewById(R.id.shouhuo_mode_bottom_ziqu_address);
		order_submit_back_iv = (ImageView) findViewById(R.id.order_submit_back_iv);
		shouhuo_mode_bottom_wuliu = (LinearLayout) findViewById(R.id.shouhuo_mode_bottom_wuliu);
		shouhuo_mode_bottom_ziqu = (LinearLayout) findViewById(R.id.shouhuo_mode_bottom_ziqu);
		order_beizhu_tv = (TextView) findViewById(R.id.order_beizhu_tv);
		youhui_relativelayout = (RelativeLayout) findViewById(R.id.youhui_relativelayout);
		ziti_address_name = (TextView) findViewById(R.id.ziti_address_name);
		all_price_tv = (TextView) findViewById(R.id.all_price_tv);
		peisong_linearlayout_merchant = (TextView) findViewById(R.id.peisong_linearlayout_merchant);
		peisong_iv_price = (TextView) findViewById(R.id.peisong_iv_price);
		no_moren_address = (TextView) findViewById(R.id.no_moren_address);
		youhui_tv = (TextView) findViewById(R.id.youhui_tv);
		order_submit_activity_one = (LinearLayout) findViewById(R.id.order_submit_activity_one);
		manjian_iv = (TextView) findViewById(R.id.manjian_iv);
		manjian_relativelayout = (RelativeLayout) findViewById(R.id.manjian_relativelayout);
		manjian_hengxian = findViewById(R.id.manjian_hengxian);
		youhui_hengxian = findViewById(R.id.youhui_hengxian);
		peisong_fee_ll = (LinearLayout) findViewById(R.id.peisong_fee_ll);
		order_fapiao_tv = (TextView) findViewById(R.id.order_fapiao_tv);
		order_youhuiquan_tv = (TextView) findViewById(R.id.order_youhuiquan_tv);
		// 设置点击监听
		no_moren_address.setOnClickListener(this);
		youhui_relativelayout.setOnClickListener(this);
		order_submit_back_iv.setOnClickListener(this);
		go_order_btn.setOnClickListener(this);
		songda_time_linearlayout.setOnClickListener(this);
		beizhu_relativelayout.setOnClickListener(this);
		go_wuiu.setOnClickListener(this);
		go_ziti.setOnClickListener(this);
		kaijufapiao_relativelayout.setOnClickListener(this);
		huodaofukuan_relativelayout.setOnClickListener(this);
		pay_line_relativelayout.setOnClickListener(this);
		shouhuo_address_listview.setOnItemClickListener(this);

	}

	/**
	 * 订单Adapter
	 */
	class MyProductAdapter extends BaseAdapter {
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
			productHolder = null;
			if (convertView == null) {
				productHolder = new ProductHolder();
				convertView = View.inflate(SubmitOrderActivity.this,
						R.layout.order_submit_listview_item, null);
				productHolder.order_product_name = (TextView) convertView
						.findViewById(R.id.order_product_name);
				productHolder.order_product_count = (TextView) convertView
						.findViewById(R.id.order_product_count);
				productHolder.order_product_price = (TextView) convertView
						.findViewById(R.id.order_product_price);
				productHolder.product_list_hengxian = convertView
						.findViewById(R.id.product_list_hengxian);
				convertView.setTag(productHolder);
			} else {
				productHolder = (ProductHolder) convertView.getTag();
			}

			if (position == getCount() - 1) {
				productHolder.product_list_hengxian.setVisibility(View.GONE);
			} else {
				productHolder.product_list_hengxian.setVisibility(View.VISIBLE);
			}

			int count = Integer.parseInt(list.get(position).get("count"));
			double price = Double.parseDouble(list.get(position).get("price"));
			productHolder.order_product_name.setText(list.get(position).get(
					"name") + "("+ list.get(position).get("pro") +")");
			productHolder.order_product_count.setText("×"
					+ list.get(position).get("count"));
			productHolder.order_product_price.setText("¥ "
					+ String.format("%.2f", price));
			return convertView;
		}
	}

	ProductHolder productHolder;

	class ProductHolder {
		private TextView order_product_name,// 商品名
				order_product_count,// 商品数量
				order_product_price// 商品价格
				;
		private View product_list_hengxian;// 分隔线
	}

	/**
	 * 地址Adapter
	 */
	class MyAddressAdapter extends BaseAdapter {
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
			addressHolder = null;
			if (convertView == null) {
				addressHolder = new AddressHolder();
				convertView = View.inflate(SubmitOrderActivity.this,
						R.layout.address_listview_item, null);
				addressHolder.shouhuo_check = (ImageView) convertView
						.findViewById(R.id.shouhuo_check);
				addressHolder.shouhuo_name = (TextView) convertView
						.findViewById(R.id.shouhuo_name);
				addressHolder.shouhuo_phone = (TextView) convertView
						.findViewById(R.id.shouhuo_phone);
				addressHolder.shouhuo_address = (TextView) convertView
						.findViewById(R.id.shouhuo_address);
				// addressHolder.caidai_view_bottom =
				// convertView.findViewById(R.id.caidai_view_bottom);
				// addressHolder.caidai_view_top =
				// convertView.findViewById(R.id.caidai_view_top);
				convertView.setTag(addressHolder);
			} else {
				addressHolder = (AddressHolder) convertView.getTag();
			}

			String name = (String) addressList.get(position).get("name");
			if (name != null) {
				addressHolder.shouhuo_name.setText(name);
			} else {
				addressHolder.shouhuo_name.setText("");
			}
			String address = (String) addressList.get(position).get("address");
			if (address != null) {
				addressHolder.shouhuo_address.setText(address);
			} else {
				addressHolder.shouhuo_address.setText("");
			}
			String phone = (String) addressList.get(position).get("phone");
			if (phone != null) {
				addressHolder.shouhuo_phone.setText(phone);
			} else {
				addressHolder.shouhuo_phone.setText("");
			}
			return convertView;
		}
	}

	AddressHolder addressHolder;
	private int hours;
	private int minues;
	private int id;
	private int count;
	private double price;
	private double allPrice;
	private String name;
	private List<Map<String, String>> list;
	// 跳转界面使用的标识
	private int index;
	// 总价格
	private double all;

	private double customer_favor_money = 0;

	class AddressHolder {
		private ImageView shouhuo_check;// 选择框
		private TextView shouhuo_name,// 收货人姓名
				shouhuo_phone,// 收货人姓名
				shouhuo_address// 收货人地址
				;
	}

	/**
	 * 为了解决ListView在ScrollView中只能显示一行数据的问题
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pay_line_relativelayout:// 在线支付
			pay_online = 0;
			pay_line_iv.setImageResource(R.drawable.check_enable);
			huodaofukuan_iv.setImageResource(R.drawable.check_red_no);
			break;
		case R.id.huodaofukuan_relativelayout:// 货到付款
			pay_online = 1;
			pay_line_iv.setImageResource(R.drawable.check_red_no);
			huodaofukuan_iv.setImageResource(R.drawable.check_enable);
			break;
		case R.id.kaijufapiao_relativelayout:// 去写发票
			IS_GET_ADDRESS = false;
			Intent intent = new Intent(this, NewInvoiceActivity.class);
			intent.putExtra("fapiao_type", fapiao_type);
			intent.putExtra("fapiao_mode", fapiao_mode);
			intent.putExtra("fapiao_title", fapiao_title);
			startActivityForResult(intent, 1);
			break;
		case R.id.go_wuliu:// 物流
			send_type = 2;
			go_ziti.setBackgroundResource(R.drawable.text_view_border);
			go_ziti.setTextColor(Color.parseColor("#8F8F8F"));
			go_wuiu.setBackgroundResource(R.drawable.text_view_border_blue);
			go_wuiu.setTextColor(Color.parseColor("#3CC6FD"));
			order_ziti_linearlayout.setVisibility(View.GONE);// 自提布局隐藏
			shouhuo_address_listview.setVisibility(View.VISIBLE);// 地址列表显示
			if (IS_XIANSHI_NO_MORE_TEXTVIEW) {// 是否显示
				no_moren_address.setVisibility(View.VISIBLE);// 显示,没有地址的提示的控件
			}
			songda_time_tag.setText("送达时间");
			getDefaltTime();
			shouhuo_mode_bottom_wuliu.setVisibility(View.VISIBLE);
			shouhuo_mode_bottom_ziqu.setVisibility(View.GONE);
			order_submit_activity_one.setVisibility(View.VISIBLE);
			if ("1".equals(is_yd)) {// 如果是易店
				peisong_fee_ll.setVisibility(View.VISIBLE);
				manjian_hengxian.setVisibility(View.VISIBLE);
			}
				BigDecimal favor_money_bd = new BigDecimal(
						customer_favor_money);
				BigDecimal new_all_bd = new BigDecimal(new_all);
				all_price_tv
						.setText(Html
								.fromHtml("<font color=\"#000000\">共</font>&nbsp<font color=\"#FEB010\">¥"
										+ String.format("%.2f", new_all_bd
												.subtract(favor_money_bd)
												.doubleValue()) + "</font>"));
			break;
		case R.id.go_ziti:// 自提
			send_type = 1;
			go_ziti.setBackgroundResource(R.drawable.text_view_border_blue);
			go_ziti.setTextColor(Color.parseColor("#3CC6FD"));
			go_wuiu.setBackgroundResource(R.drawable.text_view_border);
			go_wuiu.setTextColor(Color.parseColor("#8F8F8F"));
			order_ziti_linearlayout.setVisibility(View.VISIBLE);// 自提布局显示
			shouhuo_address_listview.setVisibility(View.GONE);// 地址列表隐藏
			no_moren_address.setVisibility(View.GONE);// 隐藏,没有地址的提示的控件
			songda_time_tag.setText("自取时间");
			if ("1".equals(is_yd)) {// 如果是易店,直隐藏配送费，不隐藏满减优惠
				peisong_fee_ll.setVisibility(View.GONE);
				manjian_hengxian.setVisibility(View.GONE);
				BigDecimal b1 = new BigDecimal(all);
				BigDecimal b3 = new BigDecimal(store_favor_money);
				all_price_tv
						.setText(Html
								.fromHtml("<font color=\"#000000\">共</font>&nbsp<font color=\"#FEB010\">¥"
										+ String.format("%.2f", b1.subtract(b3)
												.doubleValue()) + "</font>"));
			} else {// 如果是易库，隐藏配送费，隐藏满减优惠
				order_submit_activity_one.setVisibility(View.GONE);
					BigDecimal favor_money_bd_2 = new BigDecimal(
							customer_favor_money);
					BigDecimal all_bd = new BigDecimal(all);
					all_price_tv
							.setText(Html.fromHtml("<font color=\"#000000\">共</font>&nbsp<font color=\"#FEB010\">¥"
									+ String.format("%.2f",
											all_bd.subtract(favor_money_bd_2)
													.doubleValue()) + "</font>"));
			}
			getDefaltTime();

			shouhuo_mode_bottom_ziqu.setVisibility(View.VISIBLE);
			shouhuo_mode_bottom_wuliu.setVisibility(View.GONE);
			break;
		case R.id.songda_time_linearlayout:// 配送时间
			pickerDialog = new PickerDialog(this, send_type);// 弹出下面对话框
			pickerDialog.setCancelButtonOnClickListener(new OnClickListener() {// 设置取消键监听

						@Override
						public void onClick(View v) {
							pickerDialog.dismiss();// 关闭对话框
						}
					});
			pickerDialog.setConfirmButtonOnClickListener(new OnClickListener() {// 设置确认键监听

						@Override
						public void onClick(View v) {
							pickerDialog.dismiss();
						}
					});
			pickerDialog.setOnCheckTime(new ICheckTime() {// 设置所选时间的监听

						@Override
						public void onCheckTime(String time) {
							send_time = time;
							if (send_time.contains("立即送出")) {
								String newTime = send_time.substring(4);
								order_songda_time.setText(Html.fromHtml("立即送出"
										+ "<font color=\"#3CC6FD\">" + newTime
										+ "</font>"));
							}/*
							 * else if(str.contains("大约")){ String newTime =
							 * str.substring(2);
							 * order_songda_time.setText(Html.fromHtml("大约" +
							 * "<font color=\"#3CC6FD\">"+newTime+"</font>")); }
							 */else {
								order_songda_time.setText(Html
										.fromHtml("<font color=\"#3CC6FD\">"
												+ time + "</font>"));
							}
						}
					});
			break;
		case R.id.beizhu_relativelayout:// 添加备注
			IS_GET_ADDRESS = false;
			Intent intentAddNote = new Intent(this, AddNoteActivity.class);
			intentAddNote.putExtra("order_comm", order_comm);
			startActivityForResult(intentAddNote, 2);
			break;
		case R.id.go_order_btn:// 下单
			addOrder();
			break;
		case R.id.youhui_relativelayout:// 优惠卷
			IS_GET_ADDRESS = false;
			Intent couponsIntent = new Intent(this, CouponsActivity.class);
			couponsIntent.putExtra("ISORDER", true);
			couponsIntent.putExtra("total_price", all);
			startActivityForResult(couponsIntent, 110);
			break;
		case R.id.no_moren_address:// 还没有收货地址,快去设置'
			IS_GET_ADDRESS = true;
			startActivity(new Intent(SubmitOrderActivity.this,
					ShippingAddressMgrActivity.class));
			break;
		case R.id.order_submit_back_iv:// 返回
			finish();
			break;
		}
	}

	private void getStoreInfo() {
		showInfoProgressDialog();
		String url = Contacts.URL_GET_STORE_DETAILS + "id=" + store_id
				+ Contacts.URL_ENDING;
		Log.i("qcs", "url===" + url);
		JsonObjectRequest rquest = new JsonObjectRequest(url, null,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d("sss", "response === response=== " + response.toString());
						if (response.has("result")) {// 是否有字段
							try {
								String result = response.getString("result");// 获取字段值
								if (result.equalsIgnoreCase("SUCCESS")) {
									if (response.has("data")) {
										JSONObject data = response
												.getJSONObject("data");
										if (data.has("is_yd")) {
											is_yd = data.getString("is_yd");
											Log.i("qcs", "is_yd == " + is_yd);
											if ("1".equals(is_yd)) {
												manjian_relativelayout
														.setVisibility(View.VISIBLE);
												manjian_hengxian
														.setVisibility(View.VISIBLE);
												youhui_hengxian
												.setVisibility(View.GONE);
												youhui_relativelayout
												.setVisibility(View.GONE);
											} else {
												manjian_relativelayout
												.setVisibility(View.GONE);
												manjian_hengxian
												.setVisibility(View.GONE);
												youhui_hengxian
														.setVisibility(View.VISIBLE);
												youhui_relativelayout
														.setVisibility(View.VISIBLE);
											}
										}
										if (data.has("address")) {
											ziti_address_name.setText(data
													.getString("address"));
											shouhuo_mode_bottom_ziqu_address.setText(data
													.getString("address"));
										}
										if (data.has("name")) {
											peisong_linearlayout_merchant.setText("本订单由"
													+ data.getString("name")
													+ "提供配送服务");
										}
										if (data.has("send_fee")) {
											String fee = data
													.getString("send_fee");
											double doubleFee = Double
													.parseDouble(fee) / 100;
											peisong_iv_price.setText("¥ "
													+ String.format("%.2f",
															doubleFee));
											BigDecimal b1 = new BigDecimal(
													all);
											BigDecimal b2 = new BigDecimal(
													doubleFee);
											if (send_type == 2) {// 1 自提,2 送货
												Log.d("sss", "send_type == "
														+ send_type);
												Log.d("sss", "IS_FIRST == "
														+ IS_FIRST);
												if (!IS_FIRST) {// 如果是第一次进入页面
													new_all = b1.add(b2)
															.doubleValue();
													if ("1".equals(is_yd)) {// 如果是易店，加上满减
														Log.i("sss", "store_favor_money =什么啊= " + store_favor_money);
														BigDecimal b3 = new BigDecimal(
																store_favor_money);
														new_all = b1.add(b2)
																.subtract(b3)
																.doubleValue();
													}
													IS_FIRST = true;
													all_price_tv.setText(Html.fromHtml("<font color=\"#000000\">共</font>&nbsp<font color=\"#FEB010\">¥"
															+ String.format("%.2f",
																	new_all)
																	+ "</font>"));
												}
											}

										}
									}

								} else {// 信息失败
									if (response.has("error_info")) {// 是否有字段
										Toast.makeText(
												SubmitOrderActivity.this,
												""
														+ response
																.getString("error_info"),
												Toast.LENGTH_SHORT).show();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
								dismissInfoProgressDialog();
							}
						}
						dismissInfoProgressDialog();
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
						Toast.makeText(
								SubmitOrderActivity.this,
								"店铺信息获取失败...",
								Toast.LENGTH_SHORT).show();
					}
				});
		mQueue.add(rquest);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		} else {
			if (requestCode == 2 && resultCode == RESULT_OK) {// 备注
				order_comm = data.getStringExtra("note");
				order_beizhu_tv.setText(order_comm);
			} else if (requestCode == 1 && resultCode == 1) {// 发票
				if (data.getStringExtra("title") != null) {
					fapiao_title = data.getStringExtra("title");
					Log.i("qcs",
							"fapiao_title == " + data.getStringExtra("title"));
				}
				if (data.getStringExtra("type") != null) {
					fapiao_type = data.getStringExtra("type");
					Log.i("qcs",
							"fapiao_type == " + data.getStringExtra("type"));
				}
				if (data.getStringExtra("typemode") != null) {
					fapiao_mode = data.getStringExtra("typemode");
				}
				order_fapiao_tv.setText(fapiao_type + " / " + fapiao_title);
			} else if (requestCode == 110 && resultCode == 110) {// 优惠券
				if (data.getStringExtra("id") != null) {
					customer_favor_id = data.getStringExtra("id");// 优惠券ID
				}
				customer_favor_money = data.getDoubleExtra("favor_money", 0);
				Log.i("sss", "customer_favor_money == " + customer_favor_money);
					BigDecimal favor_money_bd = new BigDecimal(
							customer_favor_money);
					if (send_type == 2) {// 物流
						BigDecimal new_all_bd = new BigDecimal(
								new_all);
						all_price_tv
								.setText(Html.fromHtml("<font color=\"#000000\">共</font>&nbsp<font color=\"#FEB010\">¥"
										+ String.format("%.2f", new_all_bd
												.subtract(favor_money_bd)
												.doubleValue()) + "</font>"));
					} else {// 自提
						BigDecimal all_bd = new BigDecimal(all);
						all_price_tv
								.setText(Html.fromHtml("<font color=\"#000000\">共</font>&nbsp<font color=\"#FEB010\">¥"
										+ String.format("%.2f", all_bd
												.subtract(favor_money_bd)
												.doubleValue()) + "</font>"));
					}
					youhui_tv.setText("已优惠¥" + String.format("%.2f",customer_favor_money));
					order_youhuiquan_tv.setText("优惠¥" + String.format("%.2f",customer_favor_money) + "元");
			}
		}
	}

	/**
	 * 地址列表点击事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		IS_GET_ADDRESS = true;
		startActivity(new Intent(SubmitOrderActivity.this,
				ShippingAddressMgrActivity.class));
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected void onResume() {
		super.onResume();
		Log.i("sss", "IS_GET_ADDRESS == " + IS_GET_ADDRESS);
		if (IS_GET_ADDRESS) {
			Log.i("sss", "sss === " + IS_GET_ADDRESS);
			getAddressList();
		}
		getDefaltTime();
	}

	private boolean IS_FIRST;

	/**
	 * 得到默认时间
	 */
	private void getDefaltTime() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH-mm");
		String time = format.format(date);
		int hours = Integer.parseInt((time.split("-"))[0]);
		int minues = Integer.parseInt((time.split("-"))[1]);

		int newMinues = minues;
		int newHours = hours;
		newTime = "";
		newMinues += 30;
		if (newMinues < 60) {
			if ((newHours + "").length() == 1) {
				newTime = "0" + newHours + ":" + newMinues;
			} else {
				newTime = newHours + ":" + newMinues;
			}
		} else if (newMinues == 60) {
			newHours += 1;
			newMinues = 0;
			if ((newHours + "").length() == 1) {
				newTime = "0" + newHours + ":" + "0" + newMinues;
			} else {
				newTime = newHours + ":" + "0" + newMinues;
			}
		} else {
			newHours += 1;
			newMinues = newMinues - 60;
			if ((newMinues + "").length() == 1) {
				if ((newHours + "").length() == 1) {
					newTime = "0" + newHours + ":" + "0" + newMinues;
				} else {
					newTime = newHours + ":" + "0" + newMinues;
				}
			} else {
				if ((newHours + "").length() == 1) {
					newTime = "0" + newHours + ":" + newMinues;
				} else {
					newTime = newHours + ":" + newMinues;
				}
			}
		}

		String str = newTime;
		if (send_type == 1) {
			str = "大约" + str + "自取";
		} else if (send_type == 2) {
			str = "立即送出(大约" + str + "送达)";
		} else {
			// str = str;
		}
		if (str.contains("立即送出")) {
			String newTime = str.substring(4);
			order_songda_time.setText(Html.fromHtml("立即送出"
					+ "<font color=\"#3CC6FD\">" + newTime + "</font>"));
		} else if (str.contains("大约")) {
			String newTime = str.substring(2);
			order_songda_time.setText(Html.fromHtml("大约"
					+ "<font color=\"#3CC6FD\">" + newTime + "</font>"));
		} else {
			order_songda_time.setText(Html.fromHtml("<font color=\"#3CC6FD\">"
					+ newTime + "</font>"));
		}

		send_time = newTime;

	}

	/**
	 * 联网获取用户地址列表
	 */
	private void getAddressList() {
		addressList = new ArrayList<HashMap<String, Object>>();
		addressList.clear();
		// http://182.254.161.94:8080/ydg/customerAddress!getAddressList?client_type=A&version=1&customer_id=4
		// 用户地址列表访问头
		// String url =
		// "http://182.254.161.94:8080/ydg/customerAddress!getAddressList?client_type=A&version=1&customer_id="
		// + 4;

		// http://182.254.161.94:8080/ydg/customerAddress!addressCRUD?&action=0&customer_id=9&is_default=1&id=32&client_type=I&version=1
		String url = Contacts.URL_ADDRESS_CRUD + "customer_id="
				+ Customer.customer_id + "&action=" + 0 + "&is_default=" + 1
				+ "&id=" + Customer.address_id + Contacts.URL_ENDING;
		showInfoProgressDialog();
		JsonObjectRequest request = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d("qcs", "response--" + response.toString());
						if (response.has("result")) {// 是否有字段
							try {
								String result = response.getString("result");// 获取字段值
								if (result.equalsIgnoreCase("SUCCESS")) {
									// 有地址了 去掉这个没有地址的提示
									no_moren_address.setVisibility(View.GONE);
									shouhuo_address_listview
											.setVisibility(View.VISIBLE);
									IS_XIANSHI_NO_MORE_TEXTVIEW = false;

									if (response.has("data")) {
										JSONObject data = response
												.getJSONObject("data");
										HashMap<String, Object> hashMap = new HashMap<String, Object>();
										if (data.has("address")) {// 地址
											hashMap.put("address",
													data.getString("address"));
										}
										if (data.has("id")) {// 地址ID在当前列表中?
											hashMap.put("id",
													data.getString("id"));
										}
										if (data.has("name")) {// 收货人姓名
											hashMap.put("name",
													data.getString("name"));
										}
										if (data.has("phone")) {// 收货人电话
											hashMap.put("phone",
													data.getString("phone"));
										}
										if (data.has("zip_code")) {// 邮编
											hashMap.put("zip_code",
													data.getString("zip_code"));
										}
										addressList.add(hashMap);
									}
									Log.d("qcs",
											"addressList--"
													+ addressList.toString());
									addressAdapter = new MyAddressAdapter();
									shouhuo_address_listview
											.setAdapter(addressAdapter);
									shouhuo_mode_bottom_wuliu_address
											.setText((String) addressList
													.get(0).get("address"));
									setListViewHeightBasedOnChildren(shouhuo_address_listview);
								} else {// 信息有误
									if (response.has("error_info")) {// 是否有字段
										Toast.makeText(
												SubmitOrderActivity.this,
												"还没有默认收货地址哦!",
												Toast.LENGTH_SHORT).show();
										// 没有默认地址了 显示这个没有地址的提示
										shouhuo_address_listview
												.setVisibility(View.GONE);
										no_moren_address
												.setVisibility(View.VISIBLE);
										IS_XIANSHI_NO_MORE_TEXTVIEW = true;
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						dismissInfoProgressDialog();
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
						Toast.makeText(SubmitOrderActivity.this, "网络错误...",
								Toast.LENGTH_SHORT).show();
					}
				});

		mQueue.add(request);
	}

	/**
	 * 下单
	 */
	private void addOrder() {
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
						Toast.makeText(getApplicationContext(), "下单失败!",
								Toast.LENGTH_SHORT).show();
						return;
					}
					try {
						JSONObject result_data = new JSONObject(result);
						if (result.contains("success")
								|| result.contains("Success")
								|| result.contains("SUCCESS")) {
							if (index == 1) {// 易库
								SubmitOrderActivity.this
										.sendBroadcast(new Intent(
												YiKuFragment.REFRESH_VIEW_ACTION));// 清空购物车
							} else if (index == 2) {// 易店
								SubmitOrderActivity.this
										.sendBroadcast(new Intent(
												YiDianStoreGoodsActivity.REFRESH_VIEW_ACTION));// 清空购物车
							}

							Toast.makeText(SubmitOrderActivity.this,
									"" + result_data.getString("error_info"),
									Toast.LENGTH_SHORT).show();

							if (result_data.has("data")) {
								orderMap = new HashMap<String, String>();
								orderMap.put("data",
										result_data.getString("data"));
							}
							if (pay_online == 0) {// 0,在线,1货到
								Intent intnet = new Intent(
										SubmitOrderActivity.this,
										ShowOrderPayActivity.class);
								intnet.putExtra("send_out_trade_no",
										orderMap.get("data"));
								intnet.putExtra("storeId", store_id + "");
								BigDecimal favor_money_bd = new BigDecimal(
										customer_favor_money);
								if (send_type == 2) {//2 为送货1 是自提   // 有运费有优惠
									BigDecimal new_all_bd = new BigDecimal(new_all);
									intnet.putExtra("send_total_fee", new_all_bd
											.subtract(favor_money_bd).doubleValue()
											+ "");
								} else {// 自提没有运费但需加上，优惠
									BigDecimal all_bd = new BigDecimal(all);
									BigDecimal store_favor_bd = new BigDecimal(
											Double.toString(store_favor_money));
									if ("1".equals(is_yd)) {// 易店
										intnet.putExtra("send_total_fee", all_bd
												.subtract(store_favor_bd).doubleValue()
												+ "");
									} else {// 易库
										intnet.putExtra("send_total_fee", all_bd
												.subtract(favor_money_bd).doubleValue()
												+ "");
									}
								}
								startActivity(intnet);
							} else {
								Intent it = new Intent(
										SubmitOrderActivity.this,
										OrderDetailActivity.class);
								it.putExtra("ORDER_ID", orderMap.get("data"));
								it.putExtra("PAY_STATUS", "1");
								Log.i("qcs",
										"ORDER_ID == " + orderMap.get("data"));
								startActivity(it);
							}

							Log.i("qcs", "orderMap" + orderMap.toString());
							finish();
						} else {
							Toast.makeText(SubmitOrderActivity.this,
									"" + result_data.getString("error_info"),
									Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(SubmitOrderActivity.this, "获取数据出错..",
								Toast.LENGTH_SHORT).show();
					}

					break;
				}
				dismissInfoProgressDialog();
			}
		};

		new Thread(new Runnable() {
			public void run() {
				handler.sendEmptyMessage(STATE1);
				String url = Contacts.URL_ADD_ORDER;
				String map = "store_id=" + store_id + // 店铺的ID
						"&customer_id=" + Customer.customer_id + // 用户的ID
						"&items=" + jsonString + // 商品JSON串
						"&address_id=" + Customer.address_id + // 收货地址ID
						"&customer_favor_id=" + customer_favor_id + // 优惠券ID
						"&send_type=" + send_type + // 配送方式1为自提，2为送货
						"&order_comm=" + order_comm + // 订单备注
						"&send_time=" + send_time + // 配送时间
						"&fapiao_type=" + fapiao_type + // 发票类型
						"&fapiao_title=" + fapiao_title + // 发票抬头
						"&pay_online=" + pay_online + // 支付方式 0 在线 1 货到付款 默认0
						"&store_favor_id=" + store_favor_id + // 店铺优惠id
						Contacts.URL_ENDING;
				;
				Log.d("qcs", "map--" + url + map);
				String result = NetWorkHelper.postImg_Record2(url, map);
				Message msg = handler.obtainMessage();
				msg.what = STATE2;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
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

}
