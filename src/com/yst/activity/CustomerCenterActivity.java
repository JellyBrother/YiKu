package com.yst.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dandelionlvfengli.lib.L;
import com.guohua.common.util.Contacts;
import com.guohua.common.util.Customer;
import com.jack.contacts.AppContext;
import com.jack.json.JsonBanBenInformation;
import com.ljp.download.service.BanbenUtil;
import com.ljp.download.service.DownloadService;
import com.ljp.download.service.TipsDialog;
import com.ljp.download.service.UpdateVersionSM;
import com.yst.yiku.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.LayoutParams;

/**
 * 用户个人中心界面
 *
 */
public class CustomerCenterActivity extends Activity implements OnClickListener {

	// 界面退出和标题控件声明
	private ImageView activity_back_iv;
	private TextView activity_title_tv;
	// 退出按钮
	private ImageView customer_center_logout_iv;

	// 客服电话控件
//	private TextView service_phone_tv;

	// 用户的信息显示控件
	private RelativeLayout customer_center_info_layout;
	private TextView customer_center_phone_tv;

	// 用户的订单控件
	private TextView customer_center_orders_tv;
	// 用户的优惠券
	private TextView customer_center_coupons_tv;
	// 用户的地址管理
	private TextView customer_center_mgr_address_tv;
	// 用户的收藏
	private TextView customer_center_favorite_tv;
	// 软件更新控件
	private TextView customer_center_update_app_tv;
	private TextView customer_center_version_tv ;
	private LinearLayout customer_center_version_linear_layout;
	// 用户的推荐人
	private TextView customer_center_referee_tv, customer_center_referee1_tv;
	// 用户的钱包
	private TextView customer_center_wallet_tv, customer_center_wallet1_tv;
	private LinearLayout customer_vallet_linear_layout;
	// 修改登录密码
	private TextView customer_center_modify_login_passwd_tv;
	// 可能是修改支付密码界面
	// private TextView customer_center_modify_pay_passwd_tv ;

	/** 网络访问 */
	private RequestQueue requestQueue;

	private int m_count = 0;
	// 声明进度条对话框
	private ProgressDialog m_pDialog;

