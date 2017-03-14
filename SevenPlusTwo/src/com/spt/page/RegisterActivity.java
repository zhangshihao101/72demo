package com.spt.page;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.spt.sht.R;
import com.spt.utils.CheckSoleUtil;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.MyUtil;
import com.spt.utils.OkHttpManager;
import com.spt.utils.SignUtil;
import com.spt.utils.VolleyHelper;
import com.spt.utils.VolleyHelper.OnCallBack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity implements OnClickListener {

    private TextView tv_register_get_code;
    private ImageView iv_register_back;
    private EditText et_register_phone, et_register_code;
    private Button btn_register_confirm;
    private TimeCount time;// 计时器

    private SharedPreferences spHome;
    private Editor editor;
    private String phone, code, device_token;
    private HashMap<String, String> param_sendMsg, parm_validateCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentID(R.layout.activity_register);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        tv_register_get_code = (TextView) findViewById(R.id.tv_register_get_code);
        iv_register_back = (ImageView) findViewById(R.id.iv_register_back);
        et_register_phone = (EditText) findViewById(R.id.et_register_phone);
        et_register_code = (EditText) findViewById(R.id.et_register_code);
        btn_register_confirm = (Button) findViewById(R.id.btn_register_confirm);

        time = new TimeCount(60000, 1000);

        spHome = this.getSharedPreferences("USERINFO", MODE_PRIVATE);
        editor = spHome.edit();
        device_token = spHome.getString("device_token", "");

        param_sendMsg = new HashMap<String, String>();
        parm_validateCode = new HashMap<String, String>();
    }

    @Override
    protected void addClickEvent() {
        iv_register_back.setOnClickListener(this);
        tv_register_get_code.setOnClickListener(this);
        btn_register_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        phone = et_register_phone.getText().toString();

        switch (v.getId()) {
            case R.id.iv_register_back:
                finish();
                break;
            case R.id.tv_register_get_code:

                long nowTime = System.currentTimeMillis();
                if ("".equals(phone)) {
                    Toast.makeText(RegisterActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    if (!MyUtil.isMobile(phone)) {
                        et_register_phone.setText("");
                        Toast.makeText(RegisterActivity.this, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
                    }
                    // else if (!CheckSoleUtil.checkSole("telephone_number", phone)) {
                    // et_register_phone.setText("");
                    // Toast.makeText(RegisterActivity.this, "该手机号已经存在", Toast.LENGTH_SHORT).show();
                    // }
                    else {
                        param_sendMsg.clear();
                        param_sendMsg.put("telNum", phone);
                        param_sendMsg.put("messageType", "register");
                        param_sendMsg.put("client_id", "localhost");

                        String url = MtsUrls.base + MtsUrls.sso_sendmsg + "?telNum=" + phone
                                + "&messageType=register&client_id=localhost&sign="
                                + SignUtil.genSign(param_sendMsg, "localhost");

                        OkHttpManager.client.newCall(new Request.Builder().url(url).build()).enqueue(new Callback() {

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                final String jsonStr = response.body().string();
                                System.out.println("=====" + "短信" + "======" + jsonStr + "=======");

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
                                                    Toast.makeText(RegisterActivity.this, "发送成功，注意查收",
                                                            Toast.LENGTH_SHORT).show();
                                                } else if (error.equals("110")) {
                                                    et_register_phone.setText("");
                                                    Toast.makeText(RegisterActivity.this, "该手机号已经注册，请重新输入",
                                                            Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(RegisterActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }


                            }

                            @Override
                            public void onFailure(Call arg0, IOException arg1) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {

                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub

                                    }
                                });

                            }
                        });

                        // time.start();
                        // VolleyHelper.get(MyConstant.SERVICENAME + MyConstant.REGISTERCODE +
                        // "&no=" + phone
                        // + "&device_token=" + device_token + "&gmtime=" + nowTime + "&str="
                        // + MyUtil.getMD5(device_token + nowTime + phone), new OnCallBack() {
                        //
                        // @Override
                        // public void OnSuccess(String data) {
                        // try {
                        // JSONObject object = new JSONObject(data);
                        // String error = object.optString("error");
                        // String msg = object.optString("msg");
                        // if (error.equals("1")) {
                        // Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                        // }
                        // } catch (JSONException e) {
                        // e.printStackTrace();
                        // }
                        // }
                        //
                        // @Override
                        // public void OnError(VolleyError volleyError) {
                        // Toast.makeText(RegisterActivity.this, "网络不好，请检查网络",
                        // Toast.LENGTH_SHORT).show();
                        // }
                        // });
                    }



                }
                break;
            case R.id.btn_register_confirm:
                code = et_register_code.getText().toString();

                if ("".equals(code)) {
                    Toast.makeText(RegisterActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    parm_validateCode.clear();
                    parm_validateCode.put("telNum", phone);
                    parm_validateCode.put("checkCode", code);
                    parm_validateCode.put("validateType", "register");
                    parm_validateCode.put("client_id", "localhost");

                    String url = MtsUrls.base + MtsUrls.sso_validatecode + "?telNum=" + phone + "&checkCode=" + code
                            + "&validateType=register&client_id=localhost&sign="
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
                                                    new Intent(RegisterActivity.this, SetPasswordActivity.class);
                                            intent.putExtra("phone", phone);
                                            startActivity(intent);
                                            finish();
                                        } else if (error.equals("109")) {
                                            Toast.makeText(RegisterActivity.this, "验证码错误，请重新输入", Toast.LENGTH_SHORT)
                                                    .show();
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
                                    Toast.makeText(RegisterActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    });



                    // VolleyHelper.get(MyConstant.SERVICENAME + MyConstant.CHECKCODE + "&no=" +
                    // phone + "&code=" + code,
                    // new OnCallBack() {
                    //
                    // @Override
                    // public void OnSuccess(String data) {
                    // try {
                    // JSONObject object = new JSONObject(data);
                    // String error = object.optString("error");
                    // String msg = object.optString("msg");
                    // if (error.equals("0")) {
                    // Intent intent =
                    // new Intent(RegisterActivity.this, SetPasswordActivity.class);
                    // intent.putExtra("phone", phone);
                    // startActivity(intent);
                    // } else {
                    // Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    // }
                    // } catch (JSONException e) {
                    // e.printStackTrace();
                    // }
                    // }
                    //
                    // @Override
                    // public void OnError(VolleyError volleyError) {
                    // Toast.makeText(RegisterActivity.this, "网络不好，请检查网络",
                    // Toast.LENGTH_SHORT).show();
                    // }
                    // });
                }
                break;
            default:
                break;
        }
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        // 计时过程显示
        @Override
        public void onTick(long millisUntilFinished) {
            tv_register_get_code.setClickable(false);
            tv_register_get_code.setText(millisUntilFinished / 1000 + "");
        }

        // 计时完毕触发
        @Override
        public void onFinish() {
            tv_register_get_code.setClickable(true);
            tv_register_get_code.setText("重新获取");
        }

    }

}
