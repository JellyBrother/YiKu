package com.jack.ui;

import android.app.Activity;
import android.view.View;

public abstract class BasePager{
	public Activity context;
	public View view;
	
	public BasePager(Activity context) {
		this.context = context;
		view = initView();
	}

	public abstract View initView();
	
	//返回当前界面的方法
	public View getView(){
		return view;
	}
	
}
