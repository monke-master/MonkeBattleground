package ru.monke.battleground.domain.session

import kotlinx.coroutines.flow.StateFlow

interface SessionRepository {

    val sessions: Map<String, StateFlow<Session>>

    suspend fun insertSession(session: Session)
}