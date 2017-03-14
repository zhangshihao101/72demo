package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.bean.MtsMsgNoticeInfo;
import com.spt.bean.MtsMsgRemindInfo;
import com.spt.page.ApplyDisActivity;
import com.spt.page.ChangePassWordActivity;
import com.spt.page.DistributionActivity;
import com.spt.page.HomePageActivity;
import com.spt.page.MtsMainActivity;
import com.spt.page.MtsMsgActivity;
import com.spt.page.MtsPerfectionMessageActivity;
import com.spt.page.SptLoginActivity;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;
import com.spt.utils.TimeUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

public class JobFragment extends Fragment {

    private View view;
    private static Context mContext;

    private RelativeLayout rl_metasolo, rl_notice, rl_dis, rl_spt, RelativeLayout1, RelativeLayout2, RelativeLayout3;
    private TextView tv_mts_sale, tv_mts_stock, tv_mts_goods, tv_order_money_t, tv_order_money_y, tv_order_money_s,
            tv_order_count_t, tv_order_count_y, tv_order_count_s, tv_dis_logo, tv_notice_text1, tv_notice_text2;
    private ImageView iv_job_menu;
    private boolean isMsg, isSys, isNew;

    private List<String> sumList, countList;

    private static ProgressDialog dialog;

    private SharedPreferences spHome;
    private Editor editor;

