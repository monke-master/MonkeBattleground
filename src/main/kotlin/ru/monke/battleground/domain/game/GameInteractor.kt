package ru.monke.battleground.domain.game

import ru.monke.battleground.domain.game.models.Coordinates
import ru.monke.battleground.domain.game.models.Game
import ru.monke.battleground.domain.game.models.InventoryItem
import ru.monke.battleground.domain.session.Session

class GameInteractor(
    private val gameRepository: GameRepository,
    private val gameCreator: GameCreator
) {

    suspend fun createGame(session: Session): String {
        val game = gameCreator.createGame(session)
        gameRepository.insertGame(game)
        startGame(game)
        return game.id
    }

    suspend fun pickItem(
        gameId: String,
        itemId: String,
        playerId: String,
        inventoryX: Int,
        inventoryY: Int,
    ): Result<Any> {
        return runCatching {
            val game = getGame(gameId)?.value ?: throw GameNotFoundError()
            val player = game.getPlayer(playerId) ?: throw EntityNotFoundException()
            val team = game.teams.find { it.gamePlayers.contains(player) } ?: throw EntityNotFoundException()

            val pickableItems = game.gameMap.pickableItems.toMutableList()
            val item = pickableItems.find { it.item.id == itemId } ?: throw EntityNotFoundException()
            val inventoryItem = InventoryItem(
                item = item.item,
                x = inventoryX,
                y = inventoryY
            )
            pickableItems.remove(item)

            val teamPlayers = team.gamePlayers.toMutableList()
            teamPlayers.remove(player)

            val inventory = player.inventory.copy(items = player.inventory.items + inventoryItem)
            teamPlayers.add(player.copy(inventory = inventory))

            val teams = game.teams.toMutableList()
            teams.remove(team)
            teams.add(team.copy(gamePlayers = teamPlayers))

            gameRepository.insertGame(game.copy(teams = teams, gameMap = game.gameMap.copy(pickableItems = pickableItems)))
        }

    }

    suspend fun move(
        gameId: String,
        playerId: String,
        coordinates: Coordinates
    ): Result<Any> {
        return runCatching {
            val game = getGame(gameId)?.value ?: throw GameNotFoundError()
            val player = game.getPlayer(playerId) ?: throw EntityNotFoundException()
            val team = game.teams.find { it.gamePlayers.contains(player) } ?: throw EntityNotFoundException()

            val players = team.gamePlayers.toMutableList()
            players.remove(player)
            players.add(player.copy(coordinates = coordinates))

            val teams = game.teams.toMutableList()
            teams.remove(team)
            teams.add(team.copy(gamePlayers = players))

            gameRepository.insertGame(game.copy(teams = teams))
        }
    }

    private fun Game.getPlayer(playerId: String) = teams.flatMap { it.gamePlayers }.find { it.id == playerId }

    fun getGame(gameId: String) = gameRepository.games[gameId]

    private suspend fun startGame(game: Game) {

    }
}