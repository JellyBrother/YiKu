package com.jack.ui;

import com.yst.activity.MainActivity;
import com.yst.yiku.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class GuideView3 extends Fragment {

	private Activity context;

	public GuideView3(Activity context) {
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;
		view = View.inflate(context, R.layout.guide_view3_layout, null);
		Button btn = (Button) view.findViewById(R.id.guide_flag_iv);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setIsFirst();
				Intent it = new Intent(context, MainActivity.class);
				startActivity(it);
				context.finish();
			}
		});
		return view;
	}

	private void setIsFirst() {
		SharedPreferences sp = context.getSharedPreferences("yiku",
				Context.MODE_PRIVATE);
		sp.edit().putBoolean("IsFirstRun", true).commit();
	}
}
