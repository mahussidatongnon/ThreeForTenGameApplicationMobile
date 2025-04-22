package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.PlayerRepository
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import kotlinx.coroutines.launch

class MyAppViewModel(val playerRepository: PlayerRepository = PlayerRepository()) : ViewModel() {
    private var _username = mutableStateOf<String?>(null)

    val username: State<String?>
        get() = _username

    init {
        _username.value = GameManager.username
    }

    fun loadPlayer(username: String) {
        viewModelScope.launch {
            val player: Player = try {
                playerRepository.getPlayer(username)
            } catch (e: Exception) {
                println("error: $e")
                playerRepository.registerPlayer(username, username)
            }
            GameManager.savePlayer(player)
        }
    }

}