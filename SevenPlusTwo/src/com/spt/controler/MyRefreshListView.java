package com.spt.controler;

import java.util.Date;

import com.spt.sht.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 自定义可下拉刷新的ListView<br>
 * 
 * */
public class MyRefreshListView extends ListView implements OnScrollListener {
	private final static int RELEASE_To_REFRESH = 0;// 下拉过程的状态值
	private final static int PULL_To_REFRESH = 1; // 从下拉返回到不刷新的状态值
	private final static int REFRESHING = 2;// 正在刷新的状态值
	private final static int DONE = 3;
	private final static int LOADING = 4;
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	// 上拉加载状态值
//	private final static int PULL_LOAD = 6;

	private LayoutInflater inflater;
	private LinearLayout headView;
	private TextView tvTips;
	private TextView tvLastUpdated;
	private ImageView ivArrow;
	private ProgressBar progressBar;

//	private TextView tvTipsLoad;
//	private ProgressBar progressBarLoad;
//	private int firstVisibleItem;
//	private int scrollState;
//	private boolean isLoadFull;

	// 定义头部下拉刷新的布局的高度
	private int headContentHeight;

	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	private int startY;
	private int state;
	private boolean isBack;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;
	private MyOnRefreshListener refreshListener;
	private boolean isRefreshable;

//	private boolean isLoadable;
//	private View footerView;

	public MyRefreshListView(Context context) {
		super(context);
		init(context);
	}

	public MyRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(0); // 设置重绘view滚动时的背景色，0时取消背景色
		inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.lv_header, null);
		tvTips = (TextView) headView.findViewById(R.id.tv_tips);
		tvLastUpdated = (TextView) headView.findViewById(R.id.tv_lastUpdated);
		ivArrow = (ImageView) headView.findViewById(R.id.iv_Arrow);
		progressBar = (ProgressBar) headView.findViewById(R.id.pb_progressBar);

//		footerView = (LinearLayout) inflater.inflate(R.layout.lv_footer, null);
//		tvTipsLoad = (TextView) footerView.findViewById(R.id.tv_load_tips);
//		progressBarLoad = (ProgressBar) footerView.findViewById(R.id.pb_load_progressBar);

		// 设置下拉刷新图标的最小高度和宽度
		ivArrow.setMinimumWidth(70);
		ivArrow.setMinimumHeight(50);
		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		// 设置内边距，正好距离顶部为一个负的整个布局的高度，正好把头部隐藏
		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		// 重绘一下
		headView.invalidate();
		// 将下拉刷新的布局加入ListView的顶部
		addHeaderView(headView, null, false);
//		addFooterView(footerView, null, false);
		// 设置滚动监听事件
		setOnScrollListener(this);

		// 设置旋转动画事件
		animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		// 一开始的状态就是下拉刷新完的状态，所以为DONE
		state = DONE;
		// 是否正在刷新
		isRefreshable = false;

	}

	private void measureView(View view) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int viewWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);
		int lpHeight = params.height;
		int viewHeightSpec;
		if (lpHeight > 0) {
			viewHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			viewHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}

		view.measure(viewWidthSpec, viewHeightSpec);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		this.scrollState = scrollState;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem == 0) {
			isRefreshable = true;
		} else {
			isRefreshable = false;
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isRefreshable) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!isRecored) {
					isRecored = true;
					startY = (int) ev.getY();// 手指按下时记录当前位置
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeheadViewByState();
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeheadViewByState();
						onLvRefresh();
					}
				}
				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				if (!isRecored) {
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight)// 由松开刷新状态转变到下拉刷新状态
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeheadViewByState();
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {// 由松开刷新状态转变到done状态
							state = DONE;
							changeheadViewByState();
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {
						setSelection(0);
						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {// 由done或者下拉刷新状态转变到松开刷新
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeheadViewByState();
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {// 由DOne或者下拉刷新状态转变到done状态
							state = DONE;
							changeheadViewByState();
						}
					}
					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeheadViewByState();
						}
					}
					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight + (tempY - startY) / RATIO, 0, 0);

					}
					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO - headContentHeight, 0, 0);
					}

				}
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);

	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeheadViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			ivArrow.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tvTips.setVisibility(View.VISIBLE);
			tvLastUpdated.setVisibility(View.VISIBLE);
			ivArrow.clearAnimation();// 清除动画
			ivArrow.startAnimation(animation);// 开始动画效果

			tvTips.setText("松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tvTips.setVisibility(View.VISIBLE);
			tvLastUpdated.setVisibility(View.VISIBLE);
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				ivArrow.clearAnimation();
				ivArrow.startAnimation(reverseAnimation);

				tvTips.setText("下拉刷新");
			} else {
				tvTips.setText("下拉刷新");
			}
			break;

		case REFRESHING:
			headView.setPadding(0, 0, 0, 0);
			progressBar.setVisibility(View.VISIBLE);
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.GONE);
			tvTips.setText("正在刷新...");
			tvLastUpdated.setVisibility(View.VISIBLE);
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			progressBar.setVisibility(View.GONE);
			ivArrow.clearAnimation();
			ivArrow.setImageResource(R.drawable.fresharrow);
			tvTips.setText("下拉刷新");
			tvLastUpdated.setVisibility(View.VISIBLE);
			break;
		}
	}

	public void setonMyRefreshListener(MyOnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface MyOnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		tvLastUpdated.setText("最近更新:" + new Date().toLocaleString());
		changeHeaderViewByState();
	}

	private void onLvRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	public void setAdapter(BaseAdapter adapter) {
		tvLastUpdated.setText("最近更新:" + new Date().toLocaleString());
		super.setAdapter(adapter);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			ivArrow.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tvTips.setVisibility(View.VISIBLE);
			tvLastUpdated.setVisibility(View.VISIBLE);

			ivArrow.clearAnimation();// 清除动画
			ivArrow.startAnimation(animation);// 开始动画效果

			tvTips.setText("松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tvTips.setVisibility(View.VISIBLE);
			tvLastUpdated.setVisibility(View.VISIBLE);
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				ivArrow.clearAnimation();
				ivArrow.startAnimation(reverseAnimation);

				tvTips.setText("下拉刷新");
			} else {
				tvTips.setText("下拉刷新");
			}
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.GONE);
			tvTips.setText("正在刷新...");
			tvLastUpdated.setVisibility(View.VISIBLE);
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			ivArrow.clearAnimation();
			ivArrow.setImageResource(R.drawable.fresharrow);
			tvTips.setText("下拉刷新");
			tvLastUpdated.setVisibility(View.VISIBLE);
			break;
		}
	}

}
