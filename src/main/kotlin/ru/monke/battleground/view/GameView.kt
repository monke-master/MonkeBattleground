package ru.monke.battleground.view

import kotlinx.serialization.Serializable

@Serializable
data class GameView(
    val id: String,
    val mapView: MapView,
    val teams: List<GameTeamView>,
    val deathZoneView: List<DeathZoneView>
)

@Serializable
data class DeathZoneView(
    val center: CoordinatesView,
    val radius: Float
)