package com.spt.page;

import com.spt.sht.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MtsEditChangeActivity extends FragmentActivity {

    private TextView tv_title, tv_enter, tv_cancel;
    private EditText et_input;

    private Intent intent;
    private int position;
    private String flag;

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_change);
        super.onCreate(arg0);

        intent = getIntent();
        Bundle b = intent.getExtras();
        position = b.getInt("position");
        flag = b.getString("flag");

        initViews();

        tv_title.setText(flag.equals("price") ? "编辑分享价" : "编辑条形码");
        et_input.setHint(flag.equals("price") ? "请输入分享价" : "请输入条形码");

        tv_enter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (et_input.getText().toString().equals("")) {
                    Toast.makeText(MtsEditChangeActivity.this, "不能输入空值", Toast.LENGTH_LONG).show();
                } else {
                    if (flag.equals("price")) {
                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putDouble("value", Double.valueOf(et_input.getText().toString()));
                        b.putInt("position", position);
                        intent.putExtras(b);
                        setResult(0, intent);
                        finish();
                    } else {
                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putString("value", et_input.getText().toString());
                        b.putInt("position", position);
                        intent.putExtras(b);
                        setResult(0, intent);
                        finish();
                    }

                }
            }
        });

        tv_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(1);
                finish();
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(1);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initViews() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_enter = (TextView) findViewById(R.id.tv_enter);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        et_input = (EditText) findViewById(R.id.et_input);

    }

}
