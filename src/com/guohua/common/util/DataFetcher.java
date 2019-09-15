package com.guohua.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class DataFetcher extends AsyncTask<URL, Void, String> {

	private static final String TAG = "DataFetcher";

	public DataListener dataListener;
	public SheDataListener sListener;
	public int myTag;

	public DataFetcher(int myTag, DataListener dataListener) {
		this.myTag = myTag;
		this.dataListener = dataListener;
	}

	public DataFetcher(int myTag, SheDataListener sListener) {
		this.myTag = myTag;
		this.sListener = sListener;
	}

	public SheDataListener getsListener() {
		return sListener;
	}

	public void setsListener(SheDataListener sListener) {
		this.sListener = sListener;
	}

	public DataListener getDataListener() {
		return dataListener;
	}

	public void setDataListener(DataListener dataListener) {
		this.dataListener = dataListener;
	}

	public static final String NETWORK_ERROR = "network error";
	public static final String URL_ERROR = "invalidate url";
	public static final String OPEN_STREAM_ERROR = "open stream error";
	public static final String ERROR_OLDPASS = "该用户不存在";

	@Override
	protected String doInBackground(URL... urls) {
		HttpURLConnection urlConnection = null;
		InputStream inputStream = null;

		/*
		 * if (NetworkUtils.getNetworkState(AppContext.getApplication()) == 0) {
		 * if (dataListener != null) { return NETWORK_ERROR; } }
		 */
		try {
			URL url = urls[0];
			Log.d(TAG, "the access network url is " + url.toString());
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setReadTimeout(10000 /* millisecon ds */);
			urlConnection.setConnectTimeout(15000 /* milliseconds */);
			urlConnection.setRequestMethod("GET");
			// urlConnection.setDoInput(true);
			urlConnection.connect();
			int response = urlConnection.getResponseCode();
			Log.d(TAG, "The response is: " + response);

			// 密码错误
			if (response == 500) {
				return ERROR_OLDPASS;
			}

			inputStream = urlConnection.getInputStream();

			if (inputStream != null) {
				String value = readStream(inputStream);
				Log.d(TAG, "返回的数据是：" + value);
				inputStream.close();
				urlConnection.disconnect();
				return value;
			} else {
				Log.d(TAG, "打开输入流失败");
				inputStream.close();
				urlConnection.disconnect();
				return OPEN_STREAM_ERROR;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			if (dataListener != null) {
				return URL_ERROR;
			}
		} catch (IOException e) {
			if (dataListener != null) {
				return NETWORK_ERROR;
			}
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();

			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String jsonString) {
		// TODO Auto-generated method stub
		super.onPostExecute(jsonString);

		if (this.dataListener == null)
			return;
		if (jsonString != null) {
			if (jsonString.equals(NETWORK_ERROR)) {
				Log.d(TAG, "network error");
				dataListener.onError(myTag, "网络异常，请尝试连接网络");
				return;
			}
			if (jsonString.equals(URL_ERROR)) {
				Log.d(TAG, "url error");
				dataListener.onError(myTag, "地址异常，请检车请求地址");
				return;
			}
			if (jsonString.equals(OPEN_STREAM_ERROR)) {
				Log.d(TAG, OPEN_STREAM_ERROR);
				dataListener.onError(myTag, "打开输入异常，请尝试连接网络");
				return;
			}
			if (jsonString.equals(ERROR_OLDPASS)) {
				Log.d(TAG, ERROR_OLDPASS);
				dataListener.onError(myTag, "旧密码输入错误");
				return;
			}
			if (jsonString.contains("value")) {

				try {
					JSONObject jsonData = new JSONObject(jsonString);
					String value = jsonData.getString("value");
					if (value != null) {
						dataListener.onSuccess(myTag, jsonData);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			if (jsonString.contains("Value")) {

				try {
					JSONObject jsonData = new JSONObject(jsonString);
					String value = jsonData.getString("Value");
					if (value != null) {
						dataListener.onSuccess(myTag, jsonData);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
			if (jsonString.contains("DizhiDetail")) {
				sListener.onSuccess(myTag, jsonString);
			}
			if (jsonString.contains("Szd")) {

				try {
					JSONObject jsonData = new JSONObject(jsonString);
					dataListener.onSuccess(myTag, jsonData);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (jsonString.contains("RESULT")) {
				try {
					JSONObject jsonData = new JSONObject(jsonString);
					String result = jsonData.getString("RESULT");

					if (result != null && result.equalsIgnoreCase("SUCCESS")) {
						dataListener.onSuccess(myTag, jsonData);
					} else {
						if (result != null
								&& (result.equalsIgnoreCase("failed") || result
										.equalsIgnoreCase("failure")))
							if (jsonData.has("ERROR_MSG"))
								dataListener.onError(myTag,
										jsonData.getString("ERROR_MSG"));
							else if (jsonData.has("ERROR"))
								dataListener.onError(myTag,
										jsonData.getString("ERROR"));
							else if (result != null
									&& result.equalsIgnoreCase("Failure"))
								dataListener.onError(myTag,
										jsonData.getString("ERROR"));
							else {
								dataListener.onError(myTag,
										"return data format error");
							}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					dataListener.onError(myTag,
							"parser return data to json error");
				}

			} else {
				dataListener.onError(myTag, "修改成功");
			}

		}
	}

	/**
	 * 返回结果
	 * 
	 * @param inputStream
	 */
	public static String readStream(InputStream inputStream) {
		String value = null;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"));
			StringBuffer buf = new StringBuffer();
			String str;

			while ((str = reader.readLine()) != null) {
				buf.append(str);
			}
			value = buf.toString();
			return value;
			/*
			 * Message msg = new Message(); msg.what = CODE; msg.obj = value;
			 * handler.sendMessage(msg); Log.d(TAG, "大众点评返回的数据是：" + value);
			 */

		} catch (Exception e) {
		}

		return null;
	}

}
