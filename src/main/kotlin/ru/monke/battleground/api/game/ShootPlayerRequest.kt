package ru.monke.battleground.api.game

import kotlinx.serialization.Serializable

@Serializable
data class ShootPlayerRequest(
    val targetPlayerId: String,
    val weaponId: String
)