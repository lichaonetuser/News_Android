package com.box.app.news.data.source.local.preference

import android.content.Context
import android.content.SharedPreferences
import com.box.app.news.bean.*
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.source.local.LocalKeys
import com.box.app.news.data.source.remote.http.url.HttpBaseUrls
import com.box.app.news.widget.NewsVideoView
import com.box.common.core.CoreApp
import com.box.common.core.json.gson.util.CoreGsonUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.defaultSharedPreferences

object PreferenceAPI {

    /**********
     * COMMON *
     **********/

    private val DEFAULT_PREFERENCES by lazy {
        CoreApp.getInstance().defaultSharedPreferences
    }

    fun isVersionFirstRun(versionCode: Int): Boolean {
        val key = LocalKeys.IS_VERSION_FIRST_RUN + versionCode
        val firstRun = DEFAULT_PREFERENCES.getBoolean(key, true)
        editDefault().putBoolean(key, false).apply()
        return firstRun
    }

    fun editDefault(): SharedPreferences.Editor {
        return DEFAULT_PREFERENCES.edit()
    }

    fun getLastShowSplashAdTimestamp(): Long {
        return DEFAULT_PREFERENCES.getLong(LocalKeys.LAST_SHOW_SPLASH_AD_TIMESTAMP, -1)
    }

    fun saveLastShowSplashAdTimestamp(timestamp: Long) {
        editDefault().putLong(LocalKeys.LAST_SHOW_SPLASH_AD_TIMESTAMP, timestamp).apply()
    }

    fun getLastShowInterstitialAdTimestamp(): Long {
        return DEFAULT_PREFERENCES.getLong(LocalKeys.LAST_SHOW_INTERSTITIAL_AD_TIMESTAMP, -1)
    }

    fun saveLastShowInterstitialAdTimestamp(timestamp: Long) {
        editDefault().putLong(LocalKeys.LAST_SHOW_INTERSTITIAL_AD_TIMESTAMP, timestamp).apply()
    }

    fun getFontSize(): DataDictionary.FontSize {
        val size: String = DEFAULT_PREFERENCES.getString(LocalKeys.FONT_SIZE, DataDictionary.FontSize.M.name)
        return when (size) {
            DataDictionary.FontSize.L.name -> DataDictionary.FontSize.valueOf(DataDictionary.FontSize.L.name)
            DataDictionary.FontSize.S.name -> DataDictionary.FontSize.valueOf(DataDictionary.FontSize.S.name)
            else -> DataDictionary.FontSize.valueOf(DataDictionary.FontSize.M.name)
        }
    }

    fun saveFontSize(size: DataDictionary.FontSize) {
        editDefault().putString(LocalKeys.FONT_SIZE, size.name).apply()
    }

    fun getRegion(): Region {
        return Region(
                name = DEFAULT_PREFERENCES.getString(LocalKeys.WEATHER_REGION_NAME, ""),
                cityCode = DEFAULT_PREFERENCES.getString(LocalKeys.WEATHER_REGION_CODE, ""))
    }

    fun saveRegion(region: Region) {
        editDefault().putString(LocalKeys.WEATHER_REGION_NAME, region.name)
                .putString(LocalKeys.WEATHER_REGION_CODE, region.cityCode)
                .apply()
    }

    fun getUniqueDeviceId(): String {
        return DEFAULT_PREFERENCES.getString(LocalKeys.UNIQUE_DEVICE_ID, "")
    }

    fun saveUniqueDeviceId(id: String) {
        editDefault().putString(LocalKeys.UNIQUE_DEVICE_ID, id).commit()
    }

    fun getPushTokenSubmit(): Boolean {
        return DEFAULT_PREFERENCES.getBoolean(LocalKeys.PUSH_TOKEN_SUBMIT, false)
    }

    fun savePushTokenSubmit(submit: Boolean) {
        editDefault().putBoolean(LocalKeys.PUSH_TOKEN_SUBMIT, submit).apply()
    }

    fun getAppConfig(): AppConfig {
        val jsonString = DEFAULT_PREFERENCES.getString(LocalKeys.APP_CONFIG, null)
        return CoreGsonUtils.fromJson(jsonString, AppConfig::class.java) ?: AppConfig()
    }

