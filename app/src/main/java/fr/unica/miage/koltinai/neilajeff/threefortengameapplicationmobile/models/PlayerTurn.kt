package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto.CoordinatePlayedDTO
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils.WinningDirection
import kotlinx.serialization.Serializable

@Serializable
data class PlayerTurn (
    val turn: Int,
    val point: CoordinatePlayedDTO,
    val coinValue: Int,
    val score: Int = 0,
    val gameStateId: String,
    val wonPoints: Map<WinningDirection, Set<CoordinatePlayedDTO>>,
    var createdAt: String? = null,
)