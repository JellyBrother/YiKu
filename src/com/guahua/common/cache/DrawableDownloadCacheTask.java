/**
 * 
 */
package com.guahua.common.cache;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

/**
 * 
 * 加载缓存的task. <br>
 * 类详细说�?
 * <p>
 * Copyright: Copyright (c) 2013-1-31 下午7:11:42
 * <p>
 * Company: 北京宽连十方数字�?��有限公司
 * <p>
 * 
 * @author dingyj@c-platform.com
 * @version 1.0.0
 */
public class DrawableDownloadCacheTask extends
		AsyncTask<String, Void, Drawable> {

	protected String url;
	private Context context;
	private DrawableDownloadCacheListener dl;
	private String path;

	public DrawableDownloadCacheTask(Context context,
			DrawableDownloadCacheListener dl) {
		this.context = context;
		this.dl = dl;
	}

	@Override
	protected Drawable doInBackground(String... params) {
		this.url = params[0];
		this.path = params[1];
		return getBitmapFromUrl(url);
	}

	@Override
	protected void onPostExecute(Drawable result) {
		dl.returnDrawable(result);
	}

	/**
	 * 从url加载图片
	 * 
	 * @param url
	 * @return
	 */
	public Drawable getBitmapFromUrl(String url) {
		Drawable drawable = null;
		int timeout = 30 * 1000;
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, timeout);
		HttpConnectionParams.setSoTimeout(params, timeout);
		HttpClient client = new DefaultHttpClient(params);

		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;
		InputStream is = null;
		try {
			response = client.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}
			is = response.getEntity().getContent();
			String realpath = CacheInFileUtils.getCachePath(context, this.path);
			String name = CacheInFileUtils.cutUrlForName(url);
			// 缓存
			CacheInFileUtils.cacheInFile(realpath, name, is);
			// 读取图片
			drawable = CacheInFileUtils
					.getDrawableFromCacheFile(realpath, name);

		} catch (Exception e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			client.getConnectionManager().shutdown();
		}
		return drawable;
	}
}
