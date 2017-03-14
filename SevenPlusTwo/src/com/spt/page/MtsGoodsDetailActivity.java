package com.spt.page;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.ColorAndSizeAdapter;
import com.spt.adapter.ColorAndSizeAdapter.ItemClickListener;
import com.spt.adapter.HomePagerAdapter;
import com.spt.adapter.TagAdapter;
import com.spt.bean.Bean;
import com.spt.bean.SkuItme;
import com.spt.controler.FlowTagLayout;
import com.spt.sht.R;
import com.spt.utils.DataUtil;
import com.spt.utils.ImageHandler;
import com.spt.utils.Localxml;
import com.spt.utils.MtsUrls;
import com.spt.utils.OkHttpManager;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

@SuppressLint("NewApi")
public class MtsGoodsDetailActivity extends FragmentActivity {

    private TextView tv_goods_name, tv_goods_brand, tv_goods_style, tv_goods_price, tv_goods_public, tv_goods_mine,
            tv_goods_flag, tv_unshelve, tv_edit, tv_delete, tv_rmb, tv_sale_count, tv_count, tv_text_describe;
    private RelativeLayout rl_price;
    private FlowTagLayout fl_color, fl_size;
    private ColorAndSizeAdapter mColorAdapter, mSizeAdapter;
    private String color, size, specId;// 尺码和颜色和Id
    private List<SkuItme> mItemList;// 商品数据
    private List<Bean> mColorList, mSizeList;// 颜色与尺码列表与选中状态
    private List<String> colorList, sizeList;// 颜色尺码集合
    private int stock = 0;// 库存
    private int stockSale = 0;// 可销售库存
    private int count;// 数量
    private String minDisPrice, maxDisPrice;// 最大分销价和最小分销价
    private ImageView iv_mts_back, iv_goods_share, iv_nopager;

    public ViewPager vp_home;
    private LinearLayout ll_Point;

    private TagAdapter colorAdapter, sizeAdapter;
    private List<Object> colorData, sizeData;

    private ProgressDialog progressDialog;

    private String productId = "", imgUrl, name, partyId;

    private List<ImageView> imgList;// 轮播图片集合
    private List<String> imgUrls; // 获取的图片地址集合
    private int prePosition;// 轮播三个点前一个位置的标记
    // 轮播handler
    public ImageHandler handler = new ImageHandler(new WeakReference<MtsGoodsDetailActivity>(this));

    private Bundle b;

