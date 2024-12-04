package ru.monke.battleground.domain.stats

data class PlayerStatistics(
    val gameId: String,
    val isWinner: Boolean,
    val damage: Float,
    val playersKilled: Int,
    val accountId: String
)