    fun saveAppConfig(config: AppConfig) {
        editDefault().putString(LocalKeys.APP_CONFIG, CoreGsonUtils.toJson(config)).apply()
    }

    fun getAppLogConfig(): AppLogConfig {
        val jsonString = DEFAULT_PREFERENCES.getString(LocalKeys.APP_LOG_CONFIG, null)
        return CoreGsonUtils.fromJson(jsonString, AppLogConfig::class.java) ?: AppLogConfig()
    }

    fun saveAppLogConfig(config: AppLogConfig) {
        editDefault().putString(LocalKeys.APP_LOG_CONFIG, CoreGsonUtils.toJson(config)).apply()
    }

    fun getLastCleanDataBaseTime(): Long {
        return DEFAULT_PREFERENCES.getLong(LocalKeys.LAST_CLEAN_DATABASE_TIME, -1)
    }

    fun saveLastCleanDataBaseTime(timeInMillis: Long) {
        editDefault().putLong(LocalKeys.LAST_CLEAN_DATABASE_TIME, timeInMillis).apply()
    }

    fun getBaseUrl(): String {
        return DEFAULT_PREFERENCES.getString(LocalKeys.BASE_URL, HttpBaseUrls.TEST)
    }

    fun saveBaseUrl(url: String) {
        editDefault().putString(LocalKeys.BASE_URL, url).apply()
    }

    fun saveLastKnownLatitude(latitude: Float) {
        editDefault().putFloat(LocalKeys.LAST_KNOWN_LATITUDE, latitude).apply()
    }

    fun getLastKnownLatitude(): Float {
        return DEFAULT_PREFERENCES.getFloat(LocalKeys.LAST_KNOWN_LATITUDE, -1f)
    }

    fun saveLastKnownLongitude(longitude: Float) {
        editDefault().putFloat(LocalKeys.LAST_KNOWN_LONGITUDE, longitude).apply()
    }

    fun getLastKnownLongitude(): Float {
        return DEFAULT_PREFERENCES.getFloat(LocalKeys.LAST_KNOWN_LONGITUDE, -1f)
    }

    fun saveVideoClarity(clarity: NewsVideoView.Clarity) {
        editDefault().putString(LocalKeys.VIDEO_CLARITY, clarity.text).apply()
    }

    fun getVideoClarity(): NewsVideoView.Clarity {
        return NewsVideoView.Clarity.stringValueOf(DEFAULT_PREFERENCES.getString(LocalKeys.VIDEO_CLARITY, NewsVideoView.Clarity.NORMAL.text))
    }

    fun saveDeferredDeepLinkRan(ran: Boolean) {
        editDefault().putBoolean(LocalKeys.DEFERRED_DEEP_LINK_RAN, ran).apply()
    }

    fun getDeferredDeepLinkRan(): Boolean {
        return DEFAULT_PREFERENCES.getBoolean(LocalKeys.DEFERRED_DEEP_LINK_RAN, false)
    }

    fun saveDeferredDeepLinkOpened(opened: Boolean) {
        editDefault().putBoolean(LocalKeys.DEFERRED_DEEP_LINK_OPENED, opened).apply()
    }

    fun getDeferredDeepLinkOpened(): Boolean {
        return DEFAULT_PREFERENCES.getBoolean(LocalKeys.DEFERRED_DEEP_LINK_OPENED, false)
    }

    fun saveSearchHistory(history: String) {
        editDefault().putString(LocalKeys.SEARCH_HISTORY, history).apply()
    }

    fun getSearchHistory(): String = DEFAULT_PREFERENCES.getString(LocalKeys.SEARCH_HISTORY, "")

    fun saveArticleReadCache(channel: String, aids: String) {
        editDefault().putString(channel + LocalKeys.ARTICLE_READ_CACHE, aids).apply()
    }

    fun getArticleReadCache(channel: String): String = DEFAULT_PREFERENCES.getString(channel + LocalKeys.ARTICLE_READ_CACHE, "")

