package ru.monke.battleground.domain.auth.usecase

import ru.monke.battleground.domain.auth.AccountRepository

class DeleteAccountUseCase(
    private val accountRepository: AccountRepository
) {

    suspend fun execute(id: String): Result<Any> {
        return accountRepository.deleteAccount(id)
    }
}