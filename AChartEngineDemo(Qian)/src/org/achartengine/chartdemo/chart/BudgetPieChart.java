/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.achartengine.chartdemo.chart;

import java.text.NumberFormat;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * Budget demo pie chart.
 */
public class BudgetPieChart extends AbstractDemoChart {
	public BudgetPieChart(Context context, LinearLayout layout) {
		super();
		execute2(context, layout);
	}

	public BudgetPieChart() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Budget chart";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The budget per project for this year (pie chart)";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public Intent execute(Context context) {
		double[] values = new double[] { 12, 14, 11, 10, 19 };// 饼图分层5块,每块代表的数值
		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.CYAN };// 每块饼图的颜色
		DefaultRenderer renderer = buildCategoryRenderer(colors);
		renderer.setZoomButtonsVisible(true);// 设置显示放大缩小按钮
		renderer.setZoomEnabled(true);// 设置允许放大缩小.
		renderer.setChartTitleTextSize(20);// 设置图表标题的文字大小
		return ChartFactory.getPieChartIntent(context, buildCategoryDataset("客户状态统计", values), renderer, "饼状图");// 构建Intent,
																												// buildCategoryDataset是调用AbstraDemoChart的构建方法.
	}

	public void execute2(Context context, LinearLayout layout) {
		double[] values = new double[] { 12, 14, 11, 10, 19, 20 };
		// int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA,
		// Color.YELLOW, Color.CYAN };
		int[] colors = new int[] { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.YELLOW, Color.CYAN };
		DefaultRenderer renderer = buildCategoryRenderer(colors);
		renderer.setZoomButtonsVisible(true);
		renderer.setZoomEnabled(true);
		int[] margins = new int[] { Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK };
		renderer.setMargins(margins);
		renderer.setLegendTextSize(15);// 设置图例，下面的说明文字的大小
		renderer.setChartTitle("客户状态统计");
		renderer.setChartTitleTextSize(20);// 设置标题的大小
		renderer.setFitLegend(true);// 是否显示图例
		String[] titles = new String[] { "未联系", "已联系", "未到访", "已到访", "已认购", "已放弃" };
		SimpleSeriesRenderer simpleSeriesRenderer = new SimpleSeriesRenderer();
		CategorySeries mSeries = new CategorySeries("客户状态统计");
		for (int i = 0; i < titles.length; i++) {
			mSeries.add(titles[i] + "-" + values[i], values[i]);
			if (i < colors.length) {
				simpleSeriesRenderer.setColor(colors[i]);// 设置描绘器的颜色

			} else {

				simpleSeriesRenderer.setColor(getRandomColor());// 设置描绘器的颜色

			}

//			 renderer.setChartValuesFormat(NumberFormat.getPercentInstance());//
			// 设置百分比
		}

		renderer.addSeriesRenderer(simpleSeriesRenderer);// 将最新的描绘器添加到DefaultRenderer中
		GraphicalView view = ChartFactory.getPieChartView(context, mSeries, renderer);
		view.setBackgroundColor(Color.WHITE);
		layout.addView(view, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	int getRandomColor() {
		Random random = new Random();
		int r = random.nextInt(256);
		int g = random.nextInt(256);
		int b = random.nextInt(256);
		int mColor = Color.rgb(r, g, b);
		return mColor;
	}

}
