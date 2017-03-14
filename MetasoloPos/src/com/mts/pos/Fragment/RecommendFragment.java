package com.mts.pos.Fragment;

import java.util.ArrayList;
import java.util.List;

import com.mts.pos.R;
import com.mts.pos.Common.BaseFragment;
import com.mts.pos.listview.RecommendAdapter;
import com.mts.pos.listview.RecommendInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class RecommendFragment extends BaseFragment {

	private View view;
	private GridView gv_detail_recommend;
	private List<RecommendInfo> recommendList;
	private String[] imgUrl = { "http://p5.img.ymatou.com/upload/product/big/24901a7f8c2f482084a4f457fc85e979_b.jpg",
			"http://pic11.secooimg.com/product/500/500/12/13/13201213.jpg" };
	private String[] rNmae = { "【16春夏新品】Arcteryx/始祖鸟 男款防水徒步鞋靴 Bora2 Mid GTX ",
			"【15秋冬新品】Arcteryx始祖鸟 滑雪专用背包Khamski 31 Backpack " };
	private Context mContext;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		view = inflater.inflate(R.layout.fragment_recommend, null);

		recommendList = new ArrayList<RecommendInfo>();

		initView();
		initData();
		return view;
	}

	private void initData() {
		for (int i = 0; i < 2; i++) {
			RecommendInfo rInfo = new RecommendInfo();
			rInfo.setImgUrl(imgUrl[i]);
			rInfo.setRecommendName(rNmae[i]);
			recommendList.add(rInfo);
		}
		RecommendAdapter adapter = new RecommendAdapter(mContext, recommendList);
		gv_detail_recommend.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}

	private void initView() {
		gv_detail_recommend = (GridView) view.findViewById(R.id.gv_detail_recommend);
	}

}
