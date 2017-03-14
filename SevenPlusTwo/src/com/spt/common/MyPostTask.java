package com.spt.common;

import java.util.List;

import org.apache.http.NameValuePair;

import com.spt.controler.App;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

public class MyPostTask extends AsyncTask<String, Integer, String>{
	private Context context;
	private String url;
	private String result;
	private List<NameValuePair> nameValuePair;
	private String which;
	
	/**
	 * url是请求的地址
	 * listener是需要继承的接口
	 * nameValuePair是传给服务器的数据
	 * whichtask是判断谁启动的task
	 */
	public MyPostTask(Context context,String url,List<NameValuePair> nameValuePair,String whichtask){
		this.context = context;
		this.url = url;
		this.nameValuePair = nameValuePair;
		this.which=whichtask;
	}
	@Override
	protected String doInBackground(String... params) {
		//判断网络连接
		if (NetworkUtil.isConnected(context)) {
			App app = (App) ((Activity)context).getApplication();
			try {
				result = NetworkUtil.httpPost(app, url, nameValuePair);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			result = Constants.no_network;
		}
		return result;//传给onPostExecute
	}
	
	@Override
	protected void onPostExecute(String result) {
		
	}
	
}