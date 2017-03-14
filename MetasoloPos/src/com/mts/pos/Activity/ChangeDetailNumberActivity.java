package com.mts.pos.Activity;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeDetailNumberActivity extends BaseActivity implements OnClickListener {

	private EditText et_detail_num;
	private TextView tv_detail_yes, tv_detail_no;
	private int stock;
	private String size;
	private String color;
	public final static int RESULT_CODE = 1;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.change_detail_number);
		super.onCreate(inState);

		// 初始化控件
		initView();
		// 初始化点击事件
		initListener();

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		stock = bundle.getInt("stock");
		color = bundle.getString("color");
		size = bundle.getString("size");
	}

	private void initView() {
		et_detail_num = (EditText) findViewById(R.id.et_detail_num);
		tv_detail_yes = (TextView) findViewById(R.id.tv_detail_yes);
		tv_detail_no = (TextView) findViewById(R.id.tv_detail_no);
	}

	private void initListener() {
		tv_detail_yes.setOnClickListener(this);
		tv_detail_no.setOnClickListener(this);
	}

	// 点击事件
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_detail_yes:
			if (TextUtils.isEmpty(color) && TextUtils.isEmpty(size)) {
				Toast.makeText(this, "请选择尺码和颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(color)) {
				Toast.makeText(this, "请选择颜色", Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(size)) {
				Toast.makeText(this, "请选择尺码", Toast.LENGTH_SHORT).show();
			} else if ("".equals(et_detail_num.getText().toString())) {
				Toast.makeText(this, "输入数量不能为空", Toast.LENGTH_SHORT).show();
			} else if ("0".equals(et_detail_num.getText().toString())) {
				Toast.makeText(this, "输入数量不能为0", Toast.LENGTH_SHORT).show();
			} else if (Integer.valueOf(et_detail_num.getText().toString()) > stock) {
				Toast.makeText(this, "输入数量不能大于库存", Toast.LENGTH_SHORT).show();
			} else {
				Intent intent2 = new Intent();
				intent2.putExtra("number", Integer.valueOf(et_detail_num.getText().toString()));
				setResult(RESULT_CODE, intent2);
				finish();
			}
			break;
		case R.id.tv_detail_no:
			this.finish();
			break;
		default:
			break;
		}
	}

}
