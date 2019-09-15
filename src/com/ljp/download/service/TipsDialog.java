package com.ljp.download.service;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.TextView;

import com.yst.yiku.R;

public class TipsDialog extends Dialog {
	private Context context = null;
	private static LayoutInflater inflater;
	private static TipsDialog tipsDialog = null;

	public TipsDialog(Context context) {
		super(context);
		this.context = context;
	}

	public TipsDialog(Context context, int theme) {
		super(context, theme);
		this.inflater = LayoutInflater.from(context);
	}

	/**
	 * 
	 * @param context
	 *            activity上下�?
	 * @param message
	 *            提示内容
	 * @param mwidth
	 *            dialog宽度
	 * @param layout
	 *            dialog布局文件
	 * @return
	 */
	public static TipsDialog creatTipsDialog(Context context, String message,
			int mwidth, int layout) {
		tipsDialog = new TipsDialog(context, R.style.tipsDialog_style);
		tipsDialog.setContentView(layout);
		Window w = tipsDialog.getWindow();
		w.setWindowAnimations(R.style.tipsAnimation); // 添加动画
		w.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位�?
		LayoutParams lay = tipsDialog.getWindow().getAttributes();// dialog显示的宽�?
		lay.width = mwidth;
		TextView tvText = (TextView) tipsDialog.findViewById(R.id.tvText);
		tvText.setText(message);
		return tipsDialog;
	}

	/**
	 * 
	 * @param context
	 *            activity上下�?
	 * @param message
	 *            提示内容
	 * @param mwidth
	 *            dialog宽度
	 * @param layout
	 *            dialog布局文件
	 * @param grivate
	 *            dialog位子
	 * @return
	 */
	public static TipsDialog creatTipsDialog(Context context, int mwidth,
			int layout, int grivate, int anim) {
		tipsDialog = new TipsDialog(context, R.style.tipsDialog_style);
		tipsDialog.setContentView(layout);
		Window w = tipsDialog.getWindow();
		w.setWindowAnimations(anim); // 添加动画
		w.setGravity(grivate); // 此处可以设置dialog显示的位�?
		LayoutParams lay = tipsDialog.getWindow().getAttributes();// dialog显示的宽�?
		lay.width = mwidth;
		return tipsDialog;
	}

	public void dialogDismiss() {
		tipsDialog.dismiss();
	}

	/**
	 * 
	 * @param context
	 *            activity上下�?
	 * @param mwidth
	 *            dialog宽度
	 * @param layout
	 *            dialog布局文件
	 * @return
	 */
	public static TipsDialog creatTipsDialog(Context context, int mwidth,
			int layout) {
		tipsDialog = new TipsDialog(context, R.style.tipsDialog_style);
		tipsDialog.setContentView(layout);
		Window w = tipsDialog.getWindow();
		w.setWindowAnimations(R.style.tipsAnimation); // 添加动画
		w.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位�?
		LayoutParams lay = tipsDialog.getWindow().getAttributes();// dialog显示的宽�?
		lay.width = mwidth;
		return tipsDialog;
	}
}
