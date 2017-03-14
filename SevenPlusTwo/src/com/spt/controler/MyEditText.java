package com.spt.controler;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.spt.sht.R;

/**
 * 自定义 带删除键的EditText
 * */
public class MyEditText extends LinearLayout {

	private EditText etContent;
	private ImageView ivClear;
	private LinearLayout llClear;

	public MyEditText(Context context) {
		super(context);
	}

	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.myedittext, this);
		etContent = (EditText) view.findViewById(R.id.et_myEditText_content);
		ivClear = (ImageView) view.findViewById(R.id.iv_myEditText_clear);
		llClear = (LinearLayout) view.findViewById(R.id.ll_myEditText_clear);

		llClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 点击清空输入内容
				etContent.setText("");
			}
		});

		etContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String strContent = etContent.getText().toString();
				// 当内容不为空时候clearButton可见，否则不可见
				if (strContent == null || "".equals(strContent)) {
					llClear.setVisibility(View.INVISIBLE);
					ivClear.setVisibility(View.INVISIBLE);
				} else {
					llClear.setVisibility(View.VISIBLE);
					ivClear.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * 获取输入内容
	 * */
	public String getMyEditText() {

		String str = etContent.getText().toString();

		return str;
	}

	/**
	 * 设置输入内容
	 * */
	public void setMyEditText(String str) {

		etContent.setText(str);

	}

	/**
	 * 设置输入类型
	 * */
	public void setMyEditInputType(int type) {
		etContent.setInputType(type);
	}

	/**
	 * 设置输入类型
	 * */
	public void setMyEditHint(String hint) {
		etContent.setHint(hint);
	}

}
