package com.spt.page;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.controler.CircleImageView;
import com.spt.controler.SelectPicPopupWindow;
import com.spt.sht.R;
import com.spt.utils.ImageUtils;
import com.spt.utils.MtsUrls;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MyCompanyActivity extends FragmentActivity implements OnClickListener {

    private ImageView iv_company_back;
    private CircleImageView civ_company_logo;
    private TextView tv_name_show, tv_job_name_show, tv_commit, tv_authentication;
    private RelativeLayout rl_company_name, rl_company_logo;
    private LinearLayout ll_hole;
    private SharedPreferences sp;
    private Editor editor;
    private static ProgressDialog dialog, dialogUp;

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

    String[] proj = {MediaColumns.DATA};

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_company);
        super.onCreate(arg0);

        initViews();

        getMyCompany();
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

    /***
     * 从uri转化为文件
     */
    @SuppressWarnings("deprecation")
    public File getFileForUrl(Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);

        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_RESULT_CODE) {
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
                    civ_company_logo.setImageBitmap(bitmap);
                    file = getFileForUrl(uri);
                } else {
                    // 拍照
                    imagePath = mImagePath;
                    Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 100, 100);
                    civ_company_logo.setImageBitmap(bitmap);
                    file = new File(imagePath);
                }

                long fileSize = file.length();
                if (fileSize > 2 * 1048576) {
                    Toast.makeText(MyCompanyActivity.this, "您选择的图片不能超过2M", Toast.LENGTH_LONG).show();
                } else {
                    uploadPic();
                }
            }
        }
    }

    private void getMyCompany() {
        dialog.show();
        OkHttpManager.client
                .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.my_group)
                        .post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
                                .add("partyId", sp.getString("partyId", ""))
                                .add("accessToken", sp.getString("accessToken", "")).build())
                        .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        dialog.dismiss();
                        final String jsonStr = response.body().string();
                        System.out.println("=======我的公司====" + jsonStr);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String error = object.optString("_ERROR_MESSAGE_");
                                    String isSuccess = object.optString("isSuccess");
                                    if (isSuccess.equals("")) {
                                        tv_name_show.setText(object.optString("groupName").toString());
                                        tv_job_name_show.setText(object.optString("position").equals("")
                                                ? "暂无"
                                                : object.optString("position"));
                                        if (!object.optString("groupLogo").equals("")) {
                                            Picasso.with(MyCompanyActivity.this).load(object.optString("groupLogo"))
                                                    .placeholder(R.drawable.noheader).error(R.drawable.noheader)
                                                    .resize(100, 100).into(civ_company_logo);
                                        }
                                    } else if (error.equals("200")) {
                                        Toast.makeText(MyCompanyActivity.this, "程序报错", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(MyCompanyActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }

    private void initViews() {
        iv_company_back = (ImageView) findViewById(R.id.iv_company_back);
        civ_company_logo = (CircleImageView) findViewById(R.id.civ_company_logo);
        tv_name_show = (TextView) findViewById(R.id.tv_name_show);
        tv_job_name_show = (TextView) findViewById(R.id.tv_job_name_show);
        tv_commit = (TextView) findViewById(R.id.tv_commit);
        tv_authentication = (TextView) findViewById(R.id.tv_authentication);
        rl_company_name = (RelativeLayout) findViewById(R.id.rl_company_name);
        rl_company_logo = (RelativeLayout) findViewById(R.id.rl_company_logo);
        ll_hole = (LinearLayout) findViewById(R.id.ll_hole);
        sp = MyCompanyActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        editor = sp.edit();
        dialog = ProgressDialog.show(MyCompanyActivity.this, "请稍候。。。", "获取数据中。。。", true);
        dialog.dismiss();
        dialogUp = ProgressDialog.show(MyCompanyActivity.this, "请稍候。。。", "正在上传中。。。", true);
        dialogUp.dismiss();

        iv_company_back.setOnClickListener(this);
        civ_company_logo.setOnClickListener(this);
        tv_commit.setOnClickListener(this);
        rl_company_name.setOnClickListener(this);
        rl_company_logo.setOnClickListener(this);
        tv_authentication.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_company_back:
                finish();
                break;

            case R.id.rl_company_name:
                // Intent intent1 = new Intent(MyCompanyActivity.this,
                // CreatCompanyActivity.class);
                // startActivity(intent1);
                break;
            case R.id.rl_company_logo:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if ("N".equals(sp.getString("isAdmin", ""))) {
                        Toast.makeText(MyCompanyActivity.this, "您不是公司管理员，无法修改公司头像", Toast.LENGTH_LONG).show();
                    } else {
                        showPicturePopupWindow();
                    }
                }
                break;
            case R.id.tv_commit:

                break;
            case R.id.tv_authentication:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if ("N".equals(sp.getString("isAdmin", ""))) {
                        Toast.makeText(MyCompanyActivity.this, "您不是公司管理员，进行公司认证", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent4 = new Intent(MyCompanyActivity.this, CompanyAuthenticationActivity.class);
                        startActivity(intent4);
                    }
                }
                break;
            default:
                break;
        }

    }

    private void uploadPic() {
        dialogUp.show();
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("accessToken", sp.getString("accessToken", ""));
        paramsMap.put("partyId", sp.getString("partyId", ""));
        paramsMap.put("image", file);

        putImageForFile2(MtsUrls.base + MtsUrls.updata_companylogo, paramsMap);
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
                dialogUp.dismiss();
                Log.e("上传公司logo", "response ----->" + string);
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(string);
                            String is = object.optString("flag");
                            String message = object.optString("_ERROR_MESSAGE_");
                            String companyLogoId = object.optString("publicFileId");
                            if (is.equals("Y")) {
                                editor.putString("companyLogoId", companyLogoId);
                                editor.commit();
                                Toast.makeText(MyCompanyActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(MyCompanyActivity.this, "上传失败" + "\n" + message, Toast.LENGTH_LONG)
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
                        Toast.makeText(MyCompanyActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}
