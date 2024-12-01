package ru.monke.battleground.domain.session

import ru.monke.battleground.domain.matchmaking.model.Team
import ru.monke.battleground.domain.matchmaking.model.TeamSize

data class Session(
    val id: String,
    val teamSize: TeamSize,
    val teams: List<Team>,
    val sessionStatus: SessionStatus
)

enum class SessionStatus {
    WAITING_FOR_PLAYERS, STARTED
}