package com.mynews.common.core.json.gson.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.Type

class CoreGsonUtils {

    companion object {

        private val mDefaultInstance: Gson by lazy {
            initDefaultInstance().create()
        }

        private fun initDefaultInstance(): GsonBuilder {
            return GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
        }

        fun getDefaultInstance(): Gson {
            return mDefaultInstance
        }

        fun getNewInstanceBuildFromDefault(): GsonBuilder {
            return initDefaultInstance()
        }

        fun toJson(src: Any?): String {
            return if (src == null) {
                ""
            } else {
                try {
                    mDefaultInstance.toJson(src)
                } catch (e: Exception) {
                    ""
                }
            }
        }

        fun <T : Any> fromJson(json: String?, classOfT: Class<T>): T? {
            return if (json == null) {
                null
            } else {
                try {
                    mDefaultInstance.fromJson<T>(json, classOfT)
                } catch (e: Exception) {
                    null
                }
            }
        }

        fun <T : Any> fromJson(json: String?, typeOfT: Type): T? {
            return if (json == null) {
                null
            } else {
                try {
                    mDefaultInstance.fromJson<T>(json, typeOfT)
                } catch (e: Exception) {
                    null
                }
            }
        }

    }
}
