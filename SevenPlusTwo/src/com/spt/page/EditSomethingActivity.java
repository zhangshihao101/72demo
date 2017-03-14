package com.spt.page;

import com.spt.sht.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditSomethingActivity extends FragmentActivity {

	private ImageView iv_edit_back, iv_edit_finish, iv_clear;
	private TextView tv_todo;
	private EditText et_do_something;
	private String title, value;

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_something);
		super.onCreate(arg0);

		initViews();

		Intent intent = getIntent();
		title = intent.getStringExtra("title");
		if (!title.equals("修改个人简介")) {
			et_do_something.setSingleLine();
		} else {
			et_do_something.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
		}
		tv_todo.setText(title);
		value = intent.getStringExtra("value");
		et_do_something.setText(value);

		et_do_something.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (et_do_something.getText().toString().equals("")) {
					iv_clear.setVisibility(View.INVISIBLE);
				} else {
					iv_clear.setVisibility(View.VISIBLE);
				}

				if (et_do_something.getText().toString().length() >= 20) {
					Toast.makeText(EditSomethingActivity.this, "个人签名最多20字", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		iv_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_do_something.setText("");
			}
		});

		iv_edit_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm1 != null) {
					imm1.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				setResult(0);
				finish();
			}
		});

		iv_edit_finish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm1 != null) {
					imm1.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
				Intent intentR = new Intent();
				intentR.putExtra("editValue", et_do_something.getText().toString());
				setResult(1, intentR);
				finish();
			}
		});

	}

	private void initViews() {
		iv_edit_back = (ImageView) findViewById(R.id.iv_edit_back);
		iv_edit_finish = (ImageView) findViewById(R.id.iv_edit_finish);
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		tv_todo = (TextView) findViewById(R.id.tv_todo);
		et_do_something = (EditText) findViewById(R.id.et_do_something);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(0);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
