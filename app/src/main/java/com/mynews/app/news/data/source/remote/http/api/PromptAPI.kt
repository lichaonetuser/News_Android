package com.mynews.app.news.data.source.remote.http.api

import com.mynews.common.core.net.http.bean.APIResponse
import com.mynews.common.extension.prompt.Prompt
import io.reactivex.Observable
import retrofit2.http.GET

interface PromptAPI {

    companion object {
        const val ROOT_PATH = "prompt"
    }

    @GET("$ROOT_PATH/")
    fun prompt(): Observable<APIResponse<Prompt>>

}