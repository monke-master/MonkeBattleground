package ru.monke.battleground.domain.matchmaking

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.monke.battleground.domain.auth.AccountRepository
import ru.monke.battleground.domain.auth.error.AccountNotFoundException
import ru.monke.battleground.domain.auth.model.Account
import ru.monke.battleground.domain.game.GameInteractor
import ru.monke.battleground.domain.game.GameRepository
import ru.monke.battleground.domain.game.INVENTORY_SIZE
import ru.monke.battleground.domain.game.MAX_HEALTH
import ru.monke.battleground.domain.game.models.*
import ru.monke.battleground.domain.matchmaking.error.FullTeamException
import ru.monke.battleground.domain.matchmaking.error.TeamNotFoundException
import ru.monke.battleground.domain.matchmaking.model.*
import ru.monke.battleground.domain.session.*
import java.util.*

class MatchmakingInteractor(
    private val teamRepository: TeamRepository,
    private val sessionRepository: SessionRepository,
    private val gameInteractor: GameInteractor,
    private val accountRepository: AccountRepository
) {

    private val startingSessions = mutableListOf<String>()

    suspend fun connect(
        accountId: String,
        teamSize: TeamSize
    ): String {
        val account = accountRepository.getAccountById(accountId).getOrNull() ?: throw AccountNotFoundException()
        val code = generateSixSymbolCode().capitalize()
        if (teamRepository.getTeamByCode(code) != null) {
            return connect(accountId, teamSize)
        }

        val teamId = UUID.randomUUID().toString()
        val player = Player(
            account = account,
            teamId = teamId,
            id = UUID.randomUUID().toString()
        )
        val team = Team(
            id = teamId,
            invitationCode = code,
            players = listOf(player),
            teamSize = teamSize
        )

        teamRepository.insertTeam(team)
        return code
    }

    suspend fun connectToTeam(
        accountId: String,
        teamCode: String
    ): Result<Any> {
        val team = teamRepository.getTeamByCode(teamCode) ?: return Result.failure(TeamNotFoundException())
        val account = accountRepository.getAccountById(accountId).getOrNull() ?: return Result.failure(AccountNotFoundException())

        if (team.players.size == team.teamSize.size) {
            return Result.failure(FullTeamException())
        }

        val player = Player(
            account = account,
            teamId = team.id,
            id = UUID.randomUUID().toString()
        )
        teamRepository.insertTeam(team.copy(players = team.players + player))
        return Result.success(Unit)
    }

    suspend fun setTeamReady(teamCode: String): Result<Any> {
        val team = teamRepository.getTeamByCode(teamCode) ?: return Result.failure(TeamNotFoundException())

        teamRepository.insertTeam(team.copy(isReady = true))
        return Result.success(Unit)
    }

    suspend fun findSessionForTeam(teamCode: String): Result<String> {
        val team = teamRepository.getTeamByCode(teamCode) ?: return Result.failure(TeamNotFoundException())

        val sessions = sessionRepository.sessions.values.map { it.value }

        var appropriateSession = sessions
            .filter {
                it.sessionStatus == SessionStatus.WaitingForPlayers &&
                it.teams.size + 1 <= MAX_PLAYERS / it.teamSize.size
            }
            .maxByOrNull { it.teams.size }

        if (appropriateSession == null) {
            appropriateSession = createSession(team)
        }
        appropriateSession = appropriateSession.copy(teams = appropriateSession.teams + team)

        if (appropriateSession.teams.size >= MIN_PLAYERS_TO_START / team.teamSize.size) {
            startSessionTimer(appropriateSession)
        }

        sessionRepository.insertSession(appropriateSession)

        return Result.success(appropriateSession.id)
    }

    private suspend fun createSession(team: Team): Session {
        val session = Session(
            id = UUID.randomUUID().toString(),
            teamSize = team.teamSize,
            sessionStatus = SessionStatus.WaitingForPlayers,
            teams = emptyList()
        )
        sessionRepository.insertSession(session)
        return session
    }

    private suspend fun startSessionTimer(session: Session) {
        if (startingSessions.contains(session.id)) return
        startingSessions.add(session.id)

        CoroutineScope(currentCoroutineContext()).launch {
            delay(MILLIS_TO_START_SESSION)
            val gameId = gameInteractor.createGame(session)
            sessionRepository.insertSession(session.copy(sessionStatus = SessionStatus.Started(gameId)))
        }
    }

    fun getSession(id: String) = sessionRepository.sessions[id]
}


private fun generateSixSymbolCode(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..6)
        .map { allowedChars.random() }
        .joinToString("")
}