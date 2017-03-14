package com.mts.pos.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Activity.CreatMember;
import com.mts.pos.Activity.MemberListActivity;
import com.mts.pos.Common.BaseFragment;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;
import com.mts.pos.listview.SearchMemberInfo;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class MemerFragment extends BaseFragment implements OnClickListener {

	private View view;
	private Button btn_member_addNew;
	private EditText et_member_search;
	private ImageView iv_member_close;
	private TextView tv_member_name, tv_member_sex, tv_member_phone, tv_member_number, tv_member_grade,
			tv_member_keyword, tv_member_remark;
	private LinearLayout ll_member_add, ll_member_msg;
	private Context mContext;
	private SearchMemberInfo info;
	public static List<SearchMemberInfo> data;
	private ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		data = new ArrayList<SearchMemberInfo>();
		view = inflater.inflate(R.layout.fragment_member, null);

		// 初始化控件
		initView();

		initListener();

		return view;
	}
	
	/**
	 * 初始化点击事件
	 */
	private void initListener() {
		btn_member_addNew.setOnClickListener(this);
		iv_member_close.setOnClickListener(this);
		et_member_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (et_member_search.getText().toString().replace(" ", "").equals("")) {
					Toast.makeText(mContext, "请输入搜索内容！", Toast.LENGTH_SHORT).show();
				} else {
					List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
					nameValuePair.add(new BasicNameValuePair("searchByPartyStr", et_member_search.getText().toString()));
					nameValuePair.add(
							new BasicNameValuePair("externalLoginKey", Localxml.search(mContext, "externalloginkey")));
					getTask(mContext, Urls.base + Urls.search_member, nameValuePair, "0");
					dialog = new ProgressDialog(mContext);
					dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					dialog.setCancelable(true);
					dialog.setCanceledOnTouchOutside(false);
					dialog.setMessage("正在加载中......");
					dialog.show();
				}

				return true;
			}
		});
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		btn_member_addNew = (Button) view.findViewById(R.id.btn_member_addNew);
		et_member_search = (EditText) view.findViewById(R.id.et_member_search);
		iv_member_close = (ImageView) view.findViewById(R.id.iv_member_close);
		tv_member_name = (TextView) view.findViewById(R.id.tv_member_name);
		tv_member_sex = (TextView) view.findViewById(R.id.tv_member_sex);
		tv_member_phone = (TextView) view.findViewById(R.id.tv_member_phone);
		tv_member_number = (TextView) view.findViewById(R.id.tv_member_number);
		tv_member_grade = (TextView) view.findViewById(R.id.tv_member_grade);
		tv_member_keyword = (TextView) view.findViewById(R.id.tv_member_keyword);
		tv_member_remark = (TextView) view.findViewById(R.id.tv_member_remark);
		ll_member_add = (LinearLayout) view.findViewById(R.id.ll_member_add);
		ll_member_msg = (LinearLayout) view.findViewById(R.id.ll_member_msg);
	}

	/**
	 * 点击事件
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_member_addNew:
			Intent intent = new Intent(mContext, CreatMember.class);
			startActivity(intent);
			break;
		case R.id.iv_member_close:
			et_member_search.setText("");
			if (ll_member_msg.getVisibility() == View.VISIBLE) {
				ll_member_msg.setVisibility(View.GONE);
				ll_member_add.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			try {
				Log.e("会员数据", result);
				JSONArray array = new JSONObject(result).optJSONArray("partiesList");
				if (array.length() == 0) {
					Toast.makeText(mContext, "没有搜索的会员", Toast.LENGTH_SHORT).show();
				} else if (array.length() == 1) {
					ll_member_add.setVisibility(View.GONE);
					ll_member_msg.setVisibility(View.VISIBLE);
					for (int i = 0; i < array.length(); i++) {
						JSONObject object = array.optJSONObject(i);
						tv_member_name.setText(object.optString("firstName"));
						if (object.optString("gender").equals("M")) {
							tv_member_sex.setText("男");
						} else if (object.optString("gender").equals("F")) {
							tv_member_sex.setText("女");
						}
						tv_member_phone.setText(object.optString("contactNumber"));
						if (object.optString("memberId").equals("") || object.optString("memberId") == null
								|| object.optString("memberId").equals("null")) {
							tv_member_number.setText("");
						} else {
							tv_member_number.setText(object.optString("memberId"));
						}
						tv_member_grade.setText("等级" + object.optString("customerLevel"));
						if (object.optString("description").equals("") || object.optString("description") == null
								|| object.optString("description").equals("null")) {
							tv_member_keyword.setText("");
						} else {
							tv_member_keyword.setText(object.optString("description"));
						}
						if (object.optString("comments").equals("") || object.optString("comments") == null
								|| object.optString("comments").equals("null")) {
							tv_member_remark.setText("");
						} else {
							tv_member_remark.setText(object.optString("comments"));
						}
					}
				} else {
					for (int i = 0; i < array.length(); i++) {
						info = new SearchMemberInfo();
						JSONObject object = array.optJSONObject(i);
						info.setName(object.optString("firstName"));
						info.setSex(object.optString("gender"));
						info.setPhone(object.optString("contactNumber"));
						info.setCard(object.optString("memberId"));
						info.setGrade(object.optString("customerLevel"));
						info.setKeyword(object.optString("description"));
						info.setRemark(object.optString("comments"));
						data.add(info);
					}
					Intent intent = new Intent(mContext, MemberListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("list", (ArrayList<? extends Parcelable>) data);
					intent.putExtras(bundle);
					startActivityForResult(intent, 0);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		dialog.dismiss();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == 1) {
				ll_member_add.setVisibility(View.GONE);
				ll_member_msg.setVisibility(View.VISIBLE);
				tv_member_name.setText(data.getStringExtra("memberName"));
				if (data.getStringExtra("memberSex").equals("M")) {
					tv_member_sex.setText("男");
				} else if (data.getStringExtra("memberSex").equals("F")) {
					tv_member_sex.setText("女");
				}
				tv_member_phone.setText(data.getStringExtra("memberPhone"));
				if (data.getStringExtra("memberNumber").equals("") || data.getStringExtra("memberNumber") == null
						|| data.getStringExtra("memberNumber").equals("null")) {
					tv_member_number.setText("");
				} else {
					tv_member_number.setText(data.getStringExtra("memberNumber"));
				}
				tv_member_grade.setText("等级" + data.getStringExtra("memberGrade"));
				if (data.getStringExtra("memberKeyword").equals("") || data.getStringExtra("memberKeyword") == null
						|| data.getStringExtra("memberKeyword").equals("null")) {
					tv_member_keyword.setText("");
				} else {
					tv_member_keyword.setText(data.getStringExtra("memberKeyword"));
				}
				if (data.getStringExtra("memberRemark").equals("") || data.getStringExtra("memberRemark") == null
						|| data.getStringExtra("memberRemark").equals("null")) {
					tv_member_remark.setText("");
				} else {
					tv_member_remark.setText(data.getStringExtra("memberRemark"));
				}
			}
		}
	}

}
