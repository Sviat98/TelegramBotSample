package com.bashkevich.plugin

import com.bashkevich.handler.startCommand
import io.github.dehuckakpyt.telegrambot.ext.config.receiver.handling
import io.github.dehuckakpyt.telegrambot.ext.config.receiver.longPolling
import io.github.dehuckakpyt.telegrambot.plugin.TelegramBot
import io.ktor.server.application.*

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
}
