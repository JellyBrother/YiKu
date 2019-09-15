package com.yst.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jack.ui.MProcessDialog;
import com.jack.ui.XListView;
import com.jack.ui.XListView.IXListViewListener;
import com.yst.yiku.R;

public class QueryChargeListActivity extends Activity implements IXListViewListener{
	private XListView listview;
	private ImageView query_charge_list_back_iv;
	private MyAdapter adapter;
	private Handler mHandler;
	private int PAGE_NO = 0;
	private int PAGE_COUNT = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.queary_charge_list);
		initView();
	}
	private void initView() {
		mHandler = new Handler();
		listview = (XListView) findViewById(R.id.query_charge_listview);
		listview.setDividerHeight(0);
		listview.setXListViewListener(this);
		listview.setPullLoadEnable(true);
		listview.setPullRefreshEnable(true);
		query_charge_list_back_iv = (ImageView) findViewById(R.id.query_charge_list_back_iv);
		query_charge_list_back_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		setAdapter();
	}
	private void setAdapter() {
		if(adapter == null) {
			adapter = new MyAdapter();
			listview.setAdapter(adapter);
		}else {
			adapter.notifyDataSetChanged();
		}
	}
	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return 10;
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null; 
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(QueryChargeListActivity.this, R.layout.charge_order_list_item, null);
				holder.charge_order_name = (TextView) convertView.findViewById(R.id.charge_order_name);
				holder.charge_order_number = (TextView) convertView.findViewById(R.id.charge_order_number);
				holder.charge_order_type = (TextView) convertView.findViewById(R.id.charge_order_type);
				holder.charge_order_state = (TextView) convertView.findViewById(R.id.charge_order_state);
				holder.item_fengexian = convertView.findViewById(R.id.item_fengexian);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			if (position == 0) {
				holder.item_fengexian.setVisibility(View.GONE);
			} else {
				holder.item_fengexian.setVisibility(View.VISIBLE);
			}
			return convertView;
		}
	}
	class ViewHolder{
		TextView charge_order_name,// 名称
			     charge_order_number,// 订单号
			     charge_order_type,// 订单类型
			     charge_order_state;// 订单装态
		View item_fengexian;
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
		listview.stopRefresh();
		listview.stopLoadMore();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH时mm分");
		String time = format.format(date);
		listview.setRefreshTime(time);
	}

	@Override
	public void onLoadMore() {
		showInfoProgressDialog();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				loadData();
			}
		}, 500);
	}

	private void loadData() {
		// itemChild.clear();
		if (PAGE_NO + 1 < PAGE_COUNT) {
			Log.e("联网前", PAGE_NO + "====" + PAGE_COUNT);
			PAGE_NO += 1;
		} else {
			Toast.makeText(this, "已经加载全部", Toast.LENGTH_SHORT).show();
			listview.setPullLoadEnable(false);
			onLoad();
		}
		dismissInfoProgressDialog();
	}

	@Override
	public void onRefresh() {
		showInfoProgressDialog();
		listview.setPullLoadEnable(true);
		PAGE_NO = 0;
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				dismissInfoProgressDialog();
			}
		}, 1500);
	}
}
