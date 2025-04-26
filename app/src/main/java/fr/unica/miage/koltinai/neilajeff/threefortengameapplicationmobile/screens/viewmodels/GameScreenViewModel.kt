package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GamePart
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.services.PlayerDataStoreManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.PlayerRepository
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.GamePartRepository


data class GameScreenUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val playerName: String = "",
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val totalScore: Int = 0,
    val availableGames: List<GamePart> = emptyList()
)

class GameScreenViewModel(val playerRepository: PlayerRepository = PlayerRepository(),
                          val gamePartRepository: GamePartRepository = GamePartRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(GameScreenUiState())
    val uiState: StateFlow<GameScreenUiState> = _uiState.asStateFlow()

    // Gestionnaire d'exceptions pour éviter que l'application ne plante
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("GameScreenViewModel", "Erreur dans la coroutine: ${exception.message}", exception)
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = "Une erreur est survenue: ${exception.message}"
            )
        }
    }

    fun loadPlayerInfo(context: Context) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Essayer d'abord de récupérer les infos depuis GameManager
                val username = GameManager.username ?: ""

                if (username.isNotEmpty()) {
                    // Mise à jour du nom du joueur dans l'UI
                    _uiState.update { it.copy(playerName = username) }

                    // Essayer de récupérer les statistiques du joueur
//                    fetchPlayerStats(username)
                    val player: Player = playerRepository.getPlayer(username)
                    _uiState.update {
                        it.copy(
                            playerName = player.username,
                            gamesPlayed = player.gamesPlayed,
                            gamesWon = player.gamesWon,
                            totalScore = player.gamesWon,
                            isLoading = false
                        )
                    }
                    // Récupérer les parties disponibles
                    fetchAvailableGames(username)
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Utilisateur non authentifié"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("GameScreenViewModel", "Erreur lors du chargement des informations du joueur", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erreur lors du chargement des données: ${e.message}"
                    )
                }
            }
        }
    }

    private fun fetchAvailableGames(username: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                _uiState.update {
                    it.copy(
                        availableGames = gamePartRepository.getGamesByUsername(username),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e("GameScreenViewModel", "Erreur lors de la récupération des parties", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        availableGames = emptyList()
                    )
                }
            }
        }
    }
}