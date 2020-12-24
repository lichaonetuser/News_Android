package com.box.common.extension.location

import android.annotation.SuppressLint
import android.location.Address
import android.location.Location
import androidx.fragment.app.Fragment;
import com.box.app.news.R
import com.box.common.core.CoreApp
import com.box.common.core.rx.permission.RxPermission
import com.google.android.gms.location.LocationRequest
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import io.reactivex.Observable
import io.reactivex.Single
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import java.util.*

object RxLocation {

    const val REQUEST_CODE_SETTINGDIALOG = 200

    private val mLocationProvider by lazy {
        ReactiveLocationProvider(CoreApp.getInstance())
    }

    fun haveLocationPermission(): Single<Boolean> {
        return RxPermission.hasPermission(*Permission.LOCATION)
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(): Single<Location> {
        return RxPermission
                .request(*Permission.LOCATION)
                .singleOrError()
                .flatMap { mLocationProvider.lastKnownLocation.singleOrError() }
    }

    @SuppressLint("MissingPermission")
    fun getLastKnownAddress(): Single<Address> {
        return getLastKnownLocation()
                .flatMap { location ->
                    mLocationProvider.getReverseGeocodeObservable(
                            location.latitude,
                            location.longitude,
                            1)
                            .singleOrError()
                }.map { t -> t[0] }
    }

    @SuppressLint("MissingPermission")
    fun getLocation(request: LocationRequest =
                    LocationRequest.create()
                            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                            .setNumUpdates(5)
                            .setInterval(100))
            : Observable<Location> {
        return RxPermission
                .request(*Permission.LOCATION)
                .flatMap { mLocationProvider.getUpdatedLocation(request) }
    }

    fun hasAlwaysDeniedLocationPermission(fragment: Fragment): Boolean {
        return AndPermission.hasAlwaysDeniedPermission(fragment, Arrays.asList(*Permission.LOCATION))
    }

    fun showSettingDialogIfAlwaysDenied(fragment: Fragment): Boolean {
        if (hasAlwaysDeniedLocationPermission(fragment)) {
            AndPermission.defaultSettingDialog(fragment, REQUEST_CODE_SETTINGDIALOG)
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