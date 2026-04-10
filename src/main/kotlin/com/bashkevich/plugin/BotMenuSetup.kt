package com.bashkevich.plugin

import io.github.dehuckakpyt.telegrambot.TelegramBot
import io.github.dehuckakpyt.telegrambot.model.telegram.BotCommand
import io.github.dehuckakpyt.telegrambot.model.telegram.MenuButtonCommands
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun setupBotMenu(bot: TelegramBot, scope: CoroutineScope) {
    scope.launch {
        bot.setMyCommands(
            listOf(
                BotCommand("start", "Запустить бота"),
                BotCommand("change_city", "Изменить город"),
                BotCommand("help", "Помощь")
            )
        )
        bot.setChatMenuButton(
            chatId = null,
            menuButton = MenuButtonCommands()
        )
    }
}
