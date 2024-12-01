package ru.monke.battleground.domain.auth.usecase

import ru.monke.battleground.domain.auth.AccountRepository
import ru.monke.battleground.domain.auth.error.AccountNotFoundException

class ValidateAccountUseCase(
    private val accountRepository: AccountRepository
) {

    suspend fun execute(accountId: String): Result<Any> {
        val account = accountRepository.getAccountById(accountId).getOrNull() ?: return Result.failure(AccountNotFoundException())
        return Result.success(account)
    }
}