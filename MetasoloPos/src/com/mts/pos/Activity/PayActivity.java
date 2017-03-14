package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseLeftMenuActivity;
import com.mts.pos.Common.Constants;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.MyDate;
import com.mts.pos.Common.MyPostTask;
import com.mts.pos.Common.NetworkUtil;
import com.mts.pos.Common.Urls;
import com.mts.pos.Fragment.MemerFragment;
import com.mts.pos.Fragment.RecommendeFragment;
import com.mts.pos.listview.LeftMenuInfo;
import com.mts.pos.listview.ProductAdapter;
import com.mts.pos.listview.ProductInfo;
import com.mts.pos.listview.SearchProductInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import de.greenrobot.event.EventBus;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

/**
 * 
 * @author zhangshihao ll_promo和tv_blank被注释掉
 */
public class PayActivity extends BaseLeftMenuActivity {

	private Button btn_menu, btn_scan, btn_exit, btn_keepbills;
	private EditText et_search;
	public static ListView product_list;
	@SuppressWarnings("unused")
	private TextView tv_clean, tv_old_cost, tv_promo_cost, tv_member_promo_cost, tv_promo_message;
	public static TextView tv_total;
	public static TextView tv_account;
	private RadioGroup rgp;
	private RadioButton rbtn_member, rbtn_recommended;
	public static RelativeLayout rl;
	private EditText et_member_search;
	public static LinearLayout ll_promo;
	public LinearLayout ll_member_msg, ll_member_add;
	public static TextView tv_blank;
	private FragmentManager fragmentManager;
	private FragmentTransaction fragmentTransaction;
	private MemerFragment memerFragment;
	private RecommendeFragment recommendeFragment;
	public static List<SearchProductInfo> searchData = null;
	public static List<ProductInfo> productData = null;
	public static ProductInfo info;
	public static String productName = "";
	public static String productName2 = "";
	public static String productId = "";
	public static String productColor = "";
	public static String productSize = "";
	public static String productCount = "";
	public static String productSaleCount = "";
	public static String productOriginal = "";
	public static String productPresent = "";
	public static String productUrl = "";
	public static String productPrice = "";
	public static int count = 0;
	public static int salecount = 0;

	public static String productSmallUrl = "";
	public static String productLargeUrl = "";
	public static String productMediumUrl = "";
	public static String productDetailUrl = "";
	String[] pic = new String[5];

	public static String description = "";
	public static String barcode = "";
	public static ProductAdapter adapter = null;
	public static int total = 0;
	public static double total_coast = 0.00;
	private final static int SCANNIN_GREQUEST_CODE = 10;

	Double cashvalue;
	Double giftvalue;
	String giftNo;
	Double wechatvalue;
	Double alipayvalue;
	Double visavalue;
	String transactionId = "";

	String adjustment = "";
	String billingAccountAmt = "";

	public static Handler promoHandler;
	public static Timer promoTimer = null;

	private List<String> idList;
	private ArrayList<String> promoidList;

	public static String cartItem = "";

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
		mMenuDrawer.setContentView(R.layout.activity_pay);
		super.onCreate(inState);

		fragmentManager = getSupportFragmentManager();
		setTabSelection(0);
		getNum();
		// fragmentTransaction = fragmentManager.beginTransaction();
		//
		// memerFragment = new MemerFragment();
		// recommendeFragment = new RecommendeFragment();
		//
		// fragmentTransaction.add(R.id.fl_frame, memerFragment);
		// fragmentTransaction.add(R.id.fl_frame, recommendeFragment);
		// fragmentTransaction.show(memerFragment);
		// fragmentTransaction.hide(recommendeFragment);
		//// fragmentTransaction.addToBackStack(null);
		// fragmentTransaction.commitAllowingStateLoss();

		btn_menu = (Button) findViewById(R.id.btn_menu);
		btn_scan = (Button) findViewById(R.id.btn_scan);
		btn_exit = (Button) findViewById(R.id.btn_exit);
		et_search = (EditText) findViewById(R.id.et_search);
		product_list = (ListView) findViewById(R.id.product_list);
		tv_clean = (TextView) findViewById(R.id.tv_clean);
		tv_total = (TextView) findViewById(R.id.tv_total);
		tv_account = (TextView) findViewById(R.id.tv_account);
		// tv_shuliang = (TextView) findViewById(R.id.tv_shuliang);
		// tv_diaopaijia = (TextView) findViewById(R.id.tv_diaopaijia);
		// tv_xianjia = (TextView) findViewById(R.id.tv_xianjia);
		rgp = (RadioGroup) findViewById(R.id.rgp);
		rbtn_member = (RadioButton) findViewById(R.id.rbtn_member);
		// TODO
		// rbtn_recommended = (RadioButton) findViewById(R.id.rbtn_recommended);
		rl = (RelativeLayout) findViewById(R.id.rl);
		tv_old_cost = (TextView) findViewById(R.id.tv_old_cost);
		tv_promo_cost = (TextView) findViewById(R.id.tv_promo_cost);
		tv_blank = (TextView) findViewById(R.id.tv_blank);
		ll_promo = (LinearLayout) findViewById(R.id.ll_promo);
		tv_promo_message = (TextView) findViewById(R.id.tv_promo_message);
		tv_promo_message.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		btn_keepbills = (Button) findViewById(R.id.tv_keepbills);

