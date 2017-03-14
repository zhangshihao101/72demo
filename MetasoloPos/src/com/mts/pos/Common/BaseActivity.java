package com.mts.pos.Common;

import java.util.List;

import org.apache.http.NameValuePair;

import com.mts.pos.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {
	public RelativeLayout loading = null;

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		loading = (RelativeLayout) findViewById(R.id.loading);
	}

	/**
	 * 给子Activity调用新开一个异步线程
	 */
	public void getTask(Context context, String url, List<NameValuePair> nameValuePair, String which) {
		if (NetworkUtil.isConnected(BaseActivity.this)) {
			loading.setVisibility(View.VISIBLE);
			CommonTask commontask = new CommonTask(context, url, nameValuePair, which);
			commontask.execute("");
		} else {
			Toast.makeText(BaseActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
			// nonet.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * which是为了传给updateUI方法，用来判断是谁开启的异步线程
	 */
	class CommonTask extends MyPostTask {
		String which;

		public CommonTask(Context context, String url, List<NameValuePair> nameValuePair, String whichtask) {
			super(context, url, nameValuePair, whichtask);
			which = whichtask;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null == result || ("").equals(result) || result.length() == 0 || result.equals(Constants.not_found)
					|| result.equals(Constants.time_out)) {
				loading.setVisibility(View.GONE);
				Toast.makeText(BaseActivity.this, "网络不好，请重试！", Toast.LENGTH_SHORT).show();
				// nonet.setVisibility(View.VISIBLE);
			} else {
				loading.setVisibility(View.GONE);
				updateUI(which, result);
			}
		}
	}

	/**
	 * 为了让子Activity重写，更新UI
	 */
	protected void updateUI(String whichtask, String result) {

	}

	/**
	 * 为了让Activity重写，当网络连接失败，重新加载一次
	 */
	protected void restartNet(Context context, String url, List<NameValuePair> nameValuePair, String which) {
		getTask(context, url, nameValuePair, which);
	}
	/**
	 * 点击返回事件
	 */
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	//
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// BaseActivity.this.finish();
	// }
	// return true;
	// }
}
