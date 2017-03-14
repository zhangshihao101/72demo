package com.spt.page;

import com.spt.sht.R;
import com.spt.utils.MyConstant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SettingActivity extends FragmentActivity implements OnClickListener {

    private ImageView iv_setting_back;
    private RelativeLayout rl_changpsw, rl_contact, rl_exit;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_setting);
        super.onCreate(arg0);

        initViews();
    }

    private void initViews() {
        iv_setting_back = (ImageView) findViewById(R.id.iv_setting_back);
        rl_changpsw = (RelativeLayout) findViewById(R.id.rl_changpsw);
        rl_contact = (RelativeLayout) findViewById(R.id.rl_contact);
        rl_exit = (RelativeLayout) findViewById(R.id.rl_exit);
        sp = SettingActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);

        iv_setting_back.setOnClickListener(this);
        rl_changpsw.setOnClickListener(this);
        rl_contact.setOnClickListener(this);
        rl_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_setting_back:
                setResult(1000);
                finish();
                break;

            case R.id.rl_changpsw:
                Intent changepas = new Intent(SettingActivity.this, ChangePassWordActivity.class);
                changepas.putExtra("token", sp.getString("token", ""));
                changepas.putExtra("account", sp.getString("userName", ""));
                startActivityForResult(changepas, 0);
                break;
            case R.id.rl_contact:
                Uri uri = Uri.parse("tel:" + "4009000702");
                Intent contact = new Intent();
                // it.setAction(Intent.ACTION_CALL);//直接拨打电话
                contact.setAction(Intent.ACTION_DIAL);// 调用软件盘方式拨打电话
                contact.setData(uri);
                startActivity(contact);
                break;
            case R.id.rl_exit:
                setResult(251);
                finish();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        switch (requestCode) {
            case 0:
                if (resultCode == MyConstant.RESULTCODE_10) {
                    setResult(250);
                    finish();
                }
                break;

            default:
                break;
        }
        
        
    }

}
