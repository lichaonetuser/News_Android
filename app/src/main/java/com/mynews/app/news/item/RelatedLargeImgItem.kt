package com.mynews.app.news.item

import android.text.TextUtils
import android.view.View
import android.view.View.GONE
import com.mynews.app.news.R
import com.mynews.app.news.bean.Image
import com.mynews.app.news.bean.ImageInfo
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.item.base.BaseNewsItem
import com.mynews.app.news.item.base.BaseRelatedItem
import com.mynews.common.core.image.fresco.ImageManager
import com.mynews.common.extension.widget.recycler.adapter.CommonRecyclerAdapter
import kotlinx.android.synthetic.main.item_related_gif.view.*

/**
 * 相关阅读大图展示
 */
class RelatedLargeImgItem(mBean: BaseNewsBean)
    : BaseRelatedItem<BaseNewsBean, RelatedLargeImgItem.ViewHolder>(mBean) {

    override fun getLayoutRes(): Int {
        return R.layout.item_related_gif
    }

    override fun createViewHolder(view: View, adapter: CommonRecyclerAdapter): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: CommonRecyclerAdapter, holder: ViewHolder, position: Int, payloads: MutableList<Any?>?) {
        super.bindViewHolder(adapter, holder, position, payloads)
        if (mBean is Image) {
            holder.bind(mBean as Image)
        }
    }

    class ViewHolder(val view: View, adapter: CommonRecyclerAdapter) : BaseNewsItem.ViewHolder(view, adapter) {
        fun bind(image: Image) = with(view){
            val imageInfos: List<ImageInfo>? = image.images
            val url = if (imageInfos != null && imageInfos.isNotEmpty()) {
                imageInfos[0].urls.firstOrNull()
            } else {
                image.info.urls.firstOrNull()
            }
            ImageManager.with(gif_img).load(url)
            gif_play_btn.visibility = GONE
            news_source_txt.text = image.sourceName
            if (TextUtils.isEmpty(image.title)) {
                news_title_txt?.setTitle(image.title)
                news_title_txt?.visibility = View.GONE
            } else {
                news_title_txt?.setTitle(image.title)
                news_title_txt?.visibility = View.VISIBLE
            }

        }
    }
}