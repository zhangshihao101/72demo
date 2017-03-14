package com.spt.fragment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.spt.adapter.CenterViewPagerAdapter;
import com.spt.adapter.MtsStkStorageAdapter;
import com.spt.adapter.MtsStkVpBrandAdapter;
import com.spt.adapter.MtsStkVpGoodsAdapter;
import com.spt.adapter.MtsStkVpKindsAdapter;
import com.spt.bean.MtsStkBrandInfo;
import com.spt.bean.MtsStkGoodsInfo;
import com.spt.bean.MtsStkKindsInfo;
import com.spt.bean.MtsStkStorageInfo;
import com.spt.common.BaseMtsFragment;
import com.spt.controler.CenterViewPager;
import com.spt.controler.CenterViewPager.OnPageChangeListener;
import com.spt.controler.ZoomOutPageTransformer;
import com.spt.page.MtsStkDetailActivity;
import com.spt.sht.R;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import android.widget.LinearLayout.LayoutParams;

public class MtsStockFragment extends BaseMtsFragment {

	private View view;
	private Context mContext;
	private TextView tv_mts_stk_storage, tv_ft_mts_stock_chose, tv_stock_detail, tv_mts_stk_cancel, tv_mts_stk_confirm;
	private RadioGroup rgp_time;
	private RadioButton rbtn_seven, rbtn_thirty;
	private LinearLayout ll_stock_point, ll_ft_mts_stk;
	private CenterViewPager cvp_stock;
	private int prePosition;// 轮播三个点前一个位置的标记
	private static ProgressDialog dialog;
	private DecimalFormat df;

	private ListView lv_one, lv_two, lv_three;
	private MtsStkVpBrandAdapter brandAdapter;
	private MtsStkVpGoodsAdapter goodsAdapter;
	private MtsStkVpKindsAdapter kindsAdapter;
	private List<MtsStkBrandInfo> brandList;
	private List<MtsStkGoodsInfo> goodsList;
	private List<MtsStkKindsInfo> kindsList;
	private MtsStkBrandInfo brandInfo;
	private MtsStkGoodsInfo goodsInfo;
	private MtsStkKindsInfo kindsInfo;

	private ListView lv_mts_stk;
	private List<MtsStkStorageInfo> storageList;
	private MtsStkStorageAdapter storageAdapter;
	private String facilityId = "";
	private int selectIndex;

