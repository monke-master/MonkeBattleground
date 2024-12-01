package ru.monke.battleground.domain.matchmaking.model

data class Player(
    val id: String,
    val accountId: String,
    val teamId: String
)