package com.box.common.core.browser.agent;


public interface ILoader {


    void loadUrl(String url);

    void reload();

    void loadData(String data, String mimeType, String encoding);

    void stopLoading();

    void loadDataWithBaseURL(String baseUrl, String data,
                             String mimeType, String encoding, String historyUrl);


    void postUrl(String url, byte[] params);

    HttpHeaders getHttpHeaders();
}
