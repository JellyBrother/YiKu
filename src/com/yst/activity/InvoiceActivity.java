package com.yst.activity;
/**
 * 选择发票
 */
import com.yst.yiku.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class InvoiceActivity extends Activity implements OnClickListener{
	
	private LinearLayout new_fapiao_linearlayout;//添加新的发票
	private ImageView no_fapiao_iv,//不开发票
					  self_fapiao_iv,//个人
					  fapiao_activ_back_iv//回退
	;
	private int CHECK_FAPIAO_MODE = 1;//发票,1 不开,2个人
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fapiao);

		initView();
	}

	private void initView() {
		new_fapiao_linearlayout = (LinearLayout) findViewById(R.id.new_fapiao_linearlayout);
		no_fapiao_iv = (ImageView) findViewById(R.id.no_fapiao_iv);
		self_fapiao_iv = (ImageView) findViewById(R.id.self_fapiao_iv);
		fapiao_activ_back_iv = (ImageView) findViewById(R.id.fapiao_activ_back_iv);
		
		fapiao_activ_back_iv.setOnClickListener(this);
		new_fapiao_linearlayout.setOnClickListener(this);
		no_fapiao_iv.setOnClickListener(this);
		self_fapiao_iv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_fapiao_linearlayout://跳转
			startActivity(new Intent(this, NewInvoiceActivity.class));
			break;
		case R.id.no_fapiao_iv://不开发票
			CHECK_FAPIAO_MODE = 1;
			no_fapiao_iv.setImageResource(R.drawable.check_enable);
			self_fapiao_iv.setImageResource(R.drawable.check_red_no);
			break;
		case R.id.self_fapiao_iv://添加发票
			CHECK_FAPIAO_MODE = 2;
			self_fapiao_iv.setImageResource(R.drawable.check_enable);
			no_fapiao_iv.setImageResource(R.drawable.check_red_no);
			break;
		case R.id.fapiao_activ_back_iv://回退
			finish();
			break;
		}
	}

}
