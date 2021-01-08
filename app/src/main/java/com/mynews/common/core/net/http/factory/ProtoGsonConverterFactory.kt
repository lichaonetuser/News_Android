package com.mynews.common.core.net.http.factory

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.protobuf.MessageLite
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class ProtoGsonConverterFactory(val mGson: Gson) : Converter.Factory() {

    companion object {
        fun create(gson: Gson): ProtoGsonConverterFactory {
            return ProtoGsonConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(type: Type?, annotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        val adapter = mGson.getAdapter(TypeToken.get(type))
        return GsonResponseBodyConverter(mGson, adapter)
    }

    class GsonResponseBodyConverter<T>(private val mGson: Gson, private val mAdapter: TypeAdapter<T>) : Converter<ResponseBody, T> {

        @Throws(IOException::class)
        override fun convert(value: ResponseBody): T {
            val jsonReader = mGson.newJsonReader(value.charStream())
            value.use { return mAdapter.read(jsonReader) }
        }
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        if (type !is Class<*>) {
            return null
        }
        return if (!MessageLite::class.java.isAssignableFrom(type)) {
            null
        } else ProtoRequestBodyConverter<MessageLite>()
    }

    class ProtoRequestBodyConverter<T : MessageLite> : Converter<T, RequestBody> {

        @Throws(IOException::class)
        override fun convert(value: T): RequestBody {
            val bytes = value.toByteArray()
            return RequestBody.create(MEDIA_TYPE, bytes)
        }

        companion object {
            private val MEDIA_TYPE = MediaType.parse("application/x-protobuf")
        }
    }


}