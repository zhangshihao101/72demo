package com.spt.page;

import com.spt.sht.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MtsMsgDetailActivity extends FragmentActivity {

	private TextView tv_notice_title, tv_notice_time, tv_notice_content;
	private ImageView iv_mts_msg_back;
	private String title, time, content;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mts_msg_detail);
		super.onCreate(arg0);

		initView();

		initData();

		initListener();

	}

	private void initListener() {
		iv_mts_msg_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initData() {

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		time = intent.getStringExtra("time");
		content = intent.getStringExtra("content");

		tv_notice_title.setText(title);
		tv_notice_time.setText(time);
		tv_notice_content.setText(content);

	}

	private void initView() {
		tv_notice_title = (TextView) findViewById(R.id.tv_notice_title);
		tv_notice_time = (TextView) findViewById(R.id.tv_notice_time);
		tv_notice_content = (TextView) findViewById(R.id.tv_notice_content);
		iv_mts_msg_back = (ImageView) findViewById(R.id.iv_mts_msg_back);
		TextPaint tp = tv_notice_title.getPaint();
		tp.setFakeBoldText(true);
	}

}
