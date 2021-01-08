package com.mynews.app.news.bean.js

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class JSImage(
        @Expose @SerializedName("index") val index: String,
        @Expose @SerializedName("file_path") val filePath: String)