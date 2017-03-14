package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.bean.GoodsInfo;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.AsynImageLoader;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 【会员价/库存】页
 * */
public class GoodPriceOrStockActivity extends BaseActivity {
	private MyTitleBar mtb_GoodPriceOrStock;
	private TextView tvTitle;
	private ImageView ivLeft;
	private ListView lvGoodPriceOrStockContent;
	private List<HashMap<String, String>> lstData;
	private Button btnSave;
	private Intent itFrom;
	private TextView tvGoodName;
	private TextView tvGoodBrand;
	private TextView tvGoodType;
	private TextView tvGoodBelong;
	private ImageView ivImg;
	private String strPrice;
	private String strStock;
	private boolean isChanged = false;
	private boolean isPostServiceRunning = false;
	private BroadcastReceiver brPostHttp; // get方法广播
	private Intent iPostRequest;
	private SharedPreferences sp;
	private HashMap<String, Object> param;
	private String goods_id;
	private String spec_id;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.goodpriceorstock);
		super.onCreate(savedInstanceState);
		// 初始化内容
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
		GoodPriceOrStockActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		super.onStop();
	}

	/**
	 * 初始化
	 * */
	@Override
	protected void init() {
		this.mtb_GoodPriceOrStock = (MyTitleBar) findViewById(R.id.mtb_goodPriceOrStock_title);
		this.tvTitle = mtb_GoodPriceOrStock.getTvTitle();
		this.tvTitle.setText("会员价/库存");
		this.ivLeft = mtb_GoodPriceOrStock.getIvLeft();
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtb_GoodPriceOrStock.getLlLeft();
		this.llRight = mtb_GoodPriceOrStock.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.lvGoodPriceOrStockContent = (ListView) findViewById(R.id.lv_goodPriceOrStock_content);
		this.lstData = new ArrayList<HashMap<String, String>>();
		this.btnSave = (Button) findViewById(R.id.btn_goodPriceOrStock_save);
		this.btnSave.setText("保存");
		this.itFrom = getIntent();
		this.tvGoodName = (TextView) findViewById(R.id.tv_goodPriceOrStock_goodName);
		this.tvGoodBrand = (TextView) findViewById(R.id.tv_goodPriceOrStock_goodBrand);
		this.tvGoodType = (TextView) findViewById(R.id.tv_goodPriceOrStock_goodType);
		this.tvGoodBelong = (TextView) findViewById(R.id.tv_goodPriceOrStock_goodBelong);
		this.sp = GoodPriceOrStockActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE);
		this.param = new HashMap<String, Object>();
		this.brPostHttp = new MyBroadCastReceiver();
		this.iPostRequest = new Intent(GoodPriceOrStockActivity.this, MyHttpPostService.class); // 启动POST服务Intent对象
		this.iPostRequest.setAction(MyConstant.HttpPostServiceAciton); // 设置post
		this.ivImg = (ImageView) findViewById(R.id.iv_goodPriceOrStock_img);
		this.progressDialog = ProgressDialog.show(GoodPriceOrStockActivity.this, "请稍候。。。", "获取数据中。。。", true);
		this.progressDialog.dismiss();
	}

	/**
	 * 添加点击事件
	 * */
	@Override
	protected void addClickEvent() {

		this.llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isChanged) {
					MyUtil.ToastMessage(GoodPriceOrStockActivity.this, "请保存您的改变");
				} else {
					finish();
				}
			}
		});

		this.btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isChanged) {
					String token = sp.getString("token", "");
					param.put("token", token);
					param.put("goods_id", goods_id);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods&act=spec_edit";
					progressDialog.show();
					String type = "spec_edit";
					iPostRequest.putExtra("uri", uri);
					iPostRequest.putExtra("param", param);
					iPostRequest.putExtra("type", type);
					startService(iPostRequest);
					isPostServiceRunning = true;
					param.clear();
				} else {
					MyUtil.ToastMessage(GoodPriceOrStockActivity.this, "未检测到变化");
				}
			}
		});
	}

	/**
	 * 初始化内容
	 * */
	private void initContent() {
		// 初始化商品信息
		GoodsInfo info = (GoodsInfo) itFrom.getSerializableExtra("goodDetailInfo");
		String goods_name = info.getGoods_name();
		String brand = info.getBrand();
		String cate_name = info.getCate_name();
		String ext_activity_type_name = info.getExt_activity_type_name();
		goods_id = info.getGoods_id();
		String goods_image = info.getDefault_image();
		String url = MyUtil.getImageURL(goods_image, "180", "180");
		AsynImageLoader asynImageLoader = new AsynImageLoader();
		asynImageLoader.showImageAsyn(ivImg, url, R.drawable.test180180);
		tvGoodName.setText(goods_name);
		tvGoodBrand.setText(brand);
		tvGoodType.setText(cate_name);
		tvGoodBelong.setText(ext_activity_type_name);

		// 初始化规格列表
		SimpleAdapter sa = new SimpleAdapter(GoodPriceOrStockActivity.this, lstData, R.layout.goodpriceorstockitem,
				new String[] { "tv_priceOrStockItem_color", "tv_priceOrStockItem_size", "tv_priceOrStockItem_price",
						"tv_priceOrStockItem_stock" }, new int[] { R.id.tv_priceOrStockItem_color,
						R.id.tv_priceOrStockItem_size, R.id.tv_priceOrStockItem_price, R.id.tv_priceOrStockItem_stock });
		lvGoodPriceOrStockContent.setAdapter(sa);

		lvGoodPriceOrStockContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
				LayoutInflater inflater = LayoutInflater.from(GoodPriceOrStockActivity.this);
				final View subview = inflater.inflate(R.layout.modifypriceorstock, null);
				final HashMap<String, String> map = lstData.get(position);
				final String oldPrice = map.get("tv_priceOrStockItem_price");
				final String oldStock = map.get("tv_priceOrStockItem_stock");
				final EditText etPrice = (EditText) subview.findViewById(R.id.et_modify_price);
				final EditText etStock = (EditText) subview.findViewById(R.id.et_modify_stock);
				etPrice.setText(oldPrice);
				etStock.setText(oldStock);

				AlertDialog.Builder builder = new AlertDialog.Builder(GoodPriceOrStockActivity.this)
						.setTitle("修改价格或库存").setView(subview)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

								strPrice = etPrice.getText().toString();
								strStock = etStock.getText().toString();

								if (!strPrice.equals(oldPrice) || !strStock.equals(oldStock)) {
									map.put("tv_priceOrStockItem_price", strPrice);
									map.put("tv_priceOrStockItem_stock", strStock);
									spec_id = map.get("spec_id");
									param.put("price[" + spec_id + "]", strPrice);
									param.put("stock[" + spec_id + "]", strStock);
									SimpleAdapter sa = (SimpleAdapter) lvGoodPriceOrStockContent.getAdapter();
									sa.notifyDataSetChanged();
									isChanged = true;
								}
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});

				builder.show();
			}
		});
	}

	/**
	 * 加载数据
	 * */
	private void addData() throws JSONException {
		String data = itFrom.getStringExtra("data");
		JSONTokener jsonParser = new JSONTokener(data);
		JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
		JSONArray array = jsonReturn.getJSONArray("data");
		int length = array.length();
		for (int i = 0; i < length; i++) {
			JSONObject jsonReturn1 = array.getJSONObject(i);
			String spec_id = jsonReturn1.getString("spec_id");
			String spec_1 = jsonReturn1.getString("spec_1");
			String spec_2 = jsonReturn1.getString("spec_2");
			String price = jsonReturn1.getString("price");
			String stock = jsonReturn1.getString("stock");

			HashMap<String, String> map = new HashMap<String, String>();
			map.put("spec_id", spec_id);
			map.put("tv_priceOrStockItem_color", spec_1);
			map.put("tv_priceOrStockItem_size", spec_2);
			map.put("tv_priceOrStockItem_price", price);
			map.put("tv_priceOrStockItem_stock", stock);
			lstData.add(map);
		}

		SimpleAdapter sa = (SimpleAdapter) lvGoodPriceOrStockContent.getAdapter();
		sa.notifyDataSetChanged();
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpPostServiceAciton);
		GoodPriceOrStockActivity.this.registerReceiver(brPostHttp, filterGetHttp);
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
		if ("spec_edit".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			System.out.println(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				isChanged = false;
				GoodPriceOrStockActivity.this.finish();
			} else {
				MyUtil.ToastMessage(GoodPriceOrStockActivity.this, msg);
			}
		}
	}

}
