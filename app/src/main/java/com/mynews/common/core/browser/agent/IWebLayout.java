package com.mynews.common.core.browser.agent;

import androidx.annotation.NonNull;
import android.view.ViewGroup;
import android.webkit.WebView;


public interface IWebLayout<T extends WebView,V extends ViewGroup> {


    /**
     *  WebView 的根层父控件
     */
    @NonNull
    V getRootLayout(WebView view);

    /**
     *
     * @return WebView 的直接父控件
     */
    @NonNull
    V getLayout(WebView view);

    int getWebIndex();

    ViewGroup.LayoutParams getWebLayoutParams();
}
