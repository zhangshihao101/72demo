package org.achartengine.chartdemo;

import org.achartengine.chartdemo.chart.AbstractDemoChart;
import org.achartengine.chartdemo.chart.AverageTemperatureChart;
import org.achartengine.chartdemo.chart.BudgetPieChart;
import org.achartengine.chartdemo.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class AverageTemperatureChartActivity extends Activity {

	public AbstractDemoChart ureChart;
	public int maxY = 30;// 这个代表y的最大值。是传进来的
	public String[] personCount = new String[] { "12", "" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.achartengine_loading);
		LinearLayout layout = (LinearLayout) findViewById(R.id.line_zhexian);
		LinearLayout layout2 = (LinearLayout) findViewById(R.id.line_yuanbing);
		AverageTemperatureChart chart = new AverageTemperatureChart(this, layout, maxY);
		BudgetPieChart budgetPieChart = new BudgetPieChart(this, layout2);

	}
}
