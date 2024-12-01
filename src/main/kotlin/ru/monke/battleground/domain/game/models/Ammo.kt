package ru.monke.battleground.domain.game.models

data class Ammo(
    override val id: String,
    val count: Int,
    val weaponType: WeaponType
): Item(id)