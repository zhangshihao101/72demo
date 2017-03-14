package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 【修改物流】页
 * */
public class TransInfoActivity extends BaseActivity {

	private MyTitleBar mtbTransInfo;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Button btnTransInfoOk;
	private Intent itFrom;
	private List<HashMap<String, String>> lstCompany;
	private Spinner spinCompany;
	private EditText etTransNo;
	private EditText etTransOtherInfo;
	private String company;
	private String transNo;
	private String transOtherInfo;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private boolean isPostServiceRunning = false;
	private Intent iPostRequest; // post方法请求
	private BroadcastReceiver brPostHttp; // post方法广播
	private ProgressDialog progressDialog;
	private HashMap<String, Object> param;
	private String invoice_inc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.transinfo);
		super.onCreate(savedInstanceState);
		initContent();
		try {
			addData();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent itReturn = new Intent();
			itReturn.putExtra("company", "");
			itReturn.putExtra("transNo", "");
			itReturn.putExtra("transOtherInfo", "");
			setResult(MyConstant.RESULTCODE_31, itReturn);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver

		super.onStart();
	}

	@Override
	protected void onStop() {
		TransInfoActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	protected void addClickEvent() {
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent itReturn = new Intent();
				itReturn.putExtra("company", "");
				itReturn.putExtra("transNo", "");
				itReturn.putExtra("transOtherInfo", "");
				setResult(MyConstant.RESULTCODE_31, itReturn);
				finish();
			}
		});

		btnTransInfoOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				transOtherInfo = etTransOtherInfo.getText().toString();
				String no = etTransNo.getText().toString();
				if (!"".equals(transOtherInfo)) {
					param.put("remark", transOtherInfo);
				}
				param.put("token", itFrom.getStringExtra("token"));
				param.put("order_id", itFrom.getStringExtra("order_id"));
				param.put("invoice_no", no);
				System.out.println("token" + itFrom.getStringExtra("token"));
				System.out.println("order_id" + itFrom.getStringExtra("order_id"));
				System.out.println("invoice_no" + no);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=shipped";
				progressDialog.show();
				iPostRequest.putExtra("uri", uri);
				iPostRequest.putExtra("param", param);
				iPostRequest.putExtra("type", "shipped_modify");
				isPostServiceRunning = true;
				startService(iPostRequest);
				param.clear();
			}
		});
	}

	@Override
	protected void init() {
		this.mtbTransInfo = (MyTitleBar) findViewById(R.id.mtb_transInfo_title);
		this.tvTitle = mtbTransInfo.getTvTitle();
		this.ivLeft = mtbTransInfo.getIvLeft();
		this.tvTitle.setText("修改物流");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbTransInfo.getLlLeft();
		this.llRight = mtbTransInfo.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.btnTransInfoOk = (Button) findViewById(R.id.btn_transInfo_ok);
		this.itFrom = getIntent();
		this.lstCompany = new ArrayList<HashMap<String, String>>();
		this.spinCompany = (Spinner) findViewById(R.id.spin_transInfo_transCompany);
		this.etTransNo = (EditText) findViewById(R.id.et_transInfo_transNo);
		this.etTransOtherInfo = (EditText) findViewById(R.id.et_transInfo_transOtherInfo);
		this.iPostRequest = new Intent(TransInfoActivity.this, MyHttpPostService.class); // 启动POST服务Intent对象
		this.iPostRequest.setAction(MyConstant.HttpPostServiceAciton); // 设置POSTAction
		this.invoice_inc = itFrom.getStringExtra("invoice_inc");
		this.brPostHttp = new MyBroadCastReceiver(); // POST广播对象
		this.progressDialog = ProgressDialog.show(TransInfoActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.param = new HashMap<String, Object>(); // 调用接口参数
	}

	/**
	 * 加载数据
	 * */
	private void addData() throws JSONException {
		lstCompany.clear();
		String data = itFrom.getStringExtra("data");
		JSONTokener jsonParser = new JSONTokener(data);
		JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
		JSONArray arrayNormal = jsonReturn.getJSONArray("normal");
		JSONArray arrayOther = jsonReturn.getJSONArray("other");
		int lengthNormal = arrayNormal.length();
		int lengthOther = arrayOther.length();
		for (int i = 0; i < lengthNormal; i++) {
			JSONObject jsonReturn1 = arrayNormal.getJSONObject(i);
			String express_inc = jsonReturn1.getString("express_inc");
			String express_code = jsonReturn1.getString("express_code");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("express_code", express_code);
			map.put("tv_company_title", express_inc);
			lstCompany.add(map);
		}

		for (int i = 0; i < lengthOther; i++) {
			JSONObject jsonReturn1 = arrayOther.getJSONObject(i);
			String express_inc = jsonReturn1.getString("express_inc");
			String express_code = jsonReturn1.getString("express_code");
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("express_code", express_code);
			map.put("tv_company_title", express_inc);
			if (!lstCompany.contains(map)) {
				lstCompany.add(map);
			}
		}

		SimpleAdapter aa = (SimpleAdapter) spinCompany.getAdapter();
		aa.notifyDataSetChanged();
		if (!"".equals(invoice_inc)) {
			int index = 0;
			for (int i = 0; i < lstCompany.size(); i++) {
				HashMap<String, String> map = lstCompany.get(i);
				String name = map.get("tv_company_title");
				if (invoice_inc.equals(name)) {
					index = i;
					break;
				}
			}
			spinCompany.setSelection(index);
		}
	}

	/**
	 * 初始化页面内容
	 * */
	private void initContent() {

		// 设置【物流公司】下拉列表
		if (!(lstCompany.size() > 0)) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("express_code", "");
			map.put("tv_company_title", "无");
			lstCompany.add(map);
		}
		company = lstCompany.get(0).get("tv_company_title");
		SimpleAdapter sa = new SimpleAdapter(TransInfoActivity.this, lstCompany, R.layout.companyitem,
				new String[] { "tv_company_title" }, new int[] { R.id.tv_company_title });

		spinCompany.setAdapter(sa);

		spinCompany.setOnItemSelectedListener(new OnItemSelectedListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				company = map.get("tv_company_title");
				String code = map.get("express_code");
				param.put("invoice_inc", company);
				param.put("invoice_code", code);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		// 设置物流单号
		transNo = itFrom.getStringExtra("invoice_no");
		etTransNo.setText(transNo);
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		TransInfoActivity.this.registerReceiver(brPostHttp, filterPostHttp);
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
			}

		}
	}

	/**
	 * 解析post返回数据
	 * */
	private void parseDataPost(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("shipped_modify".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(TransInfoActivity.this, "修改物流成功");
				Intent itReturn = new Intent();
				itReturn.putExtra("company", company);
				itReturn.putExtra("transNo", transNo);
				itReturn.putExtra("transOtherInfo", transOtherInfo);
				setResult(MyConstant.RESULTCODE_31, itReturn);
				TransInfoActivity.this.finish();
			} else {
				MyUtil.ToastMessage(TransInfoActivity.this, msg);
			}
		}
	}

}
