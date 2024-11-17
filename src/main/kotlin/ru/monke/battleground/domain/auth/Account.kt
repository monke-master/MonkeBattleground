package ru.monke.battleground.domain.auth

data class Account(
    val id: String,
    val nickname: String,
    val email: String,
    val password: String
)