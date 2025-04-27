package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto

import kotlinx.serialization.Serializable

@Serializable
data class PointDTO(
    val x: Int = 0,
    val y: Int = 0,
)