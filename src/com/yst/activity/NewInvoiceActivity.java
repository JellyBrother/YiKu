package com.yst.activity;

/**
 * 新添发票
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yst.yiku.R;

public class NewInvoiceActivity extends Activity implements OnClickListener {
	private ImageView yiliao_iv,// 医疗
			bangong_iv,// 办公
			shenghuo_iv,// 生活
			shipin_iv,// 食品
			add_fapiao_back_iv// 返回
			;
	private int CHECK_FAPIAN_MODES = 0;// 发票类型,1 医疗,2办公,3生活,4食品
	private String CHECK_FAPIAN_MODE_CONTENT = "";
	private EditText new_fapiao_et;// 新添加发票抬头
	private TextView add_fapiao_save; // 保存
	// 发票抬头
	private String fapiao_title = "";
	// 发票模式
	private int mode = 0;
	//是否是易店跳过来的
	private boolean IS_YIDIAN_ACTI = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fapiao);

		initView();

		Intent intent = getIntent();
		String fapiao_mode = intent.getStringExtra("fapiao_mode");
		if(intent.getStringExtra("activity_type") != null) {// 是易店跳过来的
			IS_YIDIAN_ACTI = true; 
		}
		Log.i("qcs", "fapiao_mode" + fapiao_mode);
		if(!fapiao_mode.equals("")) {
			mode = Integer.parseInt(fapiao_mode);
			if(mode == 1) {
				yiliao_iv.setImageResource(R.drawable.check_enable);
				bangong_iv.setImageResource(R.drawable.check_red_no);
				shenghuo_iv.setImageResource(R.drawable.check_red_no);
				shipin_iv.setImageResource(R.drawable.check_red_no);			
			}else if(mode == 2) {
				yiliao_iv.setImageResource(R.drawable.check_red_no);
				bangong_iv.setImageResource(R.drawable.check_enable);
				shenghuo_iv.setImageResource(R.drawable.check_red_no);
				shipin_iv.setImageResource(R.drawable.check_red_no);			
			}else if(mode == 3) {
				yiliao_iv.setImageResource(R.drawable.check_red_no);
				bangong_iv.setImageResource(R.drawable.check_red_no);
				shenghuo_iv.setImageResource(R.drawable.check_enable);
				shipin_iv.setImageResource(R.drawable.check_red_no);			
			}else if(mode == 4) {
				yiliao_iv.setImageResource(R.drawable.check_red_no);
				bangong_iv.setImageResource(R.drawable.check_red_no);
				shenghuo_iv.setImageResource(R.drawable.check_red_no);
				shipin_iv.setImageResource(R.drawable.check_enable);			
			}else {
				
			}
		}
		fapiao_title = intent.getStringExtra("fapiao_title");
		new_fapiao_et.setText(fapiao_title);
		
		
	}

	private void initView() {
		yiliao_iv = (ImageView) findViewById(R.id.yiliao_iv);
		bangong_iv = (ImageView) findViewById(R.id.bangong_iv);
		shenghuo_iv = (ImageView) findViewById(R.id.shenghuo_iv);
		shipin_iv = (ImageView) findViewById(R.id.shipin_iv);
		new_fapiao_et = (EditText) findViewById(R.id.new_fapiao_et);
		add_fapiao_back_iv = (ImageView) findViewById(R.id.add_fapiao_back_iv);
		add_fapiao_save = (TextView) findViewById(R.id.add_fapiao_save);

		add_fapiao_save.setOnClickListener(this);
		add_fapiao_back_iv.setOnClickListener(this);
		yiliao_iv.setOnClickListener(this);
		bangong_iv.setOnClickListener(this);
		shenghuo_iv.setOnClickListener(this);
		shipin_iv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.yiliao_iv:// 医疗
			yiliao_iv.setImageResource(R.drawable.check_enable);
			bangong_iv.setImageResource(R.drawable.check_red_no);
			shenghuo_iv.setImageResource(R.drawable.check_red_no);
			shipin_iv.setImageResource(R.drawable.check_red_no);
			CHECK_FAPIAN_MODES = 1;
			CHECK_FAPIAN_MODE_CONTENT = "医疗用品";
			break;
		case R.id.bangong_iv:// 办公
			yiliao_iv.setImageResource(R.drawable.check_red_no);
			bangong_iv.setImageResource(R.drawable.check_enable);
			shenghuo_iv.setImageResource(R.drawable.check_red_no);
			shipin_iv.setImageResource(R.drawable.check_red_no);
			CHECK_FAPIAN_MODES = 2;
			CHECK_FAPIAN_MODE_CONTENT = "办公用品";
			break;
		case R.id.shenghuo_iv:// 生活
			yiliao_iv.setImageResource(R.drawable.check_red_no);
			bangong_iv.setImageResource(R.drawable.check_red_no);
			shenghuo_iv.setImageResource(R.drawable.check_enable);
			shipin_iv.setImageResource(R.drawable.check_red_no);
			CHECK_FAPIAN_MODES = 3;
			CHECK_FAPIAN_MODE_CONTENT = "生活百货";
			break;
		case R.id.shipin_iv:// 食品
			yiliao_iv.setImageResource(R.drawable.check_red_no);
			bangong_iv.setImageResource(R.drawable.check_red_no);
			shenghuo_iv.setImageResource(R.drawable.check_red_no);
			shipin_iv.setImageResource(R.drawable.check_enable);
			CHECK_FAPIAN_MODES = 4;
			CHECK_FAPIAN_MODE_CONTENT = "食品餐饮";
			break;
		case R.id.add_fapiao_back_iv:// 回退
			finish();
			break;
		case R.id.add_fapiao_save:// 保存
			String title = new_fapiao_et.getText().toString();
			if (TextUtils.isEmpty(title)) {
				Toast.makeText(this, "还没有抬头!", Toast.LENGTH_SHORT).show();
				return;
			}else if(CHECK_FAPIAN_MODES == 0) {
				Toast.makeText(this, "请选择发票类型!", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("title", title);
			intent.putExtra("type", CHECK_FAPIAN_MODE_CONTENT);
			intent.putExtra("typemode", CHECK_FAPIAN_MODES + "");
			if(IS_YIDIAN_ACTI) {//是易店跳过来的
				setResult(5, intent);
			}else {
				setResult(1, intent);
			}
			finish();
			break;
		}
	}

}
