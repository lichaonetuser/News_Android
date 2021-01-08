package com.mynews.common.core.net.http.extension

import okhttp3.FormBody
import okhttp3.HttpUrl

fun HttpUrl.Builder.addQueryParameters(parameters: Map<String, String>): HttpUrl.Builder {
    for (entry in parameters.entries) {
        this.addQueryParameter(entry.key, entry.value)
    }
    return this
}

fun FormBody.Builder.addEncodeds(parameters: Map<String, String>): FormBody.Builder {
    for (entry in parameters.entries) {
        this.addEncoded(entry.key, entry.value)
    }
    return this
}