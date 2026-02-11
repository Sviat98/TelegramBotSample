package com.bashkevich.bot

import com.bashkevich.service.CounterApiService
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.types.message.content.TextContent
import kotlinx.coroutines.flow.first


class TelegramCounterBot(
    private val token: String,
    private val apiService: CounterApiService
) {
    suspend fun start() {
        val bot = telegramBot(token)

        bot.buildBehaviourWithLongPolling {

            println(getMe())

            onCommand("start") {
                reply(it, "Welcome! Send me a name to create a counter.")

                val nameContent = waitText().first()
                val counterName = nameContent.text

                try {
                    val counter = apiService.createCounter(counterName)
                    reply(
                        it,
                        "✅ Counter created!\nName: ${counter.name}\nID: ${counter.id}"
                    )
                } catch (e: Exception) {
                    reply(
                        it,
                        "❌ Failed to create counter: ${e.message}"
                    )
                }
            }
        }.join()
    }
}
