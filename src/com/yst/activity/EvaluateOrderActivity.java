package com.yst.activity;

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
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;

/**
 * 订单评价界面
 * @author lixiangchao
 *
 */
public class EvaluateOrderActivity extends Activity implements OnClickListener, OnRatingBarChangeListener{
	
	// 标题显示控件和返回控件
	private ImageView activity_back_iv ;
	private TextView activity_title_tv ;

	//用户评分控件和提示框
//	private TextView evaluate_order_zongtipingjia_desc_tv, evaluate_order_chanpinzhiliang_desc_tv, evaluate_order_peisongfuwu_desc_tv ;
//	private RatingBar ratingBar1, ratingBar2, ratingBar3 ;
	
	private TextView evaluate_order_zongtipingjia_desc_tv ;
	private RatingBar ratingBar1 ;
	private EditText evaluate_order_desc_et ;
	private Button commit_evaluate_order_btn ;
	
	private ListView evaluate_order_list_view;
	private MyAdapter adapter ;
	private RequestQueue requestQueue;
	private MProcessDialog mInfoProgressDialog;
	private List<Map<String,String>> list, idsList;
	private String order_id = "", store_id = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evaluate_order);
		
		initView();
	}
	
	private void initView(){
		requestQueue = Volley.newRequestQueue(this);
		idsList = new ArrayList<Map<String,String>>();
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv.setText(R.string.activity_customer_evaluate_order_label);
		
		evaluate_order_zongtipingjia_desc_tv = (TextView) findViewById(R.id.evaluate_order_zongtipingjia_desc_tv);
//		evaluate_order_chanpinzhiliang_desc_tv = (TextView) findViewById(R.id.evaluate_order_chanpinzhiliang_desc_tv);
//		evaluate_order_peisongfuwu_desc_tv = (TextView) findViewById(R.id.evaluate_order_peisongfuwu_desc_tv);
		
		ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
//		ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
//		ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);
		ratingBar1.setOnRatingBarChangeListener(this);
//		ratingBar2.setOnRatingBarChangeListener(this);
//		ratingBar3.setOnRatingBarChangeListener(this);
		
		evaluate_order_desc_et = (EditText) findViewById(R.id.evaluate_order_desc_et);
		
		commit_evaluate_order_btn = (Button) findViewById(R.id.commit_evaluate_order_btn);
		commit_evaluate_order_btn.setOnClickListener(this);
		
		evaluate_order_list_view = (ListView) findViewById(R.id.evaluate_order_list_view);
		
		order_id = getIntent().getStringExtra("ORDER_ID");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getOrderDetailInfo(order_id);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.activity_back_iv:
			finish();
			break;
		case R.id.commit_evaluate_order_btn:
			commitProductEvaluate();
			break;
		}
	}
	
	
	private Handler handler = null;
	private final int STATE1 = 0;
	private final int STATE2 = 1;
	private String favor_item = "" ;
	private String comm_content = "";
	
	// 用来提交订单的评价
	private void commitProductEvaluate(){
		//TODO
		
		if(ratingBar1.getRating() < 1){
			Toast.makeText(this, "请评价满意度", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		comm_content = evaluate_order_desc_et.getText().toString().trim();
		favor_item = "";
		
		if(idsList.size() > 0){
			for(int i = 0 ; i < idsList.size() ; i++){
				if(i == idsList.size() - 1){
					favor_item += idsList.get(i).get("store_prod_id");
				} else {
					favor_item += (idsList.get(i).get("store_prod_id") + ",");
				}
			}
		}
		
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case STATE1:
					break;
				case STATE2:
					dismissInfoProgressDialog();
					String result = (String) msg.obj;
					Log.e("sss", "result is " + result);
					if (result.contains("success") || result.contains("Success") || result.contains("SUCCESS")) {
						Toast.makeText(getApplicationContext(), "订单评论成功", Toast.LENGTH_SHORT).show();
						EvaluateOrderActivity.this.sendBroadcast(new Intent(CustomerOrdersActivity.ORDER_REFRESH_ACTION));
						EvaluateOrderActivity.this.finish();
					} else {
						JSONObject data;
						try {
							data = new JSONObject(result);
							Toast.makeText(EvaluateOrderActivity.this, "" + data.getString("error_info"), Toast.LENGTH_SHORT).show();
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(EvaluateOrderActivity.this, "呀，异常了!", Toast.LENGTH_SHORT).show();
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
				String url = Contacts.URL_SAVE_CUSTOMER_COMMENT;
				String map = null;
				
				if(TextUtils.isEmpty(comm_content)) {
					comm_content = "懒人不评价";
				}
				
				map = "customer_id=" + Customer.customer_id + "&order_id=" + order_id + "&store_id=" + store_id + "&comm_content=" + comm_content + "&comm_star=" + (int)ratingBar1.getRating() + "&favor_item=" + favor_item + Contacts.URL_ENDING;
				
				Log.e("sss"," url is === " + url);
				Log.e("sss"," url is === " + map);
				
				String result = NetWorkHelper.postImg_Record2(url, map);
				Message msg = handler.obtainMessage();
				msg.what = STATE2;
				msg.obj = result;
				handler.sendMessage(msg);
			}
		}).start();
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		// TODO Auto-generated method stub
		String tips = "请评价";
		if(0 < rating && rating < 2 ){
			tips = "差";
		} else if(1 < rating && rating < 3){
			tips = "一般";
		} else if(2 < rating && rating < 4){
			tips = "好";
		} else if(3 < rating && rating < 5){
			tips = "很好";
		} else if(4 < rating && rating < 6){
			tips = "非常好";
		}
		switch(ratingBar.getId()){
		case R.id.ratingBar1:
			evaluate_order_zongtipingjia_desc_tv.setText(tips);
			break;
//		case R.id.ratingBar2:
//			evaluate_order_chanpinzhiliang_desc_tv.setText(tips);
//			break;
//		case R.id.ratingBar3:
//			evaluate_order_peisongfuwu_desc_tv.setText(tips);
//			break;
		}
	}
	
	class MyAdapter extends BaseAdapter{

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
			ViewHolder holder = null ;
			if(null == convertView){
				convertView = getLayoutInflater().inflate(R.layout.evaluate_order_list_item, null);
				holder = new ViewHolder();
				holder.mbox = (CheckBox) convertView.findViewById(R.id.evaluate_order_goods_like_checkbox);
				holder.evaluate_order_goods_name_tv = (TextView) convertView.findViewById(R.id.evaluate_order_goods_name_tv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			final int pos = position;
			
			holder.evaluate_order_goods_name_tv.setText(list.get(position).get("name"));
			holder.mbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						idsList.add(list.get(pos));
					} else {
						idsList.remove(list.get(pos));
					}
				}
			});
			
			return convertView;
		}
		
		class ViewHolder{
			CheckBox mbox;//用来保存是否喜欢保存
			TextView evaluate_order_goods_name_tv;
		}
	}
	
	/**
	 * @param order_id 订单的id
	 */
	private void getOrderDetailInfo(String order_id){
		// url = http://182.254.161.94:8080/ydg/productOrder!getInfo?order_id=20151005171948867&client_type=A&version=1
		
		showInfoProgressDialog();
		
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_GET_ORDER_DETAIL_INFO + "order_id=" + order_id + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			public void onResponse(JSONObject response) {
				try {
					if("SUCCESS".equals(response.getString("result"))){
						list = JsonToProducts(response);
//						SimpleAdapter sAdapter = new SimpleAdapter(EvaluateOrderActivity.this, list, R.layout.order_details_store_item, new String[]{"name", "count", "price"}, new int[]{R.id.order_details_store_item_name_tv, R.id.order_details_store_item_count_tv, R.id.order_details_store_item_price_tv});
						adapter = new MyAdapter();
						evaluate_order_list_view.setAdapter(adapter);
						setListViewHeightBasedOnChildren(evaluate_order_list_view);
						
						store_id = response.getJSONObject("data").getString("store_id");
						
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
					dismissInfoProgressDialog();
				} catch (JSONException e) {
					e.printStackTrace();
					dismissInfoProgressDialog();
					Toast.makeText(getApplicationContext(), "数据解析异常", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				dismissInfoProgressDialog();
				Toast.makeText(getApplicationContext(), "访问网络错误，请重试!", Toast.LENGTH_SHORT).show();
			}
		});
		requestQueue.add(jsr);
	}
	
	/**
	 * 解析订单的详情接口
	 * @param json
	 * @return
	 */
	private List<Map<String,String>> JsonToProducts(JSONObject json){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if (json.has("data")) {
			try {
				for (int i = 0; i < json.getJSONObject("data").getJSONArray("product_order_item").length(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("store_prod_id",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("store_prod_id"));
					map.put("id",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("id"));
					map.put("price",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("price"));
					map.put("count", "x" + ((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item")
							.get(i)).getString("count"));
					map.put("is_stock",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("is_stock"));
					map.put("status", ((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item")
							.get(i)).getString("status"));
					map.put("order_id",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("order_id"));
					map.put("name",
							((JSONObject) json.getJSONObject("data").getJSONArray("product_order_item").get(i))
									.getString("name"));
					list.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				list.clear();
			}
		}
		return list ;
	}
	
	/**
	 * 为了解决ListView在ScrollView中只能显示一行数据的问题
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
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
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
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
