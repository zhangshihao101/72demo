package com.spt.dialog;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spt.sht.R;
import com.spt.utils.DBManager;
import com.spt.wheel.AbstractWheelTextAdapter;
import com.spt.wheel.OnWheelChangedListener;
import com.spt.wheel.OnWheelScrollListener;
import com.spt.wheel.WheelView;

/**
 * 
 * 更改地址对话框
 */
public class ChangeAddressDialog extends Dialog implements OnClickListener {

	private WheelView wvProvince;
	private WheelView wvCitys;
	private WheelView wvCountry;
	private View lyChangeAddress;
	private View lyChangeAddressChild;
	private TextView btnSure;
	private TextView btnCancel;

	private Context context;

	private ArrayList<String> arrProvinces = new ArrayList<String>();
	private ArrayList<String> arrCitys = new ArrayList<String>();
	private ArrayList<String> arrCountrys = new ArrayList<String>();
	private AddressTextAdapter provinceAdapter;
	private AddressTextAdapter cityAdapter;
	private AddressTextAdapter countryAdapter;

	private String strProvince = "北京";
	private String strCity = "北京市";
	private String strCountry = "东城";
	private String strProvinceId;
	private String strCityId;
	private String strCountryId;
	private OnAddressCListener onAddressCListener;

	private int maxsize = 24;
	private int minsize = 14;
	// {"城市名", "城市id"}的形式，方便点选城市的时候找到对应的id
	private HashMap<String, String> mapProvince = new HashMap<String, String>();
	private HashMap<String, String> mapCity = new HashMap<String, String>();
	private HashMap<String, String> mapCountry = new HashMap<String, String>();
	private String[] selectionArgs;
	private DBManager dbHelper;
	private String dbName = "oldcity.db"; // 保存的数据库文件名
	private String packageName = "com.spt.sht"; // 包名
	private String tableName = "lym_region"; // 待查询的表名
	private int resourceId = R.raw.oldcity; // 待查询的表名
	private String[] columns = new String[] { "region_id", "region_name" };
	private String selection = " parent_id = ?";

	public ChangeAddressDialog(Context context) {
		super(context, R.style.ShareDialog);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_myinfo_changeaddress);

