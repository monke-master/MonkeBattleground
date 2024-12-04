package ru.monke.battleground.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.monke.battleground.domain.stats.PlayerStatistics
import java.sql.Connection

class StatisticsDatastore(
    private val connection: Connection
) {

    companion object {
        private const val CREATE_TABLE_STATISTICS =
            "CREATE TABLE IF NOT EXISTS Statistics (" +
                    "id Serial PRIMARY KEY, " +
                    "is_winner BOOLEAN NOT NULL, " +
                    "damage REAL NOT NULL, " +
                    "players_killed INT NOT NULL, " +
                    "game_id VARCHAR(255) NOT NULL, " +
                    "account_id VARCHAR(255) NOT NULL, " +
                    "FOREIGN KEY (account_id) REFERENCES Accounts(id) );"
        private const val SELECT_STATS_BY_ID = "SELECT * FROM Statistics WHERE account_id = ?"
        private const val INSERT_STATS = "INSERT INTO Statistics (is_winner, damage, players_killed, game_id, account_id) VALUES (?, ?, ?, ?, ?)"
    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_STATISTICS)
    }

    suspend fun insertStatistics(statistics: PlayerStatistics) {
        withContext(Dispatchers.IO) {
            val statement = connection.prepareStatement(INSERT_STATS)
            statement.setBoolean(1, statistics.isWinner)
            statement.setFloat(2, statistics.damage)
            statement.setInt(3, statistics.playersKilled)
            statement.setString(4, statistics.gameId)
            statement.setString(5, statistics.accountId)
            statement.executeUpdate()
        }
    }

    suspend fun getPlayerStats(accountId: String): List<PlayerStatistics> {
        return withContext(Dispatchers.IO) {
            val statement = connection.prepareStatement(SELECT_STATS_BY_ID)
            statement.setString(1, accountId)
            val result = statement.executeQuery()

            val entities = mutableListOf<PlayerStatistics>()
            while (result.next()) {
                val isWinner = result.getBoolean(1)
                val damage = result.getFloat(2)
                val playersKilled = result.getInt(3)
                val gameId = result.getString(4)
                entities.add(PlayerStatistics(
                    isWinner = isWinner,
                    damage = damage,
                    playersKilled = playersKilled,
                    accountId = accountId,
                    gameId = gameId
                ))
            }
            entities
        }
    }
}