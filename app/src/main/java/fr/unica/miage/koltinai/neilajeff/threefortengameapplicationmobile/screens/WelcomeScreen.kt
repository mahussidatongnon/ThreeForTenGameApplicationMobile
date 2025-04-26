package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.R
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.PlayerRepository
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.services.PlayerDataStoreManager
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(
    onValidated: (String) -> Unit // Callback une fois le user validé
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val playerDataStoreManager = remember { PlayerDataStoreManager(context) }
    val playerRepository = remember { PlayerRepository() }

    var username by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isAuthenticated by remember { mutableStateOf(false) }
    var playerStats by remember { mutableStateOf<Player?>(null) }

    // Charger automatiquement le nom s'il est déjà enregistré
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val savedUsername = playerDataStoreManager.loadUsername().first()
            if (savedUsername != null && savedUsername.isNotEmpty()) {
                username = savedUsername

                // Essayer d'authentifier l'utilisateur avec le nom sauvegardé
                try {
                    val player = playerRepository.getPlayer(savedUsername)
                    GameManager.savePlayer(player)
                    GameManager.saveUsername(savedUsername)
                    playerStats = player
                    isAuthenticated = true

                    // Attendre un peu pour montrer les stats avant de naviguer
                    kotlinx.coroutines.delay(1000)
                    isLoading = false
                    onValidated(savedUsername)
                } catch (e: Exception) {
                    // L'utilisateur n'existe pas encore sur le serveur
                    Log.e("WelcomeScreen", "Erreur lors de l'authentification: ${e.message}")
                    isLoading = false
                }
            } else {
                isLoading = false
            }
        } catch (e: Exception) {
            Log.e("WelcomeScreen", "Erreur lors du chargement du nom: ${e.message}")
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo du jeu
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo du jeu 3 pour 10",
                    modifier = Modifier
                        .size(160.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Bienvenue dans 3 pour 10",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Purple
            )

            Spacer(Modifier.height(32.dp))

            // Champ de nom d'utilisateur
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    errorMessage = ""
                },
                label = { Text("Entrez votre nom") },
                isError = errorMessage.isNotEmpty(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Purple,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Purple,
                    focusedLabelColor = Purple
                )
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Bouton de connexion
            Button(
                onClick = {
                    if (username.isBlank()) {
                        errorMessage = "Le nom d'utilisateur ne peut pas être vide"
                        return@Button
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            // Sauvegarder le nom dans le DataStore
                            playerDataStoreManager.saveUsername(username)

                            // Essayer de récupérer le joueur (vérifier s'il existe)
                            val player = try {
                                playerRepository.getPlayer(username)
                            } catch (e: Exception) {
                                // Si le joueur n'existe pas, l'enregistrer
                                try {
                                    playerRepository.registerPlayer(username, username)
                                } catch (e: Exception) {
                                    throw Exception("Impossible d'enregistrer l'utilisateur: ${e.message}")
                                }
                            }

                            // Sauvegarder les informations du joueur dans le GameManager
                            GameManager.savePlayer(player)
                            GameManager.saveUsername(username)

                            Toast.makeText(context, "Bienvenue, $username !", Toast.LENGTH_SHORT).show()
                            isLoading = false
                            onValidated(username)
                        } catch (e: Exception) {
                            Log.e("WelcomeScreen", "Erreur: ${e.message}")
                            errorMessage = "Erreur: ${e.message}"
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading && username.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    contentColor = Color.White,
                    disabledContainerColor = Purple.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        "Commencer à jouer",
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontSize = 16.sp
                    )
                }
            }

            // Si l'utilisateur est authentifié, afficher les stats du joueur
            if (isAuthenticated && playerStats != null) {
                Spacer(Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Statistiques du joueur",
                            style = MaterialTheme.typography.titleMedium,
                            color = Purple,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatItem("Parties", (playerStats?.gamesPlayed ?: 0).toString(), Blue)
                            StatItem("Victoires", (playerStats?.gamesWon ?: 0).toString(), Green)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}