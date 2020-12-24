package com.box.app.news.data.source.local

import com.box.app.news.R
import com.box.common.core.CoreApp


object LocalKeys {


    // 通用关键字,Preference,Bundle,etc..
    const val LAST_INDEX = "last_index_redot"
    const val DURATION = "duration"
    const val LAUNCH = "launch"
    const val INTEREST_EVENT = "interest"
    const val STAY_PAGE = "stay_page"
    const val ENTER_BACKGROUND = "enter_background"
    const val ENTER_FOREGROUND = "enter_foreground"
    const val MY_INTEREST = "my_interest"
    const val SELECT_GENDER_AGE_STATE = "select_gender_age_state" //首次安装进入性别年龄段界面状态key
    const val IS_SELECT_GENDER_AGE = "is_select_gender_age" //首次安装进入性别年龄段界面状态key
    const val IS_SELECTED_INTEREST = "isSelectedInterest"
    const val ACCOUNT_INFO = "AccountInfo"
    const val ACCOUNT_PLATFORM = "AccountPlatform"
    const val ACCOUNT_IS_LOGIN = "AccountIsLogin"
    const val FONT_SIZE = "FontSize"
    const val UNIQUE_DEVICE_ID = "UniqueDeviceId"
    const val WEATHER_REGION_NAME = "WeatherRegionName"
    const val WEATHER_REGION_CODE = "WeatherRegionCode"
    const val PUSH_TOKEN_SUBMIT = "PushTokenSubmit"
    const val LAST_SHOW_SPLASH_AD_TIMESTAMP = "LastShowSplashAdTimestamp"
    const val LAST_SHOW_INTERSTITIAL_AD_TIMESTAMP = "LastShowInterstitialAdTimestamp"
    const val APP_CONFIG = "AppConfig"
    const val APP_LOG_CONFIG = "AppLogConfig"
    const val LAST_CLEAN_DATABASE_TIME = "LastCleanDatabaseTime"
    const val BASE_URL = "BaseUrl"
    const val LAST_KNOWN_LATITUDE = "latitude"
    const val LAST_KNOWN_LONGITUDE = "longitude"
    const val FIRST_LAUNCH = "FirstLaunch"
    const val VIDEO_CLARITY = "VideoClarity"
    const val ARTICLE_TRANS_CODE_CSS_INFO = "TransCodeCssInfo"
    const val ARTICLE_TRANS_CODE_JS_INFO = "TransCodeJsInfo"
    const val ARTICLE_TRANS_CODE_JS_LANG_INFO = "TransCodeJsLangInfo"
    const val PUSH_USE_SOUND = "PushUseSound"
    const val PUSH_IDS = "PushIds"
    const val DEFERRED_DEEP_LINK_RAN = "DeferredDeepLinkRan"
    const val DEFERRED_DEEP_LINK_OPENED = "DeferredDeepLinkOpened"
    const val SEARCH_HISTORY = "SearchHistoryKeyword"
    const val ARTICLE_NEW_USER_GUIDE = "ArticleNewUserGuide"
    const val LAST_UPLOAD_DEVICE_APP_INFO_TIME_FAST = "LastUploadDeviceAppInfoTimeFast"
    const val LAST_UPLOAD_DEVICE_APP_INFO_TIME_ALL = "LastUploadDeviceAppInfoTimeAll"
    const val ADVERTISING_ID = "AdvertisingId"
    const val SHOW_PUSH_DIALOG_WHEN_LOCK = "ShowPushDialogWhenLock"
    const val ARTICLE_READ_CACHE = "ArticleReadCache"

    //DEBUG KEY
    const val TRANSLATE = "Translate"
    const val SHOW_REFER = "ShowRefer"
    const val AD_TEST = "AD_TEST"
    const val AD_TEST_2 = "AD_TEST_2"
    const val LIST_AD_TEST = "LIST_AD_TEST"

    //版本是否第一次运行
    const val IS_VERSION_FIRST_RUN = "isVersionFirstRun"

    // 文件路径
    val DEFAULT_FILES_DIR by lazy { "${CoreApp.getInstance().filesDir}/source" }
    val FILE_CHANNEL_LIST by lazy { "$DEFAULT_FILES_DIR/channel_list.txt" }
    val FILE_REGION_LIST by lazy { "$DEFAULT_FILES_DIR/region_list.txt" }
    val FILE_PUSH_ARRIVAL by lazy { "$DEFAULT_FILES_DIR/push_arrival.txt" }
    val FILE_ARTICLE_CONTENT_CACHE by lazy { "$DEFAULT_FILES_DIR/article_content_cache.txt" }
    val ARTICLE_TRANS_CODE_CSS by lazy { "$DEFAULT_FILES_DIR/article_trans_code_css.css" }
    val ARTICLE_TRANS_CODE_JS by lazy { "$DEFAULT_FILES_DIR/article_trans_code_js.js" }
    val ARTICLE_TRANS_CODE_JS_LANG by lazy { "$DEFAULT_FILES_DIR/article_trans_code_js_lang.js" }

    // R资源
    const val R_DEFAULT_CHANNEL_LIST = R.raw.default_channel_list
    const val R_DEFAULT_REGION_LIST = R.raw.default_region_list
    const val R_DEFAULT_ARTICLE_TRANS_CODE_CSS = R.raw.css_news_detail
    const val R_DEFAULT_ARTICLE_TRANS_CODE_JS = R.raw.js_news_detail
    const val R_DEFAULT_ARTICLE_TRANS_CODE_JS_LANG = R.raw.js_news_lang

    const val RED_DOT_LIST = "red_dot_list" //sp保存点击过红点的list的key(弃用,下版本删除)
    const val RED_DOT_KEY = "red_dot_key" //sp保存点击过红点的list的key

    //默认为false,splash页做过判断后改为true
    const val NOT_FIRST_RUN = "shouldEnterInterestOrGender"//第一次安装后运行，用来判断是否应该进入兴趣选择或者性别年龄选择
}