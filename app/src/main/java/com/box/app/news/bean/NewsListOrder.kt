package com.box.app.news.bean

import android.os.Parcel
import android.os.Parcelable
import com.box.app.news.bean.base.BaseBean
import com.box.app.news.data.DataDictionary
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table
import paperparcel.PaperParcel
import javax.annotation.Nullable

//6.0.6之前
//@Table("ArticleListOrder")
//@Setter("article") @Column var news: Article = Article(),
@Table
@PaperParcel
data class NewsListOrder(
        @Setter("order_id") @PrimaryKey var orderID: String = "",
        @Setter("chid") @Column(indexed = true) var chid: String = "",
        @Setter("aid") @Column @Nullable var aid: String = "",
        @Setter("type") @Column(defaultExpr = "0") @Nullable var type: Int = -1, //defaultExpr为0是为了兼容老版本升级
        @Setter("style") @Column var style: Int = DataDictionary.ArticleStyle.UN_KNOW.value,
        @Setter("emit_time") @Column(indexed = true) var emitTime: Long = 0
) : BaseBean(), Parcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelNewsListOrder.CREATOR
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        PaperParcelNewsListOrder.writeToParcel(this, dest, flags)
    }
}