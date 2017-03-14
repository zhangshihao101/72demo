package com.spt.page;

import com.spt.sht.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class DisLoginActivity extends FragmentActivity {

    private ImageView iv_mts_back, iv_clear_urn, iv_clear_psw, iv_logo;
    private EditText et_urn, et_psw;
    private Button btn_dis_login;
    private CheckBox cb_remain;

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dis_login);
        super.onCreate(arg0);

        initViews();
    }

    private void initViews() {
        iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
        iv_clear_urn = (ImageView) findViewById(R.id.iv_clear_urn);
        iv_clear_psw = (ImageView) findViewById(R.id.iv_clear_psw);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        et_urn = (EditText) findViewById(R.id.et_urn);
        et_psw = (EditText) findViewById(R.id.et_psw);
        btn_dis_login = (Button) findViewById(R.id.btn_dis_login);
        cb_remain = (CheckBox) findViewById(R.id.cb_remain);
    }

}
