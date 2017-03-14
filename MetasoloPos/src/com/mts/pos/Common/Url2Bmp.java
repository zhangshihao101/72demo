package com.mts.pos.Common;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.security.KeyStore;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Url2Bmp {

	public static Bitmap httpPost(Context context, String apiUrl, List<NameValuePair> nameValuePair) throws Exception {

		HttpClient httpClient = getNewHttpClient();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		HttpPost httppost = new HttpPost(apiUrl);
		int responseCode = 0;
		Bitmap bitmap = null;
		httppost.setEntity(new UrlEncodedFormEntity(nameValuePair, HTTP.UTF_8));
		HttpResponse response = httpClient.execute(httppost);
		// 返回值
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == HttpStatus.SC_OK) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			response.getEntity().writeTo(byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		} else if (responseCode == HttpStatus.SC_NOT_FOUND) {
			throw new FileNotFoundException();
		} else if (responseCode == HttpStatus.SC_REQUEST_TIMEOUT) {
			throw new Exception();
		}
		return bitmap;
	}
	public static Bitmap httpPost(Context context, String apiUrl) throws Exception {
		
		HttpClient httpClient = getNewHttpClient();
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
		HttpConnectionParams.setSoTimeout(httpParameters, 10000);
		HttpPost httppost = new HttpPost(apiUrl);
		int responseCode = 0;
		Bitmap bitmap = null;
		HttpResponse response = httpClient.execute(httppost);
		// 返回值
		responseCode = response.getStatusLine().getStatusCode();
		if (responseCode == HttpStatus.SC_OK) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			response.getEntity().writeTo(byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		} else if (responseCode == HttpStatus.SC_NOT_FOUND) {
			throw new FileNotFoundException();
		} else if (responseCode == HttpStatus.SC_REQUEST_TIMEOUT) {
			throw new Exception();
		}
		return bitmap;
	}

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
}
