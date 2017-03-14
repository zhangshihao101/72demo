package com.mts.pos.Activity;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.SomeMethod;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

/**
 * 删除购物车商品列表页面Activity 可以用对话框代替
 */
public class DeleteProductItemActivity extends BaseActivity {
	TextView yes = null;
	TextView no = null;
	TextView message = null;
	Intent it;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.delete_product_item);
		super.onCreate(savedInstanceState);
		it = getIntent();
		yes = (TextView) findViewById(R.id.yes);
		no = (TextView) findViewById(R.id.no);
		message = (TextView) findViewById(R.id.message);
		if (it.getStringExtra("which").equals("1")) {
			message.setText("确定删除此商品吗？");
		} else if (it.getStringExtra("which").equals("10")) {
			message.setText("确定清空购物车吗？");
		} else {
			message.setText("确定清空会员信息吗？");
		}
		// 删除之后，更新购物车商品列表，更新数量
		yes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (it.getStringExtra("which").equals("1")) {
					PayActivity.productData
							.remove(PayActivity.productData.get(Integer.valueOf(it.getStringExtra("position"))));
					if (PayActivity.productData.size() == 0) {
						PayActivity.ll_promo.setVisibility(View.INVISIBLE);
						PayActivity.tv_blank.setVisibility(View.INVISIBLE);
						PayActivity.rl.setVisibility(View.VISIBLE);
						PayActivity.product_list.setVisibility(View.GONE);
						PayActivity.tv_total.setText("总计：￥0");
						if (PayActivity.promoTimer != null) {
							PayActivity.promoTimer.cancel();
							PayActivity.promoTimer = null;
						}

					}
					PayActivity.adapter.notifyDataSetChanged();

				} else if (it.getStringExtra("which").equals("10")) {
					PayActivity.productData.clear();
					PayActivity.adapter.notifyDataSetChanged();
					PayActivity.total = 0;
					Intent intent = new Intent();
					intent.putExtra("if", "yes");
					setResult(4, intent);
					PayActivity.ll_promo.setVisibility(View.INVISIBLE);
					PayActivity.tv_blank.setVisibility(View.INVISIBLE);
					PayActivity.promoTimer.cancel();
					PayActivity.promoTimer = null;
					PayActivity.tv_total.setText("总计：￥0");
					if (PayActivity.promoTimer != null) {
						PayActivity.promoTimer.cancel();
						PayActivity.promoTimer = null;
					}
					
					PayActivity.rl.setVisibility(View.VISIBLE);
					PayActivity.product_list.setVisibility(View.GONE);
				} else {
					// MemberFragment.memberNum = "_NA_";
					Intent intent = new Intent();
					intent.putExtra("if", "yes");
					setResult(3, intent);
				}
				// MobclickAgent.onEvent(DeleteProductItemActivity.this,
				// "DeleteProductItemActivity_yes");
				finish();
			}
		});
		no.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (it.getStringExtra("which").equals("1")) {

				} else if (it.getStringExtra("which").equals("10")) {
					Intent intent = new Intent();
					intent.putExtra("if", "no");
					setResult(4, intent);
				} else {
					Intent intent = new Intent();
					intent.putExtra("if", "no");
					setResult(3, intent);
				}
				finish();
				// MobclickAgent.onEvent(DeleteProductItemActivity.this,
				// "DeleteProductItemActivity_no");
			}
		});
	}

	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
		// MobclickAgent.onPageStart("DeleteProductItemActivity");
	}

	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
		// MobclickAgent.onPageEnd("DeleteProductItemActivity");
	}
}
