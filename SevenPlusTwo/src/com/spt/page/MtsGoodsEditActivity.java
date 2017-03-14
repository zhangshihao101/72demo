package com.spt.page;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.MtsGoodsEditDetailPicAdapter;
import com.spt.adapter.MtsGoodsEditPicAdapter;
import com.spt.bean.VariantsInfo;
import com.spt.controler.SelectPicPopupWindow;
import com.spt.sht.R;
import com.spt.utils.ImageUtils;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MtsGoodsEditActivity extends FragmentActivity {

    private ImageView iv_mts_back, iv_first_pic;
    private EditText et_product_name, et_product_brand, et_product_style, et_product_price, et_product_description;
    private RelativeLayout rl_barcode, rl_share_price, rl_first_pic;
    private GridView gv_sku_pic, gv_detail_pic;
    private TextView tv_submit;

    private ProgressDialog progressDialog;
    private String productId = "", mColorItem = "", mColorItemId = "", firstPic = "", mColorDetail = "",
            mColorDetailId = "", mProductIdDetail = "", firstPicId = "", updateId = "", updateBrandId = "",
            privateCategoryId = "", publicCategoryId = "";

    private int mPositionItem = -1, mPositionDetail = -1;

    private List<VariantsInfo> mItemList;
    private MtsGoodsEditPicAdapter adapterItem;

    private LinkedList<VariantsInfo> mDetailList;
    private MtsGoodsEditDetailPicAdapter adapterDetail;

    private Bundle b;

    /**
     * 自定义的PopupWindow
     */
    private SelectPicPopupWindow menuWindow;

    /**
     * 选择图片的返回码
     */
    public final static int SELECT_IMAGE_RESULT_CODE_F = 200; // 首图修改

    public final static int SELECT_IMAGE_RESULT_CODE_S = 201; // 单品图修改

    public final static int SELECT_IMAGE_RESULT_CODE_P = 202; // 详情图修改

    /**
     * 当前选择的图片的路径
     */
    public String mImagePath;

    public String detail = "", price = "";

    public File file;

    public Bitmap photoBmp = null;

    public Uri imageFileUri = null;

    @Override
    protected void onCreate(Bundle arg0) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mts_goods_edit);
        super.onCreate(arg0);

        b = this.getIntent().getExtras();
        productId = b.getString("productId");

        initView();

        getProductDetail();

        gv_sku_pic.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mItemList.get(position).getSkuImage()
                        .equals("http://metasolo.cn/homepage/images/product-no-image.jpg")) {

                    showPicturePopupWindow("S");

                    mPositionItem = position;
                    mColorItem = mItemList.get(position).getSkuColor();
                    mColorItemId = mItemList.get(position).getSkuColorId();
                } else {
                    Toast.makeText(MtsGoodsEditActivity.this, "该商品没有单品，无法上传对应的图片", Toast.LENGTH_LONG).show();
                }

            }
        });

        gv_detail_pic.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // if (mDetailList.get(position).getSkuImage().equals("null")) {

                showPicturePopupWindow("P");

                mPositionDetail = position;
                // mColorDetail = mDetailList.get(position).getSkuColor();
                mColorDetailId = mDetailList.get(position).getSkuColorId();
                mProductIdDetail = mDetailList.get(position).getProductId();
                // }

            }

        });

        rl_barcode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MtsGoodsEditActivity.this, MtsBarcodeEditActivity.class);
                intent.putExtra("message", detail);
                intent.putExtra("proId", productId);
                startActivityForResult(intent, 200);
            }
        });

        rl_share_price.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MtsGoodsEditActivity.this, MtsSharepriceEditActivity.class);
                intent.putExtra("price", price);
                intent.putExtra("proId", productId);
                startActivityForResult(intent, 100);
            }
        });

        tv_submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (et_product_name.getText().toString().trim().equals("")) {
                    Toast.makeText(MtsGoodsEditActivity.this, "商品名不能为空" + "\n" + "请输入商品名", Toast.LENGTH_LONG).show();
                } else if (et_product_style.getText().toString().trim().equals("")) {
                    Toast.makeText(MtsGoodsEditActivity.this, "款号不能为空" + "\n" + "请输入款号", Toast.LENGTH_LONG).show();
                } else if (et_product_price.getText().toString().trim().equals("")) {
                    Toast.makeText(MtsGoodsEditActivity.this, "吊牌价不能为空" + "\n" + "请输入吊牌价", Toast.LENGTH_LONG).show();
                } else {
                    updateProduct();
                }

            }
        });

        iv_mts_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(302);
                finish();
            }
        });

    }

    private void initView() {
        iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
        iv_first_pic = (ImageView) findViewById(R.id.iv_first_pic);
        et_product_name = (EditText) findViewById(R.id.et_product_name);
        et_product_brand = (EditText) findViewById(R.id.et_product_brand);
        et_product_style = (EditText) findViewById(R.id.et_product_style);
        et_product_price = (EditText) findViewById(R.id.et_product_price);
        et_product_description = (EditText) findViewById(R.id.et_product_description);
        rl_barcode = (RelativeLayout) findViewById(R.id.rl_barcode);
        rl_share_price = (RelativeLayout) findViewById(R.id.rl_share_price);
        rl_first_pic = (RelativeLayout) findViewById(R.id.rl_first_pic);
        gv_sku_pic = (GridView) findViewById(R.id.gv_sku_pic);
        gv_detail_pic = (GridView) findViewById(R.id.gv_detail_pic);
        tv_submit = (TextView) findViewById(R.id.tv_submit);

        progressDialog = ProgressDialog.show(MtsGoodsEditActivity.this, "请稍候。。。", "获取数据中。。。", true);
        progressDialog.dismiss();

        mItemList = new ArrayList<VariantsInfo>();
        adapterItem = new MtsGoodsEditPicAdapter(MtsGoodsEditActivity.this, mItemList);

        mDetailList = new LinkedList<VariantsInfo>();
        VariantsInfo info = new VariantsInfo();
        info.setSkuImage("");
        mDetailList.addLast(info);
        adapterDetail = new MtsGoodsEditDetailPicAdapter(MtsGoodsEditActivity.this, mDetailList);

    }

    private void getProductDetail() {
        progressDialog.show();
        OkHttpManager.client
                .newCall(
                        new Request.Builder()
                                .url(MtsUrls.base
                                        + MtsUrls.get_product)
                                .post(new FormBody.Builder()
                                        .add("externalLoginKey",
                                                Localxml.search(MtsGoodsEditActivity.this, "externalloginkey"))
                                        .add("productId", productId).build())
                                .build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("=======" + "商品详情" + "========" + jsonStr + "=============");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                try {
                                    JSONObject object = new JSONObject(jsonStr);

                                    JSONArray arraySKU = object.optJSONArray("skuImages");

                                    if (arraySKU == null) {
                                        VariantsInfo item = new VariantsInfo();
                                        item.setSkuImage("http://metasolo.cn/homepage/images/product-no-image.jpg");
                                        item.setSkuColorId("");
                                        item.setIsLocal(false);
                                        mItemList.add(item);
                                        gv_sku_pic.setAdapter(adapterItem);

                                    } else if (!arraySKU.equals("null")) {
                                        for (int i = 0; i < arraySKU.length(); i++) {
                                            JSONObject objSKU = (JSONObject) arraySKU.get(i);
                                            VariantsInfo item = new VariantsInfo();

                                            item.setSkuColor(objSKU.optString("colorDesc"));
                                            item.setSkuImage(objSKU.optString("imageUrl"));
                                            item.setSkuColorId(objSKU.optString("colorId"));
                                            item.setIsLocal(false);

                                            mItemList.add(item);
                                        }

                                        gv_sku_pic.setAdapter(adapterItem);
                                    }

                                    JSONArray arrayDetail = object.optJSONArray("detailImages");
                                    JSONArray arrayDetails = object.optJSONArray("variants");
                                    detail = arrayDetails.toString();

                                    if (arrayDetail == null) {

                                        // for (int i = 0; i < arrayDetails.length(); i++) {
                                        // JSONObject objDetail = (JSONObject) arrayDetails.get(i);
                                        // VariantsInfo item = new VariantsInfo();
                                        //
                                        // // item.setSkuColor(objDetail.optString("productId"));
                                        // // item.setSkuColorId(objDetail.optString("colorId"));
                                        // item.setSkuImage(objDetail.optString("skuImageUrl"));
                                        // item.setIsLocal(false);
                                        // item.setProductId(objDetail.optString("productId"));
                                        //
                                        // mDetailList.addFirst(item);
                                        // }
                                        mDetailList.clear();
                                        VariantsInfo item = new VariantsInfo();
                                        item.setSkuImage("");
                                        mDetailList.addFirst(item);
                                        gv_detail_pic.setAdapter(adapterDetail);


                                    } else {

                                        if (arrayDetail.length() == 0) {
                                            mDetailList.clear();
                                            VariantsInfo item = new VariantsInfo();
                                            item.setSkuImage("");
                                            mDetailList.addFirst(item);
                                        } else {
                                            for (int i = 0; i < arrayDetail.length(); i++) {
                                                JSONObject objDetail = (JSONObject) arrayDetail.get(i);
                                                VariantsInfo item = new VariantsInfo();

                                                // item.setSkuColor(objDetail.optString("productId"));
                                                // item.setSkuColorId(objDetail.optString("colorId"));
                                                item.setSkuImage(objDetail.optString("imageUrl"));
                                                item.setIsLocal(false);
                                                item.setProductId(objDetail.optString("productId"));

                                                mDetailList.addFirst(item);
                                            }
                                        }
                                        gv_detail_pic.setAdapter(adapterDetail);
                                    }


                                    JSONArray arrayPrice = object.optJSONArray("prices");
                                    if (!arrayPrice.equals("")) {
                                        price = arrayPrice.toString();
                                    }

                                    JSONObject objFirst = object.optJSONObject("productFirstImage");

                                    firstPic = objFirst.optString("imageUrl");

                                    iv_first_pic.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            showPicturePopupWindow("F");

                                        }
                                    });
                                    // if (firstPic.equals("null")) {
                                    // iv_first_pic.setOnClickListener(new OnClickListener() {
                                    //
                                    // @Override
                                    // public void onClick(View v) {
                                    // showPicturePopupWindow("F");
                                    //
                                    // }
                                    // });
                                    //
                                    // } else {
                                    // iv_first_pic.setOnLongClickListener(new OnLongClickListener()
                                    // {
                                    //
                                    // @Override
                                    // public boolean onLongClick(View v) {
                                    // Log.e("长按", "准备删除");
                                    // return true;
                                    // }
                                    // });
                                    // }

                                    Picasso.with(MtsGoodsEditActivity.this).load(firstPic).resize(80, 80)
                                            .into(iv_first_pic);

                                    JSONObject objProduct = object.optJSONObject("product");

                                    et_product_name.setText(objProduct.optString("productName"));
                                    et_product_brand.setText(objProduct.optString("brandName"));
                                    et_product_style.setText(objProduct.optString("modelId"));
                                    et_product_price.setText(String.valueOf(objProduct.optDouble("listPrice")));
                                    et_product_description.setText(objProduct.optString("description"));

                                    updateId = objProduct.optString("productId");
                                    updateBrandId = objProduct.optString("brandId");
                                    privateCategoryId = objProduct.optString("privateCategoryId");
                                    publicCategoryId = objProduct.optString("publicCategoryId");

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call arg0, IOException arg1) {
                        progressDialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MtsGoodsEditActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });

    }


    public void updateProduct() {
        progressDialog.show();
        OkHttpManager.client
                .newCall(
                        new Request.Builder()
                                .url(MtsUrls.base
                                        + MtsUrls.update_product)
                                .post(new FormBody.Builder()
                                        .add("externalLoginKey",
                                                Localxml.search(MtsGoodsEditActivity.this, "externalloginkey"))
                                        .add("productId", updateId)
                                        .add("productName", et_product_name.getText().toString())
                                        .add("brandId", updateBrandId)
                                        .add("modelId", et_product_style.getText().toString())
                                        .add("privateCategoryId", privateCategoryId)
                                        .add("publicCategoryId", publicCategoryId)
                                        .add("listPrice", et_product_price.getText().toString())
                                        .add("description", et_product_description.getText().toString()).build())
                .build()).enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("=======" + "更新结果" + "========" + jsonStr + "=============");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String success = object.optString("isSuccess");
                                    String msg = object.optString("_ERROR_MESSAGE_");
                                    if (success.equals("Y")) {
                                        Toast.makeText(MtsGoodsEditActivity.this, "更新成功", Toast.LENGTH_LONG).show();
                                        setResult(301);
                                        finish();
                                    } else {
                                        Toast.makeText(MtsGoodsEditActivity.this, "更新失败", Toast.LENGTH_LONG).show();
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
                        progressDialog.dismiss();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                Toast.makeText(MtsGoodsEditActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });



    }

    /**
     * 拍照或从图库选择图片(PopupWindow形式)
     */
    public void showPicturePopupWindow(final String type) {
        menuWindow = new SelectPicPopupWindow(this, new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 隐藏弹出窗口
                menuWindow.dismiss();
                switch (v.getId()) {
                    case R.id.takePhotoBtn:// 拍照
                        takePhoto(type);
                        break;
                    case R.id.pickPhotoBtn:// 相册选择图片
                        pickPhoto(type);
                        break;
                    case R.id.cancelBtn:// 取消
                        break;
                    default:
                        break;
                }
            }
        });
        menuWindow.showAtLocation(findViewById(R.id.rl_mts_goods_edit), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0,
                0);
    }

    /**
     * 拍照获取图片
     */
    private void takePhoto(String types) {
        // 执行拍照前，应该先判断SD卡是否存在
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            /**
             * 通过指定图片存储路径，解决部分机型onActivityResult回调 data返回为null的情况
             */
            // 获取与应用相关联的路径
            String imageFilePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
            // 根据当前时间生成图片的名称
            String timestamp = "/" + formatter.format(new Date()) + ".jpg";
            File imageFile = new File(imageFilePath, timestamp);// 通过路径创建保存文件
            mImagePath = imageFile.getAbsolutePath();
            imageFileUri = Uri.fromFile(imageFile);// 获取文件的Uri
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);// 告诉相机拍摄完毕输出图片到指定的Uri
            if (types.equals("F")) {
                startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_F);
            } else if (types.equals("S")) {
                startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_S);
            } else if (types.equals("P")) {
                startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_P);
            }
        } else {
            Toast.makeText(this, "内存卡不存在!", Toast.LENGTH_LONG).show();
        }
    }

    /***
     * 从相册中取图片
     */
    private void pickPhoto(String types) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        if (types.equals("F")) {
            startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_F);
        } else if (types.equals("S")) {
            startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_S);
        } else if (types.equals("P")) {
            startActivityForResult(intent, SELECT_IMAGE_RESULT_CODE_P);
        }
    }

    String[] proj = {MediaColumns.DATA};

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_RESULT_CODE_S && resultCode == RESULT_OK) {
            String imagePath = "";
            Uri uri = null;
            if (data != null && data.getData() != null) {// 有数据返回直接使用返回的图片地址
                uri = data.getData();

                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                if (cursor == null) {
                    uri = ImageUtils.getUri(this, data);
                }

                imagePath = ImageUtils.getFilePathByFileUri(this, uri);

                file = getFileForUrl(uri);

                uploadPic("S", mColorItemId, productId, false);
            } else {
                // 拍照
                imagePath = mImagePath;

                file = new File(imagePath);

                uploadPic("S", mColorItemId, productId, false);
            }

            VariantsInfo info = new VariantsInfo();
            info.setSkuImage(imagePath);
            info.setSkuColor(mColorItem);
            info.setSkuColorId(mColorItemId);
            info.setIsLocal(true);
            mItemList.set(mPositionItem, info);
            adapterItem.notifyDataSetChanged();
        } else if (requestCode == SELECT_IMAGE_RESULT_CODE_P && resultCode == RESULT_OK) {
            String imagePath = "";
            Uri uri = null;
            if (data != null && data.getData() != null) {// 有数据返回直接使用返回的图片地址
                uri = data.getData();

                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                if (cursor == null) {
                    uri = ImageUtils.getUri(this, data);
                }

                imagePath = ImageUtils.getFilePathByFileUri(this, uri);

                file = getFileForUrl(uri);

                uploadPic("P", mColorDetailId, productId, true);
            } else {
                // 拍照
                imagePath = mImagePath;

                file = new File(imagePath);

                uploadPic("P", mColorDetailId, productId, true);
            }

            // 处理详情的gridview
            VariantsInfo info = new VariantsInfo();

            info.setProductId(mProductIdDetail);
            info.setSkuColorId(mColorDetailId);
            info.setSkuImage(imagePath);
            info.setIsLocal(true);
            mDetailList.set(mPositionDetail, info);
            adapterDetail.update(mDetailList);
            // adapterDetail.notifyDataSetChanged();

        } else if (requestCode == SELECT_IMAGE_RESULT_CODE_F && resultCode == RESULT_OK) {
            String imagePath = "";
            Uri uri = null;
            if (data != null && data.getData() != null) {// 有数据返回直接使用返回的图片地址
                uri = data.getData();

                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                if (cursor == null) {
                    uri = ImageUtils.getUri(this, data);
                }

                imagePath = ImageUtils.getFilePathByFileUri(this, uri);
                Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 50, 50);
                iv_first_pic.setImageBitmap(bitmap);
                file = getFileForUrl(uri);

                uploadPic("F", mColorDetailId, productId, true);
            } else {
                // 拍照
                imagePath = mImagePath;

                Bitmap bitmap = ImageUtils.getImageThumbnail(imagePath, 50, 50);
                iv_first_pic.setImageBitmap(bitmap);

                file = new File(imagePath);

                uploadPic("F", mColorDetailId, productId, true);
            }

        } else if (requestCode == 100) {
            if (resultCode == 101) {
                getProductDetail();
            } else {

            }
        } else if (requestCode == 200) {
            if (resultCode == 201) {
                getProductDetail();
            } else {

            }
        }


    }

    private void uploadPic(String type, String colorId, String proId, Boolean isFirst) {

        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        // paramsMap.put("externalLoginKey", Localxml.search(MtsGoodsEditActivity.this,
        // "externalloginkey"));
        paramsMap.put("productId", proId);
        paramsMap.put("srcImage", file);
        paramsMap.put("imageType", type);
        if (!isFirst) {
            paramsMap.put("colorId", colorId);
        } else {

        }

        putImageForFile2(MtsUrls.base + MtsUrls.put_imagefile + "?externalLoginKey="
                + Localxml.search(MtsGoodsEditActivity.this, "externalloginkey"), paramsMap, type);
    }

    private void putImageForFile2(String requestUrl, HashMap<String, Object> paramsMap, final String type) {

        MultipartBody.Builder builder = new MultipartBody.Builder();

        builder.setType(MultipartBody.FORM);

        for (String key : paramsMap.keySet()) {

            Object object = paramsMap.get(key);
            if (!(object instanceof File)) {
                builder.addFormDataPart(key, object.toString());
            } else {
                File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse("jpg/png"), file));
            }
        }

        // 创建RequestBody
        RequestBody body = builder.build();
        // 创建Request
        final Request request = new Request.Builder().url(requestUrl).post(body).build();
        final Call call = OkHttpManager.client.newBuilder().build().newCall(request);

        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }
                final String string = response.body().string();
                Log.e("哈哈", "response ----->" + string);
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(string);
                            String is = object.optString("isSuccess");
                            String message = object.optString("_ERROR_MESSAGE_");
                            if (is.equals("Y")) {
                                Toast.makeText(MtsGoodsEditActivity.this, "上传成功", Toast.LENGTH_LONG).show();
                                String picId = object.optString("imageId");
                                if (type.equals("F")) {
                                    firstPicId = picId;
                                } else if (type.equals("S")) {
                                    mItemList.get(mPositionItem).setImgId(picId);
                                    adapterItem.notifyDataSetChanged();
                                } else if (type.equals("P")) {
                                    mDetailList.get(mPositionDetail).setImgId(picId);
                                    adapterDetail.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(MtsGoodsEditActivity.this, "上传失败" + "\n" + message, Toast.LENGTH_LONG)
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

            }

            @Override
            public void onFailure(Call arg0, IOException arg1) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MtsGoodsEditActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public File getFileForUrl(Uri uri) {

        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        File file = new File(img_path);

        return file;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(302);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 通过uri获取图片并进行压缩 useless
     * 
     * @param uri
     */
    public static String getBitmapFormUri(Activity ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;// optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1)) return null;
        // 图片分辨率以480x800为标准
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0) be = 1;
        // 比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;// 设置缩放比例
        bitmapOptions.inDither = true;// optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);// 再进行质量压缩
    }

    /**
     * 质量压缩方法useless
     * 
     * @param image
     * @return
     */
    public static String compressImage(Bitmap image) {

        String string = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            // 第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差 ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        byte[] result = baos.toByteArray();
        string = Base64.encodeToString(result, Base64.DEFAULT);



        // ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        // Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
        // 把ByteArrayInputStream数据生成图片
        return string;
    }

    /**
     * useless
     * 
     * @param bm
     * @return
     */
    public byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();// 初始化一个流对象
        // bm.compress(CompressFormat.JPEG, 100, output);// 把bitmap100%高质量压缩 到 output对象里
        bm.recycle();// 自由选择是否进行回收
        byte[] result = output.toByteArray();// 转换成功了
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
