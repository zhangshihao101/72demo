package com.spt.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.common.MyDate;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.MyConstant;
import com.spt.utils.MyHttpPostService;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

public class ThirtyDaysAgoFragment extends Fragment {

	private View view;
	private Context mContext;
	private LineChartView line_chart_30_count, line_chart_30_sum;

	private static ProgressDialog dialog;
	private Intent getThirtyDays;
	private HashMap<String, String> params;// 参数集合
	private boolean isServiceRunning = false;
	private BroadcastReceiver brHttp_getThirtyDays;

	private List<PointValue> mPointValuesCount = new ArrayList<PointValue>();
	private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

	private List<PointValue> mPointValuesSum = new ArrayList<PointValue>();

	private String[] date;
	private List<String> listdate;

	private Float[] count;
	private List<Float> listcount;

	private Float[] sum;
	private List<Float> listsum;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = getActivity();
		view = inflater.inflate(R.layout.fragment_thirtydaysago, null);

		intiView();

		return view;
	}

	private void initLineChartCount() {
		Line linecount = new Line(mPointValuesCount).setColor(Color.parseColor("#66ccff")); // 折线的颜色

		List<Line> lineCount = new ArrayList<Line>();
		linecount.setShape(ValueShape.CIRCLE);
		linecount.setCubic(false);// 曲线是否平滑
		linecount.setStrokeWidth(1);// 线条的粗细，默认是3
		linecount.setFilled(true);// 是否填充曲线的面积
		// linecount.setHasLabels(false);// 曲线的数据坐标是否加上备注
		linecount.setHasLabelsOnlyForSelected(true);// 点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
		linecount.setHasLines(true);// 是否用直线显示。如果为false 则没有曲线只有点显示
		linecount.setHasPoints(true);// 是否显示圆点 如果为false 则没有原点只有点显示

		lineCount.add(linecount);

		LineChartData data = new LineChartData();
		data.setLines(lineCount);

		// 坐标轴
		Axis axisX = new Axis(); // X轴
		axisX.setHasTiltedLabels(true); // X轴下面坐标轴字体是斜的显示还是直的，true是斜的显示
		// axisX.setTextColor(Color.WHITE); //设置字体颜色
		axisX.setTextColor(Color.parseColor("#D6D6D9"));// 灰色

		axisX.setName("订单数量（笔）"); // 表格名称
		axisX.setTextSize(11);// 设置字体大小
		axisX.setMaxLabelChars(10); // 最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
		axisX.setValues(mAxisXValues); // 填充X轴的坐标名称
		data.setAxisXBottom(axisX); // x 轴在底部
		// data.setAxisXTop(axisX); //x 轴在顶部
		axisX.setHasLines(true); // x 轴分割线

		Axis axisY = new Axis(); // Y轴
		axisY.setName("");// y轴标注
		axisY.setTextSize(11);// 设置字体大小
		data.setAxisYLeft(axisY); // Y轴设置在左边
		// data.setAxisYRight(axisY); //y轴设置在右边

		line_chart_30_count.setInteractive(true);
		line_chart_30_count.setZoomType(ZoomType.HORIZONTAL); // 缩放类型，水平
		line_chart_30_count.setMaxZoom((float) 3);// 缩放比例
		line_chart_30_count.setLineChartData(data);
		line_chart_30_count.setVisibility(View.VISIBLE);

		Viewport vcount = new Viewport(line_chart_30_count.getMaximumViewport());
		vcount.left = 0;
		vcount.right = 7;
		line_chart_30_count.setCurrentViewport(vcount);
	}

	private void initLineChartSum() {
		Line linesum = new Line(mPointValuesSum).setColor(Color.parseColor("#ff6633")); // 折线的颜色
		List<Line> lineSum = new ArrayList<Line>();

		linesum.setShape(ValueShape.CIRCLE);
		linesum.setCubic(false);// 曲线是否平滑
		linesum.setStrokeWidth(1);// 线条的粗细，默认是3
		linesum.setFilled(true);// 是否填充曲线的面积
		// linesum.setHasLabels(false);// 曲线的数据坐标是否加上备注
		linesum.setHasLabelsOnlyForSelected(true);// 点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
		linesum.setHasLines(true);// 是否用直线显示。如果为false 则没有曲线只有点显示
		linesum.setHasPoints(true);// 是否显示圆点 如果为false 则没有原点只有点显示

		lineSum.add(linesum);

		LineChartData data2 = new LineChartData();
		data2.setLines(lineSum);

		// 坐标轴
		Axis axisX = new Axis(); // X轴
		axisX.setHasTiltedLabels(true); // X轴下面坐标轴字体是斜的显示还是直的，true是斜的显示
		// axisX.setTextColor(Color.WHITE); //设置字体颜色
		axisX.setTextColor(Color.parseColor("#D6D6D9"));// 灰色

		axisX.setName("订单金额（元）"); // 表格名称
		axisX.setTextSize(11);// 设置字体大小
		axisX.setMaxLabelChars(10); // 最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
		axisX.setValues(mAxisXValues); // 填充X轴的坐标名称
		data2.setAxisXBottom(axisX); // x 轴在底部
		// data.setAxisXTop(axisX); //x 轴在顶部
		axisX.setHasLines(true); // x 轴分割线

		Axis axisY = new Axis(); // Y轴
		axisY.setName("");// y轴标注
		axisY.setTextSize(11);// 设置字体大小
		data2.setAxisYLeft(axisY); // Y轴设置在左边
		// data.setAxisYRight(axisY); //y轴设置在右边

		line_chart_30_sum.setInteractive(true);
		line_chart_30_sum.setZoomType(ZoomType.HORIZONTAL); // 缩放类型，水平
		line_chart_30_sum.setMaxZoom((float) 3);// 缩放比例
		line_chart_30_sum.setLineChartData(data2);
		line_chart_30_sum.setVisibility(View.VISIBLE);

		Viewport vcount = new Viewport(line_chart_30_sum.getMaximumViewport());
		vcount.left = 0;
		vcount.right = 7;
		line_chart_30_sum.setCurrentViewport(vcount);
	}

	/**
	 * X 轴的显示
	 */
	private void getAxisXLables() {
		for (int i = 0; i < date.length; i++) {
			mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
		}
	}

	/**
	 * 图表的每个点的显示
	 */
	private void getAxisPoints() {
		for (int i = 0; i < count.length; i++) {
			mPointValuesCount.add(new PointValue(i, count[i]));
		}
	}

	/**
	 * 图表的每个点的显示2
	 */
	private void getAxisPoints2() {
		for (int i = 0; i < sum.length; i++) {
			mPointValuesSum.add(new PointValue(i, sum[i]));
		}
	}

	private void intiView() {

		line_chart_30_count = (LineChartView) view.findViewById(R.id.line_chart_30_count);
		line_chart_30_sum = (LineChartView) view.findViewById(R.id.line_chart_30_sum);

		dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();

		params = new HashMap<String, String>(); // 调用接口参数
		brHttp_getThirtyDays = new LoginBroadCastReceiver();
		getThirtyDays = new Intent(mContext, MyHttpPostService.class);
		getThirtyDays.setAction(MyConstant.HttpPostServiceAciton);

		params.clear();
		params.put("externalLoginKey", Localxml.search(mContext, "externalloginkey"));
		params.put("startDate", MyDate.getThirtyAgoDate());
		params.put("endDate", MyDate.getDate());

		String uri = MtsUrls.base + MtsUrls.get_daysago;
		dialog.show();
		getThirtyDays.putExtra("uri", uri);
		getThirtyDays.putExtra("param", params);
		getThirtyDays.putExtra("type", "thirty");
		isServiceRunning = true;
		mContext.startService(getThirtyDays);
	}

	public static boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog.dismiss();
		}
		return true;
	}

	@Override
	public void onStart() {
		registMyBroadcastRecevier();
		super.onStart();
	}

	@Override
	public void onStop() {
		mContext.unregisterReceiver(brHttp_getThirtyDays);
		if (isServiceRunning) {
			isServiceRunning = false;
			mContext.stopService(getThirtyDays);
		}
		super.onStop();
	}

	/**
	 * 注册广播
	 */
	private void registMyBroadcastRecevier() {
		IntentFilter filterHttp = new IntentFilter();
		filterHttp.addAction(MyConstant.HttpPostServiceAciton);
		mContext.registerReceiver(brHttp_getThirtyDays, filterHttp);
	}

	/**
	 * 内部广播类
	 */
	private class LoginBroadCastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MyConstant.HttpPostServiceAciton.equals(intent.getAction())) {
				String isSuccess = intent.getStringExtra("isSuccess");
				if ("ok".equals(isSuccess)) {
					String result = intent.getStringExtra("result");
					String strReturnType = intent.getStringExtra("type");
					try {
						parseJsonResult(strReturnType, result);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 解析返回内容
	 */
	private void parseJsonResult(String type, String jsonStr) throws JSONException {
		dialog.dismiss();

		if ("thirty".equals(type)) {

			listdate = new ArrayList<String>();
			listsum = new ArrayList<Float>();
			listcount = new ArrayList<Float>();

			JSONObject jsonobject = new JSONObject(jsonStr);
			JSONArray dateArray = jsonobject.optJSONArray("listAxis");
			date = new String[dateArray.length()];

			for (int i = 0; i < dateArray.length(); i++) {
				String days = (String) dateArray.get(i);
				listdate.add(days);
			}

			listdate.toArray(date);

			JSONArray sumArray = jsonobject.optJSONArray("listSeries2");
			sum = new Float[sumArray.length()];
			for (int i = 0; i < sumArray.length(); i++) {
				Float dsum = Float.parseFloat(sumArray.get(i).toString());
				listsum.add(dsum);
			}
			sum = listsum.toArray(sum);

			JSONArray countArray = jsonobject.optJSONArray("listSeries1");
			count = new Float[countArray.length()];
			for (int i = 0; i < countArray.length(); i++) {
				Float icount = Float.parseFloat(countArray.get(i).toString());
				listcount.add(icount);
			}
			count = listcount.toArray(count);

			getAxisXLables();
			getAxisPoints();
			getAxisPoints2();
			initLineChartCount();
			initLineChartSum();
		}
	}

}
