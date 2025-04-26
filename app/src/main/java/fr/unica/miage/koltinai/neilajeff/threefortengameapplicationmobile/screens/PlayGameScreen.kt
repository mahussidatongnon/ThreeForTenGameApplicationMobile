package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels.PlayGameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayGameScreen(
    playGameViewModel: PlayGameViewModel
) {
    Text(text = "PlayGameScreen")
}