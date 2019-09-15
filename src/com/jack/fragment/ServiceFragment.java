package com.jack.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.yst.activity.CustomerCenterActivity;
import com.yst.activity.LoginActivity;
import com.yst.activity.MainActivity;
import com.yst.activity.PayMobileActivity;
import com.yst.activity.QueryChargeActivity;
import com.yst.activity.WebViewActivity;
import com.yst.yiku.R;

public class ServiceFragment extends Fragment implements OnClickListener {

	// @Override
	// protected void onCreate(Bundle savedInstanceState) {
	// // TODO Auto-generated method stub
	// super.onCreate(savedInstanceState);
	// this.setContentView(R.layout.activity_service);
	// }
	private ImageView login_iv;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		rootView = inflater
				.inflate(R.layout.activity_service, container, false);
		login_iv = (ImageView) rootView.findViewById(R.id.login_iv);
		login_iv.setOnClickListener(this);
		rootView.findViewById(R.id.ser_cp).setOnClickListener(this);
		rootView.findViewById(R.id.ser_dc).setOnClickListener(this);
		rootView.findViewById(R.id.ser_df).setOnClickListener(this);
		rootView.findViewById(R.id.ser_dj).setOnClickListener(this);
		rootView.findViewById(R.id.ser_dyp).setOnClickListener(this);
		rootView.findViewById(R.id.ser_hcp).setOnClickListener(this);
		rootView.findViewById(R.id.ser_hfcz).setOnClickListener(this);
		rootView.findViewById(R.id.ser_hyyd).setOnClickListener(this);
		rootView.findViewById(R.id.ser_jdyd).setOnClickListener(this);
		rootView.findViewById(R.id.ser_jp).setOnClickListener(this);
		rootView.findViewById(R.id.ser_jqmp).setOnClickListener(this);
		rootView.findViewById(R.id.ser_kdcx).setOnClickListener(this);
		rootView.findViewById(R.id.ser_lmsh).setOnClickListener(this);
		rootView.findViewById(R.id.ser_qcp).setOnClickListener(this);
		rootView.findViewById(R.id.ser_rqf).setOnClickListener(this);
		rootView.findViewById(R.id.ser_sf).setOnClickListener(this);  
		rootView.findViewById(R.id.ser_sqfw).setOnClickListener(this);
		rootView.findViewById(R.id.ser_tshy).setOnClickListener(this);
		rootView.findViewById(R.id.ser_wmsc).setOnClickListener(this);
		rootView.findViewById(R.id.ser_wzcx).setOnClickListener(this);
		rootView.findViewById(R.id.ser_ykt).setOnClickListener(this);
		rootView.findViewById(R.id.ser_yygh).setOnClickListener(this);
		rootView.findViewById(R.id.ser_zc).setOnClickListener(this);
		rootView.findViewById(R.id.ser_zsh).setOnClickListener(this);

