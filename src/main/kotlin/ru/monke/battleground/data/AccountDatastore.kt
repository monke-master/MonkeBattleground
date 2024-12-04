package ru.monke.battleground.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.monke.battleground.domain.auth.model.Account
import java.sql.Connection

class AccountDatastore(
    private val connection: Connection
) {

    companion object {
        private const val CREATE_TABLE_ACCOUNTS =
            "CREATE TABLE IF NOT EXISTS Accounts (" +
                    "id UUID PRIMARY KEY, " +
                    "email VARCHAR(255) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "nickname VARCHAR(255) NOT NULL" +
            ");"
        private const val SELECT_ACCOUNT_BY_EMAIL = "SELECT * FROM Accounts WHERE email = ?"
        private const val SELECT_ACCOUNT_BY_ID = "SELECT * FROM Accounts WHERE id = ?"
        private const val INSERT_ACCOUNT = "INSERT INTO Accounts (id, email, password, nickname) VALUES (?, ?, ?, ?)"
        private const val DELETE_ACCOUNT = "DELETE FROM Accounts WHERE id = ?"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_ACCOUNTS)
    }

    suspend fun insertAccount(account: Account) {
        withContext(Dispatchers.IO) {
            val statement = connection.prepareStatement(INSERT_ACCOUNT)
            statement.setString(1, account.id)
            statement.setString(2, account.email)
            statement.setString(3, account.password)
            statement.setString(4, account.nickname)
            statement.executeUpdate()
        }
    }

    suspend fun getAccountWithEmail(email: String): Account? {
        return withContext(Dispatchers.IO) {
            val statement = connection.prepareStatement(SELECT_ACCOUNT_BY_EMAIL)
            statement.setString(1, email)
            val result = statement.executeQuery()

            if (result.next()) {
                val id = result.getString(1)
                val password = result.getString(3)
                val nickname = result.getString(4)
                return@withContext Account(
                    id = id,
                    email = email,
                    password = password,
                    nickname = nickname
                )
            } else {
                return@withContext null
            }
        }
    }

    suspend fun getAccountWithId(id: String): Account? {
        return withContext(Dispatchers.IO) {
            val statement = connection.prepareStatement(SELECT_ACCOUNT_BY_ID)
            statement.setString(1, id)
            val result = statement.executeQuery()

            if (result.next()) {
                val email = result.getString(2)
                val password = result.getString(3)
                val nickname = result.getString(4)
                return@withContext Account(
                    id = id,
                    email = email,
                    password = password,
                    nickname = nickname
                )
            } else {
                return@withContext null
            }
        }
    }

    suspend fun deleteAccount(id: String) {
        withContext(Dispatchers.IO) {
            val statement = connection.prepareStatement(DELETE_ACCOUNT)
            statement.setString(1, id)
            statement.executeUpdate()

        }
    }
}