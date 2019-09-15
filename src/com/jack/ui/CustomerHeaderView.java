package com.jack.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yst.yiku.R;

public class CustomerHeaderView extends FrameLayout {

	public CustomerHeaderView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public HeaderListener headerListener;

	public static interface HeaderListener {
		public void onLeftClick();

		public void onRightClick();
	}

	public CustomerHeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public CustomerHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	ImageView buttonLeft;
	ImageView buttonRight;

	private void init() {
		LayoutInflater.from(this.getContext()).inflate(
				R.layout.inner_layout_head, this);
		buttonLeft = (ImageView) this.findViewById(R.id.btn_return);
		buttonRight = (ImageView) this.findViewById(R.id.btn_main);

		buttonLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (headerListener != null) {
					headerListener.onLeftClick();
				}
			}
		});

		buttonRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (headerListener != null) {
					headerListener.onRightClick();
				}
			}
		});
	}

	public void disableLeftButton() {
		buttonLeft.setVisibility(View.GONE);
	}

	public void disableRightButton() {
		buttonRight.setVisibility(View.GONE);
	}

	public void setHeaderListener(HeaderListener headerListener) {
		this.headerListener = headerListener;
	}

	public void setLeftButtonDrawable(Drawable drawable) {
		buttonLeft.setBackgroundDrawable(drawable);
	}

	public void setRightButtonDrawable(Drawable drawable) {
		buttonRight.setBackgroundDrawable(drawable);
	}

	public void setTitle(String name) {
		((TextView) this.findViewById(R.id.head_title)).setText(name);
	}

	public String getTitle() {
		return ((TextView) this.findViewById(R.id.head_title)).getText()
				.toString().trim();
	}
}
