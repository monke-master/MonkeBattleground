package ru.monke.battleground.domain.matchmaking

data class Session(
    val id: String,
    val teamSize: TeamSize,
    val teams: List<Team>,
    val sessionStatus: SessionStatus
)

enum class SessionStatus {
    WAITING_FOR_PLAYERS, STARTED
}