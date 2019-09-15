package com.guohua.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;

public class DataFetcherPost extends AsyncTask<URL, Void, String> {

	private static final String TAG="DataFetcher";
	 
	DataListener dataListener;
	
	private int myTag;
	
	public DataFetcherPost(int myTag,DataListener dataListener){
		this.myTag = myTag;
		this.dataListener = dataListener;
	}
	
	public DataListener getDataListener() {
		return dataListener;
	}
	public void setDataListener(DataListener dataListener) {
		this.dataListener = dataListener;
	}
	@Override
	protected String doInBackground(URL... urls) {
		   HttpURLConnection urlConnection = null;
           InputStream inputStream = null;
           try {
               URL url = urls[0];
               System.out.println("the access network url is " + url.toString());
               urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.setReadTimeout(10000 /* milliseconds */);
               urlConnection.setConnectTimeout(15000 /* milliseconds */);
               urlConnection.setRequestMethod("GET");
               // urlConnection.setDoInput(true);
               urlConnection.connect();
               int response = urlConnection.getResponseCode();
               Log.d(TAG, "The response is: " + response);
               inputStream = urlConnection.getInputStream();

               String value = readStream(inputStream);
               
               Log.d(TAG, "大众点评返回的数据是：" + value);
               return value;
           } catch (MalformedURLException e) {
               e.printStackTrace();
               if(dataListener!=null){
            	   dataListener.onError( myTag, e.getLocalizedMessage());
               }
           } catch (IOException e) {
        	   if(dataListener!=null){
            	   dataListener.onError(myTag,e.getLocalizedMessage());
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
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
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
