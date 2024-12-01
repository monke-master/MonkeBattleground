package ru.monke.battleground.domain.matchmaking

interface TeamRepository {

    suspend fun insertTeam(team: Team)

    suspend fun getTeamByCode(code: String): Team?

}