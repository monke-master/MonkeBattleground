package ru.monke.battleground.domain.game.models

enum class DeathZoneLevel(val radius: Float) {
    FIRST(10f), SECOND(40f), THIRD(100f), FOURTH(200f), FIFTH(500f)
}