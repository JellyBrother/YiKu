package com.jack.chengshidingwei;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.guohua.common.util.SharePreferenceUtil;
import com.jack.contacts.AppContext_city;
import com.jack.ui.CustomerHeaderView;
import com.jack.ui.CustomerHeaderView.HeaderListener;
import com.yst.yiku.R;

public class MallStoreGPSSearchActivity extends FragmentActivity implements
		OnPoiSearchListener, OnClickListener, OnItemClickListener,
		HeaderListener {

	private EditText searchText;// 输入搜索关键字
	private String keyWord = "";// 要输入的poi搜索关键字
	private ProgressDialog progDialog = null;// 搜索时进度条
	private PoiResult poiResult; // poi返回的结果
	private int currentPage = 0;// 当前页面，从0开始计数
	private PoiSearch.Query query;// Poi查询条件类
	private PoiSearch poiSearch;// POI搜索
	private ListView listView;
	SharePreferenceUtil sph = new SharePreferenceUtil();

	MyAdapter adapter;
	private CustomerHeaderView headerView;
	private TextView geRen; // 标题
	private ImageView btn_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poikeywordsearch_activity);
		headerView = (CustomerHeaderView) this.findViewById(R.id.header);
		headerView.setHeaderListener(this);

		geRen = (TextView) findViewById(R.id.head_title);
		geRen.setText("健桥地图定位");
		btn_main = (ImageView) findViewById(R.id.btn_main);
		btn_main.setVisibility(View.GONE);
		setUpMap();
	}

	/**
	 * 设置页面监听
	 */
	private void setUpMap() {
		ImageView searButton = (ImageView) findViewById(R.id.searchButton);
		searButton.setOnClickListener(this);
		searchText = (EditText) findViewById(R.id.keyWord);
		searchText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							searchClick();
							return true;
						}
						return false;
					}
				});

		View relocate = this.findViewById(R.id.relocate);
		relocate.setOnClickListener(this);

		ImageView imageReturn = (ImageView) this.findViewById(R.id.btn_return);
		imageReturn.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.list);
		this.currentType = ListType.HISTORY;
		loadHistory();
		adapter = new MyAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

	}

	public void loadHistory() {
		this.historyList.clear();
		String hises = SharePreferenceUtil.getAsString(this, "history", "");
		if (hises != null && !hises.equalsIgnoreCase("")) {
			String[] hisArray = hises.split(";");

			for (String s : hisArray) {
				if (s.trim().length() > 0)
					historyList.add(s);
			}
		}
		if (this.historyList.size() > 0) {
			this.historyList.add("清除搜索记录。。。");
		}
	}

	public void saveHistory() {
		if (historyList.size() > 10) {
			historyList = historyList.subList(historyList.size() - 10,
					historyList.size() - 1);
		}
		StringBuilder s = new StringBuilder();

		for (int i = 0; i < historyList.size() - 1; i++) {
			s.append(historyList.get(i));
			s.append(";");
		}

		SharePreferenceUtil.setString(this, "history", s.toString());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		this.saveHistory();
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(false);
		progDialog.setMessage("正在搜索:\n" + keyWord);
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 开始进行poi搜索
	 */
	protected void doSearchQuery() {
		showProgressDialog();// 显示进度框
		currentPage = 0;
		String city = "北京";
		if (AppContext_city.chengshidingweiTM.shi != null)
			city = AppContext_city.chengshidingweiTM.shi;
		query = new PoiSearch.Query(keyWord, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
		query.setPageSize(20);// 设置每页最多返回多少条poiitem
		query.setPageNum(currentPage);// 设置查第一页

		PoiSearch.SearchBound bound = new PoiSearch.SearchBound(
				new LatLonPoint(AppContext_city.chengshidingweiTM.weidu,
						AppContext_city.chengshidingweiTM.jingdu), 20000);

		poiSearch = new PoiSearch(this, query);
		poiSearch.setBound(bound);
		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	/**
	 * 判断高德地图app是否已经安装
	 */
	public boolean getAppIn() {
		PackageInfo packageInfo = null;
		try {
			packageInfo = this.getPackageManager().getPackageInfo(
					"com.autonavi.minimap", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		// 本手机没有安装高德地图app
		if (packageInfo != null) {
			return true;
		}
		// 本手机成功安装有高德地图app
		else {
			return false;
		}
	}

	/**
	 * 获取当前app的应用名字
	 */
	public String getApplicationName() {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	/**
	 * poi没有搜索到数据，返回一些推荐城市的信息
	 */
	private void showSuggestCity(List<SuggestionCity> cities) {
		String infomation = "推荐城市\n";
		for (int i = 0; i < cities.size(); i++) {
			infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
					+ cities.get(i).getCityCode() + "城市编码:"
					+ cities.get(i).getAdCode() + "\n";
		}

	}

	class SearchItem {
		public double latitude, longitude;
		public String name;
		public String address;
		public String type;
	}

	List<SearchItem> searchList = new ArrayList<SearchItem>();

	List<String> historyList = new ArrayList<String>();

	enum ListType {
		HISTORY, STORES
	};

	ListType currentType = ListType.STORES;

	class MyAdapter extends BaseAdapter {

		class ViewHolder {
			public TextView textStore;
			public TextView textType;
			public TextView textAddress;
			public TextView textHistory;
			public View storeContainer;;
			public View searchIcon;

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			switch (currentType) {
			case HISTORY:
				return historyList.size();
			case STORES:
				return searchList.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			switch (currentType) {
			case HISTORY:
				return historyList.get(position);
			case STORES:
				return searchList.get(position);
			}

			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(MallStoreGPSSearchActivity.this)
						.inflate(R.layout.poi_list_item, null);
				holder = new ViewHolder();
				holder.storeContainer = view.findViewById(R.id.store);
				holder.textAddress = (TextView) view
						.findViewById(R.id.store_address);
				holder.textHistory = (TextView) view.findViewById(R.id.history);
				holder.textStore = (TextView) view
						.findViewById(R.id.store_name);
				holder.textType = (TextView) view.findViewById(R.id.store_type);
				holder.searchIcon = view.findViewById(R.id.searchIcon);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			switch (currentType) {
			case HISTORY:
				holder.storeContainer.setVisibility(View.GONE);
				holder.textHistory.setVisibility(View.VISIBLE);
				holder.searchIcon.setVisibility(View.VISIBLE);
				holder.textHistory.setText(historyList.get(position));
				if (position == historyList.size() - 1) {
					holder.searchIcon.setVisibility(View.GONE);
				}
				break;
			case STORES:
				holder.storeContainer.setVisibility(View.VISIBLE);
				holder.textHistory.setVisibility(View.GONE);
				holder.searchIcon.setVisibility(View.GONE);
				holder.textStore.setText(searchList.get(position).name);
				holder.textType.setText(searchList.get(position).type);
				holder.textAddress.setText(searchList.get(position).address);
				// float f = AMapUtils.calculateLineDistance(new
				// LatLng(searchList.get(position).latitude,searchList.get(position).longitude),
				// new
				// LatLng(AppContext.chengshidingweiTM.weidu,AppContext.chengshidingweiTM.jingdu));
				// holder.textType.setText((int)f+"米");
				break;
			}

			return view;
		}

	}

	/**
	 * POI详情查询回调方法
	 */
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int rCode) {

	}

	/**
	 * POI信息查询回调方法
	 */
	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {// 搜索poi的结果
				if (result.getQuery().equals(query)) {// 是否是同一条
					poiResult = result;
					// 取得搜索到的poiitems有多少页
					List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					List<SuggestionCity> suggestionCities = poiResult
							.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息

					if (poiItems != null && poiItems.size() > 0) {
						this.findViewById(R.id.historyView).setVisibility(
								View.GONE);
						this.currentType = ListType.STORES;
						this.searchList.clear();

						for (PoiItem item : poiItems) {
							SearchItem i = new SearchItem();
							i.address = item.getSnippet();
							i.latitude = item.getLatLonPoint().getLatitude();
							i.longitude = item.getLatLonPoint().getLongitude();
							i.name = item.getTitle();
							i.type = item.getTypeDes();
							if (item.getTypeDes() != null) {
								i.type = item.getTypeDes().split(";")[0];
							}
							searchList.add(i);

						}
						adapter.notifyDataSetChanged();

					} else {
						this.showToast("未找到记录");
					}
				}
			} else {

			}
		} else if (rCode == 27) {
			showToast("网络错误");
		} else if (rCode == 32) {
			showToast("未找到记录");
		}

	}

	void showToast(String toast) {
		Toast.makeText(this, toast, 500).show();
	}

	/**
	 * Button点击事件回调方法
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		/**
		 * 点击搜索按钮
		 */
		case R.id.searchButton:
			searchClick();
			break;
		case R.id.relocate:
			this.finish();
			break;
		case R.id.btn_return:
			this.finish();
		default:
			break;
		}
	}

	void searchClick() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		keyWord = searchText.getText().toString();
		if (!"".equals(keyWord)) {
			if (this.historyList.size() == 0) {
				this.historyList.add(keyWord);
				this.historyList.add("清除搜索记录。。。");
			} else {
				this.historyList.add(this.historyList.size() - 1, keyWord);
			}
			doSearchQuery();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		switch (this.currentType) {
		case HISTORY:
			if (position == this.historyList.size() - 1) {
				this.historyList.clear();
				this.adapter.notifyDataSetChanged();
				return;
			}
			searchText.setText(this.historyList.get(position));
			keyWord = searchText.getText().toString();
			this.doSearchQuery();

			break;
		case STORES:
			SearchItem item = searchList.get(position);
			AppContext_city.chengshidingweiTM.jingdu = item.longitude;
			AppContext_city.chengshidingweiTM.weidu = item.latitude;
			AppContext_city.chengshidingweiTM.street = item.name;
			this.finish();
			break;
		}
	}

	@Override
	public void onLeftClick() {
		this.finish();
	}

	@Override
	public void onRightClick() {
		// TODO Auto-generated method stub

	}

}