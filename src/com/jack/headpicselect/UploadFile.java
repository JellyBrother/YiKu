package com.jack.headpicselect;

import java.io.File;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class UploadFile {
	private static String requestURL = "http://118.126.17.101:8801/Service/MemberRestService.svc/GpsUpFile"; // ��������URL
	private static final String CHARSET = "UTF-8"; // ���ñ���
	private int readTimeOut = 10 * 1000; // ��ȡ��ʱ
	private int connectTimeout = 10 * 1000; // ��ʱʱ��
	private int mStatus; // ����״̬��

	/**
	 * @Description: �ϴ�����
	 * @param audioPath
	 *            �ϴ���Ƶ�ļ���ַ ��sdcard/image/a.amr
	 * @param text
	 *            �ϴ��ı���ֵ
	 * @param imageUrlList
	 *            ͼƬ��ַ�ļ��� ��sdcard/image/a.jpg, sdcard/image/b.jpg
	 * @return void
	 */
	public synchronized void postMethod(List<String> imageUrlList) {
		try {
			String[] filePath = new String[imageUrlList.size()];

			int size = imageUrlList.size();
			for (int i = 0; i < size; i++) {
				filePath[i] = imageUrlList.get(i);
			}

			// ���ӳ�ʱ������ʱ����
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					connectTimeout);
			HttpConnectionParams.setSoTimeout(httpParams, readTimeOut);

			// �����������
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpPost post = new HttpPost(requestURL);
			MultipartEntity entity = new MultipartEntity();

			// �ϴ�ͼƬ
			for (String p : filePath) {
				entity.addPart("fileimg", new FileBody(new File(p), "image/*"));
			}

			post.setEntity(entity);
			HttpResponse resp = client.execute(post);
			// ��ȡ�ص�ֵ
			Log.e("", EntityUtils.toString(resp.getEntity()));
			mStatus = resp.getStatusLine().getStatusCode();
			Log.e("", mStatus + "_________________-----");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
