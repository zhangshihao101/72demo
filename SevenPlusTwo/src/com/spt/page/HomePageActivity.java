package com.spt.page;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.spt.adapter.BillAdapter;
import com.spt.adapter.ConsultationAdapter;
import com.spt.adapter.GoodsManagerAdapter;
import com.spt.adapter.MyGridViewAdapter;
import com.spt.adapter.MyTreeViewAdapter;
import com.spt.adapter.MyTreeViewAdapter.CbOnClickListener;
import com.spt.adapter.MyTreeViewAdapter.EtOnClickListener;
import com.spt.adapter.MyViewPagerAdapter;
import com.spt.adapter.OrderListAdapter;
import com.spt.bean.BillInfo;
import com.spt.bean.ChatInfo;
import com.spt.bean.GoodsInfo;
import com.spt.bean.HomePageDataInfo;
import com.spt.bean.OrderListInfo;
import com.spt.bean.UserDetailInfo;
import com.spt.controler.CircleImageView;
import com.spt.controler.ImageCycleView;
import com.spt.controler.ImageCycleView.ImageCycleViewListener;
import com.spt.controler.MyGridView;
import com.spt.controler.MyRefreshListView;
import com.spt.controler.MyRefreshListView.MyOnRefreshListener;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.AsynImageLoader;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyJSONParser;
import com.spt.utils.MyTreeElement;
import com.spt.utils.MyUtil;
import com.spt.utils.ScreenUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

/**
 * 【Home】页
 */
public class HomePageActivity extends BaseActivity {

	private Context mHomeContext;
	// private SlidingMenu slidingMenu;
	private MyTitleBar mtb;
	private ImageView myTitleBar_leftIv;
	private ImageView myTitleBar_rightIv;
	private ImageView iv_homepage_back;
	private TextView myTitleBar_title;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private LinearLayout llContent;
	private View userDetailContent;
	private View homepageContent;
	private View goodsContent;
	private View ordersContent;
	private View financialContent;
	private View consultationContent;
	// private OnSetDialogListener mOnSetDialogListener;
	// private MyHomeButton btnGoods;
	// private MyHomeButton btnHomePage;
	// private MyHomeButton btnOrders;
	// private MyHomeButton btnConsultation;
	// private MyHomeButton btnFinance;
	// private MyHomeButton btnAppUpdate;
	// private MyHomeButton btnLogoff;
	// private String strSubTitle; // 显示标题
	private ImageView ivPicture;
	private CircleImageView civUserDetailPic;
	private TextView tvUser; // 用户名
	private BroadcastReceiver brGetHttp; // get方法广播
	private BroadcastReceiver brPostHttp; // post方法广播
	private HashMap<String, Object> param;
	private Intent iPostRequest; // post方法请求
	private Intent iGetRequest; // get方法请求
	private SharedPreferences spHome; // 保存信息
	private SharedPreferences spJSON; // 保存服务数据
	private TextView tvUserName; // 用户名
	private TextView tvTrueName; // 真实姓名
	private TextView tvSex; // 性别
	private TextView tvBirthday; // 出生日期
	private TextView tvBind; // 手机状态
	private PullToRefreshListView prlvOrdersList;
	// private RelativeLayout rlUserDetailBind; // 个人资料->手机验证
	// private RelativeLayout rlUserDetailChangePsw; // 个人资料->修改密码
	private View vBill; // 对账单
	private PullToRefreshListView prlvBillList;
	private View vBillhead; // 付款通知单
	private PullToRefreshListView prlvBillHeadList;
	private View vConsultationManager; // 咨询管理
	private View vWaitCallConsultation; // 待回复咨询管理
	private MyRefreshListView lvConsultationManagerList;
	private MyRefreshListView lvWaitCallConsultationList;
	private ViewPager vpConsultationContent;
	private TextView tvConsultationTitle1;
	private TextView tvConsultationTitle2;
	private List<View> lstConsultations;
	private ImageView ivImage1;
	private ImageView ivImage2;
	private PullToRefreshListView prlvGoodsManagerList; // 商品列表
	private MyRefreshListView lvGoodsTypeManagerList; // 分类排序
	private MyTreeViewAdapter treeAdapter;
	private View vGoodsManager; // 商品管理
	private View vGoodsTypeManager; // 分类管理
	private ViewPager vpGoodsContent;
	private TextView tvGoodsTitle1;
	private TextView tvGoodsTitle2;
	private List<View> lstGoods;
	private ImageView ivGoodsImg1;
	private ImageView ivGoodsImg2;
	private List<MyTreeElement> lstGoodsTypeGroups;
	private ViewPager vpFinancialContent;
	private TextView tvFinancialTitle1;
	private TextView tvFinancialTitle2;
	private List<View> lstFinancials;
	private ImageView ivFinancialImg1;
	private ImageView ivFinancialImg2;
	private TextView tvHomeLevel;
	private TextView tvHomeCount;
	private TextView tvHomeService;
	private TextView tvHomeOther;
	private TextView tvTipManager;
	private TextView tvTipWait;
	private TextView tvTipBill;
	private TextView tvTipBillhead;
	private TextView tvTipOrderList;
	private TextView tvTipGood;
	private TextView tvTipGoodType;
	// private ViewPager vpHome;
	private View vHomeImage1;
	private View vHomeImage2;
	private View vHomeImage3;
	private List<View> homeImages;
	// private TextView tvPoint;
	// private TextView tvExperience;
	// private ImageButton ibSet;
	// 底部导航栏
	private LinearLayout llbar1;
	private ImageView ivBarImg1;
	private TextView tvBarTip1;
	private LinearLayout llbar2;
	private ImageView ivBarImg2;
	private TextView tvBarTip2;
	private LinearLayout llbar3;
	private ImageView ivBarImg3;
	private TextView tvBarTip3;
	private LinearLayout llbar4;
	private ImageView ivBarImg4;
	private TextView tvBarTip4;

	// 轮播控件
	private ImageCycleView mAdView;
	private ArrayList<Integer> mImageIds;

	private boolean isGetServiceRunning = false;
	private boolean isPostServiceRunning = false;
	private String rightBtnType;

	// homepage 控件
	private MyGridView mgvMenu;
	private List<HashMap<String, Object>> lstHomePageSource; // 主页数据源
	private List<HashMap<String, String>> lstBillHeadPageSource; // 付款通知单数据源

	private String error;
	private String msg;
	private String data;
	// private String versionName;
	private long exitTime = 0;

	private String token;
	private JSONArray jsonGoodList;
	private Editor editor;
	private String avatar;
	// private HashMap<String, HashMap<String, Integer>> mapBlack;
	// private HashMap<String, HashMap<String, Integer>> mapWhite;
	private ProgressDialog progressDialog;
	private String device_token;

