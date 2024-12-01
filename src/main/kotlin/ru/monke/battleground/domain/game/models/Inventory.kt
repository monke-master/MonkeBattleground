package ru.monke.battleground.domain.game.models

data class Inventory(
    val maxSize: Int,
    val items: List<InventoryItem> = emptyList()
)