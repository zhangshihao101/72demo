package com.mts.pos.Fragment;

import java.util.Locale;

import com.mts.pos.R;
import com.mts.pos.Common.BaseFragment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CommonFragment extends BaseFragment {

	private RelativeLayout rl_common_language;
	// private Switch st_common_ticket, st_footfall;
	// private Spinner sp_start, sp_end;
	private TextView tv_language_value;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_setting_common, null);
		rl_common_language = (RelativeLayout) view.findViewById(R.id.rl_common_language);
		// st_common_ticket = (Switch) view.findViewById(R.id.st_common_ticket);
		// st_footfall = (Switch) view.findViewById(R.id.st_footfall);
		// sp_start = (Spinner) view.findViewById(R.id.sp_start);
		// sp_end = (Spinner) view.findViewById(R.id.sp_end);
		tv_language_value = (TextView) view.findViewById(R.id.tv_language_value);

		if (Locale.getDefault() == Locale.CHINESE) {
			tv_language_value.setText("中文");
		} else if (Locale.getDefault() == Locale.ENGLISH) {
			tv_language_value.setText("English");
		}

		rl_common_language.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LanguageFragment languageFragment = new LanguageFragment();
				FragmentManager manager = getFragmentManager();
				FragmentTransaction transaction = manager.beginTransaction();
				transaction.add(R.id.fl_setting_detail, languageFragment);
				transaction.commit();
			}
		});

		return view;
	}
}