	private int width;
	// 订单列表相关
	private String orderSn = "";
	private String orderStatus = "";
	private String evaluationStatus = "";
	private String extension = "";
	private String addTimeFrom = "";
	private String addTimeTo = "";
	// 商品列表相关
	private String keyword = "";
	private String check = "";
	private String character = "";
	// 对账单列表相关
	private String sta_status = "";
	private String sta_plat = "";
	// 【设置】
	// private MyTitleBar mtbSetTitle;
	// private RelativeLayout rlSetMyShop;
	// private RelativeLayout rlSetChangePsw;
	// private RelativeLayout rlSetCheckUpd;
	// private RelativeLayout rlSetContactUs;
	// private Button btnSetLogOff;
	// private TextView tvSetTitle;
	// private TextView tvSetPhone;
	// private SetDialog setDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.main_menu);
		super.onCreate(savedInstanceState);
		// 将deviceToken上传制服务器
		putDeviceTokenToServer();
		// 获取店铺名称和头像
		// callMyShopData("myShop_home");
		// 初始化各画面内容
		initView();
		// 默认显示画面
		setDefaultShow();

	}

	@Override
	protected void onResume() {
		super.onResume();
		mAdView.startImageCycle();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdView.pushImageCycle();
	}

	@Override
	protected void onStart() {
		registMyBroadcastRecevier(); // regist broadcastReceiver
		super.onStart();
	}

	@Override
	protected void onStop() {
		HomePageActivity.this.unregisterReceiver(brGetHttp);
		HomePageActivity.this.unregisterReceiver(brPostHttp);
		if (isGetServiceRunning) {
			stopService(iGetRequest);
			isGetServiceRunning = false;
		}
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAdView.pushImageCycle();
	}

	// 屏蔽back键
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	// progressDialog.dismiss();
	// // 判断间隔时间 大于2秒就退出应用
	// if ((System.currentTimeMillis() - exitTime) > 2000) {
	// // 提示消息
	// String msg = "再按一次返回桌面";
	// MyUtil.ToastMessage(mHomeContext, msg);
	// // 计算两次返回键按下的时间差
	// exitTime = System.currentTimeMillis();
	// } else {
	// // 返回桌面操作
	// Intent home = new Intent(Intent.ACTION_MAIN);
	// home.addCategory(Intent.CATEGORY_HOME);
	// startActivity(home);
	// }
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent pit) {

		switch (resultCode) {
		case RESULT_OK:
			// 订单列表
			callOrderList();
			break;
		case MyConstant.RESULTCODE_10:
			editor.remove("user_id");
			editor.remove("token");
			editor.commit();
			HomePageActivity.this.finish();
			break;
		case MyConstant.RESULTCODE_13:
			boolean isSuccessGood = pit.getBooleanExtra("isSuccess", false);
			if (isSuccessGood) {
				String jsonStr = spJSON.getString("GoodList", "");
				try {
					tvTipGood.setVisibility(View.GONE);
					HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvGoodsManagerList.getRefreshableView()
							.getAdapter();
					GoodsManagerAdapter gma = (GoodsManagerAdapter) hvla.getWrappedAdapter();
					JSONTokener jsonParser1 = new JSONTokener(jsonStr);
					JSONObject jsonReturn1 = (JSONObject) jsonParser1.nextValue();
					jsonGoodList = jsonReturn1.getJSONArray("list");
					int length = jsonGoodList.length();
					gma.clear();
					if (length > 0) {
						MyJSONParser.parse_goodsList(jsonGoodList, gma, width);
						gma.notifyDataSetChanged();
					} else {
						tvTipGood.setText("您目前还没有商品，请尽快上传");
						tvTipGood.setVisibility(View.VISIBLE);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (pit.hasExtra("keyword")) {
				keyword = pit.getStringExtra("keyword");
			} else {
				keyword = "";
			}
			if (pit.hasExtra("check")) {
				check = pit.getStringExtra("check");
			} else {
				check = "";
			}
			if (pit.hasExtra("character")) {
				character = pit.getStringExtra("character");
			} else {
				character = "";
			}
			break;
		case MyConstant.RESULTCODE_15:
			callOrderList();
			break;
		case MyConstant.RESULTCODE_14:
			data = spJSON.getString("BillList", "");

			try {
				if (!"".equals(data)) {
					JSONTokener jsonParser = new JSONTokener(data);
					JSONArray jsonBill = (JSONArray) jsonParser.nextValue();
					int length = jsonBill.length();
					HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvBillList.getRefreshableView().getAdapter();
					BillAdapter ba = (BillAdapter) hvla.getWrappedAdapter();
					ba.clear();
					ba.notifyDataSetChanged();
					if (length > 0) {
						tvTipBill.setVisibility(View.GONE);
						for (int i = 0; i < length; i++) {
							JSONObject jsonReturn2 = jsonBill.getJSONObject(i);
							BillInfo info = new BillInfo();
							String sta_id = jsonReturn2.getString("sta_id");
							String sta_sn = jsonReturn2.getString("sta_sn");
							String sta_status = jsonReturn2.getString("sta_status");
							String sta_plat = jsonReturn2.getString("sta_plat");
							String total_order_pay = jsonReturn2.getString("total_order_pay");
							String add_time = jsonReturn2.getString("add_time");
							String confirm_time = jsonReturn2.getString("confirm_time");

							info.setSta_id(sta_id);
							info.setSta_sn(sta_sn);
							info.setSta_status(sta_status);
							info.setSta_plat(sta_plat);
							info.setTotal_order_pay(total_order_pay);
							info.setAdd_time(add_time);
							info.setConfirm_time(confirm_time);

							ba.addBillInfo(info);
						}
						ba.notifyDataSetChanged();
						prlvBillList.onRefreshComplete();
					} else {
						tvTipBill.setText("您目前没有对账单信息");
						tvTipBill.setVisibility(View.VISIBLE);
					}
				}
				if (pit.hasExtra("sta_status")) {
					sta_status = pit.getStringExtra("sta_status");
				} else {
					sta_status = "";
				}
				if (pit.hasExtra("sta_plat")) {
					sta_plat = pit.getStringExtra("sta_plat");
				} else {
					sta_plat = "";
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case MyConstant.RESULTCODE_16:
			callGoodsList();
			break;
		case MyConstant.RESULTCODE_17:
			callHomePageData();
			break;
		case MyConstant.RESULTCODE_18:
			callHomePageData();
			break;
		case MyConstant.RESULTCODE_19:
			callHomePageData();
			break;
		case MyConstant.RESULTCODE_20:
			callHomePageData();
			break;
		case MyConstant.RESULTCODE_21:
			callHomePageData();
			break;
		case MyConstant.RESULTCODE_22:
			callHomePageData();
			break;
		case MyConstant.RESULTCODE_36:
			callMyShopData("myShop_home1");// 请求数据,刷新home页头像
			break;
		}
	}

	/**
	 * 初始化
	 */
	@Override
	protected void init() {
		// 页面数据项
		mHomeContext = HomePageActivity.this; // 主画面Context
		spHome = mHomeContext.getSharedPreferences("USERINFO", MODE_PRIVATE); // 获取sp对象
		spJSON = mHomeContext.getSharedPreferences("JSONDATA", MODE_PRIVATE); // 获取sp对象
		editor = spHome.edit();
		avatar = spHome.getString("avatar", "");// 获取头像
		token = spHome.getString("token", ""); // 获取token
		device_token = spHome.getString("device_token", "");
		lstHomePageSource = new ArrayList<HashMap<String, Object>>(); // 首页数据
		lstBillHeadPageSource = new ArrayList<HashMap<String, String>>(); // 财务->付款通知单数据
		lstConsultations = new ArrayList<View>();
		lstGoods = new ArrayList<View>();
		lstFinancials = new ArrayList<View>();
		lstGoodsTypeGroups = new ArrayList<MyTreeElement>();
		// mapBlack = new HashMap<String, HashMap<String, Integer>>();
		// mapWhite = new HashMap<String, HashMap<String, Integer>>();
		progressDialog = ProgressDialog.show(mHomeContext, "请稍候。。。", "获取数据中。。。", true);
		progressDialog.dismiss();
		width = ScreenUtils.getScreenWidth(mHomeContext);

		// 初始化各VIEW
		userDetailContent = getLayoutInflater().inflate(R.layout.userdetail, null); // 用户详情
		homepageContent = getLayoutInflater().inflate(R.layout.homepage, null); // 主页
		// vpHome = (ViewPager)
		// homepageContent.findViewById(R.id.vp_homepage_homeImages);
		vHomeImage1 = getLayoutInflater().inflate(R.layout.homeimage1, null);
		vHomeImage2 = getLayoutInflater().inflate(R.layout.homeimage2, null);
		vHomeImage3 = getLayoutInflater().inflate(R.layout.homeimage3, null);
		homeImages = new ArrayList<View>();

		mAdView = (ImageCycleView) homepageContent.findViewById(R.id.icv_homepage_show); // 轮播
		mImageIds = new ArrayList<Integer>();

		goodsContent = getLayoutInflater().inflate(R.layout.goods, null); // 商品
		ordersContent = getLayoutInflater().inflate(R.layout.orders, null); // 订单
		financialContent = getLayoutInflater().inflate(R.layout.financial, null); // 财务
		consultationContent = getLayoutInflater().inflate(R.layout.consultation, null); // 咨询
		// slidingMenu = new SlidingMenu(mHomeContext);// 滑动页面
		// slidingMenu.setMode(SlidingMenu.RIGHT);
		// slidingMenu.setTouchModeAbove(SlidingMenu.SLIDING_CONTENT);
		// slidingMenu.setBehindOffsetRes(R.dimen.my_sliding);
		// slidingMenu.setFadeDegree(0.35f);
		// slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// slidingMenu.setShadowWidth(1);
		// slidingMenu.setMenu(R.layout.set);

		mtb = (MyTitleBar) findViewById(R.id.mtb_titleBar); // 导航栏
		myTitleBar_leftIv = mtb.getIvLeft(); // 导航栏中按钮
		myTitleBar_title = mtb.getTvTitle(); // 导航栏标题
		myTitleBar_leftIv.setBackgroundResource(R.drawable.homemenu); // 加载导航栏图标
		myTitleBar_rightIv = mtb.getIvRight();
		myTitleBar_rightIv.setBackgroundResource(R.drawable.homemenuright); // 加载导航栏图标
		myTitleBar_rightIv.setVisibility(View.INVISIBLE);
		llLeft = mtb.getLlLeft();
		llLeft.setVisibility(View.INVISIBLE);
		llRight = mtb.getLlRight();
		llContent = (LinearLayout) findViewById(R.id.ll_content); // 加载页面内容的父容器

		// 【设置】
		// mtbSetTitle = (MyTitleBar) findViewById(R.id.mtb_set_title);
		// rlSetMyShop = (RelativeLayout) findViewById(R.id.rl_set_myShop);
		// rlSetChangePsw = (RelativeLayout)
		// findViewById(R.id.rl_set_changePsw);
		// rlSetCheckUpd = (RelativeLayout) findViewById(R.id.rl_set_checkUpd);
		// rlSetContactUs = (RelativeLayout)
		// findViewById(R.id.rl_set_contactUs);
		// btnSetLogOff = (Button) findViewById(R.id.btn_set_logOff);
		// tvSetPhone = (TextView) findViewById(R.id.tv_set_phone);
		// tvSetTitle = mtbSetTitle.getTvTitle();
		// tvSetTitle.setText("设置");
		// LinearLayout llSetRight = mtbSetTitle.getLlRight();
		// llSetRight.setVisibility(View.INVISIBLE);

		// btnHomePage = (MyHomeButton)
		// findViewById(R.id.btn_mainmeun_homepage); // 菜单【首页】
		// btnHomePage.setButtonText("首页");
		// setHomeButtonBackground(btnHomePage, R.drawable.homebuttongreen,
		// R.drawable.homepagewhite, Color.WHITE);
		// btnGoods = (MyHomeButton) findViewById(R.id.btn_mainmeun_goods); //
		// 菜单【商品】
		// btnGoods.setButtonText("商品");
		// btnGoods.setButtonImg(R.drawable.goodsblack);
		// btnOrders = (MyHomeButton) findViewById(R.id.btn_mainmeun_orders); //
		// 菜单【订单】
		// btnOrders.setButtonText("订单");
		// btnOrders.setButtonImg(R.drawable.orderblack);
		// btnConsultation = (MyHomeButton)
		// findViewById(R.id.btn_mainmeun_consultation); // 菜单【咨询】
		// btnConsultation.setButtonText("咨询");
		// btnConsultation.setButtonImg(R.drawable.consultationblack);
		// btnFinance = (MyHomeButton) findViewById(R.id.btn_mainmeun_finance);
		// // 菜单【财务】
		// btnFinance.setButtonText("财务");
		// btnFinance.setButtonImg(R.drawable.financeblack);
		// btnAppUpdate = (MyHomeButton)
		// findViewById(R.id.btn_mainmeun_appUpdate); // 菜单【版本更新】
		// btnAppUpdate.setButtonText("版本更新");
		// btnAppUpdate.setButtonImg(R.drawable.updateblack);
		// btnLogoff = (MyHomeButton) findViewById(R.id.btn_mainmeun_logoff); //
		// 菜单【退出登录】
		// btnLogoff.setButtonText("退出登录");
		// btnLogoff.setButtonImg(R.drawable.exitblack);
		ivPicture = (ImageView) homepageContent.findViewById(R.id.iv_mainmenu_picture); // 菜单【头像】
		tvUser = (TextView) homepageContent.findViewById(R.id.tv_mainmeun_user); // 菜单【用户昵称】
		// tvPoint = (TextView)
		// homepageContent.findViewById(R.id.tv_homepage_point);// 【商城积分】
		// tvExperience = (TextView)
		// homepageContent.findViewById(R.id.tv_homepage_experience);// 【经验值】
		// ibSet = (ImageButton)
		// homepageContent.findViewById(R.id.ib_mainmeun_set);// 【设置】

		// 底部导航栏
		llbar1 = (LinearLayout) findViewById(R.id.ll_home_bar1);
		ivBarImg1 = (ImageView) findViewById(R.id.iv_home_img1);
		tvBarTip1 = (TextView) findViewById(R.id.tv_home_tip1);
		ivBarImg1.setImageResource(R.drawable.bar11);// 默认选中状态
		tvBarTip1.setTextColor(Color.rgb(139, 186, 41));
		myTitleBar_title.setText("首页");
		mtb.setVisibility(View.GONE);
		llbar2 = (LinearLayout) findViewById(R.id.ll_home_bar2);
		ivBarImg2 = (ImageView) findViewById(R.id.iv_home_img2);
		tvBarTip2 = (TextView) findViewById(R.id.tv_home_tip2);
		tvBarTip2.setTextColor(Color.WHITE);
		llbar3 = (LinearLayout) findViewById(R.id.ll_home_bar3);
		ivBarImg3 = (ImageView) findViewById(R.id.iv_home_img3);
		tvBarTip3 = (TextView) findViewById(R.id.tv_home_tip3);
		tvBarTip3.setTextColor(Color.WHITE);
		llbar4 = (LinearLayout) findViewById(R.id.ll_home_bar4);
		ivBarImg4 = (ImageView) findViewById(R.id.iv_home_img4);
		tvBarTip4 = (TextView) findViewById(R.id.tv_home_tip4);
		tvBarTip4.setTextColor(Color.WHITE);

		// 各画面子VIEW
		mgvMenu = (MyGridView) homepageContent.findViewById(R.id.mgv_homepage_menu); // 首页中菜单选项
		civUserDetailPic = (CircleImageView) userDetailContent.findViewById(R.id.civ_userdetail_userPic); // 用户详情->头像
		tvUserName = (TextView) userDetailContent.findViewById(R.id.tv_userdetail_userName); // 用户详情->用户名
		tvTrueName = (TextView) userDetailContent.findViewById(R.id.tv_userdetail_trueName); // 用户详情->真实姓名
		tvSex = (TextView) userDetailContent.findViewById(R.id.tv_userdetail_sex); // 用户详情->性别
		tvBirthday = (TextView) userDetailContent.findViewById(R.id.tv_userdetail_birthday); // 用户详情->出生日期
		tvBind = (TextView) userDetailContent.findViewById(R.id.tv_userdetail_bind); // 用户详情->手机是否绑定
		// 用户详情->手机是否绑定
		// rlUserDetailBind = (RelativeLayout)
		// userDetailContent.findViewById(R.id.rl_userdetail_mobile);
		// 用户详情->修改密码
		// rlUserDetailChangePsw = (RelativeLayout)
		// userDetailContent.findViewById(R.id.rl_userdetail_changePsw);
		prlvOrdersList = (PullToRefreshListView) ordersContent.findViewById(R.id.prlv_ordersPage_OrdersList); // 订单->订单列表
		prlvOrdersList.setMode(Mode.BOTH);
		vpConsultationContent = (ViewPager) consultationContent.findViewById(R.id.vp_consultation_content); // 加载财务内容的子容器
		tvConsultationTitle1 = (TextView) consultationContent.findViewById(R.id.tv_consultation_title1); // 子标题1
		tvConsultationTitle1.setTextColor(Color.rgb(139, 186, 41));
		tvConsultationTitle2 = (TextView) consultationContent.findViewById(R.id.tv_consultation_title2); // 子标题2
		ivImage1 = (ImageView) consultationContent.findViewById(R.id.iv_consultation_img1); // 滚动条1
		ivImage2 = (ImageView) consultationContent.findViewById(R.id.iv_consultation_img2); // 滚动条2
		vConsultationManager = getLayoutInflater().inflate(R.layout.consutationmanager, null); // 咨询管理
		vWaitCallConsultation = getLayoutInflater().inflate(R.layout.consutationmanager, null); // 待回复咨询管理
		tvTipManager = (TextView) vConsultationManager.findViewById(R.id.tv_product_tip); // 加载商品内容的子容器;
		tvTipWait = (TextView) vWaitCallConsultation.findViewById(R.id.tv_product_tip); // 加载商品内容的子容器;
		vpGoodsContent = (ViewPager) goodsContent.findViewById(R.id.vp_goods_content); // 加载商品内容的子容器
		tvGoodsTitle1 = (TextView) goodsContent.findViewById(R.id.tv_goods_title1); // 子标题1
		tvGoodsTitle1.setTextColor(Color.rgb(139, 186, 41));
		tvGoodsTitle2 = (TextView) goodsContent.findViewById(R.id.tv_goods_title2); // 子标题2
		ivGoodsImg1 = (ImageView) goodsContent.findViewById(R.id.iv_goods_img1); // 滚动条1
		ivGoodsImg2 = (ImageView) goodsContent.findViewById(R.id.iv_goods_img2); // 滚动条2
		vGoodsManager = getLayoutInflater().inflate(R.layout.goodsmanager, null); // 商品管理
		vGoodsTypeManager = getLayoutInflater().inflate(R.layout.goodstypemanager, null); // 分类管理
		tvTipGood = (TextView) vGoodsManager.findViewById(R.id.tv_goodsManager_tip);// 商品管理提示
		tvTipGoodType = (TextView) vGoodsTypeManager.findViewById(R.id.tv_goodsTypeManager_tip); // 分类管理提示
		prlvGoodsManagerList = (PullToRefreshListView) vGoodsManager.findViewById(R.id.prlv_goodsManager_content);
		prlvGoodsManagerList.setMode(Mode.BOTH);
		lvGoodsTypeManagerList = (MyRefreshListView) vGoodsTypeManager.findViewById(R.id.lv_goodsTypeManager_content);
		vpFinancialContent = (ViewPager) financialContent.findViewById(R.id.vp_financial_content); // 加载财务内容的子容器
		tvFinancialTitle1 = (TextView) financialContent.findViewById(R.id.tv_financial_title1); // 财务子标题1
		tvFinancialTitle1.setTextColor(Color.rgb(139, 186, 41));
		tvFinancialTitle2 = (TextView) financialContent.findViewById(R.id.tv_financial_title2); // 财务子标题2
		ivFinancialImg1 = (ImageView) financialContent.findViewById(R.id.iv_financial_img1); // 滚动条1
		ivFinancialImg2 = (ImageView) financialContent.findViewById(R.id.iv_financial_img2); // 滚动条2
		vBill = getLayoutInflater().inflate(R.layout.bill, null); // 对账单
		prlvBillList = (PullToRefreshListView) vBill.findViewById(R.id.prlv_bill_billList); // 对账单ListView
		prlvBillList.setMode(Mode.BOTH);
		tvTipBill = (TextView) vBill.findViewById(R.id.tv_bill_tip); // 对账单提示
		vBillhead = getLayoutInflater().inflate(R.layout.billhead, null); // 付款通知单
		prlvBillHeadList = (PullToRefreshListView) vBillhead.findViewById(R.id.prlv_billhead_billHeadList); // 付款通知单ListView
		prlvBillHeadList.setMode(Mode.BOTH);
		tvTipBillhead = (TextView) vBillhead.findViewById(R.id.tv_billhead_tip); // 付款通知单提示
		lvConsultationManagerList = (MyRefreshListView) vConsultationManager.findViewById(R.id.lv_product_productList); // 咨询管理ListView
		lvWaitCallConsultationList = (MyRefreshListView) vWaitCallConsultation
				.findViewById(R.id.lv_product_productList); // 咨询管理ListView
		tvHomeLevel = (TextView) homepageContent.findViewById(R.id.tv_homepage_level);
		tvHomeCount = (TextView) homepageContent.findViewById(R.id.tv_homepage_count);
		tvHomeService = (TextView) homepageContent.findViewById(R.id.tv_homepage_service);
		tvHomeOther = (TextView) homepageContent.findViewById(R.id.tv_homepage_other);
		tvTipOrderList = (TextView) ordersContent.findViewById(R.id.tv_orderList_tip);
		iv_homepage_back = (ImageView) homepageContent.findViewById(R.id.iv_homepage_back);

		// 其他
		brPostHttp = new MyBroadCastReceiver(); // POST广播对象
		brGetHttp = new MyBroadCastReceiver(); // GET广播对象
		param = new HashMap<String, Object>(); // 调用接口参数
		iPostRequest = new Intent(HomePageActivity.this, MyHttpPostService.class); // 启动POST服务Intent对象
		iPostRequest.setAction(MyConstant.HttpPostServiceAciton); // 设置POST
																	// Action
		iGetRequest = new Intent(HomePageActivity.this, MyHttpGetService.class); // 启动GET服务Intent对象
		iGetRequest.setAction(MyConstant.HttpGetServiceAciton); // 设置GET Action

		// mOnSetDialogListener = new OnSetDialogListener() {
		//
		// @Override
		// public void updBack() {
		// Log.e("tag", "111111111111");
		// Log.i("tag", "222222");
		// Log.d("tag", "33333");
		// System.out.println("222222222222");
		// // 获取当前版本号
		// PackageManager manager = mHomeContext.getPackageManager();
		// PackageInfo info = null;
		// try {
		// info = manager.getPackageInfo(mHomeContext.getPackageName(), 0);
		// } catch (NameNotFoundException e) {
		// e.printStackTrace();
		// }
		// String vName = info.versionName;
		// param.clear();
		// param.put("type", "android");
		// param.put("version", vName);
		// String uri = MyConstant.SERVICENAME +
		// "/index.php?pf=m_seller&act=check_version";
		// String type = "appUpdate";
		// iGetRequest.putExtra("uri", uri);
		// iGetRequest.putExtra("param", param);
		// iGetRequest.putExtra("type", type);
		// startService(iGetRequest);
		// isGetServiceRunning = true;
		// }
		//
		// @Override
		// public void shopBack() {
		// callMyShopData("myShop_home");// 请求数据
		// }
		//
		// @Override
		// public void pswBack() {
		// Intent it = new Intent(mHomeContext, ChangePassWordActivity.class);
		// it.putExtra("account", tvUser.getText().toString());
		// it.putExtra("token", token);
		// startActivityForResult(it, MyConstant.RESULTCODE_10);
		// }
		//
		// @Override
		// public void logOffBack() {
		// editor.remove("user_id");
		// editor.remove("token");
		// editor.commit();
		// HomePageActivity.this.finish();
		// }
		//
		// @Override
		// public void contackUsBack() {
		//
		// Uri uri = Uri.parse("tel:" + "4009000702");
		// Intent it = new Intent();
		// // it.setAction(Intent.ACTION_CALL);//直接拨打电话
		// it.setAction(Intent.ACTION_DIAL);// 调用软件盘方式拨打电话
		// it.setData(uri);
		// mHomeContext.startActivity(it);
		// }
		// };
		// setDialog = new SetDialog(mHomeContext, mOnSetDialogListener);
	}

	/**
	 * 设置默认显示
	 */
	private void setDefaultShow() {
		callHomePageData(); // 请求首页数据
		setPageContent("首页", homepageContent);
		// 设置轮播图
		mImageIds.add(R.drawable.homepage11);
		mImageIds.add(R.drawable.homepage22);
		mImageIds.add(R.drawable.homepage33);
		mAdView.setImageResources(mImageIds, new ImageCycleViewListener() {

			@Override
			public void onImageClick(int position, View imageView) {

			}

			@Override
			public void displayImage(int imageId, ImageView imageView) {
				imageView.setImageResource(imageId);
			}
		});
		// slidingMenu.toggle();
		AsynImageLoader asynImageLoader = new AsynImageLoader();
		asynImageLoader.showImageAsyn(ivPicture, avatar, R.drawable.test140140);
		llRight.setVisibility(View.INVISIBLE);
		tvUser.setText(spHome.getString("user_name", ""));
	}

	@Override
	protected void addClickEvent() {
		// llLeft.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// slidingMenu.toggle();
		// }
		// });

		llRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("order".equals(rightBtnType)) {
					Intent it = new Intent(mHomeContext, OrderQueryActivity.class);
					it.putExtra("state", "home");
					startActivity(it);
				} else if ("goods".equals(rightBtnType)) {
					Intent it = new Intent(mHomeContext, GoodQueryActivity.class);
					startActivityForResult(it, MyConstant.RESULTCODE_13);
				} else if ("bill".equals(rightBtnType)) {
					Intent it = new Intent(mHomeContext, BillQueryActivity.class);
					startActivityForResult(it, MyConstant.RESULTCODE_14);
				}
			}
		});

		// 设置
		// ibSet.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // String type = "myShop_home";
		// // callMyShopData(type);// 请求数据
		// setDialog.show();
		// }
		// });

		iv_homepage_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HomePageActivity.this.finish();
			}
		});

		// 首页
		llbar1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 开始轮播
				mAdView.startImageCycle();
				// 隐藏顶部导航栏
				mtb.setVisibility(View.GONE);
				// 设置导航栏显示状态
				ivBarImg1.setImageResource(R.drawable.bar11);
				tvBarTip1.setTextColor(Color.rgb(139, 186, 41));
				ivBarImg2.setImageResource(R.drawable.bar2);
				tvBarTip2.setTextColor(Color.WHITE);
				ivBarImg3.setImageResource(R.drawable.bar3);
				tvBarTip3.setTextColor(Color.WHITE);
				ivBarImg4.setImageResource(R.drawable.bar4);
				tvBarTip4.setTextColor(Color.WHITE);

				callHomePageData(); // 请求数据
				llRight.setVisibility(View.INVISIBLE);
				myTitleBar_rightIv.setVisibility(View.INVISIBLE);
				setPageContent("首页", homepageContent);// 加载显示内容
			}
		});
		// 商品
		llbar2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 停止轮播
				mAdView.pushImageCycle();
				// 显示顶部导航栏
				mtb.setVisibility(View.VISIBLE);
				// 设置导航栏显示状态
				ivBarImg1.setImageResource(R.drawable.bar1);
				tvBarTip1.setTextColor(Color.WHITE);
				ivBarImg2.setImageResource(R.drawable.bar22);
				tvBarTip2.setTextColor(Color.rgb(139, 186, 41));
				ivBarImg3.setImageResource(R.drawable.bar3);
				tvBarTip3.setTextColor(Color.WHITE);
				ivBarImg4.setImageResource(R.drawable.bar4);
				tvBarTip4.setTextColor(Color.WHITE);

				// callGoodsList();
				progressDialog.show();
				param.clear();
				param.put("token", token);
				String uriGoods = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods";
				String typeGoods = "goods";
				iGetRequest.putExtra("uri", uriGoods);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typeGoods);
				startService(iGetRequest);
				isGetServiceRunning = true;

				llRight.setVisibility(View.VISIBLE);
				myTitleBar_rightIv.setVisibility(View.VISIBLE);
				rightBtnType = "goods";
				setPageContent("商品", goodsContent);
			}
		});
		// 订单
		llbar3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 停止轮播
				mAdView.pushImageCycle();
				// 显示顶部导航栏
				mtb.setVisibility(View.VISIBLE);
				// 设置导航栏显示状态
				ivBarImg1.setImageResource(R.drawable.bar1);
				tvBarTip1.setTextColor(Color.WHITE);
				ivBarImg2.setImageResource(R.drawable.bar2);
				tvBarTip2.setTextColor(Color.WHITE);
				ivBarImg3.setImageResource(R.drawable.bar33);
				tvBarTip3.setTextColor(Color.rgb(139, 186, 41));
				ivBarImg4.setImageResource(R.drawable.bar4);
				tvBarTip4.setTextColor(Color.WHITE);

				// callOrderList();

				progressDialog.show();
				param.clear();
				param.put("token", token);
				String uriAll = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
				String typeAll = "orderList";
				iGetRequest.putExtra("uri", uriAll);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typeAll);
				startService(iGetRequest);
				isGetServiceRunning = true;

				llRight.setVisibility(View.VISIBLE);
				myTitleBar_rightIv.setVisibility(View.VISIBLE);
				rightBtnType = "order";
				setPageContent("全部订单", ordersContent);
			}
		});
		// 咨询
		// llbar4.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// param.clear();
		// param.put("token", token);
		// String uri = MyConstant.SERVICENAME +
		// "/index.php?pf=m_seller&app=qa";
		// String type = "qa";
		// callHttpGet(uri, type);
		//
		// llRight.setVisibility(View.INVISIBLE);
		// myTitleBar_rightIv.setVisibility(View.INVISIBLE);
		// setPageContent("咨询", consultationContent);
		// HeaderViewListAdapter hvla = (HeaderViewListAdapter)
		// lvConsultationManagerList.getAdapter();
		// ConsultationAdapter ca = (ConsultationAdapter)
		// hvla.getWrappedAdapter();
		// ca.notifyDataSetChanged();
		// lvConsultationManagerList.onRefreshComplete();
		// }
		// });
		// 财务
		llbar4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 停止轮播
				mAdView.pushImageCycle();
				// 显示顶部导航栏
				mtb.setVisibility(View.VISIBLE);
				// 设置导航栏显示状态
				ivBarImg1.setImageResource(R.drawable.bar1);
				tvBarTip1.setTextColor(Color.WHITE);
				ivBarImg2.setImageResource(R.drawable.bar2);
				tvBarTip2.setTextColor(Color.WHITE);
				ivBarImg3.setImageResource(R.drawable.bar3);
				tvBarTip3.setTextColor(Color.WHITE);
				ivBarImg4.setImageResource(R.drawable.bar44);
				tvBarTip4.setTextColor(Color.rgb(139, 186, 41));

				// callBillList();
				progressDialog.show();
				param.clear();
				param.put("token", token);
				String uriBill = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance";
				String typeBill = "bill";
				iGetRequest.putExtra("uri", uriBill);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", typeBill);
				startService(iGetRequest);
				isGetServiceRunning = true;

				rightBtnType = "bill";
				llRight.setVisibility(View.VISIBLE);
				myTitleBar_rightIv.setVisibility(View.VISIBLE);
				setPageContent("财务", financialContent);
			}
		});
		// 个人昵称
		// tvUser.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 设置画面title
		// myTitleBar_title.setText(R.string.user_detail);
		// // 加载页面内容
		// if (llContent.getChildCount() == 0) {
		// llContent.addView(userDetailContent);
		// // slidingMenu.toggle();
		// } else {
		// llContent.removeAllViews();
		// llContent.addView(userDetailContent);
		// // slidingMenu.toggle();
		// }
		// llRight.setVisibility(View.INVISIBLE);
		// myTitleBar_rightIv.setVisibility(View.INVISIBLE);
		// // 网络请求
		// userDetailRequest();
		// }
		// });
		// 个人头像
		// civPicture.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 设置画面title
		// myTitleBar_title.setText(R.string.user_detail);
		// // 加载页面内容
		// if (llContent.getChildCount() == 0) {
		// llContent.addView(userDetailContent);
		// // slidingMenu.toggle();
		// } else {
		// llContent.removeAllViews();
		// llContent.addView(userDetailContent);
		// // slidingMenu.toggle();
		// }
		// llRight.setVisibility(View.INVISIBLE);
		// myTitleBar_rightIv.setVisibility(View.INVISIBLE);
		// // 网络请求
		// userDetailRequest();
		// }
		// });
		// 【个人资料】->绑定手机
		// rlUserDetailBind.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent it = new Intent(mHomeContext, FindPasswordActivity.class);
		// it.putExtra("phone", spHome.getString("phone", ""));
		// startActivityForResult(it, 40);
		// }
		// });
		// // 【个人资料】->修改密码
		// rlUserDetailChangePsw.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent it = new Intent(mHomeContext, ChangePassWordActivity.class);
		// startActivity(it);
		// }
		// });
		// 【个人头像】
		ivPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callMyShopData("myShop_home");
			}
		});
		// 版本更新
		// btnAppUpdate.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 获取当前版本号
		// PackageManager manager = mHomeContext.getPackageManager();
		// PackageInfo info = null;
		// try {
		// info = manager.getPackageInfo(mHomeContext.getPackageName(), 0);
		// } catch (NameNotFoundException e) {
		// e.printStackTrace();
		// }
		// versionName = info.versionName;
		// // 调用接口查看是否有更新
		// param.clear();
		// param.put("type", "android");
		// param.put("version", String.valueOf(versionName));
		// String uri = MyConstant.SERVICENAME +
		// "/index.php?pf=m_seller&act=check_version";
		// String type = "appUpdate";
		// callHttpGet(uri, type);
		// llRight.setVisibility(View.INVISIBLE);
		// myTitleBar_rightIv.setVisibility(View.INVISIBLE);
		// }
		// });
		// 【我的店铺】
		// rlSetMyShop.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// callMyShopData("myShop_home");// 请求数据
		// }
		// });

		// 【修改密码】
		// rlSetChangePsw.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent it = new Intent(mHomeContext, FindPasswordActivity.class);
		// it.putExtra("account", tvUser.getText().toString());
		// startActivityForResult(it, 40);
		// }
		// });

		// 【检查更新】
		// rlSetCheckUpd.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 获取当前版本号
		// PackageManager manager = mHomeContext.getPackageManager();
		// PackageInfo info = null;
		// try {
		// info = manager.getPackageInfo(mHomeContext.getPackageName(), 0);
		// } catch (NameNotFoundException e) {
		// e.printStackTrace();
		// }
		// String vName = info.versionName;
		// param.clear();
		// param.put("type", "android");
		// param.put("version", vName);
		// String uri = MyConstant.SERVICENAME +
		// "/index.php?pf=m_seller&act=check_version";
		// String type = "appUpdate";
		// iGetRequest.putExtra("uri", uri);
		// iGetRequest.putExtra("param", param);
		// iGetRequest.putExtra("type", type);
		// startService(iGetRequest);
		// isGetServiceRunning = true;
		// }
		// });

		// 【联系我们】
		// rlSetContactUs.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// String phoneNo = tvSetPhone.getText().toString();
		// if (!"无".equals(phoneNo)) {
		// Uri uri = Uri.parse("tel:" + phoneNo);
		// Intent it = new Intent();
		// // it.setAction(Intent.ACTION_CALL);//直接拨打电话
		// it.setAction(Intent.ACTION_DIAL);// 调用软件盘方式拨打电话
		// it.setData(uri);
		// mHomeContext.startActivity(it);
		// }
		// }
		// });

		// 退出登录
		// btnSetLogOff.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// editor.remove("user_id");
		// editor.remove("token");
		// editor.commit();
		// HomePageActivity.this.finish();
		// }
		// });
	}

	/**
	 * 向服务器发送device_token
	 */
	private void putDeviceTokenToServer() {
		for (int i = 0; i < 3; i++) {
			device_token = callUMengDeviceToken(mHomeContext);
		}
		if ("".equals(device_token)) {
			device_token = callUMengDeviceToken(mHomeContext);
		} else {
			System.out.println("开始上传");
			param.clear();
			param.put("token", token);
			param.put("device_token", device_token);
			param.put("phone_type", "1");
			String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=member&act=umeng_dtoken";
			String type = "umeng_dtoken";
			callHttpPost(uri, type);
		}
	}

	/**
	 * 设置按钮背景
	 */
	// private void selectButton(String buttonName) {
	// HashMap<String, Integer> map;
	// if ("homepage".equals(buttonName)) {
	// map = mapWhite.get("homepage");
	// setHomeButtonBackground(btnHomePage, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("good");
	// setHomeButtonBackground(btnGoods, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("order");
	// setHomeButtonBackground(btnOrders, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("consultation");
	// setHomeButtonBackground(btnConsultation, map.get("bacId"),
	// map.get("imgId"), map.get("textColor"));
	// map = mapBlack.get("finance");
	// setHomeButtonBackground(btnFinance, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("update");
	// setHomeButtonBackground(btnAppUpdate, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// } else if ("good".equals(buttonName)) {
	// map = mapBlack.get("homepage");
	// setHomeButtonBackground(btnHomePage, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapWhite.get("good");
	// setHomeButtonBackground(btnGoods, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("order");
	// setHomeButtonBackground(btnOrders, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("consultation");
	// setHomeButtonBackground(btnConsultation, map.get("bacId"),
	// map.get("imgId"), map.get("textColor"));
	// map = mapBlack.get("finance");
	// setHomeButtonBackground(btnFinance, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("update");
	// setHomeButtonBackground(btnAppUpdate, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// } else if ("order".equals(buttonName)) {
	// map = mapBlack.get("homepage");
	// setHomeButtonBackground(btnHomePage, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("good");
	// setHomeButtonBackground(btnGoods, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapWhite.get("order");
	// setHomeButtonBackground(btnOrders, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("consultation");
	// setHomeButtonBackground(btnConsultation, map.get("bacId"),
	// map.get("imgId"), map.get("textColor"));
	// map = mapBlack.get("finance");
	// setHomeButtonBackground(btnFinance, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("update");
	// setHomeButtonBackground(btnAppUpdate, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// } else if ("consultation".equals(buttonName)) {
	// map = mapBlack.get("homepage");
	// setHomeButtonBackground(btnHomePage, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("good");
	// setHomeButtonBackground(btnGoods, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("order");
	// setHomeButtonBackground(btnOrders, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapWhite.get("consultation");
	// setHomeButtonBackground(btnConsultation, map.get("bacId"),
	// map.get("imgId"), map.get("textColor"));
	// map = mapBlack.get("finance");
	// setHomeButtonBackground(btnFinance, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("update");
	// setHomeButtonBackground(btnAppUpdate, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// } else if ("finance".equals(buttonName)) {
	// map = mapBlack.get("homepage");
	// setHomeButtonBackground(btnHomePage, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("good");
	// setHomeButtonBackground(btnGoods, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("order");
	// setHomeButtonBackground(btnOrders, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("consultation");
	// setHomeButtonBackground(btnConsultation, map.get("bacId"),
	// map.get("imgId"), map.get("textColor"));
	// map = mapWhite.get("finance");
	// setHomeButtonBackground(btnFinance, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("update");
	// setHomeButtonBackground(btnAppUpdate, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// } else if ("update".equals(buttonName)) {
	// map = mapBlack.get("homepage");
	// setHomeButtonBackground(btnHomePage, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("good");
	// setHomeButtonBackground(btnGoods, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("order");
	// setHomeButtonBackground(btnOrders, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("consultation");
	// setHomeButtonBackground(btnConsultation, map.get("bacId"),
	// map.get("imgId"), map.get("textColor"));
	// map = mapBlack.get("finance");
	// setHomeButtonBackground(btnFinance, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapWhite.get("update");
	// setHomeButtonBackground(btnAppUpdate, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// }
	//
	// }

	/**
	 * 设置按钮背景
	 */
	// private void setHomeButtonBackground(MyHomeButton btn, int bacId, int
	// imgId, int textColor) {
	//
	// btn.setButtonBackground(bacId);
	// btn.setButtonImg(imgId);
	// btn.setButtonTextColor(textColor);
	// }

	/**
	 * 初始化按钮值
	 */
	// private void initButton() {
	// HashMap<String, Integer> map;
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongreen);
	// map.put("imgId", R.drawable.homepagewhite);
	// map.put("textColor", Color.WHITE);
	// mapWhite.put("homepage", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongreen);
	// map.put("imgId", R.drawable.goodswhite);
	// map.put("textColor", Color.WHITE);
	// mapWhite.put("good", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongreen);
	// map.put("imgId", R.drawable.orderwhite);
	// map.put("textColor", Color.WHITE);
	// mapWhite.put("order", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongreen);
	// map.put("imgId", R.drawable.consultationwhite);
	// map.put("textColor", Color.WHITE);
	// mapWhite.put("consultation", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongreen);
	// map.put("imgId", R.drawable.financewhite);
	// map.put("textColor", Color.WHITE);
	// mapWhite.put("finance", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongreen);
	// map.put("imgId", R.drawable.updatewhite);
	// map.put("textColor", Color.WHITE);
	// mapWhite.put("update", map);
	//
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongray);
	// map.put("imgId", R.drawable.homepageblack);
	// map.put("textColor", Color.BLACK);
	// mapBlack.put("homepage", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongray);
	// map.put("imgId", R.drawable.goodsblack);
	// map.put("textColor", Color.BLACK);
	// mapBlack.put("good", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongray);
	// map.put("imgId", R.drawable.orderblack);
	// map.put("textColor", Color.BLACK);
	// mapBlack.put("order", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongray);
	// map.put("imgId", R.drawable.consultationblack);
	// map.put("textColor", Color.BLACK);
	// mapBlack.put("consultation", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongray);
	// map.put("imgId", R.drawable.financeblack);
	// map.put("textColor", Color.BLACK);
	// mapBlack.put("finance", map);
	// map = new HashMap<String, Integer>();
	// map.put("bacId", R.drawable.homebuttongray);
	// map.put("imgId", R.drawable.updateblack);
	// map.put("textColor", Color.BLACK);
	// mapBlack.put("update", map);
	//
	// }

	/**
	 * 设置homebutton未选中
	 */
	// private void setHomeButtonUnselect() {
	// HashMap<String, Integer> map;
	// map = mapBlack.get("homepage");
	// setHomeButtonBackground(btnHomePage, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("good");
	// setHomeButtonBackground(btnGoods, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("order");
	// setHomeButtonBackground(btnOrders, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("consultation");
	// setHomeButtonBackground(btnConsultation, map.get("bacId"),
	// map.get("imgId"), map.get("textColor"));
	// map = mapBlack.get("finance");
	// setHomeButtonBackground(btnFinance, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// map = mapBlack.get("update");
	// setHomeButtonBackground(btnAppUpdate, map.get("bacId"), map.get("imgId"),
	// map.get("textColor"));
	// }

	/**
	 * 初始化各画面内容
	 */
	private void initView() {
		initHomePage(); // 初始化主页
		initOrdersPage();// 初始化订单页
		initBillPage();// 初始化对账单页
		initBillHeadPage();// 初始化付款通知单页
		initFinacialPage();// 初始化财务页
		// initConsultationManagerPage();// 咨询管理页初始化
		// initWaitCallConsultationPage();// 待回复咨询管理页初始化
		initConsultationPage();// 初始化咨询页
		initGoodsPage();// 初始化商品页
	}

	/**
	 * 主页初始化
	 */
	private void initHomePage() {
		// 设置homepage背景图片
		homeImages.add(vHomeImage1);
		homeImages.add(vHomeImage2);
		homeImages.add(vHomeImage3);
		// MyViewPagerAdapter adapter = new MyViewPagerAdapter(homeImages);
		// MyHomeViewPagerAdapter adapter = new
		// MyHomeViewPagerAdapter(homeImages);
		// vpHome.setAdapter(adapter);
		// vpHome.setCurrentItem(0);

		// 设置菜单内容
		String[] no = new String[] { "0", "0", "0", "0" };
		int[] pic = new int[] { R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4 };
		String[] title = new String[] { "待发货订单", "待处理消息", "产品咨询", "缺货登记" };
		boolean[] check = new boolean[] { false, false, false, false };

		int iSize = no.length;
		for (int i = 0; i < iSize; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("tv_itemNo", no[i]);
			map.put("iv_itemPic", pic[i]);
			map.put("isShow", check[i]);
			map.put("title", title[i]);
			lstHomePageSource.add(map);
		}

		MyGridViewAdapter mgva = new MyGridViewAdapter(mHomeContext, lstHomePageSource);
		mgvMenu.setAdapter(mgva);

		mgvMenu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					// 获取待发货订单列表
					param.clear();
					param.put("token", token);
					param.put("status", "20");
					String uriNeed = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
					String typeNeed = "needSend";
					callHttpGet(uriNeed, typeNeed);
					break;

				case 1:
					// 获取待处理消息列表
					param.clear();
					param.put("token", token);
					param.put("type", "all");
					String uriMessage = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=pm";
					String typeMessage = "message";
					callHttpGet(uriMessage, typeMessage);
					break;

				case 2:
					// 获取产品咨询列表
					param.clear();
					param.put("token", token);
					String uriPro = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
					String typePro = "Pro";
					callHttpGet(uriPro, typePro);
					break;

				case 3:
					// 获取缺货登记列表
					param.clear();
					param.put("token", token);
					String uriOut = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=notice";
					String typeOut = "Out";
					callHttpGet(uriOut, typeOut);
					break;

				// case 4:
				// // 获取全部订单列表
				// param.clear();
				// param.put("token", token);
				// String uriAll = MyConstant.SERVICENAME +
				// "/index.php?pf=m_seller&app=order";
				// String typeAll = "allOrder";
				// callHttpGet(uriAll, typeAll);
				// break;
				//
				// case 5:
				// // 获取对账单列表
				// param.clear();
				// param.put("token", token);
				// String uriAccount = MyConstant.SERVICENAME +
				// "/index.php?pf=m_seller&app=seller_finance";
				// String typeAccount = "Account";
				// callHttpGet(uriAccount, typeAccount);
				// break;
				}

			}
		});
	}

	/**
	 * 订单页初始化
	 */
	private void initOrdersPage() {

		final OrderListAdapter osa = new OrderListAdapter(mHomeContext);
		prlvOrdersList.setAdapter(osa);
		prlvOrdersList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				OrderListInfo info = (OrderListInfo) parent.getItemAtPosition(position);
				String strId = info.getOrder_id();

				param.clear();
				param.put("token", token);
				param.put("order_id", strId);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=view";
				String type = "orderDetail";
				editor.putInt("orderIndex", position);
				editor.commit();
				callHttpGet(uri, type);
			}
		});

		// lvOrdersList.setonRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		// callOrderList();
		// }
		// });

		prlvOrdersList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (prlvOrdersList.isHeaderShown()) {
					callOrderList();

				} else if (prlvOrdersList.isFooterShown()) {
					int size = osa.getCount();
					loadOrderList(size);
				}

				prlvOrdersList.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvOrdersList.onRefreshComplete();
					}
				}, 1000);
			}
		});
	}

	/**
	 * 咨询页初始化
	 */
	private void initFinacialPage() {
		// 初始化标题
		tvFinancialTitle1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vpFinancialContent.setCurrentItem(0);
				ivFinancialImg1.setVisibility(View.VISIBLE);
				ivFinancialImg2.setVisibility(View.INVISIBLE);
				tvFinancialTitle1.setTextColor(Color.rgb(139, 186, 41));
				tvFinancialTitle2.setTextColor(Color.rgb(97, 97, 97));
				llRight.setVisibility(View.VISIBLE);
				myTitleBar_rightIv.setVisibility(View.VISIBLE);
			}
		});

		tvFinancialTitle2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vpFinancialContent.setCurrentItem(1);
				ivFinancialImg1.setVisibility(View.INVISIBLE);
				ivFinancialImg2.setVisibility(View.VISIBLE);
				tvFinancialTitle1.setTextColor(Color.rgb(97, 97, 97));
				tvFinancialTitle2.setTextColor(Color.rgb(139, 186, 41));
				llRight.setVisibility(View.INVISIBLE);
				myTitleBar_rightIv.setVisibility(View.INVISIBLE);
				param.clear();
				param.put("token", token);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance&act=billhead_list";
				String type = "billhead_list";
				callHttpGet(uri, type);
			}
		});

		// 初始化ViewPager内容
		lstFinancials.add(vBill);
		lstFinancials.add(vBillhead);
		vpFinancialContent.setAdapter(new MyViewPagerAdapter(lstFinancials));
		vpFinancialContent.setCurrentItem(0);

	}

	/**
	 * 对账单页初始化
	 */
	private void initBillPage() {

		final BillAdapter ba = new BillAdapter(mHomeContext);
		prlvBillList.setAdapter(ba);

		prlvBillList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BillInfo info = (BillInfo) parent.getItemAtPosition(position);
				String sta_id = info.getSta_id();
				param.clear();
				param.put("token", token);
				param.put("sta_id", sta_id);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance&act=view";
				String type = "view";
				callHttpGet(uri, type);
			}
		});

		// lvBillList.setonRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		// callBillList();
		// }
		// });

		prlvBillList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (prlvBillList.isHeaderShown()) {
					callBillList();

				} else if (prlvBillList.isFooterShown()) {
					int size = ba.getCount();
					loadBillList(size);
				}

				prlvBillList.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvBillList.onRefreshComplete();
					}
				}, 1000);
			}
		});
	}

	/**
	 * 付款通知单页初始化
	 */
	private void initBillHeadPage() {

		final SimpleAdapter sa = new SimpleAdapter(mHomeContext, lstBillHeadPageSource, R.layout.billheaditem,
				new String[] { "tv_billHeadItem_billHeadNo", "tv_billHeadItem_price", "tv_billHeadItem_date" },
				new int[] { R.id.tv_billHeadItem_billHeadNo, R.id.tv_billHeadItem_price, R.id.tv_billHeadItem_date });
		prlvBillHeadList.setAdapter(sa);

		prlvBillHeadList.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> map = (HashMap<String, String>) parent.getItemAtPosition(position);
				Intent it = new Intent(mHomeContext, BillHeadDetailActivity.class);
				it.putExtra("data", map);
				startActivity(it);
			}
		});

		// lvBillHeadList.setonRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		// param.clear();
		// param.put("token", token);
		// String uri = MyConstant.SERVICENAME +
		// "/index.php?pf=m_seller&app=seller_finance&act=billhead_list";
		// String type = "billhead_list";
		// callHttpGet(uri, type);
		// }
		// });

		prlvBillHeadList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (prlvBillHeadList.isHeaderShown()) {
					param.clear();
					param.put("token", token);
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance&act=billhead_list";
					String type = "billhead_list";
					callHttpGet(uri, type);

				} else if (prlvBillHeadList.isFooterShown()) {
					int size = sa.getCount();
					param.clear();
					param.put("token", token);
					param.put("type", "all");
					param.put("offset", String.valueOf(size));
					String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance&act=billhead_list";
					String type = "billhead_list_load";
					callHttpGet(uri, type);
				}

				prlvBillHeadList.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvBillHeadList.onRefreshComplete();
					}
				}, 1000);
			}
		});
	}

	/**
	 * 咨询页初始化
	 */
	private void initConsultationPage() {
		// 初始化标题
		tvConsultationTitle1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vpConsultationContent.setCurrentItem(0);
				ivImage1.setVisibility(View.VISIBLE);
				ivImage2.setVisibility(View.INVISIBLE);
				tvConsultationTitle1.setTextColor(Color.rgb(139, 186, 41));
				tvConsultationTitle2.setTextColor(Color.rgb(97, 97, 97));
			}
		});

		tvConsultationTitle2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vpConsultationContent.setCurrentItem(1);
				ivImage1.setVisibility(View.INVISIBLE);
				ivImage2.setVisibility(View.VISIBLE);
				tvConsultationTitle1.setTextColor(Color.rgb(97, 97, 97));
				tvConsultationTitle2.setTextColor(Color.rgb(139, 186, 41));
				param.clear();
				param.put("token", token);
				param.put("no_reply", 1);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
				String type = "waitcallqa";
				callHttpGet(uri, type);

			}
		});
		// 初始化ViewPager内容
		lstConsultations.add(vConsultationManager);
		lstConsultations.add(vWaitCallConsultation);
		vpConsultationContent.setAdapter(new MyViewPagerAdapter(lstConsultations));
		vpConsultationContent.setCurrentItem(0);

	}

	/**
	 * 咨询管理页初始化
	 */
	// private void initConsultationManagerPage() {
	// ConsultationAdapter ca = new ConsultationAdapter(mHomeContext, new
	// TvOnClickListener() {
	//
	// @Override
	// public void myTvOnClickListener(ChatInfo info, View v) {
	// final TextView tv = (TextView) v;
	// final ChatInfo chat = info;
	// final View vv =
	// LayoutInflater.from(mHomeContext).inflate(R.layout.recallpopitem, null);
	// AlertDialog.Builder builder = new
	// AlertDialog.Builder(mHomeContext).setTitle("回复评论").setView(vv)
	// .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	//
	// }
	// }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// EditText et = (EditText) vv.findViewById(R.id.et_pop_content);
	// String reply_content = et.getText().toString();
	// tv.setText(reply_content);
	// chat.setReply_content(reply_content);
	// String ques_id = chat.getQues_id();
	// if (!"".equals(reply_content)) {
	// param.clear();
	// param.put("token", token);
	// param.put("ques_id", ques_id);
	// param.put("reply_content", reply_content);
	// String uri = MyConstant.SERVICENAME +
	// "/index.php?pf=m_seller&app=qa&act=reply";
	// String type = "reply";
	// callHttpPost(uri, type);
	// }
	// }
	// });
	//
	// builder.show();
	// }
	// });
	//
	// lvConsultationManagerList.setAdapter(ca);
	//
	// lvConsultationManagerList.setOnItemLongClickListener(new
	// OnItemLongClickListener() {
	//
	// @Override
	// public boolean onItemLongClick(AdapterView<?> parent, View view, int
	// position, long id) {
	// ChatInfo chat = (ChatInfo) parent.getItemAtPosition(position);
	// final String ques_id = chat.getQues_id();
	// AlertDialog alert = new
	// AlertDialog.Builder(mHomeContext).setTitle("确认删除该咨询吗？")
	// .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	//
	// }
	// }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// param.put("token", token);
	// param.put("ques_id", ques_id);
	// String uri = MyConstant.SERVICENAME +
	// "/index.php?pf=m_seller&app=qa&act=del";
	// progressDialog.show();
	// String type = "del";
	// iGetRequest.putExtra("uri", uri);
	// iGetRequest.putExtra("param", param);
	// iGetRequest.putExtra("type", type);
	// startService(iGetRequest);
	// isGetServiceRunning = true;
	// param.clear();
	// }
	// }).create();
	// alert.show();
	// return false;
	// }
	// });
	//
	// lvConsultationManagerList.setonRefreshListener(new OnRefreshListener() {
	//
	// @Override
	// public void onRefresh() {
	// param.clear();
	// param.put("token", token);
	// String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
	// String type = "qa";
	// callHttpGet(uri, type);
	// }
	// });
	//
	// }

	/**
	 * 待回复咨询页初始化
	 */
	// private void initWaitCallConsultationPage() {
	// ConsultationAdapter ca = new ConsultationAdapter(mHomeContext, new
	// TvOnClickListener() {
	//
	// @Override
	// public void myTvOnClickListener(ChatInfo info, View v) {
	// final TextView tv = (TextView) v;
	// final ChatInfo chat = info;
	// final View vv =
	// LayoutInflater.from(mHomeContext).inflate(R.layout.recallpopitem, null);
	// AlertDialog.Builder builder = new
	// AlertDialog.Builder(mHomeContext).setTitle("回复评论").setView(vv)
	// .setNegativeButton("取消", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	//
	// }
	// }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
	//
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// EditText et = (EditText) vv.findViewById(R.id.et_pop_content);
	// String reply_content = et.getText().toString();
	// tv.setText(reply_content);
	// chat.setReply_content(reply_content);
	// String ques_id = chat.getQues_id();
	// if (!"".equals(reply_content)) {
	// param.clear();
	// param.put("token", token);
	// param.put("ques_id", ques_id);
	// param.put("reply_content", reply_content);
	// String uri = MyConstant.SERVICENAME +
	// "/index.php?pf=m_seller&app=qa&act=reply";
	// String type = "reply";
	// callHttpPost(uri, type);
	// }
	// }
	// });
	//
	// builder.show();
	// }
	// });
	//
	// lvWaitCallConsultationList.setAdapter(ca);
	//
	// lvWaitCallConsultationList.setonRefreshListener(new OnRefreshListener() {
	//
	// @Override
	// public void onRefresh() {
	// param.clear();
	// param.put("token", token);
	// param.put("no_reply", 1);
	// String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
	// String type = "waitcallqa";
	// callHttpGet(uri, type);
	// }
	// });
	//
	// }

	/**
	 * 设置商品页
	 */
	private void initGoodsPage() {

		initGoodsManagerPage(); // 初始化商品管理
		initGoodsTypeManagerPage(); // 初始化分类管理

		tvGoodsTitle1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vpGoodsContent.setCurrentItem(0);
				ivGoodsImg1.setVisibility(View.VISIBLE);
				ivGoodsImg2.setVisibility(View.INVISIBLE);
				llRight.setVisibility(View.VISIBLE);
				tvGoodsTitle1.setTextColor(Color.rgb(139, 186, 41));
				tvGoodsTitle2.setTextColor(Color.rgb(97, 97, 97));

				callGoodsList();
			}
		});

		tvGoodsTitle2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				vpGoodsContent.setCurrentItem(1);
				ivGoodsImg1.setVisibility(View.INVISIBLE);
				ivGoodsImg2.setVisibility(View.VISIBLE);
				tvGoodsTitle1.setTextColor(Color.rgb(97, 97, 97));
				tvGoodsTitle2.setTextColor(Color.rgb(139, 186, 41));
				llRight.setVisibility(View.INVISIBLE);
				param.clear();
				param.put("token", token);
				System.out.println(token);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_category";
				String type = "category";
				callHttpGet(uri, type);

			}
		});

		// 初始化ViewPager内容
		lstGoods.add(vGoodsManager);
		lstGoods.add(vGoodsTypeManager);
		vpGoodsContent.setAdapter(new MyViewPagerAdapter(lstGoods));
		vpGoodsContent.setCurrentItem(0);
	}

	/**
	 * 初始化商品管理
	 */
	private void initGoodsManagerPage() {
		final GoodsManagerAdapter gma = new GoodsManagerAdapter(mHomeContext);
		prlvGoodsManagerList.setAdapter(gma);

		prlvGoodsManagerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvGoodsManagerList.getRefreshableView()
						.getAdapter();
				GoodsManagerAdapter gma = (GoodsManagerAdapter) hvla.getWrappedAdapter();
				GoodsInfo info = gma.getGoodsInfo(position - 1);
				Intent it = new Intent(mHomeContext, GoodDetailActivity.class);
				it.putExtra("goodDetailInfo", info);
				it.putExtra("token", token);
				startActivityForResult(it, MyConstant.RESULTCODE_16);
			}
		});

		// lvGoodsManagerList.setonRefreshListener(new OnRefreshListener() {
		//
		// @Override
		// public void onRefresh() {
		// callGoodsList();
		// }
		// });

		prlvGoodsManagerList.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (prlvGoodsManagerList.isHeaderShown()) {
					callGoodsList();

				} else if (prlvGoodsManagerList.isFooterShown()) {
					int size = gma.getCount();
					loadGoodsList(size);
				}

				prlvGoodsManagerList.postDelayed(new Runnable() {

					@Override
					public void run() {
						prlvGoodsManagerList.onRefreshComplete();
					}
				}, 1000);
			}
		});
	}

	/**
	 * 初始化分类管理
	 */
	private void initGoodsTypeManagerPage() {

		treeAdapter = new MyTreeViewAdapter(mHomeContext, lstGoodsTypeGroups, new EtOnClickListener() {

			@Override
			public void myEtOnClickListener(final MyTreeElement element, View v) {
				final EditText et = (EditText) v;
				final View vv = LayoutInflater.from(mHomeContext).inflate(R.layout.recallpopitem, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(mHomeContext).setTitle("修改排序").setView(vv)
						.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText ett = (EditText) vv.findViewById(R.id.et_pop_content);
						String value = ett.getText().toString();
						et.setText(value);
						element.setSort(value);
						String cate_id = element.getId();
						if (!"".equals(value)) {
							param.put("token", token);
							param.put("cate_id", cate_id);
							param.put("colum", "sort_order");
							param.put("value", value);
							String uri = MyConstant.SERVICENAME
									+ "/index.php?pf=m_seller&app=seller_category&act=edit_col";
							String type = "edit_col";
							callHttpGet(uri, type);
						}
					}
				});

				builder.show();

			}
		}, new CbOnClickListener() {

			@Override
			public void cbOnClickListener(MyTreeElement element, View v) {
				MyTreeElement element1 = (MyTreeElement) v.getTag();
				element1.checked = !element1.checked;
				element1.setParentChecked(element1.checked);
				String cate_id = element1.getId();
				param.put("token", token);
				param.put("cate_id", cate_id);
				System.out.println("cate_id   " + cate_id);
				param.put("colum", "If_show");
				if (element1.checked) {
					param.put("value", "1");
				} else {
					param.put("value", "0");
				}
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_category&act=edit_col";
				String type = "edit_col";
				callHttpGet(uri, type);
			}
		});
		lvGoodsTypeManagerList.setAdapter(treeAdapter);
		lvGoodsTypeManagerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("click");
				if (lstGoodsTypeGroups.get(position).isExpanded()) {
					lstGoodsTypeGroups.get(position).setExpanded(false);
					MyTreeElement element = lstGoodsTypeGroups.get(position);
					ArrayList<MyTreeElement> temp = new ArrayList<MyTreeElement>();
					for (int i = position + 1; i < lstGoodsTypeGroups.size(); i++) {
						if (element.getLevel() >= lstGoodsTypeGroups.get(i).getLevel()) {
							break;
						}
						temp.add(lstGoodsTypeGroups.get(i));
					}

					lstGoodsTypeGroups.removeAll(temp);
					treeAdapter.notifyDataSetChanged();

				} else {
					MyTreeElement obj = lstGoodsTypeGroups.get(position);
					obj.setExpanded(true);
					int level = obj.getLevel();
					int nextLevel = level + 1;
					for (MyTreeElement element : obj.getChildList()) {
						element.setLevel(nextLevel);
						element.setExpanded(false);
						lstGoodsTypeGroups.add(position + 1, element);
					}
					treeAdapter.notifyDataSetChanged();

				}
			}
		});

		lvGoodsTypeManagerList.setonMyRefreshListener(new MyOnRefreshListener() {

			@Override
			public void onRefresh() {
				param.clear();
				param.put("token", token);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_category";
				String type = "category";
				callHttpGet(uri, type);
			}
		});

	}

	/**
	 * 设置各画面内容
	 */
	private void setPageContent(String title, View view) {
		// 设置画面title
		myTitleBar_title.setText(title);
		// 加载页面内容
		if (llContent.getChildCount() == 0) {
			llContent.addView(view);
			// slidingMenu.toggle();
		} else {
			llContent.removeAllViews();
			llContent.addView(view);
			// slidingMenu.toggle();
		}
	}

	/**
	 * 加载树节点（递归）
	 */
	private void turn2Tree(MyTreeElement currentE, List<MyTreeElement> treeList) {
		for (int i = 0; i < treeList.size(); i++) {
			MyTreeElement te = treeList.get(i);
			String s = te.getUpId();
			String s1 = currentE.getId();
			if (s.equals(s1)) {
				currentE.addChild(te);
				turn2Tree(te, treeList);
			}
		}
	}

	/**
	 * 注册广播
	 */
	private void registMyBroadcastRecevier() {
		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		HomePageActivity.this.registerReceiver(brPostHttp, filterPostHttp);

		IntentFilter filterGetHttp = new IntentFilter();
		filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
		HomePageActivity.this.registerReceiver(brGetHttp, filterGetHttp);
	}

	/**
	 * get方法请求
	 */
	private void callHttpGet(String uri, String type) {
		progressDialog.show();
		iGetRequest.putExtra("uri", uri);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", type);
		startService(iGetRequest);
		isGetServiceRunning = true;
		param.clear();
	}

	/**
	 * get方法请求
	 */
	private void callHttpPost(String uri, String type) {
		progressDialog.show();
		iPostRequest.putExtra("uri", uri);
		iPostRequest.putExtra("param", param);
		iPostRequest.putExtra("type", type);
		startService(iPostRequest);
		isPostServiceRunning = true;
		param.clear();
	}

	/**
	 * 个人资料详细请求
	 */
	private void userDetailRequest() {
		if ("".equals(token)) {
			MyUtil.ToastMessage(mHomeContext, "个人资料加载失败！");
		} else {
			param.clear();
			param.put("token", token);
			String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=member&act=info";
			progressDialog.show();
			String type = "userDetail";
			callHttpGet(uri, type);

		}

	}

	/**
	 * 请求订单列表
	 */
	private void callOrderList() {
		progressDialog.show();
		param.clear();
		param.put("token", token);
		if (!"".equals(orderSn)) {
			param.put("order_sn", orderSn);
		}
		if (!"".equals(orderStatus)) {
			param.put("status", orderStatus);
		}
		if (!"".equals(evaluationStatus)) {
			param.put("evaluation_status", evaluationStatus);
		}
		if (!"".equals(extension)) {
			param.put("extension", extension);
		}
		if (!"".equals(addTimeTo)) {
			param.put("add_time_to", addTimeTo);
		}
		if (!"".equals(addTimeFrom)) {
			param.put("add_time_from", addTimeFrom);
		}
		String uriAll = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
		String typeAll = "orderList";
		iGetRequest.putExtra("uri", uriAll);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeAll);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}

	/**
	 * 请求订单列表
	 */
	private void loadOrderList(int size) {
		progressDialog.show();
		param.clear();
		param.put("token", token);
		param.put("offset", String.valueOf(size));
		if (!"".equals(orderSn)) {
			param.put("order_sn", orderSn);
		}
		if (!"".equals(orderStatus)) {
			param.put("status", orderStatus);
		}
		if (!"".equals(evaluationStatus)) {
			param.put("evaluation_status", evaluationStatus);
		}
		if (!"".equals(extension)) {
			param.put("extension", extension);
		}
		if (!"".equals(addTimeTo)) {
			param.put("add_time_to", addTimeTo);
		}
		if (!"".equals(addTimeFrom)) {
			param.put("add_time_from", addTimeFrom);
		}
		String uriAll = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
		String typeAll = "orderList_load";
		iGetRequest.putExtra("uri", uriAll);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeAll);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}

	/**
	 * 请求商品列表
	 */
	private void callGoodsList() {
		progressDialog.show();
		param.clear();
		param.put("token", token);
		// if (!"".equals(keyword)) {
		// param.put("keyword", keyword);
		// }
		// if (!"".equals(check)) {
		// param.put("check", check);
		// }
		// if (!"".equals(character)) {
		// param.put("character", character);
		// }
		String uriGoods = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods";
		String typeGoods = "goods";
		iGetRequest.putExtra("uri", uriGoods);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeGoods);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}

	/**
	 * 请求商品列表
	 */
	private void loadGoodsList(int size) {
		progressDialog.show();
		param.clear();
		param.put("token", token);
		param.put("offset", String.valueOf(size));
		if (!"".equals(keyword)) {
			param.put("keyword", keyword);
		}
		if (!"".equals(check)) {
			param.put("check", check);
		}
		if (!"".equals(character)) {
			param.put("character", character);
		}
		String uriGoods = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_goods";
		String typeGoods = "goods_load";
		iGetRequest.putExtra("uri", uriGoods);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeGoods);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}

	/**
	 * 请求对账单列表
	 */
	private void callBillList() {
		progressDialog.show();
		param.clear();
		param.put("token", token);
		if (!"".equals(sta_status)) {
			param.put("sta_status", sta_status);
		}
		if (!"".equals(sta_plat)) {
			param.put("sta_plat", sta_plat);
		}
		String uriBill = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance";
		String typeBill = "bill";
		iGetRequest.putExtra("uri", uriBill);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeBill);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}

	/**
	 * 请求对账单列表
	 */
	private void loadBillList(int size) {
		progressDialog.show();
		param.clear();
		param.put("token", token);
		param.put("offset", String.valueOf(size));
		if (!"".equals(sta_status)) {
			param.put("sta_status", sta_status);
		}
		if (!"".equals(sta_plat)) {
			param.put("sta_plat", sta_plat);
		}
		String uriBill = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_finance";
		String typeBill = "bill_load";
		iGetRequest.putExtra("uri", uriBill);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", typeBill);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}

	/**
	 * 请求我的店铺数据
	 */
	private void callMyShopData(String type) {
		param.clear();
		param.put("token", token);
		String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_store";
		iGetRequest.putExtra("uri", uri);
		iGetRequest.putExtra("param", param);
		iGetRequest.putExtra("type", type);
		startService(iGetRequest);
		isGetServiceRunning = true;
	}

	/**
	 * 内部广播类
	 */
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
			} else if (MyConstant.HttpPostServiceAciton.equals(intent.getAction())) {
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
	 */
	private void parseDataPost(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("reply".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getString("data");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(mHomeContext, "回复成功");
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("umeng_dtoken".equals(type)) {
			System.out.println("device_token    " + device_token);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getString("data");
			if ("0".equals(error)) {
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		}
	}

	/**
	 * 解析get返回数据
	 */
	private void parseDataGet(String type, String jsonStr) throws JSONException {
		progressDialog.dismiss();
		if ("userDetail".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				UserDetailInfo info = MyJSONParser.parse_userDetail(data);
				tvSex.setText(info.getGender());
				tvUserName.setText(info.getUser_name());
				tvTrueName.setText(info.getReal_name());
				tvBirthday.setText(info.getBirthday());
				tvBind.setText(info.getPhone_activated());

				AsynImageLoader asynImageLoader = new AsynImageLoader();
				asynImageLoader.showImageAsyn(civUserDetailPic, avatar, R.drawable.test9696);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("orderList".equals(type)) {
			tvTipOrderList.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				JSONTokener jasonParser1 = new JSONTokener(data);
				JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
				int length = jsonReturn1.length();
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvOrdersList.getRefreshableView().getAdapter();
				OrderListAdapter osa = (OrderListAdapter) hvla.getWrappedAdapter();
				osa.clear();
				osa.notifyDataSetChanged();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
						String order_id = jsonReturn2.getString("order_id");
						String order_sn = jsonReturn2.getString("order_sn");
						String extension = jsonReturn2.getString("extension");
						String status = jsonReturn2.getString("status");
						String final_amount = jsonReturn2.getString("final_amount");
						String add_time = jsonReturn2.getString("add_time");
						String is_change = jsonReturn2.getString("is_change");

						OrderListInfo info = new OrderListInfo();
						info.setOrder_id(order_id);
						info.setOrder_sn(order_sn);
						info.setExtension(extension);
						info.setStatus(status);
						info.setFinal_amount(final_amount);
						info.setAdd_time(add_time);
						info.setIs_change(is_change);
						osa.addOrderListInfo(info);
					}

					osa.notifyDataSetChanged();
					prlvOrdersList.onRefreshComplete();
				} else {
					osa.notifyDataSetChanged();
					prlvOrdersList.onRefreshComplete();
					tvTipOrderList.setText("未搜索到相关订单");
					tvTipOrderList.setVisibility(View.VISIBLE);
					prlvOrdersList.setVisibility(View.INVISIBLE);
				}
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
				tvTipOrderList.setText("未搜索到相关订单");
				tvTipOrderList.setVisibility(View.VISIBLE);
				prlvOrdersList.setVisibility(View.INVISIBLE);
			}
		} else if ("orderList_load".equals(type)) {
			tvTipOrderList.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				JSONTokener jasonParser1 = new JSONTokener(data);
				JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
				int length = jsonReturn1.length();
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvOrdersList.getRefreshableView().getAdapter();
				OrderListAdapter osa = (OrderListAdapter) hvla.getWrappedAdapter();
				osa.notifyDataSetChanged();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
						String order_id = jsonReturn2.getString("order_id");
						String order_sn = jsonReturn2.getString("order_sn");
						String extension = jsonReturn2.getString("extension");
						String status = jsonReturn2.getString("status");
						String final_amount = jsonReturn2.getString("final_amount");
						String add_time = jsonReturn2.getString("add_time");
						String is_change = jsonReturn2.getString("is_change");

						OrderListInfo info = new OrderListInfo();
						info.setOrder_id(order_id);
						info.setOrder_sn(order_sn);
						info.setExtension(extension);
						info.setStatus(status);
						info.setFinal_amount(final_amount);
						info.setAdd_time(add_time);
						info.setIs_change(is_change);
						osa.addOrderListInfo(info);
					}

					osa.notifyDataSetChanged();
					prlvOrdersList.onRefreshComplete();
				} else {
					osa.notifyDataSetChanged();
					prlvOrdersList.onRefreshComplete();
					tvTipOrderList.setVisibility(View.GONE);
					// prlvOrdersList.setVisibility(View.INVISIBLE);
				}
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
				tvTipOrderList.setVisibility(View.GONE);
				prlvOrdersList.setVisibility(View.INVISIBLE);
			}
		} else if ("orderDetail".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error)) {
				Intent it = new Intent(mHomeContext, OrderDetailActivity.class);
				it.putExtra("data", data);
				it.putExtra("pageCode", "home");
				startActivityForResult(it, MyConstant.RESULTCODE_15);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}

		} else if ("appUpdate".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				JSONTokener jsonParser1 = new JSONTokener(data);
				JSONObject jsonReturn1 = (JSONObject) jsonParser1.nextValue();
				String has_new = jsonReturn1.getString("has_new");
				String must_update = jsonReturn1.getString("must_update");
				final String url = jsonReturn1.getString("url");
				System.out.println(url);

				if ("1".equals(must_update)) {
					downLoadApk(mHomeContext, url);
				} else {
					if ("1".equals(has_new)) {
						AlertDialog.Builder builder = new AlertDialog.Builder(mHomeContext).setTitle("检测到更新。。。")
								.setNegativeButton("取消", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

									}
								}).setPositiveButton("确定", new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										downLoadApk(mHomeContext, url);
									}
								});
						builder.show();

					} else {
						MyUtil.ToastMessage(mHomeContext, "现在已经是最新版本");
					}
				}

			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("needSend".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Intent it = new Intent(mHomeContext, NeedSendOrderActivity.class);
				it.putExtra("data", data);
				it.putExtra("token", token);
				startActivityForResult(it, MyConstant.RESULTCODE_17);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("message".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Intent it = new Intent(mHomeContext, MessageActivity.class);
				it.putExtra("data", data);
				it.putExtra("token", token);
				startActivityForResult(it, MyConstant.RESULTCODE_18);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("allOrder".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Intent it = new Intent(mHomeContext, AllOrdersActivity.class);
				it.putExtra("data", data);
				it.putExtra("token", token);
				startActivityForResult(it, MyConstant.RESULTCODE_21);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("Account".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Intent it = new Intent(mHomeContext, AccountActivity.class);
				it.putExtra("data", data);
				it.putExtra("token", token);
				startActivityForResult(it, MyConstant.RESULTCODE_22);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("Out".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Intent it = new Intent(mHomeContext, OutOfStockActivity.class);
				it.putExtra("data", data);
				it.putExtra("token", token);
				startActivityForResult(it, MyConstant.RESULTCODE_20);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("Pro".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONArray("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				Intent it = new Intent(mHomeContext, ProductConsultationActivity.class);
				it.putExtra("data", data);
				it.putExtra("token", token);
				startActivityForResult(it, MyConstant.RESULTCODE_19);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("goods".equals(type)) {
			tvTipGood.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvGoodsManagerList.getRefreshableView()
						.getAdapter();
				GoodsManagerAdapter gma = (GoodsManagerAdapter) hvla.getWrappedAdapter();
				JSONTokener jsonParser1 = new JSONTokener(data);
				JSONObject jsonReturn1 = (JSONObject) jsonParser1.nextValue();
				jsonGoodList = jsonReturn1.getJSONArray("list");
				int length = jsonGoodList.length();
				gma.clear();
				gma.notifyDataSetChanged();
				if (length > 0) {
					System.out.println("parse   " + jsonGoodList);
					MyJSONParser.parse_goodsList(jsonGoodList, gma, width);
					int count = gma.getCount();
					System.out.println("count   " + count);
					gma.notifyDataSetChanged();
					prlvGoodsManagerList.onRefreshComplete();
				} else {
					tvTipGood.setText("您目前还没有商品，请尽快上传");
					tvTipGood.setVisibility(View.VISIBLE);
					prlvGoodsManagerList.setVisibility(View.INVISIBLE);
				}
			} else {
				tvTipGood.setText("您目前还没有商品，请尽快上传");
				tvTipGood.setVisibility(View.VISIBLE);
				MyUtil.ToastMessage(mHomeContext, msg);
				prlvGoodsManagerList.setVisibility(View.INVISIBLE);
			}
		} else if ("goods_load".equals(type)) {
			tvTipGood.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvGoodsManagerList.getRefreshableView()
						.getAdapter();
				GoodsManagerAdapter gma = (GoodsManagerAdapter) hvla.getWrappedAdapter();
				JSONTokener jsonParser1 = new JSONTokener(data);
				JSONObject jsonReturn1 = (JSONObject) jsonParser1.nextValue();
				jsonGoodList = jsonReturn1.getJSONArray("list");
				int length = jsonGoodList.length();
				gma.notifyDataSetChanged();
				if (length > 0) {
					MyJSONParser.parse_goodsList(jsonGoodList, gma, width);
					gma.notifyDataSetChanged();
					prlvGoodsManagerList.onRefreshComplete();
				} else {
					tvTipGood.setVisibility(View.GONE);
					prlvGoodsManagerList.setVisibility(View.INVISIBLE);
				}
			} else {
				tvTipGood.setVisibility(View.GONE);
				MyUtil.ToastMessage(mHomeContext, msg);
				// prlvGoodsManagerList.setVisibility(View.INVISIBLE);
			}
		} else if ("category".equals(type)) {
			tvTipGoodType.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			JSONArray array = jsonReturn.getJSONArray("data");
			if ("0".equals(error)) {
				int length = array.length();
				if (length > 0) {
					lstGoodsTypeGroups.clear();
					ArrayList<MyTreeElement> tmpList = new ArrayList<MyTreeElement>();
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn1 = array.getJSONObject(i);
						String cate_id = jsonReturn1.getString("cate_id");
						String cate_name = jsonReturn1.getString("cate_name");
						String parent_id = jsonReturn1.getString("parent_id");
						String sort_order = jsonReturn1.getString("sort_order");
						String if_show = jsonReturn1.getString("if_show");

						boolean checked = false;
						if ("1".equals(if_show)) {
							checked = true;
						} else if ("0".equals(if_show)) {
							checked = false;
						}
						MyTreeElement element = new MyTreeElement(cate_id, cate_name, parent_id, sort_order, checked);
						tmpList.add(element);
					}

					for (int i = 0; i < tmpList.size(); i++) {
						MyTreeElement element = tmpList.get(i);
						if ("0".equals(element.getUpId())) {
							lstGoodsTypeGroups.add(element);
						}

					}

					if (lstGoodsTypeGroups.size() > 0) {
						for (int i = 0; i < lstGoodsTypeGroups.size(); i++) {
							MyTreeElement element = lstGoodsTypeGroups.get(i);
							turn2Tree(element, tmpList);
						}
					}
					HeaderViewListAdapter hvla = (HeaderViewListAdapter) lvGoodsTypeManagerList.getAdapter();
					MyTreeViewAdapter adapter = (MyTreeViewAdapter) hvla.getWrappedAdapter();
					adapter.notifyDataSetChanged();
					lvGoodsTypeManagerList.onRefreshComplete();
				} else {
					tvTipGoodType.setText("您目前还没有商品分类，请尽快上传");
					tvTipGoodType.setVisibility(View.VISIBLE);
				}
			} else {
				tvTipGoodType.setText("您目前还没有商品分类，请尽快上传");
				tvTipGoodType.setVisibility(View.VISIBLE);
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("bill".equals(type)) {
			tvTipBill.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			JSONArray jsonBill = jsonReturn.getJSONArray("data");
			if ("0".equals(error) && !"".equals(jsonBill.toString())) {
				int length = jsonBill.length();
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvBillList.getRefreshableView().getAdapter();
				BillAdapter ba = (BillAdapter) hvla.getWrappedAdapter();
				ba.clear();
				ba.notifyDataSetChanged();
				if (length > 0) {
					tvTipBill.setVisibility(View.GONE);
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonBill.getJSONObject(i);
						BillInfo info = new BillInfo();
						String sta_id = jsonReturn2.getString("sta_id");
						String sta_sn = jsonReturn2.getString("sta_sn");
						String sta_status = jsonReturn2.getString("sta_status");
						String sta_plat = jsonReturn2.getString("sta_plat");
						String total_order_pay = jsonReturn2.getString("total_order_pay");
						String add_time = jsonReturn2.getString("add_time");
						String confirm_time = jsonReturn2.getString("confirm_time");

						info.setSta_id(sta_id);
						info.setSta_sn(sta_sn);
						info.setSta_status(sta_status);
						info.setSta_plat(sta_plat);
						info.setTotal_order_pay(total_order_pay);
						info.setAdd_time(add_time);
						info.setConfirm_time(confirm_time);

						ba.addBillInfo(info);
					}
					ba.notifyDataSetChanged();
					prlvBillList.onRefreshComplete();
				} else {
					tvTipBill.setText("您目前没有对账单信息");
					tvTipBill.setVisibility(View.VISIBLE);
					prlvBillList.setVisibility(View.INVISIBLE);
				}
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
				tvTipBill.setText("您目前没有对账单信息");
				tvTipBill.setVisibility(View.VISIBLE);
				prlvBillList.setVisibility(View.INVISIBLE);
			}
		} else if ("bill_load".equals(type)) {
			tvTipBill.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			JSONArray jsonBill = jsonReturn.getJSONArray("data");
			if ("0".equals(error) && !"".equals(jsonBill.toString())) {
				int length = jsonBill.length();
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvBillList.getRefreshableView().getAdapter();
				BillAdapter ba = (BillAdapter) hvla.getWrappedAdapter();
				ba.notifyDataSetChanged();
				if (length > 0) {
					tvTipBill.setVisibility(View.GONE);
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonBill.getJSONObject(i);
						BillInfo info = new BillInfo();
						String sta_id = jsonReturn2.getString("sta_id");
						String sta_sn = jsonReturn2.getString("sta_sn");
						String sta_status = jsonReturn2.getString("sta_status");
						String sta_plat = jsonReturn2.getString("sta_plat");
						String total_order_pay = jsonReturn2.getString("total_order_pay");
						String add_time = jsonReturn2.getString("add_time");
						String confirm_time = jsonReturn2.getString("confirm_time");

						info.setSta_id(sta_id);
						info.setSta_sn(sta_sn);
						info.setSta_status(sta_status);
						info.setSta_plat(sta_plat);
						info.setTotal_order_pay(total_order_pay);
						info.setAdd_time(add_time);
						info.setConfirm_time(confirm_time);

						ba.addBillInfo(info);
					}
					ba.notifyDataSetChanged();
					prlvBillList.onRefreshComplete();
				} else {
					tvTipBill.setVisibility(View.GONE);
					prlvBillList.setVisibility(View.INVISIBLE);
				}
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
				tvTipBill.setVisibility(View.GONE);
				// prlvBillList.setVisibility(View.INVISIBLE);
			}
		} else if ("billhead_list".equals(type)) {
			tvTipBillhead.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			JSONArray jsonBillhead = jsonReturn.getJSONArray("data");
			if ("0".equals(error) && !"".equals(data)) {
				lstBillHeadPageSource.clear();
				int length = jsonBillhead.length();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonBillhead.getJSONObject(i);
						String bil_sn = jsonReturn2.getString("bil_sn");
						String pay_time = jsonReturn2.getString("pay_time");
						String add_time = jsonReturn2.getString("add_time");
						String mall_account_name = jsonReturn2.getString("mall_account_name");
						String mall_account_holder = jsonReturn2.getString("mall_account_holder");
						String mall_account_num = jsonReturn2.getString("mall_account_num");
						String total_cost_ext = jsonReturn2.getString("total_cost_ext");
						String store_bank_name = jsonReturn2.getString("store_bank_name");
						String store_account_holder = jsonReturn2.getString("store_account_holder");
						String store_bank_account = jsonReturn2.getString("store_bank_account");
						String order_pay = jsonReturn2.getString("order_pay");
						String poundage = jsonReturn2.getString("poundage");
						String total_cost = jsonReturn2.getString("total_cost");
						String remark = jsonReturn2.getString("remark");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("tv_billHeadItem_billHeadNo", bil_sn);
						map.put("tv_billHeadItem_price", "￥" + total_cost);
						map.put("tv_billHeadItem_date", MyUtil.millisecondsToStr(add_time));
						map.put("mall_account_name", mall_account_name);
						map.put("mall_account_holder", mall_account_holder);
						map.put("mall_account_num", mall_account_num);
						map.put("total_cost_ext", total_cost_ext);
						map.put("store_bank_name", store_bank_name);
						map.put("store_account_holder", store_account_holder);
						map.put("store_bank_account", store_bank_account);
						map.put("order_pay", "￥" + order_pay);
						map.put("poundage", "￥" + poundage);
						map.put("pay_time", pay_time);
						map.put("remark", remark);
						lstBillHeadPageSource.add(map);
					}
					HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvBillHeadList.getRefreshableView()
							.getAdapter();
					SimpleAdapter sa = (SimpleAdapter) hvla.getWrappedAdapter();
					sa.notifyDataSetChanged();
					prlvBillHeadList.onRefreshComplete();
				} else {
					tvTipBillhead.setText("您目前没有付款通知单");
					tvTipBillhead.setVisibility(View.VISIBLE);
					prlvBillHeadList.setVisibility(View.INVISIBLE);
				}
			} else {
				tvTipBillhead.setText("您目前没有付款通知单");
				tvTipBillhead.setVisibility(View.VISIBLE);
				prlvBillHeadList.setVisibility(View.INVISIBLE);
			}
		} else if ("billhead_list_load".equals(type)) {
			tvTipBillhead.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			JSONArray jsonBillhead = jsonReturn.getJSONArray("data");
			if ("0".equals(error) && !"".equals(data)) {
				int length = jsonBillhead.length();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonBillhead.getJSONObject(i);
						String bil_sn = jsonReturn2.getString("bil_sn");
						String pay_time = jsonReturn2.getString("pay_time");
						String add_time = jsonReturn2.getString("add_time");
						String mall_account_name = jsonReturn2.getString("mall_account_name");
						String mall_account_holder = jsonReturn2.getString("mall_account_holder");
						String mall_account_num = jsonReturn2.getString("mall_account_num");
						String total_cost_ext = jsonReturn2.getString("total_cost_ext");
						String store_bank_name = jsonReturn2.getString("store_bank_name");
						String store_account_holder = jsonReturn2.getString("store_account_holder");
						String store_bank_account = jsonReturn2.getString("store_bank_account");
						String order_pay = jsonReturn2.getString("order_pay");
						String poundage = jsonReturn2.getString("poundage");
						String total_cost = jsonReturn2.getString("total_cost");
						String remark = jsonReturn2.getString("remark");

						HashMap<String, String> map = new HashMap<String, String>();
						map.put("tv_billHeadItem_billHeadNo", bil_sn);
						map.put("tv_billHeadItem_price", "￥" + total_cost);
						map.put("tv_billHeadItem_date", MyUtil.millisecondsToStr(add_time));
						map.put("mall_account_name", mall_account_name);
						map.put("mall_account_holder", mall_account_holder);
						map.put("mall_account_num", mall_account_num);
						map.put("total_cost_ext", total_cost_ext);
						map.put("store_bank_name", store_bank_name);
						map.put("store_account_holder", store_account_holder);
						map.put("store_bank_account", store_bank_account);
						map.put("order_pay", "￥" + order_pay);
						map.put("poundage", "￥" + poundage);
						map.put("pay_time", pay_time);
						map.put("remark", remark);
						lstBillHeadPageSource.add(map);
					}
					HeaderViewListAdapter hvla = (HeaderViewListAdapter) prlvBillHeadList.getRefreshableView()
							.getAdapter();
					SimpleAdapter sa = (SimpleAdapter) hvla.getWrappedAdapter();
					sa.notifyDataSetChanged();
					prlvBillHeadList.onRefreshComplete();
				} else {
					tvTipBillhead.setVisibility(View.GONE);
					// prlvBillHeadList.setVisibility(View.INVISIBLE);
				}
			} else {
				tvTipBillhead.setVisibility(View.GONE);
				prlvBillHeadList.setVisibility(View.INVISIBLE);
			}
		} else if ("homePage".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			data = jsonReturn.getJSONObject("data").toString();
			if ("0".equals(error) && !"".equals(data)) {
				HomePageDataInfo info = MyJSONParser.parse_homePageData(data);
				String order_count = info.getOrder_count();
				String newpm = info.getNewpm();
				String qa_count = info.getQa_count();
				String notice_count = info.getNotice_count();

				// String count[] = new String[] { order_count, newpm, qa_count,
				// notice_count, "0", "0" };
				String count[] = new String[] { order_count, newpm, qa_count, "0" }; // TODO
																						// 演示版暂时不显示
				for (int i = 0; i < count.length; i++) {
					HashMap<String, Object> map = lstHomePageSource.get(i);
					map.put("tv_itemNo", count[i]);
					if (count[i].equals("0")) {
						map.put("isShow", false);
					} else {
						map.put("isShow", true);
					}
				}
				MyGridViewAdapter adapter = (MyGridViewAdapter) mgvMenu.getAdapter();
				adapter.notifyDataSetChanged();

				tvHomeLevel.setText(info.getGrade());
				tvHomeService.setText(info.getService_score());
				tvHomeCount.setText(info.getGoods_count());
				float avg = Float.parseFloat(info.getAvg_line());
				if (avg > 0) {
					tvHomeOther.setText("↑ " + info.getAvg_line() + "%");
				} else if (avg < 0) {
					String str = info.getAvg_line();

					tvHomeOther.setText("↓ " + str.substring(1, str.length()) + "%");
				} else {
					tvHomeOther.setText(info.getAvg_line() + "%");
				}
				// tvPoint.setText(info.getCredits());
				// tvExperience.setText(info.getExp());

			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("qa".equals(type)) {
			tvTipManager.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			JSONArray jsonQA = jsonReturn.getJSONArray("data");
			if ("0".equals(error)) {
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) lvConsultationManagerList.getAdapter();
				ConsultationAdapter ca = (ConsultationAdapter) hvla.getWrappedAdapter();
				ca.clear();
				ca.notifyDataSetChanged();
				int length = jsonQA.length();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonQA.getJSONObject(i);
						String ques_id = jsonReturn2.getString("ques_id");
						String question_content = jsonReturn2.getString("question_content");
						String user_id = jsonReturn2.getString("user_id");
						String user_name = MyUtil.dealNullString(jsonReturn2.getString("user_name"));
						if ("无".equals(user_name)) {
							user_name = "游客";
						}
						String item_id = jsonReturn2.getString("item_id");
						String item_name = jsonReturn2.getString("item_name");
						String reply_content = jsonReturn2.getString("reply_content");
						String time_post = jsonReturn2.getString("time_post");
						String time_reply = jsonReturn2.getString("time_reply");

						ChatInfo chat = new ChatInfo();
						chat.setQues_id(ques_id);
						chat.setQuestion_content(question_content);
						chat.setUser_id(user_id);
						chat.setUser_name(user_name);
						chat.setItem_id(item_id);
						chat.setItem_name(item_name);
						chat.setReply_content(reply_content);
						chat.setTime_post(MyUtil.millisecondsToStr(time_post));
						chat.setTime_reply(MyUtil.millisecondsToStr(time_reply));
						ca.addChatInfo(chat);
					}
					ca.notifyDataSetChanged();
					lvConsultationManagerList.onRefreshComplete();
				} else {
					tvTipManager.setText("您目前没有相关咨询信息");
					tvTipManager.setVisibility(View.VISIBLE);
				}
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("waitcallqa".equals(type)) {
			tvTipWait.setVisibility(View.GONE);
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			JSONArray jsonQA = jsonReturn.getJSONArray("data");
			if ("0".equals(error)) {
				HeaderViewListAdapter hvla = (HeaderViewListAdapter) lvWaitCallConsultationList.getAdapter();
				ConsultationAdapter ca = (ConsultationAdapter) hvla.getWrappedAdapter();
				ca.clear();
				ca.notifyDataSetChanged();
				int length = jsonQA.length();
				if (length > 0) {
					for (int i = 0; i < length; i++) {
						JSONObject jsonReturn2 = jsonQA.getJSONObject(i);
						String ques_id = jsonReturn2.getString("ques_id");
						String question_content = jsonReturn2.getString("question_content");
						String user_id = jsonReturn2.getString("user_id");
						String user_name = MyUtil.dealNullString(jsonReturn2.getString("user_name"));
						if ("无".equals(user_name)) {
							user_name = "游客";
						}
						String item_id = jsonReturn2.getString("item_id");
						String item_name = jsonReturn2.getString("item_name");
						String reply_content = jsonReturn2.getString("reply_content");
						String time_post = jsonReturn2.getString("time_post");
						String time_reply = jsonReturn2.getString("time_reply");

						ChatInfo chat = new ChatInfo();
						chat.setQues_id(ques_id);
						chat.setQuestion_content(question_content);
						chat.setUser_id(user_id);
						chat.setUser_name(user_name);
						chat.setItem_id(item_id);
						chat.setItem_name(item_name);
						chat.setReply_content(reply_content);
						chat.setTime_post(MyUtil.millisecondsToStr(time_post));
						chat.setTime_reply(MyUtil.millisecondsToStr(time_reply));
						ca.addChatInfo(chat);
					}
					ca.notifyDataSetChanged();
					lvWaitCallConsultationList.onRefreshComplete();
				} else {
					tvTipWait.setText("您目前没有待回复咨询信息");
					tvTipWait.setVisibility(View.VISIBLE);
				}

			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("edit_col".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			error = jsonReturn.getString("error");
			msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				treeAdapter.notifyDataSetChanged();
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("view".equals(type)) {
			Intent intent = new Intent(mHomeContext, BillDetailActivity.class);
			intent.putExtra("url", jsonStr);
			startActivity(intent);
		} else if ("del".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(mHomeContext, "删除成功");
				param.put("token", token);
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=qa";
				progressDialog.show();
				String type_ = "qa";
				iGetRequest.putExtra("uri", uri);
				iGetRequest.putExtra("param", param);
				iGetRequest.putExtra("type", type_);
				startService(iGetRequest);
				isGetServiceRunning = true;
				param.clear();
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("myShop_home".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			String data = jsonReturn.getString("data");
			if ("0".equals(error)) {
				Intent it = new Intent(mHomeContext, MyShopActivity.class);
				it.putExtra("token", token);
				it.putExtra("data", data);
				startActivityForResult(it, MyConstant.RESULTCODE_36);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		} else if ("myShop_home1".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			String msg = jsonReturn.getString("msg");
			if ("0".equals(error)) {
				String data = jsonReturn.getString("data");
				JSONTokener jsonParser1 = new JSONTokener(data);
				JSONObject jsonReturn1 = (JSONObject) jsonParser1.nextValue();
				String store_logo = jsonReturn1.getString("store_logo");
				String store_name = jsonReturn1.getString("store_name");

				AsynImageLoader asynImageLoader = new AsynImageLoader();
				asynImageLoader.showImageAsyn(ivPicture, store_logo, R.drawable.test140140);
				llRight.setVisibility(View.INVISIBLE);
				tvUser.setText(store_name);
			} else {
				MyUtil.ToastMessage(mHomeContext, msg);
			}
		}
	}

	/**
	 * 从服务器中下载APK
	 */
	private void downLoadApk(Context context, final String url) {
		final ProgressDialog pd; // 进度条对话框
		pd = new ProgressDialog(context);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMessage("正在下载更新，请稍候。。。");
		pd.show();
		new Thread() {
			@Override
			public void run() {
				try {
					File file = MyUtil.getFileFromServer(url, pd);
					sleep(3000);
					installApk(file);
					pd.dismiss(); // 结束掉进度条对话框
				} catch (Exception e) {
					e.printStackTrace();
					// MyUtil.ToastMessage(mHomeContext, "更新失败");
					pd.dismiss();
				}
			}
		}.start();
	}

	/**
	 * 安装apk
	 */
	private void installApk(File file) {
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 执行的数据类型
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * 请求首页数据
	 */
	private void callHomePageData() {
		param.clear();
		param.put("token", token);
		String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller";
		String type = "homePage";
		callHttpGet(uri, type);
	}

}
