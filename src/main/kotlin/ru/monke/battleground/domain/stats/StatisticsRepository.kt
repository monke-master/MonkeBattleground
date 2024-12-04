package ru.monke.battleground.domain.stats

interface StatisticsRepository {

    suspend fun insertStatistics(playerStatistics: PlayerStatistics): Result<Any>

    suspend fun getStatistics(accountId: String): Result<List<PlayerStatistics>>


}