package com.mynews.app.news.util

import com.mynews.app.news.data.DataManager

/**
 * Reddot管理工具类
 */
object ReddotUtils{

    private const val RED_DOT_LIMMITED = 20 //reddot最大限制
    private const val SPLIT_CHAR = "," //每个item之间的分隔符
    private const val DEFAULT_VALUE = "0" //reddot的默认值

    private var listInPrefs = "" //最新的内存中的字符串
    private var inited = false //是否加载过
    private var redDotList = mutableListOf<String>() //缓存reddot， 列表存的是long转的string

    //初始化
    private fun initIfNeed() {
        if (!inited) {
            listInPrefs = DataManager.Local.getRedDot()
            redDotList.addAll(if (listInPrefs == "") {
                listOf()
            } else {
                listInPrefs.split(SPLIT_CHAR).filter { it.isNotEmpty() && it != DEFAULT_VALUE}
            })
            inited = true
        }
    }

    //是否含有该reddot，默认值算包含
    fun containRedDot(redDot: String): Boolean {
        if (redDot == DEFAULT_VALUE) {
            return true
        }
        initIfNeed()

        return redDotList.contains(redDot)
    }

    //添加新的reddot
    fun addRedDot(redDot: String) {
        if (redDot == DEFAULT_VALUE || redDot.isEmpty()) {
            return
        }
        initIfNeed()

        redDotList.add(redDot)
        //数量是否大于最大值
        if (redDotList.size >= RED_DOT_LIMMITED) {
            val times = redDotList.size - RED_DOT_LIMMITED
            repeat(times, {
                redDotList.removeAt(0)
            })
            listInPrefs = ""
            redDotList.forEach {
                listInPrefs += it + SPLIT_CHAR
            }
            listInPrefs = listInPrefs.substring(0, listInPrefs.length - 1)
        } else {
            listInPrefs += SPLIT_CHAR + redDot
        }
        DataManager.Local.saveRedDot(listInPrefs)
    }
}