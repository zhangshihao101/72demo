package com.spt.page;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.dialog.ChangeAddressDialog;
import com.spt.dialog.ChangeAddressDialog.OnAddressCListener;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddAddressActivity extends BaseActivity implements OnClickListener {

	private ImageView iv_addaddress_back;
	private TextView tv_addaddress_chose;
	private Button btn_addaddress;
	private EditText et_addaddress_consignee, et_addaddress_phone, et_addaddress_postal, et_addaddress_detail;

	private SharedPreferences spHome;
	private Map<String, String> params;// 参数集合
	private String token;// 必须传的参数
	private String consignee;// 收货人
	private String address;// 详细地址
	private String zipcode;// 邮编
	private String phone_mob;// 手机
	private String ext_region_id_1;// 省id
	private String ext_region_id_2;// 市id
	private String ext_region_id_3;// 区/县id
	private String is_save = "1";// 是否保存地址

	/**
	 * 电话号码正则匹配表达式（11位手机号码）^0\d{2,3}(\-)?\d{7,8}$
	 * (13[0-9]|15[0-9]|18[0-9])\\d{8}$
	 */
	public static final String TEL_CHECK = "^(13[0-9]|15[0-9]|18[0-9])\\d{8}$";

	/**
	 * 邮政编码正则表达式"^[1-9][0-9]{5}$"
	 */
	public static final String POSTAL_CHECK = "^[1-9][0-9]{5}$";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_addaddress);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void init() {
		iv_addaddress_back = (ImageView) findViewById(R.id.iv_addaddress_back);
		tv_addaddress_chose = (TextView) findViewById(R.id.tv_addaddress_chose);
		btn_addaddress = (Button) findViewById(R.id.btn_addaddress);
		et_addaddress_consignee = (EditText) findViewById(R.id.et_addaddress_consignee);
		et_addaddress_phone = (EditText) findViewById(R.id.et_addaddress_phone);
		et_addaddress_postal = (EditText) findViewById(R.id.et_addaddress_postal);
		et_addaddress_detail = (EditText) findViewById(R.id.et_addaddress_detail);

		spHome = this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		token = spHome.getString("accessToken", "");
		params = new HashMap<String, String>(); // 调用接口参数
	}

	@Override
	protected void addClickEvent() {
		iv_addaddress_back.setOnClickListener(this);
		tv_addaddress_chose.setOnClickListener(this);
		btn_addaddress.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_addaddress_back:
			AddAddressActivity.this.finish();
			break;
		case R.id.tv_addaddress_chose:
			ChangeAddressDialog mChangeAddressDialog = new ChangeAddressDialog(AddAddressActivity.this);
			mChangeAddressDialog.setAddress("天津", "天津市", "南开区");
			mChangeAddressDialog.show();
			mChangeAddressDialog.setAddresskListener(new OnAddressCListener() {

				@Override
				public void onClick(String province, String city, String country) {
					String provinces[] = province.split(" ");
					String citys[] = city.split(" ");
					String countrys[] = country.split(" ");
					ext_region_id_1 = provinces[1];
					ext_region_id_2 = citys[1];
					ext_region_id_3 = countrys[1];
					tv_addaddress_chose.setText(provinces[0] + " " + citys[0] + " " + countrys[0]);
				}
			});
			break;
		case R.id.btn_addaddress:
			if (checkEdit()) {
				final ProgressDialog progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("数据加载中...");
				progressDialog.show();
				consignee = et_addaddress_consignee.getText().toString();
				address = et_addaddress_detail.getText().toString();
				zipcode = et_addaddress_postal.getText().toString();
				phone_mob = et_addaddress_phone.getText().toString();
				params.put("token", token);
				params.put("version", "2.1");
				params.put("consignee", consignee);
				params.put("address", address);
				params.put("zipcode", zipcode);
				params.put("phone_mob", phone_mob);
				params.put("ext_region_id_1", ext_region_id_1);
				params.put("ext_region_id_2", ext_region_id_2);
				params.put("ext_region_id_3", ext_region_id_3);
				params.put("is_save", is_save);
				VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.ADDADDRESS, params, new OnCallBack() {

					@Override
					public void OnSuccess(String data) {
						try {
							JSONObject object = new JSONObject(data);
							String error = object.optString("error");
							String msg = object.optString("msg");
							if (error.equals("0")) {
								progressDialog.dismiss();
								Toast.makeText(AddAddressActivity.this, "保存成功", Toast.LENGTH_SHORT).show();

							} else if (error.equals("1")) {
								progressDialog.dismiss();
								Toast.makeText(AddAddressActivity.this, msg, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void OnError(VolleyError volleyError) {
						progressDialog.dismiss();
						Toast.makeText(AddAddressActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
					}
				});
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 检查收货人详细信息方法
	 */
	private boolean checkEdit() {

		if (et_addaddress_consignee.getText().toString().replace(" ", "").equals("")) {
			Toast toast = Toast.makeText(this, "请填写收货人信息", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (tv_addaddress_chose.getText().toString().equals("")) {
			Toast toast = Toast.makeText(this, "请选择区域信息", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (et_addaddress_phone.getText().toString().replace(" ", "").equals("")) {
			Toast toast = Toast.makeText(this, "请输入手机号", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (!checkStringStyle(et_addaddress_phone.getText().toString().replace(" ", ""), TEL_CHECK)) {
			Toast toast = Toast.makeText(this, "请填写正确手机号", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (et_addaddress_postal.getText().toString().replace(" ", "").equals("")) {
			Toast toast = Toast.makeText(this, "请填写邮政编码", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (!checkStringStyle(et_addaddress_postal.getText().toString().replace(" ", ""), POSTAL_CHECK)) {
			Toast toast = Toast.makeText(this, "请填写正确邮政编码", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		} else if (et_addaddress_detail.getText().toString().replace(" ", "").equals("")) {
			Toast toast = Toast.makeText(this, "请填写详细地址", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return false;
		}

		return true;
	}

	// 检查手机号格式、邮政编码格式
	public static boolean checkStringStyle(String str, String style) {
		Pattern REGEX = Pattern.compile(style);
		// 非空判断
		if (null == str) {
			return false;
		}
		Matcher matcher = REGEX.matcher(str);
		return matcher.matches();
	}

}
