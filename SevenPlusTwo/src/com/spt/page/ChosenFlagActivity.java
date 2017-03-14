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

public class ChosenFlagActivity extends FragmentActivity implements OnClickListener {

    private ImageView iv_edit_back, iv_leader, iv_club, iv_massorganizations, iv_webshopowner, iv_storeowner, iv_other;
    private RelativeLayout rl_leader, rl_club, rl_massorganizations, rl_webshopowner, rl_storeowner, rl_other;
    private String flag;

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chosen_flag);
        super.onCreate(arg0);

        initViews();

        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");

        if (flag.equals("领队")) {
            iv_leader.setVisibility(View.VISIBLE);
            iv_club.setVisibility(View.INVISIBLE);
            iv_massorganizations.setVisibility(View.INVISIBLE);
            iv_webshopowner.setVisibility(View.INVISIBLE);
            iv_storeowner.setVisibility(View.INVISIBLE);
            iv_other.setVisibility(View.INVISIBLE);
        } else if (flag.equals("俱乐部")) {
            iv_leader.setVisibility(View.INVISIBLE);
            iv_club.setVisibility(View.VISIBLE);
            iv_massorganizations.setVisibility(View.INVISIBLE);
            iv_webshopowner.setVisibility(View.INVISIBLE);
            iv_storeowner.setVisibility(View.INVISIBLE);
            iv_other.setVisibility(View.INVISIBLE);
        } else if (flag.equals("社团")) {
            iv_leader.setVisibility(View.INVISIBLE);
            iv_club.setVisibility(View.INVISIBLE);
            iv_massorganizations.setVisibility(View.VISIBLE);
            iv_webshopowner.setVisibility(View.INVISIBLE);
            iv_storeowner.setVisibility(View.INVISIBLE);
            iv_other.setVisibility(View.INVISIBLE);
        } else if (flag.equals("网店店主")) {
            iv_leader.setVisibility(View.INVISIBLE);
            iv_club.setVisibility(View.INVISIBLE);
            iv_massorganizations.setVisibility(View.INVISIBLE);
            iv_webshopowner.setVisibility(View.VISIBLE);
            iv_storeowner.setVisibility(View.INVISIBLE);
            iv_other.setVisibility(View.INVISIBLE);
        } else if (flag.equals("实体店店主")) {
            iv_leader.setVisibility(View.INVISIBLE);
            iv_club.setVisibility(View.INVISIBLE);
            iv_massorganizations.setVisibility(View.INVISIBLE);
            iv_webshopowner.setVisibility(View.INVISIBLE);
            iv_storeowner.setVisibility(View.VISIBLE);
            iv_other.setVisibility(View.INVISIBLE);
        } else if (flag.equals("其他")) {
            iv_leader.setVisibility(View.INVISIBLE);
            iv_club.setVisibility(View.INVISIBLE);
            iv_massorganizations.setVisibility(View.INVISIBLE);
            iv_webshopowner.setVisibility(View.INVISIBLE);
            iv_storeowner.setVisibility(View.INVISIBLE);
            iv_other.setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        iv_edit_back = (ImageView) findViewById(R.id.iv_edit_back);
        iv_leader = (ImageView) findViewById(R.id.iv_leader);
        iv_club = (ImageView) findViewById(R.id.iv_club);
        iv_massorganizations = (ImageView) findViewById(R.id.iv_massorganizations);
        iv_webshopowner = (ImageView) findViewById(R.id.iv_webshopowner);
        iv_storeowner = (ImageView) findViewById(R.id.iv_storeowner);
        iv_other = (ImageView) findViewById(R.id.iv_other);
        rl_leader = (RelativeLayout) findViewById(R.id.rl_leader);
        rl_club = (RelativeLayout) findViewById(R.id.rl_club);
        rl_massorganizations = (RelativeLayout) findViewById(R.id.rl_massorganizations);
        rl_webshopowner = (RelativeLayout) findViewById(R.id.rl_webshopowner);
        rl_storeowner = (RelativeLayout) findViewById(R.id.rl_storeowner);
        rl_other = (RelativeLayout) findViewById(R.id.rl_other);

        iv_edit_back.setOnClickListener(this);
        rl_leader.setOnClickListener(this);
        rl_club.setOnClickListener(this);
        rl_massorganizations.setOnClickListener(this);
        rl_webshopowner.setOnClickListener(this);
        rl_storeowner.setOnClickListener(this);
        rl_other.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit_back:
                setResult(0);
                finish();
                break;
            case R.id.rl_leader:
                iv_leader.setVisibility(View.VISIBLE);
                resultData("leader");
                break;
            case R.id.rl_club:
                iv_club.setVisibility(View.VISIBLE);
                resultData("club");
                break;
            case R.id.rl_massorganizations:
                iv_massorganizations.setVisibility(View.VISIBLE);
                resultData("mass");
                break;
            case R.id.rl_webshopowner:
                iv_webshopowner.setVisibility(View.VISIBLE);
                resultData("web");
                break;
            case R.id.rl_storeowner:
                iv_storeowner.setVisibility(View.VISIBLE);
                resultData("store");
                break;
            case R.id.rl_other:
                iv_other.setVisibility(View.VISIBLE);
                resultData("other");
                break;

            default:
                break;
        }

    }

    private void resultData(String f) {
        Intent intent = new Intent();
        intent.putExtra("flag", f);
        setResult(1, intent);
        finish();
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
