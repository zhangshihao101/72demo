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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * 【取消订单】页
 * */
public class CancelOrderActivity extends BaseActivity {

	private Context mCancelOrderContext;
	private MyTitleBar mtbCancelOrder;
	private TextView tvTitle;
	private ImageView ivLeft;
	private RadioGroup rgCancelReason;
	private HashMap<String, Object> paramCancel;
	private EditText etReason;
	private Button btnCancel;
	private boolean isPostServiceRunning = false;
	private Intent iPostCancel;
	private Intent iFromCancel;
	private BroadcastReceiver brPostHttp;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.cancelorder);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier();
		super.onStart();
	}

	@Override
	protected void onStop() {
		mCancelOrderContext.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostCancel);
			isPostServiceRunning = false;
		}
		super.onStop();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent data = new Intent();
			data.putExtra("isCancel", false);
			setResult(MyConstant.RESULTCODE_32, data);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void addClickEvent() {

		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent data = new Intent();
				data.putExtra("isCancel", true);
				setResult(MyConstant.RESULTCODE_32, data);
				finish();
			}
		});

		this.rgCancelReason.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int id = group.getCheckedRadioButtonId();

				switch (id) {
				case R.id.rb_cancelorder_notEnough:
					etReason.setVisibility(View.GONE);
					RadioButton rbNotEnough = (RadioButton) findViewById(R.id.rb_cancelorder_notEnough);
					String reason1 = rbNotEnough.getText().toString();
					paramCancel.put("cancel_reason", reason1);
					if (paramCancel.containsKey("remark")) {
						paramCancel.remove("remark");
					}
					etReason.setFocusable(false);
					break;
				case R.id.rb_cancelorder_notOrder:
					etReason.setVisibility(View.GONE);
					RadioButton rbNotOrder = (RadioButton) findViewById(R.id.rb_cancelorder_notOrder);
					String reason2 = rbNotOrder.getText().toString();
					paramCancel.put("cancel_reason", reason2);
					if (paramCancel.containsKey("remark")) {
						paramCancel.remove("remark");
					}
					etReason.setFocusable(false);
					break;
				case R.id.rb_cancelorder_buyer:
					etReason.setVisibility(View.GONE);
					RadioButton rbBuyer = (RadioButton) findViewById(R.id.rb_cancelorder_buyer);
					String reason3 = rbBuyer.getText().toString();
					paramCancel.put("cancel_reason", reason3);
					if (paramCancel.containsKey("remark")) {
						paramCancel.remove("remark");
					}
					etReason.setFocusable(false);
					break;
				case R.id.rb_cancelorder_otherReason:
					etReason.setVisibility(View.VISIBLE);
					RadioButton rbOtherReason = (RadioButton) findViewById(R.id.rb_cancelorder_otherReason);
					String reason4 = rbOtherReason.getText().toString();
					String reason5 = etReason.getText().toString();
					paramCancel.put("cancel_reason", reason4);
					paramCancel.put("remark", reason5);
					etReason.setFocusable(true);
					break;
				}
			}
		});

		this.btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (paramCancel.containsKey("cancel_reason")) {
					String token = iFromCancel.getStringExtra("token");
					String order_id = iFromCancel.getStringExtra("order_id");
					paramCancel.put("token", token);
					paramCancel.put("order_id", order_id);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=cancel_order";
					progressDialog.show();
					iPostCancel.putExtra("uri", uri);
					iPostCancel.putExtra("param", paramCancel);
					iPostCancel.putExtra("type", "cancelOrder");
					startService(iPostCancel);
					isPostServiceRunning = true;
					paramCancel.clear();
				} else {
					MyUtil.ToastMessage(mCancelOrderContext, "亲，请选择取消订单原因！");
				}
			}
		});

	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mCancelOrderContext = CancelOrderActivity.this;
		this.mtbCancelOrder = (MyTitleBar) findViewById(R.id.mtb_cancelorder_title);
		this.tvTitle = mtbCancelOrder.getTvTitle();
		this.ivLeft = mtbCancelOrder.getIvLeft();
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbCancelOrder.getLlLeft();
		this.llRight = mtbCancelOrder.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.tvTitle.setText("取消订单");
		this.iFromCancel = getIntent();
		this.rgCancelReason = (RadioGroup) findViewById(R.id.rg_cancelorder_reason);
		this.paramCancel = new HashMap<String, Object>();
		this.etReason = (EditText) findViewById(R.id.et_cancelorder_otherReason);
		this.btnCancel = (Button) findViewById(R.id.btn_cancelorder_cancel);
		this.iPostCancel = new Intent(mCancelOrderContext, MyHttpPostService.class);
		this.iPostCancel.setAction(MyConstant.HttpPostServiceAciton);
		this.brPostHttp = new MyBroadCastReceiver();
		this.progressDialog = ProgressDialog.show(CancelOrderActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		mCancelOrderContext.registerReceiver(brPostHttp, filterPostHttp);
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
		if ("cancelOrder".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				Intent data = new Intent();
				data.putExtra("isCancel", true);
				setResult(MyConstant.RESULTCODE_32, data);
				CancelOrderActivity.this.finish();
				MyUtil.ToastMessage(CancelOrderActivity.this, "订单取消成功");
			} else {
				MyUtil.ToastMessage(CancelOrderActivity.this, msg);
			}
		}
	}

}
