package ru.monke.battleground.di

import org.koin.dsl.module
import ru.monke.battleground.data.SessionRepositoryImpl
import ru.monke.battleground.data.TeamRepositoryImpl
import ru.monke.battleground.domain.matchmaking.MatchmakingInteractor
import ru.monke.battleground.domain.matchmaking.TeamRepository
import ru.monke.battleground.domain.session.SessionRepository

val matchModule = module {
    single<SessionRepository> { SessionRepositoryImpl() }
    single<TeamRepository> { TeamRepositoryImpl() }
    single { MatchmakingInteractor(get(), get()) }
}