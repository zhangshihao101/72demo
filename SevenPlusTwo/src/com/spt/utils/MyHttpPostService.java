package com.spt.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyHttpPostService extends Service {

	private ExecutorService executorService;
	private List<BasicNameValuePair> request_params;
	private String strUri;
	private HashMap<String, Object> receive_params;
	private String strResult;
	private String action;
	private String tpye;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		executorService = Executors.newFixedThreadPool(5);

		request_params = new ArrayList<BasicNameValuePair>();
		super.onCreate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {

		System.out.println("-----------------------------------service post---------------------");

		strUri = "";
		strUri = intent.getStringExtra("uri"); // get uri
		receive_params = (HashMap<String, Object>) intent.getSerializableExtra("param"); // get
																							// param
		strResult = ""; // init result
		request_params.clear(); // init paramlist
		action = intent.getAction(); // get action
		tpye = intent.getStringExtra("type");

		executorService.submit(new Runnable() {

			@Override
			public void run() {
				if (receive_params != null && !receive_params.isEmpty()) { // 发送请求
					final String localType = intent.getStringExtra("type");
					boolean responseState = isLoginSuccess(strUri, receive_params); // 响应请求
					if (responseState) {
						sendBroadcastToActivity(localType, responseState); // 发送广播更新ui
					}
				}

			}
		});

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		executorService.shutdown();
		super.onDestroy();
	}

	// request Http
	private boolean isLoginSuccess(String uri, HashMap<String, Object> param) {

		boolean state = false;
		try {
			strResult = OkHttpUtil.postStringToServer(uri, param);
		} catch (IOException e) {
			e.printStackTrace();
		}
		state = true;
		return state;
	}

	private void sendBroadcastToActivity(String witch, boolean isSuccess) {
		Intent itResult = new Intent(action);
		itResult.putExtra("type", witch);
		if (isSuccess) {
			itResult.putExtra("isSuccess", "ok");
			itResult.putExtra("result", strResult);
		} else {
			itResult.putExtra("isSuccess", "no");
		}
		System.out.println("-----------------------------------send---------------------");
		System.out.println("strResult" + strResult + " **");
		System.out.println(witch);
		System.out.println("post");
		super.sendBroadcast(itResult);
	}

}
