package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GamePart
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils.AiPlayerType
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.GamePartRepository
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun CreateGameDialog(
    onDismiss: () -> Unit,
    onGameCreated: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var player1Username by remember { mutableStateOf(GameManager.username ?: "") }
    var isHumanOpponent by remember { mutableStateOf(false) }
    var selectedAiType by remember { mutableStateOf(AiPlayerType.RANDOM_AI) }
    var player2Username by remember { mutableStateOf("") }
    var boardSize by remember { mutableStateOf(5) } // Taille par défaut 5x5
    var secretCode by remember { mutableStateOf(generateRandomCode()) }
    var isCreating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val gameRepository = remember { GamePartRepository() }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Titre
                Text(
                    text = "Créer une nouvelle partie",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Section: Joueur 1 (Créateur)
                Text(
                    text = "Votre nom d'utilisateur",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Purple,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = player1Username,
                    onValueChange = { player1Username = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = Purple
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Option pour choisir entre IA et joueur humain
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = !isHumanOpponent,
                            onClick = { isHumanOpponent = false },
                            colors = RadioButtonDefaults.colors(selectedColor = Purple)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("IA")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = isHumanOpponent,
                            onClick = { isHumanOpponent = true },
                            colors = RadioButtonDefaults.colors(selectedColor = Purple)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Joueur humain")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Afficher les options en fonction du choix
                if (isHumanOpponent) {
                    // Section: Joueur humain
                    Text(
                        text = "Nom du joueur adverse",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Purple,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = player2Username,
                        onValueChange = { player2Username = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Purple
                        )
                    )
                } else {
                    // Section: Type d'IA
                    Text(
                        text = "Type d'IA",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Purple,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )

                    // Alignement correct des options d'IA
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Option Aléatoire
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            RadioButton(
                                selected = selectedAiType == AiPlayerType.RANDOM_AI,
                                onClick = { selectedAiType = AiPlayerType.RANDOM_AI },
                                colors = RadioButtonDefaults.colors(selectedColor = Purple)
                            )
                            Text(
                                text = "Aléatoire",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Option Passive
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            RadioButton(
                                selected = selectedAiType == AiPlayerType.PASSIF_MOST_AWAY_CONNER_AI,
                                onClick = { selectedAiType = AiPlayerType.PASSIF_MOST_AWAY_CONNER_AI },
                                colors = RadioButtonDefaults.colors(selectedColor = Purple)
                            )
                            Text(
                                text = "Passive",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Option Active
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            RadioButton(
                                selected = selectedAiType == AiPlayerType.ACTIF_AI,
                                onClick = { selectedAiType = AiPlayerType.ACTIF_AI },
                                colors = RadioButtonDefaults.colors(selectedColor = Purple)
                            )
                            Text(
                                text = "Active",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Taille du plateau
                Text(
                    text = "Taille du plateau",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Purple,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                // Options pour la taille du plateau
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SizeOption(
                        text = "5x5",
                        selected = boardSize == 5,
                        onClick = { boardSize = 5 }
                    )
                    SizeOption(
                        text = "6x6",
                        selected = boardSize == 6,
                        onClick = { boardSize = 6 }
                    )
                    SizeOption(
                        text = "7x7",
                        selected = boardSize == 7,
                        onClick = { boardSize = 7 }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Code secret
                Text(
                    text = "Code secret",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = Purple,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = secretCode,
                        onValueChange = { secretCode = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Purple,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Purple
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { secretCode = generateRandomCode() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Blue
                        )
                    ) {
                        Text("Générer")
                    }
                }

                // Afficher un message d'erreur s'il y en a un
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color(0xFFB71C1C),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bouton Valider
                // Bouton Valider
                Button(
                    onClick = {
                        // Validation de base
                        if (player1Username.isBlank()) {
                            errorMessage = "Veuillez entrer votre nom d'utilisateur"
                            return@Button
                        }

                        if (isHumanOpponent && player2Username.isBlank()) {
                            errorMessage = "Veuillez entrer le nom du joueur adverse"
                            return@Button
                        }

                        if (secretCode.isBlank()) {
                            errorMessage = "Veuillez entrer un code secret"
                            return@Button
                        }

                        isCreating = true
                        errorMessage = ""

                        scope.launch {
                            try {
                                // Si c'est un joueur humain, vérifier s'il existe
                                if (isHumanOpponent) {
                                    try {
                                        val allPlayers = gameRepository.getAllPlayers()
                                        val playerExists = allPlayers.any { it.username == player2Username }

                                        if (!playerExists) {
                                            errorMessage = "Le joueur '$player2Username' n'existe pas"
                                            isCreating = false
                                            return@launch
                                        }
                                    } catch (e: Exception) {
                                        // En cas d'erreur pour récupérer les joueurs, on continue quand même
                                        println("Impossible de vérifier l'existence du joueur: ${e.message}")
                                    }
                                }

                                // Déterminer le nom du joueur 2 en fonction du type d'adversaire
                                val player2Name = if (isHumanOpponent) {
                                    player2Username
                                } else {
                                    when (selectedAiType) {
                                        AiPlayerType.RANDOM_AI -> "RANDOM_AI"
                                        AiPlayerType.PASSIF_MOST_AWAY_CONNER_AI -> "PASSIF_MOST_AWAY_CONNER_AI"
                                        AiPlayerType.ACTIF_AI -> "ACTIF_AI"
                                    }
                                }

                                println("Tentative de création de partie avec: player1=$player1Username, player2=$player2Name, nbCasesCote=$boardSize")

                                val gamePart = gameRepository.createGame(
                                    player1Username = player1Username,
                                    player2Username = player2Name,
                                    secretCode = secretCode,
                                    nbCasesCote = boardSize
                                )

                                // Démarrer la partie si succès
                                try {
                                    gameRepository.startGame(gamePart.id)
                                    println("Partie démarrée avec succès: ${gamePart.id}")
                                } catch (e: Exception) {
                                    println("Erreur lors du démarrage de la partie: ${e.message}")
                                    // On continue même si le démarrage échoue
                                }

                                isCreating = false
                                onGameCreated(gamePart.id)
                            } catch (e: Exception) {
                                println("Erreur lors de la création: ${e.message}")
                                errorMessage = "Erreur: ${e.message}"
                                isCreating = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isCreating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple,
                        contentColor = Color.White
                    )
                ) {
                    if (isCreating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            "Valider",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SizeOption(text: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(64.dp)
            .height(64.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Purple.copy(alpha = 0.1f) else Color.White
        ),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Purple else Color.Gray
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) Purple else Color.Gray,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp
            )
        }
    }
}

// Fonction utilitaire pour générer un code aléatoire
private fun generateRandomCode(): String {
    val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    return (1..6).map { chars[Random.nextInt(chars.length)] }.joinToString("")
}