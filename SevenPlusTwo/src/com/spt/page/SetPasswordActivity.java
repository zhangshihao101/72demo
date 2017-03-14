package com.spt.page;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.spt.utils.SignUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class SetPasswordActivity extends BaseActivity {

    private ImageView iv_set_password_back;
    private EditText et_set_psw, et_set_rpsw;
    private Button btn_set_password_confirm;
    private String phone, psw, rpsw;
    private HashMap<String, String> params;
    private ProgressDialog dialog;

    /**
     * 正则表达式:验证密码(不包含特殊字符)
     */
    public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentID(R.layout.activity_set_password);
        super.onCreate(savedInstanceState);
        phone = getIntent().getStringExtra("phone");
        Log.e("phone", phone);
    }

    @Override
    protected void init() {
        iv_set_password_back = (ImageView) findViewById(R.id.iv_set_password_back);
        et_set_psw = (EditText) findViewById(R.id.et_set_psw);
        et_set_rpsw = (EditText) findViewById(R.id.et_set_rpsw);
        btn_set_password_confirm = (Button) findViewById(R.id.btn_set_password_confirm);
        params = new HashMap<String, String>();

        dialog = ProgressDialog.show(SetPasswordActivity.this, "请稍候。。。", "获取数据中。。。", true);
        dialog.dismiss();
    }

    @Override
    protected void addClickEvent() {
        iv_set_password_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_set_password_confirm.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                psw = et_set_psw.getText().toString();
                rpsw = et_set_rpsw.getText().toString();
                params.put("userLoginId", phone);
                params.put("password", psw);
                params.put("passwordVerify", rpsw);
                params.put("telephoneNumber", phone);
                params.put("client_id", "localhost");

                if (psw.equals("")) {
                    Toast.makeText(SetPasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (rpsw.equals("")) {
                    Toast.makeText(SetPasswordActivity.this, "请重新输入密码", Toast.LENGTH_SHORT).show();
                } else if (!isPassword(psw) || (!isPassword(rpsw))) {
                    Toast.makeText(SetPasswordActivity.this, "输入格式不对，请重新输入", Toast.LENGTH_SHORT).show();
                } else if (!psw.equals(rpsw)) {
                    // et_set_psw.setText("");
                    // et_set_rpsw.setText("");
                    Toast.makeText(SetPasswordActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                } else {

                    OkHttpManager.client
                            .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.sso_register)
                                    .post(new FormBody.Builder().add("userLoginId", phone).add("password", psw)
                                            .add("passwordVerify", rpsw).add("telephoneNumber", phone)
                                            .add("client_id", "localhost")
                                            .add("sign", SignUtil.genSign(params, "localhost")).build())
                                    .build())
                            .enqueue(new Callback() {

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String jsonStr = response.body().string();
                            System.out.println("======注册结果====" + jsonStr + "=====");
                            dialog.dismiss();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        JSONObject object = new JSONObject(jsonStr);
                                        String error = object.optString("error_code");
                                        String success = object.optString("success");

                                        if (success.equals("110")) {
                                            Toast.makeText(SetPasswordActivity.this, "注册成功,请登录", Toast.LENGTH_SHORT)
                                                    .show();
                                            Intent intent = new Intent();
                                            intent.setClass(SetPasswordActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (error.equals("112") || error.equals("109")) {
                                            Toast.makeText(SetPasswordActivity.this, "注册失败" + "\n" + "手机号或用户名重复",
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
                            dialog.dismiss();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub

                                }
                            });

                        }
                    });



                    // VolleyHelper.post(MyConstant.SERVICENAME + MyConstant.CHECKANDREGIS, params,
                    // new OnCallBack() {
                    //
                    // @Override
                    // public void OnSuccess(String data) {
                    // try {
                    // JSONObject object = new JSONObject(data);
                    // String error = object.optString("error");
                    // String msg = object.optString("msg");
                    // Log.e("errAndmsg", error + msg);
                    // if (error.equals("0")) {
                    // Toast.makeText(SetPasswordActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    // Intent intent = new Intent(SetPasswordActivity.this, LoginActivity.class);
                    // startActivity(intent);
                    // } else {
                    // Toast.makeText(SetPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();
                    // }
                    // } catch (JSONException e) {
                    // e.printStackTrace();
                    // }
                    // }
                    //
                    // @Override
                    // public void OnError(VolleyError volleyError) {
                    // Toast.makeText(SetPasswordActivity.this, "网络不好，请检查网络",
                    // Toast.LENGTH_SHORT).show();
                    // }
                    // });
                }

            }
        });

    }

    /**
     * 校验密码
     * 
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

}
