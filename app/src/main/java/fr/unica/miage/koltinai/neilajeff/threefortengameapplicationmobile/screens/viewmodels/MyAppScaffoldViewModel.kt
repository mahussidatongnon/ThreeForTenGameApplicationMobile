package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager

class MyAppScaffoldViewModel() : ViewModel() {
    private var _username = mutableStateOf<String?>(null)

    val username: State<String?>
        get() = _username

    init {
        _username.value = GameManager.username
    }
}