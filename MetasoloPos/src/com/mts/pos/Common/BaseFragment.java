package com.mts.pos.Common;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BaseFragment extends Fragment {
	
//	public RelativeLayout loading = null;
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////		loading = (RelativeLayout) container.findViewById(R.id.loading);
//		return super.onCreateView(inflater, container, savedInstanceState);
//	}

	/**
	 * 给子Fragment调用新开一个异步线程
	 */
	public void getTask(Context context, String url, List<NameValuePair> nameValuePair, String which) {
		if (NetworkUtil.isConnected(getActivity())) {
			// loading.setVisibility(View.VISIBLE);
			CommonTask commontask = new CommonTask(context, url, nameValuePair, which);
			commontask.execute("");
		} else {
			Toast.makeText(getActivity(), "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
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
				// loading.setVisibility(View.GONE);
				Toast.makeText(getActivity(), "网络不好，请重试！", Toast.LENGTH_SHORT).show();
				// nonet.setVisibility(View.VISIBLE);
			} else {
				// loading.setVisibility(View.GONE);
				updateUI(which, result);
			}
		}
	}

	/**
	 * 为了让子Fragment重写，更新UI
	 */
	protected void updateUI(String whichtask, String result) {

	}

	/**
	 * 为了让Fragment重写，当网络连接失败，重新加载一次
	 */
	protected void restartNet(Context context, String url, List<NameValuePair> nameValuePair, String which) {
		getTask(context, url, nameValuePair, which);
	}

}
