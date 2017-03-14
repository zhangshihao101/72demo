package com.spt.page;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.controler.MyEditText;
import com.spt.controler.MyTitleBar;
import com.spt.page.RegisterActivity.TimeCount;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;
import com.spt.utils.OkHttpManager;
import com.spt.utils.SignUtil;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 【验证手机】
 */
public class ValidatePhoneActivity extends BaseActivity {

    private MyTitleBar mtbValidateTitle;
    private TextView tvTitle;
    private ImageView ivLeft;
    private MyEditText metPhone;
    private LinearLayout llRight;
    private EditText etCode;
    private Button btnClick;
    private Button btnCommit;
    private HashMap<String, String> param_find;
    private String error_find;
    private String phone;
    private String msg_find;
    private Intent findgetcode;
    private boolean isServiceRunning = false;
    private LinearLayout llLeft;
    private ProgressDialog progressDialog;
    private Intent itFrom;
    private BroadcastReceiver brHttp_find;
    private boolean isFromHome = false;

    private TimeCount time;// 计时器
    private HashMap<String, String> parm_validateCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentID(R.layout.validatephone);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == MyConstant.RESULTCODE_11) {
            ValidatePhoneActivity.this.finish();
        }
    }

    /**
     * 初始化
     */
    @Override
    protected void init() {
        this.mtbValidateTitle = (MyTitleBar) findViewById(R.id.mtb_validate_title);
        this.tvTitle = mtbValidateTitle.getTvTitle();
        this.ivLeft = mtbValidateTitle.getIvLeft();
        this.tvTitle.setText("验证手机");
        this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
        this.llLeft = mtbValidateTitle.getLlLeft();
        this.llRight = mtbValidateTitle.getLlRight();
        this.llRight.setVisibility(View.INVISIBLE);
        this.metPhone = (MyEditText) findViewById(R.id.met_validate_phone);
        this.metPhone.setMyEditInputType(InputType.TYPE_CLASS_PHONE);
        this.metPhone.setMyEditHint("手机号");
        this.etCode = (EditText) findViewById(R.id.et_validate_code);
        this.btnClick = (Button) findViewById(R.id.btn_validate_btnClick);
        this.btnCommit = (Button) findViewById(R.id.btn_validate_commit);
        this.findgetcode = new Intent(ValidatePhoneActivity.this, MyHttpGetService.class);
        this.findgetcode.setAction(MyConstant.HttpGetServiceAciton);
        this.progressDialog = ProgressDialog.show(ValidatePhoneActivity.this, "请稍候。。。", "获取数据中。。。", true);
        this.progressDialog.dismiss();
        this.itFrom = getIntent();
        if (itFrom.hasExtra("phone")) {
            phone = itFrom.getStringExtra("phone");
            metPhone.setMyEditText(phone);
            isFromHome = true;
        }

        param_find = new HashMap<String, String>();
        time = new TimeCount(60000, 1000);
        parm_validateCode = new HashMap<String, String>();
    }

    @Override
    protected void addClickEvent() {

        this.llLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.btnClick.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                phone = metPhone.getMyEditText();
                if ("".equals(phone)) {
                    MyUtil.ToastMessage(ValidatePhoneActivity.this, "手机号码不能为空！");
                } else {
                    if (MyUtil.isMobile(phone)) {
                        sendMessage();
                    } else {
                        metPhone.setMyEditText("");
                        MyUtil.ToastMessage(ValidatePhoneActivity.this, "手机号格式错误，请检查后重新输入");
                    }
                }

            }
        });

        this.btnCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String code = etCode.getText().toString();
                phone = metPhone.getMyEditText();
                if (!"".equals(code) && !"".equals(phone)) {
                    parm_validateCode.clear();
                    parm_validateCode.put("telNum", phone);
                    parm_validateCode.put("checkCode", code);
                    parm_validateCode.put("validateType", "findBackPassword");
                    parm_validateCode.put("client_id", "localhost");

                    String url = MtsUrls.base + MtsUrls.sso_validatecode + "?telNum=" + phone + "&checkCode=" + code
                            + "&validateType=findBackPassword&client_id=localhost&sign="
                            + SignUtil.genSign(parm_validateCode, "localhost");

                    OkHttpManager.client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String jsonStr = response.body().string();
                            System.out.println("=======" + "验证结果" + "========" + jsonStr + "=============");

                            new Handler(Looper.getMainLooper()).post(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        JSONObject object = new JSONObject(jsonStr);

                                        String error = object.optString("error_code");
                                        String success = object.optString("success");
                                        if (success.equals("108")) {
                                            Intent intent =
                                                    new Intent(ValidatePhoneActivity.this, FindPasswordActivity.class);
                                            intent.putExtra("phoneNo", phone);
                                            startActivityForResult(intent, MyConstant.RESULTCODE_11);

                                        } else if (error.equals("109")) {
                                            Toast.makeText(ValidatePhoneActivity.this, "验证码错误，请重新输入",
                                                    Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(ValidatePhoneActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });

                } else {
                    MyUtil.ToastMessage(ValidatePhoneActivity.this, "手机号或者验证码为空");
                }

            }
        });
    }

    public void sendMessage() {

        param_find.clear();
        param_find.put("telNum", metPhone.getMyEditText());
        param_find.put("messageType", "findBackPassword");
        param_find.put("client_id", "localhost");

        String url = MtsUrls.base + MtsUrls.sso_sendmsg + "?telNum=" + metPhone.getMyEditText()
                + "&messageType=findBackPassword&client_id=localhost&sign=" + SignUtil.genSign(param_find, "localhost");

        OkHttpManager.client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsonStr = response.body().string();
                System.out.println("====找回密码发送短信===" + jsonStr);

                if (jsonStr != null && !"".equals(jsonStr)) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                JSONObject object = new JSONObject(jsonStr);
                                String error = object.optString("error_code");
                                String success = object.optString("success");
                                if (success.equals("108")) {
                                    time.start();
                                    Toast.makeText(ValidatePhoneActivity.this, "发送成功，注意查收", Toast.LENGTH_SHORT).show();
                                } else if (error.equals("111")) {
                                    metPhone.setMyEditText("");
                                    Toast.makeText(ValidatePhoneActivity.this, "手机号验证错误，请重新输入", Toast.LENGTH_SHORT)
                                            .show();
                                } else if (error.equals("106")) {
                                    metPhone.setMyEditText("");
                                    Toast.makeText(ValidatePhoneActivity.this, "两次输入时间间隔太短，请稍后再试", Toast.LENGTH_SHORT)
                                            .show();
                                } 

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(ValidatePhoneActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(ValidatePhoneActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        // 计时过程显示
        @Override
        public void onTick(long millisUntilFinished) {
            btnClick.setClickable(false);
            btnClick.setText(millisUntilFinished / 1000 + "");
        }

        // 计时完毕触发
        @Override
        public void onFinish() {
            btnClick.setClickable(true);
            btnClick.setText("重新获取");
        }

    }

}
