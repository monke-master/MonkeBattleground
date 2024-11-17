package ru.monke.battleground.domain.matchmaking

data class Team(
    val id: String,
    val invitationCode: String,
    val session: Session? = null,
    val players: List<Player>,
    val isReady: Boolean = false,
    val teamSize: TeamSize
)