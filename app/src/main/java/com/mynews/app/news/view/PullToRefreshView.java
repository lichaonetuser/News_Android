package com.mynews.app.news.view;

import android.animation.IntArrayEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mynews.app.news.R;

import java.util.Date;

/**
 * 项目名称：New_Android
 * 类名：PullToRefreshView
 * 创建人：Heaven.li
 * 创建时间：2021/1/4
 * 备注：首页上拉和下拉
 */
public class PullToRefreshView extends LinearLayout {

    private static final String TAG = "PullToRefreshView";
    private Context mContext;
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;
    private static final int PULL_UP_STATE = 0;
    private static final int PULL_DOWN_STATE = 1;
    private boolean enablePullTorefresh = false;
    private boolean enablePullLoadMoreDataStatus = true;
    private int mLastMotionY;
    private int mLastMotionX;
    private boolean mLock;
    private AdapterView<?> mAdapterView;
    private ScrollView mScrollView;
    private View mHeaderView;
    private View mFooterView;
    private int mHeaderViewHeight;
    private int mFooterViewHeight;
    private int mHeaderViewWidth;
    private ImageView mHeaderImageView;
    private ImageView mFooterImageView;
    private TextView mHeaderTextView;
    private TextView mFooterTextView;
    private TextView mHeaderUpdateTextView;
    private ProgressBar mHeaderProgressBar;
    private ProgressBar mFooterProgressBar;
    private LayoutInflater mInflater;
    private int mHeaderState;
    private int mFooterState;
    private int mPullState;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private OnFooterRefreshListener mOnFooterRefreshListener;
    private OnHeaderRefreshListener mOnHeaderRefreshListener;
    private OnItemLeft mOnItemLeft;
    private OnItemRight mOnItemRight;
    private int pullAnimRes[];

    public PullToRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PullToRefreshView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        // Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mInflater = LayoutInflater.from(getContext());

