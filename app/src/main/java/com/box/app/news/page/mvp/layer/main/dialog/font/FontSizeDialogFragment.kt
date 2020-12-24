package com.box.app.news.page.mvp.layer.main.dialog.font

import android.os.Bundle
import android.view.View
import com.box.app.news.R
import com.box.app.news.data.DataDictionary
import com.box.app.news.data.DataManager
import com.box.common.extension.app.mvp.dialog.MVPDialogFragment
import kotlinx.android.synthetic.main.fragment_font.*

class FontSizeDialogFragment : MVPDialogFragment<FontSizeDialogContract.View,
        FontSizeDialogContract.Presenter<FontSizeDialogContract.View>>(),
        FontSizeDialogContract.View {

    override val mPresenter = FontSizeDialogPresenter()
    override val mLayoutRes = R.layout.fragment_dialog_font_size
    override var mEnterType = MVPDialogFragment.Companion.EnterType.BOTTOM

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        val fontSize = DataManager.Local.getFontSize()
        val barIndex = when (fontSize) {
            DataDictionary.FontSize.S -> 0
            DataDictionary.FontSize.M -> 1
            else -> 2
        }
        font_size_bar.index = barIndex
        font_size_bar.onIndexChanged {
            val size = when (it) {
                0 -> DataDictionary.FontSize.S
                1 -> DataDictionary.FontSize.M
                else -> DataDictionary.FontSize.L
            }
            mPresenter.onFontSizeChange(size)
        }
    }
}


