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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun WelcomeScreen(
    onValidated: (String) -> Unit // Callback une fois le user validé
) {

    var username by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

//    // Charger automatiquement le nom si déjà enregistré
//    LaunchedEffect(Unit) {
//
//    }

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
            onValidated(username)
        }) {
            Text("Valider")
        }

        Spacer(Modifier.height(16.dp))

        if (message.isNotBlank()) {
            Text(message)
        }
    }
}

