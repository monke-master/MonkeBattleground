package ru.monke.battleground.domain.auth

import java.util.UUID

class SignUpUseCase(
    private val accountRepository: AccountRepository
) {

    suspend fun execute(
        email: String,
        password: String,
        nickname: String
    ): Result<String> {
        val id = UUID.randomUUID().toString()
        val account = Account(
            id = id,
            email = email,
            password = password,
            nickname = nickname
        )
        val result = accountRepository.insertAccount(account)
        if (result.isSuccess) {
            return Result.success(id)
        }
        return Result.failure(result.exceptionOrNull()!!)
    }
}