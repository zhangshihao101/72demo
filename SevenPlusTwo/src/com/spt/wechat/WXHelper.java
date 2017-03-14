package com.spt.wechat;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 微信支付
 */
public class WXHelper {
	public static final String APP_ID = "wxe879ddc23a9986ad";

	public static IWXAPI registeAppId(Context context) {
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
		msgApi.registerApp(APP_ID);

		return msgApi;
	}

	public static PayReq payReq(WXPayModel model) {
		PayReq request = new PayReq();
		request.appId = model.getAppId();
		request.partnerId = model.getPartnerId();
		request.prepayId = model.getPrepayId();
		request.packageValue = model.getPackageStr();
		request.nonceStr = model.getNoncestr();
		request.timeStamp = model.getTimestamp();
		request.sign = model.getSign();

		return request;
	}

	/**
	 * 连接WIFI状态下获取IP地址
	 *
	 * @param context
	 */
	public static String getWiFiIp(Context context) {
		String ip = "";
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		ip = intToIp(ipAddress);

		return ip;
	}

	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						// if (!inetAddress.isLoopbackAddress() && inetAddress
						// instanceof Inet6Address) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreferenceIpAddress", "");
		}
		return null;
	}
}
