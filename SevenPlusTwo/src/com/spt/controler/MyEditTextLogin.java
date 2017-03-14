package com.spt.controler;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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
public class MyEditTextLogin extends LinearLayout {

	private ImageView ivImg;
	private EditText etContent;
	private ImageView ivClear;
	private LinearLayout llClear;

	public MyEditTextLogin(Context context) {
		super(context);
	}

	public MyEditTextLogin(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.myedittextlogin, this);
		ivImg = (ImageView) view.findViewById(R.id.iv_myEditTextLogin_img);
		etContent = (EditText) view.findViewById(R.id.et_myEditTextLogin_content);
		ivClear = (ImageView) view.findViewById(R.id.iv_myEditTextLogin_clear);
		llClear = (LinearLayout) view.findViewById(R.id.ll_myEditTextLogin_clear);

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

	/**
	 * 设置图片
	 * */
	public void setMyEditImg(int resId) {
		ivImg.setImageResource(resId);
	}

	/**
	 * 设置输入方式为密码
	 * */
	public void setMyEditPassWord() {
		etContent.setTransformationMethod(PasswordTransformationMethod.getInstance());
	}
	
	/**
	 * 设置输入内容
	 * */
	public void setMyEditContent(String str) {
		etContent.setText(str);
	}
	
	/**
	 * 设置输入内容
	 * */
	public void setContentMaxLength(int length) {
		etContent.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)});
	}

}
