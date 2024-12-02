package ru.monke.battleground.domain.game.models

data class DeathZone(
    val center: Coordinates,
    val level: DeathZoneLevel
)