package com.box.common.extension.share

enum class SharePlatform(val requestCode : Int) {

    FACEBOOK(0xE201),
    TWITTER(0xE202),
    LINE(0xE203),
    MAIL(0xE204),
    SMS(0xE205),
    CLIPBOARD(0xE206),
    SYSTEM(0xE207)

}