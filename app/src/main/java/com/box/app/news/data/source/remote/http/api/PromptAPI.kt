package com.box.app.news.data.source.remote.http.api

import com.box.common.core.net.http.bean.APIResponse
import com.box.common.extension.prompt.Prompt
import io.reactivex.Observable
import retrofit2.http.GET

interface PromptAPI {

    companion object {
        const val ROOT_PATH = "prompt"
    }

    @GET("$ROOT_PATH/")
    fun prompt(): Observable<APIResponse<Prompt>>

}