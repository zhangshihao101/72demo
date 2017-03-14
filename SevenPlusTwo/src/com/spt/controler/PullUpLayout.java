package com.spt.controler;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ID_MARR on 2014/9/30.
 */
public class PullUpLayout extends FrameLayout {


    private List<OnPullListener> mOnPullListenerList = new ArrayList<OnPullListener>();

    public static enum PullEdge {
        Left, Right, Top, Bottom
    }

    public static enum ShowMode {
        LayDown, PullOut
    }

    public static enum Status {
        Open, Close, Middle,
        Opening, Closing    // TODO
    }

    public interface OnPullListener {
        public void onOpenStart(PullUpLayout layout);

        public void onOpenFinish(PullUpLayout layout);

        public void onCloseStart(PullUpLayout layout);

        public void onCloseFinish(PullUpLayout layout);

        public void onUpdate(PullUpLayout layout, int offsetX, int offsetY);

        public void onRelease(PullUpLayout layout, float offsetX, float offsetY);
    }


    private boolean mPullEnabled = true;
    private ShowMode mShowMode;
    private PullEdge mPullEdge;
    private int mPullDistance = 0;

    private ViewDragHelper mDragHelper;
    private ViewDragHelper.Callback mViewDragHelpCallback;


    public PullUpLayout(Context context) {
        this(context, null);
    }

    public PullUpLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullUpLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mViewDragHelpCallback = newViewDragHelpCallback();
        mDragHelper = ViewDragHelper.create(this, mViewDragHelpCallback);
    }

    public void addOnPullListener(OnPullListener onPullListener) {
        mOnPullListenerList.add(onPullListener);
    }

    public void removeOnPullListener(OnPullListener onPullListener) {
        mOnPullListenerList.remove(onPullListener);
    }

    public void clearOnPullListener() {
        mOnPullListenerList.clear();
    }

    private ViewDragHelper.Callback newViewDragHelpCallback() {
        return new ViewDragHelper.Callback() {

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                int topBound, bottomBound ;
                if(child==getContentViewGroup()){
                    topBound = PullUpLayout.this.getPaddingTop() - getContentViewGroup().getHeight();
                    bottomBound = PullUpLayout.this.getHeight() - getContentViewGroup().getHeight();
                }else{
                    topBound = PullUpLayout.this.getPaddingTop();
                    bottomBound = PullUpLayout.this.getHeight() + getBottomViewGroup().getHeight();
                }
                return Math.min(Math.max(top, topBound), bottomBound);
            }

            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                return child == getContentViewGroup() || child == getBottomViewGroup();
            }

            @Override
            public int getViewHorizontalDragRange(View child) {
                return mPullDistance;
            }

            @Override
            public int getViewVerticalDragRange(View child) {
                return mPullDistance;
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                for (OnPullListener l : mOnPullListenerList) {
                    l.onRelease(PullUpLayout.this, xvel, yvel);
                }

                if (releasedChild == getContentViewGroup()) {
                    processContentRelease(xvel, yvel);
                } else if (releasedChild == getBottomViewGroup()) {
                    if (getShowMode() == ShowMode.PullOut) {
                        processBottomRelease(xvel, yvel);
                    }
                }

                invalidate();
            }

            @Override
            public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
