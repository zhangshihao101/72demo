package com.mts.pos.Activity;

import java.util.List;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.listview.SearchMemberAdapter;
import com.mts.pos.listview.SearchMemberInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class MemberListActivity extends BaseActivity{

	private ImageView iv_member_list_close;
	private ListView lv_member_list;
	List<SearchMemberInfo> mList;
	
	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.member_list);
		super.onCreate(inState);
		iv_member_list_close = (ImageView) findViewById(R.id.iv_member_list_close);
		lv_member_list = (ListView) findViewById(R.id.lv_member_list);
		mList = getIntent().getParcelableArrayListExtra("list");
		SearchMemberAdapter adapter = new SearchMemberAdapter(this, mList);
		lv_member_list.setAdapter(adapter);
		
		lv_member_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("memberName", mList.get(position).getName());
				intent.putExtra("memberSex", mList.get(position).getSex());
				intent.putExtra("memberPhone", mList.get(position).getPhone());
				intent.putExtra("memberNumber", mList.get(position).getCard());
				intent.putExtra("memberGrade", mList.get(position).getGrade());
				intent.putExtra("memberKeyword", mList.get(position).getKeyword());
				intent.putExtra("memberRemark", mList.get(position).getRemark());
				setResult(1, intent);
				finish();
			}
		});
		
		iv_member_list_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
