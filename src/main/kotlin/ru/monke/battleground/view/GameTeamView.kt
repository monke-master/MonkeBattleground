package ru.monke.battleground.view

import kotlinx.serialization.Serializable
import ru.monke.battleground.domain.game.models.GamePlayer
import ru.monke.battleground.domain.matchmaking.model.TeamSize

@Serializable
data class GameTeamView(
    val id: String,
    val size: TeamSize,
    val gamePlayers: List<PlayerView>
)