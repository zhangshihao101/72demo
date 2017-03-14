package com.spt.utils;

import java.lang.ref.WeakReference;

import com.spt.fragment.PeopleFragment;

import android.os.Handler;
import android.os.Message;

public class ImageHandlerF extends Handler {

    /**
     * 请求更新显示的View。
     */
    public static final int MSG_UPDATE_IMAGE = 1;
    /**
     * 请求暂停轮播。
     */
    public static final int MSG_KEEP_SILENT = 2;
    /**
     * 请求恢复轮播。
     */
    public static final int MSG_BREAK_SILENT = 3;
    /**
     * 记录最新的页号，当用户手动滑动时需要记录新页号，否则会使轮播的页面出错。 例如当前如果在第一页，本来准备播放的是第二页，而这时候用户滑动到了末页，
     * 则应该播放的是第一页，如果继续按照原来的第二页播放，则逻辑上有问题。
     */
    public static final int MSG_PAGE_CHANGED = 4;

    // 轮播间隔时间
    public static final long MSG_DELAY = 3000;

    // 使用弱引用避免Handler泄露.这里的泛型参数可以不是Activity，也可以是Fragment等
    private WeakReference<PeopleFragment> weakReference;
    private int currentItem = 0;

    public ImageHandlerF(WeakReference<PeopleFragment> wk) {
        weakReference = wk;
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        PeopleFragment fragment = weakReference.get();
        if (fragment == null) {
            return;
        }

        if (fragment.handler.hasMessages(MSG_UPDATE_IMAGE)) {
            fragment.handler.removeMessages(MSG_UPDATE_IMAGE);
        }
        switch (msg.what) {
            case MSG_UPDATE_IMAGE:
                currentItem++;
                fragment.vp_people.setCurrentItem(currentItem);
                // 准备下次播放
                fragment.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                break;
            case MSG_KEEP_SILENT:
                // 只要不发送消息就暂停了
                break;
            case MSG_BREAK_SILENT:
                fragment.handler.sendEmptyMessageDelayed(MSG_UPDATE_IMAGE, MSG_DELAY);
                break;
            case MSG_PAGE_CHANGED:
                // 记录当前的页号，避免播放的时候页面显示不正确。
                currentItem = msg.arg1;
                break;
            default:
                break;
        }
    }
}
