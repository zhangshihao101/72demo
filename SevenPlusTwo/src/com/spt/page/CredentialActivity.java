package com.spt.page;

import com.spt.sht.R;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class CredentialActivity extends FragmentActivity {

	private RelativeLayout rl_credential_bg;
	private ImageView iv_credential;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_credential);
		super.onCreate(arg0);

		initView();

		String brandImgUrl = getIntent().getStringExtra("brandImgUrl");

		Picasso.with(this).load(brandImgUrl).into(iv_credential);

		initListener();

	}

	private void initView() {
		rl_credential_bg = (RelativeLayout) findViewById(R.id.rl_credential_bg);
		iv_credential = (ImageView) findViewById(R.id.iv_credential);
	}

	private void initListener() {
		rl_credential_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
