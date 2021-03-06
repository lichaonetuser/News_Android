package com.mynews.common.core.log

import com.mynews.common.core.log.print.PrintLogAdapter

object LogManager {

    fun init() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .methodCount(3)
                .build()
        Logger.addLogAdapter(PrintLogAdapter(formatStrategy))
    }

}