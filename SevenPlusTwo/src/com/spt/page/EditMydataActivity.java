package com.spt.page;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.spt.controler.CircleImageView;
import com.spt.controler.SelectPicPopupWindow;
import com.spt.dialog.MtsChangeAddressDialog;
import com.spt.dialog.MtsChangeAddressDialog.OnAddressCListener;
import com.spt.sht.R;
import com.spt.utils.ImageUtils;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditMydataActivity extends FragmentActivity implements OnClickListener {

	private ImageView iv_edit_back, iv_edit_finish, iv_line1;
	private CircleImageView civ_header;
	private TextView tv_name_show, tv_no_show, tv_gender_show, tv_position_show, tv_flag_show, tv_intro_show;
	private RelativeLayout rl_my_header, rl_my_name, rl_my_no, rl_my_gender, rl_my_qr, rl_my_position, rl_my_flag,
			rl_my_intro;
	private LinearLayout ll_hole;
	private String objIndividual = "";
	public static final int RESULT_ENTER = 11;
	public static final int RESULT_NEGATIVE = 12;
	public static final int EDIT_NAME = 20;
	public static final int EDIT_INTRO = 21;
	public static final int EDIT_GENDER = 31;
	public static final int EDIT_FLAG = 41;

	/**
	 * 自定义的PopupWindow
	 */
	private SelectPicPopupWindow menuWindow;
	/**
	 * 选择图片的返回码
	 */
	public static final int SELECT_IMAGE_RESULT_CODE = 200;
	/**
	 * 当前选择的图片的路径
	 */
	public String mImagePath;

	public File file;

	private SharedPreferences sp;
	private Editor editor;

	private String provinceId = "", cityId = "", provinceIdF = "", cityIdF = "";

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_mydata);
		super.onCreate(arg0);

		initViews();

		Intent intent = getIntent();
		objIndividual = intent.getStringExtra("personData");

		initDatas();

	}

	private void initDatas() {
		try {
			JSONObject obj = new JSONObject(objIndividual);
			JSONObject objIndi = obj.optJSONObject("individualInfor");
			Picasso.with(EditMydataActivity.this).load(objIndi.optString("logoPath")).into(civ_header);
			tv_name_show.setText(
					objIndi.optString("connectionName").equals("null") ? "" : objIndi.optString("connectionName"));
			tv_no_show.setText(objIndi.optString("userLoginId"));

			tv_gender_show.setText(objIndi.optString("connectionSex").equals("null") ? "暂无"
					: objIndi.optString("connectionSex").equals("1") ? "男" : "女");
			String position = objIndi.optString("provinceName") + " " + objIndi.optString("cityName");

			tv_position_show.setText("".equals(position) ? "暂无" : position);

			provinceId = "null".equals(objIndi.optString("provinceId")) ? "" : objIndi.optString("provinceId");
			cityId = "null".equals(objIndi.optString("cityId")) ? "" : objIndi.optString("provinceId");

			String ss = "";
			if (objIndi.optString("connectionRole").equals("Leader")) {
				ss = "领队";
			} else if (objIndi.optString("connectionRole").equals("Club")) {
				ss = "俱乐部";
			} else if (objIndi.optString("connectionRole").equals("MassOrganizations")) {
				ss = "社团";
			} else if (objIndi.optString("connectionRole").equals("WebShopOwner")) {
				ss = "网店店主";
			} else if (objIndi.optString("connectionRole").equals("StoreOwner")) {
				ss = "实体店店主";
			} else if (objIndi.optString("connectionRole").equals("Other")) {
				ss = "其他";
			}

			tv_flag_show.setText(objIndi.optString("connectionRole").equals("null") ? "暂无" : ss);
			tv_intro_show.setText(objIndi.optString("individualResume").equals("null") ? "暂无"
					: objIndi.optString("individualResume"));
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void initViews() {
		iv_line1 = (ImageView) findViewById(R.id.iv_line1);
		iv_edit_back = (ImageView) findViewById(R.id.iv_edit_back);
		iv_edit_finish = (ImageView) findViewById(R.id.iv_edit_finish);
		civ_header = (CircleImageView) findViewById(R.id.civ_header);
		tv_name_show = (TextView) findViewById(R.id.tv_name_show);
		tv_no_show = (TextView) findViewById(R.id.tv_no_show);
		tv_gender_show = (TextView) findViewById(R.id.tv_gender_show);
		tv_position_show = (TextView) findViewById(R.id.tv_position_show);
		tv_flag_show = (TextView) findViewById(R.id.tv_flag_show);
		tv_intro_show = (TextView) findViewById(R.id.tv_intro_show);
		rl_my_header = (RelativeLayout) findViewById(R.id.rl_my_header);
		rl_my_name = (RelativeLayout) findViewById(R.id.rl_my_name);
		rl_my_no = (RelativeLayout) findViewById(R.id.rl_my_no);
		rl_my_gender = (RelativeLayout) findViewById(R.id.rl_my_gender);
		rl_my_qr = (RelativeLayout) findViewById(R.id.rl_my_qr);
		rl_my_position = (RelativeLayout) findViewById(R.id.rl_my_position);
		rl_my_flag = (RelativeLayout) findViewById(R.id.rl_my_flag);
		rl_my_intro = (RelativeLayout) findViewById(R.id.rl_my_intro);
		ll_hole = (LinearLayout) findViewById(R.id.ll_hole);

		sp = EditMydataActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
		editor = sp.edit();

		iv_edit_back.setOnClickListener(this);
		iv_edit_finish.setOnClickListener(this);
		rl_my_header.setOnClickListener(this);
		rl_my_name.setOnClickListener(this);
		rl_my_no.setOnClickListener(this);
		rl_my_gender.setOnClickListener(this);
		rl_my_qr.setOnClickListener(this);
		rl_my_position.setOnClickListener(this);
		rl_my_flag.setOnClickListener(this);
		rl_my_intro.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_edit_back:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			setResult(RESULT_NEGATIVE);
			finish();
			break;
		case R.id.iv_edit_finish:
			InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm1 != null) {
				imm1.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			updataInd();

			break;
		case R.id.rl_my_header:
			showPicturePopupWindow();
			break;
		case R.id.rl_my_name:
			Intent intent4 = new Intent();
			intent4.putExtra("title", "修改名字");
			intent4.putExtra("value",
					tv_name_show.getText().toString().equals("无名") ? "" : tv_name_show.getText().toString());
			intent4.setClass(EditMydataActivity.this, EditSomethingActivity.class);
			startActivityForResult(intent4, EDIT_NAME);
			break;
		case R.id.rl_my_no:

			break;
		case R.id.rl_my_gender:
			Intent intent6 = new Intent();
			intent6.putExtra("gender",
					tv_gender_show.getText().toString().equals("暂无") ? "0" : tv_gender_show.getText().toString());
			intent6.setClass(EditMydataActivity.this, ChangeGenderActivity.class);
			startActivityForResult(intent6, EDIT_GENDER);
			break;
		case R.id.rl_my_qr:
			View menu_view = LayoutInflater.from(EditMydataActivity.this).inflate(R.layout.pop_my_code, null);
			ImageView iv_my_qr = (ImageView) menu_view.findViewById(R.id.iv_my_qr);
			Bitmap qrcode = generateQRCode(sp.getString("username", ""));
			iv_my_qr.setImageBitmap(qrcode);
			final PopupWindow menu_pop = new PopupWindow(menu_view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			// 点击外边可让popupwindow消失
			// menu_pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_topbar_add_more_bg));
			menu_pop.setOutsideTouchable(true);
			// 获取焦点，否则无法点击
			menu_pop.setFocusable(true);
			menu_view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (menu_pop.isShowing()) {
						menu_pop.dismiss();
					}
				}
			});
			// 设置popupwindow显示位置
			// menu_pop.showAsDropDown(anchor, xoff, yoff);
			WindowManager wm = (WindowManager) EditMydataActivity.this.getSystemService(Context.WINDOW_SERVICE);

			int width = wm.getDefaultDisplay().getWidth();
			int hight = wm.getDefaultDisplay().getHeight();
			menu_pop.setWidth(width);
			menu_pop.setHeight(hight);

			menu_pop.showAtLocation(ll_hole, Gravity.START, 0, 0);
			// menu_pop.showAsDropDown(ll_hole);

			break;
		case R.id.rl_my_position:
			MtsChangeAddressDialog mChangeAddressDialog = new MtsChangeAddressDialog(EditMydataActivity.this);
			mChangeAddressDialog.setAddress("天津", "南开区");
			mChangeAddressDialog.show();
			mChangeAddressDialog.setAddresskListener(new OnAddressCListener() {

				@Override
				public void onClick(String province, String city) {
					String provinces[] = province.split(" ");
					String citys[] = city.split(" ");
					// ext_region_id_1 = provinces[1];
					// ext_region_id_2 = citys[1];
					// ext_region_id_3 = countrys[1];
					tv_position_show.setText(provinces[0] + " " + citys[0]);
					String id = provinces[1] + " " + citys[1];
					provinceId = provinces[1];
					cityId = citys[1];
					System.out.println("======省市id=====" + id + "=====");
				}
			});
			break;
		case R.id.rl_my_flag:
			Intent intent8 = new Intent();
			intent8.putExtra("flag",
					tv_flag_show.getText().toString().equals("暂无") ? "0" : tv_flag_show.getText().toString());
			intent8.setClass(EditMydataActivity.this, ChosenFlagActivity.class);
			startActivityForResult(intent8, EDIT_FLAG);

			break;
		case R.id.rl_my_intro:
			Intent intent9 = new Intent();
			intent9.putExtra("title", "修改个人简介");
			intent9.putExtra("value",
					tv_intro_show.getText().toString().equals("暂无") ? "" : tv_intro_show.getText().toString());
			intent9.setClass(EditMydataActivity.this, EditSomethingActivity.class);
			startActivityForResult(intent9, EDIT_INTRO);

			break;
		default:
			break;
		}

	}

	String[] proj = { MediaColumns.DATA };

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case EDIT_NAME:
			if (resultCode == 1) {
				tv_name_show.setText(data.getStringExtra("editValue"));
			} else {

			}
			break;

		case EDIT_INTRO:
			if (resultCode == 1) {
				tv_intro_show.setText(data.getStringExtra("editValue"));
			}
			break;
		case EDIT_GENDER:
			if (resultCode == 1) {
				String gen = "";
				if (data.getStringExtra("gender").equals("male")) {
					gen = "男";
				} else if (data.getStringExtra("gender").equals("female")) {
					gen = "女";
				}
				tv_gender_show.setText(gen);
			}
			break;
		case EDIT_FLAG:
			if (resultCode == 1) {
				String flag = "", backF = "";
				backF = data.getStringExtra("flag");

				if (backF.equals("leader")) {
					tv_flag_show.setText("领队");
				} else if (backF.equals("club")) {
					tv_flag_show.setText("俱乐部");
				} else if (backF.equals("mass")) {
					tv_flag_show.setText("社团");
				} else if (backF.equals("web")) {
					tv_flag_show.setText("网店店主");
				} else if (backF.equals("store")) {
					tv_flag_show.setText("实体店店主");
				} else if (backF.equals("other")) {
					tv_flag_show.setText("其他");
				}
			}
			break;
		case SELECT_IMAGE_RESULT_CODE:
			if (resultCode == RESULT_OK) {
				String imagePath = "";
				Uri uri = null;
				if (data != null && data.getData() != null) {
					// 相册
					uri = data.getData();

					Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
					if (cursor == null) {
						uri = ImageUtils.getUri(this, data);
					}
					imagePath = ImageUtils.getFilePathByFileUri(this, uri);
					Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 100, 100);
					civ_header.setImageBitmap(bitmap);
					file = getFileForUrl(uri);
				} else {
					// 拍照
					imagePath = mImagePath;
					Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 100, 100);
					civ_header.setImageBitmap(bitmap);
					file = new File(imagePath);
				}
				long fileSize = file.length();
				if (fileSize > 2 * 1048576) {
					Toast.makeText(EditMydataActivity.this, "您选择的图片不能超过2M", Toast.LENGTH_LONG).show();
				} else {
					uploadPic(sp.getString("username", ""));
				}

			}
			break;
		default:
			break;
		}
	}

	/**
	 * 拍照或从图库选择图片(PopupWindow形式)
	 */
	public void showPicturePopupWindow() {
		menuWindow = new SelectPicPopupWindow(this, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 隐藏弹出窗口
				menuWindow.dismiss();
				switch (v.getId()) {
				case R.id.takePhotoBtn:// 拍照
					takePhoto();
					break;
				case R.id.pickPhotoBtn:// 相册选择图片
					pickPhoto();
					break;
				case R.id.cancelBtn:// 取消
					break;
				default:
					break;
				}
			}
		});
		menuWindow.showAtLocation(findViewById(R.id.ll_hole), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * 拍照获取图片
	 */
	private void takePhoto() {
		// 执行拍照前，应该先判断SD卡是否存在
		String SDState = Environment.getExternalStorageState();
		if (SDState.equals(Environment.MEDIA_MOUNTED)) {
			/**
			 * 通过指定图片存储路径，解决部分机型onActivityResult回调 data返回为null的情况
			 */
			// 获取与应用相关联的路径
			String imageFilePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
			// 根据当前时间生成图片的名称
			String timestamp = "/" + formatter.format(new Date()) + ".jpg";
			File imageFile = new File(imageFilePath, timestamp);// 通过路径创建保存文件
			mImagePath = imageFile.getAbsolutePath();
			Uri imageFileUri = Uri.fromFile(imageFile);// 获取文件的Uri

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);// 告诉相机拍摄完毕输出图片到指定的Uri
			startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE);
		} else {
			Toast.makeText(this, "内存卡不存在!", Toast.LENGTH_LONG).show();
		}
	}

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto() {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE);
	}

	public void updataInd() {
		String gen = "";
		if (tv_gender_show.getText().toString().equals("男")) {
			gen = "1";
		} else if (tv_gender_show.getText().toString().equals("女")) {
			gen = "0";
		}

		String role = "";
		if (tv_flag_show.getText().toString().equals("领队")) {
			role = "Leader";
		} else if (tv_flag_show.getText().toString().equals("俱乐部")) {
			role = "Club";
		} else if (tv_flag_show.getText().toString().equals("社团")) {
			role = "MassOrganizations";
		} else if (tv_flag_show.getText().toString().equals("网店店主")) {
			role = "WebShopOwner";
		} else if (tv_flag_show.getText().toString().equals("实体店店主")) {
			role = "StoreOwner";
		} else if (tv_flag_show.getText().toString().equals("其他")) {
			role = "Other";
		}

		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.update_ind)
						.post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
								.add("accessToken", sp.getString("accessToken", ""))
								.add("connectionName", tv_name_show.getText().toString()).add("connectionSex", gen)
								.add("provinceId", provinceId).add("cityId", cityId).add("connectionRole", role)
								.add("individualResume", tv_intro_show.getText().toString()).build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						System.out.println("=======提交个人信息====" + jsonStr);
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								try {
									JSONObject jsonobject = new JSONObject(jsonStr);
									String error = jsonobject.optString("_ERROR_MESSAGE_");
									String success = jsonobject.optString("isSuccess");
									if (success.equals("Y")) {
										Toast.makeText(EditMydataActivity.this, "修改成功", Toast.LENGTH_LONG).show();
										setResult(RESULT_ENTER);
										finish();
									} else if (error.equals("200")) {
										Toast.makeText(EditMydataActivity.this, "修改失败,程序报错", Toast.LENGTH_LONG).show();
									} else if (error.equals("104")) {
										Toast.makeText(EditMydataActivity.this, "修改失败,无效更新", Toast.LENGTH_LONG).show();
									} else if (error.equals("103")) {
										Toast.makeText(EditMydataActivity.this, "修改失败,无效用户", Toast.LENGTH_LONG).show();
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});
					}

					@Override
					public void onFailure(Call arg0, IOException arg1) {
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(EditMydataActivity.this, "网络异常", Toast.LENGTH_LONG).show();

							}
						});
					}
				});
	}

	public File getFileForUrl(Uri uri) {

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		File file = new File(img_path);

		return file;
	}

	private void uploadPic(String userId) {

		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("accessToken", sp.getString("accessToken", ""));
		paramsMap.put("userLoginId", userId);
		paramsMap.put("srcImage", file);

		putImageForFile2(MtsUrls.base + MtsUrls.update_logo, paramsMap);
	}

	private void putImageForFile2(String requestUrl, HashMap<String, Object> paramsMap) {

		MultipartBody.Builder builder = new MultipartBody.Builder();

		builder.setType(MultipartBody.FORM);

		for (String key : paramsMap.keySet()) {

			Object object = paramsMap.get(key);
			if (!(object instanceof File)) {
				builder.addFormDataPart(key, object.toString());
			} else {
				File file = (File) object;
				builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("jpg/png"), file));
			}
		}

		// 创建RequestBody
		RequestBody body = builder.build();
		// 创建Request
		final Request request = new Request.Builder().url(requestUrl).post(body).build();
		final Call call = OkHttpManager.client.newBuilder().build().newCall(request);

		call.enqueue(new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (!response.isSuccessful()) {
					return;
				}
				final String string = response.body().string();
				Log.e("上传图片", "response ----->" + string);
				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						try {
							JSONObject object = new JSONObject(string);
							String is = object.optString("isSuccess");
							String message = object.optString("_ERROR_MESSAGE_");
							if (is.equals("Y")) {
								Toast.makeText(EditMydataActivity.this, "上传成功", Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(EditMydataActivity.this, "上传失败" + "\n" + message, Toast.LENGTH_LONG)
										.show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});

			}

			@Override
			public void onFailure(Call arg0, IOException arg1) {
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(EditMydataActivity.this, "网络异常", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(RESULT_NEGATIVE);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private Bitmap bitMatrix2Bitmap(BitMatrix matrix) {
		int w = matrix.getWidth();
		int h = matrix.getHeight();
		int[] rawData = new int[w * h];
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int color = Color.WHITE;
				if (matrix.get(i, j)) {
					color = Color.BLACK;
				}
				rawData[i + (j * w)] = color;
			}
		}

		Bitmap bitmap = Bitmap.createBitmap(w, h, Config.RGB_565);
		bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
		return bitmap;
	}

	private Bitmap generateQRCode(String content) {
		try {
			QRCodeWriter writer = new QRCodeWriter();
			// MultiFormatWriter writer = new MultiFormatWriter();
			BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 1200, 1200);
			return bitMatrix2Bitmap(matrix);
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}
}
