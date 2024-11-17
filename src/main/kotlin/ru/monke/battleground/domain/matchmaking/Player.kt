package ru.monke.battleground.domain.matchmaking

import ru.monke.battleground.domain.auth.model.Account

data class Player(
    val id: String,
    val account: Account,
    val isReady: Boolean = false
)