//                Log.e("MARR", "onViewPositionChanged:chagedView:" + changedView);
                int cLeft = getContentViewGroup().getLeft();
                int cTop = getContentViewGroup().getTop();
                int cRight = getContentViewGroup().getRight();
                int cBottom = getContentViewGroup().getBottom();

                if (changedView == getContentViewGroup()) {
                    if (mShowMode == ShowMode.PullOut) {
                        if (mPullEdge == PullEdge.Left || mPullEdge == PullEdge.Right) {
                            getBottomViewGroup().offsetLeftAndRight(dx);
                        } else {
                            getBottomViewGroup().offsetTopAndBottom(dy);
                        }
                    }

                } else if (changedView == getBottomViewGroup()) {

                    if (mShowMode == ShowMode.PullOut) {
                        getContentViewGroup().offsetLeftAndRight(dx);
                        getContentViewGroup().offsetTopAndBottom(dy);
                    }
                }
                dispatchPullEvent(cLeft, cTop, isOpening(dx, dy));
                invalidate();
            }
        };
    }

    public boolean isOpening(int dx, int dy) {
        PullEdge edge = getPullEdge();
        boolean isOpening = true;
        if (edge == PullEdge.Left && dx < 0) {
            isOpening = false;
        } else if (edge == PullEdge.Right && dx > 0) {
            isOpening = false;
        } else if (edge == PullEdge.Top && dy < 0) {
            isOpening = false;
        } else if (edge == PullEdge.Bottom && dy > 0) {
            isOpening = false;
        }
        return isOpening;
    }

    private int mEventCounter = 0;

    protected void dispatchPullEvent(int contentLeft, int contentTop, boolean open) {
        Status status = getOpenStatus();

        if (mOnPullListenerList.isEmpty()) {
            return;
        }
        mEventCounter = mEventCounter + 1;
        for (OnPullListener l : mOnPullListenerList) {
            if (mEventCounter == 1) {
                if (open) {
                    l.onOpenStart(this);
                } else {
                    l.onCloseStart(this);
                }
            }
            l.onUpdate(PullUpLayout.this, contentLeft - getPaddingLeft(), contentTop - getPaddingTop());
        }

        if (status == Status.Close) {
            for (OnPullListener l : mOnPullListenerList) {
                l.onCloseFinish(PullUpLayout.this);
            }
            mEventCounter = 0;
        }
        if (status == Status.Open) {
            getBottomViewGroup().setEnabled(true);
            for (OnPullListener l : mOnPullListenerList) {
                l.onOpenFinish(PullUpLayout.this);
            }
            mEventCounter = 0;
        }

    }


    // layout-margin is not supported temporarily
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PullUpLayout.LayoutParams(getContext(), attrs);
    }

    // layout-margin is not supported temporarily
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new IllegalStateException("You need 2  views in PullUpLayout");
        }
        if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
            throw new IllegalArgumentException("The 2 children in PullUpLayout must be an instance of ViewGroup");
        }
        if (mShowMode == ShowMode.PullOut) {
            layoutPullOut();
        } else if (mShowMode == ShowMode.LayDown) {
//            layoutLayDown();
        }

//        safeBottomView();
    }

    private boolean mTouchConsumedByChild = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isSelfNeedScroll = false;
        if (!isEnabled()) {
            return true;
        }
        if (!isSwipeEnabled()) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = ev.getX();
                mInitialMotionY = ev.getY();
