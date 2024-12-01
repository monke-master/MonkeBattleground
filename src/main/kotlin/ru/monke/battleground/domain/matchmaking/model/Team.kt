package ru.monke.battleground.domain.matchmaking.model

import ru.monke.battleground.domain.session.Session

data class Team(
    val id: String,
    val invitationCode: String,
    val session: Session? = null,
    val players: List<Player>,
    val isReady: Boolean = false,
    val teamSize: TeamSize
)