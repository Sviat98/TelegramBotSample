package com.bashkevich.service

import org.koin.core.annotation.Single
import java.util.concurrent.ConcurrentHashMap

@Single
class UserCityCache {
    private val cache = ConcurrentHashMap<Long, String>()

    fun getCity(chatId: Long): String? = cache[chatId]

    fun setCity(chatId: Long, city: String) {
        cache[chatId] = city
    }

    fun hasCity(chatId: Long): Boolean = cache.containsKey(chatId)

    fun clearCity(chatId: Long) {
        cache.remove(chatId)
    }
}
