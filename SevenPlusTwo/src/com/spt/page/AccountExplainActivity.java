package com.spt.page;

import com.spt.sht.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class AccountExplainActivity extends BaseActivity{
	
	private ImageView iv_account_explain_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_account_explain);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void init() {
		iv_account_explain_back = (ImageView) findViewById(R.id.iv_account_explain_back);
	}

	@Override
	protected void addClickEvent() {
		iv_account_explain_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
