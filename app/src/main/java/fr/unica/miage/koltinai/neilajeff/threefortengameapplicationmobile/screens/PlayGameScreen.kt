package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.PlayGameViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.LightBeige
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto.PointDTO
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.Green
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.Yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameScreen(
    playGameViewModel: PlayGameViewModel
) {
    val gamePart by playGameViewModel.gamePart
    val gameState by playGameViewModel.gameState


    if (gamePart == null) {
        println("GamePart is loading")
    }

    if (gameState == null) {
        println("GameState is loading")
    } else {
        // Récupération des informations nécessaires
        val player: Player = playGameViewModel.player
        val opponentPlayerInfo = if(gamePart!!.player1.username == player.username) {
            gamePart!!.player2
        } else {
            gamePart!!.player1
        }
        val board: Board = gameState!!.boardState!!
        val isCurrentPlayer = gameState!!.currentPlayerId == player.id
        val currentPlayerIndex = gameState!!.currentPlayerIndex
        val adversarialPlayerIndex = (currentPlayerIndex + 1) % 2
        val isGameFinished = gameState!!.isFinished
        val lastMove = gameState!!.lastMove
        var lastMovePointDTO: PointDTO? = null

        if(lastMove != null) {
            lastMovePointDTO = PointDTO(
                x = lastMove.point.x,
                y = lastMove.point.y
            )
        }


        // Calcul de l'interactivité du plateau
        val isGameBoardInteractive = !isGameFinished && isCurrentPlayer

        // Récupération des scores des joueurs (en utilisant les indices des joueurs)
        var currentPlayerScore = 0
        var opponentScore = 0
        if (isCurrentPlayer) {
            currentPlayerScore = gameState!!.scores[currentPlayerIndex] ?: 0
            opponentScore = gameState!!.scores[adversarialPlayerIndex] ?: 0
        } else {
            currentPlayerScore = gameState!!.scores[adversarialPlayerIndex] ?: 0
            opponentScore = gameState!!.scores[currentPlayerIndex] ?: 0
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBeige),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Affichage des scores avec un indicateur du joueur actuel
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${player.username} (vous): $currentPlayerScore",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.headlineMedium,
                        color = if (isCurrentPlayer) Green else Color.Black // Indicateur visuel du joueur actuel
                    )

                    Text(
                        text = "${opponentPlayerInfo!!.username}: $opponentScore",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(end = 16.dp),
                        color = if (!isCurrentPlayer) Green else Color.Black // Indicateur visuel du joueur actuel
                    )
                }

                GameBoard(board = board,
                    onCellClick = { row, col ->
                        // Simuler un clic
                        println("Clicked cell at [$row, $col]")
                    },
                    onValueChange = { row, col, value ->
                        // Mise à jour de la valeur de la cellule
                        if (isGameBoardInteractive) {
                            // Si c'est le tour du joueur actuel et que le jeu est en cours
                            println("Setting value $value for cell [$row, $col]")
                            playGameViewModel.playGame(value, row, col)
                        } else {
                            // Si ce n'est pas le tour du joueur ou si le jeu est terminé
                            println("Not your turn or the game is finished.")
                        }
                    },
                    lastMovePointDTO = lastMovePointDTO,
                    isGameBoardInteractive = isGameBoardInteractive
                )

                // Légende expliquant la couleur des indicateurs avec bandes de couleur
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bande verte pour le joueur actuel
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Green)
                        )
                        Text(
                            text = "Current Player",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp),
                            color = Color.Gray
                        )
                    }

                    // Bande jaune pour le dernier mouvement
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Yellow)
                        )
                        Text(
                            text = "Last Move",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp),
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
