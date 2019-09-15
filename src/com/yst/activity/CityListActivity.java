package com.yst.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.guohua.common.util.CityModel;
import com.guohua.common.util.DBManager;
import com.jack.ui.MyLetterListView;
import com.jack.ui.MyLetterListView.OnTouchingLetterChangedListener;
import com.yst.yiku.R;

/**
 * 城市列表
 * 
 * @author sy
 * 
 */
public class CityListActivity extends Activity implements OnClickListener {
	private BaseAdapter adapter;
	private ListView mCityLit;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private SQLiteDatabase database;
	private ArrayList<CityModel> mCityNames;
	private ImageView activity_back_iv;// 返回
	private Handler handler;
	private OverlayThread overlayThread;
	private MyLetterListView letterListView;
	private TextView overlay;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.city_list);
		initView();
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		DBManager dbManager = new DBManager(this);
		dbManager.openDateBase();
		dbManager.closeDatabase();
		database = SQLiteDatabase.openOrCreateDatabase(DBManager.DB_PATH + "/"
				+ DBManager.DB_NAME, null);
		mCityNames = getCityNames();
		database.close();
		alphaIndexer = new HashMap<String, Integer>();
		setAdapter(mCityNames);
		mCityLit.setOnItemClickListener(new CityListOnItemClick());
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();
	}

	/**
	 * 初始化空件
	 */
	private void initView() {
		mCityLit = (ListView) findViewById(R.id.city_list);
		letterListView = (MyLetterListView) findViewById(R.id.cityLetterListView);
		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);

		activity_back_iv.setOnClickListener(this);
	}

	/**
	 * 从数据库获取城市数据
	 */
	private ArrayList<CityModel> getCityNames() {
		ArrayList<CityModel> names = new ArrayList<CityModel>();
		Cursor cursor = database.rawQuery(
				"SELECT * FROM T_City ORDER BY NameSort", null);
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToPosition(i);
			CityModel cityModel = new CityModel();
			cityModel.setCityName(cursor.getString(cursor
					.getColumnIndex("CityName")));
			cityModel.setNameSort(cursor.getString(cursor
					.getColumnIndex("NameSort")));
			names.add(cityModel);
		}
		return names;
	}

	/**
	 * 城市列表点击事件
	 */
	class CityListOnItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			CityModel cityModel = (CityModel) mCityLit.getAdapter()
					.getItem(pos);
			Intent intent = new Intent();
			intent.putExtra("cityname", cityModel.getCityName());
			setResult(1, intent);
			finish();
		}
	}

	/**
	 * 为listview设置数据
	 */
	private void setAdapter(List<CityModel> list) {
		if (list != null) {
			adapter = new ListAdapter(this, list);
			mCityLit.setAdapter(adapter);
		}
	}

	/**
	 * 城市列表适配器
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<CityModel> list;

		public ListAdapter(Context context, List<CityModel> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String currentStr = list.get(i).getNameSort();
				// 上一个汉语拼音首字母，如果不存在为 ""
				String previewStr = (i - 1) >= 0 ? list.get(i - 1)
						.getNameSort() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getNameSort();
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(list.get(position).getCityName());
			String currentStr = list.get(position).getNameSort();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1)
					.getNameSort() : " ";
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activity_back_iv:
			finish();
			break;
		}
	}

	// 初始化汉语拼音首字母弹出提示框

	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				mCityLit.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread); // 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	// 设置overlay不可见

	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

}