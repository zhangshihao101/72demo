package com.mts.pos.Common;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
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

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class App extends Application{
	private HttpClient httpClient;
	public static List<Activity> activityList = new LinkedList<Activity>();
    
	@Override
	public void onCreate() {
		super.onCreate();
		httpClient = this.createHttpClient();
		LogcatHelper.getInstance(this).start();
	}
	
	/**
	 * 将已经打开的Activity添加到列表里
	 */
	public static void addActivity(Activity activity) {
		activityList.add(activity);
		Log.e("LOOK", "--------");
	}

	/**
	 * 遍历所有Activity并finish
	 */
	public static void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		/**
		 * 进程级杀死，根本不会走生命周期。
		 * 安卓的应用是单独的一个linux进程，只有在进程存活的前提下才能按部就班的调用onDestroy。如果进程突然被kill
		 * ，那么onDestroy就不会被调用。这是也不需要担心资源的释放问题。
		 */
		// System.exit(0);//加上这个Activity的onDestroy方法就不调用了
		// android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * 创建HttpClient实例
	 */
	private HttpClient createHttpClient() {
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, 40000);
		HttpConnectionParams.setSoTimeout(httpParameters, 40000);
		HttpProtocolParams.setVersion(httpParameters, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParameters,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(httpParameters, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager connMgr = new ThreadSafeClientConnManager(
				httpParameters, schReg);

		return new DefaultHttpClient(connMgr, httpParameters);
	}
	
	/**
	 * 对外提供HttpClient实例
	 */
	public HttpClient getHttpClient() {
		return httpClient;
	}

	/**
	 * 关闭连接管理器并释放资源
	 */
	private void shutdownHttpClient() {
		if (httpClient != null && httpClient.getConnectionManager() != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		this.shutdownHttpClient();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		this.shutdownHttpClient();
	}
}
