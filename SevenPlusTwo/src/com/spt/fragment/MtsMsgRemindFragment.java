package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsMsgRemindAdapter;
import com.spt.bean.MtsMsgNoticeInfo;
import com.spt.bean.MtsMsgRemindInfo;
import com.spt.page.MtsMsgDetailActivity;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.spt.utils.TimeUtils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsMsgRemindFragment extends Fragment {

    private View view;
    private Context mContext;

    private ProgressDialog dialog;

    private ListView lv_remid;
    private List<MtsMsgRemindInfo> mList;
    private MtsMsgRemindAdapter mAdapter;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_mts_msg_remind, null);

        initView();

        initData();

        initListener();

        return view;
    }

    private void initListener() {
        lv_remid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, MtsMsgDetailActivity.class);
                intent.putExtra("title", mList.get(position).getTitle());
                intent.putExtra("time", mList.get(position).getTime());
                intent.putExtra("content", mList.get(position).getContent());
                startActivity(intent);
            }
        });
    }

    private void initData() {
        dialog.show();
        OkHttpManager.client
                .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.getMessageListInterfaceForTerminal)
                        .post(new FormBody.Builder()
                                .add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
                                .add("isHtml", "N").add("type", "2").build())
                        .build())
                .enqueue(new Callback() {

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
                                dialog.dismiss();
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    JSONArray array = object.optJSONArray("messageList");
                                    if (array == null || array.length() == 0) {
                                        Toast.makeText(mContext, "暂无新提醒", Toast.LENGTH_SHORT).show();
                                    } else {
                                        for (int i = 0; i < array.length(); i++) {
                                            MtsMsgRemindInfo remindInfo = new MtsMsgRemindInfo();
                                            JSONObject obj = array.optJSONObject(i);
                                            remindInfo.setTitle(obj.optString("title"));
                                            remindInfo.setContent(obj.optString("content"));
                                            JSONObject obj2 = obj.optJSONObject("createdDateTime");
                                            remindInfo.setTime(TimeUtils.stampToDate(obj2.optString("time")));

                                            mList.add(remindInfo);
                                            mAdapter.notifyDataSetChanged();
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
                                Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

    }

    private void initView() {

        lv_remid = (ListView) view.findViewById(R.id.lv_remind);
        mList = new ArrayList<MtsMsgRemindInfo>();
        mAdapter = new MtsMsgRemindAdapter(mContext, mList);
        lv_remid.setAdapter(mAdapter);

        dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
        dialog.dismiss();
    }

}
