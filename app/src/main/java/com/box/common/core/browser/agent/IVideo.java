package com.box.common.core.browser.agent;

import android.view.View;
import android.webkit.WebChromeClient;


public interface IVideo {


    void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback);


    void onHideCustomView();


    boolean isVideoState();

}
