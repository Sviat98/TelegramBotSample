package com.bashkevich.handler

import com.bashkevich.service.CounterApiService
import io.github.dehuckakpyt.telegrambot.handling.BotHandling
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.mp.KoinPlatform

fun BotHandling.startCommand() {
    command("/start", next = "get_counter_name") {
        sendMessage("Welcome! Send me a name to create a counter.")
    }

    step("get_counter_name") {
        val counterName = text
        val apiService = KoinPlatform.getKoin().get<CounterApiService>()

        try {
            val counter = apiService.createCounter(counterName)
            sendMessage("✅ Counter created!\nName: ${counter.name}\nID: ${counter.id}")
        } catch (e: Exception) {
            sendMessage("❌ Failed to create counter: ${e.message}")
        }
    }
}
