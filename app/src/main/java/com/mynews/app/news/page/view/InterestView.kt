package com.mynews.app.news.page.view

import com.mynews.app.news.bean.Interest

interface InterestView {

    fun updateInteretTag(interest: Interest?, max: Int?, min: Int?)

    fun updateUserInterest(isUpdated: Boolean)

    fun startAnim()

    fun stopAnim()

    fun reEnableButtonAndShowToast()
}