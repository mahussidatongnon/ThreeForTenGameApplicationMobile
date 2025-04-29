package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.dto.PlayGameDTO
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GamePart
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GameState
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.Player
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

data class PointDTO(
    val x: Int = 0,
    val y: Int = 0,
)

@Serializable
data class CreateGameRequest(
    val player1Username: String,
    val player2Username: String,
    val secretCode: String,
    val nbCasesCote: Int
)

class GamePartRepository():BaseRepository() {

    suspend fun getAll(): List<GamePart> {
        val resp = client.get("$baseUrl/games") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
        return resp.body()
    }

    suspend fun getGamesByUsername(username: String): List<GamePart> {
        val resp = client.get("$baseUrl/players/${username}/games") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
        println("Games: ${resp.bodyAsText()}")
        return resp.body()
    }

    suspend fun getGameById(gameId: String): GamePart {
        val resp = client.get("$baseUrl/games/${gameId}") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
        println("Game: ${resp.bodyAsText()}")
        return resp.body()
    }

    suspend fun getGameState(gameId: String): GameState {
        val resp = client.get("$baseUrl/games/${gameId}/state") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
        println("GameState: ${resp.bodyAsText()}")
        return resp.body()
    }

    suspend fun getLegalAction(gameId: String): List<PointDTO> {
        val resp = client.get("$baseUrl/games/${gameId}/legal-actions") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
        println("LegalActions: ${resp.bodyAsText()}")
        return resp.body()
    }

    suspend fun startGame(gameId: String): GamePart {
        val resp = client.post("$baseUrl/games/${gameId}/start") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
        println("GamePart: ${resp.bodyAsText()}")
        return resp.body()
    }

    suspend fun joinGame(gameId: String, playerUsername: String, secretCode: String): GamePart {
        val resp = client.post("$baseUrl/games/${gameId}/join") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody {
                mapOf("playerUsername" to playerUsername)
                mapOf("secretCode" to secretCode)
            }
        }
        println("GamePart: ${resp.bodyAsText()}")
        return resp.body()
    }

    suspend fun playGame(gameId: String, playGameDTO: PlayGameDTO): GameState {
        val resp = client.post("$baseUrl/games/${gameId}/play") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(playGameDTO)
        }
        if (resp.status.isSuccess()) {
            return resp.body() // ou resp.bodyAsText() si vous voulez voir la réponse sous forme de texte
        } else {
            throw Exception("Error: ${resp.status}, ${resp.bodyAsText()}")
        }
        return resp.body()
    }

    suspend fun getAllPlayers(): List<Player> {
        try {
            println("Récupération de tous les joueurs")
            val resp = client.get("$baseUrl/players") {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
            }
            println("Players response: ${resp.bodyAsText()}")
            return resp.body()
        } catch (e: Exception) {
            println("Erreur lors de la récupération des joueurs: ${e.message}")
            throw e
        }
    }

    suspend fun createGame(
        player1Username: String,
        player2Username: String,
        secretCode: String,
        nbCasesCote: Int
    ): GamePart {
        val requestBody = CreateGameRequest(
            player1Username = player1Username,
            player2Username = player2Username,
            secretCode = secretCode,
            nbCasesCote = nbCasesCote
        )

        val resp = client.post("$baseUrl/games/") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }

        println("Create Game Response: ${resp.bodyAsText()}")

        if (resp.status.isSuccess()) {
            return resp.body()
        } else {
            throw Exception("Error: ${resp.status}, ${resp.bodyAsText()}")
        }
    }
}
