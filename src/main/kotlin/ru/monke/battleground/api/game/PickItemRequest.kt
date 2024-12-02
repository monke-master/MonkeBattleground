package ru.monke.battleground.api.game

import kotlinx.serialization.Serializable

@Serializable
data class PickItemRequest(
    val itemId: String,
    val inventoryX: Int,
    val inventoryY: Int,
)