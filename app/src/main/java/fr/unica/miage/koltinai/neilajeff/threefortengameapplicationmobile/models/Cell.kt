package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils.WinningDirection
import kotlinx.serialization.Serializable

@Serializable
data class Cell(
    val value: Int = 0,
    val wonCasesDirections: Set<WinningDirection> = mutableSetOf()
)
