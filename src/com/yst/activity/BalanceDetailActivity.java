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
import com.guohua.common.util.NetworkUtils;
import com.jack.ui.MProcessDialog;
import com.jack.ui.XListView;
import com.jack.ui.XListView.IXListViewListener;
import com.yst.yiku.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 充值订单记录
 */
public class BalanceDetailActivity extends Activity implements
		OnRefreshListener, OnClickListener, IXListViewListener {

	private XListView listView;
	private MyAdapter mAdapter;
//	private ArrayList<Map<String, String>> items;
	private ArrayList<Map<String, String>> all;
	private RequestQueue mQueue;

	// 用来刷新的控件
	private SwipeRefreshLayout swipeLayout;
	// 用来显示的数据列表
	private MyAdapter adapter;

	private ImageView activity_back_iv;
	private TextView activity_title_tv;

	private MProcessDialog mInfoProgressDialog;

	private int flag = 0;
	private Handler mHandler;
	private int page_size = 15;
	private int totalSize = 0;
	private int m_currentpage = 0;

	private Button mGetOrderIdBtn;
	private EditText mPriceEt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		flag = getIntent().getIntExtra("FLAG", 0);

		if (flag == 0) {
			setContentView(R.layout.activity_balance_detail);
			initView();
		} else {
			setContentView(R.layout.activity_balance_recharge);
			initRecharge();
		}
	}

	private void initRecharge() {
		mQueue = Volley.newRequestQueue(this);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv
				.setText(R.string.activity_customer_balance_recharge_label);

		mPriceEt = (EditText) findViewById(R.id.balance_recharge_price_et);
		mGetOrderIdBtn = (Button) findViewById(R.id.balance_recharge_submit_btn);
		mGetOrderIdBtn.setOnClickListener(this);
		setPricePoint(mPriceEt);
	}

	private void initView() {
		mQueue = Volley.newRequestQueue(this);
//		items = new ArrayList<Map<String, String>>();
		all = new ArrayList<Map<String, String>>();
		mHandler = new Handler();

		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv
				.setText(R.string.activity_customer_balance_details_label);

		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.balance_detail_swipe_view);
		swipeLayout.setColorScheme(R.color.holo_blue_bright,
				R.color.holo_green_light, R.color.holo_orange_light,
				R.color.holo_red_light);
		swipeLayout.setVisibility(View.VISIBLE);

		listView = (XListView) findViewById(R.id.balance_detail_list_view);
		listView.setPullLoadEnable(false);
		listView.setPullRefreshEnable(false);
		listView.setXListViewListener(this);

		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		swipeLayout.setOnRefreshListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_back_iv:
			BalanceDetailActivity.this.finish();
			break;
		case R.id.balance_recharge_submit_btn:
			updateBalance();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (flag == 0) {
			all.clear();
			this.m_currentpage = 0;
			getBalanceList();
		}
	}

	private void updateBalance() {
		final String price = mPriceEt.getText().toString().trim();
		if (TextUtils.isEmpty(price) || Double.parseDouble(price) <= 0) {
			Toast.makeText(this, "请输入充值金额", Toast.LENGTH_SHORT).show();
			return;
		}

		Integer money_fen = (int) (Double.parseDouble(price) * 100);

		showInfoProgressDialog();

		JsonObjectRequest jsr = new JsonObjectRequest(
				Contacts.URL_ADD_CUSTOMER_FEE + "custom_id="
						+ Customer.customer_id + "&money_fen=" + money_fen
						+ Contacts.URL_ENDING, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							Log.e("sss", "the result is " + response.toString());
							String success = response.getString("result")
									.toLowerCase();
							if ("success".equals(success)) {
								if (response.has("data")) {
									Intent it = new Intent(
											BalanceDetailActivity.this,
											BalanceRechargeActivity.class);
									it.putExtra("price", price);
									it.putExtra("send_out_trade_no", response.getString("data"));
									startActivity(it);
									BalanceDetailActivity.this.finish();
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
							Log.e("sss", "get merchant list json exception "
									+ e.getMessage());
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						dismissInfoProgressDialog();
						Toast.makeText(getApplicationContext(), "访问网络错误，请重试",
								Toast.LENGTH_SHORT).show();
						Log.e("sss", "ErrorListener error message "
								+ new String(error.networkResponse.data));
					}
				});
		mQueue.add(jsr);

	}

	private void getBalanceList() {
		showInfoProgressDialog();
		JsonObjectRequest jsr = new JsonObjectRequest(Contacts.URL_CUSTOMER_OUT_ACTION + "customerId=" + Customer.customer_id + "&page_size="
				+ page_size + "&page_no=" + m_currentpage + Contacts.URL_ENDING, null, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					if("SUCCESS".equals(response.getString("result"))){
						Log.e("sss","balance details response to string is === " + response.toString());
						
						all.addAll(JSONToList(response));
						
						if(response.has("page_model")){
							totalSize = response.getJSONObject("page_model").getInt("rowCount");
						} else {
							totalSize = all.size();
						}
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getApplicationContext(), "" + response.getString("error_info"), Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "收支明细数据解析异常", Toast.LENGTH_SHORT).show();
				}
				dismissInfoProgressDialog();
			}
		}, new ErrorListener() {
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "网络访问错误，请重试", Toast.LENGTH_SHORT).show();
				dismissInfoProgressDialog();
			}
		});
		mQueue.add(jsr);
	}

	class MyAdapter extends BaseAdapter {
		public int getCount() {
			return all.size();
		}

		@Override
		public Object getItem(int position) {
			return all.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.activity_balance_order_item, null);
				holder.dateTv = (TextView) convertView
						.findViewById(R.id.balance_date_tv);
				holder.orderTv = (TextView) convertView
						.findViewById(R.id.balance_order_tv);
				holder.priceTv = (TextView) convertView
						.findViewById(R.id.balance_price_tv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			/**
			 * "id":1, 记录id
				"customerId":1, 顾客的id
				"money":1200, 金额变化
				"reason":1, 原因代码
				"reasonDesc":"用户充值", 原因代码的中文解释
				"incomingId":1,
				"orderId":1,
				"previewRemain":2400, 前余额
				"newRemain":3600, 变化后余额
				"updateDate":"2015-10-21 00:00:00" 时间

			 */
			
			if ((Double.parseDouble(all.get(position).get("newRemain")) / 100) - (Double.parseDouble(all.get(position).get("previewRemain"))) / 100 > 0) {
				String content = "<font color='green'>" + all.get(position).get("reasonDesc") + Double.parseDouble(all.get(position).get("money")) / 100 + "元" + "</font>";
				holder.orderTv.setText(Html.fromHtml(content));
			} else {
				String content = "<font color='red'>" + all.get(position).get("reasonDesc") + Double.parseDouble(all.get(position).get("money")) / 100 + "元" + "</font>";
				
				holder.orderTv.setText(Html.fromHtml(content));
			}
			if(all.get(position).get("updateDate") != null){
				holder.dateTv.setText(all.get(position).get("updateDate").substring(0, 10));
			} else if(all.get(position).get("createDate") != null){
				holder.dateTv.setText(all.get(position).get("createDate").substring(0, 10));
			} else {
				holder.dateTv.setText("");
			}
			String balance_remain = "余额：" + "<font color='#F28F00'>"	+ String.format("%.2f", Double.parseDouble(all.get(position).get("newRemain")) / 100) + "</font>";
			holder.priceTv.setText(Html.fromHtml(balance_remain));
			
			return convertView;
		}

		class ViewHolder {
			TextView dateTv, orderTv, priceTv;
		}
	}

	/**
	 * 用来判断金额输入的是否有效输入的是否有效
	 * 
	 * @param editText
	 */
	public void setPricePoint(final EditText editText) {
		// TODO 设置有效的价格
		editText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (count == 0)
					mGetOrderIdBtn
							.setBackgroundResource(R.drawable.balance_recharge_un_focus_png);
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf(".") > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						editText.setText(s);
						editText.setSelection(s.length());
					}
				}
				if (s.toString().trim().substring(0).equals(".")) {
					s = "0" + s;
					editText.setText(s);
					editText.setSelection(2);
				}

				if (s.toString().startsWith("0")
						&& s.toString().trim().length() > 1) {
					if (!s.toString().substring(1, 2).equals(".")) {
						editText.setText(s.subSequence(0, 1));
						editText.setSelection(1);
						return;
					}
				}
				if (count > 0)
					mGetOrderIdBtn
							.setBackgroundResource(R.drawable.balance_recharge_focus_png);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		});

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
	public void onRefresh() {
		all.clear();
		this.m_currentpage = 0;
		getBalanceList();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				swipeLayout.setRefreshing(true);
				adapter.notifyDataSetChanged();
				swipeLayout.setRefreshing(false);
			}
		}, 3000);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LoginActivity.hintKbTwo(BalanceDetailActivity.this);
	}

	@Override
	public void onLoadMore() {
		showNextPage();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				onLoad();
			}
		}, 1000);
	}

	// 加载更多的方法
	private void showNextPage() {
		if (totalSize != all.size()) {
			if (NetworkUtils.getNetworkState(this) != NetworkUtils.NETWORN_NONE) {
				this.m_currentpage++;
				getBalanceList();
			} else {
				Toast.makeText(BalanceDetailActivity.this, "无法连接网络",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(BalanceDetailActivity.this, "已加载全部数据",
					Toast.LENGTH_SHORT).show();
		}
	}

	// 用来关闭加载和刷新视图
	private void onLoad() {
		listView.stopLoadMore();
		listView.stopRefresh();
	}
	
	/**
	 * 用来解析充值明细的记录
	 * @param json
	 * @return
	 */
	private List<Map<String,String>> JSONToList(JSONObject json){
		
		/**
		 * "id":1, 记录id
			"customerId":1, 顾客的id
			"money":1200, 金额变化
			"reason":1, 原因代码
			"reasonDesc":"用户充值", 原因代码的中文解释
			"incomingId":1,
			"orderId":1,
			"previewRemain":2400, 前余额
			"newRemain":3600, 变化后余额
			"updateDate":"2015-10-21 00:00:00" 时间

		 */
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		
		if(json == null) return list ;
		
		if(json.has("data")){
			try {
				JSONArray array = json.getJSONArray("data");
				for(int i = 0 ; i < array.length(); i++){
					Map<String, String> map = new HashMap<String,String>();
					if(((JSONObject)array.get(i)).has("id")){
						map.put("id", ((JSONObject)array.get(i)).getString("id"));
					}
					if(((JSONObject)array.get(i)).has("customerId")){
						map.put("customerId", ((JSONObject)array.get(i)).getString("customerId"));
					}
					if(((JSONObject)array.get(i)).has("money")){
						map.put("money", ((JSONObject)array.get(i)).getString("money"));
					}
					if(((JSONObject)array.get(i)).has("reason")){
						map.put("reason", ((JSONObject)array.get(i)).getString("reason"));
					}
					if(((JSONObject)array.get(i)).has("reasonDesc")){
						map.put("reasonDesc", ((JSONObject)array.get(i)).getString("reasonDesc"));
					}
					if(((JSONObject)array.get(i)).has("orderId")){
						map.put("orderId", ((JSONObject)array.get(i)).getString("orderId"));
					}
					if(((JSONObject)array.get(i)).has("previewRemain")){
						map.put("previewRemain", ((JSONObject)array.get(i)).getString("previewRemain"));
					}
					if(((JSONObject)array.get(i)).has("newRemain")){
						map.put("newRemain", ((JSONObject)array.get(i)).getString("newRemain"));
					}
					if(((JSONObject)array.get(i)).has("updateDate")){
						map.put("updateDate", ((JSONObject)array.get(i)).getString("updateDate"));
					}
					list.add(map);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.e("sss","balance details json to list exception message is === " + e.getMessage());
			}
		}
		return list ;
	}
}