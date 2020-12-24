package com.box.app.news.widget;

import android.view.View;
import android.webkit.WebView;

import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter;

public class WebViewOverScrollDecorAdapter implements IOverScrollDecoratorAdapter {

    protected final WebView mWebView;

    public WebViewOverScrollDecorAdapter(WebView view) {
        mWebView = view;
    }

    @Override
    public View getView() {
        return mWebView;
    }

    @Override
    public boolean isInAbsoluteStart() {
        return !mWebView.canScrollVertically(-1);
    }

    @Override
    public boolean isInAbsoluteEnd() {
        return !mWebView.canScrollVertically(1);
    }
}