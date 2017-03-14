package com.spt.page;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.controler.SelectPicPopupWindow;
import com.spt.sht.R;
import com.spt.utils.ImageUtils;
import com.spt.utils.MtsUrls;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;
import com.spt.utils.Verification;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PersonalAuthenticationActivity extends FragmentActivity implements OnClickListener {

	private ImageView iv_authentication_back, iv_id_front, iv_id_back, iv_success;
	private TextView tv_text1, tv_text2, tv_commit;
	private EditText et_id;
	private LinearLayout ll_hole;
	private SharedPreferences sp;
	private static ProgressDialog dialog;

	private File fileFront, fileBack;
	/**
	 * 自定义的PopupWindow
	 */
	private SelectPicPopupWindow menuWindow;
	/**
	 * 选择图片的返回码
	 */
	public static final int SELECT_IMAGE_RESULT_CODE_FRONT = 200;
	public static final int SELECT_IMAGE_RESULT_CODE_BACK = 201;
	public static final int RESULT_ENTER = 11;
	/**
	 * 当前选择的图片的路径
	 */
	public String mImagePath;
	String[] proj = { MediaColumns.DATA };

	private boolean isFront = false, isBack = false;
	private String idPicF = "", idPicB = "", shenqing = "";

	@Override
	protected void onCreate(Bundle arg0) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_personal_authentication);
		super.onCreate(arg0);

		initViews();
		// getIndividual();

		Intent intent = getIntent();
		shenqing = intent.getStringExtra("renzheng");

		if (sp.getString("idcard", "").equals("null")) {
			et_id.setHint("该用户暂时未上传身份证号");
		} else {
			et_id.setText(sp.getString("idcard", ""));
		}

		idPicF = sp.getString("idpic_front", "");
		idPicB = sp.getString("idpic_back", "");

		if (!sp.getString("idpic_front", "").equals("null") || (!sp.getString("idpic_back", "").equals("null"))) {
			tv_text1.setVisibility(View.VISIBLE);
			tv_text2.setVisibility(View.VISIBLE);
			iv_success.setVisibility(View.GONE);

			Picasso.with(PersonalAuthenticationActivity.this).load(idPicF).placeholder(R.drawable.approve_idcard_1_big)
					.error(R.drawable.approve_idcard_1_big).resize(800, 600).centerCrop().into(iv_id_front);

			Picasso.with(PersonalAuthenticationActivity.this).load(idPicB).placeholder(R.drawable.approve_idcard_2_big)
					.error(R.drawable.approve_idcard_2_big).resize(800, 600).centerCrop().into(iv_id_back);

			if (shenqing.equals("waiting_for_approval")) {
				tv_commit.setText("认证中...");
				tv_commit.setClickable(false);
				tv_commit.setBackgroundResource(R.drawable.my_co_btn_off);
				tv_commit.setTextColor(0xff333333);
			} else if (shenqing.equals("approved")) {
				tv_commit.setText("重新认证");
				tv_commit.setClickable(true);
				tv_commit.setBackgroundResource(R.drawable.my_co_btn);
				tv_commit.setTextColor(0xffffffff);
				tv_text1.setVisibility(View.GONE);
				tv_text2.setVisibility(View.GONE);
				iv_success.setVisibility(View.VISIBLE);
			} else if (shenqing.equals("refused")) {
				tv_commit.setText("重新认证");
				tv_commit.setClickable(true);
				tv_commit.setBackgroundResource(R.drawable.my_co_btn);
				tv_commit.setTextColor(0xffffffff);
				tv_text1.setVisibility(View.VISIBLE);
				tv_text2.setVisibility(View.VISIBLE);
				tv_text1.setText("您的申请已被拒绝");
				tv_text2.setText("您可以尝试重新申请");
				iv_success.setVisibility(View.GONE);
			} else if (shenqing.equals("disabled")) {
				tv_commit.setText("已禁用");
				tv_commit.setClickable(false);
				tv_commit.setBackgroundResource(R.drawable.my_co_btn_off);
				tv_commit.setTextColor(0xff333333);
			}

		} else {
			tv_text1.setVisibility(View.VISIBLE);
			tv_text2.setVisibility(View.VISIBLE);
			iv_success.setVisibility(View.GONE);
		}

	}

	private void certifiedPersonal() {
		dialog.show();
		HashMap<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("accessToken", sp.getString("accessToken", ""));
		paramsMap.put("userLoginId", sp.getString("username", ""));
		paramsMap.put("certificateNumber", et_id.getText().toString());
		paramsMap.put("srcImage1", fileFront);
		paramsMap.put("srcImage2", fileBack);

		putImageForFile2(MtsUrls.base + MtsUrls.certified_personal, paramsMap);
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
				dialog.dismiss();
				Log.e("个人认证", "response ----->" + string);
				new Handler(Looper.getMainLooper()).post(new Runnable() {

					@Override
					public void run() {
						try {
							JSONObject object = new JSONObject(string);
							String is = object.optString("isSuccess");
							String message = object.optString("_ERROR_MESSAGE_");
							if (is.equals("Y")) {
								Toast.makeText(PersonalAuthenticationActivity.this, "提交成功，请等待审核", Toast.LENGTH_LONG)
										.show();
								setResult(RESULT_ENTER);
								finish();
							} else if (message.equals("101") | message.equals("103")) {
								Toast.makeText(PersonalAuthenticationActivity.this, "上传失败" + "\n" + "参数不完全",
										Toast.LENGTH_LONG).show();
							} else if (message.equals("102")) {
								Toast.makeText(PersonalAuthenticationActivity.this, "请输入身份证号", Toast.LENGTH_LONG)
										.show();
							} else if (message.equals("111")) {
								Toast.makeText(PersonalAuthenticationActivity.this, "个人资料欠缺，请完善个人信息", Toast.LENGTH_LONG)
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
				dialog.dismiss();
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(PersonalAuthenticationActivity.this, "网络异常", Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case SELECT_IMAGE_RESULT_CODE_FRONT:
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
					Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 800, 600);
					iv_id_front.setImageBitmap(bitmap);
					fileFront = getFileForUrl(uri);
				} else {
					// 拍照
					imagePath = mImagePath;
					Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 800, 600);
					iv_id_front.setImageBitmap(bitmap);
					fileFront = new File(imagePath);
				}
				isFront = true;
			}
			break;
		case SELECT_IMAGE_RESULT_CODE_BACK:
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
					Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 800, 600);
					iv_id_back.setImageBitmap(bitmap);
					fileBack = getFileForUrl(uri);
				} else {
					// 拍照
					imagePath = mImagePath;
					Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 800, 600);
					iv_id_back.setImageBitmap(bitmap);
					fileBack = new File(imagePath);
				}
				isBack = true;
			}
			break;
		case 1001:
			if (resultCode == 1000) {
				String path = "";
				path = data.getStringExtra("path");
				iv_id_front.setImageBitmap(ImageUtils.getImageThumbnail(path, 800, 600));

				// iv_id_front.setImageBitmap(BitmapFactory.decodeFile(path));
			}
			break;
		default:
			break;
		}
	}

	private void initViews() {
		iv_authentication_back = (ImageView) findViewById(R.id.iv_authentication_back);
		iv_id_front = (ImageView) findViewById(R.id.iv_id_front);
		iv_id_back = (ImageView) findViewById(R.id.iv_id_back);
		iv_success = (ImageView) findViewById(R.id.iv_success);
		tv_text1 = (TextView) findViewById(R.id.tv_text1);
		tv_text2 = (TextView) findViewById(R.id.tv_text2);
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		et_id = (EditText) findViewById(R.id.et_id);
		ll_hole = (LinearLayout) findViewById(R.id.ll_hole);

		dialog = ProgressDialog.show(PersonalAuthenticationActivity.this, "请稍候。。。", "提交审核中。。。", true);
		dialog.dismiss();
		sp = PersonalAuthenticationActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        iv_authentication_back.setOnClickListener(this);
        iv_id_front.setOnClickListener(this);
        iv_id_back.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_authentication_back:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			finish();
			break;
		case R.id.iv_id_front:
			 Intent intent = new Intent(PersonalAuthenticationActivity.this,
			 RectCameraActivity.class);
			 startActivityForResult(intent, 1001);
//			showPicturePopupWindow("front");
			break;
		case R.id.iv_id_back:
			showPicturePopupWindow("back");
			break;
		case R.id.tv_commit:
			if (!NoDoubleClickUtils.isDoubleClick()) {
				if (et_id.getText().toString().equals("")) {
					Toast.makeText(PersonalAuthenticationActivity.this, "请输入身份证号", Toast.LENGTH_LONG).show();
				} else if (!Verification.isIDCard(et_id.getText().toString())) {
					Toast.makeText(PersonalAuthenticationActivity.this, "请输入正确的身份证号", Toast.LENGTH_LONG).show();
				} else if (!isFront || (!isBack)) {
					Toast.makeText(PersonalAuthenticationActivity.this, "请上传身份证的正面和反面照片", Toast.LENGTH_LONG).show();
				} else if (Verification.isIDCard(et_id.getText().toString())) {
					certifiedPersonal();
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
	public void showPicturePopupWindow(final String side) {
		menuWindow = new SelectPicPopupWindow(this, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 隐藏弹出窗口
				menuWindow.dismiss();
				switch (v.getId()) {
				case R.id.takePhotoBtn:// 拍照
					takePhoto(side);
					break;
				case R.id.pickPhotoBtn:// 相册选择图片
					pickPhoto(side);
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
	private void takePhoto(String side) {
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
			if (side.equals("front")) {
				startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_FRONT);
			} else if (side.equals("back")) {
				startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_BACK);
			}

		} else {
			Toast.makeText(this, "内存卡不存在!", Toast.LENGTH_LONG).show();
		}
	}

	/***
	 * 从相册中取图片
	 */
	private void pickPhoto(String side) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		if (side.equals("front")) {
			startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_FRONT);
		} else if (side.equals("back")) {
			startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_BACK);
		}

	}

	@SuppressWarnings("deprecation")
	public File getFileForUrl(Uri uri) {

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
		int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		actualimagecursor.moveToFirst();
		String img_path = actualimagecursor.getString(actual_image_column_index);
		File file = new File(img_path);

		return file;
	}

}
