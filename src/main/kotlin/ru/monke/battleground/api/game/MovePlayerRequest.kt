package ru.monke.battleground.api.game

import kotlinx.serialization.Serializable

@Serializable
data class MovePlayerRequest(
    val x: Float,
    val y: Float,
    val z: Float
)