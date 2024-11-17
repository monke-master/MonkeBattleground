package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class ConnectView(
    val teamCode: String
)