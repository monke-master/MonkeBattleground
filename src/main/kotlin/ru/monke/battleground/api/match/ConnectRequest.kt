package ru.monke.battleground.api.match

import kotlinx.serialization.Serializable
import ru.monke.battleground.domain.matchmaking.model.TeamSize

@Serializable
data class ConnectRequest(
    val teamSize: TeamSize
)