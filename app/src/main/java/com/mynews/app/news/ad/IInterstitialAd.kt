package com.mynews.app.news.ad

interface IInterstitialAd {

    fun isLoaded(): Boolean

    fun isLoading(): Boolean

    fun setAdListener(l: IAdListener)

    fun loadAd()

    fun show()

    fun setRequestTimeout(seconds: Long)

    fun getInternal(): Any

    fun getAdSourceEna(): AdSourceEna

    fun destroy()

    fun setMuted(muted: Boolean)

}
