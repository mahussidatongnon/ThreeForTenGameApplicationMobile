package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GamePart
import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GameState
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
data class PointDTO(
    val x: Int = 0,
    val y: Int = 0,
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
//        println("Games: ${resp.bodyAsText()}")
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
}