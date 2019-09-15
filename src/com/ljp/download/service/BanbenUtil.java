package com.ljp.download.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.yst.yiku.R;

/**
 * 用于版本更新的版本工具类
 * 
 * @author smile
 * 
 */
public class BanbenUtil {
	private static final String TAG = "Version";
	public static AlertDialog alertDialog;

	/**
	 * 获取当前应用程序的版本号
	 * 
	 * @param context
	 * @return
	 */
	private static String getAppVersion(Context context) {
		// 获取手机的包管理者
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packInfo = pm.getPackageInfo(context.getPackageName(),
					0);
			return packInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// 不可能发生.
			// can't reach
			return "";

		}
	}

	/**
	 * VerCode
	 * 
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	/**
	 * VerName
	 * 
	 * @param context
	 * @return
	 */
	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;
	}

	/**
	 * 从XML中获取Version name
	 * 
	 * @param context
	 * @return
	 */
	// public static String getVerNameFromStringXML(Context context) {
	// String verName = context.getResources().getText(R.string.ve)
	// .toString();
	// return verName;
	// }

	/**
	 * 从XML中获取app name
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppNameFromStringXML(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}

	/**
	 * 更新提示框
	 * 
	 * @param context
	 * @param msg
	 * @param positiveListener
	 * @param negativeListener
	 */
	public static void showSelectDialog(Context context, String msg,
			DialogInterface.OnClickListener positiveListener,
			DialogInterface.OnClickListener negativeListener) {
		if (null != alertDialog) {
			alertDialog.dismiss();
		}
		alertDialog = new AlertDialog.Builder(context).setMessage(msg)
				.setTitle("温馨提示").setPositiveButton("是", positiveListener)
				.setNegativeButton("否", negativeListener).create();
		alertDialog.show();
	}
}
