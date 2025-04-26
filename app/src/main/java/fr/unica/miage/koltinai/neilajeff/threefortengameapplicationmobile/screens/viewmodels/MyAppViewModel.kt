package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineExceptionHandler

class MyAppViewModel(
    private val playerRepository: fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.PlayerRepository =
        fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.PlayerRepository()
) : ViewModel() {

    private var _username = mutableStateOf<String?>(null)

    val username: androidx.compose.runtime.State<String?>
        get() = _username

    // Gestionnaire d'exceptions pour éviter que l'application ne plante
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.e("MyAppViewModel", "Erreur dans la coroutine: ${exception.message}", exception)
    }

    init {
        _username.value = GameManager.username
    }

    fun loadPlayer(username: String) {
        viewModelScope.launch(coroutineExceptionHandler) {
            try {
                Log.d("MyAppViewModel", "Chargement du joueur: $username")

                // Sauvegarder temporairement le nom d'utilisateur
                GameManager.saveUsername(username)
                _username.value = username

                val player = try {
                    // Essayer d'abord de récupérer le joueur existant
                    playerRepository.getPlayer(username)
                } catch (e: Exception) {
                    Log.e("MyAppViewModel", "Erreur lors de la récupération du joueur: ${e.message}")

                    try {
                        // Si le joueur n'existe pas, l'enregistrer
                        playerRepository.registerPlayer(username, username)
                    } catch (registrationError: Exception) {
                        Log.e("MyAppViewModel", "Erreur lors de l'enregistrement du joueur: ${registrationError.message}")

                        // En cas d'erreur de connexion, créer un joueur local temporaire
                        fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player(
                            id = username,
                            username = username,
                            password = username,
                            gamesPlayed = 0,
                            gamesWon = 0
                        )
                    }
                }

                // Sauvegarder le joueur dans GameManager
                GameManager.savePlayer(player)

                Log.d("MyAppViewModel", "Joueur chargé avec succès: ${player.id}, ${player.username}")
            } catch (e: Exception) {
                Log.e("MyAppViewModel", "Erreur non gérée: ${e.message}", e)
                // Ne pas propager l'exception pour éviter que l'application ne plante
            }
        }
    }
}