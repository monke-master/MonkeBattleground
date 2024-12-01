package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class PickableItemView(
    val item: ItemView,
    val coordinates: CoordinatesView
)