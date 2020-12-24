package com.box.common.core.net.http.extension

import retrofit2.Retrofit
import kotlin.reflect.KClass

fun <T : Any> Retrofit.create(service: KClass<T>): T {
    return create(service.java)
}
