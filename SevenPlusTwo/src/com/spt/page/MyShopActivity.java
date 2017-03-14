package com.spt.page;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MyTitleBar;
import com.spt.dialog.ChangeAddressDialog;
import com.spt.dialog.ChangeAddressDialog.OnAddressCListener;
import com.spt.sht.R;
import com.spt.utils.AsynImageLoader;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpPostService;
import com.spt.utils.MyUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 【我的店铺】页
 * */
public class MyShopActivity extends BaseActivity {
	private MyTitleBar mtbShopTitle;
	private TextView tvTitle;
	private ImageView ivLeft;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private Intent iPostRequest; // post方法请求
	private boolean isPostServiceRunning = false;
	private BroadcastReceiver brPostHttp; // post方法广播
	private Intent itFrom;
	private TextView tvShopName;
	private TextView tvPlace;
	private ImageView ivPhoto;
	private TextView tvAdress;
	private TextView tvTel;
	private TextView tvDescription;
	private Button btnSave;
	private String regionId;
	private String telNo;
	private HashMap<String, Object> param;
	private AlertDialog alert;
	private String photoName;
	private Bitmap bitmap;
	private LinearLayout llShopName;
	private AlertDialog dialog;
	private int flag = 0;// (1店铺名称，2详细地址，3联系电话)
	private View dialogContentView;
	private TextView tvContentTitle;
	private EditText etContent;
	private TextView tvContentCancel;
	private TextView tvContentOk;
	private String editing_store_name;
	@SuppressLint("HandlerLeak")
	private Handler uploadFile_handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				// String result = msg.obj.toString();
				MyUtil.ToastMessage(MyShopActivity.this, "图片上传成功");
//				success_flag = "1";
			} else {
				MyUtil.ToastMessage(MyShopActivity.this, "上传失败");
//				success_flag = "0";
			}
		}

	};
