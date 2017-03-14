package com.spt.utils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.spt.controler.App;

import org.json.JSONObject;

import java.util.Map;

/**
 * 网络请求框架
 * 
 * @author lihongxuan
 *
 */
public class VolleyHelper {

	/**
	 * 定义一个返回数据的接口
	 */
	public interface OnCallBack {
		void OnSuccess(String data);

		void OnError(VolleyError volleyError);
	}

	/**
	 * 发送get请求的方法
	 *
	 * @param url
	 *            请求网址
	 * @param callBack
	 *            请求的回调
	 */
	public static void get(String url, final OnCallBack callBack) {
		JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				callBack.OnSuccess(jsonObject.toString());
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callBack.OnError(volleyError);
			}
		});
		App.getRequestQueue().add(request);
	}

	/**
	 * @param url
	 *            请求的网址
	 * @param params
	 *            请求的参数
	 * @param callBack
	 *            请求的回调
	 */
	public static void post(String url, final Map<String, String> params, final OnCallBack callBack) {
		StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String s) {
				callBack.OnSuccess(s);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				callBack.OnError(volleyError);
			}
		}) {
			// 此方法用于设置参数
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (params == null) {
					return super.getParams();
				} else {
					return params;
				}

			}
		};

		App.getRequestQueue().add(request);
	}
	
}
