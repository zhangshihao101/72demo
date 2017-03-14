package com.mts.pos.Activity;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class PromoDetailActivity extends BaseActivity {

	private ListView lv_promo_detail;
	private Button btn_num_cancel, btn_num_confirm;
	private Intent intent;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_promo_detail);
		super.onCreate(inState);

		lv_promo_detail = (ListView) findViewById(R.id.lv_promo_detail);
		btn_num_cancel = (Button) findViewById(R.id.btn_num_cancel);
		btn_num_confirm = (Button) findViewById(R.id.btn_num_confirm);

		intent = getIntent();
		
	}

}
