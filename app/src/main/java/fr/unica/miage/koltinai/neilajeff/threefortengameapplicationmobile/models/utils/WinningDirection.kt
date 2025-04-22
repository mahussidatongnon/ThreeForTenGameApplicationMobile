package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.utils

import kotlinx.serialization.Serializable

@Serializable
enum class WinningDirection {
    UP_DIAGONAL,
    DOWN_DIAGONAL,
    HORIZONTAL,
    VERTICAL
}