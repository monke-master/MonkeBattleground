package ru.monke.battleground.di

import org.koin.dsl.module
import ru.monke.battleground.data.MatchmakingRepositoryImpl
import ru.monke.battleground.domain.matchmaking.MatchmakingInteractor
import ru.monke.battleground.domain.matchmaking.MatchmakingRepository

val matchModule = module {
    single<MatchmakingRepository> { MatchmakingRepositoryImpl() }
    single { MatchmakingInteractor(get()) }
}