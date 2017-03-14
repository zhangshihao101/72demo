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

public class BusinessLicenseActivity extends FragmentActivity {

	private RelativeLayout rl_business_license_bg;
	private ImageView iv_business_license;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_business_license);
		super.onCreate(arg0);

		initView();

		String licenseImgUrl = getIntent().getStringExtra("licenseImgUrl");

		Picasso.with(this).load(licenseImgUrl).into(iv_business_license);

		initListener();

	}

	private void initView() {
		rl_business_license_bg = (RelativeLayout) findViewById(R.id.rl_business_license_bg);
		iv_business_license = (ImageView) findViewById(R.id.iv_business_license);
	}

	private void initListener() {
		rl_business_license_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
