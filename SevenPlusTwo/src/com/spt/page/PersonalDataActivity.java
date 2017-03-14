package com.spt.page;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.spt.controler.CircleImageView;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@SuppressLint("InflateParams")
public class PersonalDataActivity extends FragmentActivity implements OnClickListener {

    private ImageView iv_personal_back, iv_personal_code, iv_line1;
    private TextView tv_personal_name, tv_personal_no, tv_add_friends, tv_phone_show, tv_company_show,
            tv_introduction_show, tv_positon_show, tv_flag_show;
    private CircleImageView civ_personal_header;
    private RelativeLayout rl_personal_company, rl_phone;

    private String userId = "", otherPattyId = "";
    private SharedPreferences sp;
    private ProgressDialog dialog;
    private HashMap<String, String> filterMap;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal_data);
        super.onCreate(arg0);

        initViews();

        Intent intent = getIntent();
        userId = intent.getStringExtra("personId");

        // if (intent.getStringExtra("isF").equals("2")) {
        // tv_add_friends.setBackgroundResource(R.drawable.person_detail_head_add_btn_space);
        // tv_add_friends.setText("已是好友");
        // tv_add_friends.setGravity(Gravity.CENTER);
        // tv_add_friends.setTextColor(0xffb4b4b4);
        // tv_phone_show.setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});
        // } else if (intent.getStringExtra("isF").equals("0")) {
        // tv_add_friends.setOnClickListener(this);
        // } else if (intent.getStringExtra("isF").equals("1")) {
        // tv_add_friends.setBackgroundResource(R.drawable.person_detail_head_add_btn_space);
        // tv_add_friends.setText("申请中");
        // tv_add_friends.setTextColor(0xffb4b4b4);
        // tv_add_friends.setGravity(Gravity.CENTER);
        // }

        if (userId.equals(sp.getString("username", ""))) {
            tv_add_friends.setBackgroundResource(R.drawable.person_detail_head_add_btn_space);
            tv_add_friends.setText("本用户");
            tv_add_friends.setTextColor(0xffb4b4b4);
            tv_add_friends.setGravity(Gravity.CENTER);
            tv_add_friends.setOnClickListener(null);
        }

        getPersonData();

        iv_personal_back.setOnClickListener(this);
        iv_personal_code.setOnClickListener(this);
        rl_personal_company.setOnClickListener(this);
        rl_phone.setOnClickListener(this);
    }

    @SuppressLint("NewApi")
    private void changeRelationship() {
        dialog.show();
        OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.change_relation)
                .post(new FormBody.Builder().add("masterId", sp.getString("username", "")).add("slaveId", userId)
                        .add("accessToken", sp.getString("accessToken", "")).add("changeType", "ask").build())
                .build()).enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("=====加好友====" + jsonStr + "======");
                        dialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String error = object.optString("_ERROR_MESSAGE_");
                                    String success = object.optString("isSuccess");
                                    if (error != null && "".equals(error)) {
                                        // tv_add_friends.setBackground(null);
                                        // tv_add_friends.setText("已申请");
                                        // tv_add_friends.setTextColor(0xffb4b4b4);
                                        Toast.makeText(PersonalDataActivity.this, "添加好友请求成功，等待对方确认", Toast.LENGTH_SHORT)
                                                .show();
                                    } else if (error.equals("102")) {
                                        Toast.makeText(PersonalDataActivity.this, "登录秘钥失效，请重新登录", Toast.LENGTH_SHORT)
                                                .show();
                                    } else if (error.equals("126")) {
                                        Toast.makeText(PersonalDataActivity.this, "已经请求，请等待对方确认", Toast.LENGTH_SHORT)
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
                                Toast.makeText(PersonalDataActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }

    private void getPersonData() {
        dialog.show();
        OkHttpManager.client
                .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_individual)
                        .post(new FormBody.Builder().add("userLoginId", userId)
                                .add("otherId", sp.getString("username", ""))
                                .add("accessToken", sp.getString("accessToken", "")).build())
                        .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("=====别人资料===" + jsonStr);
                        dialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String error = object.optString("_ERROR_MESSAGE_");
                                    if (error.equals("")) {
                                        JSONObject objIndi = object.optJSONObject("individualInfor");
                                        tv_positon_show.setText(objIndi.optString("provinceName"));
                                        tv_flag_show.setText(filterMap.get(objIndi.optString("connectionRole")));
                                        tv_phone_show.setText(objIndi.optString("contactsTelephoneNumber"));
                                        tv_company_show.setText("null".equals(objIndi.optString("groupName"))
                                                ? "暂无"
                                                : objIndi.optString("groupName"));
                                        tv_introduction_show
                                                .setText("null".equals(objIndi.optString("individualResume"))
                                                        ? ""
                                                        : objIndi.optString("individualResume"));

                                        Picasso.with(PersonalDataActivity.this).load(objIndi.optString("logoPath"))
                                                .placeholder(R.drawable.noheader).error(R.drawable.noheader)
                                                .resize(100, 100).into(civ_personal_header);
                                        tv_personal_name.setText(objIndi.optString("connectionName").equals("null")
                                                ? ""
                                                : objIndi.optString("connectionName"));
                                        tv_personal_no.setText("账号：" + objIndi.optString("userLoginId"));
                                        otherPattyId = objIndi.optString("partyId");
                                        if (objIndi.optString("isFirend").equals("1")) {
                                            tv_add_friends
                                                    .setBackgroundResource(R.drawable.person_detail_head_add_btn_space);
                                            tv_add_friends.setText("等待验证");
                                            tv_add_friends.setTextColor(0xffb4b4b4);
                                        } else if (objIndi.optString("isFirend").equals("0")) {
                                            tv_add_friends.setOnClickListener(new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {

                                                    tv_add_friends.setBackgroundResource(
                                                            R.drawable.person_detail_head_add_btn_space);
                                                    tv_add_friends.setGravity(Gravity.CENTER);
                                                    tv_add_friends.setText("等待验证");
                                                    tv_add_friends.setTextColor(0xffb4b4b4);
                                                    changeRelationship();
                                                }
                                            });
                                        } else if (objIndi.optString("isFirend").equals("2")) {
                                            tv_add_friends
                                                    .setBackgroundResource(R.drawable.person_detail_head_add_btn_space);
                                            tv_add_friends.setText("已是好友");
                                            tv_add_friends.setTextColor(0xffb4b4b4);
                                            tv_phone_show
                                                    .setFilters(new InputFilter[] {new InputFilter.LengthFilter(11)});
                                        }
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
                                Toast.makeText(PersonalDataActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

    }

    private void initViews() {
        iv_personal_back = (ImageView) findViewById(R.id.iv_personal_back);
        iv_personal_code = (ImageView) findViewById(R.id.iv_personal_code);
        iv_line1 = (ImageView) findViewById(R.id.iv_line1);
        tv_personal_name = (TextView) findViewById(R.id.tv_personal_name);
        tv_personal_no = (TextView) findViewById(R.id.tv_personal_no);
        tv_add_friends = (TextView) findViewById(R.id.tv_add_friends);
        tv_phone_show = (TextView) findViewById(R.id.tv_phone_show);
        tv_company_show = (TextView) findViewById(R.id.tv_company_show);
        tv_positon_show = (TextView) findViewById(R.id.tv_positon_show);
        tv_flag_show = (TextView) findViewById(R.id.tv_flag_show);
        tv_introduction_show = (TextView) findViewById(R.id.tv_introduction_show);
        civ_personal_header = (CircleImageView) findViewById(R.id.civ_personal_header);
        rl_personal_company = (RelativeLayout) findViewById(R.id.rl_personal_company);
        rl_phone = (RelativeLayout) findViewById(R.id.rl_phone);

        sp = PersonalDataActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        dialog = ProgressDialog.show(PersonalDataActivity.this, "请稍候。。。", "获取数据中。。。", true);
        dialog.dismiss();

        filterMap = new HashMap<String, String>();
        filterMap.put("Leader", "领队");
        filterMap.put("Club", "俱乐部");
        filterMap.put("MassOrganizations", "社团");
        filterMap.put("WebShopOwner", "网店店主");
        filterMap.put("StoreOwner", "实体店主");
        filterMap.put("Other", "其他");
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_personal_back:
                finish();
                break;
            case R.id.iv_personal_code:
                View menu_view = LayoutInflater.from(PersonalDataActivity.this).inflate(R.layout.pop_my_code, null);
                ImageView iv_my_qr = (ImageView) menu_view.findViewById(R.id.iv_my_qr);
                // Bitmap qrcode = generateQRCode(userId);
                try {
                    Bitmap qrcode = Create2DCode(userId);
                    iv_my_qr.setImageBitmap(qrcode);
                } catch (WriterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                final PopupWindow menu_pop =
                        new PopupWindow(menu_view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
                WindowManager wm = (WindowManager) PersonalDataActivity.this.getSystemService(Context.WINDOW_SERVICE);

                int width = wm.getDefaultDisplay().getWidth();
                int hight = wm.getDefaultDisplay().getHeight();
                menu_pop.setWidth(width);
                menu_pop.setHeight(hight);

                menu_pop.showAsDropDown(iv_line1);

                break;
//            case R.id.tv_add_friends:
//                tv_add_friends.setBackground(null);
//                tv_add_friends.setText("等待验证");
//                tv_add_friends.setTextColor(0xffb4b4b4);
//                changeRelationship();
//                break;
            case R.id.rl_personal_company:
                if ("".equals(otherPattyId) || "null".equals(otherPattyId)) {
                    Toast.makeText(PersonalDataActivity.this, "没有所属公司，无法查看详情", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(PersonalDataActivity.this, CompanyDetailActivity.class);
                    intent.putExtra("partyId", otherPattyId);
                    startActivity(intent);
                }

                break;
            case R.id.rl_phone:
                if (!tv_phone_show.getText().toString().contains("**")) {
                    Uri uri = Uri.parse("tel:" + tv_phone_show.getText().toString());
                    Intent contact = new Intent();
                    // it.setAction(Intent.ACTION_CALL);//直接拨打电话
                    contact.setAction(Intent.ACTION_DIAL);// 调用软件盘方式拨打电话
                    contact.setData(uri);
                    startActivity(contact);
                }
                break;
            default:
                break;
        }

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

    /**
     * 用字符串生成二维码
     * 
     * @param str
     * @return
     * @throws WriterException
     */
    public static Bitmap Create2DCode(String text) throws WriterException {
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix matrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 1200, 1200, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

}
