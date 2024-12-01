package ru.monke.battleground.domain.game

import ru.monke.battleground.domain.game.models.Game
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


    fun getGame(gameId: String) = gameRepository.games[gameId]

    private suspend fun startGame(game: Game) {

    }
}