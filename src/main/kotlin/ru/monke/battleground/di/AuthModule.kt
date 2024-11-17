package ru.monke.battleground.di

import org.koin.dsl.module
import ru.monke.battleground.data.AccountRepositoryImpl
import ru.monke.battleground.domain.auth.AccountRepository
import ru.monke.battleground.domain.auth.SignUpUseCase

val authModule = module {
    single<AccountRepository> { AccountRepositoryImpl() }
    single { SignUpUseCase(get()) }
}