package com.mts.pos.Activity;

import com.mts.pos.R;
import com.mts.pos.Common.BaseLeftMenuActivity;
import com.mts.pos.Fragment.AttributeFragment;
import com.mts.pos.Fragment.BitmapFragment;
import com.mts.pos.Fragment.CommonFragment;
import com.mts.pos.Fragment.RecommendFragment;
import com.mts.pos.R.id;
import com.mts.pos.listview.LeftMenuInfo;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public class SettingActivity extends BaseLeftMenuActivity implements OnClickListener {

	private TextView tv_setting_common;
	private RelativeLayout rl_setting_common;
	private Button btn_setting_menu;
	private CommonFragment commonfragment;
	private FragmentManager manager;
	private FragmentTransaction transaction;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
		mMenuDrawer.setContentView(R.layout.activity_setting);
		super.onCreate(inState);

		// 初始化控件
		initView();

		// 初始化fragment
		initFragment();

		// 初始化点击事件
		initListener();

	}

	private void initListener() {
		rl_setting_common.setOnClickListener(this);
		btn_setting_menu.setOnClickListener(this);
	}

	private void initFragment() {
		manager = getSupportFragmentManager();
		rl_setting_common.setBackgroundColor(getResources().getColor(R.color.setting_common));
		tv_setting_common.setTextColor(Color.WHITE);
		showFragment(1);
	}

	@SuppressLint("CommitTransaction")
	private void showFragment(int index) {
		transaction = manager.beginTransaction();
		hideFragment(transaction);
		switch (index) {
		case 1:
			// 如果fragment已经存在，则显示出来
			if (commonfragment != null) {
				transaction.show(commonfragment);
				// 否则第一次切换则添加fragment，注意添加后是会显示出来的，replace方法也是
			} else {
				commonfragment = new CommonFragment();
				transaction.add(R.id.fl_setting_detail, commonfragment);
			}
			break;
		default:
			break;
		}
		transaction.commit();
	}

	private void hideFragment(FragmentTransaction transaction) {
		if (commonfragment != null) {
			transaction.hide(commonfragment);
		}
	}

	private void initView() {
		tv_setting_common = (TextView) findViewById(R.id.tv_setting_common);
		rl_setting_common = (RelativeLayout) findViewById(R.id.rl_setting_common);
		btn_setting_menu = (Button) findViewById(R.id.btn_setting_menu);
	}

	/**
	 * 所有控件的点击事件
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_setting_common:
			showFragment(1);
			break;
		case R.id.btn_setting_menu:
			mMenuDrawer.openMenu();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onMenuItemClicked(int position, LeftMenuInfo item) {

	}

	@Override
	protected int getDragMode() {
		return MenuDrawer.MENU_DRAG_CONTENT;
	}

	@Override
	protected Position getDrawerPosition() {
		return Position.LEFT;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	// 禁止回退
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
