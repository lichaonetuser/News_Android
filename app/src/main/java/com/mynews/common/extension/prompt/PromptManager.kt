package com.mynews.common.extension.prompt

import android.app.Activity
import android.app.AlertDialog
import com.mynews.common.core.CoreApp
import com.mynews.common.core.log.Logger
import com.mynews.common.core.rx.schedulers.ioToMain
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.jetbrains.anko.defaultSharedPreferences
import java.lang.ref.WeakReference
import java.util.concurrent.TimeUnit

object PromptManager {

    private val mPreferencesKey = "promptId"
    private var mActivityRef: WeakReference<Activity>? = null

    enum class ButtonType(val value: Int) {
        CANCEL(0), ACTION(1)
    }

    fun loadPrompt(activity: Activity, obPrompt: Observable<Prompt>, showListener: (id: Int) -> Unit, clickListener: (id: Int, index: Int, openUrl: String) -> Unit) {
        mActivityRef = WeakReference(activity)
        obPrompt.delay { it -> Observable.just(it).delay(it.delay.toLong(), TimeUnit.SECONDS) }
                .ioToMain()
                .subscribeBy(
                        onNext = {
                            val context = mActivityRef?.get() ?: return@subscribeBy
                            showPrompt(context, it, clickListener)
                            showListener.invoke(it.id)
                        },
                        onError = {
                            Logger.d("loadPrompt Error: $it")
                        }
                )
    }

    private fun showPrompt(activity: Activity?, prompt: Prompt, listener: (id: Int, index: Int, openUrl: String) -> Unit) {
        val preferences = CoreApp.getInstance().defaultSharedPreferences
        val id = preferences.getInt(mPreferencesKey, -1)
        val newId = prompt.id
        if (id != newId || prompt.isRepeat == 1) {
            val buttons = prompt.buttons
            val size = buttons.size
            if (size <= 0) {
                return
            }

            if (activity == null) {
                return
            }

            val builder = AlertDialog.Builder(activity)
            builder.setTitle(prompt.title)
            builder.setMessage(prompt.content)
            builder.setCancelable(false)

            if (size > 0) {
                val btn = buttons[0]
                builder.setNegativeButton(btn.name, { dialog, which ->
                    listener.invoke(prompt.id, 0, btn.openUrl)
                    preferences.edit().putInt(mPreferencesKey, newId).apply()
                    dialog.dismiss()
                })
            }

            if (size > 1) {
                val btn = buttons[1]
                builder.setPositiveButton(btn.name, { dialog, which ->
                    listener.invoke(prompt.id, 1, btn.openUrl)
                    preferences.edit().putInt(mPreferencesKey, newId).apply()
                    dialog.dismiss()
                })
            }

            if (size > 2) {
                val btn = buttons[2]
                builder.setNeutralButton(btn.name, { dialog, which ->
                    listener.invoke(prompt.id, 2, btn.openUrl)
                    preferences.edit().putInt(mPreferencesKey, newId).apply()
                    dialog.dismiss()
                })
            }

            builder.create().show()
        }
    }
}
