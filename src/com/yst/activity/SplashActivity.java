package com.yst.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import com.guohua.common.util.NetworkUtils;
import com.yst.yiku.R;

/**
 * 刚进入的欢迎界面
 * 
 * @author 易库
 * 
 */

public class SplashActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGTH = 3000;
	/** dialog识别码 */
	protected static final int DLG_NETWORK_NO_CONNECTED = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	}

	private void startActivity2() {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				goHome();
//				if (isFirst()) {
//				} else {
//					goGuide();
//				}
				// 两个参数分别表示进入的动画,退出的动画
				overridePendingTransition(R.anim.loginfade_in,
						R.anim.loginfade_out);

			}
		}, SPLASH_DISPLAY_LENGTH);
	}

	private void goHome() {
		Intent it = new Intent(this, MainActivity.class);
		startActivity(it);
		SplashActivity.this.finish();
	}

	private void goGuide() {
		Intent it = new Intent(this, GuideActivity.class);
		startActivity(it);
		SplashActivity.this.finish();
	}

	private boolean isFirst() {
		SharedPreferences sp = getSharedPreferences("yiku",
				Context.MODE_PRIVATE);
		return sp.getBoolean("IsFirstRun", false);
	}

	/**
	 * 无网提示设置，有网两秒后发送消息
	 */
	@Override
	public void onResume() {
		super.onResume();
		if (NetworkUtils.getNetworkState(this) == NetworkUtils.NETWORN_NONE) {
			Toast.makeText(SplashActivity.this, "网络尚未连接，请连接网络",
					Toast.LENGTH_SHORT).show();
			showDialog(DLG_NETWORK_NO_CONNECTED);
		} else {
			startActivity2();
		}
	}

	/**
	 * 调用dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DLG_NETWORK_NO_CONNECTED:
			return popUpDialog();
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	/**
	 * dialog方法实体
	 * 
	 * @return
	 */
	private Dialog popUpDialog() {
		return new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert).setTitle("信息")
				.setMessage("没有网络连接")
				.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						onPositiveBtnClick();
					}
				})
				.setNegativeButton("退出", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				}).create();
	}

	/**
	 * dialog中设置点击事件
	 */
	private void onPositiveBtnClick() {
		try {
			Intent mIntent = new Intent();
			ComponentName comp = new ComponentName("com.android.settings",
					"com.android.settings.WirelessSettings");
			mIntent.setComponent(comp);
			mIntent.setAction(Intent.ACTION_VIEW);
			startActivity(mIntent);
		} catch (Exception ex) {
			try {
				Intent mIntent = new Intent(Settings.ACTION_SETTINGS);
				startActivity(mIntent);
			} catch (Exception e) {
				Toast.makeText(this, "", Toast.LENGTH_LONG).show();
			}
		}
	}

}
