package com.spt.page;

import com.spt.fragment.MtsStkHighFragment;
import com.spt.fragment.MtsStkLowFragment;
import com.spt.sht.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MtsStkDetailActivity extends FragmentActivity {

	private TextView tv_mts_stk_screen, tv_mts_stk_search;
	private ImageView iv_mts_stk_back;
	private RadioGroup rg_mts_stk_detail;
	private RadioButton rb_high, rb_low;

	private FragmentManager manager;
	private FragmentTransaction transaction;
	private MtsStkHighFragment highFragment;
	private MtsStkLowFragment lowFragment;
//	private String facilityId;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mts_stk_detail);
		super.onCreate(inState);
		manager = this.getSupportFragmentManager();

		initView();
		
//		facilityId = getIntent().getStringExtra("facilityId");

		setTabSelection(0);

		initListener();

	}

	private void initListener() {
		rg_mts_stk_detail.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rb_high.getId()) {
					setTabSelection(0);
					rb_high.setTextColor(0xffffffff);
					rb_low.setTextColor(0xff319ce1);
				} else if (checkedId == rb_low.getId()) {
					setTabSelection(1);
					rb_low.setTextColor(0xffffffff);
					rb_high.setTextColor(0xff319ce1);
				}
			}
		});

		iv_mts_stk_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		tv_mts_stk_screen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MtsStkDetailActivity.this, MtsStkFilterActivity.class);
				startActivityForResult(intent, 0);
			}
		});

		tv_mts_stk_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MtsStkDetailActivity.this, MtsStkSearchActivity.class);
				startActivity(intent);
			}
		});

	}

	private void initView() {
		tv_mts_stk_search = (TextView) findViewById(R.id.tv_mts_stk_search);
		tv_mts_stk_screen = (TextView) findViewById(R.id.tv_mts_stk_screen);
		iv_mts_stk_back = (ImageView) findViewById(R.id.iv_mts_stk_back);
		rg_mts_stk_detail = (RadioGroup) findViewById(R.id.rg_mts_stk_detail);
		rb_high = (RadioButton) findViewById(R.id.rb_high);
		rb_low = (RadioButton) findViewById(R.id.rb_low);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		highFragment = (MtsStkHighFragment) manager.findFragmentByTag("hFrg");
		highFragment.onActivityResult(requestCode, resultCode, data);

		lowFragment = (MtsStkLowFragment) manager.findFragmentByTag("lFrg");
		lowFragment.onActivityResult(requestCode, resultCode, data);

	}

	/**
	 * 切换两个fragment且保存数据的方法
	 * 
	 * @param index
	 */
	private void setTabSelection(int index) {
		transaction = manager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
			if (highFragment == null && lowFragment == null) {
				highFragment = new MtsStkHighFragment();
				lowFragment = new MtsStkLowFragment();
				transaction.add(R.id.fl_mts_stk_detail, highFragment, "hFrg");
				transaction.add(R.id.fl_mts_stk_detail, lowFragment, "lFrg");
//				Bundle bundle = new Bundle();
//				bundle.putString("facilityId", facilityId);
//				highFragment.setArguments(bundle);
				transaction.hide(lowFragment);
			} else {
				transaction.show(highFragment);
				transaction.hide(lowFragment);
			}
			break;

		case 1:
			if (lowFragment == null) {
				lowFragment = new MtsStkLowFragment();
				transaction.add(R.id.fl_mts_stk_detail, lowFragment, "lFrg");
//				Bundle bundle = new Bundle();
//				bundle.putString("facilityId", facilityId);
//				lowFragment.setArguments(bundle);
			} else {
				transaction.show(lowFragment);
			}
			break;
		}
		transaction.commit();
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (highFragment != null) {
			transaction.hide(highFragment);
		}
		if (lowFragment != null) {
			transaction.hide(lowFragment);
		}
	}

}
