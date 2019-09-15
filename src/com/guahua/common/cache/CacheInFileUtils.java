package com.guahua.common.cache;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class CacheInFileUtils {

	public static boolean cacheInFile(String cachePath, String cacheName,
			InputStream inputStream) {
		File path = new File(cachePath);
		if (!path.exists()) {
			path.mkdirs();
		}
		File file = new File(cachePath + "/" + cacheName);
		if (file.exists()) {
			file.delete();
		}
		try {
			BufferedOutputStream bop = new BufferedOutputStream(
					new FileOutputStream(file));
			byte[] datas = new byte[1024];
			int length = -1;
			while ((length = inputStream.read(datas)) != -1) {
				bop.write(datas, 0, length);
			}
			bop.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getCachePath(Context context, String path) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File sdFile = Environment.getExternalStorageDirectory();
			return sdFile.getAbsolutePath() + "/"
					+ context.getApplicationInfo().packageName;
		} else {
			File file = context.getFilesDir();
			return file.getAbsolutePath();
		}

	}

	public static String cutUrlForName(String url) {
		try {
			String name1 = url.replaceAll("http://", "");
			String name2 = name1.replaceAll("/", "_");
			String name3 = name2.replaceAll(".", "-");

			name2 = String.format("%d", url.hashCode());
			return name2;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

	}

	public static Drawable getDrawableFromCacheFile(String path, String name) {
		File file = new File(path + "/" + name);
		if (!file.exists()) {
			return null;
		} else {
			FileInputStream is = null;
			try {
				is = new FileInputStream(file);
				Drawable drawable = Drawable.createFromStream(is, null);
				// drawable = zoomDrawable(drawable, 500, 450);
				return drawable;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static Drawable getDrawableFromCacheFile(String path, String name,
			int height, int width) {
		File file = new File(path + "/" + name);
		if (!file.exists()) {
			return null;
		} else {
			FileInputStream is = null;
			try {
				is = new FileInputStream(file);
				Drawable drawable = Drawable.createFromStream(is, null);
				// drawable = zoomDrawable(drawable, height, width);
				return drawable;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth(); // 取 drawable 的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_4444
				: Bitmap.Config.RGB_565; // 取 drawable 的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap); // 建立对应 bitmap 的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // 把 drawable 内容画到画布中
		return bitmap;
	}

	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable); // drawable 转换成 bitmap
		Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
		float scaleWidth = ((float) w / width); // 计算缩放比例
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true); // 建立新的 bitmap ，其内容是对原 bitmap 的缩放后的图
		return new BitmapDrawable(newbmp); // 把 bitmap 转换成 drawable 并返回
	}
}
