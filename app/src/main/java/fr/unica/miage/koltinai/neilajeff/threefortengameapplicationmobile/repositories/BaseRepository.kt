package fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.repositories

import fr.unica.miage.koltinai.neilajeff.threefortengameapplicationmobile.service.GameManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json

open class BaseRepository {
    protected val client =  HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL  // Pour afficher toutes les informations de la requête
            logger = Logger.DEFAULT  // Utilise le logger par défaut de Ktor
        }
        install(ContentNegotiation) {
            json()
        }
    }

//    protected val baseUrl = "http://10.0.2.2:8080"
    protected val baseUrl = "http://${GameManager.SERVER_HOST}:${GameManager.SERVER_PORT}"


}