package com.mts.pos.Common;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;

import com.mts.pos.R;
import com.mts.pos.Activity.ClientActivity;
import com.mts.pos.Activity.GuideActivity;
import com.mts.pos.Activity.KeepbillsActivity;
import com.mts.pos.Activity.PayActivity;
import com.mts.pos.Activity.SaledorderformActivity;
import com.mts.pos.Activity.SettingActivity;
import com.mts.pos.listview.LeftMenuInfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public abstract class BaseLeftMenuActivity extends FragmentActivity implements LeftMenuAdapter.MenuListener {

	protected MenuDrawer mMenuDrawer;
	protected ListView mList;

	private int mActivePosition = 0;

	String[] category = { "收银界面", "收银界面", "导购界面", "客流量统计", "挂单记录", "销售记录", "设置", "源一云商" };
	int[] category_img = { R.drawable.pic_pay, R.drawable.pic_pay, R.drawable.pic_guide, R.drawable.pic_count,
			R.drawable.pic_keepbills, R.drawable.pic_salebills, R.drawable.pic_setting, R.drawable.pic_met };
	// int[] category_img = { R.drawable.pic_pay, R.drawable.pic_pay,
	// R.drawable.pic_guide, R.drawable.pic_count,
	// R.drawable.pic_setting, R.drawable.pic_met, R.drawable.pic_help };
	LeftMenuAdapter leftmenuAdapter;
	int last_choose = 1;// 记录上次点击的是listview那个item
	RelativeLayout loading = null;

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		List<LeftMenuInfo> data = new ArrayList<LeftMenuInfo>();
		for (int i = 0; i < category.length; i++) {
			LeftMenuInfo info = new LeftMenuInfo();
			info.setCategory(category[i]);
			info.setImg(category_img[i]);
			data.add(info);
		}
		mList = new ListView(this);
		mList.setDividerHeight(0);
		leftmenuAdapter = new LeftMenuAdapter(BaseLeftMenuActivity.this, data);
		leftmenuAdapter.setActivePosition(mActivePosition);
		mList.setAdapter(leftmenuAdapter);
		mList.setOnItemClickListener(mItemClickListener);
		mMenuDrawer.setMenuView(mList);
		mMenuDrawer.setDropShadowSize(0);// 设置侧滑菜单旁边的黑边的宽度
		mMenuDrawer.setMenuSize(dip2px(BaseLeftMenuActivity.this, 220));// 设置侧滑菜单的宽度，单位是px
		mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_BEZEL);// 设置滑动手势，屏幕任何位置滑动都行
		// mMenuDrawer.setSlideDrawable(R.drawable.ic_drawer);
		mMenuDrawer.setDrawerIndicatorEnabled(true);
		loading = (RelativeLayout) findViewById(R.id.loading);
	}

	protected abstract void onMenuItemClicked(int position, LeftMenuInfo item);

	protected abstract int getDragMode();

	protected abstract Position getDrawerPosition();

	private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mActivePosition = position;
			mMenuDrawer.setActiveView(view, position);
			leftmenuAdapter.setActivePosition(position);
			onMenuItemClicked(position, (LeftMenuInfo) leftmenuAdapter.getItem(position));
			mMenuDrawer.closeMenu();
			// final Intent intent = new Intent();
			// TODO
			switch (position) {
			case 1:
				Intent intent1 = new Intent(BaseLeftMenuActivity.this, PayActivity.class);
				startActivity(intent1);
				break;
			case 2:
				Intent intent = new Intent(BaseLeftMenuActivity.this, GuideActivity.class);
				startActivity(intent);
				break;
			case 3:
				Intent intent5 = new Intent(BaseLeftMenuActivity.this, ClientActivity.class);
				startActivity(intent5);
				break;
			case 4:
				// 挂单
				Intent intent3 = new Intent(BaseLeftMenuActivity.this, KeepbillsActivity.class);
				startActivity(intent3);
				break;
			case 5:
				// 销售记录
				Intent intent4 = new Intent(BaseLeftMenuActivity.this, SaledorderformActivity.class);
				startActivity(intent4);
				break;
			case 6:
				Intent intent2 = new Intent(BaseLeftMenuActivity.this, SettingActivity.class);
				startActivity(intent2);
				break;
			case 7:
				Uri uri = Uri.parse("https://192.168.1.18:8443");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(it);
				break;

			default:
				break;
			}
			// intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			// new Handler().postDelayed(new Runnable() {
			// @Override
			// public void run() {
			// startActivity(intent);
			// }
			// }, 350);
		}

	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.e("LOOK", "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		// outState.putInt(STATE_ACTIVE_POSITION, mActivePosition);
	}

	@Override
	public void onActiveViewChanged(View v) {
		Log.e("LOOK", "onActiveViewChanged");
		mMenuDrawer.setActiveView(v, mActivePosition);
	}

	/**
	 * 给子Activity调用新开一个异步线程
	 */
	public void getTask(Context context, String url, List<NameValuePair> nameValuePair, String which) {
		if (NetworkUtil.isConnected(BaseLeftMenuActivity.this)) {
			loading.setVisibility(View.VISIBLE);
			CommonTask commontask = new CommonTask(context, url, nameValuePair, which);
			commontask.execute("");
		} else {
			// loading.setVisibility(View.GONE);
			Toast.makeText(BaseLeftMenuActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_LONG).show();
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
			if (null == result || "".equals(result) || (Constants.not_found).equals(result)
					|| (Constants.time_out).equals(result)) {
				Toast.makeText(BaseLeftMenuActivity.this, "网络不好，请重试！", Toast.LENGTH_LONG).show();
				loading.setVisibility(View.GONE);
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 根据手机的分辨率从 dp转成为 px
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	@Override
	protected void onResume() {
		// mMenuDrawer.closeMenu();
		leftmenuAdapter.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// mMenuDrawer.closeMenu();
		super.onPause();
	}

}
