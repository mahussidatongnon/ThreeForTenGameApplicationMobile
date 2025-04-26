package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfo (
    val username: String,
    val gamesPlayed: Int,
    val gamesWon: Int
)