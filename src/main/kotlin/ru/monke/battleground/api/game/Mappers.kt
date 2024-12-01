package ru.monke.battleground.api.game

import ru.monke.battleground.domain.game.models.*
import ru.monke.battleground.domain.matchmaking.model.Player
import ru.monke.battleground.domain.session.Session
import ru.monke.battleground.domain.session.SessionStatus
import ru.monke.battleground.view.*
import ru.monke.battleground.view.GameView
import ru.monke.battleground.view.PickableItemView
import kotlin.math.max

fun GameView(game: Game): GameView {
    return GameView(
        id = game.id,
        mapView = MapView(game.gameMap),
        deathZoneView = game.deathZone?.let { DeathZoneView(it) },
        teams = game.teams.map { GameTeamView(it) }
    )
}

private fun GameTeamView(team: GameTeam): GameTeamView {
    return GameTeamView(
        id = team.id,
        size = team.size,
        gamePlayers = team.gamePlayers.map { PlayerView(it) }
    )
}

private fun PlayerView(player: GamePlayer): PlayerView {
    return PlayerView(
        id = player.id,
        health = player.health,
        coordinates = CoordinatesView(player.coordinates),
        inventory = InventoryView(player.inventory),
        statistics = StatisticsView(
            damage = player.statistics.damage,
            playersKilled = player.statistics.playersKilled
        )
    )
}

private fun InventoryView(inventory: Inventory): InventoryView {
    return InventoryView(
        maxSize = inventory.maxSize,
        items = inventory.items.map { InventoryItemView(item = ItemView(it.item), x = it.x, y = it.y) }
    )
}



private fun DeathZoneView(zone: DeathZone): DeathZoneView {
    return DeathZoneView(
        center = CoordinatesView(zone.center),
        radius = zone.radius
    )
}

private fun MapView(map: GameMap): MapView {
    return MapView(
        pickableItems = map.pickableItems.map { PickableItemView(it) }
    )
}

private fun PickableItemView(item: PickableItem): PickableItemView {
    return PickableItemView(
        item = ItemView(item.item),
        coordinates = CoordinatesView(item.coordinates)
    )
}

private fun CoordinatesView(coordinates: Coordinates): CoordinatesView {
    return CoordinatesView(
        x = coordinates.x,
        y = coordinates.y,
        z = coordinates.z
    )
}

private fun ItemView(item: Item): ItemView {
    return when (item) {
        is Ammo -> AmmoView(item.id, item.count, item.weaponType)
        is Weapon -> WeaponView(item.id, item.weaponType, ClipView(item.clip))
    }
}

private fun ClipView(clip: Clip): ClipView {
    return ClipView(
        maxAmmo = clip.maxAmmo,
        currentAmmo = clip.currentAmmo
    )
}

fun SessionStatusView(status: SessionStatus): SessionStatusView {
    return when (status) {
        is SessionStatus.Started -> SessionStatusView.Started(status.gameId)
        SessionStatus.WaitingForPlayers -> SessionStatusView.WaitingForPlayers
    }
}