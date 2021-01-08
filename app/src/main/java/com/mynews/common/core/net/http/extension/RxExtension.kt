package com.mynews.common.core.net.http.extension

import com.mynews.common.core.net.http.HttpManager
import com.mynews.common.core.net.http.bean.APIResponse
import io.reactivex.Observable


fun <T> Observable<APIResponse<T>>.extractData(): Observable<T> = HttpManager.callAPI(this)