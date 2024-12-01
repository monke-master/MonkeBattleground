package ru.monke.battleground.domain.game

import kotlinx.coroutines.flow.StateFlow
import ru.monke.battleground.domain.game.models.Game

interface GameRepository {

    val games: Map<String, StateFlow<Game>>

    suspend fun insertGame(game: Game)
}