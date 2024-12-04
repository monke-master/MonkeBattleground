package ru.monke.battleground.domain.game

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.monke.battleground.domain.game.models.*
import ru.monke.battleground.domain.session.Session
import ru.monke.battleground.domain.stats.PlayerStatistics
import ru.monke.battleground.domain.stats.StatisticsRepository
import kotlin.math.max

class GameInteractor(
    private val gameRepository: GameRepository,
    private val gameCreator: GameCreator,
    private val statisticsRepository: StatisticsRepository
) {

    private val mutex = Mutex()

    suspend fun createGame(session: Session): String {
        val game = gameCreator.createGame(session)
        gameRepository.insertGame(game)
        startGame(game.id)
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

    suspend fun shoot(
        gameId: String,
        playerId: String,
        weaponId: String,
        targetId: String
    ): Result<Any> {
        return runCatching {
            val game = getGame(gameId)?.value ?: throw GameNotFoundError()
            var player = game.getPlayer(playerId) ?: throw EntityNotFoundException()
            var team = game.teams.find { it.gamePlayers.contains(player) } ?: throw EntityNotFoundException()

            val items = player.inventory.items.toMutableList()
            var weapon: Weapon = items.find { it.item is Weapon && it.item.id == weaponId }?.item as? Weapon ?: throw EntityNotFoundException()

            if (weapon.clip.currentAmmo == 0) return Result.success(Unit)
            weapon = weapon.copy(clip = weapon.clip.copy(currentAmmo = weapon.clip.currentAmmo - 1))

            var target = game.getPlayer(targetId) ?: throw EntityNotFoundException()
            var targetTeam = game.teams.find { it.gamePlayers.contains(target) } ?: throw EntityNotFoundException()
            if (target.health == 0) return Result.success(Unit)

            target = target.copy(health = max(target.health - weapon.weaponType.damage, 0))

            if (target.health <= 0) {
                player = player.copy(
                    statistics = Statistics(
                        playersKilled = player.statistics.playersKilled + 1,
                        damage = player.statistics.damage + weapon.weaponType.damage
                    )
                )
            }

            val weaponItem = items.find { it.item.id == weaponId } ?: throw EntityNotFoundException()
            items.remove(weaponItem)
            items.add(InventoryItem(weaponItem.x, weaponItem.y, weapon))

            player = player.copy(
                inventory = player.inventory.copy(items = items)
            )

            val teamPlayers = team.gamePlayers.toMutableList()
            teamPlayers.removeAll{ it.id == playerId }
            teamPlayers.add(player)

            val targetTeamPlayers = targetTeam.gamePlayers.toMutableList()
            targetTeamPlayers.removeAll { it.id == targetId }
            targetTeamPlayers.add(target)

            targetTeam = targetTeam.copy(gamePlayers = targetTeamPlayers)
            team = team.copy(gamePlayers = teamPlayers)

            val teams = game.teams.toMutableList()
            teams.removeAll { it.id == team.id }
            teams.removeAll { it.id == targetTeam.id }
            teams.add(targetTeam)
            teams.add(team)

            gameRepository.insertGame(game.copy(teams = teams))
        }
    }

    private fun Game.getPlayer(playerId: String) = teams.flatMap { it.gamePlayers }.find { it.id == playerId }

    fun getGame(gameId: String) = gameRepository.games[gameId]

    private suspend fun startGame(gameId: String) {
       //  startZoneCycle(gameId)
        CoroutineScope(currentCoroutineContext()).launch {
            getGame(gameId)?.collect { game ->
                if (game.gameStatus is GameStatus.End) return@collect
                val deadTeams = game.getDeadTeams()

                if (game.teams.size - 1 == deadTeams) {
                    game.getWinner()?.let { winner ->
                        gameRepository.insertGame(game.copy(gameStatus = GameStatus.End(winner.id)))
                        saveStatistics(game, winner)
                    }
                }
            }
        }
    }

    private suspend fun saveStatistics(
        game: Game,
        winnerTeam: GameTeam
    ) {
        game.teams.flatMap { it.gamePlayers }.forEach { player ->
            val statistics = PlayerStatistics(
                gameId = game.id,
                isWinner = player in winnerTeam.gamePlayers,
                accountId = player.account.id,
                damage = player.statistics.damage,
                playersKilled = player.statistics.playersKilled
            )

            statisticsRepository.insertStatistics(statistics)
        }
    }

    private fun Game.getWinner() = teams.find { !it.gamePlayers.all { it.health <= 0 } }

    private fun Game.getDeadTeams(): Int = teams.count { it.gamePlayers.all { it.health <= 0 } }

    private suspend fun startZoneCycle(gameId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            addZone(gameId)
            val game = getGame(gameId)?.value ?: throw GameNotFoundError()

            if (game.deathZones.size < MAX_ZONE_COUNT) {
                delay(DEATH_ZONE_DELAY_MS)
                startZoneCycle(gameId)
            }
        }
    }

    private suspend fun addZone(gameId: String) {
        mutex.withLock {
            val zone = DeathZone(
                level = DeathZoneLevel.FIRST,
                center = randomCoordinates()
            )
            val game = getGame(gameId)?.value ?: return@withLock
            val updatedZones = game.deathZones + zone
            gameRepository.insertGame(game.copy(deathZones = updatedZones))
            CoroutineScope(currentCoroutineContext()).launch {
                delay(DEATH_ZONE_DELAY_MS)
                upgradeZone(gameId, updatedZones.lastIndex)
            }
        }
    }

    private suspend fun upgradeZone(
        gameId: String,
        zoneIndex: Int
    ) {
        mutex.withLock {
            val game = getGame(gameId)?.value ?: return
            val zone = game.deathZones[zoneIndex]

            if (zone.level == DeathZoneLevel.FIFTH) return

            val zones = game.deathZones.toMutableList()

            zones[zoneIndex] = DeathZone(
                center = zone.center,
                level = DeathZoneLevel.entries[DeathZoneLevel.entries.indexOf(zone.level) + 1]
            )

            gameRepository.insertGame(game.copy(deathZones = zones))

            CoroutineScope(currentCoroutineContext()).launch {
                delay(DEATH_ZONE_DELAY_MS)
                upgradeZone(gameId, zoneIndex)
            }
        }

    }
}