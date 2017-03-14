package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.MyDate;
import com.mts.pos.Common.SomeMethod;
import com.mts.pos.Common.Urls;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主页购物车商品列表中，修改数量和价格的Activity 可以用对话框代替
 */
public class ChangeNumberActivity extends BaseActivity {
	TextView yes = null;
	TextView no = null;
	TextView title = null;
	EditText num = null;
	Intent it;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.change_num);
		super.onCreate(savedInstanceState);
		yes = (TextView) findViewById(R.id.yes);
		no = (TextView) findViewById(R.id.no);
		title = (TextView) findViewById(R.id.title);
		num = (EditText) findViewById(R.id.num);
		it = getIntent();
		// 为了区别是修改价格还是修改数量，修改数量只能输入int类型
		if (it.getStringExtra("type").equals("number")) {
			title.setText("修改数量：");
			num.setHint("请输入数量");
			num.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
			num.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (it.getStringExtra("type").equals("clientNum")) {
			title.setText("修改客流量：");
			num.setHint("请输入客流量");
			num.setFilters(new InputFilter[] { new InputFilter.LengthFilter(3) });
			num.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else if (it.getStringExtra("type").equals("clientMark")) {
			title.setText("添加备注：");
			num.setHint("请输入备注");

		} else {
			title.setText("修改价格：");
			num.setHint("请输入价格");
			// num.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		// 修改完之后，刷新购物车列表，更新总计的金额
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.putExtra("ifelse", "yes");
				// setResult(1, intent);
				// finish();
				if (it.getStringExtra("type").equals("number")) {
					if ("".equals(String.valueOf(num.getText()))) {
						Toast.makeText(ChangeNumberActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
					} else if ("0".equals(String.valueOf(num.getText()))) {
						Toast.makeText(ChangeNumberActivity.this, "输入不能为0", Toast.LENGTH_SHORT).show();
					} else if (Integer.valueOf(num.getText().toString()) > PayActivity.productData
							.get(Integer.valueOf(it.getStringExtra("position"))).getProduct_salecount()) {
						Log.e("LOOK", "最大库存在选择数量==" + PayActivity.productData
								.get(Integer.valueOf(it.getStringExtra("position"))).getProduct_salecount());
						Toast.makeText(ChangeNumberActivity.this, "输入数量不能大于库存", Toast.LENGTH_SHORT).show();
					} else {
						PayActivity.productData.get(Integer.valueOf(it.getStringExtra("position")))
								.setProduct_count(Integer.valueOf(String.valueOf(num.getText())));
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
				} else if (it.getStringExtra("type").equals("clientNum")) {
					if ("".equals(String.valueOf(num.getText()))) {
						Toast.makeText(ChangeNumberActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
					} else {
						ClientActivity.clientData.get(Integer.valueOf(it.getStringExtra("position")))
								.setPeople(String.valueOf(num.getText()));
						ClientActivity.clientadapter.notifyDataSetChanged();

						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
						nameValuePair.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(ChangeNumberActivity.this, "externalloginkey")));
						nameValuePair.add(new BasicNameValuePair("createdDate", MyDate.getDate()));
						nameValuePair.add(new BasicNameValuePair("productStoreId",
								Localxml.search(ChangeNumberActivity.this, "storeid")));
						nameValuePair.add(new BasicNameValuePair("timeSlot", it.getStringExtra("time")));
						nameValuePair.add(new BasicNameValuePair("weatherInfo", it.getStringExtra("weather")));
						nameValuePair.add(new BasicNameValuePair("numberOfPeople", num.getText().toString()));
						nameValuePair.add(new BasicNameValuePair("commentText", it.getStringExtra("remark")));

						getTask(ChangeNumberActivity.this, Urls.base + Urls.statistic_client, nameValuePair, "0");

						finish();
					}
				} else if (it.getStringExtra("type").equals("clientMark")) {
					if ("".equals(String.valueOf(num.getText()))) {
						Toast.makeText(ChangeNumberActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
					} else {
						ClientActivity.clientData.get(Integer.valueOf(it.getStringExtra("position")))
								.setRemark(String.valueOf(num.getText()));
						ClientActivity.clientadapter.notifyDataSetChanged();

						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
						nameValuePair.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(ChangeNumberActivity.this, "externalloginkey")));
						nameValuePair.add(new BasicNameValuePair("createdDate", MyDate.getDate()));
						nameValuePair.add(new BasicNameValuePair("productStoreId",
								Localxml.search(ChangeNumberActivity.this, "storeid")));
						nameValuePair.add(new BasicNameValuePair("timeSlot", it.getStringExtra("time")));
						nameValuePair.add(new BasicNameValuePair("weatherInfo", it.getStringExtra("weather")));
						nameValuePair.add(new BasicNameValuePair("numberOfPeople", it.getStringExtra("people")));
						nameValuePair.add(new BasicNameValuePair("commentText", num.getText().toString()));

						getTask(ChangeNumberActivity.this, Urls.base + Urls.statistic_client, nameValuePair, "0");

						finish();
					}
				} else {
					if ("".equals(String.valueOf(num.getText()))) {
						Toast.makeText(ChangeNumberActivity.this, "输入不能为空", Toast.LENGTH_SHORT).show();
					} else if ("0".equals(String.valueOf(num.getText()))) {
						Toast.makeText(ChangeNumberActivity.this, "输入不能为0", Toast.LENGTH_SHORT).show();
					} else {
						PayActivity.productData.get(Integer.valueOf(it.getStringExtra("position")))
								.setPresent_cost(Double.valueOf(String.valueOf(num.getText())));
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
						// PayActivity.total_money
						// .setText("总计：￥ " +
						// String.valueOf(SomeMethod.get2Double(PayActivity.total)));
						finish();
					}
				}
			}
		});
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent intent = new Intent();
				// intent.putExtra("ifelse", "no");
				// setResult(1, intent);
				// finish();
				finish();
			}
		});
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "保存客流量信息==" + result);
			if (!result.equals("")) {
				Toast.makeText(ChangeNumberActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
		// MobclickAgent.onPageStart("ChangeNumberActivity");
	}

	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
		// MobclickAgent.onPageEnd("ChangeNumberActivity");
	}
}