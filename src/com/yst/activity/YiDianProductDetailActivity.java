package com.yst.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
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
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/**
 * 商品详情界面
 */
public class YiDianProductDetailActivity extends Activity implements
		OnClickListener {

	private RequestQueue mQueue;
	private HashMap<String, Object> serializableMap = null;
	private ImageLoader imageLoader;

	private ImageView store_details_back_iv;
	private NetworkImageView imageView_store_product_photo;
	private TextView store_details_no_pic_tv;
	private TextView store_details_product_name_tv;
	private LinearLayout store_details_stander_main_layout;
	private TextView store_details_price_tv;
	private TextView store_details_unit_name_tv;
	private ImageView imageView_store_product_item_reduce;
	private TextView textView_store_product_item_buycount;
	private ImageView imageView_store_product_item_add;
	private TextView textView_store_product_desc_tv;
	private LinearLayout store_details_stander_layout;
	private TextView textView_store_product_item_zan;
	private TextView textView_store_product_item_quantity1;

	private ImageView imageView_shopcart1;
	private TextView store_details_cart_tips_tv;
	private TextView store_details_cart_price_tv;
	private TextView store_details_cart_finish_tv;
	private TextView cart_count_tv1;

	private List<Map<String, Object>> list_property = new ArrayList<Map<String, Object>>();
	private String prices = "", store_id = "";
	private int index = 0 ;
	
	public static final int ACTIVITY1 = 1 ;
	public static final int ACTIVITY2 = 2 ;
	public static final int ACTIVITY3 = 3 ;
	public static final int ACTIVITY4 = 4 ;
	public static final int ACTIVITY5 = 5 ;
	
	private double initPrice = 0, send_fee = 0;
	
	private ImageView product_type_iv;
	private TextView textView_type_desc;
	private String is_yd = "";
	private String product_id = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yidian_product_details);
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
		
		is_yd = getIntent().getStringExtra("is_yd");
		
		if(is_yd.equals("1")) {
			product_type_iv.setVisibility(View.GONE);
		} else {
			product_type_iv.setVisibility(View.VISIBLE);
		}

		store_details_back_iv = (ImageView) findViewById(R.id.store_details_back_iv);
		store_details_back_iv.setOnClickListener(this);
		imageView_store_product_photo = (NetworkImageView) findViewById(R.id.imageView_store_product_photo);
		imageView_store_product_photo.setOnClickListener(this);
		store_details_no_pic_tv = (TextView) findViewById(R.id.store_details_no_pic_tv);
		store_details_product_name_tv = (TextView) findViewById(R.id.store_details_product_name_tv);
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
		textView_store_product_item_quantity1 = (TextView) findViewById(R.id.textView_store_product_item_quantity1);
		
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
		
		Log.e("sss","map to string is === " + serializableMap.toString());
		
		Log.e("sss","map to string is === " + list_property
				.get(Integer.parseInt(serializableMap.get("property_index")
						.toString())).get("stock").toString());
		
		if(serializableMap.get("isStock") != null) {
			//1 为现货 0 为订货
			if(is_yd.equals("1")) {
				if(list_property
						.get(Integer.parseInt(serializableMap.get("property_index")
								.toString())).get("stock") != null) {
					textView_type_desc.setText("库存：" + list_property
							.get(Integer.parseInt(serializableMap.get("property_index")
									.toString())).get("stock").toString());
				} else {
					textView_type_desc.setText("库存：0" );
				}
			} else {
				if(serializableMap.get("isStock").equals("1")) {
					product_type_iv.setImageResource(R.drawable.xian20);
					if(list_property
							.get(Integer.parseInt(serializableMap.get("property_index")
									.toString())).get("stock") != null) {
						textView_type_desc.setText("库存：" + list_property
								.get(Integer.parseInt(serializableMap.get("property_index")
										.toString())).get("stock").toString());
					} else {
						textView_type_desc.setText("库存：0" );
					}
				} else {
					product_type_iv.setImageResource(R.drawable.ding20);
					if(serializableMap.get("arrive_time") != null) {
						textView_type_desc.setText("送达时间：" + serializableMap.get("arrive_time"));
					} else {
						textView_type_desc.setText("送达时间：暂无");
					}
				}
			}
		} else {
			product_type_iv.setVisibility(View.GONE);
		}

		if (null != serializableMap && null != serializableMap.get("pro_count")) {
			textView_store_product_item_buycount.setText(serializableMap.get(
					"pro_count").toString());
			int count = Integer.parseInt(serializableMap.get("pro_count")
					.toString());
			YiDianStoreGoodsActivity.list_StoreProduct.get(index).put("pro_count", "" + count);
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
			String total_stock = serializableMap.get("total_stock").toString();
			String id = list_property.get((Integer) serializableMap.get("property_index")).get("store_prod_id").toString();
			String price = list_property.get((Integer) serializableMap.get("property_index")).get("price").toString();
			String pro = list_property.get((Integer) serializableMap.get("property_index")).get("pro").toString();
			String isStock = serializableMap.get("isStock").toString();
			String stock = list_property.get((Integer) serializableMap.get("property_index")).get("stock").toString();
			int count = Integer.parseInt(textView_store_product_item_buycount.getText().toString());
			
			if(is_yd.equals("1")) {
				if(Integer.parseInt(total_stock) <= count){
					Toast.makeText(getApplicationContext(), "库存不足", Toast.LENGTH_SHORT).show();
					return ;
				}
				if(getProCount(id) >= Integer.parseInt(stock)){
					Toast.makeText(getApplicationContext(), "库存不足", Toast.LENGTH_SHORT).show();
					return ;
				}
			} else {
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
			}
			
			boolean isHas = false ;
			for(int i = 0 ;i<YiDianStoreGoodsActivity.list1.size();i++){
				Map<String,String> map = YiDianStoreGoodsActivity.list1.get(i);
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
						id, 1, price, pro, stock, isStock);
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
			if (count1 == 0) {
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
			for(int i = 0 ;i<YiDianStoreGoodsActivity.list1.size();i++){
				Map<String,String> map = YiDianStoreGoodsActivity.list1.get(i);
				if(map.get("id").equals(id1)){
					if(Integer.parseInt(map.get("count")) > 0){
						isOver = true;
						existMap(serializableMap.get("prod_basic_id").toString(),
								serializableMap.get("name").toString(),
								id1, Integer.parseInt(map.get("count")) - 1, prices, pro1, stock1, isStock1);
					} else {
						YiDianStoreGoodsActivity.list1.remove(i);
						clearSameData();
						refreshViews();
					}
				} else {
					continue;
				}
			}
			
			if(!isOver){
				for(int i = 0 ;i<YiDianStoreGoodsActivity.list1.size();i++){
					Map<String, String> map = YiDianStoreGoodsActivity.list1.get(i);
					if(map.get("pos").equals(serializableMap.get("prod_basic_id").toString())){
						if(Integer.parseInt(map.get("count")) > 0){
							map.put("count", String.valueOf(Integer.parseInt(map.get("count")) -1));
							break;
						} else {
							YiDianStoreGoodsActivity.list1.remove(i);
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
			
			if(YiDianStoreGoodsActivity.list1.size() > 0) {
				if(MainActivity.isLogin){
					Intent it = new Intent(this, SubmitOrderActivity.class);
					it.putExtra("cart_products", (Serializable)YiDianStoreGoodsActivity.list1);
					it.putExtra("index", StoreProductDetailsActivity.ACTIVITY2);
					it.putExtra("activity", ACTIVITY2);
					it.putExtra("store_id", store_id);
					startActivity(it);
					finish();
				} else {
					Intent it = new Intent(this, LoginActivity.class);
					it.putExtra("cart_products", (Serializable)YiDianStoreGoodsActivity.list1);
					it.putExtra("index", StoreProductDetailsActivity.ACTIVITY2);
					it.putExtra("activity", ACTIVITY2);
					it.putExtra("store_id", store_id);
					startActivity(it);
					finish();
				}
			} else {
				return ;
			}
			break;
		case R.id.imageView_store_product_photo:
			//TODO 获取商品的图文详情
			getGoodWebDetails();
			break;
		}
	}

	public void existMap(String pos, String name, String id, int count, String price, String pro, String stock, String isStock){
		refreshViews();
		if(YiDianStoreGoodsActivity.list1.size() == 0){
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
			YiDianStoreGoodsActivity.list1.add(m);
		} else {
			boolean isover = false ;
			for(int i = 0 ;i<YiDianStoreGoodsActivity.list1.size();i++){
				Map<String,String> map = YiDianStoreGoodsActivity.list1.get(i);
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
						YiDianStoreGoodsActivity.list1.remove(i);
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
					YiDianStoreGoodsActivity.list1.add(m);
				}
			}
		}
		
		refreshViews();
	}
	
	// 清除商品数量是0的数据
		private void clearSameData() {
			if(YiDianStoreGoodsActivity.list1 == null) return ;
			for (int i = 0; i < YiDianStoreGoodsActivity.list1.size(); i++) {
				if (YiDianStoreGoodsActivity.list1.get(i).get("count").equals("0")) {
					YiDianStoreGoodsActivity.list1.remove(i);
				} else {
					continue;
				}
			}
			refreshViews();
		}
	
	/**
	 * 刷新购物车布局
	 */
	private void refreshViews(){
		double prices = 0;
		int tcount = 0 ;
		
		for(Map<String,String> map : YiDianStoreGoodsActivity.list1){
			prices += (Double.parseDouble(map.get("price")) * Integer.parseInt(map.get("count")));
			tcount += Integer.parseInt(map.get("count"));
		}
		
		if(tcount > 0){
			cart_count_tv1.setVisibility(View.VISIBLE);
			cart_count_tv1.setText(String.valueOf(tcount));
			store_details_cart_price_tv.setVisibility(View.VISIBLE);
			store_details_cart_price_tv.setText("¥" + String.format("%.2f", prices));
			if(send_fee > 0){
				store_details_cart_tips_tv.setText("配送费¥" + send_fee + "元");
			} else {
				store_details_cart_tips_tv.setText("免费配送");
			}
			imageView_shopcart1.setImageResource(R.drawable.shopping_cart_down);
		} else {
			cart_count_tv1.setVisibility(View.GONE);
			cart_count_tv1.setText("");
			store_details_cart_price_tv.setVisibility(View.GONE);
			store_details_cart_price_tv.setText("");
			store_details_cart_tips_tv.setText("购物车空空哒~");
			imageView_shopcart1.setImageResource(R.drawable.shopping_cart);
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
	}
	
	/**
	 * 获取当前属性规格的购物车数量
	 * @param pro_id
	 * @return
	 */
	private int getProCount(String pro_id) {
		int count = 0 ;
		if(YiDianStoreGoodsActivity.list1 == null) return count;
		
		for (int i = 0; i < YiDianStoreGoodsActivity.list1.size(); i++) {
			if (YiDianStoreGoodsActivity.list1.get(i).get("id").equals(pro_id)) {
				count = Integer.parseInt(YiDianStoreGoodsActivity.list1.get(i).get("count"));
				return count;
			}
		}
		return count ;
	}
	
	/**
	 * 商品的总数量
	 * @param pos
	 * @return
	 */
	private int rowCount(String pos) {
		int counts = 0;
		for (int i = 0; i < YiDianStoreGoodsActivity.list1.size(); i++) {
			Map<String, String> map = YiDianStoreGoodsActivity.list1.get(i);
			if (map.get("pos").equals(pos)) {
				counts += Integer.parseInt(map.get("count"));
			} else {
				continue;
			}
		}

		return counts;
	}
	
	/**
	 * 获取商品的网页详情
	 */
	private void getGoodWebDetails() {
		if(TextUtils.isEmpty(product_id)) {
			return ;
		}
		Intent intent = new Intent(YiDianProductDetailActivity.this, WebViewActivity.class);
		intent.putExtra("url", Contacts.URL_GET_GOOD_INFO + product_id);
		intent.putExtra("store_good_detail", 1);
		startActivity(intent);
	}
}