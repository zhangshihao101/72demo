package com.mts.pos.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseFragment;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;
import com.mts.pos.listview.AttributeAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class AttributeFragment extends BaseFragment {

	private ListView lv_detail_attribute;
	private View view;
	private List<String> attList;
	private Context mContext;
	private AttributeAdapter adapter;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		view = inflater.inflate(R.layout.fragment_attribute, null);
		initView();
		attList = new ArrayList<String>();
		Bundle bundle = getArguments();
		if (bundle != null) {
			String productId = bundle.getString("productId");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs
					.add(new BasicNameValuePair("externalLoginKey", Localxml.search(mContext, "externalloginkey")));
			nameValuePairs.add(new BasicNameValuePair("productId", productId));
			nameValuePairs.add(new BasicNameValuePair("productStoreId", Localxml.search(mContext, "storeid")));
			getTask(mContext, Urls.base + Urls.detail_guide, nameValuePairs, "0");
		}
		initData();
		return view;
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			try {
				JSONObject obj = new JSONObject(result).optJSONObject("product");
				if (obj.optString("description") == null || obj.optString("description").equals("null")
						|| obj.optString("description").equals("")) {
					attList.add("");
				} else {
					attList.add(obj.optString("description"));
				}
				adapter.notifyDataSetChanged();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		super.updateUI(whichtask, result);
	}

	private void initData() {
		adapter = new AttributeAdapter(mContext, attList);
		lv_detail_attribute.setAdapter(adapter);
	}

	private void initView() {
		lv_detail_attribute = (ListView) view.findViewById(R.id.lv_detail_attribute);
	}

}
