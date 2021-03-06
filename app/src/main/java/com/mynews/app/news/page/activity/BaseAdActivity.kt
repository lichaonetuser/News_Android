package com.mynews.app.news.page.activity

import android.os.Bundle
import com.mynews.app.news.ad.AdManager
import com.mynews.common.core.app.activity.CoreBaseActivity

abstract class BaseAdActivity : CoreBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AdManager.onCreate(this)
    }

    override fun onStart() {
        super.onStart()
        AdManager.onStart(this)
    }

    override fun onResume() {
        super.onResume()
        AdManager.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        AdManager.onPause(this)
    }

    override fun onStop() {
        super.onStop()
        AdManager.onStop(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AdManager.onDestroy(this)
    }

    override fun onRestart() {
        super.onRestart()
        AdManager.onRestart(this)
    }

    override fun onBackPressedSupport() {
        super.onBackPressedSupport()
        AdManager.onBackPressed(this)
    }

}