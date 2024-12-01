package ru.monke.battleground.view

import kotlinx.serialization.Serializable
import ru.monke.battleground.domain.game.models.Clip
import ru.monke.battleground.domain.game.models.WeaponType

@Serializable
sealed class ItemView(
    open val id: String
)

@Serializable
data class AmmoView(
    val ammoId: String,
    val count: Int,
    val weaponType: WeaponType
): ItemView(ammoId)

@Serializable
data class WeaponView(
    val weaponId: String,
    val weaponType: WeaponType,
    val clip: ClipView
): ItemView(weaponId)

@Serializable
data class ClipView(
    val maxAmmo: Int,
    val currentAmmo: Int
)
