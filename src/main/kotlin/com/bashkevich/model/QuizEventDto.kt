package com.bashkevich.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizWeekInfoDto(
    @SerialName(value = "id")
    val id: String,
    @SerialName(value = "title")
    val title: String,
)

@Serializable
data class QuizEventDto(
    @SerialName(value = "id")
    val id: String,
    @SerialName(value = "season_number")
    val seasonNumber: Int = 0,
    @SerialName(value = "quiz_week")
    val quizWeek: QuizWeekInfoDto,
    @Contextual
    @SerialName(value = "date_time")
    val dateTime: LocalDateTime,
    @SerialName(value = "status")
    val status: Status,
    @SerialName(value = "registration_open")
    val registrationOpen: Boolean,
    @SerialName(value = "city")
    val city: String,
    @Contextual
    @SerialName(value = "registration_time_begin")
    val registrationTimeBegin: LocalDateTime,
)

enum class Status {
    NOT_STARTED, IN_PROGRESS, COMPLETED
}
