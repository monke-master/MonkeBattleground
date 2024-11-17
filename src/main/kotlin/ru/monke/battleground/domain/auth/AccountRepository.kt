package ru.monke.battleground.domain.auth

interface AccountRepository {

    suspend fun insertAccount(account: Account): Result<Any>

    suspend fun getAccountByEmail(email: String): Result<Account>
}