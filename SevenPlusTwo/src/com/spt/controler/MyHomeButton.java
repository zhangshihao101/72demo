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
 * 自定义Btton<br>
 * 
 * */
public class MyHomeButton extends LinearLayout {
	private ImageView ivLeft;
	private TextView tvRight;
	private LinearLayout llContent;
	private boolean isGreen = false;

	public MyHomeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.homebuttonitem, this);
		ivLeft = (ImageView) view.findViewById(R.id.iv_homeButton_img);
		tvRight = (TextView) view.findViewById(R.id.tv_homeButton_text);
		llContent = (LinearLayout) view.findViewById(R.id.ll_homeButton_content);
	}

	public void setButtonImg(int iamgeResourceId) {
		ivLeft.setImageResource(iamgeResourceId);
	}

	public void setButtonText(String text) {
		tvRight.setText(text);
	}
	
	public String getButtonText() {
		String text = tvRight.getText().toString();
		return text;
	}

	public void setButtonTextColor(int color) {
		tvRight.setTextColor(color);
	}

	public void setButtonBackground(int iamgeResourceId) {
		llContent.setBackgroundResource(iamgeResourceId);
	}

	public boolean isGreen() {
		return isGreen;
	}

	public void setGreen(boolean isGreen) {
		this.isGreen = isGreen;
	}

}
