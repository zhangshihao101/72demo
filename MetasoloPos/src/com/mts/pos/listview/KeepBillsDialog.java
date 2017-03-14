package com.mts.pos.listview;

import com.mts.pos.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class KeepBillsDialog extends Dialog {

	private Context context;
	private String title1;
	private String title2;
	private String confirmButton;
	private String cacelButton;
	private ClickListenerInterface clickListenerInterface;

	public interface ClickListenerInterface {

		public void doConfirm();

		public void doCancel();
	}

	public KeepBillsDialog(Context context, String title1, String title2, String confirmButton, String cacelButton) {
		super(context, R.style.Dialog);
		this.context = context;
		this.title1 = title1;
		this.title2 = title2;
		this.confirmButton = confirmButton;
		this.cacelButton = cacelButton;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		init();
	}

	public void init() {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.add_cart_from_keepbills, null);
		setContentView(view);

		TextView textview1 = (TextView) view.findViewById(R.id.tv_text1);
		TextView textview2 = (TextView) view.findViewById(R.id.tv_text2);
		Button confirm = (Button) view.findViewById(R.id.btn_cart_confirm);
		Button cancel = (Button) view.findViewById(R.id.btn_cart_cancel);

		textview1.setText(title1);
		textview2.setText(title2);
		confirm.setText(confirmButton);
		cancel.setText(cacelButton);

		confirm.setOnClickListener(new clickListener());
		cancel.setOnClickListener(new clickListener());

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
		lp.width = (int) (d.widthPixels * 1);
		lp.height = (int) (d.heightPixels * 0.9);
		dialogWindow.setAttributes(lp);

		// WindowManager m = getWindowManager();
		// Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
		// WindowManager.LayoutParams p = getWindow().getAttributes(); //
		// 获取对话框当前的参数值
		// p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
		// p.width = (int) (d.getWidth() * 0.65); // 宽度设置为屏幕的0.95
		// dialogWindow.setAttributes(p);
	}

	public void setClicklistener(ClickListenerInterface clickListenerInterface) {
		this.clickListenerInterface = clickListenerInterface;
	}

	private class clickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.btn_cart_confirm:
				clickListenerInterface.doConfirm();
				break;
			case R.id.btn_cart_cancel:
				clickListenerInterface.doCancel();
				break;
			}
		}

	}
}
