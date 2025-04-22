package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils.GamePartStatus
import kotlinx.serialization.Serializable

@Serializable
data class GamePart(
    var id: String,
    var player1Id: String,
    var player2Id: String,
    var gameStateId: String? = null,
    var status: GamePartStatus = GamePartStatus.WAITING,
    var winnerId: String? = null,
    var history: List<String>? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    val secretCode: String? = null,
    val nbCasesCote: Int = 7,
)
