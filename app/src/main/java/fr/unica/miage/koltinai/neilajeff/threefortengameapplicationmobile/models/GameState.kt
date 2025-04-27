package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models

import kotlinx.serialization.Serializable

@Serializable
class GameState (
    var id: String? = null,
    var gamePartId: String? = null,
    var turn: Int = 1,
    var currentPlayerIndex: Int = 0,
    var currentPlayerId: String? = null,
    var boardState: List<List<Cell?>>? = null,
    var lastMove: PlayerTurn? = null,
    var isFinished: Boolean = false,
    var winnerIndex: Int? = null,
    var createdAt: String? = null,
    var updatedAt: String? = null,
    var scores: HashMap<Int, Int> = hashMapOf(),
)
