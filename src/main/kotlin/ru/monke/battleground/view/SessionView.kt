package ru.monke.battleground.view

import kotlinx.serialization.Serializable
import ru.monke.battleground.domain.matchmaking.SessionStatus

@Serializable
data class SessionView(
    val id: String,
    val sessionStatus: SessionStatus
)