    private String userName;// 用户名
    private String mtsUserName;// 源一云商用户名
    private String mtsPas;// 源一云商密码
    private String authority; // 源一权限
    private String externalLoginKey;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_job, null);

        intiView();

        String uri = MtsUrls.base + MtsUrls.login;
        OkHttpManager.client
                .newCall(
                        new Request.Builder().url(uri)
                                .post(new FormBody.Builder().add("accessToken", spHome.getString("accessToken", ""))
                                        .add("terminalInfo", getTerminalInfo()).build())
                                .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("======源一登录====" + jsonStr + "=====");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject obj = new JSONObject(jsonStr);
                                    externalLoginKey = obj.optString("externalLoginKey");
                                    authority = obj.optString("flag");
                                    if (!externalLoginKey.equals("")) {
                                        Localxml.save(mContext, "externalloginkey", externalLoginKey);
                                        RelativeLayout1.setVisibility(View.VISIBLE);
                                        RelativeLayout2.setVisibility(View.VISIBLE);
                                        RelativeLayout3.setVisibility(View.VISIBLE);
                                        isMsg = true;
                                        //
                                        OkHttpManager.client
                                                .newCall(new Request.Builder()
                                                        .url(MtsUrls.base + MtsUrls.getMessageListInterfaceForTerminal)
                                                        .post(new FormBody.Builder()
                                                                .add("externalLoginKey", externalLoginKey)
                                                                .add("isHtml", "N").add("type", "1").build())
                                                        .build())
                                                .enqueue(new Callback() {

                                            @Override
                                            public void onResponse(Call arg0, Response response) throws IOException {
                                                if (!response.isSuccessful()) {
                                                    return;
                                                }
                                                final String jsonStr = response.body().string();
                                                System.out.println("===系统===" + jsonStr + "====");
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        try {
                                                            JSONObject object = new JSONObject(jsonStr);
                                                            String error = object.optString("");

                                                            JSONArray array = object.optJSONArray("messageList");
                                                            if (array == null || array.length() == 0) {
                                                                tv_notice_text1.setText("暂无系统消息");
                                                                isSys = false;
                                                            } else {
                                                                JSONObject obj = array.optJSONObject(0);
                                                                tv_notice_text1.setText(obj.optString("title"));
                                                                isSys = true;
                                                            }

                                                            // noticeInfo.setTitle(obj.optString("title"));
                                                            // noticeInfo.setContent(obj.optString("content"));
                                                            // JSONObject obj2 =
                                                            // obj.optJSONObject("createdDateTime");
                                                            // noticeInfo.setTime(TimeUtils.stampToDate(obj2.optString("time")));

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
                                                        Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                });
                                            }
                                        });

                                        //
                                        OkHttpManager.client.newCall(new Request.Builder()
                                                .url(MtsUrls.base
                                                        + MtsUrls.getMessageListInterfaceForTerminal)
                                                .post(new FormBody.Builder()
                                                        .add("externalLoginKey",
                                                                Localxml.search(mContext, "externalloginkey"))
                                                        .add("isHtml", "N").add("type", "2").build())
                                                .build()).enqueue(new Callback() {

                                            @Override
                                            public void onResponse(Call arg0, Response response) throws IOException {
                                                if (!response.isSuccessful()) {
                                                    return;
                                                }
                                                final String jsonStr = response.body().string();
                                                System.out.println("===提醒===" + jsonStr + "====");
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        try {
                                                            JSONObject object = new JSONObject(jsonStr);
                                                            JSONArray array = object.optJSONArray("messageList");
                                                            if (array == null || array.length() == 0) {
                                                                tv_notice_text2.setText("暂无新提醒");
                                                                isNew = false;
                                                            } else {
                                                                JSONObject obj = array.optJSONObject(0);
                                                                tv_notice_text2.setText(obj.optString("title"));
                                                                isNew = true;
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
                                                        Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT)
                                                                .show();
                                                    }
                                                });
                                            }
                                        });

                                        //

                                        OkHttpManager.client.newCall(
                                                new Request.Builder().url(MtsUrls.base + MtsUrls.get_orderstatistics)
                                                        .post(new FormBody.Builder()
                                                                .add("externalLoginKey", externalLoginKey).build())
                                                .build()).enqueue(new Callback() {

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {
                                                if (!response.isSuccessful()) {
                                                    return;
                                                }
                                                final String jsonStr = response.body().string();
                                                System.out.println("======每天数据====" + jsonStr + "=====");
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        try {
                                                            JSONObject jsonobject = new JSONObject(jsonStr);
                                                            JSONArray sumArray = jsonobject.optJSONArray("listGt");
                                                            JSONArray countArray = jsonobject.optJSONArray("listRst");

                                                            if (sumArray != null && sumArray.length() != 0) {
                                                                for (int i = 0; i < sumArray.length(); i++) {
                                                                    sumList.add(sumArray.getString(i));
                                                                    countList.add(countArray.getString(i));

                                                                }

                                                                tv_order_money_t.setText("￥" + sumList.get(0));
                                                                tv_order_money_y.setText("￥" + sumList.get(1));
                                                                tv_order_money_s.setText("￥" + sumList.get(2));
                                                                tv_order_count_t.setText(countList.get(0) + "笔");
                                                                tv_order_count_y.setText(countList.get(1) + "笔");
                                                                tv_order_count_s.setText(countList.get(2) + "笔");

                                                            } else {
                                                                RelativeLayout1.setVisibility(View.GONE);
                                                                RelativeLayout2.setVisibility(View.GONE);
                                                                RelativeLayout3.setVisibility(View.GONE);
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
                                                        Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();

                                                    }
                                                });

                                            }
                                        });
                                    } else if (!authority.equals("")) {
                                        RelativeLayout1.setVisibility(View.GONE);
                                        RelativeLayout2.setVisibility(View.GONE);
                                        RelativeLayout3.setVisibility(View.GONE);

                                        tv_notice_text1.setText("暂无系统消息");
                                        tv_notice_text2.setText("暂无新提醒");
                                        isMsg = false;
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
                                Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();

                            }
                        });
                    }
                });

        rl_spt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(mContext, SptLoginActivity.class);
                    startActivity(intent);
                } else {

                }
            }
        });

        rl_dis.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.show();
                OkHttpManager.client.newCall(new Request.Builder()
                        .url(MyConstant.SERVICENAME + MyConstant.CHOSEPLATFORM).post(new FormBody.Builder()
                                .add("token", spHome.getString("accessToken", "")).add("version", "2.1").build())
                        .build()).enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("=======代销权限====" + jsonStr);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                dialog.dismiss();
                                try {
                                    JSONObject jsonobject = new JSONObject(jsonStr);
                                    String error = jsonobject.optString("error");
                                    String msg = jsonobject.optString("msg");
                                    if (error.equals("1")) {
                                        if (msg.equals("token失效")) {
                                            editor.remove("accessToken");
                                            editor.commit();
                                            ((Activity) mContext).finish();
                                            Toast.makeText(mContext, msg + ",请重新登录", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        JSONObject obj = jsonobject.optJSONObject("data");
                                        String dis = obj.optString("dis");
                                        String reason = obj.optString("reject_reason");

                                        if (dis.equals("3")) {
                                            Intent apply = new Intent(mContext, ApplyDisActivity.class);
                                            startActivityForResult(apply, 0);
                                        } else if (dis.equals("0")) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                            builder.setMessage("您的申请正在审核，请稍后重试");
                                            builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            });
                                            builder.show();
                                        } else if (dis.equals("1")) {
                                            Intent intent = new Intent(mContext, DistributionActivity.class);
                                            startActivity(intent);
                                        } else if (dis.equals("2")) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                            builder.setMessage("您的代销申请已被拒绝" + "\n拒绝理由：" + reason);
                                            builder.setNegativeButton("我知道了", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(mContext, ApplyDisActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                            builder.show();
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
                        dialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }
        });

        rl_metasolo.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!NoDoubleClickUtils.isDoubleClick()) {
                    if (!externalLoginKey.equals("")) {
                        Intent intent = new Intent(mContext, MtsMainActivity.class);
                        startActivity(intent);
                    } else if (!authority.equals("")) {
                        Intent intent = new Intent(mContext, MtsPerfectionMessageActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }

            }
        });

        tv_mts_sale.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!externalLoginKey.equals("")) {
                    Intent intent = new Intent(mContext, MtsMainActivity.class);
                    intent.putExtra("page", 0);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, "您目前没有源一云商的权限", Toast.LENGTH_LONG).show();
                }
            }
        });

        tv_mts_stock.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!externalLoginKey.equals("")) {
                    Intent intent = new Intent(mContext, MtsMainActivity.class);
                    intent.putExtra("page", 1);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, "您目前没有源一云商的权限", Toast.LENGTH_LONG).show();
                }
            }
        });

        tv_mts_goods.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!externalLoginKey.equals("")) {
                    Intent intent = new Intent(mContext, MtsMainActivity.class);
                    intent.putExtra("page", 2);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, "您目前没有源一云商的权限", Toast.LENGTH_LONG).show();
                }
            }
        });

        rl_notice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // if (isMsg) {
                // Intent intent = new Intent(mContext, MtsMsgActivity.class);
                // startActivity(intent);
                // } else {
                // Toast.makeText(mContext, "您目前没有源一云商权限或没有消息，无法查看",
                // Toast.LENGTH_LONG).show();
                // }

                if ((!isMsg) & (!isSys) & (!isNew)) {
                    Toast.makeText(mContext, "您目前没有源一云商权限或没有消息，无法查看", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(mContext, MtsMsgActivity.class);
                    startActivity(intent);
                }
            }
        });

        iv_job_menu.setOnClickListener(new OnClickListener() {

            @SuppressLint("InflateParams")
            @Override
            public void onClick(View v) {
                // 自定义一个布局来展示内容
                View menu_view = LayoutInflater.from(mContext).inflate(R.layout.pop_menu, null);
                TextView tv_pop_changepas = (TextView) menu_view.findViewById(R.id.tv_pop_changepas);
                TextView tv_pop_contact = (TextView) menu_view.findViewById(R.id.tv_pop_contact);
                TextView tv_pop_exit = (TextView) menu_view.findViewById(R.id.tv_pop_exit);
                final PopupWindow menu_pop =
                        new PopupWindow(menu_view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                // 点击外边可让popupwindow消失
                menu_pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_select));
                menu_pop.setOutsideTouchable(true);
                // 获取焦点，否则无法点击
                menu_pop.setFocusable(true);
                // 设置popupwindow显示位置
                menu_pop.showAsDropDown(v);

                // 修改密码
                tv_pop_changepas.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent changepas = new Intent(mContext, ChangePassWordActivity.class);
                        changepas.putExtra("token", spHome.getString("token", ""));
                        changepas.putExtra("account", userName);
                        startActivityForResult(changepas, 0);
                        menu_pop.dismiss();
                    }
                });

                // 联系客服
                tv_pop_contact.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("tel:" + "4009000702");
                        Intent contact = new Intent();
                        // it.setAction(Intent.ACTION_CALL);//直接拨打电话
                        contact.setAction(Intent.ACTION_DIAL);// 调用软件盘方式拨打电话
                        contact.setData(uri);
                        startActivity(contact);
                        menu_pop.dismiss();
                    }
                });

                // 退出登录
                tv_pop_exit.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        ((Activity) mContext).finish();
                        menu_pop.dismiss();
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == MyConstant.RESULTCODE_10) {
                    ((Activity) mContext).finish();
                }

                break;
            case 1:
                if (resultCode == 100) {
                    ((Activity) mContext).finish();
                }
                break;

            default:
                break;
        }
    }

    private String getTerminalInfo() {

        JSONObject allObj = new JSONObject();
        try {
            allObj.putOpt("terminalType", "PHONE");
            allObj.putOpt("isCoexist", "N");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allObj.toString();
    }

    private void intiView() {
        rl_metasolo = (RelativeLayout) view.findViewById(R.id.rl_metasolo);
        rl_notice = (RelativeLayout) view.findViewById(R.id.rl_notice);
        rl_dis = (RelativeLayout) view.findViewById(R.id.rl_dis);
        rl_spt = (RelativeLayout) view.findViewById(R.id.rl_spt);
        iv_job_menu = (ImageView) view.findViewById(R.id.iv_job_menu);
        tv_dis_logo = (TextView) view.findViewById(R.id.tv_dis_logo);
        tv_order_money_t = (TextView) view.findViewById(R.id.tv_order_money_t);
        tv_order_money_y = (TextView) view.findViewById(R.id.tv_order_money_y);
        tv_order_money_s = (TextView) view.findViewById(R.id.tv_order_money_s);
        tv_order_count_t = (TextView) view.findViewById(R.id.tv_order_count_t);
        tv_order_count_y = (TextView) view.findViewById(R.id.tv_order_count_y);
        tv_order_count_s = (TextView) view.findViewById(R.id.tv_order_count_s);
        RelativeLayout1 = (RelativeLayout) view.findViewById(R.id.RelativeLayout1);
        RelativeLayout2 = (RelativeLayout) view.findViewById(R.id.RelativeLayout2);
        RelativeLayout3 = (RelativeLayout) view.findViewById(R.id.RelativeLayout3);
        tv_notice_text1 = (TextView) view.findViewById(R.id.tv_notice_text1);
        tv_notice_text2 = (TextView) view.findViewById(R.id.tv_notice_text2);

        TextPaint tp = tv_dis_logo.getPaint();
        tp.setFakeBoldText(true);

        sumList = new ArrayList<String>();
        countList = new ArrayList<String>();

        tv_mts_sale = (TextView) view.findViewById(R.id.tv_mts_sale);
        tv_mts_stock = (TextView) view.findViewById(R.id.tv_mts_stock);
        tv_mts_goods = (TextView) view.findViewById(R.id.tv_mts_goods);

        dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
        dialog.dismiss();
        spHome = getActivity().getSharedPreferences("USERINFO", 0x0000);
        editor = spHome.edit();
        userName = spHome.getString("userName", "");
    }
}
