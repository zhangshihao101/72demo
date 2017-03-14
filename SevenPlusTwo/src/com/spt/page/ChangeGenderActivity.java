package com.spt.page;

import com.spt.sht.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ChangeGenderActivity extends FragmentActivity {

    private ImageView iv_edit_back, iv_male, iv_female;
    private RelativeLayout rl_male, rl_female;
    private String gender = "";

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_gender);
        super.onCreate(arg0);

        initViews();

        Intent intent = getIntent();
        gender = intent.getStringExtra("gender");

        if (gender.equals("男")) {
            iv_male.setVisibility(View.VISIBLE);
            iv_female.setVisibility(View.INVISIBLE);
        } else if (gender.equals("女")) {
            iv_male.setVisibility(View.INVISIBLE);
            iv_female.setVisibility(View.VISIBLE);
        } else {

        }

        rl_male.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                iv_male.setVisibility(View.VISIBLE);
                iv_female.setVisibility(View.INVISIBLE);
                Intent intentR = new Intent();
                intentR.putExtra("gender", "male");
                setResult(1, intentR);
                finish();
            }
        });

        rl_female.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                iv_male.setVisibility(View.INVISIBLE);
                iv_female.setVisibility(View.VISIBLE);
                Intent intentR = new Intent();
                intentR.putExtra("gender", "female");
                setResult(1, intentR);
                finish();

            }
        });

        iv_edit_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });
    }

    private void initViews() {
        iv_edit_back = (ImageView) findViewById(R.id.iv_edit_back);
        iv_male = (ImageView) findViewById(R.id.iv_male);
        iv_female = (ImageView) findViewById(R.id.iv_female);
        rl_male = (RelativeLayout) findViewById(R.id.rl_male);
        rl_female = (RelativeLayout) findViewById(R.id.rl_female);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(0);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
