package com.box.app.news.bean.base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

abstract class BaseListBean<out Bean> constructor(
        @Expose @SerializedName("has_more")
        open var hasMore: Boolean = true
) : BaseBean()