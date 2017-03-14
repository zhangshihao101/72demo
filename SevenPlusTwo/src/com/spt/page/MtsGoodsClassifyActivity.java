package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsGoodsClassifyAdapter;
import com.spt.bean.ChildEntity;
import com.spt.bean.ParentEntity;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsGoodsClassifyActivity extends FragmentActivity {

    private ImageView iv_mts_back;
    private ExpandableListView elv_goods_classify;
    private ArrayList<ParentEntity> parentsData;
    private MtsGoodsClassifyAdapter adapter;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_goods_classify);
        super.onCreate(savedInstanceState);

        initViews();

        getClassify();

        elv_goods_classify.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                getChildClassify(groupPosition);

                return false;
            }
        });

        elv_goods_classify.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
                    long id) {

                Intent intent = new Intent();

                Bundle b = new Bundle();
                b.putString("child", parentsData.get(groupPosition).getChilds().get(childPosition).getGroupName());
                b.putString("childId", parentsData.get(groupPosition).getChilds().get(childPosition).getGroupId());
                intent.putExtras(b);

                setResult(501, intent);
                finish();

                return false;
            }
        });

        adapter.setOnTextClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                int position = (int) v.getTag();

                Intent intent = new Intent();
                Bundle b = new Bundle();
                b.putString("child", parentsData.get(position).getGroupName());
                b.putString("childId", parentsData.get(position).getGroupId());
                intent.putExtras(b);

                setResult(501, intent);
                finish();
            }
        });

        iv_mts_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(502);
                finish();
            }
        });
    }

    private void getClassify() {
        progressDialog.show();
        OkHttpManager.client
                .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_allclassify)
                        .post(new FormBody.Builder().add("externalLoginKey",
                                Localxml.search(MtsGoodsClassifyActivity.this, "externalloginkey")).build())
                        .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("=======" + "所有分类" + "========" + jsonStr + "=============");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                progressDialog.dismiss();

                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String isSuccess = object.optString("isSuccess");
                                    String msg = object.optString("_ERROR_MESSAGE_");
                                    if (isSuccess.equals("N")) {
                                        Toast.makeText(MtsGoodsClassifyActivity.this, "请求失败" + "\n" + msg,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        JSONArray array = object.optJSONArray("data");
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = (JSONObject) array.get(i);
                                            if (obj.optString("primaryParentCategoryId").equals("null")) {
                                                ParentEntity parent = new ParentEntity();

                                                parent.setGroupName(obj.optString("categoryName"));
                                                parent.setGroupId(obj.optString("productCategoryId"));
                                                
                                                

                                                parentsData.add(parent);
                                            }
                                        }
                                        adapter.notifyDataSetChanged();

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
                        progressDialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                progressDialog.dismiss();

                                Toast.makeText(MtsGoodsClassifyActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });


    }

    private void getChildClassify(final int position) {

        OkHttpManager.client
                .newCall(
                        new Request.Builder()
                                .url(MtsUrls.base + MtsUrls.get_childclassify).post(
                                        new FormBody.Builder()
                                                .add("externalLoginKey",
                                                        Localxml.search(MtsGoodsClassifyActivity.this,
                                                                "externalloginkey"))
                                                .add("primaryParentCategoryId", parentsData.get(position).getGroupId())
                                                .build())
                                .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("=======" + "二层分类" + "========" + jsonStr + "=============");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    JSONObject childobject = new JSONObject(jsonStr);
                                    String isSuccess = childobject.optString("isSuccess");
                                    String msg = childobject.optString("_ERROR_MESSAGE_");

                                    if (isSuccess.equals("N")) {
                                        Toast.makeText(MtsGoodsClassifyActivity.this, "请求失败" + "\n" + msg,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        JSONArray childarray = childobject.optJSONArray("data");
                                        if (childarray.length() != 0) {

                                            ArrayList<ChildEntity> Childs = new ArrayList<ChildEntity>();
                                            for (int i = 0; i < childarray.length(); i++) {

                                                JSONObject childitem = (JSONObject) childarray.get(i);
                                                ChildEntity child = new ChildEntity();

                                                child.setGroupName(childitem.optString("categoryName"));
                                                child.setGroupId(childitem.optString("productCategoryId"));

                                                Childs.add(child);
                                                // parentsData.get(position).getChilds().add(child);
                                            }
                                            parentsData.get(position).setChilds(Childs);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Intent intent = new Intent();
                                            Bundle b = new Bundle();
                                            b.putString("child", parentsData.get(position).getGroupName());
                                            b.putString("childId", parentsData.get(position).getGroupId());

                                            intent.putExtras(b);
                                            setResult(501, intent);

                                            finish();
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
                                Toast.makeText(MtsGoodsClassifyActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }

    private void initViews() {
        iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
        elv_goods_classify = (ExpandableListView) findViewById(R.id.elv_goods_classify);

        parentsData = new ArrayList<ParentEntity>();
        adapter = new MtsGoodsClassifyAdapter(MtsGoodsClassifyActivity.this, parentsData);
        elv_goods_classify.setAdapter(adapter);

        progressDialog = ProgressDialog.show(MtsGoodsClassifyActivity.this, "请稍候。。。", "获取数据中。。。", true);
        progressDialog.dismiss();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(502);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
