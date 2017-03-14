package com.spt.page;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.spt.adapter.OrderListAdapter;
import com.spt.bean.OrderListInfo;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;

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
 * 【订单查询结果】页
 * 
 * */
public class OrderQueryResultActivity extends BaseActivity {
	private MyTitleBar mtbOrderResult;
	private TextView tvTitle;
	private ImageView ivLeft;
	private PullToRefreshListView prlvResult;
	private TextView tvTip;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private Intent itFrom;
	private HashMap<String, Object> param;
	private String token;
	private Intent iGetRequest; // get方法请求
	private boolean isGetServiceRunning = false;
	private BroadcastReceiver brGetHttp;
	private ImageView rightIv;
	private SharedPreferences spJSON; // 保存服务数据
	private boolean isDetailrefresh = false;
	
	private String orderSn = "";
	private String orderStatus = "";
	private String evaluationStatus = "";
	private String extension = "";
	private String addTimeFrom = "";
	private String addTimeTo = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.order_result);
		super.onCreate(savedInstanceState);
		initContent();
		initCode();
		try {
			addData(itFrom.getStringExtra("resultData"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier();
		super.onStart();
	}

	@Override
	protected void onStop() {
		OrderQueryResultActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	protected void init() {
		this.prlvResult = (PullToRefreshListView) findViewById(R.id.prlv_ordersResult_OrdersList);
		this.prlvResult.setMode(Mode.BOTH);
		this.tvTip = (TextView) findViewById(R.id.tv_ordersResult_tip);
		this.mtbOrderResult = (MyTitleBar) findViewById(R.id.mtb_ordersResult_title);
		this.tvTitle = mtbOrderResult.getTvTitle();
		this.tvTitle.setText("全部订单");
		this.ivLeft = mtbOrderResult.getIvLeft();
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbOrderResult.getLlLeft();
		this.llRight = mtbOrderResult.getLlRight();
		this.llRight.setVisibility(View.VISIBLE);
		this.rightIv = mtbOrderResult.getIvRight();
		this.rightIv.setBackgroundResource(R.drawable.homemenuright);
		this.spJSON = OrderQueryResultActivity.this.getSharedPreferences("JSONDATA", MODE_PRIVATE); // 获取sp对象
		this.itFrom = getIntent();
		this.param = new HashMap<String, Object>();
		this.token = itFrom.getStringExtra("token");
		this.iGetRequest = new Intent(OrderQueryResultActivity.this, MyHttpGetService.class);
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
		this.brGetHttp = new MyBroadCastReceiver();
		setPageTitle();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (isDetailrefresh) {
				setResult(MyConstant.RESULTCODE_15);
			}
			OrderQueryResultActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isDetailrefresh) {
					setResult(MyConstant.RESULTCODE_15);
				}
				OrderQueryResultActivity.this.finish();
			}
		});
		
		this.llRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(OrderQueryResultActivity.this, OrderQueryActivity.class);
				it.putExtra("state", "orderResult");
				startActivityForResult(it, MyConstant.RESULTCODE_12);
			}
		});
	}
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent pit) {
		switch(resultCode) {
		case MyConstant.RESULTCODE_12:
			boolean isSuccessOrder = pit.getBooleanExtra("isSuccess", false);
			if (isSuccessOrder) {
				String jsonStr = spJSON.getString("OrderQueryBack", "");
				try {
					tvTip.setVisibility(View.GONE);
					JSONTokener jsonParser = new JSONTokener(jsonStr);
					JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
					JSONArray array = jsonReturn.getJSONArray("data");
					int length_QueryBack = array.length();
					HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvResult.getRefreshableView()
							.getAdapter();
					OrderListAdapter ola = (OrderListAdapter) hvla.getWrappedAdapter();
					if (length_QueryBack > 0) {
						ola.clear();
						for (int i = 0; i < length_QueryBack; i++) {
							JSONObject jsonReturn2 = array.getJSONObject(i);
							String order_id = jsonReturn2.getString("order_id");
							String order_sn = jsonReturn2.getString("order_sn");
							String extension = jsonReturn2.getString("extension");
							String status = jsonReturn2.getString("status");
							String final_amount = jsonReturn2.getString("final_amount");
							String add_time = jsonReturn2.getString("add_time");
							String is_change = jsonReturn2.getString("is_change");

							OrderListInfo info = new OrderListInfo();
							info.setOrder_id(order_id);
							info.setOrder_sn(order_sn);
							info.setExtension(extension);
							info.setStatus(status);
							info.setFinal_amount(final_amount);
							info.setAdd_time(add_time);
							info.setIs_change(is_change);
							ola.addOrderListInfo(info);
						}
						ola.notifyDataSetChanged();
					} else {
						ola.clear();
						ola.notifyDataSetChanged();
						tvTip.setVisibility(View.VISIBLE);
						tvTip.setText("未检索到符合条件的订单");
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
			if (pit.hasExtra("orderStatus")) {
				orderStatus = pit.getStringExtra("orderStatus");
			} else {
				orderStatus = "";
			}
			if (pit.hasExtra("evaluationStatus")) {
				evaluationStatus = pit.getStringExtra("evaluationStatus");
			} else {
				evaluationStatus = "";
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
			setPageTitle();
			break;
		case MyConstant.RESULTCODE_38:
			callOrderList();
			isDetailrefresh = true;
			break;
		case MyConstant.RESULTCODE_39:
			callOrderList();
			isDetailrefresh = true;
			break;
		}
	}

	private void initContent() {
		final OrderListAdapter osa = new OrderListAdapter(OrderQueryResultActivity.this);
		prlvResult.setAdapter(osa);
		prlvResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				OrderListInfo info = (OrderListInfo) parent.getItemAtPosition(position);
				String strId = info.getOrder_id();
				isDetailrefresh = false;

				param.clear();
				param.put("token", token);
				param.put("order_id", strId);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=view";
				String type = "orderDetail_result";
				iGetRequest.putExtra("uri", uri);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", type);
				startService(iGetRequest);
				isGetServiceRunning = true;
				param.clear();
			}
		});

		prlvResult.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (prlvResult.isHeaderShown()) {
					callOrderList();

				} else if (prlvResult.isFooterShown()) {
					int size = osa.getCount();
					loadOrderList(size);
				}

				prlvResult.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvResult.onRefreshComplete();
					}
				}, 1000);
			}
		});
	}

	private void addData(String jsonStr) throws JSONException {
		JSONTokener jsonParser = new JSONTokener(jsonStr);
		JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
		JSONArray array = jsonReturn.getJSONArray("data");
		int length = array.length();
		HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvResult.getRefreshableView().getAdapter();
		OrderListAdapter osa = (OrderListAdapter) hvla.getWrappedAdapter();
		osa.clear();
		osa.notifyDataSetChanged();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn2 = array.getJSONObject(i);
				String order_id = jsonReturn2.getString("order_id");
				String order_sn = jsonReturn2.getString("order_sn");
				String extension = jsonReturn2.getString("extension");
				String status = jsonReturn2.getString("status");
				String final_amount = jsonReturn2.getString("final_amount");
				String add_time = jsonReturn2.getString("add_time");
				String is_change = jsonReturn2.getString("is_change");

				OrderListInfo info = new OrderListInfo();
				info.setOrder_id(order_id);
				info.setOrder_sn(order_sn);
				info.setExtension(extension);
				info.setStatus(status);
				info.setFinal_amount(final_amount);
				info.setAdd_time(add_time);
				info.setIs_change(is_change);
				osa.addOrderListInfo(info);
			}

			osa.notifyDataSetChanged();
			prlvResult.onRefreshComplete();
		} else {
			osa.notifyDataSetChanged();
			prlvResult.onRefreshComplete();
			tvTip.setText("未搜索到相关订单");
			tvTip.setVisibility(View.VISIBLE);
			prlvResult.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 请求订单列表
	 * */
	private void callOrderList() {
		param.clear();
		param.put("token", token);
		if (!"".equals(orderSn)) {
			param.put("order_sn", orderSn);
		}
		if (!"".equals(orderStatus)) {
			param.put("status", orderStatus);
		}
		if (!"".equals(evaluationStatus)) {
			param.put("evaluation_status", evaluationStatus);
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
		String uriAll = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
		String typeAll = "orderList_result";
		iGetRequest.putExtra("uri", uriAll);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeAll);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}
	
	/**
	 * 请求订单列表
	 * */
	private void loadOrderList(int size) {
		param.clear();
		param.put("token", token);
		param.put("offset", String.valueOf(size));
		if (!"".equals(orderSn)) {
			param.put("order_sn", orderSn);
		}
		if (!"".equals(orderStatus)) {
			param.put("status", orderStatus);
		}
		if (!"".equals(evaluationStatus)) {
			param.put("evaluation_status", evaluationStatus);
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
		String uriAll = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
		String typeAll = "orderList_result_load";
		iGetRequest.putExtra("uri", uriAll);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeAll);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}
	
	private void initCode() {
		if (itFrom.hasExtra("orderSn")) {
			orderSn = itFrom.getStringExtra("orderSn");
		} else {
			orderSn = "";
		}
		if (itFrom.hasExtra("orderStatus")) {
			orderStatus = itFrom.getStringExtra("orderStatus");
		} else {
			orderStatus = "";
		}
		if (itFrom.hasExtra("evaluationStatus")) {
			evaluationStatus = itFrom.getStringExtra("evaluationStatus");
		} else {
			evaluationStatus = "";
		}
		if (itFrom.hasExtra("extension")) {
			extension = itFrom.getStringExtra("extension");
		} else {
			extension = "";
		}
		if (itFrom.hasExtra("addTimeFrom")) {
			addTimeFrom = itFrom.getStringExtra("addTimeFrom");
		} else {
			addTimeFrom = "";
		}
		if (itFrom.hasExtra("addTimeTo")) {
			addTimeTo = itFrom.getStringExtra("addTimeTo");
		} else {
			addTimeTo = "";
		}
	}
	
	/**
	 * 根据订单状态设置页面title
	 * */
	private void setPageTitle() {
		if (itFrom.hasExtra("orderStatus")) {
			orderStatus = itFrom.getStringExtra("orderStatus");
		} else {
			orderStatus = "";
		}
		System.out.println("orderStatus->>" + orderStatus);
		if ("".equals(orderStatus)) {
			this.tvTitle.setText("全部订单");
		} else if ("11".equals(orderStatus)) {
			this.tvTitle.setText("待付款订单");
		} else if ("20".equals(orderStatus)) {
			this.tvTitle.setText("待发货订单");
		} else if ("30".equals(orderStatus)) {
			this.tvTitle.setText("已发货订单");
		} else if ("40".equals(orderStatus)) {
			this.tvTitle.setText("已完成订单");
		} else if ("50".equals(orderStatus)) {
			this.tvTitle.setText("已退款订单");
		} else if ("0".equals(orderStatus)) {
			this.tvTitle.setText("已取消订单");
		}
		
	}
	
	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		OrderQueryResultActivity.this.registerReceiver(brGetHttp, filterGetHttp);
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
		if ("orderList_result".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				JSONTokener jasonParser1 = new JSONTokener(data);
				JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
				int length = jsonReturn1.length();
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvResult.getRefreshableView().getAdapter();
				OrderListAdapter osa = (OrderListAdapter) hvla.getWrappedAdapter();
				osa.clear();
				osa.notifyDataSetChanged();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
						String order_id = jsonReturn2.getString("order_id");
						String order_sn = jsonReturn2.getString("order_sn");
						String extension = jsonReturn2.getString("extension");
						String status = jsonReturn2.getString("status");
						String final_amount = jsonReturn2.getString("final_amount");
						String add_time = jsonReturn2.getString("add_time");
						String is_change = jsonReturn2.getString("is_change");

						OrderListInfo info = new OrderListInfo();
						info.setOrder_id(order_id);
						info.setOrder_sn(order_sn);
						info.setExtension(extension);
						info.setStatus(status);
						info.setFinal_amount(final_amount);
						info.setAdd_time(add_time);
						info.setIs_change(is_change);
						osa.addOrderListInfo(info);
					}

					osa.notifyDataSetChanged();
					prlvResult.onRefreshComplete();
				} else {
					osa.notifyDataSetChanged();
					prlvResult.onRefreshComplete();
					tvTip.setText("未搜索到相关订单");
					tvTip.setVisibility(View.VISIBLE);
					prlvResult.setVisibility(View.INVISIBLE);
				}
			} else {
				MyUtil.ToastMessage(OrderQueryResultActivity.this, msg);
				tvTip.setText("未搜索到相关订单");
				tvTip.setVisibility(View.VISIBLE);
				prlvResult.setVisibility(View.INVISIBLE);
			}
		}else if ("orderList_result_load".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				JSONTokener jasonParser1 = new JSONTokener(data);
				JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
				int length = jsonReturn1.length();
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvResult.getRefreshableView().getAdapter();
				OrderListAdapter osa = (OrderListAdapter) hvla.getWrappedAdapter();
				osa.notifyDataSetChanged();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
						String order_id = jsonReturn2.getString("order_id");
						String order_sn = jsonReturn2.getString("order_sn");
						String extension = jsonReturn2.getString("extension");
						String status = jsonReturn2.getString("status");
						String final_amount = jsonReturn2.getString("final_amount");
						String add_time = jsonReturn2.getString("add_time");
						String is_change = jsonReturn2.getString("is_change");

						OrderListInfo info = new OrderListInfo();
						info.setOrder_id(order_id);
						info.setOrder_sn(order_sn);
						info.setExtension(extension);
						info.setStatus(status);
						info.setFinal_amount(final_amount);
						info.setAdd_time(add_time);
						info.setIs_change(is_change);
						osa.addOrderListInfo(info);
					}

					osa.notifyDataSetChanged();
					prlvResult.onRefreshComplete();
				} else {
					osa.notifyDataSetChanged();
					prlvResult.onRefreshComplete();
					tvTip.setVisibility(View.GONE);
//					prlvOrdersList.setVisibility(View.INVISIBLE);
				}
			} else {
				MyUtil.ToastMessage(OrderQueryResultActivity.this, msg);
				tvTip.setVisibility(View.GONE);
				prlvResult.setVisibility(View.INVISIBLE);
			}
		} else if ("orderDetail_result".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error)) {
				Intent it = new Intent(OrderQueryResultActivity.this, OrderDetailActivity.class);
				it.putExtra("data", data);
				it.putExtra("pageCode", "orderResult");
				startActivityForResult(it, MyConstant.RESULTCODE_38);
			} else {
				MyUtil.ToastMessage(OrderQueryResultActivity.this, msg);
			}
		}
	}

}
