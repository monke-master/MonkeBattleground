package ru.monke.battleground.data

import ru.monke.battleground.domain.auth.model.Account
import ru.monke.battleground.domain.auth.error.AccountNotFoundException
import ru.monke.battleground.domain.auth.AccountRepository

class AccountRepositoryImpl(
    private val accountDatastore: AccountDatastore
): AccountRepository {

    override suspend fun insertAccount(account: Account): Result<Any> {
        return runCatching {
            accountDatastore.insertAccount(account)
        }
    }

    override suspend fun getAccountByEmail(email: String): Result<Account> {
        return runCatching {
            accountDatastore.getAccountWithEmail(email) ?: throw AccountNotFoundException()
        }
    }
}