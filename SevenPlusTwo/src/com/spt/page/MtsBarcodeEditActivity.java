package com.spt.page;

import com.spt.fragment.MtsManufacturersBarcodeFragment;
import com.spt.fragment.MtsMerchantBarcodeFragment;
import com.spt.sht.R;
import com.umeng.socialize.utils.Log;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MtsBarcodeEditActivity extends FragmentActivity {

    private ImageView iv_mts_back;
    private TextView tv_text_code;
    private RadioGroup rgp_chosen_barcode;
    private RadioButton rbtn_merchant, rbtn_manufacturers;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private MtsManufacturersBarcodeFragment fragmentMfr; // 厂家
    private MtsMerchantBarcodeFragment fragmentMer; // 商家

    private String detail = "", productId = "";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_barcode_edit);
        super.onCreate(savedInstanceState);

        intent = getIntent();
        detail = intent.getStringExtra("message");
        productId = intent.getStringExtra("proId");

        initView();

        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);

        rgp_chosen_barcode.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == rbtn_merchant.getId()) {
                    setTabSelection(0);
                    tv_text_code.setText("商家条码");
                    rbtn_merchant.setTextColor(0xffffffff);
                    rbtn_manufacturers.setTextColor(0xff319ce1);
                } else if (checkedId == rbtn_manufacturers.getId()) {
                    setTabSelection(1);
                    tv_text_code.setText("厂家条码");
                    rbtn_manufacturers.setTextColor(0xffffffff);
                    rbtn_merchant.setTextColor(0xff319ce1);
                }
            }
        });

        iv_mts_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(202);
                finish();
            }
        });
    }

    private void initView() {
        iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
        tv_text_code = (TextView) findViewById(R.id.tv_text_code);
        rgp_chosen_barcode = (RadioGroup) findViewById(R.id.rgp_chosen_barcode);
        rbtn_merchant = (RadioButton) findViewById(R.id.rbtn_merchant);
        rbtn_manufacturers = (RadioButton) findViewById(R.id.rbtn_manufacturers);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (fragmentMfr != null) {
            fragmentMfr = (MtsManufacturersBarcodeFragment) fragmentManager.findFragmentByTag("mfrF");
            fragmentMfr.onActivityResult(requestCode, resultCode, data);
        } else {

        }


        if (fragmentMer != null) {
            fragmentMer = (MtsMerchantBarcodeFragment) fragmentManager.findFragmentByTag("merF");
            fragmentMer.onActivityResult(requestCode, resultCode, data);
        } else {

        }

    }

    private void setTabSelection(int index) {
        fragmentTransaction = fragmentManager.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (index) {
            case 1:
                if (fragmentMfr == null) {
                    fragmentMfr = new MtsManufacturersBarcodeFragment();
                    Bundle bundleF = new Bundle();
                    bundleF.putString("message", detail);
                    bundleF.putString("proId", productId);
                    fragmentMfr.setArguments(bundleF);
                    fragmentTransaction.add(R.id.fl_frame, fragmentMfr, "mfrF");
                } else {
                    fragmentTransaction.show(fragmentMfr);
                }
                break;
            case 0:
                if (fragmentMer == null) {
                    fragmentMer = new MtsMerchantBarcodeFragment();
                    Bundle bundleE = new Bundle();
                    bundleE.putString("message", detail);
                    bundleE.putString("proId", productId);
                    fragmentMer.setArguments(bundleE);
                    fragmentTransaction.add(R.id.fl_frame, fragmentMer, "merF");
                } else {
                    fragmentTransaction.show(fragmentMer);
                }
                break;

        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fragmentMfr != null) {
            transaction.hide(fragmentMfr);
        }
        if (fragmentMer != null) {
            transaction.hide(fragmentMer);
        }
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(202);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
