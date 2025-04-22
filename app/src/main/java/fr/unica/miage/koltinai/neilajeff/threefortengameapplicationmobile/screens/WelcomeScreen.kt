package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.services.UserDataStoreManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun WelcomeScreen(
    onValidated: (String) -> Unit // Callback une fois le user validé
) {
    val context = LocalContext.current
    val userDataStore = remember { UserDataStoreManager(context) }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Charger automatiquement le nom si déjà enregistré
    LaunchedEffect(Unit) {
        val saved = userDataStore.loadUsername().first()
        if (!saved.isNullOrBlank()) {
            // Vérifie s’il existe côté serveur
            if (checkUserExists(saved)) {
                onValidated(saved)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenue dans 3 pour 10", fontSize = 28.sp)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Entrez votre nom") }
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            scope.launch {
                if (checkUserExists(username)) {
                    userDataStore.saveUsername(username)
                    onValidated(username)
                } else {
                    message = "Utilisateur non trouvé. (Inscription plus tard)"
                }
            }
        }) {
            Text("Valider")
        }

        Spacer(Modifier.height(16.dp))

        if (message.isNotBlank()) {
            Text(message)
        }
    }
}

suspend fun checkUserExists(username: String): Boolean {
    return try {
        val url = URL("http://10.0.2.2:8080/players/$username")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        connection.responseCode == 200
    } catch (e: Exception) {
        false
    }
}
