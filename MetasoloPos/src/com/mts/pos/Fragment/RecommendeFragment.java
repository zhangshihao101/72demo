package com.mts.pos.Fragment;

import com.mts.pos.R;
import com.mts.pos.Common.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class RecommendeFragment extends BaseFragment {
	private GridView gv_recommended;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_recommanded, null);
		gv_recommended = (GridView) view.findViewById(R.id.gv_recommended);
		return view;
	}
}
