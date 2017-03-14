package com.spt.page;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.adapter.ScanTransInfoAdapter;
import com.spt.bean.ScanTransInfo;
import com.spt.controler.MyRefreshListView;
import com.spt.controler.MyRefreshListView.MyOnRefreshListener;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 【查看物流】页
 * */
public class ScanTransInfoActivity extends BaseActivity {

	private MyTitleBar mtbTransInfo;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Intent itFrom;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private MyRefreshListView lvContent;
	private TextView tvTransName;
	private TextView tvTransNo;
	private HashMap<String, String> param;
	private Intent itGet;
	private boolean isGetServiceRunning = false;
	private BroadcastReceiver brGetHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.scantransinfo);
		super.onCreate(savedInstanceState);
		String data = itFrom.getStringExtra("data");
		try {
			addData(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver
		super.onStart();
	}

	@Override
	protected void onStop() {
		ScanTransInfoActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(itGet);
			isGetServiceRunning = false;
		}

		super.onStop();
	}

	@Override
	protected void addClickEvent() {
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void init() {
		this.mtbTransInfo = (MyTitleBar) findViewById(R.id.mtb_scanTrans_title);
		this.tvTitle = mtbTransInfo.getTvTitle();
		this.ivLeft = mtbTransInfo.getIvLeft();
		this.tvTitle.setText("查看物流");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbTransInfo.getLlLeft();
		this.itFrom = getIntent();
		this.llRight = mtbTransInfo.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.itGet = new Intent(ScanTransInfoActivity.this, MyHttpGetService.class);
		this.itGet.setAction(MyConstant.HttpGetServiceAciton);
		this.brGetHttp = new MyBroadCastReceiver();
		this.param = new HashMap<String, String>();
		this.lvContent = (MyRefreshListView) findViewById(R.id.lv_scanTrans_content);
		ScanTransInfoAdapter adapter = new ScanTransInfoAdapter(ScanTransInfoActivity.this);
		this.lvContent.setAdapter(adapter);
		this.lvContent.setonMyRefreshListener(new MyOnRefreshListener() {

			@Override
			public void onRefresh() {
				param.clear();
				param.put("token", itFrom.getStringExtra("token"));
				param.put("order_id", itFrom.getStringExtra("order_id"));
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=express";
				itGet.putExtra("uri", uri);
				itGet.putExtra("param", param);
				itGet.putExtra("type", "express_refresh");
				startService(itGet);
				isGetServiceRunning = true;
			}
		});
		this.tvTransName = (TextView) findViewById(R.id.tv_scanTrans_transName);
		this.tvTransNo = (TextView) findViewById(R.id.tv_scanTrans_transNo);
	}

	/**
	 * 加载数据
	 * */
	private void addData(String pdata) throws JSONException {
		if (pdata.equals("false")) {
			tvTransName.setText("该订单暂无物流信息");
		} else {
			JSONTokener jsonParser = new JSONTokener(pdata);
			JSONObject obj = (JSONObject) jsonParser.nextValue();
			String expTextName = obj.getString("expTextName");
			String mailNo = obj.getString("mailNo");
			if ("".equals(expTextName) || "null".equals(expTextName)) {
				tvTransName.setText("该订单暂无物流信息");
			} else {
				tvTransName.setText(expTextName + "：");
			}
			tvTransNo.setText(mailNo);
			JSONArray array = obj.getJSONArray("data");
			int length = array.length();
			if (length > 0) {
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) lvContent.getAdapter();
				ScanTransInfoAdapter adapter = (ScanTransInfoAdapter) hvla.getWrappedAdapter();
				adapter.clear();
				for (int i = 0; i < length; i++) {
					ScanTransInfo info = new ScanTransInfo();
					JSONObject obj1 = array.getJSONObject(i);
					String time = obj1.getString("time");
					String context = obj1.getString("context");
					info.setTime(time);
					info.setContext(context);
					adapter.addScanTransInfo(info);
				}
				adapter.notifyDataSetChanged();
				lvContent.onRefreshComplete();
			}
		}
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		ScanTransInfoActivity.this.registerReceiver(brGetHttp, filterGetHttp);
	}

	/**
	 * 内部广播类
	 * */
	private class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyConstant.HttpGetServiceAciton.equals(intent.getAction())) {
				String isSuccess = intent.getStringExtra("isSuccess");
				String strReturnType = intent.getStringExtra("type");
				if ("ok".equals(isSuccess)) {
					String result = intent.getStringExtra("result");
					try {
						parseGetData(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * 解析get返回数据
	 * */
	private void parseGetData(String type, String jsonStr) throws JSONException {
		if ("express_refresh".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getString("data");
			if ("0".equals(error) && !"".equals(data)) {
				addData(data);
			} else {
				MyUtil.ToastMessage(ScanTransInfoActivity.this, msg);
			}
		}
	}

}
