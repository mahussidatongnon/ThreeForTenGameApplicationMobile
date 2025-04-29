package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto.CoordinatePlayedDTO
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto.PlayGameDTO
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto.PointDTO
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GamePart
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GameState
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils.GamePartStatus
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.network.WebSocketClient
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.GamePartRepository
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayGameUiState(
    val isGameStateLoading: Boolean = false
)
class PlayGameViewModel(val gameId: String, val autoStart: Boolean = false, val gamePartRepository:
GamePartRepository = GamePartRepository()) : ViewModel() {
    private var _player: Player
    private var _gameState = mutableStateOf<GameState?>(null)
    private var _gamePart = mutableStateOf<GamePart?>(null)
    private val _uiState = MutableStateFlow(PlayGameUiState())

    val gamePart: State<GamePart?>
        get() = _gamePart
    val gameState: State<GameState?>
        get() = _gameState
    val uiState: StateFlow<PlayGameUiState> = _uiState.asStateFlow()
    val player: Player
        get() = _player
    val webSocketClient = WebSocketClient.instance
    val isCurrentPlayer: Boolean
        get() {
            if (gameState.value != null) {
                return gameState.value!!.currentPlayerId == player.id
            }
            return false
        }


    init {
        _player = GameManager.player!!
        updateGameInfos()
        startAutoUpdate()
    }

    fun updateGameInfos() {

        viewModelScope.launch {
            _updateGameInfo()
        }
    }

    private suspend fun _updateGameInfo() {
        val updatedGamePart: GamePart = gamePartRepository.getGameById(gameId)
        _gamePart.value = updatedGamePart

        if(updatedGamePart.status == GamePartStatus.WAITING) {
            println("Ce jeu n'a pas de state de disponible")
            return
        }

        val gameState = gamePartRepository.getGameState(gamePart.value!!.id)
        _gameState.value = gameState
    }


    fun playGame(value: Int, x: Int, y: Int) {
        val playGameDTO = PlayGameDTO(
            coordinates = PointDTO(x, y),
            coinValue = value,
            playerUsername = GameManager.username!!
        )

        viewModelScope.launch {
            val gameState = gamePartRepository.playGame(gamePart.value!!.id, playGameDTO)
            _gameState.value = gameState
        }
    }

    // Fonction pour mettre à jour périodiquement l'état du jeu
    private fun startAutoUpdate() {
        viewModelScope.launch {
            println("Enter startAutoUpdate")
            while (true) {
                delay(3000)  // Attendre 5 secondes avant de mettre à jour
                if (gameState.value != null &&  !isCurrentPlayer) {
                    println("It's not current player ")
                    _updateGameInfo()
                } else
                    println("It's current player ")
            }
        }
    }

}