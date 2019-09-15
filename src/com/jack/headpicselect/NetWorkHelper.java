package com.jack.headpicselect;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class NetWorkHelper {
	private static String tag = "NetWorkHelper";
	/** 接口前缀 */
	private static String url_prefix = "http://118.126.17.101:8801/Service/MemberRestService.svc/";

	public static String getUrl(String suffix) {
		return url_prefix + suffix;
	}

	/**
	 * POST传给服务器信息。
	 * 
	 * @param suffix
	 *            接口后缀
	 * @param subinfo
	 *            提交的信息。（List<NameValuePair>）
	 * @return 字符串。自行解析
	 */
	public static String PostInfo(String suffix, List<NameValuePair> subinfo) {
		String responseInfo = "";
		HttpPost httpPost = new HttpPost(getUrl(suffix));
		HttpResponse httpResponse = null;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(subinfo, HTTP.UTF_8));
			HttpClient client = new DefaultHttpClient();
			httpResponse = client.execute(httpPost);
			// 连接时间20秒超时
			client.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
			// 传输时间30S超时
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
					30000);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				responseInfo = EntityUtils.toString(httpResponse.getEntity());
				return responseInfo;
			}
		} catch (Exception e) {
			// Tools.makeLog(tag, "PostInfo_Erro");
			return "";
		}
		return "";
	}

	/**
	 * 上传图片到服务器
	 * 
	 * @param file
	 *            要上传的文件
	 * @param url
	 *            接口
	 * @return
	 */
	public static String postImg(File file, String url) {
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(url);
			MultipartEntity reqEntity = new MultipartEntity();
			if (file != null) {
				reqEntity.addPart("ImageContext", new FileBody(file));
			}
			httppost.setEntity(reqEntity);
			HttpResponse response = httpclient.execute(httppost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("==传图片失败_SubmitPost");
		} finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception ignore) {
			}
		}
		return "111";
	}

	public static byte[] loadByteFromURL(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet requestGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpClient.execute(requestGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity httpEntity = httpResponse.getEntity();
				return EntityUtils.toByteArray(httpEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("====>" + e.toString());
		}
		return null;
	}

	/**
	 * 根据URL得到输入流
	 * 
	 * @param urlstr
	 * @return
	 */
	public static InputStream getInputStreamFormUrl(String urlstr) {
		InputStream inputStream = null;
		try {
			URL url = new URL(urlstr);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			inputStream = urlConn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * 下载音频
	 * 
	 * @param url
	 * @return
	 */
	public static String loadVoiceFromURL(String url) {
		URL urlObj;
		BufferedInputStream bis = null;
		HttpURLConnection httpConn = null;
		ByteArrayOutputStream baos = null;
		try {
			urlObj = new URL(url);
			httpConn = (HttpURLConnection) urlObj.openConnection();
			if (httpConn.getResponseCode() == 200) {
				bis = new BufferedInputStream(httpConn.getInputStream());
				baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[8 * 1024];
				int c = 0;
				while ((c = bis.read(buffer)) != -1) {
					baos.write(buffer, 0, c);
					baos.flush();
				}
				return baos.toString();
			}
		} catch (Exception e) {
			Log.d("loadStringFromURL", "==erro");
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				httpConn.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 附带文件流，上传信息
	 * 
	 * @param file
	 * @param suffix
	 * @return
	 */
	public static String postImg_Record(File file, String suffix,
			String[] keys, String[] values) {
		String url = url_prefix + suffix;
		HttpURLConnection httpConn = null;
		BufferedInputStream bis = null;
		DataOutputStream dos_data = null;
		ByteArrayOutputStream baos = null;
		ByteArrayOutputStream baos1 = null;
		FileInputStream fis = null;
		try {
			if (file != null) {
				fis = new FileInputStream(file);
				int c = 0;
				byte[] buffer = new byte[1024 * 8];
				baos = new ByteArrayOutputStream();
				while ((c = fis.read(buffer)) != -1) {
					baos.write(buffer, 0, c);
					baos.flush();
				}
			}
			URL urlObj = new URL(url);
			httpConn = (HttpURLConnection) urlObj.openConnection();
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Accept-Charset", "utf-8");
			httpConn.setRequestProperty("contentType", "utf-8");
			for (int i = 0; i < keys.length; i++) {
				httpConn.addRequestProperty(
						keys[i],
						new String(values[i].getBytes(), Charset
								.forName("UTF-8")));
			}
			if (file != null) {
				dos_data = new DataOutputStream(httpConn.getOutputStream());
				dos_data.write(baos.toByteArray());
				dos_data.flush();
			}

			byte[] buffer = new byte[1024 * 8];
			int c = 0;
			if (httpConn.getResponseCode() == 200) {
				bis = new BufferedInputStream(httpConn.getInputStream());
				baos1 = new ByteArrayOutputStream();
				while ((c = bis.read(buffer)) != -1) {
					baos1.write(buffer, 0, c);
					baos1.flush();
					// Log.d("postImg_Record", "==come111");
				}
			}
			return new String(baos1.toByteArray(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 附带文件流，上传信息
	 * 
	 * @param file
	 * @param suffix
	 * @return
	 */
	public static String postImg_Record2(String url, String params) {
		Log.e(tag, "==tab0");
		HttpURLConnection httpConn = null;
		BufferedInputStream bis = null;
		ByteArrayOutputStream baos = null;
		try {
			Log.e(tag, "==tab1");
			URL urlObj = new URL(url);
			httpConn = (HttpURLConnection) urlObj.openConnection();
			httpConn.setConnectTimeout(10 * 1000);
			httpConn.setDoOutput(true);
			httpConn.setRequestMethod("POST");
			Log.e(tag, "==tab1.1");
			// post请求的参数
			OutputStream out = httpConn.getOutputStream();
			out.write(params.getBytes());
			out.flush();
			out.close();
			httpConn.connect();
			int responseCode = httpConn.getResponseCode();
			Log.e(tag, "==tab011responseCode:" + responseCode);
			if (responseCode == 200) {
				Log.e(tag, "==tab2.1");
				InputStream is = httpConn.getInputStream();
				String state = IOHelper.streamToString(is, "utf-8");
				return state;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "456";
	}

	// public static String test1(String suffix, File file, String[] keys,
	// String[] values) {
	// String url = url_prefix + suffix;
	// HttpClientImp httpClientImp = HttpClientImp.INSTANCE;
	// if (file.exists() && file.isFile()) {
	//
	// Map<String, String> param = new HashMap<String, String>();
	// for (int i = 0; i < keys.length; i++) {
	// param.put(keys[i], values[i]);
	// }
	// Map<String, File> fileMap = new HashMap<String, File>();
	// fileMap.put("fileData", file);
	// // 非文件的参数 用Map<String, String>的键值对传进去 就是param
	// // 文件参数 也用Map<String, File>的键值对传递进去 fileMap。
	// try {
	// String jsonStr = httpClientImp.postForString(url, param,
	// fileMap);
	// return jsonStr;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return null;
	// }

	/**
	 * 作用：实现网络访问文件，将获取到数据储存在文件流中
	 * 
	 * @param url
	 *            ：访问网络的url地址
	 * @return inputstream
	 */
	public static InputStream loadFileFromURL(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet requestGet = new HttpGet(url);
		HttpResponse httpResponse;
		try {
			httpResponse = httpClient.execute(requestGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = httpResponse.getEntity();
				return entity.getContent();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
