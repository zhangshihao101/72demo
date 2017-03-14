package com.spt.page;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.spt.adapter.OrderListAdapter;
import com.spt.bean.OrderListInfo;
import com.spt.controler.MyRefreshListView;
import com.spt.controler.MyRefreshListView.MyOnRefreshListener;
import com.spt.controler.MyTitleBar;
import com.spt.sht.R;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpGetService;
import com.spt.utils.MyUtil;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 【全部订单】页
 */
public class AllOrdersActivity extends BaseActivity {
    private MyTitleBar mtbAllTitle;
    private TextView tvTitle;
    private ImageView ivLeft;
    private ImageView ivRight;
    private LinearLayout llRight;
    private MyRefreshListView lvAllContent;
    private Intent itAllFrom;
    private String strToken;
    private HashMap<String, Object> param;
    private Intent iGetRequest;
    private boolean isGetServiceRunning = false;
    private BroadcastReceiver brGetHttp; // get方法广播
    private TextView tvTip;
    private LinearLayout llLeft;
    private ProgressDialog progressDialog;
    private SharedPreferences spJSON; // 保存服务数据
    private String strData;
    private String orderSn = "";
    private String orderStatus = "";
    private String evaluationStatus = "";
    private String extension = "";
    private String addTimeFrom = "";
    private String addTimeTo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentID(R.layout.allorders);
        super.onCreate(savedInstanceState);
        // 初始化内容
        try {
            initContent();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        registMyBroadcastRecevier(); // regist broadcastReceiver
        super.onStart();
    }

