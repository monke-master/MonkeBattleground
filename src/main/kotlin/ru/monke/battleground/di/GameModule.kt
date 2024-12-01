package ru.monke.battleground.di

import org.koin.dsl.module
import ru.monke.battleground.data.GameRepositoryImpl
import ru.monke.battleground.domain.game.GameCreator
import ru.monke.battleground.domain.game.GameInteractor
import ru.monke.battleground.domain.game.GameRepository

val gameModule = module {
    single<GameRepository> { GameRepositoryImpl() }
    single { GameCreator() }
    single { GameInteractor(get(), get()) }
}