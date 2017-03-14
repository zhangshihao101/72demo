package com.spt.page;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.spt.adapter.NeedSendAdapter;
import com.spt.bean.NeedSendOrderInfo;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 【待发货订单】页
 * */
public class NeedSendOrderActivity extends BaseActivity {
	private MyTitleBar mtbNeed;
	private ImageView leftIv;
	private ImageView rightIv;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private TextView tvTitle;
	private Intent itNeedFrom;
	private PullToRefreshListView prlvNeedContent;
	private HashMap<String, Object> param;
	private TextView tvTip;
	private Intent iGetRequest;
	private BroadcastReceiver brGetHttp;
	private boolean isGetServiceRunning = false;
	private SharedPreferences spJSONQuery;
	private ProgressDialog progressDialog;
	private String orderSn = "";
	private String extension = "";
	private String addTimeFrom = "";
	private String addTimeTo = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.needsendorders);
		super.onCreate(savedInstanceState);
		initContent();
		try {
			addData(); // 加载数据
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
		NeedSendOrderActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent pit) {
		switch (resultCode) {
		case MyConstant.RESULTCODE_29: // 从订单查询返回
			tvTip.setVisibility(View.GONE);
			String jsonStr = spJSONQuery.getString("OrderQueryBackNeed", "");
			if (!"".equals(jsonStr) && !"null".equals(jsonStr)) {
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvNeedContent.getRefreshableView().getAdapter();
				NeedSendAdapter nsa = (NeedSendAdapter) hvla.getWrappedAdapter();
				nsa.clear();
				try {
					JSONTokener jsonParser = new JSONTokener(jsonStr);
					JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
					JSONArray array = jsonReturn.getJSONArray("data");
					int length_QueryBack = array.length();
					if (length_QueryBack > 0) {
						tvTip.setText("");
						tvTip.setVisibility(View.INVISIBLE);
						for (int i = 0; i < length_QueryBack; i++) {
							JSONObject jsonReturn2 = array.getJSONObject(i);
							String order_id = jsonReturn2.getString("order_id");
							String order_sn = jsonReturn2.getString("order_sn");
							String extension = jsonReturn2.getString("extension");
							String add_time = jsonReturn2.getString("add_time");
							String final_amount = jsonReturn2.getString("final_amount");
							String is_change = jsonReturn2.getString("is_change");

							NeedSendOrderInfo info = new NeedSendOrderInfo();
							info.setOrder_sn(order_sn + " (" + MyUtil.codeToString(extension) + ")");
							info.setOrder_id(order_id);
							info.setAdd_time(MyUtil.millisecondsToStr(add_time));
							info.setFinal_amount(final_amount);
							info.setIs_change(is_change);
							nsa.addNeedSendOrderInfo(info);
						}
						nsa.notifyDataSetChanged();
						prlvNeedContent.onRefreshComplete();
					} else {
						tvTip.setText("没有符合条件的订单");
						tvTip.setVisibility(View.VISIBLE);
						nsa.notifyDataSetChanged();
					}

				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
			if (pit.hasExtra("orderSn")) {
				orderSn = pit.getStringExtra("orderSn");
			} else {
				orderSn = "";
			}

			if (pit.hasExtra("extension")) {
				extension = pit.getStringExtra("extension");
			} else {
				extension = "";
			}

			if (pit.hasExtra("addTimeFrom")) {
				addTimeFrom = pit.getStringExtra("addTimeFrom");
			} else {
				addTimeFrom = "";
			}

			if (pit.hasExtra("addTimeTo")) {
				addTimeTo = pit.getStringExtra("addTimeTo");
			} else {
				addTimeTo = "";
			}
			break;
		case MyConstant.RESULTCODE_30: // 从订单详情返回
			callOrderList();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(MyConstant.RESULTCODE_17);
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
				setResult(MyConstant.RESULTCODE_17);
				finish();
			}
		});

		llRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(NeedSendOrderActivity.this, OrderQueryActivity.class);
				it.putExtra("state", "needSend");
				startActivityForResult(it, MyConstant.RESULTCODE_29);
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtbNeed = (MyTitleBar) findViewById(R.id.mtb_needSendOrdersPage_title);
		this.tvTitle = mtbNeed.getTvTitle();
		this.tvTitle.setText("待发货订单");
		this.leftIv = mtbNeed.getIvLeft();
		this.leftIv.setBackgroundResource(R.drawable.titlemenu);
		this.rightIv = mtbNeed.getIvRight();
		this.rightIv.setBackgroundResource(R.drawable.homemenuright);
		this.llLeft = mtbNeed.getLlLeft();
		this.llRight = mtbNeed.getLlRight();
		this.llRight.setVisibility(View.VISIBLE);
		this.itNeedFrom = getIntent();
		this.prlvNeedContent = (PullToRefreshListView) findViewById(R.id.prlv_needSendOrdersPage_content);
		this.tvTip = (TextView) findViewById(R.id.tv_needSendOrdersPage_tip);
		this.param = new HashMap<String, Object>();
		this.iGetRequest = new Intent(NeedSendOrderActivity.this, MyHttpGetService.class); // 启动GET服务Intent对象
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton); // 设置GET Action
		this.brGetHttp = new MyBroadCastReceiver();
		this.spJSONQuery = NeedSendOrderActivity.this.getSharedPreferences("JSONDATA", MODE_PRIVATE); // 获取sp对象
		this.progressDialog = ProgressDialog.show(NeedSendOrderActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
	}

	/**
	 * 加载页面内容
	 * */
	private void initContent() {

		final NeedSendAdapter nsa = new NeedSendAdapter(NeedSendOrderActivity.this);
		prlvNeedContent.setAdapter(nsa);

//		lvNeedContent.setonRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				callOrderList();
//			}
//		});
		
		prlvNeedContent.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (prlvNeedContent.isHeaderShown()) {
					callOrderList();

				} else if (prlvNeedContent.isFooterShown()) {
					int size = nsa.getCount();
					loadOrderList(size);
				}

				prlvNeedContent.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvNeedContent.onRefreshComplete();
					}
				}, 1000);
			}
		});
		prlvNeedContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NeedSendOrderInfo info = (NeedSendOrderInfo) parent.getItemAtPosition(position);
				String order_id = info.getOrder_id();
				param.clear();
				param.put("token", itNeedFrom.getStringExtra("token"));
				param.put("order_id", order_id);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=view";
				String type = "NeedSendDetail";
				iGetRequest.putExtra("uri", uri);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", type);
				startService(iGetRequest);
				isGetServiceRunning = true;
			}
		});
	}
	
	/**
	 * 请求订单列表
	 * */
	private void callOrderList() {
		progressDialog.show();
		param.clear();
		param.put("token", itNeedFrom.getStringExtra("token"));
		param.put("status", "20");
		if (!"".equals(orderSn)) {
			param.put("order_sn", orderSn);
		}
		if (!"".equals(extension)) {
			param.put("extension", extension);
		}
		if (!"".equals(addTimeTo)) {
			param.put("add_time_to", addTimeTo);
		}
		if (!"".equals(addTimeFrom)) {
			param.put("add_time_from", addTimeFrom);
		}
		String uriNeed = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
		String typeNeed = "needSend_refresh";
		iGetRequest.putExtra("uri", uriNeed);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeNeed);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}
	
	/**
	 * 请求订单列表
	 * */
	private void loadOrderList(int size) {
		progressDialog.show();
		param.clear();
		param.put("token", itNeedFrom.getStringExtra("token"));
		param.put("status", "20");
		param.put("offset", String.valueOf(size));
		if (!"".equals(orderSn)) {
			param.put("order_sn", orderSn);
		}
		if (!"".equals(extension)) {
			param.put("extension", extension);
		}
		if (!"".equals(addTimeTo)) {
			param.put("add_time_to", addTimeTo);
		}
		if (!"".equals(addTimeFrom)) {
			param.put("add_time_from", addTimeFrom);
		}
		String uriNeed = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
		String typeNeed = "needSend_load";
		iGetRequest.putExtra("uri", uriNeed);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeNeed);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}

	/**
	 * 加载数据
	 * */
	private void addData() throws JSONException {
		tvTip.setVisibility(View.GONE);
		String data = itNeedFrom.getStringExtra("data");
		System.out.println(data);
		JSONTokener jasonParser = new JSONTokener(data);
		JSONArray jsonReturn = (JSONArray) jasonParser.nextValue();
		HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvNeedContent.getRefreshableView().getAdapter();
		NeedSendAdapter nsa = (NeedSendAdapter) hvla.getWrappedAdapter();
		int length = jsonReturn.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn1 = jsonReturn.getJSONObject(i);
				String order_id = jsonReturn1.getString("order_id");
				String order_sn = jsonReturn1.getString("order_sn");
				String extension = jsonReturn1.getString("extension");
				String add_time = jsonReturn1.getString("add_time");
				String final_amount = jsonReturn1.getString("final_amount");
				String is_change = jsonReturn1.getString("is_change");

				NeedSendOrderInfo info = new NeedSendOrderInfo();
				info.setOrder_sn(order_sn + " (" + MyUtil.codeToString(extension) + ")");
				info.setOrder_id(order_id);
				info.setAdd_time(MyUtil.millisecondsToStr(add_time));
				info.setFinal_amount(final_amount);
				info.setIs_change(is_change);
				nsa.addNeedSendOrderInfo(info);
			}
			nsa.notifyDataSetChanged();
			prlvNeedContent.onRefreshComplete();
		} else {
			tvTip.setText("您目前不存在待发货订单");
			tvTip.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		NeedSendOrderActivity.this.registerReceiver(brGetHttp, filterGetHttp);
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
						parseData(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	/**
	 * 解析返回数据
	 * */
	private void parseData(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("NeedSendDetail".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Intent it = new Intent(NeedSendOrderActivity.this, OrderDetailActivity.class);
				it.putExtra("data", data);
				it.putExtra("pageCode", "need");
				startActivityForResult(it, MyConstant.RESULTCODE_30);
			} else {
				MyUtil.ToastMessage(NeedSendOrderActivity.this, "该订单数据加载失败！");
			}
		} else if ("needSend_refresh".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String data = jsonReturn.getJSONArray("data").toString();
			JSONTokener jasonParser = new JSONTokener(data);
			JSONArray jsonReturn1 = (JSONArray) jasonParser.nextValue();
			int length = jsonReturn1.length();
			HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvNeedContent.getRefreshableView().getAdapter();
			NeedSendAdapter nsa = (NeedSendAdapter) hvla.getWrappedAdapter();
			nsa.clear();
			nsa.notifyDataSetChanged();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
					String order_id = jsonReturn2.getString("order_id");
					String order_sn = jsonReturn2.getString("order_sn");
					String extension = jsonReturn2.getString("extension");
					String add_time = jsonReturn2.getString("add_time");
					String final_amount = jsonReturn2.getString("final_amount");
					String is_change = jsonReturn2.getString("is_change");

					NeedSendOrderInfo info = new NeedSendOrderInfo();
					info.setOrder_sn(order_sn + " (" + MyUtil.codeToString(extension) + ")");
					info.setOrder_id(order_id);
					info.setAdd_time(MyUtil.millisecondsToStr(add_time));
					info.setFinal_amount(final_amount);
					info.setIs_change(is_change);
					nsa.addNeedSendOrderInfo(info);
				}
				nsa.notifyDataSetChanged();
				prlvNeedContent.onRefreshComplete();
			} else {
				tvTip.setText("您目前不存在待发货订单");
				tvTip.setVisibility(View.VISIBLE);
			}
		} else if ("needSend_load".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String data = jsonReturn.getJSONArray("data").toString();
			JSONTokener jasonParser = new JSONTokener(data);
			JSONArray jsonReturn1 = (JSONArray) jasonParser.nextValue();
			int length = jsonReturn1.length();
			HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvNeedContent.getRefreshableView().getAdapter();
			NeedSendAdapter nsa = (NeedSendAdapter) hvla.getWrappedAdapter();
			nsa.notifyDataSetChanged();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
					String order_id = jsonReturn2.getString("order_id");
					String order_sn = jsonReturn2.getString("order_sn");
					String extension = jsonReturn2.getString("extension");
					String add_time = jsonReturn2.getString("add_time");
					String final_amount = jsonReturn2.getString("final_amount");
					String is_change = jsonReturn2.getString("is_change");

					NeedSendOrderInfo info = new NeedSendOrderInfo();
					info.setOrder_sn(order_sn + " (" + MyUtil.codeToString(extension) + ")");
					info.setOrder_id(order_id);
					info.setAdd_time(MyUtil.millisecondsToStr(add_time));
					info.setFinal_amount(final_amount);
					info.setIs_change(is_change);
					nsa.addNeedSendOrderInfo(info);
				}
				nsa.notifyDataSetChanged();
				prlvNeedContent.onRefreshComplete();
			} else {
				tvTip.setVisibility(View.GONE);
			}
		}
	}
}
