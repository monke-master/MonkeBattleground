package ru.monke.battleground.di

import org.koin.dsl.module
import ru.monke.battleground.data.AccountDatastore
import ru.monke.battleground.data.AccountRepositoryImpl
import ru.monke.battleground.domain.auth.AccountRepository
import ru.monke.battleground.domain.auth.usecase.SignInUseCase
import ru.monke.battleground.domain.auth.usecase.SignUpUseCase

val authModule = module {
    single { AccountDatastore(get()) }
    single<AccountRepository> { AccountRepositoryImpl(get()) }
    single { SignUpUseCase(get()) }
    single { SignInUseCase(get()) }
}