package ru.monke.battleground.domain.game.models

import ru.monke.battleground.domain.auth.model.Account

data class GamePlayer(
    val id: String,
    val account: Account,
    val health: Int,
    val coordinates: Coordinates,
    val inventory: Inventory,
    val statistics: Statistics
)