package ru.monke.battleground.domain.matchmaking.model

enum class TeamSize(val size: Int) {
    Solo(1),
    Duo(2),
    Squad(4),
    BigSix(6)
}