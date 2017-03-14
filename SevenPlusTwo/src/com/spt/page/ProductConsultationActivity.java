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
import com.spt.adapter.ConsultationAdapter;
import com.spt.adapter.ConsultationAdapter.TvOnClickListener;
import com.spt.bean.ChatInfo;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 【产品咨询】页
 * */
public class ProductConsultationActivity extends BaseActivity {

	private MyTitleBar mtbProTitle;
	private TextView tvTitle;
	private ImageView ivLeft;
	private PullToRefreshListView prlvProContent;
	private Intent itProFrom;
	private TextView tvTip;
	private HashMap<String, Object> param;
	private String token;
	private BroadcastReceiver brPostHttp; // post方法广播
	private Intent iPostRequest; // post方法请求
	private boolean isPostServiceRunning = false;
	private BroadcastReceiver brGetHttp; // get方法广播
	private Intent iGetRequest; // get方法请求
	private boolean isGetServiceRunning = false;
	private LinearLayout llLeft;
	private ProgressDialog progressDialog;
	private LinearLayout llRightText;
	private String refresh_flag;// 0已回复，1未回复，2全部

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.productconsultation);
		super.onCreate(savedInstanceState);
		// 初始化内容
		initContent();
		// 添加数据
		try {
			String strData = itProFrom.getStringExtra("data");
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
		ProductConsultationActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		ProductConsultationActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(MyConstant.RESULTCODE_19);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		ProductConsultationActivity.this.registerReceiver(brPostHttp, filterPostHttp);

		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		ProductConsultationActivity.this.registerReceiver(brGetHttp, filterGetHttp);
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		// 【返回】
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(MyConstant.RESULTCODE_19);
				finish();
			}
		});
		// 【筛选】
		llRightText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showPopupWindow(v);
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtbProTitle = (MyTitleBar) findViewById(R.id.mtb_productConsultation_title);
		this.tvTitle = mtbProTitle.getTvTitle();
		this.ivLeft = mtbProTitle.getIvLeft();
		this.tvTitle.setText("产品咨询");
		this.mtbProTitle.setRightText("筛选");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbProTitle.getLlLeft();
		this.llRightText = mtbProTitle.getLlRightText();
		this.llRightText.setVisibility(View.VISIBLE);
		this.prlvProContent = (PullToRefreshListView) findViewById(R.id.prlv_productConsultation_content);
		this.prlvProContent.setMode(Mode.BOTH);
		this.itProFrom = getIntent();
		this.token = itProFrom.getStringExtra("token");
		this.tvTip = (TextView) findViewById(R.id.tv_productConsultation_tip);
		this.param = new HashMap<String, Object>();
		this.brPostHttp = new MyBroadCastReceiver(); // POST广播对象
		this.iPostRequest = new Intent(ProductConsultationActivity.this, MyHttpPostService.class); // 启动POST服务Intent对象
		this.iPostRequest.setAction(MyConstant.HttpPostServiceAciton); // 设置POSTAction
		this.brGetHttp = new MyBroadCastReceiver(); // GET广播对象
		this.iGetRequest = new Intent(ProductConsultationActivity.this, MyHttpGetService.class); // 启动GET服务Intent对象
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton); // 设置GETAction
		this.progressDialog = ProgressDialog.show(ProductConsultationActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.refresh_flag = "2";// 默认选择全部咨询
	}

	/**
	 * 初始化内容
	 * */
	private void initContent() {

		final ConsultationAdapter ca = new ConsultationAdapter(ProductConsultationActivity.this, new TvOnClickListener() {

			@Override
			public void myTvOnClickListener(final ChatInfo info, View v) {
				final TextView tv = (TextView) v;
				final ChatInfo chat = info;
				final String content = chat.getReply_content();
				System.out.println("content   " + content);
				final View vv = LayoutInflater.from(ProductConsultationActivity.this).inflate(R.layout.recallpopitem,
						null);
				AlertDialog.Builder builder = new AlertDialog.Builder(ProductConsultationActivity.this)
						.setTitle("回复评论").setView(vv).setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								EditText et = (EditText) vv.findViewById(R.id.et_pop_content);
								et.setText(content);
								String reply_content = et.getText().toString();
								tv.setText(reply_content);
								chat.setReply_content(reply_content);
								String ques_id = chat.getQues_id();
								if (!"".equals(reply_content)) {
									param.clear();
									param.put("token", token);
									param.put("ques_id", ques_id);
									param.put("reply_content", reply_content);
									String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa&act=reply";
									progressDialog.show();
									String type = "reply";
									iPostRequest.putExtra("uri", uri);
									iPostRequest.putExtra("param", param);
									iPostRequest.putExtra("type", type);
									startService(iPostRequest);
									isPostServiceRunning = true;
								}
							}
						});

				builder.show();
			}
		});

		prlvProContent.setAdapter(ca);

