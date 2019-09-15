package com.jack.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jack.ui.PickerView.onSelectListener;
import com.yst.yiku.R;

@SuppressLint("SimpleDateFormat") 
public class PickerDialog {

	private String[] data3;
	private AlertDialog dialog;
	private TextView cancle;
	private TextView confirm;
	private PickerView pickerView;

	public PickerDialog(Context context,int mode) {

		dialog = new AlertDialog.Builder(context).create();
		dialog.show();
		// 得到窗口
		Window window = dialog.getWindow();
		// 设置动画
		window.setWindowAnimations(R.style.AnimBottom);
		// 窗体管理者
		WindowManager manager = dialog.getWindow().getWindowManager();
		// 可以获取窗体宽高的工具
		Display display = manager.getDefaultDisplay();
		// 获取对话框的宽高参数
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		// 设置对话框宽为窗体宽
		params.width = display.getWidth();
		// 为dialog设置属性
		dialog.getWindow().setAttributes(params);
		// 对话从窗体底部弹出
		window.setGravity(Gravity.BOTTOM); 
		// 设置窗体对话框布局
		window.setContentView(R.layout.check_time_dialog);
		cancle = (TextView) window.findViewById(R.id.cancle);
		confirm = (TextView) window.findViewById(R.id.confirm);
		pickerView = (PickerView) window.findViewById(R.id.pickerView);
		
		ArrayList<String> list = new ArrayList<String>();
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH-mm");
		String time = format.format(date);
		int hours = Integer.parseInt((time.split("-"))[0]);
		int minues = Integer.parseInt((time.split("-"))[1]);
		/*if( minues == 0) {
			minues = 30;
		}else if(minues <= 30) {
			minues = 00;
			hours += 1;
		}else {
			minues = 30;
			hours += 1;
		}
		int hoursTwo = hours;
		int minuesTwo = minues;
		if(minues == 00) {
			data3 = new String[(20 - hours) * 2 + 1];
			for(int m = 0;m < (20 - hours) * 2 + 1;m ++){
				if(m == 0) {
					if(mode == 1) {
						data3[m] = "立即送出(大约" + hoursTwo + ":" + minuesTwo + "0送达)";
					}else {
						data3[m] = "大约" + hoursTwo + ":" + minuesTwo + "0自取";
					}
				}else {
					minuesTwo += 30;
					if(minuesTwo == 30) {
						data3[m] = hoursTwo + ":" + minuesTwo;
					}else if(minuesTwo == 60) {
						minuesTwo = 0;
						hoursTwo += 1;
						data3[m] = hoursTwo + ":" + minuesTwo + "0";
					}
				}
			}
			for (int i = 0; i < data3.length; i++) {
				Log.i("qcs", "时间数组====" + i + "--" + data3[i]);
				list.add(data3[i]);
			}
		}else if(minues == 30) {
			data3 = new String[(20 - hours) * 2];
			for(int m = 0;m < (20 - hours) * 2 ;m ++){
				if(m == 0) {
					if(mode == 1) {
						data3[m] = "立即送出(大约" + hoursTwo + ":" + minuesTwo + "送达)";
					}else {
						data3[m] = "大约" + hoursTwo + ":" + minuesTwo + "自取";
					}
				}else {
					minuesTwo += 30;
					if(minuesTwo == 30) {
						data3[m] = hoursTwo + ":" + minuesTwo;
					}else if(minuesTwo == 60) {
						minuesTwo = 0;
						hoursTwo += 1;
						data3[m] = hoursTwo + ":" + minuesTwo + "0";
					}
				}
			}
			for (int i = 0; i < data3.length; i++) {
				Log.i("qcs", "时间数组====" + data3[i]);
				list.add(data3[i]);
			}
		}*/
		
		int newMinues = minues;
		int newHours = hours;
		String newTime = "";
		boolean flag = false;
	//	while (true) {
		while (newHours <= 19) {
			newMinues += 30;
			if (newMinues < 60) {
				if((newHours + "").length() == 1) {
					newTime = "0" + newHours + ":" + newMinues;
				}else {
					newTime = newHours + ":" + newMinues;
				}
			} else if (newMinues == 60) {
				newHours += 1;
				newMinues = 0;
				if((newHours + "").length() == 1) {
					newTime = "0" + newHours + ":" + "0" + newMinues;
				}else {
					newTime = newHours + ":" + "0" + newMinues;
				}
			} else {
				newHours += 1;
				newMinues = newMinues - 60;
				if ((newMinues + "").length() == 1) {
					if((newHours + "").length() == 1) {
						newTime = "0" + newHours + ":" + "0" + newMinues;
					}else {
						newTime = newHours + ":" + "0" + newMinues;
					}
				} else {
					if((newHours + "").length() == 1) {
						newTime = "0" + newHours + ":" + newMinues;
					}else {
						newTime = newHours + ":" + newMinues;
					}
				}
			}
			
			/*if(newHours == 24) {
				newHours = 0;
				flag = true;
			}
		 	if(flag && newHours == hours) {
		 		time = hours + ":" +  minues;
			 	Log.i("qcs", "time == " + time);
				return;
			}*/
			
			list.add(newTime);
			/*if(newHours == 20) {
				return;
			}*/
		}
		String str = list.get(0); 
		if(mode == 1) {
			str = "大约" + str + "自取";
		}else if(mode == 2){
			str = "立即送出(大约" + str + "送达)";
		}else{
			str = str;
 		}
		Log.i("qcs", "mode == " + mode);
		Log.i("qcs", "str == " + str);
		list.remove(0);
		list.add(0, str);
		pickerView.setData(list);
		pickerView.setSelected(0);
		pickerView.setOnSelectListener(new onSelectListener() {
			@Override
			public void onSelect(String text) {
				if (icheckTime != null) {
					icheckTime.onCheckTime(text);
				}
			}
		});
	}

	ICheckTime icheckTime;
	/**
	 * 选中了那一个时间的监听
	 * @param checkTime 监听接口的实例化对象
	 */
	public void setOnCheckTime(ICheckTime checkTime) {
		icheckTime = checkTime;
	}
	/**
	 * 监听接口
	 */
	public interface ICheckTime {
		void onCheckTime(String time);
	}
	/**
	 * 给返回按钮设置点击的监听事件
	 */
	public void setCancelButtonOnClickListener(OnClickListener Listener) {
		cancle.setOnClickListener(Listener);
	}
	/**
	 * 给确认按钮设置点击的监听事件
	 */
	public void setConfirmButtonOnClickListener(OnClickListener Listener) {
		confirm.setOnClickListener(Listener);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		dialog.dismiss();
	}
}
