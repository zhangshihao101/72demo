package com.spt.page;

import com.spt.sht.R;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LegalCardActicvity extends FragmentActivity {

	private RelativeLayout rl_legal_card_bg;
	private ImageView iv_legal_card_one, iv_legal_card_two;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_legal_card);
		super.onCreate(arg0);

		initView();

		Intent intent = getIntent();

		String cardImgUrlOne = intent.getStringExtra("cardImgUrlOne");
		String cardImgUrlTwo = intent.getStringExtra("cardImgUrlTwo");
		Picasso.with(this).load(cardImgUrlOne).into(iv_legal_card_one);
		Picasso.with(this).load(cardImgUrlTwo).into(iv_legal_card_two);

		initListener();

	}

	private void initView() {
		rl_legal_card_bg = (RelativeLayout) findViewById(R.id.rl_legal_card_bg);
		iv_legal_card_one = (ImageView) findViewById(R.id.iv_legal_card_one);
		iv_legal_card_two = (ImageView) findViewById(R.id.iv_legal_card_two);
	}

	private void initListener() {
		rl_legal_card_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
