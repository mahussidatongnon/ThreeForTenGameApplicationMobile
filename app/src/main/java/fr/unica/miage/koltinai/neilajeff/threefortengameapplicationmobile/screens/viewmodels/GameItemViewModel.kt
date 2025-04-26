package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.screens.viewmodels

import android.view.View
import androidx.lifecycle.ViewModel
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GamePart
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories.GamePartRepository

    class GameItemViewModel(val gamePartRepository: GamePartRepository = GamePartRepository(), val gamePart: GamePart) : ViewModel() {


}

