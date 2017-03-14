package com.ordering.movingordering.Adapter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.ordering.movingordering.Activity.LoginActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {

	// 界面列表
	private List<View> views;
	private Activity activity;
	Timer Timer = null;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}

	// 销毁arg1位置的界面
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public void finishUpdate(View container) {

	}

	// 获得当前页面数
	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	// 初始化position位置的界面
	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position), 0);
		if (position == views.size() - 1) {
			// ImageView mStartWeiboImageButton = (ImageView)
			// container.findViewById(R.id.iv_start_weibo);
			// mStartWeiboImageButton.setOnClickListener(new OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			Timer = new Timer();
			Timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// 设置已经引导
					setGuided();
					goHome();
				}
			}, 3000);

			// }
			// });
		}
		return views.get(position);
	}

	private void goHome() {
		// 跳转
		Intent intent = new Intent(activity, LoginActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}

	// 设置已经引导过了，下次启动不用再次引导
	private void setGuided() {
		SharedPreferences preferences = activity.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// 存入数据
		editor.putBoolean("isFirstIn", false);
		// 提交修改
		editor.commit();
	}

	// 判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
