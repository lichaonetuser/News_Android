package com.box.app.news.page.mvp.layer.main.debug

import android.os.Bundle
import com.box.common.extension.app.mvp.base.MVPBasePresenter

class DebugPresenter : MVPBasePresenter<DebugContract.View>(),
        DebugContract.Presenter<DebugContract.View> {

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
    }

}

