package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ReturnGoodsActivity extends BaseActivity {

	private TextView tv_can_return;
	private EditText et_return_count, et_return_price;
	private Spinner sp_return_reason, sp_is_storage, sp_storage, sp_storage_position;
	private Button btn_cancel, btn_enter;
	private List<String> listReason;
	private ArrayAdapter<String> reason_adapter;
	private List<String> listFacility;
	private ArrayAdapter<String> facility_adapter;
	private List<String> listFacilityPosition;
	private ArrayAdapter<String> facilityposition_adapter;
	private String reason = "", productId = "", orderId = "", facility = "", facilityposition = "";
	private Intent intent;
	private HashMap<String, String> reasonMap;
	private HashMap<String, String> facilityMap;
	private HashMap<String, String> facilitypositionmap;
	private int RETURN_RESULT = 1000;
	private int canReturn = 0;
	private double returnPrice = 0.00;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_return_goods);
		super.onCreate(inState);

		intiViews();

		// 退款原因
		getReturnReason();

		// 获取仓库
		getFacility();

		intent = getIntent();
		productId = intent.getExtras().getString("productId");
		orderId = intent.getExtras().getString("orderId");
		canReturn = intent.getExtras().getInt("canReturn");
		returnPrice = intent.getExtras().getDouble("returnPrice");

		tv_can_return.setText("可退数量：" + canReturn);

		sp_return_reason.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				reason = listReason.get(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		sp_storage.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				facility = listFacility.get(position);
				// 根据仓库获取仓位
				getFacilityPosition();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		btn_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("key", "NO");
				setResult(RETURN_RESULT, intent);
				finish();
			}
		});

		btn_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (et_return_count.getText().toString().replace(" ", "").equals("")) {
					Toast.makeText(ReturnGoodsActivity.this, "请输入退货数量", Toast.LENGTH_SHORT).show();
				} else if ((!et_return_count.getText().toString().replace(" ", "").equals(""))
						&& Integer.valueOf(et_return_count.getText().toString()) > Integer.valueOf(canReturn)) {
					Toast.makeText(ReturnGoodsActivity.this, "退货数量不能大于可退数量", Toast.LENGTH_SHORT).show();
				} else if ((!et_return_count.getText().toString().replace(" ", "").equals(""))
						&& Integer.valueOf(et_return_count.getText().toString()) <= Integer.valueOf(canReturn)
						&& et_return_price.getText().toString().replace(" ", "").equals("")) {
					Toast.makeText(ReturnGoodsActivity.this, "请输入退货价格", Toast.LENGTH_SHORT).show();
				} else if ((!et_return_count.getText().toString().replace(" ", "").equals(""))
						&& Integer.valueOf(et_return_count.getText().toString()) <= Integer.valueOf(canReturn)
						&& (!et_return_price.getText().toString().replace(" ", "").equals(""))
						&& Double.valueOf(et_return_price.getText().toString()) > returnPrice) {
					Toast.makeText(ReturnGoodsActivity.this, "退货价格不能大于售出价格", Toast.LENGTH_SHORT).show();
				} else {
					doReturn();
				}
			}
		});

	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "查看退货原因==" + result);
			listReason.clear();
			reasonMap.clear();

			try {
				JSONObject object = new JSONObject(result);
				JSONObject objectDetail = object.optJSONObject("returnReasons");

				listReason.add(objectDetail.optString("RTN_DEFECTIVE_ITEM"));
				listReason.add(objectDetail.optString("RTN_COD_REJECT"));
				listReason.add(objectDetail.optString("RTN_NOT_WANT"));
				listReason.add(objectDetail.optString("RTN_SIZE_EXCHANGE"));
				listReason.add(objectDetail.optString("RTN_NORMAL_RETURN"));
				listReason.add(objectDetail.optString("RTN_MISSHIP_ITEM"));
				listReason.add(objectDetail.optString("RTN_DIG_FILL_FAIL"));

				reasonMap.put(objectDetail.optString("RTN_DEFECTIVE_ITEM"), "RTN_DEFECTIVE_ITEM");
				reasonMap.put(objectDetail.optString("RTN_COD_REJECT"), "RTN_COD_REJECT");
				reasonMap.put(objectDetail.optString("RTN_NOT_WANT"), "RTN_NOT_WANT");
				reasonMap.put(objectDetail.optString("RTN_SIZE_EXCHANGE"), "RTN_SIZE_EXCHANGE");
				reasonMap.put(objectDetail.optString("RTN_NORMAL_RETURN"), "RTN_NORMAL_RETURN");
				reasonMap.put(objectDetail.optString("RTN_MISSHIP_ITEM"), "RTN_MISSHIP_ITEM");
				reasonMap.put(objectDetail.optString("RTN_DIG_FILL_FAIL"), "RTN_DIG_FILL_FAIL");

				reason_adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, listReason);
				reason_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				sp_return_reason.setAdapter(reason_adapter);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("1")) {
			Log.e("LOOK", "获取仓库==" + result);
			listFacility.clear();
			facilityMap.clear();
			try {
				JSONObject object = new JSONObject(result);
				JSONArray jsonarray = object.optJSONArray("facilityList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject jo = (JSONObject) jsonarray.opt(i);
					listFacility.add(jo.optString("facilityName"));
					facilityMap.put(jo.optString("facilityName"), jo.optString("facilityId"));
				}
				facility_adapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, listFacility);
				facility_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
				sp_storage.setAdapter(facility_adapter);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("2")) {
			Log.e("LOOK", "获取仓库位置==" + result);

		} else if (whichtask.equals("3")) {
			Log.e("LOOK", "查看退货结果==" + result);
			try {
				JSONObject object = new JSONObject(result);

				if (object.optString("isSuccess").equals("Y")) {
					Toast.makeText(ReturnGoodsActivity.this, "退货成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.putExtra("key", "OK");
					setResult(RETURN_RESULT, intent);
					finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void intiViews() {
		tv_can_return = (TextView) findViewById(R.id.tv_can_return);
		et_return_count = (EditText) findViewById(R.id.et_return_count);
		et_return_price = (EditText) findViewById(R.id.et_return_price);
		sp_return_reason = (Spinner) findViewById(R.id.sp_return_reason);
		sp_storage = (Spinner) findViewById(R.id.sp_storage);
		sp_storage_position = (Spinner) findViewById(R.id.sp_storage_position);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_enter = (Button) findViewById(R.id.btn_enter);

		listReason = new ArrayList<String>();
		reasonMap = new HashMap<String, String>();

		listFacility = new ArrayList<String>();
		facilityMap = new HashMap<String, String>();

	}

	// 退款原因
	private void getReturnReason() {

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(ReturnGoodsActivity.this, "externalloginkey")));
		getTask(ReturnGoodsActivity.this, Urls.base + Urls.get_returnreason, nameValuePair, "0");
	}

	// 获取仓库
	private void getFacility() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(ReturnGoodsActivity.this, "externalloginkey")));
		nameValuePair
				.add(new BasicNameValuePair("productStoreId", Localxml.search(ReturnGoodsActivity.this, "storeid")));
		getTask(ReturnGoodsActivity.this, Urls.base + Urls.get_facility, nameValuePair, "1");
	}

	// 获取仓库位置
	private void getFacilityPosition() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(ReturnGoodsActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("facilityId", facilityMap.get(facility)));
		Log.e("看看", "看看==" + facilityMap.get(facility));
		nameValuePair.add(new BasicNameValuePair("productId", productId));
		getTask(ReturnGoodsActivity.this, Urls.base + Urls.get_facilityposition, nameValuePair, "2");
	}

	private void doReturn() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(ReturnGoodsActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("orderId", orderId));
		nameValuePair.add(new BasicNameValuePair("productId", productId));
		Log.e("LOOK", "productId" + productId);
		nameValuePair.add(new BasicNameValuePair("returnQuantity", et_return_count.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("returnPrice", et_return_price.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("returnReasonId", reasonMap.get(reason)));
		Log.e("LOOK", "理由ID" + reasonMap.get(reason));

		// TODO
		nameValuePair.add(new BasicNameValuePair("facilityId", facilityMap.get(facility)));
		Log.e("LOOK", "仓库ID" + facilityMap.get(facility));
		// nameValuePair.add(new BasicNameValuePair("locationSeqId",
		// reasonMap.get(reason)));
		getTask(ReturnGoodsActivity.this, Urls.base + Urls.return_goods, nameValuePair, "3");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.putExtra("key", "NO");
			setResult(RETURN_RESULT, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
