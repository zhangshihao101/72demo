package com.spt.page;

import com.spt.sht.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.ImageView;

public class StartActivity extends FragmentActivity {

	private ImageView iv_start;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		super.onCreate(arg0);

		iv_start = (ImageView) findViewById(R.id.iv_start);
		iv_start.setImageResource(R.drawable.ad_start);

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(StartActivity.this, LoginActivity.class));
				StartActivity.this.finish();
			}
		}, 3000);

	}

}
