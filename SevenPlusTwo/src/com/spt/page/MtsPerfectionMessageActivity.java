package com.spt.page;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.TagAdapter;
import com.spt.controler.FlowTagLayout;
import com.spt.dialog.MtsChangeAddressDialog;
import com.spt.dialog.MtsChangeAddressDialog.OnAddressCListener;
import com.spt.interfac.OnTagClickListener;
import com.spt.interfac.OnTagSelectListener;
import com.spt.sht.R;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class MtsPerfectionMessageActivity extends FragmentActivity {

    private ImageView iv_mts_back, iv_clear_company_name, iv_clear_address, iv_clear_name, iv_clear_email;
    private Spinner sp_profession, sp_style;
    private EditText et_company_name, et_address, et_city, et_district, et_name, et_email, et_target_other;
    private RelativeLayout rl_city;
    private FlowTagLayout fl_target;
    private TagAdapter adapterTarget;
    private CheckBox cb_agree;
    private Button btn_commit;
    private List<Object> targetData;

    private List<String> professionList, styleList;
    private ArrayAdapter<String> profession_adapter, style_adapter;
    private HashMap<String, String> professionMap, styleMap, targetMap;

    private Boolean isSelected = false;
    private String profession = "", style = "", provinceId = "", cityId = "", target = "";

    private SharedPreferences sp;
    private Editor editor;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mts_perfection_message);
        super.onCreate(arg0);

        initViews();

        iv_mts_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_company_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!"".equals(et_company_name.getText().toString())) {
                    iv_clear_company_name.setVisibility(View.VISIBLE);
                } else {
                    iv_clear_company_name.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_clear_company_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                et_company_name.setText("");
            }
        });

        et_address.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!et_address.getText().toString().equals("")) {
                    iv_clear_address.setVisibility(View.VISIBLE);
                } else {
                    iv_clear_address.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_clear_address.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                et_address.setText("");
            }
        });

        et_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!et_name.getText().toString().equals("")) {
                    iv_clear_name.setVisibility(View.VISIBLE);
                } else {
                    iv_clear_name.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_clear_name.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                et_name.setText("");
            }
        });

        et_email.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!et_email.getText().toString().equals("")) {
                    iv_clear_email.setVisibility(View.VISIBLE);
                } else {
                    iv_clear_email.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iv_clear_email.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                et_email.setText("");
            }
        });

        cb_agree.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSelected = true;
                } else {
                    isSelected = false;
                }
            }
        });

        rl_city.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MtsChangeAddressDialog mChangeAddressDialog =
                        new MtsChangeAddressDialog(MtsPerfectionMessageActivity.this);
                mChangeAddressDialog.setAddress("天津", "南开区");
                mChangeAddressDialog.show();
                mChangeAddressDialog.setAddresskListener(new OnAddressCListener() {

                    @Override
                    public void onClick(String province, String city) {
                        String provinces[] = province.split(" ");
                        String citys[] = city.split(" ");
                        // ext_region_id_1 = provinces[1];
                        // ext_region_id_2 = citys[1];
                        // ext_region_id_3 = countrys[1];
                        et_city.setText(provinces[0] + " " + citys[0]);
                        provinceId = provinces[1];
                        cityId = citys[1];
                        String id = provinces[1] + " " + citys[1];
                        System.out.println("======省市id=====" + id + "=====");

                    }
                });
                // mChangeAddressDialog.setAddresskListener(new OnAddressCListener() {
                //
                // @Override
                // public void onClick(String province, String city) {
                // String provinces[] = province.split(" ");
                // String citys[] = city.split(" ");
                // String countrys[] = country.split(" ");
                // // ext_region_id_1 = provinces[1];
                // // ext_region_id_2 = citys[1];
                // // ext_region_id_3 = countrys[1];
                // et_city.setText(provinces[0] + " " + citys[0] + " " + countrys[0]);
                // String id = provinces[1] + " " + citys[1] + " " + countrys[1];
                // System.out.println("======省市id=====" + id + "=====");
                // }
                // });
            }
        });

        et_city.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MtsChangeAddressDialog mChangeAddressDialog =
                        new MtsChangeAddressDialog(MtsPerfectionMessageActivity.this);
                mChangeAddressDialog.setAddress("天津", "南开区");
                mChangeAddressDialog.show();
                mChangeAddressDialog.setAddresskListener(new OnAddressCListener() {

                    @Override
                    public void onClick(String province, String city) {
                        String provinces[] = province.split(" ");
                        String citys[] = city.split(" ");
                        // ext_region_id_1 = provinces[1];
                        // ext_region_id_2 = citys[1];
                        // ext_region_id_3 = countrys[1];
                        et_city.setText(provinces[0] + " " + citys[0]);
                        provinceId = provinces[1];
                        cityId = citys[1];
                        String id = provinces[1] + " " + citys[1];
                        System.out.println("======省市id=====" + id + "=====");

                    }
                });

            }
        });

        sp_profession.setSelection(0);
        sp_profession.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                profession = professionMap.get(professionList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        sp_profession.setSelection(0);
        sp_style.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                style = styleMap.get(styleList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });

        fl_target.setOnTagSelectListener(new OnTagSelectListener() {

            @Override
            public void onItemSelect(FlowTagLayout parent, List<Integer> selectedList) {
                for (Integer integer : selectedList) {
                    if (targetData.get(integer).equals("市场活动")) {
                        et_target_other.setVisibility(View.GONE);
                    } else if (targetData.get(integer).equals("销售人员拜访")) {
                        et_target_other.setVisibility(View.GONE);
                    } else if (targetData.get(integer).equals("网站免费试用")) {
                        et_target_other.setVisibility(View.GONE);
                    } else if (targetData.get(integer).equals("其他")) {
                        et_target_other.setVisibility(View.VISIBLE);
                    } else if (!targetData.get(integer).equals("其他")) {
                        et_target_other.setVisibility(View.GONE);
                    }
                    target = targetMap.get(targetData.get(integer));
                }
            }
        });

        btn_commit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (et_company_name.getText().toString().equals("")) {
                    Toast.makeText(MtsPerfectionMessageActivity.this, "请填写企业名称", Toast.LENGTH_LONG).show();
                } else if (et_address.getText().toString().equals("")) {
                    Toast.makeText(MtsPerfectionMessageActivity.this, "请填写企业地址", Toast.LENGTH_LONG).show();
                } else if (et_city.getText().toString().equals("")) {
                    Toast.makeText(MtsPerfectionMessageActivity.this, "请选择企业地区", Toast.LENGTH_LONG).show();
                } else if (profession.equals("")) {
                    Toast.makeText(MtsPerfectionMessageActivity.this, "请选择行业", Toast.LENGTH_LONG).show();
                } else if (et_name.getText().toString().equals("")) {
                    Toast.makeText(MtsPerfectionMessageActivity.this, "请填写申请人姓名", Toast.LENGTH_LONG).show();
                } else if (style.equals("")) {
                    Toast.makeText(MtsPerfectionMessageActivity.this, "请选择业务类型", Toast.LENGTH_LONG).show();
                } else if (target.equals("")) {
                    Toast.makeText(MtsPerfectionMessageActivity.this, "请选择商机来源", Toast.LENGTH_LONG).show();
                } else if (!isSelected) {
                    Toast.makeText(MtsPerfectionMessageActivity.this, "请同意《源一云商软件使用协议》", Toast.LENGTH_LONG).show();
                } else {
                    consummateInfo();
                }
            }

        });

    }

    private void consummateInfo() {
        dialog.show();
        OkHttpManager.client
                .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.consummate_infor)
                        .post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
                                .add("password", sp.getString("userPsw", ""))
                                .add("passwordVerify", sp.getString("userPsw", ""))
                                .add("USER_WORK_CONTACT", sp.getString("phoneNo", "")).add("USER_COUNTRY", "CHN")
                                .add("groupName", et_company_name.getText().toString()).add("industryId", profession)
                                .add("USER_ADDRESS1", et_address.getText().toString()).add("USER_STATE", provinceId)
                                .add("USER_CITY", cityId).add("userName", et_name.getText().toString())
                                .add("USER_EMAIL", et_email.getText().toString()).add("businessId", style)
                                .add("businessOpportunityId", target)
                                .add("description", et_target_other.getText().toString()).build())
                        .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        dialog.dismiss();
                        final String jsonStr = response.body().string();
                        System.out.println("=======完善资料====" + jsonStr);
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject jsonobject = new JSONObject(jsonStr);
                                    String error = jsonobject.optString("_ERROR_MESSAGE_");
                                    if (error.equals("")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "申请成功", Toast.LENGTH_LONG)
                                                .show();
                                        editor.putString("partyId", jsonobject.optString("partyId"));
                                        editor.commit();
                                        Intent intent =
                                                new Intent(MtsPerfectionMessageActivity.this, MyCompanyActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (error.equals("100")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "用户Id缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("101")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "国家信息缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("102")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "电话号码缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("103")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "密码或确认密码缺失",
                                                Toast.LENGTH_LONG).show();
                                    } else if (error.equals("104")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "密码和确认密码不一致",
                                                Toast.LENGTH_LONG).show();
                                    } else if (error.equals("105")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "公司名缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("106")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "行业Id缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("107")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "公司地址缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("108")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "公司省份缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("109")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "公司城市缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("110")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "姓名缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("111")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "业务类型缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("112")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "邮件缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("113")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "商机来源缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("114")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "其他商机来源缺失", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("115")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "注册失败", Toast.LENGTH_LONG)
                                                .show();
                                    } else if (error.equals("116")) {
                                        Toast.makeText(MtsPerfectionMessageActivity.this, "用户Id重复", Toast.LENGTH_LONG)
                                                .show();
                                    }


                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
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
                                Toast.makeText(MtsPerfectionMessageActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
    }

    private void initViews() {

        iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
        iv_clear_company_name = (ImageView) findViewById(R.id.iv_clear_company_name);
        iv_clear_address = (ImageView) findViewById(R.id.iv_clear_address);
        iv_clear_name = (ImageView) findViewById(R.id.iv_clear_name);
        iv_clear_email = (ImageView) findViewById(R.id.iv_clear_email);
        sp_profession = (Spinner) findViewById(R.id.sp_profession);
        sp_style = (Spinner) findViewById(R.id.sp_style);
        et_company_name = (EditText) findViewById(R.id.et_company_name);
        et_address = (EditText) findViewById(R.id.et_address);
        et_city = (EditText) findViewById(R.id.et_city);
        et_district = (EditText) findViewById(R.id.et_district);
        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_target_other = (EditText) findViewById(R.id.et_target_other);
        rl_city = (RelativeLayout) findViewById(R.id.rl_city);
        fl_target = (FlowTagLayout) findViewById(R.id.fl_target);
        cb_agree = (CheckBox) findViewById(R.id.cb_agree);
        btn_commit = (Button) findViewById(R.id.btn_commit);

        dialog = ProgressDialog.show(this, "请稍候。。。", "获取数据中。。。", true);
        dialog.dismiss();

        sp = MtsPerfectionMessageActivity.this.getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        editor = sp.edit();

        adapterTarget = new TagAdapter(MtsPerfectionMessageActivity.this);
        fl_target.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_SINGLE);
        fl_target.setAdapter(adapterTarget);

        targetData = new ArrayList<Object>();
        targetData.add("市场活动");
        targetData.add("销售人员拜访");
        targetData.add("网站免费试用");
        targetData.add("其他");
        adapterTarget.onlyAddAll(targetData);
        targetMap = new HashMap<String, String>();
        targetMap.put("市场活动", "MarketActivities");
        targetMap.put("销售人员拜访", "SalerVisit");
        targetMap.put("网站免费试用", " FreeTrial");
        targetMap.put("其他", "Other");

        professionList = new ArrayList<String>();
        professionList.add("户外装备");
        professionList.add("体育用品");
        professionMap = new HashMap<String, String>();
        professionMap.put("户外装备", "OUT_EQUIPMENT");
        professionMap.put("体育用品", "SPORTS_EQUIPMENT");
        profession_adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, professionList);
        profession_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_profession.setAdapter(profession_adapter);

        styleList = new ArrayList<String>();
        styleList.add("零售商版");
        styleList.add("品牌商版");
        styleMap = new HashMap<String, String>();
        styleMap.put("零售商版", "0");
        styleMap.put("品牌商版", "1");
        style_adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, styleList);
        style_adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        sp_style.setAdapter(style_adapter);


    }

}
