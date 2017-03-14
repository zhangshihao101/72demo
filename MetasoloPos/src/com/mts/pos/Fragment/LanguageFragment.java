package com.mts.pos.Fragment;

import java.util.Locale;
import com.mts.pos.R;
import com.mts.pos.Common.BaseFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class LanguageFragment extends BaseFragment {

	private RelativeLayout rl_language_chinese, rl_language_english;
	private ImageView iv_language_chinese, iv_language_english;
	private Context mContext;
	private FragmentManager manager;
	private FragmentTransaction transaction;
	private LanguageFragment fragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@SuppressLint({ "InflateParams", "CommitTransaction" })
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setting_language, null);
		manager = getFragmentManager();
		transaction = manager.beginTransaction();
		fragment = new LanguageFragment();

		rl_language_english = (RelativeLayout) view.findViewById(R.id.rl_language_english);
		rl_language_chinese = (RelativeLayout) view.findViewById(R.id.rl_language_chinese);
		iv_language_chinese = (ImageView) view.findViewById(R.id.iv_language_chinese);
		iv_language_english = (ImageView) view.findViewById(R.id.iv_language_english);

		rl_language_chinese.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_language_chinese.setVisibility(View.VISIBLE);
				iv_language_english.setVisibility(View.GONE);
				Locale.setDefault(Locale.CHINESE);
				Configuration config = mContext.getResources().getConfiguration();
				config.locale = Locale.CHINESE;
				mContext.getResources().updateConfiguration(config, mContext.getResources().getDisplayMetrics());
				transaction.hide(fragment);
			}
		});
		rl_language_english.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				iv_language_chinese.setVisibility(View.GONE);
				iv_language_english.setVisibility(View.VISIBLE);
				Locale.setDefault(Locale.ENGLISH);
				Configuration config2 = mContext.getResources().getConfiguration();
				config2.locale = Locale.ENGLISH;
				mContext.getResources().updateConfiguration(config2, mContext.getResources().getDisplayMetrics());
				
			}
		});
		transaction.commit();

		if (Locale.getDefault() == Locale.CHINESE) {
			iv_language_chinese.setVisibility(View.VISIBLE);
			iv_language_english.setVisibility(View.GONE);
		} else if (Locale.getDefault() == Locale.ENGLISH) {
			iv_language_chinese.setVisibility(View.GONE);
			iv_language_english.setVisibility(View.VISIBLE);
		}
		return view;
	}

}
