package com.spt.page;

import java.util.HashMap;

import com.spt.controler.MyTitleBar;
import com.spt.sht.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 【付款通知单详情】页
 * */
public class BillHeadDetailActivity extends BaseActivity {
	private MyTitleBar mtbBillheadTitle;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Intent itFrom;
	private TextView tvBillheadNo;
	private TextView tvPayDate;
	private TextView tvPayBank;
	private TextView tvPayUser;
	private TextView tvPayNo;
	private TextView tvReceiveBank;
	private TextView tvReceiveUser;
	private TextView tvReceiveNo;
	private TextView tvSmall;
	private TextView tvDealpay;
	private TextView tvSum;
	private TextView tvSumbig;
	private TextView tvOther;
	private HashMap<String, String> map;
	private LinearLayout llLeft;
	private LinearLayout llRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.billheaddetail);
		super.onCreate(savedInstanceState);
		// 初始化内容
		initContent();
	}

	/**
	 * 初始化
	 * */
	@SuppressWarnings("unchecked")
	@Override
	protected void init() {
		this.mtbBillheadTitle = (MyTitleBar) findViewById(R.id.mtb_billHeadDetail_title);
		this.tvTitle = mtbBillheadTitle.getTvTitle();
		this.ivLeft = mtbBillheadTitle.getIvLeft();
		this.tvTitle.setText("付款通知单详情");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.itFrom = getIntent();
		this.map = (HashMap<String, String>) itFrom.getSerializableExtra("data");
		this.tvBillheadNo = (TextView) findViewById(R.id.tv_billheadDetail_billheadNo);
		this.tvPayDate = (TextView) findViewById(R.id.tv_billheadDetail_payDate);
		this.tvPayBank = (TextView) findViewById(R.id.tv_billheadDetail_payBank);
		this.tvPayUser = (TextView) findViewById(R.id.tv_billheadDetail_payUser);
		this.tvPayNo = (TextView) findViewById(R.id.tv_billheadDetail_payNo);
		this.tvReceiveBank = (TextView) findViewById(R.id.tv_billheadDetail_receiveBank);
		this.tvReceiveUser = (TextView) findViewById(R.id.tv_billheadDetail_receiveUser);
		this.tvReceiveNo = (TextView) findViewById(R.id.tv_billheadDetail_receiveNo);
		this.tvSmall = (TextView) findViewById(R.id.tv_billheadDetail_small);
		this.tvDealpay = (TextView) findViewById(R.id.tv_billheadDetail_dealpay);
		this.tvSum = (TextView) findViewById(R.id.tv_billheadDetail_sum);
		this.tvSumbig = (TextView) findViewById(R.id.tv_billheadDetail_sumbig);
		this.tvOther = (TextView) findViewById(R.id.tv_billheadDetail_other);
		this.llLeft = mtbBillheadTitle.getLlLeft();
		this.llRight = mtbBillheadTitle.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 初始化内容
	 * */
	private void initContent() {
		this.tvBillheadNo.setText(map.get("tv_billHeadItem_billHeadNo"));
		this.tvPayDate.setText(map.get("tv_billHeadItem_date"));
		this.tvPayBank.setText(map.get("mall_account_name"));
		this.tvPayUser.setText(map.get("mall_account_holder"));
		this.tvPayNo.setText(map.get("mall_account_num"));
		this.tvReceiveBank.setText(map.get("store_bank_name"));
		this.tvReceiveUser.setText(map.get("store_account_holder"));
		this.tvReceiveNo.setText(map.get("store_bank_account"));
		this.tvSmall.setText(map.get("order_pay"));
		this.tvDealpay.setText(map.get("poundage"));
		this.tvSum.setText(map.get("tv_billHeadItem_price"));
		this.tvSumbig.setText(map.get("total_cost_ext"));
		this.tvOther.setText(map.get("remark"));
	}
}
