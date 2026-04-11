package com.bashkevich.handler

import com.bashkevich.model.QuizEventDto
import com.bashkevich.service.CounterApiService
import com.bashkevich.service.QuizEventService
import com.bashkevich.service.UserCityCache
import io.github.dehuckakpyt.telegrambot.ext.container.chatId
import io.github.dehuckakpyt.telegrambot.factory.keyboard.inlineKeyboard
import io.github.dehuckakpyt.telegrambot.handling.BotHandling
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.mp.KoinPlatform

fun BotHandling.startCommand() {
    command("/start") {
        val cityCache = KoinPlatform.getKoin().get<UserCityCache>()

        if (cityCache.hasCity(chatId)) {
            val city = cityCache.getCity(chatId)
            sendMessage("Welcome back! Your city: $city", replyMarkup = inlineKeyboard(
                callbackButton("Change city", "change_city")
            ))
        } else {
            next("cities_list")
            sendMessage("Please select your city:", replyMarkup = inlineKeyboard(
                callbackButton("Minsk", "select_city", "Minsk"),
                callbackButton("Grodno", "select_city", "Grodno"),
                callbackButton("Gomel", "select_city", "Gomel")
            ))
        }
    }

    command("/register") {
        val cityCache = KoinPlatform.getKoin().get<UserCityCache>()
        val quizEventService = KoinPlatform.getKoin().get<QuizEventService>()

        val city = cityCache.getCity(chatId)

        try {
            val events = quizEventService.getQuizEvents(city)

            if (events.isEmpty()) {
                if (city != null) {
                    sendMessage("❌ Мероприятий в вашем городе пока нет")
                } else {
                    sendMessage("❌ Мероприятий пока нет. Укажите ваш город с помощью команды /change_city")
                }
            } else {
                val message = buildEventsMessage(events, city)
                sendMessage(message)
            }
        } catch (e: Exception) {
            sendMessage("❌ Произошла ошибка при получении списка мероприятий: ${e.message}")
        }
    }

    command("/change_city") {
        sendMessage("Select your new city:", replyMarkup = inlineKeyboard(
            callbackButton("Minsk", "select_city", "Minsk"),
            callbackButton("Grodno", "select_city", "Grodno"),
            callbackButton("Gomel", "select_city", "Gomel")
        ))
    }

    callback("change_city") {
        sendMessage("Select your new city:", replyMarkup = inlineKeyboard(
            callbackButton("Minsk", "select_city", "Minsk"),
            callbackButton("Grodno", "select_city", "Grodno"),
            callbackButton("Gomel", "select_city", "Gomel")
        ))
    }

    callback("select_city") {
        val city = transferredOrNull<String>()!!
        val cityCache = KoinPlatform.getKoin().get<UserCityCache>()

        cityCache.setCity(chatId, city)
        answerCallbackQuery(query.id, "You selected: $city")
        sendMessage("✅ City saved: $city\n\nYour city is now set to $city. You can continue using the bot.")
    }

    command("/help") {
        val cityCache = KoinPlatform.getKoin().get<UserCityCache>()
        val city = cityCache.getCity(chatId)

        val message = if (city != null) {
            """
            🏙 Ваш город: $city

            📋 Доступные команды:
            /start - Начать работу / изменить город
            /register - Регистрация на мероприятия
            /change_city - Изменить город
            /help - Эта справка

            Нажмите кнопку меню слева от поля ввода для быстрого доступа к командам.
            """.trimIndent()
        } else {
            """
            🏙 Город не выбран

            📋 Доступные команды:
            /start - Выбрать город
            /register - Регистрация на мероприятия
            /change_city - Изменить город
            /help - Эта справка

            Нажмите кнопку меню слева от поля ввода для быстрого доступа к командам.
            """.trimIndent()
        }

        sendMessage(message)
    }
//    step("get_counter_name") {
//        val counterName = text
//        val apiService = KoinPlatform.getKoin().get<CounterApiService>()
//
//        try {
//            val counter = apiService.createCounter(counterName)
//            sendMessage("✅ Counter created!\nName: ${counter.name}\nID: ${counter.id}")
//        } catch (e: Exception) {
//            sendMessage("❌ Failed to create counter: ${e.message}")
//        }
//    }
}

private fun buildEventsMessage(events: List<QuizEventDto>, userCity: String?): String {
    val header = if (userCity != null) {
        "🏙 Доступные мероприятия в $userCity:\n\n"
    } else {
        "🏙 Доступные мероприятия:\n\n"
    }

    val eventsList = events.mapIndexed { index, event ->
        """
        ${index + 1}. ${event.title}
           📅 ${event.quizDay.dateTime}
           📍 ${event.quizDay.city}
        """.trimIndent()
    }.joinToString("\n\n")

    return header + eventsList
}