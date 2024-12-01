package ru.monke.battleground.domain.game.models

data class Weapon(
    override val id: String,
    val weaponType: WeaponType,
    val clip: Clip
): Item(id)