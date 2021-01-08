package com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.titles;

import android.content.Context;
import android.graphics.Paint;


/**
 * 带颜色渐变和缩放的指示器标题
 * 博客: http://hackware.lucode.net
 */
public class ScaleTransitionPagerTitleView extends ColorTransitionPagerTitleView {
    private float mMinScale = 0.75f;

    private boolean isRedDotShowed = false;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public boolean isRedDotShowed() {
        return isRedDotShowed;
    }

    public void setRedDotShowed(boolean redDotShowed) {
        isRedDotShowed = redDotShowed;
    }

    public ScaleTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        super.onEnter(index, totalCount, enterPercent, leftToRight);    // 实现颜色渐变
        setScaleX(mMinScale + (1.0f - mMinScale) * enterPercent);
        setScaleY(mMinScale + (1.0f - mMinScale) * enterPercent);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        super.onLeave(index, totalCount, leavePercent, leftToRight);    // 实现颜色渐变
        setScaleX(1.0f + (mMinScale - 1.0f) * leavePercent);
        setScaleY(1.0f + (mMinScale - 1.0f) * leavePercent);
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
    }

}
