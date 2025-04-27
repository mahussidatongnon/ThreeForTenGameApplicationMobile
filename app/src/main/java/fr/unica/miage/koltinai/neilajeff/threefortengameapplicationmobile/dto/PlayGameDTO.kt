package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlayGameDTO(
    val coordinates: PointDTO,
    val coinValue: Int,
    val playerUsername: String
)
