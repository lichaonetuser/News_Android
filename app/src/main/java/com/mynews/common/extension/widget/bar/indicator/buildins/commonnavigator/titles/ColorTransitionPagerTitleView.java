package com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.titles;

import android.content.Context;

import com.mynews.common.core.util.ArgbEvaluatorUtils;


/**
 * 两种颜色过渡的指示器标题
 * 博客: http://hackware.lucode.net
 */
public class ColorTransitionPagerTitleView extends SimplePagerTitleView {

    public ColorTransitionPagerTitleView(Context context) {
        super(context);
    }

    @Override
    public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {
        int color = ArgbEvaluatorUtils.INSTANCE.eval(leavePercent, mSelectedColor, mNormalColor);
        setTextColor(color);
    }

    @Override
    public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {
        int color = ArgbEvaluatorUtils.INSTANCE.eval(enterPercent, mNormalColor, mSelectedColor);
        setTextColor(color);
    }

    @Override
    public void onSelected(int index, int totalCount) {
    }

    @Override
    public void onDeselected(int index, int totalCount) {
    }
}
