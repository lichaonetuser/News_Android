package com.mynews.app.news.page.mvp.layer.main.container.title

import android.os.Bundle
import com.mynews.common.extension.app.mvp.base.MVPBasePresenter
import com.yatatsu.autobundle.AutoBundleField

class ContainerTitlePresenter : MVPBasePresenter<ContainerTitleContract.View>(),
        ContainerTitleContract.Presenter<ContainerTitleContract.View> {

    @AutoBundleField
    var mContentClassName: String = ""
    @AutoBundleField
    var mContentBundle: Bundle = Bundle()
    @AutoBundleField(required = false)
    var mParentTitle: String = ""
    @AutoBundleField(required = false)
    var mParentLoadWhenEnterEnd: Boolean = true
    @AutoBundleField(required = false)
    var mStatusBarIsLight: Boolean = true

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        mView?.setTitle(mParentTitle)
        if (!mParentLoadWhenEnterEnd) {
            mView?.loadContainer(mContentClassName, mContentBundle)
        }
    }

    override fun onEnterEnd() {
        super.onEnterEnd()
        if (mParentLoadWhenEnterEnd) {
            mView?.loadContainer(mContentClassName, mContentBundle)
        }
    }

    override fun onViewVisible() {
        super.onViewVisible()
        mView?.setStatusBarIsLight(mStatusBarIsLight)
    }

}

