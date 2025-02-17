package ru.monke.battleground.data

import ru.monke.battleground.domain.matchmaking.TeamRepository
import ru.monke.battleground.domain.matchmaking.model.Team

class TeamRepositoryImpl(): TeamRepository {

    private val teamsList = mutableListOf<Team>()

    override suspend fun insertTeam(team: Team) {
        val oldTeam = getTeamByCode(team.invitationCode)
        if (oldTeam != null) {
            teamsList.remove(oldTeam)
        }
        teamsList.add(team)
    }

    override suspend fun getTeamByCode(code: String): Team? {
        return teamsList.find { it.invitationCode == code }
    }

}