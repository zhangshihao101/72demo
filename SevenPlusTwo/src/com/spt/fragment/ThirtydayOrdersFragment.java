package com.spt.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.common.MyDate;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class ThirtydayOrdersFragment extends Fragment {

    private View view;
    private static Context mContext;

    private GraphicalView graphicalView, graphicalView2;
    private LinearLayout layout, layout2;
    private TextView tv_nocount, tv_nosum;

    private static ProgressDialog dialog;

    private List<String> channelList;
    private List<String> channelListSort;

    private HashMap<String, Double> sumMap;
    private HashMap<String, Integer> countMap;

    public static final int COLOR_BLUE = Color.parseColor("#727BCC");
    public static final int COLOR_AZURE = Color.parseColor("#4FCEFF");
    public static final int COLOR_GREEN = Color.parseColor("#8DD06A");
    public static final int COLOR_ORANGE = Color.parseColor("#F6AF5C");
    public static final int COLOR_RED = Color.parseColor("#77A4FF");

    public static int[] colors = {COLOR_BLUE, COLOR_AZURE, COLOR_GREEN, COLOR_ORANGE, COLOR_RED};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_channel_today2, null);

        layout = (LinearLayout) view.findViewById(R.id.chart_count);
        layout2 = (LinearLayout) view.findViewById(R.id.chart_sum);
        tv_nocount = (TextView) view.findViewById(R.id.tv_nocount);
        tv_nosum = (TextView) view.findViewById(R.id.tv_nosum);

        dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
        dialog.dismiss();

        channelList = new ArrayList<String>();
        channelListSort = new ArrayList<String>();

        sumMap = new HashMap<String, Double>();
        countMap = new HashMap<String, Integer>();

        getTodayOrders();

        return view;
    }

    private void getTodayOrders() {
        dialog.show();
        OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.order_bychannel)
                .post(new FormBody.Builder().add("reportType", "channel").add("orderTypeId", "SALES_ORDER")
                        .add("minDate", MyDate.getThirtyAgoDate()).add("maxDate", MyDate.getDate()).add("isPaging", "N")
                        .add("externalLoginKey", Localxml.search(mContext, "externalloginkey")).build())
                .build()).enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("=======" + "一个月订单" + "========" + jsonStr + "=============");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                dialog.dismiss();
                                try {
                                    JSONObject jsonobject = new JSONObject(jsonStr);
                                    String error = jsonobject.optString("_ERROR_MESSAGE_");
                                    if (error.equals("") || error == null) {
                                        JSONArray jsonarray = jsonobject.optJSONArray("listExport");
                                        if (jsonarray.length() == 0) {
                                            for (int i = 0; i < 5; i++) {
                                                if (!channelList.contains("WHOLES_CHANNEL")) {
                                                    channelList.add(i, "WHOLES_CHANNEL");
                                                } else if (!channelList.contains("DISTRI_CHANNEL")) {
                                                    channelList.add(i, "DISTRI_CHANNEL");
                                                } else if (!channelList.contains("POS_SALES_CHANNEL")) {
                                                    channelList.add(i, "POS_SALES_CHANNEL");
                                                } else if (!channelList.contains("72_SALES_CHANNEL")) {
                                                    channelList.add(i, "72_SALES_CHANNEL");
                                                } else if (!channelList.contains("WECHAT_SALES_CHANNEL")) {
                                                    channelList.add(i, "WECHAT_SALES_CHANNEL");
                                                }
                                                sumMap.put(channelList.get(i), Double.parseDouble("0"));
                                                countMap.put(channelList.get(i), 0);

                                            }
                                            layout.setVisibility(View.INVISIBLE);
                                            layout2.setVisibility(View.INVISIBLE);
                                            tv_nocount.setVisibility(View.VISIBLE);
                                            tv_nosum.setVisibility(View.VISIBLE);

                                        } else {
                                            layout.setVisibility(View.VISIBLE);
                                            layout2.setVisibility(View.VISIBLE);
                                            tv_nocount.setVisibility(View.GONE);
                                            tv_nosum.setVisibility(View.GONE);
                                            for (int i = 0; i < jsonarray.length(); i++) {
                                                JSONObject itemObj = (JSONObject) jsonarray.get(i);
                                                String channelId = itemObj.optString("salesChannelEnumId");
                                                String sum = itemObj.optString("salesMoney");
                                                Integer count = itemObj.optInt("quantityOrdered");

                                                channelList.add(channelId);

                                                sumMap.put(channelId, Double.parseDouble(sum));
                                                countMap.put(channelId, count);
                                            }

                                            if (channelList.size() < 5) {
                                                for (int i = channelList.size(); i < 5; i++) {

                                                    if (!channelList.contains("WHOLES_CHANNEL")) {
                                                        channelList.add(i, "WHOLES_CHANNEL");
                                                    } else if (!channelList.contains("DISTRI_CHANNEL")) {
                                                        channelList.add(i, "DISTRI_CHANNEL");
                                                    } else if (!channelList.contains("POS_SALES_CHANNEL")) {
                                                        channelList.add(i, "POS_SALES_CHANNEL");
                                                    } else if (!channelList.contains("72_SALES_CHANNEL")) {
                                                        channelList.add(i, "72_SALES_CHANNEL");
                                                    } else if (!channelList.contains("WECHAT_SALES_CHANNEL")) {
                                                        channelList.add(i, "WECHAT_SALES_CHANNEL");
                                                    }
                                                }
                                            }

                                            channelListSort.add(0, "WHOLES_CHANNEL");
                                            channelListSort.add(1, "POS_SALES_CHANNEL");
                                            channelListSort.add(2, "DISTRI_CHANNEL");
                                            channelListSort.add(3, "72_SALES_CHANNEL");
                                            channelListSort.add(4, "WECHAT_SALES_CHANNEL");

                                            for (int i = 0; i < 5; i++) {
                                                if (!sumMap.containsKey(channelListSort.get(i))) {
                                                    sumMap.put(channelListSort.get(i), Double.parseDouble("0"));
                                                }

                                                if (!countMap.containsKey(channelListSort.get(i))) {
                                                    countMap.put(channelListSort.get(i), 0);
                                                }

                                            }
                                        }

                                        CategorySeries dataset = buildCategoryDataset("", sumMap);
                                        CategorySeries dataset2 = buildCategoryDataset2("", countMap);

                                        DefaultRenderer renderer = buildCategoryRenderer(colors, "订单金额合计");
                                        DefaultRenderer renderer2 = buildCategoryRenderer(colors, "订单数量");

                                        graphicalView = ChartFactory.getPieChartView(mContext, dataset, renderer);
                                        graphicalView2 = ChartFactory.getPieChartView(mContext, dataset2, renderer2);

                                        // layout.removeAllViews();
                                        layout.setBackgroundColor(Color.WHITE);
                                        layout.addView(graphicalView2,
                                                new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

                                        // layout2.removeAllViews();
                                        layout2.setBackgroundColor(Color.WHITE);
                                        layout2.addView(graphicalView,
                                                new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                                    } else {
                                        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
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

    protected CategorySeries buildCategoryDataset(String title, HashMap<String, Double> values) {
        CategorySeries series = new CategorySeries(title);
        series.add("批发-" + values.get("WHOLES_CHANNEL"), values.get("WHOLES_CHANNEL"));
        series.add("零售-" + values.get("POS_SALES_CHANNEL"), values.get("POS_SALES_CHANNEL"));
        series.add("分销-" + values.get("DISTRI_CHANNEL"), values.get("DISTRI_CHANNEL"));
        series.add("七加二-" + values.get("72_SALES_CHANNEL"), values.get("72_SALES_CHANNEL"));
        series.add("微信-" + values.get("WECHAT_SALES_CHANNEL"), values.get("WECHAT_SALES_CHANNEL"));
        return series;
    }

    protected CategorySeries buildCategoryDataset2(String title, HashMap<String, Integer> values) {
        CategorySeries series = new CategorySeries(title);
        series.add("批发-" + values.get("WHOLES_CHANNEL"), values.get("WHOLES_CHANNEL"));
        series.add("零售-" + values.get("POS_SALES_CHANNEL"), values.get("POS_SALES_CHANNEL"));
        series.add("分销-" + values.get("DISTRI_CHANNEL"), values.get("DISTRI_CHANNEL"));
        series.add("七加二-" + values.get("72_SALES_CHANNEL"), values.get("72_SALES_CHANNEL"));
        series.add("微信-" + values.get("WECHAT_SALES_CHANNEL"), values.get("WECHAT_SALES_CHANNEL"));
        return series;
    }

    protected DefaultRenderer buildCategoryRenderer(int[] colors, String ss) {
        DefaultRenderer renderer = new DefaultRenderer();

        renderer.setLegendTextSize(30);// 设置左下角表注的文字大小
        renderer.setZoomButtonsVisible(false);// 设置显示放大缩小按钮
        renderer.setZoomEnabled(true);// 设置允许放大缩小.
        renderer.setChartTitleTextSize(30);// 设置图表标题的文字大小
        int[] margins = new int[] {Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK};
        renderer.setMargins(margins);
        renderer.setFitLegend(true);// 是否显示图例
        renderer.setChartTitle(ss);// 设置图表的标题 默认是居中顶部显示
        renderer.setLabelsTextSize(30);// 饼图上标记文字的字体大小
        renderer.setLabelsColor(Color.BLACK);// 饼图上标记文字的颜色
        // renderer.setPanEnabled(true);// 设置是否可以平移
        // renderer.setDisplayValues(true);// 是否显示值
        // renderer.setClickEnabled(true);// 设置是否可以被点击
        // renderer.setMargins(new int[] { 20, 30, 15, 0 });
        // margins - an array containing the margin size values, in this order:
        // top, left, bottom, right
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

}
