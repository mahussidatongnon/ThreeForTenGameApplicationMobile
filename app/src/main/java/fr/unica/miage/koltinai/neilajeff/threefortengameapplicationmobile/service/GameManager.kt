package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player

object GameManager {
    private var _username: String? = null
    private var _player: Player? = null

    val username: String?
        get() = _username

    val player: Player
        get() = _player!!

    fun saveUsername(username: String?) {
        _username = GameManager.username
    }

    fun savePlayer(player: Player) {
        _player = player
    }

}