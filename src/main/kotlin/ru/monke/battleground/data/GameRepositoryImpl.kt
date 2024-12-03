package ru.monke.battleground.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.monke.battleground.domain.game.GameRepository
import ru.monke.battleground.domain.game.models.Game

class GameRepositoryImpl: GameRepository {

    override val games = HashMap<String, MutableStateFlow<Game>>()

    override suspend fun insertGame(game: Game) {
        games.getOrPut(game.id) { MutableStateFlow(game) }
            .update { game }
    }

}