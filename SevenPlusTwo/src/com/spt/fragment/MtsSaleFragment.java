package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.OrderStatisticsAdapter;
import com.spt.bean.OrderStatisticsInfo;
import com.spt.controler.ListViewForScrollView;
import com.spt.page.MtsChannelOrdersActivity;
import com.spt.page.MtsMsgActivity;
import com.spt.page.MtsOrderListActivity;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsSaleFragment extends Fragment {

	private View view;
	private static Context mContext;
	private ImageView iv_mts_back, iv_notice;
	private TextView tv_show_detail;
	private ListViewForScrollView lv_saled_detail;
	private ScrollView msv_scrollview;
	private RadioGroup rgp_time;
	private RadioButton rbtn_seven, rbtn_thirty;
	private Button btn_order_channal;

	private OrderStatisticsAdapter orderstatisticsadapter;
	private OrderStatisticsInfo info;
	private List<OrderStatisticsInfo> orderStatisticsData;

	private static ProgressDialog dialog;

	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;

	private SevenDaysAgoFragment sevenDaysAgoFragment;
	private ThirtyDaysAgoFragment thirtyDaysAgoFragment;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		view = inflater.inflate(R.layout.fragment_mts_sale, null);

		intiView();

		getOrderstatistics();

		fragmentManager = getActivity().getSupportFragmentManager();
		setTabSelection(0);

		iv_mts_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		
		iv_notice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MtsMsgActivity.class);
				startActivity(intent);
			}
		});

		rgp_time.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rbtn_seven.getId()) {
					setTabSelection(0);
					rbtn_seven.setTextColor(0xffffffff);
					rbtn_thirty.setTextColor(0xff319ce1);
				} else if (checkedId == rbtn_thirty.getId()) {
					setTabSelection(1);
					rbtn_thirty.setTextColor(0xffffffff);
					rbtn_seven.setTextColor(0xff319ce1);
				}
			}
		});

		tv_show_detail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MtsOrderListActivity.class);
				startActivity(intent);
			}
		});
		
		btn_order_channal.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MtsChannelOrdersActivity.class);
				startActivity(intent);
			}
		});

		return view;
	}

	private void getOrderstatistics() {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_orderstatistics)
						.post(new FormBody.Builder()
								.add("externalLoginKey", Localxml.search(mContext, "externalloginkey")).build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======" + "结果？" + "========" + jsonStr + "=============");
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
								orderstatisticsadapter = new OrderStatisticsAdapter(mContext, orderStatisticsData);
								try {
									JSONObject jsonobject = new JSONObject(jsonStr);
									JSONArray sumArray = jsonobject.optJSONArray("listGt");
									JSONArray countArray = jsonobject.optJSONArray("listRst");
									for (int i = 0; i < sumArray.length(); i++) {
										info = new OrderStatisticsInfo();

										info.setOrderSum(sumArray.getDouble(i));
										info.setOrderCount(countArray.getInt(i));

										orderStatisticsData.add(info);
										orderstatisticsadapter.notifyDataSetChanged();
									}

									orderStatisticsData = new ArrayList<OrderStatisticsInfo>();
									lv_saled_detail.setAdapter(orderstatisticsadapter);
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});

					}

					@Override
					public void onFailure(Call call, IOException arg1) {
						dialog.dismiss();
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
							}
						});
					}
				});

	}

	private void intiView() {
		iv_mts_back = (ImageView) view.findViewById(R.id.iv_mts_back);
		iv_notice = (ImageView) view.findViewById(R.id.iv_notice);
		tv_show_detail = (TextView) view.findViewById(R.id.tv_show_detail);
		lv_saled_detail = (ListViewForScrollView) view.findViewById(R.id.lv_saled_detail);
		msv_scrollview = (ScrollView) view.findViewById(R.id.msv_scrollview);
		rgp_time = (RadioGroup) view.findViewById(R.id.rgp_time);
		rbtn_seven = (RadioButton) view.findViewById(R.id.rbtn_seven);
		rbtn_thirty = (RadioButton) view.findViewById(R.id.rbtn_thirty);
		btn_order_channal = (Button)view.findViewById(R.id.btn_order_channal);
		
		dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();

		orderStatisticsData = new ArrayList<OrderStatisticsInfo>();

	}

	public static boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog.dismiss();
		}
		return true;
	}

	/**
	 * 切换两个fragment且保存数据的方法
	 * 
	 * @param index
	 */
	private void setTabSelection(int index) {
		fragmentTransaction = fragmentManager.beginTransaction();
		hideFragments(fragmentTransaction);
		switch (index) {
		case 0:
			if (sevenDaysAgoFragment == null) {
				sevenDaysAgoFragment = new SevenDaysAgoFragment();
				// fragmentTransaction.add(R.id.fl_frame, memerFragment);
				fragmentTransaction.add(R.id.fl_frame_chart, sevenDaysAgoFragment, "saleF");
			} else {
				fragmentTransaction.show(sevenDaysAgoFragment);
			}
			break;

		case 1:
			if (thirtyDaysAgoFragment == null) {
				thirtyDaysAgoFragment = new ThirtyDaysAgoFragment();
				fragmentTransaction.add(R.id.fl_frame_chart, thirtyDaysAgoFragment, "stockF");
			} else {
				fragmentTransaction.show(thirtyDaysAgoFragment);
			}
			break;
		}
		fragmentTransaction.commit();
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (sevenDaysAgoFragment != null) {
			transaction.hide(sevenDaysAgoFragment);
		}
		if (thirtyDaysAgoFragment != null) {
			transaction.hide(thirtyDaysAgoFragment);
		}
	}

}
