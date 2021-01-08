package com.mynews.app.news.util

class DetailAppLogHelper {

    var firstVisibleTime = -1L
        private set
    var totalRecursiveDuration = 0L
        private set
    var onResumeTime = 0L
        private set

     fun onResume() {
        onResumeTime = System.currentTimeMillis()
    }

     fun onPause() {
        totalRecursiveDuration += (System.currentTimeMillis() - onResumeTime)
    }

    fun onViewVisible(visibleTime : Long) {
        if (firstVisibleTime < 0) {
            firstVisibleTime = visibleTime
        }
    }


}