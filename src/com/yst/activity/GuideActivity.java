package com.yst.activity;

import com.jack.ui.GuideView1;
import com.jack.ui.GuideView2;
import com.jack.ui.GuideView3;
import com.yst.yiku.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Window;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
/**
 * 引导页面
 */
public class GuideActivity extends FragmentActivity {

	private ViewPager pager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);
		
		pager = (ViewPager) findViewById(R.id.guide_view_pager);
		ViewPagerAdapter adapter = new ViewPagerAdapter(this.getSupportFragmentManager());
		pager.setAdapter(adapter);
	}
	
	class ViewPagerAdapter extends FragmentPagerAdapter {
		public ViewPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			Fragment fragment = null ;
			if(arg0 == 0 ){
				fragment = new GuideView1(GuideActivity.this);
			}else if(arg0 == 1){
				fragment = new GuideView2(GuideActivity.this);
			}else if(arg0 == 2){
				fragment = new GuideView3(GuideActivity.this);
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}
	}
}