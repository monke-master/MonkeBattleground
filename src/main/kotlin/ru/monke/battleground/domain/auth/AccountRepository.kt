package ru.monke.battleground.domain.auth

import ru.monke.battleground.domain.auth.model.Account

interface AccountRepository {

    suspend fun insertAccount(account: Account): Result<Any>

    suspend fun getAccountByEmail(email: String): Result<Account>

    suspend fun getAccountById(id: String): Result<Account?>
}