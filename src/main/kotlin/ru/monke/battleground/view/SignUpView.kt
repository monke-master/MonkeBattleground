package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class SignUpView(
    val token: String
)