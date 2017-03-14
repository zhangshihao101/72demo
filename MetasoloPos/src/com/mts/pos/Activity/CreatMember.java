package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.NoDoubleClickUtils;
import com.mts.pos.Common.Urls;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Toast;

public class CreatMember extends BaseActivity {

	private EditText et_cra_name, et_cra_phone, et_cra_id, et_cra_remark, et_cra_keyword;
	private RadioGroup rg_cra_gender;
	private Spinner sp_cra_grade;
	private Button btn_cancel, btn_creat;

	private InputMethodManager imm;

	private String memberGender = "", memberGrade = "";
	private ArrayAdapter<String> grade_adapter;
	private List<String> grade_list = new ArrayList<String>();

	/**
	 * 电话号码正则匹配表达式（11位手机号码）^0\d{2,3}(\-)?\d{7,8}$
	 * (13[0-9]|15[0-9]|18[0-9])\\d{8}$
	 */
	public static final String TEL_CHECK = "^(13[0-9]|15[0-9]|18[0-9])\\d{8}$";

	Timer Timer = null;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_creatmember);
		super.onCreate(inState);

		initview();

		grade_list.add("等级一");
		grade_list.add("等级二");
		grade_list.add("等级三");
		grade_list.add("等级四");
		grade_list.add("等级五");

		grade_adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, grade_list);
		grade_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_cra_grade.setAdapter(grade_adapter);
		sp_cra_grade.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String ss = String.valueOf(position + 1);
				memberGrade = ss;

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		rg_cra_gender.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) CreatMember.this.findViewById(radioButtonId);
				if (rb.getText().toString().equals("男")) {
					memberGender = "M";
				} else if (rb.getText().toString().equals("女")) {
					memberGender = "L";
				}
			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_creat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!NoDoubleClickUtils.isDoubleClick()) {
					if (checkEdit()) {
						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
						nameValuePair.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(CreatMember.this, "externalloginkey")));
						nameValuePair.add(new BasicNameValuePair("userName", et_cra_name.getText().toString()));
						nameValuePair.add(new BasicNameValuePair("phoneNumber", et_cra_phone.getText().toString()));
						nameValuePair.add(new BasicNameValuePair("sex", memberGender));
						nameValuePair.add(new BasicNameValuePair("level", memberGrade));
						nameValuePair.add(new BasicNameValuePair("email", ""));
						nameValuePair.add(new BasicNameValuePair("vipNumber", et_cra_id.getText().toString()));
						nameValuePair.add(new BasicNameValuePair("keywords", et_cra_keyword.getText().toString()));
						nameValuePair.add(new BasicNameValuePair("remarks", et_cra_remark.getText().toString()));
						getTask(CreatMember.this, Urls.base + Urls.member_creat, nameValuePair, "0");
					}
				}

			}
		});
	}

	public void initview() {
		et_cra_name = (EditText) findViewById(R.id.et_cra_name);
		et_cra_phone = (EditText) findViewById(R.id.et_cra_phone);
		et_cra_id = (EditText) findViewById(R.id.et_cra_id);
		et_cra_remark = (EditText) findViewById(R.id.et_cra_remark);
		et_cra_keyword = (EditText) findViewById(R.id.et_cra_keyword);
		rg_cra_gender = (RadioGroup) findViewById(R.id.rg_cra_gender);
		sp_cra_grade = (Spinner) findViewById(R.id.sp_cra_grade);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_creat = (Button) findViewById(R.id.btn_creat);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(et_cra_name.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(et_cra_phone.getWindowToken(), 1);
		imm.hideSoftInputFromWindow(et_cra_id.getWindowToken(), 2);
		imm.hideSoftInputFromWindow(et_cra_remark.getWindowToken(), 3);
		imm.hideSoftInputFromWindow(et_cra_keyword.getWindowToken(), 4);
	}

	public boolean checkEdit() {
		if (et_cra_name.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(CreatMember.this, "会员名输入不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (et_cra_phone.getText().toString().replace(" ", "").equals("")) {
			Toast.makeText(CreatMember.this, "会员电话号码输入不能为空！", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!checkStringStyle(et_cra_phone.getText().toString().replace(" ", ""), TEL_CHECK)) {
			Toast.makeText(CreatMember.this, "请正确输入电话号码！", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("看看", "看看==" + result);
			try {
				String message = new JSONObject(result).optString("flag");
				if (message.equals("Y")) {
					Toast.makeText(CreatMember.this, "新增成功", Toast.LENGTH_SHORT).show();
					finish();
				} else if (message.equals("N")) {
					Toast.makeText(CreatMember.this, "新增失败", Toast.LENGTH_SHORT).show();
				} else if (message.equals("repeat")) {
					Toast.makeText(CreatMember.this, "电话号码已存在,请重新填写", Toast.LENGTH_SHORT).show();
					et_cra_phone.setText("");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	public static boolean checkStringStyle(String str, String style) {
		Pattern REGEX = Pattern.compile(style);
		// 非空判断
		if (null == str) {
			return false;
		}
		Matcher matcher = REGEX.matcher(str);
		return matcher.matches();
	}

}
