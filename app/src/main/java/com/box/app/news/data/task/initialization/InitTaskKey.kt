package com.box.app.news.data.task.initialization


enum class InitTaskKey {

    INIT_DB,
    INIT_CACHE,
    INIT_APP_LOG_DATA,
    INIT_WEATHER_REGION_LIST,
    INIT_NEWS_CHANNEL_LIST_LOCAL,
    //7.1 这个初始化不在启动任务里做
//    INIT_NEWS_CHANNEL_LIST_REMOTE,
    APP_CONFIG,
    APP_LOG_CONFIG,
    CLEAN_DB,

}