    fun saveArticleUserGuideOption(boolean: Boolean) {
        editDefault().putBoolean(LocalKeys.ARTICLE_NEW_USER_GUIDE, boolean).apply()
    }

    fun getArticleUserGuideOption(): Boolean = DEFAULT_PREFERENCES.getBoolean(LocalKeys.ARTICLE_NEW_USER_GUIDE, true)

    fun saveLastUploadDeviceAppInfoTimeFast(time: Long) {
        editDefault().putLong(LocalKeys.LAST_UPLOAD_DEVICE_APP_INFO_TIME_FAST, time).apply()
    }

    fun getLastUploadDeviceAppInfoTimeFast(): Long = DEFAULT_PREFERENCES.getLong(LocalKeys.LAST_UPLOAD_DEVICE_APP_INFO_TIME_FAST, -1)

    fun saveLastUploadDeviceAppInfoTimeAll(time: Long) {
        editDefault().putLong(LocalKeys.LAST_UPLOAD_DEVICE_APP_INFO_TIME_ALL, time).apply()
    }

    fun getLastUploadDeviceAppInfoTimeAll(): Long = DEFAULT_PREFERENCES.getLong(LocalKeys.LAST_UPLOAD_DEVICE_APP_INFO_TIME_ALL, -1)

    fun saveAdvertisingId(id: String) {
        editDefault().putString(LocalKeys.ADVERTISING_ID, id).apply()
    }

    fun getAdvertisingId(): String = DEFAULT_PREFERENCES.getString(LocalKeys.ADVERTISING_ID, "")

    fun saveShowPushDialogWhenLock(show: Boolean) {
        editDefault().putBoolean(LocalKeys.SHOW_PUSH_DIALOG_WHEN_LOCK, show).apply()
    }

    fun getShowPushDialogWhenLock(): Boolean = DEFAULT_PREFERENCES.getBoolean(LocalKeys.SHOW_PUSH_DIALOG_WHEN_LOCK, true)

    fun saveRedDot(redDotListString: String) {
        editDefault().putString(LocalKeys.RED_DOT_KEY, redDotListString).apply()
    }

    fun getRedDot(): String = DEFAULT_PREFERENCES.getString(LocalKeys.RED_DOT_KEY, "")

    /******
     * JS *
     ******/

    fun saveArticleTransCodeCssInfo(css: HtmlResource) {
        editDefault().putString(LocalKeys.ARTICLE_TRANS_CODE_CSS_INFO,
                CoreGsonUtils.toJson(css)).apply()
    }

    fun getArticleTransCodeCssInfo(): HtmlResource {
        val jsonString = DEFAULT_PREFERENCES.getString(LocalKeys.ARTICLE_TRANS_CODE_CSS_INFO, null)
        return CoreGsonUtils.fromJson(jsonString, HtmlResource::class.java) ?: HtmlResource()
    }

    fun saveArticleTransCodeJsInfo(js: HtmlResource) {
        editDefault().putString(LocalKeys.ARTICLE_TRANS_CODE_JS_INFO,
                CoreGsonUtils.toJson(js)).apply()
    }

    fun getArticleTransCodeJsInfo(): HtmlResource {
        val jsonString = DEFAULT_PREFERENCES.getString(LocalKeys.ARTICLE_TRANS_CODE_JS_INFO, null)
        return CoreGsonUtils.fromJson(jsonString, HtmlResource::class.java) ?: HtmlResource()
    }

    fun saveArticleTransCodeJsLangInfo(jsLang: HtmlResource) {
        editDefault().putString(LocalKeys.ARTICLE_TRANS_CODE_JS_LANG_INFO,
                CoreGsonUtils.toJson(jsLang)).apply()
    }

    fun getArticleTransCodeJsLangInfo(): HtmlResource {
        val jsonString = DEFAULT_PREFERENCES.getString(LocalKeys.ARTICLE_TRANS_CODE_JS_LANG_INFO, null)
        return CoreGsonUtils.fromJson(jsonString, HtmlResource::class.java) ?: HtmlResource()
    }

    /***********
     * ACCOUNT *
     ***********/

