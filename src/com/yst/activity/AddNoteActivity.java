package com.yst.activity;
/**
 * 添加备注
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yst.yiku.R;

public class AddNoteActivity extends Activity implements OnClickListener{
	
	private EditText note_et;//编辑框
	private Button note_submit_btn;//提交备注
	private ImageView merchant_add_note_back_iv;//返回
	private String order_comm = "";
	//是否是易店跳过来的
	private boolean IS_YIDIAN_ACTI = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_note_activity);
		
		initView();
		Intent intent = getIntent();
		String content = intent.getStringExtra("order_comm");
		if(content != null) {
			order_comm = content;
			note_et.setText(order_comm);
		}
		if(intent.getStringExtra("activity_type") != null) {// 是易店跳过来的
			IS_YIDIAN_ACTI = true; 
		}
	}
	/**
	 * 初始化空间
	 */
	private void initView() {
		note_et = (EditText) findViewById(R.id.note_et);
		note_submit_btn = (Button) findViewById(R.id.note_submit_btn);
		merchant_add_note_back_iv = (ImageView) findViewById(R.id.merchant_add_note_back_iv);
		
		merchant_add_note_back_iv.setOnClickListener(this);
		note_submit_btn.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.note_submit_btn://提交备注
			String note = note_et.getText().toString().trim();
			if(TextUtils.isEmpty(note)) {
				Toast.makeText(this, "还没填写呢~~", Toast.LENGTH_SHORT).show();
				return;
			}else{
				Intent intent = new Intent();
				intent.putExtra("note", note);
				if(IS_YIDIAN_ACTI) {//是易店跳过来的
					setResult(3, intent);  
				}else {
					setResult(-1, intent);  
				}
				finish();
			}
			break;
		case R.id.merchant_add_note_back_iv://回退
			finish();
			break;
		}
	}

}
