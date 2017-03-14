package com.spt.controler;

import android.content.res.ColorStateList;

public class RadioItemModel {
	public String text; // RadioButton的文字
	public boolean isChecked;// 是否选中
	public boolean hiddenRadio;// 是否需要隐藏radio
	public ColorStateList textColor;// 文字的不同状态的颜色

	public RadioItemModel(String text, boolean isChecked, boolean hiddenRadio, ColorStateList textColor) {
		this.text = text;
		this.isChecked = isChecked;
		this.hiddenRadio = hiddenRadio;
		this.textColor = textColor;
	}

	public RadioItemModel(String text) {
		this.isChecked = false;
		this.hiddenRadio = false;
		this.textColor = null;
		this.text = text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public boolean isChecked() {
		return isChecked;
	}
}
