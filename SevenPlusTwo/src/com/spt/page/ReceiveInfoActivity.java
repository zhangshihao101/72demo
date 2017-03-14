package com.spt.page;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 【收货信息】类
 * */
public class ReceiveInfoActivity extends BaseActivity {

	private MyTitleBar mtbReceiveInfo;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Button btnReceiveInfoOk;
	private Intent itFrom;
	private TextView tvReceiver;
	private TextView tvReceiverPhone;
	private EditText etReceiverAdress;
	private String adress;
	private String city;
	private String regionId;
	private String adressDetail;
	private boolean isPostServiceRunning = false;
	private BroadcastReceiver brPostHttp; // get方法广播
	private Intent iPostRequest;
	private HashMap<String, Object> param;
	private String oldReceiver;
	private String oldReceiverPhone;
	private String oldReceiveAdress;
	private String state;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private LinearLayout llCity;
	private ProgressDialog progressDialog;
	private TextView tvCityTip;
	private String region_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.receiveinfo);
		super.onCreate(savedInstanceState);
		initContent();
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver
		super.onStart();
	}

	@Override
	protected void onStop() {
		ReceiveInfoActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		super.onStop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			adress = etReceiverAdress.getText().toString();
			adressDetail = city + adress;
			Intent data = new Intent();
			data.putExtra("Receiver", tvReceiver.getText().toString());
			data.putExtra("ReceiverPhone", tvReceiverPhone.getText().toString());
			data.putExtra("ReceiverAdress", adressDetail);
			if ("send".equals(state)) {
				setResult(MyConstant.RESULTCODE_35, data);
			} else {
				setResult(107, data);
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case MyConstant.RESULTCODE_34:
			city = data.getStringExtra("area");
			regionId = data.getStringExtra("region_id");
			tvCityTip.setText(city);
			break;
		}
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adress = etReceiverAdress.getText().toString();
				Intent data = new Intent();
				data.putExtra("Receiver", tvReceiver.getText().toString());
				data.putExtra("ReceiverPhone", tvReceiverPhone.getText().toString());
				data.putExtra("ReceiverAdress", adress);
				if ("send".equals(state)) {
					setResult(MyConstant.RESULTCODE_35, data);
				} else {
					setResult(107, data);
				}
				finish();

			}
		});

		btnReceiveInfoOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				adress = etReceiverAdress.getText().toString();
				if (!"".equals(city) && city != null && !"".equals(regionId) && regionId != null) {
					String[] regionIds = regionId.split(" ");
//					String[] citys = city.split(" ");
					param.clear();
					String token = itFrom.getStringExtra("token");
					String order_id = itFrom.getStringExtra("order_id");
					param.put("token", token);
					param.put("order_id", order_id);
					param.put("ext_region_id_1", regionIds[0]);
					param.put("ext_region_id_2", regionIds[1]);
					param.put("address", adress);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=upaddress";
					progressDialog.show();
					String type = "upaddress";
					iPostRequest.putExtra("uri", uri);
					iPostRequest.putExtra("param", param);
					iPostRequest.putExtra("type", type);
					startService(iPostRequest);
					isPostServiceRunning = true;
				} else {
					MyUtil.ToastMessage(ReceiveInfoActivity.this, "请选择您所在城市");
				}
			}
		});

		llCity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(ReceiveInfoActivity.this, AdressManageActivity.class);
				startActivityForResult(it, MyConstant.RESULTCODE_34);
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtbReceiveInfo = (MyTitleBar) findViewById(R.id.mtb_receiveInfo_title);
		this.tvTitle = mtbReceiveInfo.getTvTitle();
		this.ivLeft = mtbReceiveInfo.getIvLeft();
		this.tvTitle.setText("收货信息");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbReceiveInfo.getLlLeft();
		this.llRight = mtbReceiveInfo.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.btnReceiveInfoOk = (Button) findViewById(R.id.btn_receiveInfo_ok);
		this.itFrom = getIntent();
		this.state = itFrom.getStringExtra("state");
		this.tvCityTip = (TextView) findViewById(R.id.tv_receiveInfo_cityTip);
		this.tvReceiver = (TextView) findViewById(R.id.tv_receiveInfo_receiver);
		this.tvReceiverPhone = (TextView) findViewById(R.id.tv_receiveInfo_receiverPhone);
		this.etReceiverAdress = (EditText) findViewById(R.id.et_receiveInfo_receiverAdress);
		this.brPostHttp = new MyBroadCastReceiver();
		this.param = new HashMap<String, Object>();
		this.oldReceiver = itFrom.getStringExtra("Receiver");
		this.oldReceiverPhone = itFrom.getStringExtra("ReceiverPhone");
		this.oldReceiveAdress = itFrom.getStringExtra("ReceiveAdress");
		this.region_name = itFrom.getStringExtra("region_name");
		this.tvReceiver.setText(oldReceiver);
		this.tvReceiverPhone.setText(oldReceiverPhone);
		this.etReceiverAdress.setText(oldReceiveAdress);
		this.tvCityTip.setText(region_name);
		this.llCity = (LinearLayout) findViewById(R.id.ll_receiveInfo_city);
		this.iPostRequest = new Intent(ReceiveInfoActivity.this, MyHttpPostService.class); // 启动POST服务Intent对象
		this.iPostRequest.setAction(MyConstant.HttpPostServiceAciton); // 设置POSTAction
		this.progressDialog = ProgressDialog.show(ReceiveInfoActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
	}

	/**
	 * 初始化内容
	 * */
	private void initContent() {
		tvReceiver.setText(itFrom.getStringExtra("Receiver"));
		tvReceiverPhone.setText(itFrom.getStringExtra("ReceivePhone"));
		etReceiverAdress.setText(itFrom.getStringExtra("ReceiveAddress"));
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		ReceiveInfoActivity.this.registerReceiver(brPostHttp, filterPostHttp);
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
		if ("upaddress".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				adress = etReceiverAdress.getText().toString();
				adressDetail = city + adress;
				Intent it = new Intent();
				it.putExtra("Receiver", tvReceiver.getText().toString());
				it.putExtra("ReceiverPhone", tvReceiverPhone.getText().toString());
				it.putExtra("ReceiverAdress", adressDetail);
				if ("send".equals(state)) {
					setResult(MyConstant.RESULTCODE_35, it);
				} else {
					setResult(107, it);
				}
				ReceiveInfoActivity.this.finish();
			} else {
				MyUtil.ToastMessage(ReceiveInfoActivity.this, msg);
			}
		}
	}

}