	// 登出时弹出取消对话框
	private PopupWindow mCancelPopWindow;
	private SharedPreferences msp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customer_center);

		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		getCustomerInfo();
	}

	private void initView() {
		requestQueue = Volley.newRequestQueue(this);
		msp = getSharedPreferences("yiku", MODE_PRIVATE);

//		service_phone_tv = (TextView) findViewById(R.id.customer_center_kefudianhua_tv);
//		service_phone_tv.setMovementMethod(LinkMovementMethod.getInstance());
		
		customer_center_version_tv = (TextView) findViewById(R.id.customer_center_version_tv);
		customer_center_version_tv.setOnClickListener(this);
		customer_center_version_linear_layout = (LinearLayout) findViewById(R.id.customer_center_version_linear_layout);
		customer_center_version_linear_layout.setOnClickListener(this);
		
		customer_center_version_tv.setText("版本号" + BanbenUtil.getVerName(CustomerCenterActivity.this));

		activity_back_iv = (ImageView) findViewById(R.id.activity_back_iv);
		activity_back_iv.setOnClickListener(this);
		activity_title_tv = (TextView) findViewById(R.id.activity_title_tv);
		activity_title_tv
				.setText(R.string.activity_customer_center_title_label);

		customer_center_logout_iv = (ImageView) findViewById(R.id.customer_center_logout_iv);
		customer_center_logout_iv.setOnClickListener(this);

		customer_center_phone_tv = (TextView) findViewById(R.id.customer_center_phone_tv);
		String user = getSharedPreferences("yiku", MODE_PRIVATE).getString(
				"user", "");

		customer_center_phone_tv.setText(user.substring(0, 3) + "****"
				+ user.substring(7, user.length()));
		customer_center_phone_tv.setOnClickListener(this);

		customer_center_info_layout = (RelativeLayout) findViewById(R.id.customer_center_info_layout);
		customer_center_orders_tv = (TextView) findViewById(R.id.customer_center_orders_tv);
		customer_center_coupons_tv = (TextView) findViewById(R.id.customer_center_coupons_tv);
		customer_center_mgr_address_tv = (TextView) findViewById(R.id.customer_center_mgr_address_tv);
		customer_center_favorite_tv = (TextView) findViewById(R.id.customer_center_favorite_tv);
		customer_center_update_app_tv = (TextView) findViewById(R.id.customer_center_update_app_tv);
		customer_center_referee_tv = (TextView) findViewById(R.id.customer_center_referee_tv);
		customer_center_wallet_tv = (TextView) findViewById(R.id.customer_center_wallet_tv);
		customer_center_wallet1_tv = (TextView) findViewById(R.id.customer_center_wallet1_tv);
		customer_vallet_linear_layout = (LinearLayout) findViewById(R.id.customer_vallet_linear_layout);
		customer_center_modify_login_passwd_tv = (TextView) findViewById(R.id.customer_center_modify_login_passwd_tv);
		// customer_center_modify_pay_passwd_tv = (TextView)
		// findViewById(R.id.customer_center_modify_pay_passwd_tv);

		customer_center_referee1_tv = (TextView) findViewById(R.id.customer_center_referee1_tv);
		String coupons = getSharedPreferences("yiku", MODE_PRIVATE).getString(
				"referee", "");
		Log.e("sss", "coupons is ==== " + coupons);
		if (TextUtils.isEmpty(coupons)) {
			customer_center_referee1_tv
					.setText(R.string.activity_customer_referee_label);
		} else {
			customer_center_referee1_tv.setText(coupons);
		}

		customer_center_wallet1_tv.setOnClickListener(this);
		customer_vallet_linear_layout.setOnClickListener(this);
		customer_center_info_layout.setOnClickListener(this);
		customer_center_orders_tv.setOnClickListener(this);
		customer_center_coupons_tv.setOnClickListener(this);
		customer_center_mgr_address_tv.setOnClickListener(this);
		customer_center_favorite_tv.setOnClickListener(this);
		customer_center_update_app_tv.setOnClickListener(this);
		customer_center_referee_tv.setOnClickListener(this);
		customer_center_wallet_tv.setOnClickListener(this);
		// customer_center_modify_pay_passwd_tv.setOnClickListener(this);
		customer_center_modify_login_passwd_tv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.customer_center_phone_tv:
		// startActivity(new Intent(this, BalanceActivity.class));
		// break;
		// 跳转用户的订单界面
		case R.id.customer_center_orders_tv:
			startActivity(new Intent(this, CustomerOrdersActivity.class));
			break;
		// 跳转到优惠券界面
		case R.id.customer_center_coupons_tv:
			startActivity(new Intent(this, CouponsActivity.class));
			break;
		// 跳转到地址管理界面
		case R.id.customer_center_mgr_address_tv:
			startActivity(new Intent(this, ShippingAddressMgrActivity.class));
			break;
		// 跳转到我的收藏界面
		case R.id.customer_center_favorite_tv:
			startActivity(new Intent(this, FavoriteActivity.class));
			break;
		// 检查版本更新
		case R.id.customer_center_version_tv:
		case R.id.customer_center_update_app_tv:
		case R.id.customer_center_version_linear_layout:
			checkUpdate();
			break;
		// 跳转到用户的推荐人界面
		case R.id.customer_center_referee_tv:
			// Toast.makeText(this, "这个可能是跳转到用户的推荐人界面",
			// Toast.LENGTH_SHORT).show();
			break;
		// 跳转到我的余额
		case R.id.customer_vallet_linear_layout:
		case R.id.customer_center_wallet_tv:
		case R.id.customer_center_wallet1_tv:
			startActivity(new Intent(this, BalanceActivity.class));
			break;
		// 跳转到修改登录密码
		case R.id.customer_center_modify_login_passwd_tv:
			startActivity(new Intent(this, ModifyPasswdActivity.class));
			break;
		// 跳转到支付密码
		// case R.id.customer_center_modify_pay_passwd_tv:
		// startActivity(new Intent(this, ModifyPasswdActivity.class));
		// break;
		// 返回按键
		case R.id.activity_back_iv:
			finish();
			break;
		// 退出登录对话框
		case R.id.customer_center_logout_iv:
			showCancelDialog();
			break;
		}
	}

	static String UPDATE_CHECK = "UPDATE_CHECK";
	/** 更新时用到的进度条等 */
	private ProgressBar progressBar;
	/** 显示进度的view */
	private TextView progressText;
	/** 进度数 */
	public static int loadingProgress;
	/** 版本更新显示界面 */
	private TipsDialog tipsDialog;
	/** 版本更新dialog宽度 */
	private int width;

	void checkUpdate() {
		loadingProgress = 0;
		// 这是获取显示宽度的地方，没有这个是不会显示更新dialog宽度的
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;

		Log.d("update", "begin to check if need update");
		this.banbengengxin();
	}

	private String xianzaibanben;
	private String gengxinbanben;
	private File file;
	private UpdateVersionSM itemSm;
	final String IMGURL = "http://182.254.161.94:8080/ydg/";
	private static List<Map<String, Object>> list_banbenInfo;

	public void banbengengxin() {
		String urlString = "http://182.254.161.94:8080/ydg/version!getNewApp?platform=Android&app_name=%E6%98%93%E8%AE%A2%E8%B4%AD&client_type=A&version=1";
		StringRequest stringRequest5 = new StringRequest(urlString,
				new Listener<String>() {

					@Override
					public void onResponse(String arg0) {
						if (!arg0.equalsIgnoreCase("[]") && arg0 != null) {
							if (arg0.contains("SUCCESS")) {
								list_banbenInfo = JsonBanBenInformation
										.JsonToList(arg0);
								initDataBanBen();
							} else {
								Toast.makeText(getApplicationContext(), "已经是最新版本", Toast.LENGTH_SHORT).show();
							}
						}
					}

				}, new ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError arg0) {
						Toast.makeText(CustomerCenterActivity.this,
								"数据加载异常...", Toast.LENGTH_SHORT).show();
					}
				});
		requestQueue.add(stringRequest5);
	}

	private void initDataBanBen() {
		xianzaibanben = BanbenUtil.getVerName(CustomerCenterActivity.this);
		gengxinbanben = list_banbenInfo.get(0).get("app_version").toString();
		AppContext.lujing = list_banbenInfo.get(0).get("url").toString();
		String[] appName = list_banbenInfo.get(0).get("url").toString()
				.split("/");
		String filePath = Environment.getExternalStorageDirectory().getPath()
				+ "/" + "NightMan.apk";
		file = new File(filePath);
		boolean needUpdate = false;
		try {
			System.out.println("----------------3---------------");
			float currentVer = Float.parseFloat(xianzaibanben);
			float serverVer = Float.parseFloat(gengxinbanben);
			System.out.println("current ver is " + currentVer);
			System.out.println("server  ver is " + serverVer);
			if (currentVer < serverVer) {
				System.out.println("need update");
				needUpdate = true;
			} else {
				Toast.makeText(getApplicationContext(), "已经是最新版本", Toast.LENGTH_SHORT).show();
				System.out.println("no need update");
				needUpdate = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (needUpdate) {
			if (file != null && file.exists()) {
				Log.d("update", "delete exist file");
				file.delete();
			}
			if (!L.file.exists(filePath)) {
				BanbenUtil.showSelectDialog(CustomerCenterActivity.this,
						"新版本更新内容:\n"
								+ list_banbenInfo.get(0).get("des").toString()
								+ "\n是否更新？",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								new Thread() {
									public void run() {
										Message msg = BroadcastHandler
												.obtainMessage();
										BroadcastHandler.sendMessage(msg);
										// 线程启动下载任务，通过handler传递消息
									}
								}.start();
							}
						}, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								BanbenUtil.alertDialog.dismiss();
							}
						});

			} else {
				System.out.println("已经有apk");
				if(file.exists()){
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(), "已经是最新版本", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/** 版本更新用到的handler */
	private Handler BroadcastHandler = new Handler() {
		public void handleMessage(Message msg) {
			newVerSen();
		}
	};

	/**
	 * 更新版本设置显示dialog
	 */
	private void newVerSen() {
		Button midConfirm = null, midCancle = null;

		setDialog("下载进度提示", Gravity.CENTER, midConfirm, midCancle,
				(width / 6) * 5, R.layout.dialog_tips_mid, R.id.tvId, 0);
	}

	/**
	 * 显示更新版本的DIAlog
	 */
	private void setDialog(String message, int grivate, Button btnConfirm,
			Button btnCancle, int width, int layout, int txtId, int anim) {
		tipsDialog = TipsDialog.creatTipsDialog(this, width, layout, grivate,
				anim);
		tipsDialog.setCanceledOnTouchOutside(false);

		progressBar = (ProgressBar) tipsDialog.findViewById(R.id.down_pb);
		progressText = (TextView) tipsDialog.findViewById(txtId);
		btnCancle = (Button) tipsDialog.findViewById(R.id.btnCancle);
		btnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CustomerCenterActivity.this,
						DownloadService.class);
				// 由intent启动service，后台运行下载进程，在服务中调用notifycation状态栏显示进度条
				startService(intent);
				tipsDialog.dismiss();
			}
		});
		tipsDialog.show();
		new Thread() {
			public void run() {
				loadFile(IMGURL + AppContext.lujing);
			}
		}.start();
	}

	// 从IP地址下载文件到本地
	public void loadFile(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);

			HttpEntity entity = response.getEntity();
			float length = entity.getContentLength();

			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				File file = new File(Environment.getExternalStorageDirectory()
						.getPath(), "NightMan.apk");
				fileOutputStream = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int ch = -1;
				float count = 0;
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					count += ch;
					sendMsg(1, (int) (count * 100 / length));
				}
			}
			sendMsg(2, 0);
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (Exception e) {
			sendMsg(-1, 0);
		}
	}

	private void sendMsg(int flag, int c) {
		Message msg = new Message();
		msg.what = flag;
		msg.arg1 = c;
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case 1:
					progressBar.setProgress(msg.arg1);
					loadingProgress = msg.arg1;
					progressText.setText("已为您加载了：" + loadingProgress + "%");
					if (loadingProgress > 99)
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								tipsDialog.dismiss();
							}
						}, 1000);
					break;
				case 2:
					// 下载后打开文件
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file),
							"application/vnd.android.package-archive");
					startActivity(intent);
					break;
				case -1:
					String error = msg.getData().getString("error");
					Toast.makeText(CustomerCenterActivity.this, error,
							Toast.LENGTH_LONG).show();
					break;
				}
			}
			super.handleMessage(msg);
		}
	};

	// 退出提示框
	private void showCancelDialog() {
		final View mCancelView = getLayoutInflater().inflate(
				R.layout.activity_logout_alert_view, null);
		mCancelPopWindow = new PopupWindow(mCancelView,
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
		TextView mCancelTv = (TextView) mCancelView
				.findViewById(R.id.activity_logout_cancel_tv);
		TextView mLogoutTv = (TextView) mCancelView
				.findViewById(R.id.activity_logout_tv);
		mCancelTv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mCancelPopWindow != null && mCancelPopWindow.isShowing()) {
					mCancelPopWindow.dismiss();
				}
			}
		});
		// 退出确定按钮
		mLogoutTv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mCancelPopWindow != null && mCancelPopWindow.isShowing()) {
					mCancelPopWindow.dismiss();
				}
				Customer.getInstance(CustomerCenterActivity.this).setLogout();
				MainActivity.isLogin = false;
				startActivity(new Intent(CustomerCenterActivity.this,
						LoginActivity.class));
				CustomerCenterActivity.this.finish();
				// }
			}
		});
		mCancelPopWindow.setAnimationStyle(R.style.AnimTop2);
		mCancelPopWindow.update();
		mCancelPopWindow.setTouchable(true); // 设置可点击
		mCancelPopWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
		mCancelPopWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED); // 如果有edittext，阻挡输入法遮挡
		mCancelPopWindow.showAtLocation(
				findViewById(R.id.customer_center_layout_view), Gravity.CENTER,
				0, 0);
		mCancelPopWindow.setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					mCancelPopWindow.dismiss();
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 获取用户的余额接口
	 */
	private void getCustomerInfo() {
		JsonObjectRequest jsr = new JsonObjectRequest(
				Contacts.URL_GET_CUSTOMER_INFO + "customer_id="
						+ Customer.customer_id + Contacts.URL_ENDING, null,
				new Listener<JSONObject>() {
					public void onResponse(JSONObject response) {
						try {
							if ("SUCCESS".equals(response.getString("result"))) {
								if (response.getJSONObject("data").has("fee")) {

									String content = "<font color='#000000'>"
											+ "余额"
											+ (response.getJSONObject("data")
													.getDouble("fee") / 100)
											+ "元" + "</font>";

									customer_center_wallet1_tv.setText(Html
											.fromHtml(content));
								}

								if (response.getJSONObject("data").has(
										"address_id")) {
									msp.edit()
											.putString(
													"address_id",
													response.getJSONObject(
															"data").getString(
															"address_id"))
											.commit();
									Customer.address_id = response
											.getJSONObject("data").getString(
													"address_id");
								} else {
									msp.edit().putString("address_id", "")
											.commit();
									Customer.address_id = "";
								}

							} else {
								customer_center_wallet1_tv.setText("余额0元");
							}
						} catch (JSONException e) {
							e.printStackTrace();
							customer_center_wallet1_tv.setText("");
							Toast.makeText(getApplicationContext(), "余额数据解析异常",
									Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getApplicationContext(), "网络访问错误，请重试",
								Toast.LENGTH_SHORT).show();
						customer_center_wallet1_tv.setText("");
					}
				});
		requestQueue.add(jsr);
	}
}
