package com.spt.controler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class MyListView extends ListView {

	int mLastMotionY;

	boolean bottomFlag;

	public MyListView(Context context) {
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 阻止父类拦截事件
		if (bottomFlag) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}

		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int y = (int) ev.getRawY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 首先拦截down事件,记录y坐标
			mLastMotionY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// deltaY > 0 是向下运动,< 0是向上运动
			int deltaY = y - mLastMotionY;

			if (deltaY > 0) {
				View child = getChildAt(0);
				if (child != null) {

					if (getFirstVisiblePosition() == 0 && child.getTop() == 0) {
						bottomFlag = false;
						getParent().requestDisallowInterceptTouchEvent(false);
					}

					int top = child.getTop();
					int padding = getPaddingTop();
					if (getFirstVisiblePosition() == 0 && Math.abs(top - padding) <= 8) {
						bottomFlag = false;
						getParent().requestDisallowInterceptTouchEvent(false);

					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}

		return super.onTouchEvent(ev);
	}

	public void setBottomFlag(boolean flag) {
		bottomFlag = flag;
	}

}
