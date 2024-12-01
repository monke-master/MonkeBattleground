package ru.monke.battleground.domain.game.models

data class Game(
    val id: String,
    val gameMap: GameMap,
    val deathZone: DeathZone? = null,
    val teams: List<GameTeam>
)