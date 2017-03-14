package com.spt.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.controler.MyTitleBar;

public class SetDialog extends Dialog {

	private MyTitleBar mtbTitle;
	private TextView tvTitle;
	private ImageView leftIv;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private LinearLayout llWhole;
	private RelativeLayout rlMyShop;
	private RelativeLayout rlChangePsw;
	private RelativeLayout rlCheckUpd;
	private RelativeLayout rlContactUs;
	private Button btnLogOff;
	private OnSetDialogListener mOnSetDialogListener;

	public SetDialog(Context context, OnSetDialogListener onSetDialogListener) {
		super(context, R.style.ShareDialog);
		this.mOnSetDialogListener = onSetDialogListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		init();
		addClickEvent();
	}

	// 回调接口
	public interface OnSetDialogListener {
		public void shopBack();

		public void pswBack();

		public void updBack();

		public void contackUsBack();

		public void logOffBack();
	};

	private void init() {
		SetDialog.this.mtbTitle = (MyTitleBar) findViewById(R.id.mtb_set_title);
		SetDialog.this.tvTitle = mtbTitle.getTvTitle();
		SetDialog.this.tvTitle.setText("设置");
		SetDialog.this.leftIv = mtbTitle.getIvLeft();
		SetDialog.this.llLeft = mtbTitle.getLlLeft();
		SetDialog.this.llRight = mtbTitle.getLlRight();
		SetDialog.this.llRight.setVisibility(View.VISIBLE);
		SetDialog.this.leftIv.setBackgroundResource(R.drawable.titlemenu);
		SetDialog.this.llWhole = (LinearLayout) findViewById(R.id.ll_set_wholeContent);
		SetDialog.this.rlMyShop = (RelativeLayout) findViewById(R.id.rl_set_myShop);
		SetDialog.this.rlChangePsw = (RelativeLayout) findViewById(R.id.rl_set_changePsw);
		SetDialog.this.rlCheckUpd = (RelativeLayout) findViewById(R.id.rl_set_checkUpd);
		SetDialog.this.rlContactUs = (RelativeLayout) findViewById(R.id.rl_set_contactUs);
		SetDialog.this.btnLogOff = (Button) findViewById(R.id.btn_set_logOff);
	}

	private void addClickEvent() {
		this.llLeft.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SetDialog.this.dismiss();
			}
		});

		this.llWhole.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SetDialog.this.dismiss();
			}
		});
		// 【我的店铺】
		this.rlMyShop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mOnSetDialogListener.shopBack();
				SetDialog.this.dismiss();
			}
		});
		// 【修改密码】
		this.rlChangePsw.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mOnSetDialogListener.pswBack();
				SetDialog.this.dismiss();
			}
		});
		// 【检查更新】
		this.rlCheckUpd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mOnSetDialogListener.updBack();
				SetDialog.this.dismiss();
			}
		});
		// 【联系我们】
		this.rlContactUs.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mOnSetDialogListener.contackUsBack();
				SetDialog.this.dismiss();
			}
		});
		// 【退出登录】
		this.btnLogOff.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mOnSetDialogListener.logOffBack();
				SetDialog.this.dismiss();
			}
		});
	}
}
