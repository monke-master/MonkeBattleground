package ru.monke.battleground.domain.matchmaking

import java.util.UUID

class MatchmakingInteractor(
    private val matchmakingRepository: MatchmakingRepository
) {

    suspend fun connect(
        accountId: String,
        teamSize: TeamSize
    ): String {
        val code = generateSixSymbolCode().capitalize()
        if (matchmakingRepository.getTeamByCode(code) != null) {
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
            players = listOf(player)
        )

        matchmakingRepository.insertTeam(team)
        return code
    }

    suspend fun connectToTeam(
        accountId: String,
        teamCode: String
    ): Result<Any> {
        val team = matchmakingRepository.getTeamByCode(teamCode) ?: return Result.failure(TeamNotFoundException())

        val player = Player(
            accountId = accountId,
            teamId = team.id,
            id = UUID.randomUUID().toString()
        )
        matchmakingRepository.insertTeam(team.copy(players = team.players + player))
        return Result.success(Unit)
    }
}

private fun generateSixSymbolCode(): String {
    val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    return (1..6)
        .map { allowedChars.random() }
        .joinToString("")
}