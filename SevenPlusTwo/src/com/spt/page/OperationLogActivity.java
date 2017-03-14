package com.spt.page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.adapter.LogAdapter;
import com.spt.bean.LogInfo;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class OperationLogActivity extends BaseActivity {

	private MyTitleBar mtbLog;
	private TextView tvTitle;
	private ImageView ivLeft;
	private ListView lvLogContent;
	private String log;
	private LinearLayout llLeft;
	private LinearLayout llRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.operationlog);
		super.onCreate(savedInstanceState);
		log = getIntent().getStringExtra("log");
		try {
			setLogContent();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtbLog = (MyTitleBar) findViewById(R.id.mtb_log_title);
		this.tvTitle = mtbLog.getTvTitle();
		this.ivLeft = mtbLog.getIvLeft();
		this.tvTitle.setText("操作日志");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbLog.getLlLeft();
		this.llRight = mtbLog.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.lvLogContent = (ListView) findViewById(R.id.lv_log_log);
	}

	/**
	 * 初始化
	 * */
	private void setLogContent() throws JSONException {

		JSONTokener jasonParser = new JSONTokener(log);
		JSONArray array = (JSONArray) jasonParser.nextValue();
		int length = array.length();
		LogAdapter la = new LogAdapter(OperationLogActivity.this);
		for (int i = 0; i < length; i++) {
			LogInfo info = new LogInfo();
			JSONObject obj = array.getJSONObject(i);
			String operator = obj.getString("operator");
			String remark = obj.getString("remark");
			String log_time = obj.getString("log_time");
			String order_status = obj.getString("order_status");
			String changed_status = obj.getString("changed_status");

			info.setOperator(operator);
			info.setRemark(remark);
			info.setLog_time(log_time);
			info.setOrder_status(order_status);
			info.setChanged_status(changed_status);
			la.addLogInfo(info);
		}

		lvLogContent.setAdapter(la);
	}

}
