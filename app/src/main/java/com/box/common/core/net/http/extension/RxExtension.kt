package com.box.common.core.net.http.extension

import com.box.common.core.net.http.HttpManager
import com.box.common.core.net.http.bean.APIResponse
import io.reactivex.Observable


fun <T> Observable<APIResponse<T>>.extractData(): Observable<T> = HttpManager.callAPI(this)