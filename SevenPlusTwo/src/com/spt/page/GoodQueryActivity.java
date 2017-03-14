package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MySearchEditText;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * 【商品查询】页
 * */
public class GoodQueryActivity extends BaseActivity {

	private MyTitleBar mtb_GoodQuery;
	private TextView tvTitle;
	private ImageView ivLeft;
	private MySearchEditText mset;
	private Spinner spinType;
	private Spinner spinState;
	private Button btnQuery;
	private List<String> lstShopType;
	private List<String> lstGoodState;
	private String goodName;
	private String type;
	private String goodState;
	private boolean isGetServiceRunning = false;
	private Intent iGetRequest;
	private BroadcastReceiver brGetHttp; // post方法广播
	private SharedPreferences spGoodQuery;
	private SharedPreferences spJSONQuery;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private ProgressDialog progressDialog;
	private boolean isSuccess = false;
	private HashMap<String, Object> param;
	private Intent resultIntent;
	private String keyword = "";
	private String check = "";
	private String character = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.goodquery);
		super.onCreate(savedInstanceState);
		// 初始化商品信息
		initGoogInfo();

	}

	@Override
	protected void onResume() {
		resultIntent.removeExtra("keyword");
		resultIntent.removeExtra("character");
		resultIntent.removeExtra("check");
		super.onResume();
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver
		super.onStart();
	}

	@Override
	protected void onStop() {
		GoodQueryActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		super.onStop();
	}

	/**
	 * 初始化商品信息
	 * */
	private void initGoogInfo() {
		initShopType(); // 初始化【本店分类】下拉列表
		initGoodState(); // 初始化【商品状态】下拉列表
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				resultIntent.putExtra("isSuccess", isSuccess);
				setResult(MyConstant.RESULTCODE_13, resultIntent);
				finish();
			}
		});

		this.btnQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				goodName = mset.getMyEditText();

				param.put("token", spGoodQuery.getString("token", ""));
				if (!"".equals(goodName)) {
					param.put("keyword", goodName);
					resultIntent.putExtra("keyword", keyword);
				} else {
					if (param.containsKey("keyword")) {
						param.remove("keyword");
					}
					if (resultIntent.hasExtra("keyword")) {
						resultIntent.removeExtra("keyword");
						keyword = "";
					}
				}
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods";
				progressDialog.show();
				String type = "goodQuery";
				iGetRequest.putExtra("uri", uri);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", type);
				startService(iGetRequest);
				isGetServiceRunning = true;
				param.clear();
			}
		});
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtb_GoodQuery = (MyTitleBar) findViewById(R.id.mtb_goodQuery_title);
		this.tvTitle = mtb_GoodQuery.getTvTitle();
		this.tvTitle.setText("商品搜索");
		this.ivLeft = mtb_GoodQuery.getIvLeft();
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtb_GoodQuery.getLlLeft();
		this.llRight = mtb_GoodQuery.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.param = new HashMap<String, Object>();
		this.mset = (MySearchEditText) findViewById(R.id.mset_goodQuery_search);
		this.spinType = (Spinner) findViewById(R.id.spin_goodQuery_shopType);
		this.spinState = (Spinner) findViewById(R.id.spin_goodQuery_goodState);
		this.btnQuery = (Button) findViewById(R.id.btn_goodQuery_query);
		this.btnQuery.setText(R.string.orderquery_query);
		this.btnQuery.setClickable(true);
		this.lstShopType = new ArrayList<String>();
		this.lstGoodState = new ArrayList<String>();
		this.iGetRequest = new Intent(GoodQueryActivity.this, MyHttpGetService.class);
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
		this.brGetHttp = new MyBroadCastReceiver(); // POST广播对象
		this.spGoodQuery = GoodQueryActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE); // 获取sp对象
		this.spJSONQuery = GoodQueryActivity.this.getSharedPreferences("JSONDATA", MODE_PRIVATE); // 获取sp对象
		this.progressDialog = ProgressDialog.show(GoodQueryActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.resultIntent = new Intent();
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		GoodQueryActivity.this.registerReceiver(brGetHttp, filterGetHttp);
	}

	/**
	 * 初始化【本店分类】下拉列表
	 * */
	private void initShopType() {
		this.lstShopType.add("不限");
		this.lstShopType.add("新增");
		this.lstShopType.add("挂靠");

		ArrayAdapter<String> aa = new ArrayAdapter<String>(GoodQueryActivity.this,
				android.R.layout.simple_spinner_item, lstShopType);

		this.spinType.setAdapter(aa);

		this.spinType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				type = lstShopType.get(position);
				if (!"不限".equals(type)) {
					param.put("check", str2Code(type));
					check = str2Code(type);
					resultIntent.putExtra("check", check);
				} else {
					if (param.containsKey("check")) {
						param.remove("check");
					}
					if (resultIntent.hasExtra("check")) {
						resultIntent.removeExtra("check");
						check = "";
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	/**
	 * 初始化【商品状态】下拉列表
	 * */
	private void initGoodState() {
		this.lstGoodState.add("不限");
		this.lstGoodState.add("推荐");
		this.lstGoodState.add("上架");
		this.lstGoodState.add("下架");
		this.lstGoodState.add("禁售");

		ArrayAdapter<String> aa = new ArrayAdapter<String>(GoodQueryActivity.this,
				android.R.layout.simple_spinner_item, lstGoodState);

		this.spinState.setAdapter(aa);

		this.spinState.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				goodState = lstGoodState.get(position);
				if (!"不限".equals(goodState)) {
					param.put("character", str2Code(goodState));
					character = str2Code(goodState);
					System.out.println("character   " + character);
					resultIntent.putExtra("character", character);
				} else {
					if (param.containsKey("character")) {
						param.remove("character");
					}
					if (resultIntent.hasExtra("character")) {
						resultIntent.removeExtra("character");
						character = "";
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	/**
	 * 文字转换code
	 * */
	private String str2Code(String str) {
		String returnStr = "";
		if ("新增".equals(str)) {
			returnStr = "new";
		} else if ("挂靠".equals(str)) {
			returnStr = "anchor";
		} else if ("上架".equals(str)) {
			returnStr = "show";
		} else if ("下架".equals(str)) {
			returnStr = "hide";
		} else if ("禁售".equals(str)) {
			returnStr = "closed";
		} else if ("推荐".equals(str)) {
			returnStr = "recommended";
		}

		return returnStr;
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
		progressDialog.dismiss();
		if ("goodQuery".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				isSuccess = true;
				Editor editor = spJSONQuery.edit();
				editor.putString("GoodList", data); // 将商品列表数据存入SharedPreferences
				editor.commit();
				resultIntent.putExtra("isSuccess", isSuccess);
				setResult(MyConstant.RESULTCODE_13, resultIntent);
				GoodQueryActivity.this.finish();
			} else {
				MyUtil.ToastMessage(GoodQueryActivity.this, msg);
			}

		}
	}

}
