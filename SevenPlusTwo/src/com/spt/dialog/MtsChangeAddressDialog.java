package com.spt.dialog;

import java.util.ArrayList;
import java.util.HashMap;

import com.spt.sht.R;
import com.spt.utils.DBManager;
import com.spt.wheel.AbstractWheelTextAdapter;
import com.spt.wheel.OnWheelChangedListener;
import com.spt.wheel.OnWheelScrollListener;
import com.spt.wheel.WheelView;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MtsChangeAddressDialog extends Dialog implements OnClickListener {

    private Context context;
    private WheelView wvProvince;
    private WheelView wvCitys;
    private View lyChangeAddress;
    private View lyChangeAddressChild;
    private TextView btnSure;
    private TextView btnCancel;

    private ArrayList<String> arrProvinces = new ArrayList<String>();
    private ArrayList<String> arrCitys = new ArrayList<String>();
    private AddressTextAdapter provinceAdapter;
    private AddressTextAdapter cityAdapter;

    private String strProvince = "";
    private String strCity = "";
    private String strProvinceId;
    private String strCityId;
    private OnAddressCListener onAddressCListener;

    private int maxsize = 24;
    private int minsize = 14;

    // {"城市名", "城市id"}的形式，方便点选城市的时候找到对应的id
    private HashMap<String, String> mapProvince = new HashMap<String, String>();
    private HashMap<String, String> mapCity = new HashMap<String, String>();
    private String[] selectionArgs;
    private DBManager dbHelper;
    private String dbName = "city.db"; // 保存的数据库文件名
    private String packageName = "com.spt.sht"; // 包名
    private String tableName = "lym_region"; // 待查询的表名
    private int resourceId = R.raw.city; // 待查询的表名
    private String[] columns = new String[] {"region_id", "region_name"};
    private String selection = " parent_id = ?";

    public MtsChangeAddressDialog(Context context) {
        super(context, R.style.ShareDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mts_changeaddress);

        wvProvince = (WheelView) findViewById(R.id.wv_address_province);
        wvCitys = (WheelView) findViewById(R.id.wv_address_city);
        lyChangeAddress = findViewById(R.id.ly_myinfo_changeaddress);
        lyChangeAddressChild = findViewById(R.id.ly_myinfo_changeaddress_child);
        btnSure = (TextView) findViewById(R.id.btn_myinfo_sure);
        btnCancel = (TextView) findViewById(R.id.btn_myinfo_cancel);

        lyChangeAddress.setOnClickListener(this);
        lyChangeAddressChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        init();
        callDB("CHN", arrProvinces, mapProvince);
        provinceAdapter = new AddressTextAdapter(context, arrProvinces, getProvinceItem(strProvince), maxsize, minsize);
        wvProvince.setVisibleItems(5);
        wvProvince.setViewAdapter(provinceAdapter);
        wvProvince.setCurrentItem(getProvinceItem(strProvince));

        callDB("TIANJIN", arrCitys, mapCity);
        cityAdapter = new AddressTextAdapter(context, arrCitys, getCityItem(strCity), maxsize, minsize);
        wvCitys.setVisibleItems(5);
        wvCitys.setViewAdapter(cityAdapter);
        wvCitys.setCurrentItem(getCityItem(strCity));
        
        wvProvince.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) provinceAdapter.getItemText(wheel.getCurrentItem());
                strProvince = currentText;
                setTextviewSize(currentText, provinceAdapter);
                callDB(mapProvince.get(strProvince), arrCitys, mapCity);
                strCity = arrCitys.get(0);
                cityAdapter = new AddressTextAdapter(context, arrCitys, 0, maxsize, minsize);
                wvCitys.setVisibleItems(5);
                wvCitys.setViewAdapter(cityAdapter);
                wvCitys.setCurrentItem(0);

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
        
    }

    @Override
    public void onClick(View v) {
        if (v == btnSure) {
            if (onAddressCListener != null) {
                strProvinceId = mapProvince.get(strProvince);
                strCityId = mapCity.get(strCity);
                onAddressCListener.onClick(strProvince + " " + strProvinceId, strCity + " " + strCityId);
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
     * 初始化参数
     */
    private void init() {
        this.dbHelper = new DBManager(context, dbName, packageName, tableName, resourceId);
        this.dbHelper.openDatabase();
        this.selectionArgs = new String[1];
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

    /**
     * 回调接口
     * 
     * @author Administrator
     * 
     */
    public interface OnAddressCListener {
        public void onClick(String province, String city);
    }
    
    /**
     * 初始化地点
     * 
     * @param province
     * @param city
     */
    public void setAddress(String province, String city) {
        if (province != null && province.length() > 0) {
            this.strProvince = province;
        }
        if (city != null && city.length() > 0) {
            this.strCity = city;
        }
    }

    /**
     * 返回省份索引，没有就返回默认“天津”
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
            strProvince = "天津市";
            return 1;
        }
        return provinceIndex;
    }

    /**
     * 得到城市索引，没有返回默认“南开区”
     * 
     * @param city
     * @return
     */
    public int getCityItem(String city) {
        int size = arrCitys.size();
        int cityIndex = 0;
        boolean nocity = true;
        for (int i = 0; i < size; i++) {
            if (city.equals(arrCitys.get(i))) {
                nocity = false;
                return cityIndex;
            } else {
                cityIndex++;
            }
        }
        if (nocity) {
            strCity = "南开区";
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
    // public int getCountryItem(String country) {
    // int size = arrCountrys.size();
    // int countryIndex = 0;
    // boolean nocountry = true;
    // for (int i = 0; i < size; i++) {
    // System.out.println(arrCountrys.get(i));
    // if (country.equals(arrCountrys.get(i))) {
    // nocountry = false;
    // return countryIndex;
    // } else {
    // countryIndex++;
    // }
    // }
    // if (nocountry) {
    // strCountry = "南开区";
    // return 0;
    // }
    // return countryIndex;
    // }

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
