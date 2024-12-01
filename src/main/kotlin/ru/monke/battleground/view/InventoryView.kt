package ru.monke.battleground.view

import kotlinx.serialization.Serializable
import ru.monke.battleground.domain.game.models.InventoryItem
import ru.monke.battleground.domain.game.models.Item

@Serializable
data class InventoryView(
    val maxSize: Int,
    val items: List<InventoryItemView> = emptyList()
)

@Serializable
data class InventoryItemView(
    val x: Int,
    val y: Int,
    val item: ItemView
)