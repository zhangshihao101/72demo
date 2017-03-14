package com.spt.page;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.adapter.BillAdapter;
import com.spt.bean.BillInfo;
import com.spt.controler.MyRefreshListView;
import com.spt.controler.MyRefreshListView.MyOnRefreshListener;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 【对账单】页
 * */
public class AccountActivity extends BaseActivity {

	private MyTitleBar mtbAccountTitle;
	private TextView tvTitle;
	private ImageView ivLeft;
	private MyRefreshListView lvAccountContent;
	private Intent itAccountFrom;
	private TextView tvTip;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private Intent itFrom;
	private HashMap<String, String> param;
	private ProgressDialog progressDialog;
	private Intent iGetRequest;
	private boolean isGetServiceRunning = false;
	private BroadcastReceiver brGetHttp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.account);
		super.onCreate(savedInstanceState);
		// 初始化内容
		try {
			String strData = itAccountFrom.getStringExtra("data");
			initContent(strData);
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
		AccountActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		super.onStop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(MyConstant.RESULTCODE_22);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(MyConstant.RESULTCODE_22);
				finish();
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtbAccountTitle = (MyTitleBar) findViewById(R.id.mtb_account_title);
		this.tvTitle = mtbAccountTitle.getTvTitle();
		this.ivLeft = mtbAccountTitle.getIvLeft();
		this.tvTitle.setText("对账单");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbAccountTitle.getLlLeft();
		this.llRight = mtbAccountTitle.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.lvAccountContent = (MyRefreshListView) findViewById(R.id.lv_account_content);
		this.itAccountFrom = getIntent();
		this.tvTip = (TextView) findViewById(R.id.tv_account_tip);
		this.itFrom = getIntent();
		this.iGetRequest = new Intent(AccountActivity.this, MyHttpGetService.class);
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
		this.param = new HashMap<String, String>();
		this.brGetHttp = new MyBroadCastReceiver(); // GET广播对象
		this.progressDialog = ProgressDialog.show(AccountActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		BillAdapter ba = new BillAdapter(AccountActivity.this);
		this.lvAccountContent.setAdapter(ba);
		this.lvAccountContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BillInfo info = (BillInfo) parent.getItemAtPosition(position);
				String sta_id = info.getSta_id();
				param.clear();
				param.put("token", itFrom.getStringExtra("token"));
				param.put("sta_id", sta_id);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance&act=view";
				String type = "accountView";
				progressDialog.show();
				iGetRequest.putExtra("uri", uri);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", type);
				startService(iGetRequest);
				isGetServiceRunning = true;
			}
		});
		lvAccountContent.setonMyRefreshListener(new MyOnRefreshListener() {
			
			@Override
			public void onRefresh() {
				param.clear();
				param.put("token", itFrom.getStringExtra("token"));
				String uriAccount = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance";
				String typeAccount = "Account_refresh";
				iGetRequest.putExtra("uri", uriAccount);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typeAccount);
				startService(iGetRequest);
				isGetServiceRunning = true;
			}
		});
	}

	/**
	 * 初始化内容
	 * */
	private void initContent(String data) throws JSONException {
		tvTip.setVisibility(View.GONE);
		JSONTokener jasonParser1 = new JSONTokener(data);
		JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
		HeaderViewListAdapter hvla = (HeaderViewListAdapter) lvAccountContent.getAdapter();
		BillAdapter ba = (BillAdapter) hvla.getWrappedAdapter();
		int length = jsonReturn1.length();
		if (length > 0) {
			ba.clear();
			ba.notifyDataSetChanged();
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
				BillInfo info = new BillInfo();
				String sta_id = jsonReturn2.getString("sta_id");
				String sta_sn = jsonReturn2.getString("sta_sn");
				String sta_status = jsonReturn2.getString("sta_status");
				String sta_plat = jsonReturn2.getString("sta_plat");
				String total_order_pay = jsonReturn2.getString("total_order_pay");
				String add_time = jsonReturn2.getString("add_time");
				String confirm_time = jsonReturn2.getString("confirm_time");

				info.setSta_id(sta_id);
				info.setSta_sn(sta_sn);
				info.setSta_status(sta_status);
				info.setSta_plat(sta_plat);
				info.setTotal_order_pay(total_order_pay);
				info.setAdd_time(add_time);
				info.setConfirm_time(confirm_time);

				ba.addBillInfo(info);
			}

			ba.notifyDataSetChanged();
			lvAccountContent.onRefreshComplete();
		} else {
			tvTip.setText("您目前还没有对账单");
			tvTip.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		AccountActivity.this.registerReceiver(brGetHttp, filterGetHttp);
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
						parseDataGet(strReturnType, result);
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
	private void parseDataGet(String type, String jsonStr) throws JSONException {

		if ("accountView".equals(type)) {
			progressDialog.dismiss();
			Intent intent = new Intent(AccountActivity.this, BillDetailActivity.class);
			intent.putExtra("url", jsonStr);
			startActivity(intent);
		} else if ("Account_refresh".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String data = jsonReturn.getJSONArray("data").toString();
			initContent(data);
		}
	}

}
