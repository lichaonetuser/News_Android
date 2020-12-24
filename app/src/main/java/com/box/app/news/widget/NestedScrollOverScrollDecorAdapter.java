package com.box.app.news.widget;

import androidx.core.widget.NestedScrollView;
import android.view.View;

import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;

public class NestedScrollOverScrollDecorAdapter implements IOverScrollDecoratorAdapter {

    protected final NestedScrollView mNestedScrollView;

    public NestedScrollOverScrollDecorAdapter(NestedScrollView view) {
        mNestedScrollView = view;
    }

    @Override
    public View getView() {
        return mNestedScrollView;
    }

    @Override
    public boolean isInAbsoluteStart() {
        return !mNestedScrollView.canScrollVertically(-1);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return !mNestedScrollView.canScrollVertically(1);
    }
}