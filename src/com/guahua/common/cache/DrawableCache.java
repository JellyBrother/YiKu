/**
 * 
 */
package com.guahua.common.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class DrawableCache {

	private static ConcurrentHashMap<String, SoftReference<Drawable>> imageCache = new ConcurrentHashMap<String, SoftReference<Drawable>>();
	private static ConcurrentHashMap<String, DrawableDownloadTask> taskCache = new ConcurrentHashMap<String, DrawableDownloadTask>();
	private static ConcurrentHashMap<String, DrawableDownloadCacheTask> newTaskCache = new ConcurrentHashMap<String, DrawableDownloadCacheTask>();

	public static Drawable getDrawable(String url,
			final DrawableDownloadCallback callback) {
		if (imageCache == null) {
			imageCache = new ConcurrentHashMap<String, SoftReference<Drawable>>();
		}
		SoftReference<Drawable> ref = imageCache.get(url);
		if (ref != null) {
			Drawable drawable = ref.get();
			if (drawable != null) {
				return drawable;
			}
		}
		if (callback == null)
			return null;
		if (taskCache == null) {
			taskCache = new ConcurrentHashMap<String, DrawableDownloadTask>();
		}
		DrawableDownloadTask task = taskCache.get(url);
		if (task != null) {
			return null;
		}
		task = new DrawableDownloadTask() {

			@Override
			protected void onPostExecute(Drawable result) {
				if (isCancelled())
					return;
				if (result != null) {
					if (taskCache == null) {
						taskCache = new ConcurrentHashMap<String, DrawableDownloadTask>();
					}
					taskCache.remove(this.url);
					callback.onLoad(result);
					SoftReference<Drawable> ref = new SoftReference<Drawable>(
							result);
					if (imageCache == null) {
						imageCache = new ConcurrentHashMap<String, SoftReference<Drawable>>();
					}
					imageCache.put(this.url, ref);
				} else {
					callback.onLoadFail();
				}
			}
		};
		taskCache.put(url, task);
		task.execute(url);
		return null;
	}

	public static void clearCache() {
		if (imageCache == null) {
			imageCache = new ConcurrentHashMap<String, SoftReference<Drawable>>();
		}
		imageCache.clear();

		if (taskCache == null) {
			taskCache = new ConcurrentHashMap<String, DrawableDownloadTask>();
		}
		taskCache.clear();
	}

	public static Drawable getDrawableNew(String url, Context context,
			String path, final DrawableDownloadCacheListener callback) {
		String realpath = CacheInFileUtils.getCachePath(context, path);
		String name = CacheInFileUtils.cutUrlForName(url);
		Drawable drawable = CacheInFileUtils.getDrawableFromCacheFile(realpath,
				name);
		if (drawable != null) {
			return drawable;
		}

		if (callback == null)
			return null;

		if (newTaskCache == null) {
			newTaskCache = new ConcurrentHashMap<String, DrawableDownloadCacheTask>();
		}

		DrawableDownloadCacheTask task = newTaskCache.get(url);
		if (task != null) {
			return null;
		}

		task = new DrawableDownloadCacheTask(context, callback) {

			@Override
			protected void onPostExecute(Drawable result) {
				if (result != null) {
					if (newTaskCache == null) {
						newTaskCache = new ConcurrentHashMap<String, DrawableDownloadCacheTask>();
					}
					newTaskCache.remove(this.url);
					callback.returnDrawable(result, this.url);
				}
			}
		};
		newTaskCache.put(url, task);
		task.execute(url, path);
		return null;
	}

	public static Drawable getDrawableNew(String url, Context context,
			String path, final DrawableDownloadCacheListener callback,
			int height, int width) {
		String realpath = CacheInFileUtils.getCachePath(context, path);
		String name = CacheInFileUtils.cutUrlForName(url);
		Drawable drawable = CacheInFileUtils.getDrawableFromCacheFile(realpath,
				name, height, width);
		if (drawable != null) {
			return drawable;
		}

		if (callback == null)
			return null;

		if (newTaskCache == null) {
			newTaskCache = new ConcurrentHashMap<String, DrawableDownloadCacheTask>();
		}

		DrawableDownloadCacheTask task = newTaskCache.get(url);
		if (task != null) {
			return null;
		}

		task = new DrawableDownloadCacheTask(context, callback) {

			@Override
			protected void onPostExecute(Drawable result) {
				if (result != null) {
					if (newTaskCache == null) {
						newTaskCache = new ConcurrentHashMap<String, DrawableDownloadCacheTask>();
					}
					newTaskCache.remove(this.url);
					callback.returnDrawable(result, this.url);
				}
			}
		};
		newTaskCache.put(url, task);
		task.execute(url, path);
		return null;
	}
}
