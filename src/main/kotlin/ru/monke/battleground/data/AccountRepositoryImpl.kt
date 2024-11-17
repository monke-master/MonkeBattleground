package ru.monke.battleground.data

import ru.monke.battleground.domain.auth.Account
import ru.monke.battleground.domain.auth.AccountRepository

class AccountRepositoryImpl: AccountRepository {

    private val accounts = mutableListOf<Account>()

    override suspend fun insertAccount(account: Account): Result<Any> {
        return runCatching {
            accounts.add(account)
        }
    }

    override suspend fun getAccountByEmail(email: String): Result<Account> {
        return runCatching {
            accounts.find { it.email == email }!!
        }
    }


}