package com.jack.ui;

import com.yst.yiku.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GuideView2 extends Fragment {

	private Activity context ;
	
	public GuideView2(Activity context){
		this.context = context ;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null ;
		view = View.inflate(context,R.layout.guide_view2_layout, null);
		return view;
	}
}
