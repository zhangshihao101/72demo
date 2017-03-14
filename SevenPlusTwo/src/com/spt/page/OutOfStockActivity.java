package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 【缺货登记】页
 * */
public class OutOfStockActivity extends BaseActivity {

	private MyTitleBar mtbOutTitle;
	private TextView tvTitle;
	private ImageView ivLeft;
	private PullToRefreshListView prlvOutContent;
	private List<HashMap<String, String>> lstOutData;
	private Intent itOutFrom;
	private TextView tvTip;
	private Intent iGetRequest;
	private HashMap<String, String> param;
	private boolean isGetServiceRunning = false;
	private BroadcastReceiver brGetHttp; // get方法广播
	private Intent iPostRequest;
	private boolean isPostServiceRunning = false;
	private BroadcastReceiver brPostHttp; // post方法广播
	private int delPosition;
	private String strNoticeId;
	private LinearLayout llLeft;
	private ProgressDialog progressDialog;
	private String msgContent;
	private LinearLayout llRight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.outofstock);
		super.onCreate(savedInstanceState);
		// 初始化内容
		initContent();
		// 加载数据
		try {
			String strData = itOutFrom.getStringExtra("data");
			addData(strData);
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
		OutOfStockActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}

		OutOfStockActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(MyConstant.RESULTCODE_20);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(MyConstant.RESULTCODE_20);
				finish();
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtbOutTitle = (MyTitleBar) findViewById(R.id.mtb_out_title);
		this.tvTitle = mtbOutTitle.getTvTitle();
		this.ivLeft = mtbOutTitle.getIvLeft();
		this.tvTitle.setText("缺货登记");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbOutTitle.getLlLeft();
		this.llRight = mtbOutTitle.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.prlvOutContent = (PullToRefreshListView) findViewById(R.id.prlv_out_content);
		this.prlvOutContent.setMode(Mode.BOTH);
		this.lstOutData = new ArrayList<HashMap<String, String>>();
		this.itOutFrom = getIntent();
		this.tvTip = (TextView) findViewById(R.id.tv_out_tip);
		this.iGetRequest = new Intent(OutOfStockActivity.this, MyHttpGetService.class);
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
		this.iPostRequest = new Intent(OutOfStockActivity.this, MyHttpPostService.class);
		this.iPostRequest.setAction(MyConstant.HttpPostServiceAciton);
		this.param = new HashMap<String, String>();
		this.brGetHttp = new MyBroadCastReceiver(); // GET广播对象
		this.brPostHttp = new MyBroadCastReceiver(); // POST广播对象
		this.progressDialog = ProgressDialog.show(OutOfStockActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
	}

	/**
	 * 初始化内容
	 * */
	private void initContent() {

		final SimpleAdapter sa = new SimpleAdapter(OutOfStockActivity.this, lstOutData, R.layout.outofstockitem,
				new String[] { "tv_out_productName", "tv_out_Color", "tv_out_userName", "tv_out_phone", "tv_out_date",
						"tv_out_Info" }, new int[] { R.id.tv_out_productName, R.id.tv_out_Color, R.id.tv_out_userName,
						R.id.tv_out_phone, R.id.tv_out_date, R.id.tv_out_Info });
		prlvOutContent.setAdapter(sa);

		prlvOutContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> map = lstOutData.get(position);
				final AlertDialog alert = new AlertDialog.Builder(OutOfStockActivity.this).create();
				View subView = LayoutInflater.from(OutOfStockActivity.this).inflate(R.layout.outofstockpop, null);
				Button btnCommit = (Button) subView.findViewById(R.id.btn_outPop_commit);
				final EditText et = (EditText) subView.findViewById(R.id.et_outPop_messageContent);
				TextView tvShopName = (TextView) subView.findViewById(R.id.tv_outPop_shopName);
				TextView tvReceiver = (TextView) subView.findViewById(R.id.tv_outPop_receiveMessager);
				TextView tvGoodName = (TextView) subView.findViewById(R.id.tv_outPop_good);
				ImageView ivclear = (ImageView) subView.findViewById(R.id.iv_outPop_clear);

				tvShopName.setText(map.get("store_name"));
				tvReceiver.setText(map.get("tv_out_userName"));
				tvGoodName.setText(map.get("tv_out_productName"));
				strNoticeId = map.get("notice_id");

				System.out.println("strNoticeId    " + strNoticeId);
				System.out.println("token    " + itOutFrom.getStringExtra("token"));

				btnCommit.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						msgContent = et.getText().toString();
						System.out.println("msgContent    " + msgContent);
						param.clear();
						param.put("token", itOutFrom.getStringExtra("token"));
						param.put("notice_id", strNoticeId);
						param.put("content", msgContent);
						String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=notice&act=send_mail";
						progressDialog.show();
						String type = "send_mail";
						iPostRequest.putExtra("uri", uri);
						iPostRequest.putExtra("param", param);
						iPostRequest.putExtra("type", type);
						startService(iPostRequest);
						isPostServiceRunning = true;
						alert.dismiss();
					}
				});
				ivclear.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						alert.dismiss();
					}
				});
				alert.setView(subView);
				alert.show();
			}
		});

		prlvOutContent.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				delPosition = position;
				HashMap<String, String> map = lstOutData.get(position);
				strNoticeId = map.get("notice_id");

				AlertDialog.Builder builder = new AlertDialog.Builder(OutOfStockActivity.this).setMessage("是否删除该条记录？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								param.clear();
								param.put("token", itOutFrom.getStringExtra("token"));
								param.put("notice_id", strNoticeId);
								String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=notice&act=del";
								progressDialog.show();
								String type = "delnotice";
								iGetRequest.putExtra("uri", uri);
								iGetRequest.putExtra("param", param);
								iGetRequest.putExtra("type", type);
								startService(iGetRequest);
								isGetServiceRunning = true;
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});

				builder.show();

				return false;
			}
		});

