package ru.monke.battleground.domain.stats

class GetStatisticsUseCase(
    private val statisticsRepository: StatisticsRepository
) {

    suspend fun execute(accountId: String) = statisticsRepository.getStatistics(accountId)
}