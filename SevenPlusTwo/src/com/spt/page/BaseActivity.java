package com.spt.page;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public abstract class BaseActivity extends FragmentActivity {
    private int contentID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置全屏
        setContentView(contentID);

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        // // 透明状态栏
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // // 透明导航栏
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // }

        init();// 初始化
        addClickEvent();// 添加点击事件

    }

    @Override
    protected void onResume() {
        setVerticalScreen();// 设置竖屏
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    /**
     * 设置竖屏
     */
    private void setVerticalScreen() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 请求友盟的device_token
     * 
     * @param context
     * @return device_token
     */
    public String callUMengDeviceToken(Context context) {
        PushAgent pushAgent = PushAgent.getInstance(context);
        pushAgent.enable();
        String device_token = UmengRegistrar.getRegistrationId(context);

        return device_token;
    }

    public void setContentID(int contentID) {
        this.contentID = contentID;
    }

    /**
     * 初始化
     */
    protected abstract void init();

    /**
     * 添加点击事件
     */
    protected abstract void addClickEvent();

}
