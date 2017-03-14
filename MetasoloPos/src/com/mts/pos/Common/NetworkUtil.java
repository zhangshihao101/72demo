package com.mts.pos.Common;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.util.List;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * 判断是否有网络连接
 */
public class NetworkUtil {
	public static boolean isConnected(Context context) {
		// 网络状态
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// 网络信息
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

		// 检查网络
		if (networkInfo != null) {
			return conManager.getActiveNetworkInfo().isConnected();
		}
		return false;
	}
	/**
	 * 访问Https的时候，会有证书的问题，忽略证书直接访问
	 */
	public static HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	/**
	 * HTTP Post 通信处理
	 * apiUrl 是请求的地址
	 * nameValuePair 是发送给的数据
	 */
	public static String httpPost(Context context, String apiUrl,List<NameValuePair> nameValuePair)
			throws Exception {
			
		// HttpClient
//		HttpClient httpClient = new DefaultHttpClient();
		HttpClient httpClient = getNewHttpClient();
		// HTTP Post
		HttpPost httppost = new HttpPost(apiUrl);
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		
		int responseCode = 0;
		String result = "";
//		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
		
		// HTTP post通信
		HttpResponse response = httpClient.execute(httppost);
//		response.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		// 返回值
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == HttpStatus.SC_OK) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			response.getEntity().writeTo(byteArrayOutputStream);
			result = byteArrayOutputStream.toString();
		} else if (responseCode == HttpStatus.SC_NOT_FOUND) {
			result=Constants.not_found;
			Log.e("LOOK", "NOT_FOUND");
			// 服务器连接失败
			throw new FileNotFoundException();
		} else if (responseCode == HttpStatus.SC_REQUEST_TIMEOUT) {
			result=Constants.time_out;
			Log.e("LOOK", "TIME_OUT");
			// 连接超时
			throw new Exception();
		}
		return result;
	}
	public static String httpGet2(Context context, String url, String userAgent)
			throws Exception {
		// offline
		// 网络连接确认
		if (NetworkUtil.isConnected(context) == false) {
			throw new Exception();
		}
		// HttpParams httpParameters = new BasicHttpParams();
		// HttpConnectionParams.setConnectionTimeout(httpParameters, 40000);
		// HttpConnectionParams.setSoTimeout(httpParameters, 40000);
		// HttpClient httpClient = new DefaultHttpClient(httpParameters);
		App app = (App) ((Activity) context).getApplication();
		HttpClient httpClient = app.getHttpClient();
		//
		// HttpParams params = httpClient.getParams();
		//
		// HttpConnectionParams.setConnectionTimeout(params, 10000);
		// HttpConnectionParams.setSoTimeout(params, 10000);

		HttpGet request = new HttpGet(url);
		request.addHeader("User-Agent", userAgent);
		HttpResponse httpResponse = null;

		try {
			httpResponse = httpClient.execute(request);
			// 通信失败
			if (httpResponse == null) {
				throw new Exception();
			}
		} catch (Exception e) {
			Log.e("myTask", "HTTP error === " + e.getMessage());
			throw new Exception();
		}

		String response = null;
		if (httpResponse != null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			try {
				response = EntityUtils.toString(httpEntity);
			} catch (Exception e) {
				Log.e("myTask",
						"EntityUtils.toString error === " + e.getMessage());
				throw new Exception();
			} finally {
				try {
					httpEntity.consumeContent();
				} catch (IOException e) {
					Log.e("myTask",
							"httpEntity.consumeContent error === "
									+ e.getMessage());
					throw new Exception();
				}
			}
		} else {
			HttpEntity httpEntity = httpResponse.getEntity();

			try {
				response = EntityUtils.toString(httpEntity);
			} catch (Exception e) {
				Log.e("myTask", "EntityUtils.toString === " + e.getMessage());
				throw new Exception();
			}
			throw new Exception();
		}
		return response;

	}
}