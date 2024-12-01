package ru.monke.battleground.domain.matchmaking

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.monke.battleground.domain.session.MAX_PLAYERS
import ru.monke.battleground.domain.session.MILLIS_TO_START_SESSION
import ru.monke.battleground.domain.session.MIN_PLAYERS_TO_START
import ru.monke.battleground.domain.session.SessionRepository
import java.util.*

class MatchmakingInteractor(
    private val teamRepository: TeamRepository,
    private val sessionRepository: SessionRepository
) {

    private val startingSessions = mutableListOf<String>()

    suspend fun connect(
        accountId: String,
        teamSize: TeamSize
    ): String {
        val code = generateSixSymbolCode().capitalize()
        if (teamRepository.getTeamByCode(code) != null) {
            return connect(accountId, teamSize)
        }

        val teamId = UUID.randomUUID().toString()
        val player = Player(
            accountId = accountId,
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

        if (team.players.size == team.teamSize.size) {
            return Result.failure(FullTeamException())
        }

        val player = Player(
            accountId = accountId,
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
                it.sessionStatus == SessionStatus.WAITING_FOR_PLAYERS &&
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
        val session =  Session(
            id = UUID.randomUUID().toString(),
            teamSize = team.teamSize,
            sessionStatus = SessionStatus.WAITING_FOR_PLAYERS,
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
            sessionRepository.insertSession(session.copy(sessionStatus = SessionStatus.STARTED))
        }
    }

    suspend fun getSession(id: String) = sessionRepository.sessions[id]
}

private fun generateSixSymbolCode(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..6)
        .map { allowedChars.random() }
        .joinToString("")
}