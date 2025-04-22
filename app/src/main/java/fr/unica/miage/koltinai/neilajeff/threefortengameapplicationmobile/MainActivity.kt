package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.services.PlayerDataStoreManager
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Access  à la persistence
        val playerDataStoreManager = PlayerDataStoreManager(this)

        // Charger le panier existant au lancement de l'application
        lifecycleScope.launch {
            playerDataStoreManager.loadUsername().map { username ->
                GameManager.saveUsername(username)
            }
        }

        setContent {
            MyApp()
        }
    }
}
