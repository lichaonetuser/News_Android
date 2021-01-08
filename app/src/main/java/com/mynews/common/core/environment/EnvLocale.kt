package com.mynews.common.core.environment

import java.util.*

object EnvLocale {
    val DEFAULT_LOCALE = Locale.getDefault()
    val LANGUAGE = DEFAULT_LOCALE.language
    val COUNTRY = DEFAULT_LOCALE.country
}