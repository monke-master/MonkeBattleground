package ru.monke.battleground.domain.auth.usecase

import ru.monke.battleground.domain.auth.AccountRepository
import ru.monke.battleground.domain.auth.error.WrongPasswordException
import ru.monke.battleground.domain.auth.model.Account

class SignInUseCase(
    private val accountRepository: AccountRepository
) {

    suspend fun execute(
        email: String,
        password: String
    ): Result<Account> {
        val account = accountRepository.getAccountByEmail(email).getOrElse { return Result.failure(it) }

        if (account.password != password) {
            return Result.failure(WrongPasswordException())
        }

        return Result.success(account)
    }
}