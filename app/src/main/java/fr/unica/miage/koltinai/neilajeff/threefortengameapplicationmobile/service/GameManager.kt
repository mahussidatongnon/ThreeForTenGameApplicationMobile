package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service

import android.util.Log
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player

object GameManager {
    private var _username: String? = null
    private var _player: Player? = null

    const val SERVER_HOST = "10.0.2.2"
    const val SERVER_PORT = 8082


    val username: String?
        get() = _username

    val player: Player?
        get() = _player

    fun saveUsername(username: String?) {
        Log.d("GameManager", "Sauvegarde du nom d'utilisateur: $username")
        _username = username
    }

    fun savePlayer(player: Player) {
        Log.d("GameManager", "Sauvegarde du joueur: ${player.id}, ${player.username}")
        _player = player
    }

    fun clearUserData() {
        _username = null
        _player = null
    }
}
