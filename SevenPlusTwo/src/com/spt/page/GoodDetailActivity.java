package com.spt.page;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.bean.GoodsInfo;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.AsynImageLoader;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * 【商品详情】页
 * */
public class GoodDetailActivity extends BaseActivity {
	private MyTitleBar mtb_GoodDetail;
	private TextView tvTitle;
	private ImageView ivLeft;
	private Context mGoodDetailContext;
	private Intent itParam;
	private GoodsInfo goodsInfo;
	private ImageView ivImg;
	private TextView tvGoodName;
	private TextView tvGoodBrand;
	private TextView tvGoodType;
	private TextView tvGoodBelong;
	private EditText etNewSequence;
	private EditText etPushSequence;
	private ToggleButton tbPush;
	// private ToggleButton tbNoSale;
	private boolean isGetServiceRunning = false;
	private BroadcastReceiver brGetHttp; // get方法广播
	private BroadcastReceiver brPostHttp; // get方法广播
	private Intent iGetRequest;
	private SharedPreferences sp;
	private HashMap<String, String> param;
	private String goods_id;
	private String s_gid;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private LinearLayout llPriceRight;
	private ProgressDialog progressDialog;
	private String newSort;
	private String pushSort;
	private String push;
	// private String noSale;
	private Intent iPostRequest;
	private boolean isPostServiceRunning = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.gooddetail);
		super.onCreate(savedInstanceState);
		// 初始化商品信息
		initGoogInfo();
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver
		super.onStart();
	}

	@Override
	protected void onStop() {
		GoodDetailActivity.this.unregisterReceiver(brGetHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}

		GoodDetailActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		super.onStop();
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {
		this.llPriceRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String token = sp.getString("token", "");
				param.put("token", token);
				param.put("goods_id", goods_id);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods&act=spec_view";
				progressDialog.show();
				String type = "spec_view";
				iGetRequest.putExtra("uri", uri);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", type);
				startService(iGetRequest);
				isGetServiceRunning = true;
				param.clear();
			}
		});

		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(MyConstant.RESULTCODE_16);
				finish();
			}
		});

		this.etNewSequence.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!newSort.equals(String.valueOf(s))) {
					param.put("colum", "ext_new_sort");
					param.put("value", String.valueOf(s));
					String token = sp.getString("token", "");
					param.put("token", token);
					param.put("goods_id", goods_id);
					param.put("s_gid", s_gid);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods&act=update_col";
					progressDialog.show();
					String type = "update_col";
					iPostRequest.putExtra("uri", uri);
					iPostRequest.putExtra("param", param);
					iPostRequest.putExtra("type", type);
					startService(iPostRequest);
					isPostServiceRunning = true;
					param.clear();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		this.etPushSequence.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (!pushSort.equals(String.valueOf(s))) {
					param.put("colum", "ext_commend_sort");
					param.put("value", String.valueOf(s));
					String token = sp.getString("token", "");
					param.put("token", token);
					param.put("goods_id", goods_id);
					param.put("s_gid", s_gid);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods&act=update_col";
					progressDialog.show();
					String type = "update_col";
					iPostRequest.putExtra("uri", uri);
					iPostRequest.putExtra("param", param);
					iPostRequest.putExtra("type", type);
					startService(iPostRequest);
					isPostServiceRunning = true;
					param.clear();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		// this.tbNoSale.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean
		// isChecked) {
		// if ("1".equals(noSale)) { // 禁售
		// tbNoSale.setChecked(true);
		// } else if ("0".equals(noSale)) { // 未禁售
		// tbNoSale.setChecked(false);
		// }
		// MyUtil.ToastMessage(mGoodDetailContext, "该选项不可修改");
		// }
		// });

		this.tbPush.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					String value = "1";
					if (!value.equals(push)) {
						param.put("colum", "recommended");
						param.put("value", value);
						String token = sp.getString("token", "");
						param.put("token", token);
						param.put("goods_id", goods_id);
						param.put("s_gid", s_gid);
						String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods&act=update_col";
						progressDialog.show();
						String type = "update_col";
						iPostRequest.putExtra("uri", uri);
						iPostRequest.putExtra("param", param);
						iPostRequest.putExtra("type", type);
						startService(iPostRequest);
						isPostServiceRunning = true;
						param.clear();
						push = value;
					}

				} else {
					String value = "0";
					if (!value.equals(push)) {
						param.put("colum", "recommended");
						param.put("value", value);
						String token = sp.getString("token", "");
						param.put("token", token);
						param.put("goods_id", goods_id);
						param.put("s_gid", s_gid);
						String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods&act=update_col";
						progressDialog.show();
						String type = "update_col";
						iPostRequest.putExtra("uri", uri);
						iPostRequest.putExtra("param", param);
						iPostRequest.putExtra("type", type);
						startService(iPostRequest);
						isPostServiceRunning = true;
						param.clear();
						push = value;
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
		this.mtb_GoodDetail = (MyTitleBar) findViewById(R.id.mtb_gooddetail_title);
		this.tvTitle = mtb_GoodDetail.getTvTitle();
		this.tvTitle.setText("商品详情");
		this.ivLeft = mtb_GoodDetail.getIvLeft();
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtb_GoodDetail.getLlLeft();
		this.llRight = mtb_GoodDetail.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.mGoodDetailContext = GoodDetailActivity.this;
		this.llPriceRight = (LinearLayout) findViewById(R.id.ll_gooddetail_priceOrStock);
		this.itParam = getIntent();
		this.goodsInfo = (GoodsInfo) itParam.getSerializableExtra("goodDetailInfo");
		this.tvGoodName = (TextView) findViewById(R.id.tv_gooddetail_goodName);
		this.tvGoodBrand = (TextView) findViewById(R.id.tv_gooddetail_goodBrand);
		this.tvGoodType = (TextView) findViewById(R.id.tv_gooddetail_goodType);
		this.tvGoodBelong = (TextView) findViewById(R.id.tv_gooddetail_goodBelong);
		this.etNewSequence = (EditText) findViewById(R.id.et_gooddetail_newSequence);
		this.etPushSequence = (EditText) findViewById(R.id.et_gooddetail_pushSequence);
		this.tbPush = (ToggleButton) findViewById(R.id.tb_gooddetail_push);
		// this.tbNoSale = (ToggleButton)
		// findViewById(R.id.tb_gooddetail_noSale);
		this.brGetHttp = new MyBroadCastReceiver(); // GET广播对象
		this.brPostHttp = new MyBroadCastReceiver(); // POST广播对象
		this.iGetRequest = new Intent(GoodDetailActivity.this, MyHttpGetService.class); // 启动POST服务Intent对象
		this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton); // 设置get
		this.sp = mGoodDetailContext.getSharedPreferences("USERINFO", MODE_PRIVATE);
		this.param = new HashMap<String, String>();
		this.ivImg = (ImageView) findViewById(R.id.iv_gooddetail_img);
		this.progressDialog = ProgressDialog.show(GoodDetailActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
		this.iPostRequest = new Intent(GoodDetailActivity.this, MyHttpPostService.class);
		this.iPostRequest.setAction(MyConstant.HttpPostServiceAciton);
	}

	/**
	 * 初始化商品信息
	 * */
	private void initGoogInfo() {
		String goods_name = goodsInfo.getGoods_name();
		String brand = goodsInfo.getBrand();
		String cate_name = goodsInfo.getCate_name();
		String ext_activity_type_name = goodsInfo.getExt_activity_type_name();
		newSort = goodsInfo.getExt_new_sort();
		pushSort = goodsInfo.getExt_commend_sort();
		push = goodsInfo.getRecommended();
		// noSale = goodsInfo.getClosed();
		String goods_image = goodsInfo.getDefault_image();
		goods_id = goodsInfo.getGoods_id();
		s_gid = goodsInfo.getS_gid();

		String url = MyUtil.getImageURL(goods_image, "180", "180");

		AsynImageLoader asynImageLoader = new AsynImageLoader();
		asynImageLoader.showImageAsyn(ivImg, url, R.drawable.test180180);
		this.tvGoodName.setText(goods_name);
		this.tvGoodBrand.setText(brand);
		this.tvGoodType.setText(cate_name);
		this.tvGoodBelong.setText(ext_activity_type_name);
		this.etNewSequence.setText(newSort);
		this.etPushSequence.setText(pushSort);
		if ("1".equals(push)) { // 推荐
			this.tbPush.setChecked(true);
		} else if ("0".equals(push)) { // 不推荐
			this.tbPush.setChecked(false);
		}
		// if ("1".equals(noSale)) { // 禁售
		// this.tbNoSale.setChecked(true);
		// } else if ("0".equals(noSale)) { // 未禁售
		// this.tbNoSale.setChecked(false);
		// }
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		GoodDetailActivity.this.registerReceiver(brGetHttp, filterGetHttp);

		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		GoodDetailActivity.this.registerReceiver(brPostHttp, filterPostHttp);
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
			}

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
	 * 解析返回数据
	 * */
	private void parseDataPost(String type, String jsonStr) throws JSONException {

		if ("update_col".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			progressDialog.dismiss();
			if ("0".equals(error)) {
				MyUtil.ToastMessage(mGoodDetailContext, "修改成功");
			} else {
				MyUtil.ToastMessage(mGoodDetailContext, msg);
			}
		}
	}

	/**
	 * 解析返回数据
	 * */
	private void parseDataGet(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("spec_view".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				Intent it = new Intent(mGoodDetailContext, GoodPriceOrStockActivity.class);
				it.putExtra("data", jsonStr);
				it.putExtra("goods_id", goods_id);
				it.putExtra("goodDetailInfo", goodsInfo);
				startActivity(it);
			} else {
				MyUtil.ToastMessage(mGoodDetailContext, msg);
			}
		}
	}

}
