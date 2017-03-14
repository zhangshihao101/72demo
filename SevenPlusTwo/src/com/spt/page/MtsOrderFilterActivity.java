package com.spt.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.TagAdapter;
import com.spt.common.BaseMtsActivity;
import com.spt.common.Constants;
import com.spt.common.MyDate;
import com.spt.common.MyPostTask;
import com.spt.common.NetworkUtil;
import com.spt.controler.DateTimePickDialogUtil;
import com.spt.controler.FlowTagLayout;
import com.spt.interfac.OnTagSelectListener;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class MtsOrderFilterActivity extends BaseMtsActivity {

    private ImageView iv_back;
    private RelativeLayout rl_relativelayout;
    private EditText et_orderno, et_start_day, et_end_day;
    private Spinner sp_shop, sp_channal;
    private Button btn_enter;
    private String channel = "", selected_channelNo = "", store = "", selected_storeId = "", orderState = "ORDER_ALL",
            payState = "PMNT_ALL";

    private List<String> channelList;
    private ArrayAdapter<String> channel_adapter;
    private HashMap<String, String> channelMap;
    private List<String> channelNoList;

    private List<String> storeList;
    private ArrayAdapter<String> store_adapter;
    private HashMap<String, String> storeMap;

    private String initStartDateTime = ""; // 初始化开始时间
    private String initEndDateTime = ""; // 初始化结束时间

    private FlowTagLayout fl_order_statestate, fl_pay_statestate;
    private TagAdapter orderAdapter, payAdapter;
    private List<Object> orderStateData, payStateData;

    private int OK_RESULT = 10, NEG_RESULT = 11;

    @Override
    protected void onCreate(Bundle inState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_order_filters);
        super.onCreate(inState);

        initView();

        getChannals();
        getStores();

        sp_channal.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_channelNo = channelMap.get(channelList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        sp_shop.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_storeId = storeMap.get(storeList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        et_start_day.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePicKDialog =
                        new DateTimePickDialogUtil(MtsOrderFilterActivity.this, initStartDateTime);
                dateTimePicKDialog.dateTimePicKDialog(et_start_day);

            }
        });

        et_end_day.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                DateTimePickDialogUtil dateTimePicKDialog =
                        new DateTimePickDialogUtil(MtsOrderFilterActivity.this, initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(et_end_day);
            }
        });

        fl_order_statestate.setOnTagSelectListener(new OnTagSelectListener() {

            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                for (Integer integer : selectedList) {
                    if (orderStateData.get(integer).equals("全部")) {
                        orderState = "ORDER_ALL";
                    } else if (orderStateData.get(integer).equals("已创建")) {
                        orderState = "ORDER_CREATED";
                    } else if (orderStateData.get(integer).equals("已批准")) {
                        orderState = "ORDER_APPROVED";
                    } else if (orderStateData.get(integer).equals("已保留")) {
                        orderState = "ORDER_HOLD";
                    } else if (orderStateData.get(integer).equals("已完成")) {
                        orderState = "ORDER_COMPLETED";
                    } else if (orderStateData.get(integer).equals("已取消")) {
                        orderState = "ORDER_CANCELLED";
                    } else {
                        orderState = "ORDER_ALL";
                    }
                }
            }
        });

        fl_pay_statestate.setOnTagSelectListener(new OnTagSelectListener() {

            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                for (Integer integer : selectedList) {
                    if (payStateData.get(integer).equals("全部")) {
                        payState = "PMNT_ALL";
                    } else if (payStateData.get(integer).equals("未收款")) {
                        payState = "PMNT_NOPAY_RECV";
                    } else if (payStateData.get(integer).equals("部分收款")) {
                        payState = "PMNT_PARTIAL_RECV";
                    } else if (payStateData.get(integer).equals("已退款")) {
                        payState = "PMNT_RETURN_CUSTOMER";
                    } else if (payStateData.get(integer).equals("已结清")) {
                        payState = "PMNT_TOTAL_RECV";
                    } else {
                        payState = "PMNT_ALL";
                    }
                }
            }
        });

        btn_enter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent();

                intent.putExtra("orderId", et_orderno.getText().toString());
                intent.putExtra("store", selected_storeId);
                intent.putExtra("channel", selected_channelNo);
                intent.putExtra("orderState", orderState);
                intent.putExtra("payState", payState);
                intent.putExtra("startDay", et_start_day.getText().toString());
                intent.putExtra("endDay", et_end_day.getText().toString().equals("")
                        ? MyDate.getDate()
                        : et_end_day.getText().toString());

                setResult(OK_RESULT, intent);
                finish();
            }
        });

        // iv_back.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // Intent intent = new Intent();
        // setResult(NEG_RESULT, intent);
        // finish();
        // }
        // });

        rl_relativelayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(NEG_RESULT, intent);
                finish();
            }
        });

    }

    private void getChannals() {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("externalLoginKey",
                Localxml.search(MtsOrderFilterActivity.this, "externalloginkey")));
        nameValuePair.add(new BasicNameValuePair("enumTypeId", "ORDER_SALES_CHANNEL"));

        if (NetworkUtil.isConnected(MtsOrderFilterActivity.this)) {
            PromoTask promotask = new PromoTask(MtsOrderFilterActivity.this, MtsUrls.base + MtsUrls.get_enumsbytype,
                    nameValuePair, "0");
            promotask.execute("");
        } else {
            Toast.makeText(MtsOrderFilterActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
        }
    }

    private void getStores() {
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
        nameValuePair.add(new BasicNameValuePair("externalLoginKey",
                Localxml.search(MtsOrderFilterActivity.this, "externalloginkey")));

        if (NetworkUtil.isConnected(MtsOrderFilterActivity.this)) {
            PromoTask promotask = new PromoTask(MtsOrderFilterActivity.this, MtsUrls.base + MtsUrls.get_storelist,
                    nameValuePair, "1");
            promotask.execute("");
        } else {
            Toast.makeText(MtsOrderFilterActivity.this, "没有网络连接，请检查后再试！", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void updateUI(String whichtask, String result) {
        if (whichtask.equals("0")) {
            Log.e("LOOK", "支付渠道==" + result);
            try {
                JSONObject object = new JSONObject(result);
                JSONArray array = object.optJSONArray("listPaymentStatus");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);
                    if (obj.optString("enumId").equals("POS_SALES_CHANNEL")) {
                        channel = "POS零售";
                    } else if (obj.optString("enumId").equals("WHOLES_CHANNEL")) {
                        channel = "批发";
                    } else if (obj.optString("enumId").equals("DISTRI_CHANNEL")) {
                        channel = "分销";
                    } else if (obj.optString("enumId").equals("72_SALES_CHANNEL")) {
                        channel = "七加二商城";
                    }
                    if (!channelList.contains(channel)) {
                        channelList.add(channel);
                    }
                }

                channelList.add(0, "全部");

                HashMap<String, String> hs = new HashMap<String, String>();
                hs.put("POS零售", "POS_SALES_CHANNEL");
                hs.put("批发", "WHOLES_CHANNEL");
                hs.put("分销", "DISTRI_CHANNEL");
                hs.put("七加二商城", "72_SALES_CHANNEL");
                hs.put("全部", "CHANNEL_ALL");

                for (int i = 0; i < channelList.size(); i++) {
                    channelMap.put(channelList.get(i), hs.get(channelList.get(i)));
                }

                channel_adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, channelList);
                channel_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                sp_channal.setAdapter(channel_adapter);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else if (whichtask.equals("1")) {
            Log.e("LOOK", "店铺列表==" + result);
            try {
                JSONObject object = new JSONObject(result);
                JSONArray array = object.optJSONArray("listProductStore");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = (JSONObject) array.get(i);

                    storeList.add(obj.optString("storeName"));
                    storeMap.put(obj.optString("storeName"), obj.optString("productStoreId"));
                }
                storeList.add(0, "全部");
                storeMap.put("全部", "STORE_ALL");

                // store_adapter = new ArrayAdapter<String>(this,
                // android.R.layout.simple_spinner_item, storeList);
                store_adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, storeList);
                store_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                sp_shop.setAdapter(store_adapter);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        // iv_back = (ImageView) findViewById(R.id.iv_back);
        // tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        rl_relativelayout = (RelativeLayout) findViewById(R.id.rl_relativelayout);
        et_orderno = (EditText) findViewById(R.id.et_orderno);
        et_start_day = (EditText) findViewById(R.id.et_start_day);
        et_end_day = (EditText) findViewById(R.id.et_end_day);
        sp_shop = (Spinner) findViewById(R.id.sp_shop);
        sp_channal = (Spinner) findViewById(R.id.sp_channal);

        fl_order_statestate = (FlowTagLayout) findViewById(R.id.fl_order_statestate);
        fl_pay_statestate = (FlowTagLayout) findViewById(R.id.fl_pay_statestate);
        btn_enter = (Button) findViewById(R.id.btn_enter);

        channelList = new ArrayList<String>();
        channelMap = new HashMap<String, String>();
        channelNoList = new ArrayList<String>();

        storeList = new ArrayList<String>();
        storeMap = new HashMap<String, String>();

        orderAdapter = new TagAdapter(MtsOrderFilterActivity.this);
        fl_order_statestate.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        fl_order_statestate.setAdapter(orderAdapter);

        payAdapter = new TagAdapter(MtsOrderFilterActivity.this);
        fl_pay_statestate.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        fl_pay_statestate.setAdapter(payAdapter);

        orderStateData = new ArrayList<Object>();
        orderStateData.add("全部");
        orderStateData.add("已创建");
        orderStateData.add("已批准");
        orderStateData.add("已保留");
        orderStateData.add("已完成");
        orderStateData.add("已取消");
        orderAdapter.onlyAddAll(orderStateData);

        payStateData = new ArrayList<Object>();
        payStateData.add("全部");
        payStateData.add("未收款");
        payStateData.add("部分收款");
        payStateData.add("已退款");
        payStateData.add("已结清");
        payAdapter.onlyAddAll(payStateData);

    }

    class PromoTask extends MyPostTask {
        String which;

        public PromoTask(Context context, String url, List<NameValuePair> nameValuePair, String whichtask) {
            super(context, url, nameValuePair, whichtask);
            which = whichtask;
        }

        @Override
        protected void onPostExecute(String result) {
            if (null == result || ("").equals(result) || result.length() == 0 || result.equals(Constants.not_found)
                    || result.equals(Constants.time_out)) {
                Toast.makeText(MtsOrderFilterActivity.this, "网络不好，请重试！", Toast.LENGTH_SHORT).show();
            } else {
                updateUI(which, result);
            }
        }
    }

}
