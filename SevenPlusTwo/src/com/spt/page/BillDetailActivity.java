package com.spt.page;

import com.spt.controler.MyTitleBar;
import com.spt.sht.R;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 【对账单详情】页
 * */
public class BillDetailActivity extends BaseActivity {

	private MyTitleBar mtbWeb;
	private TextView tvTitle;
	private ImageView ivLeft;
	private LinearLayout llLeft;
	private LinearLayout llRight;
	private WebView wv;
	private Intent itFrom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentID(R.layout.billdetail);
		super.onCreate(savedInstanceState);
		initContent();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initContent() {
		String url = itFrom.getStringExtra("url");
		WebSettings webSettings = wv.getSettings();
		webSettings.setDefaultTextEncodingName("UTF-8");
		webSettings.setJavaScriptEnabled(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLightTouchEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setUseWideViewPort(true);
		wv.setHapticFeedbackEnabled(false);
		wv.setInitialScale(0);
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith("enlarge:")) {
					url = url.substring(8);
					// view.loadUrl(url);
					return true;
				}
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
		wv.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);
	}

	@Override
	protected void addClickEvent() {
		llLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void init() {
		this.mtbWeb = (MyTitleBar) findViewById(R.id.mtb_web_title);
		this.tvTitle = mtbWeb.getTvTitle();
		this.ivLeft = mtbWeb.getIvLeft();
		this.tvTitle.setText("对账单详情");
		this.ivLeft.setBackgroundResource(R.drawable.titlemenu);
		this.llLeft = mtbWeb.getLlLeft();
		this.llRight = mtbWeb.getLlRight();
		this.llRight.setVisibility(View.INVISIBLE);
		this.wv = (WebView) findViewById(R.id.wv_web_content);
		this.itFrom = getIntent();
	}
}
