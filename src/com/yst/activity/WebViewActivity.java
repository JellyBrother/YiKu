package com.yst.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yst.yiku.R;

public class WebViewActivity extends Activity {

	WebView webView;
	
	//用来展示商品详情的头标题
	private RelativeLayout web_view_title_layout;
	private ImageView web_view_back_iv ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.web_view);
		if(getIntent().getIntExtra("store_good_detail", 0) == 1) {
			initTitle();
		}
		webView = (WebView) findViewById(R.id.webView);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setWebViewClient(new WebViewClient());
		String url = this.getIntent().getStringExtra("url");
		webView.loadUrl(url);
	}

	//初始化标题栏
	private void initTitle() {
		web_view_title_layout = (RelativeLayout) findViewById(R.id.web_view_title_layout);
		web_view_title_layout.setVisibility(View.VISIBLE);
		web_view_back_iv = (ImageView) findViewById(R.id.web_view_back_iv);
		web_view_back_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WebViewActivity.this.finish();
			}
		});
	}
}
