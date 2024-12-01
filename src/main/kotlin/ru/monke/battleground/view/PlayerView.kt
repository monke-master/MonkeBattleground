package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class PlayerView(
    val id: String,
    val health: Int,
    val coordinates: CoordinatesView,
    val inventory: InventoryView,
    val statistics: StatisticsView
)

@Serializable
data class StatisticsView(
    val playersKilled: Int = 0,
    val damage: Float = 0f
)