package com.mynews.common.core.util

object CollectionUtils{

    fun isNullOrEmpty(collection: Collection<*>?) : Boolean{
        return collection == null || collection.isEmpty()
    }

}