package com.mynews.common.core.browser.agent;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public abstract class BaseAgentWebFragment extends Fragment {

    protected AgentWeb mAgentWeb;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(getAgentWebParent(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))//
                .setIndicatorColorWithHeight(getIndicatorColor(), getIndicatorHeight())//
                .setWebView(getWebView())
                .setWebLayout(getWebLayout())
                .setAgentWebWebSettings(getAgentWebSettings())//
                .setWebViewClient(getWebViewClient())
                .setPermissionInterceptor(getPermissionInterceptor())
                .setWebChromeClient(getWebChromeClient())
                .setReceivedTitleCallback(getReceivedTitleCallback())
                .setSecurityType(AgentWeb.SecurityType.strict)
                .addDownLoadResultListener(getDownLoadResultListener())
                .createAgentWeb()//
                .ready()//
                .go(getUrl());


    }




    protected void setTitle(WebView view, String title){

    }

    @Override
    public void onPause() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onPause(true);
        super.onPause();

    }

    @Override
    public void onResume() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    private @Nullable
    ChromeClientCallbackManager.ReceivedTitleCallback getReceivedTitleCallback() {
        return new ChromeClientCallbackManager.ReceivedTitleCallback() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                setTitle( view,title);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAgentWeb != null)
            mAgentWeb.uploadFileResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected @Nullable
    String getUrl() {
        return "https://github.com/Justson/AgentWeb";
    }
    @Override
    public void onDestroy() {
        if (mAgentWeb != null)
            mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    protected  @Nullable
    AgentWebSettings getAgentWebSettings() {
        return WebDefaultSettingsManager.getInstance();
    }

    protected @Nullable
    DownLoadResultListener getDownLoadResultListener() {
        return null;
    }

    protected @Nullable
    WebChromeClient getWebChromeClient() {
        return null;
    }

    protected abstract @NonNull
    ViewGroup getAgentWebParent();
    protected @ColorInt
    int getIndicatorColor() {
        return -1;
    }

    protected int getIndicatorHeight() {
        return -1;
    }

    protected @Nullable
    WebViewClient getWebViewClient() {
        return null;
    }

    protected @Nullable
    WebView getWebView() {
        return null;
    }

    protected  @Nullable
    IWebLayout getWebLayout() {
        return null;
    }
    protected PermissionInterceptor getPermissionInterceptor() {
        return null;
    }
}
