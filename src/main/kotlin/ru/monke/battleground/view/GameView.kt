package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class GameView(
    val id: String,
    val mapView: MapView,
    val teams: List<GameTeamView>,
    val deathZoneView: List<DeathZoneView>,
    val status: GameStatusView
)

@Serializable
data class DeathZoneView(
    val center: CoordinatesView,
    val radius: Float
)

@Serializable
sealed class GameStatusView {

    @Serializable
    data object Ongoing: GameStatusView()

    @Serializable
    data class End(
        val winnerTeamId: String
    ): GameStatusView()
}