        // header view 在此添加,保证是第一个添加到linearlayout的最上端
        addHeaderView();
        pullAnimRes = getAnimRes(R.array.frame_anim_news_refresh_pull);
    }


    private int[] getAnimRes(int arrayResId) {
        TypedArray typedArray = mContext.getResources().obtainTypedArray(arrayResId);
        int len = typedArray.length();
        int resId[] = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }

    private void addHeaderView() {
        // header view
        mHeaderView = mInflater.inflate(R.layout.view_pull_head, this, false);
        mHeaderImageView = (ImageView) mHeaderView.findViewById(R.id.pull_to_load_image);
        mHeaderTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_load_text);
        mHeaderUpdateTextView = (TextView) mHeaderView.findViewById(R.id.pull_to_refresh_updated_at);
        mHeaderProgressBar = (ProgressBar) mHeaderView.findViewById(R.id.pull_to_load_progress);
        // header layout
        measureView(mHeaderView);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderViewWidth = mHeaderView.getMeasuredWidth()/2;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mHeaderViewHeight);
        // 设置topMargin的值为负的header View高度,即将其隐藏在最上方
        params.topMargin = -(mHeaderViewHeight);
        // mHeaderView.setLayoutParams(params1);
        addView(mHeaderView, params);

    }

    private void addFooterView() {
        // footer view
        mFooterView = mInflater.inflate(R.layout.view_pull_foot, this, false);
        mFooterImageView = (ImageView) mFooterView.findViewById(R.id.pull_foot_image);
        mFooterTextView = (TextView) mFooterView.findViewById(R.id.pull_foot_text);
        mFooterProgressBar = (ProgressBar) mFooterView.findViewById(R.id.pull_fot_progress);
        // footer layout
        measureView(mFooterView);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        mFooterView.getMeasuredWidth();
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, mFooterViewHeight);
        // int top = getHeight();
        // params.topMargin
        // =getHeight();//在这里getHeight()==0,但在onInterceptTouchEvent()方法里getHeight()已经有值了,不再是0;
        // getHeight()什么时候会赋值,稍候再研究一下
        // 由于是线性布局可以直接添加,只要AdapterView的高度是MATCH_PARENT,那么footer view就会被添加到最后,并隐藏
        addView(mFooterView, params);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // footer view 在此添加保证添加到linearlayout中的最后
        addFooterView();
        initContentAdapterView();
    }

    private void initContentAdapterView() {
        int count = getChildCount();
        if (count < 3) {
            throw new IllegalArgumentException("this layout must contain 3 child views,and AdapterView or ScrollView must in the second position!");
        }
        View view = null;
        for (int i = 0; i < count - 1; ++i) {
            view = getChildAt(i);
            if (view instanceof AdapterView<?>) {
                mAdapterView = (AdapterView<?>) view;
            }
            if (view instanceof ScrollView) {
                // finish later
                mScrollView = (ScrollView) view;
            }
        }
        if (mAdapterView == null && mScrollView == null) {
            throw new IllegalArgumentException("must contain a AdapterView or ScrollView in this layout!");
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch(ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                getParent().requestDisallowInterceptTouchEvent(true);
//                //todo 记录点击初始位置
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (isTouch) {
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                } else {
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    int deltaY = 0;
    int deltaX = 0;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        int x = (int) e.getRawX();
        Log.d("ADB","<1>PullToRefreshView");
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                deltaY = y - mLastMotionY;
                deltaX = x-mLastMotionX;
                Log.d("ADB","<2>PullToRefreshView");
                if (deltaY < deltaX && isRefreshViewScroll(deltaY)) {
                    Log.d("ADB","<3>PullToRefreshView = true");
                    mLock = false;
                    return true;
                }
                if (deltaX > 200){
                    mLastMotionX = x;
                    mOnItemRight.onItemRight();
                    Log.d("ADB","<3>PullToRefreshView = 左 = "+mHeaderViewWidth);
                }else if (deltaX < -200){
                    mLastMotionX = x;
                    mOnItemLeft.onItemLeft();
                    Log.d("ADB","<3>PullToRefreshView = 右 = "+mHeaderViewWidth);
                }
                Log.d("ADB","<4>PullToRefreshView X= "+(deltaX));
                Log.d("ADB","<4>PullToRefreshView Y= "+(deltaY));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        mLock = true;
        return false;
    }

    /*
     * 如果在onInterceptTouchEvent()方法中没有拦截(即onInterceptTouchEvent()方法中 return
     * false)则由PullToRefreshView 的子View来处理;否则由下面的方法来处理(即由PullToRefreshView自己来处理)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("ADB","<1>onTouchEvent");
        if (mLock) {
            Log.d("ADB","<2>onTouchEvent = true");
            return true;
        }
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // onInterceptTouchEvent已经记录
                // mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastMotionY;
                Log.d("ADB","<2>onTouchEvent Y= "+ y);
                Log.d("ADB","<2>onTouchEvent = mLastMotionY"+ mLastMotionY);
                Log.d("ADB","<2>onTouchEvent = deltaY"+ deltaY);
                if (mPullState == PULL_DOWN_STATE) {
                    // PullToRefreshView执行下拉
                    Log.i("ADB", " pull down!parent view move!");
                    headerPrepareToRefresh(deltaY);
                    // setHeaderPadding(-mHeaderViewHeight);
                } else if (mPullState == PULL_UP_STATE) {
                    // PullToRefreshView执行上拉
                    Log.i("ADB", "pull up!parent view move!");
                    footerPrepareToRefresh(deltaY);
                }
                mLastMotionY = y;
                Log.d("ADB","<2>onTouchEvent");
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d("ADB","<4>onTouchEvent");
                int topMargin = getHeaderTopMargin();
                if (mPullState == PULL_DOWN_STATE) {
                    if (topMargin >= 0) {
                        // 开始刷新
                        headerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-mHeaderViewHeight);
                    }
                } else if (mPullState == PULL_UP_STATE) {
                    if (Math.abs(topMargin) >= mHeaderViewHeight + mFooterViewHeight) {
                        // 开始执行footer 刷新
                        footerRefreshing();
                    } else {
                        // 还没有执行刷新，重新隐藏
                        setHeaderTopMargin(-mHeaderViewHeight);
                    }
                }
                break;
        }
        Log.d("ADB","<5>onTouchEvent");
        return super.onTouchEvent(event);
    }

    /**
     * 是否应该到了父View,即PullToRefreshView滑动
     *
     * @param deltaY
     *            , deltaY > 0 是向下运动,< 0是向上运动
     * @return
     */
    private boolean isRefreshViewScroll(int deltaY) {
        Log.d("ADB","<5>onTouchEvent");
        if (mHeaderState == REFRESHING || mFooterState == REFRESHING) {
            Log.d("ADB","<6>onTouchEvent");
            return false;
        }
        // 对于ScrollView
        if (mScrollView != null) {
            Log.d("ADB","<7>onTouchEvent");
            if ((deltaY < 0)){
                if (!enablePullLoadMoreDataStatus){
                    return false;
                }else if (deltaY > -30){
                    return false;
                }
            }
            if ((deltaY > 0)){
                if (!enablePullTorefresh){
                    return false;
                }else if (deltaY < 30) {
                    return false;
                }
            }
            Log.d("ADB","<8>onTouchEvent");
            // 子scroll view滑动到最顶端
            View child = mScrollView.getChildAt(0);
            if (deltaY > 0 && mScrollView.getScrollY() == 0) {
                mPullState = PULL_DOWN_STATE;
                return true;
            } else if (deltaY < 0 && child.getMeasuredHeight() <= getHeight() + mScrollView.getScrollY()) {
                mPullState = PULL_UP_STATE;
                return true;
            }
        }
        return false;
    }

    /**
     * header 准备刷新,手指移动过程,还没有释放
     *
     * @param deltaY
     *            ,手指滑动的距离
     */
    private void headerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 当header view的topMargin>=0时，说明已经完全显示出来了,修改header view 的提示状态
        if (newTopMargin >= 0 && mHeaderState != RELEASE_TO_REFRESH) {
            mHeaderTextView.setText("松手可刷新");
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderImageView.clearAnimation();
            mHeaderImageView.startAnimation(mFlipAnimation);
            mHeaderState = RELEASE_TO_REFRESH;
        } else if (newTopMargin < 0 && newTopMargin > -mHeaderViewHeight) {// 拖动时没有释放
            mHeaderImageView.clearAnimation();
            mHeaderImageView.startAnimation(mFlipAnimation);
            // mHeaderImageView.
            mHeaderTextView.setText("下拉刷新");
            mHeaderState = PULL_TO_REFRESH;
        }
    }

    /**
     * footer 准备刷新,手指移动过程,还没有释放 移动footer view高度同样和移动header view
     * 高度是一样，都是通过修改header view的topmargin的值来达到
     *
     * @param deltaY
     *            ,手指滑动的距离
     */
    private void footerPrepareToRefresh(int deltaY) {
        int newTopMargin = changingHeaderViewTopMargin(deltaY);
        // 如果header view topMargin 的绝对值大于或等于header + footer 的高度
        // 说明footer view 完全显示出来了，修改footer view 的提示状态
        Log.i(TAG, "移动的距离"+deltaY+"======="+(mHeaderViewHeight + mFooterViewHeight) +"-----------------------"+mFooterState);
        if (Math.abs(newTopMargin) >= (mHeaderViewHeight + mFooterViewHeight) && mFooterState != RELEASE_TO_REFRESH) {
            Log.i(TAG, "松手可加载更多");
            mFooterTextView.setText("松手可加载更多");
//            mFooterImageView.clearAnimation();
//            mFooterImageView.startAnimation(mFlipAnimation);
            mFooterImageView.post(new Runnable() {
                @Override
                public void run() {
                    mFooterImageView.setBackgroundResource(pullAnimRes[0]);
                }
            });
            mFooterState = RELEASE_TO_REFRESH;
        } else if (Math.abs(newTopMargin) < (mHeaderViewHeight + mFooterViewHeight)) {
            Log.i(TAG, "上拉可加载更多");
//            mFooterImageView.clearAnimation();
//            mFooterImageView.startAnimation(mFlipAnimation);
            mFooterImageView.post(new Runnable() {
                @Override
                public void run() {
                    int index = Math.abs(deltaY);
                    if (index < pullAnimRes.length){
                        mFooterImageView.setBackgroundResource(pullAnimRes[index]);
                    }
                }
            });
            mFooterTextView.setText("上拉可加载更多");
            mFooterState = PULL_TO_REFRESH;
        }
    }

    /**
     * 修改Header view top margin的值
     *
     * @description
     * @param deltaY
     */
    private int changingHeaderViewTopMargin(int deltaY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        float newTopMargin = params.topMargin + deltaY * 0.3f;
        // 这里对上拉做一下限制,因为当前上拉后然后不释放手指直接下拉,会把下拉刷新给触发了,感谢网友yufengzungzhe的指出
        // 表示如果是在上拉后一段距离,然后直接下拉
        if (deltaY > 0 && mPullState == PULL_UP_STATE && Math.abs(params.topMargin) <= mHeaderViewHeight) {
            return params.topMargin;
        }
        // 同样地,对下拉做一下限制,避免出现跟上拉操作时一样的bug
        if (deltaY < 0 && mPullState == PULL_DOWN_STATE && Math.abs(params.topMargin) >= mHeaderViewHeight) {
            return params.topMargin;
        }
        params.topMargin = (int) newTopMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
        return params.topMargin;
    }

    /**
     * header refreshing
     */
    public void headerRefreshing() {
        mHeaderState = REFRESHING;
        setHeaderTopMargin(0);
        mHeaderImageView.setVisibility(View.GONE);
        mHeaderImageView.clearAnimation();
        mHeaderProgressBar.setVisibility(View.VISIBLE);
        mHeaderTextView.setText("正在刷新");
        if (mOnHeaderRefreshListener != null) {
            mOnHeaderRefreshListener.onHeaderRefresh(this);
        }
    }

    /**
     * footer refreshing
     */
    private void footerRefreshing() {
        mFooterState = REFRESHING;
        int top = mHeaderViewHeight + mFooterViewHeight;
        setHeaderTopMargin(-top);
        mFooterImageView.setVisibility(View.GONE);
        mFooterImageView.clearAnimation();
        mFooterProgressBar.setVisibility(View.VISIBLE);
        mFooterTextView.setText("正在加载。。");
        if (mOnFooterRefreshListener != null) {
            mOnFooterRefreshListener.onFooterRefresh(this);
        }
    }

    /**
     * 设置header view 的topMargin的值
     *
     * @description
     * @param topMargin
     *            ，为0时，说明header view 刚好完全显示出来； 为-mHeaderViewHeight时，说明完全隐藏了
     */
    private void setHeaderTopMargin(int topMargin) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        params.topMargin = topMargin;
        mHeaderView.setLayoutParams(params);
        invalidate();
    }

    /**
     * header view 完成更新后恢复初始状态
     */
    public void onHeaderRefreshComplete() {
        setHeaderTopMargin(-mHeaderViewHeight);
        mHeaderImageView.setVisibility(View.VISIBLE);
        mHeaderTextView.setText("下拉刷新");
        mHeaderProgressBar.setVisibility(View.GONE);
        mHeaderState = PULL_TO_REFRESH;
        setLastUpdated("最近更新:" + new Date().toLocaleString());
    }

    /**
     * Resets the list to a normal state after a refresh.
     *
     * @param lastUpdated
     *            Last updated at.
     */
    public void onHeaderRefreshComplete(CharSequence lastUpdated) {
        setLastUpdated(lastUpdated);
        onHeaderRefreshComplete();
    }

    /**
     * footer view 完成更新后恢复初始状态
     */
    public void onFooterRefreshComplete() {
        setHeaderTopMargin(-mHeaderViewHeight);
        mFooterImageView.setVisibility(View.VISIBLE);
        mFooterTextView.setText("加载完成，点击可加载更多");
        mFooterProgressBar.setVisibility(View.GONE);
        // mHeaderUpdateTextView.setText("");
        mFooterState = PULL_TO_REFRESH;
    }

    /**
     * footer view 完成更新后恢复初始状态
     */
    public void onFooterRefreshComplete(int size) {
        if (size > 0) {
            mFooterView.setVisibility(View.VISIBLE);
        } else {
            mFooterView.setVisibility(View.GONE);
        }
        setHeaderTopMargin(-mHeaderViewHeight);
        mFooterImageView.setVisibility(View.VISIBLE);
        mFooterImageView.setImageResource(R.mipmap.ic_launcher);
        mFooterTextView.setText("上拉可加载更多");
        mFooterProgressBar.setVisibility(View.GONE);
        // mHeaderUpdateTextView.setText("");
        mFooterState = PULL_TO_REFRESH;
    }

    /**
     * Set a text to represent when the list was last updated.
     *
     * @param lastUpdated
     *            Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mHeaderUpdateTextView.setVisibility(View.VISIBLE);
            mHeaderUpdateTextView.setText(lastUpdated);
        } else {
            mHeaderUpdateTextView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取当前header view 的topMargin
     *
     * @description
     */
    private int getHeaderTopMargin() {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        return params.topMargin;
    }

    /**
     * set headerRefreshListener
     *
     * @description
     * @param headerRefreshListener
     */

    public void setOnHeaderRefreshListener(OnHeaderRefreshListener headerRefreshListener) {
        mOnHeaderRefreshListener = headerRefreshListener;
    }

    public void setOnFooterRefreshListener(OnFooterRefreshListener footerRefreshListener) {
        mOnFooterRefreshListener = footerRefreshListener;
    }

    /**
     * Interface definition for a callback to be invoked when list/grid footer
     * view should be refreshed.
     */
    public interface OnFooterRefreshListener {
        public void onFooterRefresh(PullToRefreshView view);
    }

    /**
     * Interface definition for a callback to be invoked when list/grid header
     * view should be refreshed.
     */
    public interface OnHeaderRefreshListener {
        public void onHeaderRefresh(PullToRefreshView view);
    }

    public boolean isEnablePullTorefresh() {
        return enablePullTorefresh;
    }

    public void setEnablePullTorefresh(boolean enablePullTorefresh) {
        handler.sendEmptyMessage(0);
        System.out.println("setEnablePullTorefresh------------------------下拉刷新结束");


        this.enablePullTorefresh = enablePullTorefresh;
    }

    public boolean isEnablePullLoadMoreDataStatus() {
        return enablePullLoadMoreDataStatus;
    }

    public void setEnablePullLoadMoreDataStatus(boolean enablePullLoadMoreDataStatus) {
        handler.sendEmptyMessage(1);
        this.enablePullLoadMoreDataStatus = enablePullLoadMoreDataStatus;
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                setHeaderTopMargin(-mHeaderViewHeight);
                mHeaderState = PULL_TO_REFRESH;
                mHeaderProgressBar.setVisibility(View.GONE);
            }else if(msg.what==1){
                mFooterProgressBar.setVisibility(View.GONE);

                onFooterRefreshComplete(1);
            }
        }
    };


    public void setOnItemLeft(OnItemLeft onItemLeft) {
        mOnItemLeft = onItemLeft;
    }

    public void setOnItemRight(OnItemRight onItemRight) {
        mOnItemRight = onItemRight;
    }

    public interface OnItemLeft {
        public void onItemLeft();
    }

    public interface OnItemRight {
        public void onItemRight();
    }
}
