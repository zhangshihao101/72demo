package com.spt.controler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spt.sht.R;

/**
 * 自定义TitleBar<br>
 * 
 * */
public class MyTitleBar extends LinearLayout {
	private ImageView ivLeft;
	private ImageView ivRight;
	private TextView tvTitle;
	private String tv_Title; // 标题文字
	private int left_drawable; // 左侧按钮图片id
	private int right_drawable; // 右侧按钮图片id
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private LinearLayout llRightText;
	private TextView tvRightText;

	public MyTitleBar(Context context) {
		super(context);
	}

	public MyTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.titlebar_item, this);
		this.ivLeft = (ImageView) view.findViewById(R.id.iv_titleBar_left);
		this.tvTitle = (TextView) view.findViewById(R.id.tv_titleBar_title);
		this.ivRight = (ImageView) view.findViewById(R.id.iv_titleBar_right);
		this.llLeft = (LinearLayout) view.findViewById(R.id.ll_titleBar_leftImage);
		this.llRight = (LinearLayout) view.findViewById(R.id.ll_titleBar_rightImage);
		this.ivLeft.setBackgroundResource(left_drawable);
		this.ivRight.setBackgroundResource(right_drawable);
		this.llRightText = (LinearLayout) view.findViewById(R.id.ll_titleBar_rightText);
		this.tvRightText = (TextView) view.findViewById(R.id.tv_titleBar_rightText);
	}

	/**
	 * 获取左侧图片
	 * */
	public ImageView getIvLeft() {
		return ivLeft;
	}

	/**
	 * 获取标题文字
	 * */
	public TextView getTvTitle() {
		return tvTitle;
	}

	/**
	 * 设置标题文字
	 * */
	public String getStrTitle() {
		return tv_Title;
	}

	/**
	 * 获取左侧图片
	 * */
	public void setleft_drawable(int left_drawable) {
		this.left_drawable = left_drawable;
	}

	/**
	 * 获取右侧图片
	 * */
	public ImageView getIvRight() {
		return ivRight;
	}

	/**
	 * 设置右侧图片
	 * */
	public void setRight_drawable(int right_drawable) {
		this.right_drawable = right_drawable;
	}

	/**
	 * 获取左侧图片按钮
	 * */
	public LinearLayout getLlLeft() {
		return llLeft;
	}

	/**
	 * 获取右侧图片按钮
	 * */
	public LinearLayout getLlRight() {
		return llRight;
	}

	/**
	 * 获取右侧文字按钮
	 * */
	public LinearLayout getLlRightText() {
		return llRightText;
	}

	/**
	 * 设置右侧文字
	 * */
	public void setRightText(String text) {
		this.tvRightText.setText(text);
	}

	/**
	 * 获取右侧文字
	 * */
	public String getRightText() {
		return this.tvRightText.getText().toString();
	}

}