	private LineChartView line_chart_7_stk, line_chart_30_stk;
	private List<PointValue> mSevenPointValues = new ArrayList<PointValue>();
	private List<AxisValue> mSevenAxisX = new ArrayList<AxisValue>();
	private List<PointValue> mThirtyPointValues = new ArrayList<PointValue>();
	private List<AxisValue> mThirtyAxisX = new ArrayList<AxisValue>();
	private List<String> SevenListDate, thirtyListDate;
	private List<Float> sevenListCount, thirtyListCount;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_mts_stock, null);
		mContext = getActivity();

		initView();

		initPager();

		initListener();

		initData();

		initLineData();

		rgp_time.check(R.id.rbtn_seven);

		return view;
	}

	private void initLineData() {
		String uri = MtsUrls.base + MtsUrls.getInvetoryChanged;
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(uri)
						.post(new FormBody.Builder()
								.add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
								.add("dataFlg", "sevenData").build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									SevenListDate = new ArrayList<String>();
									sevenListCount = new ArrayList<Float>();
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array1 = object.optJSONArray("xAxisData");
									String[] sevenDate = new String[array1.length()];

									for (int i = 0; i < array1.length(); i++) {
										String days = array1.get(i).toString().substring(5, 10);
										SevenListDate.add(days);
									}

									SevenListDate.toArray(sevenDate);

									JSONArray array2 = object.optJSONArray("seriesData");
									Float[] sevenCount = new Float[array2.length()];
									for (int i = 0; i < array2.length(); i++) {
										Float icount = Float.parseFloat(array2.get(i).toString());
										sevenListCount.add(icount);
									}
									sevenCount = sevenListCount.toArray(sevenCount);
									for (int i = 0; i < sevenDate.length; i++) {
										mSevenAxisX.add(new AxisValue(i).setLabel(sevenDate[i]));
									}
									for (int i = 0; i < sevenCount.length; i++) {
										mSevenPointValues.add(new PointValue(i, sevenCount[i]));
									}
									initLineSevenChartCount();
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

		OkHttpManager.client
				.newCall(new Request.Builder().url(uri)
						.post(new FormBody.Builder()
								.add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
								.add("dataFlg", "thirtyData").build())
						.build())
				.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									thirtyListDate = new ArrayList<String>();
									thirtyListCount = new ArrayList<Float>();
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array1 = object.optJSONArray("xAxisData");
									String[] thirtyDate = new String[array1.length()];

									for (int i = 0; i < array1.length(); i++) {
										String days = array1.get(i).toString().substring(5, 10);
										thirtyListDate.add(days);
									}

									thirtyListDate.toArray(thirtyDate);

									JSONArray array2 = object.optJSONArray("seriesData");
									Float[] thirtyCount = new Float[array2.length()];
									for (int i = 0; i < array2.length(); i++) {
										Float icount = Float.parseFloat(array2.get(i).toString());
										thirtyListCount.add(icount);
									}
									thirtyCount = thirtyListCount.toArray(thirtyCount);
									for (int i = 0; i < thirtyDate.length; i++) {
										mThirtyAxisX.add(new AxisValue(i).setLabel(thirtyDate[i]));
									}
									for (int i = 0; i < thirtyCount.length; i++) {
										mThirtyPointValues.add(new PointValue(i, thirtyCount[i]));
									}
									initLineChartCount();
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

	private void initLineChartCount() {
		Line linecount = new Line(mThirtyPointValues).setColor(Color.parseColor("#66ccff")); // 折线的颜色

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
		axisX.setTextSize(11);// 设置字体大小
		axisX.setMaxLabelChars(7); // 最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
		axisX.setValues(mThirtyAxisX); // 填充X轴的坐标名称
		data.setAxisXBottom(axisX); // x 轴在底部
		// data.setAxisXTop(axisX); //x 轴在顶部
		axisX.setHasLines(true); // x 轴分割线

		Axis axisY = new Axis(); // Y轴
		axisY.setName("");// y轴标注
		axisY.setTextSize(11);// 设置字体大小
		data.setAxisYLeft(axisY); // Y轴设置在左边
		// data.setAxisYRight(axisY); //y轴设置在右边

		line_chart_30_stk.setInteractive(true);
		line_chart_30_stk.setZoomType(ZoomType.HORIZONTAL); // 缩放类型，水平
		line_chart_30_stk.setMaxZoom((float) 3);// 缩放比例
		line_chart_30_stk.setLineChartData(data);

		Viewport vcount = new Viewport(line_chart_30_stk.getMaximumViewport());
		vcount.left = 0;
		vcount.right = 7;
		line_chart_30_stk.setCurrentViewport(vcount);
	}

	private void initLineSevenChartCount() {
		Line linecount = new Line(mSevenPointValues).setColor(Color.parseColor("#66ccff")); // 折线的颜色

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

		axisX.setTextSize(11);// 设置字体大小
		axisX.setMaxLabelChars(7); // 最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
		axisX.setValues(mSevenAxisX); // 填充X轴的坐标名称
		data.setAxisXBottom(axisX); // x 轴在底部
		// data.setAxisXTop(axisX); //x 轴在顶部
		axisX.setHasLines(true); // x 轴分割线

		Axis axisY = new Axis(); // Y轴
		axisY.setName("");// y轴标注
		axisY.setTextSize(11);// 设置字体大小
		data.setAxisYLeft(axisY); // Y轴设置在左边
		// data.setAxisYRight(axisY); //y轴设置在右边

		line_chart_7_stk.setInteractive(true);
		line_chart_7_stk.setZoomType(ZoomType.HORIZONTAL); // 缩放类型，水平
		line_chart_7_stk.setMaxZoom((float) 3);// 缩放比例
		line_chart_7_stk.setLineChartData(data);

		Viewport vcount = new Viewport(line_chart_7_stk.getMaximumViewport());
		vcount.left = 0;
		vcount.right = 7;
		line_chart_7_stk.setCurrentViewport(vcount);
	}

	private void initData() {
		dialog.show();
		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.getFacilityInfo)
						.post(new FormBody.Builder()
								.add("externalLoginKey", Localxml.search(mContext, "externalloginkey")).build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();

						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array = object.optJSONArray("facilityList");
									for (int i = 0; i < array.length(); i++) {
										MtsStkStorageInfo storageInfo = new MtsStkStorageInfo();
										JSONObject obj = array.optJSONObject(i);
										storageInfo.setId(obj.optString("facilityId"));
										storageInfo.setName(obj.optString("facilityName"));
										storageInfo.setFlag(false);
										storageList.add(storageInfo);
										storageAdapter.notifyDataSetChanged();
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

		storageAdapter = new MtsStkStorageAdapter(mContext, storageList);
		lv_mts_stk.setAdapter(storageAdapter);

	}

	private void initListener() {

		lv_mts_stk.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				for (int i = 0; i < storageList.size(); i++) {
					if (i != position) {
						storageList.get(i).setFlag(false);
					}
				}

				if (storageList.get(position).isFlag()) {
					storageList.get(position).setFlag(false);
				} else {
					storageList.get(position).setFlag(true);
				}

				storageAdapter.notifyDataSetChanged();

			}
		});

		rgp_time.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == rbtn_seven.getId()) {
					line_chart_7_stk.setVisibility(View.VISIBLE);
					line_chart_30_stk.setVisibility(View.GONE);
					rbtn_seven.setTextColor(0xffffffff);
					rbtn_thirty.setTextColor(0xff319ce1);
				} else if (checkedId == rbtn_thirty.getId()) {
					line_chart_30_stk.setVisibility(View.VISIBLE);
					line_chart_7_stk.setVisibility(View.GONE);
					rbtn_thirty.setTextColor(0xffffffff);
					rbtn_seven.setTextColor(0xff319ce1);
				}
			}
		});

		tv_stock_detail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, MtsStkDetailActivity.class);
