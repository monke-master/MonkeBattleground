package ru.monke.battleground.domain.matchmaking

import ru.monke.battleground.domain.matchmaking.model.Team

interface TeamRepository {

    suspend fun insertTeam(team: Team)

    suspend fun getTeamByCode(code: String): Team?

}