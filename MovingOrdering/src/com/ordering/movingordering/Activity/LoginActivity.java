package com.ordering.movingordering.Activity;

import com.ordering.movingordering.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

@SuppressLint("NewApi")
public class LoginActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		super.onCreate(savedInstanceState);

	}
}
