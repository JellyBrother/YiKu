package com.guahua.common.cache;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class ApplicationDataCleanManager {

	public static String getApplicationCachePath(Context context) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdFile = Environment.getExternalStorageDirectory();
			File deletefile = new File(sdFile.getPath() + "/"
					+ context.getApplicationInfo().packageName);
			RecursionDeleteFile(deletefile);
			return sdFile.getPath() + "/"
					+ context.getApplicationInfo().packageName;
		} else {
			File file = context.getFilesDir();
			RecursionDeleteFile(file);
			return file.getPath();
		}

	}

	public static void cleanApplicationCache(Context context) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdFile = Environment.getExternalStorageDirectory();
			File deletefile = new File(sdFile.getPath() + "/"
					+ context.getApplicationInfo().packageName + "/");
			RecursionDeleteFile(deletefile);
			File deloldfile = new File(sdFile.getPath()
					+ "/qingpingguo/Downloads/");
			RecursionDeleteFile(deloldfile);
		} else {
			File file = context.getFilesDir();
			File deletefile = new File(file.getPath() + "/"
					+ context.getApplicationInfo().packageName + "/");
			RecursionDeleteFile(deletefile);
			File deloldfile = new File(file.getPath()
					+ "/qingpingguo/Downloads/");
			RecursionDeleteFile(deloldfile);
		}

	}

	public static void cleanCustomCacheApp(Context context) {
		try {
			cleanInternalCache(context);
			cleanExternalCache(context);
			cleanFiles(context);
			cleanApplicationCache(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void RecursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				RecursionDeleteFile(f);
			}
			file.delete();
		}
	}

	public static void cleanInternalCache(Context context) {
		File sdFile = context.getCacheDir();
		File deletefile = new File(sdFile.getPath() + "/"
				+ context.getApplicationInfo().packageName + "/");
		RecursionDeleteFile(deletefile);
	}

	public static void cleanDatabases(Context context) {
		RecursionDeleteFile(new File("/data/data/" + context.getPackageName()
				+ "/databases"));
	}

	public static void cleanSharedPreference(Context context) {
		RecursionDeleteFile(new File("/data/data/" + context.getPackageName()
				+ "/shared_prefs"));
	}

	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	public static void cleanFiles(Context context) {

		File sdFile = context.getFilesDir();
		File deletefile = new File(sdFile.getPath() + "/"
				+ context.getApplicationInfo().packageName + "/");
		RecursionDeleteFile(deletefile);
	}

	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {

			File sdFile = context.getCacheDir();
			File deletefile = new File(sdFile.getPath() + "/"
					+ context.getApplicationInfo().packageName + "/");

			RecursionDeleteFile(deletefile);
		}
	}

	public static void cleanCustomCache(String filePath) {
		RecursionDeleteFile(new File(filePath));
	}

	public static void cleanApplicationDataApplication(Context context) {
		cleanInternalCache(context);
		cleanExternalCache(context);
		cleanDatabases(context);
		cleanSharedPreference(context);
		cleanFiles(context);
		cleanCustomCache(getApplicationCachePath(context));

	}

	public static void cleanApplicationData(Context context, String... filepath) {
		cleanInternalCache(context);
		cleanExternalCache(context);
		cleanDatabases(context);
		cleanSharedPreference(context);
		cleanFiles(context);
		for (String filePath : filepath) {
			cleanCustomCache(filePath);
		}
	}

	public static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

}
