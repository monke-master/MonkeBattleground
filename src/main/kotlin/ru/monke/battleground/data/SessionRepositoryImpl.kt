package ru.monke.battleground.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.monke.battleground.domain.matchmaking.Session
import ru.monke.battleground.domain.session.SessionRepository

class SessionRepositoryImpl: SessionRepository {

    override val sessions = HashMap<String, MutableStateFlow<Session>>()

    override suspend fun insertSession(session: Session) {
        sessions.getOrPut(session.id) { MutableStateFlow(session) }
            .update { session }
    }
}