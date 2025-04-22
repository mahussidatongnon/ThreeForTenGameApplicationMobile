package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoordinatePlayedDTO(
    val x: Int = 0,
    val y: Int = 0,
    val playerId: String
)