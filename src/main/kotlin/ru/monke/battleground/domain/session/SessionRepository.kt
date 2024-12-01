package ru.monke.battleground.domain.session

import kotlinx.coroutines.flow.StateFlow
import ru.monke.battleground.domain.matchmaking.Session

interface SessionRepository {

    val sessions: Map<String, StateFlow<Session>>

    suspend fun insertSession(session: Session)
}