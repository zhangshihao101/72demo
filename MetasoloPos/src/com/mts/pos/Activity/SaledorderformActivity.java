package com.mts.pos.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.laiqian.print.model.BasePrinterDiscoverySession;
import com.laiqian.print.model.IPrinterDiscoverySession;
import com.laiqian.print.model.IPrinterDiscoverySession.PrinterDiscoveryObserver;
import com.laiqian.print.model.PrintContent;
import com.laiqian.print.model.PrintJobInfo;
import com.laiqian.print.model.PrintJobInfo.StatusObserver;
import com.laiqian.print.model.PrintManager;
import com.laiqian.print.model.PrintManager.PrinterConnectionResultObserver;
import com.laiqian.print.model.PrinterInfo;
import com.laiqian.print.model.type.bluetooth.BluetoothPrintManager;
import com.laiqian.print.model.type.bluetooth.BluetoothPrinterDiscoverySession;
import com.laiqian.print.model.type.serial.SerialPrinterInfo;
import com.laiqian.print.model.type.usb.UsbPrintManager;
import com.mts.pos.R;
import com.mts.pos.Common.BaseLeftMenuActivity;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.MyDate;
import com.mts.pos.Common.SomeMethod;
import com.mts.pos.Common.Urls;
import com.mts.pos.Printer.FixedSpacePrintContentBuilder;
import com.mts.pos.Printer.IFormatedPrintContentBuilder;
import com.mts.pos.listview.LeftMenuInfo;
import com.mts.pos.listview.SaledAdapter;
import com.mts.pos.listview.SaledOrderProductInfo;
import com.mts.pos.listview.SaledorderInfo;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public class SaledorderformActivity extends BaseLeftMenuActivity {

	private Button btn_saled_menu, btn_order_scan;
	private EditText et_order_search;
	private ImageView iv_order_close;
	private ListView lv_saled_order;
	private SaledAdapter saledAdapter;
	public static List<SaledorderInfo> saledorderData = null;
	public static SaledorderInfo saledorderInfo = null;

	private SaledOrderProductInfo saledorderproductInfo = null;
	private List<SaledOrderProductInfo> saledorderproductData = null;

	private int showFlag = 0;// 搜索类型
	private int page = 0;// 页数
	private int totalPage;// 总页数
	private boolean isBottom;// 判断是否到达底部
	private String stamp = "", stamp1 = "", stamp2 = "", createdBy = "", cashSum = "", alipaySum = "", wechatSum = "",
			visaSum = "", giftSum = "", paySum = "", changeSum = "";
	private final static int SCANNIN_GREQUEST_CODE = 10;

	private String orderId = "", giftNo = "", orderDetailId = "";
	private Double orderSum = 0.00, cashvalue = 0.00, giftvalue = 0.00, wechatvalue = 0.00, alipayvalue = 0.00,
			visavalue = 0.00, paidvalue = 0.00, changevalue = 0.00;

	// 打印相关
	IPrinterDiscoverySession usbSession;// USB相关对象
	IPrinterDiscoverySession bluetoothSession; // 蓝牙相关对象
	IPrinterDiscoverySession serialSession;
	PrintManager printManager;
	// PrinterInfo printerInfo = null;
	PrintContent testContent = null;
	Timer printTimer = null;
	private ArrayList<PrinterInfo> printers = new ArrayList<PrinterInfo>();
	private Boolean cash = false, gift = false, wechat = false, alipay = false, visa = false;
	NumberFormat nf;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
		mMenuDrawer.setContentView(R.layout.activity_saledorderform);
		super.onCreate(inState);

		btn_saled_menu = (Button) findViewById(R.id.btn_saled_menu);
		btn_order_scan = (Button) findViewById(R.id.btn_order_scan);
		et_order_search = (EditText) findViewById(R.id.et_order_search);
		iv_order_close = (ImageView) findViewById(R.id.iv_order_close);
		lv_saled_order = (ListView) findViewById(R.id.lv_saled_order);

		nf = new DecimalFormat("###.####");

		saledorderData = new ArrayList<SaledorderInfo>();
		saledAdapter = new SaledAdapter(SaledorderformActivity.this, saledorderData);
		lv_saled_order.setAdapter(saledAdapter);

		getAllOrder();

		copyUsbDevicePropertyFile();

		printManager = PrintManager.getInstance(this);

		printManager.setPrinterConnectionResultObserver(new PrinterConnectionResultObserver() {
			@Override
			public void onResult(String identifier, boolean result) {
				printers.get(findi()).setConnected(result);
			}
		});

		et_order_search.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				saledorderData.clear();
				saledAdapter.notifyDataSetChanged();
				searchOrder();

				showFlag = 1;
				page = 1;

				return true;
			}
		});

		iv_order_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_order_search.setText("");
				saledorderData.clear();
				saledAdapter.notifyDataSetChanged();
				getAllOrder();
			}
		});

		lv_saled_order.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == SCROLL_STATE_IDLE && isBottom && page <= totalPage) {
					page++;
					if (showFlag == 0) {
						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
						nameValuePair.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(SaledorderformActivity.this, "externalloginkey")));
						nameValuePair.add(new BasicNameValuePair("productStoreId",
								Localxml.search(SaledorderformActivity.this, "storeid")));
						nameValuePair.add(new BasicNameValuePair("channel", "POS_SALES_CHANNEL"));
						nameValuePair.add(new BasicNameValuePair("VIEW_INDEX", page + ""));
						nameValuePair.add(new BasicNameValuePair("VIEW_SIZE", "10"));

						getTask(SaledorderformActivity.this, Urls.base + Urls.get_orderlist, nameValuePair, "0");
					} else if (showFlag == 1) {
						List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
						nameValuePair.add(new BasicNameValuePair("externalLoginKey",
								Localxml.search(SaledorderformActivity.this, "externalloginkey")));
						nameValuePair.add(new BasicNameValuePair("productStoreId",
								Localxml.search(SaledorderformActivity.this, "storeid")));
						nameValuePair.add(new BasicNameValuePair("channel", "POS_SALES_CHANNEL"));
						nameValuePair.add(new BasicNameValuePair("keyword", et_order_search.getText().toString()));
						nameValuePair.add(new BasicNameValuePair("VIEW_INDEX", page + ""));
						nameValuePair.add(new BasicNameValuePair("VIEW_SIZE", "10"));

						getTask(SaledorderformActivity.this, Urls.base + Urls.get_orderlist, nameValuePair, "0");
					}
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
					isBottom = true;
				} else {
					isBottom = false;
				}
			}
		});

		lv_saled_order.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(SaledorderformActivity.this, SaledorderDetailActivity.class);
				intent.putExtra("orderId", saledorderData.get(position).getOrderId());
				startActivity(intent);

			}
		});

		btn_saled_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMenuDrawer.openMenu();
			}
		});

		btn_order_scan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(SaledorderformActivity.this, MipcaActivityCapture.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
			}
		});

		saledAdapter.setListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				saledorderproductInfo = new SaledOrderProductInfo();
				saledorderproductData = new ArrayList<SaledOrderProductInfo>();

				int i = (Integer) v.getTag();
				orderDetailId = saledorderData.get(i).getOrderId();
				getOrderDetail();

				saledorderproductData.clear();

			}
		});
	}

	/**
	 * @author Raid_Workstation actually you cannot search serial printers
	 */
	private static class SerialPrinterDiscoverySession extends BasePrinterDiscoverySession {

		@Override
		public void start() {
			SerialPrinterInfo defaultPrinter = new SerialPrinterInfo("/dev/ttyS1", 9600);

			onFoundPrinter(defaultPrinter);
		}

		@Override
		public void cancel() {

		}

	}

	@SuppressLint("HandlerLeak")
	final Handler printHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 23) {

				if (isSearching()) {
					cancelPrinterSearch();
				} else {
					startPrinterSearch();
				}
				if (printers.size() == 0) {
					Log.e("LOOK", "搜索");
					// Toast.makeText(TopActivity.this, "false",
					// Toast.LENGTH_SHORT).show();

				} else {
					// Toast.makeText(AccountingActivity.this, "打印机已就绪",
					// Toast.LENGTH_SHORT).show();
					printTimer.cancel();
					print();
				}
			}
		}
	};

	/**
	 * 打印订单内容
	 */
	public void print() {
		PrinterInfo printer = printers.get(0);
		PrintJobInfo job = new PrintJobInfo(printer, getTestContent());
		job.setStatusObserver(new StatusObserver() {

			@Override
			public void onStatus(final PrintJobInfo job, int newStatus) {
				if (job.isEnded()) {
					String msg = job.getStatusName();
					final String finalMsg = msg;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(SaledorderformActivity.this, finalMsg, Toast.LENGTH_SHORT).show();

							Intent intent = getIntent();
							overridePendingTransition(0, 0);
							intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							finish();
							overridePendingTransition(0, 0);
							startActivity(intent);
						}
					});
				}
			}
		});
		printManager.print(job);
	}

	private boolean isSearching() {
		boolean usbSearching = usbSession != null && usbSession.isSearching();
		boolean bluetoothSearching = bluetoothSession != null && bluetoothSession.isSearching();
		return usbSearching || bluetoothSearching;
	}

	private void cancelPrinterSearch() {

		if (usbSession != null) {
			usbSession.cancel();
		}
		if (bluetoothSession != null) {
			bluetoothSession.cancel();
		}
		if (serialSession != null) {
			serialSession.cancel();
		}
	}

	private void startPrinterSearch() {

		prepareSearch();

		if (usbSession != null) {
			usbSession.start();
		}
		if (bluetoothSession != null) {
			bluetoothSession.start();
		}
		if (serialSession != null) {
			serialSession.start();
		}
	}

	private void prepareSearch() {
		if (UsbPrintManager.isUsbAvaliable()) {
			usbSession = printManager.openUsbPrinterDiscoverySession();
			usbSession.setObserver(generalObserver);
		} else {
			Toast.makeText(this, "USB功能未就绪", Toast.LENGTH_SHORT).show();
		}
		if (BluetoothPrintManager.isBluetoothAvaliable()) {
			bluetoothSession = new BluetoothPrinterDiscoverySession(this);
			bluetoothSession.setObserver(generalObserver);
		} else {
			Toast.makeText(this, "蓝牙不可用", Toast.LENGTH_SHORT).show();
		}
		serialSession = new SerialPrinterDiscoverySession();
		serialSession.setObserver(generalObserver);
	}

	private PrinterDiscoveryObserver generalObserver = new PrinterDiscoveryObserver() {

		@Override
		public void onPrinterAdded(final PrinterInfo printer) {
			runOnUiThread(new Runnable() {
				public void run() {
					// printerInfo = printer;
					printers.add(printer);
					Log.e("LOOK", "printer added");
					printManager.connect(printers.get(0));
				}
			});
		}

		@Override
		public void onDiscoveryFailed() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.e("LOOK", "search failed");
				}
			});
		}

		@Override
		public void onDiscoveryCompleted() {
			if (isSearching()) {
				return;
			}
			runOnUiThread(new Runnable() {

				@Override
				public void run() {

				}
			});
		}

		@Override
		public void onDiscoveryCancelled() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Log.e("LOOK", "search cancelled");
				}
			});
		}

		@Override
		public void onDiscoveryStarted() {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {

				}
			});
		}
	};

	private PrintContent getTestContent() {
		if (testContent != null) {
			return testContent;
		}
		PrintContent.Builder builder = new PrintContent.Builder();
		IFormatedPrintContentBuilder formatBuilder = new FixedSpacePrintContentBuilder(builder);

		InputStream is;
		try {
			is = getResources().openRawResource(R.drawable.b24_print);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			builder.appendBitmap(bmp, PrintContent.ALIGN_CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}

		builder.appendString(Localxml.search(SaledorderformActivity.this, "pname"), false, false,
				PrintContent.ALIGN_CENTER, false, true);
		builder.appendString("================================", PrintContent.ALIGN_CENTER);// 32个
		// 付款方式
		StringBuilder sb = new StringBuilder();

		if (cash) {// 现金
			sb.append("现金");
		}
		if (gift) {// 礼品卡
			sb.append(" 礼品卡");
		}
		if (visa) {// 银行卡
			sb.append(" 银行卡");
		}
		if (alipay) {// 支付宝
			sb.append(" 支付宝");
		}
		if (wechat) {// 微信
			sb.append(" 微信");
		}

		formatBuilder.appendStrings(new String[] { "交易单号：", orderDetailId });
		formatBuilder.appendStrings(new String[] { "支付方式：", sb.toString() });
		formatBuilder.appendStrings(new String[] { "时间/日期：", stamp1 });
		builder.appendString(stamp2, false, false, PrintContent.ALIGN_RIGHT, false, false);
		formatBuilder
				.appendStrings(new String[] { "收银员：", "", Localxml.search(SaledorderformActivity.this, "username") });// 12空格

		if (true) {
			// formatBuilder.appendStrings(new String[] { "参考号：", "10330091" });
		}
		builder.appendString("--------------------------------", PrintContent.ALIGN_CENTER);
		formatBuilder.appendStrings(new String[] { "品名", "数量", "单价", "折后单价" });

		int allcount = 0;
		for (int i = 0; i < saledorderproductData.size(); i++) {
			formatBuilder.appendStrings(
					new String[] { saledorderproductData.get(i).getName(), "" + saledorderproductData.get(i).getCount(),
							"" + nf.format(saledorderproductData.get(i).getUnprice()),
							"" + nf.format(saledorderproductData.get(i).getPrice()) });
			allcount = allcount + saledorderproductData.get(i).getCount();
		}

		builder.appendString("--------------------------------", PrintContent.ALIGN_CENTER);
		formatBuilder.appendStrings(new String[] { "合计：", "" + allcount, "" });
		builder.appendString("￥ " + SomeMethod.getCommaDouble(orderSum), true, true, PrintContent.ALIGN_RIGHT, true,
				true);

		if (cash) {// 现金
			formatBuilder.appendStrings(new String[] { "现金金额：", "￥ " + SomeMethod.getCommaDouble(cashvalue) });
		}
		if (visa) {// 信用卡
			formatBuilder.appendStrings(new String[] { "信用卡金额：", "￥ " + SomeMethod.getCommaDouble(visavalue) });// 8空格
		}
		if (alipay) {// 支付宝
			formatBuilder.appendStrings(new String[] { "支付宝金额：", "￥ " + SomeMethod.getCommaDouble(alipayvalue) });// 8空格
		}
		if (wechat) {// 微信
			formatBuilder.appendStrings(new String[] { "微信金额：", "￥ " + SomeMethod.getCommaDouble(wechatvalue) });// 8空格
		}
		if (gift) {
			formatBuilder.appendStrings(new String[] { "礼品卡金额：", "￥ " + SomeMethod.getCommaDouble(giftvalue) });
		}
		formatBuilder.appendStrings(new String[] { "实收：", "￥ " + SomeMethod.getCommaDouble(paidvalue) });// 8空格
		formatBuilder.appendStrings(new String[] { "找零金额：", "￥ " + SomeMethod.getCommaDouble(changevalue) });// 8空格
		builder.appendString("--------------------------------", PrintContent.ALIGN_CENTER);
		// if (MemberFragment.memberNum.equals("_NA_")) {
		// formatBuilder.appendStrings(new String[] { "会员卡号：", "" });
		// } else {
		// formatBuilder.appendStrings(new String[] { "会员卡号：",
		// MemberFragment.memberNum });
		// }

		builder.appendString("================================", PrintContent.ALIGN_CENTER);
		builder.appendString("欢迎光临，谢谢惠顾", false, false, PrintContent.ALIGN_CENTER, false, false);
		builder.appendString(Localxml.search(SaledorderformActivity.this, "storeaddress"), false, false,
				PrintContent.ALIGN_CENTER, false, false);
		builder.appendString(Localxml.search(SaledorderformActivity.this, "storetelephone"), false, false,
				PrintContent.ALIGN_CENTER, false, false);

		InputStream iis;
		try {
			iis = getResources().openRawResource(R.drawable.print_blank);
			Bitmap bmp = BitmapFactory.decodeStream(iis);
			iis.close();
			builder.appendBitmap(bmp, PrintContent.ALIGN_CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream iiis;
		try {
			iiis = getResources().openRawResource(R.drawable.print_blank);
			Bitmap bmp = BitmapFactory.decodeStream(iiis);
			iiis.close();
			builder.appendBitmap(bmp, PrintContent.ALIGN_CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}

		testContent = builder.build();
		return testContent;
	}

	@SuppressWarnings("unused")
	private int findi() {
		for (int i = 0; i < printers.size(); i++) {
			if (!printers.get(i).getIdentifier().equals("")) {
				return i;
			}
			break;
		}
		return 0;
	}

	private String usbDevicePropertyFileName = "printer.json";

	private boolean copyUsbDevicePropertyFile() {
		boolean success = false;
		String folder = getApplicationInfo().dataDir + "/";
		File target = new File(folder + usbDevicePropertyFileName);

		AssetManager manager = this.getAssets();
		try {
			InputStream is = manager.open(usbDevicePropertyFileName);
			OutputStream os = new FileOutputStream(target);
			byte[] buf = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = is.read(buf)) > 0) {
				os.write(buf, 0, bytesRead);
			}
			is.close();
			os.close();
			success = true;
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}

		return success;
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "订单列表==" + result);
			try {
				JSONObject joj = new JSONObject(result);
				Integer size = joj.optInt("size");
				Integer perSize = joj.optInt("VIEW_SIZE");
				Integer count = size % perSize;
				totalPage = count + 1;
				JSONArray jsonarray = joj.optJSONArray("orderList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject jo = (JSONObject) jsonarray.opt(i);
					saledorderInfo = new SaledorderInfo();
					orderId = jo.optString("orderId");
					saledorderInfo.setOrderId(jo.optString("orderId"));
					saledorderInfo.setOrderSum(jo.optString("grandTotal"));
					// info.setOrderTime(orderTime);
					JSONObject time = jo.optJSONObject("orderDate");

					Integer monthI = Integer.valueOf(time.getString("month")) + 1;
					String month;
					if (monthI < 10) {
						month = "0" + monthI.toString();
					} else {
						month = monthI.toString();
					}

					Integer secondI = Integer.valueOf(time.getString("seconds"));
					String second;
					if (secondI < 10) {
						second = "0" + secondI.toString();
					} else {
						second = secondI.toString();
					}

					Integer minuteI = Integer.valueOf(time.getString("minutes"));
					String minute;
					if (minuteI < 10) {
						minute = "0" + minuteI.toString();
					} else {
						minute = minuteI.toString();
					}

					Integer dateI = Integer.valueOf(time.getString("date"));
					String date;
					if (dateI < 10) {
						date = "0" + dateI.toString();
					} else {
						date = dateI.toString();
					}

					Integer hoursI = Integer.valueOf(time.getString("hours"));
					String hours;
					if (hoursI < 10) {
						hours = "0" + hoursI.toString();
					} else {
						hours = hoursI.toString();
					}

					stamp = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-" + month
							+ "-" + date + "  " + hours + ":" + minute + ":" + second;
					saledorderInfo.setOrderTime(stamp);
					saledorderInfo.setClientName(jo.optString("firstName"));
					saledorderData.add(saledorderInfo);
					saledAdapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (whichtask.equals("1")) {
			Log.e("LOOK", "列表搜索==" + result);
			try {
				JSONObject joj = new JSONObject(result);
				Integer size = joj.optInt("size");
				Integer perSize = joj.optInt("VIEW_SIZE");
				Integer count = size % perSize;
				totalPage = count + 1;
				JSONArray jsonarray = joj.optJSONArray("orderList");
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject jo = (JSONObject) jsonarray.opt(i);
					saledorderInfo = new SaledorderInfo();
					saledorderInfo.setOrderId(jo.optString("orderId"));
					saledorderInfo.setOrderSum(jo.optString("grandTotal"));
					// info.setOrderTime(orderTime);
					JSONObject time = jo.optJSONObject("orderDate");

					Integer monthI = Integer.valueOf(time.getString("month")) + 1;
					String month;
					if (monthI < 10) {
						month = "0" + monthI.toString();
					} else {
						month = monthI.toString();
					}

					Integer secondI = Integer.valueOf(time.getString("seconds"));
					String second;
					if (secondI < 10) {
						second = "0" + secondI.toString();
					} else {
						second = secondI.toString();
					}

					Integer minuteI = Integer.valueOf(time.getString("minutes"));
					String minute;
					if (minuteI < 10) {
						minute = "0" + minuteI.toString();
					} else {
						minute = secondI.toString();
					}

					Integer dateI = Integer.valueOf(time.getString("date"));
					String date;
					if (dateI < 10) {
						date = "0" + dateI.toString();
					} else {
						date = dateI.toString();
					}

					stamp = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-" + month
							+ "-" + date + "  " + time.getString("hours") + ":" + minute + ":" + second;
					saledorderInfo.setOrderTime(stamp);
					saledorderInfo.setClientName(jo.optString("firstName"));
					saledorderData.add(saledorderInfo);
					saledAdapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (whichtask.equals("2")) {
			Log.e("LOOK", "打印小票详情 ==" + result);
			try {
				JSONObject Obj = new JSONObject(result);
				JSONObject objDetail = Obj.optJSONObject("orderDetails");

				JSONObject time = objDetail.optJSONObject("orderDate");

				Integer monthI = Integer.valueOf(time.getString("month")) + 1;
				String month;
				if (monthI < 10) {
					month = "0" + monthI.toString();
				} else {
					month = monthI.toString();
				}

				Integer secondI = Integer.valueOf(time.getString("seconds"));
				String second;
				if (secondI < 10) {
					second = "0" + secondI.toString();
				} else {
					second = secondI.toString();
				}

				Integer minuteI = Integer.valueOf(time.getString("minutes"));
				String minute;
				if (minuteI < 10) {
					minute = "0" + minuteI.toString();
				} else {
					minute = minuteI.toString();
				}

				Integer dateI = Integer.valueOf(time.getString("date"));
				String date;
				if (dateI < 10) {
					date = "0" + dateI.toString();
				} else {
					date = dateI.toString();
				}

				Integer hoursI = Integer.valueOf(time.getString("hours"));
				String hours;
				if (hoursI < 10) {
					hours = "0" + hoursI.toString();
				} else {
					hours = hoursI.toString();
				}
				stamp1 = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-" + month + "-"
						+ date;
				stamp2 = hours + ":" + minute + ":" + second;

				orderSum = objDetail.optDouble("grandTotal");

				paidvalue = objDetail.optDouble("orderPaymentReceived");

				changevalue = orderSum - paidvalue;

				JSONArray jsonarrayProduct = Obj.optJSONArray("orderItems");

				for (int i = 0; i < jsonarrayProduct.length(); i++) {
					JSONObject joItem = (JSONObject) jsonarrayProduct.opt(i);

					saledorderproductInfo.setColor(joItem.optString("colorDesc"));
					saledorderproductInfo.setCount(joItem.optInt("quantity"));
					saledorderproductInfo.setId(joItem.optString("productId"));
					saledorderproductInfo.setIsReturn(joItem.optInt("returnQuantity"));
					saledorderproductInfo.setName(joItem.optString("productName"));
					saledorderproductInfo.setPrice(joItem.optDouble("unitPrice"));
					saledorderproductInfo.setUnprice(joItem.optDouble("unitListPrice"));
					
					saledorderproductInfo.setSize(joItem.optString("dimensionDesc"));
					saledorderproductInfo.setStyle(joItem.optString("modelId"));
					saledorderproductInfo.setUrl(joItem.optString("skuImageUrl"));

					saledorderproductData.add(saledorderproductInfo);
				}

				JSONArray jsonarrayPayment = Obj.optJSONArray("paymentDetails");
				for (int i = 0; i < jsonarrayPayment.length(); i++) {
					JSONObject jo = (JSONObject) jsonarrayPayment.opt(i);
					if (jo.optString("paymentMethodTypeId").equals("CASH")) {
						cash = true;
						cashvalue = jo.optDouble("maxAmount");
					} else {
						cash = false;
					}

					if (jo.optString("paymentMethodTypeId").equals("GIFT_CARD")) {
						gift = true;
						giftvalue = jo.optDouble("maxAmount");
					} else {
						gift = false;
					}

					if (jo.optString("paymentMethodTypeId").equals("CREDIT_CARD")) {
						visa = true;
						visavalue = jo.optDouble("maxAmount");
					} else {
						visa = false;
					}

					if (jo.optString("paymentMethodTypeId").equals("EXT_ALIPAY")) {
						alipay = true;
						alipayvalue = jo.optDouble("maxAmount");
					} else {
						alipay = false;
					}

					if (jo.optString("paymentMethodTypeId").equals("EXT_WECHAT")) {
						wechat = true;
						wechatvalue = jo.optDouble("maxAmount");
					} else {
						wechat = false;
					}

				}

				printTimer = new Timer();
				printTimer.schedule(new TimerTask() {
					@Override
					public void run() {
						printHandler.sendEmptyMessage(23);
					}
				}, 500, 1000);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// 所有订单
	public void getAllOrder() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(SaledorderformActivity.this, "externalloginkey")));
		nameValuePair
				.add(new BasicNameValuePair("productStoreId", Localxml.search(SaledorderformActivity.this, "storeid")));
		nameValuePair.add(new BasicNameValuePair("channel", "POS_SALES_CHANNEL"));
		nameValuePair.add(new BasicNameValuePair("VIEW_INDEX", "0"));
		nameValuePair.add(new BasicNameValuePair("VIEW_SIZE", "10"));

		getTask(SaledorderformActivity.this, Urls.base + Urls.get_orderlist, nameValuePair, "0");
	}

	// 条件搜索订单
	public void searchOrder() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(SaledorderformActivity.this, "externalloginkey")));
		nameValuePair
				.add(new BasicNameValuePair("productStoreId", Localxml.search(SaledorderformActivity.this, "storeid")));
		nameValuePair.add(new BasicNameValuePair("channel", "POS_SALES_CHANNEL"));
		nameValuePair.add(new BasicNameValuePair("keyword", et_order_search.getText().toString()));
		nameValuePair.add(new BasicNameValuePair("VIEW_INDEX", "0"));
		nameValuePair.add(new BasicNameValuePair("VIEW_SIZE", "10"));

		getTask(SaledorderformActivity.this, Urls.base + Urls.get_orderlist, nameValuePair, "1");
	}

	// 获取订单详情来打印小票
	private void getOrderDetail() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(SaledorderformActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("orderId", orderDetailId));
		getTask(SaledorderformActivity.this, Urls.base + Urls.get_orderdetails, nameValuePair, "2");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SCANNIN_GREQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				// 显示扫描到的内容
				et_order_search.setText(bundle.getString("result"));
			}
		}
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