//                if (getOpenStatus() == Status.Close) {
//                    mTouchConsumedByChild = childNeedHandleTouchEvent(getContentViewGroup(), ev) != null;
//                } else if (getOpenStatus() == Status.Open) {
//                    mTouchConsumedByChild = childNeedHandleTouchEvent(getBottomViewGroup(), ev) != null;
//                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {

                if (getOpenStatus() == Status.Close) {
                    isSelfNeedScroll = needScrollSelf(getContentViewGroup(), ev);
                } else if (getOpenStatus() == Status.Open) {
                    isSelfNeedScroll = needScrollSelf(getBottomViewGroup(), ev);
                }
                break;
            }
        }
        if (isSelfNeedScroll) {
//            if (mTouchConsumedByChild) {
//                return false;
//            }
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    private float mInitialMotionX;
    private float mInitialMotionY;

    private boolean needScrollSelf(ViewGroup v, MotionEvent ev) {
        final float dx = ev.getX() - mInitialMotionX;
        final float dy = ev.getY() - mInitialMotionY;

        if(v==getContentViewGroup()){
            return ViewCompat.canScrollVertically(getContentViewGroup(), (int) (0 - dy));
        }else if(v==getBottomViewGroup()){
            // TODO 0 临时做法，必须的改一改
            return ViewCompat.canScrollVertically(getBottomViewGroup().getChildAt(0), (int) (0-dy));
        }
        return false ;
    }


    /**
     * if the ViewGroup children want to handle this event.
     *
     * @param v
     * @param event
     * @return
     */
    private View childNeedHandleTouchEvent(ViewGroup v, MotionEvent event) {
        if (v == null) return null;
        if (v.onTouchEvent(event)) {
            return v;
        }

        int childCount = v.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View child = v.getChildAt(i);
            if (child instanceof ViewGroup) {
                View grandChild = childNeedHandleTouchEvent((ViewGroup) child, event);
                if (grandChild != null)
                    return grandChild;
            } else {
                if (childNeedHandleTouchEvent(v.getChildAt(i), event))
                    return v.getChildAt(i);
            }
        }
        return null;
    }

    /**
     * if the view (v) wants to handle this event.
     *
     * @param v
     * @param event
     * @return
     */
    private boolean childNeedHandleTouchEvent(View v, MotionEvent event) {
        if (v == null) {
            return false;
        }

        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        int left = loc[0], top = loc[1];

        if (event.getRawX() > left && event.getRawX() < left + v.getWidth()
                && event.getRawY() > top && event.getRawY() < top + v.getHeight()) {
            return v.onTouchEvent(event);
        }

        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        if (!isSwipeEnabled()) {
            return super.onTouchEvent(event);
        }

        mDragHelper.processTouchEvent(event);

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    void layoutPullOut() {
        PullUpLayout.LayoutParams lp = (PullUpLayout.LayoutParams)(getContentViewGroup().getLayoutParams());

        Rect rect = computeContentViewGroupArea(false);
        getContentViewGroup().layout(rect.left, rect.top, rect.right, rect.bottom);

        rect = computeBottomViewGroupAreaViaContent(ShowMode.PullOut, rect);
        getBottomViewGroup().layout(rect.left, rect.top, rect.right, rect.bottom);

//        bringChildToFront(getContentViewGroup());
    }

    /**
     * a helper function to compute the Rect area that surface will hold in.
     *
     * @param open open status or close status.
     * @return
     */
    private Rect computeContentViewGroupArea(boolean open) {
        int l = getPaddingLeft();
        int t = getPaddingTop();
        if (open) {
            if (mPullEdge == PullEdge.Bottom) {
                t = getPaddingTop() - getBottomViewGroup().getMeasuredHeight();
            }
        }
        return new Rect(l, t, l + getMeasuredWidth(), t + getMeasuredHeight());
    }

    private Rect computeBottomViewGroupAreaViaContent(ShowMode mode, Rect contentRect) {
        Rect bottomRect = contentRect;
        if (mode == ShowMode.PullOut) {
            if (mPullEdge == PullEdge.Bottom) {
                bottomRect.top = contentRect.bottom;
            }
            bottomRect.right = bottomRect.left + getBottomViewGroup().getMeasuredWidth();
            bottomRect.bottom = bottomRect.top + getBottomViewGroup().getMeasuredHeight();

        }
        return bottomRect;
    }


    public ViewGroup getContentViewGroup() {
//        Log.e("MARR","getContentViewGroup:"+getChildAt(0));
        return (ViewGroup) getChildAt(0);
    }

    public ViewGroup getBottomViewGroup() {
        return (ViewGroup) getChildAt(1);
    }


    public boolean isSwipeEnabled() {
        return mPullEnabled;
    }

    public void setShowMode(ShowMode mode) {
        mShowMode = mode;
        requestLayout();
    }

    public ShowMode getShowMode() {
        return mShowMode;
    }

    public void setPullEdge(PullEdge pullEdge) {
        mPullEdge = pullEdge;
        requestLayout();
    }

    public PullEdge getPullEdge() {
        return mPullEdge;
    }

    public void setPullDistance(int dp) {
        if (dp < 0) {
            throw new IllegalArgumentException("Pull distance can not be < 0");
        }
        mPullDistance = dp2px(dp);
        requestLayout();
    }

    public Status getOpenStatus() {
        int cLeft = getContentViewGroup().getLeft();
        int cTop = getContentViewGroup().getTop();
        if (cLeft == getPaddingLeft() && cTop == getPaddingTop()) {
            return Status.Close;
        }

        if (cLeft == (getPaddingLeft() - getBottomViewGroup().getWidth()) ||
                cLeft == (getPaddingLeft() + getBottomViewGroup().getWidth()) ||
                cTop == (getPaddingTop() - getBottomViewGroup().getHeight()) ||
                cTop == (getPaddingTop() + getBottomViewGroup().getHeight())) {

            return Status.Open;
        }

        return Status.Middle;
    }

    private void processContentRelease(float xvel, float yvel) {
        if (xvel == 0 && getOpenStatus() == Status.Middle) {
            close();
        }

        if (yvel > 0) {
            close();
        }
        if (yvel < 0) {
            open();
        }
    }

    private void processBottomRelease(float xvel, float yvel) {
        if (xvel == 0 && getOpenStatus() == Status.Middle) {
            close();
        }
        if (yvel > 0) {
            close();
        }
        if (yvel < 0) {
            open();
        }
    }

    public void open() {
        Rect rect = computeContentViewGroupArea(true);
        mDragHelper.smoothSlideViewTo(getContentViewGroup(), rect.left, rect.top);
//        mDragHelper.settleCapturedViewAt(0,0);
        invalidate();
    }

    public void close() {
        Rect rect = computeContentViewGroupArea(false);
        mDragHelper.smoothSlideViewTo(getContentViewGroup(), rect.left, rect.top);
//        mDragHelper.settleCapturedViewAt(0, 0);
        invalidate();
    }


    private int dp2px(float dp) {
        return (int) (dp * getContext().getResources().getDisplayMetrics().density + 0.5f);
    }
}
