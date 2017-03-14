package com.spt.fragment;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.spt.adapter.ClickHomePagerAdapter;
import com.spt.adapter.HorizontalListViewAdapter;
import com.spt.bean.ConPeopleInfo;
import com.spt.controler.MipcaActivityCapture;
import com.spt.page.AddFriendsActivity;
import com.spt.page.FindCompanyActivity;
import com.spt.page.FindPersonActivity;
import com.spt.page.PeopleSearchActivity;
import com.spt.page.PersonalDataActivity;
import com.spt.sht.R;
import com.spt.utils.ImageHandler;
import com.spt.utils.ImageHandlerF;
import com.spt.utils.MtsUrls;
import com.spt.utils.NoDoubleClickUtils;
import com.spt.utils.OkHttpManager;
import com.spt.utils.SignUtil;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class PeopleFragment extends Fragment {

    private View view;
    private static Context mContext;
    private TextView tv_people_search;
    private ImageView iv_add, iv_no_pic;
    private TextView tv_peo_count;
    private HorizontalListViewAdapter hListViewAdapter;
    private GridView gv_horizon;
    private LinearLayout ll_Point;
    public ViewPager vp_people;
    private List<ImageView> imgList;// 轮播图片集合
    private List<String> imgUrls; // 获取的图片地址集合
    private List<String> wapUrls; // 获取的wap页地址集合
    // private int[] imgs = new int[] {R.drawable.lunbo_1, R.drawable.lunbo_2,
    // R.drawable.lunbo_3};
    private int prePosition;// 轮播三个点前一个位置的标记
    public RelativeLayout rl_find_company, rl_find_people, rl_count;

    public List<ConPeopleInfo> peopleList;

    private final static int SCANNIN_GREQUEST_CODE = 1;
    private SharedPreferences sp;

    private String isF;

    private int imageWidth = 0, imageHeight = 0;

    // 轮播handler
    public ImageHandlerF handler = new ImageHandlerF(new WeakReference<PeopleFragment>(this));
    private HashMap<String, String> param;

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        view = inflater.inflate(R.layout.fragment_people, null);

        intiViews();
        getPic();

        getCount();

        getRecommendPeople();

        iv_add.setOnClickListener(new OnClickListener() {

            @SuppressLint("InflateParams")
            @Override
            public void onClick(View v) {
                View menu_view = LayoutInflater.from(mContext).inflate(R.layout.pop_menu_people, null);
                TextView tv_add_friends = (TextView) menu_view.findViewById(R.id.tv_add_friends);
                TextView tv_scan = (TextView) menu_view.findViewById(R.id.tv_scan);
                final PopupWindow menu_pop =
                        new PopupWindow(menu_view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                // 点击外边可让popupwindow消失
                menu_pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_topbar_add_more_bg));
                menu_pop.setOutsideTouchable(true);
                // 获取焦点，否则无法点击
                menu_pop.setFocusable(true);
                // 设置popupwindow显示位置
                menu_pop.showAsDropDown(v);

                tv_add_friends.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, AddFriendsActivity.class);

                        startActivity(intent);
                        menu_pop.dismiss();
                    }
                });

                tv_scan.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, MipcaActivityCapture.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                        menu_pop.dismiss();
                    }
                });

            }
        });

        rl_find_company.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(mContext, FindCompanyActivity.class);
                    startActivity(intent);
                }
            }
        });

        rl_find_people.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(mContext, FindPersonActivity.class);
                    startActivity(intent);
                }
            }
        });

        tv_people_search.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(mContext, PeopleSearchActivity.class);
                    startActivity(intent);
                }
            }
        });

        gv_horizon.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!NoDoubleClickUtils.isDoubleClick()) {
                    Intent intent = new Intent(mContext, PersonalDataActivity.class);
                    intent.putExtra("personId", peopleList.get(position).getUserLoginId());
                    intent.putExtra("isF", peopleList.get(position).getIsFriend());
                    startActivity(intent);
                }
            }
        });

        rl_count.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FindPersonActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }



    private void getRecommendPeople() {
        OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.getRecommendation)
                .post(new FormBody.Builder().add("userLoginId", sp.getString("username", ""))
                        .add("accessToken", sp.getString("accessToken", "")).add("pageIndex", "0").add("pageSize", "10")
                        .build())
                .build()).enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("====推荐人====" + jsonStr + "====");

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    JSONArray array = object.optJSONArray("result");
                                    if (array == null || array.length() == 0) {
                                        Toast.makeText(mContext, "暂无推荐人", Toast.LENGTH_SHORT).show();
                                    } else {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = array.optJSONObject(i);
                                            ConPeopleInfo info = new ConPeopleInfo();
                                            info.setConnectionRole(obj.optString("connectionRole"));
                                            info.setIsFriend(obj.optString("isFriend"));
                                            info.setmHeader(obj.optString("logoPath"));
                                            info.setmName(obj.optString("connectionName"));
                                            info.setUserLoginId(obj.optString("userLoginId"));
                                            info.setFlag(false);
                                            peopleList.add(info);
                                            hListViewAdapter.notifyDataSetChanged();
                                        }
                                        horizontal_layout();
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
                                Toast.makeText(mContext, "请求失败，请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
    }

    @SuppressWarnings("deprecation")
    public void horizontal_layout() {
        int size = peopleList.size();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int allWidth = (int) (110 * size * density);
        int itemWidth = (int) (100 * density);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(allWidth, LinearLayout.LayoutParams.FILL_PARENT);
        gv_horizon.setLayoutParams(params);// 设置GirdView布局参数
        gv_horizon.setColumnWidth(itemWidth);// 列表项宽
        gv_horizon.setHorizontalSpacing(10);// 列表项水平间距
        gv_horizon.setStretchMode(GridView.NO_STRETCH);
        gv_horizon.setNumColumns(size);// 总长度
    }

    private void getPic() {
        OkHttpManager.client.newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_carousels)
                .post(new FormBody.Builder().add("accessToken", sp.getString("accessToken", "")).build()).build())
                .enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            return;
                        }
                        final String jsonStr = response.body().string();
                        System.out.println("====轮播====" + jsonStr + "====");

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    JSONObject object = new JSONObject(jsonStr);
                                    JSONArray array = object.optJSONArray("result");
                                    if (array == null || array.length() == 0) {
                                        iv_no_pic.setVisibility(View.VISIBLE);
                                        Picasso.with(mContext)
                                                .load("http://metasolo.cn/homepage/images/product-no-image.jpg")
                                                .resize(800, 600).centerCrop().into(iv_no_pic);
                                    } else {
                                        iv_no_pic.setVisibility(View.GONE);
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = (JSONObject) array.get(i);
                                            String url = obj.optString("url") + obj.optString("fileKey");
                                            imgUrls.add(url);
                                            wapUrls.add(obj.optString("fileAddress"));
                                        }
                                        initData();
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
                                Toast.makeText(mContext, "请求失败，请检查网络", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

    }

    private void getCount() {
        param.clear();
        param.put("client_id", "localhost");

        String uri =
                MtsUrls.base + MtsUrls.get_count + "?client_id=localhost&sign=" + SignUtil.genSign(param, "localhost");

        OkHttpManager.client.newCall(new Request.Builder().url(uri).build()).enqueue(new Callback() {

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }
                final String jsonStr = response.body().string();
                System.out.println("===人数====" + jsonStr + "====");
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(jsonStr);
                            String count = object.optString("count");
                            tv_peo_count.setText(count);
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
                        Toast.makeText(mContext, "请求失败，请检查网络", Toast.LENGTH_SHORT).show();

                    }
                });

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == -1) {
                    final Bundle bundle = data.getExtras();
                    OkHttpManager.client
                            .newCall(new Request.Builder().url(MtsUrls.base + MtsUrls.get_individual)
                                    .post(new FormBody.Builder().add("userLoginId", bundle.getString("result"))
                                            .add("accessToken", sp.getString("accessToken", ""))
                                            .add("otherId", sp.getString("username", "")).build())
                                    .build())
                            .enqueue(new Callback() {

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (!response.isSuccessful()) {
                                        return;
                                    }
                                    final String jsonStr = response.body().string();
                                    System.out.println("AAA===" + jsonStr);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject object = new JSONObject(jsonStr);
                                                String error = object.optString("_ERROR_MESSAGE_");
                                                if (error.equals("")) {
                                                    JSONObject obj = object.optJSONObject("individualInfor");
                                                    if (obj.optString("isFirend").equals("0")) {
                                                        isF = "0";
                                                    } else if (obj.optString("isFirend").equals("1")) {
                                                        isF = "1";
                                                    } else if (obj.optString("isFirend").equals("2")) {
                                                        isF = "2";
                                                    }
                                                    Intent intent = new Intent(mContext, PersonalDataActivity.class);
                                                    intent.putExtra("personId", bundle.getString("result"));
                                                    intent.putExtra("isF", isF);
                                                    startActivity(intent);
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
                                            Toast.makeText(mContext, "网络错误，请检查网络", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });
                }
                break;
            default:
                break;
        }
    }

    private void intiViews() {
        tv_people_search = (TextView) view.findViewById(R.id.tv_ft_people_search);
        iv_add = (ImageView) view.findViewById(R.id.iv_add);
        iv_no_pic = (ImageView) view.findViewById(R.id.iv_no_pic);
        // iv_add = (ImageView) view.findViewById(R.id.et_text);
        tv_peo_count = (TextView) view.findViewById(R.id.tv_peo_count);
        gv_horizon = (GridView) view.findViewById(R.id.gv_horizon);
        ll_Point = (LinearLayout) view.findViewById(R.id.ll_Point);
        vp_people = (ViewPager) view.findViewById(R.id.vp_people);
        rl_find_company = (RelativeLayout) view.findViewById(R.id.rl_find_company);
        rl_find_people = (RelativeLayout) view.findViewById(R.id.rl_find_people);
        rl_count = (RelativeLayout) view.findViewById(R.id.rl_count);
        peopleList = new ArrayList<ConPeopleInfo>();
        hListViewAdapter = new HorizontalListViewAdapter(mContext, peopleList);
        gv_horizon.setAdapter(hListViewAdapter);
        // hListView.setAdapter(hListViewAdapter);
        param = new HashMap<String, String>();
        sp = getActivity().getSharedPreferences("USERINFO", Context.MODE_PRIVATE);
        // editor = sp.edit();
        imgUrls = new ArrayList<String>();
        wapUrls = new ArrayList<String>();
    }

    private void initData() {
        // 存图片的集合
        imgList = new ArrayList<ImageView>();
        for (int i = 0; i < imgUrls.size(); i++) {
            ImageView image = new ImageView(mContext);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            Picasso.with(mContext).load(imgUrls.get(i)).placeholder(R.drawable.product_no_image)
                    .transform(new CropSquareTransformation()).error(R.drawable.product_no_image).into(image);
            imgList.add(image);

            // n个点
            View point = new View(mContext);
            point.setBackgroundResource(R.drawable.dot_focus);
            LayoutParams params = new LayoutParams(15, 15);
            params.leftMargin = 10;
            point.setLayoutParams(params);
            ll_Point.addView(point);
        }
        // 设置第一个点为默认点
        ll_Point.getChildAt(0).setBackgroundResource(R.drawable.dot_not_focus);
        ClickHomePagerAdapter adapter = new ClickHomePagerAdapter(mContext, imgList, wapUrls);
        vp_people.setAdapter(adapter);
        vp_people.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // 当滑动到下一张图片，修改之前图片的点
                ll_Point.getChildAt(prePosition).setBackgroundResource(R.drawable.dot_focus);
                // 滑动到当前图片，修改当前图片的点
                ll_Point.getChildAt(position % imgList.size()).setBackgroundResource(R.drawable.dot_not_focus);
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

    public class CropSquareTransformation implements Transformation {

        @Override
        public Bitmap transform(Bitmap source) {
            int targetWidth = imageWidth;

            int targetHeight = imageHeight;

            if (source.getWidth() == 0 || source.getHeight() == 0) {
                return source;
            }

            if (source.getWidth() > source.getHeight()) {// 横向长图
                if (source.getHeight() < targetHeight && source.getWidth() <= 400) {
                    return source;
                } else {
                    // 如果图片大小大于等于设置的高度，则按照设置的高度比例来缩放
                    double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                    int width = (int) (targetHeight * aspectRatio);
                    if (width > 400) { // 对横向长图的宽度 进行二次限制
                        width = 400;
                        targetHeight = (int) (width / aspectRatio);// 根据二次限制的宽度，计算最终高度
                    }
                    if (width != 0 && targetHeight != 0) {
                        Bitmap result = Bitmap.createScaledBitmap(source, width, targetHeight, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    } else {
                        return source;
                    }
                }
            } else {// 竖向长图
                // 如果图片小于设置的宽度，则返回原图
                if (source.getWidth() < targetWidth && source.getHeight() <= 600) {
                    return source;
                } else {
                    // 如果图片大小大于等于设置的宽度，则按照设置的宽度比例来缩放
                    double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                    int height = (int) (targetWidth * aspectRatio);
                    if (height > 600) {// 对横向长图的高度进行二次限制
                        height = 600;
                        targetWidth = (int) (height / aspectRatio);// 根据二次限制的高度，计算最终宽度
                    }
                    if (height != 0 && targetWidth != 0) {
                        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, height, false);
                        if (result != source) {
                            // Same bitmap is returned if sizes are the same
                            source.recycle();
                        }
                        return result;
                    } else {
                        return source;
                    }
                }
            }
        }

        @Override
        public String key() {
            return "desiredWidth" + " desiredHeight";
        }



    }


}
