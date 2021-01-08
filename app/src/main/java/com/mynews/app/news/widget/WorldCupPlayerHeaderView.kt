package com.mynews.app.news.widget

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import com.mynews.app.news.R
import com.mynews.common.core.CoreApp
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.core.widget.CoreSimpleDraweeView
import kotlinx.android.synthetic.main.item_worldcup_player_head_view.view.*
import org.jetbrains.anko.dip

class WorldCupPlayerHeaderView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val mLayout: View = LayoutInflater.from(context).inflate(R.layout.item_worldcup_player_head_view, this)

    fun setContent(playerImageUrl: String,
                   isStar: Int,
                   playerFlag: String,
                   playerTitleJapanText: String,
                   playerTitleEngText: String,
                   playerTitleOther: String) {
        ImageManager.with(mLayout.player_image).load(playerImageUrl)

//        val builder = SpannableStringBuilder()
//        builder.append(playerTitleJapanText)
//        var start = builder.length
//        if (isStar == 1) {
//            var uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" +R.drawable.starplayer)
//            builder.append("[img]")
//            builder.setSpan(DraweeSpan.Builder(uri.toString())
//                    .setLayout(CoreApp.getInstance().dip(15), CoreApp.getInstance().dip(15))
//                    .setMargin(CoreApp.getInstance().dip(1), 0, 0, 0).build(),
//                    start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        }
//        start = builder.length
//        builder.append("[img]")
//        builder.setSpan(DraweeSpan.Builder(playerFlag)
//                .setLayout(CoreApp.getInstance().dip(18), CoreApp.getInstance().dip(12))
//                .setMargin(CoreApp.getInstance().dip(2), 0, 0, 0).build(),
//                start, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//
//
//        mLayout.player_title_japan?.setText(builder)


        mLayout.player_title_japan.text = playerTitleJapanText
        mLayout.player_title_eng?.text = playerTitleEngText
        mLayout.player_other?.text = playerTitleOther
        ImageManager.with(mLayout.flag).load(playerFlag)
        if (isStar == 1) {
            mLayout.star.visibility = View.VISIBLE
        } else {
            mLayout.star.visibility = View.GONE
        }

        mLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val screenWidth = EnvDisplayMetrics.WIDTH_PIXELS
                val mRemainWidth = if (isStar == 1) {
                    screenWidth - CoreApp.getInstance().dip(144)
                } else {
                    screenWidth - CoreApp.getInstance().dip(126)
                }
                if (mLayout.player_title_japan.width > mRemainWidth) {
                    mLayout.player_title_japan.width = mRemainWidth
                }
                mLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun getFlag(): CoreSimpleDraweeView {
        return mLayout.flag
    }

}