    private val ACCOUNT_PREFERENCES by lazy {
        CoreApp.getInstance().getSharedPreferences("account", Context.MODE_PRIVATE)
    }

    fun editAccount(): SharedPreferences.Editor {
        return ACCOUNT_PREFERENCES.edit()
    }

    fun saveAccountInfo(account: Account?) {
        editAccount().putString(LocalKeys.ACCOUNT_INFO, CoreGsonUtils.toJson(account)).apply()
    }

    fun getAccountInfo(): Account? {
        val jsonString = ACCOUNT_PREFERENCES.getString(LocalKeys.ACCOUNT_INFO, "")
        return CoreGsonUtils.fromJson(jsonString, Account::class.java)
    }

    fun saveAccountIsLogin(isLogin: Boolean) {
        editAccount().putBoolean(LocalKeys.ACCOUNT_IS_LOGIN, isLogin).apply()
    }

    fun getAccountIsLogin(): Boolean {
        return ACCOUNT_PREFERENCES.getBoolean(LocalKeys.ACCOUNT_IS_LOGIN, false)
    }

    fun saveAccountPlatform(platform: Int) {
        editAccount().putInt(LocalKeys.ACCOUNT_PLATFORM, platform).apply()
    }

    fun getAccountPlatform(): Int {
        return ACCOUNT_PREFERENCES.getInt(LocalKeys.ACCOUNT_PLATFORM, -1)
    }

    fun savePushUseSound(useSound: Boolean) {
        editAccount().putBoolean(LocalKeys.PUSH_USE_SOUND, useSound).apply()
    }

    fun getPushUseSound(): Boolean {
        return ACCOUNT_PREFERENCES.getBoolean(LocalKeys.PUSH_USE_SOUND, true)
    }

    fun savePushIds(ids: ArrayList<Long>) {
        val str = CoreGsonUtils.toJson(ids)
        editDefault().putString(LocalKeys.PUSH_IDS, str).apply()
    }

    fun getPushIds(): ArrayList<Long> {
        val str = DEFAULT_PREFERENCES.getString(LocalKeys.PUSH_IDS, "")
        return CoreGsonUtils.fromJson(str, object : TypeToken<ArrayList<Long>>() {}.type)
                ?: arrayListOf()
    }

    /*********
     * DEBUG *
     *********/

    private val DEBUG_PREFERENCES by lazy {
        CoreApp.getInstance().getSharedPreferences("debug", Context.MODE_PRIVATE)
    }

    fun editDebug(): SharedPreferences.Editor {
        return DEBUG_PREFERENCES.edit()
    }

    fun saveDebugTranslateContent(translate: Boolean) {
        editDebug().putBoolean(LocalKeys.TRANSLATE, translate).apply()
    }

    fun getDebugTranslateContent(): Boolean {
        return DEBUG_PREFERENCES.getBoolean(LocalKeys.TRANSLATE, false)
    }

    fun saveDebugShowRefer(show: Boolean) {
        editDebug().putBoolean(LocalKeys.SHOW_REFER, show).apply()
    }

    fun getDebugShowRefer(): Boolean {
        return DEBUG_PREFERENCES.getBoolean(LocalKeys.SHOW_REFER, false)
    }

    fun saveDebugAdTestDevice(show: Boolean) {
        editDebug().putBoolean(LocalKeys.AD_TEST, show).apply()
    }

    fun getDebugAdTestDevice(): Boolean {
        return DEBUG_PREFERENCES.getBoolean(LocalKeys.AD_TEST, true)
    }

    fun saveDebugAdShowId(show: Boolean) {
        editDebug().putBoolean(LocalKeys.AD_TEST_2, show).apply()
    }

    fun getDebugAdShowId(): Boolean {
        return DEBUG_PREFERENCES.getBoolean(LocalKeys.AD_TEST_2, false)
    }

    fun saveDebugListAdTest(test: Boolean) {
        editDebug().putBoolean(LocalKeys.LIST_AD_TEST, test).apply()
    }

    fun getDebugListAdTest(): Boolean {
        return DEBUG_PREFERENCES.getBoolean(LocalKeys.LIST_AD_TEST, false)
    }


}