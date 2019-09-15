package com.yst.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jack.ui.CustomerHeaderView;
import com.jack.ui.CustomerHeaderView.HeaderListener;
import com.jack.ui.MProcessDialog;
import com.yst.yiku.R;

public class PayMobileActivity extends Activity implements HeaderListener,
		OnClickListener {

	private CustomerHeaderView headerView;
	private TextView geRen; // 标题
	private ImageView btn_main, confirmButton, addressBookButton;// 右边

	private EditText inputMobileEditText;
	private EditText inputAgainMobileEditText;
	private Spinner xuanzejineSpinner;

	private String selectedMoney = "30";
	private String destMobile;
	public static boolean flag = true;
	String realPayMoney = "";
	String result = "";
	private String[] mchongzhijine = { "30元", "50元", "100元" };
	private RequestQueue requestQueue = null;
	private StringRequest stringRequest = null;
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

	private TextView query_charge_history_tv, tv_pay_count;
	private ImageView query_charge_back_iv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_mobile);
		requestQueue = Volley.newRequestQueue(this);
		initView();
	}

	private void initView() {
		query_charge_back_iv = (ImageView) findViewById(R.id.query_charge_back_iv);
		query_charge_back_iv.setOnClickListener(this);
		query_charge_history_tv = (TextView) findViewById(R.id.query_charge_history_tv);
		query_charge_history_tv.setOnClickListener(this);
		tv_pay_count = (TextView) findViewById(R.id.tv_pay_count);
		inputMobileEditText = (EditText) this
				.findViewById(R.id.inputMobileEditText);
		inputAgainMobileEditText = (EditText) this
				.findViewById(R.id.inputAgainMobileEditText);
		confirmButton = (ImageView) findViewById(R.id.confirmButton);
		addressBookButton = (ImageView) findViewById(R.id.addressBookButton);
		confirmButton.setOnClickListener(this);
		addressBookButton.setOnClickListener(this);
		xuanzejineSpinner = (Spinner) findViewById(R.id.xuanzejineSpinner);
		ArrayAdapter<String> chongzhimianeAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, mchongzhijine);
		chongzhimianeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		xuanzejineSpinner.setAdapter(chongzhimianeAdapter);
		xuanzejineSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						switch (arg2) {
						case 0:
							selectedMoney = "30";
							break;
						case 1:
							selectedMoney = "50";
							break;
						case 2:
							selectedMoney = "100";
							break;
						}
						tv_pay_count.setText("您需要支付" + selectedMoney + "元");
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.query_charge_back_iv:
			finish();
			break;
		case R.id.confirmButton:
			String phoneNum = inputMobileEditText.getText().toString()
					.replaceAll(" ", "");
			String phoneNum2 = inputAgainMobileEditText.getText().toString()
					.replaceAll(" ", "");
			if (TextUtils.isEmpty(phoneNum)) {
				Toast.makeText(this, "请输入手机号!", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!LoginActivity.isMobileNO(phoneNum)) {
				Toast.makeText(this, "请输入有效的手机号", Toast.LENGTH_SHORT).show();
				return;
			}
			if (!phoneNum.equals(phoneNum2)) {
				Toast.makeText(this, "手机号码不一致", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent it = new Intent(this, PayMobileDetailActivity.class);
			it.putExtra("money", selectedMoney);
			it.putExtra("phone", phoneNum);
			it.putExtra("list", 1);
			startActivity(it);
			break;
		case R.id.addressBookButton:
			Intent i = new Intent();
			i.setAction(Intent.ACTION_PICK);
			i.setData(ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult(i, SEND_SMS_TYPE);
			break;
		case R.id.query_charge_history_tv:
			Intent itlist = new Intent(this, PayMobileDetailActivity.class);
			itlist.putExtra("list", 2);
			startActivity(itlist);
			break;
		default:
			break;
		}
	}

	private final static int SEND_SMS_TYPE = 1;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SEND_SMS_TYPE:
			if (data == null) {
				return;
			}
			Uri uri = data.getData();
			Cursor cursor = this.getContentResolver().query(uri, null, null,
					null, null);
			cursor.moveToFirst();

			String number = getContactPhone(cursor);
			String numberext = number.replace(" ", "");
			inputMobileEditText.setText(numberext);
			inputAgainMobileEditText.setText(numberext);
			break;
		default:
			break;
		}
	}

	private String getContactPhone(Cursor cursor) {
		int phoneColumn = cursor
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
		int phoneNum = cursor.getInt(phoneColumn);
		String result = "";
		if (phoneNum > 0) {
			// 获得联系人的ID号
			int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
			String contactId = cursor.getString(idColumn);
			// 获得联系人电话的cursor
			Cursor phone = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			if (phone.moveToFirst()) {
				for (; !phone.isAfterLast(); phone.moveToNext()) {
					int index = phone
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
					// int typeindex = phone
					// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
					// int phone_type = phone.getInt(typeindex);
					String phoneNumber = phone.getString(index);
					result = phoneNumber;
					// switch (phone_type) {//此处请看下方注释
					// case 2:
					// result = phoneNumber;
					// break;
					//
					// default:
					// break;
					// }
				}
				if (!phone.isClosed()) {
					phone.close();
				}
			}
		}
		return result;
	}

	@Override
	public void onLeftClick() {
		this.finish();
	}

	@Override
	public void onRightClick() {
		// TODO Auto-generated method stub

	}

	private MProcessDialog mInfoProgressDialog;

	public void showInfoProgressDialog() {
		if (mInfoProgressDialog == null)
			mInfoProgressDialog = new MProcessDialog(this);
		mInfoProgressDialog.setMessage("加载中");
		mInfoProgressDialog.setCancelable(false);
		if (!this.isFinishing()) {
			try {
				mInfoProgressDialog.show();
			} catch (Exception e) {

			}
		}
	}

	public void dismissInfoProgressDialog() {
		mInfoProgressDialog.dismiss();
	}
}
