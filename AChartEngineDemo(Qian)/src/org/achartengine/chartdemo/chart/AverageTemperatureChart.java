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

import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * Average temperature demo chart.
 */
public class AverageTemperatureChart extends AbstractDemoChart {
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */

	public AverageTemperatureChart(Context context,LinearLayout layout,int maxYValue)
	{
		super();
		execute2(context,layout,maxYValue);
	}
	public AverageTemperatureChart()
	{
		super();
	}

	public String getName() {
		return "Average temperature";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The average temperature in 4 Greek islands (line chart)";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context the context
	 * @return the built intent
	 */
	public Intent execute(Context context) {
		//几条线
		String[] titles = new String[] { "新客户", "联系客户", "到访客户", "认购客户","签约客户" };
		//每个序列中点的X坐标
		List<double[]> x = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
		}

		List<double[]> values = new ArrayList<double[]>();
		//  序列1中点的y坐标  
		values.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
				13.9 });
		//  序列2中点的y坐标  
		values.add(new double[] { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 });
		//  序列3中点的y坐标  
		values.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 });
		//  序列4中点的y坐标  
		values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });
		//  序列5中点的y坐标  
		values.add(new double[] { 9, 15, 13, 15, 15, 28, 23, 34, 22, 18, 3, 10 });

		//每个序列的颜色设置  
		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW,Color.RED};
		//每个序列中点的形状设置
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
				PointStyle.TRIANGLE, PointStyle.SQUARE };
		//		调用AbstractDemoChart中的方法设置renderer.  
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);//设置图上的点为实心  
		}
		//调用AbstractDemoChart中的方法设置图表的renderer属性. 后面是数值的x坐标 
		setChartSettings(renderer, "Average temperature", "Month", "Temperature", 0,32, 0, Integer.MAX_VALUE,
				Color.LTGRAY, Color.LTGRAY);
		renderer.setXLabels(10);//设置x轴显示12个点,根据setChartSettings的最大值和最小值自动计算点的间隔  
		renderer.setYLabels(10);//设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间
		renderer.setShowGrid(true);//是否显示网格 
		renderer.setXLabelsAlign(Align.RIGHT);//刻度线与刻度标注之间的相对位置关系  
		renderer.setYLabelsAlign(Align.CENTER);//刻度线与刻度标注之间的相对位置关系  
		renderer.setZoomButtonsVisible(true);//是否显示放大缩小按钮  

		renderer.setPanLimits(new double[] { -10, 20, -10, 40 }); //设置拖动时X轴Y轴允许的最大值最小值.  
		renderer.setZoomLimits(new double[] {  -10, 20, -10, 40 });//设置放大缩小时X轴Y轴允许的最大最小值.  


		Intent intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values),
				renderer, "Average temperature");
		return intent;
	}

	public void execute11(Context context,LinearLayout layout) {
		String[] titles = new String[] { "Crete", "Corfu", "Thassos", "Skiathos" };
		List<double[]> x = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
		}
		List<double[]> values = new ArrayList<double[]>();
		values.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
				13.9 });
		values.add(new double[] { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 });
		values.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 });
		values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });
		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
				PointStyle.TRIANGLE, PointStyle.SQUARE,PointStyle.SQUARE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}

		setChartSettings(renderer, "Average temperature", "Month", "Temperature", 0.5, 12.5, -10, 40,
				Color.LTGRAY, Color.LTGRAY);
		renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.RIGHT);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setZoomButtonsVisible(false);
		renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });

		Intent intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values),
				renderer, "Average temperature");

		GraphicalView view = ChartFactory.getLineChartView(context, buildDataset(titles, x, values), renderer);
		layout.addView(view,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));  
	}
	public void execute2(Context context,LinearLayout layout,int maxYValues) {
		//几条线
		String[] titles = new String[] { "新客户", "联系客户", "到访客户", "认购客户","签约客户" };
		//每个序列中点的X坐标
		List<double[]> x = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24 });
		}

		List<double[]> values = new ArrayList<double[]>();
		//  序列1中点的y坐标  
		values.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
				13.9,12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2,
				13.9 });
		//  序列2中点的y坐标  
		values.add(new double[] { 10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11,10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11 });
		//  序列3中点的y坐标  
		values.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6, 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6 });
		//  序列4中点的y坐标  
		values.add(new double[] { 9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10,5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6,  });
		//  序列5中点的y坐标  
		values.add(new double[] { 9, 15, 13, 15, 15, 28, 23, 34, 22, 18, 3, 10,9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10 });

		//每个序列的颜色设置  
		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW,Color.RED};
		//每个序列中点的形状设置
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.DIAMOND,
				PointStyle.TRIANGLE, PointStyle.SQUARE , PointStyle.CIRCLE};
		//		调用AbstractDemoChart中的方法设置renderer.  
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);//设置图上的点为实心  
		}
		//调用AbstractDemoChart中的方法设置图表的renderer属性. 后面是数值的x坐标 
		//这里的10表示x的刚开始显示的时候x的坐标轴的值为10
		setChartSettings(renderer, "2014年12月进度统计", "日", "客户人数", 0,31, 0, maxYValues,
				Color.LTGRAY, Color.LTGRAY);
		renderer.setChartTitle("2014年12月进度统计");
		renderer.setXLabels(10);//设置x轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔  
		renderer.setYLabels(10);//设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间
		renderer.setShowGrid(true);//是否显示网格 
		renderer.setXLabelsAlign(Align.RIGHT);//刻度线与刻度标注之间的相对位置关系  
		renderer.setYLabelsAlign(Align.CENTER);//刻度线与刻度标注之间的相对位置关系  
		renderer.setZoomButtonsVisible(true);//是否显示放大缩小按钮  
		renderer.setBackgroundColor(Color.TRANSPARENT);
		renderer.setApplyBackgroundColor(true); // 使背景色生效
		renderer.setPanLimits(new double[] { 0, 32, 0, maxYValues }); //设置拖动时X轴Y轴允许的最大值最小值.  (这里的32表示x的值数据坐标点的最大x坐标)
		renderer.setZoomLimits(new double[] {  0, 32, 0, maxYValues });//设置放大缩小时X轴Y轴允许的最大最小值.  
		Intent intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, values),
				renderer, "Average temperature");
		GraphicalView view = ChartFactory.getLineChartView(context, buildDataset(titles, x, values), renderer);
		view.setBackgroundColor(Color.WHITE);
		layout.addView(view,new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));  
	}

}
