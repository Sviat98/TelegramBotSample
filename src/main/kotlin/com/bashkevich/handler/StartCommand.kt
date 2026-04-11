package com.bashkevich.handler

import com.bashkevich.model.QuizEventDto
import com.bashkevich.service.CounterApiService
import com.bashkevich.service.QuizEventService
import com.bashkevich.service.UserCityCache
import io.github.dehuckakpyt.telegrambot.ext.container.chatId
import io.github.dehuckakpyt.telegrambot.factory.keyboard.inlineKeyboard
import io.github.dehuckakpyt.telegrambot.handling.BotHandling
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
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
                // Отправляем заголовок
                val header = if (city != null) {
                    "🏙 Доступные мероприятия в $city:\n\nВсего найдено: ${events.size}"
                } else {
                    "🏙 Доступные мероприятия:\n\nВсего найдено: ${events.size}"
                }
                sendMessage(header)

                // Отправляем каждое мероприятие отдельным сообщением
                events.forEach { event ->
                    val eventMessage = buildEventMessage(event)
                    val eventId = event.id ?: event.quizDay.id // Используем quizDay.id если event.id null
                    sendMessage(eventMessage, replyMarkup = inlineKeyboard(
                        callbackButton("Зарегистрироваться", "register_event", eventId)
                    ))
                }
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

    callback("register_event") {
        val eventId = transferredOrNull<String>()!!
        // Пока ничего не делаем, просто подтверждаем нажатие
        answerCallbackQuery(query.id, "Заявка на регистрацию принята")
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

private fun buildEventMessage(event: QuizEventDto): String {
    val formattedDateTime = formatToUtcPlus3(event.quizDay.dateTime)
    return """
        🎯 ${event.title}

        📅 Дата и время: $formattedDateTime
        📍 Город: ${event.quizDay.city}
    """.trimIndent()
}

@OptIn(FormatStringsInDatetimeFormats::class)
private fun formatToUtcPlus3(localDateTime: LocalDateTime): String {
    // Предполагаем, что входящее время в UTC, и конвертируем в UTC+3
    // Используем Instant для правильной конвертации
    val utcInstant = localDateTime.toInstant(TimeZone.of("UTC"))
    val utcPlus3DateTime = utcInstant.toLocalDateTime(TimeZone.of("UTC+3"))

    val formatter = LocalDateTime.Format {
        dayOfMonth()
        char('.')
        monthNumber()
        char('.')
        year()
        char(' ')
        hour()
        char(':')
        minute()
    }

    return formatter.format(utcPlus3DateTime)
}