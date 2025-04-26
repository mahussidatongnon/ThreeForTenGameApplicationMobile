package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GamePart
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GameState
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.GamePartRepository
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import kotlinx.coroutines.launch

class PlayGameViewModel(val gameId: String, autoStart: Boolean = false, val gamePartRepository:
GamePartRepository = GamePartRepository()) : ViewModel() {
    private var _player: Player

    private var _gameState = mutableStateOf<GameState?>(null)
    val gameState: State<GameState?>
        get() = _gameState

    private var _gamePart = mutableStateOf<GamePart?>(null)
        val gamePart: State<GamePart?>
            get() = _gamePart

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
        }

    }

    fun updateGame() {
        viewModelScope.launch {
            val gamePartInfo = gamePartRepository.getGameById(_gamePart.value!!.id)
            _gamePart.value = gamePartInfo

            val gameState = gamePartRepository.getGameState(gamePart.value!!.id)
            _gameState.value = gameState
        }
    }

    fun startGame() {
        viewModelScope.launch {
            val gamePartInfo = gamePartRepository.startGame(gamePart.value!!.id)
            _gamePart.value = gamePartInfo

            val gameState = gamePartRepository.getGameState(gamePart.value!!.id)
            _gameState.value = gameState
        }
    }
}