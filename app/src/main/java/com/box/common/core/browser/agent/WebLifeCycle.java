package com.box.common.core.browser.agent;


public interface WebLifeCycle {


    void onResume();
    void onPause(boolean pauseTimer);
    void onDestroy();


}
