package com.spt.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.widget.Toast;

import com.spt.sht.R;

public class MyUtil {
	
	/**
	 * 判断是否是银行卡号
	 * 
	 * @param cardId
	 * @return
	 */
	public static boolean checkBankCard(String cardId) {
		char bit = getBankCardCheckCode(cardId.substring(0, cardId.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return cardId.charAt(cardId.length() - 1) == bit;

	}

	private static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * 界面提示信息
	 * */
	public static void ToastMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 验证手机登录
	 * */
	public static boolean isMobile(String strPhone) {
		Pattern pattern = Pattern.compile("1[0-9]{10}");
		Matcher matcher = pattern.matcher(strPhone);

		return matcher.matches();
	}
	
	/**
	 * 验证电话号码
	 * */
	public static boolean isPhoneNum(String strPhone) {
		Pattern pattern = Pattern.compile("[0-9]{6,}");//验证最少6位数字
		Matcher matcher = pattern.matcher(strPhone);
		if (!matcher.matches()) {
			return false;
		}
		//验证 如果含有加号则，以"+"开头
		if (strPhone.contains("+")) {
			Pattern pattern1 = Pattern.compile("^[+]");//验证以加号为开头
			Matcher matcher1 = pattern1.matcher(strPhone);
			if (!matcher1.matches()) {
				return false;
			}
		}
		//验证可以包含"-, ,()"，不能有其他符号
		if (strPhone.contains("*")) {
			return false;
		} else if (strPhone.contains("#")) {
			return false;
		} else if (strPhone.contains("@")) {
			return false;
		} else if (strPhone.contains("!")) {
			return false;
		} else if (strPhone.contains("?")) {
			return false;
		} else if (strPhone.contains("<")) {
			return false;
		} else if (strPhone.contains(">")) {
			return false;
		} else if (strPhone.contains("%")) {
			return false;
		} else if (strPhone.contains("^")) {
			return false;
		} else if (strPhone.contains("$")) {
			return false;
		} else if (strPhone.contains("#")) {
			return false;
		} else if (strPhone.contains("=")) {
			return false;
		} else if (strPhone.contains("~")) {
			return false;
		} else if (strPhone.contains(".")) {
			return false;
		} else if (strPhone.contains(",")) {
			return false;
		} else if (strPhone.contains(":")) {
			return false;
		} else if (strPhone.contains(";")) {
			return false;
		} else if (strPhone.contains("/")) {
			return false;
		} else if (strPhone.contains("《")) {
			return false;
		} else if (strPhone.contains("》")) {
			return false;
		} else if (strPhone.contains("。")) {
			return false;
		} else if (strPhone.contains("，")) {
			return false;
		} else if (strPhone.contains("——")) {
			return false;
		} else if (strPhone.contains("、")) {
			return false;
		} else if (strPhone.contains("|")) {
			return false;
		}
		
		return true;
	}

	/**
	 * 验证邮箱登陆
	 * */
	public static boolean isEmail(String strEmail) {
		String strPattern = "^//s*//w+(?://.{0,1}[//w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*//.[a-zA-Z]+//s*$";
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(strEmail);

		return matcher.matches();

	}

	/**
	 * 验证订单名称是否合法
	 * */
	public static boolean chkOrderName(String name) {
		String strPattern = "[0-9]{11}";
		Pattern pattern = Pattern.compile(strPattern);
		Matcher matcher = pattern.matcher(name);

		return matcher.matches();
	}

	/**
	 * base64加密
	 * */
	public static String stringToBase64(String str) {
		String strBase64 = new String(Base64.encode(str.getBytes(), Base64.NO_WRAP));

		return strBase64;
	}

	/**
	 * base64解密
	 * */
	public static String base64ToString(String strBase64) {
		String strDecode = new String(Base64.decode(strBase64.getBytes(), Base64.NO_WRAP));

		return strDecode;
	}

	/**
	 * 判断输入是否为空
	 * */
	public static boolean isInputNull(String input) {
		if ("".equals(input)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 毫秒转日期
	 * */
	@SuppressLint("SimpleDateFormat")
	public static String millisecondsToStr(String str) {
		String returnDate = "";
		if (!"0".equals(str)) {
			long sd = Long.parseLong(str);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(sd * 1000);
			StringBuilder sb = new StringBuilder();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int maxDayOfTheMonth = getMonthLastDay(year, month);
			int day = cal.get(Calendar.DATE);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			hour = hour + 8;
			if (hour >= 24) {
				hour = hour - 24;
				day = day + 1;
			}
			if (day > maxDayOfTheMonth) {
				day = day - maxDayOfTheMonth;
				month = month + 1;
			}
			if (month > 12) {
				month = month - 12;
				year = year + 1;
			}
			sb.append(year).append("-");
			sb.append(month).append("-");
			sb.append(day).append(" ");
			sb.append(hour).append(":");
			sb.append(cal.get(Calendar.MINUTE)).append(":");
			sb.append(cal.get(Calendar.SECOND));
			String time = sb.toString();
			String a[] = time.split(" ");
			String b[] = a[0].split("-");
			String c[] = a[1].split(":");
			returnDate = b[0] + "-" + add0(b[1]) + "-" + add0(b[2]) + " " + add0(c[0]) + ":" + add0(c[1]) + ":"
					+ add0(c[2]);
		}
		return returnDate;
	}
	
	/**
	 * 毫秒转日期不加8小时
	 * */
	@SuppressLint("SimpleDateFormat")
	public static String millisecondsToDate(String str) {
		String returnDate = "";
		if (!"0".equals(str)) {
			long sd = Long.parseLong(str);
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(sd * 1000);
			StringBuilder sb = new StringBuilder();
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int maxDayOfTheMonth = getMonthLastDay(year, month);
			int day = cal.get(Calendar.DATE);
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			if (hour >= 24) {
				hour = hour - 24;
				day = day + 1;
			}
			if (day > maxDayOfTheMonth) {
				day = day - maxDayOfTheMonth;
				month = month + 1;
			}
			if (month > 12) {
				month = month - 12;
				year = year + 1;
			}
			sb.append(year).append("-");
			sb.append(month).append("-");
			sb.append(day).append(" ");
			sb.append(hour).append(":");
			sb.append(cal.get(Calendar.MINUTE)).append(":");
			sb.append(cal.get(Calendar.SECOND));
			String time = sb.toString();
			String a[] = time.split(" ");
			String b[] = a[0].split("-");
			String c[] = a[1].split(":");
			returnDate = b[0] + "-" + add0(b[1]) + "-" + add0(b[2]) + " " + add0(c[0]) + ":" + add0(c[1]) + ":"
					+ add0(c[2]);
		}
		return returnDate;
	}

	/**
	 * 得到指定月的天数
	 * */
	public static int getMonthLastDay(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month - 1);
		a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	/**
	 * 日期转毫秒
	 * */
	@SuppressLint("SimpleDateFormat")
	public static long strToMilliseconds(String date) {

		long returnDate = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(format.parse(date));
			StringBuilder sb = new StringBuilder();
			sb.append(cal.get(Calendar.YEAR)).append("-");
			sb.append(cal.get(Calendar.MONTH) + 1).append("-");
			sb.append(cal.get(Calendar.DATE));
			returnDate = format.parse(sb.toString()).getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("returnDate  " + returnDate);
		return returnDate;
	}

	private static String add0(String str) {
		if (Integer.parseInt(str) < 10) {
			str = "0" + str;
		}
		return str;
	}

	/**
	 * 处理null字符串
	 * 
	 * @param str
	 *            待处理的字符串
	 * @return 如果字符串为null或者""，则返回"无"
	 * */
	public static String dealNullString(String str) {
		String returnStr = "";
		if ("".equals(str) || "null".equals(str)) {
			returnStr = "无";
		} else if ("0".equals(str)) {
			returnStr = "无";
		} else {
			returnStr = str;
		}

		return returnStr;
	}

	/**
	 * 订单状态值转换
	 * */
	public static String codeToString(String str) {
		if ("11".equals(str)) {
			str = "待付款";
		} else if ("20".equals(str)) {
			str = "待发货";
		} else if ("30".equals(str)) {
			str = "已发货";
		} else if ("31".equals(str)) {
			str = "部分发货";
		} else if ("40".equals(str)) {
			str = "已完成";
		} else if ("50".equals(str)) {
			str = "已退款";
		} else if ("0".equals(str)) {
			str = "已取消";
		} else if ("normal".equals(str)) {
			str = "商城";
		} else if ("tuan".equals(str)) {
			str = "团购";
		}

		return str;
	}

	/**
	 * 根据一个网络连接(String)获取bitmap图像
	 * 
	 * @param imageUri
	 * @return
	 * @throws IOException
	 */
	public static Bitmap getbitmap(String imageUri) {
		// 显示网络上的图片
		Bitmap bitmap = null;
		try {
			URL myFileUrl = new URL(imageUri);
			HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;

	}

	/**
	 * MD5加密
	 * 
	 * @param 待加密的字符串
	 * @return 加密后的字符串
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
	public static String getMD5(String info) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(info.getBytes("UTF-8"));
			byte[] encryption = md5.digest();

			StringBuffer strBuf = new StringBuffer();
			for (int i = 0; i < encryption.length; i++) {
				if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
					strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
				} else {
					strBuf.append(Integer.toHexString(0xff & encryption[i]));
				}
			}

			return strBuf.toString();
		} catch (NoSuchAlgorithmException e) {
			return "";
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	/**
	 * 获取拼接的图片url
	 * 
	 * @param path
	 *            获取的图片路径
	 * @param width
	 *            设定图片的宽度
	 * @param height
	 *            设定图片的高度
	 * @return 完整的图片路径
	 */
	public static String getImageURL(String path, String width, String height) {
		String md5Path = getMD5(path);
		String base64Path = stringToBase64(path);
		String url = "http://autoimg.7jia2.com/ecmme/lvyoumall/thumbimg/" + md5Path.substring(0, 1) + "/"
				+ md5Path.substring(3, 4) + "/" + width + "/" + height + "/32/" + base64Path + ".auto.jpg";
		return url;
	}

	/**
	 * 获取服务器上的文件
	 * 
	 * @param path
	 *            获取的apk路径
	 * @param pd
	 *            设定进度条
	 * @return 服务器上面的apk
	 */
	public static File getFileFromServer(String path, ProgressDialog pd) throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File file = new File(Environment.getExternalStorageDirectory(), "SevenPlusTwo.apk");
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			return null;
		}

	}

	/**
	 * 部分改变字体颜色
	 * 
	 * @param context
	 * 
	 * @param style
	 *            待改变的字符串
	 * @param oldState
	 *            改变前状态
	 * @param newState
	 *            改变后状态
	 * 
	 * @return 改变颜色后的字符串
	 */
	public static SpannableString changePartOfStringColor(Context context, SpannableString style, String state) {
		String str = style.toString();
		int index = str.indexOf(state);
		int length = state.length();
		int color = stateColor(context, state);
		style.setSpan(new ForegroundColorSpan(color), index, index + length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return style;
	}

	/**
	 * 根据状态判断颜色值
	 * 
	 * @param context
	 * @param state
	 *            订单状态
	 * 
	 * @return 颜色值
	 */
	public static int stateColor(Context context, String state) {
		// if ("待付款".equals(state)) {
		// return context.getResources().getColor(R.color.waitPay);
		// } else if ("待发货".equals(state)) {
		// return context.getResources().getColor(R.color.waitSend);
		// } else if ("已取消".equals(state)) {
		// return context.getResources().getColor(R.color.cancelPay);
		// } else if ("已发货".equals(state)) {
		// return context.getResources().getColor(R.color.sended);
		// } else if ("已完成".equals(state)) {
		// return context.getResources().getColor(R.color.complete);
		// } else if ("已退款".equals(state)) {
		// return context.getResources().getColor(R.color.returnPay);
		// } else {
		// return Color.BLACK;
		// }

		if ("待付款".equals(state)) {
			return context.getResources().getColor(R.color.waitSend);
		} else if ("待发货".equals(state)) {
			return context.getResources().getColor(R.color.sended);
		} else {
			return context.getResources().getColor(R.color.other);
		}
	}

	/**
	 * 判断字符串中是否含有html标签
	 * 
	 * @param str
	 *            待检测的字符串
	 * 
	 */
	public static boolean isContainHTML(String str) {
		String strPattern = "<\\s*font\\s+([^>]*)\\s*>";
		Pattern pattern = Pattern.compile(strPattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	/**
	 * 文字转码，防止上传中文出现乱码的情况
	 * 
	 * @param value
	 *            待转换的字符串
	 * 
	 */
	public static String encodeStr(String value) throws Exception {
		return new String(value.getBytes(), "UTF-8");
	}

	/**
	 * 获取当前时间
	 * 
	 * @return currentDate 当前时间
	 * 
	 */
	public static String getCurrentDate() {
		String currentDate = "";
		Calendar calendar = Calendar.getInstance();
		String year = coverWith0(calendar.get(Calendar.YEAR));
		String month = coverWith0(calendar.get(Calendar.MONTH) + 1);
		String date = coverWith0(calendar.get(Calendar.DATE));
		String hour = coverWith0(calendar.get(Calendar.HOUR_OF_DAY));
		String minute = coverWith0(calendar.get(Calendar.MINUTE));
		String second = coverWith0(calendar.get(Calendar.SECOND));
		currentDate = year + month + date + hour + minute + second;

		return currentDate;
	}

	/**
	 * 日期转换<br>
	 * 如果是个位数则在该数前面补0,否则返回当前数
	 * 
	 * @return str 当前时间
	 * 
	 */
	private static String coverWith0(int number) {
		String str = "";
		if (number < 10) {
			str = "0" + String.valueOf(number);
		} else {
			str = String.valueOf(number);
		}
		return str;
	}
}
