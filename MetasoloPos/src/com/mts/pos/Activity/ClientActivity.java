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
import com.mts.pos.listview.ClientAdapter;
import com.mts.pos.listview.ClientInfo;
import com.mts.pos.listview.LeftMenuInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

public class ClientActivity extends BaseLeftMenuActivity {

	private Button btn_client_menu;
	private ListView lv_client;
	public static ClientAdapter clientadapter = null;
	public static List<ClientInfo> clientData = null;
	public static ClientInfo info = null;
	public TextView tv_save;

	@Override
	protected void onCreate(Bundle inState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mMenuDrawer = MenuDrawer.attach(this, MenuDrawer.Type.BEHIND, getDrawerPosition(), getDragMode());
		mMenuDrawer.setContentView(R.layout.activity_client);
		super.onCreate(inState);

		btn_client_menu = (Button) findViewById(R.id.btn_client_menu);
		lv_client = (ListView) findViewById(R.id.lv_client);
		tv_save = (TextView) findViewById(R.id.tv_save);

		clientData = new ArrayList<ClientInfo>();
		clientadapter = new ClientAdapter(ClientActivity.this, clientData);
		lv_client.setAdapter(clientadapter);

		List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
		nameValuePair.add(
				new BasicNameValuePair("externalLoginKey", Localxml.search(ClientActivity.this, "externalloginkey")));
		nameValuePair.add(new BasicNameValuePair("productStoreId", Localxml.search(ClientActivity.this, "storeid")));
		getTask(ClientActivity.this, Urls.base + Urls.get_clientflow, nameValuePair, "0");

		btn_client_menu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMenuDrawer.openMenu();
				
//				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
//				nameValuePair.add(new BasicNameValuePair("externalLoginKey",
//						Localxml.search(ClientActivity.this, "externalloginkey")));
//				nameValuePair
//						.add(new BasicNameValuePair("productStoreId", Localxml.search(ClientActivity.this, "storeid")));
//				nameValuePair.add(new BasicNameValuePair("clientFlowStatisticData", creatJSON()));
//				getTask(ClientActivity.this, Urls.base + Urls.save_clientflow, nameValuePair, "1");
				
			}
		});

		tv_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				

			}
		});

	}

	@Override
	protected void updateUI(String whichtask, String result) {
		if (whichtask.equals("0")) {
			Log.e("LOOK", "查询客流量数据==" + result);
			try {
				JSONObject object = new JSONObject(result);
				JSONArray jsonarray = object.optJSONArray("resultList");
				if (jsonarray.length() == 0) {
					Toast.makeText(ClientActivity.this, "目前没有客流量信息", Toast.LENGTH_SHORT).show();
				}
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject jo = (JSONObject) jsonarray.get(i);
					info = new ClientInfo();
					info.setTimeSlot(jo.optString("time"));
					String weather = "";
					if (jo.optString("weather").equals("sunny")) {
						weather = "晴";
					} else if (jo.optString("weather").equals("cloudy")) {
						weather = "多云";
					} else if (jo.optString("weather").equals("lunar")) {
						weather = "阴";
					} else if (jo.optString("weather").equals("rainy")) {
						weather = "雨";
					} else if (jo.optString("weather").equals("snowy")) {
						weather = "雪";
					} else {
						weather = "雾霾";
					}
					info.setWeather(weather);
					info.setWeatherNo(jo.optString("weather"));
					info.setPeople(jo.optString("number").equals("null") ? "0" : jo.optString("number"));
					info.setRemark(jo.optString("comment").equals("null") ? "" : jo.optString("comment"));
					clientData.add(info);
					clientadapter.notifyDataSetChanged();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (whichtask.equals("1")) {
			Log.e("LOOK", "保存结果==" + result);
			try {
				String isSuccess = new JSONObject(result).optString("isSuccess");
				if (isSuccess.equals("Y")) {
					Toast.makeText(ClientActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}

	@Override
	protected void onMenuItemClicked(int position, LeftMenuInfo item) {
		// TODO Auto-generated method stub

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

	public String creatJSON() {
		JSONObject allObj = new JSONObject();
		JSONArray clientList = new JSONArray();
		try {
			for (int i = 0; i < clientData.size(); i++) {
				JSONObject client = new JSONObject();

				client.put("timeSlot", clientData.get(i).getTimeSlot());
				client.put("weatherInfo", clientData.get(i).getWeatherNo());
				client.put("numberOfPeople", clientData.get(i).getPeople());
				client.put("commentText", clientData.get(i).getRemark());
				clientList.put(client);
			}
			allObj.putOpt("clientFlowItems", clientList);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("LOOK", "客流量JSON==" + allObj.toString());
		return allObj.toString();
	}

}
