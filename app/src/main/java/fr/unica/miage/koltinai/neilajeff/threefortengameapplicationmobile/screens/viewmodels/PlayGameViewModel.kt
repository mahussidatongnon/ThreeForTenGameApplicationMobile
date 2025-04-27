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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayGameUiState(
    val isGameStateLoading: Boolean = false
)
class PlayGameViewModel(val gameId: String, autoStart: Boolean = false, val gamePartRepository:
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

    init {
        _player = GameManager.player!!
        viewModelScope.launch {
            val gamePartInfo = gamePartRepository.getGameById(gameId)
            _gamePart.value = gamePartInfo

            if(autoStart) {
                startGame()
            } else {
                updateGame()
            }
            val topic = "/topic/games.$gameId.state"
            print("Before subscribe to: $topic")
            webSocketClient.subscribe(topic)
        }

    }

    fun updateGame() {
        val currentGamePart = _gamePart.value

        if (currentGamePart == null) {
            println("Pas de GamePart disponible pour startGame")
            return
        }

        viewModelScope.launch {
            val updatedGamePart: GamePart = gamePartRepository.getGameById(currentGamePart!!.id)
            _gamePart.value = updatedGamePart

            if(updatedGamePart.status == GamePartStatus.WAITING) {
                println("Ce jeu n'a pas de state de disponible")
                return@launch
            }

            val gameState = gamePartRepository.getGameState(gamePart.value!!.id)
            _gameState.value = gameState
        }
    }

    fun startGame() {
        val currentGamePart = _gamePart.value

        if (currentGamePart == null) {
            println("Pas de GamePart disponible pour startGame")
            return
        }

        if (currentGamePart.status != GamePartStatus.WAITING) {
            println("Le jeu est déjà démarré, pas besoin de startGame")
            return
        }

        viewModelScope.launch {
            try {
                val updatedGamePart = gamePartRepository.startGame(currentGamePart.id)
                _gamePart.value = updatedGamePart

                val gameState = gamePartRepository.getGameState(updatedGamePart.id)
                _gameState.value = gameState
            } catch (e: Exception) {
                println("Erreur lors du startGame: $e")
            }
        }
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
}