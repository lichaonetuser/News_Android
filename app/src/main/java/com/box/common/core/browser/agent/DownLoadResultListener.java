package com.box.common.core.browser.agent;


public interface DownLoadResultListener {


    void success(String path);

    void error(String path, String resUrl, String cause, Throwable e);

}
