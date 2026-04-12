package com.bashkevich.model

enum class RegistrationStep {
    TEAM_NAME,
    SQUAD_SIZE
}

data class TeamRegistrationState(
    val quizId: String,
    val teamName: String? = null,
    val squadSize: Int? = null,
    val currentStep: RegistrationStep = RegistrationStep.TEAM_NAME
)
