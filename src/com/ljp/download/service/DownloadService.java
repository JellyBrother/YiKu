package com.ljp.download.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import com.yst.activity.MainActivity;
import com.yst.yiku.R;

public class DownloadService extends Service {
	private NotificationManager notificationMrg;
	// 状态栏实例对象
	private int old_process = 0;
	private boolean isFirstStart = false;

	public void onCreate() {
		super.onCreate();
		isFirstStart = true;
		notificationMrg = (NotificationManager) this
				.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// 获得系统后台运行的NotificationManager服务
		System.out.println(MainActivity.loadingProgress + "==");
		mHandler.handleMessage(new Message());

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 1为出现，2为隐藏
			if (MainActivity.loadingProgress > 99) {
				notificationMrg.cancel(0);
				// 下载完成后状态栏取消
				stopSelf();
				// 服务终止
				return;
			}
			if (MainActivity.loadingProgress > old_process) {
				displayNotificationMessage(MainActivity.loadingProgress);
				// 定义具体的标题栏视图显示
			}

			// new Thread() {
			// public void run() {
			isFirstStart = false;
			Message msg1 = mHandler.obtainMessage();
			mHandler.sendMessage(msg1);
			// mHandler递归自调用线程不断传递消息，更新进度条
			// }
			// }.start();
			old_process = MainActivity.loadingProgress;
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// 状态栏具体视图显示要求设置
	private void displayNotificationMessage(int count) {

		// Notification的Intent，即点击后转向的Activity
		Intent notificationIntent1 = new Intent(this, this.getClass());
		notificationIntent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// addflag设置跳转类型
		PendingIntent contentIntent1 = PendingIntent.getActivity(this, 0,
				notificationIntent1, 0);

		// 创建Notifcation对象，设置图标，提示文字
		Notification notification = new Notification(R.drawable.ic_launcher,
				"DnwoLoadManager", System.currentTimeMillis());// 设定Notification出现时的声音，一般不建议自定义
		if (isFirstStart || MainActivity.loadingProgress > 97) {
			notification.defaults |= Notification.DEFAULT_SOUND;// 设定是否振动
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		// 创建一个自定义的Notification，可以使用RemoteViews
		// 要定义自己的扩展消息，首先要初始化一个RemoteViews对象，然后将它传递给Notification的contentView字段，再把PendingIntent传递给contentIntent字段
		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notification_version);
		contentView.setTextViewText(R.id.n_title, "下载提示");
		contentView.setTextViewText(R.id.n_text, "当前进度：" + count + "% ");
		contentView.setProgressBar(R.id.n_progress, 100, count, false);

		notification.contentView = contentView;
		notification.contentIntent = contentIntent1;

		notificationMrg.notify(0, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
