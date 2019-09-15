package com.jack.contacts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;

public class NetworkUtils {
	public static final int NETWORN_NONE = 0;
	public static final int NETWORN_WIFI = 1;
	public static final int NETWORN_MOBILE = 2;
	public static final int NETWORN_LOCAL = 3;

	/**
	 * 判断手机是否联网
	 */
	public static int getNetworkState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// Wifi
		State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NETWORN_WIFI;
		}
		// 本地网络
		state = connManager.getNetworkInfo(9).getState();
		if (state == State.CONNECTED || state == State.CONNECTING) {
			return NETWORN_LOCAL;
		}
		// 3G
		if (isCanUseSim(context)) {
			state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
					.getState();
			if (state == State.CONNECTED || state == State.CONNECTING) {
				return NETWORN_MOBILE;
			}
		}
		return NETWORN_NONE;
	}

	public static boolean shifoulianwang(Context context) {
		int jiguo = getNetworkState(context);
		if (jiguo == NETWORN_NONE) {
			return false;
		} else {
			return true;
		}
	}

	public static boolean isCanUseSim(Context context) {
		try {
			TelephonyManager mgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);

			return TelephonyManager.SIM_STATE_READY == mgr.getSimState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