    @Override
    protected void onStop() {
        AllOrdersActivity.this.unregisterReceiver(brGetHttp);
        if (isGetServiceRunning) {
            stopService(iGetRequest);
            isGetServiceRunning = false;
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case MyConstant.RESULTCODE_23:
                boolean isSuccessOrder = data.getBooleanExtra("isSuccess", false);
                if (isSuccessOrder) {
                    String jsonStr = spJSON.getString("AllOrderQueryBack", "");
                    try {
                        JSONTokener jsonParser = new JSONTokener(jsonStr);
                        JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
                        JSONArray array = jsonReturn.getJSONArray("data");
                        int length_QueryBack = array.length();
                        HeaderViewListAdapter hvla = (HeaderViewListAdapter) lvAllContent.getAdapter();
                        OrderListAdapter ola = (OrderListAdapter) hvla.getWrappedAdapter();
                        tvTip.setVisibility(View.GONE);
                        if (length_QueryBack > 0) {
                            ola.clear();
                            for (int i = 0; i < length_QueryBack; i++) {
                                JSONObject jsonReturn2 = array.getJSONObject(i);
                                String order_id = jsonReturn2.getString("order_id");
                                String order_sn = jsonReturn2.getString("order_sn");
                                String extension = jsonReturn2.getString("extension");
                                String status = jsonReturn2.getString("status");
                                String final_amount = jsonReturn2.getString("final_amount");
                                String add_time = jsonReturn2.getString("add_time");
                                String is_change = jsonReturn2.getString("is_change");

                                OrderListInfo info = new OrderListInfo();
                                info.setOrder_id(order_id);
                                info.setOrder_sn(order_sn);
                                info.setExtension(extension);
                                info.setStatus(status);
                                info.setFinal_amount(final_amount);
                                info.setAdd_time(add_time);
                                info.setIs_change(is_change);
                                ola.addOrderListInfo(info);
                            }
                            ola.notifyDataSetChanged();
                            lvAllContent.onRefreshComplete();
                        } else {
                            ola.clear();
                            ola.notifyDataSetChanged();
                            lvAllContent.onRefreshComplete();
                            tvTip.setVisibility(View.VISIBLE);
                            tvTip.setText("未检索到符合条件的订单");
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                if (data.hasExtra("orderSn")) {
                    orderSn = data.getStringExtra("orderSn");
                } else {
                    orderSn = "";
                }
                if (data.hasExtra("orderStatus")) {
                    orderStatus = data.getStringExtra("orderStatus");
                } else {
                    orderStatus = "";
                }

                if (data.hasExtra("evaluationStatus")) {
                    evaluationStatus = data.getStringExtra("evaluationStatus");
                } else {
                    evaluationStatus = "";
                }

                if (data.hasExtra("extension")) {
                    extension = data.getStringExtra("extension");
                } else {
                    extension = "";
                }

                if (data.hasExtra("addTimeFrom")) {
                    addTimeFrom = data.getStringExtra("addTimeFrom");
                } else {
                    addTimeFrom = "";
                }

                if (data.hasExtra("addTimeTo")) {
                    addTimeTo = data.getStringExtra("addTimeTo");
                } else {
                    addTimeTo = "";
                }

                break;
            case MyConstant.RESULTCODE_24:
                callOrderList();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(MyConstant.RESULTCODE_21);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 添加点击事件
     */
    @Override
    protected void addClickEvent() {
        this.llLeft.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(MyConstant.RESULTCODE_21);
                finish();
            }
        });

        this.llRight.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent it = new Intent(AllOrdersActivity.this, OrderQueryActivity.class);
                it.putExtra("state", "allorder");
                startActivityForResult(it, MyConstant.RESULTCODE_23);
            }
        });
    }

    /**
     * 初始化
     */
    @Override
    protected void init() {
        this.mtbAllTitle = (MyTitleBar) findViewById(R.id.mtb_all_title);
        this.tvTitle = mtbAllTitle.getTvTitle();
        this.ivLeft = mtbAllTitle.getIvLeft();
        this.ivRight = mtbAllTitle.getIvRight();
        this.tvTitle.setText("订单");
        this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
        this.ivRight.setBackgroundResource(R.drawable.homemenuright);
        this.llLeft = mtbAllTitle.getLlLeft();
        this.llRight = mtbAllTitle.getLlRight();
        this.llRight.setVisibility(View.VISIBLE);
        this.lvAllContent = (MyRefreshListView) findViewById(R.id.lv_all_content);
        this.itAllFrom = getIntent();
        this.param = new HashMap<String, Object>();
        this.iGetRequest = new Intent(AllOrdersActivity.this, MyHttpGetService.class);
        this.iGetRequest.setAction(MyConstant.HttpGetServiceAciton);
        this.brGetHttp = new MyBroadCastReceiver(); // GET广播对象
        this.tvTip = (TextView) findViewById(R.id.tv_all_tip);
        this.progressDialog = ProgressDialog.show(AllOrdersActivity.this, "请稍候。。。", "获取数据中。。。", true);
        this.progressDialog.dismiss();
        this.spJSON = AllOrdersActivity.this.getSharedPreferences("JSONDATA", MODE_PRIVATE); // 获取sp对象
        this.strToken = itAllFrom.getStringExtra("token");
        this.strData = itAllFrom.getStringExtra("data");
    }

    /**
     * 初始化内容
     */
    private void initContent() throws JSONException {
        tvTip.setVisibility(View.GONE);
        JSONTokener jasonParser1 = new JSONTokener(strData);
        JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
        int length = jsonReturn1.length();
        if (length > 0) {
            OrderListAdapter osa = new OrderListAdapter(AllOrdersActivity.this);
            for (int i = 0; i < length; i++) {
                JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
                String order_id = jsonReturn2.getString("order_id");
                String order_sn = jsonReturn2.getString("order_sn");
                String extension = jsonReturn2.getString("extension");
                String status = jsonReturn2.getString("status");
                String final_amount = jsonReturn2.getString("final_amount");
                String add_time = jsonReturn2.getString("add_time");
                String is_change = jsonReturn2.getString("is_change");

                OrderListInfo info = new OrderListInfo();
                info.setOrder_id(order_id);
                info.setOrder_sn(order_sn);
                info.setExtension(extension);
                info.setStatus(status);
                info.setFinal_amount(final_amount);
                info.setAdd_time(add_time);
                info.setIs_change(is_change);
                osa.addOrderListInfo(info);
            }
            lvAllContent.setAdapter(osa);

            lvAllContent.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    OrderListInfo info = (OrderListInfo) parent.getItemAtPosition(position);
                    String strId = info.getOrder_id();

                    param.clear();
                    param.put("token", strToken);
                    param.put("order_id", strId);
                    String uri = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order&act=view";
                    String type = "orderDetail_all";
                    progressDialog.show();
                    iGetRequest.putExtra("uri", uri);
                    iGetRequest.putExtra("param", param);
                    iGetRequest.putExtra("type", type);
                    startService(iGetRequest);
                    isGetServiceRunning = true;
                }
            });

            lvAllContent.setonMyRefreshListener(new MyOnRefreshListener() {

                @Override
                public void onRefresh() {
                    callOrderList();
                }
            });
        } else {
            tvTip.setText("您目前没有定单");
            tvTip.setVisibility(View.VISIBLE);
        }

    }

    /**
     * 请求订单列表
     */
    private void callOrderList() {
        progressDialog.show();
        param.clear();
        param.put("token", strToken);
        if (!"".equals(orderSn)) {
            param.put("order_sn", orderSn);
        }
        if (!"".equals(orderStatus)) {
            param.put("status", orderStatus);
        }
        if (!"".equals(evaluationStatus)) {
            param.put("evaluation_status", evaluationStatus);
        }
        if (!"".equals(extension)) {
            param.put("extension", extension);
        }
        if (!"".equals(addTimeTo)) {
            param.put("add_time_to", addTimeTo);
        }
        if (!"".equals(addTimeFrom)) {
            param.put("add_time_from", addTimeFrom);
        }
        String uriAll = MyConstant.SERVICENAME + "/index.php?pf=m_seller&app=order";
        String typeAll = "allOrder_refresh";
        iGetRequest.putExtra("uri", uriAll);
        iGetRequest.putExtra("param", param);
        iGetRequest.putExtra("type", typeAll);
        startService(iGetRequest);
        isGetServiceRunning = true;
    }

    /**
     * 注册广播
     */
    private void registMyBroadcastRecevier() {
        IntentFilter filterGetHttp = new IntentFilter();
        filterGetHttp.addAction(MyConstant.HttpGetServiceAciton);
        AllOrdersActivity.this.registerReceiver(brGetHttp, filterGetHttp);
    }

    /**
     * 内部广播类
     */
    private class MyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyConstant.HttpGetServiceAciton.equals(intent.getAction())) {
                String isSuccess = intent.getStringExtra("isSuccess");
                String strReturnType = intent.getStringExtra("type");
                if ("ok".equals(isSuccess)) {
                    String result = intent.getStringExtra("result");
                    try {
                        parseData(strReturnType, result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }

    /**
     * 解析返回数据
     */
    private void parseData(String type, String jsonStr) throws JSONException {
        progressDialog.dismiss();
        if ("orderDetail_all".equals(type)) {
            Log.e("BBBBB", jsonStr);
            JSONTokener jsonParser = new JSONTokener(jsonStr);
            JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
            String error = jsonReturn.getString("error");
            String msg = jsonReturn.getString("msg");
            String data = jsonReturn.getJSONObject("data").toString();
            Log.e("YYYYY", data);
            if ("0".equals(error) && !"".equals(data)) {
                Intent it = new Intent(AllOrdersActivity.this, OrderDetailActivity.class);
                it.putExtra("data", data);
                it.putExtra("frompage", "AllOrder");
                startActivityForResult(it, MyConstant.RESULTCODE_24);
            } else {
                MyUtil.ToastMessage(AllOrdersActivity.this, msg);
            }
        } else if ("allorderList".equals(type)) {
            tvTip.setVisibility(View.GONE);
            JSONTokener jsonParser = new JSONTokener(jsonStr);
            JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
            String error = jsonReturn.getString("error");
            String msg = jsonReturn.getString("msg");
            String data = jsonReturn.getJSONArray("data").toString();
            if ("0".equals(error) && !"".equals(data)) {
                JSONTokener jasonParser1 = new JSONTokener(data);
                JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
                int length = jsonReturn1.length();
                if (length > 0) {
                    HeaderViewListAdapter hvla = (HeaderViewListAdapter) lvAllContent.getAdapter();
                    OrderListAdapter osa = (OrderListAdapter) hvla.getWrappedAdapter();
                    osa.clear();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
                        String order_id = jsonReturn2.getString("order_id");
                        String order_sn = jsonReturn2.getString("order_sn");
                        String extension = jsonReturn2.getString("extension");
                        String status = jsonReturn2.getString("status");
                        String final_amount = jsonReturn2.getString("final_amount");
                        String add_time = jsonReturn2.getString("add_time");
                        String is_change = jsonReturn2.getString("is_change");

                        OrderListInfo info = new OrderListInfo();
                        info.setOrder_id(order_id);
                        info.setOrder_sn(order_sn);
                        info.setExtension(extension);
                        info.setStatus(status);
                        info.setFinal_amount(final_amount);
                        info.setAdd_time(add_time);
                        info.setIs_change(is_change);
                        osa.addOrderListInfo(info);
                    }

                    osa.notifyDataSetChanged();
                    lvAllContent.onRefreshComplete();
                }
            } else {
                MyUtil.ToastMessage(AllOrdersActivity.this, msg);
                tvTip.setText("未搜索到相关订单");
                tvTip.setVisibility(View.VISIBLE);
            }
        } else if ("allOrder_refresh".equals(type)) {
            JSONTokener jsonParser = new JSONTokener(jsonStr);
            JSONObject jsonReturn = (JSONObject) jsonParser.nextValue();
            strData = jsonReturn.getJSONArray("data").toString();
            JSONTokener jasonParser1 = new JSONTokener(strData);
            JSONArray jsonReturn1 = (JSONArray) jasonParser1.nextValue();
            int length = jsonReturn1.length();
            if (length > 0) {
                HeaderViewListAdapter hvla = (HeaderViewListAdapter) lvAllContent.getAdapter();
                OrderListAdapter osa = (OrderListAdapter) hvla.getWrappedAdapter();
                osa.clear();
                for (int i = 0; i < length; i++) {
                    JSONObject jsonReturn2 = jsonReturn1.getJSONObject(i);
                    String order_id = jsonReturn2.getString("order_id");
                    String order_sn = jsonReturn2.getString("order_sn");
                    String extension = jsonReturn2.getString("extension");
                    String status = jsonReturn2.getString("status");
                    String final_amount = jsonReturn2.getString("final_amount");
                    String add_time = jsonReturn2.getString("add_time");
                    String is_change = jsonReturn2.getString("is_change");

                    OrderListInfo info = new OrderListInfo();
                    info.setOrder_id(order_id);
                    info.setOrder_sn(order_sn);
                    info.setExtension(extension);
                    info.setStatus(status);
                    info.setFinal_amount(final_amount);
                    info.setAdd_time(add_time);
                    info.setIs_change(is_change);
                    osa.addOrderListInfo(info);
                }
                osa.notifyDataSetChanged();
                lvAllContent.onRefreshComplete();
            }
        }
    }
}
