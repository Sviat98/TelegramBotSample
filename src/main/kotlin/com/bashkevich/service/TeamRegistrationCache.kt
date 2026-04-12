package com.bashkevich.service

import com.bashkevich.model.TeamRegistrationState
import org.koin.core.annotation.Single
import java.util.concurrent.ConcurrentHashMap

@Single
class TeamRegistrationCache {
    private val cache = ConcurrentHashMap<Long, TeamRegistrationState>()

    fun getState(chatId: Long): TeamRegistrationState? = cache[chatId]

    fun setState(chatId: Long, state: TeamRegistrationState) {
        cache[chatId] = state
    }

    fun hasState(chatId: Long): Boolean = cache.containsKey(chatId)

    fun clearState(chatId: Long) {
        cache.remove(chatId)
    }

    fun updateState(chatId: Long, updater: (TeamRegistrationState) -> TeamRegistrationState) {
        cache[chatId]?.let { currentState ->
            cache[chatId] = updater(currentState)
        }
    }
}
