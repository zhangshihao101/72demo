package com.spt.page;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.controler.DateTimePickDialogUtil;
import com.spt.controler.SelectPicPopupWindow;
import com.spt.sht.R;
import com.spt.utils.ImageUtils;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.spt.utils.Verification;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class CompanyAuthenticationActivity extends FragmentActivity implements OnClickListener {

    private ImageView iv_company_back, iv_legal_name_clear, iv_legal_no_clear, iv_id_front, iv_id_back, iv_license,
            iv_license_code_clear, iv_accredit;
    private EditText et_legal_name, et_legal_no_show, et_license_code, et_license_start, et_license_end,
            et_accredit_start, et_accredit_end, et_role_show;
    private TextView tv_commit,tv_re_commit;
    private LinearLayout ll_hole;
    private SharedPreferences sp;
    private static ProgressDialog dialog;
    private String cerIdF = "", cerIdB = "", licId = "", braId = "", ids = "";

    private File fileFront, fileBack, fileLicense, fileAccredit;
    /**
     * 自定义的PopupWindow
     */
    private SelectPicPopupWindow menuWindow;
    /**
     * 选择图片的返回码
     */
    public static final int SELECT_IMAGE_RESULT_CODE_FRONT = 200;
    public static final int SELECT_IMAGE_RESULT_CODE_BACK = 201;
    public static final int SELECT_IMAGE_RESULT_CODE_LICENSE = 202;
    public static final int SELECT_IMAGE_RESULT_CODE_ACCREDIT = 203;
    /**
     * 当前选择的图片的路径
     */
    public String mImagePath;
    String[] proj = {MediaColumns.DATA};

    private HashMap<String, String> rodeMap;
    private String rodeId = "";
    private boolean isFront = false, isBack = false, isLicense = false, isAccredit = false;

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_creat_company);
        super.onCreate(arg0);

        initViews();

        getCompanyInfo();

        et_legal_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!et_legal_name.getText().toString().equals("")) {
                    iv_legal_name_clear.setVisibility(View.VISIBLE);
                } else {
                    iv_legal_name_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        et_legal_no_show.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!et_legal_no_show.getText().toString().equals("")) {
                    iv_legal_no_clear.setVisibility(View.VISIBLE);
                } else {
                    iv_legal_no_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        et_license_code.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!et_license_code.getText().toString().equals("")) {
                    iv_license_code_clear.setVisibility(View.VISIBLE);
                } else {
                    iv_license_code_clear.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

    }

    private void getCompanyInfo() {
        OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.getCompanyInformation)
                .post(new FormBody.Builder().add("accessToken", sp.getString("accessToken", ""))
                        .add("ownerPartyId", sp.getString("partyId", "")).add("partyId", sp.getString("partyId", ""))
                        .build())
                .build()).enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("====获取公司详情====" + jsonStr + "====");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    et_legal_name.setText(object.optString("legalName"));
                                    et_legal_no_show.setText(object.optString("identityCardNumber"));
                                    et_license_code.setText(object.optString("licenseNumber"));
                                    et_license_start.setText(object.optString("licenseDateStart"));
                                    et_license_end.setText(object.optString("licenseDateEnd"));
                                    et_accredit_start.setText(object.optString("authorizePeriodStart"));
                                    et_accredit_end.setText(object.optString("authorizePeriodEnd"));
                                    if (object.optString("roleTypeId").equals("CERTIFICATE_SUPPLIER")) {
                                        et_role_show.setText("供应商");
                                    } else if (object.optString("roleTypeId").equals("RETAILER")) {
                                        et_role_show.setText("零售商");
                                    } else if (object.optString("roleTypeId").equals("S_R_ALL")) {
                                        et_role_show.setText("零售商供应商");
                                    }

                                    JSONArray arrayId = object.optJSONArray("cerImages");
                                    if (arrayId == null || arrayId.length() == 0) {

                                    } else {
                                        JSONObject objF = new JSONObject(arrayId.getString(0));
                                        cerIdF = objF.optString("imageId");
                                        JSONObject objB = new JSONObject(arrayId.getString(1));
                                        cerIdB = objB.optString("imageId");
                                        Picasso.with(CompanyAuthenticationActivity.this)
                                                .load(objF.optString("imageUrl"))
                                                .placeholder(R.drawable.found_co_idcard_1)
                                                .error(R.drawable.found_co_idcard_1).resize(510, 472).centerCrop()
                                                .into(iv_id_front);
                                        Picasso.with(CompanyAuthenticationActivity.this)
                                                .load(objB.optString("imageUrl"))
                                                .placeholder(R.drawable.found_co_idcard_2)
                                                .error(R.drawable.found_co_idcard_2).resize(510, 472).centerCrop()
                                                .into(iv_id_back);
                                    }

                                    JSONArray arrayBrand = object.optJSONArray("brandImages");

                                    if (arrayBrand == null || arrayBrand.length() == 0) {

                                    } else {
                                        JSONObject objBrand = new JSONObject(arrayBrand.getString(0));
                                        braId = objBrand.optString("imageId");
                                        Picasso.with(CompanyAuthenticationActivity.this)
                                                .load(objBrand.optString("imageUrl"))
                                                .placeholder(R.drawable.found_co_certificate_2)
                                                .error(R.drawable.found_co_certificate_2).resize(800, 600).centerCrop()
                                                .into(iv_accredit);
                                    }

                                    JSONArray arrayLicense = object.optJSONArray("licenseImages");

                                    if (arrayLicense == null || arrayLicense.length() == 0) {

                                    } else {
                                        JSONObject objLicense = new JSONObject(arrayLicense.getString(0));
                                        licId = objLicense.optString("imageId");
                                        Picasso.with(CompanyAuthenticationActivity.this)
                                                .load(objLicense.optString("imageUrl"))
                                                .placeholder(R.drawable.found_co_certificate)
                                                .error(R.drawable.found_co_certificate).resize(800, 600).centerCrop()
                                                .into(iv_license);
                                    }

                                    if (object.optString("certificateState").equals("0")) {
//                                        tv_re_commit.setVisibility(View.GONE);
                                        tv_commit.setVisibility(View.VISIBLE);
                                        tv_commit.setText("认证中...");
                                        tv_commit.setClickable(false);
                                        tv_commit.setBackgroundResource(R.drawable.my_co_btn_off);
                                        tv_commit.setTextColor(0xff333333);
                                    } else if (object.optString("certificateState").equals("3")) {
//                                        tv_re_commit.setVisibility(View.GONE);
                                        tv_commit.setVisibility(View.VISIBLE);
                                        tv_commit.setText("已禁用");
                                        tv_commit.setClickable(false);
                                        tv_commit.setBackgroundResource(R.drawable.my_co_btn_off);
                                        tv_commit.setTextColor(0xff333333);
                                    } else if (object.optString("certificateState").equals("1")
                                            || object.optString("certificateState").equals("2")) {
                                        tv_commit.setVisibility(View.VISIBLE);
//                                        tv_re_commit.setVisibility(View.VISIBLE);
                                        tv_commit.setText("重新认证");
                                        tv_commit.setClickable(true);
                                        tv_commit.setBackgroundResource(R.drawable.my_co_btn);
                                        tv_commit.setTextColor(0xffffffff);
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
                                Toast.makeText(CompanyAuthenticationActivity.this, "网络错误，请检查网络", Toast.LENGTH_LONG)
                                        .show();

                            }
                        });

                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_company_back:
                finish();
                break;
            case R.id.iv_legal_name_clear:
                et_legal_name.setText("");
                break;
            case R.id.iv_legal_no_clear:
                et_legal_no_show.setText("");
                break;
            case R.id.iv_license_code_clear:
                et_license_code.setText("");
                break;
            case R.id.iv_id_front:
                showPicturePopupWindow("front");
                break;
            case R.id.iv_id_back:
                showPicturePopupWindow("back");
                break;
            case R.id.iv_license:
                showPicturePopupWindow("license");
                break;
            case R.id.iv_accredit:
                showPicturePopupWindow("accredit");
                break;
            case R.id.et_license_start:
                getDate(et_license_start);
                break;
            case R.id.et_license_end:
                getDate(et_license_end);
                break;
            case R.id.et_accredit_start:
                getDate(et_accredit_start);
                break;
            case R.id.et_accredit_end:
                getDate(et_accredit_end);
                break;
            case R.id.et_role_show:
                Intent intent = new Intent(CompanyAuthenticationActivity.this, ChosenRoleActivity.class);
                startActivityForResult(intent, 500);
                break;
            case R.id.tv_commit:
                if (et_legal_name.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "法人姓名不能为空", Toast.LENGTH_LONG).show();
                } else if (et_license_code.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "营业执照号码不能为空", Toast.LENGTH_LONG).show();
                } else if (et_license_start.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请输入营业执照有效期起始日", Toast.LENGTH_LONG).show();
                } else if (et_license_end.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请输入营业执照有效期截止日", Toast.LENGTH_LONG).show();
                } else if (rodeId.equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请选择认证角色", Toast.LENGTH_LONG).show();
                } else if (et_legal_no_show.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "法人身份证号不能为空", Toast.LENGTH_LONG).show();
                } else if (!et_legal_no_show.getText().toString().equals("")
                        && (!Verification.isIDCard(et_legal_no_show.getText().toString()))) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请输入正确的身份证号", Toast.LENGTH_LONG).show();
                } else if (isFront == false | isBack == false) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请上传身份证的正面及反面照片", Toast.LENGTH_LONG).show();
                } else if (isLicense == false) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请上传营业执照照片", Toast.LENGTH_LONG).show();
                } else if (!rodeId.equals("RETAILER") && isAccredit == false) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请上传品牌注册证或品牌商品授权证照片", Toast.LENGTH_LONG).show();
                } else if (!rodeId.equals("RETAILER") && et_accredit_start.equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请上传品牌注册证或品牌商品授权证起始日", Toast.LENGTH_LONG).show();
                } else if (!rodeId.equals("RETAILER") && et_accredit_end.equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请上传品牌注册证或品牌商品授权证截止日", Toast.LENGTH_LONG).show();
                } else {
                    companyAuthentication();
                }
                break;
            case R.id.tv_re_commit:
                if (et_legal_name.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "法人姓名不能为空", Toast.LENGTH_LONG).show();
                } else if (et_license_code.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "营业执照号码不能为空", Toast.LENGTH_LONG).show();
                } else if (et_license_start.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请输入营业执照有效期起始日", Toast.LENGTH_LONG).show();
                } else if (et_license_end.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请输入营业执照有效期截止日", Toast.LENGTH_LONG).show();
                } else if (rodeId.equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请选择认证角色", Toast.LENGTH_LONG).show();
                } else if (et_legal_no_show.getText().toString().equals("")) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "法人身份证号不能为空", Toast.LENGTH_LONG).show();
                } else if (!et_legal_no_show.getText().toString().equals("")
                        && (!Verification.isIDCard(et_legal_no_show.getText().toString()))) {
                    Toast.makeText(CompanyAuthenticationActivity.this, "请输入正确的身份证号", Toast.LENGTH_LONG).show();
                }
                
                
                
                
                break;
        }
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
                        Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 510, 472);
                        iv_id_front.setImageBitmap(bitmap);
                        fileFront = getFileForUrl(uri);
                    } else {
                        // 拍照
                        imagePath = mImagePath;
                        Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 510, 472);
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
                        Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 510, 472);
                        iv_id_back.setImageBitmap(bitmap);
                        fileBack = getFileForUrl(uri);
                    } else {
                        // 拍照
                        imagePath = mImagePath;
                        Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 510, 472);
                        iv_id_back.setImageBitmap(bitmap);
                        fileBack = new File(imagePath);
                    }
                    isBack = true;
                }
                break;
            case SELECT_IMAGE_RESULT_CODE_LICENSE:
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
                        iv_license.setImageBitmap(bitmap);
                        fileLicense = getFileForUrl(uri);
                    } else {
                        // 拍照
                        imagePath = mImagePath;
                        Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 800, 600);
                        iv_license.setImageBitmap(bitmap);
                        fileLicense = new File(imagePath);
                    }
                    isLicense = true;
                }
                break;
            case SELECT_IMAGE_RESULT_CODE_ACCREDIT:
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
                        iv_accredit.setImageBitmap(bitmap);
                        fileAccredit = getFileForUrl(uri);
                    } else {
                        // 拍照
                        imagePath = mImagePath;
                        Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 800, 600);
                        iv_accredit.setImageBitmap(bitmap);
                        fileAccredit = new File(imagePath);
                    }
                    isAccredit = true;
                }
                break;
            case 500:
                if (resultCode == 1000) {

                    et_role_show.setText(data.getStringExtra("rode"));
                    rodeId = rodeMap.get(data.getStringExtra("rode"));

                }
                break;
        }
    }

    private void getDate(EditText view) {
        DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(CompanyAuthenticationActivity.this, "");
        dateTimePicKDialog.dateTimePicKDialog(view);
    }
    
    private void companyAuthentication() {
        dialog.show();
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("accessToken", sp.getString("accessToken", ""));
        paramsMap.put("partyId", sp.getString("partyId", ""));
        paramsMap.put("corporateRepresentative", et_legal_name.getText().toString());
        paramsMap.put("licenseNumber", et_license_code.getText().toString());
        paramsMap.put("licenseDateStart", et_license_start.getText().toString());
        paramsMap.put("licenseDateEnd", et_license_end.getText().toString());
        paramsMap.put("isPublic", "Y");
//        paramsMap.put("publicFileId", "");
        paramsMap.put("isMobileTerminal", "Y");
        // 待选
        if (!rodeId.equals("RETAILER")) {
            paramsMap.put("authorizePeriodStart", et_accredit_start.getText().toString());
            paramsMap.put("authorizePeriodEnd", et_accredit_end.getText().toString());
            paramsMap.put("image4", fileAccredit);
        } else {

        }

        paramsMap.put("role", rodeId);
        paramsMap.put("identityCardNumber", et_legal_no_show.getText().toString());
        paramsMap.put("image1", fileFront);
        paramsMap.put("image2", fileBack);
        paramsMap.put("image3", fileLicense);
        // 待选

        putImageForFile2(MtsUrls.base + MtsUrls.applay_certificateInfo, paramsMap);
    }

    private void putImageForFile2(String requestUrl, HashMap<String, Object> paramsMap) {

        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.setType(MultipartBody.FORM);

        for (String key : paramsMap.keySet()) {

            Object object = paramsMap.get(key);
            if (object == null) {

            } else if (!(object instanceof File)) {
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
                Log.e("公司认证", "response ----->" + string);
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(string);
                            String is = object.optString("flag");
                            String message = object.optString("_ERROR_MESSAGE_");
                            if (is.equals("Y")) {
                                Toast.makeText(CompanyAuthenticationActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                                // setResult(RESULT_ENTER);
                                finish();
                            } else if (is.equals("N")) {
                                Toast.makeText(CompanyAuthenticationActivity.this, "提交失败" + message, Toast.LENGTH_LONG)
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
                        Toast.makeText(CompanyAuthenticationActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
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
            } else if (side.equals("license")) {
                startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_LICENSE);
            } else if (side.equals("accredit")) {
                startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_ACCREDIT);
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
        } else if (side.equals("license")) {
            startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_LICENSE);
        } else if (side.equals("accredit")) {
            startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_ACCREDIT);
        }

    }

    public File getFileForUrl(Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);

        return file;
    }

    private void initViews() {
        iv_company_back = (ImageView) findViewById(R.id.iv_company_back);
        iv_legal_name_clear = (ImageView) findViewById(R.id.iv_legal_name_clear);
        iv_legal_no_clear = (ImageView) findViewById(R.id.iv_legal_no_clear);
        iv_id_front = (ImageView) findViewById(R.id.iv_id_front);
        iv_id_back = (ImageView) findViewById(R.id.iv_id_back);
        iv_license = (ImageView) findViewById(R.id.iv_license);
        iv_license_code_clear = (ImageView) findViewById(R.id.iv_license_code_clear);
        iv_accredit = (ImageView) findViewById(R.id.iv_accredit);
        et_legal_name = (EditText) findViewById(R.id.et_legal_name);
        et_legal_no_show = (EditText) findViewById(R.id.et_legal_no_show);
        et_license_code = (EditText) findViewById(R.id.et_license_code);
        et_license_start = (EditText) findViewById(R.id.et_license_start);
        et_license_end = (EditText) findViewById(R.id.et_license_end);
        et_accredit_start = (EditText) findViewById(R.id.et_accredit_start);
        et_accredit_end = (EditText) findViewById(R.id.et_accredit_end);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        tv_re_commit = (TextView) findViewById(R.id.tv_re_commit);
        et_role_show = (EditText) findViewById(R.id.et_role_show);
        ll_hole = (LinearLayout) findViewById(R.id.ll_hole);
        rodeMap = new HashMap<String, String>();
        rodeMap.put("供应商", "CERTIFICATE_SUPPLIER");
        rodeMap.put("零售商", "RETAILER");
        rodeMap.put("零售商供应商", "S_R_ALL");

        dialog = ProgressDialog.show(CompanyAuthenticationActivity.this, "请稍候。。。", "提交审核中。。。", true);
        dialog.dismiss();

        sp = CompanyAuthenticationActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);

        iv_company_back.setOnClickListener(this);
        iv_legal_name_clear.setOnClickListener(this);
        iv_legal_no_clear.setOnClickListener(this);
        iv_license_code_clear.setOnClickListener(this);
        iv_id_front.setOnClickListener(this);
        iv_id_back.setOnClickListener(this);
        iv_license.setOnClickListener(this);
        iv_accredit.setOnClickListener(this);
        et_license_start.setOnClickListener(this);
        et_license_end.setOnClickListener(this);
        et_accredit_start.setOnClickListener(this);
        et_accredit_end.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
        tv_re_commit.setOnClickListener(this);
        et_role_show.setOnClickListener(this);
    }
}