		// 对EventBus进行注册
		EventBus.getDefault().register(this);

		productData = new ArrayList<ProductInfo>();
		searchData = new ArrayList<SearchProductInfo>();
		adapter = new ProductAdapter(PayActivity.this, productData);
		adapter.notifyDataSetChanged();
		product_list.setAdapter(adapter);

		// if (!cartItem.equals("")) {
		// productData.clear();
		// Log.e("购物车数据传回", "购物车数据传回");
		// try {
		// JSONArray jsonarray = new
		// JSONObject(cartItem).optJSONArray("cartItems");
		//
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// }

		// if (!cartItem.equals("") && cartItem != null) {
		// JSONArray jsonarray = new
		// JSONObject(promostring).optJSONArray("cartLines");
		// }

		if (productData.size() == 0) {
			ll_promo.setVisibility(View.INVISIBLE);
			tv_blank.setVisibility(View.INVISIBLE);
		}

		if (productData.size() != 0) {
			product_list.setVisibility(View.VISIBLE);
			rl.setVisibility(View.GONE);
		}

		tv_promo_message.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 跳转到优惠信息详情

			}
		});

		btn_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PayActivity.this, LogoutActivity.class);
				if (promoTimer != null) {
					promoTimer.cancel();
					promoTimer = null;
				}
				startActivityForResult(intent, 11);
			}
		});

		btn_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMenuDrawer.openMenu();
			}
		});

		btn_scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PayActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});

		et_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (et_search.getText().toString().replace(" ", "").equals("")) {
					Toast.makeText(PayActivity.this, "请输入搜索内容！", Toast.LENGTH_SHORT).show();
				} else {
					searchProduct();
				}
				return false;
			}
		});

		rgp.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rbtn_member.getId()) {
					setTabSelection(0);
					rbtn_member.setBackgroundResource(R.color.rightbtnselected);
					// TODO
					// rbtn_recommended.setBackgroundResource(R.color.rightbtnnoselect);
				} else if (checkedId == rbtn_recommended.getId()) {
					setTabSelection(1);
					// TODO
					// rbtn_recommended.setBackgroundResource(R.color.rightbtnselected);
					rbtn_member.setBackgroundResource(R.color.rightbtnnoselect);
				}

			}
		});

		tv_clean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (productData.isEmpty()) {
					Toast.makeText(PayActivity.this, "购物车是空的", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(PayActivity.this, DeleteProductItemActivity.class);
					intent.putExtra("which", "10");
					startActivityForResult(intent, 1);
				}
			}
		});

		tv_account.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (total == 0) {
					Toast.makeText(PayActivity.this, "总计为0，不能结算！", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent(PayActivity.this, AccountingActivity.class);
					// Double dd = 0.00;
					// if (!adjustment.equals("0") && adjustment != null) {
					// if (adjustment.substring(0, 1).equals("-")) {
					// dd = Double.valueOf(adjustment.substring(1));
					// }
					// intent.putExtra("totalCost", Double.valueOf(total_coast)
					// - dd);
					// }
					intent.putExtra("totalCost", total_coast);
					intent.putExtra("comeFlag", "Pay");
					// intent.putExtra("totalCost", total_coast);
					// String adjustment = "";
					// String billingAccountAmt = "";
					startActivityForResult(intent, 2);
					// if (promoTimer != null) {
					// promoTimer.cancel();
					// promoTimer = null;
					// // tv_total.setText("总计：￥0");
					// }

					// tv_old_cost.setText("￥：0");
					// tv_promo_cost.setText("￥：0");
				}
			}
		});

		btn_keepbills.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 挂单
				if (productData.size() > 0 && productData != null) {
					keepBills();
				} else {
					Toast.makeText(PayActivity.this, "购物车是空的", Toast.LENGTH_SHORT).show();
				}

			}
		});

		// memerFragment = new MemerFragment();
		// Bundle bundle = new Bundle();
		// bundle.putDouble("totalCoast", total_coast);
		// memerFragment.setArguments(bundle);

		// 每隔三秒请求
		promoHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 24) {
					// checkPromo();
					// adapter.notifyDataSetChanged();
				}
			}
		};

		new TimeThread().start();

	}

	class TimeThread extends Thread {
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(1000);
					Message msg = new Message();
					msg.what = 1; // 消息(一个整型值)
					mTimeHandler.sendMessage(msg);// 每隔1秒发送一个msg给mHandler
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (true);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler mTimeHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (MyDate.getTime().substring(MyDate.getTime().length() - 5, MyDate.getTime().length())
						.equals("00:00")) {
					Intent intent = new Intent(PayActivity.this, FootfallActivity.class);
					intent.putExtra("hour", MyDate.getTime().substring(0, 2));
					startActivity(intent);
				}

				break;

			default:
				break;
			}

		};
	};

	@Override
	protected void onResumeFragments() {
		ll_member_msg = (LinearLayout) getSupportFragmentManager().findFragmentByTag("memerF").getView()
				.findViewById(R.id.ll_member_msg);
		ll_member_add = (LinearLayout) getSupportFragmentManager().findFragmentByTag("memerF").getView()
				.findViewById(R.id.ll_member_add);
		et_member_search = (EditText) getSupportFragmentManager().findFragmentByTag("memerF").getView()
				.findViewById(R.id.et_member_search);
		super.onResumeFragments();
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("Look", "搜索商品==" + result);
			try {
				JSONArray jsonarray = new JSONObject(result).optJSONArray("productsList");
				if (jsonarray.length() == 0) {
					// rl.setVisibility(View.VISIBLE);
					// product_list.setVisibility(View.GONE);
					Toast.makeText(PayActivity.this, "没有搜索的商品", Toast.LENGTH_SHORT).show();
				} else if (jsonarray.length() == 1) {
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject jo = (JSONObject) jsonarray.opt(i);
						if (!jo.optString("totalAvailableQuantity").equals("0")) {
							productName2 = jo.optString("productName");
							productId = jo.optString("productId");
							productColor = jo.optString("colorDesc");
							productSize = jo.optString("dimensionDesc");
							productCount = jo.optString("totalQuantity");
							productSaleCount = jo.optString("totalAvailableQuantity");
							productPrice = jo.optString("listPrice");
							description = jo.optString("description");
							barcode = jo.optString("idValue");
							productUrl = jo.optString("skuImageUrl");

							// productUrl = Urls.base_url +
							// jo.optString("skuImageUrl");

							// productSmallUrl = Urls.base_url +
							// jo.optString("skuImageUrl");
							// productLargeUrl = Urls.base_url +
							// jo.optString("largeImageUrl");
							// productMediumUrl = Urls.base_url +
							// jo.optString("mediumImageUrl");
							// productDetailUrl = Urls.base_url +
							// jo.optString("detailImageUrl");

						} else {
							Toast.makeText(PayActivity.this, "该商品没有库存，无法加入购物车", Toast.LENGTH_SHORT).show();
						}

					}
					// getProductPrice();

					/**
					 * 显示购物车
					 */
					info = new ProductInfo();
					info.setProduct_name(productName);
					info.setProduct_name2(productName2);
					info.setProduct_id(productId);
					info.setProduct_count(1);
					info.setProduct_salecount(salecount);
					// info.setOriginal_cost(Double.valueOf(price));
					// info.setPresent_cost(Double.valueOf(price));
					// info.setTotal(Double.valueOf(price));

					info.setOriginal_cost(Double.valueOf(productPrice));
					info.setPresent_cost(Double.valueOf(productPrice));
					info.setTotal(Double.valueOf(productPrice));
					info.setDescription(description);
					info.setBarcode(barcode);
					info.setProductcolor(productColor);
					info.setProductsize(productSize);
					info.setProduct_img(productUrl);

					int flag = 0;
					for (ProductInfo pro : productData) {
						if (pro.getProduct_id().equals(info.getProduct_id())) {
							flag = 1;
							pro.setProduct_count(pro.getProduct_count() + info.getProduct_count());
						}
					}
					if (flag == 0) {
						productData.add(info);
					}

					adapter.notifyDataSetChanged();

					// ll_promo.setVisibility(View.VISIBLE);
					tv_blank.setVisibility(View.VISIBLE);
					
					product_list.setVisibility(View.VISIBLE);
					rl.setVisibility(View.GONE);

					// 计算促销
					// checkPromo();

					promoTimer = new Timer();
					promoTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							promoHandler.sendEmptyMessage(24);
						}
					}, 0, 3000);

					total = 0;
					total_coast = 0.00;
					for (int i = 0; i < productData.size(); i++) {
						total += productData.get(i).getProduct_count();
						total_coast += productData.get(i).getPresent_cost() * productData.get(i).getProduct_count();
					}
					adapter.notifyDataSetChanged();
					Log.e("LOOK", "总价=" + total_coast);
					tv_account.setText("结    算（" + total + "）");
					tv_total.setText("总计：￥ " + total_coast);
				} else {
					Log.e("LOOK", "jsonarray.length()==" + jsonarray.length());
					rl.setVisibility(View.GONE);
					product_list.setVisibility(View.VISIBLE);
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject jo = (JSONObject) jsonarray.opt(i);

						SearchProductInfo info = new SearchProductInfo();
						info.setProductid(jo.optString("productId"));
						info.setProductname(jo.optString("productName") + "|" + jo.optString("colorDesc") + "|"
								+ jo.optString("dimensionDesc"));
						info.setProductname2(jo.optString("productName"));
						info.setProduct_img(jo.optString("skuImageUrl"));
						info.setProduct_count(jo.optInt("totalQuantity"));
						info.setProduct_salecount(jo.optInt("totalAvailableQuantity"));
						info.setModeId(jo.optString("modelId"));
						info.setDescription(jo.optString("description"));
						info.setBarcode(jo.optString("idValue"));
						info.setProductcolor(jo.optString("colorDesc"));
						info.setProductsize(jo.optString("dimensionDesc"));
						info.setBrandName(jo.optString("brandName"));
						info.setProductPrice(jo.optString("listPrice"));
						// info.setFlag(false);
						searchData.add(info);
					}
					Intent intent = new Intent(PayActivity.this, SearchActivity.class);
					// intent.putExtra("search",
					// et_search.getText().toString());
					startActivityForResult(intent, 0);
				}
				et_search.setText("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("1")) {
			// Log.e("LOOK", "商品价格==" + result);
			// try {
			// String price = new JSONObject(result).optString("price");
			// String msg = new JSONObject(result).optString("_ERROR_MESSAGE_");
			// if (msg.equals("price is not defined") || msg.equals("Product
			// doesnot exist")) {
			// Toast.makeText(PayActivity.this, "搜索的商品没有原价",
			// Toast.LENGTH_SHORT).show();
			// } else {
			// info = new ProductInfo();
			// info.setProduct_name(productName);
			// info.setProduct_name2(productName2);
			// info.setProduct_id(productId);
			// info.setProduct_count(1);
			// info.setProduct_salecount(salecount);
			// // info.setOriginal_cost(Double.valueOf(price));
			// // info.setPresent_cost(Double.valueOf(price));
			// // info.setTotal(Double.valueOf(price));
			//
			// info.setOriginal_cost(Double.valueOf(productPrice));
			// info.setPresent_cost(Double.valueOf(productPrice));
			// info.setTotal(Double.valueOf(productPrice));
			// info.setDescription(description);
			// info.setBarcode(barcode);
			// info.setProductcolor(productColor);
			// info.setProductsize(productSize);
			// info.setProduct_img(productUrl);
			// productData.add(info);
			// adapter.notifyDataSetChanged();
			//
			// ll_promo.setVisibility(View.VISIBLE);
			// tv_blank.setVisibility(View.VISIBLE);
			//
			// // 计算促销
			// // checkPromo();
			//
			// promoTimer = new Timer();
			// promoTimer.schedule(new TimerTask() {
			// @Override
			// public void run() {
			// promoHandler.sendEmptyMessage(24);
			// }
			// }, 500, 3000);
			//
			// total = 0;
			// total_coast = 0.00;
			// for (int i = 0; i < productData.size(); i++) {
			// total += productData.get(i).getProduct_count();
			// total_coast += productData.get(i).getPresent_cost() *
			// productData.get(i).getProduct_count();
			// }
			// adapter.notifyDataSetChanged();
			// Log.e("LOOK", "总价=" + total_coast);
			// tv_account.setText("结 算（" + total + "）");
			// }
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
		} else if (whichtask.equals("2")) {
			Log.e("LOOK", "交易单号==" + result);
			try {
				transactionId = new JSONObject(result).optString("transactionId");
				if (transactionId.equals("")) {
					Toast.makeText(PayActivity.this, "不能获取交易单号，请稍后再试！", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("3")) {
			Log.e("LOOK", "请求挂单结果==" + result);
			try {
				String over = new JSONObject(result).getString("_IS_SUCCESS_");
				if (over.equals("Y")) {
					productData.clear();
					adapter.notifyDataSetChanged();
					promoTimer.cancel();
					promoTimer = null;
					tv_total.setText("总计：￥ 0");
					tv_account.setText("结    算（" + 0 + "）");
					Toast.makeText(PayActivity.this, "挂单成功，请去挂单记录查看", Toast.LENGTH_SHORT).show();

					product_list.setVisibility(View.GONE);
					rl.setVisibility(View.VISIBLE);
					ll_promo.setVisibility(View.INVISIBLE);
					tv_blank.setVisibility(View.INVISIBLE);
				} else {
					Toast.makeText(PayActivity.this, "挂单失败", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (whichtask.equals("4")) {
			Log.e("LOOK", "根据ID查询单品==" + result);
			productData.clear();
			try {
				JSONObject job = new JSONObject(result).getJSONObject("variant");
				info = new ProductInfo();
				info.setProduct_name2(job.optString("productName"));
				info.setProductcolor(job.optString("colorDesc"));
				info.setProduct_img(job.optString("skuImageUrl"));
				info.setPresent_cost(job.optInt("listPrice"));
				// info.setProduct_count(product_count);
				productData.add(info);
				adapter = new ProductAdapter(PayActivity.this, productData);
				adapter.notifyDataSetChanged();
				product_list.setAdapter(adapter);

				product_list.setVisibility(View.VISIBLE);
				rl.setVisibility(View.GONE);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (whichtask.equals("5")) {
			Log.e("LOOK", "促销信息==" + result);
			try {
				String promostring = new JSONObject(result).getString("outCart");
				JSONArray jsonarray = new JSONObject(promostring).optJSONArray("cartLines");
				adjustment = new JSONObject(promostring).optString("adjustment");
				billingAccountAmt = new JSONObject(promostring).optString("billingAccountAmt");
				Log.e("LOOK", "优惠产品信息==" + jsonarray.length());
				if (jsonarray.length() != 0 && productData.size() != 0 && jsonarray.length() == productData.size()) {
					for (int i = 0; i < jsonarray.length(); i++) {
						// 没有赠品有优惠
						JSONObject jo = (JSONObject) jsonarray.opt(i);
						productData.get(i).setPromo_cost(jo.optInt("adjustment"));
						productData.get(i).setBasePrice(jo.optInt("basePrice"));
						productData.get(i).setIspromo(jo.optBoolean("isPromo"));
						adapter.notifyDataSetChanged();
					}
				} else if (jsonarray.length() != 0 && productData.size() != 0
						&& jsonarray.length() != productData.size()) {
					// 有赠品
					promoidList = new ArrayList<String>();
					idList = new ArrayList<String>();
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject jo = (JSONObject) jsonarray.opt(i);
						String promoId = jo.optString("productId");
						promoidList.add(promoId);
					}

					for (int i = 0; i < productData.size(); i++) {
						idList.add(productData.get(i).getProduct_id());
					}

					Log.e("促销赠品", "促销赠品=" + idList.toString());

					for (int i = 0; i < promoidList.size(); i++) {
						for (int j = 0; j < idList.size(); j++) {
							if (!TextUtils.equals(promoidList.get(i), idList.get(j))) {

							}
						}
					}

				}

				tv_old_cost.setText("￥：" + total_coast);
				tv_promo_cost.setText("￥：" + adjustment);
				// Double d = 0.00;
				// if (adjustment.substring(0, 1).equals("-")) {
				// d = Double.valueOf(adjustment.substring(1));
				// }
				//
				// Double d2 = Double.valueOf(total_coast) - d;
				// String ss = d2.toString();
				// tv_total.setText("总计：￥ " + ss);
				tv_total.setText("总计：￥ " + total_coast);

				// tv_member_promo_cost.setText(text);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

	}

	public void onEvent(ProductInfo productInfo) {
		// productData.add(productInfo);

		int flag = 0;
		for (ProductInfo pro : productData) {
			if (pro.getProduct_id().equals(productInfo.getProduct_id())) {
				flag = 1;
				pro.setProduct_count(pro.getProduct_count() + productInfo.getProduct_count());
			}
		}
		if (flag == 0) {
			productData.add(productInfo);
		}

		adapter.notifyDataSetChanged();

		// ll_promo.setVisibility(View.VISIBLE);
		tv_blank.setVisibility(View.VISIBLE);

		if (productData.size() != 0) {
			product_list.setVisibility(View.VISIBLE);
			rl.setVisibility(View.GONE);
		}

		promoTimer = new Timer();
		promoTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				promoHandler.sendEmptyMessage(24);
			}
		}, 500, 3000);

		total = 0;
		total_coast = 0.00;
		for (int i = 0; i < productData.size(); i++) {
			total += productData.get(i).getProduct_count();
			total_coast += productData.get(i).getPresent_cost() * productData.get(i).getProduct_count();
		}
		tv_account.setText("结    算（" + productInfo.getProduct_count() + "）");
		tv_total.setText("总计：￥ " + total_coast);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 搜索商品
	 */
	public void searchProduct() {

		searchData.clear();
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("searchByProductStr", et_search.getText().toString()));
		nameValuePair
				.add(new BasicNameValuePair("externalLoginKey", Localxml.search(PayActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("productStoreId", Localxml.search(PayActivity.this, "storeid")));
		Log.e("productStoreId", "productStoreId==" + Localxml.search(PayActivity.this, "storeid"));
		nameValuePair.add(new BasicNameValuePair("showAvailable", "N"));
		getTask(PayActivity.this, Urls.base + Urls.search_product, nameValuePair, "0");
	};

	/**
	 * 得到价格 作废
	 */
	@SuppressWarnings("unused")
	private void getProductPrice() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair
				.add(new BasicNameValuePair("externalLoginKey", Localxml.search(PayActivity.this, "externalloginkey")));
		// nameValuePair.add(new BasicNameValuePair("USERNAME", "qje"));
		// nameValuePair.add(new BasicNameValuePair("PASSWORD", "123456"));
		nameValuePair.add(new BasicNameValuePair("posTerminalId", Localxml.search(PayActivity.this, "posid")));
		nameValuePair.add(new BasicNameValuePair("currencyUomId", "CNY"));
		nameValuePair.add(new BasicNameValuePair("productId", productId));
		// 会员ID 现在没有
		// nameValuePair.add(new BasicNameValuePair("partyId",
		// MemberFragment.memberNum));
		nameValuePair.add(new BasicNameValuePair("partyId", "_NA_"));
		// getTask(PayActivity.this, Urls.base + Urls.search_price,
		// nameValuePair, "1");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == 0) {

			} else {
				// 用返回的商品ID和name去取得价格

				/**
				 * 显示购物车
				 */

				info = new ProductInfo();
				info.setProduct_name(productName);
				info.setProduct_name2(productName2);
				info.setProduct_id(productId);
				info.setProduct_count(1);
				info.setProduct_salecount(salecount);
				// info.setOriginal_cost(Double.valueOf(price));
				// info.setPresent_cost(Double.valueOf(price));
				// info.setTotal(Double.valueOf(price));

				info.setOriginal_cost(Double.valueOf(productPrice));
				info.setPresent_cost(Double.valueOf(productPrice));
				info.setTotal(Double.valueOf(productPrice));
				info.setDescription(description);
				info.setBarcode(barcode);
				info.setProductcolor(productColor);
				info.setProductsize(productSize);
				info.setProduct_img(productUrl);
				int flag = 0;
				for (ProductInfo pro : productData) {
					if (pro.getProduct_id().equals(info.getProduct_id())) {
						flag = 1;
						pro.setProduct_count(pro.getProduct_count() + info.getProduct_count());
					}
				}
				if (flag == 0) {
					productData.add(info);
				}

				adapter.notifyDataSetChanged();

				// ll_promo.setVisibility(View.VISIBLE);
				tv_blank.setVisibility(View.VISIBLE);

				// 计算促销
				// checkPromo();

				promoTimer = new Timer();
				promoTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						promoHandler.sendEmptyMessage(24);
					}
				}, 500, 3000);

				total = 0;
				total_coast = 0.00;
				for (int i = 0; i < productData.size(); i++) {
					total += productData.get(i).getProduct_count();
					total_coast += productData.get(i).getPresent_cost() * productData.get(i).getProduct_count();
				}
				adapter.notifyDataSetChanged();
				tv_account.setText("结    算（" + total + "）");
				tv_total.setText("总计：￥ " + total_coast);
			}

		} else if (requestCode == 1) {
			Log.e("LOOK", "------1-------");
			if (data.getStringExtra("if").equals("yes")) {
				productData.clear();
				adapter.notifyDataSetChanged();
				tv_old_cost.setText("￥：0");
				tv_promo_cost.setText("￥：0");
				//
				total = 0;
				total_coast = 0.00;
				for (int i = 0; i < productData.size(); i++) {
					total += productData.get(i).getProduct_count();
					total_coast += productData.get(i).getPresent_cost() * productData.get(i).getProduct_count();
				}
				adapter.notifyDataSetChanged();
				// tv_total.setText("已选商品：" + total + "件");
				tv_account.setText("结    算（" + total + "）");
				tv_total.setText("总计：￥ " + total_coast);
				// total_money.setText("总计：￥ " +
				// String.valueOf(SomeMethod.getCommaDouble(total)));
			} else {

			}
		} else if (requestCode == 2) {
			Log.e("LOOK", "------2-------");

			if (data.getStringExtra("result").equals("done")) {
				if (promoTimer != null) {
					promoTimer.cancel();
					promoTimer = null;
				}
				et_member_search.setText("");
				if (ll_member_msg.getVisibility() == View.VISIBLE) {
					ll_member_msg.setVisibility(View.GONE);
					ll_member_add.setVisibility(View.VISIBLE);
				}

				product_list.setVisibility(View.GONE);
				rl.setVisibility(View.VISIBLE);

				total = 0;
				productName = "";
				productId = "";
				productUrl = "";

				productSmallUrl = "";
				productLargeUrl = "";
				productMediumUrl = "";
				productDetailUrl = "";

				description = "";
				barcode = "";
				productData.clear();
				searchData.clear();
				adapter.notifyDataSetChanged();
				// tv_total.setText("已选商品：" + total + "件");
				tv_account.setText("结    算（" + total + "）");
				tv_total.setText("总计：￥ 0");
				// total_money.setText("总计：￥ " +
				// String.valueOf(SomeMethod.getCommaDouble(total)));
				et_search.setText("");
				// MemberFragment m = (MemberFragment)
				// getActivity().getFragmentManager().findFragmentByTag("2");
				// m.setEmpty();
				ll_promo.setVisibility(View.INVISIBLE);
				tv_blank.setVisibility(View.INVISIBLE);

			} else {
				Log.e("LOOK", "-----for now do nothing-----");
			}

		} else if (requestCode == SCANNIN_GREQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				// 显示扫描到的内容
				et_search.setText(bundle.getString("result"));
			}
		} else if (requestCode == 3) {
			total = 0;
			total_coast = 0.00;
			for (int i = 0; i < productData.size(); i++) {
				total += productData.get(i).getProduct_count();
				total_coast += productData.get(i).getPresent_cost() * productData.get(i).getProduct_count();
			}
			adapter.notifyDataSetChanged();
			// tv_total.setText("已选商品：" + total + "件");
			tv_account.setText("结    算（" + total + "）");
			tv_total.setText("总计：￥ " + total_coast);
		} else if (requestCode == 11) {
			if (resultCode == 112) {
				finish();
			}
		}

	}

	/**
	 * 切换两个fragment且保存数据的方法
	 * 
	 * @param index
	 */
	private void setTabSelection(int index) {
		fragmentTransaction = fragmentManager.beginTransaction();
		hideFragments(fragmentTransaction);
		switch (index) {
		case 0:
			if (memerFragment == null) {
				memerFragment = new MemerFragment();
				// fragmentTransaction.add(R.id.fl_frame, memerFragment);
				fragmentTransaction.add(R.id.fl_frame, memerFragment, "memerF");
			} else {
				fragmentTransaction.show(memerFragment);
			}
			break;

		case 1:
			if (recommendeFragment == null) {
				recommendeFragment = new RecommendeFragment();
				fragmentTransaction.add(R.id.fl_frame, recommendeFragment, "recomF");
			} else {
				fragmentTransaction.show(recommendeFragment);
			}
			break;
		}
		fragmentTransaction.commit();
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (memerFragment != null) {
			transaction.hide(memerFragment);
		}
		if (recommendeFragment != null) {
			transaction.hide(recommendeFragment);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return true;
	}

	public void onResume() {
		super.onResume();
		// MobclickAgent.onResume(this);
		// MobclickAgent.onPageStart("NewActivity");

		if (!cartItem.equals("")) {
			productData.clear();
			Log.e("取单传回数据", "取单传回数据==" + cartItem);
			try {
				JSONObject json = new JSONObject(cartItem);
				String str = json.optString("cartItems");
				JSONArray jsonarray = new JSONArray(str);

				for (int i = 0; i < jsonarray.length(); i++) {

					String mColor = "", mSize = "";
					JSONObject jo = (JSONObject) jsonarray.opt(i);
					info = new ProductInfo();
					info.setProduct_name2(jo.optString("name"));
					info.setProduct_count(jo.optInt("quantity"));
					info.setProduct_salecount(jo.optInt("totalAvailableQuantity"));

					JSONArray array = jo.optJSONArray("standardFeatureList");
					for (int j = 0; j < array.length(); j++) {
						JSONObject obj = (JSONObject) array.optJSONObject(j);
						if (obj.opt("productFeatureTypeId").equals("COLOR")) {
							mColor = obj.optString("description");
						} else if (obj.opt("productFeatureTypeId").equals("DIMENSION")) {
							mSize = obj.optString("description");
						}
					}

					info.setProductcolor(mColor);
					info.setProductsize(mSize);
					info.setOriginal_cost(jo.optInt("listPrice"));
					info.setPresent_cost(jo.optInt("basePrice"));
					info.setProduct_id(jo.optString("productId"));
					info.setProduct_img(jo.optString("skuImageUrl"));

					productData.add(info);
					adapter = new ProductAdapter(PayActivity.this, productData);
					adapter.notifyDataSetChanged();
					product_list.setAdapter(adapter);

					product_list.setVisibility(View.VISIBLE);
					rl.setVisibility(View.GONE);

					// ll_promo.setVisibility(View.VISIBLE);
					tv_blank.setVisibility(View.VISIBLE);

					promoTimer = new Timer();
					promoTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							promoHandler.sendEmptyMessage(24);
						}
					}, 500, 3000);

				}

				total = 0;
				total_coast = 0.00;
				for (int i = 0; i < productData.size(); i++) {
					total += productData.get(i).getProduct_count();
					total_coast += productData.get(i).getPresent_cost() * productData.get(i).getProduct_count();
				}

				// tv_account.setText("结 算（" + info.getProduct_count() + "）");
				tv_account.setText("结    算（" + total + "）");
				tv_total.setText("总计：￥ " + total_coast);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// for (int i = 0; i < idBills.size(); i++) {
			//
			// List<NameValuePair> nameValuePair = new
			// ArrayList<NameValuePair>();
			// nameValuePair.add(new BasicNameValuePair("externalLoginKey",
			// Localxml.search(PayActivity.this, "externalloginkey")));
			// nameValuePair.add(new BasicNameValuePair("variantId",
			// idBills.get(i)));
			// getTask(PayActivity.this, Urls.base + Urls.search_product_id,
			// nameValuePair, "4");
			// }
			cartItem = "";
		}
	}

	public void onPause() {
		super.onPause();
		// MobclickAgent.onPause(this);
		// MobclickAgent.onPageEnd("NewActivity");
	}

	@Override
	protected void onMenuItemClicked(int position, LeftMenuInfo item) {

	}

	@Override
	protected int getDragMode() {
		return MenuDrawer.MENU_DRAG_CONTENT;
	}

	@Override
	protected Position getDrawerPosition() {
		return Position.LEFT;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * 获取交易单号
	 */
	private void getNum() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair
				.add(new BasicNameValuePair("externalLoginKey", Localxml.search(PayActivity.this, "externalloginkey")));
		getTask(PayActivity.this, Urls.base + Urls.transaction_id, nameValuePair, "2");
	}

	/**
	 * 拼接Json
	 */
	private String creatJSON(boolean cashYN, boolean presentYN, boolean creditYN, boolean alipayYN, boolean wechatYN) {
		JSONObject allObj = new JSONObject();
		JSONArray productList = new JSONArray();
		JSONArray moneyList = new JSONArray();

		try {
			// 交易总价，币种，会员编号，posID，交易单号
			allObj.putOpt("billingAccountAmt", total_coast);
			allObj.putOpt("currency", "CNY");
			allObj.putOpt("orderPartyId", "_NA_");
			allObj.putOpt("terminalId", Localxml.search(PayActivity.this, "posid"));
			allObj.putOpt("transactionId", transactionId);

			// 所购物品信息 价格 ID 数量
			for (int i = 0; i < productData.size(); i++) {
				JSONObject products = new JSONObject();
				products.put("basePrice", productData.get(i).getPresent_cost());
				products.put("productId", productData.get(i).getProduct_id());
				products.put("quantity", productData.get(i).getProduct_count());
				productList.put(products);
			}
			allObj.putOpt("cartLines", productList);
			// 付款方式
			if (cashYN) { // 现金
				JSONObject money1 = new JSONObject();
				money1.putOpt("paymentMethodTypeId", "CASH");
				money1.putOpt("amount", cashvalue);
				moneyList.put(money1);
			}
			if (presentYN) {// 礼品卡
				JSONObject money2 = new JSONObject();
				money2.putOpt("paymentMethodTypeId", "GIFT_CARD");
				money2.putOpt("amount", giftvalue);
				money2.putOpt("manualRefNum", giftNo);
				moneyList.put(money2);
			}
			if (creditYN) {// 银行卡
				JSONObject money3 = new JSONObject();
				money3.putOpt("paymentMethodTypeId", "CREDIT_CARD");
				money3.putOpt("amount", visavalue);
				moneyList.put(money3);
			}
			if (alipayYN) {// 支付宝
				JSONObject money4 = new JSONObject();
				money4.putOpt("paymentMethodTypeId", "EXT_ALIPAY");
				money4.putOpt("amount", alipayvalue);
				moneyList.put(money4);
			}
			if (wechatYN) {// 微信
				JSONObject money5 = new JSONObject();
				money5.putOpt("paymentMethodTypeId", "EXT_WECHAT");
				money5.putOpt("amount", wechatvalue);
				moneyList.put(money5);
			}
			allObj.putOpt("paymentInfo", moneyList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("LOOK", "促销JSON==" + allObj.toString());
		return allObj.toString();

	}

	/**
	 * 计算促销
	 */

	private void checkPromo() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair
				.add(new BasicNameValuePair("externalLoginKey", Localxml.search(PayActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("CART", creatJSON(true, false, false, false, false)));
		if (NetworkUtil.isConnected(PayActivity.this)) {
			PromoTask promotask = new PromoTask(PayActivity.this, Urls.base + Urls.check_promo, nameValuePair, "5");
			promotask.execute("");
		} else {
			Toast.makeText(PayActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 请求挂单
	 */

	private void keepBills() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair
				.add(new BasicNameValuePair("externalLoginKey", Localxml.search(PayActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("cart", creat2JSON()));
		getTask(PayActivity.this, Urls.base + Urls.add_keepbills, nameValuePair, "3");
	}

	/**
	 * 拼接挂单JSON
	 */

	private String creat2JSON() {

		JSONObject allObj = new JSONObject();
		JSONArray productList = new JSONArray();
		JSONArray promocodeList = new JSONArray();

		try {
			// 店铺ID，交易币种，会员编号，pos终端ID
			allObj.putOpt("productStoreId", Localxml.search(PayActivity.this, "storeid"));
			allObj.putOpt("currency", "CNY");
			allObj.putOpt("orderPartyId", "_NA_");
			allObj.putOpt("terminalId", Localxml.search(PayActivity.this, "posid"));

			// 所购物品信息 价格 ID 数量
			for (int i = 0; i < productData.size(); i++) {
				JSONObject products = new JSONObject();
				products.put("basePrice", productData.get(i).getPresent_cost());
				products.put("productId", productData.get(i).getProduct_id());
				products.put("quantity", productData.get(i).getProduct_count());
				productList.put(products);
			}
			allObj.putOpt("cartLines", productList);
			// 促销代码
			for (int j = 0; j < productData.size(); j++) {
				String promoCode = new String();
				promoCode = " ";
				promocodeList.put(promoCode);
			}
			allObj.putOpt("productPromoCodes", promocodeList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("LOOK", "挂单JSON==" + allObj.toString());
		return allObj.toString();
	}

	class PromoTask extends MyPostTask {
		String which;

		public PromoTask(Context context, String url, List<NameValuePair> nameValuePair, String whichtask) {
			super(context, url, nameValuePair, whichtask);
			which = whichtask;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null == result || ("").equals(result) || result.length() == 0 || result.equals(Constants.not_found)
					|| result.equals(Constants.time_out)) {
				Toast.makeText(PayActivity.this, "网络不好，请重试！", Toast.LENGTH_SHORT).show();
			} else {
				updateUI(which, result);
			}
		}
	}

}
