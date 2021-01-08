package com.mynews.app.news.ad

interface INativeAd {

    fun isLoading(): Boolean

    fun isLoaded(): Boolean

    fun setAdListener(l: IAdListener)

    fun loadAd(vararg any: Any?)

    fun destroy()

    fun getInternal(): Any?

}