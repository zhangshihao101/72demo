package com.spt.sht.wxapi;

import com.spt.page.DistributionActivity;
import com.spt.sht.R;
import com.spt.wechat.WXHelper;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this, WXHelper.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0) {
				Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
				openOrderList();
				WXPayEntryActivity.this.finish();
			} else if (resp.errCode == -1) {
				Toast.makeText(this, "交易失败", Toast.LENGTH_SHORT).show();
				openOrderList();
				WXPayEntryActivity.this.finish();
			} else if (resp.errCode == -2) {
				Toast.makeText(this, "交易取消", Toast.LENGTH_SHORT).show();
				openOrderList();
				WXPayEntryActivity.this.finish();
			}

		}
	}

	/**
	 * 打开订单列表
	 */
	private void openOrderList() {
		Intent intent = new Intent(WXPayEntryActivity.this, DistributionActivity.class);
		intent.putExtra("page", 0);
		intent.putExtra("tag", 2);
		startActivity(intent);
	}

}