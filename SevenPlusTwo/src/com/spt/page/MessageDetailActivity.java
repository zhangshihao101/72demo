package com.spt.page;

import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 【待处理消息详情】页
 * */
public class MessageDetailActivity extends BaseActivity {

	private MyTitleBar mtbMessageDetailTitle;
	private TextView tvTitle;
	private ImageView ivLeft;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private Intent itFrom;
	private TextView tvMessageName;
	private String msg_id;
	private String token;
	private HashMap<String, Object> param;
	private ProgressDialog progressDialog;
	private Intent iGetRequest;
	private boolean isGetServiceRunning = false;
	private BroadcastReceiver brGetHttp;
	private int position;
	private LinearLayout llRecallContent;
	private RelativeLayout rlReply;
	private TextView tvReply;
	private EditText etReply;
	private Intent iPostRequest;
	private boolean isPostServiceRunning = false;
	private BroadcastReceiver brPostHttp;
	private LayoutInflater inflater;
	private HashMap<Integer, JSONObject> map;
	private int[] dates;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.messagedetail);
		super.onCreate(savedInstanceState);
		// 加载数据
		try {
			addData();
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
		MessageDetailActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}

		MessageDetailActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(MyConstant.RESULTCODE_25);
			MessageDetailActivity.this.finish();
			progressDialog.dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化
	 * */
	@SuppressLint("UseSparseArrays")
	@Override
	protected void init() {
		this.itFrom = getIntent();
		this.mtbMessageDetailTitle = (MyTitleBar) findViewById(R.id.mtb_messageDetail_title);
		this.tvTitle = mtbMessageDetailTitle.getTvTitle();
		this.ivLeft = mtbMessageDetailTitle.getIvLeft();
		this.tvTitle.setText("待处理消息详情");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbMessageDetailTitle.getLlLeft();
		this.llRight = mtbMessageDetailTitle.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.tvMessageName = (TextView) findViewById(R.id.tv_messageDetail_messageName);
		this.llRecallContent = (LinearLayout) findViewById(R.id.ll_messageDetail_content);
		this.token = itFrom.getStringExtra("token");
		this.param = new HashMap<String, Object>();
		this.progressDialog = ProgressDialog.show(MessageDetailActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.iGetRequest = new Intent(MessageDetailActivity.this, MyHttpGetService.class);
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
		this.brGetHttp = new MyBroadCastReceiver(); // GET广播对象
		this.iPostRequest = new Intent(MessageDetailActivity.this, MyHttpPostService.class);
		this.iPostRequest.setAction(MyConstant.HttpPostServiceAciton);
		this.brPostHttp = new MyBroadCastReceiver(); // POST广播对象
		this.position = itFrom.getIntExtra("position", 999999);
		this.tvReply = (TextView) findViewById(R.id.tv_messageDetail_reply);
		this.etReply = (EditText) findViewById(R.id.et_messageDetail_reply);
		this.inflater = LayoutInflater.from(MessageDetailActivity.this);
		this.map = new HashMap<Integer, JSONObject>();
		this.rlReply = (RelativeLayout) findViewById(R.id.rl_messageDetail_reply);
		this.rlReply.setVisibility(View.VISIBLE);
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(MyConstant.RESULTCODE_25);
				MessageDetailActivity.this.finish();
			}
		});

		// 【回复】
		this.tvReply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = etReply.getText().toString();
				if (!"".equals(content)) {
					View view = inflater.inflate(R.layout.recall_item, null);
					TextView tvContent = (TextView) view.findViewById(R.id.tv_recallItem_Content);
					TextView tvDate = (TextView) view.findViewById(R.id.tv_recallItem_date);
					tvContent.setText(content);
					tvDate.setText(makeDate());
					llRecallContent.addView(view);
					etReply.setText("");

					param.clear();
					param.put("token", token);
					param.put("msg_id", Integer.parseInt(msg_id));
					param.put("msg_content", content);
					System.out.println("msg_id  " + msg_id);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=pm&act=reply_message";
					String type = "reply_message";
					progressDialog.show();
					iPostRequest.putExtra("uri", uri);
					iPostRequest.putExtra("param", param);
					iPostRequest.putExtra("type", type);
					startService(iPostRequest);
					isPostServiceRunning = true;
					param.clear();
				}
			}
		});

		// this.btnCheckMessage.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// param.clear();
		// param.put("token", token);
		// param.put("msg_id", Integer.parseInt(msg_id));
		// System.out.println("msg_id  " + msg_id);
		// String uri = MyConstant.SERVICENAME
		// + "/index.php?pf=m_seller&app=pm&act=view";
		// String type = "checkMessage";
		// progressDialog.show();
		// iGetRequest.putExtra("uri", uri);
		// iGetRequest.putExtra("param", param);
		// iGetRequest.putExtra("type", type);
		// startService(iGetRequest);
		// isGetServiceRunning = true;
		// param.clear();
		// }
		// });
	}

	/**
	 * 获取当前日期时间
	 * */
	private String makeDate() {
		StringBuffer sb = new StringBuffer();
		String current = MyUtil.getCurrentDate();
		sb.append(current.substring(0, 4)).append("-").append(current.substring(4, 6)).append("-")
				.append(current.substring(6, 8)).append(" ").append(current.substring(8, 10)).append(":")
				.append(current.substring(10, 12)).append(":").append(current.substring(12, 14));
		return sb.toString();
	}

	/**
	 * 添加数据
	 * */
	private void addData() throws JSONException {
		String data = itFrom.getStringExtra("data");
		JSONTokener jasonParser = new JSONTokener(data);
		JSONObject obj = (JSONObject) jasonParser.nextValue();
		JSONObject topic = obj.getJSONObject("topic");// 主题消息
		JSONArray replies = obj.getJSONArray("replies");// 回复消息数组

		String messageName = topic.getString("user_name");
		String fromId = topic.getString("from_id");
		String content = topic.getString("content");
		String addTime = topic.getString("add_time");
		msg_id = topic.getString("msg_id");
		if ("系统消息".equals(messageName)) {
			tvMessageName.setTextColor(Color.rgb(51, 120, 57));
			tvMessageName.setText(messageName);
			this.rlReply.setVisibility(View.GONE);
		} else {
			tvMessageName.setTextColor(Color.rgb(0, 0, 0));
			tvMessageName.setText(messageName);
			this.rlReply.setVisibility(View.VISIBLE);
		}
		View view = inflater.inflate(R.layout.ask_item, null);
		TextView tvContent = (TextView) view.findViewById(R.id.tv_askItem_Content);
		TextView tvDate = (TextView) view.findViewById(R.id.tv_askItem_date);
		tvContent.setText(content);
		tvDate.setText(MyUtil.millisecondsToStr(addTime));
		llRecallContent.addView(view);// 添加第一条消息

		// 添加消息数组
		int length = replies.length();
		if (length > 0) {
			dates = new int[length];
			for (int i = 0; i < length; i++) {
				JSONObject reply = replies.getJSONObject(i);// 每条回复

				int reply_addTime = Integer.parseInt(reply.getString("add_time"));
				map.put(reply_addTime, reply);
				dates[i] = reply_addTime;
			}

			Arrays.sort(dates);
			for (int i = 0; i < dates.length; i++) {
				JSONObject reply = map.get(dates[i]);// 每条回复
				String reply_fromId = reply.getString("from_id");
				String reply_content = reply.getString("content");
				String reply_addTime = reply.getString("add_time");
				if (reply_fromId.equals(fromId)) {
					View ask = inflater.inflate(R.layout.ask_item, null);
					TextView ask_content = (TextView) ask.findViewById(R.id.tv_askItem_Content);
					TextView ask_date = (TextView) ask.findViewById(R.id.tv_askItem_date);
					ask_content.setText(reply_content);
					ask_date.setText(MyUtil.millisecondsToStr(reply_addTime));
					llRecallContent.addView(ask);// 添加消息
				} else {
					View answer = inflater.inflate(R.layout.recall_item, null);
					TextView answer_content = (TextView) answer.findViewById(R.id.tv_recallItem_Content);
					TextView answer_date = (TextView) answer.findViewById(R.id.tv_recallItem_date);
					answer_content.setText(reply_content);
					answer_date.setText(MyUtil.millisecondsToStr(reply_addTime));
					llRecallContent.addView(answer);// 添加消息
				}
			}
		}

	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		MessageDetailActivity.this.registerReceiver(brGetHttp, filterGetHttp);

		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		MessageDetailActivity.this.registerReceiver(brPostHttp, filterPostHttp);
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
			} else if (MyConstant.HttpPostServiceAciton.equals(intent.getAction())) {
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
			}

		}

	}

	/**
	 * 解析get返回数据
	 * */
	private void parseDataGet(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("checkMessage".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				Intent it = new Intent();
				it.putExtra("position", position);
				setResult(MyConstant.RESULTCODE_25, it);
				MessageDetailActivity.this.finish();
			} else {
				MyUtil.ToastMessage(MessageDetailActivity.this, msg);
			}
		}
	}

	/**
	 * 解析post返回数据
	 * */
	private void parseDataPost(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("reply_message".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(MessageDetailActivity.this, "回复成功");
			} else {
				MyUtil.ToastMessage(MessageDetailActivity.this, msg);
			}
		}
	}

}
