package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils

import kotlinx.serialization.Serializable

@Serializable
enum class AiPlayerType {
    RANDOM_AI,
    PASSIF_MOST_AWAY_CONNER_AI,
    ACTIF_AI
}