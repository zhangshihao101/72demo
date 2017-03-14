package com.spt.adapter;

import java.util.List;

import com.umeng.socialize.utils.Log;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class PeopleHomePagerAdapter extends PagerAdapter {

    private List<ImageView> imgList;
    private List<String> wapList;

    public PeopleHomePagerAdapter(List<ImageView> imgList, List<String> wapList) {
        super();
        this.imgList = imgList;
        this.wapList = wapList;
    }



    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = imgList.get(position % imgList.size());

        if (view.getParent() != null) {
            ViewGroup group = (ViewGroup) view.getParent();
            group.removeView(view);
        }

        container.removeView(view);
        container.addView(view);

        return imgList.get(position % imgList.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // container.removeView(imgList.get(position % imgList.size()));
    }

    @Override
    public Object instantiateItem(View container, final int position) {
        View imgView = imgList.get(position);
        final String wap = wapList.get(position);
        imgView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("=====地址===" + wapList.get(position));
                Log.e("=========" + wapList.get(position));
            }
        });
        ((ViewPager) container).addView(imgList.get(position), 0);
        return imgView;



    }


}
