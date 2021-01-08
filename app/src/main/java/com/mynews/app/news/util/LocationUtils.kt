package com.mynews.app.news.util

import com.mynews.app.news.data.DataManager

object LocationUtils {

    val LATITUDE_KEY = "lat"
    val LONGITUDE_KEY = "lot"

    fun getLocationPairs(latitude: Float = DataManager.Local.getLastKnownLatitude(),
                         longitude: Float = DataManager.Local.getLastKnownLongitude()): Array<Pair<String, String>> {
        return if (latitude < 0 || longitude < 0) {
            arrayOf()
        } else
            arrayOf(LATITUDE_KEY to latitude.toString(),
                    LONGITUDE_KEY to longitude.toString())
    }

}