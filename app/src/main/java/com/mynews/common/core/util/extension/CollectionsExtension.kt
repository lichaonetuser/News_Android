package com.mynews.common.core.util.extension


/**
 * 自身去重，并去除指定List中已经存在的内容
 */
fun <T> Iterable<T>.distinctWith(destination: List<T>): List<T> {
    return this.distinct().filter { t -> !destination.contains(t) }
}
