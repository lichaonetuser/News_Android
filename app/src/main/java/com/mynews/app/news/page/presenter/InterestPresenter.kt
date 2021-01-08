package com.mynews.app.news.page.presenter

import com.mynews.app.news.bean.Interest

import com.mynews.app.news.data.DataManager
import com.mynews.app.news.data.source.local.LocalKeys
import com.mynews.app.news.page.view.InterestView

import com.mynews.common.core.net.http.bean.APIResponse
import com.mynews.common.core.rx.schedulers.ioToMain
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class InterestPresenter(view: InterestView) {

    private val view = view

    fun getUserInterests() {
        view.stopAnim()
        val interest:Interest = Gson().fromJson(Prefs.getString(LocalKeys.MY_INTEREST,""),Interest::class.java)
        view.updateInteretTag(interest, interest.max, interest.min)
    }

    fun sendDataInterestDataToServer(tagList: ArrayList<String>) {
        val map = HashMap<String, Any>()
        map.put("tags", tagList)

        DataManager.Remote.sendUserInterest(map).ioToMain().subscribe(object : Observer<APIResponse<Any>> {
            override fun onComplete() {

            }

            override fun onNext(t: APIResponse<Any>) {
                if (t.isOk()) {
                    Prefs.putBoolean(LocalKeys.IS_SELECTED_INTEREST, true)
                    DataManager.Init.initNewsChannelListFromRemote()
                    view.updateUserInterest(true)
                }
                view.stopAnim()
            }

            override fun onError(e: Throwable) {

                view.updateUserInterest(false)
                view.stopAnim()
                view.reEnableButtonAndShowToast()
            }

            override fun onSubscribe(d: Disposable) {

                view.startAnim()
            }
        })
    }


}