package ru.monke.battleground.domain.game

import ru.monke.battleground.domain.game.models.Coordinates

fun randomCoordinates(): Coordinates {
    return Coordinates(
        x = X_RANGE.random().toFloat(),
        y = Y_RANGE.random().toFloat(),
        z = Z_RANGE.random().toFloat()
    )
}