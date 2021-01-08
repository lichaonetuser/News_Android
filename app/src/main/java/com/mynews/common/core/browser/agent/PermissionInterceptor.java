package com.mynews.common.core.browser.agent;


public interface PermissionInterceptor {

    boolean intercept(String url, String[] permissions, String action);

}
