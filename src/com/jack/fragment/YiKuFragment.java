package com.jack.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.jack.fragment.YiKuFragment.MyAdapter.OrderHolder;
import com.jack.fragment.YiKuFragment.MyAdapter2.OrderHolder2;
import com.jack.headpicselect.BitmapCache;
import com.jack.json.JsonYikuHome;
import com.jack.json.JudgeExist;
import com.jack.ui.MProcessDialog;
import com.yst.activity.CheckAddressActivity;
import com.yst.activity.CustomerCenterActivity;
import com.yst.activity.LoginActivity;
import com.yst.activity.MainActivity;
import com.yst.activity.StoreProductDetailsActivity;
import com.yst.activity.SubmitOrderActivity;
import com.yst.yiku.R;

public class YiKuFragment extends Fragment implements OnClickListener {
	private ImageView imageView2;
	private int cur_pos = 0;// 当前显示的一行
	private View view;
	private LinearLayout linearlayout_yiku_fragment_location;
	private ListView listView_yiku_fragment_storecat,
			listView_yiku_fragment_storeproduct;
	private ImageView imageView_shopcart;
	private TextView cart_count_tv;
	public static TextView textView_yiku_fragment_location;
	private TextView textView6;

	public static RequestQueue requestQueue = null;
	private StringRequest stringRequest = null;
	private List<Map<String, Object>> list_StoreCat;// 商品分类
	public static List<Map<String, Object>> list_StoreProduct;// 商品列表
	MyAdapter adapter;
	MyAdapter2 adapter2;

	private ImageLoader imageLoader;
	public static List<Map<String, String>> list1;
	private List<Map<String, String>> tlist;

	private TextView textView3, textView3_1;
	private TextView textView5;
	private RelativeLayout linearLayout111;
	// 界面是否已经准备好接受数据
	private boolean isPrepare = false;

	private boolean finishCartFlag = false;
	private double init_price = 0;
	private double send_fee = 0;

