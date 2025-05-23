package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.PlayGameRoute
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.RULES_ROUTE
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils.GamePartStatus
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.GameItemViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.GameScreenViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.services.PlayerDataStoreManager
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: GameScreenViewModel = viewModel(),
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()
    val playerDataStoreManager = remember { PlayerDataStoreManager(context) }

    // État pour contrôler l'affichage du dialogue de création de partie
    var showCreateGameDialog by remember { mutableStateOf(false) }

    // Récupérer les informations du joueur depuis GameManager
    val player = GameManager.player

    // Charger les informations du joueur au lancement
    LaunchedEffect(Unit) {
        try {
            viewModel.loadPlayerInfo(context)
        } catch (e: Exception) {
            // Gestion supplémentaire des erreurs si nécessaire
        }
    }

    // Afficher le dialogue de création de partie si demandé
    if (showCreateGameDialog) {
        CreateGameDialog(
            onDismiss = { showCreateGameDialog = false },
            onGameCreated = { gameId ->
                showCreateGameDialog = false
                // Rafraîchir la liste des parties
                viewModel.loadPlayerInfo(context)
                // Naviguer vers la partie créée
                navController.navigate(PlayGameRoute(gameId, autoStart = false))
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // Contenu principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Carte d'informations du joueur avec bouton de déconnexion
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profil joueur",
                                modifier = Modifier.size(32.dp),
                                tint = Purple
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = player?.username ?: uiState.playerName.ifEmpty { "Joueur" },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        // Bouton de déconnexion
                        IconButton(
                            onClick = {
                                scope.launch {
                                    // Effacer les données stockées
                                    playerDataStoreManager.saveUsername("")
                                    // Effacer les données du GameManager
                                    GameManager.clearUserData()
                                    // Rediriger vers l'écran de connexion
                                    onLogout()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Déconnexion",
                                tint = Purple
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Statistiques du joueur
                    Text(
                        "Statistiques",
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp,
                        color = Purple
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Utiliser les stats du joueur depuis GameManager si disponibles, sinon utiliser celles du ViewModel
                        StatItem("Parties", (player?.gamesPlayed ?: uiState.gamesPlayed).toString(), Blue)
                        StatItem("Victoires", (player?.gamesWon ?: uiState.gamesWon).toString(), Green)
                        StatItem("Score", uiState.totalScore.toString(), Orange)
                    }
                }
            }

            // Titre "Parties disponibles"
            Text(
                "Parties disponibles",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 8.dp),
                color = Purple
            )

            // Liste des parties disponibles
            if (uiState.availableGames.isNotEmpty()) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    uiState.availableGames.forEach { game ->
                        // Déterminer la couleur en fonction du statut
                        val buttonColor = when (game.status) {
                            GamePartStatus.WAITING -> Yellow
                            GamePartStatus.STARTED -> Green
                            else -> Green
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Partie de ${game.player1.username}",
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        // Point de couleur pour le statut
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(
                                                    color = buttonColor,
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Statut: ${game.status}",
                                            fontSize = 14.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }

                                // Bouton compact à droite
                                Button(
                                    onClick = {
                                        when (game.status) {
                                            GamePartStatus.WAITING -> {
                                                if (game.player2 == null) {
                                                    // Logique pour ajouter un second joueur si nécessaire
                                                } else {
                                                    navController.navigate(PlayGameRoute(game.id, autoStart = true))
                                                }
                                            }
                                            GamePartStatus.STARTED -> {
                                                navController.navigate(PlayGameRoute(game.id, autoStart = false))
                                            }
                                            else -> {
                                                // Pour les parties terminées
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = buttonColor
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        "Ouvrir la partie",
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (!uiState.isLoading && uiState.errorMessage.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F0F0)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucune partie disponible",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Bouton pour créer une nouvelle partie
            Button(
                onClick = {
                    // Ouvrir le dialogue de création de partie
                    showCreateGameDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Créer une partie"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Créer une nouvelle partie",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Bouton pour rafraîchir la liste des parties
            OutlinedButton(
                onClick = {
                    viewModel.loadPlayerInfo(context)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Purple
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Rafraîchir"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Rafraîchir la liste",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Afficher un message d'erreur s'il y en a un
            if (uiState.errorMessage.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            color = Color(0xFFB71C1C),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.loadPlayerInfo(context) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFB71C1C)
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Réessayer")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Réessayer")
                        }
                    }
                }
            }
        }

        // Afficher un indicateur de chargement si nécessaire
        if (uiState.isLoading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.White.copy(alpha = 0.7f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Purple)
                }
            }
        }
    }
}

@Composable
fun GameStatItem(
    label: String,
    value: String,
    color: Color
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}