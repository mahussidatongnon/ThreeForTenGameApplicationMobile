package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories

import android.util.Log
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

@Serializable
data class RegisterPlayer(val username: String, val password: String)

@Serializable
data class GameInfo(
    val id: String,
    val creatorName: String,
    val status: String
)

class PlayerRepository() : BaseRepository() {

    private val MODE_OFFLINE = true

    suspend fun getPlayer(username: String): Player {
        Log.d("PlayerRepository", "Récupération du joueur: $username")

        if (MODE_OFFLINE) {
            // Simuler un délai pour imiter un appel réseau
            kotlinx.coroutines.delay(500)
            return Player(id = username, username = username, password = username, gamesPlayed = 3, gamesWon = 1)
        }
        val resp = client.get("$baseUrl/players/$username") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }

        if (resp.status != HttpStatusCode.OK) {
            Log.e("PlayerRepository", "Erreur: ${resp.status}")
            throw IllegalArgumentException("Player does not exist")
        }

        return resp.body()
    }

    suspend fun registerPlayer(username: String, password: String): Player {
        Log.d("PlayerRepository", "Enregistrement du joueur: $username")

        if (MODE_OFFLINE) {
            // Simuler un délai pour imiter un appel réseau
            kotlinx.coroutines.delay(500)
            return Player(id = username, username = username, password = password, gamesPlayed = 0, gamesWon = 0)
        }

        val resp = client.post("$baseUrl/auth/register") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(RegisterPlayer(username, password))
        }

        if (resp.status != HttpStatusCode.OK) {
            Log.e("PlayerRepository", "Erreur serveur: ${resp.bodyAsText()}")
            throw IllegalArgumentException("Can't create new player")
        }

        Log.d("PlayerRepository", "Réponse: $resp")
        return resp.body()
    }

    suspend fun getPlayerGames(username: String): List<GameInfo> {
        Log.d("PlayerRepository", "Récupération des parties du joueur: $username")

        if (MODE_OFFLINE) {
            // Simuler un délai pour imiter un appel réseau
            kotlinx.coroutines.delay(500)
            return listOf(
                GameInfo("game1", "Alice", "En attente"),
                GameInfo("game2", "Bob", "En cours")
            )
        }

        val resp = client.get("$baseUrl/players/$username/games") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }

        if (resp.status != HttpStatusCode.OK) {
            Log.e("PlayerRepository", "Erreur: ${resp.status}")
            throw IllegalArgumentException("Impossible de récupérer les parties")
        }

        return resp.body()
    }
}