package com.box.common.core.net.http.interceptor;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUserAgentInterceptor implements Interceptor {
 
    private final String userAgent;
 
    public HttpUserAgentInterceptor(String userAgent) {
        this.userAgent = userAgent;
    }
 
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();
        Request requestWithUserAgent = originRequest.newBuilder()
                                                    .header("User-Agent", userAgent)
                                                    .build();
        return chain.proceed(requestWithUserAgent);
    }
}