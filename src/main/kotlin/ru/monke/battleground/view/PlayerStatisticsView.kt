package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatisticsView(
    val gameId: String,
    val damage: Float,
    val playersKilled: Int,
    val isWinner: Boolean
)