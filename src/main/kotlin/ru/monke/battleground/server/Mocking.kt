package ru.monke.battleground.server

import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.auth.usecase.SignUpUseCase
import ru.monke.battleground.domain.game.GameInteractor
import ru.monke.battleground.domain.game.models.Weapon
import ru.monke.battleground.domain.matchmaking.MatchmakingInteractor
import ru.monke.battleground.domain.matchmaking.model.TeamSize
import ru.monke.battleground.domain.session.SessionStatus

fun Route.mockData() {
    runBlocking {
        val signUpUseCase: SignUpUseCase = KoinPlatform.getKoin().get()

        val accountId = signUpUseCase.execute(
            email = "igoryamba@gmail.com",
            password = "gspd",
            nickname = "BimboBoy"
        ).getOrThrow()

        signUpUseCase.execute(
            email = "Buldiga",
            password = "Fascia",
            nickname = "Dzerzh"
        ).getOrThrow()

        println(generateJWT(accountId))

        val matchmakingInteractor: MatchmakingInteractor = KoinPlatform.getKoin().get()

        val teamCode = matchmakingInteractor.connect(accountId, TeamSize.Solo)
        val teamCode2 = matchmakingInteractor.connect(accountId, TeamSize.Solo)
        matchmakingInteractor.setTeamReady(teamCode)
        matchmakingInteractor.setTeamReady(teamCode2)

        val sessionId = matchmakingInteractor.findSessionForTeam(teamCode).getOrThrow()
        matchmakingInteractor.findSessionForTeam(teamCode2)
        val sessionFlow = matchmakingInteractor.getSession(sessionId)

        val gameInteractor: GameInteractor = KoinPlatform.getKoin().get()

        CoroutineScope(Dispatchers.Default).launch {
            sessionFlow?.collect {
                println("Session started: ${it.sessionStatus}")
                val status = it.sessionStatus
                if (status is SessionStatus.Started) {

                    CoroutineScope(Dispatchers.Default).launch {
                        gameInteractor.getGame(status.gameId)?.collect { game ->
                            println("Game: ${game}")
                            println("death zlones: ${game.deathZones.size}")

                            val weapon = game.gameMap.pickableItems.find { it.item is Weapon }
                            val player1 = game.teams[0].gamePlayers[0]
                            val player2 = game.teams[1].gamePlayers[0]
                            weapon?.let {
                                gameInteractor.pickItem(
                                    gameId = game.id,
                                    playerId = player1.id,
                                    itemId = weapon.item.id,
                                    inventoryY = 1,
                                    inventoryX = 1
                                )
                            }
//
//                            if (player2.inventory.items.isNotEmpty()) {
//                                val picked = player2.inventory.items[0].item as Weapon
//
//                                gameInteractor.shoot(
//                                    gameId = game.id,
//                                    playerId = player2.id,
//                                    weaponId = picked.id,
//                                    targetId = player1.id
//                                )
//                            }
                        }
                    }
                }
            }
        }

    }


}