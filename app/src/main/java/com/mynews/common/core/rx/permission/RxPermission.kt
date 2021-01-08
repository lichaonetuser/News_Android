package com.mynews.common.core.rx.permission

import androidx.fragment.app.Fragment;
import com.mynews.app.news.R
import com.mynews.common.core.CoreApp
import com.mynews.common.extension.location.RxLocation
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import io.reactivex.Observable
import io.reactivex.Single
import java.util.*

object RxPermission {

    fun request(vararg permissionsArray: String): Observable<MutableList<String>> {
        return Observable.create<MutableList<String>>({ emmit ->
            AndPermission.with(CoreApp.getInstance())
                    .permission(*permissionsArray)
                    .callback(object : PermissionListener {
                        override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                            emmit.onNext(grantPermissions)
                            emmit.onComplete()
                        }

                        override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                            emmit.onError(PermissionException(deniedPermissions))
                        }
                    })
                    .start()
        })

    }

    fun hasPermission(vararg permissions: String): Single<Boolean> {
        return Single.fromCallable { AndPermission.hasPermission(CoreApp.getInstance(), *permissions) }
    }

    fun hasAlwaysDeniedLocationPermission(fragment: Fragment, vararg permissions: String): Boolean {
        return AndPermission.hasAlwaysDeniedPermission(fragment, Arrays.asList(*permissions))
    }

    fun showSettingDialogIfAlwaysDenied(fragment: Fragment, vararg permissions: String): Boolean {
        if (hasAlwaysDeniedLocationPermission(fragment, *permissions)) {
            AndPermission.defaultSettingDialog(fragment, RxLocation.REQUEST_CODE_SETTINGDIALOG)
                    .setMessage(R.string.Permission_Common_Message_Permission_Failed)
                    .setTitle(R.string.Permission_Common_Title_Permission_Failed)
                    .setPositiveButton(R.string.Permission_Common_Resume)
                    .setNegativeButton(R.string.Permission_Common_Cancel, { _, _ -> })
                    .show()
            return true
        }
        return false
    }


}