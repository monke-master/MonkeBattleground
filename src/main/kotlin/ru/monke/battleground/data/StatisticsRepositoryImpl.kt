package ru.monke.battleground.data

import ru.monke.battleground.domain.stats.PlayerStatistics
import ru.monke.battleground.domain.stats.StatisticsRepository

class StatisticsRepositoryImpl(
    private val statisticsDatastore: StatisticsDatastore
): StatisticsRepository {

    override suspend fun insertStatistics(playerStatistics: PlayerStatistics): Result<Any> {
        return runCatching {
            statisticsDatastore.insertStatistics(playerStatistics)
        }
    }

    override suspend fun getStatistics(accountId: String): Result<List<PlayerStatistics>> {
        return runCatching {
            statisticsDatastore.getPlayerStats(accountId)
        }
    }

}