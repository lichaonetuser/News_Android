package com.mynews.app.news.util;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.mynews.app.news.R;

/**
 * 项目名称：New_Android
 * 类名：LoopTransformer
 * 创建人：Heaven.li
 * 创建时间：2020/12/26
 * 备注：
 */
public class LoopTransformer implements ViewPager.PageTransformer {

    private static final float MIN_SCALE = 0.9f;
    TextView mTextView;

    @Override
    public void transformPage(View page, float position) {
        if (mTextView != page.findViewById(R.id.tv_home_cardview)){
            mTextView = page.findViewById(R.id.tv_home_cardview);
        }
        float alpha = 0.0f;
        /**
         * 过滤那些 <-1 或 >1 的值，使它区于【-1，1】之间
         */
        if (position < -1) {
            position = -1;
        } else if (position > 1) {
            position = 1;
        }
        if (0.0f <= position && position <= 1.0f) {
            alpha = 1.0f - position;
        } else if (-1.0f <= position && position < 0.0f) {
            alpha = position + 1.0f;
        }
        /**
         * 判断是前一页 1 + position ，右滑 pos -> -1 变 0
         * 判断是后一页 1 - position ，左滑 pos -> 1 变 0
         */
        float tempScale = position < 0 ? 1 + position : 1 - position; // [0,1]
        float scaleValue = MIN_SCALE + tempScale * 0.1f; // [0,1]
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);
        mTextView.setAlpha(alpha);
    }
}
