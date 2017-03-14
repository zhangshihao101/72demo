package com.spt.page;

import java.util.ArrayList;
import java.util.List;

import com.spt.adapter.MyViewPagerAdapter;
import com.spt.sht.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * 【欢迎splash】页
 */
public class WelcomeActivity extends BaseActivity {

    private ViewPager vpWelcome;
    private View vWelcome1;
    private View vWelcome2;
    private View vWelcome3;
    private View vWelcome4;
    private List<View> views;
    private SharedPreferences sp;
    private TextView tv;
    private String device_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentID(R.layout.welcome);
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化
     */
    @Override
    protected void init() {
        LayoutInflater inflater = LayoutInflater.from(WelcomeActivity.this);
        vpWelcome = (ViewPager) findViewById(R.id.vp_welcome_content);
        vWelcome1 = inflater.inflate(R.layout.welcomeitem1, null);
        vWelcome2 = inflater.inflate(R.layout.welcomeitem2, null);
        vWelcome3 = inflater.inflate(R.layout.welcomeitem3, null);
        vWelcome4 = inflater.inflate(R.layout.welcomeitem4, null);
        tv = (TextView) vWelcome4.findViewById(R.id.tv_welcome4);
        views = new ArrayList<View>();
        sp = WelcomeActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE);
        device_token = sp.getString("device_token", "");
        if ("".equals(device_token)) {
            for (int i = 0; i < 3; i++) {
                device_token = callUMengDeviceToken(WelcomeActivity.this);
            }
        }
    }

    @Override
    protected void addClickEvent() {
        views.add(vWelcome1);
        views.add(vWelcome2);
        views.add(vWelcome3);
        views.add(vWelcome4);
        // 映射数据
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(views);
        vpWelcome.setAdapter(adapter);

        tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Editor edit = sp.edit();
                edit.putBoolean("isSplash", true);
                edit.putString("device_token", device_token);
                edit.commit();
                // Intent it = new Intent(WelcomeActivity.this,
                // HomeActivity.class);
                Intent it = new Intent(WelcomeActivity.this, NewHomeActivity.class);
                startActivity(it);
                finish();
            }
        });
    }

}