//		lvOutContent.setonRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				param.clear();
//				param.put("token", itOutFrom.getStringExtra("token"));
//				String uriOut = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=notice";
//				String typeOut = "Out_refresh";
//				iGetRequest.putExtra("uri", uriOut);
//				iGetRequest.putExtra("param", param);
//				iGetRequest.putExtra("type", typeOut);
//				startService(iGetRequest);
//				isGetServiceRunning = true;
//			}
//		});

		prlvOutContent.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (prlvOutContent.isHeaderShown()) {
					param.clear();
					param.put("token", itOutFrom.getStringExtra("token"));
					String uriOut = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=notice";
					String typeOut = "Out_refresh";
					iGetRequest.putExtra("uri", uriOut);
					iGetRequest.putExtra("param", param);
					iGetRequest.putExtra("type", typeOut);
					startService(iGetRequest);
					isGetServiceRunning = true;

				} else if (prlvOutContent.isFooterShown()) {
					int size = sa.getCount();
					System.out.println(String.valueOf(size - 1));
					param.clear();
					param.put("token", itOutFrom.getStringExtra("token"));
					param.put("offset", String.valueOf(size));
					String uriOut = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=notice";
					String typeOut = "Out_load";
					iGetRequest.putExtra("uri", uriOut);
					iGetRequest.putExtra("param", param);
					iGetRequest.putExtra("type", typeOut);
					startService(iGetRequest);
					isGetServiceRunning = true;
				}

				prlvOutContent.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvOutContent.onRefreshComplete();
					}
				}, 1000);
			}
		});
	}

	/**
	 * 加载数据
	 * */
	private void addData(String data) throws JSONException {
		tvTip.setVisibility(View.GONE);
		JSONTokener jasonParser1 = new JSONTokener(data);
		JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
		int length = jsonReturn1.length();
		if (length > 0) {
			lstOutData.clear();
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
				String notice_id = jsonReturn2.getString("notice_id");
				String goods_name = jsonReturn2.getString("goods_name");
				String spec_info = jsonReturn2.getString("spec_info");
				String email = jsonReturn2.getString("email");
				String tel = jsonReturn2.getString("tel");
				String content = jsonReturn2.getString("content");
				String do_time = jsonReturn2.getString("do_time");
				String store_name = jsonReturn2.getString("store_name");

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("tv_out_productName", goods_name);
				if ("".equals(spec_info)) {
					map.put("tv_out_Color", "无");
				} else {
					map.put("tv_out_Color", spec_info);
				}
				map.put("tv_out_userName", email);
				map.put("tv_out_phone", tel);
				map.put("tv_out_date", MyUtil.millisecondsToStr(do_time));
				map.put("tv_out_Info", content);
				map.put("notice_id", notice_id);
				map.put("store_name", store_name);
				lstOutData.add(map);
			}
			HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvOutContent.getRefreshableView().getAdapter();
			SimpleAdapter sa = (SimpleAdapter) hvla.getWrappedAdapter();
			sa.notifyDataSetChanged();
			prlvOutContent.onRefreshComplete();
		} else {
			tvTip.setText("您目前没有缺货信息");
			tvTip.setVisibility(View.VISIBLE);
		}

	}
	
	/**
	 * 加载数据
	 * */
	private void loadData(String data) throws JSONException {
		tvTip.setVisibility(View.GONE);
		JSONTokener jasonParser1 = new JSONTokener(data);
		JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
		int length = jsonReturn1.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
				String notice_id = jsonReturn2.getString("notice_id");
				String goods_name = jsonReturn2.getString("goods_name");
				String spec_info = jsonReturn2.getString("spec_info");
				String email = jsonReturn2.getString("email");
				String tel = jsonReturn2.getString("tel");
				String content = jsonReturn2.getString("content");
				String do_time = jsonReturn2.getString("do_time");
				String store_name = jsonReturn2.getString("store_name");

				HashMap<String, String> map = new HashMap<String, String>();
				map.put("tv_out_productName", goods_name);
				if ("".equals(spec_info)) {
					map.put("tv_out_Color", "无");
				} else {
					map.put("tv_out_Color", spec_info);
				}
				map.put("tv_out_userName", email);
				map.put("tv_out_phone", tel);
				map.put("tv_out_date", MyUtil.millisecondsToStr(do_time));
				map.put("tv_out_Info", content);
				map.put("notice_id", notice_id);
				map.put("store_name", store_name);
				lstOutData.add(map);
			}
			HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvOutContent.getRefreshableView().getAdapter();
			SimpleAdapter sa = (SimpleAdapter) hvla.getWrappedAdapter();
			sa.notifyDataSetChanged();
			prlvOutContent.onRefreshComplete();
		} else {
			tvTip.setVisibility(View.GONE);
		}

	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		OutOfStockActivity.this.registerReceiver(brGetHttp, filterGetHttp);

		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		OutOfStockActivity.this.registerReceiver(brPostHttp, filterPostHttp);
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
			} else if (MyConstant.HttpPostServiceAciton.equals(intent.getAction())) {
				String isSuccess = intent.getStringExtra("isSuccess");
				String strReturnType = intent.getStringExtra("type");
				if ("ok".equals(isSuccess)) {
					String result = intent.getStringExtra("result");
					try {
						parsePostData(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

		}

	}

	/**
	 * 解析返回数据get
	 * */
	private void parseGetData(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("delnotice".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				lstOutData.remove(delPosition);
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvOutContent.getRefreshableView().getAdapter();
				SimpleAdapter sa = (SimpleAdapter) hvla.getWrappedAdapter();
				sa.notifyDataSetChanged();
				prlvOutContent.onRefreshComplete();
				prlvOutContent.refreshDrawableState();
				MyUtil.ToastMessage(OutOfStockActivity.this, "ok");
			} else if ("1".equals(error)) {
				MyUtil.ToastMessage(OutOfStockActivity.this, msg);
			}
		} else if ("Out_refresh".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String data = jsonReturn.getString("data");
			addData(data);
		} else if ("Out_load".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String data = jsonReturn.getString("data");
			loadData(data);
		}
	}

	/**
	 * 解析返回数据post
	 * */
	private void parsePostData(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("send_mail".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(OutOfStockActivity.this, "发送成功");
			} else if ("1".equals(error)) {
				MyUtil.ToastMessage(OutOfStockActivity.this, msg);
			}
		}
	}

}
