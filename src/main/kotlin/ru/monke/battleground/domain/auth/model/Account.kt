package ru.monke.battleground.domain.auth.model

data class Account(
    val id: String,
    val nickname: String,
    val email: String,
    val password: String
)