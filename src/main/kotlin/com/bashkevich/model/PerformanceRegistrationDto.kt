package com.bashkevich.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PerformanceRegistrationDto(
    @SerialName("performance_id")
    val performanceId: String,
    @SerialName("team_name")
    val teamName: String,
    @SerialName("reg_password")
    val regPassword: String,
    @SerialName("city")
    val city: String,
    @SerialName("date_time")
    val dateTime: LocalDateTime,
)
