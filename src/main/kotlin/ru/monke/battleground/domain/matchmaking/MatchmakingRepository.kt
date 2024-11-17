package ru.monke.battleground.domain.matchmaking

interface MatchmakingRepository {

    suspend fun insertTeam(team: Team)

    suspend fun getTeamByCode(code: String): Team?

}