package com.spt.page;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.spt.adapter.MessageAdapter;
import com.spt.adapter.MessageDeleteAdapter;
import com.spt.bean.MessageDeleteInfo;
import com.spt.bean.MessageInfo;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;

import android.app.AlertDialog;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 【待处理消息】页
 * */
public class MessageActivity extends BaseActivity {

	private MyTitleBar mtbMessageTitle;
	private TextView tvTitle;
	private ImageView ivLeft;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private PullToRefreshListView prlvMessageContent;
	private Intent itMessageFrom;
	private TextView tvTip;
	private String token;
	private Intent iGetRequest;
	private BroadcastReceiver brGetHttp;
	private boolean isGetServiceRunning = false;
	private HashMap<String, String> param;
	private ListView lvMessageDelete;
	private LinearLayout llOperatior;
	private LinearLayout llRightText;
	private LinearLayout llDelete;
	private CheckBox cbSelectAll;
	private HashMap<String, String> map;
	private String msgIds;
//	private String changeDel_flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.message);
		super.onCreate(savedInstanceState);
		// 初始化界面
		initContent();
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
		MessageActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(MyConstant.RESULTCODE_18);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case MyConstant.RESULTCODE_25:
			// if (data.hasExtra("position")) {
			// int location = data.getIntExtra("position", 999999);
			// HeaderViewListAdapter hvla = (HeaderViewListAdapter)
			// prlvMessageContent.getAdapter();
			// MessageAdapter ma = (MessageAdapter) hvla.getWrappedAdapter();
			// int size = ma.getCount();
			// if (location < size) {
			// ma.deleteMessageInfo(location);
			// prlvMessageContent.onRefreshComplete();
			// }
			// }
			param.clear();
			param.put("token", token);
			param.put("type", "all");
			String uriMessage = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=pm";
			String typeMessage = "message_refresh";
			iGetRequest.putExtra("uri", uriMessage);
			iGetRequest.putExtra("param", param);
			iGetRequest.putExtra("type", typeMessage);
			startService(iGetRequest);
			isGetServiceRunning = true;
			break;
		}
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(MyConstant.RESULTCODE_18);
				finish();
			}
		});

		this.llRightText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String text = mtbMessageTitle.getRightText();
				if ("删除".equals(text)) {
					mtbMessageTitle.setRightText("完成");
					lvMessageDelete.setVisibility(View.VISIBLE);
					prlvMessageContent.setVisibility(View.GONE);
					llOperatior.setVisibility(View.VISIBLE);
//					changeDel_flag = "delete";

					// param.clear();
					// param.put("token", token);
					// param.put("type", "all");
					// String uriMessage = MyConstant.SERVICENAME +
					// "/index.php?pf=m_seller&app=pm";
					// String typeMessage = "message_refresh_del";
					// iGetRequest.putExtra("uri", uriMessage);
					// iGetRequest.putExtra("param", param);
					// iGetRequest.putExtra("type", typeMessage);
					// startService(iGetRequest);
					// isGetServiceRunning = true;
				} else if ("完成".equals(text)) {
					mtbMessageTitle.setRightText("删除");
					lvMessageDelete.setVisibility(View.GONE);
					prlvMessageContent.setVisibility(View.VISIBLE);
					llOperatior.setVisibility(View.GONE);
//					changeDel_flag = "complete";

					// param.clear();
					// param.put("token", token);
					// param.put("type", "all");
					// String uriMessage = MyConstant.SERVICENAME +
					// "/index.php?pf=m_seller&app=pm";
					// String typeMessage = "message_refresh";
					// iGetRequest.putExtra("uri", uriMessage);
					// iGetRequest.putExtra("param", param);
					// iGetRequest.putExtra("type", typeMessage);
					// startService(iGetRequest);
					// isGetServiceRunning = true;
				}
			}
		});
		// 【删除】
		this.llDelete.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onClick(View v) {
				if (!map.isEmpty()) {
					StringBuffer sb = new StringBuffer();
					for (Iterator<?> it = map.entrySet().iterator(); it.hasNext();) {
						Map.Entry e = (Entry) it.next();
						String value = e.getValue().toString();
						System.out.println("value" + value);
						sb.append(value).append(",");
						System.out.println("value + " + sb.toString());
					}
					msgIds = sb.toString();// 将所有待删除的msg_id拼接成逗号分割的字符串
					msgIds = msgIds.substring(0, msgIds.length() - 1);// 由于多拼了一个逗号，要去掉
					System.out.println("msgIds" + msgIds);

					AlertDialog dialog = new AlertDialog.Builder(MessageActivity.this).setMessage("确定删除这几项吗？")
							.setNegativeButton("取消", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									param.clear();
									param.put("token", token);
									param.put("msg_id", msgIds);
									String uriMessage = MyConstant.SERVICENAME
											+ "/index.php?pf=m_seller&app=pm&act=del";
									String typeMessage = "message_del";
									iGetRequest.putExtra("uri", uriMessage);
									iGetRequest.putExtra("param", param);
									iGetRequest.putExtra("type", typeMessage);
									startService(iGetRequest);
									isGetServiceRunning = true;
									msgIds = "";
									map.clear();

								}
							}).create();

					dialog.show();
				} else {
					MyUtil.ToastMessage(MessageActivity.this, "没有可删除的项");
				}
			}
		});
		// 【全选】
		this.cbSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// HeaderViewListAdapter hvla = (HeaderViewListAdapter)
				// lvMessageDelete.getAdapter();
				MessageDeleteAdapter mda = (MessageDeleteAdapter) lvMessageDelete.getAdapter();
				int count = mda.getCount();
				if (count > 0) {
					if (!isChecked) {// 反选
						for (int i = 0; i < count; i++) {
							MessageDeleteInfo info = mda.getMessageDeleteInfo(i);
							info.setChecked(false);
						}
						map.clear();
						mda.notifyDataSetChanged();
					} else {// 全选
						for (int i = 0; i < count; i++) {
							MessageDeleteInfo info = mda.getMessageDeleteInfo(i);
							info.setChecked(true);
							String msg_id = info.getMsg_id();
							map.put(msg_id, msg_id);
						}
						mda.notifyDataSetChanged();
					}
				}
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtbMessageTitle = (MyTitleBar) findViewById(R.id.mtb_message_title);
		this.tvTitle = mtbMessageTitle.getTvTitle();
		this.ivLeft = mtbMessageTitle.getIvLeft();
		this.tvTitle.setText("待处理消息");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbMessageTitle.getLlLeft();
		this.llRight = mtbMessageTitle.getLlRight();
		this.llRight.setVisibility(View.GONE);
		this.llRightText = mtbMessageTitle.getLlRightText();
		this.llRightText.setVisibility(View.VISIBLE);
		this.mtbMessageTitle.setRightText("删除");
		this.prlvMessageContent = (PullToRefreshListView) findViewById(R.id.prlv_message_content);
		this.prlvMessageContent.setMode(Mode.BOTH);
		this.itMessageFrom = getIntent();
		this.tvTip = (TextView) findViewById(R.id.tv_message_tip);
		this.token = itMessageFrom.getStringExtra("token");
		this.iGetRequest = new Intent(MessageActivity.this, MyHttpGetService.class); // 启动GET服务Intent对象
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton); // 设置GETAction
		this.brGetHttp = new MyBroadCastReceiver();
		this.param = new HashMap<String, String>();
		this.lvMessageDelete = (ListView) findViewById(R.id.lv_message_delete);
		this.llOperatior = (LinearLayout) findViewById(R.id.ll_message_operatior);
		this.llDelete = (LinearLayout) findViewById(R.id.ll_message_delete);
		this.cbSelectAll = (CheckBox) findViewById(R.id.cb_message_selectAll);
		this.map = new HashMap<String, String>();
	}

	/**
	 * 初始化界面
	 * */
	private void initContent() {

		final MessageAdapter ma = new MessageAdapter(MessageActivity.this);

		prlvMessageContent.setAdapter(ma);

		prlvMessageContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MessageInfo info = (MessageInfo) parent.getItemAtPosition(position);
				final String msgId = info.getMsg_id();
				String name = info.getUser_name();
				if ("系统消息".equals(name)) {
					String content = info.getContent();
					View vview = LayoutInflater.from(MessageActivity.this).inflate(R.layout.systeminfo_show, null);
					TextView tvInfo = (TextView) vview.findViewById(R.id.tv_sysInfo_content);
					tvInfo.setText(content);
					AlertDialog dialog = new AlertDialog.Builder(MessageActivity.this).setTitle("系统消息").setView(vview)
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									param.clear();
									param.put("token", token);
									param.put("msg_id", msgId);
									String uriMessage = MyConstant.SERVICENAME
											+ "/index.php?pf=m_seller&app=pm&act=view";
									String typeMessage = "message_sys_view";
									iGetRequest.putExtra("uri", uriMessage);
									iGetRequest.putExtra("param", param);
									iGetRequest.putExtra("type", typeMessage);
									startService(iGetRequest);
									isGetServiceRunning = true;
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							}).create();
					dialog.show();
				} else {
					param.clear();
					param.put("token", token);
					param.put("msg_id", msgId);
					String uriMessage = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=pm&act=view";
					String typeMessage = "message_view";
					iGetRequest.putExtra("uri", uriMessage);
					iGetRequest.putExtra("param", param);
					iGetRequest.putExtra("type", typeMessage);
					startService(iGetRequest);
					isGetServiceRunning = true;
				}
			}
		});

		// prlvMessageContent.setonRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		// param.clear();
		// param.put("token", token);
		// param.put("type", "all");
		// String uriMessage = MyConstant.SERVICENAME +
		// "/index.php?pf=m_seller&app=pm";
		// String typeMessage = "message_refresh";
		// iGetRequest.putExtra("uri", uriMessage);
		// iGetRequest.putExtra("param", param);
		// iGetRequest.putExtra("type", typeMessage);
		// startService(iGetRequest);
		// isGetServiceRunning = true;
		// }
		// });
		//
		// prlvMessageContent.setonLoadListener(new OnLoadListener() {
		//
		// @Override
		// public void onLoad() {
		// int size = ma.getCount();
		// param.clear();
		// param.put("token", token);
		// param.put("type", "all");
		// param.put("offset", String.valueOf(size - 1));
		// String uriMessage = MyConstant.SERVICENAME +
		// "/index.php?pf=m_seller&app=pm";
		// String typeMessage = "message_load";
		// iGetRequest.putExtra("uri", uriMessage);
		// iGetRequest.putExtra("param", param);
		// iGetRequest.putExtra("type", typeMessage);
		// startService(iGetRequest);
		// isGetServiceRunning = true;
		// }
		// });

		prlvMessageContent.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				if (prlvMessageContent.isHeaderShown()) {
					param.clear();
					param.put("token", token);
					param.put("type", "all");
					String uriMessage = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=pm";
					String typeMessage = "message_refresh";
					iGetRequest.putExtra("uri", uriMessage);
					iGetRequest.putExtra("param", param);
					iGetRequest.putExtra("type", typeMessage);
					startService(iGetRequest);
					isGetServiceRunning = true;

				} else if (prlvMessageContent.isFooterShown()) {
					int size = ma.getCount();
					System.out.println(size);
					param.clear();
					param.put("token", token);
					param.put("type", "all");
					param.put("offset", String.valueOf(size));
					String uriMessage = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=pm";
					String typeMessage = "message_load";
					iGetRequest.putExtra("uri", uriMessage);
					iGetRequest.putExtra("param", param);
					iGetRequest.putExtra("type", typeMessage);
					startService(iGetRequest);
					isGetServiceRunning = true;
				}

				prlvMessageContent.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvMessageContent.onRefreshComplete();
					}
				}, 1000);

			}
		});

		final MessageDeleteAdapter mda = new MessageDeleteAdapter(MessageActivity.this);

		lvMessageDelete.setAdapter(mda);

		lvMessageDelete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				MessageDeleteInfo info = (MessageDeleteInfo) parent.getItemAtPosition(position);
				boolean bl = info.isChecked();
				if (bl) {
					System.out.println("click true");
					info.setChecked(false);
					mda.notifyDataSetChanged();
					String msg_id = info.getMsg_id();
					if (map.containsKey(msg_id)) {
						map.remove(msg_id);
					}
				} else {
					System.out.println("click false");
					info.setChecked(true);
					mda.notifyDataSetChanged();
					String msg_id = info.getMsg_id();
					map.put(msg_id, msg_id);
				}
			}
		});

		// lvMessageDelete.setonRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		// param.clear();
		// param.put("token", token);
		// param.put("type", "all");
		// String uriMessage = MyConstant.SERVICENAME +
		// "/index.php?pf=m_seller&app=pm";
		// String typeMessage = "message_refresh_del";
		// iGetRequest.putExtra("uri", uriMessage);
		// iGetRequest.putExtra("param", param);
		// iGetRequest.putExtra("type", typeMessage);
		// startService(iGetRequest);
		// isGetServiceRunning = true;
		// }
		// });
	}

	/**
	 * 加载数据
	 * */
	private void addData() throws JSONException {
		tvTip.setVisibility(View.GONE);
		String data = itMessageFrom.getStringExtra("data");
		JSONTokener jasonParser = new JSONTokener(data);
		JSONArray jsonReturn = (JSONArray) jasonParser.nextValue();
		int length = jsonReturn.length();
		HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvMessageContent.getRefreshableView().getAdapter();
		MessageAdapter ma = (MessageAdapter) hvla.getWrappedAdapter();
		ma.clear();
		ma.notifyDataSetChanged();
		// HeaderViewListAdapter hvla1 = (HeaderViewListAdapter)
		// lvMessageDelete.getAdapter();
		MessageDeleteAdapter mda = (MessageDeleteAdapter) lvMessageDelete.getAdapter();
		mda.clear();
		mda.notifyDataSetChanged();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn1 = jsonReturn.getJSONObject(i);
				String msg_id = jsonReturn1.getString("msg_id");
				String content = jsonReturn1.getString("content");
				String add_time = jsonReturn1.getString("add_time");
				String portrait = jsonReturn1.getString("portrait");
				String user_name = jsonReturn1.getString("user_name");

				MessageInfo info = new MessageInfo();
				info.setMsg_id(msg_id);
				info.setContent(content);
				info.setUser_name(user_name);
				info.setAdd_time(MyUtil.millisecondsToStr(add_time));
				info.setPortrait(portrait);
				ma.addMessageInfo(info);

				MessageDeleteInfo delInfo = new MessageDeleteInfo();
				delInfo.setMsg_id(msg_id);
				delInfo.setContent(content);
				delInfo.setUser_name(user_name);
				delInfo.setAdd_time(MyUtil.millisecondsToStr(add_time));
				delInfo.setPortrait(portrait);
				delInfo.setChecked(false);
				mda.addMessageDeleteInfo(delInfo);
			}
			ma.notifyDataSetChanged();
			prlvMessageContent.onRefreshComplete();
			mda.notifyDataSetChanged();
			// lvMessageDelete.onRefreshComplete();
		} else {
			tvTip.setText("您好！您暂时无处理消息！");
			tvTip.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 加载数据
	 * */
	private void loadData(String data) throws JSONException {
		tvTip.setVisibility(View.GONE);
		JSONTokener jasonParser = new JSONTokener(data);
		JSONArray jsonReturn = (JSONArray) jasonParser.nextValue();
		int length = jsonReturn.length();
		HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvMessageContent.getRefreshableView().getAdapter();
		MessageAdapter ma = (MessageAdapter) hvla.getWrappedAdapter();
		ma.notifyDataSetChanged();
		// HeaderViewListAdapter hvla1 = (HeaderViewListAdapter)
		// lvMessageDelete.getAdapter();
		MessageDeleteAdapter mda = (MessageDeleteAdapter) lvMessageDelete.getAdapter();
		mda.notifyDataSetChanged();
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				JSONObject jsonReturn1 = jsonReturn.getJSONObject(i);
				String msg_id = jsonReturn1.getString("msg_id");
				String content = jsonReturn1.getString("content");
				String add_time = jsonReturn1.getString("add_time");
				String portrait = jsonReturn1.getString("portrait");
				String user_name = jsonReturn1.getString("user_name");

				MessageInfo info = new MessageInfo();
				info.setMsg_id(msg_id);
				info.setContent(content);
				info.setUser_name(user_name);
				info.setAdd_time(MyUtil.millisecondsToStr(add_time));
				info.setPortrait(portrait);
				ma.addMessageInfo(info);

				MessageDeleteInfo delInfo = new MessageDeleteInfo();
				delInfo.setMsg_id(msg_id);
				delInfo.setContent(content);
				delInfo.setUser_name(user_name);
				delInfo.setAdd_time(MyUtil.millisecondsToStr(add_time));
				delInfo.setPortrait(portrait);
				delInfo.setChecked(false);
				mda.addMessageDeleteInfo(delInfo);
			}
			ma.notifyDataSetChanged();
			prlvMessageContent.onRefreshComplete();
			mda.notifyDataSetChanged();
			// lvMessageDelete.onRefreshComplete();
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
		MessageActivity.this.registerReceiver(brGetHttp, filterGetHttp);
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
		if ("message_refresh".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jasonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();
			String data = jsonReturn.getJSONArray("data").toString();
			JSONTokener jasonParser1 = new JSONTokener(data);
			JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
			int length = jsonReturn1.length();
			HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvMessageContent.getRefreshableView().getAdapter();
			MessageAdapter ma = (MessageAdapter) hvla.getWrappedAdapter();
			ma.clear();
			ma.notifyDataSetChanged();
			MessageDeleteAdapter mda = (MessageDeleteAdapter) lvMessageDelete.getAdapter();
			mda.clear();
			mda.notifyDataSetChanged();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
					String msg_id = jsonReturn2.getString("msg_id");
					String content = jsonReturn2.getString("content");
					String add_time = jsonReturn2.getString("add_time");
					String portrait = jsonReturn2.getString("portrait");
					String user_name = jsonReturn2.getString("user_name");
					
					MessageInfo info = new MessageInfo();
					info.setMsg_id(msg_id);
					info.setContent(content);
					info.setUser_name(user_name);
					info.setAdd_time(MyUtil.millisecondsToStr(add_time));
					info.setPortrait(portrait);
					ma.addMessageInfo(info);
					
					
					MessageDeleteInfo delInfo = new MessageDeleteInfo();
					delInfo.setMsg_id(msg_id);
					delInfo.setContent(content);
					delInfo.setUser_name(user_name);
					delInfo.setAdd_time(MyUtil.millisecondsToStr(add_time));
					delInfo.setPortrait(portrait);
					mda.addMessageDeleteInfo(delInfo);
				}
				ma.notifyDataSetChanged();
				mda.notifyDataSetChanged();
				prlvMessageContent.onRefreshComplete();
			} else {
				tvTip.setText("您好！您暂时无处理消息！");
				tvTip.setVisibility(View.VISIBLE);
			}
		} else if ("message_refresh_del".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jasonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();
			String data = jsonReturn.getJSONArray("data").toString();
			JSONTokener jasonParser1 = new JSONTokener(data);
			JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
			int length = jsonReturn1.length();
			// HeaderViewListAdapter hvla1 = (HeaderViewListAdapter)
			// lvMessageDelete.getAdapter();
			MessageDeleteAdapter mda = (MessageDeleteAdapter) lvMessageDelete.getAdapter();
			mda.clear();
			mda.notifyDataSetChanged();
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
					String msg_id = jsonReturn2.getString("msg_id");
					String content = jsonReturn2.getString("content");
					String add_time = jsonReturn2.getString("add_time");
					String portrait = jsonReturn2.getString("portrait");
					String user_name = jsonReturn2.getString("user_name");
					MessageDeleteInfo delInfo = new MessageDeleteInfo();
					delInfo.setMsg_id(msg_id);
					delInfo.setContent(content);
					delInfo.setUser_name(user_name);
					delInfo.setAdd_time(MyUtil.millisecondsToStr(add_time));
					delInfo.setPortrait(portrait);
					mda.addMessageDeleteInfo(delInfo);
				}
				mda.notifyDataSetChanged();
				// lvMessageDelete.onRefreshComplete();
			} else {
				tvTip.setText("您好！您暂时无处理消息！");
				tvTip.setVisibility(View.VISIBLE);
			}
		} else if ("message_view".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jasonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();
			String error = jsonReturn.getString("error");
			if ("0".equals(error)) {
				String data = jsonReturn.getJSONObject("data").toString();
				Intent it = new Intent(MessageActivity.this, MessageDetailActivity.class);
				it.putExtra("token", token);
				it.putExtra("data", data);
				startActivityForResult(it, MyConstant.RESULTCODE_25);
			}
		} else if ("message_sys_view".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jasonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();
			String error = jsonReturn.getString("error");
			if ("0".equals(error)) {
			}
		} else if ("message_del".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jasonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();
			String error = jsonReturn.getString("error");
			if ("0".equals(error)) {// 删除成功刷新列表
			// if ("complete".equals(changeDel_flag)) {
				param.clear();
				param.put("token", token);
				param.put("type", "all");
				String uriMessage = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=pm";
				String typeMessage = "message_refresh";
				iGetRequest.putExtra("uri", uriMessage);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typeMessage);
				startService(iGetRequest);
				isGetServiceRunning = true;

//				param.clear();
//				param.put("token", token);
//				param.put("type", "all");
//				String uriMessage1 = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=pm";
//				String typeMessage1 = "message_refresh_del";
//				iGetRequest.putExtra("uri", uriMessage1);
//				iGetRequest.putExtra("param", param);
//				iGetRequest.putExtra("type", typeMessage1);
//				startService(iGetRequest);
//				isGetServiceRunning = true;
				// } else if ("delete".equals(changeDel_flag)) {
				// param.clear();
				// param.put("token", token);
				// param.put("type", "all");
				// String uriMessage = MyConstant.SERVICENAME +
				// "/index.php?pf=m_seller&app=pm";
				// String typeMessage = "message_refresh_del";
				// iGetRequest.putExtra("uri", uriMessage);
				// iGetRequest.putExtra("param", param);
				// iGetRequest.putExtra("type", typeMessage);
				// startService(iGetRequest);
				// isGetServiceRunning = true;
				// }

			}
		} else if ("message_load".equals(type)) {
			tvTip.setVisibility(View.GONE);
			JSONTokener jasonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jasonParser.nextValue();
			String error = jsonReturn.getString("error");
			if ("0".equals(error)) {// 删除成功刷新列表
				String data = jsonReturn.getString("data");
				loadData(data);
			}
		}
	}
}
