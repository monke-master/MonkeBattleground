package ru.monke.battleground.domain.game.models

enum class WeaponType(
    val clipSize: Int,
    val damage: Int
) {
    PISTOL(12, 10), AUTO_RIFFLE(40, 12), RIFFLE(5, 40)
}