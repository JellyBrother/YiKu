package com.jack.headpicselect;

import java.util.HashMap;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

/**
 * @brief avoid recreate the same toast within 5s
 *
 * Detailed description starts here. 
 * 
 */
public class ToastUtil {

	private HashMap<Object, Long> map = new HashMap<Object, Long>();

	private static ToastUtil toast;

	Context context;

	private static final long INTERVAL = 2000;

	private ToastUtil(Context context) {
		this.context = context;
	}

	public static ToastUtil getInstance(Context context) {
		if (toast == null) {
			toast = new ToastUtil(context);
		}
		return toast;
	}

	/**
	 * Show toast{@link Toast#LENGTH_SHORT}
	 * 
	 * @param res
	 *            �?ext resourceId
	 * @param
	 */
	public void makeText(int res) {
		makeText(res, Toast.LENGTH_SHORT);
	}

	/**
	 * Show toast{@link Toast#LENGTH_SHORT}
	 * 
	 * @param string
	 *            been show�?
	 * @param
	 */
	public void makeText(String str) {
		makeText(str, Toast.LENGTH_SHORT);

	}
	
	public void makeView(View view) {
		makeView(view, Toast.LENGTH_SHORT);

	}

	/**
	 * Show toast with custom type
	 * 
	 * @param str
	 * @param type
	 */
	public void makeText(String str, int type) {
		if (map.get(str) == null || System.currentTimeMillis() - map.get(str) > INTERVAL) {
			Toast toast = Toast.makeText(context, str, type);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			map.put(str, System.currentTimeMillis());
		}
	}

	public void makeText(int res, int type) {
		if (map.get(res) == null || System.currentTimeMillis() - map.get(res) > INTERVAL) {
			Toast toast = Toast.makeText(context, res, type);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			map.put(res, System.currentTimeMillis());
		}
	}
	
	public void makeView(View view, int type)
	{
		int id = view.getId();
		if (map.get(id) == null || System.currentTimeMillis() - map.get(id) > INTERVAL) {
			Toast viewToast = new Toast(context);
			viewToast.setView(view);
			viewToast.setDuration(Toast.LENGTH_SHORT);
			viewToast.setGravity(Gravity.CENTER, 0, 0);
			viewToast.show();
			map.put(id, System.currentTimeMillis());
		}
	}

}
