package com.spt.page;

import com.spt.fragment.SevendayOrdersFragment;
import com.spt.fragment.ThirtydayOrdersFragment;
import com.spt.fragment.TodayOrdersFragment;
import com.spt.fragment.YesterdayOrdersFragment;
import com.spt.sht.R;

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

public class MtsChannelOrdersActivity extends FragmentActivity {

	private ImageView iv_mts_back;
	private RadioGroup rgp_days;
	private RadioButton rbtn_today, rbtn_yestoday, rbtn_sevenday, rbtn_thirtyday;

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	private TodayOrdersFragment todayFra;
	private YesterdayOrdersFragment yesterdayFra;
	private SevendayOrdersFragment sevenFra;
	private ThirtydayOrdersFragment thirtyFra;
	
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mts_channal_order);
		super.onCreate(arg0);

		fragmentManager = getSupportFragmentManager();
		setTabSelection(0);

		initView();

		iv_mts_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rgp_days.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rbtn_today.getId()) {
					setTabSelection(0);
					rbtn_today.setTextColor(0xffffffff);
					rbtn_yestoday.setTextColor(0xff319ce1);
					rbtn_sevenday.setTextColor(0xff319ce1);
					rbtn_thirtyday.setTextColor(0xff319ce1);
				} else if (checkedId == rbtn_yestoday.getId()) {
					setTabSelection(1);
					rbtn_today.setTextColor(0xff319ce1);
					rbtn_yestoday.setTextColor(0xffffffff);
					rbtn_sevenday.setTextColor(0xff319ce1);
					rbtn_thirtyday.setTextColor(0xff319ce1);
				} else if (checkedId == rbtn_sevenday.getId()) {
					setTabSelection(2);
					rbtn_today.setTextColor(0xff319ce1);
					rbtn_yestoday.setTextColor(0xff319ce1);
					rbtn_sevenday.setTextColor(0xffffffff);
					rbtn_thirtyday.setTextColor(0xff319ce1);
				} else if (checkedId == rbtn_thirtyday.getId()) {
					setTabSelection(3);
					rbtn_today.setTextColor(0xff319ce1);
					rbtn_yestoday.setTextColor(0xff319ce1);
					rbtn_sevenday.setTextColor(0xff319ce1);
					rbtn_thirtyday.setTextColor(0xffffffff);
				}
			}
		});

	}

	private void initView() {
		iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
		rgp_days = (RadioGroup) findViewById(R.id.rgp_days);
		rbtn_today = (RadioButton) findViewById(R.id.rbtn_today);
		rbtn_yestoday = (RadioButton) findViewById(R.id.rbtn_yestoday);
		rbtn_sevenday = (RadioButton) findViewById(R.id.rbtn_sevenday);
		rbtn_thirtyday = (RadioButton) findViewById(R.id.rbtn_thirtyday);
	}

	/**
	 * 切换fragment且保存数据的方法
	 * 
	 * @param index
	 */
	private void setTabSelection(int index) {
		fragmentTransaction = fragmentManager.beginTransaction();
		hideFragments(fragmentTransaction);
		switch (index) {
		case 0:
			if (todayFra == null) {
				todayFra = new TodayOrdersFragment();
				fragmentTransaction.add(R.id.fl_frame_chart, todayFra, "todayF");
			} else {
				fragmentTransaction.show(todayFra);
			}
			break;

		case 1:
			if (yesterdayFra == null) {
				yesterdayFra = new YesterdayOrdersFragment();
				fragmentTransaction.add(R.id.fl_frame_chart, yesterdayFra, "yesterdayF");
			} else {
				fragmentTransaction.show(yesterdayFra);
			}
			break;
		case 2:
			if (sevenFra == null) {
				sevenFra = new SevendayOrdersFragment();
				fragmentTransaction.add(R.id.fl_frame_chart, sevenFra, "sevenF");
			} else {
				fragmentTransaction.show(sevenFra);
			}
			break;
		case 3:
			if (thirtyFra == null) {
				thirtyFra = new ThirtydayOrdersFragment();
				fragmentTransaction.add(R.id.fl_frame_chart, thirtyFra, "thirtyF");
			} else {
				fragmentTransaction.show(thirtyFra);
			}
			break;	
		}
		fragmentTransaction.commit();
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (todayFra != null) {
			transaction.hide(todayFra);
		}
		if (yesterdayFra != null) {
			transaction.hide(yesterdayFra);
		}
		if (sevenFra != null) {
			transaction.hide(sevenFra);
		}
		if (thirtyFra != null) {
			transaction.hide(thirtyFra);
		}
	}

}
