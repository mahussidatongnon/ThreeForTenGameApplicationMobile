package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils

import kotlinx.serialization.Serializable

@Serializable
enum class GamePartStatus {
    WAITING,
    STARTED,
    FINISHED,
}