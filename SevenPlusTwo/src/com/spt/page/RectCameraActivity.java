package com.spt.page;

import java.io.File;

import com.spt.controler.CameraHelper;
import com.spt.controler.MaskSurfaceView;
import com.spt.controler.OnCaptureCallback;
import com.spt.sht.R;
import com.spt.utils.DisplayUtil;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class RectCameraActivity extends FragmentActivity implements OnCaptureCallback {

    private MaskSurfaceView surfaceview;
    // 拍照
    private Button btn_capture;
    // 重拍
    private Button btn_recapture;
    // 取消
    private Button btn_cancel;
    // 确认
    private Button btn_ok;

    // 拍照后得到的保存的文件路径
    private String filepath;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.activity_rect_camera);

        surfaceview = (MaskSurfaceView) findViewById(R.id.surface_view);
        btn_capture = (Button) findViewById(R.id.btn_capture);
        btn_recapture = (Button) findViewById(R.id.btn_recapture);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        // 设置矩形区域大小
        surfaceview.setMaskSize(DisplayUtil.dip2px(this, 480), DisplayUtil.dip2px(this, 270));

        // 拍照
        btn_capture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                btn_capture.setEnabled(false);
//                btn_ok.setEnabled(true);
//                btn_recapture.setEnabled(true);
                CameraHelper.getInstance().tackPicture(RectCameraActivity.this);

            }
        });

        // 重拍
        btn_recapture.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                btn_capture.setEnabled(true);
//                btn_ok.setEnabled(false);
//                btn_recapture.setEnabled(false);
                surfaceview.setVisibility(View.VISIBLE);
                deleteFile();
                CameraHelper.getInstance().startPreview();
            }
        });

        // 确认
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

        // 取消
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                deleteFile();
                RectCameraActivity.this.finish();
            }
        });
    }

    /**
     * 删除图片文件呢
     */
    private void deleteFile() {
        if (this.filepath == null || this.filepath.equals("")) {
            return;
        }
        File f = new File(this.filepath);
        if (f.exists()) {
            f.delete();
        }
    }

    @Override
    public void onCapture(boolean success, String filePath) {
        this.filepath = filePath;
        String message = "拍照成功";
        if (!success) {
            message = "拍照失败";
            CameraHelper.getInstance().startPreview();
            // this.imageView.setVisibility(View.GONE);
            this.surfaceview.setVisibility(View.VISIBLE);
        } else {
            // this.imageView.setVisibility(View.VISIBLE);
            this.surfaceview.setVisibility(View.GONE);
            // this.imageView.setImageBitmap(BitmapFactory.decodeFile(filepath));
            Intent intent = new Intent();
            intent.putExtra("path", filepath);
            setResult(1000, intent);
            finish();
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

    }

}
