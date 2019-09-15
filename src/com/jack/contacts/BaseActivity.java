package com.jack.contacts;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

/**
 * @brief 页面抽象基类，可以在此页面进行一些公共的操作，大部分页面都应该从此类派生
 * 
 * 
 */

public abstract class BaseActivity extends FragmentActivity {
	protected final String TAG = getClass().getSimpleName();
	protected Context mContext;
	protected String REQUEST_RESULT = "request_result";
	protected String REQUEST_ERROR_CODE = "request_error_code";
	protected String REQUEST_ERROR_MSG = "request_error_msg";
	protected int LOAD_DATA_DELAY_TIME = 300;

	// protected ToastUtil mToast;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		// mToast = ToastUtil.getInstance(this);
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {

		super.finish();
	}

	/**
	 * @brief 执行网络请求方法
	 * 
	 * @param request
	 */
	// public void executeRequest(Request<?> request) {
	// RequestManager.addRequest(request, this);
	// }

	/**
	 * @brief 执行网络请求方法
	 * 
	 * @param request
	 */
	// public void executeRequest(Request<?> request, String tag) {
	// RequestManager.addRequest(request, tag);
	// }

	// protected Response.ErrorListener errorListener() {
	// return new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// Log.d("requestError", " " + error.getMessage());
	// }
	// };
	// }

	protected void startActivityBase(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		if (bundle != null)
			intent.putExtras(bundle);
		intent.setClass(this, cls);
		startActivity(intent);
	}

	protected void startActivityForResultBase(Class<?> cls, Bundle bundle,
			int requestCode) {
		Intent intent = new Intent();
		if (bundle != null)
			intent.putExtras(bundle);
		intent.setClass(this, cls);
		startActivityForResult(intent, requestCode);
	}

	protected void backForResultBase(int resultCode, Bundle bundle) {
		Intent intent = new Intent();
		if (bundle != null)
			intent.putExtras(bundle);
		setResult(resultCode, intent);
		finish();
	}

	protected void backToActivityBase(Class<?> cls, Bundle bundle) {
		Intent intent = new Intent();
		if (bundle != null)
			intent.putExtras(bundle);
		intent.setClass(this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}

	/**
	 * 隐藏软键�? *
	 */
	protected void hideSoftKeyBoard() {
		try {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(
					this.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
		}
	}

	protected void showSoftKeyBoard(final View v) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				InputMethodManager inputManager = (InputMethodManager) v
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(v, 0);
			}
		}, 500);
	}

	protected View.OnClickListener backClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			backEvent();
		}
	};

	protected void backEvent() {
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	/**
	 * @brief �?��公共的错误提�? *
	 * @param errorCode
	 * @param msg
	 */
	// protected void checkCommonError(int errorCode, Message msg) {
	// if (errorCode == BuptRequestErrorCode.VOLLY_ERROR_CODE) {
	// mToast.makeText(msg.getData().getString(REQUEST_ERROR_MSG));
	// } else {
	// String errorMsg = msg.getData().getString(REQUEST_ERROR_MSG);
	// if (!StringUtil.isEmpty(errorMsg)) {
	// mToast.makeText(errorMsg);
	// }
	// }
	// }
}
