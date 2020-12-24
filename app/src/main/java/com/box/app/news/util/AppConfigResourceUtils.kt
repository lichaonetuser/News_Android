package com.box.app.news.util

import com.box.app.news.bean.AppConfigResource
import com.box.app.news.data.DataManager
import com.box.common.core.log.Logger

object AppConfigResourceUtils {

    fun updateHtmlResource(res: AppConfigResource, css: String, js: String, jslang: String) {
        synchronized(this) {
            if (res.css.url.isNotBlank() && css.isNotBlank()) {
                Logger.d("updateJS_update css success")
                DataManager.Local.saveArticleTransCodeCss(css)
                DataManager.Local.saveArticleTransCodeCssInfo(res.css)
            }
            if (res.js.url.isNotBlank() && js.isNotBlank()) {
                Logger.d("updateJS_update js success")
                DataManager.Local.saveArticleTransCodeJs(js)
                DataManager.Local.saveArticleTransCodeJsInfo(res.js)
            }
            if (res.jsLang.url.isNotBlank() && jslang.isNotBlank()) {
                Logger.d("updateJS_update jsLang success")
                DataManager.Local.saveArticleTransCodeJsLang(jslang)
                DataManager.Local.saveArticleTransCodeJsLangInfo(res.jsLang)
            }
        }
    }

    //0=css，1=js,2=js_lang,保证非null
    fun getHtmlResource(): Array<String> {
        synchronized(this) {
            return arrayOf(DataManager.Local.getArticleTransCodeCss(),
                    DataManager.Local.getArticleTransCodeJs(),
                    DataManager.Local.getArticleTransCodeJsLang())
        }
    }


}