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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 【调整费用】页
 * */
public class ChangeMoneyActivity extends BaseActivity {

	private MyTitleBar mtbChangeMoney;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Intent itFrom;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private HashMap<String, String> param;
	private Intent itPost;
	private boolean isPostServiceRunning = false;
	private BroadcastReceiver brPostHttp;
	private Button btnConfirm;
	private ProgressDialog progressDialog;
	private EditText etShip;
	private EditText etGoodSum;
	private TextView tvBuyer;
	private TextView tvOrderNo;
	private TextView tvGoodSum;
	private TextView tvShip;
	private TextView tvFee;
	private TextView tvOrderSum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.changemoney);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver
		super.onStart();
	}

	@Override
	protected void onStop() {
		ChangeMoneyActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(itPost);
			isPostServiceRunning = false;
		}

		super.onStop();
	}

	@Override
	protected void onResume() {
		this.tvBuyer.setText(itFrom.getStringExtra("consignee"));
		this.tvOrderNo.setText(itFrom.getStringExtra("order_sn"));
		this.tvGoodSum.setText(itFrom.getStringExtra("goods_amount"));
		this.tvShip.setText(itFrom.getStringExtra("shipping_fee"));
		this.tvFee.setText(itFrom.getStringExtra("shipping_insurance"));
		this.tvOrderSum.setText(itFrom.getStringExtra("final_amount"));
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent it = new Intent();
			it.putExtra("goods_amount", itFrom.getStringExtra("goods_amount"));
			it.putExtra("shipping_fee", itFrom.getStringExtra("shipping_fee"));
			setResult(MyConstant.RESULTCODE_33, it);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent();
				it.putExtra("goods_amount", itFrom.getStringExtra("goods_amount"));
				it.putExtra("shipping_fee", itFrom.getStringExtra("shipping_fee"));
				setResult(MyConstant.RESULTCODE_33, it);
				finish();
			}
		});

		this.etShip.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {// 限制为1000000以内的数且保留两位小数
				String str = s.toString();
				if (".".equals(str)) {
					s.delete(0, 1);
				} else if (!"".equals(str)) {
					float num = Float.parseFloat(str);
					if (num >= 1000000) {
						int length = s.length();// 整个字符串的长度
						if (str.contains(".")) {// 此时为小数，先保留两位小数，再变为1000000万以内的数
							int index = str.indexOf(".");// 先找到小数点的位置
							if (index > 6) {// 此时大于1000000
								s.delete(index - 1, index);// 删掉最后一个整数位
							}
						} else {// 此时为整数，直接删掉最后一位
							s.delete(length - 1, length);
						}
					} else {// 此时是1000000以内的数，直接保留两位小数
						int length = s.length();// 整个字符串的长度
						if (str.contains(".")) {// 此时为小数，保留两位小数
							int index = str.indexOf(".");// 先找到小数点的位置
							if ((length - index) > 3) {// 此时为3位小数，应该去掉最后一个小数位
								s.delete(length - 1, length);// 删掉最后一个小数位
							}
						}
					}
				}
			}
		});

		this.etGoodSum.addTextChangedListener(new TextWatcher() {// 限制为1000000以内的数且保留两位小数

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
						String str = s.toString();
						if (".".equals(str)) {
							s.delete(0, 1);
						} else if (!"".equals(str)) {
							float num = Float.parseFloat(str);
							if (num >= 1000000) {
								int length = s.length();// 整个字符串的长度
								if (str.contains(".")) {// 此时为小数，先保留两位小数，再变为1000000万以内的数
									int index = str.indexOf(".");// 先找到小数点的位置
									if (index > 6) {// 此时大于1000000
										s.delete(index - 1, index);// 删掉最后一个整数位
									}
								} else {// 此时为整数，直接删掉最后一位
									s.delete(length - 1, length);
								}
							} else {// 此时是1000000以内的数，直接保留两位小数
								int length = s.length();// 整个字符串的长度
								if (str.contains(".")) {// 此时为小数，保留两位小数
									int index = str.indexOf(".");// 先找到小数点的位置
									if ((length - index) > 3) {// 此时为3位小数，应该去掉最后一个小数位
										s.delete(length - 1, length);// 删掉最后一个小数位
									}
								}
							}
						}
					}
				});

		this.btnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String goodSum = etGoodSum.getText().toString();
				String ship = etShip.getText().toString();
				if ("".equals(goodSum) && "".equals(ship)) {
					MyUtil.ToastMessage(ChangeMoneyActivity.this, "修改金额不能为空");
				} else {
					param.clear();
					param.put("token", itFrom.getStringExtra("token"));
					param.put("order_id", itFrom.getStringExtra("order_id"));
					if (!"".equals(goodSum)) {
						param.put("goods_amount", goodSum);
					}
					if (!"".equals(ship)) {
						param.put("shipping_fee", ship);
					}
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=adjust_fee";
					progressDialog.show();
					itPost.putExtra("uri", uri);
					itPost.putExtra("param", param);
					itPost.putExtra("type", "adjust_fee");
					startService(itPost);
					isPostServiceRunning = true;
				}

			}
		});
	}

	@Override
	protected void init() {
		this.mtbChangeMoney = (MyTitleBar) findViewById(R.id.mtb_changeMoney_title);
		this.tvTitle = mtbChangeMoney.getTvTitle();
		this.ivLeft = mtbChangeMoney.getIvLeft();
		this.tvTitle.setText("调整费用");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbChangeMoney.getLlLeft();
		this.llRight = mtbChangeMoney.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.etShip = (EditText) findViewById(R.id.et_changeMoney_ship);
		this.etGoodSum = (EditText) findViewById(R.id.et_changeMoney_goodSum);
		this.tvBuyer = (TextView) findViewById(R.id.tv_changeMoney_buyer);
		this.tvOrderNo = (TextView) findViewById(R.id.tv_changeMoney_orderNo);
		this.tvGoodSum = (TextView) findViewById(R.id.tv_changeMoney_goodSum);
		this.tvShip = (TextView) findViewById(R.id.tv_changeMoney_ship);
		this.tvFee = (TextView) findViewById(R.id.tv_changeMoney_fee);
		this.tvOrderSum = (TextView) findViewById(R.id.tv_changeMoney_orderSum);
		this.itFrom = getIntent();
		this.itPost = new Intent(ChangeMoneyActivity.this, MyHttpPostService.class);
		this.itPost.setAction(MyConstant.HttpPostServiceAciton);
		this.brPostHttp = new MyBroadCastReceiver();
		this.param = new HashMap<String, String>();
		this.btnConfirm = (Button) findViewById(R.id.btn_changeMoney_confirm);
		this.progressDialog = ProgressDialog.show(ChangeMoneyActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		ChangeMoneyActivity.this.registerReceiver(brPostHttp, filterPostHttp);
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
						parsePostData(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 * 解析post返回数据
	 * */
	private void parsePostData(String type, String jsonStr) throws JSONException {
		this.progressDialog.dismiss();
		if ("adjust_fee".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				Intent it = new Intent();
				String goodSum = etGoodSum.getText().toString();
				String ship = etShip.getText().toString();
				if (!"".equals(goodSum)) {
					it.putExtra("goods_amount", goodSum);
				} else {
					it.putExtra("goods_amount", itFrom.getStringExtra("goods_amount"));
				}
				if (!"".equals(ship)) {
					it.putExtra("shipping_fee", ship);
				} else {
					it.putExtra("shipping_fee", itFrom.getStringExtra("shipping_fee"));
				}
				setResult(MyConstant.RESULTCODE_33, it);
				MyUtil.ToastMessage(ChangeMoneyActivity.this, "费用修改成功");
				ChangeMoneyActivity.this.finish();
			} else {
				MyUtil.ToastMessage(ChangeMoneyActivity.this, msg);
			}
		}
	}

}
