package com.spt.page;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.adapter.HomePagerAdapter;
import com.spt.sht.R;
import com.spt.utils.ImageHandler;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.MyUtil;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 首页
 */

public class HomeActivity extends BaseActivity {

	private TextView tv_home_dis, tv_home_meta, tv_home_manage;
	private ImageView iv_home_menu;
	private RelativeLayout rl_home_manage, rl_home_dis, rl_home_meta;
	private LinearLayout ll_home_store, ll_home_dis, ll_home_meta, ll_Point;
	public ViewPager vp_home;
	private List<ImageView> imgList;// 轮播图片集合
	private int[] imgs = new int[] { R.drawable.lunbo_1, R.drawable.lunbo_2, R.drawable.lunbo_3 };
	private int prePosition;// 轮播三个点前一个位置的标记
	// 轮播handler
	public ImageHandler handler ;
	private ProgressDialog progressdialog;
	private SharedPreferences spHome;
	private HashMap<String, String> params;// 参数集合
	private Editor editor;
	private String token;// 必须传的参数
	private String userName;// 用户名
	private String mtsUserName;// 源一云商用户名
	private String mtsPas;// 源一云商密码
	private String dis;// 分销状态判断码
	private String store;// 商户状态判断码
	private String reject_reason;// 审核拒绝原因
	private long exitTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.activity_home);
		super.onCreate(savedInstanceState);
		
		if (MtsUrls.base.equals("https://www.metasolo.cn/")) {
			params.clear();
			params.put("username", "test_retail");
			params.put("password", "metasolo");
			params.put("mappingType", "Admin");
			params.put("sevenPlusTwoValue", userName);
		} else {
			params.clear();
			params.put("username", "qje");
			params.put("password", "123456");
			params.put("mappingType", "Admin");
			params.put("sevenPlusTwoValue", userName);
		}

		String uri1 = MtsUrls.base + MtsUrls.get_metasolo;
		VolleyHelper.post(uri1, params, new OnCallBack() {

			@Override
			public void OnSuccess(String data) {
				progressdialog.dismiss();
				try {
					JSONObject obj = new JSONObject(data);
					String error_msg = obj.optString("_ERROR_MESSAGE_");
					if (error_msg.equals("") || error_msg == null) {
						String metasoloValue = obj.optString("metasoloValue");
						String str[] = metasoloValue.split("&");
						mtsUserName = str[0];
						mtsPas = str[1];
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void OnError(VolleyError volleyError) {
				progressdialog.dismiss();
				Toast.makeText(HomeActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
			}
		});

		initData();

	}

	@Override
	protected void onStart() {
		progressdialog.show();
		params.clear();
		params.put("token", token);
		String uri = MyConstant.SERVICENAME + MyConstant.CHOSEPLATFORM;
		VolleyHelper.post(uri, params, new OnCallBack() {

			@Override
			public void OnSuccess(String data) {
				progressdialog.dismiss();
				try {
					JSONObject object = new JSONObject(data);
					String error = object.optString("error");
					if ("0".equals(error)) {
						JSONObject obj = object.optJSONObject("data");
						store = obj.optString("store");
						dis = obj.optString("dis");
						reject_reason = obj.optString("reject_reason");

						if (store.equals("3")) {
							tv_home_manage.setText("未申请");
						} else if (store.equals("0")) {
							tv_home_manage.setText("申请中");
						} else if (store.equals("1")) {
							tv_home_manage.setText("七加二店铺管理后台");
						} else if (store.equals("2")) {
							tv_home_manage.setText("关闭");
						}

						if (dis.equals("3")) {
							tv_home_dis.setText("未申请");
						} else if (dis.equals("0")) {
							tv_home_dis.setText("未审核");
						} else if (dis.equals("1")) {
							tv_home_dis.setText("零售商订购平台");
						} else if (dis.equals("2")) {
							tv_home_dis.setText("审核拒绝");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void OnError(VolleyError volleyError) {
				progressdialog.dismiss();
				Toast.makeText(HomeActivity.this, "网络不好,请检查网络", Toast.LENGTH_SHORT).show();
			}
		});
		
		super.onStart();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 存图片的集合
		imgList = new ArrayList<ImageView>();
		for (int i = 0; i < imgs.length; i++) {
			ImageView image = new ImageView(HomeActivity.this);
			image.setBackgroundResource(imgs[i]);
			imgList.add(image);

			// 三个点
			View point = new View(HomeActivity.this);
			point.setBackgroundResource(R.drawable.dot_not_focus);
			LayoutParams params = new LayoutParams(15, 15);
			params.leftMargin = 10;
			point.setLayoutParams(params);
			ll_Point.addView(point);
		}
		// 设置第一个点为默认点
		ll_Point.getChildAt(0).setBackgroundResource(R.drawable.dot_focus);
		HomePagerAdapter adapter = new HomePagerAdapter(imgList);
		vp_home.setAdapter(adapter);
		vp_home.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 当滑动到下一张图片，修改之前图片的点
				ll_Point.getChildAt(prePosition).setBackgroundResource(R.drawable.dot_not_focus);
				// 滑动到当前图片，修改当前图片的点
				ll_Point.getChildAt(position % imgList.size()).setBackgroundResource(R.drawable.dot_focus);
				// 这一次的当前位置为下一次当前位置的前一个选中条目
				prePosition = position % imgList.size();
				handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, position, 0));
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				switch (arg0) {
				case ViewPager.SCROLL_STATE_DRAGGING:
					handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
					break;
				case ViewPager.SCROLL_STATE_IDLE:
					handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
					break;
				default:
					break;
				}
			}
		});

		handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);

	}

	@Override
	protected void init() {
		// 初始化控件
		tv_home_dis = (TextView) findViewById(R.id.tv_home_dis);
		tv_home_manage = (TextView) findViewById(R.id.tv_home_manage);
		tv_home_meta = (TextView) findViewById(R.id.tv_home_meta);
		iv_home_menu = (ImageView) findViewById(R.id.iv_home_menu);
		rl_home_manage = (RelativeLayout) findViewById(R.id.rl_home_manage);
		rl_home_dis = (RelativeLayout) findViewById(R.id.rl_home_dis);
		rl_home_meta = (RelativeLayout) findViewById(R.id.rl_home_meta);
		ll_home_dis = (LinearLayout) findViewById(R.id.ll_home_dis);
		ll_home_meta = (LinearLayout) findViewById(R.id.ll_home_meta);
		ll_home_store = (LinearLayout) findViewById(R.id.ll_home_store);
		ll_Point = (LinearLayout) findViewById(R.id.ll_Point);
		vp_home = (ViewPager) findViewById(R.id.vp_home);
		spHome = this.getSharedPreferences("USERINFO", MODE_PRIVATE);
		progressdialog = ProgressDialog.show(this, "请稍等...", "数据获取中...", true);
		token = spHome.getString("token", "");
		userName = spHome.getString("userName", "");
		editor = spHome.edit();
		params = new HashMap<String, String>(); // 调用接口参数
	}

	@Override
	protected void addClickEvent() {
		// 首页设置按钮
		iv_home_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				showPopupWindow(v);

			}
		});

		/**
		 * 商城后台状态判断
		 */
		rl_home_manage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tv_home_manage.getText().toString().equals("未申请")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setMessage("您未申请商户通商户，具体细节请咨询七加二客服中心");
					builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});

					builder.show();
				} else if (tv_home_manage.getText().toString().equals("申请中")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setMessage("您的申请正在审核，请稍后重试");
					builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});

					builder.show();
				} else if (tv_home_manage.getText().toString().equals("关闭")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setMessage("您的店铺已被关闭，详情请咨询七加二客服中心");
					builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});

					builder.show();
				} else if (tv_home_manage.getText().toString().equals("七加二店铺管理后台")) {
					params.clear();
					params.put("token", token);
					String uri = MyConstant.SERVICENAME + MyConstant.SHOPMSG;
					VolleyHelper.post(uri, params, new OnCallBack() {

						@Override
						public void OnSuccess(String data) {
							progressdialog.dismiss();
							try {
								JSONObject obj = new JSONObject(data);
								String error = obj.optString("error");
								if (error.equals("0")) {
									JSONObject object = obj.optJSONObject("data");
									String shopName = object.optString("store_name");
									String avatar = object.optString("store_logo");
									editor.putString("user_name", shopName);
									editor.putString("avatar", avatar);
									editor.commit();

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}

						@Override
						public void OnError(VolleyError volleyError) {
							progressdialog.dismiss();
							Toast.makeText(HomeActivity.this, "网络不好，请检查网络", Toast.LENGTH_SHORT).show();
						}
					});

					Intent homepage = new Intent(HomeActivity.this, HomePageActivity.class);
					startActivity(homepage);

				}
			}
		});

		/**
		 * 分销后台状态判断
		 */
		rl_home_dis.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tv_home_dis.getText().toString().equals("未审核")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setMessage("您的申请正在审核，请稍后重试");
					builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					builder.show();
				} else if (tv_home_dis.getText().toString().equals("审核拒绝")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setMessage("您的分销申请已被拒绝" + "/n拒绝理由：" + reject_reason);
					builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
					builder.show();
				} else if (tv_home_dis.getText().toString().equals("未申请")) {
					Intent apply = new Intent(HomeActivity.this, ApplyDisActivity.class);
					startActivity(apply);
				} else if (tv_home_dis.getText().toString().equals("零售商订购平台")) {
					Intent intent = new Intent(HomeActivity.this, DistributionActivity.class);
					startActivity(intent);
				}
			}
		});

		/**
		 * metasolo模块
		 */
		rl_home_meta.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(HomeActivity.this, MtsLogin.class);
				intent.putExtra("userName", mtsUserName);
				intent.putExtra("pas", mtsPas);
				startActivity(intent);

			}
		});
	}

	@SuppressLint("InflateParams")
	private void showPopupWindow(View view) {
		// 自定义一个布局来展示内容
		View menu_view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.pop_menu, null);
		TextView tv_pop_changepas = (TextView) menu_view.findViewById(R.id.tv_pop_changepas);
		TextView tv_pop_contact = (TextView) menu_view.findViewById(R.id.tv_pop_contact);
		TextView tv_pop_exit = (TextView) menu_view.findViewById(R.id.tv_pop_exit);
		final PopupWindow menu_pop = new PopupWindow(menu_view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 点击外边可让popupwindow消失
		menu_pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_select));
		menu_pop.setOutsideTouchable(true);
		// 获取焦点，否则无法点击
		menu_pop.setFocusable(true);
		// 设置popupwindow显示位置
		menu_pop.showAsDropDown(view);

		// 修改密码
		tv_pop_changepas.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent changepas = new Intent(HomeActivity.this, ChangePassWordActivity.class);
				changepas.putExtra("token", token);
				changepas.putExtra("account", userName);
				startActivity(changepas);
				menu_pop.dismiss();
			}
		});

		// 联系客服
		tv_pop_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("tel:" + "4009000702");
				Intent contact = new Intent();
				// it.setAction(Intent.ACTION_CALL);//直接拨打电话
				contact.setAction(Intent.ACTION_DIAL);// 调用软件盘方式拨打电话
				contact.setData(uri);
				startActivity(contact);
				menu_pop.dismiss();
			}
		});

		// 退出登录
		tv_pop_exit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				HomeActivity.this.finish();
				menu_pop.dismiss();
			}
		});
	}

	// 屏蔽back键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			progressdialog.dismiss();
			// 判断间隔时间 大于2秒就退出应用
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				// 提示消息
				String msg = "再按一次返回桌面";
				MyUtil.ToastMessage(HomeActivity.this, msg);
				// 计算两次返回键按下的时间差
				exitTime = System.currentTimeMillis();
			} else {
				// 返回桌面操作
				Intent home = new Intent(Intent.ACTION_MAIN);
				home.addCategory(Intent.CATEGORY_HOME);
				startActivity(home);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
