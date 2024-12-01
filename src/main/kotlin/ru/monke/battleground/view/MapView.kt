package ru.monke.battleground.view

import kotlinx.serialization.Serializable
import ru.monke.battleground.domain.game.models.PickableItem

@Serializable
data class MapView(
    val pickableItems: List<PickableItemView>
)