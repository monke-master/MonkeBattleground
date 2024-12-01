package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class TeamReadyView(
    val sessionId: String
)