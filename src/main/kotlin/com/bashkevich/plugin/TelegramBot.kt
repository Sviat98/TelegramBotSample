package com.bashkevich.plugin

import com.bashkevich.handler.startCommand
import io.github.dehuckakpyt.telegrambot.ext.config.receiver.handling
import io.github.dehuckakpyt.telegrambot.ext.config.receiver.longPolling
import io.github.dehuckakpyt.telegrambot.plugin.TelegramBot
import io.ktor.server.application.*
import org.koin.mp.KoinPlatform

fun Application.configureTelegramBot() {
    install(TelegramBot) {
        receiving {
            longPolling {
                limit = 10
                timeout = 25
            }

            handling {
                startCommand()
            }
        }
    }

    // Настраиваем меню бота после старта приложения
    monitor.subscribe(ApplicationStarted) {
        try {
            val koin = KoinPlatform.getKoin()
            val bot = koin.get<io.github.dehuckakpyt.telegrambot.TelegramBot>()
            setupBotMenu(bot, this@configureTelegramBot)
        } catch (e: Exception) {
            println("Failed to setup bot menu: ${e.message}")
        }
    }
}