//	private String success_flag;// 0失败，1成功

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.shop);
		super.onCreate(savedInstanceState);
		try {
			parseData();
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
		MyShopActivity.this.unregisterReceiver(brPostHttp);
		if (isPostServiceRunning) {
			stopService(iPostRequest);
			isPostServiceRunning = false;
		}
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			if ("1".equals(success_flag)) {
//				setResult(MyConstant.RESULTCODE_36);
//				success_flag = "0";
//				MyShopActivity.this.finish();
//			} else if ("0".equals(success_flag)) {
//				success_flag = "0";
//				MyShopActivity.this.finish();
//			}
			
			setResult(MyConstant.RESULTCODE_36);
			MyShopActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void init() {
		this.itFrom = getIntent();
		this.param = new HashMap<String, Object>();
		this.mtbShopTitle = (MyTitleBar) findViewById(R.id.mtb_shop_title);
		this.tvTitle = mtbShopTitle.getTvTitle();
		this.ivLeft = mtbShopTitle.getIvLeft();
		this.tvTitle.setText("我的店铺");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbShopTitle.getLlLeft();
		this.llRight = mtbShopTitle.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.iPostRequest = new Intent(MyShopActivity.this, MyHttpPostService.class); // 启动POST服务Intent对象
		this.iPostRequest.setAction(MyConstant.HttpPostServiceAciton); // 设置POSTAction
		this.brPostHttp = new MyBroadCastReceiver(); // POST广播对象
		this.tvShopName = (TextView) findViewById(R.id.tv_shop_shopNameShow);
		this.tvPlace = (TextView) findViewById(R.id.tv_shop_placeShow);
		this.ivPhoto = (ImageView) findViewById(R.id.iv_shop_photo);
		this.tvAdress = (TextView) findViewById(R.id.tv_shop_adressShow);
		this.tvTel = (TextView) findViewById(R.id.tv_shop_telShow);
		this.tvDescription = (TextView) findViewById(R.id.tv_shop_descriptionShow);
		this.btnSave = (Button) findViewById(R.id.btn_shop_save);
		this.llShopName = (LinearLayout) findViewById(R.id.ll_shop_shopName);
		this.dialog = new AlertDialog.Builder(MyShopActivity.this).create();
		this.dialogContentView = LayoutInflater.from(MyShopActivity.this).inflate(R.layout.shopalertitem, null);
		this.tvContentTitle = (TextView) dialogContentView.findViewById(R.id.tv_alertItem_title);
		this.etContent = (EditText) dialogContentView.findViewById(R.id.et_alertItem_content);
		this.tvContentCancel = (TextView) dialogContentView.findViewById(R.id.tv_alertItem_cancel);
		this.tvContentOk = (TextView) dialogContentView.findViewById(R.id.tv_alertItem_ok);
//		this.success_flag = "0";
	}

	@Override
	protected void addClickEvent() {

		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if ("1".equals(success_flag)) {
//					setResult(MyConstant.RESULTCODE_36);
//					success_flag = "0";
//					MyShopActivity.this.finish();
//				} else if ("0".equals(success_flag)) {
//					success_flag = "0";
//					MyShopActivity.this.finish();
//				}
				setResult(MyConstant.RESULTCODE_36);
				MyShopActivity.this.finish();
			}
		});
		// 【保存】按钮
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				postMyShopData();// 上传数据
			}
		});
		// 【所在地区】按钮
		tvPlace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View v) {
				ChangeAddressDialog mChangeAddressDialog = new ChangeAddressDialog(MyShopActivity.this);
				mChangeAddressDialog.setAddress("天津", "天津市", "南开区");
				mChangeAddressDialog.show();
				mChangeAddressDialog.setAddresskListener(new OnAddressCListener() {

					@Override
					public void onClick(String province, String city, String country) {
						String provinces[] = province.split(" ");
						String citys[] = city.split(" ");
						String countrys[] = country.split(" ");
						regionId = countrys[1];
						tvPlace.setText(provinces[0] + " " + citys[0] + " " + countrys[0]);
						System.out.println("城市     " + province + "-" + city + "-" + country);
						System.out.println("regionId     " + regionId);
					}
				});
			}
		});
		// 【头像】
		ivPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LayoutInflater inflater = LayoutInflater.from(MyShopActivity.this);
				View view = inflater.inflate(R.layout.shopitem, null);
				TextView tvGallery = (TextView) view.findViewById(R.id.tv_shopItem_gallery);
				TextView tvCamara = (TextView) view.findViewById(R.id.tv_shopItem_carama);

				tvGallery.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						photoName = "";
						// 激活系统图库，选择一张图片
						Intent intent = new Intent(Intent.ACTION_PICK);
						intent.setType("image/*");
						startActivityForResult(intent, MyConstant.RESULTCODE_26);
						alert.dismiss();
					}
				});

				tvCamara.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						photoName = MyUtil.getCurrentDate() + ".jpg";
						Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
						// 判断存储卡是否可以用，可用进行存储
						if (hasSdcard()) {
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
									.getExternalStorageDirectory() + "/DCIM/Camera/", photoName)));
						}
						startActivityForResult(intent, MyConstant.RESULTCODE_27);
						alert.dismiss();
					}
				});

				alert = new AlertDialog.Builder(MyShopActivity.this).setView(view).create();
				alert.show();
			}
		});

		// 【店铺名称】
		llShopName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ("".equals(editing_store_name) || "null".equals(editing_store_name)) {
					flag = 1;
					showAlert(tvShopName.getText().toString());
				} else {
					MyUtil.ToastMessage(MyShopActivity.this, "店铺名称正在审核中");
				}

			}
		});

		// 【详细地址】
		tvAdress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				flag = 2;
				showAlert(tvAdress.getText().toString());
			}
		});

		// 【联系电话】
		tvTel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				flag = 3;
				showAlert(tvTel.getText().toString());
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case MyConstant.RESULTCODE_26:// 选图片
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}
			break;
		case MyConstant.RESULTCODE_27:// 拍照片
			if (hasSdcard()) {
				File tempFile = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera/", photoName);
				crop(Uri.fromFile(tempFile));
			} else {
				MyUtil.ToastMessage(MyShopActivity.this, "未找到存储卡，无法存储照片！");
			}
			break;
		case MyConstant.RESULTCODE_28:// 剪切图片
			if (data != null && data.hasExtra("data")) {
				bitmap = data.getParcelableExtra("data");
				this.ivPhoto.setImageBitmap(bitmap);
				photoName = MyUtil.getCurrentDate() + ".jpg";
				saveFile(photoName);// 保存图片
				postMyShopPhoto();// 自动上传图片
			}
			break;
		}
	}

	/**
	 * 点击弹出编辑对话框
	 * 
	 * */
	private void showAlert(String str) {
		switch (flag) {
		case 1: // 店铺名称
			tvContentTitle.setText("编辑店铺名称");
			etContent.setInputType(InputType.TYPE_CLASS_TEXT);
			etContent.setText("");
			etContent.setText(str);
			break;
		case 2: // 详细地址
			tvContentTitle.setText("编辑地址");
			etContent.setInputType(InputType.TYPE_CLASS_TEXT);
			etContent.setText("");
			etContent.setText(str);
			break;
		case 3: // 联系电话
			tvContentTitle.setText("编辑电话");
			etContent.setInputType(InputType.TYPE_CLASS_TEXT);
			etContent.setText("");
			etContent.setText(str);
			break;
		}
		// 取消
		tvContentCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		// 确定
		tvContentOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (flag) {
				case 1: // 店铺名称
					String str = etContent.getText().toString();
					tvShopName.setText(str);
					dialog.dismiss();
					break;
				case 2: // 详细地址
					String str1 = etContent.getText().toString();
					tvAdress.setText(str1);
					dialog.dismiss();
					break;
				case 3: // 联系电话
					String str2 = etContent.getText().toString();
					if (MyUtil.isPhoneNum(str2)) {
						tvTel.setText(str2);
					} else {
						MyUtil.ToastMessage(MyShopActivity.this, "电话由数字，加号，减号，空格，括号组成，并不能少于6位");
					}
					dialog.dismiss();
					break;
				}
			}
		});

		dialog.setView(dialogContentView);
		dialog.show();
	}

	/**
	 * 解析页面数据
	 * */
	private void parseData() throws JSONException {
		String data = itFrom.getStringExtra("data");
		JSONTokener jsonParser = new JSONTokener(data);
		JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
		String store_logo = jsonReturn.getString("store_logo");
		String store_name = jsonReturn.getString("store_name");
		String region_name = jsonReturn.getString("region_name");
		String region_id = jsonReturn.getString("region_id");
		String address = jsonReturn.getString("address");
		telNo = jsonReturn.getString("tell");
		String description = jsonReturn.getString("description");
		editing_store_name = jsonReturn.getString("editing_store_name");

		System.out.println("region_name   " + region_name);
		System.out.println("region_id   " + region_id);

		AsynImageLoader asynImageLoader = new AsynImageLoader();
		asynImageLoader.showImageAsyn(ivPhoto, store_logo, R.drawable.test140140);
		tvShopName.setText(store_name);
		try {
			tvPlace.setText(MyUtil.encodeStr(region_name));
		} catch (Exception e) {
			e.printStackTrace();
		}
		tvAdress.setText(address);
		if ("".equals(telNo) || "null".equals(telNo)) {
			tvTel.setText("无");
		} else {
			tvTel.setText(telNo);
		}
		tvDescription.setText(description);
		regionId = region_id;
	}

	/**
	 * 是否安装sd卡
	 * */
	private boolean hasSdcard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 剪切图片
	 * 
	 * @param uri
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, MyConstant.RESULTCODE_28);
	}

	/**
	 * 注册广播
	 * */
	private void registMyBroadcastRecevier() {
		IntentFilter filterPostHttp = new IntentFilter();
		filterPostHttp.addAction(MyConstant.HttpPostServiceAciton);
		MyShopActivity.this.registerReceiver(brPostHttp, filterPostHttp);
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
	 * */
	private void parseDataPost(String type, String jsonStr) throws JSONException {
		if ("myShop_saveData".equals(type)) {
			JSONTokener jsonParser = new JSONTokener(jsonStr);
			JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
			String error = jsonReturn.getString("error");
			if ("0".equals(error)) {
				MyUtil.ToastMessage(MyShopActivity.this, "保存成功");
				setResult(MyConstant.RESULTCODE_36);
//				success_flag = "0";
				MyShopActivity.this.finish();
			} else {
				String msg = jsonReturn.getString("msg");
//				success_flag = "0";
				MyUtil.ToastMessage(MyShopActivity.this, msg);
			}
		}
	}

	/**
	 * 上传【我的店铺】数据
	 */
	private void postMyShopData() {
		String shopName = tvShopName.getText().toString();
		String place = tvPlace.getText().toString();
		String adress = tvAdress.getText().toString();
		String tel = tvTel.getText().toString();
		param.put("token", itFrom.getStringExtra("token"));
		param.put("store_name", shopName);
		param.put("tell", tel);
		if ("".equals(adress)) {
			if (param.containsKey("address")) {
				param.remove("address");
			}
		} else {
			param.put("address", adress);
		}
		param.put("region_name", place);
		param.put("region_id", regionId);
		String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_store&act=save_store_info";
		String type = "myShop_saveData";
		iPostRequest.putExtra("uri", uri);
		iPostRequest.putExtra("param", param);
		iPostRequest.putExtra("type", type);
		startService(iPostRequest);
		isPostServiceRunning = true;
		param.clear();
	}

	/**
	 * 上传【我的店铺头像】数据
	 */
	private void postMyShopPhoto() {

		new Thread() {

			@Override
			public void run() {
				String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=seller_store&act=save_store_logo";
				param.put("token", itFrom.getStringExtra("token"));
				param.put("store_logo", tvShopName.getText().toString());
				try {
					uploadFile(uri, param, uploadFile_handler);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();

	}

	private void saveFile(String fileName) {
		String path = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/";
		File dirFile = new File(path);
		dirFile.mkdir();
		File myCaptureFile = new File(path + fileName);
		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private void uploadFile(String uploadUrl, HashMap<String, Object> map)
	// throws Exception {
	// final String IMGUR_CLIENT_ID = "...";
	// final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
	// final OkHttpClient client = new OkHttpClient();
	// String path = Environment.getExternalStorageDirectory().getPath() +
	// "/revoeye/";
	// String fileName = "test3.png";
	// RequestBody requestBody = new MultipartBuilder()
	// .type(MultipartBuilder.FORM)
	// .addPart(Headers.of("Content-Disposition", "form-data; name=\"token\""),
	// RequestBody.create(null, itFrom.getStringExtra("token")))
	// .addPart(Headers.of("Content-Disposition",
	// "form-data; name=\"region_id\""),
	// RequestBody.create(null, regionId))
	// .addPart(Headers.of("Content-Disposition",
	// "form-data; name=\"store_name\""),
	// RequestBody.create(null, etShopName.getText().toString()))
	// .addPart(Headers.of("Content-Disposition", "form-data; name=\"tell\""),
	// RequestBody.create(null, etTel.getText().toString()))
	// .addPart(Headers.of("Content-Disposition",
	// "form-data; name=\"region_name\""),
	// RequestBody.create(null, tvPlace.getText().toString()))
	// .addPart(Headers.of("Content-Disposition",
	// "form-data; name=\"address\""),
	// RequestBody.create(null, etAdress.getText().toString()))
	// .addPart(Headers.of("Content-Disposition",
	// "form-data; name=\"store_logo\""),
	// RequestBody.create(MEDIA_TYPE_PNG, new File(path + fileName))).build();
	// Request request = new Request.Builder().header("Authorization",
	// "Client-ID " + IMGUR_CLIENT_ID).url(uploadUrl)
	// .post(requestBody).build();
	// Response response = client.newCall(request).execute();
	// if (response.isSuccessful()) {
	// String responseUrl = response.body().string();
	// System.out.println("response   " + responseUrl);
	// } else {
	// throw new IOException("Unexpected code " + response);
	// }
	// }
	private void uploadFile(String uploadUrl, HashMap<String, Object> map, Handler handler) throws Exception {
		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "******";
		String srcPath = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + photoName;
		System.out.println("   &&&   " + srcPath);
		String token = itFrom.getStringExtra("token");
		// System.out.println(token);
		// String store_name =
		// MyUtil.encodeStr(tvShopName.getText().toString());
		// String tell = tvTel.getText().toString();
		// String region_name = MyUtil.encodeStr(tvPlace.getText().toString());
		// String address = MyUtil.encodeStr(tvAdress.getText().toString());

		try {
			URL url = new URL(uploadUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			// 设置每次传输的流大小，可以有效防止手机因为内存不足崩溃
			// 此方法用于在预先不知道内容长度时启用没有进行内部缓冲的 HTTP 请求正文的流。
			httpURLConnection.setChunkedStreamingMode(128 * 1024);// 128K
			// 允许输入输出流
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			// 使用POST方法
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			httpURLConnection.setRequestProperty("Charset", "UTF-8");
			httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			// 建立tcp连接
			DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
			// 将请求体写入内存中
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"store_logo\"; filename=\""
					+ srcPath.substring(srcPath.lastIndexOf("/") + 1) + "\"" + end);
			dos.writeBytes(end);
			FileInputStream fis = new FileInputStream(srcPath);
			byte[] buffer = new byte[8192]; // 8k
			int count = 0;
			// 读取文件
			while ((count = fis.read(buffer)) != -1) {
				dos.write(buffer, 0, count);
			}
			fis.close();
			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);

			// 添加token
			dos.writeBytes(twoHyphens + boundary + end);
			dos.writeBytes("Content-Disposition: form-data; name=\"token\"" + end);
			dos.writeBytes("Content-Type: text/plain; charset=UTF-8" + end);
			dos.writeBytes("Content-Length: " + token.length() + end);
			dos.writeBytes(end);
			dos.writeBytes(token);
			dos.writeBytes(end);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + end);


			dos.flush();// 关闭流，自动生成请求体
			InputStream is = httpURLConnection.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			System.out.println("result  +++  " + result);

			Message msg = new Message();
			msg.obj = result;
			msg.what = 0;
			handler.sendMessage(msg);

			dos.close();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			setTitle(e.getMessage());
		}
	}

	/**
	 * 旋转照片
	 * */
	private Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 读取照片角度
	 * */
	private int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
