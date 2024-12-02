package ru.monke.battleground.server

import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.mp.KoinPlatform
import ru.monke.battleground.domain.auth.usecase.SignUpUseCase
import ru.monke.battleground.domain.game.GameInteractor
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

        println(generateJWT(accountId))

        val matchmakingInteractor: MatchmakingInteractor = KoinPlatform.getKoin().get()

        val teamCode = matchmakingInteractor.connect(accountId, TeamSize.Solo)
        matchmakingInteractor.setTeamReady(teamCode)

        val sessionId = matchmakingInteractor.findSessionForTeam(teamCode).getOrThrow()
        val session = matchmakingInteractor.getSession(sessionId)

        val gameInteractor: GameInteractor = KoinPlatform.getKoin().get()

        CoroutineScope(Dispatchers.Default).launch {
            session?.collect {
                println("Session started: ${it.sessionStatus}")
                val status = it.sessionStatus
                if (status is SessionStatus.Started) {
                    gameInteractor.getGame(status.gameId)?.collect {
                        println("Game started: ${it}")
                    }
                }
            }
        }
    }


}