//		lvProContent.setonRefreshListener(new OnRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				param.clear();
//				param.put("token", token);
//				if ("0".equals(refresh_flag)) {// 已回复
//					param.put("reply", 1);
//				} else if ("1".equals(refresh_flag)) {// 未回复
//					param.put("no_reply", 1);
//				} else if ("2".equals(refresh_flag)) {// 全部
//					
//				}
//
//				String uriPro = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
//				String typePro = "Pro_refresh";
//				iGetRequest.putExtra("uri", uriPro);
//				iGetRequest.putExtra("param", param);
//				iGetRequest.putExtra("type", typePro);
//				startService(iGetRequest);
//				isGetServiceRunning = true;
//			}
//		});
		
		prlvProContent.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (prlvProContent.isHeaderShown()) {
					param.clear();
					param.put("token", token);
					if ("0".equals(refresh_flag)) {// 已回复
						param.put("reply", 1);
					} else if ("1".equals(refresh_flag)) {// 未回复
						param.put("no_reply", 1);
					} else if ("2".equals(refresh_flag)) {// 全部
						
					}

					String uriPro = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
					String typePro = "Pro_refresh";
					iGetRequest.putExtra("uri", uriPro);
					iGetRequest.putExtra("param", param);
					iGetRequest.putExtra("type", typePro);
					startService(iGetRequest);
					isGetServiceRunning = true;

				} else if (prlvProContent.isFooterShown()) {
					int size = ca.getCount();
					param.clear();
					param.put("token", token);
					param.put("offset", String.valueOf(size));
					if ("0".equals(refresh_flag)) {// 已回复
						param.put("reply", 1);
					} else if ("1".equals(refresh_flag)) {// 未回复
						param.put("no_reply", 1);
					} else if ("2".equals(refresh_flag)) {// 全部
						
					}

					String uriPro = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
					String typePro = "Pro_load";
					iGetRequest.putExtra("uri", uriPro);
					iGetRequest.putExtra("param", param);
					iGetRequest.putExtra("type", typePro);
					startService(iGetRequest);
					isGetServiceRunning = true;
				}

				prlvProContent.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvProContent.onRefreshComplete();
					}
				}, 1000);
			}
		});
		// 【长按删除】
		prlvProContent.getRefreshableView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				ChatInfo chat = (ChatInfo) parent.getItemAtPosition(position);
				final String ques_id = chat.getQues_id();
				AlertDialog alert = new AlertDialog.Builder(ProductConsultationActivity.this).setTitle("确认删除该咨询吗？")
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								param.put("token", token);
								param.put("ques_id", ques_id);
								String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa&act=del";
								progressDialog.show();
								String type = "del";
								iGetRequest.putExtra("uri", uri);
								iGetRequest.putExtra("param", param);
								iGetRequest.putExtra("type", type);
								startService(iGetRequest);
								isGetServiceRunning = true;
								param.clear();
							}
						}).create();
				alert.show();
				return false;
			}
		});

	}

	/**
	 * 加载数据
	 * */
	private void addData(String strData) throws JSONException {
		tvTip.setVisibility(View.GONE);
		HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvProContent.getRefreshableView().getAdapter();
		ConsultationAdapter ca = (ConsultationAdapter) hvla.getWrappedAdapter();
		ca.clear();
		ca.notifyDataSetChanged();
		JSONTokener jasonParser1 = new JSONTokener(strData);
		JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
		int length = jsonReturn1.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
				String ques_id = jsonReturn2.getString("ques_id");
				String question_content = jsonReturn2.getString("question_content");
				String user_id = jsonReturn2.getString("user_id");
				String user_name = MyUtil.dealNullString(jsonReturn2.getString("user_name"));
				if ("无".equals(user_name)) {
					user_name = "游客";
				}
				String item_id = jsonReturn2.getString("item_id");
				String item_name = jsonReturn2.getString("item_name");
				String reply_content = jsonReturn2.getString("reply_content");
				String time_post = jsonReturn2.getString("time_post");
				String time_reply = jsonReturn2.getString("time_reply");

				ChatInfo chat = new ChatInfo();
				chat.setQues_id(ques_id);
				chat.setQuestion_content(question_content);
				chat.setUser_id(user_id);
				chat.setUser_name(user_name);
				chat.setItem_id(item_id);
				chat.setItem_name(item_name);
				chat.setReply_content(reply_content);
				chat.setTime_post(MyUtil.millisecondsToStr(time_post));
				chat.setTime_reply(MyUtil.millisecondsToStr(time_reply));
				ca.addChatInfo(chat);
			}
			ca.notifyDataSetChanged();
			prlvProContent.onRefreshComplete();
		} else {
			tvTip.setText("您目前没有相关咨询信息");
			tvTip.setVisibility(View.VISIBLE);
		}

	}
	
	/**
	 * 加载数据
	 * */
	private void loadData(String strData) throws JSONException {
		tvTip.setVisibility(View.GONE);
		HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvProContent.getRefreshableView().getAdapter();
		ConsultationAdapter ca = (ConsultationAdapter) hvla.getWrappedAdapter();
		ca.notifyDataSetChanged();
		JSONTokener jasonParser1 = new JSONTokener(strData);
		JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
		int length = jsonReturn1.length();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
				String ques_id = jsonReturn2.getString("ques_id");
				String question_content = jsonReturn2.getString("question_content");
				String user_id = jsonReturn2.getString("user_id");
				String user_name = MyUtil.dealNullString(jsonReturn2.getString("user_name"));
				if ("无".equals(user_name)) {
					user_name = "游客";
				}
				String item_id = jsonReturn2.getString("item_id");
				String item_name = jsonReturn2.getString("item_name");
				String reply_content = jsonReturn2.getString("reply_content");
				String time_post = jsonReturn2.getString("time_post");
				String time_reply = jsonReturn2.getString("time_reply");

				ChatInfo chat = new ChatInfo();
				chat.setQues_id(ques_id);
				chat.setQuestion_content(question_content);
				chat.setUser_id(user_id);
				chat.setUser_name(user_name);
				chat.setItem_id(item_id);
				chat.setItem_name(item_name);
				chat.setReply_content(reply_content);
				chat.setTime_post(MyUtil.millisecondsToStr(time_post));
				chat.setTime_reply(MyUtil.millisecondsToStr(time_reply));
				ca.addChatInfo(chat);
			}
			ca.notifyDataSetChanged();
			prlvProContent.onRefreshComplete();
		} else {
			tvTip.setVisibility(View.GONE);
		}

	}

	private void showPopupWindow(View view) {

		// 一个自定义的布局，作为显示的内容
		View contentView = LayoutInflater.from(ProductConsultationActivity.this).inflate(R.layout.popitem, null);
		final PopupWindow popupWindow = new PopupWindow(contentView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		// 设置按钮的点击事件
		TextView tvRecalled = (TextView) contentView.findViewById(R.id.tv_popItem_recalled);
		TextView tvUnRecall = (TextView) contentView.findViewById(R.id.tv_popItem_unrecall);
		TextView tvAll = (TextView) contentView.findViewById(R.id.tv_popItem_all);
		tvRecalled.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// 已回复
				refresh_flag = "0";
				popupWindow.dismiss();
				
				param.clear();
				param.put("token", token);
				param.put("reply", 1);
				String uriPro = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
				String typePro = "Pro_refresh";
				iGetRequest.putExtra("uri", uriPro);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typePro);
				startService(iGetRequest);
				isGetServiceRunning = true;
			}
		});

		tvUnRecall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// 未回复
				refresh_flag = "1";
				popupWindow.dismiss();
				
				param.clear();
				param.put("token", token);
				param.put("no_reply", 1);
				String uriPro = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
				String typePro = "Pro_refresh";
				iGetRequest.putExtra("uri", uriPro);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typePro);
				startService(iGetRequest);
				isGetServiceRunning = true;
			}
		});

		tvAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// 全部
				refresh_flag = "2";
				popupWindow.dismiss();
				
				param.clear();
				param.put("token", token);
				String uriPro = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
				String typePro = "Pro_refresh";
				iGetRequest.putExtra("uri", uriPro);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typePro);
				startService(iGetRequest);
				isGetServiceRunning = true;
			}
		});

		popupWindow.setTouchable(true);

		// 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
		// 我觉得这里是API的一个bug
		popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_select));

		// 设置好参数之后再show
		popupWindow.showAsDropDown(view);

	}

	/**
	 * 内部广播类
	 * */
	private class MyBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyConstant.HttpPostServiceAciton.equals(intent.getAction())) {
				String isSuccess = intent.getStringExtra("isSuccess");
				String strReturnType = intent.getStringExtra("type");
				if ("ok".equals(isSuccess)) {
					String result = intent.getStringExtra("result");
					try {
						parseDataPost(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else if (MyConstant.HttpGetServiceAciton.equals(intent.getAction())) {
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
		progressDialog.dismiss();
		if ("del".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(ProductConsultationActivity.this, "删除成功");
				
				param.clear();
				param.put("token", token);
				if ("0".equals(refresh_flag)) {// 已回复
					param.put("reply", 1);
				} else if ("1".equals(refresh_flag)) {// 未回复
					param.put("no_reply", 1);
				} else if ("2".equals(refresh_flag)) {// 全部
					
				}
				String uriPro = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
				String typePro = "Pro_refresh";
				iGetRequest.putExtra("uri", uriPro);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typePro);
				startService(iGetRequest);
				isGetServiceRunning = true;
			} else {
				MyUtil.ToastMessage(ProductConsultationActivity.this, msg);
			}
		} else if ("Pro_refresh".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				String data = jsonReturn.getJSONArray("data").toString();
				addData(data);
			} else {
				MyUtil.ToastMessage(ProductConsultationActivity.this, msg);
			}
		} else if ("Pro_load".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				String data = jsonReturn.getJSONArray("data").toString();
				loadData(data);
			} else {
				MyUtil.ToastMessage(ProductConsultationActivity.this, msg);
			}
		}
	}

	/**
	 * 解析post返回数据
	 * */
	private void parseDataPost(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("reply".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(ProductConsultationActivity.this, "回复成功");
			} else {
				MyUtil.ToastMessage(ProductConsultationActivity.this, msg);
			}
		}
	}

}
