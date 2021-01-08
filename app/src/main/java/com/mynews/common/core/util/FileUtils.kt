package com.mynews.common.core.util

import com.mynews.common.core.json.gson.util.CoreGsonUtils
import com.mynews.common.core.log.Logger
import java.io.File
import java.lang.reflect.Type
import java.nio.charset.Charset

object FileUtils {

    private val DEFAULT_CHARSET = Charset.forName("UTF-8")

    fun createNewFile(file: File): Boolean {
        return try {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
            file.createNewFile()
        } catch (e: Exception) {
            Logger.e(e)
            false
        }
    }

    fun getText(file: File): String {
        return try {
            if (file.exists()) file.readText(DEFAULT_CHARSET) else ""
        } catch (e: Exception) {
            Logger.e(e)
            ""
        }
    }

    fun writeText(file: File, text: String) {
        try {
            if (!file.exists()) {
                createNewFile(file)
            }
            file.writeText(text, DEFAULT_CHARSET)
        } catch (e: Exception) {
            Logger.e(e)
        }
    }

    fun <T : Any> getBean(file: File, classOfT: Class<T>): T? {
        return CoreGsonUtils.fromJson(getText(file), classOfT)
    }

    fun <T : Any> getBean(file: File, typeOfT: Type): T? {
        return CoreGsonUtils.fromJson(getText(file), typeOfT)
    }

    fun <T : Any> writeBean(file: File, t: T?) {
        return writeText(file, CoreGsonUtils.toJson(t))
    }

}