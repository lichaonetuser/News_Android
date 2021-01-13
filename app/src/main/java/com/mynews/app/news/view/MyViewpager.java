package com.mynews.app.news.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 项目名称：New_Android
 * 类名：MyViewpager
 * 创建人：Heaven.li
 * 创建时间：2021/1/4
 * 备注：首页轮播
 */
public class MyViewpager extends ViewPager {

    private static final String TAG = "ViewPager";
    private boolean noScroll = true;

    public MyViewpager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyViewpager(Context context) {
        super(context);
    }

    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        Log.d("ADB","<2>==========================================================");
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        Log.d("ADB","<1>==========================================================");
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item);
    }
}
