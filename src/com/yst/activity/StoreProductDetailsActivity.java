package com.yst.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.jack.fragment.YiKuFragment;
import com.jack.headpicselect.BitmapCache;
import com.yst.yiku.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * 商品详情界面
 */
public class StoreProductDetailsActivity extends Activity implements
		OnClickListener {

	private RequestQueue mQueue;
	private HashMap<String, Object> serializableMap = null;
	private ImageLoader imageLoader;

	private ImageView store_details_back_iv;
	private NetworkImageView imageView_store_product_photo;
	private TextView store_details_no_pic_tv;
	private TextView store_details_product_name_tv;
	private TextView textView_store_product_item_quantity1;
	private LinearLayout store_details_stander_main_layout;
	private TextView store_details_price_tv;
	private TextView store_details_unit_name_tv;
	private ImageView imageView_store_product_item_reduce;
	private TextView textView_store_product_item_buycount;
	private ImageView imageView_store_product_item_add;
	private TextView textView_store_product_desc_tv;
	private LinearLayout store_details_stander_layout;
	private TextView textView_store_product_item_zan;

	private ImageView imageView_shopcart1;
	private TextView store_details_cart_tips_tv;
	private TextView store_details_cart_price_tv;
	private TextView store_details_cart_finish_tv;
	private TextView cart_count_tv1;
	private TextView cart_price_tv, cart_tips_tvs;

	private List<Map<String, Object>> list_property = new ArrayList<Map<String, Object>>();
	private String prices ,store_id = "";
	private int index = 0 ;
	
	public static final int ACTIVITY1 = 1 ;
	public static final int ACTIVITY2 = 2 ;
	public static final int ACTIVITY3 = 3 ;
	public static final int ACTIVITY4 = 4 ;
	public static final int ACTIVITY5 = 5 ;
	
	private double initPrice = 0, send_fee = 0;
	private ImageView product_type_iv;
	private TextView textView_type_desc;
	private TextView textView_type_desc_count_tv;
	
	private String product_id = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store_product_details);
		setResult(RESULT_OK);

		Bundle bundle = getIntent().getExtras();
		serializableMap = (HashMap<String, Object>) bundle.get("PRODUCT_INFO");
		store_id = getIntent().getStringExtra("store_id");
		initPrice = getIntent().getDoubleExtra("init_price", 0);
		send_fee = getIntent().getDoubleExtra("send_fee", 0);
		initView();
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mQueue = Volley.newRequestQueue(this);
		imageLoader = new ImageLoader(mQueue, new BitmapCache());
		Bundle bundle = getIntent().getExtras();
		serializableMap = (HashMap<String, Object>) bundle.get("PRODUCT_INFO");
		index = getIntent().getIntExtra("pos", 0);
		
		product_type_iv  = (ImageView) findViewById(R.id.product_type_iv);
		textView_type_desc = (TextView) findViewById(R.id.textView_type_desc);

		store_details_back_iv = (ImageView) findViewById(R.id.store_details_back_iv);
		store_details_back_iv.setOnClickListener(this);
		imageView_store_product_photo = (NetworkImageView) findViewById(R.id.imageView_store_product_photo);
		imageView_store_product_photo.setOnClickListener(this);
		
		store_details_no_pic_tv = (TextView) findViewById(R.id.store_details_no_pic_tv);
		store_details_product_name_tv = (TextView) findViewById(R.id.store_details_product_name_tv);
		textView_store_product_item_quantity1 = (TextView) findViewById(R.id.textView_store_product_item_quantity1);
		store_details_stander_main_layout = (LinearLayout) findViewById(R.id.store_details_stander_main_layout);
		store_details_price_tv = (TextView) findViewById(R.id.store_details_price_tv);
		store_details_unit_name_tv = (TextView) findViewById(R.id.store_details_unit_name_tv);
		imageView_store_product_item_reduce = (ImageView) findViewById(R.id.imageView_store_product_item_reduce);
		imageView_store_product_item_reduce.setOnClickListener(this);
		textView_store_product_item_buycount = (TextView) findViewById(R.id.textView_store_product_item_buycount);
		imageView_store_product_item_add = (ImageView) findViewById(R.id.imageView_store_product_item_add);
		imageView_store_product_item_add.setOnClickListener(this);
		textView_store_product_desc_tv = (TextView) findViewById(R.id.textView_store_product_desc_tv);
		store_details_stander_layout = (LinearLayout) findViewById(R.id.store_details_stander_layout);
		imageView_shopcart1 = (ImageView) findViewById(R.id.imageView_shopcart1);
		// imageView_shopcart1.setOnClickListener(this);
		store_details_cart_tips_tv = (TextView) findViewById(R.id.store_details_cart_tips_tv);
		store_details_cart_price_tv = (TextView) findViewById(R.id.store_details_cart_price_tv);
		store_details_cart_finish_tv = (TextView) findViewById(R.id.store_details_cart_finish_tv);
		store_details_cart_finish_tv.setOnClickListener(this);
		cart_count_tv1 = (TextView) findViewById(R.id.cart_count_tv1);
		textView_store_product_item_zan = (TextView) findViewById(R.id.textView_store_product_item_zan);
		
		textView_type_desc_count_tv = (TextView) findViewById(R.id.textView_type_desc_count_tv);
		
		setData();
		refreshViews();
	}

	/**
	 * 用来刷新界面数据
	 */
	private void setData() {
		if (null != serializableMap && null != serializableMap.get("image")) {
			imageView_store_product_photo.setVisibility(View.VISIBLE);
			store_details_no_pic_tv.setVisibility(View.GONE);
			imageView_store_product_photo.setDefaultImageResId(R.drawable.app_default_icon);
			imageView_store_product_photo.setErrorImageResId(R.drawable.app_default_icon);
			imageView_store_product_photo.setImageUrl(
					"http://182.254.161.94:8080/ydg/"
							+ serializableMap.get("image").toString(),
					imageLoader);
		} else {
			imageView_store_product_photo.setVisibility(View.GONE);
			store_details_no_pic_tv.setVisibility(View.VISIBLE);
		}
		if (null != serializableMap && null != serializableMap.get("name")) {
			store_details_product_name_tv.setText(serializableMap.get("name")
					.toString());
		} else {
			store_details_product_name_tv.setText("商品名称不详");
		}

		if (null != serializableMap && null != serializableMap.get("property")) {
			store_details_stander_main_layout.setVisibility(View.VISIBLE);
			store_details_stander_layout.removeAllViews();
			list_property = (List<Map<String, Object>>) serializableMap.get("property");
			
			product_id = serializableMap.get("prod_basic_id").toString();
					
			for (int i = 0; i < list_property.size(); i++) {
				Button btn = new Button(this);
				int id = Integer.parseInt(list_property.get(i)
						.get("store_prod_id").toString());
				btn.setId(id);
				btn.setText("" + list_property.get(i).get("pro"));
				btn.setOnClickListener(new MyListener(id));
				// TODO 此处需要来显示当前是那种商品参数
				if ((Integer) serializableMap.get("property_index") == i) {
					btn.setTextColor(Color.parseColor("#FFFFFF"));
					btn.setBackgroundResource(R.drawable.property_list_item_focus_png);
				} else {
					btn.setTextColor(Color.parseColor("#000000"));
					btn.setBackgroundResource(R.drawable.property_list_item_un_focus_png);
				}
				LinearLayout.LayoutParams params = new LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.leftMargin = 10;
				btn.setLayoutParams(params);
				store_details_stander_layout.addView(btn);
			}
			
			if(serializableMap.get("isStock") != null) {
				//1 为现货 0 为订货
				if(serializableMap.get("isStock").equals("1")) {
					product_type_iv.setImageResource(R.drawable.xian20);
					if(list_property != null) {
						textView_type_desc.setText("库存：");
						textView_type_desc_count_tv.setText(list_property
								.get(Integer.parseInt(serializableMap.get("property_index")
										.toString())).get("stock").toString());
					} else {
						textView_type_desc.setText("库存：0" );
						textView_type_desc_count_tv.setVisibility(View.GONE);
						textView_type_desc_count_tv.setText("");
					}
				} else {
					product_type_iv.setImageResource(R.drawable.ding20);
					if(serializableMap.get("arrive_time") != null) {
						textView_type_desc.setText("送达时间：" + serializableMap.get("arrive_time"));
						textView_type_desc_count_tv.setVisibility(View.GONE);
						textView_type_desc_count_tv.setText("");
					} else {
						textView_type_desc_count_tv.setText("");
						textView_type_desc_count_tv.setVisibility(View.GONE);
						textView_type_desc.setText("送达时间：暂无");
					}
				}
			} else {
				product_type_iv.setVisibility(View.GONE);
			}
			
			if(serializableMap.get("month_sale") != null) {
				textView_store_product_item_quantity1.setText("销售量" + serializableMap.get("month_sale").toString());
			} else {
				textView_store_product_item_quantity1.setText("销售量0");
			}
			
			if(serializableMap.get("zan") != null) {
				textView_store_product_item_zan.setText(serializableMap.get("zan").toString());
			} else {
				textView_store_product_item_zan.setText("0");
			}

		} else {
			store_details_stander_main_layout.setVisibility(View.GONE);
		}

		if (null != serializableMap && null != serializableMap.get("property")) {
			prices = list_property
					.get(Integer.parseInt(serializableMap.get("property_index")
							.toString())).get("price").toString();
			store_details_price_tv.setVisibility(View.VISIBLE);
			store_details_price_tv.setText("¥" + prices);
		} else {
			store_details_price_tv.setVisibility(View.GONE);
			store_details_price_tv.setText("");
		}
		if (null != serializableMap && null != serializableMap.get("unitname")) {
			store_details_unit_name_tv.setText(" /"
					+ serializableMap.get("unitname").toString());
		} else {
			store_details_unit_name_tv.setVisibility(View.GONE);
		}

		if (null != serializableMap
				&& null != serializableMap.get("Short_desc")) {
			textView_store_product_desc_tv.setText(serializableMap.get(
					"Short_desc").toString());
		} else {
			textView_store_product_desc_tv.setVisibility(View.GONE);
		}

		if (null != serializableMap && null != serializableMap.get("pro_count")) {
			textView_store_product_item_buycount.setText(serializableMap.get(
					"pro_count").toString());
			int count = Integer.parseInt(serializableMap.get("pro_count")
					.toString());
			YiKuFragment.list_StoreProduct.get(index).put("pro_count", "" + count);
			if (count == 0) {
				cart_count_tv1.setVisibility(View.GONE);
				cart_count_tv1.setText("");
				imageView_shopcart1.setImageResource(R.drawable.shopping_cart);
				store_details_cart_tips_tv.setVisibility(View.VISIBLE);
				store_details_cart_tips_tv.setText("购物车空空哒~");
				store_details_cart_price_tv.setVisibility(View.GONE);
				store_details_cart_price_tv.setText("");
				store_details_cart_finish_tv
						.setBackgroundResource(R.drawable.ok);
				store_details_cart_finish_tv.setText("还差¥0");
			} else if (count > 0) {
				cart_count_tv1.setVisibility(View.VISIBLE);
				cart_count_tv1.setText("1");
				if (!TextUtils.isEmpty(prices)) {
					double tprice = Double.parseDouble(prices);
					if (count * tprice < 0) {
						imageView_shopcart1
								.setImageResource(R.drawable.shopping_cart);
						store_details_cart_tips_tv.setVisibility(View.VISIBLE);
						store_details_cart_tips_tv.setText("还差¥"
								+ (0 - count * tprice));
						store_details_cart_price_tv.setVisibility(View.GONE);
						store_details_cart_price_tv.setText("");
						store_details_cart_finish_tv
								.setBackgroundResource(R.drawable.ok);
						store_details_cart_finish_tv.setText("还差¥"
								+ (0 - count * tprice));
					} else {
						imageView_shopcart1
								.setImageResource(R.drawable.shopping_cart_down);
						store_details_cart_tips_tv.setVisibility(View.VISIBLE);
						store_details_cart_tips_tv.setText("免费配送");
						store_details_cart_price_tv.setVisibility(View.VISIBLE);
						store_details_cart_price_tv.setText("¥ "
								+ String.valueOf(count * tprice));
						store_details_cart_finish_tv
								.setBackgroundResource(R.drawable.ok_dpwn);
						store_details_cart_finish_tv.setText("选好了");
					}
				}
			}
		} else {
			textView_store_product_item_buycount.setText("0");
		}
	}

	/**
	 * 动态添加的规格数据
	 */
	class MyListener implements OnClickListener {
		int id;

		public MyListener(int id) {
			this.id = id;
		}

		public void onClick(View v) {
			for (int i = 0; i < list_property.size(); i++) {
				if (Integer.parseInt(list_property.get(i).get("store_prod_id")
						.toString()) == id) {
					serializableMap.put("property_index", i);
					if(list_property != null){
						prices = list_property.get(Integer.parseInt(serializableMap.get("property_index").toString())).get("price").toString();
					}
					setData();
					refreshViews();
					break;
				}
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.store_details_back_iv:
			finish();
			break;
		case R.id.imageView_store_product_item_add:
			//保持加载的数据的一致性
			String isStock = serializableMap.get("isStock").toString();
			String total_stock = serializableMap.get("total_stock").toString();
			String stock = list_property.get((Integer) serializableMap.get("property_index")).get("stock").toString();
			String id = list_property.get((Integer) serializableMap.get("property_index")).get("store_prod_id").toString();
			String price = list_property.get((Integer) serializableMap.get("property_index")).get("price").toString();
			String pro = list_property.get((Integer) serializableMap.get("property_index")).get("pro").toString();
			int count = Integer.parseInt(textView_store_product_item_buycount.getText().toString());
			
			if(isStock.equals("1")) {
				if(Integer.parseInt(total_stock) <= count){
					Toast.makeText(getApplicationContext(), "库存不足", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(getProCount(id) >= Integer.parseInt(stock)){
					Toast.makeText(getApplicationContext(), "库存不足", Toast.LENGTH_SHORT).show();
					return ;
				}
			}
			
			boolean isHas = false ;
			for(int i = 0 ;i<YiKuFragment.list1.size();i++){
				Map<String,String> map = YiKuFragment.list1.get(i);
				if(map.get("id").equals(id)){
					isHas = true;
					existMap(serializableMap.get("prod_basic_id").toString(),
							serializableMap.get("name").toString(),
							id, Integer.parseInt(map.get("count")) + 1,	price, pro, stock, isStock);
				} else {
					continue;
				}
			}
			
			if(!isHas){
				existMap(serializableMap.get("prod_basic_id").toString(),
						serializableMap.get("name").toString(),
						id, 1,	price, pro, stock, isStock);
			}
			//设置界面的文字显示
			textView_store_product_item_buycount.setText(String.valueOf(count + 1));
			serializableMap.put("pro_count", count + 1);

			if (count + 1 > 0) {
				cart_count_tv1.setVisibility(View.VISIBLE);
				cart_count_tv1.setText("1");
			} else {
				cart_count_tv1.setVisibility(View.GONE);
				cart_count_tv1.setText("");
			}

			refreshViews();
			break;
		case R.id.imageView_store_product_item_reduce:
			
			int count1 = Integer.parseInt(textView_store_product_item_buycount.getText().toString());
			if (count1 == 0){
				refreshViews();
				return;
			}
			
			String id1 = list_property.get((Integer) serializableMap.get("property_index")).get("store_prod_id").toString();
			String prices = list_property.get((Integer) serializableMap.get("property_index")).get("price").toString();
			String pro1 = list_property.get((Integer) serializableMap.get("property_index")).get("pro").toString();
			String stock1 = list_property.get((Integer) serializableMap.get("property_index")).get("stock").toString();
			String isStock1 = serializableMap.get("isStock").toString();
			String prod_basic_id = serializableMap.get("prod_basic_id").toString();
			
			boolean isOver = false ;
			for(int i = 0 ;i<YiKuFragment.list1.size();i++){
				Map<String,String> map = YiKuFragment.list1.get(i);
				if(map.get("id").equals(id1)){
					if(Integer.parseInt(map.get("count")) > 0){
						isOver = true;
						existMap(serializableMap.get("prod_basic_id").toString(),
								serializableMap.get("name").toString(),
								id1, Integer.parseInt(map.get("count")) - 1, prices, pro1, stock1, isStock1);
					} else {
						YiKuFragment.list1.remove(i);
						clearSameData();
						refreshViews();
					}
				} else {
					continue;
				}
			}
			
			if(!isOver){
				for(int i = 0 ;i<YiKuFragment.list1.size();i++){
					Map<String, String> map = YiKuFragment.list1.get(i);
					if(map.get("pos").equals(serializableMap.get("prod_basic_id").toString())){
						if(Integer.parseInt(map.get("count")) > 0){
							map.put("count", String.valueOf(Integer.parseInt(map.get("count")) -1));
							break;
						} else {
							YiKuFragment.list1.remove(i);
							continue;
						}
					}
				}
			}

			if (count1 - 1 > 0) {
				cart_count_tv1.setVisibility(View.VISIBLE);
				cart_count_tv1.setText("1");
			} else {
				cart_count_tv1.setVisibility(View.GONE);
				cart_count_tv1.setText("");
			}
			serializableMap.put("pro_count", count1 - 1);
			
			textView_store_product_item_buycount.setText(String.valueOf(rowCount(prod_basic_id)));
			refreshViews();
			break;
		case R.id.imageView_shopcart1:
			break;
		case R.id.store_details_cart_finish_tv:
			
			if(YiKuFragment.list1.size() > 0) {
				if(MainActivity.isLogin){
					Intent it = new Intent(this, SubmitOrderActivity.class);
					it.putExtra("cart_products", (Serializable)YiKuFragment.list1);
					it.putExtra("index", ACTIVITY1);
					it.putExtra("activity", ACTIVITY1);
					it.putExtra("store_id", store_id);
					startActivity(it);
					finish();
				} else {
					Intent it = new Intent(this, LoginActivity.class);
					it.putExtra("cart_products", (Serializable)YiKuFragment.list1);
					it.putExtra("index", ACTIVITY1);
					it.putExtra("activity", ACTIVITY1);
					it.putExtra("store_id", store_id);
					startActivity(it);
					finish();
				}
			} else {
				return ;
			}
			break;
		case R.id.imageView_store_product_photo:
			//TODO 需要获取商品的图文详情接口
			getGoodWebDetails();
			break;
		}
	}

	// 显示购物车界面
	private void showPopwindow() {
		View parent = ((ViewGroup) this.findViewById(android.R.id.content))
				.getChildAt(0);
		View popView = getLayoutInflater().inflate(R.layout.cart_popwindow, null);
		// 初始化视图

		ListView cart_list_view = (ListView) popView
				.findViewById(R.id.cart_list_view);
		CartAdapter cadapter = new CartAdapter();
		cart_list_view.setAdapter(cadapter);

		Button cart_finish_btn = (Button) popView
				.findViewById(R.id.cart_finish_btn);
		ImageView cancel_cart_window_iv = (ImageView) popView
				.findViewById(R.id.cancel_cart_window_iv);

		cart_price_tv = (TextView) popView.findViewById(R.id.cart_price_tv);
		cart_tips_tvs = (TextView) popView.findViewById(R.id.cart_tips_tvs);
		int totalCounts = Integer.parseInt(serializableMap.get("pro_count")
				.toString());
		double tprice = Double.parseDouble(prices);
		cart_price_tv.setText("¥" + String.valueOf(totalCounts * tprice));
		cart_tips_tvs.setText("免费配送");

		// 屏幕的宽高
		int width = getResources().getDisplayMetrics().widthPixels;
		int height = getResources().getDisplayMetrics().heightPixels;

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		cart_list_view.setLayoutParams(params);

		// 显示控件
		final PopupWindow popWindow = new PopupWindow(popView, width, height);
		popWindow.setAnimationStyle(R.style.AnimBottom);
		popWindow.setFocusable(true);
		popWindow.setOutsideTouchable(true);

		OnClickListener listener = new OnClickListener() {
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_camera_pop_camera:

					break;
				case R.id.btn_camera_pop_album:

					break;
				case R.id.btn_camera_pop_cancel:

					break;
				}
				setData();
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

	class CartAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int position) {
			return serializableMap.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CartViewHolder holder = null;
			if (null == convertView) {
				convertView = getLayoutInflater().inflate(
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

			holder.cart_list_item_pro_name.setText(serializableMap.get("name")
					.toString());
			holder.cart_list_item_pro_price.setText("¥" + prices);
			holder.textView_store_product_item_buycount2
					.setText(serializableMap.get("pro_count").toString());
			holder.imageView_store_product_item_reduce2
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							int pro_count = Integer.parseInt(serializableMap
									.get("pro_count").toString());
							if (pro_count == 0) {
								cart_price_tv.setText("");
								return;
							}
							serializableMap.put("pro_count", pro_count - 1);
							cart_price_tv.setText("¥ " + (pro_count - 1)
									* Double.parseDouble(prices));
							notifyDataSetChanged();
							setData();
						}
					});
			holder.imageView_store_product_item_add2
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							int pro_count = Integer.parseInt(serializableMap
									.get("pro_count").toString());
							serializableMap.put("pro_count", pro_count + 1);
							cart_price_tv.setText("¥ " + (pro_count + 1)
									* Double.parseDouble(prices));
							notifyDataSetChanged();
							setData();
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
	
	public void existMap(String pos, String name, String id, int count, String price, String pro, String stock, String isStock){
		refreshViews();
		if(YiKuFragment.list1.size() == 0){
			imageView_shopcart1.setImageResource(R.drawable.shopping_cart);
			cart_count_tv1.setText("");
			cart_count_tv1.setVisibility(View.GONE);
			
			if(count <= 0 ) {
				return ;
			}
			Map<String, String> m = new HashMap<String,String>();
			m.put("id", id);
			m.put("pos", pos);
			m.put("name", name);
			m.put("count", String.valueOf(count));
			m.put("price", price);
			m.put("pro", pro);
			m.put("stock", stock);
			m.put("isStock", isStock);
			YiKuFragment.list1.add(m);
		} else {
			boolean isover = false ;
			for(int i = 0 ;i<YiKuFragment.list1.size();i++){
				Map<String,String> map = YiKuFragment.list1.get(i);
				if(map.get("id").equals(id)){
					isover = true ;
					if(count > 0){
						map.put("count", String.valueOf(count));
						map.put("price", price);
						map.put("pro", pro);
						map.put("stock", stock);
						map.put("isStock", isStock);
						break;
					} else {
						YiKuFragment.list1.remove(i);
						break;
					}
				} else {
					continue;
				}
			}
			
			if(!isover) {
				if(count > 0){
					Map<String, String> m = new HashMap<String,String>();
					m.put("id", id);
					m.put("count", "" + count);
					m.put("price", price);
					m.put("pos", pos);
					m.put("name", name);
					m.put("pro", pro);
					m.put("stock", stock);
					m.put("isStock", isStock);
					YiKuFragment.list1.add(m);
				}
			}
		}
		
		refreshViews();
	}
	
	// 清除商品数量是0的数据
		private void clearSameData() {
			if(YiKuFragment.list1 == null) return ;
			for (int i = 0; i < YiKuFragment.list1.size(); i++) {
				if (YiKuFragment.list1.get(i).get("count").equals("0")) {
					YiKuFragment.list1.remove(i);
				} else {
					continue;
				}
			}
			refreshViews();
		}
	
	/**
	 * 获取当前属性规格的购物车数量
	 * @param pro_id
	 * @return
	 */
	private int getProCount(String pro_id) {
		int count = 0 ;
		if(YiKuFragment.list1 == null) return count;
		
		for (int i = 0; i < YiKuFragment.list1.size(); i++) {
			if (YiKuFragment.list1.get(i).get("id").equals(pro_id)) {
				count = Integer.parseInt(YiKuFragment.list1.get(i).get("count"));
				return count;
			}
		}
		return count ;
	}
	/**
	 * 刷新购物车布局
	 */
	private void refreshViews(){
		double prices = 0;
		int tcount = 0 ;
		
		for(Map<String,String> map : YiKuFragment.list1){
			prices += (Double.parseDouble(map.get("price")) * Integer.parseInt(map.get("count")));
			tcount += Integer.parseInt(map.get("count"));
		}
		
		if(initPrice == 0){
			if(prices > initPrice){
				store_details_cart_finish_tv.setText("选好了");
				store_details_cart_finish_tv.setBackgroundResource(R.drawable.ok_dpwn);
			} else {
				store_details_cart_finish_tv.setText("还差¥" + String.format("%.2f", (initPrice - prices)));
				store_details_cart_finish_tv.setBackgroundResource(R.drawable.ok);
			}
		} else {
			if(prices >= initPrice){
				store_details_cart_finish_tv.setText("选好了");
				store_details_cart_finish_tv.setBackgroundResource(R.drawable.ok_dpwn);
			} else {
				store_details_cart_finish_tv.setText("还差¥" + String.format("%.2f", (initPrice - prices)));
				store_details_cart_finish_tv.setBackgroundResource(R.drawable.ok);
			}
		}
		
		Log.e("sss","tcount ==== " + tcount);
		
		
		if(tcount > 0){
			if(send_fee > 0){
				store_details_cart_tips_tv.setText("配送费¥" + send_fee + "元");
			} else {
				store_details_cart_tips_tv.setText("免费配送");
			}
			cart_count_tv1.setVisibility(View.VISIBLE);
			cart_count_tv1.setText(String.valueOf(tcount));
			store_details_cart_price_tv.setVisibility(View.VISIBLE);
			store_details_cart_price_tv.setText("¥" + String.format("%.2f", prices));
			imageView_shopcart1.setImageResource(R.drawable.shopping_cart_down);
		} else {
			cart_count_tv1.setVisibility(View.GONE);
			cart_count_tv1.setText("");
			store_details_cart_tips_tv.setText("购物车空空哒~");
			store_details_cart_price_tv.setVisibility(View.GONE);
			store_details_cart_price_tv.setText("");
			imageView_shopcart1.setImageResource(R.drawable.shopping_cart);
		}
	}
	/**
	 * 商品的总数量
	 * @param pos
	 * @return
	 */
	private int rowCount(String pos) {
		int counts = 0;
		for (int i = 0; i < YiKuFragment.list1.size(); i++) {
			Map<String, String> map = YiKuFragment.list1.get(i);
			if (map.get("pos").equals(pos)) {
				counts += Integer.parseInt(map.get("count"));
			} else {
				continue;
			}
		}

		return counts;
	}
	
	private void getGoodWebDetails() {
		if(TextUtils.isEmpty(product_id)) {
			return ;
		}
		Intent intent = new Intent(StoreProductDetailsActivity.this, WebViewActivity.class);
		intent.putExtra("url", Contacts.URL_GET_GOOD_INFO + product_id);
		intent.putExtra("store_good_detail", 1);
		startActivity(intent);
	}
}