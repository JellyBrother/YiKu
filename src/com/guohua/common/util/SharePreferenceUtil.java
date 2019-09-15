package com.guohua.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 
 * 标题、简要说明. <br>
 * 类详细说明.
 * <p>
 * Copyright: Copyright (c) Jul 7, 2013 1:11:48 PM
 * <p>
 * Company: 北京宽连十方数字技术有限公司
 * <p>
 * 
 * @author xiaqz@c-platform.com
 * @version 1.0.0
 */
public class SharePreferenceUtil {
	public static String PREF_NAME = "otm_lite";
	public static String DEF_STRING_VALUE = null;
	public static int DEF_INT_VALUE = -887787;
	public static long DEF_LONG_VALUE = -7878761l;

	public static String getAsString(Context context, String key,
			String defaultValue) {
		if (key == null || context == null)
			return defaultValue;

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preference.getString(key, defaultValue);
	}

	public static int getAsInt(Context context, String key, int defaultValue) {
		if (key == null || context == null)
			return defaultValue;

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preference.getInt(key, defaultValue);
	}

	public static long getAsLong(Context context, String key, long defaultValue) {
		if (key == null || context == null)
			return defaultValue;

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preference.getLong(key, defaultValue);
	}

	public static boolean getAsBoolean(Context context, String key,
			boolean defaultValue) {
		if (key == null || context == null)
			return defaultValue;

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		return preference.getBoolean(key, defaultValue);
	}

	public static void setBoolean(Context context, String key, boolean value) {
		if (key == null || context == null)
			return;

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preference.edit();
		editor.putBoolean(key, value);
		editor.commit();

	}

	public static void setString(Context context, String key, String value) {
		if (key == null || context == null)
			return;

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preference.edit();
		editor.putString(key, value);
		editor.commit();

	}

	public static void setInt(Context context, String key, int value) {
		if (key == null || context == null)
			return;

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preference.edit();
		editor.putInt(key, value);
		editor.commit();

	}

	public static void setLong(Context context, String key, long value) {
		if (key == null || context == null)
			return;

		SharedPreferences preference = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = preference.edit();
		editor.putLong(key, value);
		editor.commit();

	}
}
