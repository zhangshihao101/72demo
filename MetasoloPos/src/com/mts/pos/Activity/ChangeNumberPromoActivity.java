package com.mts.pos.Activity;

import java.text.DecimalFormat;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangeNumberPromoActivity extends BaseActivity {

	private EditText et_present_cost, et_promo;
	private Button btn_num_cancel, btn_num_confirm;
	private Intent intent;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(inState);
		setContentView(R.layout.change_number_promo);

		et_present_cost = (EditText) findViewById(R.id.et_present_cost);
		et_promo = (EditText) findViewById(R.id.et_promo);
		btn_num_cancel = (Button) findViewById(R.id.btn_num_cancel);
		btn_num_confirm = (Button) findViewById(R.id.btn_num_confirm);
		intent = getIntent();

		btn_num_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (String.valueOf(et_present_cost.getText()).equals("")) {
					Toast.makeText(ChangeNumberPromoActivity.this, "请输入价格", Toast.LENGTH_SHORT).show();
				} else if (String.valueOf(et_present_cost.getText()).equals("0")) {
					Toast.makeText(ChangeNumberPromoActivity.this, "输入不能为0", Toast.LENGTH_SHORT).show();
				} else {
					DecimalFormat df = new DecimalFormat("0.00");
					if (!et_promo.getText().toString().equals("")) {
						String promo = et_promo.getText().toString();
						Float percent = Float.valueOf(promo) / 100;
						PayActivity.productData.get(Integer.valueOf(intent.getStringExtra("position")))
								.setPresent_cost(Double.valueOf(
										df.format(Float.valueOf(et_present_cost.getText().toString()) * percent)));
						PayActivity.adapter.notifyDataSetChanged();
						PayActivity.total = 0;
						PayActivity.total_coast = 0.00;
						for (int i = 0; i < PayActivity.productData.size(); i++) {
							PayActivity.total += PayActivity.productData.get(i).getProduct_count();
							PayActivity.total_coast += PayActivity.productData.get(i).getPresent_cost()
									* PayActivity.productData.get(i).getProduct_count();
						}
						PayActivity.tv_total.setText("总计：" + PayActivity.total_coast);
						PayActivity.tv_account.setText("结    算（" + PayActivity.total + "）");
						finish();
					} else {
						PayActivity.productData.get(Integer.valueOf(intent.getStringExtra("position")))
								.setPresent_cost(Double.valueOf(String.valueOf(et_present_cost.getText())));
						PayActivity.adapter.notifyDataSetChanged();
						PayActivity.total = 0;
						PayActivity.total_coast = 0.00;
						for (int i = 0; i < PayActivity.productData.size(); i++) {
							PayActivity.total += PayActivity.productData.get(i).getProduct_count();
							PayActivity.total_coast += PayActivity.productData.get(i).getPresent_cost()
									* PayActivity.productData.get(i).getProduct_count();
						}
						PayActivity.tv_total.setText("总计：" + PayActivity.total_coast);
						PayActivity.tv_account.setText("结    算（" + PayActivity.total + "）");
						finish();
					}

				}

			}
		});

		btn_num_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
