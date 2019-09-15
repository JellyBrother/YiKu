package com.jack.ui;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yst.yiku.R;

public class QueryDetailsDialog {
	private AlertDialog dialog;
	private Context context;
	private List<Map<String,String>> list = null;
	private ListView listview;
	private MyAdapter adapter;
	public QueryDetailsDialog(Context context,List<Map<String,String>> list) {
		this.context = context;
		this.list = list;
		init();
	}

	private void init() {
		dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		// 得到窗口
		Window window = dialog.getWindow();
		// 设置动画	
		//window.setWindowAnimations(R.style.AnimBottom);
		// 窗体管理者
		WindowManager manager = dialog.getWindow().getWindowManager();
		// 可以获取窗体宽高的工具
		Display display = manager.getDefaultDisplay();
		// 获取对话框的宽高参数
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		// 设置对话框宽为窗体宽
		params.width = display.getWidth() * 4 / 5;
		params.height = display.getHeight() * 2 / 3;
		// 为dialog设置属性
		dialog.getWindow().setAttributes(params);
		// 对话从窗体底部弹出
		window.setGravity(Gravity.CENTER); 
		// 设置窗体对话框布局
		window.setContentView(R.layout.query_details_dialog);
		listview = (ListView) window.findViewById(R.id.dialog_listview);
		listview.setDividerHeight(0);
		setAdapter();
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(queryHistoryItem != null) {
					queryHistoryItem.getQueryHistoryItem(position);
				}
				dismiss();
			}
		});
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
			ViewHolder holder = null; 
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(context, R.layout.query_details_item, null);
				holder.query_details_item_tv = (TextView) convertView.findViewById(R.id.query_details_item_tv);
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
			
			holder.query_details_item_tv.setText(list.get(position).get("name"));
			return convertView;
		}
	}
	
	class ViewHolder{
		TextView query_details_item_tv;
		View item_fengexian;
	}
	
	IQueryHistoryItem queryHistoryItem = null;
	
	public interface IQueryHistoryItem{
		void getQueryHistoryItem(int position);
	}
	
	public void setIQueryHistoryItem(IQueryHistoryItem queryHistoryItem){
		this.queryHistoryItem = queryHistoryItem;
	}
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		dialog.dismiss();
	}
}
