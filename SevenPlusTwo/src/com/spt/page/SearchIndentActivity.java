package com.spt.page;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.spt.adapter.TagAdapter;
import com.spt.controler.FlowTagLayout;
import com.spt.interfac.OnTagSelectListener;
import com.spt.sht.R;
import com.spt.utils.MyUtil;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 订单查询
 * 
 * @author lihongxuan
 *
 */
public class SearchIndentActivity extends BaseActivity implements OnClickListener {

	private TextView tv_search_indent_starttime, tv_search_indent_endtime;
	private ImageView iv_search_indent_back;
	private EditText et_search_indent;
	private FlowTagLayout fl_search_indent_state;
	private Button btn_search_indent;
	private TagAdapter mStateAdapter;
	private List<Object> stateData;
	private String indentStatus = "";
	private long startTime, endTime;

	private int iYear;
	private int iMonth;
	private int iDay;
	private Calendar calenderNow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_search_indent);
		super.onCreate(savedInstanceState);

		initData();

		initStateData();

		getNowTime();

	}

	private void getNowTime() {
		calenderNow = Calendar.getInstance(Locale.CHINA);
		Date date = new Date();
		calenderNow.setTime(date);
		iYear = calenderNow.get(Calendar.YEAR);
		iMonth = calenderNow.get(Calendar.MONTH);
		iDay = calenderNow.get(Calendar.DATE);
	}

	private void initData() {

		mStateAdapter = new TagAdapter(SearchIndentActivity.this);
		fl_search_indent_state.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
		fl_search_indent_state.setAdapter(mStateAdapter);

	}

	private void initStateData() {
		stateData = new ArrayList<Object>();
		stateData.add("不限");
		stateData.add("待付款");
		stateData.add("待发货");
		stateData.add("已发货");
		stateData.add("已完成");
		stateData.add("已取消");
		stateData.add("已退款");
		mStateAdapter.onlyAddAll(stateData);
	}

	@Override
	protected void init() {
		tv_search_indent_starttime = (TextView) findViewById(R.id.tv_search_indent_starttime);
		tv_search_indent_endtime = (TextView) findViewById(R.id.tv_search_indent_endtime);
		iv_search_indent_back = (ImageView) findViewById(R.id.iv_search_indent_back);
		et_search_indent = (EditText) findViewById(R.id.et_search_indent);
		fl_search_indent_state = (FlowTagLayout) findViewById(R.id.fl_search_indent_state);
		btn_search_indent = (Button) findViewById(R.id.btn_search_indent);
	}

	@Override
	protected void addClickEvent() {
		iv_search_indent_back.setOnClickListener(this);
		tv_search_indent_starttime.setOnClickListener(this);
		tv_search_indent_endtime.setOnClickListener(this);
		btn_search_indent.setOnClickListener(this);
		fl_search_indent_state.setOnTagSelectListener(new OnTagSelectListener() {

			@Override
			public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
				for (Integer integer : selectedList) {
					if (stateData.get(integer).equals("不限")) {
						indentStatus = "";
					} else if (stateData.get(integer).equals("待付款")) {
						indentStatus = "11";
					} else if (stateData.get(integer).equals("待发货")) {
						indentStatus = "20";
					} else if (stateData.get(integer).equals("已发货")) {
						indentStatus = "30";
					} else if (stateData.get(integer).equals("已完成")) {
						indentStatus = "40";
					} else if (stateData.get(integer).equals("已取消")) {
						indentStatus = "0";
					} else if (stateData.get(integer).equals("已退货")) {
						indentStatus = "50";
					}
				}
				if (selectedList.size() == 0) {
					indentStatus = "";
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_search_indent_back:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			SearchIndentActivity.this.finish();
			break;
		case R.id.tv_search_indent_starttime:
			DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					String month = "";
					if ((monthOfYear + 1) < 10) {
						month = "0" + String.valueOf((monthOfYear + 1));
					} else {
						month = String.valueOf((monthOfYear + 1));
					}
					String day = "";
					if ((dayOfMonth + 1) < 10) {
						day = "0" + String.valueOf((dayOfMonth));
					} else {
						day = String.valueOf((dayOfMonth));
					}

					tv_search_indent_starttime.setText(year + "-" + month + "-" + day);
					tv_search_indent_starttime.setTextColor(Color.BLACK);
					startTime = MyUtil
							.strToMilliseconds(year + "-" + month + "-" + String.valueOf(Integer.parseInt(day) + 1));
				}
			}, iYear, iMonth, iDay);
			dpd.show();
			break;
		case R.id.tv_search_indent_endtime:
			DatePickerDialog dpd1 = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
					String month = "";
					if ((monthOfYear + 1) < 10) {
						month = "0" + String.valueOf((monthOfYear + 1));
					} else {
						month = String.valueOf((monthOfYear + 1));
					}
					String day = "";
					if ((dayOfMonth + 1) < 10) {
						day = "0" + String.valueOf((dayOfMonth));
					} else {
						day = String.valueOf((dayOfMonth));
					}

					tv_search_indent_endtime.setText(year + "-" + month + "-" + day);
					tv_search_indent_endtime.setTextColor(Color.BLACK);
					endTime = MyUtil
							.strToMilliseconds(year + "-" + month + "-" + String.valueOf(Integer.parseInt(day) + 1));
				}
			}, iYear, iMonth, iDay);
			dpd1.show();
			break;
		case R.id.btn_search_indent:

			Intent intent = new Intent(SearchIndentActivity.this, DistributionActivity.class);
			intent.putExtra("page", 3);
			intent.putExtra("tag", 1);
			intent.putExtra("order_sn", et_search_indent.getText().toString());
			intent.putExtra("status", indentStatus);
			intent.putExtra("add_time_from", startTime);
			intent.putExtra("add_time_to", endTime);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

}
