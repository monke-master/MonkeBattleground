package ru.monke.battleground.di

import org.koin.dsl.module
import ru.monke.battleground.data.StatisticsDatastore
import ru.monke.battleground.data.StatisticsRepositoryImpl
import ru.monke.battleground.domain.auth.usecase.SignUpUseCase
import ru.monke.battleground.domain.stats.StatisticsRepository

val statisticsModule = module {
    single { StatisticsDatastore(get()) }
    single<StatisticsRepository> { StatisticsRepositoryImpl(get()) }
    single { SignUpUseCase(get()) }
}