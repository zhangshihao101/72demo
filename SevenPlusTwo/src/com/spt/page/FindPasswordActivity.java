package com.spt.page;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.controler.MyEditText;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 【找回密码】
 */
public class FindPasswordActivity extends BaseActivity {

    private MyTitleBar mtbFindTitle;
    private TextView tvTitle;
    private ImageView ivLeft;
    private LinearLayout llRight;
    private LinearLayout llLeft;
    private TextView tvPhone;
    private MyEditText metNewpsw;
    private MyEditText metAgain;
    private Button btnCommit;
    private Intent itFrom;
    private HashMap<String, String> param;
    private Intent itGet;
    private ProgressDialog progressDialog;
    private boolean isServiceRunning = false;
    private BroadcastReceiver brGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentID(R.layout.findpassword);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        this.mtbFindTitle = (MyTitleBar) findViewById(R.id.mtb_findPsw_title);
        this.tvTitle = mtbFindTitle.getTvTitle();
        this.ivLeft = mtbFindTitle.getIvLeft();
        this.tvTitle.setText("找回密码");
        this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
        this.llLeft = mtbFindTitle.getLlLeft();
        this.llRight = mtbFindTitle.getLlRight();
        this.llRight.setVisibility(View.INVISIBLE);
        this.itFrom = getIntent();
        this.tvPhone = (TextView) findViewById(R.id.tv_findPsw_phone);
        this.tvPhone.setText(itFrom.getStringExtra("phoneNo"));
        this.metNewpsw = (MyEditText) findViewById(R.id.met_findPsw_newPsw);
        this.metNewpsw.setMyEditInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        this.metAgain = (MyEditText) findViewById(R.id.met_findPsw_againNewPsw);
        this.metAgain.setMyEditInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        this.btnCommit = (Button) findViewById(R.id.btn_findPsw_commit);
        this.param = new HashMap<String, String>();
        this.itGet = new Intent(FindPasswordActivity.this, MyHttpGetService.class);
        this.itGet.setAction(MyConstant.HttpGetServiceAciton);
        this.progressDialog = ProgressDialog.show(FindPasswordActivity.this, "请稍候。。。", "获取数据中。。。", true);
        this.progressDialog.dismiss();
    }

    @Override
    protected void addClickEvent() {

        this.llLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 【提交】
        this.btnCommit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String phone = tvPhone.getText().toString();
                String newPsw = metNewpsw.getMyEditText();
                String againPsw = metAgain.getMyEditText();
                // 验证合法性
                if (phone.equals("")) {
                    metNewpsw.setMyEditText("");
                    metAgain.setMyEditText("");
                    MyUtil.ToastMessage(FindPasswordActivity.this, "手机号不能为空");
                } else if (newPsw.equals("") || againPsw.equals("")) {
                    MyUtil.ToastMessage(FindPasswordActivity.this, "新密码不能为空");
                } else if (!newPsw.equals(againPsw)) {
                    metNewpsw.setMyEditText("");
                    metAgain.setMyEditText("");
                    MyUtil.ToastMessage(FindPasswordActivity.this, "密码不一致");
                } else if (!MyUtil.isMobile(phone)) {
                    metNewpsw.setMyEditText("");
                    metAgain.setMyEditText("");
                    MyUtil.ToastMessage(FindPasswordActivity.this, "手机号不合法");
                } else {// 发送请求
                    progressDialog.show();
                    OkHttpManager.client
                            .newCall(
                                    new Request.Builder().url(MtsUrls.base + MtsUrls.find_psw)
                                            .post(new FormBody.Builder().add("telephoneNumber", phone)
                                                    .add("newPassword", newPsw).build())
                                            .build())
                            .enqueue(new Callback() {

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                return;
                            }
                            final String jsonStr = response.body().string();
                            System.out.println("=====找回结果=====" + jsonStr + "======");
                            new Handler(Looper.getMainLooper()).post(new Runnable() {

                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject jsonobject = new JSONObject(jsonStr);
                                        String success = jsonobject.optString("success");
                                        String error = jsonobject.optString("error");
                                        if (!success.equals("")) {
                                            Toast.makeText(FindPasswordActivity.this, "找回成功，请重新登录", Toast.LENGTH_LONG)
                                                    .show();
                                            setResult(MyConstant.RESULTCODE_11);
                                            FindPasswordActivity.this.finish();
                                        } else if (!error.equals("")) {
                                            Toast.makeText(FindPasswordActivity.this, "修改失败", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call arg0, IOException arg1) {
                            progressDialog.dismiss();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(FindPasswordActivity.this, "网络错误，请检查网络", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    });



                }
            }
        });
    }

}
