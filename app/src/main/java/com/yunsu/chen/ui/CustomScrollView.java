package com.yunsu.chen.ui;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yunsu.chen.R;

public class CustomScrollView extends ScrollView {

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private ViewGroup parentView = null;
	private LinearLayout headView = null;
	private LinearLayout footView = null;
	private TextView tipsTextview;// 下拉刷新
	private TextView lastUpdatedTextView;// 最新更新
	private ImageView arrowImageView;// 箭头
	private ProgressBar progressBar;// 刷新进度条
	private ProgressBar moreProgressBar;
	private TextView loadMoreView;

	private RotateAnimation animation;// 旋转特效 刷新中箭头翻转 向下变向上
	private RotateAnimation reverseAnimation; // 箭头向上变向下

	private int headContentHeight;// 头部高度
	private boolean isRecored;
	private int startY;// 高度起始位置，用来记录与头部距离

	private int state;// 下拉刷新中、松开刷新中、正在刷新中、完成刷新

	private boolean isBack;
	private boolean isSeeHead;
	private boolean isScroll; // 判断是否有滑动

	private final static int RATIO = 3; // 实际的padding的距离与界面上偏移距离的比例

	private OnRefreshListener refreshListener;
	private OnLoadListener loadListener;

	public CustomScrollView(Context context) {
		super(context);
		initView(context);
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		headView = (LinearLayout) inflater.inflate(R.layout.header, null);
		footView = (LinearLayout) inflater.inflate(R.layout.listfooter_more,
				null);

		arrowImageView = (ImageView) headView
				.findViewById(R.id.head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(R.id.head_progressBar);
		tipsTextview = (TextView) headView.findViewById(R.id.head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(R.id.head_lastUpdatedTextView);
		moreProgressBar = (ProgressBar) footView
				.findViewById(R.id.pull_to_refresh_progress);
		loadMoreView = (TextView) footView.findViewById(R.id.load_more);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRecored = false;
		isSeeHead = false;
		isScroll = false;
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if (parentView == null) {
			parentView = (ViewGroup) this.getChildAt(0);
			headView.setPadding(0, -1 * headContentHeight, 0, 0);
			headView.invalidate();
			parentView.addView(headView, 0);
			parentView.addView(footView);
		}
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (this.getScrollY() == 0 && !isRecored) {
					startY = (int) ev.getY();
					isRecored = true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				if (!isScroll)
					isScroll = true;
				if (!isRecored && this.getScrollY() == 0) {
					isRecored = true;
					startY = tempY;
				}
				if (tempY - startY < 0) {
					startY = tempY;
				}
				if (state != REFRESHING && state != LOADING && isRecored) {
					if (state == RELEASE_To_REFRESH) {
						if ((tempY - startY) > 0
								&& (tempY - startY) / RATIO < headContentHeight + 10) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						} else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
						}
					}
					if (state == PULL_To_REFRESH) {
						if ((tempY - startY) / RATIO >= headContentHeight + 10) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
						} else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();
						}
					}
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
							isSeeHead = true;
						}
					}
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
						headView.invalidate();
					} else if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);
						headView.invalidate();
					}
				}

				break;
			case MotionEvent.ACTION_UP:
				if (state == PULL_To_REFRESH) {
					state = DONE;
					changeHeaderViewByState();
				}
				if (state == RELEASE_To_REFRESH) {
					state = REFRESHING;
					changeHeaderViewByState();
					onRefresh();
				}
				isRecored = false;
				isSeeHead = false;
				isBack = false;

				if (parentView != null) {
					int viewHeight = parentView.getHeight();
					int y = this.getScrollY();
					if ((viewHeight - y - this.getHeight()) == 0 && isScroll
							&& state != REFRESHING && state != LOADING) {
						onLoad();
					}
				}
				isScroll = false;
				break;
		}

		if (isSeeHead) {
			return true;
		} else {
			return super.onTouchEvent(ev);
		}
	}

	private void changeHeaderViewByState() {
		switch (state) {
			case RELEASE_To_REFRESH:
				arrowImageView.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(animation);
				tipsTextview.setText("松开刷新");
				break;
			case PULL_To_REFRESH:
				progressBar.setVisibility(View.GONE);
				tipsTextview.setVisibility(View.VISIBLE);
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.VISIBLE);
				// 是由RELEASE_To_REFRESH状态转变来的
				if (isBack) {
					isBack = false;
					arrowImageView.clearAnimation();
					arrowImageView.startAnimation(reverseAnimation);
				}
				tipsTextview.setText("下拉刷新");
				break;

			case REFRESHING:
				headView.setPadding(0, 0, 0, 0);
				progressBar.setVisibility(View.VISIBLE);
				arrowImageView.clearAnimation();
				arrowImageView.setVisibility(View.GONE);
				tipsTextview.setText("正在刷新...");
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				break;
			case DONE:
				headView.setPadding(0, -1 * headContentHeight, 0, 0);
				progressBar.setVisibility(View.GONE);
				arrowImageView.clearAnimation();
				arrowImageView.setImageResource(R.drawable.arrow);
				tipsTextview.setText("下拉刷新");
				lastUpdatedTextView.setVisibility(View.VISIBLE);
				isRecored = false;
				break;
		}
	}

	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	public void onRefreshComplete() {
		state = DONE;
		lastUpdatedTextView.setText("上次刷新:" + new Date().toLocaleString());
		changeHeaderViewByState();
	}

	public void setOnLoadListener(OnLoadListener loadListener) {
		this.loadListener = loadListener;
	}

	public interface OnLoadListener {
		public void onLoad();
	}

	private void onLoad() {
		if (loadListener != null) {
			state = LOADING;
			footView.setVisibility(View.VISIBLE);
			moreProgressBar.setVisibility(View.VISIBLE);
			loadMoreView.setText("正在加载更多...");
			loadListener.onLoad();
		}
	}

	public void onLoadComplete() {
		state = DONE;
		moreProgressBar.setVisibility(View.GONE);
		loadMoreView.setText("查看更多");
		footView.setVisibility(View.INVISIBLE);
	}

}