package com.box.common.core.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class CoreHorizontalScrollView extends HorizontalScrollView {

    private OnScrollListener onScrollListener;

    public CoreHorizontalScrollView(Context context) {
        this(context,null);
    }

    public CoreHorizontalScrollView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CoreHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollListener != null) {
            onScrollListener.onScroll(l, t, oldl, oldt);
        }
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener{
        void onScroll(int l, int t, int ol, int ot);
    }
}
