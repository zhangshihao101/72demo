package com.spt.page;

import com.spt.sht.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PayMethodActivity extends BaseActivity implements OnClickListener {

	private ImageView iv_paymethod_back;
	private LinearLayout ll_paymethod_alipay, ll_paymethod_wechat, ll_paymethod_downline;
	private int tag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_paymethod);
		super.onCreate(savedInstanceState);

		tag = getIntent().getIntExtra("tag", 0);

	}

	@Override
	protected void init() {
		iv_paymethod_back = (ImageView) findViewById(R.id.iv_paymethod_back);
		ll_paymethod_alipay = (LinearLayout) findViewById(R.id.ll_paymethod_alipay);
		ll_paymethod_downline = (LinearLayout) findViewById(R.id.ll_paymethod_downline);
		ll_paymethod_wechat = (LinearLayout) findViewById(R.id.ll_paymethod_wechat);
	}

	@Override
	protected void addClickEvent() {
		iv_paymethod_back.setOnClickListener(this);
		ll_paymethod_alipay.setOnClickListener(this);
		ll_paymethod_wechat.setOnClickListener(this);
		ll_paymethod_downline.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_paymethod_back:
			PayMethodActivity.this.finish();
			break;
		case R.id.ll_paymethod_alipay:
			if (tag == 0) {
				Intent intent = new Intent(this, ConfirmOrderActivity.class);
				setResult(0, intent);
			} else if (tag == 1) {
				Intent intent = new Intent(this, DisOrderDetailActivity.class);
				intent.putExtra("payment", "1");
				setResult(1, intent);
			}
			finish();
			break;
		case R.id.ll_paymethod_wechat:
			if (tag == 0) {
				Intent intent2 = new Intent(this, ConfirmOrderActivity.class);
				setResult(1, intent2);
			} else if (tag == 1) {
				Intent intent = new Intent(this, DisOrderDetailActivity.class);
				intent.putExtra("payment", "23");
				setResult(2, intent);
			}
			finish();
			break;
		case R.id.ll_paymethod_downline:
			if (tag == 0) {
				Intent intent3 = new Intent(this, ConfirmOrderActivity.class);
				setResult(2, intent3);
			} else if (tag == 1) {
				Intent intent = new Intent(this, DisOrderDetailActivity.class);
				intent.putExtra("payment", "18");
				setResult(3, intent);
			}

			finish();
			break;
		default:
			break;
		}
	}

}
