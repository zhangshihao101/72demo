package com.spt.page;

import java.util.ArrayList;
import java.util.List;

import com.spt.sht.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class ChosenRoleActivity extends FragmentActivity {

    private ImageView iv_chosen_back;
    private ListView lv_rode;
    private List<String> rodeData;


    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chosen_role);
        super.onCreate(arg0);

        iv_chosen_back = (ImageView) findViewById(R.id.iv_chosen_back);
        lv_rode = (ListView) findViewById(R.id.lv_rode);
        rodeData = new ArrayList<String>();
        rodeData.add("供应商");
        rodeData.add("零售商");
        rodeData.add("零售商供应商");

        lv_rode.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_spinner_item, rodeData));
        lv_rode.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("rode", rodeData.get(position));
                setResult(1000, intent);
                finish();
            }
        });

        iv_chosen_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(1001);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            setResult(1001);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


}
