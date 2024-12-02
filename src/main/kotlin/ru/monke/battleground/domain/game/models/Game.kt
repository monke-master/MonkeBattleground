package ru.monke.battleground.domain.game.models

data class Game(
    val id: String,
    val gameMap: GameMap,
    val deathZones: List<DeathZone>,
    val teams: List<GameTeam>
)