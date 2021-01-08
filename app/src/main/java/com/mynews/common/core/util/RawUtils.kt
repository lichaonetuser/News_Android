package com.mynews.common.core.util

import androidx.annotation.RawRes
import com.mynews.common.core.CoreApp
import com.mynews.common.core.json.gson.util.CoreGsonUtils
import java.io.InputStreamReader
import java.lang.reflect.Type

object RawUtils {

    fun getString(@RawRes id: Int): String? {
        var returnStr: String? = null
        return try {
            InputStreamReader(CoreApp.getInstance().resources.openRawResource(id))
                    .use({ reader: InputStreamReader -> returnStr = reader.readText() })
            returnStr
        } catch (e: Exception) {
            null
        }
    }

    fun <T : Any> getBean(@RawRes id: Int, classOfT: Class<T>): T? {
        return CoreGsonUtils.fromJson(getString(id), classOfT)
    }

    fun <T : Any> getBean(@RawRes id: Int, typeOfT: Type): T? {
        return CoreGsonUtils.fromJson(getString(id), typeOfT)
    }

}