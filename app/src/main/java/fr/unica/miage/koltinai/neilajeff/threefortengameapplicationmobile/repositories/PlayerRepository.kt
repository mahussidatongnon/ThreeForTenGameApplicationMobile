package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories
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

class PlayerRepository():BaseRepository() {


    suspend fun getPlayer(username: String): Player {
        val resp = client.get("$baseUrl/players/$username") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
        }
        if (resp.status != HttpStatusCode.OK) {
            println("Error: ${resp.status}")
            throw IllegalArgumentException("Player does not exist")
        }
        return resp.body()
    }

    suspend fun registerPlayer(username: String, password: String): Player {
        val resp = client.post("$baseUrl/auth/register") {
            accept(ContentType.Application.Json)
            contentType(ContentType.Application.Json)
            setBody(RegisterPlayer(username, password))
        }

        if(resp.status != HttpStatusCode.OK) {
            println("Erreur serveur : ${resp.bodyAsText()}")
            throw IllegalArgumentException("Can't create new player")
        }

        println("resp: $resp")
        return resp.body()
    }

}