package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.PlayGameViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.ui.theme.LightBeige

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameScreen(
    playGameViewModel: PlayGameViewModel
) {

    val gamePart = playGameViewModel.gamePart
    val gameState = playGameViewModel.gameState

    if (gamePart.value == null) {
        println("GamePart is loading")
    }

    if (gameState.value == null) {
        println("GameState is loading")
    } else {
        val board: Board = gameState.value!!.boardState!!

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
                GameBoard(board = board) { row, col ->
                    // Simuler un clic
                    println("Clicked cell at [$row, $col]")
                }
            }
        }
    }
}