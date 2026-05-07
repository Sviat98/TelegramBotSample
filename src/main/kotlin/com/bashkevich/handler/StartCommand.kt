package com.bashkevich.handler

import com.bashkevich.model.QuizEventDto
import com.bashkevich.model.RegistrationStep
import com.bashkevich.model.TeamRegistrationState
import com.bashkevich.service.QuizEventService
import com.bashkevich.service.TeamRegistrationCache
import com.bashkevich.service.UserCityCache
import io.github.dehuckakpyt.telegrambot.ext.container.chatId
import io.github.dehuckakpyt.telegrambot.factory.keyboard.inlineKeyboard
import io.github.dehuckakpyt.telegrambot.handling.BotHandling
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
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
                    val eventId = event.id
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
        val registrationCache = KoinPlatform.getKoin().get<TeamRegistrationCache>()

        // Создаем состояние регистрации
        val registrationState = TeamRegistrationState(
            quizId = eventId,
            currentStep = RegistrationStep.TEAM_NAME
        )
        registrationCache.setState(chatId, registrationState)

        answerCallbackQuery(query.id, "Регистрация начата")
        next("registration_team_name")
        sendMessage("📝 Шаг 1 из 2\n\nВведите название команды (до 30 символов):")
    }

    // Шаг 1: Ввод названия команды
    step("registration_team_name", next = "registration_squad_size") {
        val teamName = text

        when {
            teamName.isBlank() -> {
                sendMessage("❌ Название не может быть пустым. Пожалуйста, введите название команды:")
                next("registration_team_name")
                return@step
            }
            teamName.length > 30 -> {
                sendMessage("❌ Название не должно превышать 30 символов. Текущая длина: ${teamName.length}. Пожалуйста, введите название команды:")
                next("registration_team_name")
                return@step
            }
            else -> {
                // Сохраняем название команды в кеше
                val registrationCache = KoinPlatform.getKoin().get<TeamRegistrationCache>()
                registrationCache.updateState(chatId) { currentState ->
                    currentState.copy(
                        teamName = teamName.trim(),
                        currentStep = RegistrationStep.SQUAD_SIZE
                    )
                }

                sendMessage("✅ Название принято: ${teamName.trim()}\n\n📝 Шаг 2 из 2\n\nВведите количество человек в команде (от 2 до 10):")
            }
        }
    }

    // Шаг 2: Ввод количества человек
    step("registration_squad_size") {
        val registrationCache = KoinPlatform.getKoin().get<TeamRegistrationCache>()
        val quizEventService = KoinPlatform.getKoin().get<QuizEventService>()
        val squadSizeStr = text
        val squadSize = squadSizeStr.toIntOrNull()

        when {
            squadSize == null -> {
                sendMessage("❌ Пожалуйста, введите число. Количество человек должно быть от 2 до 10:")
                next("registration_squad_size")
                return@step
            }
            squadSize !in 2..10 -> {
                sendMessage("❌ Количество человек должно быть от 2 до 10. Пожалуйста, введите корректное число:")
                next("registration_squad_size")
                return@step
            }
            else -> {
                try {
                    val state = registrationCache.getState(chatId)!!
                    val updatedState = state.copy(squadSize = squadSize)
                    registrationCache.setState(chatId, updatedState)

                    val request = com.bashkevich.model.PerformanceRegisterRequest(
                        teamName = updatedState.teamName!!,
                        squadSize = squadSize
                    )

                    val registrationResult = quizEventService.registerTeam(updatedState.quizId, request)

                    registrationCache.clearState(chatId)

                    val formattedDateTime = formatToUtcPlus3(registrationResult.dateTime)
                    val message = """
                        ✅ Регистрация успешно завершена!

                        📋 Детали регистрации:
                        Команда: ${registrationResult.teamName}
                        Количество человек: $squadSize
                        🕒 Дата регистрации: ${formatToUtcPlus3(registrationResult.registeredAt, includeSeconds = true)}
                        📍 Город: ${registrationResult.city}
                        📅 Дата и время: $formattedDateTime

                        🔑 Ваш пароль для регистрации: <code>${registrationResult.regPassword}</code>
                    """.trimIndent()

                    sendMessage(message, parseMode = "HTML")
                } catch (e: Exception) {
                    registrationCache.clearState(chatId)
                    sendMessage("❌ Произошла ошибка при регистрации: ${e.message}\n\nПопробуйте зарегистрироваться позже.")
                }
            }
        }
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
}

private fun buildEventMessage(event: QuizEventDto): String {
    val formattedDateTime = formatToUtcPlus3(event.dateTime)
    return """
        🎯 ${event.quizWeek.title}

        📅 Дата и время: $formattedDateTime
        📍 Город: ${event.city}
    """.trimIndent()
}

@OptIn(FormatStringsInDatetimeFormats::class)
private fun formatToUtcPlus3(localDateTime: LocalDateTime, includeSeconds: Boolean = false): String {
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
        if (includeSeconds) {
            char(':')
            second()
        }
    }

    return formatter.format(utcPlus3DateTime)
}