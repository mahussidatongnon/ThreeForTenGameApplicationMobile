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
                        GameItem(
                            GameItemViewModel(gamePart = game),
                            onAdd2ndPlayerClick = {
                            },
                            onStartedClick = {
                                navController.to(PlayGameRoute(game.id, autoStart = true))
                            },
                            onReviewClick = {

                            },
                            onContinueClick = {
                                navController.to(PlayGameRoute(game.id, autoStart = false))
                            }
                        )
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
                    // Action à implémenter pour créer une nouvelle partie
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameItem(
    gameItemViewModel: GameItemViewModel,
    onAdd2ndPlayerClick: () -> Unit = {},
    onStartedClick: () -> Unit = {},
    onContinueClick: () -> Unit = {},
    onReviewClick: () -> Unit = {},
) {

    val _color = when {
        gameItemViewModel.gamePart.status == GamePartStatus.WAITING -> Yellow
        gameItemViewModel.gamePart.status == GamePartStatus.STARTED -> Green
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
                    text = "Partie de ${gameItemViewModel.gamePart.player1.username}",
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = "ID: ${gameItemViewModel.gamePart.id}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = _color,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Statut: ${gameItemViewModel.gamePart.status}",
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )
                }
            }
            if (gameItemViewModel.gamePart.status == GamePartStatus.WAITING) {
                if (gameItemViewModel.gamePart.player2 == null) {
                    Button(
                        onClick = onAdd2ndPlayerClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = _color
                        ),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text("Ajouter Joueur", color = Color.Black)
                    }
                } else {
                    Button(
                        onClick = onStartedClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = _color
                        ),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text("Démarrer", color = Color.Black)
                    }
                }
            } else {
                if (gameItemViewModel.gamePart.status == GamePartStatus.STARTED) {
                    Button(
                        onClick = onContinueClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor =_color
                        ),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text("Continuer", color = Color.Black)
                    }
                } else {
                    Button(
                        onClick = onReviewClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = _color
                        ),
                        shape = RoundedCornerShape(20.dp),
                    ) {
                        Text("Revoir la partie", color = Color.Black)
                    }
                }
            }
        }
    }
}