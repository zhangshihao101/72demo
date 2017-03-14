package com.spt.page;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.fragment.JobFragment;
import com.spt.fragment.MineFragment;
import com.spt.fragment.PeopleFragment;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.MyUtil;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class NewHomeActivity extends FragmentActivity {

    private RadioGroup rgp_menu;
    private RadioButton rbt_job, rbt_people, rbt_mine;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private JobFragment jobFrg;
    private PeopleFragment peopleFrg;
    private MineFragment mineFrg;
    private long exitTime = 0;

    private int version;

    private SharedPreferences sp;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_home);
        super.onCreate(savedInstanceState);

        initView();

        getUserAuthority();
        getIndividual();

        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        version = info.versionCode;
        OkHttpManager.client.newCall(new Request.Builder().url(MyConstant.BASEIMG + MyConstant.UPDATE)
                .post(new FormBody.Builder().add("type", "android").add("version", version + "").build()).build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String error = object.optString("error");
                                    if (error.equals("0")) {
                                        JSONObject obj = object.optJSONObject("data");
                                        final String url = obj.optString("url");
                                        String info = obj.optString("info");
                                        if (obj.optString("must_update").equals("1")) {
                                            downLoadApk(NewHomeActivity.this, url);
                                        } else {
                                            if (obj.optString("has_new").equals("1")) {
                                                AlertDialog.Builder builder =
                                                        new AlertDialog.Builder(NewHomeActivity.this)
                                                                .setTitle("有新版本，是否更新").setMessage(info)
                                                                .setNegativeButton("否",
                                                                        new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                            }
                                                }).setPositiveButton("是", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        downLoadApk(NewHomeActivity.this, url);
                                                    }
                                                });
                                                builder.show();
                                            } else {

                                    }
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
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(NewHomeActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

        fragmentManager = getSupportFragmentManager();
        setTabSelection(1);

        rgp_menu.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbt_job.getId()) {
                    setTabSelection(0);
                    Drawable drawable1 = getResources().getDrawable(R.drawable.tabbar_icon1_hl);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    rbt_job.setCompoundDrawables(null, drawable1, null, null);
                    rbt_job.setTextColor(0xff2492da);

                    Drawable drawable2 = getResources().getDrawable(R.drawable.tabbar_icon2);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                    rbt_people.setCompoundDrawables(null, drawable2, null, null);
                    rbt_people.setTextColor(0xff969696);

                    Drawable drawable3 = getResources().getDrawable(R.drawable.tabbar_icon3);
                    drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                    rbt_mine.setCompoundDrawables(null, drawable3, null, null);
                    rbt_mine.setTextColor(0xff969696);

                } else if (checkedId == rbt_people.getId()) {
                    setTabSelection(1);
                    Drawable drawable4 = getResources().getDrawable(R.drawable.tabbar_icon2_hl);
                    drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
                    rbt_people.setCompoundDrawables(null, drawable4, null, null);
                    rbt_people.setTextColor(0xff2492da);

                    Drawable drawable5 = getResources().getDrawable(R.drawable.tabbar_icon1);
                    drawable5.setBounds(0, 0, drawable5.getMinimumWidth(), drawable5.getMinimumHeight());
                    rbt_job.setCompoundDrawables(null, drawable5, null, null);
                    rbt_job.setTextColor(0xff969696);

                    Drawable drawable6 = getResources().getDrawable(R.drawable.tabbar_icon3);
                    drawable6.setBounds(0, 0, drawable6.getMinimumWidth(), drawable6.getMinimumHeight());
                    rbt_mine.setCompoundDrawables(null, drawable6, null, null);
                    rbt_mine.setTextColor(0xff969696);

                } else if (checkedId == rbt_mine.getId()) {
                    setTabSelection(2);
                    Drawable drawable7 = getResources().getDrawable(R.drawable.tabbar_icon3_hl);
                    drawable7.setBounds(0, 0, drawable7.getMinimumWidth(), drawable7.getMinimumHeight());
                    rbt_mine.setCompoundDrawables(null, drawable7, null, null);
                    rbt_mine.setTextColor(0xff2492da);

                    Drawable drawable8 = getResources().getDrawable(R.drawable.tabbar_icon1);
                    drawable8.setBounds(0, 0, drawable8.getMinimumWidth(), drawable8.getMinimumHeight());
                    rbt_job.setCompoundDrawables(null, drawable8, null, null);
                    rbt_job.setTextColor(0xff969696);

                    Drawable drawable9 = getResources().getDrawable(R.drawable.tabbar_icon2);
                    drawable9.setBounds(0, 0, drawable9.getMinimumWidth(), drawable9.getMinimumHeight());
                    rbt_people.setCompoundDrawables(null, drawable9, null, null);
                    rbt_people.setTextColor(0xff969696);
                }
            }
        });
    }

    private void getUserAuthority() {
        OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_userauthority)
                .post(new FormBody.Builder().add("accessToken", sp.getString("accessToken", "")).build()).build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("======获取用户权限=====" + jsonStr + "=====");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String error = object.optString("_ERROR_MESSAGE_");
                                    if (error.equals("")) {
                                        String admin = object.optString("isAdmin");
                                        String partyId = object.optString("partyId");

                                        editor.putString("isAdmin", admin);
                                        editor.putString("partyId", partyId);
                                        editor.commit();

                                    } else {
                                        if (error.equals("100")) {
                                            Toast.makeText(NewHomeActivity.this, "参数错误", Toast.LENGTH_SHORT).show();
                                        } else if (error.equals("101")) {
                                            Toast.makeText(NewHomeActivity.this, "参数失效", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(NewHomeActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                                        }

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
                                Toast.makeText(NewHomeActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

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
                        final String objIndividual = response.body().string();
                        System.out.println("======首先获取个人资料=====" + objIndividual + "=====");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(objIndividual);
                                    String error = object.optString("_ERROR_MESSAGE_");
                                    if (error.equals("")) {
                                        JSONObject objIndi = object.optJSONObject("individualInfor");
                                        editor.putString("phoneNo", objIndi.optString("contactsTelephoneNumber"));
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
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(NewHomeActivity.this, "网络错误,请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }

    private void initView() {
        rgp_menu = (RadioGroup) findViewById(R.id.rgp_menu);
        rbt_job = (RadioButton) findViewById(R.id.rbt_job);
        rbt_people = (RadioButton) findViewById(R.id.rbt_people);
        rbt_mine = (RadioButton) findViewById(R.id.rbt_mine);

        sp = NewHomeActivity.this.getSharedPreferences("USERINFO", MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 从服务器中下载APK
     */
    private void downLoadApk(Context context, final String url) {
        final ProgressDialog pd; // 进度条对话框
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新，请稍候。。。");
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = MyUtil.getFileFromServer(url, pd);
                    sleep(3000);
                    installApk(file);
                    pd.dismiss(); // 结束掉进度条对话框
                } catch (Exception e) {
                    e.printStackTrace();
                    // MyUtil.ToastMessage(mHomeContext, "更新失败");
                    pd.dismiss();
                }
            }
        }.start();
    }

    /**
     * 安装apk
     */
    private void installApk(File file) {
        Intent intent = new Intent();
        // 执行动作
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     * 切换两个fragment且保存数据的方法
     * 
     * @param index
     */
    private void setTabSelection(int index) {
        fragmentTransaction = fragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (index) {
            case 0:
                if (jobFrg == null) {
                    jobFrg = new JobFragment();
                    fragmentTransaction.add(R.id.fl_frame, jobFrg, "jobF");
                } else {
                    fragmentTransaction.show(jobFrg);
                }
                break;

            case 1:
                if (peopleFrg == null) {
                    peopleFrg = new PeopleFragment();
                    fragmentTransaction.add(R.id.fl_frame, peopleFrg, "peopleF");
                } else {
                    fragmentTransaction.show(peopleFrg);
                }
                break;

            case 2:
                if (mineFrg == null) {
                    mineFrg = new MineFragment();
                    fragmentTransaction.add(R.id.fl_frame, mineFrg, "mineF");
                } else {
                    fragmentTransaction.show(mineFrg);
                }
                break;
        }
        fragmentTransaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     * 
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (jobFrg != null) {
            transaction.hide(jobFrg);
        }
        if (peopleFrg != null) {
            transaction.hide(peopleFrg);
        }
        if (mineFrg != null) {
            transaction.hide(mineFrg);
        }
    }

    // 屏蔽back键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // 判断间隔时间 大于2秒就退出应用
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                // 提示消息
                String msg = "再按一次返回桌面";
                MyUtil.ToastMessage(NewHomeActivity.this, msg);
                // 计算两次返回键按下的时间差
                exitTime = System.currentTimeMillis();
            } else {
                // 返回桌面操作
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
