package com.mts.pos.Activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mts.pos.R;
import com.mts.pos.Common.BaseLeftMenuActivity;
import com.mts.pos.Common.Localxml;
import com.mts.pos.Common.Urls;
import com.mts.pos.listview.KeepbillsAdapter;
import com.mts.pos.listview.KeepbillsInfo;
import com.mts.pos.listview.KeepbillsPicInfo;
import com.mts.pos.listview.LeftMenuInfo;
import com.mts.pos.listview.ProductInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public class KeepbillsActivity extends BaseLeftMenuActivity {

	private Button btn_keepbills_menu;
	private ListView lv_keepbills;
	private KeepbillsAdapter keepbillsadapter;
	public static List<KeepbillsPicInfo> billsPicData = null;
	public static KeepbillsPicInfo picinfo = null;

	public static List<KeepbillsInfo> billsData = null;
	public static KeepbillsInfo info = null;

	public static String billsId, total, name, stamp, productName, productImg, productId, productColor, productSize,
			productCount, productPrice, productBrand;
	public boolean being;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
		mMenuDrawer.setContentView(R.layout.activity_keepbills);
		super.onCreate(inState);

		btn_keepbills_menu = (Button) findViewById(R.id.btn_keepbills_menu);
		lv_keepbills = (ListView) findViewById(R.id.lv_keepbills);

		billsData = new ArrayList<KeepbillsInfo>();
		billsPicData = new ArrayList<KeepbillsPicInfo>();
		keepbillsadapter = new KeepbillsAdapter(KeepbillsActivity.this, billsData);
		lv_keepbills.setAdapter(keepbillsadapter);

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(new BasicNameValuePair("externalLoginKey",
				Localxml.search(KeepbillsActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("productStoreId", Localxml.search(KeepbillsActivity.this, "storeid")));
		getTask(KeepbillsActivity.this, Urls.base + Urls.search_keepbills, nameValuePair, "0");

		btn_keepbills_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMenuDrawer.openMenu();
			}
		});
	}

	@Override
	protected void updateUI(String whichtask, String result) {

		if (whichtask.equals("0")) {
			Log.e("Look", "挂单信息==" + result);

			try {
				String count = new JSONObject(result).getString("itemCount");
				JSONArray jsonarray = new JSONObject(result).getJSONArray("bills");
				if (jsonarray.length() != 0) {
					for (int i = 0; i < jsonarray.length(); i++) {

						JSONObject jo = (JSONObject) jsonarray.get(i);
						JSONObject value = jo.getJSONObject("value");

						billsId = value.getString("shoppingListId");
						total = value.getString("grandTotal");
						name = value.getString("customerName");
						JSONObject time = value.getJSONObject("createdStamp");
						Integer monthI = Integer.valueOf(time.getString("month")) + 1;
						String month;
						if (monthI < 10) {
							month = "0" + monthI.toString();
						} else {
							month = monthI.toString();
						}

						Integer secondI = Integer.valueOf(time.getString("seconds"));
						String second;
						if (secondI < 10) {
							second = "0" + secondI.toString();
						} else {
							second = secondI.toString();
						}

						Integer minuteI = Integer.valueOf(time.getString("minutes"));
						String minute;
						if (minuteI < 10) {
							minute = "0" + minuteI.toString();
						} else {
							minute = secondI.toString();
						}

						Integer dateI = Integer.valueOf(time.getString("date"));
						String date;
						if (dateI < 10) {
							date = "0" + dateI.toString();
						} else {
							date = dateI.toString();
						}

						Integer hoursI = Integer.valueOf(time.getString("hours"));
						String hours;
						if (hoursI < 10) {
							hours = "0" + hoursI.toString();
						} else {
							hours = hoursI.toString();
						}

						stamp = "20" + time.getString("year").substring(1, time.getString("year").length()) + "-"
								+ month + "-" + date + "  " + hours + ":" + minute + ":" + second;
						JSONArray picarray = jo.getJSONArray("items");

						for (int j = 0; j < picarray.length(); j++) {
							JSONObject jop = (JSONObject) picarray.opt(j);
							productName = jop.optString("productName");
							productImg = jop.optString("skuImageUrl");
							productId = jop.optString("productId");
							productColor = jop.optString("colorDesc");
							productCount = jop.optString("quantity");
							productPrice = jop.optString("listPrice");
							productBrand = jop.optString("brandName");
							productSize = jop.optString("dimensionDesc");

							picinfo = new KeepbillsPicInfo();
							picinfo.setProductName(productName);
							picinfo.setImageUrl(productImg);
							picinfo.setBrandCnName(productBrand);
							picinfo.setColor(productColor);
							picinfo.setCount(Integer.valueOf(productCount));
							picinfo.setId(productId);
							picinfo.setPrice(Integer.valueOf(productPrice));
							picinfo.setSize(productSize);
							billsPicData.add(picinfo);
						}

						info = new KeepbillsInfo();
						info.setAmount(total);
						info.setBillsID(billsId);
						info.setBillsTime(stamp);
						info.setCustomerName(name);
						info.setList(billsPicData);
						billsData.add(info);
						keepbillsadapter.notifyDataSetChanged();

					}

				} else {
					Toast.makeText(KeepbillsActivity.this, "当前没有挂单信息！", Toast.LENGTH_SHORT).show();

				}

				// picinfo = new KeepbillsPicInfo();
				// picinfo.setProductName(productName);
				// picinfo.setImageUrl(productImg);
				// billsPicData.add(picinfo);
				// keepbillsadapter.notifyDataSetChanged();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onMenuItemClicked(int position, LeftMenuInfo item) {

	}

	@Override
	protected int getDragMode() {
		return MenuDrawer.MENU_DRAG_CONTENT;
	}

	@Override
	protected Position getDrawerPosition() {
		return Position.LEFT;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mMenuDrawer.toggleMenu();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

}
