package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.services.PlayerDataStoreManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class GameInfo(
    val id: String,
    val creatorName: String,
    val status: String
)

data class GameScreenUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val playerName: String = "",
    val gamesPlayed: Int = 0,
    val gamesWon: Int = 0,
    val totalScore: Int = 0,
    val availableGames: List<GameInfo> = emptyList()
)

class GameScreenViewModel : ViewModel() {
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
                    fetchPlayerStats(username)

                    // Récupérer les parties disponibles
                    fetchAvailableGames()
                } else {
                    // Si pas de nom dans GameManager, essayer depuis DataStore
                    val playerDataStoreManager = PlayerDataStoreManager(context)
                    val storedUsername = playerDataStoreManager.loadUsername().first() ?: ""

                    if (storedUsername.isNotEmpty()) {
                        _uiState.update { it.copy(playerName = storedUsername) }
                        GameManager.saveUsername(storedUsername)

                        // Essayer de récupérer les statistiques du joueur
                        fetchPlayerStats(storedUsername)

                        // Récupérer les parties disponibles
                        fetchAvailableGames()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = "Utilisateur non authentifié"
                            )
                        }
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

    private fun fetchPlayerStats(username: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                // Pour le développement, simulation des données du joueur
                simulateAPIResponse(username)
            } catch (e: Exception) {
                Log.e("GameScreenViewModel", "Erreur lors de la récupération des stats", e)
                // En cas d'erreur, on laisse les valeurs par défaut (0)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun fetchAvailableGames() {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                // Pour le développement, on simule des données
                val sampleGames = listOf(
                    GameInfo(
                        id = "game1",
                        creatorName = "Alice",
                        status = "En attente"
                    ),
                    GameInfo(
                        id = "game2",
                        creatorName = "Bob",
                        status = "En cours"
                    )
                )

                _uiState.update {
                    it.copy(
                        availableGames = sampleGames,
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

    private fun simulateAPIResponse(username: String) {
        // Simulation des stats du joueur pour le développement
        val gamesPlayed = (1..10).random()
        val gamesWon = (0..gamesPlayed).random()
        val totalScore = (gamesWon * 10 + (gamesPlayed - gamesWon) * 3)

        _uiState.update {
            it.copy(
                playerName = username,
                gamesPlayed = gamesPlayed,
                gamesWon = gamesWon,
                totalScore = totalScore,
                isLoading = false
            )
        }
    }
}