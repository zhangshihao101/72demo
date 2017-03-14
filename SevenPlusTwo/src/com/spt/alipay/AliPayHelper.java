package com.spt.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.spt.utils.MyConstant;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by ID_MARR on 2014/11/25.
 */
public class AliPayHelper {

	// public static boolean IS_DEBUG = true;

	// public static final String NOTIFY_URL = MallApi.getHost() +
	// MallApi.ALIPAY_NOTIFY_URL;
	// public static final String NOTIFY_URL_DEBUG = MallApi.getHost() +
	// MallApi.ALIPAY_NOTIFY_URL_DEBUG;

	public interface AliPayCallback {
		public void onPay(int status);

	}

	private static AliPayHelper mInstance;

	private Activity mActivity;
	private AliPayCallback mCallback;

	private AliPayHelper(Activity activity) {
		this.mActivity = activity;
	}

	public static AliPayHelper getInstance(Activity activity) {
		if (mInstance == null) {
			mInstance = new AliPayHelper(activity);
		}
		return mInstance;
	}

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	private Handler mAlipayHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			Result result = new Result((String) msg.obj);
			Log.i("alipay", "result = " + result);
			Toast.makeText(mActivity, result.getResult(), Toast.LENGTH_SHORT).show();
			if (mCallback != null) {
				mCallback.onPay(Integer.parseInt(result.getStatus()));
			}
		}
	};

	public void setCallback(AliPayCallback callback) {
		this.mCallback = callback;
	}

	/**
	 * check whether the device has authentication alipay account.
	 * 查询终端设备是否存在支付宝认证账户
	 */
	public void isAccountExist() {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				PayTask payTask = new PayTask(mActivity);
				boolean isExist = payTask.checkAccountIfExist();

				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mAlipayHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * 获取AliPaySDK的版本
	 */
	public String getSDKVersion() {
		PayTask payTask = new PayTask(mActivity);
		return payTask.getVersion();
	}

	public void pay(String out_trade_no, String total_fee, String order_sn, AliPayCallback callback) {
		setCallback(callback);

		String info = getOrderInfo(out_trade_no, total_fee, "七加二驴友商城订单，订单号：", order_sn);
		Log.e("MARR", "alipay info : " + info);
		info = getSignedOrderInfo(info);
		final String orderInfo = info;

		new Thread() {
			public void run() {
				PayTask alipay = new PayTask(mActivity);
				String result = alipay.pay(orderInfo);
				mAlipayHandler.obtainMessage(0, result).sendToTarget();
			}
		}.start();

	}

	private String getSignedOrderInfo(@NonNull String info) {
		String sign = Rsa.sign(info, Keys.RSA_PRIVATE);
		try {
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return info + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
	}

	private String getOrderInfo(String out_trade_no, String total_fee, String subject, String body) {
		StringBuilder sb = new StringBuilder();

		sb.append("out_trade_no=").append("\"").append(out_trade_no).append("\"");// 商户网站唯一订单号
		if (MyConstant.SERVICENAME.equals("http://www.7jia2.org")) {
			sb.append("&total_fee=").append("\"").append(total_fee).append("\""); // 商品金额
			sb.append("&notify_url=").append("\"").append("http://oapi.7jia2.org/tpay/pay-.ntmb").append("\""); // 服务器异步通知页面路径，目前的sdk版本已经不需要做URL编码
		} else {
			sb.append("&total_fee=").append("\"").append(total_fee).append("\""); // 商品金额
			sb.append("&notify_url=").append("\"").append("http://www.7jia2.com/pay/pay-.ntmb").append("\""); // 服务器异步通知页面路径，目前的sdk版本已经不需要做URL编码
		}
		sb.append("&subject=").append("\"").append(subject).append("\"");// 商品名称
		sb.append("&body=").append("\"").append(body).append("\"");// 商品详情
		sb.append("&partner=").append("\"").append(Keys.APPID).append("\""); // 合作者身份ID
		sb.append("&seller_id=").append("\"").append(Keys.SELLER).append("\""); // 卖家支付宝账号
		sb.append("&service=\"mobile.securitypay.pay\"");// 接口名称， 固定值
		sb.append("&payment_type=\"1\""); // 支付类型， 固定值1（商品购买）
		sb.append("&_input_charset=\"UTF-8\"");// 参数的字符编码， 固定值

		return new String(sb);
	}

}
