#include <jni.h>
#include <string>
#include "md5.h"

using std::string;
using std::transform;

extern "C" JNIEXPORT bool JNICALL
Java_com_box_app_news_ad_AdManager_checkIDValid(
        JNIEnv *env,
        jobject /* this */,
        jstring jAdId, jstring jCheckId) {
    if (jAdId == nullptr || jCheckId == nullptr) {
        return false;
    }

    string adId = env->GetStringUTFChars(jAdId, nullptr);
    if (adId.empty()) {
        return false;
    }
    string tmpStr = adId + "zwb6uAr^J";
    string tmpMd5Str = MD5(tmpStr).toStr();
    transform(tmpMd5Str.begin(), tmpMd5Str.end(), tmpMd5Str.begin(), ::toupper);
    string resultMd5Str = MD5(tmpMd5Str).toStr();
    transform(resultMd5Str.begin(), resultMd5Str.end(), resultMd5Str.begin(), ::toupper);

    string checkId = env->GetStringUTFChars(jCheckId, nullptr);
    if (checkId != resultMd5Str) {
        return false;
    }

    return true;
}
