package com.box.app.news.util

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.util.Base64
import com.box.app.news.App
import com.box.common.core.log.Logger
import com.box.common.core.util.FileUtils
import java.io.File
import java.lang.Exception

object SaveInstanceStateHelper {

    const val path = "/state"
    var cacheBundle: Bundle = Bundle()

    fun save(context: Context, bundle: Bundle): Int {
        return try {
            val file = File(context.cacheDir.path + path)
            if (!file.exists()) {
                FileUtils.createNewFile(file)
            }
            val parcel = Parcel.obtain()
            parcel.writeBundle(bundle)
            val size: Float = parcel.dataSize().toFloat() / 1000 //KB
            if (App.isDebug()) {
                Logger.d("save $size KB")
            }
            val encodedString = Base64.encodeToString(parcel.marshall(), 0)
            FileUtils.writeText(file, encodedString)
            parcel.recycle()
            cacheBundle.clear()
            cacheBundle.putAll(bundle)
            size.toInt()
        } catch (e: Exception) {
            -1
        }
    }

    fun restore(context: Context): Bundle? {
        try {
            if (!cacheBundle.isEmpty) {
                return cacheBundle
            }
            val file = File(context.cacheDir.path + path)
            if (!file.exists()) {
                return null
            }
            val encodedString = FileUtils.getText(file)
            val parcelBytes = Base64.decode(encodedString, 0)
            val parcel = Parcel.obtain()
            parcel.unmarshall(parcelBytes, 0, parcelBytes.size)
            parcel.setDataPosition(0)
            val bundle: Bundle = parcel.readBundle(context.classLoader) ?: return null
            if (App.isDebug()) {
                val size: Float = parcel.dataSize().toFloat() / 1000 //KB
                Logger.d("read $size KB")
            }
            cacheBundle.clear()
            cacheBundle.putAll(bundle)
            parcel.recycle()
            return cacheBundle
        } catch (e: Throwable) {
            return null
        }
    }

    fun clear(context: Context) {
        try {
            cacheBundle.clear()
            val file = File(context.cacheDir.path + path)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            Logger.d("clear fail.")
        }
    }

}