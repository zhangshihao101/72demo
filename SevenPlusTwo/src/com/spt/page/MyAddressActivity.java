package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.MyAddressAdapter;
import com.spt.bean.AddressInfo;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * 我的地址
 * 
 * @author lihongxuan
 *
 */
public class MyAddressActivity extends BaseActivity implements OnClickListener {

	// private TextView tv_myaddress_confirm;
	private ImageView iv_myaddress_back;
	private Button btn_add_address;
	private ListView lv_myaddress;
	private List<AddressInfo> mList;
	private MyAddressAdapter mAdapter;

	private ProgressDialog dialog;
	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数
	private String user_id;// 用户id

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_myaddress);
		super.onCreate(savedInstanceState);

		user_id = getIntent().getStringExtra("user_id");

	}

	@Override
	protected void onResume() {
		super.onResume();

		initData();
	}

	private void initData() {
		dialog = new ProgressDialog(this);
		dialog.setMessage("数据加载中,请稍等...");
		dialog.show();
		mList = new ArrayList<AddressInfo>();
		VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.ADDRESS + user_id, params, new OnCallBack() {

			@Override
			public void OnSuccess(String data) {
				dialog.dismiss();
				try {
					JSONObject object = new JSONObject(data);
					String error = object.optString("error");
					String msg = object.optString("msg");
					if (error.equals("0")) {
						JSONArray array = object.optJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.optJSONObject(i);
							AddressInfo addressInfo = new AddressInfo();
							addressInfo.setAddr_id(obj.optString("addr_id"));
							addressInfo.setUser_id(obj.optString("user_id"));
							addressInfo.setConsignee(obj.optString("consignee"));
							addressInfo.setRegion_id(obj.optString("region_id"));
							addressInfo.setRegion_name(obj.optString("region_name"));
							addressInfo.setAddress(obj.optString("address"));
							addressInfo.setZipcode(obj.optString("zipcode"));
							addressInfo.setPhone_tel(obj.optString("phone_tel"));
							addressInfo.setPhone_mob(obj.optString("phone_mob"));
							addressInfo.setExt_region_id_0(obj.optString("ext_region_id_0"));
							addressInfo.setExt_region_id_1(obj.optString("ext_region_id_1"));
							addressInfo.setExt_region_id_2(obj.optString("ext_region_id_2"));
							addressInfo.setExt_region_id_3(obj.optString("ext_region_id_3"));
							mList.add(addressInfo);
							mAdapter.notifyDataSetChanged();
						}
					} else if (error.equals("1")) {
						Toast.makeText(MyAddressActivity.this, msg, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void OnError(VolleyError volleyError) {
				dialog.dismiss();
				Toast.makeText(MyAddressActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
			}
		});

		mAdapter = new MyAddressAdapter(this, mList);
		lv_myaddress.setAdapter(mAdapter);

		lv_myaddress.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MyAddressActivity.this, ConfirmOrderActivity.class);
				intent.putExtra("consignee", mList.get(position).getConsignee());
				intent.putExtra("ext_region_id_1", mList.get(position).getExt_region_id_1());
				intent.putExtra("ext_region_id_2", mList.get(position).getExt_region_id_2());
				intent.putExtra("ext_region_id_3", mList.get(position).getExt_region_id_3());
				intent.putExtra("region_name", mList.get(position).getRegion_name());
				intent.putExtra("phone_mob", mList.get(position).getPhone_mob());
				intent.putExtra("zipcode", mList.get(position).getZipcode());
				intent.putExtra("address", mList.get(position).getAddress());
				setResult(0, intent);
				finish();
			}
		});

	}

	@Override
	protected void init() {
		iv_myaddress_back = (ImageView) findViewById(R.id.iv_myaddress_back);
		btn_add_address = (Button) findViewById(R.id.btn_add_address);
		lv_myaddress = (ListView) findViewById(R.id.lv_myaddress);

		spHome = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
		params.put("token", token);
		params.put("version", "2.1");
	}

	@Override
	protected void addClickEvent() {
		iv_myaddress_back.setOnClickListener(this);
		btn_add_address.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_myaddress_back:
			setResult(1);
			finish();
			break;
		case R.id.btn_add_address:
			Intent intent = new Intent(MyAddressActivity.this, AddAddressActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(1);
		}

		return super.onKeyDown(keyCode, event);
	}

}
