package com.spt.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsSharePriceEditAdapter;
import com.spt.bean.MtsBarcodeInfo;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.umeng.socialize.utils.Log;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsSharepriceEditActivity extends FragmentActivity {

    private ImageView iv_mts_back;
    private ListView lv_barcode_edit;
    private TextView tv_submit;

    private ProgressDialog progressDialog;

    private List<MtsBarcodeInfo> mData;
    // private Double mPrice[];
    private MtsSharePriceEditAdapter adapter = null;

    private Intent intent;
    private String price = "", productId = "", productColor = "", productSize = "";
    private Double mPrice = 0.00;
    private int position = -1;

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shareprice_edit);
        super.onCreate(arg0);

        intent = getIntent();
        price = intent.getStringExtra("price");
        productId = intent.getStringExtra("proId");

        initView();

        initData();

        iv_mts_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(102);
                finish();
            }
        });

        tv_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                updataPrice();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
                if (resultCode == 0) {
                    Bundle b = data.getExtras();
                    mPrice = b.getDouble("value");
                    position = b.getInt("position");

                    MtsBarcodeInfo info = new MtsBarcodeInfo();
                    info.setProprice(mPrice);
                    info.setProId(mData.get(position).getProId());
                    info.setProColor(mData.get(position).getProColor());
                    info.setProSize(mData.get(position).getProSize());

                    mData.set(position, info);
                    adapter.notifyDataSetChanged();
                } else {

                }

                break;
        }
    }

    private void initView() {
        iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
        lv_barcode_edit = (ListView) findViewById(R.id.lv_barcode_edit);
        tv_submit = (TextView) findViewById(R.id.tv_submit);

        mData = new ArrayList<MtsBarcodeInfo>();
        adapter = new MtsSharePriceEditAdapter(MtsSharepriceEditActivity.this, mData);
        lv_barcode_edit.setAdapter(adapter);

        progressDialog = ProgressDialog.show(MtsSharepriceEditActivity.this, "请稍候。。。", "获取数据中。。。", true);
        progressDialog.dismiss();
    }

    private void initData() {
        try {
            JSONArray arrayPrice = new JSONArray(price);
            // mPrice = new Double[arrayPrice.length()];
            for (int i = 0; i < arrayPrice.length(); i++) {
                JSONObject objPrice = (JSONObject) arrayPrice.get(i);

                MtsBarcodeInfo info = new MtsBarcodeInfo();
                info.setProId(objPrice.optString("productId"));
                info.setProColor(objPrice.optString("colorDesc"));
                info.setProSize(objPrice.optString("dimensionDesc"));
                info.setProprice(String.valueOf(objPrice.optDouble("sharePrice")).equals("null")
                        ? 0
                        : objPrice.optDouble("sharePrice"));

                mData.add(info);
                // mPrice[i] =
                // String.valueOf(objPrice.optDouble("price")).equals("null")?0:objPrice.optDouble("price");
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void updataPrice() {
        progressDialog.show();

        OkHttpManager.client
                .newCall(
                        new Request.Builder()
                                .url(MtsUrls.base + MtsUrls.update_shareprice).post(
                                        new FormBody.Builder()
                                                .add("externalLoginKey",
                                                        Localxml.search(MtsSharepriceEditActivity.this,
                                                                "externalloginkey"))
                                                .add("productId", productId).add("variants", getVariants()).build())
                        .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();

                        progressDialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String isSuccess = object.optString("isSuccess");
                                    String message = object.optString("_ERROR_MESSAGE_");
                                    if (isSuccess.equals("Y")) {
                                        Toast.makeText(MtsSharepriceEditActivity.this, "更新成功！", Toast.LENGTH_SHORT)
                                                .show();
                                        setResult(101);
                                        finish();
                                    } else {
                                        Toast.makeText(MtsSharepriceEditActivity.this, "更新失败" + "\n" + message,
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
                        progressDialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(MtsSharepriceEditActivity.this, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });



    }

    public String getVariants() {
        JSONArray varArray = new JSONArray();
        try {
            for (int i = 0; i < mData.size(); i++) {
                JSONObject varObjact = new JSONObject();

                varObjact.put("variantId", mData.get(i).getProId());
                varObjact.put("sharePrice", mData.get(i).getProprice());
                varArray.put(varObjact);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return varArray.toString();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(102);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