//				intent.putExtra("facilityId", facilityId);
				startActivity(intent);
			}
		});

		tv_ft_mts_stock_chose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ll_ft_mts_stk.setVisibility(View.VISIBLE);
			}
		});

		tv_mts_stk_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ll_ft_mts_stk.setVisibility(View.GONE);
			}
		});

		tv_mts_stk_confirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				selectIndex = -1;
				for (int i = 0; i < storageList.size(); i++) {
					if (storageList.get(i).isFlag()) {
						selectIndex = i;
					}
				}
				if (selectIndex != -1) {
					tv_mts_stk_storage.setText(storageList.get(selectIndex).getName());
					facilityId = storageList.get(selectIndex).getId();
				} else {
					tv_mts_stk_storage.setText("总库");
					facilityId = "";
				}

				kindsList.clear();
				brandList.clear();
				goodsList.clear();
				dialog.show();
				OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_top5inventorylist)
						.post(new FormBody.Builder()
								.add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
								.add("facilityId", facilityId).build())
						.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();

						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array1 = object.optJSONArray("resultListByCategoryName");
									double kindsTotal = 0;
									for (int i = 0; i < array1.length(); i++) {
										kindsInfo = new MtsStkKindsInfo();
										JSONObject obj1 = array1.getJSONObject(i);
										kindsInfo.setName(obj1.optString("name"));
										kindsTotal += obj1.optDouble("value");
										kindsInfo.setPercent(df.format(obj1.optDouble("value") / kindsTotal));
										kindsInfo.setStk(obj1.optString("value"));
										kindsList.add(kindsInfo);
										kindsAdapter.notifyDataSetChanged();
									}

									JSONArray array2 = object.optJSONArray("resultListByBrandName");
									double brandTotal = 0;
									for (int i = 0; i < array2.length(); i++) {
										brandInfo = new MtsStkBrandInfo();
										JSONObject obj2 = array2.getJSONObject(i);
										brandInfo.setName(obj2.optString("name"));
										brandTotal += obj2.optDouble("value");
										brandInfo.setPercent(df.format(obj2.optDouble("value") / brandTotal));
										brandInfo.setStk(obj2.optString("value"));
										brandList.add(brandInfo);
										brandAdapter.notifyDataSetChanged();
									}

									JSONArray array3 = object.optJSONArray("resultListByProductName");
									double goodsTotal = 0;
									for (int i = 0; i < array3.length(); i++) {
										goodsInfo = new MtsStkGoodsInfo();
										JSONObject obj3 = array3.getJSONObject(i);
										goodsInfo.setName(obj3.optString("name"));
										goodsTotal += obj3.optDouble("value");
										goodsInfo.setPercent(df.format(obj3.optDouble("value") / goodsTotal));
										goodsInfo.setStk(obj3.optString("value"));
										goodsList.add(goodsInfo);
										goodsAdapter.notifyDataSetChanged();
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
				ll_ft_mts_stk.setVisibility(View.GONE);

				String uri = MtsUrls.base + MtsUrls.getInvetoryChanged;
				SevenListDate.clear();
				sevenListCount.clear();
				mSevenAxisX.clear();
				mSevenPointValues.clear();

				OkHttpManager.client
						.newCall(new Request.Builder().url(uri)
								.post(new FormBody.Builder()
										.add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
										.add("facilityId", facilityId).add("dataFlg", "sevenData").build())
								.build())
						.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									SevenListDate = new ArrayList<String>();
									sevenListCount = new ArrayList<Float>();
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array1 = object.optJSONArray("xAxisData");
									String[] sevenDate = new String[array1.length()];

									for (int i = 0; i < array1.length(); i++) {
										String days = array1.get(i).toString().substring(5, 10);
										SevenListDate.add(days);
									}

									SevenListDate.toArray(sevenDate);

									JSONArray array2 = object.optJSONArray("seriesData");
									Float[] sevenCount = new Float[array2.length()];
									for (int i = 0; i < array2.length(); i++) {
										Float icount = Float.parseFloat(array2.get(i).toString());
										sevenListCount.add(icount);
									}
									sevenCount = sevenListCount.toArray(sevenCount);
									for (int i = 0; i < sevenDate.length; i++) {
										mSevenAxisX.add(new AxisValue(i).setLabel(sevenDate[i]));
									}
									for (int i = 0; i < sevenCount.length; i++) {
										mSevenPointValues.add(new PointValue(i, sevenCount[i]));
									}
									initLineSevenChartCount();
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

				thirtyListDate.clear();
				thirtyListCount.clear();
				mThirtyAxisX.clear();
				mThirtyPointValues.clear();

				OkHttpManager.client
						.newCall(new Request.Builder().url(uri)
								.post(new FormBody.Builder()
										.add("externalLoginKey", Localxml.search(mContext, "externalloginkey"))
										.add("facilityId", facilityId).add("dataFlg", "thirtyData").build())
								.build())
						.enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();
						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									thirtyListDate = new ArrayList<String>();
									thirtyListCount = new ArrayList<Float>();
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array1 = object.optJSONArray("xAxisData");
									String[] thirtyDate = new String[array1.length()];

									for (int i = 0; i < array1.length(); i++) {
										String days = array1.get(i).toString().substring(5, 10);
										thirtyListDate.add(days);
									}

									thirtyListDate.toArray(thirtyDate);

									JSONArray array2 = object.optJSONArray("seriesData");
									Float[] thirtyCount = new Float[array2.length()];
									for (int i = 0; i < array2.length(); i++) {
										Float icount = Float.parseFloat(array2.get(i).toString());
										thirtyListCount.add(icount);
									}
									thirtyCount = thirtyListCount.toArray(thirtyCount);
									for (int i = 0; i < thirtyDate.length; i++) {
										mThirtyAxisX.add(new AxisValue(i).setLabel(thirtyDate[i]));
									}
									for (int i = 0; i < thirtyCount.length; i++) {
										mThirtyPointValues.add(new PointValue(i, thirtyCount[i]));
									}
									initLineChartCount();
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

	}

	private void initPager() {

		dialog.show();

		OkHttpManager.client
				.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_top5inventorylist)
						.post(new FormBody.Builder()
								.add("externalLoginKey", Localxml.search(mContext, "externalloginkey")).build())
				.build()).enqueue(new Callback() {

					@Override
					public void onResponse(Call arg0, Response response) throws IOException {
						if (!response.isSuccessful()) {
							return;
						}
						final String jsonStr = response.body().string();

						new Handler(Looper.getMainLooper()).post(new Runnable() {

							@Override
							public void run() {
								dialog.dismiss();
								try {
									JSONObject object = new JSONObject(jsonStr);
									JSONArray array1 = object.optJSONArray("resultListByCategoryName");
									double kindsTotal = 0;
									for (int i = 0; i < array1.length(); i++) {
										kindsInfo = new MtsStkKindsInfo();
										JSONObject obj1 = array1.getJSONObject(i);
										kindsInfo.setName(obj1.optString("name"));
										kindsTotal += obj1.optDouble("value");
										kindsInfo.setPercent(df.format(obj1.optDouble("value") / kindsTotal));
										kindsInfo.setStk(obj1.optString("value"));
										kindsList.add(kindsInfo);
										kindsAdapter.notifyDataSetChanged();
									}

									JSONArray array2 = object.optJSONArray("resultListByBrandName");
									double brandTotal = 0;
									for (int i = 0; i < array2.length(); i++) {
										brandInfo = new MtsStkBrandInfo();
										JSONObject obj2 = array2.getJSONObject(i);
										brandInfo.setName(obj2.optString("name"));
										brandTotal += obj2.optDouble("value");
										brandInfo.setPercent(df.format(obj2.optDouble("value") / brandTotal));
										brandInfo.setStk(obj2.optString("value"));
										brandList.add(brandInfo);
										brandAdapter.notifyDataSetChanged();
									}

									JSONArray array3 = object.optJSONArray("resultListByProductName");
									double goodsTotal = 0;
									for (int i = 0; i < array3.length(); i++) {
										goodsInfo = new MtsStkGoodsInfo();
										JSONObject obj3 = array3.getJSONObject(i);
										goodsInfo.setName(obj3.optString("name"));
										goodsTotal += obj3.optDouble("value");
										goodsInfo.setPercent(df.format(obj3.optDouble("value") / goodsTotal));
										goodsInfo.setStk(obj3.optString("value"));
										goodsList.add(goodsInfo);
										goodsAdapter.notifyDataSetChanged();
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

		brandAdapter = new MtsStkVpBrandAdapter(mContext, brandList);
		goodsAdapter = new MtsStkVpGoodsAdapter(mContext, goodsList);
		kindsAdapter = new MtsStkVpKindsAdapter(mContext, kindsList);

		final List<ListView> pagerList = new ArrayList<ListView>();

		lv_one = new ListView(mContext);
		lv_two = new ListView(mContext);
		lv_three = new ListView(mContext);
		lv_one.setBackgroundResource(R.drawable.stock_top5_bg);
		lv_two.setBackgroundResource(R.drawable.stock_goods5_bg);
		lv_three.setBackgroundResource(R.drawable.stock_kinds5_bg);
		lv_one.setPadding(50, 100, 50, 20);
		lv_two.setPadding(50, 100, 50, 20);
		lv_three.setPadding(50, 100, 50, 20);
		lv_one.setDivider(null);
		lv_two.setDivider(null);
		lv_three.setDivider(null);
		lv_one.setDividerHeight(15);
		lv_two.setDividerHeight(15);
		lv_three.setDividerHeight(15);

		lv_one.setAdapter(brandAdapter);
		lv_two.setAdapter(goodsAdapter);
		lv_three.setAdapter(kindsAdapter);

		pagerList.add(lv_one);
		pagerList.add(lv_two);
		pagerList.add(lv_three);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		CenterViewPagerAdapter adapter = new CenterViewPagerAdapter(mContext, pagerList);
		cvp_stock.setAdapter(adapter);
		cvp_stock.enableCenterLockOfChilds();
		cvp_stock.setPageTransformer(true, new ZoomOutPageTransformer());

		for (int i = 0; i < pagerList.size(); i++) {
			// 三个点
			View point = new View(mContext);
			point.setBackgroundResource(R.drawable.dot_not_focus);
			LayoutParams params = new LayoutParams(15, 15);
			params.leftMargin = 10;
			point.setLayoutParams(params);
			ll_stock_point.addView(point);
		}
		// 设置第一个点为默认点
		ll_stock_point.getChildAt(0).setBackgroundResource(R.drawable.dot_focus);
		cvp_stock.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// 当滑动到下一张图片，修改之前图片的点
				ll_stock_point.getChildAt(prePosition).setBackgroundResource(R.drawable.dot_not_focus);
				// 滑动到当前图片，修改当前图片的点
				ll_stock_point.getChildAt(position % pagerList.size()).setBackgroundResource(R.drawable.dot_focus);
				// 这一次的当前位置为下一次当前位置的前一个选中条目
				prePosition = position % pagerList.size();
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}

	private void initView() {
		tv_mts_stk_storage = (TextView) view.findViewById(R.id.tv_mts_stk_storage);
		tv_ft_mts_stock_chose = (TextView) view.findViewById(R.id.tv_ft_mts_stock_chose);
		tv_stock_detail = (TextView) view.findViewById(R.id.tv_stock_detail);
		tv_mts_stk_cancel = (TextView) view.findViewById(R.id.tv_mts_stk_cancel);
		tv_mts_stk_confirm = (TextView) view.findViewById(R.id.tv_mts_stk_confirm);
		ll_ft_mts_stk = (LinearLayout) view.findViewById(R.id.ll_ft_mts_stock);
		lv_mts_stk = (ListView) view.findViewById(R.id.lv_mts_stk);
		rgp_time = (RadioGroup) view.findViewById(R.id.rgp_time);
		rbtn_seven = (RadioButton) view.findViewById(R.id.rbtn_seven);
		rbtn_thirty = (RadioButton) view.findViewById(R.id.rbtn_thirty);
		ll_stock_point = (LinearLayout) view.findViewById(R.id.ll_stock_point);
		cvp_stock = (CenterViewPager) view.findViewById(R.id.cvp_stock);

		line_chart_7_stk = (LineChartView) view.findViewById(R.id.line_chart_7_stk);
		line_chart_30_stk = (LineChartView) view.findViewById(R.id.line_chart_30_stk);

		dialog = ProgressDialog.show(mContext, "请稍候。。。", "获取数据中。。。", true);
		dialog.dismiss();
		df = new DecimalFormat("#0.0");

		brandList = new ArrayList<MtsStkBrandInfo>();
		goodsList = new ArrayList<MtsStkGoodsInfo>();
		kindsList = new ArrayList<MtsStkKindsInfo>();
		storageList = new ArrayList<MtsStkStorageInfo>();

	}

	@SuppressWarnings("static-access")
	public static boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK && event.getRepeatCount() == 0) {
			dialog.dismiss();
		}
		return true;
	}

}
