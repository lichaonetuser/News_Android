package com.mynews.common.extension.app.mvp.dialog

import com.mynews.common.extension.app.mvp.base.MVPBaseContract

interface MVPDialogContract {

    interface View : MVPBaseContract.View

    interface Presenter<in V : View> : MVPBaseContract.Presenter<V>

}