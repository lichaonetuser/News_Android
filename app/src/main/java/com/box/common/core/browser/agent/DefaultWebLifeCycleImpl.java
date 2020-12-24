package com.box.common.core.browser.agent;

import android.os.Build;
import android.webkit.WebView;


public class DefaultWebLifeCycleImpl implements WebLifeCycle {
    private WebView mWebView;

    DefaultWebLifeCycleImpl(WebView webView) {
        this.mWebView = webView;
    }

    @Override
    public void onResume() {
        if (this.mWebView != null) {

            if (Build.VERSION.SDK_INT >= 11){
                this.mWebView.onResume();
            }
            this.mWebView.resumeTimers();
        }


    }

    @Override
    public void onPause(boolean  pauseTimer) {

        if (this.mWebView != null) {

            if (Build.VERSION.SDK_INT >= 11) {
                this.mWebView.onPause();
            }
            if (pauseTimer) {
                this.mWebView.pauseTimers();
            }
        }
    }

    @Override
    public void onDestroy() {

        if(this.mWebView!=null){
            this.mWebView.resumeTimers();
        }
        AgentWebUtils.clearWebView(this.mWebView);

    }
}
