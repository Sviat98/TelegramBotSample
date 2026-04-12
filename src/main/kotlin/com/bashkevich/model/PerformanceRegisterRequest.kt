package com.bashkevich.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PerformanceRegisterRequest(
    @SerialName(value = "team_name")
    val teamName: String,
    @SerialName(value = "squad_size")
    val squadSize: Int = 0,
)