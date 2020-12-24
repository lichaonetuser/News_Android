package com.box.app.news.ad

import com.box.app.news.util.MD5Utils
import com.box.common.core.environment.EnvSecure

object AdIdCenter {

    //默认配置
    //冷启动插屏  ca-app-pub-2599375973832321/1711487362
    //热启动插屏  ca-app-pub-2599375973832321/1922529914
    const val ADMOB_APP_ID = ""
    const val ADMOB_APP_ID_CHECK = ""
    const val ADMOB_DEFAULT_INTERSTITIAL_HOT = "" //热启动插屏
    const val ADMOB_DEFAULT_INTERSTITIAL_HOT_CHECK = ""
    const val ADMOB_DEFAULT_INTERSTITIAL_COLD = "" //冷启动插屏
    const val ADMOB_DEFAULT_INTERSTITIAL_COLD_CHECK = ""

    //Admob提供的测试广告
    //https://developers.google.com/admob/android/test-ads
    const val ADMOB_TEST_ID_BANNER = "ca-app-pub-3940256099942544/6300978111" //横幅广告
    const val ADMOB_LIST_TEST_ID_IINTERSTITIAL = "ca-app-pub-3940256099942544/1033173712" //插页式广告
    const val ADMOB_LIST_TEST_ID_IINTERSTITIAL_VIDEO = "ca-app-pub-3940256099942544/8691691433" //插页式视频广告
    const val ADMOB_LIST_TEST_ID_REWARD_VIDEO = "ca-app-pub-3940256099942544/5224354917" //激励视频广告
    const val ADMOB_LIST_TEST_ID_NATIVE = "ca-app-pub-3940256099942544/2247696110" //原生高级广告
    const val ADMOB_LIST_TEST_ID_NATIVE_VIDEO = "ca-app-pub-3940256099942544/1044960115" //原生高级视频广告

    //Admob的测试设备ID
    val ADMOB_TEST_DEVICE_ID by lazy { MD5Utils.md5ToString(EnvSecure.ANDROID_ID).toUpperCase() }

    //Mopub提供的测试广告
    const val MOPUB_TEST_ID_IINTERSTITIAL = "24534e1901884e398f1253216226017e"
    const val MOPUB_TEST_ID_IINTERSTITIAL_CHECK = "95C6A618914005DFF372635902A4D12D"
    const val MOPUB_TEST_ID_NATIVE = "11a17b188668469fb0412708c3d16813"
    const val MOPUB_TEST_ID_NATIVE_CHECK = "54F7D22094F939CB4D7E83F7F396E3EC"

}