		wvProvince = (WheelView) findViewById(R.id.wv_address_province);
		wvCitys = (WheelView) findViewById(R.id.wv_address_city);
		wvCountry = (WheelView) findViewById(R.id.wv_address_country);
		lyChangeAddress = findViewById(R.id.ly_myinfo_changeaddress);
		lyChangeAddressChild = findViewById(R.id.ly_myinfo_changeaddress_child);
		btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
		btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);

		lyChangeAddress.setOnClickListener(this);
		lyChangeAddressChild.setOnClickListener(this);
		btnSure.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

		init();
		callDB("1", arrProvinces, mapProvince);
		provinceAdapter = new AddressTextAdapter(context, arrProvinces, getProvinceItem(strProvince), maxsize, minsize);
		wvProvince.setVisibleItems(5);
		wvProvince.setViewAdapter(provinceAdapter);
		wvProvince.setCurrentItem(getProvinceItem(strProvince));

		callDB("135", arrCitys, mapCity);
		cityAdapter = new AddressTextAdapter(context, arrCitys, getCityItem(strCity), maxsize, minsize);
		wvCitys.setVisibleItems(5);
		wvCitys.setViewAdapter(cityAdapter);
		wvCitys.setCurrentItem(getCityItem(strCity));

		callDB("3545", arrCountrys, mapCountry);
		countryAdapter = new AddressTextAdapter(context, arrCountrys, getCountryItem(strCountry), maxsize, minsize);
		wvCountry.setVisibleItems(5);
		wvCountry.setViewAdapter(countryAdapter);
		wvCountry.setCurrentItem(getCountryItem(strCountry));

		wvProvince.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
				strProvince = currentText;
				setTextviewSize(currentText, provinceAdapter);
				callDB(mapProvince.get(strProvince), arrCitys, mapCity);
				cityAdapter = new AddressTextAdapter(context, arrCitys, 0, maxsize, minsize);
				wvCitys.setVisibleItems(5);
				wvCitys.setViewAdapter(cityAdapter);
				wvCitys.setCurrentItem(0);
				// 第三级城市联动
				strCity = (String) cityAdapter.getItemText(0);
				callDB(mapCity.get(strCity), arrCountrys, mapCountry);
				countryAdapter = new AddressTextAdapter(context, arrCountrys, 0, maxsize, minsize);
				wvCountry.setVisibleItems(5);
				wvCountry.setViewAdapter(countryAdapter);
				wvCountry.setCurrentItem(0);
				strCountry = (String) countryAdapter.getItemText(0);

			}
		});

		wvProvince.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, provinceAdapter);
			}
		});

		wvCitys.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
				strCity = currentText;
				setTextviewSize(currentText, cityAdapter);
				callDB(mapCity.get(strCity), arrCountrys, mapCountry);
				countryAdapter = new AddressTextAdapter(context, arrCountrys, 0, maxsize, minsize);
				wvCountry.setVisibleItems(5);
				wvCountry.setViewAdapter(countryAdapter);
				wvCountry.setCurrentItem(0);
				strCountry = (String) countryAdapter.getItemText(0);
			}
		});

		wvCitys.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = (String) cityAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, cityAdapter);
			}
		});

		wvCountry.addChangingListener(new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String currentText = (String) countryAdapter.getItemText(wheel.getCurrentItem());
				strCountry = currentText;
				setTextviewSize(currentText, countryAdapter);
			}
		});

		wvCountry.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				String currentText = (String) countryAdapter.getItemText(wheel.getCurrentItem());
				setTextviewSize(currentText, countryAdapter);
			}
		});
	}

	private class AddressTextAdapter extends AbstractWheelTextAdapter {
		ArrayList<String> list;

		protected AddressTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize,
				int minsize) {
			super(context, R.layout.item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
			this.list = list;
			setItemTextResource(R.id.tempValue);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return list.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return list.get(index) + "";
		}
	}

	/**
	 * 设置字体大小
	 * 
	 * @param curriteItemText
	 * @param adapter
	 */
	public void setTextviewSize(String curriteItemText, AddressTextAdapter adapter) {
		ArrayList<View> arrayList = adapter.getTestViews();
		int size = arrayList.size();
		String currentText;
		for (int i = 0; i < size; i++) {
			TextView textvew = (TextView) arrayList.get(i);
			currentText = textvew.getText().toString();
			if (curriteItemText.equals(currentText)) {
				textvew.setTextSize(24);
			} else {
				textvew.setTextSize(14);
			}
		}
	}

	public void setAddresskListener(OnAddressCListener onAddressCListener) {
		this.onAddressCListener = onAddressCListener;
	}

	@Override
	public void onClick(View v) {
		if (v == btnSure) {
			if (onAddressCListener != null) {
				strProvinceId = mapProvince.get(strProvince);
				strCityId = mapCity.get(strCity);
				strCountryId = mapCountry.get(strCountry);
				onAddressCListener.onClick(strProvince + " " + strProvinceId, strCity + " " + strCityId,
						strCountry + " " + strCountryId);
			}
		} else if (v == btnCancel) {

		} else if (v == lyChangeAddressChild) {
			return;
		} else {
			dismiss();
		}
		dismiss();
	}

	/**
	 * 回调接口
	 * 
	 * @author Administrator
	 * 
	 */
	public interface OnAddressCListener {
		public void onClick(String province, String city, String country);
	}

	/**
	 * 初始化参数
	 */
	private void init() {
		this.dbHelper = new DBManager(context, dbName, packageName, tableName, resourceId);
		this.dbHelper.openDatabase();
		this.selectionArgs = new String[1];
	}

	/**
	 * 从文件中读取地址数据
	 */
	// private void initJsonData() {
	// try {
	// StringBuffer sb = new StringBuffer();
	// InputStream is = context.getAssets().open("city.json");
	// int len = -1;
	// byte[] buf = new byte[1024];
	// while ((len = is.read(buf)) != -1) {
	// sb.append(new String(buf, 0, len, "gbk"));
	// }
	// is.close();
	// mJsonObj = new JSONObject(sb.toString());
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 解析数据
	 */
	// private void initDatas() {
	// try {
	// JSONArray jsonArray = mJsonObj.getJSONArray("citylist");
	// mProvinceDatas = new String[jsonArray.length()];
	// for (int i = 0; i < jsonArray.length(); i++) {
	// JSONObject jsonP = jsonArray.getJSONObject(i);
	// String province = jsonP.getString("p");
	//
	// mProvinceDatas[i] = province;
	//
	// JSONArray jsonCs = null;
	// try {
	// /**
	// * Throws JSONException if the mapping doesn't exist or is
	// * not a JSONArray.
	// */
	// jsonCs = jsonP.getJSONArray("c");
	// } catch (Exception e1) {
	// continue;
	// }
	// String[] mCitiesDatas = new String[jsonCs.length()];
	// for (int j = 0; j < jsonCs.length(); j++) {
	// JSONObject jsonCity = jsonCs.getJSONObject(j);
	// String city = jsonCity.getString("n");
	// mCitiesDatas[j] = city;
	// JSONArray jsonAreas = null;
	// try {
	// /**
	// * Throws JSONException if the mapping doesn't exist or
	// * is not a JSONArray.
	// */
	// jsonAreas = jsonCity.getJSONArray("a");
	// } catch (Exception e) {
	// continue;
	// }
	//
	// String[] mAreasDatas = new String[jsonAreas.length()];
	// for (int k = 0; k < jsonAreas.length(); k++) {
	// String area = jsonAreas.getJSONObject(k).getString("s");
	// mAreasDatas[k] = area;
	// }
	// }
	// mCitisDatasMap.put(province, mCitiesDatas);
	// }
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// mJsonObj = null;
	// }

	/**
	 * 初始化省会
	 */
	// public void initProvinces() {
	// int length = mProvinceDatas.length;
	// for (int i = 0; i < length; i++) {
	// arrProvinces.add(mProvinceDatas[i]);
	// }
	// }

	/**
	 * 根据省会，生成该省会的所有城市
	 * 
	 * @param citys
	 */
	// public void initCitys(String[] citys) {
	// if (citys != null) {
	// arrCitys.clear();
	// int length = citys.length;
	// for (int i = 0; i < length; i++) {
	// arrCitys.add(citys[i]);
	// }
	// } else {
	// String[] city = mCitisDatasMap.get("四川");
	// arrCitys.clear();
	// int length = city.length;
	// for (int i = 0; i < length; i++) {
	// arrCitys.add(city[i]);
	// }
	// }
	// if (arrCitys != null && arrCitys.size() > 0 &&
	// !arrCitys.contains(strCity)) {
	// strCity = arrCitys.get(0);
	// }
	// }

	/**
	 * 初始化地点
	 * 
	 * @param province
	 * @param city
	 */
	public void setAddress(String province, String city, String country) {
		if (province != null && province.length() > 0) {
			this.strProvince = province;
		}
		if (city != null && city.length() > 0) {
			this.strCity = city;
		}
		if (country != null && country.length() > 0) {
			this.strCountry = country;
		}
	}

	/**
	 * 返回省会索引，没有就返回默认“天津”
	 * 
	 * @param province
	 * @return
	 */
	public int getProvinceItem(String province) {
		int size = arrProvinces.size();
		int provinceIndex = 0;
		boolean noprovince = true;
		for (int i = 0; i < size; i++) {
			if (province.equals(arrProvinces.get(i))) {
				noprovince = false;
				return provinceIndex;
			} else {
				provinceIndex++;
			}
		}
		if (noprovince) {
			strProvince = "天津";
			return 22;
		}
		return provinceIndex;
	}

	/**
	 * 得到城市索引，没有返回默认“天津市”
	 * 
	 * @param city
	 * @return
	 */
	public int getCityItem(String city) {
		int size = arrCitys.size();
		int cityIndex = 0;
		boolean nocity = true;
		for (int i = 0; i < size; i++) {
			System.out.println(arrCitys.get(i));
			if (city.equals(arrCitys.get(i))) {
				nocity = false;
				return cityIndex;
			} else {
				cityIndex++;
			}
		}
		if (nocity) {
			strCity = "天津市";
			return 0;
		}
		return cityIndex;
	}

	/**
	 * 得到城市索引，没有返回默认“南开区”
	 * 
	 * @param country
	 * @return
	 */
	public int getCountryItem(String country) {
		int size = arrCountrys.size();
		int countryIndex = 0;
		boolean nocountry = true;
		for (int i = 0; i < size; i++) {
			System.out.println(arrCountrys.get(i));
			if (country.equals(arrCountrys.get(i))) {
				nocountry = false;
				return countryIndex;
			} else {
				countryIndex++;
			}
		}
		if (nocountry) {
			strCountry = "南开区";
			return 0;
		}
		return countryIndex;
	}

	/**
	 * 访问数据库
	 */
	private void callDB(String selectionArg, ArrayList<String> list, HashMap<String, String> map) {
		map.clear();
		list.clear();
		ArrayList<String> ids = new ArrayList<String>();
		this.selectionArgs[0] = selectionArg;
		Cursor cursor = dbHelper.select(this.dbHelper.getDatabase(), this.columns, this.selection, this.selectionArgs);
		int count = cursor.getCount();
		int nameIndex = cursor.getColumnIndex("region_name");
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String name = cursor.getString(nameIndex);
			list.add(name);
		}
		int idIndex = cursor.getColumnIndex("region_id");
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String id = cursor.getString(idIndex);
			ids.add(id);
		}

		for (int i = 0; i < count; i++) {
			map.put(list.get(i), ids.get(i));
		}

	}

}