	public static final String REFRESH_VIEW_ACTION = "com.jack.fragment.cart.refresh.action";
	public BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			/**
			 * 这里是用来刷新fragment的主界面的
			 */
			if (null != intent) {
				if (intent.getAction().equals(REFRESH_VIEW_ACTION)) {
					setZeroCount();
					stayTheSame();
					refreshViews();
					cur_pos = 0;
					queryData(store_id);
				}
			}
		}
	};
	private SharedPreferences msp;

	// private NetworkImageView yidian_store_icon_iv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.yiku_fragment, null);
		requestQueue = Volley.newRequestQueue(this.getActivity());
		imageLoader = new ImageLoader(requestQueue, new BitmapCache());
		getActivity().registerReceiver(receiver,
				new IntentFilter(REFRESH_VIEW_ACTION));
		initView();
		/*
		 * this.queryData(); this.queryData2("1"); this.getStoreInfo();
		 */
		Log.e("sss", "oncreate is invoick");
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (store_id == null) {
		} else {
			cur_pos = 0;
			queryData(store_id);
			getStoreInfo(store_id);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("123", "==1111111111111111111");
		getActivity().unregisterReceiver(receiver);
		if (list1 != null) {
			list1.clear();
		}
		list1 = null;
	}

	/**
	 * 获取店铺名称
	 */
	public void getStoreInfo(String store_id) {
		this.store_id = store_id;
		String url = com.guohua.common.util.Contacts.URL_GET_STORE_DETAILS
				+ "id=" + store_id + com.guohua.common.util.Contacts.URL_ENDING;
		Log.i("qcs", "url===" + url);
		JsonObjectRequest rquest = new JsonObjectRequest(url, null,
				new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (response.has("result")) {// 是否有字段
							try {
								String result = response.getString("result");// 获取字段值
								if (result.equalsIgnoreCase("SUCCESS")) {
									if (response.has("data")) {
										JSONObject data = response
												.getJSONObject("data");
										if (data.has("name")) {
											textView_yiku_fragment_location
													.setText(data
															.getString("name"));
										}
										if (data.has("send_low_price")) {
											init_price = data
													.getDouble("send_low_price") / 100;
										}
										if (data.has("send_fee")) {
											send_fee = data
													.getDouble("send_fee") / 100;
										}

										textView6.setText("还差¥" + init_price);
									}
								} else {// 信息失败
									if (response.has("error_info")) {// 是否有字段
										Toast.makeText(
												YiKuFragment.this.getActivity(),
												""
														+ response
																.getString("error_info"),
												Toast.LENGTH_SHORT).show();
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
					}
				});
		requestQueue.add(rquest);
	}

	public void queryData(final String store_id) {
		this.store_id = store_id;
		String urlString = "http://182.254.161.94:8080/ydg/storeProduct!getStoreProdctByCat?store_cat_id=-1&store_id="
				+ store_id
				+ "&page_size=1000&page_no=0&client_type=A&version=1";
		Log.e("TAG", "==" + urlString);
		showInfoProgressDialog();
		stringRequest = new StringRequest(urlString, new Listener<String>() {

			@Override
			public void onResponse(String arg0) {
				if (!arg0.equalsIgnoreCase("[]") && arg0 != null) {
					if (arg0.contains("FAILED")) {
						if (isPrepare) {
							textView5.setVisibility(View.GONE);
							cart_count_tv.setVisibility(View.GONE);
							linearLayout111.setVisibility(View.GONE);
							// yidian_store_icon_iv.setVisibility(View.GONE);
							imageView_shopcart.setVisibility(View.GONE);
						}
						list_StoreCat = JsonYikuHome.JsonToListFAILED(arg0);
						Toast.makeText(
								YiKuFragment.this.getActivity(),
								list_StoreCat.get(0).get("error_info")
										.toString(), Toast.LENGTH_SHORT).show();
					} else {
						list_StoreCat = JsonToList1(arg0);
						adapter = new MyAdapter(list_StoreCat);
						listView_yiku_fragment_storecat.setAdapter(adapter);
						adapter.notifyDataSetChanged();

						if (isPrepare && list_StoreCat.size() > 0) {
							textView5.setVisibility(View.VISIBLE);
							// yidian_store_icon_iv.setVisibility(View.VISIBLE);
							imageView_shopcart.setVisibility(View.VISIBLE);
							linearLayout111.setVisibility(View.VISIBLE);
						}

						listView_yiku_fragment_storecat
								.setOnItemClickListener(new OnItemClickListener() {
									@Override
									public void onItemClick(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										cur_pos = arg2;// 更新当前行
										adapter.notifyDataSetChanged();

										list_StoreProduct.clear();
										if (adapter2 != null) {
											adapter2.notifyDataSetChanged();
										}
										list_StoreProduct = JsonToListProduct1((JSONArray) list_StoreCat
												.get(arg2).get("items"));
										if (adapter2 != null) {
											adapter2.notifyDataSetChanged();
										}

										adapter2 = new MyAdapter2(
												list_StoreProduct);
										listView_yiku_fragment_storeproduct
												.setAdapter(adapter2);
										listView_yiku_fragment_storeproduct
												.setOnItemClickListener(new OnItemClickListener() {
													@Override
													public void onItemClick(
															AdapterView<?> arg0,
															View arg1,
															int arg2, long arg3) {
														Intent it = new Intent(
																getActivity(),
																StoreProductDetailsActivity.class);
														HashMap<String, Object> maps = (HashMap<String, Object>) list_StoreProduct
																.get(arg2);
														Bundle extras = new Bundle();
														extras.putSerializable(
																"PRODUCT_INFO",
																maps);
														it.putExtras(extras);
														it.putExtra("pos", arg2);
														it.putExtra("store_id",
																store_id);
														it.putExtra(
																"init_price",
																init_price);
														it.putExtra("send_fee",
																send_fee);
														startActivityForResult(
																it, 1099);
													}
												});
										adapter2.notifyDataSetChanged();
										stayTheSame();
									}
								});
						if (list_StoreCat.size() > 0) {
							list_StoreProduct.clear();
							if (adapter2 != null) {
								adapter2.notifyDataSetChanged();
							}
							list_StoreProduct = JsonToListProduct1((JSONArray) list_StoreCat
									.get(0).get("items"));
							if (adapter2 != null) {
								adapter2.notifyDataSetChanged();
							}
							adapter2 = new MyAdapter2(list_StoreProduct);
							listView_yiku_fragment_storeproduct
									.setAdapter(adapter2);
							listView_yiku_fragment_storeproduct
									.setOnItemClickListener(new OnItemClickListener() {
										@Override
										public void onItemClick(
												AdapterView<?> arg0, View arg1,
												int arg2, long arg3) {
											Intent it = new Intent(
													getActivity(),
													StoreProductDetailsActivity.class);
											HashMap<String, Object> maps = (HashMap<String, Object>) list_StoreProduct
													.get(arg2);
											Bundle extras = new Bundle();
											extras.putSerializable(
													"PRODUCT_INFO", maps);
											it.putExtras(extras);
											it.putExtra("pos", arg2);
											it.putExtra("store_id", store_id);
											it.putExtra("init_price",
													init_price);
											it.putExtra("send_fee", send_fee);
											startActivityForResult(it, 1099);
										}
									});
							adapter2.notifyDataSetChanged();
							stayTheSame();
						}
					}
				}
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError arg0) {
				dismissInfoProgressDialog();
				textView5.setVisibility(View.GONE);
				imageView_shopcart.setVisibility(View.GONE);
				linearLayout111.setVisibility(View.GONE);
				// yidian_store_icon_iv.setVisibility(View.GONE);
				Toast.makeText(YiKuFragment.this.getActivity(), "数据加载异常...",
						Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(stringRequest);
	}

	private void initView() {
		msp = getActivity().getSharedPreferences("yiku",
				getActivity().MODE_PRIVATE);
		textView_yiku_fragment_location = (TextView) view
				.findViewById(R.id.textView_yiku_fragment_location);
		list1 = new ArrayList<Map<String, String>>();
		tlist = new ArrayList<Map<String, String>>();
		list_StoreProduct = new ArrayList<Map<String, Object>>();
		imageView2 = (ImageView) view.findViewById(R.id.imageView2);
		imageView2.setOnClickListener(this);
		linearlayout_yiku_fragment_location = (LinearLayout) view
				.findViewById(R.id.linearlayout_yiku_fragment_location);
		linearlayout_yiku_fragment_location.setOnClickListener(this);
		listView_yiku_fragment_storecat = (ListView) view
				.findViewById(R.id.listView_yiku_fragment_storecat);
		listView_yiku_fragment_storeproduct = (ListView) view
				.findViewById(R.id.listView_yiku_fragment_storeproduct);

		imageView_shopcart = (ImageView) view
				.findViewById(R.id.imageView_shopcart);
		imageView_shopcart.setOnClickListener(this);
		cart_count_tv = (TextView) view.findViewById(R.id.cart_count_tv);
		textView6 = (TextView) view.findViewById(R.id.textView6);
		textView6.setOnClickListener(this);

		textView3 = (TextView) view.findViewById(R.id.textView3);
		textView3_1 = (TextView) view.findViewById(R.id.textView3_1);
		textView5 = (TextView) view.findViewById(R.id.textView5);
		linearLayout111 = (RelativeLayout) view
				.findViewById(R.id.linearLayout111);

		// yidian_store_icon_iv = (NetworkImageView)
		// view.findViewById(R.id.yidian_store_icon_iv);

		isPrepare = true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView2:
			if (MainActivity.isLogin) {
				startActivity(new Intent(getActivity(),
						CustomerCenterActivity.class));
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.linearlayout_yiku_fragment_location:
			Intent intent = new Intent(getActivity(),
					CheckAddressActivity.class);
			startActivityForResult(intent, 3);
			break;
		case R.id.imageView_shopcart:
			if (list1.size() > 0) {
				showPopwindow();
			}
			break;
		case R.id.textView6:
			if (list1.size() > 0 && send_free_condition()) {
				if (MainActivity.isLogin) {
					Intent it = new Intent(getActivity(),
							SubmitOrderActivity.class);
					it.putExtra("cart_products", (Serializable) list1);
					it.putExtra("index", 1);
					it.putExtra("store_id", store_id);
					startActivity(it);
				} else {
					Intent it = new Intent(getActivity(), LoginActivity.class);
					it.putExtra("cart_products", (Serializable) list1);
					it.putExtra("index", 1);
					it.putExtra("store_id", store_id);
					it.putExtra("activity",
							StoreProductDetailsActivity.ACTIVITY1);
					startActivity(it);
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 用来判断是否可以去下单了
	 * 
	 * @return
	 */
	private boolean send_free_condition() {
		double price = 0;
		for (int i = 0; i < list1.size(); i++) {
			price += (Integer.parseInt(list1.get(i).get("count").toString()) * Double
					.parseDouble(list1.get(i).get("price").toString()));
		}

		if (price >= init_price)
			return true;

		return false;
	}

	OrderHolder2 holder2;

	class MyAdapter2 extends BaseAdapter {
		private List<Map<String, Object>> list = null;

		public MyAdapter2(List<Map<String, Object>> list) {
			super();
			this.list = list;
		}

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

		class OrderHolder2 {
			public NetworkImageView imageView_store_product_item_photo;
			public ImageView imageView_store_product_item_reduce;
			public ImageView imageView_store_product_item_add;
			public TextView textView_store_product_item_buycount;
			public TextView textView_store_product_item_name;
			public TextView textView_store_product_item_quantity;
			public TextView textView_store_product_item_price;
			public TextView textView_store_product_item_unit;
			public LinearLayout linearlayout_store_product_item_des;
			public TextView textView_store_product_item_des;
			public LinearLayout linearLayout_store_product_item_count;
			public LinearLayout linearLayout_store_product_item_standard;
			public ImageView imageView_store_product_item_standard;
			public LinearLayout linearLayout_store_product_item_parameter_layout,
					linearLayout_store_product_item_parameter_layout1;
			public LinearLayout linearLayout_store_product_item_count1;
			public ImageView imageView_store_product_item_reduce1;
			public ImageView imageView_store_product_item_add1;
			public TextView textView_store_product_item_buycount1;
			public TextView textView_store_product_item_zan;
			public ImageView store_product_list_type_iv;
		}

		class MyClickListener implements OnClickListener {
			private int position, tposition;
			private List<Map<String, Object>> list;
			private LinearLayout layout;

			public MyClickListener(int pos, int tlist_index,
					List<Map<String, Object>> tlist, LinearLayout llayout) {
				this.position = pos;
				this.tposition = tlist_index;
				this.list = tlist;
				this.layout = llayout;
			}

			@Override
			public void onClick(View v) {

				list_StoreProduct.get(position)
						.put("property_index", tposition);

				for (int i = 0; i < layout.getChildCount(); i++) {
					Button btn = (Button) layout.getChildAt(i);
					btn.setTextColor(Color.parseColor("#000000"));
					btn.setBackgroundResource(R.drawable.property_list_item_un_focus_png);
				}

				Button btn = (Button) v;
				btn.setTextColor(Color.parseColor("#FFFFFF"));
				btn.setBackgroundResource(R.drawable.property_list_item_focus_png);

				adapter2.notifyDataSetChanged();
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			holder2 = null;
			if (convertView == null) {
				convertView = YiKuFragment.this.getActivity()
						.getLayoutInflater()
						.inflate(R.layout.store_product_list_item, null);
				holder2 = new OrderHolder2();
				holder2.textView_store_product_item_name = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_name);
				holder2.textView_store_product_item_buycount = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_buycount);
				holder2.textView_store_product_item_quantity = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_quantity);
				holder2.textView_store_product_item_zan = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_zan);
				holder2.textView_store_product_item_price = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_price);
				holder2.textView_store_product_item_unit = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_unit);
				holder2.imageView_store_product_item_photo = (NetworkImageView) convertView
						.findViewById(R.id.imageView_store_product_item_photo);
				holder2.imageView_store_product_item_reduce = (ImageView) convertView
						.findViewById(R.id.imageView_store_product_item_reduce);
				holder2.imageView_store_product_item_add = (ImageView) convertView
						.findViewById(R.id.imageView_store_product_item_add);
				holder2.imageView_store_product_item_standard = (ImageView) convertView
						.findViewById(R.id.imageView_store_product_item_standard);
				holder2.textView_store_product_item_des = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_des);
				holder2.linearlayout_store_product_item_des = (LinearLayout) convertView
						.findViewById(R.id.linearlayout_store_product_item_des);
				holder2.linearLayout_store_product_item_count = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_store_product_item_count);
				holder2.linearLayout_store_product_item_standard = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_store_product_item_standard);
				holder2.linearLayout_store_product_item_parameter_layout = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_store_product_item_parameter_layout);
				holder2.linearLayout_store_product_item_parameter_layout1 = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_store_product_item_parameter_layout1);
				holder2.linearLayout_store_product_item_count1 = (LinearLayout) convertView
						.findViewById(R.id.linearLayout_store_product_item_count1);
				holder2.imageView_store_product_item_reduce1 = (ImageView) convertView
						.findViewById(R.id.imageView_store_product_item_reduce1);
				holder2.imageView_store_product_item_add1 = (ImageView) convertView
						.findViewById(R.id.imageView_store_product_item_add1);
				holder2.textView_store_product_item_buycount1 = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_buycount1);
				holder2.store_product_list_type_iv = (ImageView) convertView
						.findViewById(R.id.store_product_list_type_iv);

				convertView.setTag(holder2);
				view = convertView;
			} else {
				view = convertView;
				holder2 = (OrderHolder2) convertView.getTag();
			}

			if (list.get(position).get("unitname") != null) {
				holder2.textView_store_product_item_unit
						.setVisibility(View.VISIBLE);
				holder2.textView_store_product_item_unit.setText(" / "
						+ list.get(position).get("unitname").toString());
			} else {
				holder2.textView_store_product_item_unit
						.setVisibility(View.GONE);
				holder2.textView_store_product_item_unit.setText("");
			}

			if (list.get(position).get("image") != null) {
				holder2.imageView_store_product_item_photo
						.setVisibility(View.VISIBLE);
				holder2.imageView_store_product_item_photo
						.setDefaultImageResId(R.drawable.app_default_icon);
				holder2.imageView_store_product_item_photo
						.setErrorImageResId(R.drawable.app_default_icon);
				holder2.imageView_store_product_item_photo.setImageUrl(
						"http://182.254.161.94:8080/ydg/"
								+ list.get(position).get("image").toString(),
						imageLoader);
			} else {
				holder2.imageView_store_product_item_photo
						.setVisibility(View.GONE);
			}

			if (list.get(position).get("month_sale") != null) {
				holder2.textView_store_product_item_quantity.setText("销售量"
						+ list.get(position).get("month_sale").toString());
			} else {
				holder2.textView_store_product_item_quantity.setText("销售量0");
			}
			if (list.get(position).get("zan") != null) {
				holder2.textView_store_product_item_zan.setText(list
						.get(position).get("zan").toString());
			} else {
				holder2.textView_store_product_item_zan.setText("0");
			}

			if (list.get(position).get("isStock") != null) {
				// 1 为现货 0 为订货
				if (list.get(position).get("isStock").equals("1")) {
					holder2.store_product_list_type_iv
							.setImageResource(R.drawable.xian20);
				} else {
					holder2.store_product_list_type_iv
							.setImageResource(R.drawable.ding20);
				}
			} else {
				holder2.store_product_list_type_iv.setVisibility(View.GONE);
			}

			holder2.textView_store_product_item_name.setText(list.get(position)
					.get("name").toString());
			final List<Map<String, Object>> list_property = (List<Map<String, Object>>) list
					.get(position).get("property");
			holder2.textView_store_product_item_price.setText("￥"
					+ list_property
							.get((Integer) list.get(position).get(
									"property_index")).get("price").toString());
			if (list_property.size() > 1) {
				holder2.linearLayout_store_product_item_count
						.setVisibility(View.GONE);
				holder2.linearLayout_store_product_item_standard
						.setVisibility(View.VISIBLE);
				if ((Boolean) list_StoreProduct.get(position).get("flag")) {
					holder2.imageView_store_product_item_standard
							.setImageResource(R.drawable.select_standard);
				} else {
					holder2.imageView_store_product_item_standard
							.setImageResource(R.drawable.select);
				}
			} else {
				holder2.linearLayout_store_product_item_count
						.setVisibility(View.VISIBLE);
				holder2.linearLayout_store_product_item_standard
						.setVisibility(View.GONE);
			}

			final int pos = position;// 用来定位当前是那个条目被点击了

			/**
			 * 这里是用来判断商品是否有多中规格 true 说明商品有规格，点击选择规格后弹出选择选项 false 说明商品么有规格
			 */
			if ((Boolean) list_StoreProduct.get(pos).get("flag")) {
				holder2.linearLayout_store_product_item_count1
						.setVisibility(View.VISIBLE);
				holder2.linearLayout_store_product_item_parameter_layout
						.setVisibility(View.VISIBLE);
				holder2.linearLayout_store_product_item_parameter_layout1
						.removeAllViews();
				for (int i = 0; i < list_property.size(); i++) {
					Button btn = new Button(getActivity());
					btn.setId(i);
					btn.setText("" + list_property.get(i).get("pro"));
					btn.setOnClickListener(new MyClickListener(
							pos,
							i,
							list_property,
							holder2.linearLayout_store_product_item_parameter_layout1));
					// TODO 此处需要来显示当前是那种商品参数
					if ((Integer) list.get(pos).get("property_index") == i) {
						btn.setTextColor(Color.parseColor("#FFFFFF"));
						btn.setBackgroundResource(R.drawable.property_list_item_focus_png);
					} else {
						btn.setTextColor(Color.parseColor("#000000"));
						btn.setBackgroundResource(R.drawable.property_list_item_un_focus_png);
					}
					LinearLayout.LayoutParams params = new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.leftMargin = 10;
					btn.setLayoutParams(params);
					holder2.linearLayout_store_product_item_parameter_layout1
							.addView(btn);
				}
				int count = Integer.parseInt(list_StoreProduct.get(pos)
						.get("pro_count").toString());
				if (count > 0) {
					holder2.imageView_store_product_item_reduce1
							.setVisibility(View.VISIBLE);
					holder2.textView_store_product_item_buycount1
							.setVisibility(View.VISIBLE);
					holder2.textView_store_product_item_buycount1.setText(count
							+ "");

					refreshViews();
				} else {
					holder2.imageView_store_product_item_reduce1
							.setVisibility(View.GONE);
					holder2.textView_store_product_item_buycount1
							.setVisibility(View.GONE);
					holder2.textView_store_product_item_buycount1.setText("");
					refreshViews();
				}
			} else {
				holder2.linearLayout_store_product_item_parameter_layout
						.setVisibility(View.GONE);
				holder2.linearLayout_store_product_item_count1
						.setVisibility(View.GONE);
				int count = Integer.parseInt(list_StoreProduct.get(pos)
						.get("pro_count").toString());
				if (count > 0) {
					holder2.imageView_store_product_item_reduce
							.setVisibility(View.VISIBLE);
					holder2.textView_store_product_item_buycount
							.setVisibility(View.VISIBLE);
					holder2.textView_store_product_item_buycount.setText(count
							+ "");
					refreshViews();
				} else {
					holder2.imageView_store_product_item_reduce
							.setVisibility(View.GONE);
					holder2.textView_store_product_item_buycount
							.setVisibility(View.GONE);
					holder2.textView_store_product_item_buycount.setText("");
					refreshViews();
				}
			}

			holder2.imageView_store_product_item_add1
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							int count = Integer.parseInt(list_StoreProduct
									.get(pos).get("pro_count").toString());

							int cur_count = getProCount(list_property
									.get((Integer) list.get(pos).get(
											"property_index"))
									.get("store_prod_id").toString());

							int stock_count = Integer.parseInt(list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("stock")
									.toString());

							if (list_StoreProduct.get(pos).get("isStock")
									.equals("1")) {
								if (cur_count >= stock_count
										|| stock_count == 0) {
									Toast.makeText(getActivity(), "库存不足",
											Toast.LENGTH_SHORT).show();
									return;
								}
							}

							if (count >= 0) {
								list_StoreProduct.get(pos).put("pro_count",
										count + 1);
								holder2.imageView_store_product_item_reduce1
										.setVisibility(View.VISIBLE);
								holder2.textView_store_product_item_buycount1
										.setVisibility(View.VISIBLE);
								holder2.textView_store_product_item_buycount1
										.setText((count + 1) + "");
							} else {
								holder2.textView_store_product_item_buycount1
										.setVisibility(View.GONE);
								holder2.imageView_store_product_item_reduce1
										.setVisibility(View.GONE);
								holder2.textView_store_product_item_buycount1
										.setText("");
							}
							count = (Integer) list.get(pos).get("pro_count");

							String id = list_property
									.get((Integer) list.get(pos).get(
											"property_index"))
									.get("store_prod_id").toString();
							String prices = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("price")
									.toString();
							String pro = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("pro")
									.toString();
							// String stock = list.get(pos).get("stock")
							// .toString();
							String stock = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("stock")
									.toString();

							String isStock = list.get(pos).get("isStock")
									.toString();
							boolean isHas = false;
							for (int i = 0; i < list1.size(); i++) {
								Map<String, String> map = list1.get(i);
								if (map.get("id").equals(id)) {
									isHas = true;
									existMap(
											list_StoreProduct.get(pos)
													.get("prod_basic_id")
													.toString(),
											list_StoreProduct.get(pos)
													.get("name").toString(),
											id,
											Integer.parseInt(map.get("count")) + 1,
											prices, pro, stock, isStock);
								} else {
									continue;
								}
							}

							if (!isHas) {
								existMap(
										list_StoreProduct.get(pos)
												.get("prod_basic_id")
												.toString(), list_StoreProduct
												.get(pos).get("name")
												.toString(), id, 1, prices,
										pro, stock, isStock);
							}

							adapter2.notifyDataSetChanged();
							refreshViews();
						}
					});

			holder2.imageView_store_product_item_reduce1
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {

							int count = Integer.parseInt(list_StoreProduct
									.get(pos).get("pro_count").toString());
							// int count =
							// rowCount(list_StoreProduct.get(pos).get("prod_basic_id").toString());

							if (count > 0) {
								list_StoreProduct.get(pos).put("pro_count",
										count - 1);
								holder2.textView_store_product_item_buycount1
										.setText((count - 1) + "");
								holder2.imageView_store_product_item_reduce1
										.setVisibility(View.VISIBLE);
							} else {
								list_StoreProduct.get(pos).put("pro_count", 0);
								holder2.imageView_store_product_item_reduce1
										.setVisibility(View.GONE);
							}
							adapter2.notifyDataSetChanged();

							String id = list_property
									.get((Integer) list.get(pos).get(
											"property_index"))
									.get("store_prod_id").toString();
							String prices = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("price")
									.toString();
							String[] t = rowCounts(
									list_StoreProduct.get(pos)
											.get("prod_basic_id").toString())
									.split(",");
							String pro = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("pro")
									.toString();
							// String stock = list.get(pos).get("stock")
							// .toString();

							String stock = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("stock")
									.toString();

							String isStock = list.get(pos).get("isStock")
									.toString();
							boolean isOver = false;

							for (int i = 0; i < list1.size(); i++) {
								Map<String, String> map = list1.get(i);
								if (map.get("id").equals(id)) {
									if (Integer.parseInt(map.get("count")) > 0) {
										isOver = true;
										existMap(list_StoreProduct.get(pos)
												.get("prod_basic_id")
												.toString(), list_StoreProduct
												.get(pos).get("name")
												.toString(), id,
												Integer.parseInt(map
														.get("count")) - 1,
												prices, pro, stock, isStock);
									} else {
										list1.remove(i);
										clearSameData();
									}
								} else {
									continue;
								}
							}

							if (!isOver) {
								for (int i = 0; i < list1.size(); i++) {
									Map<String, String> map = list1.get(i);
									if (map.get("pos").equals(
											list_StoreProduct.get(pos)
													.get("prod_basic_id")
													.toString())) {
										if (Integer.parseInt(map.get("count")) > 0) {
											// existMap(list_StoreProduct.get(pos).get("prod_basic_id").toString(),
											// list_StoreProduct.get(pos).get("name").toString(),
											// id,
											// Integer.parseInt(map.get("count"))
											// - 1, prices);
											map.put("count",
													String.valueOf(Integer.parseInt(map
															.get("count")) - 1));
											break;
										} else {
											list1.remove(i);
										}
									}
								}
							}

							refreshViews();
						}
					});

			holder2.imageView_store_product_item_standard
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							holder2.linearLayout_store_product_item_standard
									.setVisibility(View.GONE);
							holder2.linearLayout_store_product_item_count
									.setVisibility(View.VISIBLE);
							Log.e("123", "==:" + "选择规格");
							if ((Boolean) list.get(pos).get("flag")) {
								list_StoreProduct.get(pos).put("flag", false);
							} else {
								list_StoreProduct.get(pos).put("flag", true);
							}
							adapter2.notifyDataSetChanged();
						}
					});

			holder2.imageView_store_product_item_add
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							int count = Integer.parseInt(list_StoreProduct
									.get(pos).get("pro_count").toString());

							int cur_count = getProCount(list_property
									.get((Integer) list.get(pos).get(
											"property_index"))
									.get("store_prod_id").toString());

							int stock_count = Integer.parseInt(list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("stock")
									.toString());

							if (list_StoreProduct.get(pos).get("isStock")
									.equals("1")) {
								if (cur_count >= stock_count
										|| stock_count == 0) {
									Toast.makeText(getActivity(), "库存不足",
											Toast.LENGTH_SHORT).show();
									return;
								}
							}
							if (count >= 0) {
								list_StoreProduct.get(pos).put("pro_count",
										count + 1);
								holder2.imageView_store_product_item_reduce
										.setVisibility(View.VISIBLE);
								holder2.textView_store_product_item_buycount
										.setVisibility(View.VISIBLE);
								holder2.textView_store_product_item_buycount
										.setText((count + 1) + "");
							} else {
								holder2.textView_store_product_item_buycount
										.setVisibility(View.GONE);
								holder2.imageView_store_product_item_reduce
										.setVisibility(View.GONE);
								holder2.textView_store_product_item_buycount
										.setText("");
							}

							count = (Integer) list.get(pos).get("pro_count");
							String id = list_property
									.get((Integer) list.get(pos).get(
											"property_index"))
									.get("store_prod_id").toString();
							String prices = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("price")
									.toString();
							String pro = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("pro")
									.toString();
							// String stock = list.get(pos).get("stock")
							// .toString();
							String stock = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("stock")
									.toString();

							String isStock = list.get(pos).get("isStock")
									.toString();
							boolean isHas = false;
							for (int i = 0; i < list1.size(); i++) {
								Map<String, String> map = list1.get(i);
								if (map.get("id").equals(id)) {
									isHas = true;
									existMap(
											list_StoreProduct.get(pos)
													.get("prod_basic_id")
													.toString(),
											list_StoreProduct.get(pos)
													.get("name").toString(),
											id,
											Integer.parseInt(map.get("count")) + 1,
											prices, pro, stock, isStock);
								} else {
									continue;
								}
							}

							if (!isHas) {
								existMap(
										list_StoreProduct.get(pos)
												.get("prod_basic_id")
												.toString(), list_StoreProduct
												.get(pos).get("name")
												.toString(), id, 1, prices,
										pro, stock, isStock);
							}

							refreshViews();

							adapter2.notifyDataSetChanged();
							refreshViews();
						}
					});
			holder2.imageView_store_product_item_reduce
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {

							String id = list_property
									.get((Integer) list.get(pos).get(
											"property_index"))
									.get("store_prod_id").toString();
							String prices = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("price")
									.toString();
							String pro = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("pro")
									.toString();
							// String stock = list.get(pos).get("stock")
							// .toString();
							String stock = list_property
									.get((Integer) list.get(pos).get(
											"property_index")).get("stock")
									.toString();
							String isStock = list.get(pos).get("isStock")
									.toString();
							for (int i = 0; i < list1.size(); i++) {
								Map<String, String> map = list1.get(i);
								if (map.get("id").equals(id)) {
									if (Integer.parseInt(map.get("count")) > 0) {
										existMap(list_StoreProduct.get(pos)
												.get("prod_basic_id")
												.toString(), list_StoreProduct
												.get(pos).get("name")
												.toString(), id,
												Integer.parseInt(map
														.get("count")) - 1,
												prices, pro, stock, isStock);
									} else {
										list1.remove(i);
									}
								} else {
									continue;
								}
							}

							int count = Integer.parseInt(list_StoreProduct
									.get(pos).get("pro_count").toString());
							if (count > 0) {
								list_StoreProduct.get(pos).put("pro_count",
										count - 1);
								holder2.textView_store_product_item_buycount
										.setText((count - 1) + "");
								holder2.imageView_store_product_item_reduce
										.setVisibility(View.VISIBLE);
							} else {
								holder2.imageView_store_product_item_reduce
										.setVisibility(View.GONE);
								holder2.textView_store_product_item_buycount
										.setVisibility(View.GONE);
							}
							adapter2.notifyDataSetChanged();
							refreshViews();
						}
					});
			if (!list.get(position).get("Short_desc").toString()
					.equalsIgnoreCase("")
					&& list.get(position).get("Short_desc").toString() != null) {
				holder2.linearlayout_store_product_item_des
						.setVisibility(View.VISIBLE);
				holder2.textView_store_product_item_des.setText(list
						.get(position).get("Short_desc").toString());
			} else {
				holder2.linearlayout_store_product_item_des
						.setVisibility(View.GONE);
			}
			return view;
		}
	}

	OrderHolder holder;

	class MyAdapter extends BaseAdapter {
		private List<Map<String, Object>> list = null;

		public MyAdapter(List<Map<String, Object>> list) {
			super();
			this.list = list;
		}

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

		class OrderHolder {
			public TextView textView_store_cat_name;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			holder = null;
			if (convertView == null) {
				convertView = YiKuFragment.this.getActivity()
						.getLayoutInflater()
						.inflate(R.layout.store_cat_list_item, null);
				holder = new OrderHolder();
				holder.textView_store_cat_name = (TextView) convertView
						.findViewById(R.id.textView_store_cat_name);
				convertView.setTag(holder);
				view = convertView;
			} else {
				view = convertView;
				holder = (OrderHolder) convertView.getTag();
			}
			holder.textView_store_cat_name.setText(list.get(position)
					.get("cat_name").toString());
			if (position == cur_pos) {// 如果当前的行就是ListView中选中的一行，就更改显示样式
				convertView.setBackgroundResource(R.color.shouye_title_blue);// 更改整行的背景色
				holder.textView_store_cat_name.setTextColor(Color.WHITE);// 更改字体颜色
			} else {
				convertView.setBackgroundResource(R.color.white);// 更改整行的背景色
				holder.textView_store_cat_name
						.setTextColor(R.color.product_name);// 更改字体颜色
			}
			return view;
		}

	}

	private MProcessDialog mInfoProgressDialog;

	public void showInfoProgressDialog() {
		if (mInfoProgressDialog == null)
			mInfoProgressDialog = new MProcessDialog(this.getActivity());
		mInfoProgressDialog.setMessage("加载中");
		mInfoProgressDialog.setCancelable(false);
		if (!this.getActivity().isFinishing()) {
			try {
				mInfoProgressDialog.show();
			} catch (Exception e) {

			}
		}
	}

	public void dismissInfoProgressDialog() {
		mInfoProgressDialog.dismiss();
	}

	/**
	 * 临时购物车
	 * 
	 * @param id
	 * @param count
	 * @param property
	 */
	public void existMap(String pos, String name, String id, int count,
			String price, String pro, String stock, String isStock) {
		refreshViews();
		if (list1.size() == 0) {
			imageView_shopcart.setImageResource(R.drawable.shopping_cart);
			cart_count_tv.setText("");
			cart_count_tv.setVisibility(View.GONE);

			if (count <= 0) {
				return;
			}
			Map<String, String> m = new HashMap<String, String>();
			m.put("id", id);
			m.put("pos", pos);
			m.put("name", name);
			m.put("count", String.valueOf(count));
			m.put("price", price);
			m.put("pro", pro);
			m.put("stock", stock);
			m.put("isStock", isStock);
			list1.add(m);
		} else {
			boolean isover = false;
			for (int i = 0; i < list1.size(); i++) {
				Map<String, String> map = list1.get(i);
				if (map.get("id").equals(id)) {
					isover = true;
					if (count > 0) {
						map.put("count", String.valueOf(count));
						map.put("price", price);
						map.put("pro", pro);
						map.put("stock", stock);
						map.put("isStock", isStock);
						break;
					} else {
						list1.remove(i);
						break;
					}
				} else {
					continue;
				}
			}

			if (!isover) {
				if (count > 0) {
					Map<String, String> m = new HashMap<String, String>();
					m.put("id", id);
					m.put("count", "" + count);
					m.put("price", price);
					m.put("pos", pos);
					m.put("name", name);
					m.put("pro", pro);
					m.put("stock", stock);
					m.put("isStock", isStock);
					list1.add(m);
				}
			}
		}
		refreshViews();
	}

	/**
	 * 刷新购物车布局
	 */
	private void refreshViews() {
		double prices = 0;
		int tcount = 0;

		clearSameData();

		if (list1 != null) {
			for (Map<String, String> map : list1) {
				prices += (Double.parseDouble(map.get("price")) * Integer
						.parseInt(map.get("count")));
				tcount += Integer.parseInt(map.get("count"));
			}
		}

		if (tcount > 0) {
			cart_count_tv.setVisibility(View.VISIBLE);
			cart_count_tv.setText(String.valueOf(tcount));
			if (send_fee > 0) {
				textView3_1.setText("配送费¥" + send_fee + "元");
			} else {
				textView3_1.setText("免费配送");
			}
			textView3.setVisibility(View.VISIBLE);
			textView3.setText("¥" + String.format("%.2f", prices));
			imageView_shopcart.setImageResource(R.drawable.shopping_cart_down);
		} else {
			cart_count_tv.setVisibility(View.GONE);
			cart_count_tv.setText("");
			textView3_1.setText("购物车空空哒~");
			textView3.setVisibility(View.GONE);
			textView3.setText("");
			imageView_shopcart.setImageResource(R.drawable.shopping_cart);
		}

		if (init_price == 0) {
			if (prices > init_price) {
				textView6.setText("选好了");
				textView6.setBackgroundResource(R.drawable.ok_dpwn);
			} else {
				textView6.setText("还差¥"
						+ String.format("%.2f", init_price - prices));
				textView6.setBackgroundResource(R.drawable.ok);
			}
		} else {
			if (prices >= init_price) {
				textView6.setText("选好了");
				textView6.setBackgroundResource(R.drawable.ok_dpwn);
			} else {
				textView6.setText("还差¥"
						+ String.format("%.2f", init_price - prices));
				textView6.setBackgroundResource(R.drawable.ok);
			}
		}
	}

	/**
	 * 获取当前选中的商品的所有购货量
	 * 
	 * @param pos
	 * @return
	 */
	private int rowCount(String pos) {
		int counts = 0;
		for (int i = 0; i < list1.size(); i++) {
			Map<String, String> map = list1.get(i);
			if (map.get("pos").equals(pos)) {
				counts += Integer.parseInt(map.get("count"));
			} else {
				continue;
			}
		}

		return counts;
	}

	private String rowCounts(String pos) {
		String temps = "";
		for (int i = 0; i < list1.size(); i++) {
			Map<String, String> map = list1.get(i);
			if (map.get("pos").equals(pos)) {
				temps += (map.get("id") + ",");
			} else {
				continue;
			}
		}
		Log.e("sss", "temsp is === " + temps);
		return temps;
	}

	TextView tips_right_red_circle_tv;
	TextView cart_price_tv;
	TextView cart_tips_tvs;
	PopupWindow popWindow;
	Button cart_finish_btn;
	ImageView cancel_cart_window_iv;
	private String store_id;

	// 显示购物车界面
	private void showPopwindow() {
		View parent = ((ViewGroup) this.getActivity().findViewById(
				android.R.id.content)).getChildAt(0);
		View popView = getActivity().getLayoutInflater().inflate(
				R.layout.cart_popwindow, null);
		// 初始化视图

		ListView cart_list_view = (ListView) popView
				.findViewById(R.id.cart_list_view);
		tlist = new ArrayList<Map<String, String>>(list1);

		// 屏幕的宽高
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;

		CartAdapter cadapter = new CartAdapter();
		cart_list_view.setAdapter(cadapter);

		cart_finish_btn = (Button) popView.findViewById(R.id.cart_finish_btn);
		cancel_cart_window_iv = (ImageView) popView
				.findViewById(R.id.cancel_cart_window_iv);

		tips_right_red_circle_tv = (TextView) popView
				.findViewById(R.id.tips_right_red_circle_tv);

		cart_price_tv = (TextView) popView.findViewById(R.id.cart_price_tv);
		cart_tips_tvs = (TextView) popView.findViewById(R.id.cart_tips_tvs);
		refreshPopWindow();

		// LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT);
		// cart_list_view.setLayoutParams(params);

		if (tlist.size() > 5) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 310);
			cart_list_view.setLayoutParams(params);
		} else {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			cart_list_view.setLayoutParams(params);
		}

		// 显示控件
		popWindow = new PopupWindow(popView, width, height);
		popWindow.setAnimationStyle(R.style.AnimBottom);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);

		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.cart_finish_btn:
					if (finishCartFlag) {
						if (MainActivity.isLogin) {
							Intent it = new Intent(getActivity(),
									SubmitOrderActivity.class);
							it.putExtra("cart_products", (Serializable) list1);
							it.putExtra("index", 1);
							it.putExtra("store_id", store_id);
							Log.e("sss", "store_id == " + store_id);
							startActivity(it);
						} else {
							Intent it = new Intent(getActivity(),
									LoginActivity.class);
							it.putExtra("cart_products", (Serializable) list1);
							it.putExtra("index", 1);
							it.putExtra("store_id", store_id);
							Log.e("sss", "store_id == " + store_id);
							it.putExtra("activity",
									StoreProductDetailsActivity.ACTIVITY1);
							startActivity(it);
						}
					}
					break;
				case R.id.cancel_cart_window_iv:

					break;
				case R.id.btn_camera_pop_cancel:
					list1 = new ArrayList<Map<String, String>>(tlist);
					stayTheSame();
					clearSameData();
					break;
				}
				popWindow.dismiss();
			}
		};
		cart_finish_btn.setOnClickListener(listener);
		cancel_cart_window_iv.setOnClickListener(listener);

		ColorDrawable dw = new ColorDrawable(0x30000000);
		popWindow.setBackgroundDrawable(dw);
		popWindow.showAtLocation(parent, Gravity.BOTTOM
				| Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	// 用来刷新弹出的购物车的金额
	void refreshPopWindow() {
		double price = 0;
		int count = 0;
		for (int i = 0; i < list1.size(); i++) {
			price += (Integer.parseInt(list1.get(i).get("count").toString()) * Double
					.parseDouble(list1.get(i).get("price").toString()));
			count += Integer.parseInt(list1.get(i).get("count").toString());
		}

		// DecimalFormat df = new DecimalFormat("#.00");

		tips_right_red_circle_tv.setText(String.valueOf(count));
		cart_price_tv.setText("共 ¥" + String.format("%.2f", price));

		if (send_fee > 0) {
			cart_tips_tvs.setText("配送费¥" + send_fee + "元");
		} else {
			cart_tips_tvs.setText("免费配送");
		}

		if (count > 0) {
			tips_right_red_circle_tv.setVisibility(View.VISIBLE);
			tips_right_red_circle_tv.setText(String.valueOf(count));
			cancel_cart_window_iv
					.setBackgroundResource(R.drawable.shopping_cart_down);
		} else {
			tips_right_red_circle_tv.setVisibility(View.GONE);
			tips_right_red_circle_tv.setText("");
			cancel_cart_window_iv
					.setBackgroundResource(R.drawable.shopping_cart);
		}

		if (init_price == 0) {
			if (price > init_price) {
				finishCartFlag = true;
				cart_finish_btn.setText("选好了");
				cart_finish_btn.setBackgroundResource(R.drawable.ok_dpwn);
			} else {
				finishCartFlag = false;
				cart_finish_btn.setText("还差¥"
						+ String.format("%.2f", init_price - price));
				cart_finish_btn.setBackgroundResource(R.drawable.ok);
			}
		} else {
			if (price >= init_price) {
				finishCartFlag = true;
				cart_finish_btn.setText("选好了");
				cart_finish_btn.setBackgroundResource(R.drawable.ok_dpwn);
			} else {
				finishCartFlag = false;
				cart_finish_btn.setText("还差¥"
						+ String.format("%.2f", init_price - price));
				cart_finish_btn.setBackgroundResource(R.drawable.ok);
			}
		}
	}

	// 数据保持一致，然后刷新界面
	private void stayTheSame() {
		if (list1 == null)
			return;
		if (list1.size() > 0) {

			if (list_StoreProduct != null) {
				for (int i = 0; i < list_StoreProduct.size(); i++) {
					list_StoreProduct.get(i).put("pro_count", 0);
				}
			}

			for (int i = 0; i < list1.size(); i++) {
				Map<String, String> map = list1.get(i);
				for (int k = 0; k < list_StoreProduct.size(); k++) {
					if (map.get("pos").equals(
							list_StoreProduct.get(k).get("prod_basic_id")
									.toString())) {
						// list_StoreProduct.get(k).put("pro_count",
						// map.get("count"));
						list_StoreProduct.get(k).put("pro_count",
								String.valueOf(rowCount(map.get("pos"))));
					} else {
						continue;
					}
				}
			}
		} else {
			if (list_StoreProduct != null) {
				for (int i = 0; i < list_StoreProduct.size(); i++) {
					list_StoreProduct.get(i).put("pro_count", 0);
				}
			}
		}
		if (adapter2 != null) {
			adapter2.notifyDataSetChanged();
		}
	}

	// 清除商品数量是0的数据
	private void clearSameData() {
		if (list1 == null)
			return;
		for (int i = 0; i < list1.size(); i++) {
			if (list1.get(i).get("count").equals("0")) {
				list1.remove(i);
			} else {
				continue;
			}
		}
		if (adapter2 != null) {
			adapter2.notifyDataSetChanged();
		}
	}

	/**
	 * 购物车列表显示适配器
	 * 
	 * @author lixiangchao
	 * 
	 */
	class CartAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return tlist.size();
		}

		@Override
		public Object getItem(int position) {
			return tlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CartViewHolder holder = null;
			if (null == convertView) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.cart_list_item, null);
				holder = new CartViewHolder();
				holder.cart_list_item_pro_name = (TextView) convertView
						.findViewById(R.id.cart_list_item_pro_name);
				holder.imageView_store_product_item_reduce2 = (ImageView) convertView
						.findViewById(R.id.imageView_store_product_item_reduce2);
				holder.textView_store_product_item_buycount2 = (TextView) convertView
						.findViewById(R.id.textView_store_product_item_buycount2);
				holder.imageView_store_product_item_add2 = (ImageView) convertView
						.findViewById(R.id.imageView_store_product_item_add2);
				holder.cart_list_item_pro_price = (TextView) convertView
						.findViewById(R.id.cart_list_item_pro_price);
				convertView.setTag(holder);
			} else {
				holder = (CartViewHolder) convertView.getTag();
			}

			final int pos = position;

			if (tlist.get(position).get("pro") != null) {
				holder.cart_list_item_pro_name.setText(tlist.get(position).get(
						"name")
						+ "(" + tlist.get(position).get("pro") + ")");
			} else {
				holder.cart_list_item_pro_name.setText(tlist.get(position).get(
						"name"));
			}

			holder.cart_list_item_pro_price.setText("¥"
					+ tlist.get(position).get("price"));
			holder.textView_store_product_item_buycount2.setText(tlist.get(
					position).get("count"));

			holder.imageView_store_product_item_reduce2
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							int count = Integer.parseInt(tlist.get(pos).get(
									"count"));

							if (count <= 0) {
								tlist.get(pos)
										.put("count", String.valueOf("0"));
								list1 = new ArrayList<Map<String, String>>(
										tlist);
								notifyDataSetChanged();
								stayTheSame();
								refreshPopWindow();
								return;
							}

							tlist.get(pos).put("count",
									String.valueOf(count - 1));
							list1 = new ArrayList<Map<String, String>>(tlist);
							notifyDataSetChanged();
							stayTheSame();
							refreshPopWindow();
						}
					});
			holder.imageView_store_product_item_add2
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							int count = Integer.parseInt(tlist.get(pos).get(
									"count"));

							int stock = Integer.parseInt(tlist.get(pos).get(
									"stock"));
							/**
							 * 对于有规格的商品 要查看所属商品的对应的总库存
							 */
							// count = rowCount(tlist.get(pos).get("pos"));
							if (tlist.get(pos).get("isStock").equals("1")) {
								if (count >= stock || stock == 0) {
									Toast.makeText(getActivity(), "库存不足",
											Toast.LENGTH_SHORT).show();
									return;
								}
							}

							tlist.get(pos).put(
									"count",
									String.valueOf(Integer.parseInt(tlist.get(
											pos).get("count")) + 1));
							list1 = new ArrayList<Map<String, String>>(tlist);
							notifyDataSetChanged();
							stayTheSame();
							refreshPopWindow();
						}
					});
			return convertView;
		}

		class CartViewHolder {
			TextView cart_list_item_pro_name;
			ImageView imageView_store_product_item_reduce2;
			TextView textView_store_product_item_buycount2;
			ImageView imageView_store_product_item_add2;
			TextView cart_list_item_pro_price;
		}
	}

	@Override
	public void onStart() {
		stayTheSame();
		refreshViews();
		super.onStart();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		stayTheSame();
		refreshViews();
		if (adapter2 != null) {
			adapter2.notifyDataSetChanged();
		}
		Log.e("sss", "onactivity result " + list1.toString());

		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 3 && resultCode == 3) {
			if (data != null) {
				cur_pos = 0;
				if (list_StoreCat != null) {
					list_StoreCat.clear();
				}
				if (list_StoreProduct != null) {
					list_StoreProduct.clear();
				}
				if (adapter != null) {
					adapter.notifyDataSetChanged();
				}
				if (adapter2 != null) {
					adapter2.notifyDataSetChanged();
				}

				String store_id = data.getStringExtra("store_id");

				list1.clear();
				stayTheSame();
				refreshViews();

				queryData(store_id);
				getStoreInfo(store_id);
			}
		}
	}

	public void setZeroCount() {
		if (list1 == null)
			return;
		for (int i = 0; i < list1.size(); i++) {
			list1.get(i).put("count", "0");
		}
	}

	/**
	 * 获取当前属性规格的购物车数量
	 * 
	 * @param pro_id
	 * @return
	 */
	private int getProCount(String pro_id) {
		int count = 0;
		if (list1 == null)
			return count;

		for (int i = 0; i < list1.size(); i++) {
			if (list1.get(i).get("id").equals(pro_id)) {
				count = Integer.parseInt(list1.get(i).get("count"));
				return count;
			}
		}
		return count;
	}

	public List<Map<String, Object>> JsonToList1(String jsonString) {
		List<Map<String, Object>> list_json = new ArrayList<Map<String, Object>>();

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			String RESULT = jsonObject.getString("result");
			if (RESULT.equalsIgnoreCase("SUCCESS")) {
				JSONArray jsonArray_list = jsonObject.getJSONArray("data");
				for (int i = 0; i < jsonArray_list.length(); i++) {
					JSONObject jsonObject_list2 = jsonArray_list
							.getJSONObject(i);
					Map<String, Object> map = new HashMap<String, Object>();
					if (JudgeExist.judgeExist(jsonObject_list2, "id")) {
						map.put("id", jsonObject_list2.getString("id"));// 商品id
					}
					if (JudgeExist.judgeExist(jsonObject_list2, "cat_name")) {
						map.put("cat_name",
								jsonObject_list2.getString("cat_name"));// 商品分类名称
					}
					if (JudgeExist.judgeExist(jsonObject_list2, "items")) {
						map.put("items", jsonObject_list2.getJSONArray("items"));// 商品分类下标
					}
					list_json.add(map);
				}
			}
			return list_json;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public List<Map<String, Object>> JsonToListProduct1(JSONArray array) {
		List<Map<String, Object>> list_json = new ArrayList<Map<String, Object>>();
		try {
			for (int j = 0; j < array.length(); j++) {
				JSONObject jsonObject_items = array.getJSONObject(j);
				Map<String, Object> map = new HashMap<String, Object>();
				if (JudgeExist.judgeExist(jsonObject_items, "prod_basic_id")) {
					map.put("prod_basic_id",
							jsonObject_items.getString("prod_basic_id"));// 商品id

				}

				map.put("flag", false);// 商品标识

				if (JudgeExist.judgeExist(jsonObject_items, "name")) {
					map.put("name", jsonObject_items.getString("name"));// 商品名称
				}
				if (JudgeExist.judgeExist(jsonObject_items, "image")) {
					map.put("image", jsonObject_items.getString("image"));// 商品图片
				}
				if (JudgeExist.judgeExist(jsonObject_items, "unitname")) {
					map.put("unitname", jsonObject_items.getString("unitname"));// 单位
				}
				if (JudgeExist.judgeExist(jsonObject_items, "Short_desc")) {
					map.put("Short_desc",
							jsonObject_items.getString("Short_desc"));// 简单描述
				}
				if (JudgeExist.judgeExist(jsonObject_items, "sale_count")) {
					map.put("month_sale",
							jsonObject_items.getString("sale_count"));// 商品名称
				}
				if (JudgeExist.judgeExist(jsonObject_items, "favor_count")) {
					map.put("zan", jsonObject_items.getString("favor_count"));// 商品名称
				}
				if (JudgeExist.judgeExist(jsonObject_items, "arrive_time")) {
					map.put("arrive_time",
							jsonObject_items.getString("arrive_time"));// 商品名称
				}
				if (JudgeExist.judgeExist(jsonObject_items, "isStock")) {
					map.put("isStock", jsonObject_items.getString("isStock"));//
				}
				if (JudgeExist.judgeExist(jsonObject_items, "property")) {
					JSONArray jsonArray_property = jsonObject_items
							.getJSONArray("property");
					List<Map<String, Object>> list_property = new ArrayList<Map<String, Object>>();
					int total_stock = 0;
					for (int k = 0; k < jsonArray_property.length(); k++) {
						JSONObject jsonObject_property = jsonArray_property
								.getJSONObject(k);
						Map<String, Object> map_property = new HashMap<String, Object>();
						if (JudgeExist.judgeExist(jsonObject_property,
								"store_prod_id")) {
							map_property.put("store_prod_id",
									jsonObject_property
											.getString("store_prod_id"));// 商品id
						}
						if (JudgeExist.judgeExist(jsonObject_property, "price")) {
							map_property.put("price", String
									.format("%.2f", jsonObject_property
											.getDouble("price") / 100));// 价格
						}
						if (JudgeExist.judgeExist(jsonObject_property, "pro")) {
							map_property.put("pro",
									jsonObject_property.getString("pro"));// 规格
						}
						if (JudgeExist.judgeExist(jsonObject_property,
								"product_count")) {
							map_property.put("product_count",
									jsonObject_property
											.getString("product_count"));// 数量
						}
						if (JudgeExist.judgeExist(jsonObject_property,
								"arrive_time")) {
							map_property.put("arrive_time", jsonObject_property
									.getString("arrive_time"));// 配送时间
						}
						if (JudgeExist.judgeExist(jsonObject_property, "stock")) {
							map_property.put("stock",
									jsonObject_property.getString("stock"));//
							total_stock += jsonObject_property.getInt("stock");
						}
						list_property.add(map_property);
					}
					map.put("total_stock", String.valueOf(total_stock));
					map.put("property", list_property);
					map.put("property_index", 0);
					map.put("pro_count", 0);
				}
				list_json.add(map);
			}

			return list_json;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
}
