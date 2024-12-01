package ru.monke.battleground.domain.game.models

import ru.monke.battleground.domain.matchmaking.model.TeamSize

data class GameTeam(
    val id: String,
    val size: TeamSize,
    val gamePlayers: List<GamePlayer>
)