package ru.monke.battleground.api.stats

import ru.monke.battleground.domain.stats.PlayerStatistics
import ru.monke.battleground.view.PlayerStatisticsView

internal fun PlayerStatisticsView(stats: PlayerStatistics): PlayerStatisticsView {
    return PlayerStatisticsView(
        gameId = stats.gameId,
        damage = stats.damage,
        playersKilled = stats.playersKilled,
        isWinner = stats.isWinner
    )
}