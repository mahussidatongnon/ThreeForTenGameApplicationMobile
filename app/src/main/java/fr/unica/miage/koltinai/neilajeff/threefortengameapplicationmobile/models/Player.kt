package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models

import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.JsonIgnoreUnknownKeys


@Serializable
data class Player(
    val id: String,
    val username: String,
    var password: String?,
    val gamesPlayed: Int,
    val gamesWon: Int
) {
    init {
        if (password == null)
            password = username
    }
}
