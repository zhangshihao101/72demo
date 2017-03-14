package com.spt.page;

import com.spt.fragment.MtsMsgNoticeFragment;
import com.spt.fragment.MtsMsgRemindFragment;
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

public class MtsMsgActivity extends FragmentActivity{

	private ImageView iv_mts_msg_back;
	private RadioGroup rgp_msg;
	private RadioButton rbtn_notice, rbtn_remind;
	
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private MtsMsgNoticeFragment noticeFragment;
	private MtsMsgRemindFragment remindFragment;
	
	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mts_msg);
		super.onCreate(arg0);
		
		initView();
		
		setTabSelection(0);
		
		initListener();
		
	}

	private void initListener() {
		iv_mts_msg_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		rgp_msg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rbtn_notice.getId()) {
					setTabSelection(0);
					rbtn_notice.setTextColor(0xffffffff);
					rbtn_remind.setTextColor(0xff319ce1);
				} else if (checkedId == rbtn_remind.getId()) {
					setTabSelection(1);
					rbtn_remind.setTextColor(0xffffffff);
					rbtn_notice.setTextColor(0xff319ce1);
				}
			}
		});
	}

	private void initView() {
		manager = getSupportFragmentManager();
		iv_mts_msg_back = (ImageView) findViewById(R.id.iv_mts_msg_back);
		rgp_msg = (RadioGroup) findViewById(R.id.rgp_msg);
		rbtn_notice = (RadioButton) findViewById(R.id.rbtn_notice);
		rbtn_remind = (RadioButton) findViewById(R.id.rbtn_remind);
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
			if (noticeFragment == null) {
				noticeFragment = new MtsMsgNoticeFragment();
				transaction.add(R.id.fl_mts_msg, noticeFragment, "notice");
			} else {
				transaction.show(noticeFragment);
			}
			break;

		case 1:
			if (remindFragment == null) {
				remindFragment = new MtsMsgRemindFragment();
				transaction.add(R.id.fl_mts_msg, remindFragment, "remind");
			} else {
				transaction.show(remindFragment);
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
		if (noticeFragment != null) {
			transaction.hide(noticeFragment);
		}
		if (remindFragment != null) {
			transaction.hide(remindFragment);
		}
	}
	
}