    private int sizeF = 0, colorF = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mts_goods_detail);
        super.onCreate(savedInstanceState);

        b = this.getIntent().getExtras();
        productId = b.getString("productId");
        imgUrl = b.getString("imgUrl");
        name = b.getString("name");
        partyId = b.getString("partyId");
        // productId = b.getStringExtra("productId");

        initView();

        getProduct();

        /**
         * 粗暴的在主线程中进行网络连接，效果不好
         */
        // StrictMode.setThreadPolicy(new
        // StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
        // .detectNetwork().penaltyLog().build());
        // StrictMode.setVmPolicy(new
        // StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
        // .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        fl_color.setAdapter(mColorAdapter);
        mColorAdapter.setItemClickListener(new ItemClickListener() {

            @Override
            public void ItemClick(Bean bean, int position) {
                color = bean.getName();
                if (bean.getStates().equals("0")) {
                    colorF = 0;
                    if (colorF == 1 && sizeF == 1) {
                        rl_price.setVisibility(View.VISIBLE);
                    } else {
                        rl_price.setVisibility(View.GONE);
                    }
                    // 清空尺码
                    mSizeList = DataUtil.clearAdapterStates(mSizeList);
                    mSizeAdapter.notifyDataSetChanged();
                    // 清空颜色
                    mColorList = DataUtil.clearAdapterStates(mColorList);
                    mColorAdapter.notifyDataSetChanged();
                    color = "";
                    // 判断使用选中了尺码
                    if (!TextUtils.isEmpty(size)) {
                        // 选中尺码，计算库存
                        stock = DataUtil.getSizeAllStock(mItemList, size);

                        if (stock > 0) {
                            tv_count.setText(stock + "件");
                            tv_sale_count.setText(stockSale + "件");
                        } else {
                            tv_count.setText("0件");
                            tv_sale_count.setText("0件");
                        }
                        List<String> list = DataUtil.getColorListBySize(mItemList, size);
                        if (list != null && list.size() > 0) {
                            // 更新颜色列表
                            mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, color);
                            mColorAdapter.notifyDataSetChanged();
                        }
                        mSizeList = DataUtil.setAdapterStates(mSizeList, size);
                        mSizeAdapter.notifyDataSetChanged();
                    } else {
                        // tv_sale_count.setText(tv_goods_details_stock.getText().toString());
                    }
                } else if (bean.getStates().equals("1")) {
                    colorF = 1;
                    if (colorF == 1 && sizeF == 1) {
                        rl_price.setVisibility(View.VISIBLE);
                    } else {
                        rl_price.setVisibility(View.GONE);
                    }
                    // 选中颜色
                    mColorList = DataUtil.updateAdapterStates(mColorList, "0", position);
                    mColorAdapter.notifyDataSetChanged();
                    // 计算该颜色对应的尺码列表
                    List<String> list = DataUtil.getSizeListByColor(mItemList, color);
                    if (!TextUtils.isEmpty(size)) {
                        // 计算该颜色与尺码对应的库存
                        stock = DataUtil.getStockByColorAndSize(mItemList, color, size);
                        stockSale = DataUtil.getStockSaleByColorAndSize(mItemList, color, size);
                        // 获取单品ID
                        specId = DataUtil.getProductIdByColorAndSize(mItemList, color, size);
                        // 获取价格
                        tv_rmb.setText("￥" + DataUtil.getPriceByColorAndSize(mItemList, color, size));
                        if (stock > 0) {
                            tv_count.setText(stock + "件");
                            tv_sale_count.setText(stockSale + "件");
                        } else {
                            tv_count.setText("0件");
                            tv_sale_count.setText("0件");
                        }
                        if (list != null && list.size() > 0) {
                            // 更新尺码列表
                            mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, size);
                            mSizeAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // 根据颜色计算库存
                        stock = DataUtil.getSizeAllStock(mItemList, color);
                        if (stock > 0) {
                            tv_count.setText(stock + "件");
                            tv_sale_count.setText(stockSale + "件");
                        } else {
                            tv_count.setText("0件");
                            tv_sale_count.setText("0件");
                        }
                        if (list != null && list.size() > 0) {
                            // 更新尺码列表
                            mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, "");
                            mSizeAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        fl_size.setAdapter(mSizeAdapter);
        mSizeAdapter.setItemClickListener(new ItemClickListener() {

            @Override
            public void ItemClick(Bean bean, int position) {
                size = bean.getName();
                if (bean.getStates().equals("0")) {
                    sizeF = 0;
                    if (colorF == 1 && sizeF == 1) {
                        rl_price.setVisibility(View.VISIBLE);
                    } else {
                        rl_price.setVisibility(View.GONE);
                    }
                    // 清空尺码
                    mSizeList = DataUtil.clearAdapterStates(mSizeList);
                    mSizeAdapter.notifyDataSetChanged();
                    // 清空颜色
                    mColorList = DataUtil.clearAdapterStates(mColorList);
                    mColorAdapter.notifyDataSetChanged();
                    size = "";
                    if (!TextUtils.isEmpty(color)) {
                        // 计算该颜色对应的所有库存
                        stock = DataUtil.getColorAllStock(mItemList, color);
                        if (stock > 0) {
                            tv_count.setText(stock + "件");
                            tv_sale_count.setText(stockSale + "件");
                        } else {
                            tv_count.setText("0");
                            tv_sale_count.setText("0件");
                        }
                        // 计算该颜色对应的尺码列表
                        List<String> list = DataUtil.getSizeListByColor(mItemList, color);
                        if (list != null && list.size() > 0) {
                            // 更新尺码列表
                            mSizeList = DataUtil.setSizeOrColorListStates(mSizeList, list, size);
                            mSizeAdapter.notifyDataSetChanged();
                        }
                        mColorList = DataUtil.setAdapterStates(mColorList, color);
                        mColorAdapter.notifyDataSetChanged();
                    } else {
                        // tv_sale_count.setText(tv_goods_details_stock.getText().toString());
                    }
                } else if (bean.getStates().equals("1")) {
                    sizeF = 1;
                    if (colorF == 1 && sizeF == 1) {
                        rl_price.setVisibility(View.VISIBLE);
                    } else {
                        rl_price.setVisibility(View.GONE);
                    }
                    // 选中尺码
                    mSizeList = DataUtil.updateAdapterStates(mSizeList, "0", position);
                    mSizeAdapter.notifyDataSetChanged();
                    // 获取该尺码对应的颜色列表
                    List<String> list = DataUtil.getColorListBySize(mItemList, size);
                    if (!TextUtils.isEmpty(color)) {
                        // 计算该颜色与尺码对应的库存
                        stock = DataUtil.getStockByColorAndSize(mItemList, color, size);
                        stockSale = DataUtil.getStockSaleByColorAndSize(mItemList, color, size);
                        // 获取单品Id
                        specId = DataUtil.getProductIdByColorAndSize(mItemList, color, size);
                        // 获取价格
                        tv_rmb.setText("￥" + DataUtil.getPriceByColorAndSize(mItemList, color, size));
                        if (stock > 0) {
                            tv_count.setText(stock + "件");
                            tv_sale_count.setText(stockSale + "件");
                        } else {
                            tv_count.setText("0件");
                            tv_sale_count.setText(stockSale + "0件");
                        }
                        if (list != null && list.size() > 0) {
                            // 更新颜色列表
                            mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, color);
                            mColorAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // 计算该尺码的所有库存
                        stock = DataUtil.getSizeAllStock(mItemList, size);
                        if (stock > 0) {
                            tv_count.setText(stock + "件");
                            tv_sale_count.setText(stockSale + "件");
                        } else {
                            tv_count.setText("0");
                            tv_sale_count.setText("0件");
                        }
                        if (list != null && list.size() > 0) {
                            mColorList = DataUtil.setSizeOrColorListStates(mColorList, list, "");
                            mColorAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        tv_unshelve.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OkHttpManager.client
                        .newCall(
                                new Request.Builder().url(MtsUrls.base + MtsUrls.off_shelf)
                                        .post(new FormBody.Builder()
                                                .add("externalLoginKey",
                                                        Localxml.search(MtsGoodsDetailActivity.this,
                                                                "externalloginkey"))
                                                .add("productId", productId).build())
                                        .build())
                        .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String isSuccess = object.optString("isSuccess");
                                    String _ERROR_MESSAGE_ = object.optString("_ERROR_MESSAGE_");
                                    if (isSuccess.equals("N")) {
                                        Toast.makeText(MtsGoodsDetailActivity.this, _ERROR_MESSAGE_, Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        Intent intent = new Intent();
                                        setResult(0, intent);
                                        finish();
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
                                Toast.makeText(MtsGoodsDetailActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        tv_delete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                OkHttpManager.client
                        .newCall(
                                new Request.Builder().url(MtsUrls.base + MtsUrls.remove_product)
                                        .post(new FormBody.Builder()
                                                .add("externalLoginKey",
                                                        Localxml.search(MtsGoodsDetailActivity.this,
                                                                "externalloginkey"))
                                                .add("productId", productId).build())
                                        .build())
                        .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {

                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    String isSuccess = object.optString("isSuccess");
                                    String _ERROR_MESSAGE_ = object.optString("_ERROR_MESSAGE_");
                                    if (isSuccess.equals("N")) {
                                        Toast.makeText(MtsGoodsDetailActivity.this, _ERROR_MESSAGE_, Toast.LENGTH_SHORT)
                                                .show();
                                    } else {
                                        Intent intent = new Intent();
                                        setResult(1, intent);
                                        finish();
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
                                Toast.makeText(MtsGoodsDetailActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        iv_goods_share.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MtsGoodsDetailActivity.this, MtsChoseShopActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("imgUrl", imgUrl);
                intent.putExtra("name", name);
                intent.putExtra("partyId", partyId);
                startActivity(intent);
            }
        });

        iv_mts_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 编辑

        tv_edit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MtsGoodsDetailActivity.this, MtsGoodsEditActivity.class);
                Bundle b = new Bundle();
                b.putString("productId", productId);
                intent.putExtras(b);
                startActivityForResult(intent, 300);
                // startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 300) {
            if (resultCode == 301) {
                // imgUrls.clear();
                // mItemList.clear();
                // colorList.clear();
                // sizeList.clear();
                // mColorList.clear();
                // mSizeList.clear();
                // mColorAdapter.notifyDataSetChanged();
                // mSizeAdapter.notifyDataSetChanged();

                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                getProduct();
            } else {

            }
        }
    }

    private void initView() {
        tv_goods_name = (TextView) findViewById(R.id.tv_goods_name);
        tv_goods_brand = (TextView) findViewById(R.id.tv_goods_brand);
        tv_goods_style = (TextView) findViewById(R.id.tv_goods_style);
        tv_goods_price = (TextView) findViewById(R.id.tv_goods_price);
        tv_goods_public = (TextView) findViewById(R.id.tv_goods_public);
        tv_goods_mine = (TextView) findViewById(R.id.tv_goods_mine);
        tv_goods_flag = (TextView) findViewById(R.id.tv_goods_flag);
        tv_unshelve = (TextView) findViewById(R.id.tv_unshelve);
        tv_edit = (TextView) findViewById(R.id.tv_edit);
        tv_delete = (TextView) findViewById(R.id.tv_delete);
        tv_rmb = (TextView) findViewById(R.id.tv_rmb);
        tv_sale_count = (TextView) findViewById(R.id.tv_sale_count);
        tv_count = (TextView) findViewById(R.id.tv_count);
        fl_color = (FlowTagLayout) findViewById(R.id.fl_color);
        fl_size = (FlowTagLayout) findViewById(R.id.fl_size);
        vp_home = (ViewPager) findViewById(R.id.vp_home);
        ll_Point = (LinearLayout) findViewById(R.id.ll_Point);
        rl_price = (RelativeLayout) findViewById(R.id.rl_price);
        iv_mts_back = (ImageView) findViewById(R.id.iv_mts_back);
        iv_goods_share = (ImageView) findViewById(R.id.iv_goods_share);
        iv_nopager = (ImageView) findViewById(R.id.iv_nopager);
        tv_text_describe = (TextView) findViewById(R.id.tv_text_describe);

        progressDialog = ProgressDialog.show(MtsGoodsDetailActivity.this, "请稍候。。。", "获取数据中。。。", true);
        progressDialog.dismiss();

        imgUrls = new ArrayList<String>();

        mItemList = new ArrayList<SkuItme>();
        mColorList = new ArrayList<Bean>();
        mSizeList = new ArrayList<Bean>();
        mColorAdapter = new ColorAndSizeAdapter(MtsGoodsDetailActivity.this);
        mSizeAdapter = new ColorAndSizeAdapter(MtsGoodsDetailActivity.this);
    }

    private void getProduct() {
        progressDialog.show();

        OkHttpManager.client
                .newCall(
                        new Request.Builder()
                                .url(MtsUrls.base + MtsUrls.get_product).post(
                                        new FormBody.Builder()
                                                .add("externalLoginKey",
                                                        Localxml.search(MtsGoodsDetailActivity.this,
                                                                "externalloginkey"))
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
                                    JSONArray arrayDetailImg = object.optJSONArray("detailImages");
                                    if (arrayDetailImg != null) {
                                        iv_nopager.setVisibility(View.GONE);
                                        if (arrayDetailImg.length() > 5) {
                                            for (int i = 0; i < 5; i++) {
                                                JSONObject objImg = (JSONObject) arrayDetailImg.get(i);
                                                String imgUrl = objImg.optString("imageUrl");
                                                imgUrls.add(imgUrl);
                                            }
                                            setImage();
                                        } else {
                                            for (int i = 0; i < arrayDetailImg.length(); i++) {
                                                JSONObject objImg = (JSONObject) arrayDetailImg.get(i);
                                                String imgUrl = objImg.optString("imageUrl");
                                                imgUrls.add(imgUrl);
                                            }
                                            setImage();
                                        }

                                    } else {
                                        // 无图的情况
                                        iv_nopager.setVisibility(View.VISIBLE);
                                        Picasso.with(MtsGoodsDetailActivity.this)
                                                .load("http://metasolo.cn/homepage/images/product-no-image.jpg")
                                                .into(iv_nopager);
                                    }

                                    JSONObject objPro = object.optJSONObject("product");
                                    tv_goods_name.setText(objPro.optString("productName"));
                                    tv_goods_brand.setText(objPro.optString("brandName"));
                                    tv_goods_style.setText(objPro.optString("modelId"));
                                    tv_goods_price.setText("￥ " + String.valueOf(objPro.optDouble("listPrice")));
                                    tv_goods_public.setText(objPro.optString("publicCategoryId"));
                                    tv_goods_mine.setText(objPro.optString("fullCategoryName"));
                                    tv_text_describe.setText(objPro.optString("description").equals("null")
                                            ? "暂无描述"
                                            : objPro.optString("description"));

                                    String tag = "";
                                    if (objPro.optString("assocTypeId").equals("P")) {
                                        tag = "私有";
                                    } else if (objPro.optString("assocTypeId").equals("A")) {
                                        tag = "挂靠";
                                    } else if (objPro.optString("assocTypeId").equals("Q")) {
                                        tag = "引用";
                                    } else if (objPro.optString("assocTypeId").equals("F")) {
                                        tag = "完全公开";
                                    } else if (objPro.optString("assocTypeId").equals("L")) {
                                        tag = "限制公开";
                                    }
                                    tv_goods_flag.setText(tag);

                                    JSONArray arraySKU = object.optJSONArray("variants");

                                    for (int i = 0; i < arraySKU.length(); i++) {
                                        JSONObject objSKU = (JSONObject) arraySKU.get(i);
                                        SkuItme item = new SkuItme();
                                        item.setColorId(objSKU.optString("colorId"));
                                        item.setDisPrice("" + objSKU.optDouble("listPrice"));
                                        item.setProductId(objSKU.optString("productId"));
                                        item.setProductName(objSKU.optString("productName"));
                                        item.setSizeId(objSKU.optString("dimensionId"));
                                        item.setSkuColor(objSKU.optString("colorDesc"));
                                        item.setSkuSize(objSKU.optString("dimensionDesc"));
                                        item.setSkuStock(objSKU.optInt("productQoh"));
                                        item.setSkuStockSale(objSKU.optInt("productAtp"));
                                        mItemList.add(item);
                                    }
                                    colorList = new ArrayList<String>();
                                    for (SkuItme item : mItemList) {
                                        String color = item.getSkuColor();
                                        if (!colorList.contains(color)) {
                                            colorList.add(color);
                                        } else {
                                            continue;
                                        }
                                    }
                                    for (int i = 0; i < colorList.size(); i++) {
                                        Bean bean = new Bean();
                                        bean.setName(colorList.get(i));
                                        bean.setStates("1");
                                        mColorList.add(bean);
                                    }

                                    mColorAdapter.onlyAddAll(mColorList);

                                    sizeList = new ArrayList<String>();
                                    for (SkuItme item : mItemList) {
                                        String size = item.getSkuSize();
                                        if (!sizeList.contains(size)) {
                                            sizeList.add(size);
                                        } else {
                                            continue;
                                        }
                                    }
                                    for (int i = 0; i < sizeList.size(); i++) {
                                        Bean bean = new Bean();
                                        bean.setName(sizeList.get(i));
                                        bean.setStates("1");
                                        mSizeList.add(bean);
                                    }
                                    mSizeAdapter.onlyAddAll(mSizeList);

                                    // XXXXX

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
                                Toast.makeText(MtsGoodsDetailActivity.this, "网络异常", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

    }

    /**
     * 轮播图展示
     */
    private void setImage() {
        // 存图片的集合
        imgList = new ArrayList<ImageView>();

        for (int i = 0; i < imgUrls.size(); i++) {
            ImageView image = new ImageView(MtsGoodsDetailActivity.this);
            image.setScaleType(ImageView.ScaleType.CENTER);
            Picasso.with(MtsGoodsDetailActivity.this).load(imgUrls.get(i)).into(image);
            // image.setImageBitmap(getBitMBitmap(imgUrls.get(i)));
            imgList.add(image);

            // n个点
            View point = new View(MtsGoodsDetailActivity.this);
            point.setBackgroundResource(R.drawable.dot_not_focus);
            LayoutParams params = new LayoutParams(15, 15);
            params.leftMargin = 10;
            point.setLayoutParams(params);
            ll_Point.addView(point);
        }
        // 设置第一个点为默认点
        ll_Point.getChildAt(0).setBackgroundResource(R.drawable.dot_focus);
        HomePagerAdapter adapter = new HomePagerAdapter(imgList);
        vp_home.setAdapter(adapter);
        vp_home.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // 当滑动到下一张图片，修改之前图片的点
                ll_Point.getChildAt(prePosition).setBackgroundResource(R.drawable.dot_not_focus);
                // 滑动到当前图片，修改当前图片的点
                ll_Point.getChildAt(position % imgList.size()).setBackgroundResource(R.drawable.dot_focus);
                // 这一次的当前位置为下一次当前位置的前一个选中条目
                prePosition = position % imgList.size();
                handler.sendMessage(Message.obtain(handler, ImageHandler.MSG_PAGE_CHANGED, position, 0));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        handler.sendEmptyMessage(ImageHandler.MSG_KEEP_SILENT);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);
                        break;
                    default:
                        break;
                }
            }
        });

        handler.sendEmptyMessageDelayed(ImageHandler.MSG_UPDATE_IMAGE, ImageHandler.MSG_DELAY);

    }
}
