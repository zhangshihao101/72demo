package com.spt.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyHttpGetService extends Service {

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
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("-----------------------------------start get---------------------");
		strUri = "";
		strUri = intent.getStringExtra("uri"); // get uri
		receive_params = (HashMap<String, Object>) intent.getSerializableExtra("param"); // get
																							// param
		strResult = ""; // init result
		request_params.clear(); // init paramlist
		action = intent.getAction(); // get action
		tpye = intent.getStringExtra("type");

		executorService.submit(new Runnable() {

			@SuppressWarnings("rawtypes")
			@Override
			public void run() {

				if (receive_params != null && !receive_params.isEmpty()) { // set
					for (Iterator<?> it = receive_params.entrySet().iterator(); it.hasNext();) {
						Map.Entry e = (Entry) it.next();
						request_params.add(new BasicNameValuePair(e.getKey().toString(), e.getValue().toString()));
					}
				}
				String param = URLEncodedUtils.format(request_params, "UTF-8");
				boolean responseState = isLoginSuccess(param); // get response
				if (responseState) {
					sendBroadcastToActivity(responseState); // send broadcast to
															// activity and
															// update UI
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
	private boolean isLoginSuccess(String param) {

		boolean state = false;
		strUri = OkHttpUtil.attachHttpGetParams(strUri, request_params);
		try {
			strResult = OkHttpUtil.getStringFromServer(strUri);
			state = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return state;
	}

	private void sendBroadcastToActivity(boolean isSuccess) {
		Intent itResult = new Intent(action);
		itResult.putExtra("type", tpye);
		if (isSuccess) {
			itResult.putExtra("isSuccess", "ok");
			itResult.putExtra("result", strResult);
		} else {
			itResult.putExtra("isSuccess", "no");
		}
		System.out.println("-----------------------------------send---------------------");
		System.out.println("strResult" + strResult);
		System.out.println(tpye);
		System.out.println("get");
		super.sendBroadcast(itResult);
	}

}
