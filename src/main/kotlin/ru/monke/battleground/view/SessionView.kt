package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class SessionView(
    val id: String,
    val sessionStatus: SessionStatusView
)

@Serializable
sealed class SessionStatusView {
    @Serializable
    data object WaitingForPlayers: SessionStatusView()

    @Serializable
    data class Started(
        val gameId: String
    ): SessionStatusView()
}
