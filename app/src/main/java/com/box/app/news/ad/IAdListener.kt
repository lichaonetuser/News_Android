package com.box.app.news.ad

interface IAdListener {

    fun onAdFailedToLoad(error: AdError)

    fun onAdFailedToShow(error: AdError)

    fun onAdLoaded(any: Any? = null)

    fun onAdOpened()

    fun onAdClicked()

    fun onAdClosed()

    fun onAdImpression()

}
