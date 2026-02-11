package com.bashkevich

import com.bashkevich.bot.TelegramCounterBot
import com.bashkevich.service.CounterApiService
import kotlinx.coroutines.runBlocking

suspend fun main() {
    val token = System.getenv("BOT_TOKEN")
        ?: error("BOT_TOKEN environment variable is not set")

    val apiService = CounterApiService()

    try {
        val bot = TelegramCounterBot(token, apiService)
        bot.start()
    } finally {
        apiService.close()
    }
}
