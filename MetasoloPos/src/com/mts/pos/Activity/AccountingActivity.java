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
import com.mts.pos.Common.BaseActivity;
import com.mts.pos.Common.Constants;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.MyDate;
import com.mts.pos.Common.MyPostTask;
import com.mts.pos.Common.NetworkUtil;
import com.mts.pos.Common.NoDoubleClickUtils;
import com.mts.pos.Common.ScanGunKeyEventHelper;
import com.mts.pos.Common.SomeMethod;
import com.mts.pos.Common.Url2Bmp;
import com.mts.pos.Common.Urls;
import com.mts.pos.Printer.FixedSpacePrintContentBuilder;
import com.mts.pos.Printer.IFormatedPrintContentBuilder;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class AccountingActivity extends BaseActivity
		implements OnClickListener, ScanGunKeyEventHelper.OnScanSuccessListener {

	private ImageView iv_back, iv_cash, iv_gift, iv_wechat, iv_alipay, iv_visa, iv_qr;
	private TextView tv_ar, tv_nr, tv_changge, tv_scan, tv_payno, tv_account, tv_scanO;
	private EditText et_cash, et_giftsum, et_giftno, et_wechat_amount, et_alipay_amount, et_visa_amount;
	private RelativeLayout rl_paycontent, rl_noaccount, rl_row1, rl_row2, rl_row3, rl_row4, rl_row5, rl_row6, rl_row7,
			rl_row8;
	private Spinner sp_account_spinner;
	private Boolean cash = false, gift = false, wechat = false, alipay = false, visa = false;
	private double total_cost;
	private String comeFlag;
	private String productId;
	private String productName;
	private int count;
	private List<String> mList = new ArrayList<String>();
	private ArrayAdapter<String> arrayAdapter;
	Timer timer = new Timer();
	Double cashvalue = 0.00;
	Double giftvalue = 0.00;
	String giftNo;
	Double wechatvalue = 0.00;
	Double alipayvalue = 0.00;
	Double visavalue = 0.00;
	Double paidvalue = 0.00;
	Double changevalue = 0.00;

	String transactionId = "";
	String alipayType = "1";
	String alipayCode = ""; // 获取到的支付宝id
	String wechatCode = ""; // 获取到的微信id
	String orderId = "";// 订单号
	String wechatTradeNo = "";
	String isSuccess = "";

	boolean alipay_QR_OK = false;

	Handler zuiLeHandler;
	Timer zuiLeTimer = null;

	// 打印相关
	IPrinterDiscoverySession usbSession;// USB相关对象
	IPrinterDiscoverySession bluetoothSession; // 蓝牙相关对象
	IPrinterDiscoverySession serialSession;
	PrintManager printManager;
	// PrinterInfo printerInfo = null;
	PrintContent testContent = null;
	Timer printTimer = null;
	NumberFormat nf;

	private ScanGunKeyEventHelper mScanGunKeyEventHelper;

	private ArrayList<PrinterInfo> printers = new ArrayList<PrinterInfo>();
	// private SimplePrinterAdapter adapter = null;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_accounting);
		super.onCreate(inState);

		getViewId();

		mList.add("打印小票");
		mList.add("不打印小票");
		arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_dropdown_item, mList);
		arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp_account_spinner.setAdapter(arrayAdapter);
		sp_account_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.e("LOOOOOOOO", mList.get(position));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		registerBoradcastReceiver();
		mScanGunKeyEventHelper = new ScanGunKeyEventHelper(this);

		nf = new DecimalFormat("###.####");

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		productName = bundle.getString("productName");
		total_cost = bundle.getDouble("totalCost");
		comeFlag = bundle.getString("comeFlag");
		productId = bundle.getString("productId");
		count = bundle.getInt("count");
		Log.e("LOOK", "优惠后的价格==" + comeFlag);

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 33) {

					DecimalFormat df = new DecimalFormat("0.00");
					if (et_cash.getText().toString().equals("")
							|| Double.valueOf(et_cash.getText().toString()) == 0.00) {
						cashvalue = 0.00;
					} else {
						cashvalue = Double.valueOf(et_cash.getText().toString());
					}
					if (et_giftsum.getText().toString().equals("")
							|| Double.valueOf(et_giftsum.getText().toString()) == 0.00) {
						giftvalue = 0.00;
					} else {
						giftvalue = Double.valueOf(et_giftsum.getText().toString());
					}
					if (et_giftno.getText().toString().equals("")) {
						giftNo = "";
					} else {
						giftNo = et_giftno.getText().toString();
					}

					if (et_wechat_amount.getText().toString().equals("")
							|| Double.valueOf(et_wechat_amount.getText().toString()) == 0.00) {
						wechatvalue = 0.00;
					} else {
						wechatvalue = Double.valueOf(et_wechat_amount.getText().toString());
					}
					if (et_alipay_amount.getText().toString().equals("")
							|| Double.valueOf(et_alipay_amount.getText().toString()) == 0.00) {
						alipayvalue = 0.00;
					} else {
						alipayvalue = Double.valueOf(et_alipay_amount.getText().toString());
					}
					if (et_visa_amount.getText().toString().equals("")
							|| Double.valueOf(et_visa_amount.getText().toString()) == 0.00) {
						visavalue = 0.00;
					} else {
						visavalue = Double.valueOf(et_visa_amount.getText().toString());
					}

					paidvalue = cashvalue + giftvalue + wechatvalue + alipayvalue + visavalue;

					if (isSuccess.equals("Y")) {
						paidvalue = alipayvalue = total_cost;
					}
					tv_nr.setText("￥  " + df.format(paidvalue));
					changevalue = total_cost - paidvalue;
					tv_changge.setText("￥  " + df.format(changevalue));
					tv_ar.setText("￥  " + total_cost);
				}
			}
		};

		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(33);
			}
		}, 600, 1000);

		if (cash == false && gift == false && wechat == false && alipay == false && visa == false) {
			rl_noaccount.setVisibility(View.VISIBLE);
			rl_paycontent.setVisibility(View.GONE);
		}

		iv_cash.setOnClickListener(this);
		iv_gift.setOnClickListener(this);
		iv_wechat.setOnClickListener(this);
		iv_alipay.setOnClickListener(this);
		iv_visa.setOnClickListener(this);
		tv_scan.setOnClickListener(this);
		tv_payno.setOnClickListener(this);
		tv_scanO.setOnClickListener(this);

		tv_account.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cash || gift || visa || alipay || wechat) {
					if (cash && cashvalue == 0.00) {
						Toast.makeText(AccountingActivity.this, "选择现金付款，请输入现金金额", Toast.LENGTH_SHORT).show();
					} else if (gift && giftvalue == 0.00 || (giftNo.equals("") && gift)) {
						Toast.makeText(AccountingActivity.this, "选择礼品卡付款，请输入礼品卡号和金额", Toast.LENGTH_SHORT).show();
					} else if (wechat && wechatvalue == 0.00) {
						Toast.makeText(AccountingActivity.this, "选择微信支付，请输入支付金额", Toast.LENGTH_SHORT).show();
					} else if (alipay && alipayvalue == 0.00) {
						Toast.makeText(AccountingActivity.this, "选择支付宝支付，请输入支付金额", Toast.LENGTH_SHORT).show();
					} else if (visa && visavalue == 0.00) {
						Toast.makeText(AccountingActivity.this, "选择银行卡支付，请输入支付金额", Toast.LENGTH_SHORT).show();
					} else {
						if (paidvalue < total_cost) {
							Toast.makeText(AccountingActivity.this, "支付金额不足，无法结账", Toast.LENGTH_SHORT).show();
						} else {
							getNum();
							if (PayActivity.promoTimer != null) {
								PayActivity.promoTimer.cancel();
								PayActivity.promoTimer = null;
							}
						}
					}
				} else {
					Toast.makeText(AccountingActivity.this, "请选择付款方式", Toast.LENGTH_SHORT).show();
				}
			}
		});

		copyUsbDevicePropertyFile();
		printManager = PrintManager.getInstance(this);

		printManager.setPrinterConnectionResultObserver(new PrinterConnectionResultObserver() {
			@Override
			public void onResult(String identifier, boolean result) {
				printers.get(findi()).setConnected(result);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {

					}
				});
			}
		});
		printTimer = new Timer();

		// 每隔五秒请求
		zuiLeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 24) {
					zuiLe();
				}
			}
		};

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.putExtra("result", "undone");
			setResult(2, intent);
			finish();
			// zuiLeTimer.cancel();
			zuiLeTimer = null;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void getViewId() {
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_cash = (ImageView) findViewById(R.id.iv_cash);
		iv_gift = (ImageView) findViewById(R.id.iv_gift);
		iv_wechat = (ImageView) findViewById(R.id.iv_wechat);
		iv_alipay = (ImageView) findViewById(R.id.iv_alipay);
		iv_visa = (ImageView) findViewById(R.id.iv_visa);
		iv_qr = (ImageView) findViewById(R.id.iv_qr);
		tv_ar = (TextView) findViewById(R.id.tv_ar);
		tv_nr = (TextView) findViewById(R.id.tv_nr);
		tv_changge = (TextView) findViewById(R.id.tv_changge);
		tv_scan = (TextView) findViewById(R.id.tv_scan);
		tv_payno = (TextView) findViewById(R.id.tv_payno);
		tv_account = (TextView) findViewById(R.id.tv_account);
		tv_scanO = (TextView) findViewById(R.id.tv_scanO);
		et_cash = (EditText) findViewById(R.id.et_cash);
		et_giftsum = (EditText) findViewById(R.id.et_giftsum);
		et_giftno = (EditText) findViewById(R.id.et_giftno);
		et_wechat_amount = (EditText) findViewById(R.id.et_wechat_amount);
		et_alipay_amount = (EditText) findViewById(R.id.et_alipay_amount);
		et_visa_amount = (EditText) findViewById(R.id.et_visa_amount);
		rl_paycontent = (RelativeLayout) findViewById(R.id.rl_paycontent);
		rl_noaccount = (RelativeLayout) findViewById(R.id.rl_noaccount);
		rl_row1 = (RelativeLayout) findViewById(R.id.rl_row1);
		rl_row2 = (RelativeLayout) findViewById(R.id.rl_row2);
		rl_row3 = (RelativeLayout) findViewById(R.id.rl_row3);
		rl_row4 = (RelativeLayout) findViewById(R.id.rl_row4);
		rl_row5 = (RelativeLayout) findViewById(R.id.rl_row5);
		rl_row6 = (RelativeLayout) findViewById(R.id.rl_row6);
		rl_row7 = (RelativeLayout) findViewById(R.id.rl_row7);
		rl_row8 = (RelativeLayout) findViewById(R.id.rl_row8);
		sp_account_spinner = (Spinner) findViewById(R.id.sp_account_spinner);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_cash:
			if (cash) {
				cash = false;
				rl_row1.setVisibility(View.GONE);
				et_cash.setText("");
				iv_cash.setBackgroundResource(R.drawable.cash);
				if (alipay && gift == false && wechat == false && visa == false) {
					rl_noaccount.setVisibility(View.VISIBLE);
					rl_paycontent.setVisibility(View.GONE);
				}
			} else {
				rl_row1.setVisibility(View.VISIBLE);
				if (wechat == false) {
					rl_row7.setVisibility(View.GONE);
				}
				if (alipay == false) {
					rl_row8.setVisibility(View.GONE);
				}

				iv_cash.setBackgroundResource(R.drawable.cash_press);
				cash = true;
				rl_noaccount.setVisibility(View.GONE);
				rl_paycontent.setVisibility(View.VISIBLE);
				// if (alipay && gift == false && wechat == false && visa ==
				// false) {
				// tv_payno.setVisibility(View.GONE);
				// tv_payno_gray.setVisibility(View.VISIBLE);
				// } else {
				// rl_row7.setVisibility(View.GONE);
				// }
			}
			break;
		case R.id.iv_gift:
			if (gift) {
				gift = false;
				rl_row2.setVisibility(View.GONE);
				rl_row3.setVisibility(View.GONE);
				et_giftsum.setText("");
				et_giftno.setText("");
				iv_gift.setBackgroundResource(R.drawable.gift);
				if (alipay == false && cash == false && wechat == false && visa == false) {
					rl_noaccount.setVisibility(View.VISIBLE);
					rl_paycontent.setVisibility(View.GONE);
				} else {
					rl_noaccount.setVisibility(View.GONE);
					rl_paycontent.setVisibility(View.VISIBLE);
				}
			} else {
				rl_row2.setVisibility(View.VISIBLE);
				rl_row3.setVisibility(View.VISIBLE);
				if (wechat == false) {
					rl_row7.setVisibility(View.GONE);
				}
				if (alipay == false) {
					rl_row8.setVisibility(View.GONE);
				}
				iv_gift.setBackgroundResource(R.drawable.gift_press);
				gift = true;
				rl_noaccount.setVisibility(View.GONE);
				rl_paycontent.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.iv_wechat:
			if (wechat) {
				wechat = false;
				rl_row4.setVisibility(View.GONE);
				rl_row7.setVisibility(View.INVISIBLE);
				et_wechat_amount.setText("");
				iv_wechat.setBackgroundResource(R.drawable.wechat);
				if (alipay == false && cash == false && gift == false && visa == false) {
					rl_noaccount.setVisibility(View.VISIBLE);
					rl_paycontent.setVisibility(View.GONE);
				} else {
					rl_noaccount.setVisibility(View.GONE);
					rl_paycontent.setVisibility(View.VISIBLE);
				}
			} else {
				wechat = true;
				rl_row4.setVisibility(View.VISIBLE);
				rl_row7.setVisibility(View.VISIBLE);
				if (alipay) {
					rl_row8.setVisibility(View.VISIBLE);
				} else {
					rl_row8.setVisibility(View.GONE);
				}

				iv_wechat.setBackgroundResource(R.drawable.wechat_press);
				rl_noaccount.setVisibility(View.GONE);
				rl_paycontent.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.iv_alipay:
			if (alipay) {
				alipay = false;
				rl_row5.setVisibility(View.GONE);
				rl_row8.setVisibility(View.GONE);
				et_alipay_amount.setText("");
				iv_qr.setVisibility(View.GONE);
				iv_alipay.setBackgroundResource(R.drawable.alipay);
				if (wechat == false && cash == false && gift == false && visa == false) {
					rl_noaccount.setVisibility(View.VISIBLE);
					rl_paycontent.setVisibility(View.GONE);
				} else {
					rl_noaccount.setVisibility(View.GONE);
					rl_paycontent.setVisibility(View.VISIBLE);
				}
			} else {
				alipay = true;
				rl_row5.setVisibility(View.VISIBLE);
				if (wechat) {
					rl_row7.setVisibility(View.VISIBLE);
				} else {
					rl_row7.setVisibility(View.INVISIBLE);
				}
				iv_qr.setVisibility(View.VISIBLE);
				rl_row8.setVisibility(View.VISIBLE);
				iv_alipay.setBackgroundResource(R.drawable.alipay_press);
				rl_noaccount.setVisibility(View.GONE);
				rl_paycontent.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.iv_visa:
			if (visa) {
				visa = false;
				rl_row6.setVisibility(View.GONE);
				et_visa_amount.setText("");
				iv_visa.setBackgroundResource(R.drawable.visa);
				if (wechat == false && cash == false && gift == false && alipay == false) {
					rl_noaccount.setVisibility(View.VISIBLE);
					rl_paycontent.setVisibility(View.GONE);
				} else {
					rl_noaccount.setVisibility(View.GONE);
					rl_paycontent.setVisibility(View.VISIBLE);
				}
			} else {
				visa = true;
				rl_row6.setVisibility(View.VISIBLE);
				if (wechat == false) {
					rl_row7.setVisibility(View.GONE);
				}
				if (alipay == false) {
					rl_row8.setVisibility(View.GONE);
				}
				iv_visa.setBackgroundResource(R.drawable.visa_press);
				rl_noaccount.setVisibility(View.GONE);
				rl_paycontent.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.tv_scan:
		case R.id.tv_scanO:
			// 扫描买家的支付宝获取买家支付宝账号 支付指定金额
			// 应该是连接扫码器 现在用设备的摄像头代替
			Intent intent = new Intent();
			intent.setClass(AccountingActivity.this, MipcaActivityCapture.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(intent, 11);
			if (alipay) {
				alipayType = "1";
			}
			break;
		case R.id.tv_payno:
			// 让买家扫描卖家的二维码来支付金额 （支付全款）
			if (!NoDoubleClickUtils.isDoubleClick()) {
				if (alipay) {
					alipayType = "2";
				}
				getNum();
			}

			break;
		}

	}

	/**
	 * 关闭此页面
	 */
	public void closeActivity(View v) {
		Intent intent = new Intent();
		intent.putExtra("result", "undone");
		setResult(2, intent);
		finish();
		// zuiLeTimer.cancel();
		zuiLeTimer = null;
	}

	@Override
	protected void updateUI(String whichtask, String result) {
		super.updateUI(whichtask, result);
		if (whichtask.equals("0")) {
			Log.e("LOOK", "交易单号==" + result);
			try {
				transactionId = new JSONObject(result).optString("transactionId");
				if (!transactionId.equals("")) {
					if (alipay) {
						if (alipayType.equals("1")) {// 支付宝扫码
							alipayScan();
						} else {// 支付宝二维码
							if (alipay_QR_OK) {
								Log.e("LOOK", "二维码支付成功");
							} else {
								Log.e("LOOK", "二维码支付失败");
								getAlipayQR();
							}
						}
					} else if (wechat) {
						wechatPay();
					} else {
						submitShoppingCart();
					}
				} else {
					Toast.makeText(AccountingActivity.this, "不能获取交易单号，请稍后再试！", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("1")) {
			// 提交购物车
			Log.e("LOOK", "结算==" + result);
			try {
				orderId = new JSONObject(result).optString("orderId");
				String message = new JSONObject(result).optString("_ERROR_MESSAGE_");
				if (!"".equals(orderId) && orderId != null) {
					// 连接打印机
					printTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							printHandler.sendEmptyMessage(23);
						}
					}, 0, 1000);
					Toast.makeText(AccountingActivity.this, "结算成功", Toast.LENGTH_SHORT).show();

					PayActivity.tv_total.setText("总计：￥0");
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent();
							intent.putExtra("result", "done");
							setResult(2, intent);
							finish();
						}
					}, 2500);
				} else {
					Toast.makeText(AccountingActivity.this, message, Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("2")) {
			// 支付宝扫码支付
			Log.e("LOOk", "支付宝扫码支付==" + result);
			try {
				String buyerLogonId = new JSONObject(result).optString("buyerLogonId");
				if (!buyerLogonId.equals("")) {
					// 提交购物车
					submitShoppingCart();
				} else {
					Toast.makeText(AccountingActivity.this, "支付宝支付失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("3")) {
			// 支付宝生成二维码
			Log.e("LOOK", "支付宝生成二维码==" + result);
			try {
				String qrCode = new JSONObject(result).optString("qrCode");
				if (!qrCode.equals("")) {
					// 生成二维码
					PicPostTask task = new PicPostTask(qrCode);
					task.execute();
				} else {
					Toast.makeText(AccountingActivity.this, "获取支付宝二维码失败！", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("4")) {
			// 微信支付
			Log.e("LOOK", "微信支付结果==" + result);
			try {
				JSONArray jsonarray = new JSONObject(result).optJSONArray("_ERROR_MESSAGE_LIST_");
				if (jsonarray == null) {
					wechatTradeNo = new JSONObject(result).optString("wechatTradeNo");
					if (!wechatTradeNo.equals("")) {
						submitShoppingCart();
					}
				} else {
					Toast.makeText(AccountingActivity.this, "微信子商号错误，请与源一云商客服联系。", Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (whichtask.equals("5")) {
			Log.e("LOOK", "每隔五秒查看是否交易成功==" + result);
			try {
				isSuccess = new JSONObject(result).optString("isSuccess");
				if (isSuccess.equals("Y")) {
					zuiLeTimer.cancel();
					zuiLeTimer = null;
					Toast.makeText(AccountingActivity.this, "支付宝支付成功！", Toast.LENGTH_LONG).show();
					paidvalue = total_cost;
					submitShoppingCart();
					alipay_QR_OK = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 11:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				if (alipay) {
					// 扫描到的内容
					alipayCode = bundle.getString("result");
				} else if (wechat) {
					wechatCode = bundle.getString("result");
				}
			}
			break;
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (mScanGunKeyEventHelper.isScanGunEvent(event)) {
			mScanGunKeyEventHelper.analysisKeyEvent(event);
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mScanGunKeyEventHelper.hasScanGun()) {
			Toast.makeText(AccountingActivity.this, "未检测到蓝牙扫码枪", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(stateChangeReceiver);
		mScanGunKeyEventHelper.onDestroy();
	}

	private void registerBoradcastReceiver() {
		IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
		IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		registerReceiver(stateChangeReceiver, filter1);
		registerReceiver(stateChangeReceiver, filter2);
	}

	private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				Toast.makeText(AccountingActivity.this, "蓝牙设备连接状态已变更", Toast.LENGTH_SHORT).show();
			} else if (action.equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
				Toast.makeText(AccountingActivity.this, "蓝牙设备连接状态已变更", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 获取交易单号
	 */
	private void getNum() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(AccountingActivity.this, "externalloginkey")));
		getTask(AccountingActivity.this, Urls.base + Urls.transaction_id, nameValuePair, "0");
	}

	/**
	 * 支付宝扫码支付
	 */
	private void alipayScan() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(AccountingActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("transId", transactionId));
		nameValuePair.add(new BasicNameValuePair("authCode", alipayCode));// 支付宝码
		nameValuePair.add(new BasicNameValuePair("amountAlipay", String.valueOf(alipayvalue)));// 金额（支付宝指定金额）
		nameValuePair.add(new BasicNameValuePair("terminalId", Localxml.search(AccountingActivity.this, "posid")));
		nameValuePair.add(new BasicNameValuePair("storeId", Localxml.search(AccountingActivity.this, "storeid")));
		getTask(AccountingActivity.this, Urls.base + Urls.alipay_saoma, nameValuePair, "2");
	}

	/**
	 * 支付宝支付，获取二维码
	 */
	private void getAlipayQR() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(AccountingActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("transId", transactionId));
		nameValuePair.add(new BasicNameValuePair("amountAlipay", String.valueOf(total_cost)));// 金额（全款）
		nameValuePair.add(new BasicNameValuePair("terminalId", Localxml.search(AccountingActivity.this, "posid")));
		nameValuePair.add(new BasicNameValuePair("storeId", Localxml.search(AccountingActivity.this, "storeid")));
		getTask(AccountingActivity.this, Urls.base + Urls.alipay_erweima, nameValuePair, "3");
	}

	/**
	 * 微信支付接口
	 */
	private void wechatPay() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(AccountingActivity.this, "externalloginkey")));

		nameValuePair.add(new BasicNameValuePair("transId", transactionId));
		nameValuePair.add(new BasicNameValuePair("authCode", wechatCode));
		nameValuePair.add(new BasicNameValuePair("amountWechat", String.valueOf(wechatvalue)));
		nameValuePair.add(new BasicNameValuePair("terminalId", Localxml.search(AccountingActivity.this, "posid")));
		nameValuePair.add(new BasicNameValuePair("storeId", Localxml.search(AccountingActivity.this, "storeid")));
		getTask(AccountingActivity.this, Urls.base + Urls.wechat_scan, nameValuePair, "4");
	}

	/**
	 * 提交购物车信息
	 */
	private void submitShoppingCart() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(AccountingActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("CART", creatJSON(cash, gift, visa, alipay, wechat)));
		getTask(AccountingActivity.this, Urls.base + Urls.shopping_cart, nameValuePair, "1");
	}

	/**
	 * 查看支付宝是否支付成功
	 */
	private void zuiLe() {
		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(AccountingActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("transId", transactionId));
		if (NetworkUtil.isConnected(AccountingActivity.this)) {
			ZuiLeTask zuiletask = new ZuiLeTask(AccountingActivity.this, Urls.base + Urls.zuiLe, nameValuePair, "5");
			zuiletask.execute("");
		} else {
			Toast.makeText(AccountingActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 拼接JSON
	 * 
	 * @return
	 */

	private String creatJSON(boolean cashYN, boolean presentYN, boolean creditYN, boolean alipayYN, boolean wechatYN) {
		JSONObject allObj = new JSONObject();
		JSONArray productList = new JSONArray();
		JSONArray moneyList = new JSONArray();
		try {
			// 交易总价，币种，会员编号，posID，交易单号
			allObj.putOpt("billingAccountAmt", String.valueOf(SomeMethod.get2Double(total_cost)));
			allObj.putOpt("currency", "CNY");
			allObj.putOpt("orderPartyId", "_NA_");
			allObj.putOpt("terminalId", Localxml.search(AccountingActivity.this, "posid"));
			allObj.putOpt("transactionId", transactionId);
			allObj.putOpt("salesman", Localxml.search(AccountingActivity.this, "username"));
			// 所购物品信息 价格 ID 数量
			if (comeFlag.equals("Goods")) {
				// 导购传来的商品信息
				JSONObject products1 = new JSONObject();
				products1.put("basePrice", total_cost);
				products1.put("productId", productId);
				products1.put("quantity", count);
				productList.put(products1);
				allObj.putOpt("cartLines", productList);
			} else if (comeFlag.equals("Pay")) {
				// 收银传来的商品信息
				for (int i = 0; i < PayActivity.productData.size(); i++) {
					JSONObject products = new JSONObject();
					products.put("basePrice", PayActivity.productData.get(i).getPresent_cost());
					products.put("productId", PayActivity.productData.get(i).getProduct_id());
					products.put("quantity", PayActivity.productData.get(i).getProduct_count());
					productList.put(products);
				}
				allObj.putOpt("cartLines", productList);
			}
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
		Log.e("LOOK", "购物车JSON==" + allObj.toString());
		return allObj.toString();

	}

	/**
	 * 打印订单内容
	 */
	public void print() {
		int size = printers.size();
		// for (int i = 0; i < size; i++) {
		PrinterInfo printer = printers.get(0);
		PrintJobInfo job = new PrintJobInfo(printer, getTestContent());
		job.setStatusObserver(new StatusObserver() {

			@Override
			public void onStatus(final PrintJobInfo job, int newStatus) {
				if (job.isEnded()) {
					// String msg = job.getName() + " " + job.getStatusName() +
					// "\n";
					// msg += "wait " + job.getWaitTime() + "ms\n";
					// msg += "execution " + job.getExecutionTime() + "ms";
					String msg = job.getStatusName();
					final String finalMsg = msg;
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(AccountingActivity.this, finalMsg, Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		});
		printManager.print(job);
		// }
		// PrinterInfo printer = printers.get(findi());
		// if (null == printer) {
		// Toast.makeText(AccountingActivity.this, "请连接打印机",
		// Toast.LENGTH_SHORT).show();
		// } else {
		//
		// // if (printerInfo.isConnected()) {
		// if (printer.isConnected()) {
		// Log.i("tag", "connected");
		// PrintJobInfo job = new PrintJobInfo(printer, getTestContent());
		// job.setStatusObserver(new StatusObserver() {
		//
		// @Override
		// public void onStatus(PrintJobInfo job, int newStatus) {
		// if (job.isEnded()) {
		// String msg = job.getName() + " " + job.getStatusName() + "\n";
		// msg += "error message: " + job.getErrorMessage() + "\n";
		// msg += "wait " + job.getWaitTime() + "ms\n";
		// msg += "execution " + job.getExecutionTime() + "ms";
		// final String finalMsg = msg;
		// PrintUtils.runInMainThread(new Runnable() {
		// @Override
		// public void run() {
		// Toast.makeText(AccountingActivity.this, finalMsg,
		// Toast.LENGTH_SHORT).show();
		// Log.e("打印对象队列", "" + printers.size());
		// Log.e("打印内容", finalMsg);
		// }
		// });
		// }
		// }
		// });
		//
		// printManager.print(job);
		// } else {
		// Toast.makeText(AccountingActivity.this, "打印失败",
		// Toast.LENGTH_SHORT).show();
		// printManager.connect(printers.get(findi()));
		// }
		// }
	}

	final Handler printHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 23) {
				/**
				 * 有线打印
				 */
				// if (null == printerInfo || !printerInfo.isConnected()) {

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

	private boolean isSearching() {
		boolean usbSearching = usbSession != null && usbSession.isSearching();
		boolean bluetoothSearching = bluetoothSession != null && bluetoothSession.isSearching();
		return usbSearching || bluetoothSearching;
		//
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
		// builder.appendString(" ", false, false, PrintContent.ALIGN_CENTER,
		// false, false);
		InputStream is;
		try {
			is = getResources().openRawResource(R.drawable.b24_print);
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			builder.appendBitmap(bmp, PrintContent.ALIGN_CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 开钱箱
		builder.appendPulseSignal();

		builder.appendString(Localxml.search(AccountingActivity.this, "pname"), false, false, PrintContent.ALIGN_CENTER,
				false, true);
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
		formatBuilder.appendStrings(new String[] { "交易单号：", orderId });
		formatBuilder.appendStrings(new String[] { "支付方式：", sb.toString() });
		formatBuilder.appendStrings(new String[] { "时间/日期：", MyDate.getDate() });
		builder.appendString(MyDate.getTime(), false, false, PrintContent.ALIGN_RIGHT, false, false);
		formatBuilder.appendStrings(new String[] { "收银员：", "", Localxml.search(AccountingActivity.this, "username") });// 12空格
		// formatBuilder.appendStrings(new String[] { "终端编号：", "",
		// Localxml.search(AccountingActivity.this, "posid") });// 8空格
		if (visa) {
			// formatBuilder.appendStrings(new String[] { "参考号：", "10330091" });
		}
		builder.appendString("--------------------------------", PrintContent.ALIGN_CENTER);
		formatBuilder.appendStrings(new String[] { "品名", "数量", "原价", "折后价" });
		int allcount = 0;
		if (comeFlag.equals("Pay")) {
			for (int i = 0; i < PayActivity.productData.size(); i++) {
				formatBuilder.appendStrings(new String[] { PayActivity.productData.get(i).getProduct_name(),
						"" + PayActivity.productData.get(i).getProduct_count(),
						"" + nf.format(PayActivity.productData.get(i).getOriginal_cost()),
						"" + nf.format(PayActivity.productData.get(i).getPresent_cost()) });
				allcount = allcount + PayActivity.productData.get(i).getProduct_count();
			}
		} else if (comeFlag.equals("Goods")) {
			formatBuilder.appendStrings(
					new String[] { productName, "" + count, "￥ " + SomeMethod.getCommaDouble(total_cost) });
			allcount = allcount + count;
		}

		builder.appendString("--------------------------------", PrintContent.ALIGN_CENTER);
		formatBuilder.appendStrings(new String[] { "合计：", "" + allcount, "" });
		builder.appendString("￥ " + SomeMethod.getCommaDouble(total_cost), true, true, PrintContent.ALIGN_RIGHT, true,
				true);
		// builder.appendString(" ", false, false, PrintContent.ALIGN_CENTER,
		// false, false);
		if (giftvalue == 0.00) {
			// formatBuilder.appendStrings(new String[] { "礼品卡金额：", "" });
		} else {
			formatBuilder.appendStrings(new String[] { "礼品卡金额：", "￥ " + SomeMethod.getCommaDouble(giftvalue) });
		}
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
		builder.appendString(Localxml.search(AccountingActivity.this, "storeaddress"), false, false,
				PrintContent.ALIGN_CENTER, false, false);
		builder.appendString(Localxml.search(AccountingActivity.this, "storetelephone"), false, false,
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

	// 没用的方法1
	private PrinterInfo findPrinter(String identifier) {
		for (PrinterInfo printer : printers) {
			if (printer.getIdentifier().equals(identifier)) {
				return printer;
			}
		}
		return null;
	}

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

	/**
	 * 将每三个数字加上逗号处理
	 * 
	 * @param str无逗号的数字
	 * 
	 * @return加上逗号的数字
	 * 
	 */

	private static String addComma(String str) {
		// 将传进数字反转
		String reverseStr = new StringBuilder(str).reverse().toString();
		String strTemp = "";
		for (int i = 0; i < reverseStr.length(); i++) {
			if (i * 3 + 3 > reverseStr.length()) {
				strTemp += reverseStr.substring(i * 3, reverseStr.length());
				break;
			}
			strTemp += reverseStr.substring(i * 3, i * 3 + 3) + ",";
		}
		// 将[789,456,] 中最后一个[,]去除
		if (strTemp.endsWith(",")) {
			strTemp = strTemp.substring(0, strTemp.length() - 1);
		}
		// 将数字重新反转
		String resultStr = new StringBuilder(strTemp).reverse().toString();
		return resultStr;
	}

	/**
	 * 生成二维码图片
	 */

	class PicPostTask extends AsyncTask<String, Integer, Bitmap> {
		private String imgurl;
		private Bitmap bitmap;

		public PicPostTask(String url) {
			this.imgurl = url;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			nameValuePair.add(new BasicNameValuePair("width", "200"));
			nameValuePair.add(new BasicNameValuePair("height", "200"));
			nameValuePair.add(new BasicNameValuePair("message", imgurl));
			try {
				// bitmap = httpPost(AccountingActivity.this, Urls.qrcode,
				// nameValuePair);
				bitmap = Url2Bmp.httpPost(AccountingActivity.this, Urls.qrcode, nameValuePair);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			iv_qr.setImageBitmap(bitmap);
			// Log.e("LOOK", "bitmap.getByteCount()==" + bitmap.getByteCount());
			zuiLeTimer = new Timer();
			zuiLeTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					zuiLeHandler.sendEmptyMessage(24);
					// zuiLe();
				}
			}, 2000, 5000);
		}
	}

	class ZuiLeTask extends MyPostTask {
		String which;

		public ZuiLeTask(Context context, String url, List<NameValuePair> nameValuePair, String whichtask) {
			super(context, url, nameValuePair, whichtask);
			which = whichtask;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null == result || ("").equals(result) || result.length() == 0 || result.equals(Constants.not_found)
					|| result.equals(Constants.time_out)) {
				Toast.makeText(AccountingActivity.this, "网络不好，请重试！", Toast.LENGTH_SHORT).show();
			} else {
				updateUI(which, result);
			}
		}
	}

	@Override
	public void onScanSuccess(String barcode) {
		Log.e("扫描结果==", barcode);
		if (alipay) {
			// 扫描到的内容
			alipayCode = barcode;
		} else if (wechat) {
			wechatCode = barcode;
		}

	}
}
