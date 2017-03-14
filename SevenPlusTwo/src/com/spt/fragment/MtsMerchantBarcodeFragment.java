package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsBarcodeEditAdapter;
import com.spt.bean.MtsBarcodeInfo;
import com.spt.controler.MipcaActivityCapture;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.umeng.socialize.utils.Log;

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
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class MtsMerchantBarcodeFragment extends Fragment {

    private final static int SCANNIN_GREQUEST_CODE = 1;

    private View view;
    private Context mContext;

    private ListView lv_barcode_edit;
    private TextView tv_submit;

    private ProgressDialog progressDialog;

    private List<MtsBarcodeInfo> mData;
    private MtsBarcodeEditAdapter adapter;

    private String detail = "", productId = "", barcode = "";
    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        view = LayoutInflater.from(mContext).inflate(R.layout.fragment_manufacturers_barcode, null);

        Bundle bundle = getArguments();
        detail = bundle.getString("message");
        productId = bundle.getString("proId");

        initView();

        mData = new ArrayList<MtsBarcodeInfo>();
        adapter = new MtsBarcodeEditAdapter(mContext, mData);
        lv_barcode_edit.setAdapter(adapter);

        initData();

        adapter.setShareClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                position = (Integer) v.getTag();

                Intent intent = new Intent();
                intent.setClass(mContext, MipcaActivityCapture.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

            }
        });

        tv_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                updateManuBarcode();
            }
        });

        return view;
    }

    private void initView() {
        lv_barcode_edit = (ListView) view.findViewById(R.id.lv_barcode_edit);
        tv_submit = (TextView) view.findViewById(R.id.tv_submit);

        progressDialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
        progressDialog.dismiss();

    }

    private void initData() {
        try {
            JSONArray arrayDetail = new JSONArray(detail);
            for (int i = 0; i < arrayDetail.length(); i++) {
                JSONObject objDetail = (JSONObject) arrayDetail.get(i);

                MtsBarcodeInfo info = new MtsBarcodeInfo();

                info.setProId(objDetail.optString("productId"));
                info.setProColor(objDetail.optString("colorDesc"));
                info.setProSize(objDetail.optString("dimensionDesc"));
                info.setProBarcode(objDetail.optString("skuValue"));

                mData.add(info);
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == -1) {
                    Bundle bundle = data.getExtras();

                    MtsBarcodeInfo info = new MtsBarcodeInfo();
                    info.setProId(mData.get(position).getProId());
                    info.setProColor(mData.get(position).getProColor());
                    info.setProSize(mData.get(position).getProSize());
                    info.setProBarcode(bundle.getString("result"));

                    mData.set(position, info);
                    adapter.notifyDataSetChanged();
                }
                break;

            case 0:
                if (resultCode == 0) {
                    Bundle b = data.getExtras();
                    barcode = b.getString("value");
                    position = b.getInt("position");

                    MtsBarcodeInfo info = new MtsBarcodeInfo();
                    info.setProBarcode(barcode);
                    info.setProId(mData.get(position).getProId());
                    info.setProColor(mData.get(position).getProColor());
                    info.setProSize(mData.get(position).getProSize());

                    mData.set(position, info);
                    adapter.notifyDataSetChanged();
                }
                break;

        }

    }

    public String getVariants() {
        JSONArray varArray = new JSONArray();
        try {
            for (int i = 0; i < mData.size(); i++) {
                JSONObject varObjact = new JSONObject();

                varObjact.put("variantId", mData.get(i).getProId());
                varObjact.put("skuValue", mData.get(i).getProBarcode());
                varArray.put(varObjact);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return varArray.toString();

    }

    public void updateManuBarcode() {
        progressDialog.show();

        OkHttpManager.client
                .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.update_skuvalue)
                        .post(new FormBody.Builder()
                                .add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
                                .add("productId", productId).add("variants", getVariants()).build())
                        .build())
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
                                progressDialog.dismiss();
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String isSuccess = object.optString("isSuccess");
                                    String message = object.optString("_ERROR_MESSAGE_");
                                    if (isSuccess.equals("Y")) {
                                        Toast.makeText(mContext, "更新成功！", Toast.LENGTH_SHORT).show();
                                        getActivity().setResult(201);
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(mContext, "更新失败" + "\n" + message, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(mContext, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

    }

}
