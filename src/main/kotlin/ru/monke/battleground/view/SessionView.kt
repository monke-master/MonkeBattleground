package ru.monke.battleground.view

import kotlinx.serialization.Serializable
import ru.monke.battleground.domain.session.SessionStatus

@Serializable
data class SessionView(
    val id: String,
    val sessionStatus: SessionStatus
)