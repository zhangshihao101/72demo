package com.spt.page;

import java.util.ArrayList;
import java.util.List;

import com.spt.adapter.TagAdapter;
import com.spt.controler.FlowTagLayout;
import com.spt.interfac.OnTagSelectListener;
import com.spt.sht.R;
import com.umeng.socialize.utils.Log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MtsGoodsFilterActivity extends FragmentActivity {

	private ImageView iv_mts_back;
	private EditText et_storage, et_name, et_brand, et_barcode, et_style, et_classify;
	private FlowTagLayout fl_pay_state, fl_title_state;
	private Button btn_search;
	private RelativeLayout rl_classify;

	private TagAdapter payAdapter, titleAdapter;
	private List<Object> payStateData, titleStateData;

	private String storageId = "", goodsName = "", brandId = "", barcodeId = "", styleId = "", classifyId = "",
			stateId = "", titleId = "";

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mts_goods_filter);
		super.onCreate(inState);

		initViews();

		fl_pay_state.setOnTagSelectListener(new OnTagSelectListener() {

			@Override
			public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
				for (Integer integer : selectedList) {
					if (payStateData.get(integer).equals("全部")) {
						stateId = "active_all";
					} else if (payStateData.get(integer).equals("上架")) {
						stateId = "Y";
					} else if (payStateData.get(integer).equals("下架")) {
						stateId = "N";
					}
				}
			}
		});

		fl_title_state.setOnTagSelectListener(new OnTagSelectListener() {

			@Override
			public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
				for (Integer integer : selectedList) {
					if (titleStateData.get(integer).equals("全部")) {
						titleId = "assoctypeid_all";
					} else if (titleStateData.get(integer).equals("公开")) {
						titleId = "F";
					} else if (titleStateData.get(integer).equals("私有")) {
						titleId = "P";
					}

					// else if (titleStateData.get(integer).equals("私有")) {
					// titleId = "P";
					// } else if (titleStateData.get(integer).equals("引用公共")) {
					// titleId = "Q";
					// } else if (titleStateData.get(integer).equals("挂靠公共")) {
					// titleId = "A";
					// } else if (titleStateData.get(integer).equals("公开")) {
					// titleId = "F";
					// } else if (titleStateData.get(integer).equals("受保护公开")) {
					// titleId = "L";
					// }
				}
			}
		});

		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();

				intent.putExtra("storageId", et_storage.getText().toString());
				intent.putExtra("goodsName", et_name.getText().toString());
				intent.putExtra("brandId", et_brand.getText().toString());
				intent.putExtra("barcodeId", et_barcode.getText().toString());
				intent.putExtra("styleId", et_style.getText().toString());
				intent.putExtra("classifyId", classifyId);
				intent.putExtra("stateId", stateId);
				intent.putExtra("titleId", titleId);

				setResult(0, intent);
				finish();
			}
		});

		iv_mts_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				setResult(1);
				finish();
			}
		});

		// 分层筛选暂时隐藏

		rl_classify.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MtsGoodsFilterActivity.this, MtsGoodsClassifyActivity.class);
				startActivityForResult(intent, 500);
				// startActivity(intent);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 500) {
			if (resultCode == 501) {
				Bundle b = new Bundle();
				b = data.getExtras();
				et_classify.setText(b.getString("child"));
				classifyId = b.getString("childId");
			} else {

			}

		}
	}

	private void initViews() {
		iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
		et_storage = (EditText) findViewById(R.id.et_storage);
		et_name = (EditText) findViewById(R.id.et_name);
		et_brand = (EditText) findViewById(R.id.et_brand);
		et_barcode = (EditText) findViewById(R.id.et_barcode);
		et_style = (EditText) findViewById(R.id.et_style);
		et_classify = (EditText) findViewById(R.id.et_classify);
		fl_pay_state = (FlowTagLayout) findViewById(R.id.fl_pay_state);
		fl_title_state = (FlowTagLayout) findViewById(R.id.fl_title_state);
		btn_search = (Button) findViewById(R.id.btn_search);
		rl_classify = (RelativeLayout) findViewById(R.id.rl_classify);

		payAdapter = new TagAdapter(MtsGoodsFilterActivity.this);
		fl_pay_state.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
		fl_pay_state.setAdapter(payAdapter);

		titleAdapter = new TagAdapter(MtsGoodsFilterActivity.this);
		fl_title_state.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
		fl_title_state.setAdapter(titleAdapter);

		payStateData = new ArrayList<Object>();
		payStateData.add("全部");
		payStateData.add("上架");
		payStateData.add("下架");
		payAdapter.onlyAddAll(payStateData);

		titleStateData = new ArrayList<Object>();
		titleStateData.add("全部");
		titleStateData.add("私有");
		titleStateData.add("公开");
		// titleStateData.add("引用公共");
		// titleStateData.add("挂靠公共");
		// titleStateData.add("受保护公开");
		titleAdapter.onlyAddAll(titleStateData);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(1);
		}
		return super.onKeyDown(keyCode, event);
	}

}
