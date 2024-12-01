package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
class CoordinatesView(
    val x: Float,
    val y: Float,
    val z: Float
)