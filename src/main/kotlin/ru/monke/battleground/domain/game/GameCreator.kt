package ru.monke.battleground.domain.game

import ru.monke.battleground.domain.game.models.*
import ru.monke.battleground.domain.matchmaking.model.Player
import ru.monke.battleground.domain.matchmaking.model.Team
import ru.monke.battleground.domain.session.Session
import java.util.UUID

class GameCreator {

    suspend fun createGame(session: Session): Game {
        val game = Game(
            id = UUID.randomUUID().toString(),
            gameMap = generateMap(),
            deathZone = null,
            teams = session.teams.map { it.toGameTeam() }
        )

        return game
    }

    private fun generateMap(): GameMap {
        val ammoItems = buildList {
            repeat(PICKABLE_AMMO_COUNT) {
                add(
                    PickableItem(
                        item = Ammo(
                            id = UUID.randomUUID().toString(),
                            count = AMMO_COUNT_RANGE.random(),
                            weaponType = randomWeaponType()
                        ),
                        coordinates = randomCoordinates()
                    )
                )
            }
        }

        val weaponTypes = buildList {
            repeat(PICKABLE_AMMO_COUNT) {
                val type = randomWeaponType()
                add(
                    PickableItem(
                        item = Weapon(
                            id = UUID.randomUUID().toString(),
                            clip = Clip(
                                maxAmmo = type.clipSize,
                                currentAmmo = type.clipSize
                            ),
                            weaponType = type
                        ),
                        coordinates = randomCoordinates()
                    )
                )
            }
        }

        return GameMap(
            pickableItems = ammoItems
        )
    }

}

private fun randomWeaponType(): WeaponType {
    return WeaponType.entries[(0 until WeaponType.entries.size).random()]
}

private fun randomCoordinates(): Coordinates {
    return Coordinates(
        x = X_RANGE.random().toFloat(),
        y = Y_RANGE.random().toFloat(),
        z = Z_RANGE.random().toFloat()
    )
}

private fun Team.toGameTeam(): GameTeam {
    return GameTeam(
        id = id,
        size = teamSize,
        gamePlayers = players.map { it.toGamePlayer() }
    )
}

private fun Player.toGamePlayer(): GamePlayer {
    return GamePlayer(
        id = id,
        account = account,
        health = MAX_HEALTH,
        coordinates = Coordinates(),
        statistics = Statistics(),
        inventory = Inventory(INVENTORY_SIZE)
    )
}