		rootView.findViewById(R.id.btn_bar).setOnClickListener(this);
		rootView.findViewById(R.id.bar_layout).setOnClickListener(this);
		rootView.findViewById(R.id.bar_layout).setVisibility(View.GONE);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		// rootView.findViewById(R.id).setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_iv:
			if (MainActivity.isLogin) {
				startActivity(new Intent(getActivity(),
						CustomerCenterActivity.class));
			} else {
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;

		case R.id.ser_hfcz:
			if (MainActivity.isLogin) {
				startActivity(new Intent(getActivity(), PayMobileActivity.class));
			} else {
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.ser_sf:
			if (MainActivity.isLogin) {
				startActivity(new Intent(getActivity(), QueryChargeActivity.class).putExtra("Query_Charge_Index", QueryChargeActivity.QUERY_CHARGE_WATER));
			} else {
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.ser_df:
			if (MainActivity.isLogin) {
				startActivity(new Intent(getActivity(), QueryChargeActivity.class).putExtra("Query_Charge_Index", QueryChargeActivity.QUERY_CHARGE_ELECTRIC));
			} else {
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.ser_rqf:
			if (MainActivity.isLogin) {
				startActivity(new Intent(getActivity(), QueryChargeActivity.class).putExtra("Query_Charge_Index", QueryChargeActivity.QUERY_CHARGE_GAS));
			} else {
				Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
				startActivity(new Intent(getActivity(), LoginActivity.class));
			}
			break;
		case R.id.ser_zsh:// 中石化
			// UI.showToast("即将开通");
			Toast.makeText(this.getActivity(), "研发中!", Toast.LENGTH_SHORT)
					.show();
			// if (AppContext.saveUserState == null) {
			// UI.showToast("请先登录");
			// // UI.push(DengLuActivity.class);
			//
			// Intent mIntent = new Intent();
			// mIntent.setClass(getActivity(), DengLuActivity.class);
			// int requestCode = 2;
			// startActivityForResult(mIntent, requestCode);
			//
			// } else {
			// Intent intent = new Intent(this.getActivity(),
			// PetroChinaActivity.class);
			// this.startActivity(intent);
			// }
			break;
		case R.id.ser_cp: // 彩票
			// UI.showToast("即将开通");
			break;
		case R.id.ser_lmsh: {
			Toast.makeText(this.getActivity(), "研发中!", Toast.LENGTH_SHORT)
					.show();
		}
			// if (AppContext.chengshidingweiTM.shi == null) {
			// UI.showToast("尚未定位成功，请稍后...");
			// return;
			// }
			// Intent intent = new Intent(this.getActivity(),
			// CouponMainActivity.class);
			// intent.putExtra(CouponListActivity.LAT,
			// AppContext.chengshidingweiTM.weidu);
			// intent.putExtra(CouponListActivity.LON,
			// AppContext.chengshidingweiTM.jingdu);
			// intent.putExtra(CouponListActivity.CITY,
			// AppContext.chengshidingweiTM.shi);
			// intent.putExtra(CouponListActivity.REGION,
			// AppContext.chengshidingweiTM.qu);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// startActivity(intent);
			// }
			break;
		case R.id.ser_dyp: {
			// Intent intent = new Intent(this.getActivity(),
			// CouponListActivity.class);
			// CouponContext.currentCity = AppContext.chengshidingweiTM.shi;
			// intent.putExtra(CouponListActivity.LAT,
			// AppContext.chengshidingweiTM.weidu + "");
			// intent.putExtra(CouponListActivity.LON,
			// AppContext.chengshidingweiTM.jingdu + "");
			// intent.putExtra(CouponListActivity.CITY,
			// AppContext.chengshidingweiTM.shi);
			// intent.putExtra(CouponListActivity.KEYWORD, "电影票");
			// this.startActivity(intent);

			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra(
					"url",
					"http://m.dianping.com/tuan/home/c/2002_0_10_0?utm_source=ucss&utm_medium=cpc&utm_term=%E7%94%B5%E5%BD%B1%E7%A5%A8%E5%9B%A2%E8%B4%AD&utm_content=%E5%85%A8%E5%9B%BD-%E7%94%B5%E5%BD%B1&utm_campaign=%E5%85%A8%E5%9B%BD-%E7%94%B5%E5%BD%B1-%E5%9B%A2%E8%B4%AD");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_jdyd: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://m.ly.com/hotel/?refid=41854027");
			this.startActivity(intent);
			// if (AppContext.saveUserState == null) {
			// UI.showToast("请先登录");
			// UI.push(DengLuActivity.class);
			// return;
			//
			// }
			//
			// if (AppContext.chengshidingweiTM.shi == null) {
			// UI.showToast("尚未定位成功，请稍后...");
			// return;
			// }
			// Intent intent2 = new Intent(this.getActivity(),
			// HotelSearchActivity.class);
			// this.startActivity(intent2);
		}
			break;
		case R.id.ser_jqmp: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://m.ly.com/scenery/?refid=41854027");
			this.startActivity(intent);
			// if (AppContext.chengshidingweiTM.shi == null) {
			// UI.showToast("尚未定位成功，请稍后...");
			// return;
			// }
			// Intent intent = new Intent(this.getActivity(),
			// ScenicSpotsSearchActivity.class);
			// this.startActivity(intent);
		}
			break;
		case R.id.ser_jp: {

			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://m.ly.com/flightnew/#");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_hcp: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url",
					"http://m.tieyou.com/?utm_source=zsx734971012&channel_type=app");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_qcp: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://touch.trip8080.com");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_zc: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://m.zuche.com");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_dj: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://h5.edaijia.cn");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_dc: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://taxi.map.baidu.com");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_wmsc: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://i.waimai.meituan.com");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_hyyd: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://m.daoxila.com");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_kdcx: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://m.kuaidi100.com/");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_wzcx: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://www.aneee.com/wap.html");
			this.startActivity(intent);
		}
			break;
		case R.id.ser_yygh: {
			Intent intent = new Intent(this.getActivity(),
					WebViewActivity.class);
			intent.putExtra("url", "http://wy.guahao.com");
			this.startActivity(intent);
		}
			break;
		case R.id.btn_gr:
		}

	}

}