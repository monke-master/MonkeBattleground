package ru.monke.battleground.domain.game.models

data class Game(
    val id: String,
    val gameMap: GameMap,
    val deathZones: List<DeathZone>,
    val teams: List<GameTeam>,
    val gameStatus: GameStatus
)

sealed class GameStatus {

    data object Ongoing: GameStatus()

    data class End(
        val winnerTeamId: String
    ): GameStatus()
}