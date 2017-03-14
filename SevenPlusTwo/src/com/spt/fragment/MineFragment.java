package com.spt.fragment;

import java.io.File;
import java.io.IOException;
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
import com.spt.page.ChangePassWordActivity;
import com.spt.page.EditMydataActivity;
import com.spt.page.HavenoCompanyActivity;
import com.spt.page.MyCompanyActivity;
import com.spt.page.PeopleSearchFriendsActivity;
import com.spt.page.PersonalAuthenticationActivity;
import com.spt.page.SettingActivity;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

@SuppressLint("InflateParams")
public class MineFragment extends Fragment implements OnClickListener {

    private View view;
    private static Context mContext;

    private TextView tv_my_name, tv_my_no, tv_edit_data;
    private RelativeLayout rl_new_friends, rl_my_friends, rl_my_message, rl_my_company, rl_my_company_order,
            rl_my_apply, rl_setting, rl_title;
    private ImageView iv_my_code;
    private CircleImageView civ_my_header;
    private static ProgressDialog dialog;
    private SharedPreferences sp;
    private Editor editor;
    private String objIndividual = "", renzheng = "";
    public static final int REQUEST_EDITDATA = 10;
    public static final int RESULT_ENTER = 11;
    public static final int RESULT_NEGATIVE = 12;
    public static final int REQUEST_AUTHENTICATION = 14;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_mine, null);

        initViews();

        getIndividual();

        return view;
    }

    private void getIndividual() {

        OkHttpManager.client
                .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_individual)
                        .post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
                                .add("accessToken", sp.getString("accessToken", "")).build())
                        .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        objIndividual = response.body().string();
                        System.out.println("======获取个人资料=====" + objIndividual + "=====");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(objIndividual);
                                    String error = object.optString("_ERROR_MESSAGE_");
                                    if (error.equals("")) {
                                        JSONObject objIndi = object.optJSONObject("individualInfor");
                                        tv_my_name.setText(objIndi.optString("connectionName").equals("null")
                                                ? "无名"
                                                : objIndi.optString("connectionName"));
                                        Picasso.with(mContext).load(objIndi.optString("logoPath"))
                                                .placeholder(R.drawable.noheader).error(R.drawable.noheader)
                                                .resize(100, 100).into(civ_my_header);
                                        renzheng = objIndi.optString("affirmStatus");
                                        tv_my_no.setText("账号：" + objIndi.optString("userLoginId"));
                                        editor.putString("idpic_front", objIndi.optString("credentialsImg1"));
                                        editor.putString("idpic_back", objIndi.optString("credentialsImg2"));
                                        editor.putString("idcard", objIndi.optString("certificateNumber"));
                                        editor.commit();
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
                                Toast.makeText(mContext, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }

    private void initViews() {
        tv_my_name = (TextView) view.findViewById(R.id.tv_my_name);
        tv_my_no = (TextView) view.findViewById(R.id.tv_my_no);
        tv_edit_data = (TextView) view.findViewById(R.id.tv_edit_data);
        rl_new_friends = (RelativeLayout) view.findViewById(R.id.rl_new_friends);
        rl_my_friends = (RelativeLayout) view.findViewById(R.id.rl_my_friends);
        rl_my_message = (RelativeLayout) view.findViewById(R.id.rl_my_message);
        rl_my_company = (RelativeLayout) view.findViewById(R.id.rl_my_company);
        rl_my_company_order = (RelativeLayout) view.findViewById(R.id.rl_my_company_order);
        rl_my_apply = (RelativeLayout) view.findViewById(R.id.rl_my_apply);
        rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);
        rl_title = (RelativeLayout) view.findViewById(R.id.rl_title);
        iv_my_code = (ImageView) view.findViewById(R.id.iv_my_code);
        civ_my_header = (CircleImageView) view.findViewById(R.id.civ_person_header);

        dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
        dialog.dismiss();
        sp = getActivity().getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        editor = sp.edit();

        tv_edit_data.setOnClickListener(this);
        rl_new_friends.setOnClickListener(this);
        rl_my_friends.setOnClickListener(this);
        rl_my_message.setOnClickListener(this);
        rl_my_company.setOnClickListener(this);
        rl_my_company_order.setOnClickListener(this);
        rl_my_apply.setOnClickListener(this);
        rl_setting.setOnClickListener(this);
        iv_my_code.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_EDITDATA:
                if (resultCode == RESULT_ENTER) {
                    getIndividual();
                } else {

                }
                break;
            case REQUEST_AUTHENTICATION:
                if (resultCode == RESULT_ENTER) {
                    getIndividual();
                } else {

                }

                break;
            case 600:
                if (resultCode == 100) {
                    getActivity().finish();
                }
                break;
            case 700:
                if (resultCode == 250 || resultCode == 251) {
                    getActivity().finish();
                } else {

                }
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_new_friends:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("type", "newfri");
                    intent1.setClass(mContext, PeopleSearchFriendsActivity.class);
                    startActivity(intent1);
                }
                break;
            case R.id.tv_edit_data:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent2 = new Intent();
                    intent2.putExtra("personData", objIndividual);
                    intent2.setClass(mContext, EditMydataActivity.class);
                    startActivityForResult(intent2, REQUEST_EDITDATA);
                }
                break;
            case R.id.rl_my_friends:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent3 = new Intent();
                    intent3.putExtra("type", "myfri");
                    intent3.setClass(mContext, PeopleSearchFriendsActivity.class);
                    startActivity(intent3);
                }
                break;
            case R.id.rl_my_message:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent4 = new Intent(mContext, PersonalAuthenticationActivity.class);
                    intent4.putExtra("renzheng", renzheng);
                    startActivityForResult(intent4, REQUEST_AUTHENTICATION);
                }
                break;
            case R.id.rl_my_company:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if (sp.getString("partyId", "").equals("")) {
                        Toast.makeText(mContext, "您还没有所属公司，可以搜索公司申请加入或者是自行申请注册公司", Toast.LENGTH_LONG).show();
                        Intent intent5 = new Intent(mContext, HavenoCompanyActivity.class);
                        // startActivityForResult(intent5, 600);
                        startActivity(intent5);
                    } else {
                        Intent intent5 = new Intent(mContext, MyCompanyActivity.class);
                        startActivity(intent5);
                    }
                }

                break;
            case R.id.rl_my_company_order:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if (sp.getString("isAdmin", "").equals("N")) {
                        Toast.makeText(mContext, "您不是管理员，无法查看公司成员", Toast.LENGTH_LONG).show();
                    } else if (sp.getString("partyId", "").equals("")) {
                        Toast.makeText(mContext, "您还没有所属公司，无法查看公司成员", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent6 = new Intent();
                        intent6.putExtra("type", "no");
                        intent6.setClass(mContext, PeopleSearchFriendsActivity.class);
                        startActivity(intent6);
                    }
                }

                break;
            case R.id.rl_my_apply:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if (sp.getString("partyId", "").equals("")) {
                        Toast.makeText(mContext, "您还没有所属公司，无法查看合作申请", Toast.LENGTH_LONG).show();
                    } else {
                        Intent intent7 = new Intent();
                        intent7.putExtra("type", "join");
                        intent7.setClass(mContext, PeopleSearchFriendsActivity.class);
                        startActivity(intent7);
                    }
                }
                break;
            case R.id.rl_setting:
                Intent intent8 = new Intent(mContext, SettingActivity.class);
                startActivityForResult(intent8, 700);
                break;
            case R.id.iv_my_code:
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    View menu_view = LayoutInflater.from(mContext).inflate(R.layout.pop_my_code, null);
                    ImageView iv_my_qr = (ImageView) menu_view.findViewById(R.id.iv_my_qr);
                    Bitmap qrcode = generateQRCode(sp.getString("username", ""));
                    iv_my_qr.setImageBitmap(qrcode);
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
                    WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                    int width = wm.getDefaultDisplay().getWidth();
                    int hight = wm.getDefaultDisplay().getHeight();
                    menu_pop.setWidth(width);
                    menu_pop.setHeight(hight);
                    menu_pop.showAsDropDown(rl_title);
                }

                break;
        }

    }

    // 文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
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
