package com.box.common.extension.app.mvp.dialog

import com.box.common.extension.app.mvp.base.MVPBasePresenter

abstract class MVPDialogPresenter<V : MVPDialogContract.View>
    : MVPBasePresenter<V>(), MVPDialogContract.Presenter<V>