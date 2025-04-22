package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.models.GamePart
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType

class GamePartRepository():BaseRepository() {

    suspend fun getAll(): List<GamePart> {
        val resp = client.get("$baseUrl/games") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
        return resp.body()
    }
}